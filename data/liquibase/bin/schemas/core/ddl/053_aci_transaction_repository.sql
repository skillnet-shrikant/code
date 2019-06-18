create table aci_transaction (
	transaction_id varchar(40) not null,
	type NUMBER(*,0),
	atg_artifact_id varchar(40),
	ord_Id varchar(40),
	act_cd varchar(40),
	div_num varchar(40),
	stat_cd varchar(40),
	req_id varchar(40),
	req_type_cd varchar(40),
	request CLOB,
	response CLOB,
	transaction_time timestamp,
constraint aci_tranaction_p primary key (transaction_id));

create table aci_fraud_transaction (
	transaction_id varchar(40) not null,
	fraud_rec_id varchar(40),
	fraud_rsp_cd varchar(40),
	fraud_stat_cd varchar(40),
	fraud_rsp_desc varchar(40),
	fraud_neural varchar(40),
	fraud_rcf varchar(40),
constraint aci_fraud_transaction_p primary key (transaction_id));

alter table aci_fraud_transaction add constraint aci_fraud_transaction_f foreign key (transaction_id) references aci_transaction (transaction_id) enable;

create table aci_payment_transaction (
	transaction_id varchar(40) not null,
	rsp_cd varchar(40),
	rsp_dt timestamp,
	rsp_tm timestamp,
	rsp_msg varchar(120),
constraint aci_payment_transaction_p primary key (transaction_id));

alter table aci_payment_transaction add constraint aci_payment_transaction_f foreign key (transaction_id) references aci_transaction (transaction_id) enable;