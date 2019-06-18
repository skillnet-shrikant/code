create table mff_itemprev_allocations
(
	commerce_item_id	varchar(40) not null references dcspp_item (commerce_item_id),
	store				varchar2(10) not null
);