create table mff_store_landing_page
  (
    location_id        varchar2(40) not null,
    store_landing_page varchar2(254) not null,
	asset_version   int   not null,	
    constraint mff_store_landing_page_p primary key(location_id,asset_version)
  );