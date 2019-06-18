alter table google_feed_data_onsale
add (sku_length NUMBER NULL,
	 girth NUMBER NULL,
	 width NUMBER NULL,
	 oversized NUMBER(1) NULL,
	 weight NUMBER NULL,
	 restrict_air NUMBER(1) NULL,
	 ltl_fuel_surcharge NUMBER(1) NULL,
	 eds NUMBER(1) NULL,
	 ltl_lift_gate NUMBER(1) NULL,
	 freight_class varchar2(254) NULL,
	 ltl_res_delivery NUMBER(1) NULL,
	 sku_depth NUMBER NULL,
	 long_light NUMBER(1) NULL,
	 shipping_surcharge_qnty_range varchar2(4000) NULL,
	 free_shipping NUMBER(1) NULL,
	 is_ffl NUMBER(1) NULL,
	 minimum_age NUMBER(38) NULL
	 );
  
  
alter table google_feed_data_regular
add (sku_length NUMBER NULL,
	 girth NUMBER NULL,
	 width NUMBER NULL,
	 oversized NUMBER(1) NULL,
	 weight NUMBER NULL,
	 restrict_air NUMBER(1) NULL,
	 ltl_fuel_surcharge NUMBER(1) NULL,
	 eds NUMBER(1) NULL,
	 ltl_lift_gate NUMBER(1) NULL,
	 freight_class varchar2(254) NULL,
	 ltl_res_delivery NUMBER(1) NULL,
	 sku_depth NUMBER NULL,
	 long_light NUMBER(1) NULL,
	 shipping_surcharge_qnty_range varchar2(4000) NULL,
	 free_shipping NUMBER(1) NULL,
	 is_ffl NUMBER(1) NULL,
	 minimum_age NUMBER(38) NULL
	 );