alter table mff_tax_exemptions drop constraint mff_tax_exemptions_p;
alter table  mff_tax_exemptions add constraint mff_tax_exemptions_p primary key (id,nick_name,exemption_info_id);