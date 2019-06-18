create table tmp_current_inv_level(
    inventory_id varchar2(40),
    catalog_ref_id varchar2(40),
    stock_level number(5,0),
    sold number(5,0),
    allocated number(5,0),
    shipped number(5,0)
);