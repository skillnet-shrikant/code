merge into dcs_product dest
using (
	select product_id,brand,romance_copy from prod_updates
) src
on(src.product_id=dest.product_id)
when matched then
  update set dest.brand=src.brand,
  	dest.long_description=src.romance_copy;
  