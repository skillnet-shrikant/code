CREATE INDEX FF_INV_TRAN_SHP_IDX ON ff_inventory_transaction (shipped);
CREATE INDEX FF_ST_INV_TRAN_SHP_IDX ON ff_store_inv_transaction (shipped);
CREATE INDEX FF_ST_INV_IDSTKDMG_IDX ON ff_store_inventory (inventory_id, stock_level, is_damaged);