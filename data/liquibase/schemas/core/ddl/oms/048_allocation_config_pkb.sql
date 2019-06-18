create or replace package body allocation_config as
    type varAssocArray is table of varchar2(500) index by varchar2(100);
    props varAssocArray;

--*****************************************************************************
-- Load properties from properties table.
--*****************************************************************************
procedure load as
begin
  props.delete;
	for r in (select prop_name, prop_value from allocation_props) loop
		props(r.prop_name) := r.prop_value;
	end loop;
end load;

--*****************************************************************************
-- Get property value by key.
--*****************************************************************************
function get_value(p_key in varchar2) return varchar2 as
  v_prop_value varchar2(500);
begin
  select prop_value into v_prop_value from allocation_props 
    where prop_name = p_key;
  return v_prop_value;

  --return props(p_key);
  exception
  when no_data_found then
    return null;  
end get_value;

--*****************************************************************************
-- Print all properties in name/value pair.
--*****************************************************************************
procedure debug_print as
  v_key varchar2(100);
begin
    load;
    v_key := props.first;
    while v_key is not null loop
      dbms_output.put_line('-- (name, value): (' || v_key || ', ' || props(v_key) || ')' );
      v_key := props.next(v_key);
    end loop;
end debug_print;

--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  dbms_output.put_line('allocation_config instance loaded');
end allocation_config;
