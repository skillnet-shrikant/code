select p.product_id, p.start_date, p.end_date, s.sku_id, s.start_date, s.end_date, pr.price_list, pr.list_price 
from ATG_CATA.dcs_prd_chldsku ps, atg_cata.dcs_product p, atg_cata.dcs_sku s, atg_core.dcs_price pr
where ps.sku_id=s.sku_id
and ps.product_id=p.product_id
and (
  (p.start_date < sysdate or p.start_date is null) and
  (p.end_date > sysdate or p.end_date is null) 
)
and (
  (s.start_date < sysdate or s.start_date is null) and 
  (s.end_date > sysdate or s.end_date is null) 
)
and ps.sku_id in (select distinct sku_id from atg_core.dcs_price where list_price < .01)
and pr.sku_id=s.sku_id
and p.product_id not in ('0000000257332','0000000257333')
order by p.product_id