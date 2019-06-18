create table onsale_temp nologging as (select * from gle_lcl_st_inv_fd_onsale);
create table regular_temp nologging as (select * from gle_lcl_st_inv_fd_regular);