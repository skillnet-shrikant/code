alter table mff_order add tax_exemption varchar(100);

alter table mff_tax_exemption drop column tax_classification;

alter table mff_tax_exemption add tax_classification_id varchar2(100);
alter table mff_tax_exemption add tax_classification_name varchar2(100);