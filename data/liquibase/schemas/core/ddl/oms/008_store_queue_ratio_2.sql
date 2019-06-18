drop table store_queue_ratio;
create table store_queue_ratio (	
	store_id 	varchar2(40),
	current_queue_size	number(5,0),
	max_queue_size number(5,0),
	ratio	number(38,7),
	ranking number(5,0)
);