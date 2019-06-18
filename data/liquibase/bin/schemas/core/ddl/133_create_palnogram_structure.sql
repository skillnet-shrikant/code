create table item_department (
	item_id   varchar2(40) not null,
	department_name varchar2(120),
	constraint item_department_pk primary key (item_id)
);

create table item_planogram (
	planogram_id   varchar2(40) not null,
	item_id   varchar2(40) not null,
	store_id   varchar2(40) not null,
	planogram_name   varchar2(120), 
	planogram_address   varchar2(40),
	constraint planogram_id primary key (item_id)
);

create index itm_dept_name_idx on item_department (department_name);
create index itm_pog_qry_idx on item_planogram (item_id,store_id);