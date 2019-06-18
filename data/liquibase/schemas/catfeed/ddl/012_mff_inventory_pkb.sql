create or replace package body mff_inventory_loader as

  PKG_NAME constant varchar2(50) := 'mff_inventory_loader';
  -- Main proc called from Java
  procedure run( p_dirpath varchar2, p_delta int, p_reset_damaged int ) as
  begin
  
  	mff_logger.log_sp_info(PKG_NAME || '.run',PKG_NAME || ' run started');

  	setup_inv_feed;
  	load_csv_filenames(p_dirpath,p_delta);
  	update_inventory(p_delta, p_reset_damaged);
  	
  	mff_logger.add_sp_info(PKG_NAME || '.run', 'completed successfully');

    	mff_logger.flush;

    	-- ** HERE SHOULD BE THE ONLY PLACE FOR ERROR LOGGING **
    	exception
    	when others then
      		rollback;

      	-- Log errors to MFF_LOG table
      	mff_logger.add_sp_error(pkg_name || '.run');
      	mff_logger.flush;

      	-- rethrow the exception Java so that message queue 
      	-- status can be updated accordingly
      	raise;  	
  end;
  
  -- Setup tables we need for inventory loading
  procedure setup_inv_feed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_inv_feed;
  end;
  
  procedure reset_shipped_counter as
  v_sql varchar2(2000);
  begin
      	-- Resetting shipped counters
      	mff_logger.add_sp_info(PKG_NAME || '.reset_shipped_counter', ' Resetting shipped counter in ff_inventory_transaction: ');

	v_sql := 'update atg_core.ff_inventory_transaction set shipped=0 where shipped > 0 ';
      	mff_logger.add_sp_info(PKG_NAME || '.reset_shipped_counter', ' Resetting shipped counter in ff_inventory_transaction sql: ' || v_sql);
	execute immediate v_sql;  	
	
	mff_logger.add_sp_info(PKG_NAME || '.reset_shipped_counter', ' Resetting shipped counter in ff_store_inv_transaction: ');
	
	v_sql := 'update atg_core.ff_store_inv_transaction set shipped=0 where shipped > 0 ';
      	mff_logger.add_sp_info(PKG_NAME || '.reset_shipped_counter', ' Resetting shipped counter in ff_store_inv_transaction sql: ' || v_sql);
	execute immediate v_sql;  		
	
	
  end;
  
  procedure adjust_allocation_count as
  v_sql varchar2(2000);
  v_order_sql varchar2(4000);
  v_order_sql_frag1 varchar2(4000);
  v_order_sql_frag2 varchar2(4000);
  v_store_sql varchar2(4000);
  v_inv_update_sql varchar2(4000);
  begin
	v_sql:='truncate table tmp_inv_adj';
	mff_logger.add_sp_info(PKG_NAME || '.adjust_allocation_count', ' Truncating tmp_inv_adj table sql: ' || v_sql);
	execute immediate v_sql;

	v_sql:='truncate table tmp_inv_adj_log';
	mff_logger.add_sp_info(PKG_NAME || '.adjust_allocation_count', ' Truncating tmp_inv_adj_log table sql: ' || v_sql);
	execute immediate v_sql;
	
	v_sql:='truncate table tmp_current_inv_level';
	mff_logger.add_sp_info(PKG_NAME || '.adjust_allocation_count', ' Truncating tmp_current_inv_level table sql: ' || v_sql);
	execute immediate v_sql;

	v_order_sql_frag1:= 'select di.order_ref ' || 
			', mo.order_number ' ||
			',do.submitted_date ' ||
			', do.state order_state' ||
			', mi.fulfillment_store ' ||
			', di.catalog_ref_id ' ||
			', sum(di.quantity) quantity ' ||
			', di.state item_state ' ||
        		', systimestamp ' ||
		'from atg_oms.mff_item mi ' ||
			', atg_oms.dcspp_item di ' ||
			', atg_oms.dcspp_order do ' ||
			', atg_oms.mff_order mo ' ||
		'where mi.fulfillment_store in ( ';
		
	v_store_sql:='select store_id from inventory_adjustment where processed=0 ';
	
	v_order_sql_frag2:= ') ' ||
			'and di.state != ''INITIAL'' ' ||
			'and di.state != ''SHIPPED'' ' ||
			'and di.state != ''CANCELLED'' ' ||
			'and di.commerce_item_id=mi.commerce_item_id ' ||
			'and do.order_id=di.order_ref ' ||
			'and do.order_id=mo.order_id ' ||
		'group by di.order_ref ' ||
			', mi.fulfillment_store ' ||
			', di.catalog_ref_id ' ||
			', di.state ' ||
			',mo.order_number ' ||
			', do.submitted_date ' ||
			', do.state ' ||
		'order by di.order_ref ' ||
			',mi.fulfillment_store ' ||
			', di.catalog_ref_id ' ||
			', di.state';
	
	v_order_sql:= v_order_sql_frag1 || v_store_sql || v_order_sql_frag2;
	
	v_sql:='insert into tmp_inv_adj_log ' ||
		v_order_sql;
	mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Logging orders before change sql: ' || v_sql);
	execute immediate v_sql;

	v_sql:= 'merge into tmp_current_inv_level dest ' ||
		'using ( ' ||
			'select inv.inventory_id ' ||
				', inv.catalog_ref_id ' ||
				', inv.stock_level ' ||
				', trans.sold ' ||
				', trans.allocated ' ||
				', trans.shipped ' ||
			'from atg_core.ff_inventory inv ' ||
				', atg_core.ff_inventory_transaction trans ' ||
			'where inv.inventory_id=trans.inventory_id ' ||
				'and inv.catalog_ref_id in ( ' ||
					'select distinct catalog_ref_id from tmp_inv_adj_log ' ||
				') ' ||
		') src ' ||
		'on(dest.catalog_ref_id=src.catalog_ref_id) ' ||
		'when not matched then ' ||
			'insert(dest.inventory_id ' ||
				',dest.catalog_ref_id ' ||
				',dest.stock_level ' ||
				',dest.sold ' ||
				',dest.allocated ' ||
				',dest.shipped ' ||
				') ' ||
			'values(src.inventory_id ' ||
				', src.catalog_ref_id ' ||
				', src.stock_level ' ||
				', src.sold ' ||
				', src.allocated ' ||
				', src.shipped ' ||
				') ';
	mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Saving current inventory levels sql: ' || v_sql);
	execute immediate v_sql;
	
	for o in (select * from inventory_adjustment where processed=0)
	loop
		mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Inventory for store ' || o.store_id || ' needs to be adjusted');
	
		v_sql:='truncate table tmp_inv_adj';
		mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Truncating tmp_inv_adj table sql: ' || v_sql);
		execute immediate v_sql;
	
		v_sql:= 'merge into tmp_inv_adj dest ' ||
				'using ( ' ||
    					'select catalog_ref_id , sum(qty) qty ' ||
    					'from tmp_inv_adj_log ' ||
    					'where fulfillment_store in ( ''' || o.store_id || ''') ' || 
    						'and item_state != ''INITIAL''' ||
    						'and item_state != ''SHIPPED''' ||
    						'and item_state != ''CANCELLED''' ||
    					'group by catalog_ref_id ' ||
					') src ' ||
				'on(dest.sku_id=src.catalog_ref_id) ' ||
				'when not matched then ' ||
					'insert(dest.sku_id,dest.qty) ' ||
					'values(src.catalog_ref_id, src.qty) ' ||
				'when matched then ' ||
					'update set dest.qty=dest.qty + src.qty ';
		mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Compiling SKU list to be adjusted sql: ' || v_sql);
		execute immediate v_sql;
		dbms_output.put_line ('Rollup flag for store ' || o.store_id || ' is ' || o.rollup_inventory);
		if o.rollup_inventory = 1
		then
			v_inv_update_sql:='update set dest.allocated = dest.allocated + src.qty';
			dbms_output.put_line ('1 - update sql is ' || v_inv_update_sql);
		else
			v_inv_update_sql:='update set dest.allocated = dest.allocated - src.qty';
			dbms_output.put_line ('0 - update sql is ' || v_inv_update_sql);
		end if;		
		
		v_sql:= 'merge into atg_core.ff_inventory_transaction dest ' ||
				'using ( ' ||
					' select ''inv-'' || sku_id inventory_id, qty  from tmp_inv_adj ' ||
				') src ' ||
				'on (dest.inventory_id = src.inventory_id) ' ||
				'when matched then ' ||
					v_inv_update_sql;
		mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Adjusting master inventory sql: ' || v_sql);
		execute immediate v_sql;
		
		-- Mark store as processed
		v_sql:= 'update inventory_adjustment set processed=1 where store_id = ''' || o.store_id || '''';
		mff_logger.log_sp_info(PKG_NAME || '.adjust_allocation_count', ' Marking store as processed sql: ' || v_sql);
		execute immediate v_sql;		
		
  	end loop;

	commit;
  	
  end;
  
  -- Insert rows in the transaction tables
  procedure insert_trans_records as
  v_sql varchar2(2000);
  begin
  	-- To avoid having to re-read the CSV files.. we will use info from the 
  	-- tmp table populated during the inv import.
      	
      	-- update store transaction table
      	mff_logger.add_sp_info(PKG_NAME || '.insert_trans_records', ' inserting store trans records : ');
      	
    	v_sql := 'merge into atg_core.ff_store_inv_transaction dest ' ||
   		 'using (select inventory_id, ' ||
   		 		'store_id ' ||
   		          'from tmp_inv_csv where store_id!=''4000'') src ' ||
   		 'on (dest.inventory_id = src.inventory_id) ' ||
   		 'when not matched then ' ||
   			'insert (dest.inventory_id, ' ||
   				'dest.store_id, ' ||
   				'dest.allocated, ' ||
   				'dest.shipped, ' ||
   				'dest.is_damaged) ' ||
   			'values (src.inventory_id, ' ||
   				'src.store_id, ' ||
   				'0, ' ||
   				'0, ' ||
      				'0)';
      	mff_logger.add_sp_info(PKG_NAME || '.insert_trans_records', ' Merge ff_store_inv_transaction sql: ' || v_sql);

	execute immediate v_sql;  
	
      	-- update store transaction table
      	mff_logger.add_sp_info(PKG_NAME || '.insert_trans_records', ' inserting main trans records : ');
      	
    	v_sql := 'merge into atg_core.ff_inventory_transaction dest ' ||
   		 'using (select distinct (''inv-'' || sku_id) as inventory_id from tmp_inv_csv) src ' ||
   		 'on (dest.inventory_id = src.inventory_id) ' ||
   		 'when not matched then ' ||
   			'insert (dest.inventory_id, ' ||
   				'dest.sold, ' ||
   				'dest.allocated, ' ||
   				'dest.shipped) ' ||
   			'values (src.inventory_id, ' ||
   				'0, ' ||
   				'0, ' ||
      				'0)';
      	mff_logger.add_sp_info(PKG_NAME || '.insert_trans_records', ' Merge ff_inventory_transaction sql: ' || v_sql);

	execute immediate v_sql;  	
  end;
  
  -- Cleanup tables before and/or after we're done with inv_feed
  procedure cleanup_inv_feed as
  	v_sql varchar2(100);
  begin
  	-- truncating because we simply want to empty the tmp table
  	-- delete is slower due to logging

  	v_sql:='truncate table tmp_inv_csv';
    	execute immediate v_sql;

  	mff_logger.add_sp_info(PKG_NAME || '.cleanup_inv_feed', ' tmp_inv_csv truncated');  
  end;

  -- Load feed file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  	v_sql varchar2(2000);
  begin
  	-- Start by loading the raw file into tmp_xml
  	
	mff_logger.add_sp_info(PKG_NAME || '.load_file_into_tmp', ' Loading file into tmp : ' || p_filename);
	
    	
  	-- alter external table defn so it can load the new CSV file
  	mff_logger.add_sp_info(PKG_NAME || '.load_file_into_tmp', ' Altering tmp_ext_inv_csv location : ' || p_filename);
  	
  	v_sql:='alter table tmp_ext_inv_csv location (''' || p_filename || ''')';
	execute immediate v_sql;
	
  	commit;
	

	-- Load the csv data from external table into a tmp table
	
	mff_logger.add_sp_info(PKG_NAME || '.load_file_into_tmp', ' Loading store inventory to tmp_inv_csv');
	
    	-- This should really be a merge statement.. .and not an insert.. since we may be processing multiple files
    	-- and tmp_inv_csv could be having data from another file
    	
    	v_sql:='merge into tmp_inv_csv dest ' ||
    		'using ( ' ||
			'select ''inv-'' || sku_id || ''-'' || store_id as inventory_id, ' || 
				'retailer_id, ' ||
				'sku_id, ' ||
				'store_id, ' ||
				'threshold, ' ||
				'avail_qty, ' ||
				'unit_retail, ' ||
				'tax_rate, ' ||
				'sell_thru ' || 
			'from tmp_ext_inv_csv where store_id!=''4000'' ' ||    	
    		') src ' ||
    		'on(dest.inventory_id=src.inventory_id) ' ||
    		'when matched then ' ||
    			'update set dest.avail_qty=src.avail_qty ' ||
    		'when not matched then ' ||
    			'insert (dest.inventory_id, ' ||
    				'dest.retailer_id, ' ||
    				'dest.sku_id, ' ||
    				'dest.store_id, ' ||
    				'dest.threshold, ' ||
    				'dest.avail_qty, ' ||
    				'dest.unit_retail, ' ||
    				'dest.tax_rate, ' ||
    				'dest.sell_thru) ' ||
    			'values(src.inventory_id, ' ||
				'src.retailer_id, ' ||
				'src.sku_id, ' ||
				'src.store_id, ' ||
				'src.threshold, ' ||
				'src.avail_qty, ' ||
				'src.unit_retail, ' ||
				'src.tax_rate, ' ||
				'src.sell_thru)'; 
		mff_logger.add_sp_info(PKG_NAME || '.load_file_into_tmp', ' Loading into tmp_inv_csv : ' || v_sql);
		execute immediate v_sql;
    	commit;
    	
    	mff_logger.add_sp_info(PKG_NAME || '.load_file_into_tmp', ' Rows inserted into tmp_inv_csv : ' || sql%rowcount);
  	
  end;

  procedure load_csv_filenames ( p_dirpath varchar2, p_delta int) as
	v_sql varchar2(254);
	v_fileprefix varchar2(254);
	v_directory varchar2(254);
  begin
  
  	if p_delta = 0 then
  		v_fileprefix := 'ECOM_INV%';
  		v_directory := 'inv_feed_incoming';
  	else
  		v_fileprefix := 'ECOM_INV_DELTA%';
  		v_directory := 'inv_delta_feed_incoming';
  	end if;
  	
	-- Clear out the current list of filenames, first
	mff_logger.add_sp_info(PKG_NAME || '.load_csv_filenames', ' Deleting old info from tmp_xml_filenames with prefix : ' || v_fileprefix);
	
	v_sql := 'delete from tmp_xml_filenames where filename like ''' || v_fileprefix || '''';
	execute immediate v_sql;
	commit;

  	mff_logger.add_sp_info(PKG_NAME || '.load_csv_filenames', ' Altering directory location : ' || v_directory);
  	v_sql:='alter table tmp_ext_inv_csv DEFAULT DIRECTORY ' || v_directory;
	execute immediate v_sql;
	
	-- Populate the file list table using our Java stored procedure
	
	mff_logger.add_sp_info(PKG_NAME || '.load_csv_filenames', ' Loading files from directory: ' || p_dirpath);

	mff_import_dir.get_dir_list(p_dirpath);

	
	commit;
	
	for o in (select * from tmp_xml_filenames where filename like v_fileprefix)
	loop
	    load_file_into_tmp(o.filename);
  	end loop;	
	
  end;

  -- clean up all inventory related tables

  procedure update_inventory(p_delta int, p_reset_damaged int) as
  v_sql varchar2(4000);
  v_promo_gc_skus varchar2(4000);
  v_online_reset_cols varchar2(400);
  v_store_reset_cols varchar2(400);
  v_stock_update varchar2(400);
      begin
    
    	-- promo gc ids
    	v_promo_gc_skus := '''100799054'',''100799055'',''100799056'',''100799197'',''100799198'',''100799199'',''100799200'',''100799201'',''100799202'',''100799203'',''100794100''';
    	
    	-- stock update during delta runs
    	v_stock_update := ', dest.stock_level= ( ' ||
    						'case ' ||
    							'when (dest.stock_level + (src.avail_qty)) < 0 then 0 ' ||
    							'else (dest.stock_level + (src.avail_qty)) end) ';
    	
    	
    	if(p_delta = 0) then
    		-- v_online_reset_cols := ', shipped = 0 ';
    		-- v_store_reset_cols := ', shipped = 0 ';
    		v_stock_update := ', dest.stock_level=src.avail_qty ';
    	end if;
    	
    	if(p_reset_damaged = 1) then
    		v_store_reset_cols := v_store_reset_cols || ', is_damaged=0 ';
    	end if;

      	-- First update store_inventory. Then using the updated values
      	-- we will compute online inventory
	
      	-- update store inventory
    	v_sql := 'merge into atg_core.ff_store_inventory dest ' ||
   		 'using (select inventory_id, ' ||
   		 		'sku_id, store_id,avail_qty ' ||
   		          'from tmp_inv_csv where store_id!=''4000'') src ' ||
   		 'on (dest.inventory_id = src.inventory_id) ' ||
   		 'when matched then ' ||
   			'update set last_update_date=systimestamp ' ||
   				   v_stock_update ||
   				   v_store_reset_cols || 
   				'where dest.inventory_id=src.inventory_id ' ||
   		 'when not matched then ' ||
   			'insert (dest.inventory_id, ' ||
   				'dest.store_id, ' ||
   				'dest.catalog_ref_id, ' ||
   				'dest.stock_level, ' ||
   				'dest.creation_date) ' ||
   			'values (src.inventory_id, ' ||
   				'src.store_id, ' ||
   				'src.sku_id, ' ||
   				'case when src.avail_qty < 0 then 0 ' ||
   				' else src.avail_qty end, ' ||
      				'systimestamp)';
      	mff_logger.add_sp_info(PKG_NAME || '.update_inventory', ' Merge ff_store_inventory sql: ' || v_sql);

	execute immediate v_sql;
      	 
        -- update the online inventory
	
	
	v_sql := 'merge into atg_core.ff_inventory dest ' ||
		 'using (select ''inv-'' || si.catalog_ref_id as inventory_id, ' ||
		 		'si.catalog_ref_id as sku_id, ' ||
		 		'sum(si.stock_level) as stock_level ' ||
		 	'from atg_core.ff_store_inventory si, atg_cata.mff_location ml  ' ||
      			'where si.catalog_ref_id in ( ' ||
      				'select distinct sku_id from tmp_inv_csv ' ||
      					'where sku_id not in ( ' || v_promo_gc_skus ||
      					') ' ||
      			    ') ' ||
      			' and si.store_id=ml.location_id (+)' ||
      			' and (ml.is_bopis_only=0 or ml.is_bopis_only is null) ' ||
      			'group by ''inv-'' || si.catalog_ref_id,catalog_ref_id) src ' ||
		'on(dest.inventory_id=src.inventory_id) ' ||
		'when matched then ' ||
  			'update set dest.stock_level=src.stock_level ' ||
		'when not matched then ' ||
  			'insert (dest.inventory_id, ' ||
  				'dest.display_name, ' ||
  				'dest.version, ' ||
  				'dest.catalog_ref_id, ' ||
  				'dest.avail_status, ' ||
  				'dest.stock_level, ' ||
  				'dest.creation_date, ' ||
  				'dest.location_id) ' ||
  			'values(src.inventory_id, ' ||
  				'src.inventory_id, ' ||
  				'1, ' ||
  				'src.sku_id, ' ||
  				'1000, ' ||
  				'src.stock_level, ' ||
  				'systimestamp, ' ||
  				'4000)';
  
      	mff_logger.add_sp_info(PKG_NAME || '.update_inventory', ' Online ff_inventory sql: ' || v_sql);
      	execute immediate v_sql;

      	--commit;
    end;
  
  -- clean up all inventory related tables
  procedure reset_import_data as
  v_sql varchar2(100);
  begin
  	-- Cleanup all tmp tables
  	cleanup_inv_feed;
  	
  	-- clean up master data. Using truncate since this is just for development
  	v_sql:='truncate table atg_core.ff_inventory';
  	execute immediate v_sql;
  	v_sql:='truncate table atg_core.ff_store_inventory';
  	execute immediate v_sql;
  	-- commit;
  end;

  -- file archiver...  files are moved via Java stored procedures into /archive subdirectory
  procedure archive_files( p_dirpath varchar2, p_fileprefix varchar2) as
  	v_feedpath varchar2(200);
  begin
	-- Go through each of the feed files in tmp and archive them
  	for fileinfo in (select filename from tmp_xml_filenames where filename like p_fileprefix) loop
  		v_feedpath := p_dirpath || '/' || fileinfo.filename;
  		
  		mff_logger.add_sp_info(PKG_NAME || '.archive_files', ' Archiving: ' || v_feedpath);
  		mff_import_dir.archive_feed( v_feedpath);
    	end loop;
	
  end;
  
  -- file deleter.  If there's another process to archive feed files, then there's no need to
  -- keep two copies.  A nucleus property can be configured to just delete feed files instead.
  procedure delete_files( p_dirpath varchar2, p_fileprefix varchar2) as
    v_feedpath varchar2(200);
  begin
  -- Go through each of the feed files in tmp and delete them
    for fileinfo in (select filename from tmp_xml_filenames where filename like p_fileprefix) loop
      v_feedpath := p_dirpath || '/' || fileinfo.filename;
      mff_import_dir.delete_feed( v_feedpath);
    end loop;
  end;  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  --mff_import_message.message_output_debug('mff_inventory_loader', 'Initializing package mff_inventory_loader');
  mff_logger.log_sp_info(PKG_NAME, PKG_NAME || ' instance loaded');
end mff_inventory_loader;