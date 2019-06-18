create table google_mff_category_map (
	id varchar(40) not null,
	mff_category_id varchar(40),
	mff_category_name varchar(244),
	google_category_id varchar(40),
	google_category_name varchar(244),
	ASSET_VERSION number(19) NOT NULL,
	BRANCH_ID varchar2(40) NOT NULL,
	workspace_id varchar2(40) NOT NULL,
	is_head number(1,0) NOT NULL,
	version_deleted number(1,0) NOT NULL,
	version_editable number(1,0) NOT NULL,
	pred_version number(19,0),
	checkin_date timestamp(6),
constraint google_mff_category_map_pk primary key (id,ASSET_VERSION));

create index gglmffcatmap_IDX1 on google_mff_category_map(mff_category_id);
create index gglmffcatmap_IDX2 on google_mff_category_map(google_category_id);
create index gglmffcatmap_IDX3 on google_mff_category_map(workspace_id);
create index gglmffcatmap_IDX4 on google_mff_category_map(checkin_date);