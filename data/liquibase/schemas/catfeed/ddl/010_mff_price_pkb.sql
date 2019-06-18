create or replace
package body mff_price_loader as

  PKG_NAME constant varchar2(50) := 'mff_price_loader';
  -- main procedure that performs all tasks related to price import
  procedure run( p_dirpath varchar2) as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_price_feed;
  	load_csv_filenames(p_dirpath);
  	update_prices;
  end;
  
  -- Setup tables we need for price loading
  procedure setup_price_feed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_price_feed;
  end;
  
  
  -- Cleanup tables before and/or after we're done with price_feed
  procedure cleanup_price_feed as
  v_sql varchar2(100);
  begin
  	-- truncating because we simply want to empty the tmp table
  	-- delete is slower due to logging
  	v_sql:='truncate table tmp_price_csv';
    	execute immediate v_sql;
  	commit;
  end;

  -- Load an XML file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  v_sql varchar2(2000);
  begin
  	
	--mff_import_message.message_output_debug('load_file_into_tmp', 'deleting tmp_xml');
  	-- Clear out old data from tmp_price_csv...  we're merging one file at a time into tmp_price
  	v_sql:='truncate table tmp_price_csv';
    	--execute immediate v_sql;
    	
  	-- alter external table defn so it can load the new CSV file
  	v_sql:='alter table tmp_ext_price_csv location (''' || p_filename || ''')';
	execute immediate v_sql;  	
  	commit;
	
	--mff_import_message.message_output_debug('load_file_into_tmp', 'Loading file ' || p_filename || ' into tmp_xml');

	-- Load the csv data from external table into a tmp table
    	-- insert /*+ append */ into tmp_price_csv (sku_id,effective_date,retail_price,sale_price,batch_id,promo_id)
    	-- select sku_id, effective_date ,retail_price ,sale_price ,batch_id,replace(promo_id,chr(13)) 
    	-- from tmp_ext_price_csv;
    	
    	v_sql:='merge into tmp_price_csv dest ' ||
    		'using ( ' ||
    		     'select sku_id, ' ||
    			'effective_date, ' ||
    			'retail_price, ' ||
    			'sale_price, ' ||
    			'batch_id, ' ||
    			'replace(promo_id,chr(13)) promo_id ' ||
    		      'from tmp_ext_price_csv ' ||
    		 ') src ' ||
    		 'on(dest.sku_id=src.sku_id) ' ||
    		 'when matched then ' ||
    		 	'update set dest.retail_price=src.retail_price, ' ||
    		 		'dest.sale_price=src.sale_price ' ||
    		 'when not matched then ' ||
    		 	'insert (dest.sku_id, ' ||
    		 		'dest.effective_date, ' ||
    		 		'dest.retail_price, ' ||
    		 		'dest.sale_price, ' ||
    		 		'dest.batch_id, ' ||
    		 		'dest.promo_id) ' ||
    		 	'values(src.sku_id, ' ||
    		 		'src.effective_date, ' ||
    		 		'src.retail_price, ' ||
    		 		'src.sale_price, ' ||
    		 		'src.batch_id, ' ||
    				'src.promo_id)';
		mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', ' Loading into tmp_price_csv : ' || v_sql);
		
		execute immediate v_sql;

  	commit;
  	
    exception
    when others then
      rollback;
      dbms_output.put_line('ERROR!!!!!!!!!!! - ');
      raise;
  end;

  procedure load_csv_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin
  	-- First, setup the directory from the package variable
	-- v_sql := 'create or replace directory PRICE_FEED_INCOMING as ''' || p_dirpath || '''';
	-- execute immediate v_sql;

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames where filename like 'ECOM_PRC%';
	commit;
	
	-- Populate the file list table using our Java stored procedure
	-- exp_import_message.message_output_debug('load_xml_filenames', 'Loading XML filenames from directory');
	-- pass in the inventory feed file path /vagrant/csv/pricing
	mff_import_dir.get_dir_list(p_dirpath);
	dbms_output.put_line('DIR PATH - ' || p_dirpath);
	commit;
	--tmp_xml_filenames
	for o in (select * from tmp_xml_filenames where filename like 'ECOM_PRC%')
	loop
	    dbms_output.put_line('Processing - ' || o.filename);
	    load_file_into_tmp(o.filename);
  	end loop;
  end;

  -- clean up all inventory related tables

  procedure update_prices as
    begin
    
        -- update list price
    	merge into atg_core.dcs_price dest
   		using (select 'lp-' || sku_id as price_id, sku_id,retail_price from tmp_price_csv ) src
   		on (dest.price_id = src.price_id)
   		when matched then 
   			update set dest.list_price = src.retail_price where dest.price_id=src.price_id
   		when not matched then 
   			insert (dest.price_id,dest.version,dest.price_list,dest.sku_id,dest.pricing_scheme,dest.list_price) 
   			values ('lp-'||src.sku_id,
   				1,
   				'mffListPrice',
   				src.sku_id,
   				0,
   				src.retail_price);

        -- update promo id in list price - Left commented out as this may be needed only for sale prices
    	 -- merge into atg_core.mff_price dest
   	 --	using (select 'lp-' || sku_id as price_id, sku_id,promo_id from tmp_price_csv ) src
   	 --	on (dest.price_id = src.price_id)
   	 --	when matched then 
   	 --		update set dest.promo_id = src.promo_id where dest.price_id=src.price_id
   	 --	when not matched then 
   	 --		insert (dest.price_id,dest.promo_id) 
   	 --		values ('lp-'||src.sku_id,
   	 --			src.promo_id);
   				
        -- update sale price
    	merge into atg_core.dcs_price dest
   		using (select 'sp-' || sku_id as price_id, sku_id,sale_price from tmp_price_csv ) src
   		on (dest.price_id = src.price_id)
   		when matched then 
   			update set dest.list_price = src.sale_price where dest.price_id=src.price_id
   		when not matched then 
   			insert (dest.price_id,dest.version,dest.price_list,dest.sku_id,dest.pricing_scheme,dest.list_price) 
   			values ('sp-'||src.sku_id,
   				1,
   				'mffSalePrice',
   				src.sku_id,
   				0,
   				src.sale_price);
        -- update promo id in sale price
    	merge into atg_core.mff_price dest
   		using (select 'sp-' || sku_id as price_id,sku_id,promo_id from tmp_price_csv ) src
   		on (dest.price_id = src.price_id)
   		when matched then 
   			update set dest.promo_id = src.promo_id where dest.price_id=src.price_id
   		when not matched then 
   			insert (dest.price_id,dest.promo_id) 
   			values ('sp-'||src.sku_id,
   				src.promo_id);      	

      	commit;
    end;
		
  -- file archiver...  files are moved via Java stored procedures into /archive subdirectory
  procedure archive_files( p_dirpath varchar2, p_fileprefix varchar2) as
  	v_feedpath varchar2(200);
  begin
	-- Go through each of the feed files in tmp and archive them
  	for fileinfo in (select filename from tmp_xml_filenames where filename like p_fileprefix) loop
  		v_feedpath := p_dirpath || '/' || fileinfo.filename;
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
  
  -- clean up all price related tables
  procedure reset_price_data as
  v_sql varchar2(100);
  begin
  	-- Cleanup all tmp tables
  	cleanup_price_feed;
  	
  	-- clean up master data. 
	delete from atg_core.dcs_price where price_list='mffListPrice';
	delete from atg_core.dcs_price where price_list='mffSalePrice';
  	commit;
  end;
  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info(PKG_NAME, PKG_NAME || ' instance loaded.');
end mff_price_loader;
