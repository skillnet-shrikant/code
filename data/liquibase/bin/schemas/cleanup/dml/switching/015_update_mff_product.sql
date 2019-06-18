update mff_product dest
set (dest.is_choking_hazard,
  dest.SELLING_POINTS,
  dest.IS_IN_STORE_ONLY,
  dest.IS_MADE_IN_USA,
  dest.IS_EDS,
  dest.MINIMUM_AGE,
  dest.IS_FFL)=
  (select IS_CHOKING_HAZARD,SELLING_POINTS,IS_IN_STORE_ONLY,IS_MADE_IN_USA,IS_EDS,MINIMUM_AGE,IS_FFL from prod_updates src
  where src.product_id=dest.product_id)
where exists(
select 1 from prod_updates src
where dest.product_id=src.product_id);