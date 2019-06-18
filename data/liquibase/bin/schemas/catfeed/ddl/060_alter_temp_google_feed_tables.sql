alter table google_feed_data_onsale
add (age_group varchar2(40) NULL,
	 gender varchar2(40) NULL,
	 color varchar2(40) NULL,
	 sku_size varchar2(40) NULL
	 );
  
  
alter table google_feed_data_regular
add (age_group varchar2(40) NULL,
	 gender varchar2(40) NULL,
	 color varchar2(40) NULL,
	 sku_size varchar2(40) NULL
	 );