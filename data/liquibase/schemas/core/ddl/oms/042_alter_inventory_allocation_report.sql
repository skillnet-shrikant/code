alter table mff_inventory_report modify (order_datetime timestamp );

alter table mff_inventory_report modify (ecom_process_datetime timestamp );

alter table mff_inventory_report modify (create_datetime timestamp );

alter table mff_inventory_report modify (last_updated_datetime timestamp );

alter table mff_inventory_report add (reason_code varchar2(100));
