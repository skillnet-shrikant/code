package oms.commerce.salesaudit.util;

import java.util.List;
import java.util.Vector;

/**
 * Records the counts/dollars for a given line and for the overall file.  This class is
 * used by the Invoice Manager.
 * 
 * @author jvose
 *
 */
public class ExtractStatistics {
  
  public ExtractStatistics () {    
    // Initialize totals
    mTransactionTotal         = 0.00;
    mTransactionTaxableTotal  = 0.00;
    mTransactionTaxTotal      = 0.00;
    mTransactionCount         = 0;
    mLineCount                = 0;
    mPaymentTotal             = 0.00;
    mPaymentCount             = 0;
    mDiscountTotal            = 0.00;
    mDiscountCount            = 0;
    mGiftcardSoldTotal        = 0.00;
    mGiftcardSoldCount        = 0;
    mExtractPaymentSummary = new Vector <ExtractPaymentSummary> ();
  }
  
  // *********************************************************
  //            Getter/setters
  // *********************************************************
  private double  mTransactionTotal;
  private double  mTransactionTaxableTotal;
  private double  mTransactionTaxTotal;
  private long    mTransactionCount;
  private long    mLineCount;
  private double  mPaymentTotal;
  private long    mPaymentCount;
  private double  mDiscountTotal;
  private long    mDiscountCount;
  private double  mGiftcardSoldTotal;
  private long    mGiftcardSoldCount;
  List<ExtractPaymentSummary> mExtractPaymentSummary;  
  
  public double getTransactionTotal() {
    return mTransactionTotal;
  }
  public void setTransactionTotal(double pTransactionTotal) {
    this.mTransactionTotal = pTransactionTotal;
  }
  public double getTransactionTaxableTotal() {
    return mTransactionTaxableTotal;
  }
  public void setTransactionTaxableTotal(double pTransactionTaxableTotal) {
    this.mTransactionTaxableTotal = pTransactionTaxableTotal;
  }
  public double getTransactionTaxTotal() {
    return mTransactionTaxTotal;
  }
  public void setTransactionTaxTotal(double pTransactionTaxTotal) {
    this.mTransactionTaxTotal = pTransactionTaxTotal;
  }
  public long getTransactionCount() {
    return mTransactionCount;
  }
  public void setTransactionCount(long pTransactionCount) {
    this.mTransactionCount = pTransactionCount;
  }
  public long getLineCount() {
    return mLineCount;
  }
  public void setLineCount(long pLineCount) {
    this.mLineCount = pLineCount;
  }  
  public double getPaymentTotal() {
    return mPaymentTotal;
  }
  public void setPaymentTotal(double pPaymentTotal) {
    this.mPaymentTotal = pPaymentTotal;
  }
  public long getPaymentCount() {
    return mPaymentCount;
  }
  public void setPaymentCount(long pPaymentCount) {
    this.mPaymentCount = pPaymentCount;
  }
  public double getDiscountTotal() {
    return mDiscountTotal;
  }
  public void setDiscountTotal(double pDiscountTotal) {
    this.mDiscountTotal = pDiscountTotal;
  }
  public long getDiscountCount() {
    return mDiscountCount;
  }
  public void setDiscountCount(long pDiscountCount) {
    this.mDiscountCount = pDiscountCount;
  }
  public double getGiftcardSoldTotal() {
    return mGiftcardSoldTotal;
  }
  public void setGiftcardSoldTotal(double pGiftcardSoldTotal) {
    this.mGiftcardSoldTotal = pGiftcardSoldTotal;
  }
  public long getGiftcardSoldCount() {
    return mGiftcardSoldCount;
  }
  public void setGiftcardSoldCount(long pGiftcardSoldCount) {
    this.mGiftcardSoldCount = pGiftcardSoldCount;
  }
  public List<ExtractPaymentSummary> getExtractPaymentSummary() {
    return mExtractPaymentSummary;
  }
  public void setExtractPaymentSummary(List<ExtractPaymentSummary> pExtractPaymentSummary) {
    this.mExtractPaymentSummary = pExtractPaymentSummary;
  }

}
