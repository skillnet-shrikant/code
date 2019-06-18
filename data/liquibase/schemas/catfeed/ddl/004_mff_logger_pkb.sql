create or replace package body mff_logger as
  
  type mff_log_type is table of mff_log%rowtype;
  buffer_size constant integer := 1000;
  log_rec     mff_log%rowtype;
  log_buffer  mff_log_type;
  log_level   integer;
  msg_count   integer;

  -- ==========================================================================
  function get_msg_type (p_log_level in integer) return varchar2 is
  begin
    case p_log_level
      when msg_error_level  then return msg_error;
      when msg_warn_level   then return msg_warn;
      when msg_info_level   then return msg_info;      
      when msg_debug_level  then return msg_debug;
      else return null;
    end case;
  end get_msg_type;

  -- ==========================================================================
  -- usage example:
  -- select * from table(mff_logger.job_status('productloaderservice',1440); 
  function job_status (p_component in varchar2, p_interval_min in integer) 
    return status_rec_type pipelined is
  v_threshold_dt date;
  begin
  
    v_threshold_dt := sysdate - numtodsinterval(p_interval_min, 'minute');

    for i in (
      -- return columns must match those of mff_job_status
      select start_log_id, end_log_id, component, row_num, start_time, end_time,
      case 
        when start_time < v_threshold_dt then '0'
        when status = 'success' then '1'
        when status = 'running' then '3'
        else '2' end status
      from mff_job_status where component = p_component and row_num = 1
    ) loop
      pipe row(i);
    end loop;
    return;
  end job_status;
  
  -- ==========================================================================  
  procedure set_log_rec (  p_app_node     mff_log.app_node%type
                         , p_component    mff_log.component%type
                         , p_log_level    in integer
                         , p_msg_code     mff_log.msg_code%type
                         , p_msg_text     mff_log.msg_text%type
                         , p_stack_trace  mff_log.stack_trace%type
                        ) is
  begin
    log_rec.log_time    := systimestamp;
    log_rec.app_node    := p_app_node;
    log_rec.component   := p_component;
    log_rec.msg_type    := get_msg_type(p_log_level);
    log_rec.msg_code    := p_msg_code;
    log_rec.msg_text    := p_msg_text;
    log_rec.stack_trace := p_stack_trace;
  end set_log_rec;

  -- --------------------------------------------------------------------------
  -- ==========================================================================
  procedure log_msg( p_app_node     mff_log.app_node%type
                   , p_component    mff_log.component%type
                   , p_log_level    in integer
                   , p_msg_code     mff_log.msg_code%type
                   , p_msg_text     mff_log.msg_text%type
                   , p_stack_trace  mff_log.stack_trace%type
                  ) is
  pragma autonomous_transaction;
  begin
    if p_log_level >= log_level then 
      set_log_rec ( p_app_node, p_component, p_log_level,    
                    p_msg_code, p_msg_text, p_stack_trace );
                    
      select mff_log_seq.nextval into log_rec.log_id from dual;
      insert into mff_log values log_rec;
      commit;
    end if;
    
    exception
    when others then
      -- we don't want an message logging to 
      -- cause the application to crash
      return;    
  end log_msg;                  

  -- ========================================================================== 
  procedure log_sp_info(  p_component  in mff_log.component%type                    
                        , p_msg_text   in mff_log.msg_text%type) is
  begin
    log_msg('database', upper(p_component), mff_logger.msg_info_level, 
      null, p_msg_text);
  end log_sp_info;

  -- ========================================================================== 
  procedure log_sp_error( p_component in mff_log.component%type ) is
    v_err_num varchar2(50);
  begin
    -- lpad(sqlcode, 5, '0') 
    v_err_num := 'ora-' || lpad(abs(sqlcode), 5, '0');
    log_msg ('database'
      , upper(p_component)
      , mff_logger.msg_error_level
      , v_err_num
      , sys.dbms_utility.format_error_stack
      , sys.dbms_utility.format_error_backtrace || chr(10) 
        || sys.dbms_utility.format_call_stack);
  end log_sp_error;
  
  -- ==========================================================================   
  procedure log_sp_debug(  p_component in mff_log.component%type
                         , p_msg_text  in mff_log.msg_text%type) is
  begin
    log_msg('database', upper(p_component), mff_logger.msg_debug_level, 
      null, p_msg_text);  
  end log_sp_debug;
  
  -- ======== buffered logging
  -- ==========================================================================
  procedure add_msg( p_app_node     mff_log.app_node%type
                   , p_component    mff_log.component%type
                   , p_log_level    in integer
                   , p_msg_code     mff_log.msg_code%type
                   , p_msg_text     mff_log.msg_text%type
                   , p_stack_trace  mff_log.stack_trace%type
                  ) is
  begin
    if p_log_level >= log_level then 
      set_log_rec ( p_app_node, p_component, p_log_level,    
                    p_msg_code, p_msg_text, p_stack_trace );

      msg_count := msg_count + 1;
      if msg_count = 1 then
        log_buffer.extend(buffer_size);
        log_buffer(msg_count) := log_rec;
      elsif msg_count <= buffer_size then
        log_buffer(msg_count) := log_rec;
      end if; 

      if msg_count = buffer_size then
        flush;
      end if;
    end if;

  end add_msg;

  -- ==========================================================================   
  procedure add_sp_debug(  p_component in mff_log.component%type                    
                         , p_msg_text  in mff_log.msg_text%type) is
  begin
    add_msg('database', upper(p_component), mff_logger.msg_debug_level, 
      null, p_msg_text);  
  end add_sp_debug;
  
  -- ========================================================================== 
  -- for stored procedure only
  procedure add_sp_info(  p_component in mff_log.component%type                    
                        , p_msg_text  in mff_log.msg_text%type) is
  begin
    add_msg('database', upper(p_component), mff_logger.msg_info_level,
      null, p_msg_text);
  end add_sp_info;
  
  -- ========================================================================== 
  procedure add_sp_warn(  p_component in mff_log.component%type
                        , p_msg_text  in mff_log.msg_text%type) is
  begin
    add_msg('database', upper(p_component), mff_logger.msg_warn_level,
      null, p_msg_text);  
  end add_sp_warn;
  
  -- ========================================================================== 
  -- for stored procedure only  
  procedure add_sp_error( p_component in mff_log.component%type
                         ,p_msg_prefix in mff_log.msg_text%type default null) is
    v_err_num varchar2(50);
    v_msg_text varchar2(4000);
  begin
    v_msg_text := sys.dbms_utility.format_error_stack;
    if p_msg_prefix is not null then
      v_msg_text := p_msg_prefix || ': ' || v_msg_text;
    end if;
    
    -- lpad(sqlcode, 5, '0') 
    v_err_num := 'ora-' || lpad(abs(sqlcode), 5, '0');
    add_msg ('database'
      , upper(p_component)
      , mff_logger.msg_error_level
      , v_err_num
      , v_msg_text
      , sys.dbms_utility.format_error_backtrace || chr(10) 
        || sys.dbms_utility.format_call_stack);
  end add_sp_error;

  -- ==========================================================================
  procedure flush is
  pragma autonomous_transaction;
  begin
    for i in log_buffer.first..msg_count loop
      select mff_log_seq.nextval into log_buffer(i).log_id from dual;
    end loop;
    
    forall i in log_buffer.first..msg_count
      insert into mff_log values log_buffer(i);
    commit;

    log_buffer.delete;
    log_rec := null;
    msg_count := 0;

    exception
    when others then
      -- we don't want an message logging to 
      -- cause the application to crash
      return;    
  end flush;

  -- ==========================================================================
  procedure get_log_level (p_level out integer) is 
  begin
    p_level := log_level;
  end get_log_level;
  
  -- ==========================================================================
  procedure set_log_level (p_level in integer) is
  begin
    if p_level is not null then
      log_level := p_level;
    end if;
  end set_log_level;

  -- ==========================================================================
  procedure purge (p_day_older in number, 
                   p_component in mff_log.component%type default null) is
    date_older date;
  begin
    date_older := trunc(sysdate - p_day_older);
    if p_component is null then
      delete from mff_log where trunc(log_time) < date_older;
    else
      delete from mff_log where trunc(log_time) < date_older and component = p_component;
    end if;
    commit;
  end purge;

begin
  -- default log level
  log_level := msg_info_level;  
  log_buffer := mff_log_type();
  msg_count := 0;
end mff_logger;
