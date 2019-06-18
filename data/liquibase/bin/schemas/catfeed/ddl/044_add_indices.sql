create index tmp_sku_data_sc on tmp_sku_data(sku_change);
create index tmp_sku_data_se_sc_sid on tmp_sku_data(sku_exists,sku_change,sku_id);
create index tmp_prod_data_pe_phns_pid on tmp_prod_data(prod_exists,prod_has_new_skus,product_id);