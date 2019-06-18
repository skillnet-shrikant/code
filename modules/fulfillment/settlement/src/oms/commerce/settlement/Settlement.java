package oms.commerce.settlement;

import java.util.Date;

public class Settlement {
  private Date createDate;

  private String orderId;
  private int mQuantity;
  private String mCommerceItemId;

  private String pgId;
  private boolean partialSettlement;
  private int settlementStatus;
  private double settlementAmount;
  private Date settlementDate;
  private String errorMessage;
  private String orderNumber;
  private String pgDesc;

  
  public int getQuantity() {
    return mQuantity;
  }

  public void setQuantity(int quantity) {
    this.mQuantity = quantity;
  }

  public String getCommerceItemId() {
    return mCommerceItemId;
  }

  public void setCommerceItemId(String commerceItemId) {
    this.mCommerceItemId = commerceItemId;
  }

  public Settlement() 
  {
    this.settlementStatus = 0;
  }
  
  public Date getCreateDate() {
    return createDate;
  }
  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }
  public String getOrderId() {
    return orderId;
  }
  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
  public String getPgId() {
    return pgId;
  }
  public void setPgId(String pgId) {
    this.pgId = pgId;
  }
  public boolean isPartialSettlement() {
    return partialSettlement;
  }
  public void setPartialSettlement(boolean partialSettlement) {
    this.partialSettlement = partialSettlement;
  }
  public int getSettlementStatus() {
    return settlementStatus;
  }
  public void setSettlementStatus(int settlementStatus) {
    this.settlementStatus = settlementStatus;
  }
  public double getSettlementAmount() {
    return settlementAmount;
  }
  public void setSettlementAmount(double settlementAmount) {
    this.settlementAmount = settlementAmount;
  }
  public Date getSettlementDate() {
    return settlementDate;
  }
  public void setSettlementDate(Date settlementDate) {
    this.settlementDate = settlementDate;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getPgDesc() {
    return pgDesc;
  }

  public void setPgDesc(String pgDesc) {
    this.pgDesc = pgDesc;
  }

  /**
   * @return the settlementType
   */
  public int getSettlementType() {
    if (settlementAmount < 0.0d) {
      return 0;
    } else {
      return 1;
    }
  }
}
