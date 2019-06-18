create or replace
package body allocation_rules as

  PKG_NAME constant varchar2(50) := 'allocation_rules';
	-- Program Constants
	TRUE	number (1,0) := 1;	-- True 
	FALSE	number (1,0) := 0;	-- False
	log_order_number	varchar2(40) := 'N/A';
	DEBUGGING_ENABLED					number (1,0) := TRUE;					-- Set to FALSE to disable debugging

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
   	dbms_output.put_line('Log Message ---- ');
   	
   end if;
end;   	

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

--	exception
--		when VALUE_ERROR then return FALSE;
end;


-- ******************************************************************
--                       get_ship_postal_code
--   Get the postal code for the shipping address of this order.
--
-- ******************************************************************	
function get_ship_postal_code (pOrderNumber varchar2) 
	return varchar2
		is
lPostalCode varchar2(40);	
begin	
--	printDebugOutput ('Begin get_ship_postal_code ' || pOrderNumber); 
	
	-- Get a list of the items for a given order
	select  distinct (substr(postal_code,1,5))
	into    lPostalCode
	from    dcspp_ship_group SG,
			dcspp_ship_addr ADDR
	where   SG.shipping_group_id = ADDR.shipping_group_id and
			SG.order_ref = pOrderNumber;		
--	printDebugOutput ('End get_ship_postal_code ' || pOrderNumber); 
	printDebugOutput('Postal code in the order is ' || lPostalCode);
	return 	lPostalCode;		
/*** TODO	
    exception
	when no_data_found then
		printErrorOutput ('Could not find ship postal code - using default of ' || DEFAULT_POSTAL_CODE); 
		return DEFAULT_POSTAL_CODE;
***/		
end;

-- ******************************************************************
--                       get_shipTo_coordinates
--   Get the longitude/latitude for the zip code where the item is 
--	 being sent.
--
-- ******************************************************************	
function get_shipTo_coordinates (pOrderNumber varchar2,pPostalCode varchar2)
	return Coordinates is
	lCoordinates Coordinates;
begin
	log_order_number 	:= pOrderNumber;
--	printDebugOutput ('Begin get_shipTo_coordinates for postal code: ' || pPostalCode); 	
	lCoordinates.latitude 			:= 0;
	lCoordinates.longitude 			:= 0;
	lCoordinates.validCoordinates 	:= FALSE;
	
	-- Check if this value is a number
	if isNumber (pPostalCode) = FALSE then
		-- printErrorOutput ('Non Numeric postal code - assume Canadian address' || pPostalCode || ' - using default'); 
		lCoordinates.validCoordinates 	:= FALSE;
		return lCoordinates;
	end if;
	
	-- Lookup the postal code to get Latitude/Longitude
	select  latitude, 
			longitude
	into 	lCoordinates.latitude,
			lCoordinates.longitude
	from    atg_core.mff_zipcode_usa
	where 	zipcode = to_number(pPostalCode) and latitude!=0 and longitude!=0 and
			rownum < 2;
--	printDebugOutput ('Coordinates Found for postal code ' || pPostalCode); 
--	printDebugOutput ('End get_shipTo_coordinates for postal code: ' || pPostalCode); 	
	lCoordinates.validCoordinates 	:= TRUE;

	printDebugOutput('Coordinates Found for postal code ' || pPostalCode || ' -> Latitude ' || lCoordinates.latitude || ' Longitude ' || lCoordinates.longitude);
	-- printDebugOutput('Longitude ' || lCoordinates.longitude);
	
	return 	lCoordinates;
    exception
	when no_data_found then
		printErrorOutput ('No data found for postal code - ' || pPostalCode); 
		lCoordinates.validCoordinates 	:= FALSE;
		return lCoordinates;
end;

  
-- ******************************************************************
--                       allocate_all_to_store
--   TODO: Revisit this method
-- ******************************************************************

procedure allocate_all_to_store(pSkuId varchar2,pOrderNumber varchar2)
as
	v_sql varchar2(2000);
	v_where_clause varchar2(1000);
	v_filter varchar2(1000);
	lEligibleStoreCount 	number(5,0); 

begin
	log_order_number 	:= pOrderNumber;
	-- Rule 1 - Do we have stores that can fulfill the entire order
	-- This would be a needless check
	
	if pSkuId is not null then
		v_where_clause := ' and sku_id = ''' || pSkuId || '''';
		v_filter := ' and item.sku_id = ''' || pSkuId || '''';
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
					v_filter ||
				' group by inv.store_id ' ||
				' having count(inv.sku_id)=(select count(*) from order_item where fulfillment_store is null' ||
				v_where_clause ||
				')' ||
			')' ||
		')';

	-- printDebugOutput('allocate_all_to_store for sku ' || pSkuId);
	printDebugOutput('Executing SQL ' || v_sql);
	execute immediate v_sql;
	
	select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
	printDebugOutput('After rule allocate_all_to_store, ' || lEligibleStoreCount || ' store(s) remain');
	printDebugOutput('---------------------------------------------------------------------------------');

end;


procedure allocate_by_queue_ratio(pSkuId varchar2,pOrderNumber varchar2)
as
	lEligibleStoreCount 	number(5,0); 
begin
	log_order_number 	:= pOrderNumber;
	-- Rule 2 - Use stores with higher queue ratio

	-- calculate queue ratios

	insert into store_queue_ratio(store_id,current_queue_size,max_queue_size,ratio,ranking)
	select store_id, current_queue_size,max_queue_size,ratio, row_number() over(order by ratio desc) from (
		select si.store_id, sum(si.allocated) current_queue_size,ml.max_queue_size,
				nvl ( (ml.max_queue_size/nullif(sum(si.allocated),0)),ml.max_queue_size) as ratio 
			from atg_cata.dcs_location dl,atg_cata.mff_location ml,atg_core.ff_store_inv_transaction si
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
	printDebugOutput('After rule allocate_by_queue_ratio, ' || lEligibleStoreCount || ' store(s) remain');
	printDebugOutput('---------------------------------------------------------------------------------');

end;

procedure allocate_by_inventory(pSkuId varchar2,pOrderNumber varchar2)
as
	v_sql varchar2(2000);
	sku_with_max_qty varchar2(40);
	allocated_store varchar2(40);
	lEligibleStoreCount 	number(5,0);
	lAllocatedStore		varchar2(40);
begin
	log_order_number 	:= pOrderNumber;
	
	-- Rule 3 - Based on highest Inventory
	printDebugOutput('allocate_by_inventory called for ' || pOrderNumber);

	if (pSkuId is null) then
		printDebugOutput('allocate_by_inventory for entire order ' || pOrderNumber);
		v_sql := 'select sku_id from (select sku_id from order_item order by quantity desc) where rownum < 2';		
		execute immediate v_sql into sku_with_max_qty;
		
		printDebugOutput('sku ' || sku_with_max_qty || ' has max qty.');
		
		v_sql := 'update eligible_stores ' ||
				'set eligible=''N'', filtered_out_by=''Rule: allocate_by_inventory'' ' ||
				'where store_id not in (select store_id from (select store_id from sku_inventory where sku_id = ''' || sku_with_max_qty || '''' || 
					' and store_id in (select store_id from eligible_stores where eligible=''Y'' or eligible is null) order by available_qty desc) ' ||
					' where rownum < 2)';
		printDebugOutput('allocate_by_inventory v_sql ' || v_sql);
		execute immediate v_sql;

		select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
		printDebugOutput('After rule allocate_by_inventory, ' || lEligibleStoreCount || ' stores remain');

		select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null;
		printDebugOutput('Selected store is ' || lAllocatedStore || ' after rule 3 execution for order ' || pOrderNumber);
		printDebugOutput('---------------------------------------------------------------------------------');
		
	else 
		printDebugOutput('allocate_by_inventory for sku ' || pSkuId);
		-- v_sql := 'select sku_id from (select sku_id from order_item order by quantity desc) where rownum < 2';		
		-- execute immediate v_sql into sku_with_max_qty;
		
		-- printDebugOutput('sku ' || sku_with_max_qty || ' has max qty.');
		
		v_sql := 'update eligible_stores ' ||
				'set eligible=''N'', filtered_out_by=''Rule: allocate_by_inventory'' ' ||
				'where store_id not in (select store_id from (select store_id from sku_inventory where sku_id = ''' || pSkuId || '''' || 
					' and store_id in (select store_id from eligible_stores where eligible=''Y'' or eligible is null) order by available_qty desc) ' ||
					' where rownum < 2)';
		printDebugOutput('allocate_by_inventory v_sql ' || v_sql);
		execute immediate v_sql;

		select count(*) into lEligibleStoreCount from eligible_stores where eligible='Y' or eligible is null;
		printDebugOutput('After rule allocate_by_inventory, ' || lEligibleStoreCount || ' stores remain');

		select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null;
		printDebugOutput('Selected store is ' || lAllocatedStore || ' after rule 3 execution for order ' || pOrderNumber);
		printDebugOutput('---------------------------------------------------------------------------------');		
	end if;

end;


procedure allocate_by_distance(pSkuId varchar2,pOrderNumber varchar2)
as
	lPostalCode 		varchar2(10);
	lAllocatedStore		varchar2(40);
	lEligibleStoreCount 	number(5,0);
	lCoordinates Coordinates;
begin
	log_order_number 	:= pOrderNumber;
	-- Rule 3 - Distance
	printDebugOutput('Getting postal code for order ' || pOrderNumber);
	lPostalCode := get_ship_postal_code (pOrderNumber);
	-- printDebugOutput('Postal code in the order is ' || lPostalCode);

	printDebugOutput('Getting coordinates for postal code ' || lPostalCode);
	lCoordinates := get_shipTo_coordinates (pOrderNumber,lPostalCode);
	-- printDebugOutput('Latitude ' || lCoordinates.latitude);
	-- printDebugOutput('Longitude ' || lCoordinates.longitude);

	if(lCoordinates.validCoordinates = FALSE) then
		printDebugOutput('Invalid coordinates. Returning... ');
		log_order_number := pOrderNumber;
		printErrorOutput ('Invalid coordinates. Skipping allocate_by_distance');
		raise allocation_errors.invalid_zip_err;
		return;
	end if;

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
	printDebugOutput('After rule allocate_by_distance, ' || lEligibleStoreCount || ' stores remain');

	select store_id into lAllocatedStore from eligible_stores where eligible='Y' or eligible is null;
	printDebugOutput('Selected store is ' || lAllocatedStore || ' after rule 3 execution for sku ' || pSkuId);
	printDebugOutput('---------------------------------------------------------------------------------');

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

	printDebugOutput('Store ' || lAllocatedStore || ' passes max rule check');
	
--	printDebugOutput('All items in order_item assigned to ' || lAllocatedStore);

end;
*/

-- ******************************************************************
--                       split_allocate_by_distance
--
-- Handles allocation logic of skus that are split into multiple commerce
-- items.
-- Eg: sku_id 0012345 with qty 5. No single store has sufficient inventory
-- to fulfull that sku. We need to spread this to multiple stores.
-- OMS split that request into 5 commerce items with sku_id 0012345 and 
-- qty 1 each. Each of those 5 items have to now be allocated.

-- ******************************************************************	

procedure split_allocate_by_distance(pOrderNumber varchar2,pSkuId varchar2,
			pRequestedQty number)
as
	lAllocateStore varchar2(40);
	lAllocateQty number;
	lTotalAllocQty number;
	lRemainingQty number;
	lDistanceRank number;
	
begin
	log_order_number 	:= pOrderNumber;
	lAllocateQty := 0;
	lTotalAllocQty := 0;
	lRemainingQty := pRequestedQty;
	
	printDebugOutput('Trying to allocate ' || pRequestedQty || ' item(s) of sku ' || pSkuId);
	
	while lTotalAllocQty < pRequestedQty
	loop
	
		-- query to allocate by distance
		select distance_rank, 
			store_id, 
			available_qty 
			into lDistanceRank,lAllocateStore,lAllocateQty
		from (
			select ROW_NUMBER() OVER (ORDER BY dist) as distance_rank, 
				store_id, 
				available_qty,
				dist from (
					select si.store_id as store_id,
						si.available_qty,
    						f_compute_distance(
				        		44.0289,-88.62905,
				        		loc.latitude,loc.longitude) as dist
					from sku_inventory si,
						atg_cata.dcs_location loc
					where sku_id= pSkuId
  						and si.store_id=loc.location_id
  						and si.available_qty>0
  					 )
  		      )
		where distance_rank=1;

		if lAllocateQty < lRemainingQty then
			lTotalAllocQty := lTotalAllocQty + lAllocateQty;
			lRemainingQty := lRemainingQty - lAllocateQty;
			printDebugOutput('Allocate ' || lAllocateQty || ' to ' || lAllocateStore);
			allocation.allocate_item_to_store(lAllocateStore,pSkuId,lAllocateQty);
		else
			lTotalAllocQty := lTotalAllocQty + lRemainingQty;
			printDebugOutput('Allocate ' || lRemainingQty || ' to ' || lAllocateStore);
			allocation.allocate_item_to_store(lAllocateStore,pSkuId,lRemainingQty);
		end if;
	end loop;
	
--	exception
--	when NO_DATA_FOUND then
--		printDebugOutput('Unable to successfully allocate sku ' || pSkuId || 
--			'. There are still ' || lRemainingQty || ' item(s) to be allocated');
--		mark_as_force_allocate(pSkuId);
end;

-- ******************************************************************
--                       split_allocate_by_quantity
--
-- Handles allocation logic of skus that are split into multiple commerce
-- items.
-- Eg: sku_id 0012345 with qty 5. No single store has sufficient inventory
-- to fulfull that sku. We need to spread this to multiple stores.
-- OMS split that request into 5 commerce items with sku_id 0012345 and 
-- qty 1 each. Each of those 5 items have to now be allocated.

-- ******************************************************************	

procedure split_allocate_by_quantity(pOrderNumber varchar2,pSkuId varchar2,
			pRequestedQty number)
as
	lAllocateStore varchar2(40);
	lAllocateQty number;
	lTotalAllocQty number;
	lRemainingQty number;
	lDistanceRank number;
	
begin
	log_order_number 	:= pOrderNumber;
	lAllocateQty := 0;
	lTotalAllocQty := 0;
	lRemainingQty := pRequestedQty;
	
	printDebugOutput('Trying to allocate ' || pRequestedQty || ' item(s) of sku ' || pSkuId);
	
	while lTotalAllocQty < pRequestedQty
	loop
	
		 -- Query to allocate by max qty
		select  store_id, 
			available_qty 
			into lAllocateStore,lAllocateQty 
		from (
				select store_id,
					available_qty 
				from sku_inventory 
				where sku_id= pSkuId and
					available_qty > 0
				order by available_qty desc
		      )
		where rownum=1;

		if lAllocateQty < lRemainingQty then
			lTotalAllocQty := lTotalAllocQty + lAllocateQty;
			lRemainingQty := lRemainingQty - lAllocateQty;
			printDebugOutput('Allocate ' || lAllocateQty || ' to ' || lAllocateStore);
			allocation.allocate_item_to_store(lAllocateStore,pSkuId,lAllocateQty);
		else
			lTotalAllocQty := lTotalAllocQty + lRemainingQty;
			printDebugOutput('Allocate ' || lRemainingQty || ' to ' || lAllocateStore);
			allocation.allocate_item_to_store(lAllocateStore,pSkuId,lRemainingQty);
		end if;
	end loop;
	
--	exception
--	when NO_DATA_FOUND then
--		printDebugOutput('Unable to successfully allocate sku ' || pSkuId || 
--			'. There are still ' || lRemainingQty || ' item(s) to be allocated');
--		mark_as_force_allocate(pSkuId);
end;

-- ******************************************************************
--                     		check_order_split_count
--  Returns count of distinct stores, items in the order have been allocated to
--  Does not count force allocated items or gift cards...
-- ******************************************************************	
procedure check_order_split_count (pOrderNumber varchar2) 
   as
   v_sql varchar2(2000);
   lCurSplitCount		number;
   lPrevSplitCount		number;
   
	begin
		log_order_number 	:= pOrderNumber;
		-- get distinct stores CURRENTLY marked for allocation. Do not consider
		-- gift cards
		select count(distinct fulfillment_store) into lCurSplitCount from order_item
		where fulfillment_store is not null and 
			is_gift_card=0;

		-- get distinct stores PREVIOUSLY marked for allocation. Do not consider
		-- gift cards
		v_sql := 'select count(distinct mi.fulfillment_store) ' ||
			'from dcspp_item di, mff_item mi ' ||
			'where di.commerce_item_id=mi.commerce_item_id ' ||
				'and di.order_ref=''' || pOrderNumber || '''' ||
				'and mi.is_giftcard=0';
		printDebugOutput('check_order_split_count v_sql ' || v_sql);
		execute immediate v_sql into lPrevSplitCount;
			
		printDebugOutput('Item(s) in order ' || pOrderNumber || ' are CURRENTLY assigned to ' || lCurSplitCount || ' distinct stores.');
		printDebugOutput('Item(s) in order ' || pOrderNumber || ' were PREVIOUSLY assigned to ' || lPrevSplitCount || ' distinct stores.');
		
		if (lCurSplitCount+lPrevSplitCount) > to_number(allocation_config.get_value('order_split_threshold')) then
			printDebugOutput('Order exceeds split threshold. Marking all items for force allocate.');
			v_sql := 'update order_item ' ||
				'set is_force_allocate=1, ' ||
				'fulfillment_store=null ' ||
				'where fulfillment_store is not null  and is_gift_card=0';
	
			printDebugOutput('Force allocate all items in order. Executing SQL ' || v_sql);
			execute immediate v_sql;
			printDebugOutput(sql%rowcount || ' record(s) force allocated.');				
		else 
			printDebugOutput('Order within split threshold. Retain current allocation results.');
		end if;
	end;
	

--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  printDebugOutput('Allocation rules initalized');
end allocation_rules;