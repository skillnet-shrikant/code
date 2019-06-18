create table tmp_catg_data (
  category_id varchar2(40),
  description varchar2(4000),
  parent_id varchar2(40),
  catg_level number,
  activation_date date,
  deactivation_date date,
  template_id varchar2(254),
  batch_id varchar2(254),
  catg_exists number(1),
  create_new_catg number(1),
  catg_change number(1)
);