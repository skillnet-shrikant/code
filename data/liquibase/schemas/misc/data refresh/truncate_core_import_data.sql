-- *** clean up price ***** --
truncate table mff_price;
delete from dcs_price;
commit;

-- *** clean up price complete ***** --

-- *** clean up inventory ***** --
truncate table ff_store_inv_transaction;
truncate table ff_inventory_transaction;
truncate table ff_store_inventory;
truncate table ff_inventory;

-- *** clean up inventory complete ***** --

-- *** clean up facets ***** --

truncate table mff_sf_dyn_prop_map_str;
truncate table mff_sku_facet;


-- *** clean up facets complete ***** --