create table google_mff_category_map (
	id varchar(40) not null,
	mff_category_id varchar(40),
	mff_category_name varchar(244),
	google_category_id varchar(40),
	google_category_name varchar(244),
constraint google_mff_category_map_pk primary key (id));

create index gglmffcatmap_IDX1 on google_mff_category_map(mff_category_id);
create index gglmffcatmap_IDX2 on google_mff_category_map(google_category_id);