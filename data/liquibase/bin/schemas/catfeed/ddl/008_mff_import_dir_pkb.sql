create or replace
package body mff_import_dir as

procedure get_dir_list(dir in varchar2) as
language java
	name 'MFFFeedDirUtils.getList( java.lang.String )';

procedure archive_feed(feed_name in varchar2) as
language java
	name 'MFFFeedDirUtils.archiveFeed( java.lang.String )';

procedure delete_feed(feed_name in varchar2) as
language java
  name 'MFFFeedDirUtils.deleteFeed( java.lang.String )';


begin
  dbms_output.put_line(' ');
end mff_import_dir;

