create table ff_prod_redirect (
	id 		varchar2(40)	not null,
	product_id	varchar2(40) not null,
	inactive_url	varchar2(254)	not null,
	redirect_url	varchar2(254) not null,
	is_inactive	int default 0,
	is_vip		int default 0,
	creation_date	timestamp,
	modified_date	timestamp,
	CONSTRAINT ff_prod_redirect_pk primary key(id)
);
