-- *** clean up price ***** --
truncate table mff_price;
delete from dcs_price;
commit;

-- *** clean up price complete ***** --

-- *** clean up inventory ***** --
truncate table mff_inventory;
truncate table mff_store_inventory;

-- *** clean up inventory complete ***** --

-- *** clean up facets ***** --

truncate table mff_sf_dyn_prop_map_str;
truncate table mff_sku_facet;


-- *** clean up facets complete ***** --