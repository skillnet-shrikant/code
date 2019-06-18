create table sku_Inventory (	
	sku_id			varchar2(40),
	row_fullfillment_type	varchar2(1),
	item_fullfillment_type	varchar2(1),
	store_id		varchar2(6),
	available_qty		number(5,0),
	requested_qty		number(5,0),
	current_queue_size	number(5,0),
	ship_postal_code	varchar2(10),
	ship_latitude		number(15,9),
	ship_longitude		number(15,9),
	store_latitude		number(15,9),
	store_longitude		number(15,9),
	distance		number(15,9)
);