alter table mff_item add
(
	fulfillment_store 		varchar2(10) null,
	ship_date 				timestamp null,
	return_date				timestamp null,
	cancel_date				timestamp null
);