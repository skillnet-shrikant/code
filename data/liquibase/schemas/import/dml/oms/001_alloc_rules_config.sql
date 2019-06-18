truncate table allocation_rules_config;
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_all_to_store','allocate_all_to_store',0,1,'ALLOCATE_ORDER');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_by_queue_ratio','allocate_by_queue_ratio',1,1,'ALLOCATE_ORDER');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_by_distance','allocate_by_distance',2,1,'ALLOCATE_ORDER');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_all_to_store','allocate_all_to_store',0,1,'ALLOCATE_ITEM');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_by_queue_ratio','allocate_by_queue_ratio',1,1,'ALLOCATE_ITEM');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('allocate_by_distance','allocate_by_distance',2,1,'ALLOCATE_ITEM');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('split_allocate_by_quantity','split_allocate_by_quantity',0,1,'SPLIT_ITEM');
insert into allocation_rules_config (RULE_NAME,PROC_NAME,SEQ_NUM,ENABLED,CATEGORY) values ('split_allocate_by_distance','split_allocate_by_distance',0,0,'SPLIT_ITEM');
