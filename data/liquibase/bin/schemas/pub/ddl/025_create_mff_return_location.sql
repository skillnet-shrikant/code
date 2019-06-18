create table mff_return_location (
        asset_version   number(19)      not null,
        location_id     varchar2(40)    not null,
        return_location_id     varchar2(40)    null
,constraint mff_return_location_p primary key (location_id,asset_version));