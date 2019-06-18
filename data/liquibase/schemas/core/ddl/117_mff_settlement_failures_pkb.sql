create or replace package body mff_settlement_failures as

  PKG_NAME constant varchar2(50) := 'mff_settlement_failures';
  
  -- main procedure that performs all tasks related to settlement failures
  procedure run as
  begin
  	-- Cleanup first in case we have remnants of a failed job lying around
  	cleanup_settlement_failures;
  	load_nosettlement_records;
    load_noauthcode_records;
  	commit;
  end;
  
  -- Cleanup tables before and/or after we're done with settlement failure records
  procedure cleanup_settlement_failures as
  begin
  	delete from mff_nosettlement_records;
    delete from mff_noauthcode_records;
  	commit;
  end;
  
   procedure load_nosettlement_records as
   begin
      execute immediate 'insert /*+ APPEND */ into mff_nosettlement_records (id,order_number,amount,submitted_date,state,last_extract_date) select stlmnt_fail_seq.nextval,invc.order_number,invc.amount,ordr.submitted_date,paygrp.state,invoice.last_extract_date from dcspp_order ordr inner join mff_order mffordr on mffordr.order_id=ordr.order_id inner join (select mi.order_number, sum(mip.amount) as amount from atg_oms.mff_invoice mi, atg_oms.mff_invoice_rel_inv_payment relp, atg_oms.mff_invoice_payment mip where trunc(last_extract_date) = trunc(sysdate-1) and mi.invoice_id=relp.invoice_id and relp.payment_id=mip.payment_id group by mi.order_number minus select os.order_number, sum(os.amount) as amount from atg_oms.oms_settlement os where trunc(os.settle_date) = trunc(sysdate - 1) and os.settlement_type=1 group by os.order_number)invc on mffordr.order_number=invc.order_number inner join dcspp_pay_group paygrp on paygrp.order_ref=ordr.order_id inner join mff_invoice invoice on invoice.order_number=invc.order_number where trunc(invoice.last_extract_date) = trunc(sysdate-1) and invc.amount > 0';
      commit;
   end;
   
   procedure load_noauthcode_records as
   begin
      execute immediate 'insert /*+ APPEND */ into mff_noauthcode_records(id,order_ref,amount_debited,state,auth_code,submitted_date,order_number) select stlmnt_fail_seq.nextval,paygrp.order_ref,amount_debited,state,auth_code,ordr.submitted_date,mffordr.order_number from dcspp_order ordr inner join mff_order mffordr on mffordr.order_id=ordr.order_id inner join (select dpp.order_ref,dpp.amount_debited, dpp.state,accs.auth_code from atg_oms.dcspp_pay_status dps, atg_oms.aci_credit_card_status accs, atg_oms.dcspp_debit_status dbs, atg_oms.dcspp_pay_group dpp where dps.status_id=accs.status_id and dps.trans_success=1 and accs.auth_code is null and dbs.payment_group_id=dpp.payment_group_id and dbs.debit_status=dps.status_id)paygrp on paygrp.order_ref=ordr.order_id';
      commit;
   end;
   
   end mff_settlement_failures;