alter table google_feed_data_onsale
add (fulfillment_method number(2) NULL
	 );

alter table google_feed_data_regular
add (fulfillment_method number(2) NULL
	 );