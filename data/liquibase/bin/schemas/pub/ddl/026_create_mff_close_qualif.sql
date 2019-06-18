create table mff_close_qualif (
  close_qualif_id    varchar2(40) not null,
  upsell_instructions 	varchar2(254),
  asset_version   int   not null,
  constraint mff_close_qualif_p primary key(close_qualif_id,asset_version)
);