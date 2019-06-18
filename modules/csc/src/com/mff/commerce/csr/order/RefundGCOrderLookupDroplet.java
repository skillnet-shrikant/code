package com.mff.commerce.csr.order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.mff.constants.MFFConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class RefundGCOrderLookupDroplet extends DynamoServlet{
  
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    List<RefundGCLookupResponse> refundOrderList = getRefundGCOrders();
    if(refundOrderList != null && refundOrderList.size() > 0){
      pRequest.setParameter("result", refundOrderList);
      pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
    }else{
      pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
    }
    
  }
  
  private List<RefundGCLookupResponse> getRefundGCOrders(){
    
    // query the message table
      DataSource ds = getDataSource();
      PreparedStatement ps = null;
      List<RefundGCLookupResponse> refundOrderList = new ArrayList<RefundGCLookupResponse>();
      
    if (getDataSource() == null) {
      if (isLoggingDebug()) {
        logDebug("no configured datasource. aborting");
      }
      return refundOrderList;
    }
     
    if (isLoggingDebug()) {
      logDebug("querying SQL datasource " + dataSource.toString() + " with SQL: " + getQuerySQL());
    }
    
    try (Connection conn = ds.getConnection()) {
      ps = conn.prepareStatement(getQuerySQL());
      ResultSet rs = ps.executeQuery();
      
      try {
        while(rs.next()){
          RefundGCLookupResponse response = new RefundGCLookupResponse();
          response.setOrderNumber(rs.getString("orderNumber"));
          response.setOrderId(rs.getString("orderId"));
          refundOrderList.add(response);
        }
      } catch (SQLException e) {
        vlogError(e,"Error in getRefundGCOrders");
      }
    
    } catch (SQLException ex) {
      String errorMessage = "getRefundGCOrders - Unable to get records";
      vlogError(ex, errorMessage);
    }
    
    return refundOrderList;
  }
  
  private DataSource dataSource;
  
  public DataSource getDataSource() {
     return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
     this.dataSource = dataSource;
  }
   
  /** Sql Query to get records*/
  private String querySQL;
  public String getQuerySQL() {
     return querySQL;
  }
  
  public void setQuerySQL(String querySQL) {
     this.querySQL = querySQL;
  }

}
