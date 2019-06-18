create table mff_back_in_stock (
	id	varchar(40)	not null,
	email	varchar(255)	default null,
	catalog_ref_id	varchar(40)	default null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	constraint mff_back_in_ntfy_p primary key (id)
);