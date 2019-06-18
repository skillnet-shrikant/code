
-- Run this in the CORE and OMS Schema, if you want to clean both
-- This sql needs to be run if there is data in these tables before the AES encryption starts
-- Custom Profile Tables for MFF
delete from  MFF_USER;
delete from  MFF_USER_TAX_EXMPTION;
delete from  MFF_TAX_EXEMPTIONS;
delete from  MFF_REC_VIEWED;
delete from  MFF_TAX_EXEMPTION;
delete from  MFF_USER_ORG_ADDRESS;
--delete from  MFF_CREDIT_CARD;
delete from  MFF_BACK_IN_STOCK;
delete from  MFF_REC_VIEWED_PROD;
delete from  ACI_PROFILE_CREDIT_CARD;

delete from DPS_USER_PREVPWD;
delete from DPS_USER_ORG_ANC;
delete from DPS_USER_ORG;
delete from DPS_USER_ROLES;
delete from DPS_USER_MAILING;
delete from DPS_OTHER_ADDR;
delete from DPS_USER_ADDRESS;
delete from DPS_USER_SCENARIO;
delete from DPS_USR_MARKERS;
delete from DCS_USR_ACTVPROMO;
delete from DBC_ORG_APPROVER;
delete from DBC_BUYER_PREFVNDR;
delete from DPS_USER_SLOT;
delete from DPS_USER_PREVPWD;
delete from DPS_USR_CREDITCARD;
delete from DCS_USER;
delete from DPS_CREDIT_CARD;
delete from DPS_SCENARIO_VALUE;
delete from DBC_BUYER_APPROVER;
delete from AGENT_PROFILE_CMTS;

delete from DPS_USER;  

DELETE FROM srch_profile_tokens;

DELETE FROM srch_profile_modified;

commit;


exit;