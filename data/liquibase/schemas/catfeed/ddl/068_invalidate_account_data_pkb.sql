create or replace package body invalidate_account_data_pkg
as

  PKG_NAME constant varchar2(50) := 'invalidate_account_data_pkg';
  
  -- main procedure that performs all tasks related to invalud account cc removal
  
PROCEDURE force_password_reset (durationMonths NUMBER, useLastActivity NUMBER) AS
BEGIN
  CASE useLastActivity
    WHEN 0 THEN paswd_rst_ordr_submtd(durationMonths);
    WHEN 1 THEN paswd_rst_lst_actvty(durationMonths);
  END CASE;
END;
 
PROCEDURE paswd_rst_ordr_submtd (durationMonths NUMBER) AS
    l_sql_usrid varchar2(1000);
    l_c_usrid   sys_refcursor;    
    usr_rec ATG_CORE.dps_user.id%TYPE;

BEGIN
--insert into tmp_c values('Entered invalidAccountsCC_removal with durationMonths-->'||durationMonths);commit;
   l_sql_usrid :='select distinct uidbelow.user_id from (select distinct du.id as user_id from ATG_CORE.dps_user du inner join ATG_CORE.dcspp_order do on du.id=do.profile_id where SUBMITTED_DATE <= add_months(sysdate,-'||durationMonths||'))uidbelow '||
   				 'where uidbelow.user_id not in '||
   				 '(select distinct du.id as user_id from ATG_CORE.dps_user du inner join ATG_CORE.dcspp_order do on du.id=do.profile_id where SUBMITTED_DATE > add_months(sysdate,-'||durationMonths||'))';
   -- insert into tmp_c values(l_sql);commit;
   open l_c_usrid for l_sql_usrid;               
     loop
        fetch l_c_usrid into usr_rec;
        exit when l_c_usrid%notfound; 
		--insert into tmp_c values('Entered for loop with cc_rec.credit_card_id-->'||cc_rec.credit_card_id);
        update ATG_CORE.mff_user mu set mu.force_reset_password=1 where mu.user_id=usr_rec;
     END loop;
   close l_c_usrid;   
 END;
 
PROCEDURE paswd_rst_lst_actvty (durationMonths NUMBER) AS
    l_sql_usrid varchar2(1000);
    l_c_usrid   sys_refcursor;    
    usr_rec ATG_CORE.dps_user.id%TYPE;

BEGIN
--insert into tmp_c values('Entered invalidAccountsCC_removal with durationMonths-->'||durationMonths);commit;
   l_sql_usrid :='select distinct du.id as user_id from ATG_CORE.dps_user du where LASTACTIVITY_DATE is not null and LASTACTIVITY_DATE <= add_months(sysdate,-'||durationMonths||')';
   -- insert into tmp_c values(l_sql);commit;
   open l_c_usrid for l_sql_usrid;               
     loop
        fetch l_c_usrid into usr_rec;
        exit when l_c_usrid%notfound; 
		--insert into tmp_c values('Entered for loop with cc_rec.credit_card_id-->'||cc_rec.credit_card_id);
        update ATG_CORE.mff_user mu set mu.force_reset_password=1 where mu.user_id=usr_rec;
     END loop;
   close l_c_usrid;   
 END;
  

--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('invalidate_account_data_pkg', 'Initializing package invalidate_account_data_pkg');
end invalidate_account_data_pkg;