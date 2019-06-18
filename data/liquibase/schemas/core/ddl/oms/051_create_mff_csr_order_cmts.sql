create table mff_csr_order_cmts (
	comment_id   varchar2(40) not null,
	store_number varchar2(40),
	constraint mff_csr_order_cmts_pk primary key (comment_id)
);