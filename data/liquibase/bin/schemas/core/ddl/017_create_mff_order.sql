create table MFF_ORDER (
	ORDER_ID 		varchar(40) not null references dcspp_order (order_id),
	ORDER_NUMBER       VARCHAR(40),	
	constraint mff_order_pk primary key (ORDER_ID)
);