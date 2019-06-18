create or replace package mff_catalog_loader
as
	procedure setup_catfeed;
	procedure load_xml_filenames( p_dirpath varchar2);
	procedure archive_xml_files( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure load_prod_data_into_tmp;
	-- procedure load_prod_selling_points;
	procedure load_sku_data_into_tmp;
	procedure load_catg_data_into_tmp;
	procedure post_process_tmp_data;
	procedure copy_tmp_to_all;
	procedure copy_tmp_to_pub ( p_schema_name varchar2 );
	procedure copy_tmp_to_head ( p_schema_prefix varchar2 );
	procedure copy_tmp_to_workspace ( p_schema_prefix varchar2, p_ws_id varchar2 );
	procedure copy_tmp_to_schema ( p_schema_name varchar2 );
	procedure copy_departmentdata_to_core;
	procedure cleanup_catfeed;
end mff_catalog_loader;