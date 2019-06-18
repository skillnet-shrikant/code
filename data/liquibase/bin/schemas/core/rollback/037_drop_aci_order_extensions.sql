alter table mff_order drop (
	is_fraud,
	aci_transaction_id
);

drop table mff_aci_order_info;

alter table mff_credit_card drop (
	token_number
);