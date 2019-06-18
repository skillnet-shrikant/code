create or replace
package body mff_google_recompile_package as

  PKG_NAME constant varchar2(50) := 'mff_google_recompile_package';
  
  -- Re Compile Package
  procedure run as
  begin
  	execute immediate 'ALTER PACKAGE ATG_CATFEED.gle_lcl_str_inv_fd_crtr COMPILE PACKAGE';
  end;

  
  
   
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('mff_google_recompile_package', 'Initializing package mff_google_recompile_package');
end mff_google_recompile_package;