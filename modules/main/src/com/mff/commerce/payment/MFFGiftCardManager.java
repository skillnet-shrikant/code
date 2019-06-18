package com.mff.commerce.payment;

import com.firstdata.payment.MFFGiftCardInfo;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;
import com.mff.commerce.order.purchase.GiftCardPaymentInfo;

import atg.nucleus.GenericService;

/**
 * 
 * This class will perform general Gift Card integration calls. For example the
 * call to get the balance of a Gift Card.
 * 
 * @author
 *
 */
public class MFFGiftCardManager extends GenericService {

  private FirstDataService mFirstDataService;

  public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public void setFirstDataService(FirstDataService mFirstDataService) {
    this.mFirstDataService = mFirstDataService;
  }

  /**
   * GiftCard Balance, requires both GC# and EAN(Extended Account Number)
   * 
   * @param pGiftCardNumber
   * @param pEan
   * @return
   */
  public MFFGiftCardInfo checkGiftCardBalance(String pGiftCardNumber, String pEan) {

    MFFGiftCardInfo returnVal = new MFFGiftCardInfo();
    MFFGiftCardPaymentStatus balanceResponse;
    try {
      if (!pGiftCardNumber.isEmpty() && !pEan.isEmpty()) {
        // Call the client payment service
        try {

          balanceResponse = getFirstDataService().balanceInquiry(pGiftCardNumber, pEan);

          if (balanceResponse.getTransactionSuccess()) {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
            returnVal.setBalanceAmount(balanceResponse.getNewBalance() - balanceResponse.getLockAmount());
            returnVal.setGiftCardNumber(pGiftCardNumber);
            returnVal.setEan(pEan);
          } else {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCodeMsg(balanceResponse.getResponseCodeMsg());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
            returnVal.setBalanceAmount(0.0);
            returnVal.setGiftCardNumber(pGiftCardNumber);
            returnVal.setEan(pEan);
            returnVal.setErrorMessage(balanceResponse.getErrorMessage());
          }

        } catch (Exception e) {
          returnVal.setErrorMessage(e.getMessage());
          if (isLoggingError()) {
            logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance: " + e);
          }
        }

      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance: " + ex);
      }
      returnVal.setErrorMessage(ex.getMessage());
    }

    return returnVal;
  }

  /**
   * GiftCard Balance, requires only GC#
   * 
   * @param pGiftCardNumber
   * @return
   */
  public MFFGiftCardInfo checkGiftCardBalance(String pGiftCardNumber) {

    MFFGiftCardInfo returnVal = new MFFGiftCardInfo();
    MFFGiftCardPaymentStatus balanceResponse;
    try {
      if (!pGiftCardNumber.isEmpty()) {
        // Call the client payment service
        try {

          balanceResponse = getFirstDataService().balanceInquiry(pGiftCardNumber);

          if (balanceResponse.getTransactionSuccess()) {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
            returnVal.setBalanceAmount(balanceResponse.getNewBalance());
            returnVal.setGiftCardNumber(pGiftCardNumber);

          } else {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCodeMsg(balanceResponse.getResponseCodeMsg());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
            returnVal.setBalanceAmount(0.0);
            returnVal.setGiftCardNumber(pGiftCardNumber);
          }

        } catch (Exception e) {
          returnVal.setErrorMessage(e.getMessage());
          if (isLoggingError()) {
            logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance(gc): " + e);
          }
        }

      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance(gc): " + ex);
      }
      returnVal.setErrorMessage(ex.getMessage());
    }

    return returnVal;
  }

  /**
   * GiftCard Activation, requires only GC#
   * 
   * @param pGiftCardNumber
   * @return
   */
  public MFFGiftCardInfo giftCardActivation(String pGiftCardNumber, double pAmountToActivate) {

    MFFGiftCardInfo returnVal = new MFFGiftCardInfo();
    MFFGiftCardPaymentStatus balanceResponse;
    try {
      if (!pGiftCardNumber.isEmpty() && pAmountToActivate > 0.0d) {
        // Call the client payment service
        try {
          balanceResponse = getFirstDataService().activate(pGiftCardNumber, pAmountToActivate);

          if (balanceResponse.getTransactionSuccess()) {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
            returnVal.setNewBalance(balanceResponse.getNewBalance());

          } else {
            returnVal.setTransactionSuccess(balanceResponse.getTransactionSuccess());
            returnVal.setResponseCodeMsg(balanceResponse.getResponseCodeMsg());
            returnVal.setResponseCode(balanceResponse.getResponseCode());
          }

        } catch (Exception e) {
          returnVal.setErrorMessage(e.getMessage());
          if (isLoggingError()) {
            logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance(gc): " + e);
          }
        }

      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance(gc): " + ex);
      }
      returnVal.setErrorMessage(ex.getMessage());
    }

    return returnVal;
  }

  public GiftCardPaymentInfo checkGiftCardBalance(String pGiftCardNumber, String pGiftCardPin, String pOrderId) {
    vlogDebug("CheckGiftCardBalance - Entering");
    GiftCardPaymentInfo returnVal = new GiftCardPaymentInfo();
    returnVal.setGiftCardNumber(pGiftCardNumber);
    returnVal.setAmountAssignedToThisOrder(0.0);
    returnVal.setAmountRemainingToSpend(0.0);
    returnVal.setAmountOnCard(0.0);
    returnVal.setFoundInSystem(false);
    
    double balance = 0;

    try {
      if (!pGiftCardNumber.isEmpty() && !pGiftCardPin.isEmpty()) {

        MFFGiftCardInfo balanceInfo = checkGiftCardBalance(pGiftCardNumber, pGiftCardPin);

        balance = balanceInfo.getBalanceAmount();
        vlogDebug("Balance Amount on the Card:{0}, error message:{1}, responsecode:{2}", balance,balanceInfo.getResponseCodeMsg(),balanceInfo.getResponseCode());
        returnVal.setErrorMessage(balanceInfo.getResponseCodeMsg());
        returnVal.setTransactionSuccess(balanceInfo.isTransactionSuccess());
      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError("An exception was caught in MFFGiftCardManager.checkGiftCardBalance: " + ex);
      }
      returnVal.setTransactionSuccess(false);
      returnVal.setErrorMessage(ex.getMessage());
      returnVal.setFoundInSystem(false);
    }
    returnVal.setAmountOnCard(balance);
    vlogDebug("CheckGiftCardBalance - Exiting");
    return returnVal;
  }

}
