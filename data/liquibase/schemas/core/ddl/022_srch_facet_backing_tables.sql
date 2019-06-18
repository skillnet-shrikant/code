create table mff_sku_facet (
	sku_id 		varchar2(40),
	constraint mff_sku_facet_p primary key (sku_id) enable
);
create table mff_sf_dyn_prop_map_big_str (
	id 		varchar2(40) not null,
  	prop_name 	varchar2(254) not null,
  	prop_value 	clob,
  	constraint mff_sf_dyn_prop_map_big_str_p primary key (id,prop_name)
);

create table mff_sf_dyn_prop_map_str (
  	id 		varchar2(40) not null,
  	prop_name 	varchar2(254) not null,
  	prop_value 	varchar2(400),
  	constraint mff_sf_dyn_prop_map_str_p primary key (id,prop_name)
);
create table mff_sf_dyn_prop_map_long (
  	id 		varchar2(40) not null,
  	prop_name 	varchar2(254) not null,
  	prop_value 	number(19),
  	constraint mff_sf_dyn_prop_map_long_p primary key (id,prop_name)
);
create table mff_sf_dyn_prop_map_double (
        id      	varchar2(40)    not null,
        prop_name       varchar2(254)   not null,
        prop_value      number(19,7)    null,
        constraint mff_sf_dyn_prop_map_double_p primary key (id,prop_name)
);