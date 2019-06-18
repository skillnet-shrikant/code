create table mff_info_message (
	id varchar2(40)	not null,
	info_key varchar2(254)	not null,
	info_msg clob 		not null,
	asset_version      int           not null,
	workspace_id       varchar2(40)   not null,
	branch_id          varchar2(40)   not null,
	is_head            numeric(1)    not null,
	version_deleted    numeric(1)    not null,
	version_editable   numeric(1)    not null,
	pred_version       int           null,
	checkin_date       timestamp     null,
	CONSTRAINT mff_inf_msg_pk primary key(id, asset_version)
);
CREATE index mff_inf_msg_ws_idx on mff_info_message (workspace_id);
CREATE index mff_inf_msg_ck_idx on mff_info_message (checkin_date);