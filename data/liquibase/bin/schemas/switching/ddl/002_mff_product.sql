create table mff_product (
  product_id    varchar2(40) not null,
  
  -- custom properties go here
  selling_point_1  varchar2(2000) null,
  selling_point_2  varchar2(2000) null,
  selling_point_3  varchar2(2000) null,
  selling_point_4  varchar2(2000) null,
  selling_point_5  varchar2(2000) null,
  wt_description  varchar2(2000) null,
  dim_description  varchar2(2000) null,
  num_alt_images number null,
  batch_id varchar2(40),

  constraint mff_product_p primary key(product_id),
  constraint mff_product_f foreign key(product_id) references dcs_product(product_id)
);