create table prod_updates (
  product_id varchar2(254),
  selling_points clob,
  is_in_store_only number(1),
  is_made_in_usa number(1),
  is_eds number(1),
  is_choking_hazard number(1),
  minimum_age integer,
  is_ffl number(1)
);