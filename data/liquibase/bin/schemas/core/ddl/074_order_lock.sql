create table oms_order_lock (
	order_id varchar(40) not null,
	lock_id varchar(40) not null,
	server_ip varchar(40) not null,
	lock_time timestamp not null,
	constraint oms_order_lock_pk primary key (order_id)
);