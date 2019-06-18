CREATE TABLE mff_info_message (
	id varchar2(40)	NOT NULL,
	info_key varchar2(254)	NOT NULL,
	info_msg clob not NULL,
	PRIMARY KEY(id)
);