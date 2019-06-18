alter table mff_inventory_report modify (order_datetime date );

alter table mff_inventory_report modify (ecom_process_datetime date );

alter table mff_inventory_report modify (create_datetime date );

alter table mff_inventory_report modify (last_updated_datetime date );

alter table mff_inventory_report drop column reason_code;
