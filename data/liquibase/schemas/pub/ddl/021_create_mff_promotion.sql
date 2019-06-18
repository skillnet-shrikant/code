create table mff_promotion (
  promotion_id    varchar2(40) not null,
  short_description 	varchar2(254),
  asset_version   int   not null,
  constraint mff_promotion_p primary key(promotion_id,asset_version)
);