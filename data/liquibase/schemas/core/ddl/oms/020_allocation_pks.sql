create or replace package allocation
as
	-- Order Item (Commerce item)
	TYPE OrderItem IS RECORD (
		orderItemId		varchar2(40),
		orderId			varchar2(40),
		commerceItemId		varchar2(40),
		sku_id			varchar2(40),
		quantity		number(5,0)
	);
	lOrderItem OrderItem;
	
	-- SKUs in Order 
	TYPE OrderSKU IS RECORD (
		orderId			varchar2(40),
		skuId			varchar2(40),
		quantity		number(5,0),
		commerceItemId		varchar2(40),
		requestedQty		number
	);
	lOrderSKU OrderSKU;

	-- Allocation Rules 
	TYPE AllocationRule IS RECORD (
		ruleName		varchar2(40),
		procName		varchar2(40),
		enabled			number(1)
	);
	lAllocationRule AllocationRule;
	
	-- Latitude/Longitude Coordinates
    	TYPE Coordinates IS RECORD (
		longitude		number,
		latitude		number,
		validCoordinates	number (1,0)
	);  	
	lCoordinates		Coordinates;	-- Coordinates for shipping address


	procedure get_order_allocation(pOrderNumber varchar2);
	
	procedure allocate_order(pOrderNumber varchar2);
	procedure allocate_item(pSkuId varchar2, pOrderNumber varchar2);
	
	procedure split_allocate_items;
	procedure split_allocate_item(pOrderNumber varchar2, pSkuId varchar2, pRequestedQty number);
	
	procedure allocate_item_to_store(pStoreId varchar2, pSkuId varchar2, pAllocQty number);
	procedure update_allocations(pSkuId varchar2,pOrderNumber varchar2);
	procedure force_allocate_items(pOrderNumber varchar2);
	procedure add_order_items (pOrderNumber varchar2);
	procedure populate_eligible_stores (pSkuId varchar2, pCommerceItemId varchar2,pOrderNumber varchar2,pQuantity number);
	procedure mark_as_force_allocate (pSkuId varchar2, pSplitItem number);	
	procedure get_inventory_for_order_skus (pOrderNumber varchar2);
	procedure populate_store_bandwidth;
	
	procedure printDebugOutput (pMessage varchar2);
	procedure printErrorOutput (pMessage varchar2);

	function is_split_order return number;
	function is_order_eligible_to_split (pOrderNumber varchar2) return number;
	function is_allocation_complete return number;
	function is_pending_split_allocation return number;	
end allocation;