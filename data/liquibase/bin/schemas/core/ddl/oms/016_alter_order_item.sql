alter table order_item add(
	is_split_allocate	number(1) default 0,
	is_split_item 		number(1) default 0);