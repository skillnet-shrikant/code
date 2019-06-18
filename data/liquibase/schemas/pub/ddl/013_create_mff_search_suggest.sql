create table mff_search_suggest (
    id    			   varchar2(40) not null,
    term 			   varchar2(254) not null,
	asset_version      int           not null,
	workspace_id       varchar2(40)   not null,
	branch_id          varchar2(40)   not null,
	is_head            numeric(1)    not null,
	version_deleted    numeric(1)    not null,
	version_editable   numeric(1)    not null,
	pred_version       int           null,
	checkin_date       timestamp     null,
    primary key (id, asset_version)
  );

CREATE index mff_srch_sug_ws_idx on mff_search_suggest (workspace_id);
CREATE index mff_srch_sug_ck_idx on mff_search_suggest (checkin_date);

