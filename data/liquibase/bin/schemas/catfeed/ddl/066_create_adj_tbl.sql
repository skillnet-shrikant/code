create table inventory_adjustment (
store_id varchar2(40),
rollup_inventory number(1),
processed number(1) default 0);

create table tmp_inv_adj (
sku_id varchar2(40),
qty number (5,0)
);

create table tmp_inv_adj_log (
order_ref varchar2(40),
order_number varchar2(40),
submitted_date timestamp,
order_state varchar2(255),
fulfillment_store varchar2(40),
catalog_ref_id varchar2(40),
qty number(5,0),
item_state varchar2(255),
creation_date timestamp);