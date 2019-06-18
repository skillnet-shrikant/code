create table mff_location (
  location_id    varchar2(40) not null,
  
  -- custom properties go here
  description  varchar2(2000) null,
  label  varchar2(254) null,
  address_4  varchar2(50) null,
  address_5  varchar2(50) null,
  primary_category  varchar2(254) null,
  website  varchar2(3000) null,
  addln_phones  varchar2(254) null,
  asset_version   int   not null,
  constraint mff_location_p primary key(location_id,asset_version)
);
