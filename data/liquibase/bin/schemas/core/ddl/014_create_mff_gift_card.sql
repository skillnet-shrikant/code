CREATE TABLE mff_gift_card
(
	payment_group_id VARCHAR2(40) NOT NULL REFERENCES dcspp_pay_group (payment_group_id),
	card_number VARCHAR(100) DEFAULT NULL,
	CONSTRAINT mff_gift_card_pk PRIMARY KEY (payment_group_id)
);

CREATE TABLE mff_gift_card_status
(
	status_id 	    VARCHAR2(40) NOT NULL REFERENCES dcspp_pay_status (status_id),
	auth_code           VARCHAR2(256)   DEFAULT NULL,
	CONSTRAINT mff_gift_card_status_pk PRIMARY KEY (status_id)
);
