drop table mff_content_items;
drop table mff_static_left_nav_rltd_links;
drop table mff_static_left_nav_links;
drop table mff_content;
drop table mff_static_left_nav;

create table mff_static_left_nav (
        id                      varchar(40)    not null,
        title                   varchar(254)    not null,
        primary key(id)
);

create table mff_content (
        content_id              varchar(40)    not null,
        content_key             integer null,
        display_name            varchar(254)    null,
        page_url                varchar(254)    null,
        url_title               varchar(254)    null,
        target                  integer null,
        start_date              timestamp       null,
        end_date                timestamp       null,
        primary key(content_id)
);

create table mff_static_left_nav_links (
        id                      varchar(40)    not null references mff_static_left_nav(id),
        sequence_num            integer not null,
        link_id                 varchar(254)    not null references mff_content(content_id),
        primary key(id, sequence_num)
);

create table mff_static_left_nav_rltd_links (
        link_id                 varchar(40)    not null references mff_content(content_id),
        sequence_num            integer not null,
        related_link_id         varchar(254)    not null references mff_content(content_id),
        primary key(link_id, sequence_num)
);

create table mff_content_items (
        content_id              varchar(40)    not null references mff_content(content_id),
        sequence_num            integer not null,
        content_section         varchar(40)    not null references wcm_article(id),
        primary key(content_id, sequence_num)
);