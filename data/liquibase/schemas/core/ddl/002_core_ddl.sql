CREATE TABLE mff_tax_exemption (
	id	 			varchar2(40)	NOT NULL,
	nick_name 			varchar2(255)	NOT NULL,
	tax_classification 		varchar2(110)	NOT NULL,
	tax_id 				varchar2(110)	NOT NULL,
	organization_name 		varchar2(255)	NOT NULL,
	business_desc 			varchar2(255)	NOT NULL,
	mechandise	 		varchar2(255)	NOT NULL,
	tax_city	 		varchar2(40)	NOT NULL,
	tax_state	 		varchar2(40)	NOT NULL,				
	PRIMARY KEY(id)
);


CREATE TABLE mff_user_org_address (
	id	 			varchar2(40)	NOT NULL,
	org_addr_id 			varchar2(40)	NOT NULL,				
	PRIMARY KEY(id)
);

CREATE TABLE mff_tax_exemptions (
	id	 			varchar2(40)	NOT NULL,
	nick_name			VARCHAR2(42) NOT NULL,
	exemption_info_id 		varchar2(40)	NOT NULL,				
	constraint mff_tax_exemptions_p primary key (id,nick_name)
);