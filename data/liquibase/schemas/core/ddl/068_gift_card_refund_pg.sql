CREATE TABLE mff_gc_exch_method
(
	exchange_method_id VARCHAR2(40) NOT NULL ENABLE,
	payment_group_id VARCHAR2(40) NOT NULL ENABLE,
	CONSTRAINT mff_gc_exch_method_p PRIMARY KEY (exchange_method_id)
);
