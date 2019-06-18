update mff_product
set picker_template='PICKER'
where product_id in (select product_id from tmp_pick_prod);
update mff_product
set picker_template='TABLE'
where product_id in (select product_id from tmp_table_prod);