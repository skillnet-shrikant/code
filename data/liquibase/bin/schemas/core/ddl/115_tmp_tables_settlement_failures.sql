create table mff_nosettlement_records(
  id				NUMBER		    NOT NULL,
  order_number		varchar2(40) 	NULL,
  amount  			NUMBER			NULL,
  submitted_date	TIMESTAMP(6)	NULL,
  state				varchar2(40) 	NULL,
  last_extract_date	 TIMESTAMP(6)	NULL,
  PRIMARY KEY (id));
 
 create table mff_noauthcode_records
(
  id				NUMBER			NOT NULL,
  order_ref			varchar2(40) 	NULL,
  amount_debited	NUMBER			NULL,
  state				varchar2(40)	NULL,
  auth_code			varchar2(40) 	NULL,
  submitted_date	TIMESTAMP(6)	NULL,
  order_number		varchar2(40) 	NULL,
  PRIMARY KEY (id));
  
Create sequence stlmnt_fail_seq start with 1 minvalue 1 maxvalue 10000 increment by 1  CYCLE;