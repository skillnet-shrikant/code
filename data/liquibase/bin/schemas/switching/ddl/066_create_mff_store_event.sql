create table mff_store_event (
        event_id  varchar2(40)    not null,
        event_description     varchar2(2000) not null,
		event_short_detail varchar2(256)    not null,
		event_long_detail     varchar2(2000),
		event_date    date not null,
		event_start_time    timestamp(6)    not null,
		event_end_time    timestamp(6)    not null,
		event_display_start_time    timestamp(6),
		PRIMARY KEY (event_id)
        );
		
create table mff_store_rltd_event (
		event_id  varchar2(40)    not null,
		location_id        varchar2(40)    not null,
		sequence_num    integer         not null
		);