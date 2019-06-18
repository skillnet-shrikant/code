create table mff_agent_log
(
	action_log_id			varchar(40)   	not null,
	agent_id				varchar2(40) 	null,
	order_number			varchar2(40) 	null,
	action					 varchar2(40) 	null,
	creation_date			timestamp (6)	null,
	constraint mff_agent_log_pk primary key (action_log_id)
);