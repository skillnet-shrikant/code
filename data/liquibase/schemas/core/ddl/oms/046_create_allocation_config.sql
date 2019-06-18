create table allocation_props (
  prop_name   varchar2(100) not null,
  prop_value  varchar2(500),
  constraint allocation_config_p primary key (prop_name) enable
);