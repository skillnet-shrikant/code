create table migration_log (
id varchar2(40),
login varchar2(100),
email varchar2(100),
migrate_user number(1) default 0,
reject_reason varchar2(254));