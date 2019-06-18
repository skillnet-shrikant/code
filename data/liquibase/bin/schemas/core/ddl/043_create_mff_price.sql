create table mff_price
(
	price_id varchar2(40),
	promo_id varchar2(40),
  constraint mff_price_p primary key(price_id),
  constraint mff_price_f foreign key(price_id) references dcs_price(price_id)
	
);