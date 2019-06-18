/*
 ******************************************************
          SITE CONFIGS
 ******************************************************
*/

grant select, update, delete, insert on atg_cata.dcs_catalog to atg_catfeed;
grant select, update, delete, insert on atg_cata.dcs_cat_catalogs to atg_catfeed;
grant select, update, delete, insert on atg_cata.site_types to atg_catfeed;
grant select, update, delete, insert on atg_cata.site_group to atg_catfeed;
grant select, update, delete, insert on atg_cata.site_configuration to atg_catfeed;
grant select, update, delete, insert on atg_cata.site_group_sites to atg_catfeed;
grant select, update, delete, insert on atg_cata.site_group_shareable_types to atg_catfeed;
grant select, update, delete, insert on atg_cata.dcs_catalog_sites to atg_catfeed;
grant select, update, delete, insert on atg_cata.dcs_category_sites to atg_catfeed;
grant select, update, delete, insert on atg_cata.dcs_product_sites to atg_catfeed;
grant select, update, delete, insert on atg_cata.dcs_sku_sites to atg_catfeed;

grant select, update, delete, insert on atg_catb.dcs_catalog to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_cat_catalogs to atg_catfeed;
grant select, update, delete, insert on atg_catb.site_types to atg_catfeed;
grant select, update, delete, insert on atg_catb.site_group to atg_catfeed;
grant select, update, delete, insert on atg_catb.site_configuration to atg_catfeed;
grant select, update, delete, insert on atg_catb.site_group_sites to atg_catfeed;
grant select, update, delete, insert on atg_catb.site_group_shareable_types to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_catalog_sites to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_category_sites to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_product_sites to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_sku_sites to atg_catfeed;

/*
 ******************************************************
 	CATALOG IMPORT 
 ******************************************************
*/
grant select, update, delete, insert on atg_pub.avm_devline to atg_catfeed;
grant select, update, delete, insert on atg_pub.epub_project to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_category to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_category to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_category to atg_catfeed;

grant select, update, delete, insert on atg_cata.mff_category to atg_catfeed;
grant select, update, delete, insert on atg_catb.mff_category to atg_catfeed;
grant select, update, delete, insert on atg_pub.mff_category to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_product to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_product to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_product to atg_catfeed;

grant select, update, delete, insert on atg_cata.mff_product to atg_catfeed;
grant select, update, delete, insert on atg_catb.mff_product to atg_catfeed;
grant select, update, delete, insert on atg_pub.mff_product to atg_catfeed;

grant select, update, delete, insert on atg_cata.mff_product_attr to atg_catfeed;
grant select, update, delete, insert on atg_catb.mff_product_attr to atg_catfeed;
grant select, update, delete, insert on atg_pub.mff_product_attr to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_sku to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_sku to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_sku to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_sku_attr to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_sku_attr to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_sku_attr to atg_catfeed;

grant select, update, delete, insert on atg_cata.mff_sku to atg_catfeed;
grant select, update, delete, insert on atg_catb.mff_sku to atg_catfeed;
grant select, update, delete, insert on atg_pub.mff_sku to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_root_cats to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_root_cats to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_root_cats to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_cat_chldcat to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_cat_chldcat to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_cat_chldcat to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_cat_catalogs to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_cat_catalogs to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_cat_catalogs to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_cat_chldprd to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_cat_chldprd to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_cat_chldprd to atg_catfeed;

grant select, update, delete, insert on atg_cata.dcs_prd_chldsku to atg_catfeed;
grant select, update, delete, insert on atg_catb.dcs_prd_chldsku to atg_catfeed;
grant select, update, delete, insert on atg_pub.dcs_prd_chldsku to atg_catfeed;
/*
 ******************************************************
 	INVENTORY IMPORT 
 ******************************************************
*/

grant select, update, delete, insert on atg_core.mff_inventory to atg_catfeed;
grant select, update, delete, insert on atg_core.mff_store_inventory to atg_catfeed;
grant all on directory inv_feed_incoming to atg_catfeed;
grant all on directory inv_delta_feed_incoming to atg_catfeed;
GRANT JAVAUSERPRIV TO ATG_CATFEED;

/*
 ******************************************************
 	PRICE IMPORT 
 ******************************************************
*/

grant select, update, delete, insert on atg_core.dcs_price to atg_catfeed;
grant select, update, delete, insert on atg_core.dcs_price_list to atg_catfeed;
grant select, update, delete, insert on atg_core.mff_price to atg_catfeed;
grant all on directory price_feed_incoming to atg_catfeed;

/*
 ******************************************************
 	SEARCH FACET IMPORT 
 ******************************************************
*/
grant select, update, delete, insert on atg_core.mff_sku_facet to atg_catfeed;
grant select, update, delete, insert on atg_core.mff_sf_dyn_prop_map_str to atg_catfeed;
grant all on directory facet_feed_incoming to atg_catfeed;

/*
 ******************************************************
 	ALLOCATIONS
 ******************************************************
*/

grant select, update, delete, insert on atg_core.mff_store_inventory to atg_oms;
grant select, update, delete, insert on atg_cata.mff_location to atg_oms;
grant select, update, delete, insert on atg_cata.dcs_location to atg_oms;
grant select, update, delete, insert on atg_catb.dcs_location to atg_oms;
grant select, update, delete, insert on atg_core.mff_zipcode_usa to atg_oms;
grant select, update, delete, insert on atg_core.mff_inventory to atg_oms;

grant select, update, delete, insert on atg_cata.mff_site_configuration to atg_oms;


/*
 ******************************************************
 	STORES
 ******************************************************
*/
grant select, update, delete, insert on atg_core.zipcode_usa to atg_cata;
grant select, update, delete, insert on atg_core.zipcode_usa to atg_catb;

/*
 ******************************************************
 	COMPUTED PROPERTIES
 ******************************************************
*/

grant select, update, delete, insert on atg_cata.mff_sku_computed to atg_catfeed;
grant select, update, delete, insert on atg_catb.mff_sku_computed to atg_catfeed;
grant select, update, delete, insert on atg_pub.mff_sku_computed to atg_catfeed;
grant select, update, delete, insert on atg_cata.google_mff_category_map to atg_catfeed;
grant select, update, delete, insert on atg_catb.google_mff_category_map to atg_catfeed;