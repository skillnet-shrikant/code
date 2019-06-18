create or replace package allocation_config as
    function get_value(p_key in varchar2) return varchar2;
    procedure load;
    procedure debug_print;
end allocation_config;
