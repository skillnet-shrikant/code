select sku_id, count(*) from atg_cata.dcs_prd_chldsku
group by sku_id
having count(*) > 1