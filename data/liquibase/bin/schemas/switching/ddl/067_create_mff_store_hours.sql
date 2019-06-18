create table mff_store_hours (
        store_hours_id  varchar2(40)    not null,
		day_type		varchar2(3)     not null,
        holiday_description     varchar2(2000),
		holiday_date    date,
		opening_time    timestamp(6)    not null,
		closing_time    timestamp(6)    not null,
		PRIMARY KEY (store_hours_id)
        );
		
create table mff_store_rltd_hours (
		store_hours_id  varchar2(40)    not null,
		location_id        varchar2(40)    not null,
		sequence_num    integer         not null
		);		