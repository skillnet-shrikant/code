with item_ship as ( select mo.order_number,    case mo.bopis_order     when 0 then       case di.gwp          when 1 then           case di.catalog_ref_id              when '100799055' then '100257872'             when '100799198' then '100257872'             when '100799054' then '100257872'             when '100799197' then '100257872'             when '100799200' then '100257872'             when '100799199' then '100257872'             when '100799056' then '100257872'             when '100799202' then '100257872'             when '100799201' then '100257872'             when '100799203' then '100257872'             else di.catalog_ref_id           end          else         di.catalog_ref_id       end      else       di.catalog_ref_id   end   catalog_ref_id,    sum(mpi.quantity) quantity  from atg_oms.dcspp_item di, atg_oms.mff_item mi, atg_oms.mff_order mo, atg_oms.mff_return_items mri, atg_oms.mff_prorate_item mpi where to_date(to_char(mi.ship_date,'dd-MON-yy'),'dd-MON-yy') >= to_date(to_char(sysdate-2,'dd-MON-yy'),'dd-MON-yy')   and di.commerce_item_id=mi.commerce_item_id   and di.state='SHIPPED'   and mo.order_id=di.order_ref   and mri.prorate_item_id=mpi.prorate_item_id   and mri.commerce_item_id=di.commerce_item_id    group by mo.order_number, mo.bopis_order,di.gwp, di.catalog_ref_id ), inv_ship as ( select mi.order_number, mil.skucode, sum(mil.quantity) quantity from atg_oms.mff_invoice mi, atg_oms.mff_invoice_line mil, atg_oms.mff_invoice_rel_inv_ship mir,    atg_oms.mff_invoice_line_shipped mils where to_date(to_char(mi.order_date,'dd-MON-yy'),'dd-MON-yy') >= to_date(to_char(sysdate-2,'dd-MON-yy'),'dd-MON-yy')   and mi.invoice_id=mir.invoice_id   and mir.line_id=mil.line_id   and mils.line_id=mir.line_id   and mil.skucode != '008176158' group by mi.order_number, mil.skucode ) select item.order_number,    item.catalog_ref_id,    sum(item.quantity) quantity from item_ship item group by item.order_number, item.catalog_ref_id minus select * from inv_ship