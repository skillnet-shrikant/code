create table global_messages (
	message_id varchar(40) NOT NULL,
	message_text varchar(4000),
	message_type number(38,0),
	message_start_time    timestamp(6)    not null,
	message_end_time    timestamp(6)    not null,
	message_destination number(38,0),
	ASSET_VERSION number(19) NOT NULL,
	BRANCH_ID varchar2(40) NOT NULL,
	workspace_id varchar2(40) NOT NULL,
	is_head number(1,0) NOT NULL,
	version_deleted number(1,0) NOT NULL,
	version_editable number(1,0) NOT NULL,
	pred_version number(19,0),
	checkin_date timestamp(6),
constraint global_messages_pk primary key (message_id,ASSET_VERSION));

create index gblmessage_IDX1 on global_messages(message_id);