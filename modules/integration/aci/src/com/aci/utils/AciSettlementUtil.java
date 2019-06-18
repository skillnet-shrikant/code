package com.aci.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.aci.commerce.service.AciService;
import com.aci.configuration.AciConfiguration;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.payment.creditcard.AciCreditCardStatus;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;

public class AciSettlementUtil extends GenericService { 
  
  private static final String ZERO = "0";
  private static final int CLIENT_TRAN_ID_LENGTH = 8;
  private static final String SETTLEMENT_TYPE_CREDIT = "credit";
  
  boolean enableValidation  = false;
  
  public SettlementGenerator settlementGenerator;
  
  
  public boolean isEnableValidation() {
    return enableValidation;
  }
  public void setEnableValidation(boolean pEnableValidation) {
    enableValidation = pEnableValidation;
  }

  String mFileName;
  public String getFileName() {
    return mFileName;
  }
  public void setFileName(String pFileName) {
    mFileName = pFileName;
  }
  
  String mDateFormat;
  public String getDateFormat() {
    return mDateFormat;
  }
  public void setDateFormat(String pDateFormat) {
    this.mDateFormat = pDateFormat;
  }
  
  String mOutputDirectory;
  public String getOutputDirectory() {
    return mOutputDirectory;
  }
  public void setOutputDirectory(String pOutputDirectory) {
    mOutputDirectory = pOutputDirectory;
  }
  
  AciConfiguration mAciConfiguration;
  public AciConfiguration getAciConfiguration(){
    return mAciConfiguration;
  }
  
  public void setAciConfiguration(AciConfiguration pAciConfiguration){
    mAciConfiguration=pAciConfiguration;
  }
  
  
  /**
   * Method to Generate Trigger File for ACI Debit(Sale) and Credit(Returns).
   * 
   * Note: The parameter order of the generated file needs to be exact.
   * 
   * @param pAciCreditCardInfo
   * @param aciCCStatus
   * @param settlmentType
   * @throws CommerceException
   */
  public void generateSettlementTriggerFile(AciCreditCardInfo pAciCreditCardInfo, AciCreditCardStatus aciCCStatus, String settlmentType) throws CommerceException {
    
    
      SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(getDateFormat());
      Date lNow      = new Date();
      String lDate   = lSimpleDateFormat.format(lNow);
      String transRecord = new String();

      /******************************************************************************************/
      /******************************************************************************************/
      /********************PLEASE DO NOT CHANGE THE ORDER OF THESE ATTRIBUTES********************/
      /********************IT WILL BE EASIER TO MANAGE CHANGES************/
      /******************************************************************************************/
      /******************************************************************************************/
      /***********A1 - HEADER RECORD FIELD****************/
      String businessDate = StringUtils.rightPad(lDate, 8,' '); // 8 digit 1 - 8 M M
      String storeNumber = StringUtils.rightPad(getAciConfiguration().getStoreNumber(), 4,' ');   //4 digit 9 - 12 M M
      String terminalNumber = StringUtils.rightPad(getAciConfiguration().getTerminalNumber(), 4,' '); ; // 4 digit 13 - 16 M M
      String transactionNumber = StringUtils.rightPad(getAciConfiguration().getTransNumber(), 4,' '); ; //4 digit 17 - 20 M M
      String tenderSequence = StringUtils.rightPad(getAciConfiguration().getTenderSequence(), 3,' '); ; // 3 digit 21 - 23 M M
      String recordIdentifier = StringUtils.rightPad(getAciConfiguration().getRecordIdentifier(), 2,' '); ; // 2 char 24 - 25 M M
      String keySequence = StringUtils.rightPad(getAciConfiguration().getKeySequence(), 3,' '); ; // 3 digit 26 - 28 M M
      /***********A1 - Online, Electronic, Offline, Referral or Return Record Fields****************/
      
      String lJournalKey = null;
      if (settlmentType.equalsIgnoreCase(AciService.TRANS_TYPE_CREDIT)) {
        lJournalKey = buildJournalKey();
      } else {
        lJournalKey = aciCCStatus.getOriginalJournalKey();
      }
      String journalKey = StringUtils.rightPad(lJournalKey, 16,' '); // 16 char 29 - 44 M M
      vlogDebug("journal key set to {0}", journalKey);
      
      
      String operatorNumber = StringUtils.rightPad(aciCCStatus.getOperatorNumber(), 10,' '); //      10 char 45 - 54 O O
      String transactionDate = StringUtils.rightPad(aciCCStatus.getTransactionDate(), 8,' '); // 8 digit 55 - 62 M M
      String processingIdentifier = StringUtils.rightPad(getAciConfiguration().getProcessingIdentifier(), 1,' '); //      1 char 63 - 63 M M
      String tenderAmount = StringUtils.leftPad(AciUtils.convertAmountToRedReadableFormat(pAciCreditCardInfo.getAmount(), 2), 11,'0'); // 11 digit 64 - 74 M M
      //
      String transactionType = StringUtils.rightPad(getAciConfiguration().getTransType().get(settlmentType), 1,' '); //1 char 75 - 75 M M
      String invoiceNumber = StringUtils.rightPad(aciCCStatus.getInvoiceNumber(), 10,' '); // 10 char 76 - 85 O O
      String tenderType = StringUtils.rightPad(getAciConfiguration().getTenderType(), 2,' '); //2 digit 86 - 87 M M
      String accountNumber = StringUtils.rightPad(aciCCStatus.getAccountNumber(), 19,' '); // 19 char 88 - 106 C C
      String expirationDate = StringUtils.rightPad(aciCCStatus.getExpirationDate(), 4,' '); // 4 digit 107 - 110 M M
      String authorizationCode = StringUtils.rightPad(aciCCStatus.getAuthCode(), 6,' '); // 6 char 111 - 116 M M
      String authorizationDate = StringUtils.rightPad(aciCCStatus.getAuthorizationDate(), 8,' '); // 8 digit 117 - 124 M M
      String authorizationTime = StringUtils.rightPad(aciCCStatus.getAuthorizationTime(), 6,' '); // 6 digit 125 - 130 M M
      String authorizationSource = StringUtils.rightPad(checkAuthSource(settlmentType), 1,' '); // 1 char 131 - 131 C C
      String posEntryMode = StringUtils.rightPad(getAciConfiguration().getPosEntryMode(), 2,' '); // 2 char 132 - 133 M M
      String currencyCode = StringUtils.rightPad(getAciConfiguration().getCurrencyCode(), 3,' '); // 3 digit 134 - 136 M M
      String userData = StringUtils.rightPad(pAciCreditCardInfo.getOrder().getId(), 20,' '); // 20 char 137 - 156 O O
      String orderNumber = StringUtils.rightPad(pAciCreditCardInfo.getOrder().getId(), 15,' '); // 15 char 157 - 171 O O
      String pointofSaleConditionCode = StringUtils.rightPad(getAciConfiguration().getPosSaleConditionCode(), 2,' '); // 2 digit 172 - 173 M M
      String pointofSaleTerminalType = StringUtils.rightPad(getAciConfiguration().getPosTerminalType(), 1,' '); // 1 digit 174 - 174 M M
      String pointofSaleTerminalEntryCapability = StringUtils.rightPad(getAciConfiguration().getPosTerminalEntryCap(), 1,' '); // 1 digit 175 - 175 M M
      String rReAuthProcessIndicator = StringUtils.rightPad(aciCCStatus.getReAuthProcessIndicator(), 1,' '); // 1 char 176 - 176 I M
      String retryCounter = StringUtils.rightPad(aciCCStatus.getRetryCounter(), 2,' '); // 2 digit 177 - 178 O M
      String cardStartDate = StringUtils.rightPad(aciCCStatus.getCardStartDate(), 4,' '); // 4 char 179 - 182 C C
      String cardIssueNumber = StringUtils.rightPad(aciCCStatus.getCardIssueNumber(), 2,' '); // 2 char 183 - 184 C C
      String transactionBillingMethodIndicator = StringUtils.rightPad(aciCCStatus.getTransactionBillingMethodIndicator(), 1,' '); //  1 char 185 - 185 C C
      String mccCode = StringUtils.rightPad(getAciConfiguration().getMccCode(), 4,' '); //4 digit 186 - 189 M M
      String cashBackAmount = StringUtils.rightPad(aciCCStatus.getCashBackAmount(), 11,' '); //  11 digit 190 - 200 C C
      String localTime = StringUtils.rightPad(aciCCStatus.getLocalTime(), 6,' '); // 6 char 201 - 206 C C
      String networkID = StringUtils.rightPad(aciCCStatus.getNetworkID(), 4,' '); // 4 char 207 - 210 C C
      //
      String retrievalReferenceNum = StringUtils.rightPad(aciCCStatus.getRetrievalReferenceNum(), 12,' '); // 12 char 211 - 222 I I
      String eBTVoucherNumber = StringUtils.rightPad(aciCCStatus.getEbtVoucherNumber(), 15,' '); //  15 char 223 - 237 M M
      String productDeliveryMethod = StringUtils.rightPad(aciCCStatus.getProductDeliveryMethod(), 1,' '); //  1 char 238 - 238 O O
      String accountNumberLength = StringUtils.rightPad(getAciConfiguration().getAccountNumLength(), 2,' '); //   2 digit 239 - 240 M M
      String sequenceOfCard = StringUtils.rightPad(aciCCStatus.getSequenceOfCard(), 4,' '); // 4 digit 241 - 244 C C
      String cardLevelResults = StringUtils.rightPad(aciCCStatus.getCardLevelResults(), 2,' '); // 2 char 245 - 246 C C
      String authorizationTrackNumber = StringUtils.rightPad(aciCCStatus.getAuthorizationTrackNumber(), 1,' '); // 1 char 247 - 247 C C
      String authorizerBankNumber = StringUtils.rightPad(aciCCStatus.getAuthorizerBankNumber(), 3,' '); //  3 digit 248 - 250 C C
      String marketSpecificIndicator = StringUtils.rightPad(aciCCStatus.getMarketSpecificIndicator(), 1,' '); //  1 char 251 - 251 C C
      String encryptedStringToken = StringUtils.rightPad(pAciCreditCardInfo.getTokenNumber(), 73,' '); //     73 char 252 - 324 C C
      String approvedCashBackAmount = StringUtils.rightPad(aciCCStatus.getApprovedCashBackAmount(), 12,' '); //   12 digit 325 - 336 C C
      String partialApprovalIndicator = StringUtils.rightPad(aciCCStatus.getPartialApprovalIndicator(), 1,' '); //  1 char 337 - 337 C C
      String authorizedNetworkID = StringUtils.rightPad(aciCCStatus.getAuthorizedNetworkID(), 6,' '); //   6 char 338 - 343 C C
      String feeProgramIndicator = StringUtils.rightPad(aciCCStatus.getFeeProgramIndicator(), 3,' '); //   3 char 344 - 346 C C
      String checkMICRData = StringUtils.rightPad(aciCCStatus.getCheckMICRData(), 76,' '); //   76 char 347 - 422 C C
      String mICRLineFormatCode = StringUtils.rightPad(aciCCStatus.getMicrLineFormatCode(), 2,' '); // 2 char 423 - 424 C C
      String accountType = StringUtils.rightPad(aciCCStatus.getAccountType(), 1,' '); //1 digit 425 - 425 C C
      
      //String originalJournalKey = StringUtils.rightPad(lJournalKey, 16,' '); //  16 char 426 - 441 C C
      //String originalJournalKey = null;
      if (settlmentType.equalsIgnoreCase(AciService.TRANS_TYPE_CREDIT)) {
        lJournalKey = "";
      } else {
        lJournalKey = aciCCStatus.getOriginalJournalKey();
      }      
      String originalJournalKey = StringUtils.rightPad(lJournalKey, 16,' '); //  16 char 426 - 441 C C
      
      
      String originalAuthorizationAmount = StringUtils.leftPad(AciUtils.convertAmountToRedReadableFormat(aciCCStatus.getAmount(),2), 11,'0'); //    11 digit 442 - 452 C C
      String salesTax = StringUtils.rightPad(aciCCStatus.getSalesTax(), 11,' '); // 11 digit 453 - 463 C C
      String netAuthorizedAmount = StringUtils.rightPad(aciCCStatus.getNetAuthorizedAmount(), 11,' '); //  11 digit 464 - 474 C C
      String merchantOrderNumber = StringUtils.rightPad(aciCCStatus.getMerchantOrderNumber(), 25,' '); //     25 char 475 - 499 C C
      String eCommerceIndicator = StringUtils.rightPad(aciCCStatus.getEcommerceIndicator(), 2,' '); //      2 char 500 - 501 C C
      //Conditional fields that are not required.
      /**
      String eCommerceCollectionIndicator = StringUtils.rightPad("", 2,' '); //      2 char 502 - 503 C C
      String mobileDeviceType = StringUtils.rightPad("", 2,' '); //      2 char 504 - 505 C C
      String originalPOSEntryMode = StringUtils.rightPad("", 2,' '); //      2 char 506 - 507 C C
      String terminalOutputCapability = StringUtils.rightPad("", 1,' '); //      1 char 508 - 508 C C
      String clearingSequenceNumber = StringUtils.rightPad("", 2,' '); //      2 digit 509 - 510 C C
      String clearingCount = StringUtils.rightPad("", 2,' '); // 2 digit 511 - 512 C C
      String paymentTenderType = StringUtils.rightPad("04", 2,' '); //     2 char 513 - 514 C C
      String serviceCode = StringUtils.rightPad("", 3,' '); // 3 digit 515 - 517 C C
      String transactionLoadFee = StringUtils.rightPad("", 11,' '); //      11 digit 518 - 528 C C
      String deviceID = StringUtils.rightPad("", 4,' '); // 4 digit 529 - 532 C C
      String spendQualifiedIndicator = StringUtils.rightPad("", 1,' '); //     1 char 533 - 533 C C
      String tokenRequestorID = StringUtils.rightPad("", 11,' '); //     11 digits 534 - 544 C C
      String tokenAssuranceLevel = StringUtils.rightPad("", 2,' '); //      2 char 545 - 546 C C
      String accountStatus = StringUtils.rightPad("", 1,' '); // 1 char 547 - 547 C C
      String merchantInvoiceNumber = StringUtils.rightPad("", 40,' '); //      40 char 548 - 587 C C
      String originalTransactionDate = StringUtils.rightPad("", 8,' '); //    8 digit 588 - 595 C C
      **/
      vlogInfo("tender amount set to {0}, settlement type = {1}, order id = {2}, payment group id = {3}", tenderAmount, settlmentType, 
          pAciCreditCardInfo.getOrder() != null ? pAciCreditCardInfo.getOrder().getId() : "null", pAciCreditCardInfo.getPaymentId());
  
      /******************************************************************************************/
      /******************************************************************************************/
      /********************PLEASE DO NOT CHANGE THE ORDER OF SEQUENCE OF THE ATTRIBUTES**********/
      /********************AN EXACT ORDER IS NEDED FOR SETTLEMENT FILES**************************/
      /******************************************************************************************/
      /******************************************************************************************/
      transRecord=    
          businessDate+
          storeNumber+
          terminalNumber+
          transactionNumber+
          tenderSequence+
          recordIdentifier+
          keySequence+
          journalKey+
          operatorNumber+
          transactionDate+ 
          processingIdentifier+
          tenderAmount+
          transactionType+
          invoiceNumber+
          tenderType+
          accountNumber+
          expirationDate+
          authorizationCode+
          authorizationDate+
          authorizationTime+
          authorizationSource+
          posEntryMode+
          currencyCode+
          userData+
          orderNumber+
          pointofSaleConditionCode+
          pointofSaleTerminalType+
          pointofSaleTerminalEntryCapability+
          rReAuthProcessIndicator+
          retryCounter+
          cardStartDate+
          cardIssueNumber+
          transactionBillingMethodIndicator+
          mccCode+
          cashBackAmount+
          localTime+
          networkID+
          retrievalReferenceNum+
          eBTVoucherNumber+
          productDeliveryMethod+
          accountNumberLength+
          sequenceOfCard+
          cardLevelResults+
          authorizationTrackNumber+
          authorizerBankNumber+
          marketSpecificIndicator+
          encryptedStringToken+
          approvedCashBackAmount+
          partialApprovalIndicator+
          authorizedNetworkID+
          feeProgramIndicator+
          checkMICRData+
          mICRLineFormatCode+
          accountType+
          originalJournalKey+
          originalAuthorizationAmount+
          salesTax+
          netAuthorizedAmount+
          merchantOrderNumber+
          eCommerceIndicator;
      
      vlogDebug("Settlement File Generator of Type: {0}", settlmentType);
      getSettlementGenerator().getPrintWriter().println(transRecord);

  }
  
  public void testGenerateTriggerFile() throws CommerceException {
    AciCreditCardStatus aciCCStatus = new AciCreditCardStatus();
    AciCreditCardInfo pAciCreditCardInfo = new AciCreditCardInfo();
    generateSettlementTriggerFile(pAciCreditCardInfo,aciCCStatus,"debit");
  }
  
  /**
   *  Validate the mandatory fields.
   * @param pAciCreditCardInfo
   * @param aciCCStatus
   * @return
   */
  public boolean mandatoryFeildsValidator(AciCreditCardInfo pAciCreditCardInfo,AciCreditCardStatus aciCCStatus) {
    
    boolean validFields = true;
    
    if(isEnableValidation()){
      if(aciCCStatus.getJournalKey() == null || aciCCStatus.getJournalKey().equals("")){
        this.logError("Missing Required Field getJournalKey");
        validFields = false;
        
      }
      if(aciCCStatus.getTransactionDate() == null || aciCCStatus.getTransactionDate().equals("")){
        this.logError("Missing Required Field getTransactionDate");
        validFields = false;
        
      }
      if(aciCCStatus.getTenderAmount() == null || aciCCStatus.getTenderAmount().equals("")){
        this.logError("Missing Required Field getTenderAmount");
        validFields = false;
        
      }
      if(aciCCStatus.getExpirationDate() == null || aciCCStatus.getExpirationDate().equals("")){
        this.logError("Missing Required Field getExpirationDate");
        validFields = false;
        
      }
      if(aciCCStatus.getAuthorizationCode() == null || aciCCStatus.getAuthorizationCode().equals("")){
        this.logError("Missing Required Field getAuthorizationCode");
        validFields = false;
        
      }
      if(aciCCStatus.getAuthorizationDate() == null || aciCCStatus.getAuthorizationDate().equals("")){
        this.logError("Missing Required Field getAuthorizationDate");
        validFields = false;
        
      }
      if(aciCCStatus.getAuthorizationTime() == null || aciCCStatus.getAuthorizationTime().equals("")){
        this.logError("Missing Required Field getAuthorizationTime");
        validFields = false;
        
      }
      if(aciCCStatus.getReAuthProcessIndicator() == null || aciCCStatus.getReAuthProcessIndicator().equals("")){
        this.logError("Missing Required Field getReAuthProcessIndicator");
        validFields = false;
        
      }
      if(aciCCStatus.getEbtVoucherNumber() == null || aciCCStatus.getEbtVoucherNumber().equals("")){
        this.logError("Missing Required Field getEbtVoucherNumber");
        validFields = false;
        
      }      
    }else{
      this.logInfo("Mandatory Feilds Validator turned off.");
    }

    return validFields;
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

  private String journeyKeyPrefix;
  
  
  public String getJourneyKeyPrefix() {
    return journeyKeyPrefix;
  }
  public void setJourneyKeyPrefix(String pJourneyKeyPrefix) {
    journeyKeyPrefix = pJourneyKeyPrefix;
  }
  
  public String buildJournalKey() {
    StringBuilder sb = new StringBuilder();
    try {
      String nextTransId;
      nextTransId = getIdGenerator().generateStringId(getIdGeneratorSpaceName());
      int numIdLen = nextTransId.length();
      int zeroCount = CLIENT_TRAN_ID_LENGTH - numIdLen;
      
      for (int i = 0; i < zeroCount; i++) {
        sb.append(ZERO);
      }
      
      sb.append(getJourneyKeyPrefix());
      sb.append(nextTransId);
      vlogDebug("ClientRefid {0}", sb.toString());
      
    } catch (IdGeneratorException e) {
      this.logError("buildJournalKey - " + e.toString());
    }
    return sb.toString();
  }
  
  public String checkAuthSource(String settlmentType){
    String authSourceValue = "";
    if(settlmentType!=null && settlmentType.equals(SETTLEMENT_TYPE_CREDIT)){
      authSourceValue = getAciConfiguration().getAuthSource();
    }
    return authSourceValue;
  }
  
  
  public SettlementGenerator getSettlementGenerator() {
    return settlementGenerator;
  }
  public void setSettlementGenerator(SettlementGenerator pSettlementGenerator) {
    settlementGenerator = pSettlementGenerator;
  }

  

}
