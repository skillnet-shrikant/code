package com.aci.commerce.service;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;
import atg.core.util.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.aci.configuration.AciConfiguration;
import com.aci.constants.FieldMappingConstants;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.payment.creditcard.AciCreditCardStatus;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.result.AciPipelineResult;
import com.aci.utils.AciSettlementUtil;

public class AciService extends GenericService {

  public static final String TRANS_TYPE_DEBIT = "debit";
  public static final String TRANS_TYPE_CREDIT = "credit";
  private AciSettlementUtil mAciSettlementUtil;
  private AciConfiguration mAciConfiguration;
  private Repository mOrderRepository;
  private PipelineManager mAciPipelineManager;

  public AciSettlementUtil getAciSettlementUtil() {
    return mAciSettlementUtil;
  }

  public void setAciSettlementUtil(AciSettlementUtil pAciSettlementUtil) {
    mAciSettlementUtil = pAciSettlementUtil;
  }
  
  public AciConfiguration getAciConfiguration() {
    return mAciConfiguration;
  }

  public void setAciConfiguration(AciConfiguration pAciConfiguration) {
    mAciConfiguration = pAciConfiguration;
  }

  public Repository getOrderRepository() {
    return mOrderRepository;
  }

  public void setOrderRepository(Repository pOrderRepository) {
    mOrderRepository = pOrderRepository;
  }

  public PipelineManager getAciPipelineManager() {
    return mAciPipelineManager;
  }

  public void setAciPipelineManager(PipelineManager pPipelineManager) {
    mAciPipelineManager = pPipelineManager;
  }
  
 
 public String screenForFraud(Order pOrder) {
    vlogDebug("AciService:screenForFraud:Start");
    try {

      AciPipelineProcessParam pParams = new AciPipelineProcessParam();
      pParams.setAciConfiguration(getAciConfiguration());
      pParams.setTransactionAction(getAciConfiguration().getFraudTransactionAction());
      pParams.setDivNum(getAciConfiguration().getDiv_Num_Fraud_Only());
      pParams.setCurrencyCode(getAciConfiguration().getDefaultCurrencyCode());
      pParams.setSubclientId(getAciConfiguration().getFraudSubClientId());
      pParams.setMopType(getAciConfiguration().getDefaultMopType());
      pParams.setReqType("E"); // This line has to be changed
      pParams.setOrder(pOrder);
      AciPipelineResult result = (AciPipelineResult) getAciPipelineManager().runProcess(getAciConfiguration().getFraudScreenPipelineChainName(), pParams);
      vlogDebug("Transaction status is: " + result.isTransactionSuccess());
      vlogDebug("Fraud status is: " + result.isFraudTransactionSuccess());
      vlogDebug("AciService:screenForFraud:End");
      if (result.isTransactionSuccess()) {
        if (result.isFraudTransactionSuccess()) {
          String statusCode = getAciConfiguration().getFraudStatCdMap().get(result.getFraudResult().toUpperCase());
          return statusCode;
        } else {
          vlogWarning("screenForFraud(): Aci Fraud Transaction not successful: ");
          return "FRAUD_ERROR";
        }
      } else {
        vlogWarning("screenForFraud(): Aci Transaction not successful: ");
        return "FRAUD_ERROR";
      }
    } catch (Exception exception) {    
      vlogWarning(exception,"screenForFraud(): Exception occurred: " );
      vlogDebug("AciService:screenForFraud():End");
      return "FRAUD_ERROR";
    }

  }

  public AciCreditCardStatus authorizeCreditCard(AciCreditCardInfo pAciCreditCardInfo) throws AciPipelineException {
    vlogDebug("AciService:authorizeCreditCard:Start");

    
    AciCreditCardStatus authorizationStatus = new AciCreditCardStatus("", pAciCreditCardInfo.getAmount(), true, "", new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));

    
    
    if (pAciCreditCardInfo.getAmount() == 0.0d) {
      return authorizationStatus;
    }

    if (testAuthorizationDeclines) {
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    }
    AciPipelineProcessParam pParams = new AciPipelineProcessParam();
    pParams.setAciConfiguration(getAciConfiguration());
    pParams.setTransactionAction(getAciConfiguration().getAuthorizeTransactionAction());
    pParams.setDivNum(getAciConfiguration().getDiv_Num_CC());
    pParams.setCurrencyCode(getAciConfiguration().getDefaultCurrencyCode());
    pParams.setSubclientId(getAciConfiguration().getCreditCardSubClientId());
    pParams.setOriginalJournalKey(this.getAciSettlementUtil().buildJournalKey());
    if (pAciCreditCardInfo.isOnlineOrder()) {
      pParams.setReqType(getAciConfiguration().getOnlineOrderReqType());
    } else {
      pParams.setReqType(getAciConfiguration().getCscOrderReqType());
    }

    String mopType = pAciCreditCardInfo.getMopTypeCd();
    if (StringUtils.isEmpty(mopType)) {
      mopType = (String) (getAciConfiguration().getCardCodesMap().get(pAciCreditCardInfo.getCreditCardType()));
    }

    if (mopType != null) {
      pParams.setMopType(mopType);
    } else {
      vlogWarning("AciService:authorizeCreditCard(): Credit Card Type is needed");
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    }
    pParams.setAciCreditCardInfo(pAciCreditCardInfo);

    try {

      AciPipelineResult result = (AciPipelineResult) getAciPipelineManager().runProcess(getAciConfiguration().getAuthorizePipelineChainName(), pParams);

      if (result.isTransactionSuccess()) {
        vlogDebug("authorizeCreditCard(): Auth code being set: " + result.getAuthorizationCode());
        authorizationStatus = new AciCreditCardStatus(result.getAciReqId(), pAciCreditCardInfo.getAmount(), true, "", new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));

        String aciAuthDate=result.getOriginalAciAuthDate();
        if(!StringUtils.isEmpty(aciAuthDate)){
        	authorizationStatus.setOriginalAciAuthDate(aciAuthDate);
		}
        String aciAuthTime=result.getOriginalAciAuthTime();
        if(!StringUtils.isEmpty(aciAuthTime)){
        	authorizationStatus.setOriginalAciAuthTime(aciAuthTime);
		}
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat lSimpleTimeFormat = new SimpleDateFormat("hhmmss");
        Date lNow = new Date();
        String lDate = lSimpleDateFormat.format(lNow);
        String lTime = lSimpleTimeFormat.format(lNow);
        // authrizatinoTime, authorizationdate, TransactionDate
        authorizationStatus.setTransactionDate(lDate);
        authorizationStatus.setAuthorizationDate(lDate);
        authorizationStatus.setAuthorizationTime(lTime);
        authorizationStatus.setOriginalJournalKey(result.getOriginalJournalKey());
        authorizationStatus.setOriginalReqId(result.getAciReqId());
        //
        authorizationStatus.setAuthCode(result.getAuthorizationCode());
        String avsCode = result.getAddressValidationCode();
        if (!StringUtils.isEmpty(avsCode)) {
          authorizationStatus.setAvsCode(avsCode.trim().toUpperCase());
          if (getAciConfiguration().isEnableAvsCheck()) {
            if (!avsValidationCheck(avsCode)) {
              vlogWarning("authorizeCreditCard(): Aci Transaction AVS check enabled and AVS not success ");
              vlogDebug("AciService:authorizeCreditCard():End");
              throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
            }

          }
        } else {
          if (!StringUtils.isEmpty(getAciConfiguration().getAvsVerificationDefaultValue())) {
            authorizationStatus.setAvsCode(getAciConfiguration().getAvsVerificationDefaultValue().trim().toUpperCase());
          } else {
            authorizationStatus.setAvsCode("U");
          }
        }
        String cvvVerificationCode = result.getCvvValidationCode();
        if (!StringUtils.isEmpty(cvvVerificationCode)) {
          authorizationStatus.setCvvVerificationCode(cvvVerificationCode.trim().toUpperCase());
          if (getAciConfiguration().isEnableCVVCheck()) {
            if (!cvvValidationCheck(cvvVerificationCode)) {
              vlogWarning("authorizeCreditCard(): Aci Transaction CVV check enabled and CVV not success ");
              vlogDebug("AciService:authorizeCreditCard():End");
              throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
            }

          }
        } else {
          if (!StringUtils.isEmpty(getAciConfiguration().getCvvVerificationDefaultValue())) {
            authorizationStatus.setCvvVerificationCode(getAciConfiguration().getCvvVerificationDefaultValue().trim().toUpperCase());
          } else {
            authorizationStatus.setCvvVerificationCode("U");
          }
        }
        authorizationStatus.setCallType(result.getCallType());
        authorizationStatus.setResponseCode(result.getPaymentResponseCode());
        authorizationStatus.setResponseCodeMsg(result.getPaymentResponseMessage());
        authorizationStatus.setAuthorizationSource(getAciConfiguration().getPaymentSource());
        vlogDebug("AciService:authorizeCreditCard():End");
        vlogDebug("Successfully authorized the amount {0}, payment group id {1}", pAciCreditCardInfo.getAmount(), pAciCreditCardInfo.getPaymentId());
        return authorizationStatus;
      } else {
        vlogWarning("authorizeCreditCard(): Aci Transaction is not successful ");
        vlogDebug("AciService:authorizeCreditCard():End");
        throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
      }

    } catch (RunProcessException exception) {
      vlogError(exception, "authorizeCreditCard(): RunProcessException occurred");
      vlogDebug("AciService:authorizeCreditCard():End");
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    } catch (Exception ex) {
      vlogError(ex, "authorizeCreditCard(): Exception occurred: ");
      vlogDebug("AciService:authorizeCreditCard():End");
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    }
  }
  
  private boolean cvvValidationCheck(String cvvCode){
	  boolean cvvEnabledCheck=false;
	  if(!StringUtils.isBlank(cvvCode) && getAciConfiguration().getCvvVerificationSuccessList().contains(cvvCode.toUpperCase().trim())){
		  cvvEnabledCheck=true;
	  }
	  else if(getAciConfiguration().getCvvVerificationNotCarriedList().contains(cvvCode.toUpperCase().trim())){
		  cvvEnabledCheck=true;
	  }
	  else if(getAciConfiguration().getCvvVerificationFailureList().contains(cvvCode.toUpperCase().trim())){
		  cvvEnabledCheck=false;
	  }
	  return cvvEnabledCheck;
  }
  
  private boolean avsValidationCheck(String avsCode){
	  boolean avsEnabledCheck=false;
	  if(!StringUtils.isBlank(avsCode) && getAciConfiguration().getAvsVerificationSuccessList().contains(avsCode.toUpperCase().trim())){
		  avsEnabledCheck=true;
	  }
	  else if(!StringUtils.isBlank(avsCode) && getAciConfiguration().getAvsVerificationNotCarriedList().contains(avsCode.toUpperCase().trim())){
		  avsEnabledCheck=true;
	  }
	  else if(!StringUtils.isBlank(avsCode) && getAciConfiguration().getAvsVerificationFailureList().contains(avsCode.toUpperCase().trim())){
		  avsEnabledCheck=false;
	  }
	  return avsEnabledCheck;
  }

  public void tokenizeCreditCard(AciCreditCardInfo pAciCreditCardInfo) throws AciPipelineException {
    vlogDebug("AciService:tokenizeCreditCard:Called");
    AciPipelineProcessParam pParams = new AciPipelineProcessParam();
    if(!StringUtils.isEmpty(pAciCreditCardInfo.getSecurityCode())){
    	if(getAciConfiguration().isEnableCVVCheck()){
    		pParams.setEnableCvvVerification(true);
    	}
    	else {
    		pParams.setEnableCvvVerification(false);
    	}
    }
    else {
    	pParams.setEnableCvvVerification(false);
    }
    pParams.setAciConfiguration(getAciConfiguration());
    pParams.setTransactionAction(getAciConfiguration().getTokenizeTransactionAction());
    pParams.setDivNum(getAciConfiguration().getDiv_Num_CC());
    pParams.setCurrencyCode(getAciConfiguration().getDefaultCurrencyCode());
    pParams.setSubclientId(getAciConfiguration().getCreditCardSubClientId());
    pParams.setOriginalJournalKey(getAciSettlementUtil().buildJournalKey());
    if (pAciCreditCardInfo.isOnlineOrder()) {
      pParams.setReqType(getAciConfiguration().getOnlineOrderReqType());
    } else {
      pParams.setReqType(getAciConfiguration().getCscOrderReqType());
    }

    String mopType = (String)(getAciConfiguration().getCardCodesMap().get(pAciCreditCardInfo.getCreditCardType()));
    
    if(mopType.equalsIgnoreCase(getAciConfiguration().getDefaultMopType())){
    	mopType="??";
    }


    pParams.setMopType(mopType);
    pParams.setAciCreditCardInfo(pAciCreditCardInfo);
    boolean isEIVCCNError=false;
    try {
      AciPipelineResult result = (AciPipelineResult) getAciPipelineManager().runProcess(getAciConfiguration().getTokenizePipelineChainName(), pParams);
      if (testAuthorizationDeclines) throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
      if (result.isTransactionSuccess()) {
        pAciCreditCardInfo.setTokenNumber(result.getTokenNumber());
        String pMopType=result.getMopTypeCd();
        if(!StringUtils.isEmpty(pMopType)){
        	pAciCreditCardInfo.setMopTypeCd(pMopType.trim().toUpperCase());
        }
      } else {
        vlogWarning("tokenizeCreditCard(): Aci Transaction not successful ");
        if(result.getStatusCode().equalsIgnoreCase(FieldMappingConstants.ACI_EIVCCN_CODE)) {
          isEIVCCNError=true;
        }
        throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
      }
      vlogDebug("AciService:tokenizeCreditCard:End");
    } catch (RunProcessException exception) {
      vlogError(exception, "An error occurred while tokenizing the card");
      vlogDebug("tokenizeCreditCard(): RunProcessException occurred: " + exception);
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    } catch (AciPipelineException ex) {
      vlogError(ex, "An error occurred while tokenizing the card");
      vlogDebug("tokenizeCreditCard(): Exception occurred: " + ex, ex);
      throw new AciPipelineException(FieldMappingConstants.ACI_EIVCCN_CODE, true);
    }catch (Exception ex) {
      vlogError(ex, "An error occurred while tokenizing the card");
      vlogDebug("tokenizeCreditCard(): Exception occurred: " + ex, ex);
      throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
    }
  }

  public AciCreditCardStatus debitCreditCard(AciCreditCardInfo pAciCreditCardInfo, AciCreditCardStatus pAciCreditCardStatus) throws AciPipelineException {
    vlogDebug("AciService:debitCreditCard:Start");

    try {

          AciCreditCardStatus debitStatus = new AciCreditCardStatus(pAciCreditCardStatus.getTransactionId(), pAciCreditCardInfo.getAmount(), true, "", new Date(), new Date(System.currentTimeMillis() + getDebitThreshold()));
          if(getAciSettlementUtil().mandatoryFeildsValidator(pAciCreditCardInfo, pAciCreditCardStatus)){
            getAciSettlementUtil().generateSettlementTriggerFile(pAciCreditCardInfo,pAciCreditCardStatus,TRANS_TYPE_DEBIT);
            
            vlogDebug("debitCreditCard(): Auth code being set: {0}", pAciCreditCardStatus.getAuthorizationCode());
            
            SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat lSimpleTimeFormat = new SimpleDateFormat("hhmmss");
            Date lNow      = new Date();
            String lDate   = lSimpleDateFormat.format(lNow);
            String lTime = lSimpleTimeFormat.format(lNow);
            debitStatus.setTransactionDate(lDate);
            debitStatus.setAuthorizationDate(lDate);
            debitStatus.setAuthorizationTime(lTime);
            debitStatus.setAuthCode(pAciCreditCardStatus.getAuthCode());
            debitStatus.setAvsCode(pAciCreditCardStatus.getAvsCode());
            debitStatus.setCvvVerificationCode(pAciCreditCardStatus.getCvvVerificationCode());
            debitStatus.setCallType(pAciCreditCardStatus.getCallType());
            debitStatus.setResponseCode(pAciCreditCardStatus.getResponseCode());
            debitStatus.setResponseCodeMsg(pAciCreditCardStatus.getResponseCodeMsg());
            debitStatus.setAuthorizationSource(getAciConfiguration().getAuthSource());
            debitStatus.setOriginalJournalKey(pAciCreditCardStatus.getOriginalJournalKey());
            
            
          }else{
            
            vlogDebug("debitCreditCard(): Transaction failed: mandatoryFields Missing:");
            vlogDebug("debitCreditCard():End");
            throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
          }
          vlogDebug("Successfully debitted the amount = {0}, payment group id {1}", pAciCreditCardInfo.getAmount(), pAciCreditCardInfo.getPaymentId());
          vlogDebug("AciService:debitCreditCard():End");
          return debitStatus;

    } catch (CommerceException e) {
          vlogError("debitCreditCard(): generatingSettlmentTriggerFile");
          vlogError("AciService:debitCreditCard():End");
          throw new AciPipelineException(e);
    }
   
  }

  public AciCreditCardStatus creditCreditCard(AciCreditCardInfo pAciCreditCardInfo, AciCreditCardStatus pAciCreditCardStatus) throws AciPipelineException {
     vlogDebug("AciService:creditCreditCard:Start");
     try {
           AciCreditCardStatus creditStatus = new AciCreditCardStatus(pAciCreditCardStatus.getTransactionId(), pAciCreditCardInfo.getAmount(), true, "", new Date(), new Date(System.currentTimeMillis() + getCreditThreshold()));
           if(getAciSettlementUtil().mandatoryFeildsValidator(pAciCreditCardInfo, pAciCreditCardStatus)){
             getAciSettlementUtil().generateSettlementTriggerFile(pAciCreditCardInfo, pAciCreditCardStatus, TRANS_TYPE_CREDIT);
             vlogDebug("creditCreditCard(): Auth code being set: "+pAciCreditCardStatus.getAuthorizationCode());
             SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
             SimpleDateFormat lSimpleTimeFormat = new SimpleDateFormat("hhmmss");
             Date lNow      = new Date();
             String lDate   = lSimpleDateFormat.format(lNow);
             String lTime = lSimpleTimeFormat.format(lNow);
             creditStatus.setTransactionDate(lDate);
             creditStatus.setAuthorizationDate(lDate);
             creditStatus.setAuthorizationTime(lTime);
             creditStatus.setAuthCode(pAciCreditCardStatus.getAuthCode());
             creditStatus.setAvsCode(pAciCreditCardStatus.getAvsCode());
             creditStatus.setCvvVerificationCode(pAciCreditCardStatus.getCvvVerificationCode());
             creditStatus.setCallType(pAciCreditCardStatus.getCallType());
             creditStatus.setResponseCode(pAciCreditCardStatus.getResponseCode());
             creditStatus.setResponseCodeMsg(pAciCreditCardStatus.getResponseCodeMsg());
             vlogDebug("Successfully credited the following amount {0}, payment group id {1}", pAciCreditCardInfo.getAmount(), pAciCreditCardInfo.getPaymentId());
             vlogDebug("creditCreditCard():End");             
           }else{
             vlogDebug("creditCreditCard(): mandatoryFields Missing. ");
             throw new AciPipelineException(FieldMappingConstants.ACI_PIPELINE_FAILED, true);
           }
           return creditStatus;
     } catch (CommerceException e) {
           vlogError(e,"creditCreditCard(): generatingSettlmentTriggerFile");
           pAciCreditCardStatus.setTransactionSuccess(false);
           vlogDebug("creditCreditCard():End");
           throw new AciPipelineException(e);
     }
  }  
  
  
  public AciCreditCardStatus reversePreviousAuthorization(AciCreditCardInfo pAciCreditCardInfo){
		vlogDebug("AciService:reversePreviousAuthorization:Start");
		double reversalAmount=pAciCreditCardInfo.getAuthReversalAmount();
		DecimalFormat f=new DecimalFormat("#.##");
	  	String decimalAuthAmount=f.format(reversalAmount);
	  	double newRevAmount=Double.parseDouble(decimalAuthAmount);
		AciCreditCardStatus authorizationReversalStatus = new AciCreditCardStatus("", (-1*newRevAmount), false, "", new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));
		
		try {
			AciPipelineProcessParam pParams = new AciPipelineProcessParam();
			pParams.setAuthReversalRequest(true);
			pParams.setAciConfiguration(getAciConfiguration());
			pParams.setTransactionAction(getAciConfiguration().getAuthReversalTransactionAction());
			pParams.setDivNum(getAciConfiguration().getDiv_Num_CC());
			pParams.setCurrencyCode(getAciConfiguration().getDefaultCurrencyCode());
			pParams.setSubclientId(getAciConfiguration().getCreditCardSubClientId());
			if (pAciCreditCardInfo.isOnlineOrder()) {
				pParams.setReqType(getAciConfiguration().getOnlineOrderReqType());
			} else {
				pParams.setReqType(getAciConfiguration().getCscOrderReqType());
			}
			String mopType = pAciCreditCardInfo.getMopTypeCd();
			if (StringUtils.isEmpty(mopType)) {
				mopType = (String) (getAciConfiguration().getCardCodesMap().get(pAciCreditCardInfo.getCreditCardType()));
			}
			if (mopType != null) {
				pParams.setMopType(mopType);
			}
			String originalReqId=pAciCreditCardInfo.getOriginalReqId();
			if(getAciConfiguration().isUseReqIdForAuthReversal()) {
				if(StringUtils.isEmpty((originalReqId))){
					vlogWarning("Transaction not successful: reversePreviousAuthorization(): Original Req id is null ");
					vlogDebug("AciService:reversePreviousAuthorization:End");
					return authorizationReversalStatus;
				}
			
				pParams.setOriginalReqId(originalReqId);
				
			}
			
			
			String originalJournalKey=pAciCreditCardInfo.getOriginalJournalKey();
			if(StringUtils.isEmpty((originalJournalKey))){
				vlogWarning("Transaction not successful: reversePreviousAuthorization(): Original Journal key is null");
				vlogDebug("AciService:reversePreviousAuthorization:End");
				return authorizationReversalStatus;
			}
			pParams.setOriginalJournalKey(originalJournalKey);
			pParams.setAmountToReverse(newRevAmount);
			
			
			AciPipelineResult result = (AciPipelineResult) getAciPipelineManager().runProcess(getAciConfiguration().getAuthReversalPipelineChainName(), pParams);
			vlogDebug("Transaction status is: " + result.isTransactionSuccess());
			if (result.isTransactionSuccess()) {
				authorizationReversalStatus = new AciCreditCardStatus(result.getAciReqId(),(-1*newRevAmount),true,"",new Date(), new Date(System.currentTimeMillis() + getAuthThreshold()));
				String aciAuthDate=result.getOriginalAciAuthDate();
				authorizationReversalStatus.setAuthorizationSource(pAciCreditCardInfo.getPaymentSource());
		        authorizationReversalStatus.setResponseCode(result.getPaymentResponseCode());
				if(!StringUtils.isEmpty(aciAuthDate)){
		        	authorizationReversalStatus.setOriginalAciAuthDate(aciAuthDate);
				}
		        
		        String aciAuthTime=result.getOriginalAciAuthTime();
		        if(!StringUtils.isEmpty(aciAuthTime)){
		        	authorizationReversalStatus.setOriginalAciAuthTime(aciAuthTime);
				}
		        
		        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		        SimpleDateFormat lSimpleTimeFormat = new SimpleDateFormat("hhmmss");
		        Date lNow = new Date();
		        String lDate = lSimpleDateFormat.format(lNow);
		        String lTime = lSimpleTimeFormat.format(lNow);
		        // authrizatinoTime, authorizationdate, TransactionDate
		        authorizationReversalStatus.setTransactionDate(lDate);
		        authorizationReversalStatus.setAuthorizationDate(lDate);
		        authorizationReversalStatus.setAuthorizationTime(lTime);
		        authorizationReversalStatus.setOriginalJournalKey(originalJournalKey);
		        authorizationReversalStatus.setAuthCode(result.getAuthorizationCode());
		        authorizationReversalStatus.setCallType(result.getCallType());
		        authorizationReversalStatus.setOriginalReqId(originalReqId);
		        authorizationReversalStatus.setCvvVerificationCode(pAciCreditCardInfo.getCvvVerificationCode());
		        authorizationReversalStatus.setAvsCode(pAciCreditCardInfo.getAvsCode());
		        authorizationReversalStatus.setAuthorizationSource(pAciCreditCardInfo.getPaymentSource());
		        vlogDebug("AciService:reversePreviousAuthorization:End");
				return authorizationReversalStatus;
			} else {
				vlogWarning("reversePreviousAuthorization(): Aci Transaction not successful ");
				vlogDebug("AciService:reversePreviousAuthorization:End");
				return authorizationReversalStatus;
			}
		}catch (Exception exception) {    
			vlogWarning(exception,"reversePreviousAuthorization(): Exception occurred: " );
			vlogDebug("AciService:reversePreviousAuthorization:End");
			return authorizationReversalStatus;
		}
  }


  /**
   * Simulate authorization declines
   */
  private boolean testAuthorizationDeclines = false;
  
  public boolean isTestAuthorizationDeclines() {
    return testAuthorizationDeclines;
  }

  public void setTestAuthorizationDeclines(boolean pTestAuthorizationDeclines) {
    testAuthorizationDeclines = pTestAuthorizationDeclines;
  }
  
  protected AciCreditCardStatus createErrorStatus(AciCreditCardStatus pAuthorizationStatus) {

    pAuthorizationStatus.setTransactionSuccess(false);
    pAuthorizationStatus.setResponseCode("09");
    pAuthorizationStatus.setAuthCode("000");
    Date now = new Date();
    pAuthorizationStatus.setTransactionTimestamp(now);
    return pAuthorizationStatus;
  }
  
  public long getAuthThreshold(){
	  int authExpiryThreshold=getAciConfiguration().getAuthExpiryThreshold();
	  if(authExpiryThreshold==0){
		  authExpiryThreshold=7;
	  }
	  return (authExpiryThreshold*24*60*60*1000);
  }
  
  public long getCreditThreshold(){
	  int creditExpiryThreshold=getAciConfiguration().getCreditExpiryThreshold();
	  if(creditExpiryThreshold==0){
		  creditExpiryThreshold=7;
	  }
	  return (creditExpiryThreshold*24*60*60*1000);
  }
  
  public long getDebitThreshold(){
	  int debitExpiryThreshold=getAciConfiguration().getDebitExpiryThreshold();
	  if(debitExpiryThreshold==0){
		  debitExpiryThreshold=7;
	  }
	  return (debitExpiryThreshold*24*60*60*1000);
  }
  
  
  
  
  
  
}
