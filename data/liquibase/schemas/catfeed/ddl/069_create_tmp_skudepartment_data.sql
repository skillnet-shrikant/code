
create table tmp_sku_department_data (
sku_id        varchar2(40),       
department_name varchar2(120) );

create index tmp_sku_department_data_sid on tmp_sku_department_data(sku_id);