alter table mff_prorate_item drop
(
  shipping_city_tax,
  shipping_county_tax,
  shipping_state_tax,
  shipping_district_tax,
  shipping_country_tax,
  item_city_tax,
  item_county_tax,
  item_state_tax,
  item_district_tax,
  item_country_tax,
  list_price,
  sale_price,
  order_discount_share,
  discount_price
);

alter table mff_item drop
(
  shipping_city_tax,
  shipping_county_tax,
  shipping_state_tax,
  shipping_district_tax,
  shipping_country_tax
);
