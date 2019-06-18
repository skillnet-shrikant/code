CREATE TABLE mff_order_coupons (order_id VARCHAR2(40) NOT NULL, sequence_num INTEGER NOT NULL, coupon_id VARCHAR2(40) NOT NULL);

ALTER TABLE mff_order_coupons ADD CONSTRAINT mff_order_coupons_pk PRIMARY KEY (order_id, sequence_num);

ALTER TABLE mff_order_coupons ADD CONSTRAINT mff_order_coupons_order_id_fk FOREIGN KEY (order_id) REFERENCES dcspp_order (order_id);