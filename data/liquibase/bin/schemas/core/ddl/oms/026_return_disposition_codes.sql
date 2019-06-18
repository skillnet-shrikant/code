delete from CSR_EXCH_ITEMS;
delete from MFF_EXCH_ITEM;
delete from CSR_EXCH_ITEM;
delete from csr_exch_item_disp;
insert into csr_exch_item_disp (ID,DESCRIPTION,UPD_INVENTORY) values ('returnToGoodStock','returnToGoodStock',0);
insert into csr_exch_item_disp (ID,DESCRIPTION,UPD_INVENTORY) values ('returnToScrap','returnToScrap',0);
commit;
