create table mff_return_items
(
	commerce_item_id	varchar(40) not null references dcspp_item (commerce_item_id),
	prorate_item_id		varchar2(40) not null
);

create table mff_prorate_item
(
	prorate_item_id			varchar(40)   	not null,
	commerce_item_id		varchar2(40) 	not null,
	order_id				varchar2(40) 	not null,
	line_number				number(19,7) 	null,
	unit_price 				number(19,7) 	null,
	shipping 				number(19,7) 	null,
	shipping_tax			number(19,7) 	null,
	tax						number(19,7) 	null,
	total					number(19,7) 	null,
	total_without_shipping	number(19,7) 	null,
	state					varchar2(40) 	null,
	quantity				number(19,7) 	null,
	creation_date			timestamp (6)	null,
	return_date				timestamp (6),
  constraint mff_prorate_item_pk primary key (prorate_item_id)
);