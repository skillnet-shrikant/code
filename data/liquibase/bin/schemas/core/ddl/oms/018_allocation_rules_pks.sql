create or replace package allocation_rules
as
    	TYPE Coordinates IS RECORD (
		longitude		number,
		latitude		number,
		validCoordinates	number (1,0)
	);  	
	invalid_zipcode   EXCEPTION;

	procedure allocate_all_to_store(pSkuId varchar2,pOrderNumber varchar2);
	procedure allocate_by_queue_ratio(pSkuId varchar2,pOrderNumber varchar2);
	procedure allocate_by_distance(pSkuId varchar2,pOrderNumber varchar2);
	procedure allocate_by_inventory(pSkuId varchar2,pOrderNumber varchar2);
	-- procedure allocate_by_max_capacity;
	procedure split_allocate_by_distance(pOrderNumber varchar2,pSkuId varchar2,pRequestedQty number);
	procedure split_allocate_by_quantity(pOrderNumber varchar2,pSkuId varchar2,pRequestedQty number);
	
	procedure check_order_split_count (pOrderNumber varchar2);
	
	function get_ship_postal_code (pOrderNumber varchar2) return varchar2;
	function get_shipTo_coordinates (pOrderNumber varchar2,pPostalCode varchar2) return Coordinates;
	function isNumber(pValue in varchar2) return number;
end allocation_rules;
