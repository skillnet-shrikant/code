create table mff_store_event (
        event_id  varchar2(40)    not null,
        event_description     varchar2(2000) not null,
		event_short_detail varchar2(256)    not null,
		event_long_detail     varchar2(2000),
		event_date    date not null,
		event_start_time    timestamp(6)    not null,
		event_end_time    timestamp(6)    not null,
		event_display_start_time    timestamp(6),
		asset_version      int           not null,
		workspace_id       varchar2(40)   not null,
		branch_id          varchar2(40)   not null,
		is_head            numeric(1)    not null,
		version_deleted    numeric(1)    not null,
		version_editable   numeric(1)    not null,
		pred_version       int           null,
		checkin_date       timestamp     null,
		PRIMARY KEY (event_id, asset_version)
		
        );
		
create table mff_store_rltd_event (
		event_id  varchar2(40)    not null,
		location_id        varchar2(40)    not null,
		sequence_num    integer         not null,
		asset_version      int           not null,
		sec_asset_version      int
		);