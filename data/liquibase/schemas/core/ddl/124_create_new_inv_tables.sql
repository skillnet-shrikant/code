create table ff_inventory ( 
	inventory_id   			varchar2(40 byte) not null enable,
	version        			number(38,0) not null enable,
	inventory_lock 			varchar2(20 byte),
	creation_date 			timestamp (6),
	start_date 				timestamp (6),
	end_date 				timestamp (6),
	display_name   			varchar2(254 byte),
	description    			varchar2(254 byte),
	catalog_ref_id 			varchar2(40 byte) not null enable,
	avail_status			number(38,0) not null enable,
	availability_date 		timestamp (6),
	stock_level      		number(38,0) default 0,
	stock_level_with_fraction	number(19,7),
	backorder_level  			number(38,0),
	backorder_level_with_fraction	number(19,7),
	preorder_level   				number(38,0),
	preorder_level_with_fraction	number(19,7),
	stock_thresh     				number(38,0),
	backorder_thresh 				number(38,0),
	preorder_thresh  				number(38,0),
	location_id						varchar2(40 byte),
	constraint ff_inventory_p primary key (inventory_id) enable,
	constraint ff_inventory_idx unique (catalog_ref_id) enable
); 

create table ff_inventory_transaction ( 
	inventory_id   		varchar2(40 byte) not null enable,
	inventory_lock 		varchar2(20 byte),
	sold				number(19),
	allocated			number(19),
	shipped				number(19),
	constraint ff_inventory_transaction_p primary key (inventory_id) enable
); 

create table ff_store_inventory (
	inventory_id   		varchar2(40 byte) not null enable,
	store_id   			varchar2(40 byte) not null enable,
	catalog_ref_id 		varchar2(40 byte) not null enable,
	stock_level      	number(38,0) default 0,
	creation_date 		timestamp (6),
	last_update_date	timestamp (6),
	eod_stock_level		number(19),	
	is_damaged	number(1) default 0,
	inventory_lock 			VARCHAR2(20),
	constraint ff_store_inventory_p primary key (inventory_id) enable
);

CREATE INDEX ff_store_inventory_idx1 ON ff_store_inventory (catalog_ref_id);
CREATE INDEX ff_str_inv_cat_ref_store_idx ON ff_store_inventory (catalog_ref_id,store_id);


create table ff_store_inv_transaction (
	inventory_id   		varchar2(40 byte) not null enable,
	store_id   			varchar2(40 byte) not null enable,
	inventory_lock 		varchar2(20 byte),
	allocated			number(19),
	shipped				number(19),	
	constraint ff_store_inv_transaction_p primary key (inventory_id) enable
);
