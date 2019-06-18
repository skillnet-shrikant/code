alter table mff_product add (
	selling_points clob null,
	is_in_store_only number(1),
	is_made_in_usa number(1),
	is_eds number(1),
	is_choking_hazard number(1),
	minimum_age integer,
	is_ffl number(1)
);