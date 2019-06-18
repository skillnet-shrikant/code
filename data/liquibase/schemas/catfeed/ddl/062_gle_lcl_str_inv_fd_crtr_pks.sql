create or replace package gle_lcl_str_inv_fd_crtr AUTHID CURRENT_USER
as
	procedure clean_gle_str_inv_fd;
	procedure load_gle_str_inv_fd;
	procedure load_onsale;
	procedure load_regular;
	procedure load_onsale_skus_inv;
	procedure load_regular_skus_inv;
	procedure del_onsale_skus_not_active;
	procedure del_regular_skus_not_active;
	procedure load_onsale_skus_price;
	procedure load_regular_skus_price;
	procedure load_onsale_skus_prdinfo;
	procedure load_regular_skus_prdinfo;
	procedure del_onsale_skus_noprd_pkup;
	procedure del_regular_skus_noprd_pkup;
	procedure load_onsale_skus_inv_par;
	procedure load_regular_skus_inv_par;
	procedure merge_onsale_inv;
	procedure merge_regular_inv;
	procedure run_full;
	procedure run_partial;
	procedure merge_inv;
end gle_lcl_str_inv_fd_crtr;