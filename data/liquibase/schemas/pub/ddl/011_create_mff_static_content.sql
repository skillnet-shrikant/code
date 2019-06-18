create table mff_static_left_nav (
    id    			   varchar2(40) not null,
    title 			   varchar2(254) not null,
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
  
CREATE index mff_stc_lftnav_ws_idx on mff_static_left_nav (workspace_id);
CREATE index mff_stc_lftnav_ck_idx on mff_static_left_nav (checkin_date);

create table mff_content (
    content_id   varchar2(40) not null,
    display_name varchar2(254),
    page_url     varchar2(254),
    target       number(38),
    content_data clob,
    content_key 	number(38),
	url_title varchar2(254),
	start_date timestamp(6),
	end_date timestamp(6),
	asset_version      int           not null,
	workspace_id       varchar2(40)   not null,
	branch_id          varchar2(40)   not null,
	is_head            numeric(1)    not null,
	version_deleted    numeric(1)    not null,
	version_editable   numeric(1)    not null,
	pred_version       int           null,
	checkin_date       timestamp     null,
    primary key (content_id, asset_version)
  );
  
CREATE index mff_content_ws_idx on mff_content (workspace_id);
CREATE index mff_content_ck_idx on mff_content (checkin_date);

create table mff_static_left_nav_links (
    id           varchar2(40) not null,
    sequence_num number(*,0) not null,
    link_id      varchar2(254),
	asset_version   int   not null,
    primary key (id, sequence_num, asset_version)
    
  );

create table mff_static_left_nav_rltd_links (
    link_id         varchar2(40) not null,
    sequence_num    number(*,0) not null,
    related_link_id varchar2(40),
	asset_version   int   not null,
    primary key (link_id, sequence_num, asset_version)
    
  );

create table mff_content_items (
    content_id      varchar2(40) not null,
    sequence_num    number(*,0) not null,
    content_section varchar2(254) not null,
	asset_version   int   not null,
    primary key (content_id, sequence_num, asset_version)
  );

create table mff_tax_exmp_classification (
	id 				varchar2(40)	not null,
	display_name 	varchar2(254)	not null,
	status     		number(1) 		null,
	asset_version      int           not null,
	workspace_id       varchar2(40)   not null,
	branch_id          varchar2(40)   not null,
	is_head            numeric(1)    not null,
	version_deleted    numeric(1)    not null,
	version_editable   numeric(1)    not null,
	pred_version       int           null,
	checkin_date       timestamp     null,
	primary key(id, asset_version)
);

CREATE index mff_tax_emp_classif_ws_idx on mff_tax_exmp_classification (workspace_id);
CREATE index mff_tax_exp_classif_ck_idx on mff_tax_exmp_classification (checkin_date);