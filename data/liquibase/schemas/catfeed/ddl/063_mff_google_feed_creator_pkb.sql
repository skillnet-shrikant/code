create or replace
package body mff_google_feed_creator as

  PKG_NAME constant varchar2(50) := 'mff_google_feed_creator';
  
  -- main procedure that performs all tasks related to google feed creator
  procedure run as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_google_feed_creator;
  	load_google_feed_creator;
  	commit;
  end;
  
  -- Cleanup tables before and/or after we're done with google feed creator
  procedure cleanup_google_feed_creator as
  begin
  	delete from google_feed_data_onsale;
	delete from google_feed_data_regular;
  	commit;
  end;
  
  --load data into temp table
  procedure load_google_feed_creator as
  begin
	load_onsale;
	load_regular;
	commit;
	
  end;
  
   procedure load_onsale as
   begin
	load_onsale_skus;
	load_onsale_skus_price;
	del_onsale_skus_noprice;
	load_onsale_skus_inv;
	del_onsale_skus_noinv;
	load_onsale_skus_prdinfo;
	del_onsale_skus_noprd;
	load_onsale_skus_catinfo;
	del_onsale_skus_nocatinfo;
	load_onsale_skus_googleinfo;
	load_onsale_skus_color;
	load_onsale_skus_age_group;
	load_onsale_skus_gender;
	load_onsale_skus_size;
	load_onsale_skus_color_attr;
   end;
   
   procedure load_regular as
   begin
	load_regular_skus;
	load_regular_skus_price;
	del_regular_skus_noprice;
	load_regular_skus_inv;
	del_regular_skus_noinv;
	load_regular_skus_prdinfo;
	del_regular_skus_noprd;
	load_regular_skus_catinfo;
	del_regular_skus_nocatinfo;
	load_regular_skus_googleinfo;
	load_regular_skus_color;
	load_regular_skus_age_group;
	load_regular_skus_gender;
	load_regular_skus_size;
	load_regular_skus_color_attr;
   end;
  
  procedure load_onsale_skus as
  begin
	insert into google_feed_data_onsale (sku_id,on_sale,clearance,upcs,ltl,sku_length,girth,width,oversized,weight,restrict_air,ltl_fuel_surcharge,eds,ltl_lift_gate,freight_class,ltl_res_delivery,sku_depth,long_light,shipping_surcharge_qnty_range,free_shipping)
	select atgsku.sku_id,atgsku.on_sale,mffsku.clearance,mffsku.upcs,mffsku.ltl,mffsku.sku_length,mffsku.girth,mffsku.width,mffsku.oversized,mffsku.weight,mffsku.restrict_air,mffsku.ltl_fuel_surcharge,mffsku.eds,mffsku.ltl_lift_gate,mffsku.freight_class,mffsku.ltl_res_delivery,mffsku.sku_depth,mffsku.long_light,mffsku.shipping_surcharge_qnty_range,mffsku.free_shipping from ATG_CATA.dcs_sku atgsku
	inner join ATG_CATA.mff_sku mffsku on mffsku.sku_id=atgsku.sku_id
	where atgsku.start_date<=CURRENT_TIMESTAMP AND (atgsku.end_date>=CURRENT_TIMESTAMP OR atgsku.end_date IS NULL);
	commit;
  end;
  
  procedure load_regular_skus as
  begin
	insert into google_feed_data_regular (sku_id,on_sale,clearance,upcs,ltl,sku_length,girth,width,oversized,weight,restrict_air,ltl_fuel_surcharge,eds,ltl_lift_gate,freight_class,ltl_res_delivery,sku_depth,long_light,shipping_surcharge_qnty_range,free_shipping)
	select atgsku.sku_id,atgsku.on_sale,mffsku.clearance,mffsku.upcs,mffsku.ltl,mffsku.sku_length,mffsku.girth,mffsku.width,mffsku.oversized,mffsku.weight,mffsku.restrict_air,mffsku.ltl_fuel_surcharge,mffsku.eds,mffsku.ltl_lift_gate,mffsku.freight_class,mffsku.ltl_res_delivery,mffsku.sku_depth,mffsku.long_light,mffsku.shipping_surcharge_qnty_range,mffsku.free_shipping from ATG_CATA.dcs_sku atgsku
	inner join ATG_CATA.mff_sku mffsku on mffsku.sku_id=atgsku.sku_id
	where atgsku.start_date<=CURRENT_TIMESTAMP AND (atgsku.end_date>=CURRENT_TIMESTAMP OR atgsku.end_date IS NULL);
	commit;
	
  end;
  
  procedure load_onsale_skus_price as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
	  (SELECT onsale.sku_id,price.list_price from ATG_CATFEED.google_feed_data_onsale onsale
	   INNER JOIN 
	   (select saleprice.list_price,saleprice.sku_id from (select atgprice.sku_id,atgprice.list_price,atgprice.price_list from ATG_CORE.dcs_price atgprice
		inner join (select dcsprice.sku_id,min(dcsprice.list_price) as list_price from ATG_CORE.dcs_price dcsprice group by sku_id)prc on prc.sku_id=atgprice.sku_id and prc.list_price=atgprice.list_price
		where atgprice.list_price-prc.list_price=0 and atgprice.price_list='mffSalePrice')saleprice
		where saleprice.sku_id not in 
		(select atgprice2.sku_id from ATG_CORE.dcs_price atgprice2
		inner join (select dcsprice2.sku_id,min(dcsprice2.list_price) as list_price from ATG_CORE.dcs_price dcsprice2 group by dcsprice2.sku_id)prc2 on prc2.sku_id=atgprice2.sku_id and prc2.list_price=atgprice2.list_price
		where atgprice2.list_price-prc2.list_price=0 and atgprice2.price_list='mffListPrice'))price
		on price.sku_id=onsale.sku_id)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		  SET target.list_price=src.list_price;
	commit;
	  
	
  end;
  
  procedure load_regular_skus_price as
  begin
	
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
	  (SELECT regular.sku_id,price.list_price from ATG_CATFEED.google_feed_data_regular regular
	   INNER JOIN 
	   (select atgprice.sku_id,atgprice.list_price from ATG_CORE.dcs_price atgprice
		inner join (select dcsprice.sku_id,min(dcsprice.list_price) as list_price from ATG_CORE.dcs_price dcsprice group by sku_id)prc on prc.sku_id=atgprice.sku_id and prc.list_price=atgprice.list_price
		where atgprice.list_price-prc.list_price=0 and atgprice.price_list='mffListPrice')price
		on price.sku_id=regular.sku_id)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		  SET target.list_price=src.list_price;
	commit;
	
	
  end;
  
  procedure del_onsale_skus_noprice as
  begin
  
	delete from ATG_CATFEED.google_feed_data_onsale where list_price is NULL;
	commit;
  
  end;
  
  procedure del_regular_skus_noprice as
  begin
  
	delete from ATG_CATFEED.google_feed_data_regular where list_price is NULL;
	commit;
  
  end;
  
  procedure load_onsale_skus_inv as
  begin
	
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select onsale.sku_id,inventory.stock_level from ATG_CATFEED.google_feed_data_onsale onsale
		  INNER JOIN
		  ( select (inv.stock_level-invtra.sold-invtra.allocated-invtra.shipped)as stock_level,inv.catalog_ref_id from ATG_CORE.ff_inventory inv
		  	inner join ATG_CORE.ff_inventory_transaction invtra on invtra.inventory_id=inv.inventory_id
			where inv.catalog_ref_id in (select onsle.sku_id from ATG_CATFEED.google_feed_data_onsale onsle)
		  )inventory
		  on inventory.catalog_ref_id=onsale.sku_id
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
			SET target.stock_level=src.stock_level;
	commit;
  
	
  end;
  
  procedure load_regular_skus_inv as
  begin
	
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select regular.sku_id,inventory.stock_level from ATG_CATFEED.google_feed_data_regular regular
		  INNER JOIN
		  ( select (inv.stock_level-invtra.sold-invtra.allocated-invtra.shipped)as stock_level,inv.catalog_ref_id from ATG_CORE.ff_inventory inv
		  	inner join ATG_CORE.ff_inventory_transaction invtra on invtra.inventory_id=inv.inventory_id
			where inv.catalog_ref_id in (select reg.sku_id from ATG_CATFEED.google_feed_data_regular reg)
		  )inventory
		  on inventory.catalog_ref_id=regular.sku_id
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
			SET target.stock_level=src.stock_level;
	commit;
	
	
  end;
  
  procedure del_onsale_skus_noinv as
  begin
  
	delete from ATG_CATFEED.google_feed_data_onsale where stock_level is NULL;
	commit;
  
  end;
  
  procedure del_regular_skus_noinv as
  begin
  
	delete from ATG_CATFEED.google_feed_data_regular where stock_level is NULL;
	commit;
  
  end;
  
  procedure load_onsale_skus_prdinfo as
  begin
	
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select  onsale.sku_id,product.product_id,product.brand,product.display_name,product.selling_points,product.num_images,product.is_hide_price,product.description,product.is_ffl,product.minimum_age,product.fulfillment_method from ATG_CATFEED.google_feed_data_onsale onsale
		 INNER JOIN
		 (	select prdsku.product_id,gglefeed.sku_id,prd.brand,prd.display_name,mffprd.selling_points,mffprd.num_images,mffprd.is_hide_price,prd.description,mffprd.is_ffl,mffprd.minimum_age,mffprd.fulfillment_method from google_feed_data_onsale gglefeed
			inner join ATG_CATA.dcs_prd_chldsku prdsku on gglefeed.sku_id=prdsku.sku_id
			inner join ATG_CATA.dcs_product prd on prd.product_id=prdsku.product_id
			inner join ATG_CATA.mff_product mffprd on mffprd.product_id=prdsku.product_id
			where ((prd.start_date<=CURRENT_TIMESTAMP) AND (prd.end_date>=CURRENT_TIMESTAMP OR prd.end_date IS NULL) AND ((mffprd.teaser_startdate is null) OR (mffprd.teaser_startdate>CURRENT_TIMESTAMP) or (mffprd.teaser_enddate < CURRENT_TIMESTAMP)))
		 )product
	   on product.sku_id=onsale.sku_id
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.product_id=src.product_id,
			target.brand=src.brand,
			target.product_display_name=src.display_name,
			target.product_selling_points=src.selling_points,
			target.product_num_images=src.num_images,
			target.is_hide_price=src.is_hide_price,
			target.product_description=src.description,
			target.fulfillment_method=src.fulfillment_method;
	commit;
  
  end;
  
  procedure load_regular_skus_prdinfo as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select  regular.sku_id,product.product_id,product.brand,product.display_name,product.selling_points,product.num_images,product.is_hide_price,product.description,product.is_ffl,product.minimum_age,product.fulfillment_method from ATG_CATFEED.google_feed_data_regular regular
		 INNER JOIN
		 (	select prdsku.product_id,gglefeed.sku_id,prd.brand,prd.display_name,mffprd.selling_points,mffprd.num_images,mffprd.is_hide_price,prd.description,mffprd.is_ffl,mffprd.minimum_age,mffprd.fulfillment_method from google_feed_data_regular gglefeed
			inner join ATG_CATA.dcs_prd_chldsku prdsku on gglefeed.sku_id=prdsku.sku_id
			inner join ATG_CATA.dcs_product prd on prd.product_id=prdsku.product_id
			inner join ATG_CATA.mff_product mffprd on mffprd.product_id=prdsku.product_id
			where ((prd.start_date<=CURRENT_TIMESTAMP) AND (prd.end_date>=CURRENT_TIMESTAMP OR prd.end_date IS NULL) AND ((mffprd.teaser_startdate is null) OR (mffprd.teaser_startdate>CURRENT_TIMESTAMP) or (mffprd.teaser_enddate < CURRENT_TIMESTAMP)))
		 )product
	   on product.sku_id=regular.sku_id
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.product_id=src.product_id,
			target.brand=src.brand,
			target.product_display_name=src.display_name,
			target.product_selling_points=src.selling_points,
			target.product_num_images=src.num_images,
			target.is_hide_price=src.is_hide_price,
			target.product_description=src.description,
			target.fulfillment_method=src.fulfillment_method;
	commit;
  
  
  end;
  
  procedure del_onsale_skus_noprd as
  begin
  
	delete from ATG_CATFEED.google_feed_data_onsale where product_id is NULL;
	commit;
  
  end;
  
  procedure del_regular_skus_noprd as
  begin
  
	delete from ATG_CATFEED.google_feed_data_regular where product_id is NULL;
	commit;
  
  end;
  
  procedure load_onsale_skus_catinfo as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select onsale.sku_id,onsale.product_id,f_get_category_product(onsale.product_id) as category_id from ATG_CATFEED.google_feed_data_onsale onsale
		)src
		on (target.product_id=src.product_id AND target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.category_id=src.category_id;
	commit;
  
  end;
  
  procedure load_regular_skus_catinfo as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select regular.sku_id,regular.product_id,f_get_category_product(regular.product_id) as category_id from ATG_CATFEED.google_feed_data_regular regular
		)src
		on (target.product_id=src.product_id AND target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.category_id=src.category_id;
		
	commit;
  
  end;
  
  procedure del_onsale_skus_nocatinfo as
  begin
  
	delete from ATG_CATFEED.google_feed_data_onsale where category_id is NULL;
	commit;
  
  end;
  
  procedure del_regular_skus_nocatinfo as
  begin
  
	delete from ATG_CATFEED.google_feed_data_regular where category_id is NULL;
	commit;
  
  end;
  
  procedure load_onsale_skus_googleinfo as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select feed.category_id,feed.product_id,feed.sku_id,gglecatmap.google_category_id, gglecatmap.google_category_name from google_feed_data_onsale feed
			inner join ATG_CATA.google_mff_category_map gglecatmap on gglecatmap.mff_category_id=feed.category_id
		)src
		on (target.product_id=src.product_id AND target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.google_category_id=src.google_category_id,
			target.google_category_name=src.google_category_name;
			
	commit;
  
  end;
  
  procedure load_regular_skus_googleinfo as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select feed.category_id,feed.product_id,feed.sku_id,gglecatmap.google_category_id, gglecatmap.google_category_name from google_feed_data_regular feed
			inner join ATG_CATA.google_mff_category_map gglecatmap on gglecatmap.mff_category_id=feed.category_id
		)src
		on (target.product_id=src.product_id AND target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.google_category_id=src.google_category_id,
			target.google_category_name=src.google_category_name;
			
	commit;
  
  end;
  
  procedure del_onsale_skus_nogoogleinfo as
  begin
  
	delete from ATG_CATFEED.google_feed_data_onsale where google_category_name is NULL;
    commit;
  end;
  
  procedure del_regular_skus_nogoogleinfo as
  begin
  
	delete from ATG_CATFEED.google_feed_data_regular where google_category_name is NULL;
	commit;
  
  end;
  
  procedure load_onsale_skus_color as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select feed.sku_id,skudynprpty.prop_value as color from google_feed_data_onsale feed
			inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
			where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_Color'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.color=src.color;
	commit;
	
  end;
  
  procedure load_regular_skus_color as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select feed.sku_id,skudynprpty.prop_value as color from google_feed_data_regular feed
			inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
			where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_Color'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.color=src.color;
	commit;
	
  end;
  
  procedure load_onsale_skus_age_group as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select feed.sku_id,
		 CASE when lower(skudynprpty.prop_value) like '%big tall%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%men%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%misses%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%women%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%boys%' then 'kids'
		 when lower(skudynprpty.prop_value) like '%girls%' then 'kids'
		 when lower(skudynprpty.prop_value)='kids' then 'kids'
         when lower(skudynprpty.prop_value)='toddler (2T - 5T)' then 'kids'
         else NULL
         END as age_group
		 from google_feed_data_onsale feed
		 inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
		 where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_SizeRange'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.age_group=src.age_group;
	commit;
	
  end;
  
  procedure load_regular_skus_age_group as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select feed.sku_id,
		 case when lower(skudynprpty.prop_value) like '%big tall%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%men%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%misses%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%women%' then 'adult'
		 when lower(skudynprpty.prop_value) like '%boys%' then 'kids'
		 when lower(skudynprpty.prop_value) like '%girls%' then 'kids'
		 when lower(skudynprpty.prop_value)='kids' then 'kids'
         when lower(skudynprpty.prop_value)='toddler (2T - 5T)' then 'kids'
         else NULL
         END as age_group
		 from google_feed_data_regular feed
		 inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
		 where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_SizeRange'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.age_group=src.age_group;
	commit;
	
  end;
  
  procedure load_onsale_skus_gender as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select feed.sku_id,
		 case when lower(skudynprpty.prop_value) like '%big tall%' then 'male'
         when lower(skudynprpty.prop_value) like '%boys%' then 'male'
         when lower(skudynprpty.prop_value) like '%men%' then 'male'
         when lower(skudynprpty.prop_value) like '%girls%' then 'female'
         when lower(skudynprpty.prop_value) like '%misses%' then 'female'
         when lower(skudynprpty.prop_value) like '%women%' then 'female'
         when lower(skudynprpty.prop_value)='kids' then 'unisex'
         when lower(skudynprpty.prop_value)='toddler (2T - 5T)' then 'unisex'
         else NULL
         END AS gender
		 from google_feed_data_onsale feed
		 inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
		 where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_SizeRange'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.gender=src.gender;
	commit;
	
  end;
  
procedure load_regular_skus_gender as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select feed.sku_id,
		 case when lower(skudynprpty.prop_value) like '%big tall%' then 'male'
         when lower(skudynprpty.prop_value) like '%boys%' then 'male'
         when lower(skudynprpty.prop_value) like '%men%' then 'male'
         when lower(skudynprpty.prop_value) like '%girls%' then 'female'
         when lower(skudynprpty.prop_value) like '%misses%' then 'female'
         when lower(skudynprpty.prop_value) like '%women%' then 'female'
         when lower(skudynprpty.prop_value)='kids' then 'unisex'
         when lower(skudynprpty.prop_value)='toddler (2T - 5T)' then 'unisex'
         else NULL
         END AS gender
		 from google_feed_data_regular feed
		 inner join ATG_CORE.mff_sf_dyn_prop_map_str skudynprpty on skudynprpty.id=feed.sku_id
		 where skudynprpty.prop_name='/mff/facets/SearchFacet_skuSearchFacet_SizeRange'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.gender=src.gender;
	commit;
	
  end;
  
procedure load_onsale_skus_size as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select prdattr.product_id,skuattr.sku_id,prdattr.attribute_name,prdattr.attribute_value as variant,skuattr.attribute_value as variant_value
			from google_feed_data_onsale onsale
			inner join atg_cata.dcs_sku_attr skuattr on skuattr.sku_id=onsale.sku_id
			inner join atg_cata.mff_product_attr prdattr on prdattr.product_id=onsale.product_id
			where prdattr.attribute_name=skuattr.attribute_name and lower(prdattr.attribute_value)='size'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.sku_size=src.variant_value;
	commit;
	
  end;
  

procedure load_regular_skus_size as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select prdattr.product_id,skuattr.sku_id,prdattr.attribute_name,prdattr.attribute_value as variant,skuattr.attribute_value as variant_value
			from google_feed_data_regular regular
			inner join atg_cata.dcs_sku_attr skuattr on skuattr.sku_id=regular.sku_id
			inner join atg_cata.mff_product_attr prdattr on prdattr.product_id=regular.product_id
			where prdattr.attribute_name=skuattr.attribute_name and lower(prdattr.attribute_value)='size'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.sku_size=src.variant_value;
	commit;	
	
  end;
  
procedure load_regular_skus_color_attr as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_regular target
	USING
		(select prdattr.product_id,skuattr.sku_id,prdattr.attribute_name,prdattr.attribute_value as variant,skuattr.attribute_value as variant_value
			from google_feed_data_regular regular
			inner join atg_cata.dcs_sku_attr skuattr on skuattr.sku_id=regular.sku_id
			inner join atg_cata.mff_product_attr prdattr on prdattr.product_id=regular.product_id
			where prdattr.attribute_name=skuattr.attribute_name and lower(prdattr.attribute_value)='color'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.color=src.variant_value;
	commit;	
	
  end;
  
procedure load_onsale_skus_color_attr as
  begin
  
	MERGE INTO ATG_CATFEED.google_feed_data_onsale target
	USING
		(select prdattr.product_id,skuattr.sku_id,prdattr.attribute_name,prdattr.attribute_value as variant,skuattr.attribute_value as variant_value
			from google_feed_data_onsale onsale
			inner join atg_cata.dcs_sku_attr skuattr on skuattr.sku_id=onsale.sku_id
			inner join atg_cata.mff_product_attr prdattr on prdattr.product_id=onsale.product_id
			where prdattr.attribute_name=skuattr.attribute_name and lower(prdattr.attribute_value)='color'
		)src
		on (target.sku_id=src.sku_id)
		WHEN MATCHED THEN UPDATE
		SET target.color=src.variant_value;
	commit;
	
  end;
  

  
  
   
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('mff_google_feed_creator', 'Initializing package mff_google_feed_creator');
end mff_google_feed_creator;