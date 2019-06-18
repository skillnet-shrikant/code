CREATE TABLE mff_site_configuration (
	site_id 						VARCHAR2(40) 			NOT NULL, 
 	item_quantity_threshold         NUMBER(38,2) DEFAULT 0  NOT NULL,
	order_quantity_threshold        NUMBER(38,2) DEFAULT 0  NOT NULL,
	asset_version 					NUMBER(19) 				NOT NULL,
	constraint mff_site_configuration_pk primary key (site_id,asset_version)
);
