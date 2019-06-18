create table dcspp_claimable (
	claimable_id	varchar2(40)	not null,
	version	integer	not null,
	type	integer	not null,
	status	integer	null,
	start_date	timestamp	null,
	expiration_date	timestamp	null,
	last_modified	timestamp	null
,constraint dcspp_claimable_p primary key (claimable_id));


create table dcspp_giftcert (
	giftcertificate_id	varchar2(40)	not null,
	amount	double precision	not null,
	amount_authorized	double precision	not null,
	amount_remaining	double precision	not null,
	purchaser_id	varchar2(40)	null,
	purchase_date	timestamp	null,
	last_used	timestamp	null
,constraint dcspp_giftcert_p primary key (giftcertificate_id)
,constraint dcspp_gigiftcrtf_f foreign key (giftcertificate_id) references dcspp_claimable (claimable_id));

create index claimable_user_idx on dcspp_giftcert (purchaser_id);

create table dcspp_cp_folder (
	folder_id	varchar2(40)	not null,
	name	varchar2(254)	not null,
	parent_folder	varchar2(40)	null
,constraint dcspp_cp_folder_p primary key (folder_id)
,constraint dcspp_cp_prntfol_f foreign key (parent_folder) references dcspp_cp_folder (folder_id));

create index dcspp_prntfol_idx on dcspp_cp_folder (parent_folder);

create table dcspp_coupon (
	coupon_id	varchar2(40)	not null,
	promotion_id	varchar2(40)	not null
,constraint dcspp_coupon_p primary key (coupon_id,promotion_id)
,constraint dcspp_coupon_df foreign key (coupon_id) references dcspp_claimable (claimable_id));


create table dcspp_coupon_info (
	coupon_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	use_promo_site	number(10)	null,
	parent_folder	varchar2(40)	null,
	max_uses	number(10)	null,
	uses	number(10)	null
,constraint dcspp_copninfo_p primary key (coupon_id)
,constraint dcspp_copninfo_d_f foreign key (coupon_id) references dcspp_claimable (claimable_id)
,constraint dcspp_cpnifol_f foreign key (parent_folder) references dcspp_cp_folder (folder_id));

create index dcspp_folder_idx on dcspp_coupon_info (parent_folder);

create table dcs_order_markers (
	marker_id	varchar2(40)	not null,
	order_id	varchar2(40)	not null,
	marker_key	varchar2(100)	not null,
	marker_value	varchar2(100)	null,
	marker_data	varchar2(100)	null,
	creation_date	timestamp	null,
	version	number(10)	not null,
	marker_type	number(10)	null
,constraint dcsordermarkers_p primary key (marker_id,order_id)
,constraint dcsordermarkers_f foreign key (order_id) references dcspp_order (order_id));

create index dcs_ordrmarkers1_x on dcs_order_markers (order_id);

create table dcspp_commerce_item_markers (
	marker_id	varchar2(40)	not null,
	commerce_item_id	varchar2(40)	not null,
	marker_key	varchar2(100)	not null,
	marker_value	varchar2(100)	null,
	marker_data	varchar2(100)	null,
	creation_date	timestamp	null,
	version	number(10)	not null,
	marker_type	number(10)	null
,constraint dcscitemmarkers_p primary key (marker_id,commerce_item_id)
,constraint dcscitemmarkers_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id));

create index dcs_itemmarkers1_x on dcspp_commerce_item_markers (commerce_item_id);

create table dcspp_ord_abandon (
	abandonment_id	varchar2(40)	not null,
	version	number(10)	not null,
	order_id	varchar2(40)	not null,
	ord_last_updated	timestamp	null,
	abandon_state	varchar2(40)	null,
	abandonment_count	number(10)	null,
	abandonment_date	timestamp	null,
	reanimation_date	timestamp	null,
	convert_date	timestamp	null,
	lost_date	timestamp	null
,constraint dcspp_ord_abndn_p primary key (abandonment_id));

create index dcspp_ordabandn1_x on dcspp_ord_abandon (order_id);

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