package com.mff.util.alerts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mff.util.SMTPEmailUtil;

import oms.commerce.jobs.OMSSingletonScheduleTask;

public class GenericSQLAlerts extends OMSSingletonScheduleTask {
  private DataSource dataSource;
  
  private SMTPEmailUtil smtpEmailUtil;
  
  private String sql;
  
  private String alertText;
  
  private String alertSubject;
  
  
  @Override
  protected void performTask() {
    vlogDebug("Entering performTask : ");
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = getDataSource().getConnection();
      stmt = conn.createStatement();
      
      ResultSet rset = stmt.executeQuery(sql);
      rset.next();
      int count = rset.getInt(1);
      if (count > 0) {
        getSmtpEmailUtil().sendEmailAlertForJob(getAlertSubject(), getAlertText(), null, null);
      }
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
    
    vlogDebug("Exiting performTask : ");
  }


  public DataSource getDataSource() {
    return dataSource;
  }


  public void setDataSource(DataSource pDataSource) {
    dataSource = pDataSource;
  }


  public SMTPEmailUtil getSmtpEmailUtil() {
    return smtpEmailUtil;
  }


  public void setSmtpEmailUtil(SMTPEmailUtil pSmtpEmailUtil) {
    smtpEmailUtil = pSmtpEmailUtil;
  }


  public String getSql() {
    return sql;
  }


  public void setSql(String pSql) {
    sql = pSql;
  }


  public String getAlertText() {
    return alertText;
  }


  public void setAlertText(String pAlertText) {
    alertText = pAlertText;
  }


  public String getAlertSubject() {
    return alertSubject;
  }


  public void setAlertSubject(String pAlertSubject) {
    alertSubject = pAlertSubject;
  }

}
