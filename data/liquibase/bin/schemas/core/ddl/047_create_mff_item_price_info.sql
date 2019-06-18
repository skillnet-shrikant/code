create table mff_item_price
(
  amount_info_id varchar2(40) not null,
  prorated_discount_price number(19,2),
  sale_price_list_id varchar2(40 byte),
  constraint mff_item_price_pk primary key(amount_info_id),
  constraint mff_item_price_fk foreign key (amount_info_id) references  dcspp_amount_info(amount_info_id)
);