alter table tmp_sku_data add (
	ormd	number(1) default 0);
alter table tmp_sku_data drop(customize_max_char,sold_at,recurring_allowed,serial_tracking,customize,age_restriction,lot_tracking);	
