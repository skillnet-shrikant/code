alter TABLE MFF_ORDER add
(
  bopis_order          number(1,0)    DEFAULT 0,
  bopis_store          varchar2(10) 
);
