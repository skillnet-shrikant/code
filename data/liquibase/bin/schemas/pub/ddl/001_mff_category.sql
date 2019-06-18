create table mff_category (
  category_id    varchar2(40) not null,
  
  -- custom properties go here
  cat_level number null,
  template_id varchar2(254),
  batch_id varchar2(40),
  asset_version   int   not null,
  constraint mff_category_p primary key(category_id,asset_version)
);