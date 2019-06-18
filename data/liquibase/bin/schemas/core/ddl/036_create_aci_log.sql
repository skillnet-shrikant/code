create table aci_transaction_log (
	id varchar(40) not null,
	aci_transaction_id varchar(40),
	aci_order_id varchar(40),
	aci_fraud_response varchar(244),
	aci_status_code varchar(244),
	aci_error_code varchar(40),
	aci_fraud_error_code varchar(40),
	created_on timestamp,
constraint aci_log_pk primary key (id));