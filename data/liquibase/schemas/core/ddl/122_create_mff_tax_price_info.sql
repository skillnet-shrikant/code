create table mff_tax_price_info
(
  amount_info_id    varchar2(40) not null,
  county_tax_info   varchar2(40),
  constraint mff_tax_price_info_pk primary key(amount_info_id),
  constraint mff_tax_price_info_fk foreign key (amount_info_id) references  dcspp_amount_info(amount_info_id)
);