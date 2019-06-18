create table mff_search_suggest (
    id    varchar2(40) not null,
    term varchar2(254) not null
	,constraint mff_srch_sug_p primary key (id)
  );
