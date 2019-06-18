package com.mff.commerce.csr.order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.mff.constants.MFFConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class GenericeSQLDroplet extends DynamoServlet{
 
 private DataSource dataSource;
 private String sql;
 
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    int count = runCountSqlQuery();
   
    pRequest.setParameter("count", count);
    pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
  }
 
  private int runCountSqlQuery() {
    vlogDebug("Entering runSqlQuery : ");
    Connection conn = null;
    Statement stmt = null;
    int count = 0;
    try {
      conn = getDataSource().getConnection();
      stmt = conn.createStatement();

      ResultSet rset = stmt.executeQuery(getSql());
      rset.next();
      count = rset.getInt(1);
     
    } catch (SQLException e) {
      vlogError(e, "SQL Exception occurred while executing the following sql {0}", getSql());
    } finally {
      try {
        if (stmt != null) {
          stmt.close();
        }

        if (conn != null) {
          conn.close();
        }
      } catch (Throwable e) {
        vlogError(e, "Error occurred while closing the connection");
      }
    }

    vlogDebug("Exiting runSqlQuery : ");
    return count;
  }
  
  public DataSource getDataSource() {
    return dataSource;
  }
  
  public void setDataSource(DataSource pDataSource) {
    dataSource = pDataSource;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String pSql) {
    sql = pSql;
  }
  
  

}
