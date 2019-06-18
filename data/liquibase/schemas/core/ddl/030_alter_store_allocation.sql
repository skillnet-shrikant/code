alter table mff_order add
(
  bopis_signature		CLOB
);

alter table mff_store_allocation add
(
	order_number	varchar2(40),
	bopis_order 	number(1,0)	DEFAULT 0
);