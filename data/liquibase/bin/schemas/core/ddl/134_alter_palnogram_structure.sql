alter table item_planogram add
(
	serialNumber			varchar2(40)	null	
);

alter table item_planogram drop constraint planogram_id;
alter table item_planogram add constraint item_planogram_pk PRIMARY KEY(planogram_id);