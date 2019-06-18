create table mff_alert_query
(
	alert_id		varchar(40)   	not null,
	job_name		varchar2(254) 	not null,
	job_desc		varchar2(4000) 	not null,
	alert_query		clob		not null,
	schedule_string		varchar2(254)	not null,
	enabled			number(1),
	job_id			varchar2(40)	null,
	email_distro		varchar2(4000) 	not null,
	email_subject		varchar2(4000)	not null,
	email_body		varchar2(4000)  not null,
	constraint mff_alert_query_pk primary key (alert_id)
);