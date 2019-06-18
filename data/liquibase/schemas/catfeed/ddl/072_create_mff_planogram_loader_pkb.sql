create or replace package body mff_planogram_loader as

  PKG_NAME constant varchar2(50) := 'mff_planogram_loader';
  -- main procedure that performs all tasks related to price import
  procedure run( p_dirpath varchar2) as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_planogram_feed;
  	load_xml_filenames(p_dirpath);
  	update_planogram;
  end;
  
  -- Setup tables we need for price loading
  procedure setup_planogram_feed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_planogram_feed;
  end;
  
  
  -- Cleanup tables before and/or after we're done with planogram_feed
  procedure cleanup_planogram_feed as
  v_sql varchar2(100);
  begin
  	-- truncating because we simply want to empty the tmp table
  	-- delete is slower due to logging
  	v_sql:='truncate table tmp_item_planogram';
    	execute immediate v_sql;
  	commit;
  end;

  -- Load an XML file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  v_sql varchar2(2000);
  begin
  	
	--mff_import_message.message_output_debug('load_file_into_tmp', 'deleting tmp_xml');
  	-- Clear out old data from tmp_item_planogram...  we're merging one file at a time into tmp_price
  	delete from tmp_xml;
  	commit;
	
	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', 'Loading file ' || p_filename || ' into tmp_xml');

	-- Load the raw XML of the file into tmp_xml as an xmltype
  	insert into tmp_xml values ( p_filename, xmltype(bfilename('PIM_FEED_INCOMING', p_filename), nls_charset_id('UTF8')));
  	commit;
	
	-- Extract xml into temp table
	insert into tmp_item_planogram (item_id, store_id, planogram_name, planogram_address, serialNumber) select skuLocation.item_id,storeskus.store_id,planogram.planogram_name,planogram.planogram_address,planogram.serialnumber from tmp_xml,
	xmltable('/STORES/STORE' passing tmp_xml.xml_col columns store_id varchar2(40) path '@NUMBER', sku xmltype path 'SKU') storeskus,
	xmltable('/SKU' passing storeskus.sku columns item_id varchar2(40) path '@ID', location xmltype path 'LOCATION')skuLocation,
	xmltable('/LOCATION' passing skuLocation.location columns serialnumber varchar2(40) path '@SERIAL', planogram_name varchar2(120) path 'POG_NAME', planogram_address varchar2(40) path 'ADDRESS')planogram;
	commit;
  	
    exception
    when others then
      rollback;
      dbms_output.put_line('ERROR!!!!!!!!!!! - ');
      raise;
  end;

  procedure load_xml_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin
  	-- First, setup the directory from the package variable
	-- v_sql := 'create or replace directory planogram_FEED_INCOMING as ''' || p_dirpath || '''';
	-- execute immediate v_sql;

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames where upper(filename) like 'ITEM_STORE_ATTR%';
	commit;
	
	-- Populate the file list table using our Java stored procedure
	-- exp_import_message.message_output_debug('load_xml_filenames', 'Loading XML filenames from directory');
	-- pass in the inventory feed file path /vagrant/csv/pricing
	mff_import_dir.get_dir_list(p_dirpath);
	dbms_output.put_line('DIR PATH - ' || p_dirpath);
	commit;
	--tmp_xml_filenames
	for o in (select * from tmp_xml_filenames where upper(filename) like 'ITEM_STORE_ATTR%')
	loop
	    dbms_output.put_line('Processing - ' || o.filename);
	    load_file_into_tmp(o.filename);
  	end loop;
  end;

  -- clean up all inventory related tables

  procedure update_planogram as
	cnt number;
		begin
			select count(*) into cnt from tmp_item_planogram;
			if cnt > 0 then
				delete from atg_core.item_planogram;
				insert into atg_core.item_planogram (planogram_id, item_id, store_id, planogram_name,planogram_address,serialNumber) select 'pg-' || src.item_id ||src.store_id ||src.serialNumber, src.item_id, src.store_id,	src.planogram_name,	src.planogram_address, src.serialNumber from tmp_item_planogram src;
				commit;
			end if;
		end;
		
  -- file archiver...  files are moved via Java stored procedures into /archive subdirectory
  procedure archive_files( p_dirpath varchar2, p_fileprefix varchar2) as
  	v_feedpath varchar2(200);
  begin
	-- Go through each of the feed files in tmp and archive them
  	for fileinfo in (select filename from tmp_xml_filenames where filename like lower('%'||p_fileprefix||'%')) loop
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
  procedure reset_planogram_data as
  v_sql varchar2(100);
  begin
  	-- Cleanup all tmp tables
  	cleanup_planogram_feed;
  	
  	-- clean up master data. 
	delete from atg_core.item_planogram;
  	commit;
  end;
  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info(PKG_NAME, PKG_NAME || ' instance loaded.');
end mff_planogram_loader;