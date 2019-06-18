create table order_item (	
	order_item_id		varchar2(40),
	order_id		varchar2(40),
	commerce_item_id	varchar2(40),
	quantity		number(5,0),
	sku_id			varchar2(40),
	fulfillment_store	varchar2(40)
);