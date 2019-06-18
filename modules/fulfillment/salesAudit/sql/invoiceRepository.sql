-- ***************************************************************
-- *****				 Drop Tables
-- *****        NEED TO ORDER DROP STATEMENTS!
-- ***************************************************************
DROP TABLE mff_invoice_rel_inv_ship;
DROP TABLE mff_invoice_rel_inv_return;
DROP TABLE mff_invoice_rel_inv_appease;
DROP TABLE mff_invoice_rel_inv_carton;
DROP TABLE mff_invoice_rel_inv_payment;
DROP TABLE mff_invoice_rel_line_disc;
DROP TABLE mff_invoice_rel_line_carton;
DROP TABLE mff_invoice_rel_inv_aux;
DROP TABLE mff_invoice_rel_line_aux;
DROP TABLE mff_invoice_rel_pay_aux;
DROP TABLE mff_invoice_rel_ext_pay;
DROP TABLE mff_invoice;
DROP TABLE mff_invoice_address;
DROP TABLE mff_invoice_appeasement;
DROP TABLE mff_invoice_auxilliary;
DROP TABLE mff_invoice_carton;
DROP TABLE mff_invoice_line_carton;
DROP TABLE mff_invoice_line_discount;
DROP TABLE mff_invoice_payment;
DROP TABLE mff_invoice_line;
DROP TABLE mff_invoice_line_returned;
DROP TABLE mff_invoice_line_shipped;
DROP TABLE mff_invoice_line_summary;
DROP TABLE mff_invoice_extract;
DROP TABLE mff_invoice_extract_summary;
DROP TABLE mff_invoice_extract_payment;

-- ***************************************************************
-- *****			         Primary Tables 
-- ***************************************************************

-- ***************************************************************
-- *****				 Invoice Table 
-- ***************************************************************
CREATE TABLE mff_invoice (
	invoice_id						varchar(40)		not null,
	order_number					varchar (100) 	not null,
	order_date						timestamp(6)  	not null,
	source							varchar (100) 	not null,
	order_type						varchar(50),	
	business_type					varchar(50),	
	loyalty_identifier				varchar(30),	
	customer_purchase_order			varchar(100),	
	reference						varchar(200)  	not null,
	tax_exemption_certificate		varchar(100),
	tax_exemption_name				varchar(100),	
	tax_exemption_type				varchar(20),
	order_shipping					number(19,7)	not null,
	order_shipping_local_tax		number(19,7)	not null,
	order_shipping_county_tax		number(19,7)	not null,
	order_shipping_state_tax		number(19,7)	not null,
	order_shipping_total			number(19,7)	not null,
	order_shipping_tax				number(19,7),	
	order_shipping_extended_total	number(19,7)	not null,
	ship_via						varchar(4)		not null,
	status							varchar(20)		not null,
	last_extract_date				timestamp(6),
	extract							varchar(40),
	shipping_address				varchar(40)		not null,
	billing_address					varchar(40)		not null,
	line_summary					varchar(40)		not null,
	PRIMARY KEY(invoice_id)
);

-- ***************************************************************
-- *****				Invoice Address Table
-- ***************************************************************
CREATE TABLE mff_invoice_address (
	address_id						varchar(40)		not null,
	address_type					varchar(20)		not null,
	first_name						varchar(100)	not null,
	middle_name						varchar(100),
	last_name						varchar(100)	not null,
	organization					varchar(100),	
	company_name					varchar(100),	
	home_phone						varchar(50),	
	work_phone						varchar(50),	
	mobile_phone					varchar(50),	
	fax								varchar(50),	
	email							varchar(200),	
	address_1						varchar(100)	not null,
	address_2						varchar(100),
	address_3						varchar(100),	
	address_4						varchar(100),	
	city							varchar(50)		not null,
	province_code					char(2)			not null,
	province						varchar(50),	
	postal_code						varchar(11)		not null,
	country_code					char(2)			not null,
	PRIMARY KEY (address_id)
);	

-- ***************************************************************
-- *****			Invoice Appeasement Table
-- ***************************************************************
CREATE TABLE mff_invoice_appeasement (
	appeasement_id					varchar(40)		not null,
	appease_code					varchar(10)		not null,
	appease_description				varchar(100)	not null,
	reference						varchar(100)	not null,
	appease_date					timestamp(6)	not null,
	amount							number (19,7)	not null,
	PRIMARY KEY (appeasement_id)
);	

-- ***************************************************************
-- *****			Invoice Order Auxilliary Entries
-- ***************************************************************
CREATE TABLE mff_invoice_auxilliary (
	auxilliary_id					varchar(40)		not null,
	auxilliary_type					number(38,0)	not null,
	auxilliary_name					varchar(255)	not null,
	auxilliary_value				varchar(255) 	not null,
	PRIMARY KEY (auxilliary_id)
);	

-- ***************************************************************
-- *****					Invoice Cartons
-- ***************************************************************
CREATE TABLE mff_invoice_carton (
	carton_id						varchar(40)		not null,
	tracking_number					varchar(30)		not null,
	carton_number					varchar(20)		not null,
	ship_date						timestamp (6)	not null,
	ship_via						varchar(4),	
	facility_cd						varchar(20),	
	weight							number (19,7),	
	carton_size						varchar(3),	
	carton_type						varchar(3),	
	bill_of_lading					varchar(20),	
	pro_num							varchar(20),
	manifest_number					varchar(10),	
	pick_ticket						varchar(10),
	return_label_number				varchar(50),	
	deliver_confirmation_number		varchar(50),	
	PRIMARY KEY (carton_id)
);	

-- ***************************************************************
-- *****				Invoice Line Cartons
-- ***************************************************************
CREATE TABLE mff_invoice_line_carton (
	line_carton_id					varchar(40)		not null,
	tracking_number					varchar(30)		not null,
	ship_via						varchar(4)		not null,
	quantity						number(19,0)	not null,
	deliver_confirmation_number		varchar(50),	
	serial_number					varchar(30),	
	carton_number					varchar(20)		not null,
	PRIMARY KEY (line_carton_id)
);	

-- ***************************************************************
-- *****				Invoice Line Discounts
-- ***************************************************************
CREATE TABLE mff_invoice_line_discount (
	line_discount_id				varchar(40)		not null,
	discount_type					varchar(50)		not null,
	discount_code					varchar(20)		not null,
	source							varchar(20)		not null,
	amount							number (19,7)	not null,
	PRIMARY KEY (line_discount_id)
);	

-- ***************************************************************
-- *****				Invoice Payments
-- ***************************************************************
CREATE TABLE mff_invoice_payment (
	payment_id						varchar(40)		not null,
	payment_type					varchar(10)		not null,
	amount							number (19,7)	not null,
	transaction_reference			varchar(20),	
	payment_date					timestamp(6),	
	card_reference					varchar(4),	
	card_number						varchar(50),	
	token_id						varchar(50),
	PRIMARY KEY (payment_id)
);	

-- ***************************************************************
-- *****				Invoice Line 
-- ***************************************************************
CREATE TABLE mff_invoice_line (
	line_id							varchar(40)		not null,
	type		 					number(38,0)	not null,
	extract_line_id					number(19,0)	not null,
	client_line_id					varchar(100)	not null,
	skucode							varchar(20)		not null,
	barcode							varchar(20)		not null,
	item_number						varchar(16)		not null,
	color_code						varchar(10)		not null,
	size_code						varchar(10)		not null,
	quantity						number(19,0)	not null,
	unit_price						number (19,7)	not null,
	facility_cd						varchar(20)		not null,
	shipping_amount					number(19,7)	not null,
	line_local_tax					number(19,7)	not null,
	line_county_tax					number(19,7)	not null,
	line_state_tax					number(19,7)	not null,
	line_tax_total					number(19,7)	not null,
	line_shipping_tax				number(19,7),	
	line_extended_total				number(19,7)	not null,
	extended_price					number(19,7)	not null,
	PRIMARY KEY (line_id)
);	

-- ***************************************************************
-- *****				Invoice Line Returned
-- ***************************************************************
CREATE TABLE mff_invoice_line_returned (
	line_id							varchar(40)		not null,
	line_number						number(19,0),	
	return_reason					varchar(255)	not null,
	rma_number						varchar(255),
	returned_amount					number (19,7),	
	restock_local_tax				number (19,7)	not null,
	restock_county_tax				number (19,7)	not null,
	restock_state_tax				number (19,7)	not null,
	restock_tax_total				number (19,7)	not null,
	restock_shipping_tax			number (19,7)	not null,
	restock_extended_total			number (19,7)	not null,
	PRIMARY KEY (line_id)
);	

-- ***************************************************************
-- *****				Invoice Line Shipped
-- ***************************************************************
CREATE TABLE mff_invoice_line_shipped (
	line_id							varchar(40)		not null,
	line_number						number(19,0),	
	PRIMARY KEY (line_id)
);	

-- ***************************************************************
-- *****				Invoice Line Summary
-- ***************************************************************
CREATE TABLE mff_invoice_line_summary (
	line_summary_id					varchar(40)		not null,
	transaction_total				number (19,7)	not null,
	transaction_taxable_total		number (19,7)	not null,
	transaction_tax_total			number (19,7)	not null,
	line_count						number (19,0)	not null,
	payment_total					number (19,7)	not null,	
	payment_count					number(19,0)	not null,
	discount_total					number (19,7)	not null,
	discount_count					number(19,0)	not null,
	giftcard_sold_total				number (19,7)	not null,
	giftcard_sold_count				number (19,0)	not null,
	PRIMARY KEY (line_summary_id)
);	


-- ***************************************************************
-- *****				    Extract 
-- ***************************************************************
CREATE TABLE mff_invoice_extract (
	extract_id						varchar(40)		not null,
	extract_date					timestamp (6)	not null,
	extract_file_name				varchar(255)	not null,
	run_type						varchar(10)		not null,
	extract_summary					varchar(40)		not null,
	PRIMARY KEY (extract_id)
);	

-- ***************************************************************
-- *****				Extract Summary
-- ***************************************************************
CREATE TABLE mff_invoice_extract_summary (
	extract_summary_id				varchar(40)		not null,
	transactions_total				number (19,7)	not null,
	transactions_taxable_total		number (19,7)	not null,
	transactions_tax_total			number (19,7)	not null,
	transaction_counts				number (19,0)	not null,
	transaction_lines_counts		number (19,0)	not null,
	payments_total					number (19,7)	not null,
	payments_count					number (19,0)	not null,
	discounts_total					number (19,7)	not null,
	discounts_count					number (19,0)	not null,
	giftcard_sold_total				number (19,7)	not null,
	giftcard_sold_count				number (19,0)	not null,
	PRIMARY KEY (extract_summary_id)
);	


-- ***************************************************************
-- *****			Extract Summary Payments
-- ***************************************************************
CREATE TABLE mff_invoice_extract_payment (
	extract_payment_id				varchar(40)		not null,
	payment_type					varchar(10)		not null,
	credit_total					number (19,7)	not null,
	credit_count					number (19,0)	not null,
	debit_total						number (19,7)	not null,
	debit_count						number (19,0)	not null,
	PRIMARY KEY (extract_payment_id)
);	

-- ***************************************************************
-- *****			   End of Primary Tables
-- ***************************************************************




-- ***************************************************************
-- *****			      Relationship Tables 
-- ***************************************************************

-- ***************************************************************
-- *****			Invoice Shipped Relationship Table 
-- ***************************************************************
CREATE TABLE mff_invoice_rel_inv_ship  (
	invoice_id						varchar(40)		not null,
	line_id							varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_ship_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_ship_fk2 FOREIGN KEY (line_id) REFERENCES mff_invoice_line_shipped (line_id)
);	

-- ***************************************************************
-- *****			Invoice Returned Relationship Table 
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_inv_return  (
	invoice_id						varchar(40)		not null,
	line_id			    			varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_return_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_return_fk2 FOREIGN KEY (line_id) REFERENCES mff_invoice_line_returned (line_id)
);	

-- ***************************************************************
-- *****		Invoice Appeasement Relationship Table 
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_inv_appease  (
	invoice_id						varchar(40)		not null,
	appeasement_id					varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_appeas_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_appeas_fk2 FOREIGN KEY (appeasement_id) REFERENCES mff_invoice_appeasement (appeasement_id)
);

-- ***************************************************************
-- *****		Invoice Cartons Relationship Table 
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_inv_carton  (
	invoice_id						varchar(40)		not null,
	carton_id						varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_carton_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_carton_fk2 FOREIGN KEY (carton_id) REFERENCES mff_invoice_carton (carton_id)
);	

-- ***************************************************************
-- *****		Invoice Payment Relationship Table 
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_inv_payment  (
	invoice_id						varchar(40)		not null,
	payment_id						varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_paymnt_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_paymnt_fk2 FOREIGN KEY (payment_id) REFERENCES mff_invoice_payment (payment_id)
);

-- ***************************************************************
-- *****			Item Discount Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_line_disc  (
	line_id							varchar(40)		not null,
	line_discount_id				varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(line_id, sequence_num),
	CONSTRAINT mff_invoice_rel_line_disc_fk1 FOREIGN KEY (line_id) REFERENCES mff_invoice_line (line_id),
	CONSTRAINT mff_invoice_rel_line_disc_fk2 FOREIGN KEY (line_discount_id) REFERENCES mff_invoice_line_discount (line_discount_id)
);	


-- ***************************************************************
-- *****			Item Carton Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_line_carton  (
	line_id							varchar(40)		not null,
	line_carton_id					varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(line_id, sequence_num),
	CONSTRAINT mff_invoice_rel_line_cartn_fk1 FOREIGN KEY (line_id) REFERENCES mff_invoice_line (line_id),
	CONSTRAINT mff_invoice_rel_line_cartn_fk2 FOREIGN KEY (line_carton_id) REFERENCES mff_invoice_line_carton (line_carton_id)
);	


-- ***************************************************************
-- *****			Invoice Auxilliary Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_inv_aux  (
	invoice_id						varchar(40)		not null,
	auxilliary_id					varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(invoice_id, sequence_num),
	CONSTRAINT mff_invoice_rel_inv_aux_fk1 FOREIGN KEY (invoice_id) REFERENCES mff_invoice (invoice_id),
	CONSTRAINT mff_invoice_rel_inv_aux_fk2 FOREIGN KEY (auxilliary_id) REFERENCES mff_invoice_auxilliary (auxilliary_id)
);	


-- ***************************************************************
-- *****			Line Auxilliary Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_line_aux  (
	line_id							varchar(40)		not null,
	auxilliary_id					varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(line_id, sequence_num),
	CONSTRAINT mff_invoice_rel_line_aux_fk1 FOREIGN KEY (line_id) REFERENCES mff_invoice_line (line_id),
	CONSTRAINT mff_invoice_rel_line_aux_fk2 FOREIGN KEY (auxilliary_id) REFERENCES mff_invoice_auxilliary (auxilliary_id)
);	


-- ***************************************************************
-- *****			Payment Auxilliary Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_pay_aux  (	
	payment_id						varchar(40)		not null,
	auxilliary_id					varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(payment_id, sequence_num),
	CONSTRAINT mff_invoice_rel_pay_aux_fk1 FOREIGN KEY (payment_id) REFERENCES mff_invoice_payment (payment_id),
	CONSTRAINT mff_invoice_rel_pay_aux_fk2 FOREIGN KEY (auxilliary_id) REFERENCES mff_invoice_auxilliary (auxilliary_id)
);	

-- ***************************************************************
-- *****		Extract summary Payment Relationship
-- ***************************************************************	
CREATE TABLE mff_invoice_rel_ext_pay  (	
	extract_id						varchar(40)		not null,
	extract_payment_id				varchar(40)		not null,
	sequence_num					number(*,0)		not null,
	PRIMARY KEY(extract_id, sequence_num),
	CONSTRAINT mff_invoice_rel_ext_pay_fk1 FOREIGN KEY (extract_id) REFERENCES mff_invoice_extract(extract_id),
	CONSTRAINT mff_invoice_rel_ext_pay_fk2 FOREIGN KEY (extract_payment_id) REFERENCES mff_invoice_extract_payment (extract_payment_id)
);	

-- ***************************************************************
-- *****			   End of Relationship Tables
-- ***************************************************************
