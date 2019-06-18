alter table MFF_SEARCH_SUGGEST add (
		weight NUMBER default 100,
		enabled NUMBER(1) default 1
		);