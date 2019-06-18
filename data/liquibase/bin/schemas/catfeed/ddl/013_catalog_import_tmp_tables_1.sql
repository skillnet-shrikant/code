create table tmp_prod_data (
  product_id varchar2(254),
  description varchar2(4000),
  selling_point_1 varchar2(4000),
  selling_point_2 varchar2(4000),
  parent_category varchar2(254),
  weight_description varchar2(4000),
  dimension_description varchar2(4000),
  activate_date date,
  no_of_alt_images number,
  batch_id varchar2(254),
  prod_exists number(1),
  create_new_product number(1),
  prod_change number(1),
  prod_remove number(1),
  prod_skus_change number(1),
  prod_has_new_skus number(1)
);