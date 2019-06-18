package oms.commerce.salesaudit.util;

/**
 * Working class used by the Invoice manager to record total counts for the payment 
 * methods in a given extract file.
 * 
 * @author jvose
 *
 */
class ExtractPaymentSummary {
  private String mPaymentType;
  private double  mCreditTotal;
  private long    mCreditCount;
  private double  mDebitTotal;
  private long    mDebitCount;
  
  public ExtractPaymentSummary () {    
    // Initialize totals
    mCreditTotal  = 0.00;
    mCreditCount  = 0;
    mDebitTotal   = 0.00;
    mDebitCount   = 0;
  }
  
  // *********************************************************
  //            Getter/setters
  // *********************************************************
  public String getPaymentType() {
    return mPaymentType;
  }
  public void setPaymentType(String pPaymentType) {
    this.mPaymentType = pPaymentType;
  }
  public double getCreditTotal() {
    return mCreditTotal;
  }
  public void setCreditTotal(double pCreditTotal) {
    this.mCreditTotal = pCreditTotal;
  }
  public long getCreditCount() {
    return mCreditCount;
  }
  public void setCreditCount(long pCreditCount) {
    this.mCreditCount = pCreditCount;
  }
  public double getDebitTotal() {
    return mDebitTotal;
  }
  public void setDebitTotal(double pDebitTotal) {
    this.mDebitTotal = pDebitTotal;
  }
  public long getDebitCount() {
    return mDebitCount;
  }
  public void setDebitCount(long pDebitCount) {
    this.mDebitCount = pDebitCount;
  }    
}   