alter table mff_store_allocation add (
    address1 		VARCHAR2(50) null,
	address2 		VARCHAR2(50) null,
    city 			VARCHAR2(40) null,
	ship_state 		VARCHAR2(40) null,
	postal_code 	VARCHAR2(10) null,
    county 			VARCHAR2(40) null,
	country			VARCHAR2(40) null,
	phone_number	VARCHAR2(40) null,
	contact_email	VARCHAR2(255) null
);