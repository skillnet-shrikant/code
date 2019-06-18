create table mff_exch_prorate_items
(
	exch_item_id		varchar(40) not null references csr_exch_item (id),
	prorate_item_id		varchar2(40) not null
);