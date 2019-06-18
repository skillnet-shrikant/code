CREATE TABLE MFF_ORDER_DASHBOARD(
  order_id 	varchar2(40) NOT NULL,
  quantity		number(5,0),
  shipped_quantity		number(5,0),
  returned_quantity		number(5,0),
  canceled_quantity		number(5,0),
  amount	number(19,7)	null,
  canceled_amount	number(19,7)	null,
  returned_amount	number(19,7)	null,
  shipped_amount	number(19,7)	null,
  gift_amount	number(19,7)	null,
  status	integer	null,
  discount_amount	number(19,7)	null,
  shipping_amount	number(19,7)	null,
  tax_amount	number(19,7)	null,
  submitted_date	timestamp	null,
  modified_date	timestamp	null
);
