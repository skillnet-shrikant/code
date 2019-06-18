create table mff_item
(
	commerce_item_id 		varchar(40) not null references dcspp_item (commerce_item_id),
	shipping 				number(19,7) null,	
	shipping_tax 			number(19,7) null,
	constraint mff_item_pk primary key (commerce_item_id)
);

commit;