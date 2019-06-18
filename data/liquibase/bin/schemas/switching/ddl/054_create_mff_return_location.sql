create table mff_return_location (
        location_id     varchar2(40)    not null,
        return_location_id     varchar2(40)    null
,constraint mff_return_location_p primary key (location_id)
,constraint mff_loc_d_f foreign key (location_id) references dcs_location (location_id)
,constraint mff_retloc_d_f foreign key (return_location_id) references dcs_location (location_id));