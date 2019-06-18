alter table tmp_catg_data add (
	orig_parent_id varchar2(40),
	parent_catg_change number(1),
	parent_catg_is_new number(1),
	orig_parent_has_new_products number(1),
	parent_has_new_products number(1)
);