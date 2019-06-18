create or replace package mff_price_loader
as
	procedure run( p_dirpath varchar2);
	procedure setup_price_feed;
	procedure load_csv_filenames( p_dirpath varchar2);
	procedure load_file_into_tmp ( p_filename varchar2 );
	procedure update_prices;
	procedure cleanup_price_feed;
	procedure reset_price_data;
	procedure archive_files(p_dirpath varchar2, p_fileprefix varchar2);
	procedure delete_files(p_dirpath varchar2, p_fileprefix varchar2);
end mff_price_loader;
