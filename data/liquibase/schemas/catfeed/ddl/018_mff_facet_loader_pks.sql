create or replace package mff_facet_loader
as
	procedure run( p_dirpath varchar2);
	procedure setup_facet_feed;
	procedure load_xml_filenames( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure update_facets;
	procedure cleanup_facet_feed;
	procedure archive_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files(p_dirpath varchar2, p_fileprefix varchar2);
end mff_facet_loader;