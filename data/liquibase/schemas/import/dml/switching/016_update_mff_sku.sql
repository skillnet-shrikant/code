update mff_sku dest
set (dest.caliber_gauge,
  dest.restricted_locations)=
  (select caliber_gauge,restricted_locations from sku_updates src
  where src.sku_id=dest.sku_id)
where exists(
select 1 from sku_updates src
where dest.sku_id=src.sku_id);