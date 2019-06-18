-- *************************************************************************************
--  Procedure: p_get_inventory_for_sku
-- *************************************************************************************
create or replace
procedure p_get_inventory_for_sku  (pSkuId in varchar2,
					pQuantity in number,
					pPostalCode in varchar2) 
    as
	
	-- Program Constants
	TRUE				number (1,0) := 1;			-- True 
	FALSE				number (1,0) := 0;			-- False
	INVENTORY_THRESHOLD		number (1,0) := 3;			-- The 
	WAREHOUSE_STORE_NO		varchar2(6) := '197975';	-- Store number for the warehouse
	MAX_STORES			number (6,0) := 10;			-- Maximum number of stores to return
	MAX_DISTANCE			number (6,0) := 5000;		-- Maximum distance to search
	
	log_order_number		varchar2(40) := 'N/A';
	
	-- Allow Debugging
	DEBUGGING_ENABLED		number (1,0) := TRUE;		-- Set to FALSE to disable debugging
	
	-- Default Coordinates (Edison NJ)
	DEFAULT_LATITUDE		number(38,8) := 40.5187154;
	DEFAULT_LONGITUDE		number(38,8) := -74.4120953;
	
	-- Inventory Record
    	TYPE Inventory IS RECORD (
		skuId			varchar2(40),
		available		number(1,0),
		stockLevel		number(6,0),
		validRecord		number(1,0)
	);  
	lInventory	Inventory;	-- Inventory record 

	-- Latitude/Longitude Coordinates
    	TYPE Coordinates IS RECORD (
		longitude		number,
		latitude		number,
		validCoordinates	number (1,0)
	);  	
	lCoordinates		Coordinates;	-- Coordinates for shipping address
	fulfillable		number (1,0);	-- Item can be fulfilled

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
--                       printDebugOutput
--
--   Prints debugging output if DEBUGGING_ENABLED is true.
-- ******************************************************************	
procedure printDebugOutput (pMessage varchar2) 
   as
   PRAGMA AUTONOMOUS_TRANSACTION;
begin   
   if DEBUGGING_ENABLED = TRUE then
		dbms_output.put_line (pMessage); 
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
--                       get_inventory_for_sku
--
--   Get the inventory for a given sku
--   Check if there is any inventory across all the stores
-- ******************************************************************	
function get_inventory_for_sku (pSkuId varchar2)
	  return Inventory is
	lSkuId			varchar2(40);
	lStockLevel		number;
	lAllocated		number;
	lShipped		number;
	lSold			number;

	lInventoryInStock	number;
	lW1Inventory		number;
	lStoreInventory		number;
	lInventory		Inventory;
	
begin

	-- Get the inventory record for the UPC
	printDebugOutput ('Begin get_inventory_for_sku ' || pSkuId); 	
	lInventory.validRecord 	:= FALSE;
	
	select 	catalog_ref_id,
			stock_level,
			sold,
			allocated,
			shipped
	into 	lSkuId,
			lStockLevel,
			lSold,
			lAllocated,
			lShipped
	from atg_core.mff_inventory		
	where catalog_ref_id = pSkuId;


	lInventoryInStock := (lStockLevel - (lSold + lAllocated + lShipped));		
	lInventory.skuId := pSkuId;
	lInventory.validRecord 	:= TRUE;
	
	dbms_output.put_line('Inventory for Sku = ' || pSkuId);
	dbms_output.put_line('Inventory in stock = ' || lInventoryInStock);

	-- If Inventory > 0, then the item is in stock
	if lInventoryInStock >= 0 then
		lInventory.available := TRUE;
	else
		lInventory.available := FALSE;
	end if;
	lInventory.stockLevel := lInventoryInStock;
	
	printDebugOutput ('End get_inventory_for_sku ' || pSkuId); 	
	return lInventory;
end;

-- ******************************************************************
--                       can_item_be_fulfilled
--
--   Check to see if sufficient inventory exists in either the store
--   or the warehouse to fulfill this item.
--
-- ******************************************************************	
function can_item_be_fulfilled (pInventory Inventory, pItemsRequested number)
	  return number is
begin	  

	-- Is there sufficient inventory to fulfill this order 
	if pItemsRequested > lInventory.stockLevel then
		return FALSE;
	else 
		return TRUE;
	end if;
end;


procedure display_Inventory (pInventory Inventory)
	as
begin	

	printDebugOutput ('SKU ...................... ' || pInventory.skuId);
	printDebugOutput ('Available ................ ' || pInventory.available);
	printDebugOutput ('Stock Level .............. ' || pInventory.stockLevel);
	printDebugOutput ('Valid Record ............. ' || pInventory.validRecord);
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
		lCoordinates.latitude 			:= DEFAULT_LATITUDE;
		lCoordinates.longitude 			:= DEFAULT_LONGITUDE;
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
	return 	lCoordinates;
    exception
	when no_data_found then
		printErrorOutput ('No data found for postal code' || pPostalCode || ' - using default'); 
		lCoordinates.latitude 			:= DEFAULT_LATITUDE;
		lCoordinates.longitude 			:= DEFAULT_LONGITUDE;
		lCoordinates.validCoordinates 	:= FALSE;
		return lCoordinates;
end;

-- ******************************************************************
--                       get_eligible_stores
--   Get the list of eligible stores for this sku
--
-- ******************************************************************	
procedure get_eligible_stores (pSkuId varchar2, pQuantity number, pLatitude number, pLongitude number) 
	as
	lRowCount 	number(5,0);
begin
	printDebugOutput ('Begin get_eligible_stores for skuId: ' || pSkuId); 	

	select count(*) into lRowCount from (
	
		select si.catalog_ref_id,si.STORE_ID,(si.stock_level-(si.allocated+si.shipped)) as available_qty,pPostalCode, 
		f_compute_distance(pLatitude,pLongitude,loc.latitude,loc.longitude) as distance,
		loc.latitude,loc.longitude,pLatitude,pLongitude,si.allocated,pQuantity
		from atg_core.mff_store_inventory si,atg_cata.dcs_location loc
		where si.catalog_ref_id=pSkuId and (si.stock_level-(si.allocated+si.shipped)) >= pQuantity
		and si.store_id=loc.location_id
	);
	dbms_output.put_line('Found ' || lRowCount || ' to be added to sku_inventory');
	insert into sku_inventory
	(sku_id,store_id,available_qty,ship_postal_code,distance,store_latitude,store_longitude,
		ship_latitude,ship_longitude,current_queue_size,requested_qty)
	select si.catalog_ref_id,si.STORE_ID,(si.stock_level-(si.allocated+si.shipped)) as available_qty,pPostalCode, 
	f_compute_distance(pLatitude,pLongitude,loc.latitude,loc.longitude) as distance,
	loc.latitude,loc.longitude,pLatitude,pLongitude,si.allocated,pQuantity
	from atg_core.mff_store_inventory si,atg_cata.dcs_location loc
	where si.catalog_ref_id=pSkuId and (si.stock_level-(si.allocated+si.shipped)) >= pQuantity
	and si.store_id=loc.location_id;
	
	UPDATE sku_inventory sku 
	    SET current_queue_size = 
		(SELECT allocation_queue 
		    FROM (select store_id, sum(allocated) allocation_queue from atg_core.mff_store_inventory group by store_id) q 
		    WHERE sku.store_id= q.store_id); 	

	printDebugOutput ('End get_eligible_stores for sku: ' || pSkuId); 		  
end;

-- ******************************************************************
--
--                       Main Processing
--
-- ******************************************************************	
begin
	dbms_output.enable;
	dbms_output.put_line('Begin processing allocation for SKU ... ' || pSkuId);
	
	-- Get the current inventory for the UPC
	lInventory	:= get_inventory_for_sku (pSkuId);

	if lInventory.validRecord = TRUE then
		display_Inventory (lInventory);
	else	
		dbms_output.put_line('Cannot find an inventory record for SKU: ' || pSkuId);
	end if;

	-- Check to see if sufficient inventory exists to fulfill 
	fulfillable := can_item_be_fulfilled (lInventory, pQuantity);
	if fulfillable = FALSE then
		printDebugOutput ('Insufficient inventory for SKU ' || lInventory.skuId || ' Requested: ' || pQuantity || ' Available: ' || lInventory.stockLevel);
	else
		dbms_output.put_line('Fulfillable');
	end if;	

	-- Get the ship To Coordinates
	lCoordinates := get_shipTo_coordinates (pPostalCode);
	dbms_output.put_line('Latitude ' || lCoordinates.latitude);
	dbms_output.put_line('Longitude ' || lCoordinates.longitude);
	-- Get fulfillment stores
	dbms_output.put_line('getting eligible store for sku ' || pSkuId || ' quantity ' || pQuantity);
	get_eligible_stores (pSkuId, pQuantity, lCoordinates.latitude, lCoordinates.longitude);
end;