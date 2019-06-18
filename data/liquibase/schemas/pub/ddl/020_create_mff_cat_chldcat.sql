create table mff_cat_chldcat (
        sec_asset_version       number(19)      not null,
        asset_version   number(19)      not null,
        category_id     varchar2(40)    not null,
        sequence_num    integer not null,
        child_cat_id    varchar2(40)    not null
,constraint mff_cat_chldcat_p primary key (category_id,sequence_num,asset_version,sec_asset_version));

create index mff_cat_chldcat_cci_idx on mff_cat_chldcat (child_cat_id);
create index mff_cat_chldcat_cid_idx on mff_cat_chldcat (category_id);
