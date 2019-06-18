alter table mff_gift_card add
(
	gc_ean	        varchar(256) DEFAULT NULL,
	local_lockId	varchar(5) DEFAULT NULL, 
	amount	        number(19,7) DEFAULT NULL
);

