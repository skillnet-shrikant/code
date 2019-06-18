package oms.commerce.payment;


import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.aci.commerce.service.AciService;
import com.aci.configuration.AciConfiguration;
import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.payment.creditcard.AciCreditCardStatus;
import com.aci.payment.creditcard.processor.AciCreditCardProcessor;
import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.service.FirstDataService;

import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.Constants;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManager;
import atg.commerce.payment.PaymentManagerAction;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.states.StateDefinitions;
import atg.payment.PaymentStatus;
import atg.payment.PaymentStatusImpl;
import atg.payment.creditcard.CreditCardStatus;
import oms.commerce.states.OMSOrderStates;

public class MFFPaymentManager extends PaymentManager {

  /**
   * The duration for which the authorization is valid for
   */
  private int creditCardExpiryTimeInDays;

  private static final long MILLISECONDS_IN_A_DAY = 86400000;
  
  private boolean testFlag = true;
  
  private boolean useCreditCardExpiryCheckWithDays;
  
  FirstDataService firstDataService;
  
  private AciService mAciService;
  
  public boolean isUseCreditCardExpiryCheckWithDays() {
	return useCreditCardExpiryCheckWithDays;
}

public void setUseCreditCardExpiryCheckWithDays(boolean pUseCreditCardExpiryCheckWithDays) {
	useCreditCardExpiryCheckWithDays = pUseCreditCardExpiryCheckWithDays;
}

public AciService getAciService(){
	  return mAciService;
  }
  
  public void setAciService(AciService pAciService){
	  mAciService=pAciService;
  }
 
  
  
  public boolean isTestFlag() {
    return testFlag;
  }
  public void setTestFlag(boolean pTestFlag) {
    testFlag = pTestFlag;
  }
  
  
  /**
   * The following method is responsible for reversing an authorization on a
   * payment group if supported. Currently only gift cards are supported. For a
   * gift card we just reverse the authorization by performing a debit operation
   * of $0 which voids the hold on the card.
   * 
   * @param lPaymentGroup
   *          the payment group for which the authorization needs to be reversed
   * @throws CommerceException
   */
  public void reverseAuthorization(PaymentGroup lPaymentGroup,double amountToReverse,boolean isPartial) throws CommerceException {
    vlogDebug("Entering reverseAuthorization : lPaymentGroup");
    
    if(lPaymentGroup instanceof AciCreditCard){
    	try {
    		Order order = lPaymentGroup.getOrderRelationship().getOrder();
    		AciCreditCard aciCreditCard=(AciCreditCard)lPaymentGroup;
    		if(getAciService().getAciConfiguration().getReversalSupportedCards().contains(aciCreditCard.getMopTypeCode().trim())){
	    		AciCreditCardStatus lastAuthStatus=(AciCreditCardStatus)getLastAuthorizationStatus(lPaymentGroup);
	    		double previousAuthAmount=getPricingTools().round(lPaymentGroup.getAmountAuthorized());
	    		double amtToReverse=getPricingTools().round(amountToReverse);
	    		vlogDebug("Previous Auth Amount: "+previousAuthAmount);
	    		vlogDebug("Previous Amount to Reverse: "+amtToReverse);
	    		vlogDebug("Is Partial: "+isPartial);
		    	if(isPartial){
		    		decreaseAuthorization(order, lPaymentGroup,previousAuthAmount);
		    	}
		    	else {
		    		decreaseAuthorization(order, lPaymentGroup,amtToReverse);
		    	}
    		}
	    }
	    catch(Exception ex){
	    	vlogError("Reverse Authorization: Error occurred while reversing authorization",ex);
	    }
    }

    else if (lPaymentGroup instanceof MFFGiftCardPaymentGroup) {
      
      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lPaymentGroup;
      
      // get the last authorization that was performed on the gift card to get
      // handle on the lock id.
      MFFGiftCardPaymentStatus lLastAuthStatus = (MFFGiftCardPaymentStatus) getLastAuthorizationStatus(lPaymentGroup);

      String lEAN = lGiftCardPaymentGroup.getEan();
      String lLocalLockId = lLastAuthStatus.getLocalLockId();
      String lGiftCardNumber = lGiftCardPaymentGroup.getCardNumber();

      try {
        getFirstDataService().debit(lGiftCardNumber, 0.0d, lLocalLockId, lEAN);
      } catch (Exception e) {
        String lErrorMessage = String.format("Could not reverse the authorization for the payment group id %s", lPaymentGroup.getId());
        throw new CommerceException(lErrorMessage, e);
      }
      vlogInfo("Reversed authorization: pg id {0}", lPaymentGroup.getId());
    }
 
    vlogDebug("Exiting reverseAuthorization : lPaymentGroup");
  }
  
  protected void createNewAuthorizationStatus(Order pOrder,PaymentGroup pPaymentGroup,double previousAuthAmount,double amountToReverse,PaymentStatus pLastAuthStatus){
	  if(pLastAuthStatus != null)
      {
		  	AciCreditCardStatus lastCreditCardStatus=(AciCreditCardStatus)pLastAuthStatus;
		  	double newAmountForAuth=previousAuthAmount-amountToReverse;
		  	double newAuthAmount=getPricingTools().round(newAmountForAuth);
		  	double newAmount=pPaymentGroup.getAmount()-amountToReverse;
		  	double newAmountRounded= getPricingTools().round(newAmount);
		  	AciCreditCardStatus authorizationStatus = new AciCreditCardStatus(lastCreditCardStatus.getTransactionId(),newAuthAmount,lastCreditCardStatus.getTransactionSuccess(),"",lastCreditCardStatus.getTransactionTimestamp(),lastCreditCardStatus.getAuthorizationExpiration());
			if(pPaymentGroup.getAmountAuthorized()!=0.0d){
				pPaymentGroup.setAmountAuthorized(newAuthAmount);
				pPaymentGroup.setAmount(newAmountRounded);
				authorizationStatus.setCallType(lastCreditCardStatus.getCallType());
				authorizationStatus.setOriginalJournalKey(lastCreditCardStatus.getOriginalJournalKey());
				authorizationStatus.setCvvVerificationCode(lastCreditCardStatus.getCvvVerificationCode());
				authorizationStatus.setAuthorizationDate(lastCreditCardStatus.getAuthorizationDate());
				authorizationStatus.setTransactionDate(lastCreditCardStatus.getTransactionDate());
				authorizationStatus.setAuthCode(lastCreditCardStatus.getAuthCode());
				authorizationStatus.setAvsCode(lastCreditCardStatus.getAvsCode());
				authorizationStatus.setResponseCode(lastCreditCardStatus.getResponseCode());
				if(lastCreditCardStatus.getResponseCodeMsg()!=null){
					authorizationStatus.setResponseCodeMsg(lastCreditCardStatus.getResponseCodeMsg());
				}
				authorizationStatus.setAuthorizationTime(lastCreditCardStatus.getAuthorizationTime());
				if(lastCreditCardStatus.getAuthorizationSource()!=null){
					authorizationStatus.setAuthorizationSource(lastCreditCardStatus.getAuthorizationSource());
				}
				authorizationStatus.setOriginalAciAuthDate(lastCreditCardStatus.getOriginalAciAuthDate());
				authorizationStatus.setOriginalAciAuthTime(lastCreditCardStatus.getOriginalAciAuthTime());
				authorizationStatus.setOriginalReqId(lastCreditCardStatus.getOriginalReqId());
				
			}
			pPaymentGroup.addAuthorizationStatus(authorizationStatus);
			int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorized");
			pPaymentGroup.setState(state);
      }
  }

  /**
   * The following method validates authorizations are still valid for all
   * payment groups associated with a given order. If any of the authorizations
   * are not valid anymore an attempt is made to reauthorize the payment group.
   * This method loops through the list of payments and validates their
   * authorization. If any of the payment group fails reauthorization, it stops
   * the validation of any remaining payment groups.
   * 
   * @param orderId
   *          the order id for which the authorizations needs to be validated
   * @return <code>true</code> if all payment groups have successfully been
   *         validated. <code>false</code> if any one of the payment groups
   *         fails validation.
   */
  @SuppressWarnings("unchecked")
  public boolean validateAndReauthorizePayments(Order lOrder) {
    vlogDebug("Entering validateAndReauthorizePayments : orderId");
    boolean paymentsAuthorized = true;
    
    try {
      

      List<PaymentGroup> payments = lOrder.getPaymentGroups();
      for (PaymentGroup payment : payments) {
        if (paymentsAuthorized) {
          paymentsAuthorized = validateAndReauthorizedPayment(payment);
        }
      }
      
    } catch (CommerceException e) {
      paymentsAuthorized = false;
      vlogWarning(e, "Could not validate the authorization for order id {0}", lOrder.getId());
    }
    if(!isTestFlag()){
      return false;
    }
    vlogDebug("Exiting validateAndReauthorizePayments : orderId");
    return paymentsAuthorized;
  }
  
  private boolean validateAndReauthForPartialCancellationForCreditCard(PaymentGroup pPaymentGroup,double amountToReverse) throws CommerceException {
	  vlogDebug("Entering validateAndReauthorizeCreditCard : pPaymentGroup");
	  double balanceAmount=getPricingTools().round(pPaymentGroup.getAmountAuthorized()-amountToReverse);
	  double balancePaymentGroupAmount=getPricingTools().round(pPaymentGroup.getAmount()-amountToReverse);
	  boolean paymentAuthorized=false;
	  if(pPaymentGroup instanceof AciCreditCard){
		  if(balanceAmount > 0.0d){
	  		pPaymentGroup.setAmountAuthorized(0.0);
	  		Order order = pPaymentGroup.getOrderRelationship().getOrder();
	  		 if(order.getState()!=StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.CANCELLED)){
	  		      
	  	    	  authorize(order, pPaymentGroup, balanceAmount);
	  	    	  pPaymentGroup.setAmount(balancePaymentGroupAmount);
	  	    	  vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmount, pPaymentGroup.getId());
	  	    	  paymentAuthorized= true;
	  	      }
	  		 else if(order.getState()==StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.SHIPPED)){
	  			 if(!hasSettlementProcessed(pPaymentGroup)){
	  				  authorize(order, pPaymentGroup, balanceAmount);
	  				  pPaymentGroup.setAmount(balancePaymentGroupAmount);
	    	    	  vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmount, pPaymentGroup.getId());
	  			 }
	  			 paymentAuthorized= true;
	  		 }
	  	    }
	  	    else if(balanceAmount == 0.0d){
	  	    	vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmount, pPaymentGroup.getId());
	  	    	 paymentAuthorized= true;
	  	    }
	  	    vlogDebug("Exiting validateAndReauthorizeCreditCard : pPaymentGroup");
  		}
	  else {
		  paymentAuthorized= true;
	  }
  	    return paymentAuthorized;
  }

  private boolean hasSettlementProcessed(PaymentGroup pPaymentGroup){
	  boolean isSettled=false;
	  List<PaymentStatus> debitStatuses=(List<PaymentStatus>) pPaymentGroup.getDebitStatus();
	  if(debitStatuses!=null){
		  double totalDebitAmount=0.0;
		  for(PaymentStatus debitStatus:debitStatuses){
			  if (debitStatus.getTransactionSuccess()){
				  	totalDebitAmount+=debitStatus.getAmount();
			  }
		  }
		  if(getPricingTools().round(totalDebitAmount)==getPricingTools().round(pPaymentGroup.getAmount())){
			  isSettled=true;
		  }
	  }
	  return isSettled;
  }
  
  public boolean validateAndReauthorizedPayment(PaymentGroup pPaymentGroup) throws CommerceException {
    vlogDebug("Entering validateAndReauthorizeGiftCard : pPaymentGroup");
    boolean paymentAuthorized = false;
    double balanceAmt = 0.0d;

    balanceAmt = getReAuthAmount(pPaymentGroup);

    if (balanceAmt > 0.0d) {
      pPaymentGroup.setAmountAuthorized(0.0);
      Order order = pPaymentGroup.getOrderRelationship().getOrder();
      
      if(order.getState()!=StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.SHIPPED) &&order.getState()!=StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.CANCELLED)){
      
    	  authorize(order, pPaymentGroup, balanceAmt);
    	  vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmt, pPaymentGroup.getId());
    	  paymentAuthorized= true;
      }
    }
    else if(balanceAmt == 0.0d){
    	vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmt, pPaymentGroup.getId());
    	 paymentAuthorized= true;
    }
    vlogDebug("Exiting validateAndReauthorizeGiftCard : pPaymentGroup");
    return paymentAuthorized;
  }

  public boolean validateAndReauthorizedPaymentForSettlement(PaymentGroup pPaymentGroup) throws CommerceException {
	    vlogDebug("Entering validateAndReauthorizeGiftCard : pPaymentGroup");
	    boolean paymentAuthorized = false;
	    double balanceAmt = 0.0d;

	    balanceAmt = calculateBalanceAmount(pPaymentGroup);

	    if (balanceAmt > 0.0d) {
	      pPaymentGroup.setAmountAuthorized(0.0);
	      Order order = pPaymentGroup.getOrderRelationship().getOrder();
	      
	      if(order.getState()!=StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.SHIPPED) &&order.getState()!=StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.CANCELLED)){
	      
	    	  authorize(order, pPaymentGroup, balanceAmt);
	    	  vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmt, pPaymentGroup.getId());
	    	  paymentAuthorized= true;
	      }
	    }
	    else if(balanceAmt == 0.0d){
	    	vlogInfo("Reauth : balance amt {0} pg id {1}", balanceAmt, pPaymentGroup.getId());
	    	 paymentAuthorized= true;
	    }
	    vlogDebug("Exiting validateAndReauthorizeGiftCard : pPaymentGroup");
	    return paymentAuthorized;
	  }
  /**
   * The following method computes the amount that is to be reauthed based on
   * the amount that was originally authorized and the amount that has been
   * debitted so far. This value is compared against the last authorization. If
   * these values are not identical then the reauth amount is set to the former
   * value. Under no circumstances will these two values be different.
   * 
   * @param pPaymentGroup
   *          the payment group for which the reauth amount needs to be computed
   * @return the reauth amount. Set to zero if there is no need for an
   *         reauthorizaiton
   * @throws CommerceException
   */
  @SuppressWarnings("unchecked")
  protected double getReAuthAmount(PaymentGroup pPaymentGroup) throws CommerceException {

    vlogDebug("Entering getReAuthAmount : pPaymentGroup");

    double balanceAmt = 0.0d;
    double lastAuthorizedAmount = 0.0d;

    if (pPaymentGroup.getAmount() > 0.0d) {

      PaymentStatus lastAuthStatus = getLastAuthorizationStatus(pPaymentGroup);

      if (lastAuthStatus.getTransactionSuccess()) {

        if (!paymentAuthStatusExpired(lastAuthStatus)|| ((AciCreditCardStatus)lastAuthStatus).getCallType().equalsIgnoreCase("OX")) {

          lastAuthorizedAmount = lastAuthStatus.getAmount();

        } else {
          // last authorization has expired so reverse auth if its a credit card or ignore if its a gift card
        	vlogDebug("Payment group id {0}, last authorization has expired", pPaymentGroup.getId());
        	if(pPaymentGroup instanceof AciCreditCard){
        		try {
        			reverseAuthorization(pPaymentGroup,pPaymentGroup.getAmountAuthorized(),false);
        		}
        		catch(Exception ex){
        			vlogWarning("Reverse Authorization: Error occurred while reversing authorization for expired auth",ex);
        		}
        	}
          
        }

      } else {
        // last authorization was not a successful one so ignoring it
        vlogDebug("Payment group id {0}, last auth was not successful", pPaymentGroup.getId());
      }

      vlogDebug("Payment group id {0} last auth amount {1}", pPaymentGroup.getId(), lastAuthorizedAmount);

      double outstandingBalance = pPaymentGroup.getAmount();

      for (PaymentStatus debitStatus : (List<PaymentStatus>) pPaymentGroup.getDebitStatus()) {
        if (debitStatus.getTransactionSuccess()) {
          outstandingBalance = outstandingBalance - debitStatus.getAmount();
        } else {
          // the debit was not successful so ignoring it
          vlogDebug("Payment group {0} ignoring debit since it was not successful");
        }
      }

      outstandingBalance = getPricingTools().round(outstandingBalance);
      vlogDebug("Outstanding balance {0}", outstandingBalance);

      // last auth amount should be equal to outstanding balance. last auth
      // amount
      // will be greater than the outstanding balance in cases where there was
      // amount debited due to a partial shipment and subsequent re-auth failed
      //
      // For e.g. order total = 100
      //
      // $10 shipped and settled
      //
      // last auth amount = $100 in case of reauth fails
      //
      // or
      //
      // last auth amount = $90 if reauth is successful.
      //
      // So when the 2nd item is shipped the amount is shipped the last auth
      // amount should be equal to the outstanding balance
      if (outstandingBalance != lastAuthorizedAmount) {
        balanceAmt = outstandingBalance;
      } else {
        balanceAmt = 0.0d;
      }
    }
    vlogDebug("Balance amount set to {0}", balanceAmt);
    vlogDebug("Exiting getReAuthAmount : pPaymentGroup");
    return balanceAmt;
  }
  
  
  
  public double calculateBalanceAmount(PaymentGroup pPaymentGroup) throws CommerceException{
	  vlogDebug("Entering calculateBalanceAmount : pPaymentGroup");
	  double balanceAmt=0.0d;
	  double lPaymentGroupAmount=getPricingTools().round(pPaymentGroup.getAmount());
	  double lAlreadyDebitedAmount=0.0d;
      for (PaymentStatus debitStatus : (List<PaymentStatus>) pPaymentGroup.getDebitStatus()) {
          if (debitStatus.getTransactionSuccess()) {
        	  lAlreadyDebitedAmount = lAlreadyDebitedAmount + debitStatus.getAmount();
          } else {
            // the debit was not successful so ignoring it
            vlogDebug("Payment group {0} ignoring debit since it was not successful");
          }
        }
     balanceAmt=lPaymentGroupAmount-getPricingTools().round(lAlreadyDebitedAmount);
	  vlogDebug("Balance amount set to {0}", balanceAmt);
	  vlogDebug("Exiting getReAuthAmount : pPaymentGroup");
	  return balanceAmt;
  }

  /**
   * A utility method to compare the transaction time in
   * <code>java.util.Date</code> is older by certain number of days
   * 
   * @param transactionTime
   *          the date to be compared against
   * @param pDaysOld
   *          the number of days
   * @return true if transactionTime is older than pDaysOld
   */
  protected boolean transactionExpiredCheckInDays(Date transactionTime, int pDaysOld) {

    vlogDebug("Entering transactionExpired - transactionTime, daysOld");
    boolean transactionExpired = false;
    long lTransactionTime = transactionTime.getTime();

    long lTodaysTime = (new Date()).getTime();

    if (lTransactionTime < (lTodaysTime - (pDaysOld * MILLISECONDS_IN_A_DAY))) {
      transactionExpired = true;
    }

    vlogDebug("Exiting transactionExpired - transactionTime, daysOld");
    return transactionExpired;
  }
  
  /**
   * A utility method to compare the transaction time in
   * <code>java.util.Date</code> is older by certain number of days
   * 
   * @param transactionTime
   *          the date to be compared against
   * @param pDaysOld
   *          the number of days
   * @return true if transactionTime is older than pDaysOld
   */
  protected boolean transactionExpired(Date authExpirationDate) {

    vlogDebug("Entering transactionExpired - transactionTime, daysOld");
    boolean transactionExpired = false;
    long expirationTime = authExpirationDate.getTime();

    long lTodaysTime = (new Date()).getTime();

    if (expirationTime < lTodaysTime) {
      transactionExpired = true;
    }

    vlogDebug("Exiting transactionExpired - transactionTime, daysOld");
    return transactionExpired;
  }

  /**
   * Determines is the auth status has expired based on the expiry time period
   * defined for the payment type and the timestamp on the authorization status.
   * For gift card we do not have an expiry date, hence this check is applicable
   * only for credit cards.
   * 
   * @param pPaymentStatus
   *          the payment status
   * @return <code>true</true> if the payment auths status is older than the configured value for the payment type.
   */
  protected boolean paymentAuthStatusExpired(PaymentStatus pPaymentStatus) {
    vlogDebug("Entering paymentAuthStatusExpired - pPaymentStatus");

    boolean retValue = false;

    if (pPaymentStatus.getTransactionSuccess()) {

      // for credit card we look at the transactionTimestamp. Gift card are not
      // needed at this time.
      if (pPaymentStatus.getClass().getName().equalsIgnoreCase("com.aci.payment.creditcard.AciCreditCardStatus")) {
    	if(isUseCreditCardExpiryCheckWithDays()){
    		retValue=transactionExpiredCheckInDays(pPaymentStatus.getTransactionTimestamp(),creditCardExpiryTimeInDays);
    	}
    	else {
	    	Date expiryDate=((AciCreditCardStatus)pPaymentStatus).getAuthorizationExpiration();
	    	if(expiryDate!=null){
	    		retValue = transactionExpired(expiryDate);
	    	}
    	}
      } else {
        retValue = false;
      }

    } else {
      vlogWarning("Last payment auth status was not successful for payment status id {0}, assuming that the payment auth has expired", pPaymentStatus.getTransactionId());
    }
    vlogDebug("Exiting paymentAuthStatusExpired - pPaymentStatus");
    return retValue;
  }

  /**
   * The following method is overriden to handle the restriction around
   * paypal. For Paypal the credits have to be applied based on the amount
   * debited. If there are multiple number of debits the credits also match
   * the debits. For e.g. a $100 amount applied towards a paypal payment group
   * and there are 2 debits for $40 followed by a $60. A credit for $70 should
   * be split as a 60 credit against the debit of $60 and a $10 credit against
   * the first debit.
   * 
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void credit(Order pOrder, PaymentGroup pPaymentGroup, double pAmount) throws CommerceException {
    
    vlogDebug("Entering credit - pOrder, pPaymentGroup, pAmount");
    HashMap<String, Double> creditMap = constructCreditMap(pPaymentGroup, pAmount);
      List debits = pPaymentGroup.getDebitStatus();
      for (Object oDebit : debits) {
        PaymentStatus debit = (PaymentStatus) oDebit;
        if (debit.getTransactionSuccess()) {
          String debitTransactionId = debit.getTransactionId();
          Double creditAmtDouble = creditMap.get(debitTransactionId);

          if (creditAmtDouble != null && creditAmtDouble.doubleValue() > 0) {
            super.credit(pOrder, pPaymentGroup, creditAmtDouble.doubleValue());
            vlogInfo("credit : pg id {0}, amount {1}", pPaymentGroup.getId(), creditAmtDouble.doubleValue());
          }
        }
      }

    vlogDebug("Exiting credit - pOrder, pPaymentGroup, pAmount");
  }
  

  /**
   * The following method is used to construct a map of payment status ids and
   * the amount to be credited based on the debits and credits so far. This
   * will be used to call the refund methods in the credit method.
   * 
   * One of the key assumption is that the payment status returned by the
   * payment group is a sorted list of debits and credits. This is driven off
   * of the repository definition where in payment status has a multi-column
   * 
   * 
   * @param pPaymentGroup
   *            the CC payment group for which the credit is being
   *            processed
   * @param pAmount
   *            the amount to be credited
   */
  @SuppressWarnings("rawtypes")
  public HashMap<String, Double> constructCreditMap(PaymentGroup pPaymentGroup, double pAmount) {

    // credit map is a mapping of all the debit status and the amount that
    // we are planning to credit for each of the debits
    HashMap<String, Double> creditMap = new HashMap<String, Double>();

    // The balance amt map contains the balance amt in each of the debits
    // that is available for the crediting. Arrived at based on the total
    // refunds performed so far
    HashMap<String, Double> balanceAmtMap = new HashMap<String, Double>();

    List debitStatuses = null;
    double totalRefundSoFar = 0.0;

    // Lets start by getting all the list of debit status
    debitStatuses = pPaymentGroup.getDebitStatus();
    if (debitStatuses == null || debitStatuses.size() == 0) {
      vlogWarning ("No debit status found, pg id {0}, amount {1}", pPaymentGroup.getId(), pAmount);
    }

    // Lets get started by adding 0s to all the available debits in the
    // creditMap and lets set all the balances to the initial amt debited in
    // the balanceAmtMap
    for (Object oPaymentStatus : debitStatuses) {
      PaymentStatus debitStatus = (PaymentStatus) oPaymentStatus;
      if (debitStatus.getTransactionSuccess()) {
        String debitTransactionId = debitStatus.getTransactionId();
        creditMap.put(debitTransactionId, new Double(0d));
        balanceAmtMap.put(debitTransactionId, new Double(debitStatus.getAmount()));
      }
    }
    totalRefundSoFar = pPaymentGroup.getAmountCredited();

    vlogDebug("Total refund so far = " +  totalRefundSoFar);
    // now determine what is the balance amt in each of the debits
    if (totalRefundSoFar > 0) {
      for (Object oDebitStatus : debitStatuses) {
        PaymentStatus debit = (PaymentStatus) oDebitStatus;
        if (debit.getTransactionSuccess() && totalRefundSoFar > 0) {
          // consider only the transactions that have been marked as
          // successful. Ignoring the rest
          String debitTransactionId = debit.getTransactionId();
          if (totalRefundSoFar >= debit.getAmount()) {
            
            vlogDebug("Setting the balance amt to 0 for transaction id " + debitTransactionId);
            
            balanceAmtMap.put(debitTransactionId, new Double(0d));
            totalRefundSoFar = totalRefundSoFar - debit.getAmount();
            
            vlogDebug("Total refund amount set to " + totalRefundSoFar);

          } else {
            double balanceAmt = debit.getAmount() - totalRefundSoFar;
            vlogDebug("Setting the balance amt to " + balanceAmt + " for transaction id " + debitTransactionId);
            balanceAmtMap.put(debitTransactionId, new Double(balanceAmt));
            totalRefundSoFar = 0;
            break;
          }
        }
      }
      if (totalRefundSoFar > 0) {
        vlogWarning ("Total refund so far more than 0, pg id {0}, amount {1}", pPaymentGroup.getId(), pAmount);
      }
    }

    // Now based on the balanceAmtMap compute the credit map
    double remainingRefund = pAmount;

    
    vlogDebug("Computing the credit map for the amount " + pAmount);
    for (Object oDebitStatus : debitStatuses) {
      PaymentStatus debit = (PaymentStatus) oDebitStatus;

      if (debit.getTransactionSuccess() && remainingRefund > 0) {
        // consider only the transactions that have been marked as
        // successful. Ignoring the rest
        String debitTransactionId = debit.getTransactionId();
        Double balanceAmtDouble = balanceAmtMap.get(debitTransactionId);
        if (balanceAmtDouble == null) {
          // throw an exception here
        }
        double balanceAmt = balanceAmtDouble.doubleValue();
        if (balanceAmt > 0) {
          if (balanceAmt >= remainingRefund) {

            // enough funds are there to cover this refund
            // just set the creditMap to the remaining refund
            // and set the remaining refund to 0
           
            vlogDebug("Setting the credit map value for " + debitTransactionId + " to the remain refund of " + remainingRefund);
            creditMap.put(debitTransactionId, remainingRefund);
            remainingRefund = 0d;

          } else {
            // the balance left on the debit is not enough to
            // cover the entire refund. So lets get what we can
            // from the debit by first refunding the balance.
            // And spread the rest of the balance to other
            // debits.
            vlogDebug("Setting the credit map value for " + debitTransactionId + " to the balance of " + balanceAmt);
            creditMap.put(debitTransactionId, balanceAmtDouble);
            remainingRefund = remainingRefund - balanceAmtDouble;
          }
        } else {
          vlogDebug("No amount available to credit for " + debitTransactionId);
          // don't do any thing here. Move on to the next debit
        }
      }
    }

    if (remainingRefund > 0) {
      vlogWarning ("Remaining refund > 0 pg id {0}, amount {1}", pPaymentGroup.getId(), pAmount);
    }

    return creditMap;

  }
  
  /**
   * Pre Process Decrease or Reverse Authorization for cancelled items or orders
   */
  protected void preProcessDecreaseAuthorization(Order pOrder, PaymentGroup pPaymentGroup, double pDecreaseAmount)
          throws CommerceException
      {
			if(pPaymentGroup == null)
          {
				createErrorPaymentStatus(3, pPaymentGroup, pDecreaseAmount, Constants.INVALID_PAYMENT_GROUP);
				throw new InvalidParameterException(Constants.INVALID_PAYMENT_GROUP);
          }
			if(pDecreaseAmount < 0.0D)
          {
				createErrorPaymentStatus(3, pPaymentGroup, pDecreaseAmount, Constants.INVALID_AUTH_AMOUNT);
				throw new InvalidParameterException(Constants.INVALID_AUTH_AMOUNT);
          } else
          {
				return;
          }
      }
  
  /**
   * Set the decrease amount only if parital order is cancelled
   */
  protected void postProcessDecreaseAuthorization(PaymentGroup pPaymentGroup, PaymentStatus pStatus, double pDecreaseAmount)
          throws CommerceException
      {
			if(pStatus != null)
          {
				pPaymentGroup.addAuthorizationStatus(pStatus);
				if(pStatus.getTransactionSuccess())
				{
					if(pPaymentGroup.getAmountAuthorized()!=pDecreaseAmount){
						pPaymentGroup.setAmountAuthorized(pPaymentGroup.getAmountAuthorized() - pDecreaseAmount);
					}
					int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorized");
					pPaymentGroup.setState(state);
              } 
			
				/**
			else
				{
					int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorize_failed");
					pPaymentGroup.setState(state);
					pPaymentGroup.setStateDetail(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
						pPaymentGroup.getId(), pStatus.getErrorMessage()
                  }));
						throw new PaymentException(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
						pPaymentGroup.getId(), pStatus.getErrorMessage()
                  }));
              }
              **/
          }
      }
  
  
  /**
   * The following method is responsible for reversing an authorization on a
   * payment group if supported. Currently only gift cards are supported. For a
   * gift card we just reverse the authorization by performing a debit operation
   * of $0 which voids the hold on the card.
   * 
   * @param lPaymentGroup
   *          the payment group for which the authorization needs to be reversed
   * @throws CommerceException
   */
  public void reverseAuthorizationDuringSettlement(PaymentGroup lPaymentGroup,double amountToReverse) throws CommerceException {
    vlogDebug("Entering reverseAuthorization : lPaymentGroup");
    
    if(lPaymentGroup instanceof AciCreditCard){
    	try {
    		Order order = lPaymentGroup.getOrderRelationship().getOrder();
    		AciCreditCard aciCreditCard=(AciCreditCard)lPaymentGroup;
    		
    		if(getAciService().getAciConfiguration().getReversalSupportedCards().contains(aciCreditCard.getMopTypeCode().trim())){
	    		double amtToReverse=getPricingTools().round(amountToReverse);
		    	decreaseAuthorization(order, lPaymentGroup, amtToReverse);
		    	lPaymentGroup.setAmountAuthorized(0.0);
    		}
    		 vlogInfo("Reversed authorization: pg id {0}", lPaymentGroup.getId());
	    }
	    catch(Exception ex){
	    	vlogError("Reverse Authorization: Error occurred while reversing authorization",ex);
	    }
    }

    else if (lPaymentGroup instanceof MFFGiftCardPaymentGroup) {
      
      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = (MFFGiftCardPaymentGroup) lPaymentGroup;
      
      // get the last authorization that was performed on the gift card to get
      // handle on the lock id.
      MFFGiftCardPaymentStatus lLastAuthStatus = (MFFGiftCardPaymentStatus) getLastAuthorizationStatus(lPaymentGroup);

      String lEAN = lGiftCardPaymentGroup.getEan();
      String lLocalLockId = lLastAuthStatus.getLocalLockId();
      String lGiftCardNumber = lGiftCardPaymentGroup.getCardNumber();

      try {
        getFirstDataService().debit(lGiftCardNumber, 0.0d, lLocalLockId, lEAN);
      } catch (Exception e) {
        String lErrorMessage = String.format("Could not reverse the authorization for the payment group id %s", lPaymentGroup.getId());
        throw new CommerceException(lErrorMessage, e);
      }
      vlogInfo("Reversed authorization: pg id {0}", lPaymentGroup.getId());
    }
 
    vlogDebug("Exiting reverseAuthorization : lPaymentGroup");
  }
  
  public int getCreditCardExpiryTimeInDays() {
    return creditCardExpiryTimeInDays;
  }

  public void setCreditCardExpiryTimeInDays(int pCreditCardExpiryTimeInDays) {
    creditCardExpiryTimeInDays = pCreditCardExpiryTimeInDays;
  }
  public FirstDataService getFirstDataService() {
    return firstDataService;
  }
  public void setFirstDataService(FirstDataService pFirstDataService) {
    firstDataService = pFirstDataService;
  }

  public CreditCardStatus reAuthorize(Order pOrder, PaymentGroup pPaymentGroup, double pAmount)throws CommerceException
  {
	PaymentStatus status;
	double authAmount=getPricingTools().round(pAmount);
  	if(getIgnoreTransactionsWithZeroAmount() && authAmount == 0.0D)
  	{
  		if(isLoggingDebug())
  			logDebug((new StringBuilder()).append("Ignoring PaymentGroup ").append(pPaymentGroup.getId()).append(" because amount is 0.0").toString());
  			status = new PaymentStatusImpl(Long.toString(System.currentTimeMillis(), 10), pAmount, true, null, new Date());
  	}
  	 
  	else {
  	
	  	try
	  	{
	  		String chainName = getAuthorizationChainName(pPaymentGroup);
	  		PaymentManagerPipelineArgs args = new PaymentManagerPipelineArgs();
	  		args.setOrder(pOrder);
	  		args.setPaymentManager(this);
	  		args.setPaymentGroup(pPaymentGroup);
	  		args.setAmount(authAmount);
	  		args.setAction(PaymentManagerAction.AUTHORIZE);
	  		runProcessorChain(chainName, args);
	  		status = args.getPaymentStatus();
	  	}
	  	catch(CommerceException e)
	  	{
	  		status=new PaymentStatusImpl(Long.toString(System.currentTimeMillis(), 10), authAmount, false, e.getMessage(), new Date());
	  		if(isLoggingDebug())
	  			logDebug(e.toString());
	  		e.printStackTrace();
	  	}
	  }
  	return (CreditCardStatus)status;
  }
  
  public void postProcessReAuthorizeCancellation(PaymentGroup pPaymentGroup, PaymentStatus pStatus, double pAmount,double pAmountToReverse)
          throws CommerceException
      {
	  	
	  	if(isLoggingDebug())
	  		logDebug((new StringBuilder()).append("Authorized PaymentGroup ").append(pPaymentGroup.getId()).append(". Results[").append(pStatus.getTransactionId()).append(", ").append(pStatus.getTransactionSuccess()).append(",").append(pStatus.getErrorMessage()).append(",").append(pStatus.getTransactionTimestamp()).append("]").toString());
	  	pAmount=getPricingTools().round(pAmount);
	  	pPaymentGroup.addAuthorizationStatus(pStatus);
	  	if(pStatus.getTransactionSuccess())
          {
	  		pPaymentGroup.setAmountAuthorized(pAmount);
	  		double amount=getPricingTools().round(pPaymentGroup.getAmount());
	  		double amountToReverse=getPricingTools().round(pAmountToReverse);
	  		double remainingAmount=getPricingTools().round(amount-amountToReverse);
	  		pPaymentGroup.setAmount(remainingAmount);
	  		int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorized");
	  		pPaymentGroup.setState(state);
          } else
          {
        	  int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorize_failed");
        	  pPaymentGroup.setState(state);
        	  pPaymentGroup.setStateDetail(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
        			  pPaymentGroup.getId(), pStatus.getErrorMessage()
              }));
        	  throw new PaymentException(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
        			  pPaymentGroup.getId(), pStatus.getErrorMessage()
              }));
          }
      }

  public void postProcessReAuthorization(PaymentGroup pPaymentGroup, PaymentStatus pStatus, double pAmount)
          throws CommerceException
      {
	  	if(isLoggingDebug())
	  		logDebug((new StringBuilder()).append("Authorized PaymentGroup ").append(pPaymentGroup.getId()).append(". Results[").append(pStatus.getTransactionId()).append(", ").append(pStatus.getTransactionSuccess()).append(",").append(pStatus.getErrorMessage()).append(",").append(pStatus.getTransactionTimestamp()).append("]").toString());
	  	pAmount=getPricingTools().round(pAmount);
	  	pPaymentGroup.addAuthorizationStatus(pStatus);
	  	if(pStatus.getTransactionSuccess())
          {
	  		pPaymentGroup.setAmountAuthorized(pAmount);
	  		int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorized");
	  		pPaymentGroup.setState(state);
          } else
          {
        	  int state = StateDefinitions.PAYMENTGROUPSTATES.getStateValue("authorize_failed");
        	  pPaymentGroup.setState(state);
        	  pPaymentGroup.setStateDetail(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
        			  pPaymentGroup.getId(), pStatus.getErrorMessage()
              }));
        	  throw new PaymentException(MessageFormat.format(Constants.PAYMENT_GROUP_AUTH_FAILURE, new Object[] {
        			  pPaymentGroup.getId(), pStatus.getErrorMessage()
              }));
          }
      }
  

  
}
