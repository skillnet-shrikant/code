create or replace package mff_logger as
  msg_debug  constant varchar2(10) := 'debug';
  msg_info   constant varchar2(10) := 'info';
  msg_warn   constant varchar2(10) := 'warn';
  msg_error  constant varchar2(10) := 'error';

  msg_debug_level  constant integer := 1;
  msg_info_level   constant integer := 2;
  msg_warn_level   constant integer := 3;
  msg_error_level  constant integer := 4;

  type status_rec_type is table of mff_job_status%rowtype;
  
  procedure log_msg (  p_app_node     in mff_log.app_node%type
                     , p_component    in mff_log.component%type
                     , p_log_level    in integer
                     , p_msg_code     in mff_log.msg_code%type
                     , p_msg_text     in mff_log.msg_text%type
                     , p_stack_trace  in mff_log.stack_trace%type default null );

  procedure add_msg (  p_app_node     in mff_log.app_node%type
                     , p_component    in mff_log.component%type
                     , p_log_level    in integer
                     , p_msg_code     in mff_log.msg_code%type
                     , p_msg_text     in mff_log.msg_text%type
                     , p_stack_trace  in mff_log.stack_trace%type default null );

  procedure purge (  p_day_older in number
                   , p_component in mff_log.component%type default null);

  procedure log_sp_debug(  p_component in mff_log.component%type
                         , p_msg_text  in mff_log.msg_text%type);
  
  procedure log_sp_info(  p_component in mff_log.component%type
                        , p_msg_text  in mff_log.msg_text%type);
  
  procedure log_sp_error( p_component in mff_log.component%type);
    
  procedure add_sp_debug(  p_component in mff_log.component%type
                         , p_msg_text  in mff_log.msg_text%type);
  
  procedure add_sp_info(  p_component in mff_log.component%type
                        , p_msg_text  in mff_log.msg_text%type);

  procedure add_sp_warn(  p_component in mff_log.component%type
                        , p_msg_text  in mff_log.msg_text%type);
                        
  procedure add_sp_error(  p_component in mff_log.component%type
                         , p_msg_prefix in mff_log.msg_text%type default null);

  procedure flush;
  procedure get_log_level (p_level out integer);
  procedure set_log_level (p_level in integer);
    
  function job_status (p_component in varchar2, p_interval_min in integer) 
    return status_rec_type pipelined;
    
end mff_logger;