alter table mff_order drop
(
  bopis_signature
);

alter table mff_store_allocation drop
(
	order_number,
	bopis_order
);