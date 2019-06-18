create table mff_exch_item
(
	id				varchar(40) not null references csr_exch_item (id),
	comments		varchar2(2048) null,
	constraint mff_exch_item_pk primary key (id)
);