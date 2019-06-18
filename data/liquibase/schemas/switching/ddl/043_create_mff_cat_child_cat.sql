create table mff_cat_chldcat (
        category_id     varchar2(40)    not null,
        sequence_num    integer not null,
        child_cat_id    varchar2(40)    not null
,constraint mff_cat_chldcat_p primary key (category_id,sequence_num)
,constraint mff_cat_chldcat_ct_f foreign key (child_cat_id) references dcs_category (category_id)
,constraint mff_cat_chldcat_d_f foreign key (category_id) references dcs_category (category_id));

create index mff_cat_chldcat_cci_idx on mff_cat_chldcat (child_cat_id);
create index mff_cat_chldcat_cid_idx on mff_cat_chldcat (category_id);
