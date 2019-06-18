drop table mff_content_items;
drop table mff_static_left_nav_rltd_links;
drop table mff_static_left_nav_links;
drop table mff_content;
drop table mff_static_left_nav;

create table mff_static_left_nav (
    id    varchar2(40) not null,
    title varchar2(254) not null    
	,constraint mff_stc_lft_nav_p primary key (id)
  );

create table mff_content (
    content_id   varchar2(40) not null,
    display_name varchar2(254),
	content_key number(38),
    page_url     varchar2(254),
	url_title varchar2(254),
    target       number(38),
    content_data clob,
	start_date timestamp(6),
	end_date timestamp(6)
	,constraint mff_content_p primary key (content_id)
  );

create table mff_static_left_nav_links (
    id           varchar2(40) not null,
    sequence_num integer not null,
    link_id      varchar2(40) not null
    ,constraint mff_stc_lftnav_lnk_p primary key (id, sequence_num)
	,constraint mff_stc_lftnav_lnk_f1 foreign key (id) references mff_static_left_nav (id)
	,constraint mff_stc_lftnav_lnk_f2 foreign key (link_id) references mff_content (content_id));
  
create table mff_static_left_nav_rltd_links (
    link_id         varchar2(40) not null,
    sequence_num    integer not null,
    related_link_id varchar2(254) not null
    ,constraint mff_lftnav_rltd_lnks_p primary key (link_id, sequence_num)
	,constraint mff_lftnav_rltd_lnks_f1 foreign key (link_id) references mff_content (content_id)
	,constraint mff_lftnav_rltd_lnks_f2 foreign key (related_link_id) references mff_content (content_id));
    
create table mff_content_items (
    content_id      varchar2(40) not null,
    sequence_num    integer not null,
    content_section varchar2(40) not null,
    primary key (content_id, sequence_num),
    foreign key (content_id) references mff_content (content_id),
    foreign key (content_section) references wcm_article (id)
  );