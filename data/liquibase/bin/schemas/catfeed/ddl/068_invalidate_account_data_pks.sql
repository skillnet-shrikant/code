create or replace package invalidate_account_data_pkg AUTHID CURRENT_USER
as
	procedure force_password_reset (durationMonths NUMBER, useLastActivity NUMBER);
	procedure paswd_rst_ordr_submtd (durationMonths NUMBER);
	procedure paswd_rst_lst_actvty (durationMonths NUMBER);
end invalidate_account_data_pkg;