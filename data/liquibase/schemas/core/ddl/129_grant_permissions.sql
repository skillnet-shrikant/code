/* *******************************************************
	For Inactive Accounts Credit Card info removal
   *******************************************************
*/

grant select, update, delete, insert on dcs_user to ATG_CATFEED;
grant select, update, delete, insert on dps_usr_creditcard to ATG_CATFEED;
grant select, update, delete, insert on dps_credit_card to ATG_CATFEED;
grant select, update, delete, insert on aci_profile_credit_card to ATG_CATFEED;
grant select, update, delete, insert on dcspp_order to ATG_CATFEED;