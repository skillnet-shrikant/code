-- 7 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_CORE' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_PRICE_LIST','MFF_SF_DYN_PROP_MAP_STR','MFF_PRICE','MFF_SKU_FACET','DCS_PRICE','MFF_STORE_INVENTORY','MFF_INVENTORY');

-- 22 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_CATA' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_CATALOG_SITES','SITE_GROUP','SITE_TYPES','DCS_ROOT_CATS','DCS_CAT_CHLDCAT','DCS_PRODUCT','DCS_CATALOG','DCS_CATEGORY_SITES','SITE_GROUP_SITES','SITE_CONFIGURATION','MFF_SKU','DCS_SKU_SITES','DCS_PRODUCT_SITES','DCS_PRD_CHLDSKU','MFF_PRODUCT','MFF_PRODUCT_ATTR','SITE_GROUP_SHAREABLE_TYPES','DCS_CATEGORY','MFF_CATEGORY','DCS_SKU','DCS_CAT_CATALOGS','DCS_CAT_CHLDPRD');

-- 22 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_CATB' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_CATALOG_SITES','SITE_GROUP','SITE_TYPES','DCS_ROOT_CATS','DCS_CAT_CHLDCAT','DCS_PRODUCT','DCS_CATALOG','DCS_CATEGORY_SITES','SITE_GROUP_SITES','SITE_CONFIGURATION','MFF_SKU','DCS_SKU_SITES','DCS_PRODUCT_SITES','DCS_PRD_CHLDSKU','MFF_PRODUCT','MFF_PRODUCT_ATTR','SITE_GROUP_SHAREABLE_TYPES','DCS_CATEGORY','MFF_CATEGORY','DCS_SKU','DCS_CAT_CATALOGS','DCS_CAT_CHLDPRD');

-- 22 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_STAGING_CATA' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_CATALOG_SITES','SITE_GROUP','SITE_TYPES','DCS_ROOT_CATS','DCS_CAT_CHLDCAT','DCS_PRODUCT','DCS_CATALOG','DCS_CATEGORY_SITES','SITE_GROUP_SITES','SITE_CONFIGURATION','MFF_SKU','DCS_SKU_SITES','DCS_PRODUCT_SITES','DCS_PRD_CHLDSKU','MFF_PRODUCT','MFF_PRODUCT_ATTR','SITE_GROUP_SHAREABLE_TYPES','DCS_CATEGORY','MFF_CATEGORY','DCS_SKU','DCS_CAT_CATALOGS','DCS_CAT_CHLDPRD');

-- 22 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_STAGING_CATB' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_CATALOG_SITES','SITE_GROUP','SITE_TYPES','DCS_ROOT_CATS','DCS_CAT_CHLDCAT','DCS_PRODUCT','DCS_CATALOG','DCS_CATEGORY_SITES','SITE_GROUP_SITES','SITE_CONFIGURATION','MFF_SKU','DCS_SKU_SITES','DCS_PRODUCT_SITES','DCS_PRD_CHLDSKU','MFF_PRODUCT','MFF_PRODUCT_ATTR','SITE_GROUP_SHAREABLE_TYPES','DCS_CATEGORY','MFF_CATEGORY','DCS_SKU','DCS_CAT_CATALOGS','DCS_CAT_CHLDPRD');

-- 14 tables
select count(distinct table_name) from USER_TAB_PRIVS 
where upper(grantor)='ATG_PUB' and upper(grantee)='ATG_CATFEED'
and upper(table_name) in ('DCS_CAT_CHLDCAT','DCS_ROOT_CATS','DCS_PRODUCT','MFF_SKU','DCS_PRD_CHLDSKU','EPUB_PROJECT','MFF_PRODUCT_ATTR','MFF_PRODUCT','DCS_SKU','DCS_CAT_CATALOGS','AVM_DEVLINE','MFF_CATEGORY','DCS_CATEGORY','DCS_CAT_CHLDPRD');