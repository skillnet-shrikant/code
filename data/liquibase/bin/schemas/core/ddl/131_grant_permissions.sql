/* *******************************************************
	For Inactive Accounts Credit Card info removal
   *******************************************************
*/

grant select, update, delete, insert on dcs_user to ATG_CATFEED;
grant select, update, delete, insert on mff_user to ATG_CATFEED;
grant select, update, delete, insert on dcspp_order to ATG_CATFEED;
grant select, update, delete, insert on dps_user to ATG_CATFEED;