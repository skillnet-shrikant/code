create table mff_sku_computed (
  sku_id varchar2(40) not null, 
  online_available number(1), 
  last_modified_time timestamp, 
  constraint mff_sku_computed_pk primary key (sku_id) enable
);

create index mff_sku_cmptd_idx1 on mff_sku_computed (online_available);
