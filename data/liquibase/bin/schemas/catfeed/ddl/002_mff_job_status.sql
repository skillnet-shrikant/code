create or replace view mff_job_status as
select 
  start_log_id, end_log_id, component, row_num,
  start_time, end_time,
  case 
    when msg_type = 'info'  and end_time is null then 'running'
    when msg_type = 'info'  and end_time > start_time then 'success'
    when msg_type = 'error' and end_time > start_time then 'failed'
    else 'unknown'
  end status
from (
select log_id, component, msg_code, msg_type, log_time,
  lag(case when msg_code = 'MFF-SERVICE-END' then 0 else 1 end) 
    over (partition by component order by log_id desc) status_row,
  row_number() over (partition by component order by log_id desc) row_num,
  coalesce(
    lead(case when msg_code = 'MFF-SERVICE-START' then log_id else null end) 
      over (partition by component order by log_id desc),
    log_id
  ) start_log_id,  
  case when msg_code = 'MFF-SERVICE-END' then log_id else null end end_log_id,    
  coalesce(
    case when msg_code = 'MFF-SERVICE-START' then log_time else null end,
    lead(case when msg_code = 'MFF-SERVICE-START' then log_time else null end) 
      over (partition by component order by log_id desc) 
  ) start_time,
  case when msg_code = 'MFF-SERVICE-END' then log_time else null end end_time  
  from mff_log where log_time > trunc(sysdate-30)
  and msg_code in ('MFF-SERVICE-END', 'MFF-SERVICE-START') 
) where status_row is null or status_row = 1;

