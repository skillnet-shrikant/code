create table mff_allocation_log (	
	order_number	varchar2(40),
	status		varchar2(10),
	log_ts		timestamp(6),
	log_message	varchar2(2048)
);