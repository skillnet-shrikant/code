create table mff_order_price
(
  amount_info_id varchar2(40) not null,
  order_subtotal_less_giftcard number(19,2),
  constraint mff_order_price_pk primary key(amount_info_id),
  constraint mff_order_price_fk foreign key (amount_info_id) references  dcspp_amount_info(amount_info_id)
);