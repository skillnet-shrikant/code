alter table mff_inventory modify (shipped number(19) default 0, allocated number(19) default 0, sold number(19) default 0);
alter table mff_store_inventory modify (shipped number(19) default 0, allocated number(19) default 0);