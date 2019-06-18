alter table CSR_APPEASEMENT_APPROVAL	modify
(
  CUSTOMER_EMAIL	varchar2(255)
);

alter table CSR_ORDER_APPROVAL	modify
(
  CUSTOMER_EMAIL	varchar2(255)
);

alter TABLE DPS_USER	 MODIFY
(
	LOGIN varchar2(255)
);	