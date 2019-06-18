alter table tmp_prod_data add (
	brand		varchar2(254),
	template	varchar2(254),
	in_store_only	number(1),
	made_in_usa	number(1),
	eds_product	number(1),
	minimum_age	number,
	ffl_required 	number(1),
	selling_points	clob);
alter table tmp_prod_data drop(weight_description,dimension_description,parent_category,selling_point_1,selling_point_2);	
