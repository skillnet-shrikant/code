alter table mff_order add (
	is_fraud 	number(1,0)	DEFAULT 0,
	aci_transaction_id	varchar2(40)
);

create table mff_aci_order_info (
    mff_order_id       varchar2(40),
    aci_order_id   varchar2(40)
  );
  
alter table mff_credit_card drop (
	CVV
);

alter table mff_credit_card add (
	token_number	varchar2(40)
);