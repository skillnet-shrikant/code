alter table mff_location add page_store_name varchar2(254);
alter table mff_location add page_store_address varchar2(2000);
alter table mff_location add page_phone_number varchar2(254);
alter table mff_location add page_store_hours_header varchar2(254);
alter table mff_location add page_nearby_location_header varchar2(254);
alter table mff_location add page_WeeklyAd_Enabled NUMBER(1) default 1;
alter table mff_location add page_WeeklyAd_header varchar2(254);
alter table mff_location add page_WeeklyAd_body clob;
alter table mff_location add page_Text_Promotions_Enabled NUMBER(1) default 1;
alter table mff_location add page_Text_Promotions_header varchar2(254);
alter table mff_location add page_Text_Promotions_body clob;
alter table mff_location add page_Email_Promotions_Enabled NUMBER(1) default 1;
alter table mff_location add page_Email_Promotions_header varchar2(254);
alter table mff_location add page_Email_Promotions_body clob;
alter table mff_location add page_connecton_fb_Enabled NUMBER(1) default 1;
alter table mff_location add page_connecton_fb_header varchar2(254);
alter table mff_location add page_connecton_fb_body clob;
alter table mff_location add page_catalog_signup_Enabled NUMBER(1) default 1;
alter table mff_location add page_catalog_signup_header varchar2(254);
alter table mff_location add page_catalog_signup_body clob;
alter table mff_location add page_gas_mart_Enabled NUMBER(1) default 1;
alter table mff_location add page_gas_mart_header varchar2(254);
alter table mff_location add page_gas_mart_body clob;
alter table mff_location add page_cstore_Enabled NUMBER(1) default 1;
alter table mff_location add page_cstore_header varchar2(254);
alter table mff_location add page_cstore_body clob;
alter table mff_location add page_car_wash_Enabled NUMBER(1) default 1;
alter table mff_location add page_car_wash_header varchar2(254);
alter table mff_location add page_car_wash_body clob;
alter table mff_location add page_auto_center_Enabled NUMBER(1) default 1;
alter table mff_location add page_auto_center_header varchar2(254);
alter table mff_location add page_auto_center_body clob;
alter table mff_location add page_garden_center_Enabled NUMBER(1) default 1;
alter table mff_location add page_garden_center_header varchar2(254);
alter table mff_location add page_garden_center_body clob;
alter table mff_location add page_store_events_header varchar2(254);
alter table mff_location add page_store_no_events_message varchar2(254);
alter table mff_location add page_career_header varchar2(254);
alter table mff_location add page_Store_Detail_header varchar2(254);
alter table mff_location add page_Store_Detail_body clob;
alter table mff_location add page_coming_soon_Enabled NUMBER(1) default 0;