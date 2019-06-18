CREATE TABLE mff_location_addln_cat (
  location_id  VARCHAR(32) not null,
  seq_num     INTEGER not null,
  category     VARCHAR(32) null,
  primary key(location_id, seq_num),
  constraint mff_location_addln_cat_f foreign key(location_id) references dcs_location(location_id)
);