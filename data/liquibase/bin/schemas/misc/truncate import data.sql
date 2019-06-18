-- *** Clean up categories **** --

truncate table mff_category;
truncate table dcs_cat_chldcat;
truncate table dcs_root_cats;
truncate table dcs_cat_catalogs;
truncate table dcs_cat_chldprd;
truncate table dcs_cat_media;
truncate table dcs_cat_anc_cats;
truncate table dcs_allroot_cats;
truncate table dcs_prd_anc_cats;
truncate table dcs_category_sites;
truncate table dcs_cat_prnt_cats;
truncate table dcs_prd_prnt_cats;
delete from dcs_category;
commit;
-- ***** clean up categories complete **** ----

-- *** Clean up products **** --
truncate table mff_product;
truncate table mff_product_attr;
truncate table dcs_prd_chldsku;
truncate table dcs_product_sites;
--truncate table dcs_product;
truncate table dcs_prd_catalogs;
delete from dcs_product;
commit;
-- *** Clean up products complete **** --

-- *** Clean up sku **** --
truncate table mff_sku;
truncate table dcs_sku_attr;
truncate table dcs_sku_sites;
truncate table dcs_sku_catalogs;
delete from dcs_sku;
commit;
-- *** Clean up sku complete **** --

-- *** Clean up seed data **** --
truncate table dcs_catalog_sites;
truncate table site_group_shareable_types;
truncate table site_group_sites;
delete from site_configuration;
commit;
delete from dcs_site;
commit;
delete from site_group;
commit;
delete from site_types;
commit;
delete from dcs_dir_anc_ctlgs;
commit;
delete from dcs_catalog;
commit;
-- ** clean up seed data complete ****----

-- ** clean up facet attributes ** --
truncate table das_gsa_dyn_prop_attr;
truncate table das_gsa_dyn_prop;

