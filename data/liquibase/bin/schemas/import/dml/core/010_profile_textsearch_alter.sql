CREATE OR REPLACE TYPE ATG_CORE.t_varchar2_tab AS TABLE OF VARCHAR2(32767);
CREATE OR REPLACE FUNCTION ATG_CORE.tab_to_string (p_varchar2_tab IN t_varchar2_tab, p_delimiter IN VARCHAR2 DEFAULT ' ') RETURN CLOB IS v_tmp_string varchar2(32767);
BEGIN 
  FOR i IN p_varchar2_tab.FIRST .. p_varchar2_tab.LAST LOOP
    IF i != p_varchar2_tab.FIRST THEN
    v_tmp_string := v_tmp_string || p_delimiter;
    END IF;
    v_tmp_string := v_tmp_string || p_varchar2_tab(i);
  END LOOP;
  RETURN v_tmp_string;
END tab_to_string;
