create or replace
package body mff_catalog_loader as

  PKG_NAME constant varchar2(50) := 'mff_catalog_loader';
  -- Setup tables we need for product loading
  procedure setup_catfeed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_catfeed;
  end;
  
  
  -- Cleanup tables before and/or after we're done with catfeed
  procedure cleanup_catfeed as
  begin
  	delete from tmp_xml_filenames;
  	delete from tmp_xml;
  	
  	delete from tmp_prod_data;
  	delete from tmp_prod_selling_point_data;
  	delete from tmp_prod_catg_data;
  	
  	delete from tmp_sku_data;
  	
  	delete from tmp_catg_data;
  	delete from tmp_cat_chldprd;
  	
  	delete from tmp_prod_picker_data;
  	delete from tmp_sku_picker_data;

	delete from tmp_update_rels;
	delete from tmp_cat_prod_rel_change;
	delete from tmp_sku_orig_prod;
	delete from tmp_prod_chldsku;

	delete from tmp_chld_skus;
	delete from tmp_chld_skus_headline;

	delete from tmp_cat_chldcat;
	delete from tmp_changed_cats;
	
	delete from tmp_sku_department_data;
  	commit;
  end;
  
  -- Load an XML file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  begin
  	-- Start by loading the raw file into tmp_xml
  	
	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', 'completed successfully');
  	-- Clear out old data from tmp_xml...  we're merging one file at a time into tmp_prod, sku, cat
  	delete from tmp_xml;
  	commit;
	
	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', 'Loading file ' || p_filename || ' into tmp_xml');

	-- Load the raw XML of the file into tmp_xml as an xmltype
  	insert into tmp_xml values ( p_filename, xmltype(bfilename('PIM_FEED_INCOMING', p_filename), nls_charset_id('UTF8')));
  	commit;

  	-- Now copy the actual product data into tmp tables
  	 load_prod_data_into_tmp;
  	-- load_prod_selling_points;
  	 load_sku_data_into_tmp;
  	 load_catg_data_into_tmp;
    exception
    when others then
      raise;
  end;

  procedure copy_tmp_to_all as
  begin
  	-- Pull the list of schema names from the DB and deploy to each one
  	-- Go through all the other schemas in order
  	for schema in (select schema_name,is_versioned from tmp_deploy_to_schemas order by sequence_num) loop
  		if ( schema.is_versioned = 1) then
  			copy_tmp_to_pub( schema.schema_name);
  			mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Just an empty message');
  		else
  			copy_tmp_to_schema( schema.schema_name);
  			mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Copied feed data to ' || schema.schema_name);
  		end if;
        	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Copied feed data to ' || schema.schema_name);
    end loop;

    copy_departmentdata_to_core;
    exception
    when others then
      raise;
  end;

  procedure copy_departmentdata_to_core as
  	v_sql varchar2(5000);
  begin
	  
	  --SKU/ITEM Department Name
    v_sql :='update atg_core.item_department itmD set department_name = (select tmp.department_name from tmp_sku_department_data tmp where tmp.sku_id=itmD.item_id)'||
    'where exists (select 1 from tmp_sku_department_data tmp where tmp.sku_id = itmD.item_id )';
	
    mff_logger.log_sp_info(PKG_NAME || '.copy_departmentdata_to_core', 'Updating existing items with departments - SQL -- ' || v_sql);
    execute immediate v_sql;
    
    v_sql :='delete from tmp_sku_department_data tmp where exists (select 1 from atg_core.item_department dep where dep.item_id = tmp.sku_id )';
    mff_logger.log_sp_info(PKG_NAME || '.copy_departmentdata_to_core', 'Delete Execute SQL -- ' || v_sql);
    
    execute immediate v_sql;
    
    v_sql:='insert into atg_core.item_department (item_id, department_name) select sku_id, department_name from tmp_sku_department_data';
    
    mff_logger.log_sp_info(PKG_NAME || '.copy_departmentdata_to_core', 'Insert Execute SQL -- ' || v_sql);
    execute immediate v_sql;
	
  end;
  
  procedure copy_tmp_to_head( p_schema_prefix varchar2) as
    v_sql varchar2(5000);
  begin
    	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Copying feed data to mainline branch of versioned schema ' || p_schema_prefix);
    	dbms_output.put_line(PKG_NAME || '.copy_tmp_to_head');

	-- Copying data to mainline versions of assets

	-- ----------------------
	-- Category...  1 table
	-- ----------------------

	-- dcs_category
	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_category c set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activation_date,deactivation_date '||
   	   'from tmp_catg_data tmp '||
    	'where tmp.category_id = c.category_id'||
  	  ') '||
  	  'where exists '||
    	'(select category_id from tmp_catg_data tmp '||
     		'where tmp.category_id = c.category_id and catg_change=1 and catg_exists=1 and c.is_head=1)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	
	-- ----------------------
	-- Product...  3 tables
	-- ----------------------

	-- dcs_product
	-- Update the primary table's properties for any products that need to change via direct SQL
	--   ie. prod_change=1 and product cannot point to new SKUs
	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_product p set ' ||
	  '(display_name,description,start_date,end_date,brand,long_description) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date,brand,romance_copy '||
   	   'from tmp_prod_data tmp '||
    	'where tmp.product_id = p.product_id '||
  	  ') '||
  	  'where exists '||
    	'(select product_id from tmp_prod_data tmp '||
     		'where tmp.product_id = p.product_id and tmp.prod_change=1 and tmp.prod_has_new_skus=0 and tmp.prod_exists=1) ' ||
   		'and is_head=1';
	
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;


	-- mff_product
	v_sql :=
	 'update ' || p_schema_prefix || 'mff_product p set ' ||
  		'(picker_template,num_alt_images,num_images,fulfillment_method,is_hide_price,is_in_store_only,is_made_in_usa, ' ||
  		' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard) = ' ||
  		'(select  ' ||
    		'template,no_of_alt_images,no_of_images,fulfillment_method,is_hide_price,in_store_only,made_in_usa, ' ||
    		' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard ' ||
   		'from tmp_prod_data tmp  ' ||
    		'where tmp.product_id = p.product_id ' ||
  		')  ' ||
  	   'where exists ' ||
    	'(select product_id, asset_version from ' || p_schema_prefix || 'dcs_product pp ' ||
        'where (pp.is_head=1 and p.product_id = pp.product_id and p.asset_version = pp.asset_version ' ||
          'and pp.product_id in  ' ||
            '( select product_id from tmp_prod_data tmp2 ' ||
              'where tmp2.prod_change=1 and tmp2.prod_has_new_skus=0 and tmp2.prod_exists=1)) ' ||
      	')';
      
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- product pickers
      	v_sql := 'delete from ' || p_schema_prefix || 'mff_product_attr p where exists( ' ||
		    'select 1 from ' || p_schema_prefix || 'dcs_product pp  ' ||
      			'where (pp.is_head=1 and p.product_id = pp.product_id ' || 
      				'and p.asset_version = pp.asset_version and pp.product_id in ' || 
	          			'(select tmp.product_id from tmp_prod_data tmp where ' || 
	          				'tmp.prod_change=1 and tmp.prod_has_new_skus=0 and tmp.prod_exists=1 and tmp.picker_change=1)))';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || p_schema_prefix || 'mff_product_attr ' ||
	    		'select sp.product_id,sp.seq_num,sp.picker_label,pp.asset_version ' ||
	    			'from ' || p_schema_prefix || 'dcs_product pp, tmp_prod_picker_data sp ' ||
      				'where (pp.is_head=1 and sp.product_id = pp.product_id and pp.product_id in ' ||
	          			'(select tmp.product_id from tmp_prod_data tmp ' ||
	          				'where tmp.prod_change=1 and tmp.prod_has_new_skus=0 and tmp.prod_exists=1 and tmp.picker_change=1))';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- ----------------------
	-- SKU...  3 tables
	-- ----------------------
	-- dcs_sku
	
	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_sku s set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date '||
   	   'from tmp_sku_data tmp '||
    	'where tmp.sku_id = s.sku_id '||
  	  ') '||
  	  'where exists '||
    	'(select sku_id from tmp_sku_data tmp '||
     		'where tmp.sku_id = s.sku_id and sku_exists=1 and sku_change=1) ' ||
   		'and is_head=1';

  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- mff_sku

	v_sql :=
	 'update ' || p_schema_prefix || 'mff_sku s set ' ||
		  '(sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized, ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code,restricted_locations,shipping_surcharge_qnty_range, eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping, sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl) ='||
  		'(select  ' ||
    		'sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized,  ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code,restricted_locations, shipping_surcharges,eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping,sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl '||
   		'from tmp_sku_data tmp  ' ||
    		'where tmp.sku_id = s.sku_id ' ||
  		')  ' ||
  	   'where exists ' ||
    	'(select sku_id, asset_version from ' || p_schema_prefix || 'dcs_sku ss ' ||
        'where (ss.is_head=1 and s.sku_id = ss.sku_id and s.asset_version = ss.asset_version ' ||
          'and ss.sku_id in  ' ||
            '( select sku_id from tmp_sku_data tmp2 ' ||
              'where tmp2.sku_exists=1 and tmp2.sku_change=1)) ' ||
      	')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

      	v_sql := 'delete from ' || p_schema_prefix || 'dcs_sku_attr s where exists( ' ||
		    'select 1 from ' || p_schema_prefix || 'dcs_sku ss  ' ||
      			'where (ss.is_head=1 and s.sku_id = ss.sku_id ' || 
      				'and s.asset_version = ss.asset_version and ss.sku_id in ' || 
	          			'(select tmp.sku_id from tmp_sku_data tmp where ' || 
	          				'tmp.sku_change=1 and tmp.sku_exists=1 and tmp.picker_change=1)))';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || p_schema_prefix || 'dcs_sku_attr ' ||
			'(sku_id, attribute_name,attribute_value,asset_version) ' ||
	    		'select sp.sku_id,sp.seq_num,sp.picker_label,ss.asset_version ' ||
	    			'from ' || p_schema_prefix || 'dcs_sku ss, tmp_sku_picker_data sp ' ||
      				'where (ss.is_head=1 and sp.sku_id = ss.sku_id and ss.sku_id in ' ||
	          			'(select tmp.sku_id from tmp_sku_data tmp ' ||
	          				'where tmp.sku_change=1 and tmp.sku_exists=1 and tmp.picker_change=1))';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;	
	
	-- dcs_cat_chldprd
	-- We have to update cat-child product relationships

	-- Delete old relationships
	v_sql := 'delete from ' || p_schema_prefix || 'dcs_cat_chldprd cat where exists ' ||
			'(select 1 from  ' || p_schema_prefix || 'dcs_category cc ' ||
			'where (cc.is_head=1 and cat.category_id = cc.category_id ' ||
				'and cat.asset_version = cc.asset_version)) ' ||
				'and (cat.child_prd_id,cat.category_id) in ( ' ||
					'select tmp.product_id , tmp.category_id from tmp_cat_prod_rel_change tmp ' ||
					'where tmp.action = ''DELETE'')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldprd ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_prd_id)' ||
		'select p.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+9999, ' ||
			'p.product_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_product p, ' ||
			'tmp_cat_prod_rel_change tmp ' ||
		'where cc.is_head=1 ' ||
			'and p.is_head=1 ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and p.product_id=tmp.product_id ' ||
			'and tmp.action =''ADD''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	
	-- build updated, clean, sequenced relationships
	v_sql := 'insert into tmp_cat_chldprd '||
		  'select category_id, ' ||
			'(row_number() over (partition by category_id order by sequence_num))-1 sequence_num, ' ||
			'child_prd_id from ( ' ||
				'select distinct cat.category_id category_id, ' ||
					'cat.sequence_num sequence_num, ' ||
					'cat.child_prd_id child_prd_id ' ||
				'from ' || p_schema_prefix || 'dcs_cat_chldprd cat ' ||
	 			'inner join tmp_cat_prod_rel_change tmp on cat.category_id = tmp.category_id ' ||
	 			'inner join ' || p_schema_prefix || 'dcs_category cc ' ||
	 				'on (cat.category_id = cc.category_id and cat.asset_version = cc.asset_version) ' ||
				'where cc.is_head=1 order by cat.sequence_num)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Merge updated relationships 
	v_sql :=
		'merge into ' || p_schema_prefix || 'dcs_cat_chldprd seq1 '||
		    'using (select '||
                'cc.asset_version,'||
 		        'c.category_id,'||
                'c.sequence_num sequence_num,'||
		        'c.child_prd_id from tmp_cat_chldprd c '||
              'inner join ' || p_schema_prefix || 'dcs_category cc on (c.category_id = cc.category_id) '||
              'where cc.is_head=1) seq2 '||
    		    'on (seq1.asset_version = seq2.asset_version and seq1.category_id = seq2.category_id and seq1.child_prd_id = seq2.child_prd_id) '||
		    'when matched then update '||
		      'set seq1.sequence_num = seq2.sequence_num';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- dcs_prd_chldsku
	
	-- delete old relationships
	-- modified SQL after final round of testing!!!
    	v_sql := 'delete from ' || p_schema_prefix || 'dcs_prd_chldsku p where exists ' ||
			'(select 1 from ' || p_schema_prefix || 'dcs_product pp ' ||
				'where (pp.is_head=1 and ' ||
					'p.product_id = pp.product_id and ' ||
					'p.asset_version = pp.asset_version and ' ||
					'pp.product_id in ' ||
						'(select product_id from tmp_chld_skus_headline)))';		
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	-- if orig_prod has no skus left then it will not be in tmp_chld_skus_headline table
	-- as there is no need to sequence it. Lets remove skus from those products if they exist
	-- *****
	-- 0000000001296 has only 1 sku 000000224
	-- 0000000001298 has only 1 sku 000000240
	-- 000000224 moves from 0000000001296 -> 0000000001298
	-- End result 0000000001298 will have 2 SKUs
	-- 0000000001296 will have no SKUs
	-- tmp_chld_skus_headline will have the sequenced SKUs for 0000000001298
	-- since 0000000001296 will have no SKUs, it will not be in the tmp_chld_skus_headline
	-- The below delete is to handle those products that are in 
	--	tmp_sku_orig_prod but not tmp_chld_skus_headline
	-- ******
    	v_sql := 'delete from ' || p_schema_prefix || 'dcs_prd_chldsku p where exists ' ||
			'(select 1 from ' || p_schema_prefix || 'dcs_product pp ' ||
				'where (pp.is_head=1 and ' ||
					'p.product_id = pp.product_id and ' ||
					'p.asset_version = pp.asset_version and ' ||
					'pp.product_id in ' ||
						'(select product_id from tmp_sku_orig_prod ' ||
							'where product_id not in (select product_id from tmp_chld_skus_headline) ' ||
						') ' ||
					') ' ||
			')';		
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- insert new relationships
	-- if prod is new & sku is new - then this is handled in the Java process
	-- if prod is new & sku exists - The new relationship is added by Java
	-- if prod exists & sku is new - The new relationship is added by Java
	-- if prod exists & sku exists - then we need to add the relationship to the headline

	v_sql := 'insert into ' || p_schema_prefix || 'dcs_prd_chldsku ' ||
		 '(product_id,sequence_num,sku_id,asset_version,sec_asset_version) ' ||
		'select tmp.product_id, ' ||
			'(row_number() over (partition by tmp.product_id order by rownum))-1 sequence_num, ' ||
			'tmp.sku_id, ' ||
			'p.asset_version, ' ||
			's.asset_version sec_asset_verion ' ||
		'from ' || p_schema_prefix || 'dcs_product p, ' ||
			p_schema_prefix || 'dcs_sku s, ' ||
			'tmp_chld_skus_headline tmp ' ||
		'where s.sku_id=tmp.sku_id ' ||
			'and p.product_id=tmp.product_id ' ||
			'and p.is_head=1 ' ||
			'and s.is_head=1';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	-- update cat-child cat relationships
	-- delete existing rows
	v_sql := 'delete from ' || p_schema_prefix || 'dcs_cat_chldcat cc ' ||
		'where exists ( ' ||
			'select 1 from ' || p_schema_prefix || 'dcs_category c ' ||
			'where (c.is_head=1 ' ||
				'and cc.category_id=c.category_id ' ||
				'and cc.asset_version=c.asset_version) ' ||
				'and c.category_id in ( ' ||
					'select distinct category_id from tmp_cat_chldcat ' ||
				') ' ||
		')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	
	-- insert clean rows
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldcat ' ||
		 	'(asset_version,sec_asset_version,category_id,sequence_num,child_cat_id) ' ||
		 'select c.asset_version asset_version, ' ||
		 	'c1.asset_version sec_asset_version, ' ||
		 	'cat.category_id, ' ||
		 	'cat.sequence_num, ' ||
		 	'cat.child_cat_id ' ||
		 'from tmp_cat_chldcat cat, ' ||
		 	p_schema_prefix || 'dcs_category c , ' ||
		 	p_schema_prefix || 'dcs_category c1 ' ||
		 'where cat.category_id=c.category_id ' ||
			'and cat.child_cat_id=c1.category_id ' ||
			'and c.is_head=1 ' ||
			'and c1.is_head=1';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

    exception
    when others then
      raise;	  
  end;

  procedure copy_tmp_to_pub ( p_schema_name varchar2 ) as
	type cur_type is ref cursor;
	v_ws_cur cur_type;
    v_schema_prefix varchar2(80);
    v_sql varchar2(200);
    v_ws_id varchar2(40);
    begin
      	v_schema_prefix := p_schema_name || '.';

      	-- Do the updates to target schema...   this should all be in one TX
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_pub', 'Copying feed data to versioned schema ' || p_schema_name);
	-- Start by pushing data to mainline branch
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_pub', 'Copying feed data to mainline');
	dbms_output.put_line(PKG_NAME || '.copy_tmp_to_pub');

	copy_tmp_to_head( v_schema_prefix);
	
	-- Get a list of active project workspaces (that are not a Catalog Import project) and push data to each one
	v_sql := 'select id from ' || v_schema_prefix || 'avm_devline where name in (select workspace from ' || v_schema_prefix || 'epub_project where status=0)';
	mff_logger.log_sp_info(PKG_NAME || '.avm_dev_line', 'Execute SQL -- ' || v_sql);

	open v_ws_cur for v_sql;
	loop
		fetch v_ws_cur into v_ws_id;
		exit when v_ws_cur%NOTFOUND;
	    	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_pub', 'Copying feed data to workspace ' || v_ws_id);
	    	copy_tmp_to_workspace( v_schema_prefix, v_ws_id);
    	end loop;
  end;

  procedure copy_tmp_to_workspace( p_schema_prefix varchar2, p_ws_id varchar2) as
    v_sql varchar2(5000);
  begin
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Copying feed data to versioned schema ' || p_schema_prefix || ' and workspace ID ' || p_ws_id);

	-- ----------------------
	-- Category...  1 table
	-- ----------------------

	-- dcs_category
	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_category c set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activation_date,deactivation_date '||
   	   'from tmp_catg_data tmp '||
    	'where tmp.category_id = c.category_id'||
  	  ') '||
  	  'where exists '||
    	'(select category_id from tmp_catg_data tmp '||
     		'where tmp.category_id = c.category_id and catg_change=1 and catg_exists=1 ' ||
     		'and workspace_id=''' || p_ws_id || '''' ||	
     		')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- ----------------------
	-- Product...  3 tables
	-- ----------------------

	-- dcs_product
	-- Update the primary table's properties for any products in the current workspace that need to change via direct SQL
	--   ie. prod_exists=1 and product cannot point to new SKUs
	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_product p set ' ||
	  '(display_name,description,start_date,end_date,brand,long_description) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date,brand,romance_copy '||
   	   'from tmp_prod_data tmp '||
    	'where tmp.product_id = p.product_id '||
  	  ') '||
  	  'where exists '||
    	'(select product_id from tmp_prod_data tmp '||
     		'where tmp.product_id = p.product_id and prod_change=1 and prod_exists=1 and prod_has_new_skus=0) ' ||
   		'and workspace_id=''' || p_ws_id || '''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

   		
	-- mff_product
	-- Update the aux Express attributes table's properties for any products in the current workspace that need to change via direct SQL
	--   ie. prod_exists=1 and product cannot point to new SKUs
	v_sql :=
	  'update ' || p_schema_prefix || 'mff_product p set '||
		  '(picker_template,num_alt_images,num_images,fulfillment_method, is_hide_price,is_in_store_only,is_made_in_usa, ' ||
		  ' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard) ='||
		  '(select template,no_of_alt_images,no_of_images,fulfillment_method,is_hide_price,in_store_only,made_in_usa, ' ||
		  ' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard '||
		   'from tmp_prod_data tmp '||
		    'where tmp.product_id = p.product_id) '||
        'where exists '||
    	'(select product_id, asset_version from ' || p_schema_prefix || 'dcs_product pp '||
         'where (pp.workspace_id=''' || p_ws_id || ''' and p.product_id = pp.product_id and p.asset_version = pp.asset_version '||
          'and pp.product_id in '||
            '(select product_id from tmp_prod_data tmp2 '||
              'where tmp2.prod_exists=1 and tmp2.prod_change=1 and tmp2.prod_has_new_skus=0)))';
      	
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

      	v_sql := 'delete from ' || p_schema_prefix || 'mff_product_attr p where exists( ' ||
		    'select 1 from ' || p_schema_prefix || 'dcs_product pp  ' ||
      			'where (pp.workspace_id=''' || p_ws_id || ''' and p.product_id = pp.product_id ' || 
      				'and p.asset_version = pp.asset_version and pp.product_id in ' || 
	          			'(select tmp.product_id from tmp_prod_data tmp where ' || 
	          				'tmp.prod_change=1 and tmp.prod_has_new_skus=0 and tmp.prod_exists=1 and tmp.picker_change=1)))';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || p_schema_prefix || 'mff_product_attr ' ||
	    		'select sp.product_id,sp.seq_num,sp.picker_label,pp.asset_version ' ||
	    			'from ' || p_schema_prefix || 'dcs_product pp, tmp_prod_picker_data sp ' ||
      				'where (pp.workspace_id=''' || p_ws_id || ''' and sp.product_id = pp.product_id and pp.product_id in ' ||
	          			'(select tmp.product_id from tmp_prod_data tmp ' ||
	          				'where tmp.prod_change=1 and tmp.prod_has_new_skus=0 and tmp.prod_exists=1 and tmp.picker_change=1))';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- ----------------------
	-- SKU...  3 tables
	-- ----------------------
	-- dcs_sku

	v_sql := 
	 'update ' || p_schema_prefix || 'dcs_sku s set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date '||
   	   'from tmp_sku_data tmp '||
    	'where tmp.sku_id = s.sku_id '||
  	  ') '||
  	  'where exists '||
    	'(select sku_id from tmp_sku_data tmp '||
     		'where tmp.sku_id = s.sku_id and sku_exists=1 and sku_change=1) ' ||
   		'and workspace_id=''' || p_ws_id || '''';

  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- mff_sku
	v_sql :=
	 'update ' || p_schema_prefix || 'mff_sku s set ' ||
		  '(sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized, ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code,restricted_locations,shipping_surcharge_qnty_range, eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping, sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl) ='||
  		'(select  ' ||
    		'sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized,  ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code,restricted_locations,shipping_surcharges, eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping, sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl '||
   		'from tmp_sku_data tmp  ' ||
    		'where tmp.sku_id = s.sku_id ' ||
  		')  ' ||
  	   'where exists ' ||
    	'(select sku_id, asset_version from ' || p_schema_prefix || 'dcs_sku ss ' ||
        'where (ss.workspace_id=''' || p_ws_id || ''' and s.sku_id = ss.sku_id and s.asset_version = ss.asset_version ' ||
          'and ss.sku_id in  ' ||
            '( select sku_id from tmp_sku_data tmp2 ' ||
              'where tmp2.sku_exists=1 and tmp2.sku_change=1)) ' ||
      	')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

      	v_sql := 'delete from ' || p_schema_prefix || 'dcs_sku_attr s where exists( ' ||
		    'select 1 from ' || p_schema_prefix || 'dcs_sku ss  ' ||
      			'where (ss.workspace_id=''' || p_ws_id || ''' and s.sku_id = ss.sku_id ' || 
      				'and s.asset_version = ss.asset_version and ss.sku_id in ' || 
	          			'(select tmp.sku_id from tmp_sku_data tmp where ' || 
	          				'tmp.sku_change=1 and tmp.sku_exists=1 and tmp.picker_change=1)))';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || p_schema_prefix || 'dcs_sku_attr ' ||
			'(sku_id, attribute_name,attribute_value,asset_version) ' ||
	    		'select sp.sku_id,sp.seq_num,sp.picker_label,ss.asset_version ' ||
	    			'from ' || p_schema_prefix || 'dcs_sku ss, tmp_sku_picker_data sp ' ||
      				'where (ss.workspace_id=''' || p_ws_id || ''' and sp.sku_id = ss.sku_id and ss.sku_id in ' ||
	          			'(select tmp.sku_id from tmp_sku_data tmp ' ||
	          				'where tmp.sku_change=1 and tmp.sku_exists=1 and tmp.picker_change=1))';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- dcs_cat_chldprd
	
	-- delete old relationships
	
	-- Delete cat_prod relationships that match
	-- workspace revision of the category
	v_sql :=
		'delete from ' || p_schema_prefix || 'dcs_cat_chldprd cat where exists ' ||
			'(select 1 from ' || p_schema_prefix || 'dcs_category cc ' ||
          'where (cc.workspace_id=''' || p_ws_id || ''' and cat.category_id = cc.category_id and cat.asset_version = cc.asset_version)) and (cat.child_prd_id,cat.category_id) in ' ||
            '(select tmp.product_id, tmp.category_id from tmp_cat_prod_rel_change tmp where tmp.action = ''DELETE'')';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 1 - When the category is in the workspace
	-- Add workspace category -> headline product relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldprd ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_prd_id)' ||
		'select p.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+9999, ' ||
			'p.product_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_product p, ' ||
			'tmp_cat_prod_rel_change tmp ' ||
		'where cc.workspace_id=''' || p_ws_id || ''' ' ||
			'and p.is_head=1 ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and p.product_id=tmp.product_id ' ||
			'and tmp.action =''ADD''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 2 - When the product is in the workspace
	-- Add headline category -> workspace product relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldprd ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_prd_id)' ||
		'select p.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+9999, ' ||
			'p.product_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_product p, ' ||
			'tmp_cat_prod_rel_change tmp ' ||
		'where p.workspace_id=''' || p_ws_id || ''' ' ||
			'and cc.is_head=1 ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and p.product_id=tmp.product_id ' ||
			'and tmp.action =''ADD''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 3 - When BOTH category AND product are in the same workspace
	-- Add workspace category -> workspace product relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldprd ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_prd_id)' ||
		'select p.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+99999, ' ||
			'p.product_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_product p, ' ||
			'tmp_cat_prod_rel_change tmp ' ||
		'where p.workspace_id=''' || p_ws_id || ''' ' ||
			'and cc.workspace_id=''' || p_ws_id || ''' ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and p.product_id=tmp.product_id ' ||
			'and tmp.action =''ADD''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- Now, copy over the sequence numbers from the tmp table we created during the headline pass over to all
	--   child product versions of the workspace version of the category
	v_sql :=
		'merge into ' || p_schema_prefix || 'dcs_cat_chldprd seq1 '||
		    'using (select '||
                'cc.asset_version,'||
 		        'c.category_id,'||
                'c.sequence_num sequence_num,'||
		        'c.child_prd_id from tmp_cat_chldprd c '||
              'inner join ' || p_schema_prefix || 'dcs_category cc on (c.category_id = cc.category_id) '||
              'where cc.workspace_id=''' || p_ws_id || ''') seq2 '||
    		    'on (seq1.asset_version = seq2.asset_version and seq1.category_id = seq2.category_id and seq1.child_prd_id = seq2.child_prd_id) '||
		    'when matched then update '||
		      'set seq1.sequence_num = seq2.sequence_num';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql :=
		'merge into ' || p_schema_prefix || 'dcs_cat_chldprd seq1 '||
		    'using (select '||
                'pp.asset_version,'||
 		        'c.category_id,'||
                'c.sequence_num sequence_num,'||
		        'c.child_prd_id from tmp_cat_chldprd c '||
              'inner join ' || p_schema_prefix || 'dcs_product pp on (c.child_prd_id = pp.product_id) '||
              'where pp.workspace_id=''' || p_ws_id || ''') seq2 '||
    		    'on (seq1.sec_asset_version = seq2.asset_version and seq1.category_id = seq2.category_id and seq1.child_prd_id = seq2.child_prd_id) '||
		    'when matched then update '||
		      'set seq1.sequence_num = seq2.sequence_num';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
  
  -- ********************************* PROD SKU RELATIONSHIP UPDATES ***************************** --------------------

	-- Delete child skus of products in workspace
	-- These would be all affected products
	v_sql :=
		'delete from ' || p_schema_prefix || 'dcs_prd_chldsku prod where exists ' ||
			'(select 1 from ' || p_schema_prefix || 'dcs_product pp ' ||
          			'where (pp.workspace_id=''' || p_ws_id || ''' and ' ||
          				'prod.product_id = pp.product_id and ' ||
          				'prod.asset_version = pp.asset_version) ' ||
          				'and pp.product_id in ' ||
            					'(select tmp.product_id from tmp_chld_skus_headline tmp))';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'PRINT SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 1 - When the product is in the workspace
	-- Add workspace product -> headline sku relationships
	v_sql :=
	  'insert into ' || p_schema_prefix || 'dcs_prd_chldsku ' ||
		  'select ' ||
		  	'ss.asset_version sec_asset_version, ' ||
        	'pp.asset_version asset_version, ' ||
        	's.product_id product_id, ' ||
		    '(row_number() over (partition by s.product_id order by rownum))-1 sequence_num, ' ||
    		's.sku_id sku_id ' ||
		  'from tmp_chld_skus_headline s, ' || p_schema_prefix || 'dcs_product pp , ' || p_schema_prefix || 'dcs_sku ss ' ||
	  'where pp.product_id = s.product_id and pp.workspace_id=''' || p_ws_id || ''' and ss.sku_id = s.sku_id and ss.is_head=1';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;
	

	-- Case 2 - When the sku is in the workspace
	-- Add headline product -> workspace sku relationships
	v_sql :=
	  'insert into ' || p_schema_prefix || 'dcs_prd_chldsku ' ||
		  'select ' ||
		  	'ss.asset_version sec_asset_version, ' ||
        	'pp.asset_version asset_version, ' ||
        	's.product_id product_id, ' ||
		    '(row_number() over (partition by s.product_id order by rownum))-1 sequence_num, ' ||
    		's.sku_id sku_id ' ||
		  'from tmp_chld_skus_headline s, ' || p_schema_prefix || 'dcs_product pp , ' || p_schema_prefix || 'dcs_sku ss ' ||
	  'where pp.product_id = s.product_id and pp.is_head=1 and ss.sku_id = s.sku_id and ss.workspace_id=''' || p_ws_id || '''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- Case 3 - When BOTH product AND sku are in the same workspace
	-- Add workspace product -> workspace sku relationships
	v_sql :=
	  'insert into ' || p_schema_prefix || 'dcs_prd_chldsku ' ||
		  'select ' ||
		  	'ss.asset_version sec_asset_version, ' ||
        	'pp.asset_version asset_version, ' ||
        	's.product_id product_id, ' ||
		    '(row_number() over (partition by s.product_id order by rownum))-1 sequence_num, ' ||
    		's.sku_id sku_id ' ||
		  'from tmp_chld_skus_headline s, ' || p_schema_prefix || 'dcs_product pp , ' || p_schema_prefix || 'dcs_sku ss ' ||
	  'where pp.product_id = s.product_id and pp.workspace_id=''' || p_ws_id || ''' and ss.sku_id = s.sku_id and ss.workspace_id=''' || p_ws_id || '''';
  	
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	-- ************** update category -> child category relationships
	v_sql :=
		'delete from ' || p_schema_prefix || 'dcs_cat_chldcat cat where exists ' ||
			'(select 1 from ' || p_schema_prefix || 'dcs_category cc ' ||
          'where (cc.workspace_id=''' || p_ws_id || ''' and cat.category_id = cc.category_id and cat.asset_version = cc.asset_version)) and cat.category_id in ' ||
            '(select distinct tmp.category_id from tmp_changed_cats tmp)';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 1 - When the parent category is in the workspace
	-- Add workspace category -> headline child cat relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldcat ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_cat_id)' ||
		'select cc1.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+9999, ' ||
			'cc1.category_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_category cc1, ' ||
			'tmp_cat_chldcat tmp ' ||
		'where cc.workspace_id=''' || p_ws_id || ''' ' ||
			'and cc1.is_head=1 ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and cc1.category_id=tmp.child_cat_id ';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 2 - When the child cat is in the workspace
	-- Add headline category -> workspace childcat relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldcat ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_cat_id)' ||
		'select cc1.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+9999, ' ||
			'cc1.category_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_category cc1, ' ||
			'tmp_cat_chldcat tmp ' ||
		'where cc1.workspace_id=''' || p_ws_id || ''' ' ||
			'and cc.is_head=1 ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and cc1.category_id=tmp.child_cat_id ';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Case 3 - When BOTH category AND product are in the same workspace
	-- Add workspace category -> workspace product relationships
	v_sql := 'insert into ' || p_schema_prefix || 'dcs_cat_chldcat ' ||
			'(sec_asset_version, asset_version,category_id,sequence_num, child_cat_id)' ||
		'select cc1.asset_version, ' ||
			'cc.asset_version, ' ||
			'cc.category_id, ' ||
			'rownum+99999, ' ||
			'cc1.category_id ' ||
		'from ' || p_schema_prefix || 'dcs_category cc, ' ||
			p_schema_prefix || 'dcs_category cc1, ' ||
			'tmp_cat_chldcat tmp ' ||
		'where cc1.workspace_id=''' || p_ws_id || ''' ' ||
			'and cc.workspace_id=''' || p_ws_id || ''' ' ||
			'and tmp.category_id=cc.category_id ' ||
			'and cc1.category_id=tmp.child_cat_id ';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- Now, copy over the sequence numbers from the tmp table we created during the headline pass over to all
	--   child product versions of the workspace version of the category
	v_sql :=
		'merge into ' || p_schema_prefix || 'dcs_cat_chldcat seq1 '||
		    'using (select '||
                'cc.asset_version,'||
 		        'c.category_id,'||
                'c.sequence_num sequence_num,'||
		        'c.child_cat_id from tmp_cat_chldcat c '||
              'inner join ' || p_schema_prefix || 'dcs_category cc on (c.category_id = cc.category_id) '||
              'where cc.workspace_id=''' || p_ws_id || ''') seq2 '||
    		    'on (seq1.asset_version = seq2.asset_version and seq1.category_id = seq2.category_id and seq1.child_cat_id = seq2.child_cat_id) '||
		    'when matched then update '||
		      'set seq1.sequence_num = seq2.sequence_num';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql :=
		'merge into ' || p_schema_prefix || 'dcs_cat_chldcat seq1 '||
		    'using (select '||
                'cc.asset_version,'||
 		        'c.category_id,'||
                'c.sequence_num sequence_num,'||
		        'c.child_cat_id from tmp_cat_chldcat c '||
              'inner join ' || p_schema_prefix || 'dcs_category cc on (c.child_cat_id = cc.category_id) '||
              'where cc.workspace_id=''' || p_ws_id || ''') seq2 '||
    		    'on (seq1.sec_asset_version = seq2.asset_version and seq1.category_id = seq2.category_id and seq1.child_cat_id = seq2.child_cat_id) '||
		    'when matched then update '||
		      'set seq1.sequence_num = seq2.sequence_num';
  	mff_logger.log_sp_info('copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

  
    exception
    when others then
      raise;
  end;


  procedure copy_tmp_to_schema ( p_schema_name varchar2 ) as
    v_schema_prefix varchar2(80);
    v_sql varchar2(2000);
  begin
    if ( p_schema_name = '') then
      v_schema_prefix := '';
    else
      v_schema_prefix := p_schema_name || '.';
    end if;
    
    -- Do the updates to target schema...   this should all be in one TX
    mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Copying feed data to ' || p_schema_name);

	-- ----------------------
	-- Category...  1 tables
	-- ----------------------

	-- dcs_product
	v_sql := 
	 'update ' || v_schema_prefix || 'dcs_category c set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activation_date,deactivation_date '||
   	   'from tmp_catg_data tmp '||
    	'where tmp.category_id = c.category_id'||
  	  ') '||
  	  'where exists '||
    	'(select category_id from tmp_catg_data tmp '||
     		'where tmp.category_id = c.category_id and catg_change=1 and catg_exists=1)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- ----------------------
	-- Product...  3 tables
	-- ----------------------

	-- dcs_product
	v_sql := 
	 'update ' || v_schema_prefix || 'dcs_product p set ' ||
	  '(display_name,description,start_date,end_date,brand,long_description) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date,brand,romance_copy '||
   	   'from tmp_prod_data tmp '||
    	'where tmp.product_id = p.product_id'||
  	  ') '||
  	  'where exists '||
    	'(select product_id from tmp_prod_data tmp '||
     		'where tmp.product_id = p.product_id and prod_change=1 and prod_exists=1 and prod_has_new_skus=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	

	-- mff_product
	v_sql :=
	  'update ' || v_schema_prefix || 'mff_product p set ' ||
		  '(picker_template,num_alt_images,num_images,fulfillment_method,is_hide_price,is_in_store_only,is_made_in_usa, ' ||
		  ' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard) ='||
		  '(select template,no_of_alt_images,no_of_images,fulfillment_method,is_hide_price,in_store_only,made_in_usa, ' ||
		  ' teaser_startdate, teaser_enddate, minimum_age,selling_points,choking_hazard '||
		   'from tmp_prod_data tmp '||
		    'where tmp.product_id = p.product_id) '||
	  'where exists '||
	    '(select product_id from tmp_prod_data tmp '||
	     'where tmp.product_id = p.product_id and prod_change=1 and prod_exists=1 and prod_has_new_skus=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- product pickers
	v_sql := 'delete from ' || v_schema_prefix || 'mff_product_attr where product_id in ( ' ||
			'select distinct product_id from tmp_prod_data where prod_change=1  and prod_exists=1 and picker_change=1)';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	v_sql := 'insert into ' || v_schema_prefix || 'mff_product_attr (product_id,attribute_name,attribute_value) ' ||
		  'select product_id,seq_num,picker_label from tmp_prod_picker_data where product_id in ( ' ||
			'select product_id from tmp_prod_data where prod_change=1  and prod_exists=1 and picker_change=1)';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- 10/17 commenting out the below to test prd->sku relationships
	-- Delete old relationships
	v_sql := 'delete from ' || v_schema_prefix || 'dcs_cat_chldprd cat where ' ||
				'(cat.child_prd_id,cat.category_id) in ( ' ||
					'select tmp.product_id,tmp.category_id from tmp_cat_prod_rel_change tmp ' ||
					'where tmp.action = ''DELETE'')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	v_sql := 'insert into ' || v_schema_prefix || 'dcs_cat_chldprd ' ||
			'(category_id,sequence_num, child_prd_id)' ||
		'select category_id, ' ||
			'rownum+9999, ' ||
			'product_id ' ||
		'from tmp_cat_prod_rel_change tmp ' ||
		'where tmp.action =''ADD''';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- build updated, clean, sequenced relationships

	-- Now reset the sequence numbers
	v_sql :=
		'merge into ' || v_schema_prefix || 'dcs_cat_chldprd seq1 ' ||
		    'using (select ' ||
		        'cat.category_id, ' ||
		       '(row_number() over (partition by cat.category_id order by cat.sequence_num))-1 sequence, ' ||
		        'cat.child_prd_id from ' || v_schema_prefix || 'dcs_cat_chldprd cat ' ||
		      'where cat.category_id in ( ' ||
		      	'select distinct category_id from tmp_cat_prod_rel_change tmp)) seq2 ' ||
		    'on (seq1.category_id = seq2.category_id and seq1.child_prd_id = seq2.child_prd_id) ' ||
		    'when matched then update ' ||
		      'set seq1.sequence_num = seq2.sequence';
  	mff_logger.log_sp_info(PKG_NAME || 'copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
			
	-- ----------------------
	-- SKU...  3 tables
	-- ----------------------
	-- dcs_sku
	v_sql := 
	 'update ' || v_schema_prefix || 'dcs_sku s set ' ||
	  '(display_name,description,start_date,end_date) ='||
  	  '(select '||
  		'description,description,activate_date,deactivate_date '||
   	   'from tmp_sku_data tmp '||
    	'where tmp.sku_id = s.sku_id'||
  	  ') '||
  	  'where exists '||
    	'(select sku_id from tmp_sku_data tmp '||
     		'where tmp.sku_id = s.sku_id and sku_exists=1 and sku_change=1)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;

	-- mff_sku
	v_sql :=
	  'update ' || v_schema_prefix || 'mff_sku s set ' ||
		  '(sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized, ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code, restricted_locations,shipping_surcharge_qnty_range,eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping, sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl) ='||
		  '(select sku_length,girth,date_sensitive,hazardous,refrigerate,width,oversized, ' ||
		    'weight,restrict_air, ltl_fuel_surcharge, vpn, tax_code,restricted_locations,shipping_surcharges, eds, is_cube, ltl_lift_gate, ' ||
		    'consumable, clearance, freight_class, ltl_res_delivery, vacinne,ormd,free_shipping, sku_depth, ' ||
		    'long_light, upcs, pps_msg_ids, freezable, ltl '||
		   'from tmp_sku_data tmp '||
		    'where tmp.sku_id = s.sku_id) '||
	  'where exists '||
	    '(select sku_id from tmp_sku_data tmp '||
	     'where tmp.sku_id = s.sku_id and sku_exists=1 and sku_change=1)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	v_sql := 'delete from ' || v_schema_prefix || 'dcs_sku_attr where sku_id in ( ' ||
			'select distinct sku_id from tmp_sku_data where sku_change=1 and sku_exists=1 and picker_change=1)';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	v_sql := 'insert into ' || v_schema_prefix || 'dcs_sku_attr (sku_id,attribute_name,attribute_value) ' ||
		  'select sku_id,seq_num,picker_label from tmp_sku_picker_data where sku_id in ( ' ||
			'select sku_id from tmp_sku_data where sku_change=1 and sku_exists=1 and picker_change=1)';
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- delete old relationships
	v_sql := 'delete from ' || v_schema_prefix || 'dcs_prd_chldsku ps where ' ||
			'ps.product_id in (select product_id from tmp_chld_skus_headline)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;
	
	-- if orig_prod has no skus left then it will not be in tmp_chld_skus_headline table
	-- as there is no need to sequence it. Lets remove skus from those products if they exist
	-- *****
	-- 0000000001296 has only 1 sku 000000224
	-- 0000000001298 has only 1 sku 000000240
	-- 000000224 moves from 0000000001296 -> 0000000001298
	-- End result 0000000001298 will have 2 SKUs
	-- 0000000001296 will have no SKUs
	-- tmp_chld_skus_headline will have the sequenced SKUs for 0000000001298
	-- since 0000000001296 will have no SKUs, it will not be in the tmp_chld_skus_headline
	-- The below delete is to handle those products that are in 
	--	tmp_sku_orig_prod but not tmp_chld_skus_headline
	-- ******
	v_sql := 'delete from ' || v_schema_prefix || 'dcs_prd_chldsku ps where ' ||
			'ps.product_id in ( ' ||
				'select product_id from tmp_sku_orig_prod ' ||
					'where product_id not in ( ' ||
						'select product_id from tmp_chld_skus_headline ' ||
					') ' ||
			')';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;	

	-- insert new relationships
	v_sql := 'insert into ' || v_schema_prefix || 'dcs_prd_chldsku (product_id,sequence_num,sku_id) ' ||
		 'select tmp.product_id, tmp.sequence_num, tmp.sku_id ' ||
		 'from tmp_chld_skus_headline tmp ';  
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	-- update cat-child cat relationships
	-- delete existing rows
	v_sql := 'delete from ' || v_schema_prefix || 'dcs_cat_chldcat cc ' ||
			'where cc.category_id in ( ' ||
				'select distinct category_id from tmp_cat_chldcat) ';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;

	
	-- insert clean rows
	v_sql := 'insert into ' || v_schema_prefix || 'dcs_cat_chldcat ' ||
		 	'(category_id,sequence_num,child_cat_id) ' ||
		 'select cat.category_id, ' ||
		 	'cat.sequence_num, ' ||
		 	'cat.child_cat_id ' ||
		 'from tmp_cat_chldcat cat';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'LATEST SQL -- ' || v_sql);
	execute immediate v_sql;
    
    exception
    when others then
      raise;
  end;
  
  procedure load_catg_data_into_tmp as
  begin
    merge into tmp_catg_data dest
      using (
	     select 
		  p.category_id, 
		  p.description,
		  to_date(p.activation_date,'YYYY-MM-DD') as activation_date,
		  to_date(p.deactivation_date,'YYYY-MM-DD') as deactivation_date,
		  p.template_id,
		  p.catg_level,
		  p.parent_id,
		  p.batch_id,
		  1 catg_exists,
		  0 create_new_catg,
		  0 catg_change,
		  0 parent_catg_change,
		  0 parent_catg_is_new,
		  0 orig_parent_has_new_products,
		  0 parent_has_new_products,
		  tmp_xml.filename xml_file_name
		from tmp_xml,
		  -- create pseudo-columns from the subtags of the Product tags of the xmltype
		  xmltable('/categories/category' passing tmp_xml.xml_col
		    columns
			  category_id path '@id',
			  description path 'description',
			  activation_date path 'activationDate',
			  deactivation_date path 'deactivationDate',
			  template_id path 'template',
			  catg_level number path 'level',
			  parent_id path 'parent_id',
			  batch_id path 'batchID'
		    ) p
		where tmp_xml.filename like '%CAT%'
		) src
		on (dest.category_id = src.category_id)
		when matched then
		  -- Category already loaded from a previous file during this run, so overlay
		  update set
			  dest.description = src.description,
			  dest.parent_id = src.parent_id,
			  dest.catg_level = src.catg_level,
			  dest.activation_date = src.activation_date,
			  dest.deactivation_date = src.deactivation_date,
			  dest.template_id = src.template_id,
			  dest.batch_id = src.batch_id,
			  dest.catg_exists = src.catg_exists,
			  dest.create_new_catg = src.create_new_catg,
			  dest.catg_change = src.catg_change,
			  dest.parent_catg_change = src.parent_catg_change,
			  dest.parent_catg_is_new = src.parent_catg_is_new,
			  dest.orig_parent_has_new_products = src.orig_parent_has_new_products,
			  dest.parent_has_new_products = src.parent_has_new_products
		when not matched then
		  -- We haven't encountered this category yet during this run, so add it to the table
		  insert (category_id, description, parent_id, catg_level, activation_date, deactivation_date,
  				template_id, batch_id, catg_exists, create_new_catg, catg_change,
  				parent_catg_change,parent_catg_is_new,orig_parent_has_new_products,parent_has_new_products)
		  values (src.category_id, src.description, src.parent_id, src.catg_level, src.activation_date, src.deactivation_date,
  				src.template_id, src.batch_id, src.catg_exists, src.create_new_catg, src.catg_change,
  				src.parent_catg_change,src.parent_catg_is_new,src.orig_parent_has_new_products,src.parent_has_new_products);

    exception
    when others then
      raise;
  
  end;
  
  procedure load_sku_data_into_tmp as
  begin
    merge into tmp_sku_data dest
      using (
	     select 
		  p.sku_id, 
		  p.product_id,
		  p.sku_length, 
		  to_date(p.activate_date,'YYYY-MM-DD') as activate_date,
		  to_date(p.deactivate_date,'YYYY-MM-DD') as deactivate_date,
		  p.girth, 
		  p.width,
		  p.weight,
		  p.description,
		  p.vpn,
		  p.tax_code,
		  p.is_cube, 
		  p.freight_class,
		  p.sku_depth,
		  p.upc_1,
		  p.upc_2, 
		  p.upc_3,
    		  case p.date_sensitive when 'Y' then 1 when 'y' then 1 else 0 end date_sensitive,
    		  case p.hazardous when 'Y' then 1 when 'y' then 1 else 0 end hazardous,
    		  case p.refrigerate when 'Y' then 1 when 'y' then 1 else 0 end refrigerate,
    		  case p.oversized when 'Y' then 1 when 'y' then 1 else 0 end oversized,
    		  case p.restrict_air when 'Y' then 1 when 'y' then 1 else 0 end restrict_air,
    		  case p.ltl_fuel_surcharge when 'Y' then 1 when 'y' then 1 else 0 end ltl_fuel_surcharge,
    		  case p.eds when 'Y' then 1 when 'y' then 1 else 0 end eds,
    		  case p.ltl_lift_gate when 'Y' then 1 when 'y' then 1 else 0 end ltl_lift_gate,
    		  case p.consumable when 'Y' then 1 when 'y' then 1 else 0 end consumable,
    		  case p.clearance when 'Y' then 1 when 'y' then 1 else 0 end clearance,
    		  case p.ltl_res_delivery when 'Y' then 1 when 'y' then 1 else 0 end ltl_res_delivery,
    		  case p.vacinne when 'Y' then 1 when 'y' then 1 else 0 end vacinne,
    		  case p.long_light when 'Y' then 1 when 'y' then 1 else 0 end long_light,
    		  case p.freezable when 'Y' then 1 when 'y' then 1 else 0 end freezable,
    		  case p.ltl when 'Y' then 1 when 'y' then 1 else 0 end ltl,
    		  case p.ormd when 'Y' then 1 when 'y' then 1 else 0 end ormd,
    		  case p.free_shipping when 'Y' then 1 when 'y' then 1 else 0 end free_shipping,
		  1 sku_exists,
		  0 create_new_sku,
		  0 sku_change,
		  0 prod_change,
		  0 sku_has_new_prod,
		  0 picker_change,
		  tmp_xml.filename xml_file_name
		from tmp_xml,
		  -- create pseudo-columns from the subtags of the Product tags of the xmltype
		  xmltable('/SKUs/SKU' passing tmp_xml.xml_col
		    columns
			  sku_id path '@id',
			  product_id path 'PRODUCT',
			  sku_length number path 'LENGTH',
			  activate_date path 'ACTIVATE_DATE',
			  deactivate_date path 'DEACTIVATE_DATE',
			  girth number path 'GIRTH',
			  date_sensitive path 'DATE_SENSITIVE',
			  hazardous path 'HAZARDOUS',
			  refrigerate path 'REFRIGERATE',
			  width number path 'WIDTH',
			  oversized path 'OVERSIZED',
			  weight number path 'WEIGHT',
			  restrict_air path 'RESTRICT_AIR',
			  ltl_fuel_surcharge path 'LTL_FUEL_SURCHARGE',
			  description path 'DESCRIPTION',
			  vpn path 'VPN',
			  tax_code path 'TAX_CODE',
			  eds path 'EDS',
			  is_cube number path 'CUBE',
			  ltl_lift_gate path 'LTL_LIFT_GATE',
			  consumable path 'CONSUMABLE',
			  clearance path 'CLEARANCE',
			  freight_class path 'FREIGHT_CLASS',
			  ltl_res_delivery path 'LTL_RES_DELIVERY',
			  vacinne path 'VACINNE',
			  batch_id number path 'BATCH_ID',
			  sku_depth number path 'DEPTH',
			  long_light path 'LONG_LIGHT',
			  upc_1 path 'UPC',
			  upc_2 path 'UPC',
			  upc_3 path 'UPC',
			  freezable path 'FREEZABLE',
			  ltl path 'LTL',
			  ormd path 'ORMD',
			  free_shipping path 'FREE_SHIPPING'
		    ) p
		where tmp_xml.filename like '%ITM%'
		) src
		on (dest.product_id = src.product_id and dest.sku_id = src.sku_id)
		when matched then
		  -- SKU already loaded from a previous file during this run, so overlay
		  update set
			dest.sku_length = src.sku_length, 
			dest.activate_date = src.activate_date,
			dest.deactivate_date=src.deactivate_date,
			dest.girth = src.girth, 
			dest.width = src.width,
			dest.weight = src.weight,
			dest.description = src.description,
			dest.vpn = src.vpn,
			dest.tax_code=src.tax_code,
			dest.is_cube = src.is_cube, 
			dest.freight_class = src.freight_class,
			dest.sku_depth = src.sku_depth,
			dest.upc_1 = src.upc_1,
			dest.upc_2 = src.upc_2, 
			dest.upc_3 = src.upc_3,
			dest.date_sensitive = src.date_sensitive,
			dest.hazardous = src.hazardous,
			dest.refrigerate = src.refrigerate,
			dest.oversized = src.oversized,
			dest.restrict_air = src.restrict_air,
			dest.ltl_fuel_surcharge = src.ltl_fuel_surcharge,
			dest.eds = src.eds,
			dest.ltl_lift_gate = src.ltl_lift_gate,
			dest.consumable = src.consumable,
			dest.clearance = src.clearance,
			dest.ltl_res_delivery = src.ltl_res_delivery,
			dest.vacinne = src.vacinne,
			dest.long_light = src.long_light,
			dest.freezable = src.freezable,
			dest.ltl = src.ltl,
			dest.ormd=src.ormd,
			dest.free_shipping=src.free_shipping,
			dest.sku_exists = src.sku_exists,
			dest.create_new_sku = src.create_new_sku,
			dest.sku_change = src.sku_change,
			dest.prod_change = src.prod_change,
			dest.sku_has_new_prod = src.sku_has_new_prod,
			dest.picker_change=src.picker_change
		when not matched then
		  -- We haven't encountered this SKU yet during this run, so add it to the table
		  insert (sku_id, sku_length, activate_date,deactivate_date,girth, width,weight,description,vpn,tax_code,product_id,
		  		is_cube, freight_class,sku_depth,upc_1,upc_2, upc_3,
		  		date_sensitive,hazardous, refrigerate,oversized,
		  		restrict_air,ltl_fuel_surcharge, eds,ltl_lift_gate,
				consumable,clearance,ltl_res_delivery,vacinne,ormd,free_shipping,
				long_light,freezable,ltl,sku_exists, create_new_sku,sku_change,prod_change,sku_has_new_prod,picker_change)
		  values (src.sku_id, src.sku_length, src.activate_date,src.deactivate_date,src.girth, src.width,src.weight,src.description,src.vpn,src.tax_code,src.product_id,
		  		src.is_cube, src.freight_class,src.sku_depth,src.upc_1,src.upc_2, src.upc_3,
		  		src.date_sensitive,src.hazardous, src.refrigerate,src.oversized,
		  		src.restrict_air,src.ltl_fuel_surcharge, src.eds,src.ltl_lift_gate,
				src.consumable,src.clearance,src.ltl_res_delivery,src.vacinne,src.ormd,src.free_shipping,
				src.long_light,src.freezable,src.ltl,src.sku_exists, src.create_new_sku,src.sku_change,src.prod_change,src.sku_has_new_prod,src.picker_change);

	--tmp_prod_picker_data
	 merge into tmp_sku_picker_data dest
	 using (
	 	select 
	 		skus.id sku_id, 
	 		(picker_labels.seq_num-1) seq_num, 
	 		picker_labels.picker_label
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      picker_labels xmltype path 'PICKERS/PICKER'
	 		 ) skus,
	 		 xmltable('/PICKER' passing skus.picker_labels
	 		 columns
	 			picker_label varchar2(254) path '.',
	 	  		seq_num for ordinality
	 		 ) picker_labels
	 	   where tmp_xml.filename like '%ITM%'
	     	) src
	 	on (dest.sku_id=src.sku_id and dest.seq_num=src.seq_num)
	 	when matched then
	 		update set dest.picker_label=src.picker_label
	 	when not matched then
	 		insert (sku_id,seq_num,picker_label)
	 		values(src.sku_id,src.seq_num,src.picker_label);				

	 merge into tmp_sku_data dest
	 using (
	 	select 
	 		skus.id sku_id,
      	 		listagg(restricted_states.restricted_state,'^') within group (order by skus.id,restricted_states.seq_num) as restricted_locations
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      restricted_states xmltype path 'RESTRICTED_STATES/RESTRICTED_STATE'
	 		 ) skus,
	 		 xmltable('/RESTRICTED_STATE' passing skus.restricted_states
	 		 columns
	 			restricted_state varchar2(254) path '.',
	 	  		seq_num for ordinality
	 		 ) restricted_states
	 	   where tmp_xml.filename like '%ITM%'
       	 	   group by skus.id
	 	) src
	 on(dest.sku_id=src.sku_id)
	 when matched then
	 	update set dest.restricted_locations=src.restricted_locations;

	 merge into tmp_sku_data dest
	 using (
	 	select 
	 		skus.id sku_id,
      	 		listagg(messages.message,'|') within group (order by skus.id,messages.seq_num) as pps_msg_ids
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      messages xmltype path 'MESSAGES/MESSAGE'
	 		 ) skus,
	 		 xmltable('/MESSAGE' passing skus.messages
	 		 columns
	 			message varchar2(254) path '.',
	 	  		seq_num for ordinality
	 		 ) messages
	 	   where tmp_xml.filename like '%ITM%'
       	 	   group by skus.id
	 	) src
	 on(dest.sku_id=src.sku_id)
	 when matched then
	 	update set dest.pps_msg_ids=src.pps_msg_ids;
	 	
	 merge into tmp_sku_data dest
	 using (
	 	select 
	 		skus.id sku_id,
      	 		listagg(upcs.upc,'|') within group (order by skus.id,upcs.seq_num) as upcs
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      upcs xmltype path 'UPCS/UPC'
	 		 ) skus,
	 		 xmltable('/UPC' passing skus.upcs
	 		 columns
	 			upc varchar2(254) path '.',
	 	  		seq_num number path '@seq'
	 		 ) upcs
	 	   where tmp_xml.filename like '%ITM%'
       	 	   group by skus.id
	 	) src
	 on(dest.sku_id=src.sku_id)
	 when matched then
	 	update set dest.upcs=src.upcs;	 	

	 merge into tmp_sku_data dest
	 using (
	 	select 
	 		skus.id sku_id,
      	 		listagg(shipping_surcharges.min_qty || ':' || nvl(shipping_surcharges.max_qty,999999) || ':' ||shipping_surcharges.surcharge,';') within group (order by skus.id,shipping_surcharges.min_qty) as shipping_surcharges
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      shipping_surcharges xmltype path 'SHIPPING_SURCHARGES/SHIPPING_SURCHARGE'
	 		 ) skus,
	 		 xmltable('/SHIPPING_SURCHARGE' passing skus.shipping_surcharges
	 		 columns
	 			min_qty number path 'MIN_QTY',
	 	  		max_qty number path 'MAX_QTY',
	 	  		surcharge number path 'SURCHARGE'
	 		 ) shipping_surcharges
	 	   where tmp_xml.filename like '%ITM%'
       	 	   group by skus.id
	 	) src
	 on(dest.sku_id=src.sku_id)
	 when matched then
	 	update set dest.shipping_surcharges=src.shipping_surcharges;
	 	
	 --SKU/ITEM Department Name
     merge into tmp_sku_department_data dest
	 using (
	 	select 
	 		skus.id sku_id, 
	 		skus.department_name department_name
	 	from tmp_xml,
	 		xmltable('/SKUs/SKU' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
                  department_name path 'DEPT_NAME'
	 		 ) skus
	 	   where tmp_xml.filename like '%ITM%'
	     	) src
	 	on (dest.sku_id=src.sku_id)
	 	when matched then
	 		update set dest.department_name=src.department_name
	 	when not matched then
	 		insert (sku_id,department_name)
	 		values(src.sku_id,src.department_name);	
	 		
    exception
    when others then
      raise;
  
  end;
  
  
  -- Merge into the tmp_prod_data table with data from tmp_xml
  procedure load_prod_data_into_tmp as
  begin
  	mff_logger.log_sp_info(PKG_NAME || '.load_prod_data_into_tmp', 'Merging in product data from XML');
	--execute immediate 'ALTER SESSION SET nls_timestamp_tz_format=''YYYY-MM-DDTZR''';

  	-- insert and update as needed from the raw xmltype of the data loaded for the current file
  	-- the data from each subsequent file is merge a top the data from the previous files, ensuring
  	--  that product data from newer extracts overrides data from older extracts
    merge into tmp_prod_data dest
      using (
	    select 
		p.prod_id product_id, 
		p.description description,
		to_date(p.activate_date,'YYYY-MM-DD') activate_date,
		to_date(p.deactivate_date,'YYYY-MM-DD') deactivate_date,
		to_date(p.teaser_startdate,'YYYY-MM-DD HH24:MI') teaser_startdate,
		to_date(p.teaser_enddate,'YYYY-MM-DD HH24:MI') teaser_enddate,
		p.no_of_alt_images no_of_alt_images,
		p.no_of_images no_of_images,
		p.fulfillment_method fulfillment_method,
		p.brand brand,
		p.romance_copy romance_copy,
		p.template template,
		case p.in_store_only when 'Y' then 1 when 'y' then 1 else 0 end in_store_only,
		case p.made_in_usa when 'Y' then 1 when 'y' then 1 else 0 end made_in_usa,
		case p.is_hide_price when 'Y' then 1 when 'y' then 1 else 0 end is_hide_price,
		p.minimum_age minimum_age,
		p.batch_id batch_id,
		1 prod_exists,			-- notes when a product already exists in CATA...  supports future queries
		0 create_new_product,		-- notes when a product is new and should be create on the Java side of the loader
		0 prod_change,			-- notes when a product exists, and the incoming feed has new data for that product...  update via SQL
		0 prod_remove,			-- notes when a product exists, and the "delete" flag has been newly assigned to that product...  support category updates via SQL
		0 orig_prod_remove,		-- notes when a non-clearance product needs to be removed from the hierarchy...  update categories via SQL
		0 prod_skus_change,		-- notes when a product exists, and the incoming feed has different SKUs for that product...  update products via SQL
		0 catg_change,			-- notes when a product exists and its catg has changed
		0 prod_has_new_skus, 		-- notes when a product exists, and the incoming feed has at least one *new* SKU for that product...  update products via Java
		0 picker_change
		from tmp_xml,
		  -- create pseudo-columns from the subtags of the Product tags of the xmltype
		  xmltable('/products/product' passing tmp_xml.xml_col
		    columns
			  prod_id path '@id',
			  description PATH 'description',
			  activate_date path 'activate_date',
			  deactivate_date path 'deactivate_date',
			  teaser_startdate path 'teaser_startdatetime',
			  teaser_enddate path 'teaser_enddatetime',
			  no_of_alt_images path 'no_of_alt_images',
			  no_of_images path 'no_of_images',
			  fulfillment_method path 'fulfillment_method',
			  is_hide_price path 'hide_price',
			  brand path 'brand',
			  romance_copy path 'romance_Copy',
			  template path 'template',
			  in_store_only path 'in_store_only',
			  made_in_usa path 'made_in_usa',
			  minimum_age path 'minimum_age',
			  batch_id path 'batch_id'
		    ) p
		where tmp_xml.filename like '%PRD%'
		) src
	    on (dest.product_id = src.product_id)
	    when matched then
		  -- Product already loaded from a previous file during this run, so overlay
      	  update set
		dest.description = src.description,
		dest.activate_date = src.activate_date,
		dest.deactivate_date=src.deactivate_date,
		dest.teaser_startdate=src.teaser_startdate,
		dest.teaser_enddate=src.teaser_enddate,
		dest.no_of_alt_images = src.no_of_alt_images,
		dest.no_of_images = src.no_of_images,
		dest.fulfillment_method = src.fulfillment_method,
		dest.is_hide_price = src.is_hide_price,
		dest.brand=src.brand,
		dest.romance_copy=src.romance_copy,
		dest.template=src.template,
		dest.in_store_only=src.in_store_only,
		dest.made_in_usa=src.made_in_usa,
		dest.minimum_age=src.minimum_age,
		dest.batch_id = src.batch_id,
		dest.prod_exists = src.prod_exists,
		dest.create_new_product = src.create_new_product,
		dest.prod_change = src.prod_change,
		dest.prod_remove = src.prod_remove,
		dest.prod_skus_change = src.prod_skus_change,
		dest.prod_has_new_skus = src.prod_has_new_skus,
		dest.picker_change=src.picker_change
	    when not matched then
		  -- We haven't encountered this product yet during this run, so add it to the table
	      insert (product_id, description, brand, romance_copy,template,in_store_only,made_in_usa,minimum_age,activate_date,deactivate_date, 
	      		teaser_startdate, teaser_enddate, no_of_alt_images, batch_id, 
	                no_of_images, fulfillment_method, is_hide_price,prod_exists, create_new_product,	
			prod_change, prod_remove, prod_skus_change, prod_has_new_skus,picker_change )
	      values (src.product_id, src.description, src.brand, src.romance_copy,src.template,src.in_store_only,src.made_in_usa,
	      		src.minimum_age,src.activate_date, src.deactivate_date,src.teaser_startdate, src.teaser_enddate,src.no_of_alt_images, 
	      		src.batch_id, src.no_of_images, src.fulfillment_method, src.is_hide_price, src.prod_exists, src.create_new_product,	
			src.prod_change, src.prod_remove, src.prod_skus_change, src.prod_has_new_skus,src.picker_change);
	
	 merge into tmp_prod_data dest
	 using (
	 	select 
	 		prods.id product_id,
      	 		listagg(selling_points.selling_point,'^;') within group (order by prods.id,selling_points.seq_num) as selling_points
	 	from tmp_xml,
	 		xmltable('/products/product' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      selling_points xmltype path 'selling_points/selling_point'
	 		 ) prods,
	 		 xmltable('/selling_point' passing prods.selling_points
	 		 columns
	 			selling_point varchar2(254) path '.',
	 	  		seq_num number path '@seq_no'
	 		 ) selling_points
	 	   where tmp_xml.filename like '%PRD%'
       	 	   group by prods.id
	 	) src
	 on(dest.product_id=src.product_id)
	 when matched then
	 	update set dest.selling_points=src.selling_points;

	 merge into tmp_prod_data dest
	 using (
	 	select 
	 		prods.id product_id,
      	 		listagg(choking_hazards.choking_hazard,'^') within group (order by prods.id,choking_hazards.seq_num) as choking_hazard
	 	from tmp_xml,
	 		xmltable('/products/product' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      choking_hazards xmltype path 'choking_hazards/choking_hazard'
	 		 ) prods,
	 		 xmltable('/choking_hazard' passing prods.choking_hazards
	 		 columns
	 			choking_hazard varchar2(254) path '.',
	 	  		seq_num for ordinality
	 		 ) choking_hazards
	 	   where tmp_xml.filename like '%PRD%'
       	 	   group by prods.id
	 	) src
	 on(dest.product_id=src.product_id)
	 when matched then
	 	update set dest.choking_hazard=src.choking_hazard;
	 	
	--tmp_prod_picker_data
	 merge into tmp_prod_picker_data dest
	 using (
	 	select 
	 		prods.id product_id, 
	 		(picker_labels.seq_num-1) seq_num, 
	 		picker_labels.picker_label
	 	from tmp_xml,
	 		xmltable('/products/product' passing tmp_xml.xml_col
	 		columns
	 		      id varchar2(40) path '@id',
	 		      picker_labels xmltype path 'picker_labels/picker_label'
	 		 ) prods,
	 		 xmltable('/picker_label' passing prods.picker_labels
	 		 columns
	 			picker_label varchar2(254) path '.',
	 	  		seq_num varchar2(254) path '@seq_no'
	 		 ) picker_labels
	 	   where tmp_xml.filename like '%PRD%'
	     	) src
	 	on (dest.product_id=src.product_id and dest.seq_num=src.seq_num)
	 	when matched then
	 		update set dest.picker_label=src.picker_label
	 	when not matched then
	 		insert (product_id,seq_num,picker_label)
	 		values(src.product_id,src.seq_num,src.picker_label);
			
	 merge into tmp_prod_catg_data dest
	 using (
	 	select 
	 		prods.id product_id, 
	 		(categories.seq_num-1) seq_num, 
	 		categories.category_id,
	     		case categories.primary_category when 'Y' then 1 when 'y' then 1 else 0 end primary_category,
	     		0 prod_exists,
	     		0 catg_exists
	 	from tmp_xml,
	 		xmltable('/products/product' passing tmp_xml.xml_col
	 		    columns
	 		      id varchar2(40) path '@id',
	 		      categories xmltype path 'categories/parent_category'
	 		) prods,
	 		xmltable('/parent_category' passing prods.categories
	 		    columns
	 		      	category_id varchar2(254) path '.',
	 	  		seq_num varchar2(254) path '@seq_no',
	 	  		primary_category varchar2(40) path '@primary_category'
	 		) categories
	 	  where tmp_xml.filename like '%PRD%'
	     	) src
	 on (dest.product_id=src.product_id and dest.category_id=src.category_id)
	 when matched then
	 	update set dest.primary_catg=src.primary_category,
	 		dest.seq_num=src.seq_num,
	 		dest.prod_exists=src.prod_exists,
	 		dest.catg_exists=src.catg_exists
	 when not matched then
	 	insert (product_id,seq_num,category_id,primary_catg,catg_exists,prod_exists)
	 	values(src.product_id,src.seq_num,src.category_id,src.primary_category,src.catg_exists,src.prod_exists);			
    -- exception
    -- when others then
    --  raise;
  end;

    -- Update 'changed', 'removed', 'exists' flags, etc in the tmp tables
  procedure post_process_tmp_data as
    sqlv  VARCHAR2(5000);
	cata  VARCHAR2(40);
  begin
  	-- Get the name of the cata schema
	select schema_name into cata from tmp_deploy_to_schemas where schema_id='cata';
	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Post processing TMP data - compares are against schema ' || cata);


	-- Update exists flags
	--

	-- Mark categories that don't yet exist for future queries
	sqlv := 'update tmp_catg_data set catg_exists=0,create_new_catg=1 where category_id not in (select category_id from ' || cata || '.dcs_category)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	
	-- Mark skus that don't yet exist for future queries
	sqlv := 'update tmp_sku_data set sku_exists=0,create_new_sku=1 where sku_id not in (select sku_id from ' || cata || '.dcs_sku)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	
	-- Mark products that don't yet exist for future queries
	sqlv := 'update tmp_prod_data set prod_exists=0,create_new_product=1 where product_id not in (select product_id from ' || cata || '.dcs_product)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	-- update category changed flags
	sqlv := 'update tmp_catg_data set catg_change=1 where category_id in ( ' ||
	  		'select tmp.category_id from tmp_catg_data tmp ' ||
	    			'inner join ' || cata || '.dcs_category c on c.category_id=tmp.category_id ' ||
	    		'where ( ' ||
	      			'(tmp.activation_date IS NOT NULL and tmp.activation_date != c.start_date) or ' ||
	      			'(tmp.deactivation_date IS NOT NULL and tmp.deactivation_date != c.end_date) or ' ||
	      			'(nvl(tmp.description,''ZZZ'') != nvl(c.display_name,''ZZZ''))))';
	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	sqlv := 'update tmp_catg_data dest ' ||
		'set orig_parent_id=(select category_id from ' || cata || '.dcs_cat_chldcat cc ' ||
			'where dest.category_id=cc.child_cat_id)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	sqlv := 'update tmp_catg_data set parent_catg_change=1 ' ||
			'where parent_id!=orig_parent_id';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
	
	sqlv := 'update tmp_catg_data set parent_catg_is_new=1 ' ||
			'where parent_id in ( ' ||
				'select category_id from tmp_catg_data where catg_exists=0)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

  	
	-- Update product changed flags so we know what data to copy over and what items to invalidate
	sqlv := 'update tmp_prod_data set prod_change=1 where product_id in ( ' ||
			  'select tmp.product_id from tmp_prod_data tmp ' ||
				'inner join ' || cata || '.dcs_product p on p.product_id = tmp.product_id ' ||
				'inner join ' || cata || '.mff_product mp on mp.product_id = tmp.product_id ' ||
				'where ( ' ||
					'(nvl(tmp.description,''ZZZ'') != nvl(p.display_name,''ZZZ'')) or ' ||
					'(tmp.description IS NOT NULL and nvl(tmp.description,''ZZZ'') != nvl(p.description,''ZZZ'')) or ' ||
					'dbms_lob.compare(nvl(tmp.romance_copy,''ZZZ''),nvl(p.long_description,''ZZZ'')) != 0 or ' ||
					'(nvl(trunc(tmp.activate_date),sysdate) != nvl(trunc(p.start_date),sysdate)) or ' ||
					'(nvl(trunc(tmp.deactivate_date),sysdate) != nvl(trunc(p.end_date),sysdate)) or ' ||
					'(nvl(trunc(tmp.teaser_startdate),systimestamp) != nvl(trunc(mp.teaser_startdate),systimestamp)) or ' ||
					'(nvl(trunc(tmp.teaser_enddate),systimestamp) != nvl(trunc(mp.teaser_enddate),systimestamp)) or ' ||					
					'(nvl(tmp.brand,''ZZZ'') != nvl(p.brand,''ZZZ'')) or ' ||
					'dbms_lob.compare(nvl(tmp.selling_points,''ZZZ''),nvl(mp.selling_points,''ZZZ'')) != 0 or ' ||
					'(nvl(tmp.choking_hazard,''ZZZ'') != nvl(mp.choking_hazard,''ZZZ'')) or ' ||
					'(nvl(tmp.template,''ZZZ'') != nvl(mp.picker_template,''ZZZ'')) or ' ||
					'(nvl(tmp.made_in_usa,0) != nvl(mp.is_made_in_usa,0)) or ' ||
					'(nvl(tmp.is_hide_price,0) != nvl(mp.is_hide_price,0)) or ' ||
					'(nvl(tmp.minimum_age,0) != nvl(mp.minimum_age,0)) or ' ||
					'(tmp.no_of_images != nvl(mp.num_images,0)) or ' ||
					'(nvl(tmp.fulfillment_method,0) != nvl(mp.fulfillment_method,0))))';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	-- Update product changed flags so we know what data to copy over and what items to invalidate
	sqlv := 'update tmp_prod_data set prod_change=1,picker_change=1 where product_id in ( ' ||
				'select distinct product_id from ( ' ||
					'(select product_id,seq_num,picker_label from tmp_prod_picker_data ' ||
						'minus ' ||
					'select product_id,to_number(attribute_name) seq_num,attribute_value picker_label from ' || cata || '.mff_product_attr ' ||
					')' ||
					'union all ' ||
					'(select product_id,to_number(attribute_name) seq_num,attribute_value picker_label from ' || cata || '.mff_product_attr ' ||
						'minus ' ||
					'select product_id,seq_num,picker_label from tmp_prod_picker_data) ' ||
				'))';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	

	-- Update changed flags on skus
	sqlv := 'update tmp_sku_data set sku_change=1 where sku_id in ( ' ||
			  'select tmp.sku_id from tmp_sku_data tmp ' ||
				'inner join ' || cata || '.dcs_sku s on s.sku_id = tmp.sku_id ' ||
				'inner join ' || cata || '.mff_sku ms on ms.sku_id = tmp.sku_id ' ||
			  	'where ( ' ||
					'(nvl(tmp.description,''ZZZ'') != nvl(s.display_name,''ZZZ'')) or ' ||
					'(nvl(tmp.description,''ZZZ'') != nvl(s.description,''ZZZ'')) or ' ||
					'(nvl(trunc(tmp.activate_date),sysdate) != nvl(trunc(s.start_date),sysdate)) or ' ||
					'(nvl(trunc(tmp.deactivate_date),sysdate) != nvl(trunc(s.end_date),sysdate)) or ' ||
					'(nvl(tmp.vpn,''ZZZ'') != nvl(ms.vpn,''ZZZ'')) or ' ||
					'(nvl(tmp.tax_code,''ZZZ'') != nvl(ms.tax_code,''ZZZ'')) or ' ||
					'(nvl(tmp.restricted_locations,''ZZZ'') != nvl(ms.restricted_locations,''ZZZ'')) or ' ||
					'(nvl(tmp.shipping_surcharges,''ZZZ'') != nvl(ms.shipping_surcharge_qnty_range,''ZZZ'')) or ' ||
					'(nvl(tmp.freight_class,''ZZZ'') != nvl(ms.freight_class,''ZZZ'')) or ' ||
					'(nvl(tmp.upcs,''ZZZ'') != nvl(ms.upcs,''ZZZ'')) or ' ||
					'(nvl(tmp.pps_msg_ids,''ZZZ'') != nvl(ms.pps_msg_ids,''ZZZ'')) or ' ||
					'(tmp.sku_length != nvl(ms.sku_length,0)) or ' ||
					'(tmp.girth != nvl(ms.girth,0)) or ' ||
					'(tmp.width != nvl(ms.width,0)) or ' ||
					'(tmp.weight != nvl(ms.weight,0)) or ' ||
					'(tmp.is_cube != nvl(ms.is_cube,0)) or ' ||
					'(tmp.sku_depth != nvl(ms.sku_depth,0)) or ' ||
					'(tmp.date_sensitive != nvl(ms.date_sensitive,0)) or ' ||
					'(tmp.hazardous != nvl(ms.hazardous,0)) or ' ||
					'(tmp.refrigerate != nvl(ms.refrigerate,0)) or ' ||
					'(tmp.oversized != nvl(ms.oversized,0)) or ' ||
					'(tmp.restrict_air != nvl(ms.restrict_air,0)) or ' ||
					'(tmp.ltl_fuel_surcharge != nvl(ms.ltl_fuel_surcharge,0)) or ' ||
					'(tmp.eds != nvl(ms.eds,0)) or ' ||
					'(tmp.ltl_lift_gate != nvl(ms.ltl_lift_gate,0)) or ' ||
					'(tmp.consumable != nvl(ms.consumable,0)) or ' ||
					'(tmp.clearance != nvl(ms.clearance,0)) or ' ||
					'(tmp.ltl_res_delivery != nvl(ms.ltl_res_delivery,0)) or ' ||
					'(tmp.vacinne != nvl(ms.vacinne,0)) or ' ||
					'(tmp.long_light != nvl(ms.long_light,0)) or ' ||
					'(tmp.freezable != nvl(ms.freezable,0)) or ' ||
					'(tmp.ormd != nvl(ms.ormd,0)) or ' ||
					'(tmp.free_shipping != nvl(ms.free_shipping,0)) or ' ||
					'(tmp.ltl != nvl(ms.ltl,0))) )';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	sqlv := 'update tmp_sku_data set sku_change=1,picker_change=1 where sku_id in ( ' ||
				'select distinct sku_id from ( ' ||
					'(select sku_id,seq_num,picker_label from tmp_sku_picker_data ' ||
						'minus ' ||
					'select sku_id,to_number(attribute_name) seq_num,attribute_value picker_label from ' || cata || '.dcs_sku_attr ' ||
					')' ||
					'union all ' ||
					'(select sku_id,to_number(attribute_name) seq_num,attribute_value picker_label from ' || cata || '.dcs_sku_attr ' ||
						'minus ' ||
					'select sku_id,seq_num,picker_label from tmp_sku_picker_data) ' ||
				'))';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	
	-- Mark categories that don't yet exist for future queries
	sqlv := 'update tmp_prod_catg_data set catg_exists=1 where category_id in (select category_id from ' || cata || '.dcs_category)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	-- Mark products that don't yet exist for future queries
	sqlv := 'update tmp_prod_catg_data set prod_exists=1 where product_id in (select product_id from ' || cata || '.dcs_product)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	sqlv := 'update tmp_catg_data set orig_parent_has_new_products=1 ' ||
			'where orig_parent_id in ( ' ||
				'select category_id from tmp_prod_catg_data where prod_exists=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;

	-- 
	sqlv := 'update tmp_catg_data set parent_has_new_products=1 ' ||
			'where parent_id in ( ' ||
				'select category_id from tmp_prod_catg_data where prod_exists=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	
	-- Mark products that don't yet exist for future queries
	sqlv := 'update tmp_sku_data set prod_exists=1 where product_id in (select product_id from ' || cata || '.dcs_product)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
	execute immediate sqlv;
	
	--
	-- check child skus and update prod_skus_change flags as appropriate
	-- Only active SKUs go into the list
	-- REMOVED prod_change=1, 10/11
	-- First query compares child SKUs from dcs_prd_chldsku with active SKUs in the feed for each product, and flags
	--   products where child SKUs are missing from tmp_sku_data
	sqlv := 'update tmp_prod_data p set prod_skus_change=1 where p.prod_exists=1 and p.product_id in ( ' ||
			  'select a.product_id from tmp_sku_data a ' ||
			    'full outer join ' || cata || '.dcs_prd_chldsku b on a.product_id = b.product_id and a.sku_id = b.sku_id ' ||
			    'where (a.sku_id is null or b.sku_id is null) and a.product_id is not null)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- Second query compares child SKUs from dcs_prd_chldsku with active SKUs in the feed for each product, and flags
	--   products where child SKUs are missing from dcs_prd_chldsku
	-- REMOVED prod_change=1, 10/11
	sqlv := 'update tmp_prod_data p set prod_skus_change=1 where p.prod_exists=1 and p.product_id in ( ' ||
			  'select b.product_id from tmp_sku_data a ' ||
			    'full outer join ' || cata || '.dcs_prd_chldsku b on a.product_id = b.product_id and a.sku_id = b.sku_id ' ||
			    'where (a.sku_id is null or b.sku_id is null) and b.product_id is not null)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- if an existing product points to a new sku, treat it as a new product...  it has to go through the Java update process
	sqlv := 'update tmp_prod_data set prod_has_new_skus=1 where product_id in ( ' ||
		'select distinct p.product_id from tmp_prod_data p ' ||
		  'inner join tmp_sku_data s on p.product_id = s.product_id ' ||
		  'where p.prod_exists=1 and s.create_new_sku=1)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
	

	-- mark skus that have been re-assigned to different products
	sqlv := 'update tmp_sku_data tmp ' ||
		'set prod_change = 1 where not exists ( ' ||
	    		'select * from ' || cata || '.dcs_prd_chldsku where ' ||
	      			'sku_id=tmp.sku_id ' ||
	      			'and product_id=tmp.product_id ' ||
			'and tmp.sku_exists=1 and tmp.create_new_sku=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- mark skus that have been re-assigned to different products
	sqlv := 'update tmp_sku_data tmp ' ||
		'set sku_has_new_prod = 1 where ' ||
			'tmp.prod_change=1 and tmp.prod_exists=0';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
  	sqlv := 'update tmp_prod_catg_data tmp ' ||
  			'set tmp.catg_change=1 where ' ||
				'(tmp.category_id,tmp.product_id) not in ( ' || 
					'select category_id,child_prd_id from ' || cata || '.dcs_cat_chldprd) ' ||
				'and tmp.prod_exists=1 and tmp.catg_exists=1';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- Maintain table to track old relationships that need to be deleted and resequenced
	-- For ex: Existing Product assigned to New Category. We have to delete the relationship
	-- of the product with the old category (only if indeed the relationship has been removed)
	
	-- We are essentially comparing new relationships with existing relationships to detect changes
	-- Minus apparently works better than joins
	-- Also, we have to detect change only when the prod already exists. For new products... there are no
	-- existing cat-prod relationships
	
	sqlv := 'merge into tmp_cat_prod_rel_change dest ' || 
		'using ( ' ||
		'select child_prd_id product_id,category_id category_id,''DELETE'' action ' ||
	        'from ' || cata || '.dcs_cat_chldprd where child_prd_id in ( ' ||
	        			'select distinct product_id from tmp_prod_catg_data where prod_exists=1) ' ||
		'minus ' ||
		'select product_id product_id,category_id category_id,''DELETE'' action ' ||
		'from tmp_prod_catg_data where product_id in ( ' ||
			'select distinct product_id from tmp_prod_catg_data where prod_exists=1) ' ||
		') src ' ||
		'on (src.product_id=dest.product_id and src.category_id=dest.category_id) ' ||
		'when matched then ' ||
		  'update set action =''DELETE'' ' ||
		'when not matched then ' ||
		  'insert (product_id,category_id,action) ' ||
  		  'values(src.product_id,src.category_id,src.action)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;


	-- Adding to this table... so we can resequence the affected categories
	-- and also track for java cache invalidations.
	-- Doing this only for categories that do not have new products. Those would be handled in
	-- Java.
	sqlv := 'merge into tmp_cat_prod_rel_change dest ' ||
		'using ( ' ||
			'select product_id product_id,category_id category_id,''ADD'' action ' ||
				'from tmp_prod_catg_data where catg_change=1 and category_id not in ( ' ||
					'select distinct category_id from tmp_prod_catg_data ' ||
						'where catg_exists=1 and prod_exists=0) ' ||
		') src ' ||
		'on(src.product_id=dest.product_id and src.category_id=dest.category_id) ' ||
		'when matched then ' ||
			'update set action =''ADD'' ' ||
		'when not matched then ' ||
			'insert (product_id,category_id,action) ' ||
			'values(src.product_id,src.category_id,src.action)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

  	
 	-- Repeat the above comparison for prd_chldsku relationships as well
 	-- NOTE: SKUs @ Fleet Farm can have exactly one product.
 	sqlv := 'merge into tmp_sku_orig_prod dest ' ||
 		'using ( ' ||
 			'select product_id,sku_id from ' || cata || '.dcs_prd_chldsku ' ||
 			' where sku_id in (select sku_id from tmp_sku_data ' ||
 				'where sku_exists=1 and prod_change=1) ' ||
	 		')src ' ||
 		'on(dest.sku_id=src.sku_id) ' ||
 		'when not matched then ' ||
 			'insert(sku_id,product_id) ' ||
 			'values(src.sku_id,src.product_id)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- This is the original set of sequenced child skus
	-- 1. Contains products that will lose a sku
	-- 2. Contains products that will receive a new sku
	-- This is prior to any processing of the feeds... current state if you will!
	sqlv := 'insert into tmp_prod_chldsku(product_id,sequence_num,sku_id) ' ||
		'select p.product_id, ' ||
			'(row_number() over (partition by p.product_id order by rownum))-1 sequence_num, ' ||
			's.sku_id ' ||
		 'from ' || cata || '.dcs_prd_chldsku ps, ' ||
		 	cata || '.dcs_product p, ' ||
		 	cata || '.dcs_sku s ' ||
		 'where p.product_id=ps.product_id ' ||
			'and s.sku_id=ps.sku_id ' ||
			'and (ps.product_id in (select product_id from tmp_sku_orig_prod) or ' ||
			     'p.product_id in (select product_id from tmp_sku_data where prod_change=1))';

  	-- mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	-- execute immediate sqlv;
	sqlv := 'insert into tmp_prod_chldsku(product_id,sequence_num,sku_id) ' ||
		'select ps.product_id, ' ||
			'ps.sequence_num, ' ||
			'ps.sku_id ' ||
		 'from ' || cata || '.dcs_prd_chldsku ps ' ||
		 'where (ps.product_id in (select product_id from tmp_sku_orig_prod) or ' ||
			     'ps.product_id in (select product_id from tmp_sku_data where prod_change=1))';

  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- Delete old prd-sku relationships
	sqlv := 'delete from tmp_prod_chldsku ' ||
		'where (product_id,sku_id) in ' ||
			'(select product_id,sku_id from tmp_sku_orig_prod)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- insert any new relationships
	sqlv := 'merge into tmp_prod_chldsku dest ' ||
		'using (select sku_id, product_id from tmp_sku_data) src ' ||
		'on (dest.sku_id=src.sku_id and dest.product_id=src.product_id) ' ||
		'when not matched then ' ||
			'insert (product_id,sequence_num,sku_id) ' ||
			'values(src.product_id,555,src.sku_id)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Sequenced final set of affected products
	sqlv := 'merge into tmp_prod_chldsku dest ' ||
		'using ( ' ||
  			'select product_id, ' || 
  				'(row_number() over (partition by product_id order by rownum))-1 sequence_num, ' ||
  				'sku_id ' ||
  			'from tmp_prod_chldsku ' ||
		') src ' ||
		'on (dest.product_id=src.product_id and dest.sku_id=src.sku_id) ' ||
		'when matched then ' ||
  			'update set dest.sequence_num=src.sequence_num';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	sqlv := 'update tmp_sku_data dest ' ||
		'set orig_product=(select product_id from ' || cata || '.dcs_prd_chldsku ps ' ||
			'where dest.sku_id=ps.sku_id)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	sqlv := 'update tmp_sku_data set prod_change=1 ' ||
			'where product_id != orig_product ' ||
			'and orig_product is not null'; 
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Get current list of skus for existing product with a new sku
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from ' || cata || '.dcs_prd_chldsku where product_id in ( ' ||
			'select distinct product_id from tmp_sku_data where sku_exists=0 and prod_exists=1)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Add the new sku relationship
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from tmp_sku_data where sku_exists=0 and prod_exists=1';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Add the new sku relationship
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from tmp_sku_data where sku_exists=1 and prod_exists=0';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Add the new sku relationship
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from tmp_sku_data where sku_exists=0 and prod_exists=0';
		
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- SKUs that were created without a product initially
	-- may not have a product association. They have to be added as well
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from tmp_sku_data where ' ||
			'sku_exists=1 and prod_exists=1 and prod_change=1 and orig_product is null';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Existing products' childSkus are updated using data from this table
	-- existing product that has a new SKU will be in Java
	-- any existing SKUs moving to this product will have to be part of the table
	-- else in Java, the product's childSKUs will not contain existing SKUs 
	-- moving to it
	sqlv := 'insert into tmp_chld_skus(product_id,sequence_num,sku_id) ' ||
		'select product_id,523,sku_id from tmp_sku_data where ' ||
			'sku_exists=1 and prod_exists=1 and prod_change=1 and orig_product is not null';
		
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- sku_exists=1 and prod_exists = 0
	-- New product with an existing SKU
	-- but the existing sku is moving from different existing product

	sqlv := 'delete from tmp_chld_skus ' ||
		'where (product_id,sku_id) in ( ' ||
			'select orig_product,sku_id from tmp_sku_data ' ||
				'where orig_product is not null and prod_change=1)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	sqlv := 'merge into tmp_chld_skus dest ' ||
		'using( ' ||
			'select product_id, ' ||
  				'(row_number() over (partition by product_id order by sequence_num))-1 sequence_num, ' ||
  				'sku_id ' ||
  			'from tmp_chld_skus) src ' ||
		 'on (dest.product_id=src.product_id and dest.sku_id=src.sku_id)  ' ||
		 'when matched then ' ||
  			'update set dest.sequence_num=src.sequence_num';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	

	-- get existing list of skus for affected products that are NOT going to be in Java
	-- the products here are products that will get a new sku
	sqlv := 'insert into tmp_chld_skus_headline(product_id,sequence_num,sku_id) ' ||
		'select product_id, ' ||
			'sequence_num, ' ||
			'sku_id ' ||
		'from ' || cata || '.dcs_prd_chldsku ' ||
		'where product_id in ( ' ||
			'select distinct product_id ' ||
			'from tmp_sku_data ' ||
			'where sku_exists=1 ' ||
				'and prod_exists=1 ' ||
				'and prod_change=1 ' ||
				'and product_id not in ( ' ||
					'select distinct product_id from tmp_sku_data where sku_exists=0))';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- add the new sku relationship
	sqlv := 'insert into tmp_chld_skus_headline(product_id,sequence_num,sku_id) ' ||
		'select product_id, ' ||
			'767, ' ||
			'sku_id ' ||
		'from tmp_sku_data ' ||
		'where sku_exists=1 ' ||
			'and prod_exists=1 ' ||
			'and prod_change=1 ' ||
			'and product_id not in ( ' ||
				'select distinct product_id from tmp_sku_data where sku_exists=0)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	-- get existing list of skus for affected products that are NOT going to be in Java
	-- the products here are products that will lose a sku
	sqlv := 'insert into tmp_chld_skus_headline(product_id,sequence_num,sku_id) ' ||
		'select product_id, ' ||
			'sequence_num, ' ||
			'sku_id ' ||
		'from ' || cata || '.dcs_prd_chldsku ' ||
		'where product_id in ( ' ||
			'select distinct orig_product ' ||
			'from tmp_sku_data ' ||
			'where sku_exists=1 ' ||
				'and prod_change=1 ' ||
				'and orig_product not in ( ' ||
					'select distinct product_id from tmp_sku_data where sku_exists=0)) ' ||
		'and product_id not in (select distinct product_id from tmp_chld_skus_headline)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
  	
	sqlv := 'delete from tmp_chld_skus_headline ' ||
		'where (sku_id,product_id) in ( ' ||
				'select sku_id, ' ||
					'orig_product ' ||
				'from tmp_sku_data ' ||
				'where sku_exists=1 ' ||
					'and prod_change=1 ' ||
					'and orig_product not in ( ' ||
						'select distinct product_id from tmp_chld_skus where sku_exists=0))';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
	commit;
  	sqlv := 'merge into tmp_chld_skus_headline dest ' ||
  		'using( ' ||
  		'select product_id, ' ||
      			'(row_number() over (partition by product_id order by sequence_num))-1 sequence_num, ' ||
      			'sku_id ' ||
  		'from tmp_chld_skus_headline) src ' ||
  		'on(dest.product_id=src.product_id and dest.sku_id=src.sku_id) ' ||
  		'when matched then ' ||
    			'update set dest.sequence_num=src.sequence_num';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;
    

	-- Create & maintain a clean sequence of cat-childcat relationships
	-- tmp_changed_cats maintains a list of categories that will need to be resequenced
	-- 
	-- ****  Use Case 1 ****
	-- catg exists(has no new prod), parent exists (no new prod), orig_parent (no new products)
	-- None of these would be in the BCC
	-- catg-parent reln needs to be added (parent needs to be resequenced)
	-- orig_parent-catg reln needs to be deleted (orig parent needs to be resequenced)
	-- 
	-- Affected categories are parent & orignal parent.
	-- Copy over their existing child cat relationships

	-- ****  Use Case 2 ****
	-- catg exists(has no new prod), parent exists (no new prod), orig_parent (has new products)
	-- Same as use case 1 except orig_parent will be in the BCC
	
	-- Parent categories that will receive a new child catg
	-- and that will not be in the BCC
	-- Use Case 1 & Use Case 2
	sqlv := 'merge into tmp_changed_cats dest ' ||
		'using ( ' ||
			'select parent_id from tmp_catg_data ' ||
			'where catg_exists=1 and ' ||
				'parent_has_new_products=0 and ' ||
				'parent_catg_change=1 and ' ||
				'parent_catg_is_new=0 ' ||
		') src ' ||
		'on(dest.category_id=src.parent_id) ' ||
		'when not matched then ' ||
			'insert(category_id) values (src.parent_id)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- orig_parent catg - It will lose a child catg
	-- and has to be resequenced (also not in the bcc)
	-- Use Case 1 (Use case 2 is handled in the BCC)
	sqlv := 'merge into tmp_changed_cats dest ' ||
		'using ( ' ||
			'select orig_parent_id from tmp_catg_data ' ||
			'where catg_exists=1 and ' ||
				'orig_parent_has_new_products=0 and ' ||
				'parent_catg_change=1 ' ||
		') src ' ||
		'on(dest.category_id=src.orig_parent_id) ' ||
		'when not matched then ' ||
			'insert(category_id) values (src.orig_parent_id)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Get the existing child categories of all the categories
	-- that need to be resequenced. This is the current state in CATA
	sqlv := 'insert into tmp_cat_chldcat(category_id,sequence_num,child_cat_id) ' ||
		'select category_id, ' ||
			'sequence_num, ' ||
			'child_cat_id ' ||
		'from ' || cata || '.dcs_cat_chldcat ' ||
		'where category_id in ( ' ||
			'select distinct category_id ' ||
			'from tmp_changed_cats)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Apply the changes we know of
	-- delete OLD parent-catg relns. 
	-- Use Case 1 (UC2 is in the BCC)
	sqlv := 'delete from tmp_cat_chldcat ' ||
		'where (category_id,child_cat_id) in ' ||
			'(select orig_parent_id, category_id from tmp_catg_data ' ||
				'where catg_exists=1 and ' ||
					'parent_catg_change=1 and ' ||
					'orig_parent_has_new_products=0)';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Insert NEW parent-catg relns. 
	-- Use Case 1
	sqlv := 'insert into tmp_cat_chldcat (category_id,sequence_num,child_cat_id) ' ||
		'select parent_id,333,category_id from tmp_catg_data ' ||
		'where catg_exists=1 and ' ||
			'parent_catg_change=1 and ' ||
			'parent_has_new_products=0 and ' ||
			'parent_catg_is_new=0';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Resequence at the end of all changes
	sqlv := 'merge into tmp_cat_chldcat dest ' ||
		'using ( ' ||
			'select cat.category_id, ' ||
				'(row_number() over (partition by cat.category_id order by cat.sequence_num))-1 sequence_num, ' ||
				'cat.child_cat_id ' ||
			'from tmp_cat_chldcat cat ' ||
		') src ' ||
		'on(dest.category_id=src.category_id and ' ||
			'dest.child_cat_id=src.child_cat_id and ' ||
			'dest.rowid=src.rowid) ' ||
		'when matched then ' ||
			'update set dest.sequence_num=src.sequence_num';
    	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || sqlv);
  	execute immediate sqlv;

	-- Commit this right away, since it's just tmp data and don't need it to be part of the main TX
	commit;

    exception
    when others then
      raise;
  end;

  -- XML file archiver...  files are moved via Java stored procedures into /archive subdirectory
  procedure archive_xml_files( p_dirpath varchar2) as
  	v_feedpath varchar2(200);
  begin
	-- Go through each of the feed files in tmp and archive them
  	for fileinfo in (select filename from tmp_xml_filenames where filename like 'ECOM_ITM_%' or filename like 'ECOM_CAT_%' or filename like 'ECOM_PRD_%') loop
  		v_feedpath := p_dirpath || '/' || fileinfo.filename;
  		mff_logger.log_sp_info( 'archive_xml_files', 'Archiving ' || v_feedpath);
  		mff_import_dir.archive_feed( v_feedpath);
  		mff_logger.log_sp_info( 'archive_xml_files', v_feedpath || ' is archived');
    end loop;
	
  end;
  
  procedure load_xml_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin
  	-- First, setup the directory from the package variable
	--v_sql := 'create or replace directory PIM_FEED_INCOMING as ''' || p_dirpath || '''';
	--execute immediate v_sql;

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames;
	commit;
	
	-- Populate the file list table using our Java stored procedure
	mff_logger.log_sp_info(PKG_NAME || '.load_xml_filenames', 'Loading XML filenames from directory');
	mff_import_dir.get_dir_list(p_dirpath);
	commit;
  end;
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('mff_catalog_loader', 'Initializing package mff_catalog_loader');
end mff_catalog_loader;