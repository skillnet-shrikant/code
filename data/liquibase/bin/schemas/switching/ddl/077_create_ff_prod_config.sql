create table ff_prod_config (
	id 			varchar2(40)	not null,
	product_id		varchar2(40) not null,
	is_exclude_from_report	int default 0,
	is_vip			int default 0,
	creation_date		timestamp,
	modified_date		timestamp,
	CONSTRAINT ff_prod_config_pk primary key(id)
);
