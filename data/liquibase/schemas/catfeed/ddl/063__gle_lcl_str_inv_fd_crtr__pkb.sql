create or replace package body gle_lcl_str_inv_fd_crtr
as

  PKG_NAME constant varchar2(50) := 'gle_lcl_str_inv_fd_crtr';
  
  -- main procedure that performs all tasks related to google feed creator
  procedure run_full as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	clean_gle_str_inv_fd;
  	load_gle_str_inv_fd;
  end;
  
  procedure run_partial as
  begin
	load_onsale_skus_inv_par;
	load_regular_skus_inv_par;
  end;
  
  -- Cleanup tables before and/or after we're done with google feed creator
  procedure clean_gle_str_inv_fd as
  begin
  	execute immediate 'truncate table gle_lcl_st_inv_fd_onsale cascade';
    execute immediate 'truncate table gle_lcl_st_inv_fd_regular cascade';
  	commit;
  end;
  
  --load data into temp table
  procedure load_gle_str_inv_fd as
  begin
	load_onsale;
	load_regular;
	
  end;
  
   procedure load_onsale as
   begin
    load_onsale_skus_inv;
    del_onsale_skus_not_active;
    load_onsale_skus_price;
    load_onsale_skus_prdinfo;
    del_onsale_skus_noprd_pkup;
    commit;
   end;
   
   procedure load_regular as
   begin
    load_regular_skus_inv;
    del_regular_skus_not_active;
    load_regular_skus_price;
    load_regular_skus_prdinfo;
    del_regular_skus_noprd_pkup;
    commit;
  end;
   
   
  procedure load_onsale_skus_inv as
  begin
    execute immediate 'alter table gle_lcl_st_inv_fd_onsale nologging';
    execute immediate 'alter index gle_lclfd_onsale_strcd UNUSABLE';
    execute immediate 'alter index gle_lclfd_onsale_itemid UNUSABLE';
    execute immediate 'alter index gle_lclfd_onsale_prdid UNUSABLE';
    execute immediate 'alter session set skip_unusable_indexes=true' ;
    execute immediate 'insert /*+ APPEND */ into gle_lcl_st_inv_fd_onsale (inventory_id,store_code,item_id,quantity) select storeinv.inventory_id,storeinv.store_id,storeinv.catalog_ref_id,(case when storeinv.stock_level <0 then 0 else (storeinv.stock_level-storeinvtra.allocated-storeinvtra.shipped) END)as quantity from ATG_CORE.ff_store_inventory storeinv inner join ATG_CORE.ff_store_inv_transaction storeinvtra on storeinvtra.inventory_id=storeinv.inventory_id where storeinvtra.is_damaged=0';
    commit;
    execute immediate 'alter index gle_lclfd_onsale_strcd REBUILD NOLOGGING';
    execute immediate 'alter index gle_lclfd_onsale_itemid REBUILD NOLOGGING';
    execute immediate 'alter index gle_lclfd_onsale_prdid REBUILD NOLOGGING';
    execute immediate 'alter table gle_lcl_st_inv_fd_onsale LOGGING';
    execute immediate 'alter index gle_lclfd_onsale_strcd LOGGING';
    execute immediate 'alter index gle_lclfd_onsale_itemid LOGGING';
    execute immediate 'alter index gle_lclfd_onsale_prdid LOGGING';
  end;
  
  procedure load_regular_skus_inv as
  begin
    execute immediate 'alter table gle_lcl_st_inv_fd_regular nologging';
    execute immediate 'alter index gle_lclfd_regular_strcd UNUSABLE';
    execute immediate 'alter index gle_lclfd_regular_itemid UNUSABLE';
    execute immediate 'alter index gle_lclfd_regular_prdid UNUSABLE';
    execute immediate 'alter session set skip_unusable_indexes=true';
    execute immediate 'insert /*+ APPEND */ into gle_lcl_st_inv_fd_regular (inventory_id,store_code,item_id,quantity) select storeinv.inventory_id,storeinv.store_id,storeinv.catalog_ref_id,(case when storeinv.stock_level <0 then 0 else (storeinv.stock_level-storeinvtra.allocated-storeinvtra.shipped) END)as quantity from ATG_CORE.ff_store_inventory storeinv inner join ATG_CORE.ff_store_inv_transaction storeinvtra on storeinvtra.inventory_id=storeinv.inventory_id where storeinvtra.is_damaged=0';
    commit;
    execute immediate 'alter index gle_lclfd_regular_strcd REBUILD NOLOGGING';
    execute immediate 'alter index gle_lclfd_regular_itemid REBUILD NOLOGGING';
    execute immediate 'alter index gle_lclfd_regular_prdid REBUILD NOLOGGING';
    execute immediate 'alter table gle_lcl_st_inv_fd_regular LOGGING';
    execute immediate 'alter index gle_lclfd_regular_strcd LOGGING';
    execute immediate 'alter index gle_lclfd_regular_itemid LOGGING';
    execute immediate 'alter index gle_lclfd_regular_prdid LOGGING';	
  end;
  
  procedure del_onsale_skus_not_active as
  begin
	execute immediate 'create table onsale_tmp1 nologging as (select * from onsale_temp)';
	execute immediate 'insert /*+ Append */ into onsale_tmp1 select * from gle_lcl_st_inv_fd_onsale where gle_lcl_st_inv_fd_onsale.item_id in (select atgsku.sku_id from ATG_CATA.dcs_sku atgsku where atgsku.start_date<=CURRENT_TIMESTAMP AND (atgsku.end_date>=CURRENT_TIMESTAMP OR atgsku.end_date IS NULL))';
	execute immediate 'drop table gle_lcl_st_inv_fd_onsale purge';
	execute immediate 'alter table onsale_tmp1 rename to gle_lcl_st_inv_fd_onsale';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale add constraint lclstinvfdonsale_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale logging';
	execute immediate 'create index gle_lclfd_onsale_strcd on gle_lcl_st_inv_fd_onsale(store_code) logging';
	execute immediate 'create index gle_lclfd_onsale_itemid on gle_lcl_st_inv_fd_onsale(item_id) logging';
	execute immediate 'create index gle_lclfd_onsale_prdid on gle_lcl_st_inv_fd_onsale(product_id) logging';
	commit;
  end;
  
  procedure del_regular_skus_not_active as
  begin
	execute immediate 'create table regular_tmp1 nologging as (select * from regular_temp)';
	execute immediate 'insert /*+ Append */ into regular_tmp1 select * from gle_lcl_st_inv_fd_regular where gle_lcl_st_inv_fd_regular.item_id in (select atgsku.sku_id from ATG_CATA.dcs_sku atgsku where atgsku.start_date<=CURRENT_TIMESTAMP AND (atgsku.end_date>=CURRENT_TIMESTAMP OR atgsku.end_date IS NULL))';
	execute immediate 'drop table gle_lcl_st_inv_fd_regular purge';
	execute immediate 'alter table regular_tmp1 rename to gle_lcl_st_inv_fd_regular';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular add constraint lclstinvfdregular_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular logging';
	execute immediate 'create index gle_lclfd_regular_strcd on gle_lcl_st_inv_fd_regular(store_code) logging';
	execute immediate 'create index gle_lclfd_regular_itemid on gle_lcl_st_inv_fd_regular(item_id) logging';
	execute immediate 'create index gle_lclfd_regular_prdid on gle_lcl_st_inv_fd_regular(product_id) logging';
	commit;
  end;
  
  procedure load_onsale_skus_price as
  begin
  
  execute immediate 'create table onsale_tmp1 nologging as (select * from onsale_temp)';
	execute immediate 'insert /*+ Append */ into onsale_tmp1 (inventory_id,store_code,item_id,quantity,price) select onsale.inventory_id,onsale.store_code,onsale.item_id,onsale.quantity,(case when price.list_price>0 and price.list_price <1 then ''USD '' || ''0'' || price.list_price else ''USD '' || price.list_price end)as list_price from ATG_CATFEED.gle_lcl_st_inv_fd_onsale onsale INNER JOIN (select saleprice.list_price,saleprice.sku_id from (select atgprice.sku_id,atgprice.list_price,atgprice.price_list from ATG_CORE.dcs_price atgprice inner join (select dcsprice.sku_id,min(dcsprice.list_price) as list_price from ATG_CORE.dcs_price dcsprice group by sku_id)prc on prc.sku_id=atgprice.sku_id and prc.list_price=atgprice.list_price where atgprice.list_price-prc.list_price=0 and atgprice.price_list=''mffSalePrice'')saleprice where saleprice.sku_id not in  (select atgprice2.sku_id from ATG_CORE.dcs_price atgprice2 inner join (select dcsprice2.sku_id,min(dcsprice2.list_price) as list_price from ATG_CORE.dcs_price dcsprice2 group by dcsprice2.sku_id)prc2 on prc2.sku_id=atgprice2.sku_id and prc2.list_price=atgprice2.list_price where atgprice2.list_price-prc2.list_price=0 and atgprice2.price_list=''mffListPrice''))price on price.sku_id=onsale.item_id';
	execute immediate 'drop table gle_lcl_st_inv_fd_onsale purge';
	execute immediate 'alter table onsale_tmp1 rename to gle_lcl_st_inv_fd_onsale';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale add constraint lclstinvfdonsale_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale logging';
	execute immediate 'create index gle_lclfd_onsale_strcd on gle_lcl_st_inv_fd_onsale(store_code) logging';
	execute immediate 'create index gle_lclfd_onsale_itemid on gle_lcl_st_inv_fd_onsale(item_id) logging';
	execute immediate 'create index gle_lclfd_onsale_prdid on gle_lcl_st_inv_fd_onsale(product_id) logging';
  commit;

end;
  
  procedure load_regular_skus_price as
  begin
    execute immediate 'create table regular_tmp1 nologging as (select * from regular_temp)';
    execute immediate 'insert /*+ Append */ into regular_tmp1 (inventory_id,store_code,item_id,quantity,price) select regular.inventory_id,regular.store_code,regular.item_id,regular.quantity,(case when price.list_price>0 and price.list_price <1 then ''USD '' || ''0'' || price.list_price else ''USD '' || price.list_price end)as list_price from ATG_CATFEED.gle_lcl_st_inv_fd_regular regular inner join (select atgprice.sku_id,atgprice.list_price from ATG_CORE.dcs_price atgprice inner join (select dcsprice.sku_id,min(dcsprice.list_price) as list_price from ATG_CORE.dcs_price dcsprice group by sku_id)prc on prc.sku_id=atgprice.sku_id and prc.list_price=atgprice.list_price where atgprice.list_price-prc.list_price=0 and atgprice.price_list=''mffListPrice'')price on price.sku_id=regular.item_id';
    execute immediate 'drop table gle_lcl_st_inv_fd_regular purge';
    execute immediate 'alter table regular_tmp1 rename to gle_lcl_st_inv_fd_regular';
    execute immediate 'alter table gle_lcl_st_inv_fd_regular add constraint lclstinvfdregular_pk PRIMARY KEY (inventory_id)';
    execute immediate 'alter table gle_lcl_st_inv_fd_regular logging';
    execute immediate 'create index gle_lclfd_regular_strcd on gle_lcl_st_inv_fd_regular(store_code) logging';
    execute immediate 'create index gle_lclfd_regular_itemid on gle_lcl_st_inv_fd_regular(item_id) logging';
    execute immediate 'create index gle_lclfd_regular_prdid on gle_lcl_st_inv_fd_regular(product_id) logging';
    commit;
 end;

  procedure load_onsale_skus_prdinfo as
  begin
	
	MERGE /*+ APPEND */ INTO ATG_CATFEED.gle_lcl_st_inv_fd_onsale target
	USING
		(select gglefeed.inventory_id,prdsku.product_id,gglefeed.item_id,lower(regexp_replace(regexp_replace(replace(replace(replace(replace(replace(trim(prd.display_name),'  ',' '),'''',''),' ','-'),'.',''),'--','-'),'[^0-9A-Za-z]','-'),'-+','-')) as display_name from gle_lcl_st_inv_fd_onsale gglefeed
		 	inner join ATG_CATA.dcs_prd_chldsku prdsku on gglefeed.item_id=prdsku.sku_id
			inner join ATG_CATA.dcs_product prd on prd.product_id=prdsku.product_id
			inner join ATG_CATA.mff_product mffprd on mffprd.product_id=prdsku.product_id
			where ((prd.start_date<=CURRENT_TIMESTAMP) AND (prd.end_date>=CURRENT_TIMESTAMP OR prd.end_date IS NULL) AND ((mffprd.teaser_startdate is null) OR (mffprd.teaser_startdate>CURRENT_TIMESTAMP) or (mffprd.teaser_enddate < CURRENT_TIMESTAMP)))
		)src
		on (target.inventory_id=src.inventory_id)
		WHEN MATCHED THEN UPDATE
		SET target.product_id=src.product_id,
			target.product_display_name=src.display_name;
	commit;
  
  end;
  
  procedure load_regular_skus_prdinfo as
  begin
	
	MERGE /*+ APPEND */ INTO ATG_CATFEED.gle_lcl_st_inv_fd_regular target
	USING
		(select gglefeed.inventory_id,prdsku.product_id,gglefeed.item_id,lower(regexp_replace(regexp_replace(replace(replace(replace(replace(replace(trim(prd.display_name),'  ',' '),'''',''),' ','-'),'.',''),'--','-'),'[^0-9A-Za-z]','-'),'-+','-')) as display_name from gle_lcl_st_inv_fd_regular gglefeed
		 	inner join ATG_CATA.dcs_prd_chldsku prdsku on gglefeed.item_id=prdsku.sku_id
			inner join ATG_CATA.dcs_product prd on prd.product_id=prdsku.product_id
			inner join ATG_CATA.mff_product mffprd on mffprd.product_id=prdsku.product_id
			where ((prd.start_date<=CURRENT_TIMESTAMP) AND (prd.end_date>=CURRENT_TIMESTAMP OR prd.end_date IS NULL) AND ((mffprd.teaser_startdate is null) OR (mffprd.teaser_startdate>CURRENT_TIMESTAMP) or (mffprd.teaser_enddate < CURRENT_TIMESTAMP)))
		)src
		on (target.inventory_id=src.inventory_id)
		WHEN MATCHED THEN UPDATE
		SET target.product_id=src.product_id,
			target.product_display_name=src.display_name;
	commit;
  
  end;
  
  
  procedure del_onsale_skus_noprd_pkup as
  begin
	execute immediate 'create table onsale_tmp1 nologging as (select * from onsale_temp)';
	execute immediate 'insert /*+ Append */ into onsale_tmp1 (inventory_id,store_code,item_id,price,quantity,pickup_method,product_display_name,product_id,pickup_sla,pickup_link_template) select org.inventory_id,org.store_code,org.item_id,org.price,org.quantity,''buy'',org.product_display_name,org.product_id,''same day'',(''https://www.fleetfarm.com/store/detail/'' || org.product_display_name || ''/'' || org.product_id || ''/'' || org.store_code) from gle_lcl_st_inv_fd_onsale org where org.product_id is not null and org.product_display_name is not null';
	execute immediate 'drop table gle_lcl_st_inv_fd_onsale purge';
	execute immediate 'alter table onsale_tmp1 rename to gle_lcl_st_inv_fd_onsale';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale add constraint lclstinvfdonsale_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale logging';
	execute immediate 'create index gle_lclfd_onsale_strcd on gle_lcl_st_inv_fd_onsale(store_code) logging';
	execute immediate 'create index gle_lclfd_onsale_itemid on gle_lcl_st_inv_fd_onsale(item_id) logging';
	execute immediate 'create index gle_lclfd_onsale_prdid on gle_lcl_st_inv_fd_onsale(product_id) logging';
	commit;
  end;
  
  procedure del_regular_skus_noprd_pkup as
  begin
	execute immediate 'create table regular_tmp1 nologging as (select * from regular_temp)';
	execute immediate 'insert /*+ Append */ into regular_tmp1 (inventory_id,store_code,item_id,price,quantity,pickup_method,product_display_name,product_id,pickup_sla,pickup_link_template) select org.inventory_id,org.store_code,org.item_id,org.price,org.quantity,''buy'',org.product_display_name,org.product_id,''same day'',(''https://www.fleetfarm.com/store/detail'' || org.product_display_name || ''/'' || org.product_id || ''/'' || org.store_code) from gle_lcl_st_inv_fd_regular org where org.product_id is not null and org.product_display_name is not null';
	execute immediate 'drop table gle_lcl_st_inv_fd_regular purge';
	execute immediate 'alter table regular_tmp1 rename to gle_lcl_st_inv_fd_regular';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular add constraint lclstinvfdregular_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular logging';
	execute immediate 'create index gle_lclfd_regular_strcd on gle_lcl_st_inv_fd_regular(store_code) logging';
	execute immediate 'create index gle_lclfd_regular_itemid on gle_lcl_st_inv_fd_regular(item_id) logging';
	execute immediate 'create index gle_lclfd_regular_prdid on gle_lcl_st_inv_fd_regular(product_id) logging';
	commit;
  end;
  
  procedure load_onsale_skus_inv_par as
  begin
	
	MERGE /*+ Append */ INTO ATG_CATFEED.gle_lcl_st_inv_fd_onsale target
	USING
		( select onsale.inventory_id,onsale.item_id,(case when inv.stock_level <0 then 0 else (inv.stock_level-invtra.allocated-invtra.shipped) END)as stock_level from ATG_CATFEED.gle_lcl_st_inv_fd_onsale onsale
		  inner join ATG_CORE.ff_store_inventory inv on inv.inventory_id=onsale.inventory_id
		  inner join ATG_CORE.ff_store_inv_transaction invtra on invtra.inventory_id=onsale.inventory_id
		  where invtra.is_damaged=0)src
		on (target.inventory_id=src.inventory_id)
		WHEN MATCHED THEN UPDATE
			SET target.quantity_par=src.stock_level;
	commit;
 
  end;
  
  procedure load_regular_skus_inv_par as
  begin
	
	MERGE /*+ Append */ INTO ATG_CATFEED.gle_lcl_st_inv_fd_regular target
	USING
		( select regular.inventory_id,regular.item_id,(case when inv.stock_level <0 then 0 else (inv.stock_level-invtra.allocated-invtra.shipped) END)as stock_level from ATG_CATFEED.gle_lcl_st_inv_fd_regular regular
		  inner join ATG_CORE.ff_store_inventory inv on inv.inventory_id=regular.inventory_id
		  inner join ATG_CORE.ff_store_inv_transaction invtra on invtra.inventory_id=regular.inventory_id
		  where invtra.is_damaged=0)src
		on (target.inventory_id=src.inventory_id)
		WHEN MATCHED THEN UPDATE
			SET target.quantity_par=src.stock_level;
	commit;
		
  end;
  
 procedure merge_regular_inv as
  begin
	execute immediate 'create table regular_tmp1 nologging as (select * from regular_temp)';
	execute immediate 'insert /*+ Append */ into regular_tmp1 (inventory_id,store_code,item_id,price,quantity,pickup_method,product_display_name,product_id,pickup_sla,pickup_link_template,quantity_par) select org.inventory_id,org.store_code,org.item_id,org.price,org.quantity_par,org.pickup_method,org.product_display_name,org.product_id,org.pickup_sla,org.pickup_link_template,org.quantity_par from gle_lcl_st_inv_fd_regular org';
	execute immediate 'drop table gle_lcl_st_inv_fd_regular purge';
	execute immediate 'alter table regular_tmp1 rename to gle_lcl_st_inv_fd_regular';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular add constraint lclstinvfdregular_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_regular logging';
	execute immediate 'create index gle_lclfd_regular_strcd on gle_lcl_st_inv_fd_regular(store_code) logging';
	execute immediate 'create index gle_lclfd_regular_itemid on gle_lcl_st_inv_fd_regular(item_id) logging';
	execute immediate 'create index gle_lclfd_regular_prdid on gle_lcl_st_inv_fd_regular(product_id) logging';
	commit;
  end;
  
 procedure merge_onsale_inv as
  begin
	execute immediate 'create table onsale_tmp1 nologging as (select * from onsale_temp)';
	execute immediate 'insert /*+ Append */ into onsale_tmp1 (inventory_id,store_code,item_id,price,quantity,pickup_method,product_display_name,product_id,pickup_sla,pickup_link_template,quantity_par) select org.inventory_id,org.store_code,org.item_id,org.price,org.quantity_par,org.pickup_method,org.product_display_name,org.product_id,org.pickup_sla,org.pickup_link_template,org.quantity_par from gle_lcl_st_inv_fd_onsale org';
	execute immediate 'drop table gle_lcl_st_inv_fd_onsale purge';
	execute immediate 'alter table onsale_tmp1 rename to gle_lcl_st_inv_fd_onsale';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale add constraint lclstinvfdonsale_pk PRIMARY KEY (inventory_id)';
	execute immediate 'alter table gle_lcl_st_inv_fd_onsale logging';
	execute immediate 'create index gle_lclfd_onsale_strcd on gle_lcl_st_inv_fd_onsale(store_code) logging';
	execute immediate 'create index gle_lclfd_onsale_itemid on gle_lcl_st_inv_fd_onsale(item_id) logging';
	execute immediate 'create index gle_lclfd_onsale_prdid on gle_lcl_st_inv_fd_onsale(product_id) logging';
	commit;
  end;
  
 procedure merge_inv as
  begin
	merge_onsale_inv;
	merge_regular_inv;
	commit;
  end;
     
--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  mff_logger.log_sp_info('gle_lcl_str_inv_fd_crtr', 'Initializing package gle_lcl_str_inv_fd_crtr');
end gle_lcl_str_inv_fd_crtr;