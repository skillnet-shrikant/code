alter table mff_product drop (is_eds,is_choking_hazard);
alter table mff_product add choking_hazard varchar2(254);