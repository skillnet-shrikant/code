DECLARE
v_command VARCHAR2(32767);
BEGIN
v_command :='create or replace
and compile java source named "MFFFeedDirUtils" as
import java.io.*;
import java.sql.*;
import java.util.*;
public class MFFFeedDirUtils {
  public static void getList(String directory) throws SQLException {
    File path = new File( directory );
    File[] list = path.listFiles();
    ArrayList fileList = new ArrayList();
    for ( int idx = 0; idx < list.length; idx++) {
    	if ( list[idx].getName().endsWith("xml") || list[idx].getName().endsWith("dat")) {
	    	fileList.add(list[idx]);
	    }
    }
    
	Collections.sort(fileList, new Comparator(){
		public int compare( Object f1, Object f2 ){
			if ( ( ( File ) f1 ).lastModified() < ( ( File ) f2 ).lastModified() ) {
				return -1;
			}
			else if ( ( ( File ) f1 ).lastModified() > ( ( File ) f2 ).lastModified() ) {
				return 1;
			}
			else {
				return 0;
			}
		}
	});

    String element;
    for( int idx = 0; idx < fileList.size(); idx++) {
    	File feed = (File)fileList.get(idx);
        element = feed.getName();
        #sql { INSERT INTO tmp_xml_filenames (filename, sequence_num)
               VALUES (:element, :idx) };
    }
  }
  
  public static void archiveFeed(String feedPath) throws IOException {
    File feed = new File(feedPath);
    String archiveDirPath = feed.getParent() + "/archive";
    File archiveDir = new File(archiveDirPath);
    if ( !archiveDir.exists()) {
    	archiveDir.mkdir();
    }
	  String archiveFeedPath = archiveDir.getAbsolutePath() + "/" + feed.getName();
	  File archiveFeedFile = new File(archiveFeedPath);
	  if (archiveFeedFile.exists()) {
	    throw new IOException("cannot archive feed file: " + feedPath + " to " + archiveFeedPath + " : archive file exists");
	  } else {
	    feed.renameTo(archiveFeedFile);
	  }
  }
  
  public static void deleteFeed(String feedPath) throws IOException {
    File feed = new File(feedPath);
    feed.delete();
  }
  
}';
EXECUTE IMMEDIATE v_command;
END;