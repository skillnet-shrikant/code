package com.aci.payment.creditcard.processor;

import java.text.DecimalFormat;
import java.util.Date;

import atg.nucleus.GenericService;
import atg.payment.PaymentStatus;
import atg.payment.creditcard.CreditCardInfo;
import atg.payment.creditcard.CreditCardProcessor;
import atg.payment.creditcard.CreditCardStatus;
import atg.payment.creditcard.DecreaseCreditCardAuthorizationProcessor;

import com.aci.commerce.service.AciService;
import com.aci.configuration.AciConfiguration;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.payment.creditcard.AciCreditCardStatus;

/**
 * This class will handle ACI credit card integration in the checkout pipeline.
 * 
 * @author DMI
 *
 */

public class AciCreditCardProcessor extends GenericService implements CreditCardProcessor, DecreaseCreditCardAuthorizationProcessor {

  private AciService mAciService;

  public AciService getAciService() {
    return mAciService;
  }

  public void setAciService(AciService pAciService) {
    mAciService = pAciService;
  }
  
  public long getAuthThreshold(){
	  int authExpiryThreshold=getAciService().getAciConfiguration().getAuthExpiryThreshold();
	  if(authExpiryThreshold==0){
		  authExpiryThreshold=7;
	  }
	  return (authExpiryThreshold*24*60*60*1000);
  }
  
  public long getCreditThreshold(){
	  int creditExpiryThreshold=getAciService().getAciConfiguration().getCreditExpiryThreshold();
	  if(creditExpiryThreshold==0){
		  creditExpiryThreshold=7;
	  }
	  return (creditExpiryThreshold*24*60*60*1000);
  }
  
  public long getDebitThreshold(){
	  int debitExpiryThreshold=getAciService().getAciConfiguration().getDebitExpiryThreshold();
	  if(debitExpiryThreshold==0){
		  debitExpiryThreshold=7;
	  }
	  return (debitExpiryThreshold*24*60*60*1000);
  }

  @Override
  public CreditCardStatus decreaseAuthorization(CreditCardInfo pCreditcardinfo, PaymentStatus pPaymentstatus) {
    if (isLoggingDebug()){
    	logLastFourCreditCardDigits(pCreditcardinfo, "DecreaseAuthorization");
    }
    double reversalAmount=pCreditcardinfo.getAmount();
  	DecimalFormat f=new DecimalFormat("#.##");
  	String decimalAuthAmount=f.format(reversalAmount);
  	double newRevAmount=Double.parseDouble(decimalAuthAmount);
    AciCreditCardStatus retValue=new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), (-1*newRevAmount), true, "", new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));
    AciCreditCardInfo ccInfo=(AciCreditCardInfo)pCreditcardinfo;
    try{
    	AciConfiguration aciConfiguration=getAciService().getAciConfiguration();
    	if(aciConfiguration!=null){
    		if(aciConfiguration.isUseAuthReversal()){
    			AciCreditCardStatus lcreditCardStatus=(AciCreditCardStatus)pPaymentstatus;
    			String authCode=lcreditCardStatus.getAuthCode();
    			String reqId=lcreditCardStatus.getTransactionId();
		    	String originalJournalKey=lcreditCardStatus.getOriginalJournalKey();
		    	String originalAuthDate=lcreditCardStatus.getOriginalAciAuthDate();
		    	String originalAuthTime=lcreditCardStatus.getOriginalAciAuthTime();
		    	String cvvVerificationCode=lcreditCardStatus.getCvvVerificationCode();
		    	String avsCode=lcreditCardStatus.getAvsCode();
		    	ccInfo.setAuthReversalAmount(newRevAmount);
		    	ccInfo.setAuthorizationNumber(authCode);
		    	ccInfo.setOriginalJournalKey(originalJournalKey);
		    	ccInfo.setCvvVerificationCode(cvvVerificationCode);
		    	ccInfo.setAvsCode(avsCode);
		    	ccInfo.setPaymentSource(lcreditCardStatus.getAuthorizationSource());
		    	if(aciConfiguration.isUseReqIdForAuthReversal()){
		    		ccInfo.setOriginalReqId(reqId);
			    	if(aciConfiguration.isUseOriginalAuthDateTime()){
			    		ccInfo.setOriginalAuthDate(originalAuthDate);
			    		ccInfo.setOriginalAuthTime(originalAuthTime);
			    	}
		    	}
		    	retValue=getAciService().reversePreviousAuthorization(ccInfo);
    		}
    	}
    	return retValue;
    }
    catch(Exception ex){
    	vlogError("Reverse Authorization: Error occurred while reversing authorization",ex);
    	return new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), pCreditcardinfo.getAmount(), false, ex.getMessage(), new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));
    }
    
    
  }

  @Override
  public CreditCardStatus authorize(CreditCardInfo pCreditcardinfo) {
    if (isLoggingDebug()) logLastFourCreditCardDigits(pCreditcardinfo, "authorize");
    AciCreditCardInfo ccInfo = (AciCreditCardInfo) pCreditcardinfo;
    try {
      return getAciService().authorizeCreditCard(ccInfo);
    } catch (Exception ex) {
      vlogError(ex, "An exception occurred while trying to authorize the following payment group id {0}, amount = {1}", pCreditcardinfo.getPaymentId(), pCreditcardinfo.getAmount());
      return new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), pCreditcardinfo.getAmount(), false, ex.getMessage(), new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));
    }

  }

  @Override
  public CreditCardStatus debit(CreditCardInfo pCreditcardinfo, CreditCardStatus pAuthStatus) {
    if (isLoggingDebug()) logLastFourCreditCardDigits(pCreditcardinfo, "debit");
    AciCreditCardInfo ccInfo = (AciCreditCardInfo) pCreditcardinfo;
    AciCreditCardStatus ccStatus = (AciCreditCardStatus) pAuthStatus;
    try {
      return getAciService().debitCreditCard(ccInfo, ccStatus);
    } catch (Exception ex) {
      vlogError(ex, "An exception occurred while trying to debit the following payment group id {0}, amount = {1}", pCreditcardinfo.getPaymentId(), pCreditcardinfo.getAmount());
      return new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), pCreditcardinfo.getAmount(), false, ex.getMessage(), new Date(), new Date(System.currentTimeMillis() + getDebitThreshold()));
    }
  }

  @Override
  public CreditCardStatus credit(CreditCardInfo pCreditcardinfo, CreditCardStatus pDebitStatus) {
    if (isLoggingDebug()) logLastFourCreditCardDigits(pCreditcardinfo, "credit");

    AciCreditCardInfo ccInfo = (AciCreditCardInfo) pCreditcardinfo;
    AciCreditCardStatus ccStats = (AciCreditCardStatus) pDebitStatus;
    try {
      return getAciService().creditCreditCard(ccInfo, ccStats);
    } catch (Exception ex) {
      vlogError(ex, "An exception occurred while trying to credit the following payment group id {0}, amount = {1}", pCreditcardinfo.getPaymentId(), pCreditcardinfo.getAmount());
      return new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), pCreditcardinfo.getAmount(), false, ex.getMessage(), new Date(), new Date(System.currentTimeMillis() + getCreditThreshold()));
    }

  }

  @Override
  public CreditCardStatus credit(CreditCardInfo pCreditcardinfo) {
    logLastFourCreditCardDigits(pCreditcardinfo, "credit");
    return new AciCreditCardStatus(Long.toString(System.currentTimeMillis()), pCreditcardinfo.getAmount(), true, "", new Date(), new Date(System.currentTimeMillis() + getCreditThreshold()));
  }

  private void logLastFourCreditCardDigits(CreditCardInfo pCreditCardInfo, String pLogType) {
    if (isLoggingInfo()) {
      String tokenNumber = "";
      String lastFourDigits = "";
      if (pCreditCardInfo != null) {
        tokenNumber = ((AciCreditCardInfo) pCreditCardInfo).getTokenNumber();
        if (tokenNumber != null && tokenNumber.length() > 4) lastFourDigits = tokenNumber.substring(tokenNumber.length() - 4);
      }
      vlogDebug((new StringBuilder()).append(pLogType).append(" credit card **** **** **** ").append(lastFourDigits).append(" for ").append(pCreditCardInfo.getAmount()).toString());
    }
    
  }

}