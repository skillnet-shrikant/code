
-- Run this in the CORE and OMS Schema, if you want to clean both
-- This sql needs to be run if there is data in these tables before the AES encryption starts

delete from MFF_EXCH_PRORATE_ITEMS;
delete from CSR_EXCH_IPROMOS;
delete from ACI_CREDIT_CARD;
delete from ACI_CREDIT_CARD_STATUS;
delete from MFF_GIFT_CARD_STATUS;
delete from MFF_GIFT_CARD;
delete from DCSPP_STORE_CRED;
delete from CSR_NONRETURN_ADJ;
delete from MFF_EXCH_ITEM;
delete from ACI_ORDER;

--Custom Tables of MFF

delete from mff_itemprev_allocations;
delete from mff_return_items;
delete from mff_prorate_item;
delete from mff_ship_group;
delete from mff_order_price;
delete from MFF_TAX_PRICE_INFO;
delete from mff_item_price;
delete from oms_pg_gc_settlement;
delete from oms_pg_settlement_rel;
delete from oms_pg_gc_settlement_rel;
delete from oms_pg_settlement;


delete from mff_item;
delete from mff_order;


delete from CSR_PROMO_ADJUST;
delete from CSR_RETURN_FEE;
delete from CSR_EXCH_ITEMS;
delete from CSR_EXCH_ITEM;

delete from CSR_EXCH_METHODS;
delete from CSR_EXCH_OPROMOS;
delete from CSR_EXCH;
delete from OMS_PG_SETTLEMENT_REL;

delete from MFF_ORDER_COUPONS;
delete from DCSPP_GWP_ITEM_MARKERS;
delete from DCS_GWP_ORDER_MARKERS;
delete from DCSPP_PAYITEM_REL;
delete from dcspp_sg_hand_inst;
delete from dcspp_ele_ship_grp;
delete from dcspp_ship_inst;
delete from dcspp_hrd_ship_grp;
delete from dcspp_ship_addr;
delete from dcspp_ship_group;
delete from dcspp_pay_inst;
delete from dcspp_auth_status;
delete from dcspp_debit_status;
delete from dcspp_cred_status;
delete from dcspp_credit_card;
delete from dcspp_bill_addr;
delete from dcspp_pay_group;
delete from DCSPP_GC_STATUS;
delete from dcspp_cc_status;
delete from dcspp_pay_status; 
delete from dcspp_price_adjust;
delete from dcspp_shipitem_rel;
delete from dcspp_payorder_rel;
delete from dcspp_payship_rel;
delete from dcspp_relationship;
delete from dcspp_order_price;
delete from dcspp_shipitem_sub;
delete from dcspp_taxshipitem;
delete from dcspp_ntaxshipitem;
delete from dcspp_amtinfo_adj;
delete from dcspp_tax_price;
delete from dcspp_shipitem_tax;
delete from dcspp_item_price;
delete from dcspp_itmprice_det;
delete from dcspp_ship_price;
delete from dcspp_amount_info;
delete from dcspp_item;
delete from dcspp_order_sg;
delete from dcspp_order_pg;
delete from dcspp_order_item;
delete from dcspp_order_rel;
delete from dcspp_order_adj;
delete from dcspp_gift_cert;
delete from dcspp_order;

delete from OMS_SETTLEMENT;

delete from mff_store_allocation;

delete from mff_allocation_log;

DELETE FROM srch_order_tokens;

DELETE FROM srch_order_modified;