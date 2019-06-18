create table ff_prod_redirect (
	id 		varchar2(40)	not null,
	product_id	varchar2(40) not null,
	inactive_url	varchar2(254)	not null,
	redirect_url	varchar2(254) not null,
	is_inactive	int default 0,
	is_vip		int default 0,
	creation_date	timestamp,
	modified_date	timestamp,
	asset_version      int           not null,
	workspace_id       varchar2(40)   not null,
	branch_id          varchar2(40)   not null,
	is_head            numeric(1)    not null,
	version_deleted    numeric(1)    not null,
	version_editable   numeric(1)    not null,
	pred_version       int           null,
	checkin_date       timestamp     null,	
	CONSTRAINT ff_prod_redirect_pk primary key(id, asset_version)
);
CREATE index ff_prod_redirect_ws_idx on ff_prod_redirect (workspace_id);
CREATE index ff_prod_redirect_ck_idx on ff_prod_redirect (checkin_date);
