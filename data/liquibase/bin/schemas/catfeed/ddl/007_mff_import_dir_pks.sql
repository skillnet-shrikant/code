create or replace package mff_import_dir as
  procedure get_dir_list(dir in varchar2);
  procedure archive_feed(feed_name in varchar2);
  procedure delete_feed(feed_name in varchar2);
end mff_import_dir;
