alter table mff_item_price add 
(
	discount_amount number(19,2) default 0,
	effective_price number(19,2) default 0
);