create table mff_store_hours (
        store_hours_id  varchar2(40)    not null,
		day_type		varchar2(3)     not null,
        holiday_description     varchar2(2000),
		holiday_date    date,
		opening_time    timestamp(6)    not null,
		closing_time    timestamp(6)    not null,
		asset_version      int           not null,
		workspace_id       varchar2(40)   not null,
		branch_id          varchar2(40)   not null,
		is_head            numeric(1)    not null,
		version_deleted    numeric(1)    not null,
		version_editable   numeric(1)    not null,
		pred_version       int           null,
		checkin_date       timestamp     null,
		PRIMARY KEY (store_hours_id, asset_version)
        );
		
create table mff_store_rltd_hours (
		store_hours_id  varchar2(40)    not null,
		location_id        varchar2(40)    not null,
		sequence_num    integer         not null,
		asset_version      int           not null,
		sec_asset_version      int
		);		