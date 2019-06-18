alter table order_item add (
    is_force_allocate number(1) default 0,
    is_gift_card number(1) default 0
);