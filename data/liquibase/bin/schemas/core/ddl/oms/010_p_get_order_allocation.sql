create or replace procedure p_get_order_allocation 	   (pOrderNumber in varchar2) 
as

	-- Program Constants
	TRUE								number (1,0) := 1;						-- True 
	FALSE 								number (1,0) := 0;						-- False
	DEBUGGING_ENABLED					number (1,0) := TRUE;					-- Set to FALSE to disable debugging
	ITEM_ALLOCATION_COMPLETE	number(1,0) := FALSE;
	log_order_number					varchar2(40) := 'N/A';

	lPostalCode varchar2(10);
        lSplitOrder		number(1,0);
        lAllocComplete		number(1,0);
        lSplitCount		number(1,0);
        
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
		commerceItemId		varchar2(40)
	);
	lOrderSKU OrderSKU;

	-- Latitude/Longitude Coordinates
    	TYPE Coordinates IS RECORD (
		longitude		number,
		latitude		number,
		validCoordinates	number (1,0)
	);  	
	lCoordinates		Coordinates;	-- Coordinates for shipping address

	-- Get a list of the items for a given order.  We only pull items
	-- with a line status of INITIAL.
	cursor OrderItemsCursor is 
	select  rownum,
			ORD.order_id,
			ITEM.commerce_item_id,
			ITEM.catalog_ref_id,
			ITEM.quantity
	from    dcspp_order   ORD,
		dcspp_item    ITEM
	where   ITEM.order_ref = ORD.order_id and
		ORD.order_id  = pOrderNumber and
		ITEM.state in ('INITIAL', 'BACK_ORDERED', 'PENDING_ALLOCATION');
		
	-- Get a list of the items for a given order, but group them by SKUs.  We only pull items
	-- with a line status of INITIAL.
	cursor OrderSKUsCursor is 
	select order_id,
		sku_id,
		quantity,
		commerce_item_id
	from	order_item
	where	fulfillment_store is null
		and (is_force_allocate=0 or is_force_allocate is null);


-- ******************************************************************
--                       printErrorOutput
--
--   Prints error messages.
-- ******************************************************************	
procedure printErrorOutput (pMessage varchar2) 
   as
   PRAGMA AUTONOMOUS_TRANSACTION;
begin   
   dbms_output.put_line (pMessage); 
	insert into mff_allocation_log
		(order_number,
		status,
		log_ts,
		log_message)
	values 
		(log_order_number,
		'ERROR',
		systimestamp,
		pMessage);
	commit;	
end;

-- ******************************************************************
--                       printDebugOutput
--
--   Prints debugging output if DEBUGGING_ENABLED is true.
-- ******************************************************************	
procedure printDebugOutput (pMessage varchar2) 
   as
   PRAGMA AUTONOMOUS_TRANSACTION;
begin   
   if DEBUGGING_ENABLED = TRUE then
		--dbms_output.put_line (pMessage); 
		insert into mff_allocation_log
			(order_number,
			status,
			log_ts,
			log_message)
		values 
			(log_order_number,
			'DEBUG',
			systimestamp,
			pMessage);
		commit;	
   else
   	dbms_output.put_line('Log Message ---- ' || pMessage);
   end if;
end; 

-- ******************************************************************
--                       isNumber
--   Determine if the value is a number.
--
-- ******************************************************************	
function isNumber(pValue in varchar2)
	return number is
	lNumber number;
begin

	lNumber := to_number(pValue);
	return TRUE;

	exception
		when VALUE_ERROR then return FALSE;
end;

-- ******************************************************************
--                       get_ship_postal_code
--   Get the postal code for the shipping address of this order.
--
-- ******************************************************************	
function get_ship_postal_code (pOrderNumber varchar2) 
	return varchar2
		is
	
begin	
	printDebugOutput ('Begin get_ship_postal_code ' || pOrderNumber); 
	
	-- Get a list of the items for a given order
	select  distinct (substr(postal_code,1,5))
	into    lPostalCode
	from    dcspp_ship_group SG,
			dcspp_ship_addr ADDR
	where   SG.shipping_group_id = ADDR.shipping_group_id and
			SG.order_ref = pOrderNumber;		
	printDebugOutput ('End get_ship_postal_code ' || pOrderNumber); 
	dbms_output.put_line('Postal code in the order is ' || lPostalCode);
	return 	lPostalCode;		
/*** TODO	
    exception
	when no_data_found then
		printErrorOutput ('Could not find ship postal code - using default of ' || DEFAULT_POSTAL_CODE); 
		return DEFAULT_POSTAL_CODE;
***/		
end;

--get_inventory_for_order_skus

-- ******************************************************************
--                     get_inventory_for_order_skus
-- 
--   Get store inventory for all SKUs in the order.
-- 	1. Consider only SKUs that have not been force allocated to a specific store (Gift card is an example)
--      2. SKUs that not been marked force_allocate for exceeding store rejection threshold
--
-- ******************************************************************	
procedure get_inventory_for_order_skus (pOrderNumber varchar2) 
   as
--   lUPCInventory UPCInventory;
   --lPostalCode 	varchar2(10);
   -- lSkuCount 	number(5,0);
   -- lRowCount 	number(5,0);
begin
	-- get inventory for items not allocated
	insert into sku_inventory(sku_id,store_id,available_qty,requested_qty)
	select si.catalog_ref_id,si.store_id,(si.stock_level - (si.allocated+si.shipped)) available_qty, 
		oi.quantity requested_qty 
	from atg_core.mff_store_inventory si, order_item oi
	where si.catalog_ref_id in (select sku_id from order_item where fulfillment_store is null and is_force_allocate=0)
		and oi.sku_id=si.catalog_ref_id;
	
	dbms_output.put_line('get_sku_inventory - Added ' || sql%rowcount || ' rows to sku_inventory table');
end;


-- ******************************************************************
--                     get_sku_inventory
--   Get the list of stores in which this SKU is available and add 
--   them into the temporary table.
--
-- ******************************************************************	
procedure get_sku_inventory (pOrderSKU OrderSKU) 
   as
--   lUPCInventory UPCInventory;
   --lPostalCode 	varchar2(10);
   lSkuCount 	number(5,0);
   lRowCount 	number(5,0);
begin
	dbms_output.put_line('Begin get_sku_inventory for order ' || pOrderSKU.orderId || ' and SKU ' || pOrderSKU.skuId);
/*	
	printDebugOutput ('Begin get_sku_inventory ' || pOrderSKU.orderId);
	dbms_output.put_line('Begin get_sku_inventory for order ' || pOrderSKU.orderId || ' and SKU ' || pOrderSKU.skuId);

	-- Get the ship postal code
	lPostalCode := get_ship_postal_code (pOrderSKU.orderId);
	dbms_output.put_line('Postal code in the order is ' || lPostalCode);
	
	select count(*) into lSkuCount from sku_inventory where sku_id = pOrderSKU.skuId;
	dbms_output.put_line('Found ' || lSkuCount || ' stores in sku_inventory.');

	if lSkuCount <= 0 then
		dbms_output.put_line('No record found. Populating stores with inventory in sku_inventory.');
		
		p_get_inventory_for_sku (pOrderSKU.skuId, pOrderSKU.quantity, lPostalCode);
		
		printDebugOutput ('Added SKU inventory records for SKU : ' || pOrderItem.sku_id); 	
		dbms_output.put_line('Added SKU inventory records for SKU : ' || pOrderItem.sku_id);
	else 
		printDebugOutput ('Inventory already exists in sku_inventory for SKU : ' || pOrderItem.sku_id); 	
		dbms_output.put_line('Inventory already exists in sku_inventory for SKU : ' || pOrderItem.sku_id);
		dbms_output.put_line('Increment quantity requested ');
		
		update sku_inventory set requested_qty = (requested_qty + pOrderItem.quantity)
		where sku_id = pOrderItem.sku_id;
		
		dbms_output.put_line('Quantity incremented ');
	end if;
	
*/	
end;

-- ******************************************************************
--                       get_shipTo_coordinates
--   Get the longitude/latitude for the zip code where the item is 
--	 being sent.
--
-- ******************************************************************	
function get_shipTo_coordinates (pPostalCode varchar2)
	return Coordinates is
	lCoordinates Coordinates;
begin	
	printDebugOutput ('Begin get_shipTo_coordinates for postal code: ' || pPostalCode); 	
	lCoordinates.latitude 			:= 0;
	lCoordinates.longitude 			:= 0;
	lCoordinates.validCoordinates 	:= FALSE;
	
	-- Check if this value is a number
	if isNumber (pPostalCode) = FALSE then
		printErrorOutput ('Non Numeric postal code - assume Canadian address' || pPostalCode || ' - using default'); 
		lCoordinates.validCoordinates 	:= FALSE;
		return lCoordinates;
	end if;
	
	-- Lookup the postal code to get Latitude/Longitude
	select  latitude, 
			longitude
	into 	lCoordinates.latitude,
			lCoordinates.longitude
	from    atg_core.zipcode_usa
	where 	zipcode = to_number(pPostalCode) and latitude!=0 and longitude!=0 and
			rownum < 2;
	printDebugOutput ('Coordinates Found for postal code ' || pPostalCode); 
	printDebugOutput ('End get_shipTo_coordinates for postal code: ' || pPostalCode); 	
	lCoordinates.validCoordinates 	:= TRUE;

	dbms_output.put_line('Coordinates Found for postal code ' || pPostalCode || ' -> Latitude ' || lCoordinates.latitude || ' Longitude ' || lCoordinates.longitude);
	-- dbms_output.put_line('Longitude ' || lCoordinates.longitude);
	
	return 	lCoordinates;
    exception
	when no_data_found then
		printErrorOutput ('No data found for postal code' || pPostalCode || ' - using default'); 
		lCoordinates.validCoordinates 	:= FALSE;
		return lCoordinates;
end;

procedure mark_as_force_allocate (pSkuId varchar2)
as
v_sql varchar2(2000);
begin
	v_sql := 'update order_item set is_force_allocate=1 where sku_id = ''' || pSkuId || '''';
	dbms_output.put_line('mark_as_force_allocate - v_sql ' || v_sql);
	execute immediate v_sql;
	-- commit;
end;

-- ******************************************************************
--                       populate_eligible_stores
--   Populates eligible_stores table with stores that carry inventory for items in the order
--   When a skuId is passed, we will populate stores that carry inventory for that sku
--   When a commerceItem is passed, we will look at prev allocations to avoid allocating to stores
--   that have already rejected fulfilling that item
--
-- ******************************************************************

procedure populate_eligible_stores (pSkuId varchar2, pCommerceItemId varchar2)
as
      lEligibleStoreCount 	number(5,0);
      v_sql varchar2(2000);
      v_where_clause varchar2(1000);
begin

  	v_where_clause := 'where inv.available_qty >= inv.requested_qty ';
  	
  	if pSkuId IS NOT NULL then
  		-- lookup inventory only for the specific item requested
  		-- We're in split alloc mode where each item is being allocated
  		v_where_clause := v_where_clause || ' and inv.sku_id = ''' || pSkuId || '''' ||
  			'and store_id not in ( ' ||
  				'select store from mff_itemprev_allocations where commerce_item_id=''' ||
  					pCommerceItemId || ''')';
  	else
	  	-- Do not consider stores to which item was previously allocated to.
	  	-- Those stores may have rejected the allocation
  		v_where_clause := v_where_clause || 'and store_id not in ( ' ||
  					'select alloc.store from mff_itemprev_allocations alloc, order_item oi ' ||
					'where alloc.commerce_item_id=oi.commerce_item_id)';
  	end if;
    	
	-- populate results table
	v_sql := 'merge into eligible_stores dest ' ||
		'using (select inv.store_id,count(inv.sku_id) from sku_inventory inv ' ||
			    v_where_clause ||
			    ' group by inv.store_id) src ' ||
	        'on (dest.store_id=src.store_id) ' ||
 			' when matched then ' ||
                             'update set dest.eligible=''Y'', dest.filtered_out_by=null ' ||
 			' when not matched then ' ||
    	                     ' insert (store_id) values(src.store_id)';

    	dbms_output.put_line('v_sql ' || v_sql);
    	execute immediate v_sql;
    	dbms_output.put_line('Merged ' || sql%rowcount || ' row(s)');
    	lEligibleStoreCount := sql%rowcount;
    	
    	-- select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
    	if pSkuId is null then
    		dbms_output.put_line('A total of ' || lEligibleStoreCount || ' store(s) carry inventory for skus in the order');
    		-- TODO: if order has only 1 item & we do not have enough inventory for that item
    		-- we need to put that in forceAllocate as well.
    		-- ITEM_ALLOCATION_COMPLETE check needs to be added here as well
    	else
    		dbms_output.put_line('A total of ' || lEligibleStoreCount || ' store(s) carry inventory for sku ' || pSkuId || ' in order ' || pOrderNumber);
    		
    		-- If we do not have any eligible stores with sufficient inventory for skuId passed in
    		-- we mark the item for force_allocation
    		
    		if lEligibleStoreCount = 0 Then
    			ITEM_ALLOCATION_COMPLETE := TRUE;
    			dbms_output.put_line('Allocation complete for item ' || pSkuId || '. Marking for force_allocate. Going to next SKU');
    			mark_as_force_allocate(pSkuId);
    		end if;
    	end if;
end;

-- ******************************************************************
--                       is_allocation_complete
--   Checks if there are items in the order to be allocated
-- 
-- ******************************************************************

function is_allocation_complete 
	  return number is
      lPendingAllocCount 	number(5,0);
	  
begin	  

	select count(*) into lPendingAllocCount 
	from order_item
	where fulfillment_store is null 
		and is_force_allocate=0;
	
	-- If there items pending allocation
	if lPendingAllocCount >= 1 then
		dbms_output.put_line('There are still ' || lPendingAllocCount || ' item(s) to be allocated.');
		return FALSE;
	else 
		return TRUE;
	end if;
end;


-- ******************************************************************
--                       is_split_order
--   Checks whether the order needs to be split or if there are stores
--   that can fulfill all items
-- ******************************************************************

function is_split_order 
	  return number is
      lEligibleStoreCount 	number(5,0);
	  
begin	  

	select count(*) into lEligibleStoreCount from (
		select inv.store_id,count(inv.sku_id) from sku_inventory inv,order_item item
		where inv.sku_id=item.sku_id
			and inv.available_qty >= item.quantity
		group by inv.store_id
		having count(inv.sku_id)=(select count(*) from order_item where fulfillment_store is null and is_force_allocate=0)
	);
	
	-- Is there sufficient inventory to fulfill this order 
	if lEligibleStoreCount >= 1 then
		return FALSE;
	else 
		return TRUE;
	end if;
end;


-- ******************************************************************
--                       allocate_all_to_store
--   TODO: Revisit this method
-- ******************************************************************

procedure allocate_all_to_store(pSkuId varchar2)
as
	v_sql varchar2(2000);
	v_where_clause varchar2(1000);
	lEligibleStoreCount 	number(5,0); 

begin
	-- Rule 1 - Do we have stores that can fulfill the entire order
	-- This would be a needless check
	
	if pSkuId is not null then
		v_where_clause := ' and sku_id = ''' || pSkuId || '''';
	end if;

	-- TODO Revisit this update statement 
	-- in subquery fails all rows will be updated
	-- needs a where exists clause
	v_sql := 'update eligible_stores ' ||
		' set eligible=''N'', filtered_out_by=''Rule: allocate_all_to_store'' ' ||
		' where store_id not in ( ' ||
			' select store_id from ( ' ||
				' select inv.store_id,count(inv.sku_id) from sku_inventory inv,order_item item ' ||
				' where inv.sku_id=item.sku_id ' ||
					' and inv.available_qty >= item.quantity ' ||
				' group by inv.store_id ' ||
				' having count(inv.sku_id)=(select count(*) from order_item where fulfillment_store is null' ||
				v_where_clause ||
				')' ||
			')' ||
		')';

	-- dbms_output.put_line('allocate_all_to_store for sku ' || pSkuId);
	dbms_output.put_line('Executing SQL ' || v_sql);
	execute immediate v_sql;
	
	select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
	dbms_output.put_line('After rule allocate_all_to_store, ' || lEligibleStoreCount || ' store(s) remain');
	dbms_output.put_line('---------------------------------------------------------------------------------');

end;


procedure allocate_by_queue_ratio(pSkuId varchar2)
as
	lEligibleStoreCount 	number(5,0); 
begin
	-- Rule 2 - Use stores with higher queue ratio

	-- calculate queue ratios

	insert into store_queue_ratio(store_id,current_queue_size,max_queue_size,ratio,ranking)
	select store_id, current_queue_size,max_queue_size,ratio, row_number() over(order by ratio desc) from (
		select si.store_id, sum(si.allocated) current_queue_size,ml.max_queue_size,
				nvl ( (ml.max_queue_size/nullif(sum(si.allocated),0)),ml.max_queue_size) as ratio 
			from atg_cata.dcs_location dl,atg_cata.mff_location ml,atg_core.mff_store_inventory si
			where dl.location_id=ml.location_id
				and ml.location_id=si.store_id
				and si.store_id in (select store_id from eligible_stores where eligible='Y' or eligible is null)
			group by si.store_id,ml.max_queue_size
	);
	-- commit;

	update eligible_stores
		set eligible='N', filtered_out_by='Rule: allocate_by_queue_ratio'
		where store_id not in (
			select store_id from store_queue_ratio where ranking <= 3
		) 
		and (eligible is null or eligible='Y');
	-- commit;
	select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
	dbms_output.put_line('After rule allocate_by_queue_ratio, ' || lEligibleStoreCount || ' store(s) remain');
	dbms_output.put_line('---------------------------------------------------------------------------------');

end;

procedure allocate_by_distance(pSkuId varchar2)
as
	lPostalCode 		varchar2(10);
	lAllocatedStore		varchar2(40);
	lEligibleStoreCount 	number(5,0);
begin
	-- Rule 3 - Distance
	dbms_output.put_line('Getting postal code for order ' || pOrderNumber);
	lPostalCode := get_ship_postal_code (pOrderNumber);
	-- dbms_output.put_line('Postal code in the order is ' || lPostalCode);

	dbms_output.put_line('Getting coordinates for postal code ' || lPostalCode);
	lCoordinates := get_shipTo_coordinates (lPostalCode);
	-- dbms_output.put_line('Latitude ' || lCoordinates.latitude);
	-- dbms_output.put_line('Longitude ' || lCoordinates.longitude);

	update eligible_stores
		set eligible='N', filtered_out_by='Rule: allocate_by_distance'
		where store_id not in (
			select store_id from (
				select st.store_id,f_compute_distance(
				        lCoordinates.latitude,lCoordinates.longitude,
				        loc.latitude,loc.longitude) as dist
				from eligible_stores st, atg_cata.dcs_location loc
				where st.eligible='Y' or eligible is null
					and loc.location_id=st.store_id
				order by dist asc
			)
			where rownum<2
		) 
		and (eligible is null or eligible='Y');

	select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
	dbms_output.put_line('After rule allocate_by_distance, ' || lEligibleStoreCount || ' stores remain');

	select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null;
	dbms_output.put_line('Selected store is ' || lAllocatedStore || ' after rule 3 execution for sku ' || pSkuId);
	dbms_output.put_line('---------------------------------------------------------------------------------');

end;

/*
procedure allocate_by_max_capacity
as
begin
	-- Rule 4 - Max Queue Size not reached
	
	select store_id into lAllocatedStore from store_queue_ratio 
		where store_id in (
			select store_id from eligible_stores 
			where eligible='Y' or eligible is null
		)
		and current_queue_size < max_queue_size;

	dbms_output.put_line('Store ' || lAllocatedStore || ' passes max rule check');
	
--	dbms_output.put_line('All items in order_item assigned to ' || lAllocatedStore);

end;
*/
-- ******************************************************************
--                     		allocate_items
--   Allocate the items in the order to either a store or a warehouse.
--
-- ******************************************************************	
procedure allocate_items 
   as
      lEligibleStoreCount 	number(5,0);
      lSplitOrder		number(1,0);
      lAllocatedStore		varchar2(40);
      lPostalCode 		varchar2(10);
begin
	printDebugOutput ('Begin allocate_items '); 	
	
	populate_eligible_stores(null,null);
    	
	lSplitOrder := is_split_order;
	dbms_output.put_line('Split Order ' || lSplitOrder);
    	
    	
    	if lSplitOrder = FALSE Then
		update order_item set fulfillment_store=lAllocatedStore;
	end if;	
	printDebugOutput ('End allocate_items '); 		
end;


-- ******************************************************************
--                     		add_order_item
--   Add the order item into a temporary table for downstream 
--   analysis.
--
-- ******************************************************************	
procedure add_order_item (pOrderItem OrderItem) 
   as
begin
	printDebugOutput ('    Begin add_order_item ' || pOrderItem.orderId); 		
	dbms_output.put_line('    add_order_item: Add order -> ' ||pOrderItem.orderId || ' to order_item table');
	
	insert into order_item 	
		(order_item_id,
		order_id,
		commerce_item_id,
		sku_id,
		quantity)
	values 
		(pOrderItem.orderItemId,
		pOrderItem.orderId,
		pOrderItem.commerceItemId,
		pOrderItem.sku_id,
		pOrderItem.quantity);
		
	printDebugOutput ('End add_order_item ' || pOrderItem.orderId);
	dbms_output.put_line('    Added order -> ' ||pOrderItem.orderId || ' to order_item table');
end;

-- ******************************************************************
--                     		check_order_split_count
--  Returns count of distinct stores, items in the order have been allocated to
--  Does not count force allocated items or gift cards...
-- ******************************************************************	
procedure check_order_split_count (pOrderNumber varchar2) 
   as
   v_sql varchar2(2000);
	begin
	
		-- get distinct stores marked for allocation. Do not consider
		-- items that have been force_allocated or gift cards
		select count(distinct fulfillment_store) into lSplitCount from order_item
		where is_force_allocate = 0 and fulfillment_store is not null and is_gift_card=0;
		
		dbms_output.put_line('Item(s) in order ' || pOrderNumber || ' are assigned to ' || lSplitCount || ' distinct stores.');
		if lSplitCount > 3 then
			dbms_output.put_line('Order exceeds split threshold. Marking all items for force allocate.');
			v_sql := 'update order_item ' ||
				'set is_force_allocate=1, ' ||
				'fulfillment_store=null ' ||
				'where is_force_allocate=0 and fulfillment_store is not null  and is_gift_card=0';
	
			dbms_output.put_line('Force allocate all items in order. Executing SQL ' || v_sql);
			execute immediate v_sql;
			dbms_output.put_line(sql%rowcount || ' record(s) force allocated.');				
		else 
			dbms_output.put_line('Order within split threshold. Retain current allocation results.');
		end if;
	end;
	
-- ******************************************************************
--                     		add_order_items
--   Add items from the order that are pending allocation
--   There may be items in the order that have already been allocated. 
--   We do not want to bring those items in.
--
-- ******************************************************************	
procedure add_order_items (pOrderNumber varchar2) 
   as
	begin
		printDebugOutput ('    Begin add_order_items ' || pOrderNumber); 		

		insert into order_item(order_item_id,order_id,commerce_item_id,sku_id,quantity)
		select  rownum,
			ORD.order_id,
			ITEM.commerce_item_id,
			ITEM.catalog_ref_id,
			ITEM.quantity
		from    dcspp_order   ORD,
			dcspp_item    ITEM
		where   ITEM.order_ref = ORD.order_id and
			ORD.order_id  = pOrderNumber and
			ITEM.state in ('INITIAL', 'BACK_ORDERED', 'PENDING_ALLOCATION');
		-- commit;
		
		-- printDebugOutput ('End add_order_items ' || pOrderNumber);
		dbms_output.put_line('    Added ' || to_char(SQL%ROWCOUNT) || ' row(s) to order_item table.');
		if sql%rowcount = 0 then
			ITEM_ALLOCATION_COMPLETE := TRUE;
			dbms_output.put_line('    No items to allocate. Exiting...procedure ');
		end if;
	end;

-- ******************************************************************
--                       force_allocate_items
--
-- Handles a couple of special scenarios before allocation algo
-- is executed.
--
-- Certain SKUs are fulfilled by specific stores. We do not have to
-- run allocation logic for those SKUs. This mapping of stores-SKUs
-- is maintained in force_allocation table. Currently, only gift cards
-- are mapped here. But this can be extended to other items if desired.
-- For most other items, this implicit force_allocation is handled
-- by MFF inventory data. Stock level is sent only for the store that
-- can fulfill those items.
--
-- Stores can reject allocation. In these cases, the item comes back for
-- reallocation (its STATE is still PENDING_ALLOCATION or INITIAL). It
-- looks no different from INITIAL allocation. We make only 3 attempts to 
-- re-allocate the item. If stores continue to reject, we mark the
-- is_force_allocate flag to true so an agent may manually allocate
-- the item to the store.
-- ******************************************************************	

procedure force_allocate_items(pOrderNumber varchar2)
as
v_sql varchar2(2000);
begin

	-- Force allocate specific items 
	v_sql := 'update order_item dest ' ||
		'set (dest.fulfillment_store,dest.is_gift_card) = ( ' ||
			'select src.store_id,src.is_gift_card from force_allocation src ' ||
			'where src.sku_id=dest.sku_id ' ||
		') ' ||
		'where exists ( ' ||
			'select 1 from force_allocation src ' ||
				'where src.sku_id=dest.sku_id)';
	
	dbms_output.put_line('force_allocate_items Executing SQL ' || v_sql);
	execute immediate v_sql;
	dbms_output.put_line(sql%rowcount || ' record(s) force allocated.');

	-- mark as force_allocate items that have be allocated at least 3 times
	-- TODO: Make this threshold configurable

	v_sql := 'update order_item oi set oi.is_force_allocate = 1 ' ||
			'where exists( ' ||
  				'select mia.commerce_item_id, count(mia.store) alloc_count ' ||
  				'from mff_itemprev_allocations mia where mia.commerce_item_id in ( ' ||
  					'select commerce_item_id from order_item ' ||
  						'where fulfillment_store is null) ' ||
  							'and mia.commerce_item_id = oi.commerce_item_id ' ||
  					'group by commerce_item_id ' ||
  					'having count(store) >= 3)';

	dbms_output.put_line('force_allocate_items Exceeds previous allocation threshold ' || v_sql);
	execute immediate v_sql;
	dbms_output.put_line(sql%rowcount || ' record(s) exceeded store rejection threshold.');

end;


procedure update_allocations(pSkuId varchar2)
as
	lAllocatedStore varchar2(40);
begin

	select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null;
	
	if pSkuId is null then
		dbms_output.put_line('Allocating store ' || lAllocatedStore || ' to all items in order ' || pOrderNumber);
		update order_item set fulfillment_store=lAllocatedStore where order_id=pOrderNumber and fulfillment_store is null;
	else
		dbms_output.put_line('Allocating store ' || lAllocatedStore || ' to items with sku ' || pSkuId);
		update order_item set fulfillment_store=lAllocatedStore where order_id=pOrderNumber and sku_id=pSkuId  and fulfillment_store is null;
	end if;
end;

-- ******************************************************************
--
--                       Main Processing
--
-- ******************************************************************	
begin
	dbms_output.enable;
  	dbms_output.put_line ('  Begin find optimal store mainline for order ' || pOrderNumber); 	
  	log_order_number 	:= pOrderNumber;

	-- Clear out tmp tables used for intermediate processing
	dbms_output.put_line ('    Deleting temp tables'); 
	delete from order_item;
	delete from sku_inventory;
	delete from eligible_stores;
	delete from store_queue_ratio;
	delete from mff_allocation_log;
	
	-- Reset allocation_complete flag
	ITEM_ALLOCATION_COMPLETE := FALSE;

  	
  	-- Add items to be allocated from the order to tmp table
  	dbms_output.put_line ('    Adding order skus to order_item table'); 	
  	add_order_items(pOrderNumber);
  	
  	if ITEM_ALLOCATION_COMPLETE = TRUE then
  		return;
  	end if;
  	
  	-- See if there are items to be force allocated
  	-- Gift card... items exceeding store rejection threshold
  	-- TODO: Order being split too many times because of store rejections
  	dbms_output.put_line ('    Check for force_allocate_items'); 	
  	force_allocate_items(pOrderNumber);
  	
  	-- check if there are still items to be allocated.
  	-- if order contains only force_allocation_items
  	-- then we can exit the procedure.. say there is just a gift card in the order
  	
  	dbms_output.put_line ('    Check is_allocation_complete');
  	lAllocComplete := is_allocation_complete;
  	if lAllocComplete = TRUE then
  		dbms_output.put_line ('    Alloc complete... exiting..');
  		return;
  	end if;
  	
  	-- get inventory levels for all skus in the order
  	dbms_output.put_line ('    Getting inventory levels for order ' || pOrderNumber);
  	get_inventory_for_order_skus(pOrderNumber);
  	
	-- get list of stores that have sufficient inventory
	populate_eligible_stores(null,null);
    	
	-- Find out if the order needs to be split-allocated
	lSplitOrder := is_split_order;
	dbms_output.put_line('Split Order = ' || lSplitOrder);
    	
    	
    	-- There are stores that can fulfill the entire order
    	-- Find the best store by applying filtering rules in order
    	if lSplitOrder = FALSE Then
    		
		dbms_output.put_line ('********* STARTING ALLOCATION ALGORITHM FOR ORDER ***********');
		-- Filter out stores that do not have sufficient quantity
		dbms_output.put_line('********* -> Calling allocate_all_to_store.');
		allocate_all_to_store(null);

		-- Filter out stores based on queue_ratio
		dbms_output.put_line('********* -> Calling allocate_by_queue_ratio.');
		allocate_by_queue_ratio(null);

		-- Filter out stores based on distance
		dbms_output.put_line('********* -> Calling allocate_by_distance.');
		allocate_by_distance(null);

		-- Filter out stores based on max capacity
		-- allocate_by_max_capacity(null);
		
		dbms_output.put_line('********* -> Calling update_allocations.');
		update_allocations(null);
    	else
    		-- The order needs to be split-allocated
    		-- Will attempt to allocate best-fit store for each sku in the order
    		dbms_output.put_line('Allocate each sku in order by calling allocate_item. SplitOrder is ' || lSplitOrder);
    		

		open OrderSKUsCursor;
		loop
			-- Reset allocation flag
			ITEM_ALLOCATION_COMPLETE := FALSE;
			
			-- clear out any stale data from eligible store list
			dbms_output.put_line('Deleting from eligible stores');
			delete from eligible_stores;
			
			
			dbms_output.put_line ('********* STARTING ALLOCATION ALGORITHM EACH SKU IN THE ORDER.');
			
			fetch 	OrderSKUsCursor 
			into 	lOrderSKU.orderId,
				lOrderSKU.skuId,
				lOrderSKU.quantity,
				lOrderSKU.commerceItemId;
			exit when OrderSKUsCursor%notfound;
			-- dbms_output.put_line('    Fetched order number is ---> '|| lOrderSKU.orderId);    
			-- dbms_output.put_line('    Fetched sku is ---> '|| lOrderSKU.skuId);

			dbms_output.put_line('********** Being Executing Alloc Algorithm for sku ' || lOrderSKU.skuId);			
			
			-- dbms_output.put_line('ITEM_ALLOCATION_COMPLETE is ' || ITEM_ALLOCATION_COMPLETE || ' before populating store');
			
			-- Get stores that have sufficient inventory
			-- to fulfill requested qty of sku

			populate_eligible_stores(lOrderSKU.skuId,lOrderSKU.commerceItemId);

			dbms_output.put_line('ITEM_ALLOCATION_COMPLETE is ' || ITEM_ALLOCATION_COMPLETE || ' after populating store');
			if ITEM_ALLOCATION_COMPLETE = TRUE then
				dbms_output.put_line('Allocation complete for item ' || lOrderSKU.skuId || '. Continuing...');
				continue;
			end if;
			

			dbms_output.put_line('********* -> Calling allocate_all_to_store');
			-- Filter out stores that do not have sufficient quantity
			allocate_all_to_store(lOrderSKU.skuId);
			
			-- Filter out stores based on queue_ratio
			dbms_output.put_line('********* -> Calling allocate_by_queue_ratio');
			allocate_by_queue_ratio(lOrderSKU.skuId);
			
			-- Filter out stores based on distance
			dbms_output.put_line('********* -> Calling allocate_by_distance');
			allocate_by_distance(lOrderSKU.skuId);
			
			-- Filter out stores based on max capacity
			--allocate_by_max_capacity(lOrderSKU.skuId);
			
			dbms_output.put_line('********* -> Calling update_allocations');
			update_allocations(lOrderSKU.skuId);
			
			dbms_output.put_line('********** End Executing Alloc Algorithm for sku ' || lOrderSKU.skuId);			

		end loop;
		close OrderSKUsCursor;
    	end if;
  	
	check_order_split_count(pOrderNumber);
end;