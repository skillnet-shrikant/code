create table global_messages (
	message_id varchar(40) NOT NULL,
	message_text varchar(4000),
	message_type number(38,0),
	message_start_time    timestamp(6)    not null,
	message_end_time    timestamp(6)    not null,
	message_destination number(38,0),
	constraint global_messages_pk primary key (message_id));
	