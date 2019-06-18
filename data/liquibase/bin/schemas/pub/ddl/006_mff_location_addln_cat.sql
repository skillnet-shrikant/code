CREATE TABLE mff_location_addln_cat (
  location_id  VARCHAR(32) not null,
  seq_num     INTEGER not null,
  category     VARCHAR(32) null,
  asset_version   int   not null,
  primary key(location_id, seq_num,asset_version)
);