create or replace
function f_get_order_allocation 	(pOrderNumber in varchar2) 
return SYS_REFCURSOR
as

	-- Program Constants
	TRUE				number (1,0) := 1;		-- True 
	FALSE				number (1,0) := 0;		-- False

	-- Allow Debugging
	DEBUGGING_ENABLED		number (1,0) := TRUE;	-- Set to FALSE to disable debugging
 
 	log_order_number		varchar2(40) := 'N/A';

	lRecordset SYS_REFCURSOR;	-- Returned record set
	
	-- Order Item (Commerce item)
	TYPE OrderItem IS RECORD (
		orderItemId		varchar2(40),
		orderId			varchar2(40),
		commerceItemId		varchar2(40),
		quantity		number(5,0),
		sku_id			varchar2(40),
		fulfillmentStore	varchar2(40),
		forceAllocate		number(1),
		giftCard		number(1),
		splitItem		number(1),
		dropShip		number(1)
	);
	lOrderItem OrderItem; 
 
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
		-- dbms_output.put_line (pMessage); 
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
   end if;
end;   

-- ******************************************************************
--                       get_allocation_results
--
--   Get results from the allocation.
-- ******************************************************************	
function get_allocation_results  
	return SYS_REFCURSOR  
	is
	lAllocation SYS_REFCURSOR;
begin
	printDebugOutput('begin get_allocation_results ... ');

	OPEN lAllocation FOR
	select 	order_item_id,
			order_id,
			commerce_item_id,
			quantity,
			sku_id,
			fulfillment_store,
			nvl(is_force_allocate,0) is_force_allocate,
			nvl(is_gift_card,0) is_gift_card,
			nvl(is_split_item,0) is_split_item, 
			nvl(is_drop_ship,0) is_drop_ship
	from order_item;	
	printDebugOutput('End get_allocation_results ... ');
	return lAllocation;
end;
 
 -- ******************************************************************
--                       print_allocation
--
--   Prints the final allocation for each item in the order.
-- ******************************************************************	
procedure print_allocation  
   as
   	lDebugRecordset SYS_REFCURSOR;
 begin   

	OPEN lDebugRecordset FOR
	select 	order_item_id,
		order_id,
		commerce_item_id,
		quantity,
		sku_id,
		fulfillment_store,
		nvl(is_force_allocate,0) is_force_allocate,
		nvl(is_gift_card,0) is_gift_card,
		nvl(is_split_item,0) is_split_item,
		nvl(is_drop_ship,0) is_drop_ship
	from order_item;	

	loop
	  fetch lDebugRecordset into lOrderItem;
	  exit when lDebugRecordset%notfound;
	  log_order_number := lOrderItem.orderId;
	  printDebugOutput('-------------------------------------');
	  printDebugOutput('Order Item Id ...................... ' || lOrderItem.orderItemId);
	  printDebugOutput('Order Id ........................... ' || lOrderItem.orderId);
	  printDebugOutput('Commerce Item Id ................... ' || lOrderItem.commerceItemId);
	  printDebugOutput('Quantity ........................... ' || lOrderItem.quantity);
	  printDebugOutput('SKU ................................ ' || lOrderItem.sku_id);
	  printDebugOutput('Fulfillment Store .................. ' || lOrderItem.fulfillmentStore);
	  printDebugOutput('Force Allocation ................... ' || lOrderItem.forceAllocate);
	  printDebugOutput('Gift Card .......................... ' || lOrderItem.giftCard);
	  printDebugOutput('Split Item ......................... ' || lOrderItem.splitItem);
	  printDebugOutput('Drop Ship .......................... ' || lOrderItem.dropShip);
  	  printDebugOutput('-------------------------------------');
	end loop;
 end;   
 
 begin
	-- dbms_output.enable;
	-- dbms_output.put_line('Begin processing allocation for Order Number ... ' || pOrderNumber);
	
	-- dbms_output.put_line('--> Calling p_get_order_allocation for order number ... ' || pOrderNumber);
	allocation.get_order_allocation (pOrderNumber);
	
	-- dbms_output.put_line('************* PRINTING ALLOCATION RESULTS ************');
	print_allocation ();
	lRecordset := get_allocation_results ();	
  return lRecordset;
end;