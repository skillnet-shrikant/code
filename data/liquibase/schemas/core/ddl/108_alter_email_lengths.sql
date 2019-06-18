alter TABLE MFF_ORDER MODIFY
(
  bopis_email	varchar2(255),
  contact_email	varchar2(255)
);

alter table mff_invoice_address MODIFY
(
	email	varchar(255)
);

alter TABLE migration_log MODIFY
(
	email varchar2(255)
);	

alter TABLE DPS_USER	 MODIFY
(
	LOGIN varchar2(255)
);	