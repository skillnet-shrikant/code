package com.firstdata.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import net.datawire.vxn3.VXNException;
import net.datawire.vxn3.VXNReturnCodeException;
import atg.commerce.CommerceException;
import atg.core.util.NumberUtils;
import atg.nucleus.GenericService;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;

import com.firstdata.bean.FDActivationResponseBean;
import com.firstdata.bean.FDAuthorizeResponseBean;
import com.firstdata.bean.FDBalanceInquiryResponseBean;
import com.firstdata.bean.FDDebitResponseBean;
import com.firstdata.bean.FDTimeoutReversalResponseBean;
import com.firstdata.bean.FirstDataBean;
import com.firstdata.constants.FirstDataConstants;
import com.firstdata.payment.MFFGiftCardPaymentStatus;
import com.firstdata.security.EANEncryptorService;
import com.firstdata.util.FirstDataUtil;

/**
 * Service class for invoking First Data API calls. It is in the
 * <code>com.firstdata.service package</code> .
 * 
 * @author DMI
 *
 */
public class FirstDataService extends GenericService {

  private VXN vxn;
  private List<String> sdUrls;
  private Map<String, String> initializeParams;
  private Map<String, String> mResponseCodeMap;
  private FirstDataUtil mFirstDataUtil;
  private Map<String, String> mServiceToBeanMap;
  private Map<String, String> mServiceTransactionCodeMap;
  private String clientRefId;
  private static final String ZERO = "0";
  private static final int CLIENT_TRAN_ID_LENGTH = 7;
  private String mDid;
  private boolean mUseEncryption;
  private EANEncryptorService mEanEncryptorService;
  private String mMwkKeyId;
  private ArrayList<String> mVxnErrorList;
  private static final String TIMEOUT_REVERSAL = "timeoutReversal";
  private boolean testTimeoutReversal = false;
  
  public ArrayList<String> getVxnErrorList() {
    return mVxnErrorList;
  }

  public void setVxnErrorList(ArrayList<String> pVxnErrorList) {
    mVxnErrorList = pVxnErrorList;
  }

  public String getMwkKeyId(){
	  return mMwkKeyId;
  }
  
  public void setMwkKeyId(String pMwkKeyId){
	  mMwkKeyId=pMwkKeyId;
  }
  
  /**
   * Simulate authorization declines
   */
  private boolean testAuthorizationDeclines = false;

  /**
   * Simulate debit declines operations
   */
  private boolean testDebitDeclines = false;
  
	public boolean isUseEncryption() {
	    return mUseEncryption;
	  }
	
	  public void setUseEncryption(boolean pUseEncryption) {
	    mUseEncryption = pUseEncryption;
	  }
	
	  public VXN getVxn() {
	    if (vxn == null) {
	      try {
	        vxn = VXN.getInstance(getSdUrls(), getDid(), getInitializeParams().get(FirstDataConstants.MID), getInitializeParams().get(FirstDataConstants.TID), getInitializeParams().get(FirstDataConstants.SVC_ID), getInitializeParams().get(FirstDataConstants.APPLICATION_ID));
	      } catch (VXNException e) {
	        logError("Exception occured while initializing First Data Service {0}", e);
	      } catch (RuntimeException e1) {
	        logError("Exception occured while initializing First Data Service {0}", e1);
	      }
	    }
	    return vxn;
	  }

  /**
   * authorize method to invoke First Data 2408 API Call for authorizing Gift
   * Card Payments
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @param pEan
   *          The Extended Account Number
   * @param pAount
   *          Amount as a double value
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus authorize(String pGiftCardNumber, double pAmount, String pEan) throws Exception {

    vlogDebug("Entered authorize method in FirstDataService with Card Number {0} and Amount {1}", pGiftCardNumber, pAmount);
    
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);
    
    Map<String, String> params = new LinkedHashMap<String, String>();
    
    if (testAuthorizationDeclines) {
      return createErrorStatus(gcStatus);
    }

    if (isUseEncryption()) {
      pEan=getEanEncryptorService().encryptEan(pEan);
      setCommonValues(params, pGiftCardNumber, pEan);
    } else {
      setCommonValues(params, pGiftCardNumber);

    }
    params.put(FirstDataConstants.AMOUNT_KEY, convertInputAmount(pAmount));

    FDAuthorizeResponseBean lFirstDataBean = (FDAuthorizeResponseBean) callService(FirstDataConstants.AUTHORIZE_SERVICE, params,clientRefTransId);

    vlogInfo("Lock amount = {0}, lock id = {1}, prev balance = {2},  new balance = {3}", lFirstDataBean.getLockAmount(), lFirstDataBean.getLocalLockId(), lFirstDataBean.getPreviousBalance(), lFirstDataBean.getNewBalance());
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.AUTHORIZE_SERVICE);
      }
    } else {

      setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.AUTHORIZE_SERVICE);
    }
    vlogDebug("Exitted authorize method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * authorize method to invoke First Data 2408 API Call for authorizing Gift
   * Card Payments
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @param pEan
   *          The Extended Account Number
   * @param pAount
   *          Amount as a double value
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus authorizeEncrypted(String pGiftCardNumber, double pAmount, String pEan) throws Exception {

    vlogDebug("Entered authorize method in FirstDataService with Card Number {0} and Amount {1}", pGiftCardNumber, pAmount);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);
    
    if (testAuthorizationDeclines) {
      return createErrorStatus(gcStatus);
    }
    
    Map<String, String> params = new LinkedHashMap<String, String>();
    
    if (testAuthorizationDeclines) throw new CommerceException ("test declines");
    if (isUseEncryption()) {
    	pEan=getEanEncryptorService().encryptEan(pEan);
      setCommonValues(params, pGiftCardNumber, pEan);
    } else {
      setCommonValues(params, pGiftCardNumber);

    }
    params.put(FirstDataConstants.AMOUNT_KEY, convertInputAmount(pAmount));

    FDAuthorizeResponseBean lFirstDataBean = (FDAuthorizeResponseBean) callService(FirstDataConstants.AUTHORIZE_SERVICE, params,clientRefTransId);

    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.AUTHORIZE_SERVICE);
      }
    } else {
      setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.AUTHORIZE_SERVICE);
      gcStatus.setTransactionSuccess(false);
    }
    vlogDebug("Exitted authorize method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * debit method to invoke First Data 2208 API Call to debit Gift Card Account
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @param pAount
   *          Amount as a double value
   * @param pLocalLockId
   *          Local Lock Id obtained in authorize call
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus debit(String pGiftCardNumber, double pAmount, String pLocalLockId, String pEan) throws Exception {
    
    vlogDebug("Entered debit method in FirstDataService with Card Number {0} and Amount {1} and Local Lock Id {2}", pGiftCardNumber, pAmount, pLocalLockId);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);
    if (testDebitDeclines) {
      return createErrorStatus(gcStatus);
    }
    
    Map<String, String> params = new LinkedHashMap<String, String>();
    if (isUseEncryption()) {
    	pEan=getEanEncryptorService().encryptEan(pEan);
      setCommonValues(params, pGiftCardNumber, pEan);
    } else {
      setCommonValues(params, pGiftCardNumber);

    }
    params.put(FirstDataConstants.TRANSACTION_AMOUNT, convertInputAmount(pAmount));
    params.put(FirstDataConstants.LOCK_ID_KEY, pLocalLockId);
    
    // As Debit and Authorize has same response using same bean
    FDDebitResponseBean lFirstDataBean = (FDDebitResponseBean) callService(FirstDataConstants.DEBIT_SERVICE, params,clientRefTransId);

    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.DEBIT_SERVICE);
      }
    } else {
      setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.DEBIT_SERVICE);

    }
    vlogDebug("Exitted debit method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * debit method to invoke First Data 2208 API Call to debit Gift Card Account
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @param pEan
   *          The Extended Account Number
   * @param pAount
   *          Amount as a double value
   * @param pLocalLockId
   *          Local Lock Id obtained in authorize call
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus debitWithEan(String pGiftCardNumber, String pEan, double pAmount, String pLocalLockId) throws Exception {
    
    vlogDebug("Entered debit method in FirstDataService with Card Number {0} and Amount {1} and Local Lock Id {2} with Ean", pGiftCardNumber, pAmount, pLocalLockId);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);

    if (testDebitDeclines) {
      return createErrorStatus(gcStatus);
    }
    
    Map<String, String> params = new LinkedHashMap<String, String>();
    
    params.put(FirstDataConstants.AMOUNT_KEY, convertInputAmount(pAmount));
    pEan=getEanEncryptorService().encryptEan(pEan);
    setCommonValues(params, pGiftCardNumber, pEan);
    params.put(FirstDataConstants.LOCK_ID_KEY, pLocalLockId);
    // As Debit and Authorize has same response using same bean
    FDDebitResponseBean lFirstDataBean = (FDDebitResponseBean) callService(FirstDataConstants.DEBIT_SERVICE, params,clientRefTransId);

    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.DEBIT_SERVICE);
      }
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());

    }
    vlogDebug("Exitted debit method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * Method to invoke First Data 2104 API Call for activation of physical Gift
   * Card TURNED SOURCE_CODE_KEY to 31 = Internet Without EAN
   * 
   * 20Denom
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus activate(String pGiftCardNumber) throws Exception {
    vlogDebug("Entered credit method in FirstDataService with Card Number {0} ", pGiftCardNumber);

    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);
    
    Map<String, String> params = new LinkedHashMap<String, String>();
    setCommonValues(params, pGiftCardNumber);

    
    FDActivationResponseBean lFirstDataBean = (FDActivationResponseBean) callService(FirstDataConstants.ACTIVATE_SERVICE, params,clientRefTransId);
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.ACTIVATE_SERVICE);
      }
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());

    }
    vlogDebug("Exitted activation method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * Method to invoke First Data 2104 API Call for activation of physical Gift
   * Card TURNED SOURCE_CODE_KEY to 31 = Internet Without EAN
   * 
   * NonDenom and Denom given correct amount on card when activating.
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @return A MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus activate(String pGiftCardNumber, double amount) throws Exception {

    vlogDebug("Entered credit method in FirstDataService with Card Number {0} ", pGiftCardNumber);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);

    Map<String, String> params = new LinkedHashMap<String, String>();
    
    setCommonValues(params, pGiftCardNumber);

    params.put(FirstDataConstants.CARD_COST, convertInputAmount(amount));
    params.put(FirstDataConstants.TRANSACTION_AMOUNT, convertInputAmount(amount));
    
   
    FDActivationResponseBean lFirstDataBean = (FDActivationResponseBean) callService(FirstDataConstants.ACTIVATE_SERVICE, params,clientRefTransId);
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getLockAmount() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getLockAmount())) {
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.ACTIVATE_SERVICE);
      }
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());

    }
    vlogDebug("Exitted activation method in FirstDataService with Status as {0}", gcStatus.getTransactionSuccess());
    return gcStatus;
  }

  /**
   * Method to invoke First Data 2400 API Call to get Gift Card Balance with EAN
   * (Extended Account Number)
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @return MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus balanceInquiry(String pGiftCardNumber, String pEan) throws Exception {
    vlogDebug("Entered balanceInquiry method in FirstDataService with Card Number {0}", pGiftCardNumber);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);

    Map<String, String> params = new LinkedHashMap<String, String>();
    
    
    if (isUseEncryption()) {
    	pEan=getEanEncryptorService().encryptEan(pEan);
      setCommonValues(params, pGiftCardNumber, pEan);
    } else {
      setCommonValues(params, pGiftCardNumber);

    }
    FDBalanceInquiryResponseBean lFirstDataBean = (FDBalanceInquiryResponseBean) callService(FirstDataConstants.BALANCE_INQUIRY_SERVICE, params,clientRefTransId);
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getNewBalance() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getNewBalance())) {
        vlogDebug("Exitted balanceInquiry method in FirstDataService with Balance as {0}", lFirstDataBean.getNewBalance());
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.BALANCE_INQUIRY_SERVICE);

        gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
        gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
        return gcStatus;
      }
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
      vlogDebug("Exitted balanceInquiry call with response code {0}", lFirstDataBean.getResponseCode());
    }
    vlogDebug("Exitted balanceInquiry method in FirstDataService");
    return gcStatus;
  }

  /**
   * Method to invoke First Data 2400 API Call to get Gift Card Balance without
   * EAN(Extended Account Number)
   * 
   * @param pGiftCardNumber
   *          The Gift Card Number
   * @return MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus balanceInquiry(String pGiftCardNumber) throws Exception {
    vlogDebug("Entered balanceInquiry method in FirstDataService with Card Number {0}", pGiftCardNumber);

    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);

    Map<String, String> params = new LinkedHashMap<String, String>();
    setCommonValues(params, pGiftCardNumber);

    
    FDBalanceInquiryResponseBean lFirstDataBean = (FDBalanceInquiryResponseBean) callService(FirstDataConstants.BALANCE_INQUIRY_SERVICE, params,clientRefTransId);
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getNewBalance() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getNewBalance())) {
        
        vlogDebug("Exitted balanceInquiry method in FirstDataService with Balance as {0}", lFirstDataBean.getNewBalance());
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.BALANCE_INQUIRY_SERVICE);
        
        gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
        gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
        return gcStatus;
      }
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
      vlogDebug("Exitted balanceInquiry call with response code {0}", lFirstDataBean.getResponseCode());
    }
    vlogDebug("Exitted balanceInquiry method in FirstDataService");
    return gcStatus;
  }

  /**
   * Method to invoke First Data 2010 API Call to  assign pMerchKey
   * 
   * 
   * @param pMerchKey
   *         
   * @return MFFGiftCardPaymentStatus
   */
  public MFFGiftCardPaymentStatus assignMerchWorkingKey(String pMerchKey) throws Exception {
    vlogDebug("Entered assignMerchWorkingKey in FirstDataService with Key {0}", pMerchKey);
    
    String clientRefTransId = buildClientRefId();
    MFFGiftCardPaymentStatus gcStatus = new MFFGiftCardPaymentStatus(clientRefTransId);

    Map<String, String> params = new LinkedHashMap<String, String>();

    setCommonMerchKeyValues(params, pMerchKey);

    FDBalanceInquiryResponseBean lFirstDataBean = (FDBalanceInquiryResponseBean) callService(FirstDataConstants.ASSIGN_MERCH_KEY_SERVICE, params,clientRefTransId);
    if (lFirstDataBean != null && lFirstDataBean.getResponseCode().equalsIgnoreCase(FirstDataConstants.RESPONSE_OK)) {
      if (lFirstDataBean.getNewBalance() != null && NumberUtils.isValidFloatingPointFormat(lFirstDataBean.getNewBalance())) {
        vlogDebug("Exitted assignMerchWorkingKey method in FirstDataService");
        setGiftCardPaymentObj(gcStatus, lFirstDataBean, FirstDataConstants.BALANCE_INQUIRY_SERVICE);

        gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
        gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
        return gcStatus;
      }
    } else {
      gcStatus.setTransactionSuccess(false);
      gcStatus.setResponseCodeMsg(this.getResponseCodeMap().get(lFirstDataBean.getResponseCode()));
      gcStatus.setResponseCode(lFirstDataBean.getResponseCode());
      vlogDebug("Exitted assignMerchWorkingKey call with response code {0}", lFirstDataBean.getResponseCode());
    }
    vlogDebug("Exitted assignMerchWorkingKey method in FirstDataService");
    return gcStatus;
  }
  
  
  
  public void issueGiftCard() {

  }

  private FirstDataBean callService(String mServiceType, Map<String, String> pParams, String tranId) throws Exception {

    if (getVxn() != null) {
      try {
        SimpleTransaction simpleTrans = getVxn().newSimpleTransaction(tranId);
        char[] servicePayload = getPayloadForService(mServiceType, pParams);
        vlogDebug("Request being sent to Service Call is {0}", new String(servicePayload));
        simpleTrans.setPayload(servicePayload);
        simpleTrans.executeXmlRequest();
        char[] delimeter = { 28 };
        vlogDebug("Response obtained from Service Call is {0}", new String(simpleTrans.getPayload()));
        FirstDataBean fdBean = getFirstDataUtil().parseResponse(new String(simpleTrans.getPayload()), new String(delimeter), getServiceToBeanMap().get(mServiceType));
        if (isTestTimeoutReversal()) timeOutReversalCall(mServiceType,pParams,tranId);
        return fdBean;
      } catch (VXNReturnCodeException erc) {
        logError("Exception occured while executing first data service: ", erc);
        logError("Return Code:" + erc.getReturnCode());
        if(getVxnErrorList().contains(erc.getReturnCode())){
            timeOutReversalCall(mServiceType,pParams,tranId);
        }
        throw erc;
      } catch (VXNException e) {
        if (e.getCause() instanceof VXNReturnCodeException) {
          VXNReturnCodeException lVXNReturnCodeException = (VXNReturnCodeException) e.getCause();
          if (getVxnErrorList().contains(lVXNReturnCodeException.getReturnCode())) {
            timeOutReversalCall(mServiceType,pParams,tranId);
          }
        }
        logError("Exception occured while executing first data service request {0}", e);
        throw e;
      } catch (Exception e) {
        logError("Exception occured while calling service {0}", e);
        throw e;
      }
    }
    return null;
  }
  

  /**
   *   
   * @param mOrginalServiceType
   * @param pParams
   * @param tranId
   * @return
   * @throws Exception
   */
  private boolean timeOutReversalCall(String mOrginalServiceType, Map<String, String> pParams, String tranId) {

    String payload = null;

    try {
      SimpleTransaction simpleTrans = getVxn().newSimpleTransaction(tranId);
      // pParams.put(FirstDataConstants.TIMEOUT_REVERSAL_CODE,
      // mOrginalServiceType);//ADDING F6 FOR ORGINAL SERVICETYPE
      pParams.put(FirstDataConstants.TIMEOUT_ORIG_TRANC_CODE_KEY, getServiceTransactionCodeMap().get(mOrginalServiceType));

      // SETTING SERVICETYPE AS 0704 TIMEOUTREVERSAL
      char[] servicePayload = getPayloadForService(TIMEOUT_REVERSAL, pParams);
      payload = new String(servicePayload);

      vlogDebug("Request being sent to for Service Call 0704 is {0}", payload);

      simpleTrans.setPayload(servicePayload);
      simpleTrans.executeXmlRequest();

      char[] delimeter = { 28 };
      vlogDebug("Response obtained from Service Call 0704 is {0}", new String(simpleTrans.getPayload()));
      FirstDataBean fdBean = getFirstDataUtil().parseResponse(new String(simpleTrans.getPayload()), new String(delimeter), getServiceToBeanMap().get(TIMEOUT_REVERSAL));
      FDTimeoutReversalResponseBean lFirstDataBean = (FDTimeoutReversalResponseBean) fdBean;
      if (lFirstDataBean != null) {
        vlogDebug("Exitted timeOutReversalCall in FirstDataService with responseCode: {0} ", lFirstDataBean.getResponseCode());
      }
    } catch (Throwable e) {
      vlogDebug("Payload is set to {0}", payload);
      vlogWarning(e, "Could not reverse a transaction {0}", tranId);
    }
    return true;
  }
  

  public Map<String, String> getServiceToBeanMap() {
    return mServiceToBeanMap;
  }

  public void setServiceToBeanMap(Map<String, String> pServiceToBeanMap) {
    this.mServiceToBeanMap = pServiceToBeanMap;
  }

  private void setCommonValues(Map<String, String> pParams, String pGiftCardNumber) {
    Date now = new Date();
    SimpleDateFormat sf = new SimpleDateFormat(FirstDataConstants.TIMESTAMP_PATTERN);
    SimpleDateFormat d = new SimpleDateFormat(FirstDataConstants.DATE_PATTERN);
    pParams.put(FirstDataConstants.TRANS_TIME_KEY, sf.format(now));
    pParams.put(FirstDataConstants.TRANS_DATE_KEY, d.format(now));
    pParams.put(FirstDataConstants.CARD_NUMBER_KEY, pGiftCardNumber);
    pParams.put(FirstDataConstants.MERCH_TERMINAL_KEY, getInitializeParams().get(FirstDataConstants.MERCHANT_TERMINAL_ID));
    pParams.put(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_KEY, getInitializeParams().get(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_ID));
    pParams.put(FirstDataConstants.SOURCE_CODE_KEY, getInitializeParams().get(FirstDataConstants.EA_VAL));
    
  }

  private void setCommonValues(Map<String, String> pParams, String pGiftCardNumber, String pEAN) {
    Date now = new Date();
    SimpleDateFormat sf = new SimpleDateFormat(FirstDataConstants.TIMESTAMP_PATTERN);
    SimpleDateFormat d = new SimpleDateFormat(FirstDataConstants.DATE_PATTERN);
    pParams.put(FirstDataConstants.TRANS_TIME_KEY, sf.format(now));
    pParams.put(FirstDataConstants.TRANS_DATE_KEY, d.format(now));
    pParams.put(FirstDataConstants.CARD_NUMBER_KEY, pGiftCardNumber);
    pParams.put(FirstDataConstants.EAN_KEY, pEAN);
    pParams.put(FirstDataConstants.MERCH_TERMINAL_KEY, getInitializeParams().get(FirstDataConstants.MERCHANT_TERMINAL_ID));
    pParams.put(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_KEY, getInitializeParams().get(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_ID));
    
    // TURNED SOURCE_CODE_KEY to 30 = Internet With EAN
    pParams.put(FirstDataConstants.SOURCE_CODE_KEY, getInitializeParams().get(FirstDataConstants.EA_WITH_VAL));
    //MERCH_WORKING_KEY_ID
    pParams.put(FirstDataConstants.MERCH_WORKING_KEY_ID, getMwkKeyId());
    
    
  }
  
  private void setCommonMerchKeyValues(Map<String, String> pParams, String pMerchKey) {
    Date now = new Date();
    SimpleDateFormat sf = new SimpleDateFormat(FirstDataConstants.TIMESTAMP_PATTERN);
    SimpleDateFormat d = new SimpleDateFormat(FirstDataConstants.DATE_PATTERN);
    pParams.put(FirstDataConstants.TRANS_TIME_KEY, sf.format(now));//12
    pParams.put(FirstDataConstants.TRANS_DATE_KEY, d.format(now));//13
    pParams.put(FirstDataConstants.MERCH_TERMINAL_KEY, getInitializeParams().get(FirstDataConstants.MERCHANT_TERMINAL_ID));//42
    pParams.put(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_KEY, getInitializeParams().get(FirstDataConstants.ALTERNATE_MERCHANT_NUMBER_ID));  //44  
    pParams.put(FirstDataConstants.MERCH_WORKING_KEY, pMerchKey);//63
    // TURNED SOURCE_CODE_KEY to 30 = Internet With EAN
    pParams.put(FirstDataConstants.SOURCE_CODE_KEY, getInitializeParams().get(FirstDataConstants.EA_WITH_VAL));//EA=30
    //MERCH_WORKING_KEY_ID
    pParams.put(FirstDataConstants.MERCH_WORKING_KEY_ID, getMwkKeyId());//F3=1

    
  }

  @SuppressWarnings("rawtypes")
  private char[] getPayloadForService(String pServiceType, Map<String, String> pParams) {
    char[] payload = { 'S', 'V', 46 };
    char[] FS = { 28 };
    payload = concatCharArray(payload, getInitializeParams().get(FirstDataConstants.MID).toCharArray()); // Merchant
                                                                                                         // ID
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, getInitializeParams().get(FirstDataConstants.VERSION_NUMBER).toCharArray());
    payload = concatCharArray(payload, getInitializeParams().get(FirstDataConstants.FORMAT_NUMBER).toCharArray());
    payload = concatCharArray(payload, getServiceTransactionCodeMap().get(pServiceType).toCharArray());
    Iterator paramItr = pParams.keySet().iterator();
    while (paramItr.hasNext()) {
      String paramKey = (String) paramItr.next();
      payload = concatCharArray(payload, FS);
      payload = concatCharArray(payload, paramKey.toCharArray());
      payload = concatCharArray(payload, pParams.get(paramKey).toCharArray());
    }

    return payload;
  }

  private char[] concatCharArray(char[] c1, char[] c2) {
    char[] concat = new char[c1.length + c2.length];
    System.arraycopy(c1, 0, concat, 0, c1.length);
    System.arraycopy(c2, 0, concat, c1.length, c2.length);
    return concat;
  }

  private void setGiftCardPaymentObj(MFFGiftCardPaymentStatus pGiftCardPS, FirstDataBean pResponseBean, String pServiceType) throws IdGeneratorException {

    Date now = new Date();

    // 2400
    if (pServiceType == FirstDataConstants.BALANCE_INQUIRY_SERVICE) {
      FDBalanceInquiryResponseBean biReponseBean = (FDBalanceInquiryResponseBean) pResponseBean;
      pGiftCardPS.setCallType(FirstDataConstants.BALANCE_INQUIRY_SERVICE);
      pGiftCardPS.setPreviousBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getPreviousBalance())));
      pGiftCardPS.setNewBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getNewBalance())));
      pGiftCardPS.setLockAmount(convertOutputAmount(Double.parseDouble(biReponseBean.getLockAmount())));
      pGiftCardPS.setAuthCode(biReponseBean.getAuthCode());
      pGiftCardPS.setTransactionSuccess(true);
      pGiftCardPS.setTransactionTimestamp(now);
      pGiftCardPS.setResponseCodeMsg(this.getResponseCodeMap().get(biReponseBean.getResponseCode()));
      pGiftCardPS.setResponseCode(biReponseBean.getResponseCode());
    }
    // 2408
    if (pServiceType == FirstDataConstants.AUTHORIZE_SERVICE) {
      FDAuthorizeResponseBean biReponseBean = (FDAuthorizeResponseBean) pResponseBean;
      pGiftCardPS.setCallType(FirstDataConstants.AUTHORIZE_SERVICE);
      pGiftCardPS.setPreviousBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getPreviousBalance())));
      pGiftCardPS.setNewBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getNewBalance())));
      pGiftCardPS.setLockAmount(convertOutputAmount(Double.parseDouble(biReponseBean.getLockAmount())));
      pGiftCardPS.setLocalLockId(biReponseBean.getLocalLockId());
      pGiftCardPS.setAuthCode(biReponseBean.getAuthCode());
      pGiftCardPS.setTransactionSuccess(true);
      pGiftCardPS.setTransactionTimestamp(now);
      pGiftCardPS.setResponseCodeMsg(this.getResponseCodeMap().get(biReponseBean.getResponseCode()));
      pGiftCardPS.setResponseCode(biReponseBean.getResponseCode());

    }
    // 2104
    if (pServiceType == FirstDataConstants.ACTIVATE_SERVICE) {

      FDActivationResponseBean biReponseBean = (FDActivationResponseBean) pResponseBean;
      pGiftCardPS.setCallType(FirstDataConstants.ACTIVATE_SERVICE);
      pGiftCardPS.setPreviousBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getPreviousBalance())));
      pGiftCardPS.setNewBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getNewBalance())));
      pGiftCardPS.setLockAmount(convertOutputAmount(Double.parseDouble(biReponseBean.getLockAmount())));
      pGiftCardPS.setAuthCode(biReponseBean.getAuthCode());
      pGiftCardPS.setTransactionSuccess(true);
      pGiftCardPS.setTransactionTimestamp(now);
      pGiftCardPS.setResponseCodeMsg(this.getResponseCodeMap().get(biReponseBean.getResponseCode()));
      pGiftCardPS.setResponseCode(biReponseBean.getResponseCode());

    }
    // 2208
    if (pServiceType == FirstDataConstants.DEBIT_SERVICE) {
      FDDebitResponseBean biReponseBean = (FDDebitResponseBean) pResponseBean;
      pGiftCardPS.setCallType(FirstDataConstants.DEBIT_SERVICE);
      pGiftCardPS.setPreviousBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getPreviousBalance())));
      pGiftCardPS.setNewBalance(convertOutputAmount(Double.parseDouble(biReponseBean.getNewBalance())));
      pGiftCardPS.setLockAmount(convertOutputAmount(Double.parseDouble(biReponseBean.getLockAmount())));
      pGiftCardPS.setLocalLockId(biReponseBean.getLocalLockId());
      pGiftCardPS.setAuthCode(biReponseBean.getAuthCode());
      pGiftCardPS.setTransactionSuccess(true);
      pGiftCardPS.setTransactionTimestamp(now);
      pGiftCardPS.setResponseCodeMsg(this.getResponseCodeMap().get(biReponseBean.getResponseCode()));
      pGiftCardPS.setResponseCode(biReponseBean.getResponseCode());

    }

  }

  public Map<String, String> getInitializeParams() {
    return initializeParams;
  }

  public void setInitializeParams(Map<String, String> pInitializeParams) {
    this.initializeParams = pInitializeParams;
  }

  public FirstDataUtil getFirstDataUtil() {
    return mFirstDataUtil;
  }

  public void setFirstDataUtil(FirstDataUtil pFirstDataUtil) {
    this.mFirstDataUtil = pFirstDataUtil;
  }

  public Map<String, String> getServiceTransactionCodeMap() {
    return mServiceTransactionCodeMap;
  }

  public void setServiceTransactionCodeMap(Map<String, String> pServiceTransactionCodeMap) {
    this.mServiceTransactionCodeMap = pServiceTransactionCodeMap;
  }

  public List<String> getSdUrls() {
    return sdUrls;
  }

  public void setSdUrls(List<String> sdUrls) {
    this.sdUrls = sdUrls;
  }

  public String getClientRefId() {
    return clientRefId;
  }

  public void setClientRefId(String clientRefId) {
    this.clientRefId = clientRefId;
  }

  public Map<String, String> getResponseCodeMap() {
    return mResponseCodeMap;
  }

  public void setResponseCodeMap(Map<String, String> pResponseCodeMap) {
    mResponseCodeMap = pResponseCodeMap;
  }

  public static String convertInputAmount(double d) {
	  Double roundingDecimal=Math.pow(10, 2);
	  Double roundedAmount=(d*roundingDecimal);
	  String priceAmount = Long.toString(Math.round(roundedAmount));
	  return priceAmount;
  }

  public static double convertOutputAmount(double d) {
	  Double roundingDecimal=Math.pow(10, 2);
	  Double roundedAmount=(d/roundingDecimal);
	  return roundedAmount.doubleValue();
  }

  private IdGenerator mIdGenerator;
  private String mIdGeneratorSpaceName;

  public String getIdGeneratorSpaceName() {
    return mIdGeneratorSpaceName;
  }

  public void setIdGeneratorSpaceName(String pIdGeneratorSpaceName) {
    mIdGeneratorSpaceName = pIdGeneratorSpaceName;
  }

  public IdGenerator getIdGenerator() {
    return mIdGenerator;
  }

  public void setIdGenerator(IdGenerator pIdGenerator) {
    mIdGenerator = pIdGenerator;
  }

  public String buildClientRefId() throws IdGeneratorException {
    String nextTransId = getIdGenerator().generateStringId(getIdGeneratorSpaceName());
    int numIdLen = nextTransId.length();
    StringBuilder sb = new StringBuilder();
    int zeroCount = CLIENT_TRAN_ID_LENGTH - numIdLen;
    if (zeroCount <= 0) {
      logWarning("buildClientRef: The Id number threshold is reached for id space : " + getIdGeneratorSpaceName());
    }
    for (int i = 0; i < zeroCount; i++) {
      sb.append(ZERO);
    }
    sb.append(nextTransId);
    sb.append(getClientRefId());
    vlogDebug("ClientRefid " + sb.toString());

    return sb.toString();
  }

  protected MFFGiftCardPaymentStatus createErrorStatus(MFFGiftCardPaymentStatus pGiftCardStatus) {

    pGiftCardStatus.setTransactionSuccess(false);
    pGiftCardStatus.setLocalLockId("4");
    pGiftCardStatus.setResponseCode("09");
    pGiftCardStatus.setAuthCode("777");
    Date now = new Date();
    pGiftCardStatus.setTransactionTimestamp(now);
    return pGiftCardStatus;
  }
  
  public EANEncryptorService getEanEncryptorService() {
	return mEanEncryptorService;
  }

  public void setEanEncryptorService(EANEncryptorService pEanEncryptorService) {
	mEanEncryptorService = pEanEncryptorService;
  }

  public String getDid() {
	return mDid;
  }

  public void setDid(String pDid) {
	mDid = pDid;
  }


public boolean isTestAuthorizationDeclines() {
    return testAuthorizationDeclines;
  }

  public void setTestAuthorizationDeclines(boolean pTestAuthorizationDeclines) {
    testAuthorizationDeclines = pTestAuthorizationDeclines;
  }

  public boolean isTestDebitDeclines() {
    return testDebitDeclines;
  }

  public void setTestDebitDeclines(boolean pTestDebitDeclines) {
    testDebitDeclines = pTestDebitDeclines;
  }

  public boolean isTestTimeoutReversal() {
    return testTimeoutReversal;
  }

  public void setTestTimeoutReversal(boolean pTestTimeoutReversal) {
    testTimeoutReversal = pTestTimeoutReversal;
  }


}
