alter table mff_order drop column tax_exemption;

alter table mff_order add tax_exemption_code varchar2(40);

alter table mff_tax_exemption add tax_classification_code varchar2(40);