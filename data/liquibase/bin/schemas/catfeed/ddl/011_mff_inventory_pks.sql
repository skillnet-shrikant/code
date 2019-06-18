create or replace package mff_inventory_loader
as
	procedure setup_inv_feed;
	procedure load_csv_filenames( p_dirpath varchar2, p_delta int);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure update_inventory (p_delta int, p_reset_damaged int);
	-- procedure reset_counters;
	procedure cleanup_inv_feed;
	procedure reset_import_data;
	procedure run( p_dirpath varchar2, p_delta int, p_reset_damaged int);
	procedure archive_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure insert_trans_records;
	procedure reset_shipped_counter;
	procedure adjust_allocation_count;
end mff_inventory_loader;
