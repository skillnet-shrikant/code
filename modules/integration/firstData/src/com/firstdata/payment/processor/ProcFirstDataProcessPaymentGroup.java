package com.firstdata.payment.processor;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;

import com.firstdata.constants.FirstDataConstants;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardInfo;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;

public class ProcFirstDataProcessPaymentGroup extends ProcProcessPaymentGroup {

  private FirstDataService mFirstDataService;

  /**
   * variables intended only to be used while unit testing when realtime api is
   * not available
   */
  boolean testMode;
  
  private static final String GIFT_CARD_INSUFFICIENT_FUNDS="FailedGiftCardAuthNSF";

  @Override
  public MFFGiftCardPaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pPipelineArguments) throws CommerceException {
    vlogDebug("Entered authorizePaymentGroup method in ProcFirstDataProcessPaymentGroup");

    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) pPipelineArguments;
    MFFGiftCardInfo giftCardInfo = (MFFGiftCardInfo) params.getPaymentInfo();
    MFFGiftCardPaymentStatus paymentStatus;

    if (giftCardInfo != null && !giftCardInfo.getGiftCardNumber().isEmpty() && !giftCardInfo.getEan().isEmpty() && giftCardInfo.getAmount() != 0.0) {

      if (getFirstDataService().isTestAuthorizationDeclines()) {

      }
      try {
        if (isTestMode()) {
          vlogDebug("Entered authorizePaymentGroup - Test Mode");
          paymentStatus = new MFFGiftCardPaymentStatus("224");
          paymentStatus.setLocalLockId("4");
          paymentStatus.setResponseCode("09");
          paymentStatus.setTransactionSuccess(true);
          paymentStatus.setAuthCode("777");
        } else {

          paymentStatus = getFirstDataService().authorize(giftCardInfo.getGiftCardNumber(), giftCardInfo.getAmount(), giftCardInfo.getEan());
          paymentStatus.setAmount(giftCardInfo.getAmount());

          // Validating if lock amount and other things are set right.
          // If not throw an exception
          // first validate if trx is successful. If set to false
          // the OOTB payment manager will throw a PaymentException

          if (paymentStatus.getTransactionSuccess()) {

            if (!paymentStatus.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {

              if (paymentStatus.getLockAmount() == 0.0 && paymentStatus.getLocalLockId() == null) {
                
                
                vlogError("Could not authorize for the amount {0}, response code {1}, response message {2}, auth code {3}, payment group id {4} lock amount {5}, lock id {6}", 
                      paymentStatus.getAmount(), 
                      paymentStatus.getResponseCode(), 
                      paymentStatus.getResponseCodeMsg(), 
                      paymentStatus.getAuthCode(), 
                      giftCardInfo.getPaymentId(), 
                      paymentStatus.getLockAmount(),
                      paymentStatus.getLocalLockId());

                throw new CommerceException("Could not authorize the gift card");
              }
              

            } else {
              if (checkForInsufficientFundsOnAuth(paymentStatus, giftCardInfo)) {
                paymentStatus.setTransactionSuccess(false);
                paymentStatus.setErrorMessage(GIFT_CARD_INSUFFICIENT_FUNDS);
              }
            }
            vlogInfo("Successfully authorized gift card {0}, amount {1}, response code {2}", giftCardInfo.getPaymentId(), giftCardInfo.getAmount(), paymentStatus.getResponseCode());
          } // end of trx success check

        }

      } catch (Exception e) {
        vlogError(e, "An exception occurred while authorizing gift card");
        throw new CommerceException(e);
      }
    } else {
      throw new CommerceException("ProcFirstDataProcessPaymentGroup: AuthorizationPaymentGroup: MFFGiftCardPaymentStatus is Null Can not Proceed with Auth");
    }
    vlogDebug("Exit authorizePaymentGroup method in ProcFirstDataProcessPaymentGroup with Authorized amount as {0}, AuthCode {1} and Order# {2}, ", paymentStatus.getLockAmount(), paymentStatus.getAuthCode(), pPipelineArguments.getOrder().getId());
    return paymentStatus;
  }

  /**
   * The following method verifies that the authorization call resulted in the
   * right amount being locked. This is likely to happen when an order that is
   * not shipped yet followed by a second order for a value that exceeds the
   * actual balance on the card. In such cases we the lock amount is not equal
   * to the original requested amount then we will call the debit operation with
   * the newly acquired lock with an amount of $0. This will release the lock on
   * the gift card
   * 
   * @param pAuthorizationStatus
   *          the authorization status
   * @param pGiftCardInfo
   *          the gift card info that was used to authorize the card
   * @return <true> if there is insufficient funds
   * @throws Exception
   */
  protected boolean checkForInsufficientFundsOnAuth(MFFGiftCardPaymentStatus pAuthorizationStatus, MFFGiftCardInfo pGiftCardInfo) {

    vlogDebug("Entering checkForInsufficientFundsOnAuth : pAuthorizationStatus");

    double originalAuthorizationRequest = pGiftCardInfo.getAmount();
    double lockAmount = pAuthorizationStatus.getLockAmount();
    boolean nsf = true;

    if (originalAuthorizationRequest != lockAmount) {

      vlogInfo("Requested amount {0}, lock amount {1} payment group id {2} - releasing the lock using the debit method ", originalAuthorizationRequest, lockAmount, pGiftCardInfo.getPaymentId());
      // unlock the authorization. This is performed using the debit operation
      // but with $0 amount
      try {
        getFirstDataService().debit(pGiftCardInfo.getGiftCardNumber(), 0.0d, pAuthorizationStatus.getLocalLockId(), pGiftCardInfo.getEan());

      } catch (Exception e) {
        vlogError("An error occurred while rolling back the auth for the payment group id {0}", pGiftCardInfo.getPaymentId());
      }

    } else {
      nsf = false;
    }
    vlogDebug("Exiting checkForInsufficientFundsOnAuth : pAuthorizationStatus");
    return nsf;

  }

  @Override
  public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pPipelineArgs) throws CommerceException {
    vlogDebug("Entered debitPaymentGroup method in ProcFirstDataProcessPaymentGroup");

    PaymentGroup pg = pPipelineArgs.getPaymentGroup();

    MFFGiftCardPaymentStatus lLastSuccessfulAuth = (MFFGiftCardPaymentStatus) pPipelineArgs.getPaymentManager().getLastAuthorizationStatus(pg);
    MFFGiftCardPaymentStatus paymentStatusUpdate;

    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs) pPipelineArgs;
    MFFGiftCardInfo giftCardInfo = (MFFGiftCardInfo) params.getPaymentInfo();
    MFFGiftCardPaymentGroup lPaymentGroup;

    lPaymentGroup = (MFFGiftCardPaymentGroup) pPipelineArgs.getPaymentGroup();

    if (lLastSuccessfulAuth != null && lLastSuccessfulAuth.getLockAmount() != 0.0 && !lLastSuccessfulAuth.getLocalLockId().isEmpty()) {
      try {
        if (isTestMode()) {
          paymentStatusUpdate = new MFFGiftCardPaymentStatus("31");
          paymentStatusUpdate.setTransactionSuccess(true);
          paymentStatusUpdate.setCallType("test");
          paymentStatusUpdate.setResponseCode("12");
          paymentStatusUpdate.setAuthCode("31");

        } else {
          paymentStatusUpdate = getFirstDataService().debit(giftCardInfo.getGiftCardNumber(), giftCardInfo.getAmount(), lLastSuccessfulAuth.getLocalLockId(), giftCardInfo.getEan());
          paymentStatusUpdate.setAmount(giftCardInfo.getAmount());

          // Validating if lock amount and other things are set right.
          // If not throw an exception
          // first validate if trx is successful. If set to false
          // the OOTB payment manager will throw a PaymentException
          if (paymentStatusUpdate.getTransactionSuccess()) {

            if (!paymentStatusUpdate.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {

              if (paymentStatusUpdate.getLockAmount() == 0.0 && paymentStatusUpdate.getLocalLockId() == null) {

                vlogError("Could not debit the amount {0}, payment group id {1}, response code {2}, response message {3}, auth code {4}, payment group id {5} lock amount {6}, lock id {7}", paymentStatusUpdate.getAmount(), paymentStatusUpdate.getResponseCode(), paymentStatusUpdate.getResponseCodeMsg(), paymentStatusUpdate.getAuthCode(), giftCardInfo.getPaymentId(),
                    paymentStatusUpdate.getLockAmount(), paymentStatusUpdate.getLocalLockId());
                throw new CommerceException("Debit operation unsuccessful");

              } // end of lock amount if stmt
            } // end of response check

            vlogInfo("Successfully debited the amount {0} from payment group id {1}", giftCardInfo.getAmount(), lPaymentGroup.getId());
          } // end of trx check

        }
      } catch (Exception e) {
        throw new CommerceException(e);
      }
    } else {
      throw new CommerceException("ProcFirstDataProcessPaymentGroup: DebitPaymentGroup: MFFGiftCardPaymentStatus is Null Can not Proceed with Debit");
    }
    vlogDebug("Exit debitPaymentGroup method in ProcFirstDataProcessPaymentGroup with Debit operation as {0} with Amount {1} , AuthCode {2} and Order Number {3}", paymentStatusUpdate.getTransactionSuccess(), paymentStatusUpdate.getAmount(), paymentStatusUpdate.getAuthCode(), pPipelineArgs.getOrder().getId());
    return paymentStatusUpdate;
  }

  public FirstDataService getFirstDataService() {
    return mFirstDataService;
  }

  public void setFirstDataService(FirstDataService mFirstDataService) {
    this.mFirstDataService = mFirstDataService;
  }

  @Override
  public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs arg0) throws CommerceException {
    throw new CommerceException("Credit of Gift Card payments is not supported");
  }

  public boolean isTestMode() {
    return testMode;
  }

  public void setTestMode(boolean pTestMode) {
    testMode = pTestMode;
  }

}
