create table allocation_rules_config (
  rule_name      varchar2(254), 
  proc_name      varchar2(254),
  seq_num        number(38),
  enabled        number(1),   
  category       varchar2(40)
);