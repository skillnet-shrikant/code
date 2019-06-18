-- Access Rights for PPS

insert into atg_agent.dpi_access_right (ACCESS_RIGHT_ID,RIGHT_TYPE,RIGHT_SCOPE,VERSION,NAME,DISPLAY_NAME,DESCRIPTION) values ('ppsStorePickPackRight',1,2,1,'ppsStorePickPack',null,'PPS Store Pick Pack Right');
insert into atg_agent.dpi_access_right (ACCESS_RIGHT_ID,RIGHT_TYPE,RIGHT_SCOPE,VERSION,NAME,DISPLAY_NAME,DESCRIPTION) values ('ppsStoreManagerRight',1,2,1,'ppsStoreManager',null,'PPS Store Manager Right');
insert into atg_agent.dpi_access_right (ACCESS_RIGHT_ID,RIGHT_TYPE,RIGHT_SCOPE,VERSION,NAME,DISPLAY_NAME,DESCRIPTION) values ('ppsStoreBopisFeoRight',1,2,1,'ppsStoreBopisFeo',null,'PPS Store Bopis Right');
insert into atg_agent.dpi_access_right (ACCESS_RIGHT_ID,RIGHT_TYPE,RIGHT_SCOPE,VERSION,NAME,DISPLAY_NAME,DESCRIPTION) values ('ppsStoreGateRight',1,2,1,'ppsStoreGate',null,'PPS Store Gate Right');
insert into atg_agent.dpi_access_right (ACCESS_RIGHT_ID,RIGHT_TYPE,RIGHT_SCOPE,VERSION,NAME,DISPLAY_NAME,DESCRIPTION) values ('ppsReturnsRight',1,2,1,'ppsReturns',null,'PPS Return Right');

-- Roles for PPS

insert into atg_agent.dpi_role (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) values ('ppsStorePickPack',2000,1,'PPS-Store-Pick-Pack','Role for PPS Store Pick Pack');
insert into atg_agent.dpi_role (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) values ('ppsStoreManager',2000,1,'PPS-Store-Manager','Role for PPS Store Manager');
insert into atg_agent.dpi_role (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) values ('ppsStoreBopisFeo',2000,1,'PPS-Store-Bopis-Feo','Role for PPS Store Bopis Feo');
insert into atg_agent.dpi_role (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) values ('ppsStoreGate',2000,1,'PPS-Store-Gate','Role for Store Gate');
insert into atg_agent.dpi_role (ROLE_ID,TYPE,VERSION,NAME,DESCRIPTION) values ('ppsReturns',2000,1,'PPS-Returns','Role for PPS Returns');

-- Roles location

insert into atg_agent.dpi_rolefold_chld (ROLEFOLD_ID,ROLE_ID) values ('csrRoleFolder','ppsStorePickPack');
insert into atg_agent.dpi_rolefold_chld (ROLEFOLD_ID,ROLE_ID) values ('csrRoleFolder','ppsStoreManager');
insert into atg_agent.dpi_rolefold_chld (ROLEFOLD_ID,ROLE_ID) values ('csrRoleFolder','ppsStoreBopisFeo');
insert into atg_agent.dpi_rolefold_chld (ROLEFOLD_ID,ROLE_ID) values ('csrRoleFolder','ppsStoreGate');
insert into atg_agent.dpi_rolefold_chld (ROLEFOLD_ID,ROLE_ID) values ('csrRoleFolder','ppsReturns');

-- Roles initial access rights

insert into atg_agent.dpi_role_right (ROLE_ID,ACCESS_RIGHT_ID) values ('ppsStorePickPack','ppsStorePickPackRight');
insert into atg_agent.dpi_role_right (ROLE_ID,ACCESS_RIGHT_ID) values ('ppsStoreManager','ppsStoreManagerRight');
insert into atg_agent.dpi_role_right (ROLE_ID,ACCESS_RIGHT_ID) values ('ppsStoreBopisFeo','ppsStoreBopisFeoRight');
insert into atg_agent.dpi_role_right (ROLE_ID,ACCESS_RIGHT_ID) values ('ppsStoreGate','ppsStoreGateRight');
insert into atg_agent.dpi_role_right (ROLE_ID,ACCESS_RIGHT_ID) values ('ppsReturns','ppsReturnsRight');
