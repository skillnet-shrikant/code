alter table mff_gift_card_status add
(
	response_code     	  varchar(10),
	response_code_msg 	  varchar(40),
	gift_card_number   	  varchar(40),
	extended_account_number   varchar(40),
	lock_amount		  NUMBER(19,7),
	call_type		  varchar(40)
);
