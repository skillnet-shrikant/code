CREATE TABLE mff_ship_group (
	shipping_group_id VARCHAR2(40) NOT NULL, 
	fulfillment_store varchar2(10) NULL,
	constraint mff_ship_group_p PRIMARY KEY (shipping_group_id)
);