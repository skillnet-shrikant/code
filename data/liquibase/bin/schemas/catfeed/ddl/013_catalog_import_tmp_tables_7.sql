create table tmp_prod_catg_data (
	product_id	varchar2(40),  
	seq_num		number,  
	category_id	varchar2(254),
	primary_catg	number(1),
	prod_exists 	number(1),
	catg_exists	number(1)
);