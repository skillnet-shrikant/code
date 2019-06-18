create or replace package mff_planogram_loader
as
	procedure run( p_dirpath varchar2);
	procedure setup_planogram_feed;
	procedure load_xml_filenames( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure update_planogram;
	procedure cleanup_planogram_feed;
	procedure reset_planogram_data;
	procedure archive_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files(p_dirpath varchar2, p_fileprefix varchar2);
end mff_planogram_loader;
