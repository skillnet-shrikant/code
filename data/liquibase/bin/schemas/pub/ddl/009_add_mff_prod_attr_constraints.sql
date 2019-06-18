alter table mff_product_attr add asset_version   number(19)      not null;
alter table mff_product_attr add constraint mff_product_attr_p primary key (product_id,attribute_name,asset_version);