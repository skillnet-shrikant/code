drop table tmp_rule_3_results;
create table tmp_rule_3_results (
	store_id varchar2(40),
	distance number(38,7),
	ranking number(5,0),
	current_queue_size number(5,0),
	max_queue_size number(5,0)
);