create table dcspp_pay_inst (
	payment_group_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	special_inst	varchar2(254)	null
,constraint dcspp_pay_inst_p primary key (payment_group_id,tag)
,constraint dcspp_papaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index pay_inst_pgrid_idx on dcspp_pay_inst (payment_group_id);

create table dcspp_config_item (
	config_item_id	varchar2(40)	not null,
	reconfig_data	varchar2(255)	null,
	notes	varchar2(255)	null,
	configured	number(1)	null,
	configurator_id	varchar2(254)	null
,constraint dcspp_config_ite_p primary key (config_item_id)
,constraint dcspp_coconfg_tm_f foreign key (config_item_id) references dcspp_item (commerce_item_id));

create table dcspp_subsku_item (
	subsku_item_id	varchar2(40)	not null,
	ind_quantity	integer	null,
	config_prop_id	varchar2(40)	null,
	config_opt_id	varchar2(40)	null
,constraint dcspp_subsku_ite_p primary key (subsku_item_id)
,constraint dcspp_susubsk_tm_f foreign key (subsku_item_id) references dcspp_item (commerce_item_id));

create table dcspp_item_ci (
	commerce_item_id	varchar2(40)	not null,
	commerce_items	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_item_ci_p primary key (commerce_item_id,sequence_num)
,constraint dcspp_itcommrc_t_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id));

create table dcspp_gift_cert (
	payment_group_id	varchar2(40)	not null,
	profile_id	varchar2(40)	null,
	gift_cert_number	varchar2(50)	null
,constraint dcspp_gift_cert_p primary key (payment_group_id)
,constraint dcspp_gipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index gc_number_idx on dcspp_gift_cert (gift_cert_number);
create index gc_profile_idx on dcspp_gift_cert (profile_id);

create table dcspp_store_cred (
	payment_group_id	varchar2(40)	not null,
	profile_id	varchar2(40)	null,
	store_cred_number	varchar2(50)	null
,constraint dcspp_store_cred_p primary key (payment_group_id)
,constraint dcspp_stpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index sc_number_idx on dcspp_store_cred (store_cred_number);
create index sc_profile_idx on dcspp_store_cred (profile_id);

create table dcspp_credit_card (
	payment_group_id	varchar2(40)	not null,
	credit_card_number	varchar2(40)	null,
	credit_card_type	varchar2(40)	null,
	expiration_month	varchar2(20)	null,
	exp_day_of_month	varchar2(20)	null,
	expiration_year	varchar2(20)	null
,constraint dcspp_credit_car_p primary key (payment_group_id)
,constraint dcspp_crpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create table dcspp_bill_addr (
	payment_group_id	varchar2(40)	not null,
	prefix	varchar2(40)	null,
	first_name	varchar2(40)	null,
	middle_name	varchar2(40)	null,
	last_name	varchar2(40)	null,
	suffix	varchar2(40)	null,
	job_title	varchar2(40)	null,
	company_name	varchar2(40)	null,
	address_1	varchar2(50)	null,
	address_2	varchar2(50)	null,
	address_3	varchar2(50)	null,
	city	varchar2(40)	null,
	county	varchar2(40)	null,
	state	varchar2(40)	null,
	postal_code	varchar2(10)	null,
	country	varchar2(40)	null,
	phone_number	varchar2(40)	null,
	fax_number	varchar2(40)	null,
	email	varchar2(255)	null
,constraint dcspp_bill_addr_p primary key (payment_group_id)
,constraint dcspp_bipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create table dcspp_pay_status (
	status_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	trans_id	varchar2(50)	null,
	amount	number(19,7)	null,
	trans_success	number(1,0)	null,
	error_message	varchar2(254)	null,
	trans_timestamp	timestamp	null
,constraint dcspp_pay_status_p primary key (status_id)
,constraint dcspp_pay_status_c check (trans_success IN (0,1)));


create table dcspp_cc_status (
	status_id	varchar2(40)	not null,
	auth_expiration	timestamp	null,
	avs_code	varchar2(40)	null,
	avs_desc_result	varchar2(254)	null,
	integration_data	varchar2(256)	null
,constraint dcspp_cc_status_p primary key (status_id)
,constraint dcspp_ccstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));

create table dcspp_gc_status (
	status_id	varchar2(40)	not null,
	auth_expiration	timestamp	null
,constraint dcspp_gc_status_p primary key (status_id)
,constraint dcspp_gcstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));


create table dcspp_sc_status (
	status_id	varchar2(40)	not null,
	auth_expiration	timestamp	null
,constraint dcspp_sc_status_p primary key (status_id)
,constraint dcspp_scstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));


create table dcspp_auth_status (
	payment_group_id	varchar2(40)	not null,
	auth_status	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_auth_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_atpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index auth_stat_pgid_idx on dcspp_auth_status (payment_group_id);

create table dcspp_debit_status (
	payment_group_id	varchar2(40)	not null,
	debit_status	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_debit_stat_p primary key (payment_group_id,sequence_num)
,constraint dcspp_depaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index debit_stat_pgi_idx on dcspp_debit_status (payment_group_id);

create table dcspp_cred_status (
	payment_group_id	varchar2(40)	not null,
	credit_status	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_cred_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_crpaymntgr_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index cred_stat_pgid_idx on dcspp_cred_status (payment_group_id);

create table dcspp_shipitem_rel (
	relationship_id	varchar2(40)	not null,
	shipping_group_id	varchar2(40)	null,
	commerce_item_id	varchar2(40)	null,
	quantity	number(19,0)	null,
	quantity_with_fraction	number(19,7)	null,
	returned_qty	number(19,0)	null,
	returned_qty_with_fraction	number(19,7)	null,
	amount	number(19,7)	null,
	state	varchar2(40)	null,
	state_detail	varchar2(254)	null
,constraint dcspp_shipitem_r_p primary key (relationship_id)
,constraint dcspp_shreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index sirel_item_idx on dcspp_shipitem_rel (commerce_item_id);
create index sirel_shipgrp_idx on dcspp_shipitem_rel (shipping_group_id);

create table dcspp_rel_range (
	relationship_id	varchar2(40)	not null,
	low_bound	integer	null,
	low_bound_with_fraction	number(19,7)	null,
	high_bound	integer	null,
	high_bound_with_fraction	number(19,7)	null
,constraint dcspp_rel_range_p primary key (relationship_id));


create table dcspp_payitem_rel (
	relationship_id	varchar2(40)	not null,
	payment_group_id	varchar2(40)	null,
	commerce_item_id	varchar2(40)	null,
	quantity	number(19,0)	null,
	quantity_with_fraction	number(19,7)	null,
	amount	number(19,7)	null
,constraint dcspp_payitem_re_p primary key (relationship_id)
,constraint dcspp_pareltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index pirel_item_idx on dcspp_payitem_rel (commerce_item_id);
create index pirel_paygrp_idx on dcspp_payitem_rel (payment_group_id);

create table dcspp_payship_rel (
	relationship_id	varchar2(40)	not null,
	payment_group_id	varchar2(40)	null,
	shipping_group_id	varchar2(40)	null,
	amount	number(19,7)	null
,constraint dcspp_payship_re_p primary key (relationship_id)
,constraint dcspp_pshrltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index psrel_paygrp_idx on dcspp_payship_rel (payment_group_id);
create index psrel_shipgrp_idx on dcspp_payship_rel (shipping_group_id);

create table dcspp_payorder_rel (
	relationship_id	varchar2(40)	not null,
	payment_group_id	varchar2(40)	null,
	order_id	varchar2(40)	null,
	amount	number(19,7)	null
,constraint dcspp_payorder_r_p primary key (relationship_id)
,constraint dcspp_odreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index porel_order_idx on dcspp_payorder_rel (order_id);
create index porel_paygrp_idx on dcspp_payorder_rel (payment_group_id);


