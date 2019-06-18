create or replace package mff_bvreviews_loader
as
	procedure cleanup_bvreviews_feed;
	procedure load_xml_filenames( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure archive_files( p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files( p_dirpath varchar2, p_fileprefix varchar2);
	procedure load_bv_prod_data_into_tmp;
	procedure copy_tmp_to_all;
	procedure copy_tmp_to_pub ( p_schema_name varchar2 );
	procedure copy_tmp_to_head ( p_schema_prefix varchar2 );
	procedure copy_tmp_to_workspace ( p_schema_prefix varchar2, p_ws_id varchar2 );
	procedure copy_tmp_to_schema ( p_schema_name varchar2 );
	procedure run( p_dirpath varchar2);
end mff_bvreviews_loader;