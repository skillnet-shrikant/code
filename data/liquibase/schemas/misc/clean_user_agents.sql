delete from  DPI_OTHER_ADDR where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_ADDRESS where ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_SCENARIO_VALUE where ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_MAILING where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_ORG where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_ORG_ANC where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_PREVPWD where ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_ROLES where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_SCENARIO where ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER_SEC_ORGS where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  EPUB_INT_PRJ_HIST where USER_ID not in ('svcUserAdmin','portal-admin');
delete from  TKT_PUSH_AGENT where ID not in ('svcUserAdmin','portal-admin');
delete from  CSR_AGENT_APP_LIMIT where AGENT_ID not in ('svcUserAdmin','portal-admin');
delete from  DPI_USER where ID not in ('svcUserAdmin','portal-admin');

commit;

exit;