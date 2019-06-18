create table google_feed_data_onsale
(
  category_id      			varchar2(40) NULL,
  google_category_id		varchar2(40) NULL,
  google_category_name  	varchar2(2000) NULL,
  product_id				varchar2(40) NULL,
  brand						varchar2(255) NULL,
  product_display_name	  	varchar2(255) NULL,
  product_selling_points	varchar2(2000) NULL,
  product_num_images		NUMBER NULL,
  sku_id					varchar2(40) NOT NULL,
  upcs						varchar2(2000) NULL,
  ltl						NUMBER default(0),
  on_sale					NUMBER default(0),
  clearance					NUMBER default(0),
  stock_level				NUMBER NULL,
  list_price				NUMBER NULL,
  promo_id					varchar2(40) NULL,
  is_hide_price				NUMBER default(0),
  PRIMARY KEY (sku_id));
  
  
create table google_feed_data_regular
(
  category_id      			varchar2(40) NULL,
  google_category_id		varchar2(40) NULL,
  google_category_name  	varchar2(2000) NULL,
  product_id				varchar2(40) NULL,
  brand						varchar2(255) NULL,
  product_display_name	  	varchar2(255) NULL,
  product_selling_points	varchar2(2000) NULL,
  product_num_images		NUMBER NULL,
  sku_id					varchar2(40) NOT NULL,
  upcs						varchar2(2000) NULL,
  ltl						NUMBER default(0),
  on_sale					NUMBER default(0),
  clearance					NUMBER default(0),
  stock_level				NUMBER NULL,
  list_price				NUMBER NULL,
  promo_id					varchar2(40) NULL,
  is_hide_price				NUMBER default(0),
  PRIMARY KEY (sku_id));

