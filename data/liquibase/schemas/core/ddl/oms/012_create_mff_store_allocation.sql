create table mff_store_allocation (
	store_allocation_id		varchar2(40) 	not null,
    order_id				varchar2(40) 	not null,
	commerce_item_id		varchar2(40) 	null,
	store_id				varchar2(10) 	null,
	sku_id					varchar2(40) 	null,
	quantity				number(19,0) 	null,
	order_date				timestamp(6)	null,
	allocation_date			timestamp(6)	null,	
	decline_date			timestamp(6)	null,	
	ship_date				timestamp(6)	null,	
	state					varchar2(40) 	null,
	state_detail			varchar2(254) 	null,
  constraint mff_store_allocation_p primary key (store_allocation_id)
);


-- Create a non-unique index on order Id
CREATE INDEX mff_store_allocation_idx_1 ON mff_store_allocation (order_id);

-- Create a non-unique index on order number
CREATE INDEX mff_store_allocation_idx_2 ON mff_store_allocation (store_id, allocation_date, state);