drop table mff_store_landing_page;

create table mff_store_landing_page
  (
    location_id        varchar2(40) not null,
    store_landing_page varchar2(254) not null,
    constraint mff_store_landing_page_p primary key(location_id), 
    constraint mff_store_landing_page_dc_fk foreign key (location_id) references dcs_location (location_id),
    constraint mff_store_landing_page_wc_fk foreign key (store_landing_page) references wcm_article (id)
  );