create table mff_log 
(
  log_id number(*, 0) not null 
, log_time timestamp(6) 
, app_node varchar2(250) 
, component varchar2(250) 
, msg_type varchar2(10) 
, msg_code varchar2(50) 
, msg_text varchar2(4000) 
, stack_trace clob 
, constraint mff_log_pk primary key ( log_id ) enable 
);

create index mff_log_tme_idx on mff_log (log_time);

comment on column mff_log.msg_type is 'debug, info, warn, error';

create sequence mff_log_seq start with 1;


