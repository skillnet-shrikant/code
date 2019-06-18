alter table tmp_sku_data add (
	free_shipping number(1) default 0,
	discountable number(1) default 0
);