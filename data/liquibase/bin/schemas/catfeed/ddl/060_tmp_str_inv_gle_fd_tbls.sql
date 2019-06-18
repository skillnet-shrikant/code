create table gle_lcl_st_inv_fd_onsale
(
  inventory_id      		varchar2(40) NOT NULL,
  store_code				varchar2(40) NULL,
  item_id				  	varchar2(40) NULL,
  price						varchar2(40) NULL,
  quantity					NUMBER	NULL,
  quantity_par				NUMBER	NULL,
  pickup_method			  	varchar2(40) NULL,
  product_display_name		varchar2(2000) NULL,
  product_id				varchar2(40) NULL,
  pickup_sla				varchar2(40) NULL,
  pickup_link_template		varchar2(2000) NULL,
  PRIMARY KEY (inventory_id));
  
  
create table gle_lcl_st_inv_fd_regular
(
  inventory_id      		varchar2(40) NOT NULL,
  store_code				varchar2(40) NULL,
  item_id				  	varchar2(40) NULL,
  price						varchar2(40) NULL,
  quantity					NUMBER	NULL,
  quantity_par				NUMBER	NULL,
  pickup_method			  	varchar2(40) NULL,
  product_display_name		varchar2(2000) NULL,
  product_id				varchar2(40) NULL,
  pickup_sla				varchar2(40) NULL,
  pickup_link_template		varchar2(2000) NULL,
  PRIMARY KEY (inventory_id));
  
  create index gle_lclfd_onsale_strcd on gle_lcl_st_inv_fd_onsale(store_code);
  create index gle_lclfd_onsale_itemid on gle_lcl_st_inv_fd_onsale(item_id);
  create index gle_lclfd_onsale_prdid on gle_lcl_st_inv_fd_onsale(product_id);
  
  create index gle_lclfd_regular_strcd on gle_lcl_st_inv_fd_regular(store_code);
  create index gle_lclfd_regular_itemid on gle_lcl_st_inv_fd_regular(item_id);
  create index gle_lclfd_regular_prdid on gle_lcl_st_inv_fd_regular(product_id);
  

