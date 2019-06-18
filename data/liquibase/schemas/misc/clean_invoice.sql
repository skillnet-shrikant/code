delete from mff_invoice_rel_inv_ship;
delete from mff_invoice_rel_inv_return;
delete from mff_invoice_rel_inv_appease;
delete from mff_invoice_rel_inv_carton;
delete from mff_invoice_rel_inv_payment;
delete from mff_invoice_rel_inv_aux;
delete from mff_invoice;

delete from mff_invoice_address;

delete from mff_invoice_appeasement;

delete from mff_invoice_auxilliary;

delete from mff_invoice_line_discount;

delete from mff_invoice_line_carton;

delete from mff_invoice_carton;

delete from mff_invoice_rel_pay_aux;
delete from mff_invoice_payment;

delete from mff_invoice_rel_line_aux;
delete from mff_invoice_rel_line_carton;
delete from mff_invoice_rel_line_disc;
delete from mff_invoice_rel_line_carton;
delete from mff_invoice_line;

delete from mff_invoice_line_shipped;

delete from mff_invoice_line_returned;

delete from mff_invoice_line_summary;

delete from mff_invoice_rel_ext_pay;
delete from mff_invoice_extract;

delete from mff_invoice_extract_summary;

delete from mff_invoice_extract_payment;

commit;

exit;