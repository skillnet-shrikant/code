alter table mff_prorate_item add 
(
  shipping_city_tax			  	number(19,7) 	null,
  shipping_county_tax			number(19,7) 	null,
  shipping_state_tax			number(19,7) 	null,
  shipping_district_tax			number(19,7) 	null,
  shipping_country_tax			number(19,7) 	null,
  item_city_tax 	        	number(19,7) 	null,
  item_county_tax	        	number(19,7) 	null,
  item_state_tax	        	number(19,7) 	null,
  item_district_tax	      		number(19,7) 	null,
  item_country_tax				number(19,7) 	null,
  list_price					number(19,7) 	null,
  sale_price					number(19,7) 	null,
  order_discount_share			number(19,7) 	null,
  discount_price				number(19,7) 	null
);

alter table mff_item add 
(
  shipping_city_tax			number(19,7) 	null,
  shipping_county_tax			number(19,7) 	null,
  shipping_state_tax			number(19,7) 	null,
  shipping_district_tax			number(19,7) 	null,
  shipping_country_tax			number(19,7) 	null
);
