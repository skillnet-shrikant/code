create table order_item (	
	order_item_id		varchar2(40),
	order_id		varchar2(40),
	commerce_item_id	varchar2(40),
	quantity		number(5,0),
	sku_id			varchar2(40),
	fulfillment_store	varchar2(40)
);

create table order_item_qty (	
	order_id		varchar2(40),
	quantity		number(5,0),
	sku_id			varchar2(40)
);

create table tmp_rule_1_results (
	store_id varchar2(40),
	sku_count number(5,0)
);

create table store_queue_ratio (
	store_id varchar2(40),
	current_queue_size number(5,0),
	max_queue_size number(5,0),
	queue_ratio number(38,7)
);

create table tmp_rule_2_results (
	store_id varchar2(40),
	current_queue_size number(5,0),
	max_queue_size number(5,0),
	queue_ratio number(38,7)
);

create table tmp_rule_3_results (
	store_id varchar2(40),
	distance number(38,7),
	ranking number(5,0)
);