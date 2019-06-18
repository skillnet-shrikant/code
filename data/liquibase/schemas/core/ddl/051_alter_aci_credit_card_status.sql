alter table aci_credit_card_status add
(
	response_code     	  varchar(10),
	response_code_msg 	  varchar(40),
	cvv_verification_code varchar(10),
	call_type		  varchar(40)
	
);

