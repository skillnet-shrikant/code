create table mff_inventory_report 
  (
    allocation_sequence_id varchar2(40 byte) not null, 
	web_order varchar2(40 byte) not null, 
	order_datetime date not null, 
	ecom_process_datetime date not null, 
	allocated_location number(10,0) not null, 
	allocated_sku_id varchar2(40 byte) not null, 
	allocation_sub_type number(*,0) not null, 
	allocation_type number(*,0) not null, 
	allocated_quantity number(*,0) not null, 
	create_datetime date, 
	created_by_user varchar2(40 byte), 
	last_updated_datetime date, 
	last_updated_by_user varchar2(40 byte), 
	 constraint mff_inventory_report_pk primary key (allocation_sequence_id)
   );