alter table mff_gift_card_status drop
(
	response_code,
	response_code_msg,
	gift_card_number,
	extended_account_number,
	lock_amount,
	call_type
);
