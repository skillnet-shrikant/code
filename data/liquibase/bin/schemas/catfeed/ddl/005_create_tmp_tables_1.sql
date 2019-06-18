create table tmp_xml
    (filename varchar2(80), xml_col XMLTYPE)
     XMLTYPE xml_col STORE AS SECUREFILE BINARY XML;