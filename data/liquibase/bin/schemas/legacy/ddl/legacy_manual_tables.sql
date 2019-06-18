create table dbcpp_approverids (
	order_id	varchar2(40)	not null,
	approver_ids	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_approverid_p primary key (order_id,sequence_num)
,constraint dbcpp_apordr_d_f foreign key (order_id) references dcspp_order (order_id));

create table dbcpp_cost_center (
	cost_center_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	costctr_class_type	varchar2(40)	null,
	identifier	varchar2(40)	null,
	amount	number(19,7)	null,
	order_ref	varchar2(40)	null
,constraint dbcpp_cost_cente_p primary key (cost_center_id));

create table dbcpp_order_cc (
	order_id	varchar2(40)	not null,
	cost_centers	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_order_cc_p primary key (order_id,sequence_num)
,constraint dbcpp_orordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_cc_ordid_idx on dbcpp_order_cc (order_id);