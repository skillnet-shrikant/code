create or replace
package body mff_facet_loader as

  PKG_NAME constant varchar2(50) := 'mff_facet_loader';
  
  -- main procedure that performs all tasks related to facet import
  procedure run( p_dirpath varchar2) as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_facet_feed;
  	load_xml_filenames(p_dirpath);
  	update_facets;
  end;
  
  -- Setup tables we need for facet loading
  procedure setup_facet_feed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_facet_feed;
  end;
  
  
  -- Cleanup tables before and/or after we're done with price_feed
  procedure cleanup_facet_feed as
  v_sql varchar2(100);
  begin
  	-- truncating because we simply want to empty the tmp table
  	-- delete is slower due to logging
  	v_sql:='truncate table tmp_facet_data';
    	execute immediate v_sql;
  end;

  -- Load an XML file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  v_sql varchar2(200);
  begin

	-- Load the raw XML of the file into tmp_xml as an xmltype
  	insert into tmp_xml values ( p_filename, xmltype(bfilename('FACET_FEED_INCOMING', p_filename), nls_charset_id('UTF8')));

    	merge into tmp_facet_data dest
      	using (
	     select 
		  p.item as sku_id, 
		  '/mff/facets/SearchFacet_skuSearchFacet_' || p.facet as facet_name,
		  p.value as facet_value,
      		  p.action as action,
		  tmp_xml.filename xml_file_name
	     from tmp_xml,
		  -- create pseudo-columns from the subtags of the Product tags of the xmltype
		  xmltable('/item_facets/item_facet' passing tmp_xml.xml_col
		    columns
			  item path '@item',
			  facet path '@facet',
			  value path '@value',
        		  action path '@action'
		    ) p
	     where tmp_xml.filename like 'ECOM_FACET%'
	 ) src
	 on (dest.sku_id = src.sku_id and dest.facet_name=src.facet_name)
	 when matched then
		  update set dest.facet_value = src.facet_value,
        		dest.action=src.action
	 when not matched then
		  insert (sku_id, facet_name, facet_value,action)
		  values (src.sku_id, src.facet_name, src.facet_value,src.action);

    exception
    when others then
      rollback;
      dbms_output.put_line('ERROR!!!!!!!!!!! - ');
      raise;
  end;

  procedure load_xml_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames where filename like 'ECOM_FACET%';
	delete from tmp_xml where filename like 'ECOM_FACET%';
	
	mff_import_dir.get_dir_list(p_dirpath);
	dbms_output.put_line('DIR PATH - ' || p_dirpath);
	commit;

	--tmp_xml_filenames
	for o in (select * from tmp_xml_filenames where filename like 'ECOM_FACET%')
	loop
	    dbms_output.put_line('Processing - ' || o.filename);
	    load_file_into_tmp(o.filename);
  	end loop;
  	commit;
  end;

  -- update main facet tables

  procedure update_facets as
    lIncremental number(5,0);
    v_sql varchar2(2000);
    begin
	dbms_output.put_line('Updating facets');

     	v_sql := 'select count(*) ' ||
     		 'from tmp_xml, ' ||
	  		'xmltable(''/item_facets'' passing tmp_xml.xml_col ' ||
	    		'columns ' ||
		  		'proc_type path ''@type''' ||
	    		') p ' ||
     		 'where tmp_xml.filename like ''ECOM_FACET%'' and p.proc_type=''full''';
     	
     	dbms_output.put_line('Executing ' || v_sql);
     	execute immediate v_sql into lIncremental;
     	
     	if lIncremental > 0 then
     		dbms_output.put_line('Incremental = ' || lIncremental || '. Truncate & insert');
     		delete from atg_core.mff_sku_facet;
     		delete from atg_core.mff_sf_dyn_prop_map_str;
     	end if;

	merge into atg_core.mff_sku_facet dest
	using (select distinct sku_id as sku_id
		from tmp_facet_data
		where action in ('I','U')
	) src
	on(src.sku_id=dest.sku_id)
	when not matched then
		insert(sku_id)
		values(src.sku_id);
		
	merge into atg_core.mff_sf_dyn_prop_map_str dest
	using (select sku_id as id,
		 	facet_name as prop_name,
		 	facet_value as prop_value
		from tmp_facet_data
		where action in ('I','U')
	) src
	on(src.id=dest.id and src.prop_name=dest.prop_name)
	when matched then
		update set dest.prop_value=src.prop_value
	when not matched then
		insert(id,prop_name,prop_value)
		values(src.id,src.prop_name,src.prop_value);
	
	delete from atg_core.mff_sf_dyn_prop_map_str
	where (id,prop_name) in (
		select sku_id,facet_name from tmp_facet_data
		where action='D');
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
  procedure reset_facet_data as
  v_sql varchar2(100);
  begin
  	-- Cleanup all tmp tables
  	cleanup_facet_feed;
  	
  	-- clean up master data. 

  end;
  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info(PKG_NAME, PKG_NAME || ' instance loaded.');
end mff_facet_loader;
