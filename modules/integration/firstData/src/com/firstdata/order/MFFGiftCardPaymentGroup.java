package com.firstdata.order;

import com.firstdata.constants.FirstDataConstants;

import atg.commerce.order.PaymentGroupImpl;

public class MFFGiftCardPaymentGroup extends PaymentGroupImpl {

  /**
   * 
   */
  private static final long serialVersionUID = 198545971962780989L;
  

  /**
   * Get GiftCardNumber
   * @return
   */
  public String getCardNumber() {
    return (String) getPropertyValue(FirstDataConstants.GIFT_CARD_NUMBER);
    
  }
  /**
   * Set GiftCardNumber
   * @param pCardNumber
   */
  public void setCardNumber(String pCardNumber) {
    if (pCardNumber != null) {
      setPropertyValue(FirstDataConstants.GIFT_CARD_NUMBER, pCardNumber);
    }
  }

  /**
   * Get Extended Account Number(PIN)
   * @return
   */
  public String getEan() {
    return (String) getPropertyValue(FirstDataConstants.GIFT_CARD_EAN);
  }
  /**
   * Set Extended Account Number (PIN)
   * @param pEan
   */
  public void setEan(String pEan) {
    if (pEan != null) {
      setPropertyValue(FirstDataConstants.GIFT_CARD_EAN, pEan);
    }
  }
 
  /**
   * Get LockId
   * @return
   */
  public String getLocalLockId() {
    return (String) getPropertyValue(FirstDataConstants.GIFT_CARD_LOCK_ID);
  }
  /**
   * Set LockId
   * @param pLocalLockId
   */
  public void setLocalLockId(String pLocalLockId) {
    if (pLocalLockId != null) {
      setPropertyValue(FirstDataConstants.GIFT_CARD_LOCK_ID, pLocalLockId);
    }
  }

  /**
   * Get Amount hold
   */
  public double getBalanceAmount() {
    return (Double) getPropertyValue(FirstDataConstants.GIFT_CARD_AMOUNT);
  }
  /**
   * Set Amount hold
   * @param pAmount
   */
  public void setBalanceAmount(double pAmount) {
      setPropertyValue(FirstDataConstants.GIFT_CARD_AMOUNT, pAmount);
  }
  
    
  public String getPaymentId() {
    return (String) getPropertyValue("id");
  }

  public void setPaymentId(String pPaymentId) {
    setPropertyValue("id", pPaymentId);
  }
  
  
  /**
   * @return activation attempts
   */
  public Integer getActivationAttempts() {
    return (Integer) getPropertyValue(FirstDataConstants.ACTIVATION_ATTEMPTS);
  }
  
  public void setActivationAttempts(Integer pActivationAttempts) {
    setPropertyValue(FirstDataConstants.ACTIVATION_ATTEMPTS, pActivationAttempts);
  }  
  
}
