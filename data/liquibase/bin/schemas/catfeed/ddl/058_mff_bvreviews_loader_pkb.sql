create or replace
package body mff_bvreviews_loader as

  PKG_NAME constant varchar2(50) := 'mff_bvreviews_loader';
  
  -- main procedure that performs all tasks related to bv facet loader import
  procedure run( p_dirpath varchar2) as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_bvreviews_feed;
  	load_xml_filenames(p_dirpath);
  	copy_tmp_to_all;
  end;
  
  -- Cleanup tables before and/or after we're done with bv reviews feed
  procedure cleanup_bvreviews_feed as
  begin
  	delete from tmp_xml_filenames;
  	delete from tmp_xml;
  	delete from tmp_bvreviews_data;
  	commit;
  end;
  
  -- Load XML file data into temp tables
  procedure load_xml_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames where filename like '%bv_fleetfarm_ratings%';
	delete from tmp_xml where filename like '%bv_fleetfarm_ratings%';
	
	mff_import_dir.get_dir_list(p_dirpath);
	dbms_output.put_line('DIR PATH - ' || p_dirpath);
	commit;

	--tmp_xml_filenames
	for o in (select * from tmp_xml_filenames where filename like '%bv_fleetfarm_ratings%')
	loop
	    dbms_output.put_line('Processing - ' || o.filename);
	    load_file_into_tmp(o.filename);
  	end loop;
  	commit;
  end;
  
  
  -- Load an XML file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  begin
  	-- Start by loading the raw file into tmp_xml

	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', 'Loading file ' || p_filename || ' into tmp_xml');

	-- Load the raw XML of the file into tmp_xml as an xmltype
  	insert into tmp_xml values ( p_filename, xmltype(bfilename('FACET_FEED_INCOMING', p_filename), nls_charset_id('UTF8')));
  	commit;

  	-- Now copy the actual product data into tmp tables
  	load_bv_prod_data_into_tmp;
    exception
    when others then
      raise;
	commit;
	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', 'completed successfully');
  end;

   -- XML file archiver...  files are moved via Java stored procedures into /archive subdirectory
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
 
    -- Merge into the tmp_prod_data table with data from tmp_xml
  procedure load_bv_prod_data_into_tmp as
  begin
  	mff_logger.log_sp_info(PKG_NAME || '.load_prod_data_into_tmp', 'Merging in bv product data from XML');
	--execute immediate 'ALTER SESSION SET nls_timestamp_tz_format=''YYYY-MM-DDTZR''';

  	-- insert and update as needed from the raw xmltype of the data loaded for the current file
  	-- the data from each subsequent file is merge a top the data from the previous files, ensuring
  	--  that product data from newer extracts overrides data from older extracts
    merge into tmp_bvreviews_data dest
      using (
	    select 
		p.ext_id as external_id, 
		p.overall_rating as overall_rating,
		p.overall_rating_syndicate as overall_rating_syndicate,
		case p.removed
			when 'true'
				then 1
			when 'false'
				then 0
			end as is_removed
		from tmp_xml,
		  -- create pseudo-columns from the subtags of the Product tags of the xmltype
		  xmltable(xmlnamespaces(default 'http://www.bazaarvoice.com/xs/PRR/SyndicationFeed/5.6'),'/Feed/Product' passing tmp_xml.xml_col
		    columns
			  ext_id path 'ExternalId',
			  overall_rating path 'NativeReviewStatistics/AverageOverallRating',
			  overall_rating_syndicate path 'ReviewStatistics/AverageOverallRating',
			  removed path '@removed'
		    ) p
		where tmp_xml.filename like '%bv_fleetfarm_ratings%'
		) src
	    on (dest.external_id = src.external_id)
	    when matched then
		  -- BV product review already loaded from a previous file during this run, so overlay
      	update set
		dest.overall_rating = src.overall_rating,
		dest.overall_rating_syndicate = src.overall_rating_syndicate,
		dest.is_removed=src.is_removed
	    when not matched then
		  -- We haven't encountered this product review yet during this run, so add it to the table
	      insert (external_id, overall_rating,overall_rating_syndicate,is_removed)
	      values (src.external_id, src.overall_rating,src.overall_rating_syndicate,src.is_removed);
	commit;
  end;
  
  -- Copy temp table bv feed data to schemas
  procedure copy_tmp_to_all as
  begin
  	-- Pull the list of schema names from the DB and deploy to each one
  	-- Go through all the other schemas in order
  	for schema in (select schema_name,is_versioned from tmp_deploy_to_schemas order by sequence_num) loop
  		if ( schema.is_versioned = 1) then
  			mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Just an empty message');
  		else
  			copy_tmp_to_schema( schema.schema_name);
  			mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Copied bv feed data to ' || schema.schema_name);
  		end if;
        	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_all', 'Copied bv feed data to ' || schema.schema_name);
    end loop;

    exception
    when others then
      raise;
    commit;
  end;
  
  -- Copy temp table bv feed data to pub
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
   commit;
  end;
  
  -- Push bv feed data to pub head version
   procedure copy_tmp_to_head( p_schema_prefix varchar2) as
   v_sql varchar2(5000);
   v_false varchar2(40);
   begin
    	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Copying feed data to mainline branch of versioned schema ' || p_schema_prefix);
    	dbms_output.put_line(PKG_NAME || '.copy_tmp_to_head');
		
    v_false := 'false';
	-- mff_product
	v_sql :=
	 'update ' || p_schema_prefix || 'mff_product p set ' ||
  		'(bv_reviews,bv_reviews_group) = ' ||
  		'(select  ' ||
    		'round(tmp.overall_rating,2),floor(tmp.overall_rating)' ||
   		'from (select external_id,(case when overall_rating_syndicate !=0 then overall_rating_syndicate else overall_rating end)overall_rating from tmp_bvreviews_data)tmp ' ||
    		'where tmp.external_id = p.product_id ' ||
  		')  ' ||
  	   'where exists ' ||
    	'(select product_id, asset_version from ' || p_schema_prefix || 'dcs_product pp ' ||
        'where (pp.is_head=1 and p.product_id = pp.product_id and p.asset_version = pp.asset_version ' ||
          'and pp.product_id in  ' ||
            '( select tmp2.external_id from tmp_bvreviews_data tmp2 where tmp2.is_removed=0)))';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_head', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	exception
    when others then
      raise;
    commit;
   end;
   
  -- Copy bv feed data to pub workspace
  procedure copy_tmp_to_workspace( p_schema_prefix varchar2, p_ws_id varchar2) as
  v_sql varchar2(5000);
  v_false varchar2(200);
  begin
	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Copying bv feed data to versioned schema ' || p_schema_prefix || ' and workspace ID ' || p_ws_id);
	-- mff_product
	-- Copy bv feed data to pub workspace
	v_false := 'false';
	v_sql :=
	  'update ' || p_schema_prefix || 'mff_product p set '||
		  '(bv_reviews,bv_reviews_group) ='||
		  '(select round(tmp.overall_rating,2),floor(tmp.overall_rating) '||
		   'from (select external_id,(case when overall_rating_syndicate !=0 then overall_rating_syndicate else overall_rating end)overall_rating from tmp_bvreviews_data)tmp '||
		    'where tmp.external_id = p.product_id) '||
        'where exists '||
    	'(select product_id, asset_version from ' || p_schema_prefix || 'dcs_product pp '||
         'where (pp.workspace_id=''' || p_ws_id || ''' and p.product_id = pp.product_id and p.asset_version = pp.asset_version '||
          'and pp.product_id in '||
            '( select tmp2.external_id from tmp_bvreviews_data tmp2 where tmp2.is_removed=0)))';
      	
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_workspace', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	exception
    when others then
      raise;
    commit;
  end;
  
  -- Copy bv data feed to schemas
  procedure copy_tmp_to_schema ( p_schema_name varchar2 ) as
  v_schema_prefix varchar2(80);
  v_sql varchar2(2000);
  v_false varchar2(200);
  begin
    if ( p_schema_name = '') then
      v_schema_prefix := '';
    else
      v_schema_prefix := p_schema_name || '.';
    end if;
    
    v_false := 'false';
    -- Do the updates to target schema...   this should all be in one TX
    mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Copying feed data to ' || p_schema_name);
    
    -- mff_product
	v_sql :=
	  'update ' || v_schema_prefix || 'mff_product p set ' ||
		  '(bv_reviews,bv_reviews_group) ='||
		  '(select round(tmp.overall_rating,2),floor(tmp.overall_rating) '||
		   'from (select external_id,(case when overall_rating_syndicate !=0 then overall_rating_syndicate else overall_rating end)overall_rating from tmp_bvreviews_data)tmp '||
		    'where tmp.external_id = p.product_id) '||
	  'where exists '||
	    '(select tmp2.external_id from tmp_bvreviews_data tmp2 where tmp2.is_removed=0)';
  	mff_logger.log_sp_info(PKG_NAME || '.copy_tmp_to_schema', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	exception
    when others then
      raise;
    commit;
  end;

  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('mff_bvreviews_loader', 'Initializing package mff_bvreviews_loader');
end mff_bvreviews_loader;