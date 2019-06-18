create table dbcpp_sched_order (
	scheduled_order_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar2(32)	null,
	profile_id	varchar2(40)	null,
	create_date	timestamp	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	template_order	varchar2(32)	null,
	state	integer	null,
	next_scheduled	timestamp	null,
	schedule	varchar2(255)	null,
	site_id	varchar2(40)	null
,constraint dbcpp_sched_orde_p primary key (scheduled_order_id));

create index sched_tmplt_idx on dbcpp_sched_order (template_order);
create index sched_profile_idx on dbcpp_sched_order (profile_id);
create index sched_site_idx on dbcpp_sched_order (site_id);

create table dbcpp_sched_clone (
	scheduled_order_id	varchar2(40)	not null,
	cloned_order	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_sched_clon_p primary key (scheduled_order_id,sequence_num)
,constraint dbcpp_scschedld__f foreign key (scheduled_order_id) references dbcpp_sched_order (scheduled_order_id));


create table dcspp_order (
	order_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	order_class_type	varchar2(40)	null,
	profile_id	varchar2(40)	null,
	organization_id	varchar2(40)	null,
	description	varchar2(64)	null,
	state	varchar2(40)	null,
	state_detail	varchar2(254)	null,
	created_by_order	varchar2(40)	null,
	origin_of_order	number(10)	null,
	creation_date	timestamp	null,
	submitted_date	timestamp	null,
	last_modified_date	timestamp	null,
	completed_date	timestamp	null,
	price_info	varchar2(40)	null,
	tax_price_info	varchar2(40)	null,
	explicitly_saved	number(1,0)	null,
	agent_id	varchar2(40)	null,
	sales_channel	number(10)	null,
	creation_site_id	varchar2(40)	null,
	site_id	varchar2(40)	null,
	gwp	number(1,0)	null,
	quote_info	varchar2(40)	null,
	active_quote_order_id	varchar2(40)	null,
	configurator_id	varchar2(254)	null
,constraint dcspp_order_p primary key (order_id)
,constraint dcspp_order_c check (explicitly_saved IN (0,1)));

create index order_lastmod_idx on dcspp_order (last_modified_date);
create index order_profile_idx on dcspp_order (profile_id);
create index order_submit_idx on dcspp_order (submitted_date);
create index ord_creat_site_idx on dcspp_order (creation_site_id);
create index ord_site_idx on dcspp_order (site_id);
create index ord_activequote_idx on dcspp_order (active_quote_order_id);
create index ord_organization_idx on dcspp_order (organization_id);

create table dcspp_ship_group (
	shipping_group_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	shipgrp_class_type	varchar2(40)	null,
	shipping_method	varchar2(40)	null,
	description	varchar2(64)	null,
	ship_on_date	timestamp	null,
	actual_ship_date	timestamp	null,
	state	varchar2(40)	null,
	state_detail	varchar2(254)	null,
	submitted_date	timestamp	null,
	price_info	varchar2(40)	null,
	order_ref	varchar2(40)	null
,constraint dcspp_ship_group_p primary key (shipping_group_id));

create table dcspp_pay_group (
	payment_group_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	paygrp_class_type	varchar2(40)	null,
	payment_method	varchar2(40)	null,
	amount	number(19,7)	null,
	amount_authorized	number(19,7)	null,
	amount_debited	number(19,7)	null,
	amount_credited	number(19,7)	null,
	currency_code	varchar2(10)	null,
	state	varchar2(40)	null,
	state_detail	varchar2(254)	null,
	submitted_date	timestamp	null,
	order_ref	varchar2(40)	null
,constraint dcspp_pay_group_p primary key (payment_group_id));

create table dcspp_item (
	commerce_item_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	item_class_type	varchar2(40)	null,
	catalog_id	varchar2(40)	null,
	catalog_ref_id	varchar2(40)	null,
	external_id	varchar2(40)	null,
	catalog_key	varchar2(40)	null,
	product_id	varchar2(40)	null,
	site_id	varchar2(40)	null,
	quantity	number(19,0)	null,
	quantity_with_fraction	number(19,7)	null,
	state	varchar2(40)	null,
	state_detail	varchar2(254)	null,
	price_info	varchar2(40)	null,
	order_ref	varchar2(40)	null,
	gwp	number(1,0)	null
,constraint dcspp_item_p primary key (commerce_item_id));

create index item_catref_idx on dcspp_item (catalog_ref_id);
create index item_prodref_idx on dcspp_item (product_id);
create index item_site_idx on dcspp_item (site_id);

create table dcspp_relationship (
	relationship_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	rel_class_type	varchar2(40)	null,
	relationship_type	varchar2(40)	null,
	order_ref	varchar2(40)	null
,constraint dcspp_relationsh_p primary key (relationship_id));

create table dcspp_rel_orders (
	order_id	varchar2(40)	not null,
	related_orders	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_rel_orders_p primary key (order_id,sequence_num)
,constraint dcspp_reordr_d_f foreign key (order_id) references dcspp_order (order_id));

create table dcspp_order_inst (
	order_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	special_inst	varchar2(254)	null
,constraint dcspp_order_inst_p primary key (order_id,tag)
,constraint dcspp_orordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_inst_oid_idx on dcspp_order_inst (order_id);

create table dcspp_order_sg (
	order_id	varchar2(40)	not null,
	shipping_groups	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_sg_p primary key (order_id,sequence_num)
,constraint dcspp_sgordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_sg_ordid_idx on dcspp_order_sg (order_id);

create table dcspp_order_pg (
	order_id	varchar2(40)	not null,
	payment_groups	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_pg_p primary key (order_id,sequence_num)
,constraint dcspp_orpgordr_f foreign key (order_id) references dcspp_order (order_id));

create index order_pg_ordid_idx on dcspp_order_pg (order_id);

create table dcspp_order_item (
	order_id	varchar2(40)	not null,
	commerce_items	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_item_p primary key (order_id,sequence_num)
,constraint dcspp_oritordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_item_oid_idx on dcspp_order_item (order_id);
create index order_item_cit_idx on dcspp_order_item (commerce_items);

create table dcspp_order_rel (
	order_id	varchar2(40)	not null,
	relationships	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_rel_p primary key (order_id,sequence_num)
,constraint dcspp_orlordr_d_f foreign key (order_id) references dcspp_order (order_id));

create table dcspp_ship_inst (
	shipping_group_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	special_inst	varchar2(254)	null
,constraint dcspp_ship_inst_p primary key (shipping_group_id,tag)
,constraint dcspp_shshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create index ship_inst_sgid_idx on dcspp_ship_inst (shipping_group_id);

create table dcspp_hrd_ship_grp (
	shipping_group_id	varchar2(40)	not null,
	tracking_number	varchar2(40)	null
,constraint dcspp_hrd_ship_g_p primary key (shipping_group_id)
,constraint dcspp_hrshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));


create table dcspp_ele_ship_grp (
	shipping_group_id	varchar2(40)	not null,
	email_address	varchar2(255)	null
,constraint dcspp_ele_ship_g_p primary key (shipping_group_id)
,constraint dcspp_elshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create table dcspp_ship_addr (
	shipping_group_id	varchar2(40)	not null,
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
,constraint dcspp_ship_addr_p primary key (shipping_group_id)
,constraint dcspp_shdshippng_f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create table dcspp_hand_inst (
	handling_inst_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	hndinst_class_type	varchar2(40)	null,
	handling_method	varchar2(40)	null,
	shipping_group_id	varchar2(40)	null,
	commerce_item_id	varchar2(40)	null,
	quantity	integer	null,
	quantity_with_fraction	number(19,7)	null
,constraint dcspp_hand_inst_p primary key (handling_inst_id));

create index hi_item_idx on dcspp_hand_inst (commerce_item_id);
create index hi_ship_group_idx on dcspp_hand_inst (shipping_group_id);

create table dcspp_gift_inst (
	handling_inst_id	varchar2(40)	not null,
	giftlist_id	varchar2(40)	null,
	giftlist_item_id	varchar2(40)	null
,constraint dcspp_gift_inst_p primary key (handling_inst_id)
,constraint dcspp_gihandlng__f foreign key (handling_inst_id) references dcspp_hand_inst (handling_inst_id));

create table dcspp_sg_hand_inst (
	shipping_group_id	varchar2(40)	not null,
	handling_instrs	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_sg_hand_in_p primary key (shipping_group_id,sequence_num)
,constraint dcspp_sgshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create index sg_hnd_ins_sgi_idx on dcspp_sg_hand_inst (shipping_group_id);


