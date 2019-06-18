insert into tmp_catg_from_xml(cat_id,description,parent_id,cat_level,activation_date,deactivation_date,template_id,batch_id)
select cat_id, description,parent_id,cat_level,to_date(activation_date,'YYYYMMDD') as activation_date,
to_date(deactivation_date,'YYYYMMDD') as deactivation_date,template_id,batch_id from tmp_relational_table src,
xmltable('/categories/category'
        passing src.xml_value
        columns 
          cat_id path '@id',
          description PATH 'description',
          activation_date path 'activationDate',
          deactivation_date path 'deactivationDate',
          template_id path 'template',
          cat_level number path 'level',
          parent_id path 'parent_id',
          batch_id path 'batchID'
        ) src_xml_col
  where src.filename like 'full_%';
  
 commit;
 
 -- create a catalog
 insert into atg_cata.dcs_catalog (catalog_id,version,display_name)values('mffFeedCatalog',200,'Starter Catalog');
 insert into atg_catb.dcs_catalog (catalog_id,version,display_name)values('mffFeedCatalog',200,'Starter Catalog');
 
 commit;
 
/********************************************************************************************

			CREATE CATEGORIES
		
*********************************************************************************************/	
 insert into atg_cata.dcs_category(category_id,version,catalog_id,creation_date,start_date,end_date,display_name,description,long_description,PARENT_CAT_ID)
 select cat_id, 200, 'mffFeedCatalog',systimestamp,activation_date,deactivation_date,description, description,description,parent_id from tmp_catg_from_xml;

insert into atg_catb.dcs_category(category_id,version,catalog_id,creation_date,start_date,end_date,display_name,description,long_description,PARENT_CAT_ID)
 select cat_id, 200, 'mffFeedCatalog',systimestamp,activation_date,deactivation_date,description, description,description,parent_id from tmp_catg_from_xml;
 
 insert into atg_cata.mff_category(category_id, cat_level,template_id,batch_id)
select cat_id, cat_level,template_id,batch_id from tmp_catg_from_xml;

 insert into atg_catb.mff_category(category_id, cat_level,template_id,batch_id)
select cat_id, cat_level,template_id,batch_id from tmp_catg_from_xml;

commit;

/********************************************************************************************

			CREATE CATEGORY RELATIONSHIPS
		
*********************************************************************************************/	

insert into atg_cata.dcs_root_cats(root_cat_id,catalog_id)
select category_id,'mffFeedCatalog' from atg_cata.dcs_category where parent_cat_id='rootCategory';

insert into atg_catb.dcs_root_cats(root_cat_id,catalog_id)
select category_id,'mffFeedCatalog' from atg_catb.dcs_category where parent_cat_id='rootCategory';



insert into atg_cata.dcs_cat_chldcat
SELECT parent_id, (DENSE_RANK() OVER(PARTITION BY parent_id ORDER BY cat_id))-1 AS seq_no, cat_id
FROM tmp_catg_from_xml where parent_id != 'rootCategory';

insert into atg_catb.dcs_cat_chldcat
SELECT parent_id, (DENSE_RANK() OVER(PARTITION BY parent_id ORDER BY cat_id))-1 AS seq_no, cat_id
FROM tmp_catg_from_xml where parent_id != 'rootCategory';



insert into atg_cata.dcs_cat_catalogs
select cat_id,'mffFeedCatalog' from tmp_catg_from_xml;

insert into atg_catb.dcs_cat_catalogs
select cat_id,'mffFeedCatalog' from tmp_catg_from_xml;

commit;

/********************************************************************************************

			CREATE PRODUCTS
		
*********************************************************************************************/

insert into tmp_prod_from_xml(prod_id, description,selling_point_1,selling_point_2,parent_category,
weight_description,dimension_description,activate_date,no_of_alt_images,batch_id)
select prod_id, description,selling_point_1,selling_point_2,parent_category,
weight_description,dimension_description,to_date(activate_date,'YYYY-MM-DD'),no_of_alt_images,batch_id from tmp_relational_table src,
xmltable('/products/product'
        passing src.xml_value
        columns 
          prod_id path '@id',
          description PATH 'description',
          selling_point_1 path 'selling_points/selling_point[@seq_no="1"]',
          selling_point_2 path 'selling_points/selling_point[@seq_no="2"]',
          parent_category path 'categories/parent_category[@seq_no="1"]',
          weight_description path 'weight_description',
          dimension_description path 'dimension_description',
          activate_date path 'activate_date',
          no_of_alt_images path 'no_of_alt_images',
          batch_id path 'batch_id'
        ) src_xml_col
  where src.filename like 'ECOM_PRD%';
  
  commit;


insert into atg_cata.dcs_product (product_id, description,parent_cat_id,start_date,version,creation_date)
select prod_id, description,parent_category,activate_date,200,systimestamp from tmp_prod_from_xml where parent_category not like '%^%';

insert into atg_catb.dcs_product (product_id, description,parent_cat_id,start_date,version,creation_date)
select prod_id, description,parent_category,activate_date,200,systimestamp from tmp_prod_from_xml where parent_category not like '%^%';


insert into atg_cata.mff_product(product_id, selling_point_1, selling_point_2,wt_description,dim_description, num_alt_images,batch_id)
select prod_id,selling_point_1,selling_point_2,weight_description,dimension_description,no_of_alt_images,batch_id from tmp_prod_from_xml 
where parent_category not like '%^%';

insert into atg_catb.mff_product(product_id, selling_point_1, selling_point_2,wt_description,dim_description, num_alt_images,batch_id)
select prod_id,selling_point_1,selling_point_2,weight_description,dimension_description,no_of_alt_images,batch_id from tmp_prod_from_xml 
where parent_category not like '%^%';

commit;

insert into atg_cata.dcs_cat_chldprd(category_id,sequence_num, child_prd_id)
SELECT parent_category, (DENSE_RANK() OVER(PARTITION BY parent_category ORDER BY prod_id))-1 AS seq_no, prod_id
FROM tmp_prod_from_xml where parent_category not like '%^%';

insert into atg_catb.dcs_cat_chldprd(category_id,sequence_num, child_prd_id)
SELECT parent_category, (DENSE_RANK() OVER(PARTITION BY parent_category ORDER BY prod_id))-1 AS seq_no, prod_id
FROM tmp_prod_from_xml where parent_category not like '%^%';

commit;

/********************************************************************************************

			CREATE SKUs
		
*********************************************************************************************/


insert into tmp_sku_from_xml
(sku_id, sku_length, activate_date, girth, width, weight, description, vpn, product,
customize_max_char, is_cube, sold_at, freight_class, batch_id, sku_depth, upc_1,upc_2, upc_3,
recurring_allowed, date_sensitive, hazardous, refrigerate, oversized, serial_tracking,
restrict_air, ltl_fuel_surcharge, customize, age_restriction, eds,ltl_lift_gate,
consumable, usa, clearance, ltl_res_delivery, vacinne, lot_tracking, 
long_light,freezable,ltl)
select sku_id, sku_length, to_date(activate_date,'YYYY-MM-DD') as activate_date,
girth, 
width,
weight,
description,
vpn,
product,
customize_max_char, --number
is_cube, -- double
sold_at,
freight_class,
batch_id, sku_depth,
upc_1,upc_2, upc_3,

case recurring_allowed
when 'Y' then 1
else 0
end as recurring_allowed,

case date_sensitive
when 'Y' then 1
else 0
end as date_sensitive, 

case hazardous
when 'Y' then 1
else 0
end as hazardous,
case refrigerate
when 'Y' then 1
else 0
end as refrigerate,
case oversized
when 'Y' then 1
else 0
end as oversized, 
case serial_tracking
when 'Y' then 1
else 0
end as serial_tracking,
case restrict_air
when 'Y' then 1
else 0
end as restrict_air,
case ltl_fuel_surcharge
when 'Y' then 1
else 0
end as ltl_fuel_surcharge, 
case customize
when 'Y' then 1
else 0
end as customize,  
case age_restriction
when 'Y' then 1
else 0
end as age_restriction,
case eds
when 'Y' then 1
else 0
end as eds,  
case ltl_lift_gate
when 'Y' then 1
else 0
end as ltl_lift_gate,
case consumable
when 'Y' then 1
else 0
end as consumable, 
case usa
when 'Y' then 1
else 0
end as usa, 
case clearance
when 'Y' then 1
else 0
end as clearance, 
case ltl_res_delivery
when 'Y' then 1
else 0
end as ltl_res_delivery, 
case vacinne
when 'Y' then 1
else 0
end as vacinne, 
case lot_tracking
when 'Y' then 1
else 0
end as lot_tracking, 
case long_light
when 'Y' then 1
else 0
end as long_light,
case freezable
when 'Y' then 1
else 0
end as freezable,
case ltl
when 'Y' then 1
else 0
end as ltl from tmp_relational_table src,
xmltable('/SKUs/SKU'
        passing src.xml_value
        columns 
          sku_id path '@id',
          sku_length number path 'LENGTH',
          activate_date path 'ACTIVATE_DATE',
          recurring_allowed path 'RECURRING_ALLOWED',
          girth number path 'GIRTH',
          date_sensitive path 'DATE_SENSITIVE',
          hazardous path 'HAZARDOUS',
          refrigerate path 'REFRIGERATE',
          width number path 'WIDTH',
          oversized path 'OVERSIZED',
          serial_tracking path 'SERIAL_TRACKING',
          weight number path 'WEIGHT',
          restrict_air path 'RESTRICT_AIR',
          ltl_fuel_surcharge path 'LTL_FUEL_SURCHARGE',
          customize path 'CUSTOMIZE',
          description path 'DESCRIPTION',
          age_restriction path 'Age_Restriction',
          vpn path 'VPN',
          eds path 'EDS',
          product path 'PRODUCT',
          customize_max_char number path 'CUSTOMIZE_MAX_CHAR',
          is_cube number path 'CUBE',
          ltl_lift_gate path 'LTL_LIFT_GATE',
          sold_at path 'SOLD_AT',
          consumable path 'CONSUMABLE',
          usa path 'USA',
          clearance path 'CLEARANCE',
          freight_class path 'FREIGHT_CLASS',
          ltl_res_delivery path 'LTL_RES_DELIVERY',
          vacinne path 'VACINNE',
          batch_id number path 'BATCH_ID',
          sku_depth number path 'DEPTH',
          lot_tracking path 'LOT_TRACKING',
          long_light path 'LONG_LIGHT',
          upc_1 path 'UPC',
          upc_2 path 'UPC',
          upc_3 path 'UPC',
          freezable path 'FREEZABLE',
          ltl path 'LTL'
        ) src_xml_col
  where src.filename like 'ECOM_ITM%';
commit;


insert into atg_cata.dcs_sku(sku_id,start_date, description,version,sku_type,creation_date)
select sku_id, to_date(activate_date,'YYYY-MM-DD'), description,200,1,systimestamp from tmp_sku_from_xml;

insert into atg_catb.dcs_sku(sku_id,start_date, description,version,sku_type,creation_date)
select sku_id, to_date(activate_date,'YYYY-MM-DD'), description,200,1,systimestamp from tmp_sku_from_xml;

insert into atg_cata.mff_sku(sku_id, sku_length, 
girth, 
width,
weight,
vpn,
customize_max_char, --number
is_cube, -- double
sold_at,
freight_class,
batch_id, sku_depth,
upc_1,upc_2, upc_3,
recurring_allowed,
date_sensitive, 
hazardous,
refrigerate,
oversized, 
serial_tracking,
restrict_air,
ltl_fuel_surcharge, 
customize,  
age_restriction,
eds,  
ltl_lift_gate,
consumable, 
usa, 
clearance, 
ltl_res_delivery, 
vacinne, 
lot_tracking, 
long_light,
freezable,
ltl)
select sku_id, sku_length, 
girth, 
width,
weight,
vpn,
customize_max_char, --number
is_cube, -- double
sold_at,
freight_class,
batch_id, sku_depth,
upc_1,upc_2, upc_3,
recurring_allowed,
date_sensitive, 
hazardous,
refrigerate,
oversized, 
serial_tracking,
restrict_air,
ltl_fuel_surcharge, 
customize,  
age_restriction,
eds,  
ltl_lift_gate,
consumable, 
usa, 
clearance, 
ltl_res_delivery, 
vacinne, 
lot_tracking, 
long_light,
freezable,
ltl from tmp_sku_from_xml;

insert into atg_catb.mff_sku(sku_id, sku_length, 
girth, 
width,
weight,
vpn,
customize_max_char, --number
is_cube, -- double
sold_at,
freight_class,
batch_id, sku_depth,
upc_1,upc_2, upc_3,
recurring_allowed,
date_sensitive, 
hazardous,
refrigerate,
oversized, 
serial_tracking,
restrict_air,
ltl_fuel_surcharge, 
customize,  
age_restriction,
eds,  
ltl_lift_gate,
consumable, 
usa, 
clearance, 
ltl_res_delivery, 
vacinne, 
lot_tracking, 
long_light,
freezable,
ltl)
select sku_id, sku_length, 
girth, 
width,
weight,
vpn,
customize_max_char, 
is_cube, 
sold_at,
freight_class,
batch_id, sku_depth,
upc_1,upc_2, upc_3,
recurring_allowed,
date_sensitive, 
hazardous,
refrigerate,
oversized, 
serial_tracking,
restrict_air,
ltl_fuel_surcharge, 
customize,  
age_restriction,
eds,  
ltl_lift_gate,
consumable, 
usa, 
clearance, 
ltl_res_delivery, 
vacinne, 
lot_tracking, 
long_light,
freezable,
ltl from tmp_sku_from_xml;

commit;

insert into atg_cata.dcs_prd_chldsku(product_id,sequence_num,sku_id)
SELECT product, (DENSE_RANK() OVER(PARTITION BY product ORDER BY sku_id))-1 AS seq_no, sku_id
FROM tmp_sku_from_xml 
where product in (select product_id from atg_cata.dcs_product);

insert into atg_catb.dcs_prd_chldsku(product_id,sequence_num,sku_id)
SELECT product, (DENSE_RANK() OVER(PARTITION BY product ORDER BY sku_id))-1 AS seq_no, sku_id
FROM tmp_sku_from_xml 
where product in (select product_id from atg_catb.dcs_product);

commit;

/********************************************************************************************

			CREATE PRICES
		
*********************************************************************************************/

insert into atg_core.dcs_price_list(locale ,last_mod_date ,price_list_id ,version ,creation_date ,display_name)
  values
  (
    0,
    systimestamp,
    'mffListPrice',
    1,
    systimestamp,
    'MFF List Prices'
  );
insert into atg_core.dcs_price_list(locale ,last_mod_date ,price_list_id ,version ,creation_date ,display_name)
  values
  (
    0,
    systimestamp,
    'mffSalePrice',
    1,
    systimestamp,
    'MFF Sale Prices'
  );

commit;

merge into atg_core.dcs_price dest
	using (select 'lp-' || sku_id as price_id, sku_id,retail_price from tmp_mff_price_data ) src
	on (dest.price_id = src.price_id)
	when matched then 
		update set dest.list_price = src.retail_price where dest.price_id=src.price_id
	when not matched then 
		insert (dest.price_id,dest.version,dest.price_list,dest.sku_id,dest.pricing_scheme,dest.list_price) 
		values ('lp-'||src.sku_id,
			1,
			'mffListPrice',
			src.sku_id,
			0,
			src.retail_price);

merge into atg_core.dcs_price dest
	using (select 'sp-' || sku_id as price_id, sku_id,sale_price from tmp_mff_price_data ) src
	on (dest.price_id = src.price_id)
	when matched then 
		update set dest.list_price = src.sale_price where dest.price_id=src.price_id
	when not matched then 
		insert (dest.price_id,dest.version,dest.price_list,dest.sku_id,dest.pricing_scheme,dest.list_price) 
		values ('sp-'||src.sku_id,
			1,
			'mffSalePrice',
			src.sku_id,
			0,
			src.sale_price);


commit;

/********************************************************************************************

			CREATE INVENTORY
		
*********************************************************************************************/

merge into tmp_mff_inv_data dest
  using (select sku_id, sum(avail_qty) as avail_qty from tmp_mff_inv_data where store_id != '4000' group by sku_id) src
  on(dest.sku_id=src.sku_id and dest.store_id='4000')
  when matched then
    update set dest.avail_qty=src.avail_qty;
    
commit;

merge into atg_core.mff_inventory dest
	using (select 'inv-' || sku_id as inventory_id, sku_id, store_id,avail_qty from tmp_mff_inv_data where store_id='4000') src
	on (dest.inventory_id = src.inventory_id)
	when matched then 
		update set dest.stock_level = src.avail_qty where dest.inventory_id=src.inventory_id
	when not matched then 
		insert (dest.inventory_id,dest.display_name,dest.version,dest.catalog_ref_id,dest.avail_status,dest.stock_level,dest.location_id) 
		values (src.inventory_id,
			src.inventory_id,
			1,
			src.sku_id,
			1000,
			src.avail_qty,
			src.store_id);
			
merge into atg_core.mff_store_inventory dest
	using (select 'inv-' || sku_id || '-' || store_id as inventory_id, sku_id, store_id,avail_qty 
		  from tmp_mff_inv_data where store_id!='4000') src
	on (dest.inventory_id = src.inventory_id)
	when matched then 
		update set dest.stock_level = src.avail_qty,
		       last_update_date=systimestamp
			where dest.inventory_id=src.inventory_id
	when not matched then 
		insert (dest.inventory_id,dest.store_id,dest.catalog_ref_id,dest.stock_level,dest.creation_date) 
		values (src.inventory_id,
			src.store_id,
			src.sku_id,
			src.avail_qty,
			systimestamp);
			
/********************************************************************************************

			CREATE SITE
		
*********************************************************************************************/

insert into atg_cata.site_types(id,site_type) values ('mffSite','commerce');

insert into atg_cata.site_group(id,display_name) values ('mffGroup','MFF Site Group');
insert into atg_cata.dcs_site(id,catalog_id,list_pricelist_id,sale_pricelist_id) 
	values('mffSite','mffFeedCatalog','mffListPrice','mffSalePrice');
	

insert into atg_cata.site_configuration(id,name,description,production_url,enabled,context_root,
site_priority,access_level,endeca_site_id) values(
'mffSite','MFF Store','MFF Store','/',1,'/',1,30,'/mffSite');

insert into atg_cata.site_group_sites(site_id,site_group_id) values ('mffSite','mffGroup');

insert into atg_cata.site_group_shareable_types(shareable_types,site_group_id) values('atg.ShoppingCart','mffGroup');

insert into atg_cata.dcs_catalog_sites(catalog_id,site_id) values ('mffFeedCatalog','mffSite');

insert into atg_cata.dcs_category_sites(category_id,site_id)
select category_id,'mffSite' from atg_cata.dcs_category;

insert into atg_cata.dcs_product_sites(product_id,site_id)
select product_id,'mffSite' from atg_cata.dcs_product;

insert into atg_cata.dcs_sku_sites(sku_id,site_id)
select sku_id,'mffSite' from atg_cata.dcs_sku;


insert into atg_catb.site_types(id,site_type) values ('mffSite','commerce');

insert into atg_catb.site_group(id,display_name) values ('mffGroup','MFF Site Group');
insert into atg_catb.dcs_site(id,catalog_id,list_pricelist_id,sale_pricelist_id) 
	values('mffSite','mffFeedCatalog','mffListPrice','mffSalePrice');
	

insert into atg_catb.site_configuration(id,name,description,production_url,enabled,context_root,
site_priority,access_level,endeca_site_id) values(
'mffSite','MFF Store','MFF Store','/',1,'/',1,30,'/mffSite');

insert into atg_catb.site_group_sites(site_id,site_group_id) values ('mffSite','mffGroup');

insert into atg_catb.site_group_shareable_types(shareable_types,site_group_id) values('atg.ShoppingCart','mffGroup');

insert into atg_catb.dcs_catalog_sites(catalog_id,site_id) values ('mffFeedCatalog','mffSite');

insert into atg_catb.dcs_category_sites(category_id,site_id)
select category_id,'mffSite' from atg_catb.dcs_category;

insert into atg_catb.dcs_product_sites(product_id,site_id)
select product_id,'mffSite' from atg_catb.dcs_product;

insert into atg_catb.dcs_sku_sites(sku_id,site_id)
select sku_id,'mffSite' from atg_catb.dcs_sku;


