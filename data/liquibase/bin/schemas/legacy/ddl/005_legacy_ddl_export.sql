create table dcspp_amount_info (
	amount_info_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	currency_code	varchar2(10)	null,
	amount	number(19,7)	null,
	discounted	number(1,0)	null,
	amount_is_final	number(1,0)	null,
	final_rc	number(10)	null
,constraint dcspp_amount_inf_p primary key (amount_info_id)
,constraint dcspp_amount_in1_c check (discounted IN (0,1))
,constraint dcspp_amount_in2_c check (amount_is_final IN (0,1)));

create table dcspp_order_price (
	amount_info_id	varchar2(40)	not null,
	raw_subtotal	number(19,7)	null,
	tax	number(19,7)	null,
	shipping	number(19,7)	null,
	manual_adj_total	number(19,7)	null
,constraint dcspp_order_pric_p primary key (amount_info_id)
,constraint dcspp_oramnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_item_price (
	amount_info_id	varchar2(40)	not null,
	list_price	number(19,7)	null,
	raw_total_price	number(19,7)	null,
	sale_price	number(19,7)	null,
	on_sale	number(1,0)	null,
	order_discount	number(19,7)	null,
	qty_discounted	number(19,0)	null,
	qty_with_fraction_discounted	number(19,7)	null,
	qty_as_qualifier	number(19,0)	null,
	qty_with_fraction_as_qualifier	number(19,7)	null,
	price_list	varchar2(40)	null,
	discountable	number(1)	null
,constraint dcspp_item_price_p primary key (amount_info_id)
,constraint dcspp_itamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id)
,constraint dcspp_item_price_c check (on_sale IN (0,1)));


create table dcspp_tax_price (
	amount_info_id	varchar2(40)	not null,
	city_tax	number(19,7)	null,
	county_tax	number(19,7)	null,
	state_tax	number(19,7)	null,
	country_tax	number(19,7)	null
,constraint dcspp_tax_price_p primary key (amount_info_id)
,constraint dcspp_taamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_ship_price (
	amount_info_id	varchar2(40)	not null,
	raw_shipping	number(19,7)	null
,constraint dcspp_ship_price_p primary key (amount_info_id)
,constraint dcspp_shamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_amtinfo_adj (
	amount_info_id	varchar2(40)	not null,
	adjustments	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_amtinfo_ad_p primary key (amount_info_id,sequence_num)
,constraint dcspp_amamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index amtinf_adj_aid_idx on dcspp_amtinfo_adj (amount_info_id);
create index amtinf_adj_adj_idx on dcspp_amtinfo_adj (adjustments);

create table dcspp_price_adjust (
	adjustment_id	varchar2(40)	not null,
	version	integer	not null,
	adj_description	varchar2(254)	null,
	pricing_model	varchar2(40)	null,
	pricing_model_index	number(10)	null,
	pricing_model_group_index	number(10)	null,
	manual_adjustment	varchar2(40)	null,
	coupon_id	varchar2(40)	null,
	adjustment	number(19,7)	null,
	qty_adjusted	integer	null,
	qty_with_fraction_adjusted	number(19,7)	null
,constraint dcspp_price_adju_p primary key (adjustment_id));


create table dcspp_shipitem_sub (
	amount_info_id	varchar2(40)	not null,
	shipping_group_id	varchar2(42)	not null,
	ship_item_subtotal	varchar2(40)	not null
,constraint dcspp_shipitem_s_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_sbamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ship_item_sub_idx on dcspp_shipitem_sub (amount_info_id);

create table dcspp_taxshipitem (
	amount_info_id	varchar2(40)	not null,
	shipping_group_id	varchar2(42)	not null,
	tax_ship_item_sub	varchar2(40)	not null
,constraint dcspp_taxshipite_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_shamntxnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index tax_ship_item_idx on dcspp_taxshipitem (amount_info_id);

create table dcspp_ntaxshipitem (
	amount_info_id	varchar2(40)	not null,
	shipping_group_id	varchar2(42)	not null,
	non_tax_item_sub	varchar2(40)	not null
,constraint dcspp_ntaxshipit_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_ntamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ntax_ship_item_idx on dcspp_ntaxshipitem (amount_info_id);

create table dcspp_shipitem_tax (
	amount_info_id	varchar2(40)	not null,
	shipping_group_id	varchar2(42)	not null,
	ship_item_tax	varchar2(40)	not null
,constraint dcspp_shipitem_t_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_txamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ship_item_tax_idx on dcspp_shipitem_tax (amount_info_id);

create table dcspp_itmprice_det (
	amount_info_id	varchar2(40)	not null,
	cur_price_details	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_itmprice_d_p primary key (amount_info_id,sequence_num)
,constraint dcspp_sbamntnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index itmprc_det_aii_idx on dcspp_itmprice_det (amount_info_id);

create table dcspp_det_price (
	amount_info_id	varchar2(40)	not null,
	tax	number(19,7)	null,
	order_discount	number(19,7)	null,
	order_manual_adj	number(19,7)	null,
	quantity	number(19,0)	null,
	quantity_with_fraction	number(19,7)	null,
	qty_as_qualifier	number(19,0)	null,
	qty_with_fraction_as_qualifier	number(19,7)	null
,constraint dcspp_det_price_p primary key (amount_info_id));


create table dcspp_det_range (
	amount_info_id	varchar2(40)	not null,
	low_bound	integer	null,
	low_bound_with_fraction	number(19,7)	null,
	high_bound	integer	null,
	high_bound_with_fraction	number(19,7)	null
,constraint dcspp_det_range_p primary key (amount_info_id));


create table dcspp_order_adj (
	order_id	varchar2(40)	not null,
	adjustment_id	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_adj_p primary key (order_id,sequence_num)
,constraint dcspp_oradj_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_adj_orid_idx on dcspp_order_adj (order_id);

create table dcspp_manual_adj (
	manual_adjust_id	varchar2(40)	not null,
	type	integer	not null,
	adjustment_type	integer	not null,
	reason	integer	not null,
	amount	number(19,7)	null,
	notes	varchar2(255)	null,
	version	integer	not null
,constraint dcspp_manual_adj_p primary key (manual_adjust_id));

create table dcspp_scherr_aux (
	scheduled_order_id	varchar2(40)	not null,
	sched_error_id	varchar2(40)	not null
,constraint dcspp_scherr_aux_p primary key (scheduled_order_id));

create index sched_error_idx on dcspp_scherr_aux (sched_error_id);

create table dcspp_sched_error (
	sched_error_id	varchar2(40)	not null,
	error_date	timestamp	not null
,constraint dcspp_sched_err_p primary key (sched_error_id));


create table dcspp_schd_errmsg (
	sched_error_id	varchar2(40)	not null,
	error_txt	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_schd_errs_p primary key (sched_error_id,sequence_num)
,constraint dcspp_sch_errs_f foreign key (sched_error_id) references dcspp_sched_error (sched_error_id));





