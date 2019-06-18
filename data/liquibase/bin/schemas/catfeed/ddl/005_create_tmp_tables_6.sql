create table tmp_inv_csv
(
  inventory_id varchar2(40),
  retailer_id         varchar2(40),
  sku_id      varchar2(40),
  store_id           varchar2(40),
  threshold          number,
  avail_qty      number,
  unit_retail number,
  tax_rate   varchar2(40),
  sell_thru  varchar2(40)
);

