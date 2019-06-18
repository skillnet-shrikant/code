create table force_allocation (
	sku_id varchar2(40),
	store_id varchar2(40),
	is_gift_card number(1) default 0
);