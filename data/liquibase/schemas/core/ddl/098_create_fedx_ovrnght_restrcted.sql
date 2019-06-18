create table fedx_ovrnght_restrcted (
	zip_code char(5 byte) NOT NULL, 
	city varchar2(35 byte),
	state char(2 byte),
	constraint fedx_ovrnght_restrcted_p primary key (zip_code)
   );