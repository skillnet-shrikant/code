CREATE TABLE mff_credit_card (
	id	varchar2(40)	NOT NULL,
	name_on_card	varchar2(255)	NOT NULL,
	cvv	varchar2(3)	NOT NULL,				
	CONSTRAINT MFF_credit_card_FK FOREIGN KEY (ID) REFERENCES DPS_CREDIT_CARD (ID)
);

