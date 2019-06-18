CREATE TABLE mff_appeasement_refund_gc
(
	appeasement_refund_id VARCHAR2(40) NOT NULL ENABLE,
	payment_group_id VARCHAR2(40) NOT NULL ENABLE,
	CONSTRAINT mff_appeasement_refund_gc PRIMARY KEY (appeasement_refund_id)
);
