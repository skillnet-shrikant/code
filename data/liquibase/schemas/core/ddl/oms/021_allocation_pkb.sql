create or replace
package body allocation as

	TRUE								number (1,0) := 1;						-- True 
	FALSE 								number (1,0) := 0;						-- False
	DEBUGGING_ENABLED					number (1,0) := TRUE;					-- Set to FALSE to disable debugging
	ITEM_ALLOCATION_COMPLETE	number(1,0) := FALSE;
	log_order_number	varchar2(40) := 'N/A';
	invalid_zipcode   EXCEPTION;


-- ******************************************************************
--                       printErrorOutput
--
--   Prints error messages.
-- ******************************************************************	
procedure printErrorOutput (pMessage varchar2) 
   as
   PRAGMA AUTONOMOUS_TRANSACTION;
begin   
   printDebugOutput (pMessage); 
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
		--printDebugOutput (pMessage); 
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
   	printDebugOutput('Log Message ---- ' || pMessage);
   end if;
end; 


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
	insert into sku_inventory(sku_id,
				store_id,
				available_qty,
				requested_qty)
	select si.catalog_ref_id,
		si.store_id,
		(si.stock_level - (sit.allocated+sit.shipped)) available_qty,
		oi.quantity requested_qty
	from atg_core.ff_store_inventory si, order_item oi,
		store_bandwidth sb, atg_cata.mff_location ml, atg_core.ff_store_inv_transaction sit
	where si.catalog_ref_id in (select sku_id from order_item
					    where fulfillment_store is null and 
						is_force_allocate=0 and
					is_split_allocate=0)
  		and oi.sku_id=si.catalog_ref_id
  		and si.inventory_id=sit.inventory_id
  		and ml.location_id=si.store_id
  		and sb.store_id=si.store_id
  		and ml.is_pps=1
  		and si.is_damaged=0
  		and (sb.queue_count < ml.max_queue_size and sb.total_orders < total_orders_per_day);

	printDebugOutput('get_sku_inventory - Added ' || sql%rowcount || ' rows to sku_inventory table');
end;

procedure force_allocate_order (pOrderId varchar2)
as
v_sql varchar2(2000);
begin
	v_sql := 'update order_item set is_force_allocate=1 where order_id = ''' || pOrderId || '''';
	printDebugOutput('Order needs review. Unable to allocate order - v_sql ' || v_sql);
	execute immediate v_sql;
	-- commit;
end;

procedure mark_as_force_allocate (pSkuId varchar2, pSplitItem number)
as
v_sql varchar2(2000);
begin
	if(pSplitItem = TRUE) then
		v_sql := 'update order_item set is_force_allocate=0,is_split_item= ' || pSplitItem || ' where sku_id = ''' || pSkuId || '''';
	else
		v_sql := 'update order_item set is_force_allocate=1,is_split_item= ' || pSplitItem || ' where sku_id = ''' || pSkuId || '''';
	end if;
	
	printDebugOutput('mark_as_force_allocate - v_sql ' || v_sql);
	execute immediate v_sql;
	-- commit;
end;


function is_split_item (pSkuId varchar2, pQuantity number)
return number is
v_sql varchar2(2000);
lEligibleStoreCount 	number(5,0);
begin

	v_sql := 'select count(*) from ( ' || 
			'select sum(available_qty) from sku_inventory ' ||
				'where sku_id = ''' || pSkuId || ''' ' ||
					'and store_id not in ( ' ||
						'select distinct store ' || 
						'from mff_itemprev_allocations ' ||
						'where commerce_item_id in ( ' ||
							'select commerce_item_id ' || 
							'from order_item ' || 
							'where sku_id=''' || pSkuId || '''))' ||
				'having sum(available_qty) >= ' || pQuantity || ')';
				
	printDebugOutput('is_split_item - v_sql ' || v_sql);
	execute immediate v_sql into lEligibleStoreCount;
	
	if(lEligibleStoreCount > 0) then
		return TRUE;
	else
		return FALSE;
	end if;
end;

-- ******************************************************************
--                       populate_eligible_stores
--   Populates eligible_stores table with stores that carry inventory for items in the order
--   When a skuId is passed, we will populate stores that carry inventory for that sku
--   When a commerceItem is passed, we will look at prev allocations to avoid allocating to stores
--   that have already rejected fulfilling that item
--
-- ******************************************************************

procedure populate_eligible_stores (pSkuId varchar2, pCommerceItemId varchar2,pOrderNumber varchar2, pQuantity number)
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

    	printDebugOutput('v_sql ' || v_sql);
    	execute immediate v_sql;
    	lEligibleStoreCount := sql%rowcount;
    	printDebugOutput('Merged ' || lEligibleStoreCount || ' row(s)');
    	
    	
    	-- select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
    	if pSkuId is null then
    		printDebugOutput('A total of ' || lEligibleStoreCount || ' store(s) carry inventory for skus in the order');
    		-- TODO: if order has only 1 item & we do not have enough inventory for that item
    		-- we need to put that in forceAllocate as well.
    		-- ITEM_ALLOCATION_COMPLETE check needs to be added here as well
    	else
    		printDebugOutput('A total of ' || lEligibleStoreCount || ' store(s) carry inventory for sku ' || pSkuId || ' in order ' || pOrderNumber);
    		
    		-- If we do not have any eligible stores with sufficient inventory for skuId passed in
    		-- we mark the item for force_allocation
    		
    		if lEligibleStoreCount = 0 Then
    			ITEM_ALLOCATION_COMPLETE := TRUE;
    			printDebugOutput('Allocation complete for item ' || pSkuId || '. Marking for force_allocate. Going to next SKU');
    			mark_as_force_allocate(pSkuId,is_split_item(pSkuId,pQuantity));
    		end if;
    	end if;
end;

-- ******************************************************************
--                       is_pending_split_allocation
--   Checks if there are items in the order 
--   that are split and have to be allocated
--   case: sku with qty > 1 being split into commerce items 
--   with qty 1 each
-- 
-- ******************************************************************

function is_pending_split_allocation 
	  return number is
      lPendingAllocCount 	number(5,0);
	  
begin	  

	select count(*) into lPendingAllocCount 
	from order_item
	where fulfillment_store is null 
		and is_force_allocate=0
		and is_split_allocate=1;
	
	-- If there items pending allocation
	if lPendingAllocCount >= 1 then
		printDebugOutput('There are still ' || lPendingAllocCount || ' item(s) to be SPLIT allocated.');
		return TRUE;
	else 
		return FALSE;
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
		printDebugOutput('There are still ' || lPendingAllocCount || ' item(s) to be allocated.');
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
			and store_id not in (
  				select alloc.store from mff_itemprev_allocations alloc, order_item oi 
					where alloc.commerce_item_id=oi.commerce_item_id)		
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
--                       split_allocate_items
--
-- Handles allocation logic of skus that are split into multiple commerce
-- items.
-- Eg: sku_id 0012345 with qty 5. No single store has sufficient inventory
-- to fulfull that sku. We need to spread this to multiple stores.
-- OMS split that request into 5 commerce items with sku_id 0012345 and 
-- qty 1 each. Each of those 5 items have to now be allocated.

-- ******************************************************************	


procedure split_allocate_items 
as
	-- Get a list of the items for a given order, 
	-- that have been split into multiple commerce items
	
	cursor OrderSplitItemsCursor is 
	select distinct sku_id, 
		order_id,
		sum(quantity) as requested_qty
	from order_item 
	where fulfillment_store is null and 
		is_force_allocate=0 and
		is_split_allocate=1
	group by order_id,sku_id;
	v_sql varchar2(2000);
	lAvailStock number(5,0);
begin
	-- TODO: Merge the overall inventory handling in sku_inventory table
	-- 1. Delete unused columns
	-- 2. Get inventory for all skus in the order.. split and non-split items
	
	-- At minimum, the calls below have to be done once prior to
	-- split_allocation start.
	
	delete from sku_inventory;
		
	open OrderSplitItemsCursor;
	loop
		fetch 	OrderSplitItemsCursor 
		into 	lOrderSKU.skuId,
			lOrderSKU.orderId,
			lOrderSKU.requestedQty;
		exit when OrderSplitItemsCursor%notfound;
		
		delete from sku_inventory;

		-- Get inventory levels for split_alloc skus
		v_sql:= 'insert into sku_inventory(sku_id, ' ||
				'store_id, ' ||
				'available_qty) ' ||
		'select si.catalog_ref_id, ' ||
			'si.store_id, ' ||
			'(si.stock_level - (sit.allocated+sit.shipped)) available_qty ' ||
		'from atg_core.ff_store_inventory si, atg_core.ff_store_inv_transaction sit, atg_cata.mff_location ml, ' ||
			'store_bandwidth sb ' ||
		'where si.catalog_ref_id =''' || lOrderSKU.skuId || '''' ||
			'and ml.location_id=si.store_id ' ||
			'and si.inventory_id=sit.inventory_id ' ||
			'and si.store_id=sb.store_id ' ||
			'and ml.is_pps=1 ' ||
			'and si.is_damaged=0 ' ||
			'and (sb.queue_count < ml.max_queue_size and sb.total_orders < total_orders_per_day) ' ||
			'and si.store_id not in ( ' ||
				'select distinct store ' || 
				'from mff_itemprev_allocations ' ||
				'where commerce_item_id in ( ' ||
					'select commerce_item_id ' || 
					'from order_item ' || 
					'where sku_id=''' || lOrderSKU.skuId || '''))';
		printDebugOutput('Getting inventory for sku ' || lOrderSKU.skuId || ' sql - ' || v_sql);
		execute immediate v_sql;
		
		-- check if there is enough inventory to allocate this item.
		-- if not, mark_as_force_allocate
		
		v_sql:= 'select sum(available_qty) ' ||
			'from sku_inventory ' || 
			'where sku_id=''' || lOrderSKU.skuId || '''';
		printDebugOutput('Checking available inventory for sku ' || lOrderSKU.skuId || ' sql - ' || v_sql);
		execute immediate v_sql	into lAvailStock;
		printDebugOutput('Available inventory for sku ' || lOrderSKU.skuId || ' is - ' || lAvailStock);
		
		if lAvailStock >= lOrderSKU.requestedQty then
			printDebugOutput('Sufficient inventory available. Calling split_allocate_item');
			-- TODO Rewrite
			split_allocate_item(lOrderSKU.orderId,lOrderSKU.skuId,lOrderSKU.requestedQty);
		else
			printDebugOutput('Not enough inventory for sku ' || lOrderSKU.skuId || 
				' requested ' || lOrderSKU.requestedQty ||
				' available ' || lAvailStock || '. call mark_as_force_allocate');
			mark_as_force_allocate (lOrderSKU.skuId,0);	
		end if;
	end loop;
	close OrderSplitItemsCursor;
end;

procedure allocate_item_to_store(pStoreId varchar2,
			pSkuId varchar2,
			pAllocQty number)
as
begin
	update sku_inventory
	set available_qty = (available_qty - pAllocQty)
	where sku_id=pSkuId and
		store_id=pStoreId;
		
	update order_item
	set fulfillment_store=pStoreId
	where sku_id=pSkuId and
		fulfillment_store is null
		and rownum <= pAllocQty;
	
end;

procedure update_allocations(pSkuId varchar2,pOrderNumber varchar2)
as
	lAllocatedStore varchar2(40);
begin

	select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null and rownum < 2;
	
	if pSkuId is null then
		printDebugOutput('Allocating store ' || lAllocatedStore || ' to all items in order ' || pOrderNumber);
		update order_item set fulfillment_store=lAllocatedStore where order_id=pOrderNumber and fulfillment_store is null;
	else
		printDebugOutput('Allocating store ' || lAllocatedStore || ' to items with sku ' || pSkuId);
		update order_item set fulfillment_store=lAllocatedStore where order_id=pOrderNumber and sku_id=pSkuId  and fulfillment_store is null;
	end if;
end;

-- ******************************************************************
--                     		populate_store_bandwidth
--   Populates metrics on all stores' bandwidth
--   Number of orders allocated but pending shipment
--   Total number of orders sent to the store today.
--   
--
-- ******************************************************************	

procedure populate_store_bandwidth
as
begin
	insert into store_bandwidth(store_id,queue_count)
	select ml.location_id, 
		count(alloc.order_id) queue_size
	from atg_cata.mff_location ml 
		left outer join mff_store_allocation alloc
	on (ml.location_id=alloc.store_id
		and alloc.state='PRE_SHIP'
		and to_char(alloc.allocation_date,'DD-MON-YY')<=to_date((sysdate))
		and ml.is_pps=1
	)
	group by ml.location_id;
	
	merge into store_bandwidth dest
	using (
		select ml.location_id store_id, 
			count(distinct order_id) total_orders
		from atg_cata.mff_location ml 
			left outer join mff_store_allocation alloc
		on (ml.location_id=alloc.store_id
			and alloc.state!='DECLINE'
			and to_char(allocation_date,'DD-MON-YY')=to_date((sysdate))
			and ml.is_pps=1)
		group by ml.location_id
	) src
	on(dest.store_id=src.store_id)
	when matched then
		update set total_orders=src.total_orders
	when not matched then
	insert (store_id,total_orders) values(src.store_id,src.total_orders);
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

		insert into order_item(order_item_id,order_id,commerce_item_id,sku_id,quantity,is_drop_ship)
		select  rownum,
			ORD.order_id,
			ITEM.commerce_item_id,
			ITEM.catalog_ref_id,
			ITEM.quantity,
			MFITEM.is_drop_ship
		from    dcspp_order   ORD,
			dcspp_item    ITEM,
			mff_item      MFITEM
		where   ITEM.order_ref = ORD.order_id and
			ITEM.commerce_item_id=MFITEM.commerce_item_id and
			ORD.order_id  = pOrderNumber and
			ITEM.state in ('INITIAL', 'BACK_ORDERED', 'PENDING_ALLOCATION');
		-- commit;
		
		-- printDebugOutput ('End add_order_items ' || pOrderNumber);
		-- printDebugOutput('    Added ' || to_char(SQL%ROWCOUNT) || ' row(s) to order_item table.');
		if sql%rowcount = 0 then
			ITEM_ALLOCATION_COMPLETE := TRUE;
			printDebugOutput('    No items to allocate. Exiting...procedure ');
		else
			-- update order_item table to mark items 
			-- that are to be split allocated. These are 
			-- requests for skus with qty > 1
			-- that have been broken into multiple commerce items
			-- of qty 1
			
			-- Note: we want to ignore skus in force_allocation table
			-- Gift cards may be broken down in multiple commerce items
			-- We do not want to consider those
			
			update order_item dest
			set is_split_allocate=1
			where exists (
			  select src.sku_id, count(src.sku_id) from order_item src 
			  where src.sku_id not in 
			  	(select sku_id from force_allocation)
			      and src.sku_id=dest.sku_id
			  group by src.sku_id 
			  having count(src.sku_id) > 1
			);
			printDebugOutput('    Marked ' || to_char(SQL%ROWCOUNT) || ' row(s) for split allocation.');

		end if;
	end;

procedure allocate_order(pOrderNumber varchar2)
as

    -- Get list of rules to be executed in the order they 
    -- should be executed

    cursor AllocationRulesCursor is 
    select rule_name, 
    	proc_name,
    	enabled
    from allocation_rules_config
    where enabled=1
    and category='ALLOCATE_ORDER'
    order by seq_num asc;

    lProcSQL varchar2(400);
    lProcParams varchar2(40);
    lEligibleStoreCount 	number(5,0);
  v_code  NUMBER;
  v_errm  VARCHAR2(64);    
begin
	printDebugOutput ('********* STARTING ALLOCATION ALGORITHM FOR ORDER ***********');

	open AllocationRulesCursor;
	loop

		fetch 	AllocationRulesCursor 
		into 	lAllocationRule.ruleName,
			lAllocationRule.procName,
			lAllocationRule.enabled;
		exit when AllocationRulesCursor%notfound;

		select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
		if(lEligibleStoreCount = 0) then
			printDebugOutput('Available store = ' || lEligibleStoreCount || '. Return...');
			ITEM_ALLOCATION_COMPLETE := TRUE;
			force_allocate_order(pOrderNumber);
			return;
		end if;

		lProcSQL := 'begin allocation_rules.' || lAllocationRule.procName || '(:skuId,:orderId); end;';
		printDebugOutput('********* -> Calling allocation_rules.' || lAllocationRule.procName);
		execute immediate lProcSQL using lProcParams,pOrderNumber;
	end loop;
	close AllocationRulesCursor;
	
	-- printDebugOutput('********* -> Calling update_allocations.');
	update_allocations(null,pOrderNumber);
    exception

	when allocation_errors.invalid_zip_err then
    v_code := SQLCODE;
    v_errm := SUBSTR(SQLERRM, 1, 64);
		printErrorOutput ('Invalid zip code. Marking order for force_allocation ' || v_code || ' -> ' || v_errm);
		force_allocate_order(pOrderNumber);
		
end;


procedure split_allocate_item(pOrderNumber varchar2, pSkuId varchar2,
			pRequestedQty number)
as

    lProcSQL varchar2(400);
    lSplitItemAllocProc varchar2(40);

begin
	printDebugOutput('********** Being Executing Alloc Algorithm for sku ' || pSkuId);			

    	select proc_name into lSplitItemAllocProc
    	from allocation_rules_config
    	where enabled=1
    		and category='SPLIT_ITEM'
    		and seq_num=0
    		and rownum=1;
	
	printDebugOutput('Trying to allocate ' || pRequestedQty || ' item(s) of sku ' || pSkuId);
	
	lProcSQL := 'begin allocation_rules.' || lSplitItemAllocProc || '(:orderId,:skuId,:requestedQty); end;';
	printDebugOutput('********* -> Calling split alloc proc .' || lSplitItemAllocProc);
	execute immediate lProcSQL using pOrderNumber,pSkuId,pRequestedQty;		
end;

procedure allocate_item(pSkuId varchar2,
			pOrderNumber varchar2)
as

    -- Get list of rules to be executed in the order they 
    -- should be executed

    cursor ItemAllocRulesCursor is 
    select rule_name, 
    	proc_name,
    	enabled
    from allocation_rules_config
    where enabled=1
    and category='ALLOCATE_ITEM'
    order by seq_num asc;

    lProcSQL varchar2(400);
  v_code  NUMBER;
  v_errm  VARCHAR2(64);
begin
	printDebugOutput('********** Being Executing Alloc Algorithm for sku ' || pSkuId);			
	
	-- printDebugOutput('ITEM_ALLOCATION_COMPLETE is ' || ITEM_ALLOCATION_COMPLETE || ' before populating store');
	
	open ItemAllocRulesCursor;
	loop

		fetch 	ItemAllocRulesCursor 
		into 	lAllocationRule.ruleName,
			lAllocationRule.procName,
			lAllocationRule.enabled;
		exit when ItemAllocRulesCursor%notfound;

		lProcSQL := 'begin allocation_rules.' || lAllocationRule.procName || '(:skuId,:orderId); end;';
		printDebugOutput('********* -> Calling allocation_rules.' || lAllocationRule.procName);
		execute immediate lProcSQL using pSkuId,pOrderNumber;			
	end loop;
	close ItemAllocRulesCursor;	
	
	printDebugOutput('********* -> Calling update_allocations');
	update_allocations(pSkuId,pOrderNumber);
	
	printDebugOutput('********** End Executing Alloc Algorithm for sku ' || lOrderSKU.skuId);			
    exception
   
	when no_data_found then
    v_code := SQLCODE;
    v_errm := SUBSTR(SQLERRM, 1, 64); 
		printErrorOutput ('Error in allocate_item for sku ' || pSkuId || ' in order ' || pOrderNumber || 
		' code ' || v_code || ' -> ' || v_errm);
		force_allocate_order(pOrderNumber);
end;

-- ******************************************************************
--                       validate_order
--
-- validates the order to ensure it can be put through the allocation
-- algorithm
-- 1. Check if the ship zip code exists in mff_zipcode_usa table. There
-- are instances where user can choose to checkout with an invalid zip code
-- Instead of denying the order on the UI, we accept it and let CSR handle allocation
--
-- 2. Check order is within threshold - 
--	Total quantity of items in the order (sum of all commerce item qty) is checked
--      Quantity per item is checked
-- 
--
--
-- ******************************************************************	

procedure validate_order(pOrderNumber varchar2)
as
v_sql varchar2(2000);

lItemQtyThreshold number(5,0);
lOrderQtyThreshold number(5,0);

lCurrItemQtyThresh number(5,0);
lCurrOrderQtyThresh number(5,0);

begin

	select item_quantity_threshold into lItemQtyThreshold 
	from atg_cata.mff_site_configuration
	where site_id='mffSite';
	printDebugOutput('validate_order lItemQtyThreshold ' || lItemQtyThreshold);
	-- execute immediate v_sql;

	select order_quantity_threshold into lOrderQtyThreshold 
	from atg_cata.mff_site_configuration 
	where site_id='mffSite';
	
	printDebugOutput('validate_order lOrderQtyThreshold ' || lOrderQtyThreshold);
	-- execute immediate v_sql;


	v_sql := 'select sum(count(*)) from dcspp_item ' ||
		 'where order_ref=''' || pOrderNumber || ''' ' ||
		 'group by catalog_ref_id ' ||
		 'having sum(quantity) > ' || lItemQtyThreshold;
		 
	printDebugOutput('validate_order Executing SQL ' || v_sql);
	execute immediate v_sql into lCurrItemQtyThresh;
	printDebugOutput('validate_order lCurrItemQtyThresh ' || lCurrItemQtyThresh);

	v_sql := 'select sum(quantity) from dcspp_item ' ||
		 'where order_ref=''' || pOrderNumber || '''';
	
	printDebugOutput('validate_order Executing SQL ' || v_sql);
	execute immediate v_sql into lCurrOrderQtyThresh;
	printDebugOutput('validate_order lCurrOrderQtyThresh ' || lCurrOrderQtyThresh);
	
	if(lCurrItemQtyThresh > 0) then
		printDebugOutput('There are ' || lCurrItemQtyThresh || 
			' item(s) exceeding item qty thresh ' || lItemQtyThreshold);
		force_allocate_order(pOrderNumber);
	end if;

	if(lCurrOrderQtyThresh > lOrderQtyThreshold) then
		printDebugOutput('Order has ' || lCurrOrderQtyThresh || 
			' item(s) & exceeds order qty thresh ' || lOrderQtyThreshold);
		force_allocate_order(pOrderNumber);
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
-- EDS - External drop ship items. These are items that are fulfilled
-- by an external fulfiller. When these items come in, we set the fulfillment
-- store to 4001. Since EDS skus are many in the catalog, we do not maintain
-- individual SKUs in the force_allocation table.
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
lRejectionThreshold number(5,0);
begin

	-- Force allocate gift cards
	v_sql := 'update order_item dest ' ||
		'set (dest.fulfillment_store,dest.is_gift_card) = ( ' ||
			'select src.store_id,src.is_gift_card from force_allocation src ' ||
			'where src.sku_id=dest.sku_id ' ||
		') ' ||
		'where exists ( ' ||
			'select 1 from force_allocation src ' ||
				'where src.sku_id=dest.sku_id ' ||
				'and src.is_gift_card = 1 ' ||
				')';
	
	printDebugOutput('force_allocate_items Executing SQL ' || v_sql);
	execute immediate v_sql;
	printDebugOutput(sql%rowcount || ' gift card record(s) force allocated.');

	-- Force allocate non-gift card items 
	v_sql := 'update order_item dest ' ||
		'set (dest.fulfillment_store,dest.is_force_allocate) = ( ' ||
			'select src.store_id,0 from force_allocation src ' ||
			'where src.sku_id=dest.sku_id ' ||
		') ' ||
		'where exists ( ' ||
			'select 1 from force_allocation src ' ||
				'where src.sku_id=dest.sku_id ' ||
				'and dest.quantity >= src.threshold ' ||
				'and src.is_gift_card=0 ' ||
				')';
	
	printDebugOutput('force_allocate_items Executing SQL ' || v_sql);
	execute immediate v_sql;
	printDebugOutput(sql%rowcount || ' non-gift card record(s) force allocated.');
	
	-- Mark EDS items
	v_sql := 'update order_item dest ' ||
		  'set fulfillment_store=4001 ' ||
		  'where is_drop_ship=1';

	printDebugOutput('force_allocate_items Executing SQL ' || v_sql);
	execute immediate v_sql;
	printDebugOutput(sql%rowcount || ' record(s) marked for EDS.');

	-- mark as force_allocate items that have be allocated at least 3 times
	-- TODO: Make this threshold configurable
	
	lRejectionThreshold := to_number(allocation_config.get_value('rejection_threshold'));
	v_sql := 'update order_item oi set oi.is_force_allocate = 1 ' ||
			'where exists( ' ||
  				'select mia.commerce_item_id, count(mia.store) alloc_count ' ||
  				'from mff_itemprev_allocations mia where mia.commerce_item_id in ( ' ||
  					'select commerce_item_id from order_item ' ||
  						'where fulfillment_store is null) ' ||
  							'and mia.commerce_item_id = oi.commerce_item_id ' ||
  					'group by commerce_item_id ' ||
  					'having count(store) >= ' || lRejectionThreshold || ')';

	printDebugOutput('force_allocate_items Exceeds previous allocation threshold ' || v_sql);
	execute immediate v_sql;
	printDebugOutput(sql%rowcount || ' record(s) exceeded store rejection threshold.');

end;

-- ******************************************************************
--                     		is_order_eligible_to_split
--  Order with certain shipping methods should not split.
--  
-- ******************************************************************	

function is_order_eligible_to_split (pOrderNumber varchar2)
return number is
   v_sql varchar2(2000);
   lShipMethods		number;
begin
	v_sql := 'select count(*) from dcspp_ship_group where order_ref=''' || pOrderNumber || ''' and shipping_method !=''Standard''';
	printDebugOutput('is_order_eligible_to_split v_sql ' || v_sql);
	execute immediate v_sql into lShipMethods;
	
	if lShipMethods > 0 then
		printDebugOutput('is_order_eligible_to_split is FALSE');
		return FALSE;
	else 
		printDebugOutput('is_order_eligible_to_split is TRUE');
		return TRUE;
	end if;
end;

procedure get_order_allocation (pOrderNumber in varchar2) 
as
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
		and (is_force_allocate=0 or is_force_allocate is null) 
		and is_split_allocate=0;
	
	lAllocComplete		number(1,0);
	lSplitOrder		number(1,0);
	lEligibleToSplit	number(1,0);
	lPendingSplitAlloc	number(1,0);

begin


  	printDebugOutput ('  Begin find optimal store mainline for order ' || pOrderNumber); 	
  	log_order_number 	:= pOrderNumber;

	-- Clear out tmp tables used for intermediate processing
	printDebugOutput ('    Deleting temp tables'); 
	delete from order_item;
	delete from sku_inventory;
	delete from eligible_stores;
	delete from store_queue_ratio;
	delete from store_bandwidth;
	
	populate_store_bandwidth;
	
	
	-- Reset allocation_complete flag
	ITEM_ALLOCATION_COMPLETE := FALSE;

  	
  	-- Add items to be allocated from the order to tmp table
  	printDebugOutput ('    Adding order skus to order_item table'); 	
  	add_order_items(pOrderNumber);
  	
  	if ITEM_ALLOCATION_COMPLETE = TRUE then
  		return;
  	end if;
  	
  	-- validate order
  	validate_order(pOrderNumber);
  	
  	-- See if there are items to be force allocated
  	-- Gift card... items exceeding store rejection threshold
  	-- TODO: Order being split too many times because of store rejections
  	printDebugOutput ('    Check for force_allocate_items'); 	
  	force_allocate_items(pOrderNumber);
  	
  	-- check if there are still items to be allocated.
  	-- if order contains only force_allocation_items
  	-- then we can exit the procedure.. say there is just a gift card in the order
  	
  	printDebugOutput ('    Check is_allocation_complete');
  	lAllocComplete := is_allocation_complete;
  	if lAllocComplete = TRUE then
  		printDebugOutput ('    Alloc complete... exiting..');
  		return;
  	end if;
  	
  	-- get inventory levels for all skus in the order
  	printDebugOutput ('    Getting inventory levels for order ' || pOrderNumber);
  	get_inventory_for_order_skus(pOrderNumber);
  	
	-- get list of stores that have sufficient inventory
	populate_eligible_stores(null,null,pOrderNumber,null);
    	
	-- Find out if the order needs to be split-allocated
	lSplitOrder := is_split_order;
	printDebugOutput('Split Order = ' || lSplitOrder);
    	
    	
    	-- There are stores that can fulfill the entire order
    	-- Find the best store by applying filtering rules in order

    	if lSplitOrder = FALSE Then
		allocate_order(pOrderNumber);
    	else
    		printDebugOutput('Checking if order is eligible for split allocation ');
    		lEligibleToSplit := is_order_eligible_to_split (pOrderNumber);
    		
    		if lEligibleToSplit = FALSE then
			printDebugOutput('Order is not eligible for split allocation. Returning...');
			lAllocComplete := is_allocation_complete;
			printDebugOutput('Is allocation complete ' || lAllocComplete);
			if lAllocComplete = FALSE then
				printDebugOutput('Marking order for force_allocation as it cannot be split');
				force_allocate_order(pOrderNumber);
			end if;
		else
			printDebugOutput('Order is eligible for split allocation.');    		
			-- The order needs to be split-allocated
			-- Will attempt to allocate best-fit store for each sku in the order
			printDebugOutput('Allocate each sku in order by calling allocate_item. SplitOrder is ' || lSplitOrder);


			open OrderSKUsCursor;
			loop
				-- Reset allocation flag
				ITEM_ALLOCATION_COMPLETE := FALSE;

				-- clear out any stale data from eligible store list
				printDebugOutput('Deleting from eligible stores');
				delete from eligible_stores;

				printDebugOutput ('********* STARTING ALLOCATION ALGORITHM EACH SKU IN THE ORDER.');

				fetch 	OrderSKUsCursor 
				into 	lOrderSKU.orderId,
					lOrderSKU.skuId,
					lOrderSKU.quantity,
					lOrderSKU.commerceItemId;
				exit when OrderSKUsCursor%notfound;

				-- Get stores that have sufficient inventory
				-- to fulfill requested qty of sku

				populate_eligible_stores(lOrderSKU.skuId,lOrderSKU.commerceItemId,lOrderSKU.orderId,lOrderSKU.quantity);

				printDebugOutput('ITEM_ALLOCATION_COMPLETE is ' || ITEM_ALLOCATION_COMPLETE || ' after populating store');
				if ITEM_ALLOCATION_COMPLETE = TRUE then
					printDebugOutput('Allocation complete for item ' || lOrderSKU.skuId || '. Continuing...');
					continue;
				end if;		

				allocate_item(lOrderSKU.skuId,lOrderSKU.orderId);

			end loop;
			close OrderSKUsCursor;
    		end if;
    	end if;

	-- check if order is within store split threshold
	-- check_order_split_count(pOrderNumber);

	-- check if there are individual items that are split
	-- and are yet to be allocated
	lPendingSplitAlloc := is_pending_split_allocation;
	
	if lPendingSplitAlloc = TRUE then
		printDebugOutput('Call split alloc routines');
		split_allocate_items;
	end if;
	allocation_rules.check_order_split_count(pOrderNumber);
end;
-- ******************************************************************
--
--                       Main Processing
--
-- ******************************************************************	
begin
	
	dbms_output.enable;

end allocation;