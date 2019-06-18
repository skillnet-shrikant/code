/*
 ******************************************************
 	Redesigned inventory
 ******************************************************
*/

grant select, update, delete, insert on ff_inventory to atg_catfeed;
grant select, update, delete, insert on ff_store_inventory to atg_catfeed;
grant select, update, delete, insert on ff_store_inv_transaction to atg_catfeed;
grant select, update, delete, insert on ff_inventory_transaction to atg_catfeed;

grant select, update, delete, insert on ff_inventory to atg_oms;
grant select, update, delete, insert on ff_store_inventory to atg_oms;
grant select, update, delete, insert on ff_store_inv_transaction to atg_oms;
grant select, update, delete, insert on ff_inventory_transaction to atg_oms;

