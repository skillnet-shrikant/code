create table tmp_item_planogram (
	item_id   varchar2(40) not null,
	store_id   varchar2(40) not null,
	planogram_name   varchar2(120), 
	planogram_address   varchar2(40),
	serialNumber	varchar2(40)	null
);

create index tmp_itm_pog_qry_idx on tmp_item_planogram (item_id,store_id);