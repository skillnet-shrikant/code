create or replace
package body mff_employee_loader as

  PKG_NAME constant varchar2(50) := 'mff_employee_loader';
  -- main procedure that performs all tasks related to employee import
  procedure run( p_dirpath varchar2) as
  lRecCount		number;
  v_sql 		varchar2(100);
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_employee_feed;
  	load_csv_filenames(p_dirpath);
  	post_process_tmp_data;
  	
  	v_sql := 'select count(*) from tmp_emp_csv';
  	execute immediate v_sql into lRecCount;
  	if(lRecCount > 0) then
  		update_employees;
  	else
  		mff_logger.log_sp_info(PKG_NAME || '.run', 'No data sent in the feed.');
  	end if;
  end;
  
  -- Setup tables we need for employee loading
  procedure setup_employee_feed as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_employee_feed;
  end;
  
  
  -- Cleanup tables before and/or after we're done with employee feed
  procedure cleanup_employee_feed as
  v_sql varchar2(100);
  begin
  	-- truncating because we simply want to empty the tmp table
  	-- delete is slower due to logging
  	v_sql:='truncate table tmp_emp_csv';
    	execute immediate v_sql;
  	commit;
  end;

  -- Load file into catfeed tmp tables
  procedure load_file_into_tmp ( p_filename varchar2 ) as
  v_sql varchar2(2000);
  begin
    	
  	-- alter external table defn so it can load the new CSV file
  	v_sql:='alter table tmp_ext_emp_csv location (''' || p_filename || ''')';
  	mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', ' Processing file : ' || p_filename);
	execute immediate v_sql;  	
  	commit;
	
	-- Load the csv data from external table into a tmp table
    	
    	v_sql:='merge into tmp_emp_csv dest ' ||
    		'using ( ' ||
    		     'select employee_id, ' ||
    			'som_card, ' ||
    			'replace(phone_number,''-'','''') phone_number ' ||
    		      'from tmp_ext_emp_csv ' ||
    		 ') src ' ||
    		 'on(dest.employee_id=src.employee_id) ' ||
    		 'when matched then ' ||
    		 	'update set dest.som_card=src.som_card, ' ||
    		 		'dest.phone_number=src.phone_number ' ||
    		 'when not matched then ' ||
    		 	'insert (dest.employee_id, ' ||
    		 		'dest.som_card, ' ||
    		 		'dest.phone_number) ' ||
    		 	'values(src.employee_id, ' ||
    		 		'src.som_card, ' ||
    		 		'src.phone_number)';
		mff_logger.log_sp_info(PKG_NAME || '.load_file_into_tmp', ' Loading into tmp_emp_csv : ' || v_sql);
		
		execute immediate v_sql;

  	commit;
  	
    exception
    when others then
      rollback;
      dbms_output.put_line('ERROR!!!!!!!!!!! - ');
      raise;
  end;

  procedure post_process_tmp_data as
    v_sql varchar2(5000);
  begin
	-- update employee changed flags
	v_sql := 'update tmp_emp_csv set is_changed=1 where employee_id in ( ' ||
	  		'select tmp.employee_id from tmp_emp_csv tmp ' ||
	    			'inner join atg_core.mff_employee e on e.employee_id=tmp.employee_id ' ||
	    		'where ( ' ||
	      			'(nvl(tmp.som_card,''ZZZ'') != nvl(e.som_card,''ZZZ'')) or ' ||
	      			'(nvl(tmp.phone_number,''ZZZ'') != nvl(e.phone_number,''ZZZ''))))';
	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;
	
	v_sql := 'update tmp_emp_csv set is_new=1 where employee_id not in (select employee_id from atg_core.mff_employee)';
  	mff_logger.log_sp_info(PKG_NAME || '.post_process_tmp_data', 'Execute SQL -- ' || v_sql);
	execute immediate v_sql;	
  end;
  
  procedure load_csv_filenames ( p_dirpath varchar2) as
	v_sql varchar2(254);
  begin

	-- Clear out the current list of filenames, first
	delete from tmp_xml_filenames where filename like 'ECOM_EMP%';
	commit;
	
	-- Populate the file list table using our Java stored procedure
	-- pass in the employee feed file path /vagrant/csv/employee
	mff_import_dir.get_dir_list(p_dirpath);
	dbms_output.put_line('DIR PATH - ' || p_dirpath);
	commit;
	--tmp_xml_filenames
	for o in (select * from tmp_xml_filenames where filename like 'ECOM_EMP%')
	loop
	    dbms_output.put_line('Processing - ' || o.filename);
	    load_file_into_tmp(o.filename);
  	end loop;
  end;

  
  procedure update_employees as
    v_sql varchar2(5000);
    begin
    
        -- update employees
    	v_sql := 'merge into atg_core.mff_employee dest ' ||
   		 'using ( ' ||
   		 	'select employee_id ' ||
   		 		',som_card ' ||
   		 		',phone_number ' ||
   		 	'from tmp_emp_csv ' ||
   		 	'where is_changed=1 or is_new=1 ' ||
   		 	') src ' ||
   		  'on (dest.employee_id = src.employee_id) ' ||
   		  'when matched then ' ||
   			'update set dest.som_card = src.som_card, ' ||
   				'dest.phone_number=src.phone_number, ' ||
   				'dest.last_update_date=systimestamp ' ||
   		  'when not matched then ' ||
   			'insert (dest.employee_id ' ||
   				',dest.som_card ' ||
   				',dest.phone_number ' ||
   				',dest.creation_date) ' || 
   			'values (src.employee_id, ' ||
   				'src.som_card, ' ||
   				'src.phone_number, ' ||
   				'systimestamp)';
   	
   	mff_logger.log_sp_info(PKG_NAME || '.update_employees', ' Merge into mff_employee : ' || v_sql);
	execute immediate v_sql;

	mff_logger.log_sp_info(PKG_NAME || '.update_employees', ' Merged ' || to_char(SQL%ROWCOUNT) || ' record(s)');
	
	
   	v_sql := 'delete from atg_core.mff_employee where employee_id not in (select employee_id from tmp_emp_csv) ';
   	mff_logger.log_sp_info(PKG_NAME || '.update_employees', ' delete from mff_employee : ' || v_sql);
	execute immediate v_sql;
	mff_logger.log_sp_info(PKG_NAME || '.update_employees', ' Deleted ' || to_char(SQL%ROWCOUNT) || ' record(s)');
	
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
  
  -- clean up all employee related tables
  procedure reset_employee_data as
  v_sql varchar2(100);
  begin
  	-- Cleanup all tmp tables
  	cleanup_employee_feed;
  	
  	-- clean up master data. 
	delete from atg_core.mff_employee;

  	commit;
  end;
  
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info(PKG_NAME, PKG_NAME || ' instance loaded.');
end mff_employee_loader;
