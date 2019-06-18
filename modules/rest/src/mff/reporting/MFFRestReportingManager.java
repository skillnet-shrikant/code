package mff.reporting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import atg.nucleus.GenericService;

/**
 * 
 * @author vsingh
 *
 */
public class MFFRestReportingManager extends GenericService {

  private DataSource dataSource;
  
  private MFFReportingQuery[] reportQueries;
  
  public Map<String,String> runReportingQueries(){

    vlogDebug("Entering runReportingQueries");
    Connection conn = null;
    Map<String,String> outputMap = new HashMap<String,String>();
    try {
        conn = getDataSource().getConnection();
        for (int i=0; i<getReportQueries().length; i++){
          MFFReportingQuery reportQuery = getReportQueries()[i];
          String responseJson = runReportQueries(conn, reportQuery);
          outputMap.put(reportQuery.getReportName(), responseJson);
        }
    } catch (SQLException se) {
        vlogError(se,"Error in runReportingQueries");
    } finally {
        DbUtils.closeQuietly(conn);
    }
    
    vlogDebug("Exiting runReportingQueries");  
    return outputMap;
  }
  
  private String runReportQueries(Connection pConnection, MFFReportingQuery pReportQuery) throws SQLException{
    List<Map<String, Object>> listOfMaps = null;
    String responseJson = "";
    QueryRunner queryRunner = new QueryRunner();
    listOfMaps = queryRunner.query(pConnection, pReportQuery.getSql(), new MapListHandler());
    
    Gson gson =  new GsonBuilder().disableHtmlEscaping().create();
    responseJson = gson.toJson(listOfMaps);
    vlogDebug("Exiting runReportQueries : ReportName - {0} responseJson - {1}",pReportQuery.getReportName(),responseJson);
    
    return responseJson;
  }
  
  public void testRunReportingQueries(){
    runReportingQueries();
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource pDataSource) {
    dataSource = pDataSource;
  }

  public MFFReportingQuery[] getReportQueries() {
    return reportQueries;
  }

  public void setReportQueries(MFFReportingQuery[] pReportQueries) {
    reportQueries = pReportQueries;
  }
}
