alter table google_mff_category_map
drop constraint google_mff_category_map_pk;

alter table google_mff_category_map modify mff_category_id varchar(40) NOT NULL;

alter table google_mff_category_map
add constraint google_mff_category_map_pk PRIMARY KEY(mff_category_id);

alter table google_mff_category_map drop column id;