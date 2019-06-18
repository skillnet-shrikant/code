create or replace package body mff_computed_property as
  -- this pacakge name
  pkg_name      constant varchar2(50) := 'mff_computed_property';
  
  type t_deploy_schema_list is table of varchar2(100);
  m_deploy_schema_list t_deploy_schema_list;  
  
  m_curr_time timestamp;
  m_debug boolean;
  m_sql varchar2(4000);
  
  -- ==========================================================================
  procedure debug_print(p_text in varchar2) as
  begin
    if m_debug = true then
      dbms_output.put_line(p_text);
    end if;
  end debug_print;

  
  -- ==========================================================================
  function get_elapsed_sec (p_start in timestamp, p_end in timestamp) return varchar2 is
    v_interval    INTERVAL DAY(9) TO SECOND(6);
    v_elapsed_sec NUMBER(10,2);
  begin
    v_interval := p_end - p_start;
    v_elapsed_sec := abs(extract(SECOND FROM v_interval) 
      + extract(minute FROM v_interval) * 60 
      + extract(hour FROM v_interval) * 60 * 60 
      + extract(DAY FROM v_interval) * 24 * 60 * 60);
    
    return to_char(v_elapsed_sec, 'fm9999990.90');
  end get_elapsed_sec;
  
  -- ==========================================================================
  -- insert new sku to the computed table
  procedure init(p_schema in varchar2) as
  begin
  
    m_sql := '
      merge into ' || p_schema || '.mff_sku_computed dest
        using ' || p_schema || '.mff_sku src
        on (dest.sku_id = src.sku_id)
      when not matched then
        insert (sku_id, online_available, last_modified_time) 
        values (src.sku_id, 0, :t1)';
        
    debug_print(m_sql);
    execute immediate m_sql using m_curr_time;
    
  end init;
  
  -- ==========================================================================
  procedure online_available as
  begin    
    -- online inventory = 0
    -- update atg_cata.mff_sku_computed msc set 
    --   msc.online_available = 0, msc.last_modified_time = m_curr_time
    -- where msc.sku_id in (
    --   select ms.sku_id from atg_cata.mff_sku ms
    --   join atg_core.mff_inventory mi on ms.sku_id = mi.catalog_ref_id
    --   where (mi.stock_level - (mi.sold+mi.allocated+mi.shipped)) <= 0 );

    -- online inventory = 1
    -- update atg_cata.mff_sku_computed msc set 
    --   msc.online_available = 1, msc.last_modified_time = m_curr_time
    -- where msc.sku_id in (
    --   select ms.sku_id from atg_cata.mff_sku ms
    --   join atg_core.mff_inventory mi on ms.sku_id = mi.catalog_ref_id
    --   where (mi.stock_level - (mi.sold+mi.allocated+mi.shipped)) > 0 );

    -- Inactive products
    update atg_cata.mff_sku_computed msc set 
       msc.online_available = 0, msc.last_modified_time = m_curr_time
     where msc.sku_id in (
	select sku_id from atg_cata.dcs_sku where sku_id in (
	  select sku_id from atg_cata.dcs_prd_chldsku where product_id in 
	    (select product_id from atg_cata.dcs_product where 
	      (end_date is not null and end_date < sysdate) or
	      (start_date is not null and start_date > sysdate)
	    )
	)
     );

    -- active products
    update atg_cata.mff_sku_computed msc set 
      msc.online_available = 1, msc.last_modified_time = m_curr_time
    where msc.sku_id in (
	select sku_id from atg_cata.dcs_sku where sku_id in (
	  select sku_id from atg_cata.dcs_prd_chldsku where product_id in 
	    (select product_id from atg_cata.dcs_product where 
	      (end_date is null or end_date > sysdate) and
	      (start_date is null or start_date < sysdate)
	    )
	)
     );

  end online_available;
    
  
  -- ==========================================================================
  -- copy from cata to other scheam
  procedure copy_schema(p_schema in varchar2) as
  begin    

    m_sql := '
      merge into ' || p_schema || '.mff_sku_computed dest
      using atg_cata.mff_sku_computed src
      on (dest.sku_id = src.sku_id) 
      when not matched then
        insert (sku_id, online_available, last_modified_time)
        values (src.sku_id, src.online_available, :t1)
      when matched then update set
        dest.online_available = src.online_available,
        dest.last_modified_time = :t2';
    
    debug_print(m_sql);
    execute immediate m_sql using m_curr_time, m_curr_time;
    
  end copy_schema;
  
  -- ==========================================================================
  procedure copy_all as
    v_deploy_schema varchar2(20);
  begin    
	copy_schema('atg_catb');
	copy_schema('atg_pub');
--    for i in m_deploy_schema_list.first..m_deploy_schema_list.last
--    loop
--      v_deploy_schema := db_pkg_config.get_value(m_deploy_schema_list(i));
--      if v_deploy_schema is not null then 
--        copy_schema(v_deploy_schema);
--      end if;
--    end loop;

  end copy_all;
  
  -- ==========================================================================
  procedure compute as
    v_sec varchar2(10);
  begin
    m_curr_time := systimestamp;
   
    init('atg_cata');
    online_available;

    copy_all;
    commit;
    
    v_sec := get_elapsed_sec(m_curr_time, systimestamp);
    
      mff_logger.log_sp_info(pkg_name || '.compute', 'Completed Successfully. Execution Time (sec): ' || v_sec);
      mff_logger.flush;

    exception
    when others then
      rollback;
      mff_logger.log_sp_info(pkg_name || '.compute','Error');
      mff_logger.flush;
      raise;       
  end; 
  
-- ****************************************************************************
-- initialization - only execute once after loading the package
-- ****************************************************************************
begin
  m_deploy_schema_list := t_deploy_schema_list();
  m_deploy_schema_list.extend(4);
  m_deploy_schema_list(1) := 'schema.catb';
  m_deploy_schema_list(2) := 'schema.staging.cata';
  m_deploy_schema_list(3) := 'schema.staging.catb';
  m_deploy_schema_list(4) := 'schema.pub';  

  mff_logger.log_sp_info(pkg_name, pkg_name || ' instance loaded');
end mff_computed_property;
