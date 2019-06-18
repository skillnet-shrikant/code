create or replace package mff_employee_loader
as
	procedure run( p_dirpath varchar2);
	procedure setup_employee_feed;
	procedure post_process_tmp_data;
	procedure load_csv_filenames( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure update_employees;
	procedure cleanup_employee_feed;
	procedure reset_employee_data;
	procedure archive_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files(p_dirpath varchar2, p_fileprefix varchar2);
end mff_employee_loader;
