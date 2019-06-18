create table tmp_catg_from_xml (
cat_id varchar2(40),
description varchar2(4000),
parent_id varchar2(40),
cat_level number,
activation_date date,
deactivation_date date,
template_id varchar2(254),
batch_id varchar2(254));