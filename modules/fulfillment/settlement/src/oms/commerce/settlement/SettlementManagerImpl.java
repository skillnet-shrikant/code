package oms.commerce.settlement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import com.aci.payment.creditcard.AciCreditCard;

//import org.bouncycastle.openpgp.PGPException;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderTools;
import com.mff.commerce.states.MFFPaymentGroupStates;
import com.mff.util.MFFUtils;
import com.mff.util.pgp.PGPEncryptionService;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.payment.PaymentManager;
import atg.commerce.states.StateDefinitions;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.payment.creditcard.CreditCardStatus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.payment.MFFPaymentManager;

public class SettlementManagerImpl extends GenericService implements SettlementConstants, SettlementManager {

  private static final String SETTLEMENT_PROP_RETRY_COUNT = "settlementRetryCount";
  private static final String SETTLEMENT_ID_RQL_QUERY = "settlementStatus = 0 OR settlementStatus = 2 RANGE +?1";
  private static final String SETTLEMENT_BY_STATUS_AND_PG_RQL_QUERY = "settlementStatus = ?0 and pgId = ?1";

  private static final String REFUND_PAYMENT_GROUP_RQL_QUERY = "state = ?0 order by submittedDate RANGE ?1+?2";

  /**
   * The settlement repository
   */
  private Repository settlementRepository;

  /**
   * A map between the status and values stored in the repository
   */
  private Map<String, String> statusStringMap = new HashMap<String, String>();

  /**
   * A map between the settlement types and values stored in the repository
   */
  private Map<String, String> settlementTypeStringMap = new HashMap<String, String>();

  /**
   * Maximum number of settlement records that can be processed in a given run
   */
  private int maxSettlementRecords;

  private OMSOrderManager orderManager;

  private PaymentManager paymentManager;

  /**
   * The maximum number of times a settlement can be processed before it is
   * marked as failed
   */
  private int maxSettlementRetryCount;

  private TransactionManager transactionManager;

  private static final String ITEM_DESCRIPTOR_PAYMENT_GROUP = "paymentGroup";
  
  private SettlementFileGenerator settlementFileGenerator;
  
  private PGPEncryptionService mPGPEncryptionService;
  
  private boolean checkForEmptySettlementFile;
  
  private boolean enablePGPEncryption;
  
  private String mSettlementId;
  
  private boolean checkSettlementStatus = true;
  
  private boolean deleteEmptyFiles = true;
  
  
  public void processSettlementRecord() {
    vlogDebug("Entering processSettlementRecord : ");

    if (StringUtils.isNotBlank(mSettlementId)) {
      vlogInfo("Processing the following settlement Id {0}", mSettlementId);
      String lFilename = null;
      try {
        lFilename = getSettlementFileGenerator().getCurrentFilename();

      } catch (CommerceException e) {
        vlogError(e, "Error occurred while creating the settlement file generator");
      }

      if (lFilename != null) {
        try {
          if (validateSettlementStatusForProcessing(getSettlementId())) {
            processSettlement(getSettlementId());
          }
        } catch (CommerceException e) {
          vlogError(e, "An error occurred while processing the settlement record {0}", getSettlementId());
        } finally {
          getSettlementFileGenerator().closeWriter();
        }
        encryptFile(lFilename);
      }
    }
    vlogDebug("Exiting processSettlementRecord : ");
  }
  
  protected boolean validateSettlementStatusForProcessing(String settlementId) {
    
    vlogDebug("Entering validateSettlementStatusForProcessing : settlementId");

    RepositoryItem pSettlement = null;
    boolean validationResult = false;

    try {
      pSettlement = getSettlementRepository().getItem(settlementId, SETTLEMENT_ITEM_DESCRIPTOR);
    } catch (RepositoryException e) {
      vlogError(e, "An error occurred while processing the settlement record {0}", settlementId);
    }

    if (pSettlement != null) {
      vlogDebug("Entering validateSettlementStatusForProcessing : pSettlement");
      if (checkSettlementStatus) {
        String lSettlementStatus = (String) pSettlement.getPropertyValue(SETTLEMENT_PROP_STATUS);
        if (lSettlementStatus.equalsIgnoreCase(SETTLE_STATUS_INITIAL) || lSettlementStatus.equalsIgnoreCase(SETTLE_STATUS_ERROR)) {

          validationResult = true;
        }
        vlogDebug("Exiting validateSettlementStatusForProcessing : pSettlement");
      } else {
        validationResult = true;
      }
    }

    vlogDebug("Exiting validateSettlementStatusForProcessing : settlementId");
    return validationResult;
  }

  /**
   * The following method is used to process settlement records that are to be
   * processed. The number of records that this method processes is limited by
   * the maximum number of settlement records that can be processed. This method
   * does not support concurrent executions of the settlement records.
   */
  public void processSettlementRecords() {

    vlogDebug("Entering processSettlementRecords");

    boolean hasMoreRecords = true;

    String fileName = null;
    try {

      fileName = getSettlementFileGenerator().getCurrentFilename();
    } catch (CommerceException e1) {
      vlogError(e1, "Could not open file for settlement");
      // we stop the execution of the settlements since we can't generate the
      // file
      hasMoreRecords = false;
    }

    try {
      while (hasMoreRecords) {
        List<String> settlementIds = getSettlementRecords();

        if (settlementIds != null && settlementIds.size() > 0) {
          for (String settlementId : settlementIds) {
            try {
              processSettlement(settlementId);
            } catch (CommerceException e) {
              vlogInfo(e, "An error occurred while processing the settlement");
              // we will just log the error and process the next
              // settlement record
              vlogError("An exception occurred while processing the settlement Id {0}", settlementId);
            }
          }
        }

        if (settlementIds == null || settlementIds.size() < getMaxSettlementRecords()) {
          hasMoreRecords = false;
        }
      } // end of while loop
      
      
    } finally {
      getSettlementFileGenerator().closeWriter();
    }
    
    encryptFile(fileName);
    
    vlogDebug("Exiting processSettlementRecords");
  }

  protected void encryptFile(String pFilename) {
    vlogDebug("Entering encryptFile : ");
    if (enablePGPEncryption) {
      

      boolean lFileHasRecords = true;
      if (checkForEmptySettlementFile) {
        try {
          BufferedReader br = new BufferedReader(new FileReader(pFilename));
          if (br.readLine() == null) {
            lFileHasRecords = false;
          }
          br.close();
        } catch (IOException e1) {
          vlogWarning(e1, "Trigger file could not be read {0}", pFilename);
        }
        
        if (!lFileHasRecords && isDeleteEmptyFiles()) {
          File inputFile = new File(pFilename);
          inputFile.delete();
        }
        
      }
      
      
      if (lFileHasRecords) {
        try {
          if (pFilename != null) getPGPEncryptionService().encrypt(pFilename);
        } catch (CommerceException e) {
          vlogWarning(e, "File could not be found {0}. Skipping the encryption of the file", pFilename);
        }
      }
          
      
    }
    vlogDebug("Exiting encryptFile : ");
  }


  /**
   * The following method creates a settlement record. This method creates a new
   * settlement record the status of "INITIAL" and with payment group provided
   * in input parameter
   * 
   * @param settlement
   *          the Settlement to be created
   * @throws RepositoryException
   *           when it fails to persists the settlement to the repository
   */
  protected void createSettlementRecord(Settlement settlement) throws RepositoryException {
    vlogDebug("Entering createSettlementRecord - settlement");
    MutableRepository lSettlementRep = (MutableRepository) getSettlementRepository();

    MutableRepositoryItem settleItem;

    settleItem = lSettlementRep.createItem(SETTLEMENT_ITEM_DESCRIPTOR);
    settleItem.setPropertyValue(SETTLEMENT_PROP_CREATE_DATE, settlement.getCreateDate());
    settleItem.setPropertyValue(SETTLEMENT_PROP_ORDER_ID, settlement.getOrderId());
    settleItem.setPropertyValue(SETTLEMENT_PROP_PG_ID, settlement.getPgId());
    settleItem.setPropertyValue(SETTLEMENT_PROP_AMOUNT, settlement.getSettlementAmount());
    settleItem.setPropertyValue(SETTLEMENT_PROP_ORDER_NUMBER, settlement.getOrderNumber());
    settleItem.setPropertyValue(SETTLEMENT_PROP_PG_DESC, settlement.getPgDesc());
    settleItem.setPropertyValue(SETTLEMENT_PROP_TYPE, getSettlementTypeStringMap().get(Integer.toString(settlement.getSettlementType())));

    settleItem.setPropertyValue(SETTLEMENT_PROP_PARTIAL_SETTLEMENT, settlement.isPartialSettlement());
    settleItem.setPropertyValue(SETTLEMENT_PROP_STATUS, getStatusStringMap().get(Integer.toString(settlement.getSettlementStatus())));
    lSettlementRep.addItem(settleItem);

    vlogDebug("Created settlement record for order {0} and pg {1}", settlement.getOrderId(), settlement.getPgId());
    vlogDebug("Exiting createSettlementRecord - settlement");
  }

  /**
   * The following method creates a settlement record. This method creates a new
   * settlement record the status of "INITIAL" and with payment group provided
   * in input parameter
   * 
   * @param settlement
   *          the Settlement to be created
   * 
   * @throws RepositoryException
   *           when it fails to persists the settlement to the repository
   */
  protected void createOrUpdateSettlementRecord(Settlement settlement) throws RepositoryException {

    vlogDebug("Entering createOrUpdateSettlementRecord : settlement, combineSettlementRecords");

    MutableRepositoryItem settleItem = null;
    MutableRepository lSettlementRep = null;

    lSettlementRep = (MutableRepository) getSettlementRepository();

    settleItem = (MutableRepositoryItem) getUnsettledSettlementRecordsByPaymentGroupId(settlement.getPgId());

    if (settleItem == null) {
      createSettlementRecord(settlement);
    } else {

      Double amtToBeSettled = (Double) settleItem.getPropertyValue(SETTLEMENT_PROP_AMOUNT);
      double updatedAmount = amtToBeSettled + settlement.getSettlementAmount();
      settleItem.setPropertyValue(SETTLEMENT_PROP_AMOUNT, roundPrice(updatedAmount));

      vlogDebug("Incrementing the settlement record {0} by {1}: new value = {2}", settleItem.getRepositoryId(), settlement.getSettlementAmount(), settleItem.getPropertyValue(SETTLEMENT_PROP_AMOUNT));

      lSettlementRep.updateItem(settleItem);

    }
    vlogDebug("Exiting createOrUpdateSettlementRecord : settlement, combineSettlementRecords");
  }

  public Repository getSettlementRepository() {
    return settlementRepository;
  }

  public void setSettlementRepository(Repository settlementRepository) {
    this.settlementRepository = settlementRepository;
  }

  public int getMaxSettlementRecords() {
    return maxSettlementRecords;
  }

  public void setMaxSettlementRecords(int maxSettlementRecords) {
    this.maxSettlementRecords = maxSettlementRecords;
  }
  
  /**
   * The following method queries the settlement records and gets the next batch
   * of settlement record ids to be processed
   * 
   * @return a list of settlement record ids
   */  
  protected List<String> getSettlementRecordsByOrderNumber() {
    vlogDebug("Entering getSettlementRecords ");
    List<String> settlementIds = new ArrayList<String>();

    Repository settlementRepository = getSettlementRepository();
    try {
      RepositoryItemDescriptor settlementItemDescriptor = settlementRepository.getItemDescriptor(SETTLEMENT_ITEM_DESCRIPTOR);
      RepositoryView settlementView = settlementItemDescriptor.getRepositoryView();
      RqlStatement statement = RqlStatement.parseRqlStatement(SETTLEMENT_ID_RQL_QUERY);

      Object params[] = new Object[21];
      // params[0] = new Integer(getMaxSettlementRetryCount());
      params[0] = new Integer(getMaxSettlementRecords());

      RepositoryItem[] paymentSettlements = statement.executeQuery(settlementView, params);
      if (paymentSettlements == null || !(paymentSettlements.length > 0)) {
        vlogDebug("no more records available to be processed");

      } else {
        for (RepositoryItem paymentSettlement : paymentSettlements) {
          settlementIds.add(paymentSettlement.getRepositoryId());
        }
      }
    } catch (RepositoryException e) {
      vlogError(e, "Could not determine the list of settlement records");
    }

    vlogDebug("Exiting getSettlementRecords");
    return settlementIds;
  }  

  /**
   * The following method queries the settlement records and gets the next batch
   * of settlement record ids to be processed
   * 
   * @return a list of settlement record ids
   */
  protected List<String> getSettlementRecords() {
    vlogDebug("Entering getSettlementRecords ");
    List<String> settlementIds = new ArrayList<String>();

    Repository settlementRepository = getSettlementRepository();
    try {
      RepositoryItemDescriptor settlementItemDescriptor = settlementRepository.getItemDescriptor(SETTLEMENT_ITEM_DESCRIPTOR);
      RepositoryView settlementView = settlementItemDescriptor.getRepositoryView();
      RqlStatement statement = RqlStatement.parseRqlStatement(SETTLEMENT_ID_RQL_QUERY);

      Object params[] = new Object[21];
      // params[0] = new Integer(getMaxSettlementRetryCount());
      params[0] = new Integer(getMaxSettlementRecords());

      RepositoryItem[] paymentSettlements = statement.executeQuery(settlementView, params);
      if (paymentSettlements == null || !(paymentSettlements.length > 0)) {
        vlogDebug("no more records available to be processed");

      } else {
        for (RepositoryItem paymentSettlement : paymentSettlements) {
          settlementIds.add(paymentSettlement.getRepositoryId());
        }
      }
    } catch (RepositoryException e) {
      vlogError(e, "Could not determine the list of settlement records");
    }

    vlogDebug("Exiting getSettlementRecords");
    return settlementIds;
  }

  /**
   * Following method processes settlement record associated with a settlement
   * ID. This method uses the payment manager's debit and credit operations to
   * process the settlement. </br>
   * 
   * If these operations are successfully executed then the settlement record is
   * marked as {@link SettlementConstants#SETTLE_STATUS_SETTLED}. </br>
   * 
   * If the credit or debit fails then the record is marked
   * {@link SettlementConstants#SETTLE_STATUS_ERROR} if we have not reached the
   * maximum number of retries defined by {@link #maxSettlementRetryCount}.
   * </br>
   * 
   * If max number of attempts has been made then the settlement status is set
   * to {@link SettlementConstants#SETTLE_STATUS_FAILURE}
   * 
   * @param settlementId
   *          the settlement id to be processed
   * @throws CommerceException
   *           when an exception occurs while trying to restore order or payment
   *           group
   */
  @SuppressWarnings("unchecked")
  protected void processSettlement(String settlementId) throws CommerceException {

    vlogDebug("Entering processSettlement");

    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      MutableRepositoryItem paymentSettlement;
      String paymentGroupId;
      Double settlementAmt;
      String orderId;

      paymentSettlement = ((MutableRepository) getSettlementRepository()).getItemForUpdate(settlementId, SETTLEMENT_ITEM_DESCRIPTOR);

      paymentGroupId = (String) paymentSettlement.getPropertyValue(SETTLEMENT_PROP_PG_ID);
      settlementAmt = (Double) paymentSettlement.getPropertyValue(SETTLEMENT_PROP_AMOUNT);
      orderId = (String) paymentSettlement.getPropertyValue(SETTLEMENT_PROP_ORDER_ID);

      if (isLoggingDebug()) {
        vlogDebug("Processing the setttlement id {0}:", settlementId);
        vlogDebug("Payment group id {0}, settlement amount = {1}, order id = {2}", paymentGroupId, settlementAmt, orderId);
      }

      if (settlementAmt == null || orderId == null) {

        vlogWarning("Invalid settlement record {0} payment group id {1}", paymentSettlement.getRepositoryId(), paymentGroupId);
        vlogWarning("Skipping the processing of the settlement record");

      } else {

        Order order = getOrderManager().loadOrder(orderId);
        List<PaymentGroup> paymentGroups = order.getPaymentGroups();

        PaymentGroup settlementPaymentGroup = null;

        for (PaymentGroup paymentGroup : paymentGroups) {
          if (paymentGroupId.equalsIgnoreCase(paymentGroup.getId())) {
            settlementPaymentGroup = paymentGroup;
            break;
          }
        }
        try {
          double lAbsSettlementAmount = Math.abs(settlementAmt.doubleValue());
          if (isCredit(settlementAmt)) {

            // credit the amount
            getPaymentManager().credit(order, settlementPaymentGroup, lAbsSettlementAmount);

            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_STATUS, SETTLE_STATUS_SETTLED);
            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_SETTLE_DATE, new Date());

            vlogInfo("Settlement - Credit successful order id = {0} payment group = {1} settlement amount = {2}", orderId, paymentGroupId, lAbsSettlementAmount);

            String lTransType = "";

            if (paymentSettlement.getPropertyValue("settlementType") != null) {

              lTransType = paymentSettlement.getPropertyValue("settlementType").toString();
              vlogDebug("TransType:{0}", lTransType);
            }

          } else {

            // debit the amount
        	if(lAbsSettlementAmount!=0){
        		if(!isSettlementAmtEqualsAuthorizedAmt(settlementPaymentGroup.getAmountAuthorized(), lAbsSettlementAmount)){
        			if(settlementPaymentGroup instanceof AciCreditCard){
	        			//((MFFPaymentManager) getPaymentManager()).reverseAuthorizationDuringSettlement(settlementPaymentGroup, settlementPaymentGroup.getAmountAuthorized());
	        			//((MFFPaymentManager) getPaymentManager()).authorize(order, settlementPaymentGroup, lAbsSettlementAmount);
        				CreditCardStatus reAuthStatus=((MFFPaymentManager)getPaymentManager()).reAuthorize(order, settlementPaymentGroup, lAbsSettlementAmount);
        				if(reAuthStatus.getTransactionSuccess()){
        					((MFFPaymentManager)getPaymentManager()).reverseAuthorizationDuringSettlement(settlementPaymentGroup, settlementPaymentGroup.getAmountAuthorized());
        					((MFFPaymentManager)getPaymentManager()).postProcessReAuthorization(settlementPaymentGroup, reAuthStatus, lAbsSettlementAmount);
        					getOrderManager().updateOrder(order);
        				}
        			}
        		}
        		
        		getPaymentManager().debit(order, settlementPaymentGroup, lAbsSettlementAmount);
                vlogDebug("Debit settlement successful for order id = {0}, payment group = {1} the amount = {2}", orderId, paymentGroupId, lAbsSettlementAmount);
        	}
            // set the payment
            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_STATUS, SETTLE_STATUS_SETTLED);
            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_SETTLE_DATE, new Date());

            try {

              ((MFFPaymentManager) getPaymentManager()).validateAndReauthorizedPaymentForSettlement(settlementPaymentGroup);

            } catch (Throwable t) {

              // We ignore any exceptions being thrown by this method. We will
              // try to re-authorize again prior to shipment.
              vlogWarning(t, "Could not validate and reauthorize after settling the payment group id {0}", settlementPaymentGroup.getId());
            }

            vlogInfo("Settlement - Debit successful order id = {0} payment group = {1} settlement amout = {2}", orderId, paymentGroupId, lAbsSettlementAmount);
          }
        } catch (Throwable t) {
          vlogWarning(t, "Could not settle the amount for {0} settlement amount = {1}", paymentSettlement.getRepositoryId(), settlementAmt.doubleValue());

          // set the settlement status to error and
          // increment the retry count;
          Integer retryCountInt = (Integer) paymentSettlement.getPropertyValue(SETTLEMENT_PROP_RETRY_COUNT);
          int retryCount = 0;
          if (retryCountInt != null) {
            retryCount = retryCountInt.intValue();
          }
          ++retryCount;
          paymentSettlement.setPropertyValue(SETTLEMENT_PROP_RETRY_COUNT, new Integer(retryCount));
          if (retryCount < getMaxSettlementRetryCount()) {
            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_STATUS, SETTLE_STATUS_ERROR);
          } else {
            paymentSettlement.setPropertyValue(SETTLEMENT_PROP_STATUS, SETTLE_STATUS_FAILURE);
          }
        }

        getOrderManager().updateOrder(order);
      }
      rollback = false;
    } catch (RepositoryException e) {
      // log the error and move on to the next record
      vlogError(e, "Could not process the settlement record id = ", settlementId);
      throw new CommerceException(e);
    } catch (TransactionDemarcationException e) {
      vlogError(e, "Transaction could not be handlled while settling the settlement record : id = {0}", settlementId);
      throw new CommerceException(e);
    } finally {
      if (getTransactionManager() != null) {
        try {
          td.end(rollback);
        } catch (Throwable e) {
          if (isLoggingError()) vlogWarning(e, "Error occurred while rollbacking the changes");
        }
      }
    }

    vlogDebug("Exiting processSettlement");
  }

  protected boolean isCredit(double settlementAmt) {

    return (settlementAmt < 0);
  }

  public int getMaxSettlementRetryCount() {
    return maxSettlementRetryCount;
  }

  public void setMaxSettlementRetryCount(int maxSettlementRetryCount) {
    this.maxSettlementRetryCount = maxSettlementRetryCount;
  }

  public PaymentManager getPaymentManager() {
    return paymentManager;
  }

  public void setPaymentManager(PaymentManager paymentManager) {
    this.paymentManager = paymentManager;
  }

  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * @return the statusStringMap
   */
  public Map<String, String> getStatusStringMap() {
    return statusStringMap;
  }

  /**
   * @param statusStringMap
   *          the statusStringMap to set
   */
  public void setStatusStringMap(Map<String, String> statusStringMap) {
    this.statusStringMap = statusStringMap;
  }

  /**
   * Retrieves the settlement record that are yet to be settled for a given
   * payment group id that is yet to be settled. We assume that there is only
   * one unsettled record for a given payment group.
   * 
   * @return the repository item associated with the settlement record
   */
  protected RepositoryItem getUnsettledSettlementRecordsByPaymentGroupId(String pgId) {
    vlogDebug("Entering getSettlementRec - pgId");
    RepositoryItem retValue = null;
    Repository settlementRepository = getSettlementRepository();
    RepositoryItemDescriptor settlementItemDescriptor;
    try {
      settlementItemDescriptor = settlementRepository.getItemDescriptor(SETTLEMENT_ITEM_DESCRIPTOR);
      RepositoryView settlementView = settlementItemDescriptor.getRepositoryView();
      RqlStatement statement = RqlStatement.parseRqlStatement(SETTLEMENT_BY_STATUS_AND_PG_RQL_QUERY);
      Object params[] = new Object[2];

      params[0] = SETTLE_STATUS_INITIAL;
      params[1] = pgId;

      RepositoryItem[] paymentSettlements = statement.executeQuery(settlementView, params);
      if (paymentSettlements != null && paymentSettlements.length > 0) {
        retValue = paymentSettlements[0];
      }
    } catch (RepositoryException e) {
      vlogError(e, "Error while fetching settlment record for payment group id = {0}", pgId);
    }
    vlogDebug("Exiting getSettlementRec - pgId");
    return retValue;

  }

  /**
   * Creates a settlement record
   * 
   * @param pOrderId
   * @param pOrderNumber
   * @param pPaymentGroupId
   * @param pPaymentDescription
   * @param pPartialSettlement
   * @param pAmountToSettle
   * @throws RepositoryException
   */
  protected void createSettlement(String pOrderId, String pOrderNumber, String pPaymentGroupId, String pPaymentDescription, boolean pPartialSettlement, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws RepositoryException {
    vlogDebug("Entering createSettlement : pOrderId, pOrderNumber, pPaymentGroupId, pPaymentDescription, pPartialSettlement, pAmountToSettle");
    Settlement stl = new Settlement();
    stl.setCreateDate(new Date());
    stl.setOrderId(pOrderId);
    stl.setPgId(pPaymentGroupId);
    stl.setOrderNumber(pOrderNumber);
    stl.setPgDesc(pPaymentDescription);
    stl.setPartialSettlement(pPartialSettlement);

    stl.setSettlementAmount(roundPrice(pAmountToSettle));

    createOrUpdateSettlementRecord(stl);

    updateSettlementMap(pSettlementMap, pPaymentGroupId, pAmountToSettle);

    vlogDebug("Exiting createSettlement : pOrderId, pOrderNumber, pPaymentGroupId, pPaymentDescription, pPartialSettlement, pAmountToSettle");
  }

  /**
   * The following method is used to create settlements for credit cards
   * 
   * @param paymentGroup
   * @param pOrderNumber
   * @param pOrderId
   * @param pAmountToSettle
   * @throws RepositoryException
   */
  protected void createCreditCardSettlement(CreditCard paymentGroup, String pOrderNumber, String pOrderId, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws RepositoryException {
    vlogDebug("Entering createCreditCardSettlement : paymentGroup, pOrderNumber, pOrderId, pAmountToSettle");
    boolean lPartialSettlement = false;
    if (paymentGroup.getAmount() < pAmountToSettle) {
      lPartialSettlement = true;
    }

    createSettlement(pOrderId, pOrderNumber, paymentGroup.getId(), paymentGroup.getPaymentGroupClassType(), lPartialSettlement, pAmountToSettle, pSettlementMap);
  }

  /**
   * The following method is used to create settlement records for gift cards
   * 
   * @param paymentGroup
   * @param pOrderNumber
   * @param pOrderId
   * @param pAmountToSettle
   * @throws RepositoryException
   */
  protected void createGiftCardSettlement(MFFGiftCardPaymentGroup paymentGroup, String pOrderNumber, String pOrderId, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws RepositoryException {
    vlogDebug("Entering createGiftCardSettlement : paymentGroup, pOrderNumber, pOrderId, pAmountToSettle");
    boolean lPartialSettlement = false;
    if (paymentGroup.getAmount() < pAmountToSettle) {
      lPartialSettlement = true;
    }
    createSettlement(pOrderId, pOrderNumber, paymentGroup.getId(), paymentGroup.getPaymentGroupClassType(), lPartialSettlement, pAmountToSettle, pSettlementMap);
    vlogDebug("Exiting createGiftCardSettlement : paymentGroup, pOrderNumber, pOrderId, pAmountToSettle");
  }

  /**
   * The following method is responsible for crediting a specific amount from
   * the list of available payment groups. This method will first use the
   * available funds on the credit card before using the funds available on the
   * gift card payments. If there isn't sufficient funds available this method
   * will throw a CommerceException
   * 
   * @param pOrder
   * @param pAmountToSettle
   * @throws CommerceException
   * @throws RepositoryException
   */
  @SuppressWarnings("unchecked")
  public void createCreditSettlements(Order pOrder, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws CommerceException, RepositoryException {

    vlogDebug("Entering createCreditSettlements : pOrder, pAmountToSettle, pInvoiceId");

    List<PaymentGroup> paymentGroups = null;
    double lAmtToSettle = pAmountToSettle;
    String lOrderNumber = ((MFFOrderImpl) pOrder).getOrderNumber();
    if (pAmountToSettle <= 0.0d) {
      vlogError("The amount to credit was less than zero order={0}, amount to settle={1}", pOrder.getId(), pAmountToSettle);
      throw new CommerceException("Invalid amount to debit");
    }

    // Get a handle on the list of payment groups available on the order
    paymentGroups = pOrder.getPaymentGroups();
    lAmtToSettle = pAmountToSettle;

    // first loop through the credit cards and create settlement records
    for (PaymentGroup pg : paymentGroups) {
      if (pg instanceof CreditCard) {

        lAmtToSettle = createCCCreditSettlements((CreditCard) pg, lOrderNumber, pOrder.getId(), lAmtToSettle, pSettlementMap);
      }
      if (lAmtToSettle <= 0.0d) break;
    }

    // Get the total funds available and create a blank gift card
    if (lAmtToSettle > 0.0d) {
      createGCCreditSettlements(pOrder, lAmtToSettle);
    }

    vlogDebug("Exiting createCreditSettlements : pOrder, pAmountToSettle, pInvoiceId");
  }

  /**
   * The following method will be used to create debit settlement records. This
   * method will first debit funds available on a gift card and then proceed to
   * debit any funds that are available on the credit card payments. If the
   * funds available is insufficient then a CommerceException is thrown by this
   * method.
   * 
   * @param pOrder
   *          the order
   * @param pAmountToSettle
   *          the amount to settle
   * @throws CommerceException
   * @throws RepositoryException
   */
  @SuppressWarnings("unchecked")
  public void createDebitSettlements(Order pOrder, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws CommerceException, RepositoryException {
    vlogDebug("Entering createDebitSettlements : pOrder, pCommerceItemId, pInvoiceId, pAmountToSettle");

    List<PaymentGroup> paymentGroups = null;
    double lAmtToSettle = pAmountToSettle;

    String lOrderNumber = ((MFFOrderImpl) pOrder).getOrderNumber();

    if (pAmountToSettle <= 0.0d) {
      vlogError("The amount to debit was less than zero order={0}, amount to settle={1}", pOrder.getId(), pAmountToSettle);
      throw new CommerceException("Invalid amount to settle");
    }

    // Get a handle on the list of payment groups available on the order
    paymentGroups = pOrder.getPaymentGroups();

    for (PaymentGroup pg : paymentGroups) {

      // First loop through the list of gift card payments
      // settle gift card payments first

      if (pg instanceof MFFGiftCardPaymentGroup) {
        lAmtToSettle = createDebitSettlement(pg, lOrderNumber, pOrder.getId(), lAmtToSettle, pSettlementMap);
        vlogDebug("Amount to settle set = {0}", lAmtToSettle);
      }
      if (lAmtToSettle <= 0.0d) break;

    }

    // Now loop through the list of payment groups if needed and
    // debit credit cards
    if (lAmtToSettle > 0.0d) {
      for (PaymentGroup pg : paymentGroups) {
        if (pg instanceof CreditCard) {
          lAmtToSettle = createDebitSettlement(pg, lOrderNumber, pOrder.getId(), lAmtToSettle, pSettlementMap);
          vlogDebug("Amount to settle set = {0}", lAmtToSettle);
        }

        if (lAmtToSettle <= 0.0d) break;
      }
    }
    

    if (lAmtToSettle > 0.0d) {
      vlogError("There are not enough funds available to perform the debit order id {0} amount = {1}, lAmtToSettle = {2}", pOrder.getId(), pAmountToSettle, lAmtToSettle);
      logFundsAvailable(paymentGroups);
      throw new CommerceException("Insufficient Funds - Please Contact the Call Center");

    }

    vlogDebug("Exiting createDebitSettlements : pOrder, pCommerceItemId, pInvoiceId, pAmountToSettle");
    return;
  }

  private void logFundsAvailable(List<PaymentGroup> paymentGroups) {
    if (isLoggingError()) {
      for (PaymentGroup pg : paymentGroups) {
        vlogError("payment group id {0}, funds available = {1}", pg.getId(), calcAmountAvailableToDebit(pg));
      }
    }
  }

  /**
   * The following method attempts to debit an amount from a payment group. The
   * method determines the amount available for settlement and based on this it
   * debits the right amount. If the payment group contains enough funds to
   * cover the settlement amount it will debits the complete amount and returns
   * zero. If can cover only partial amount then it debits funds available and
   * returns the balance amount. If there is no funds are available then no
   * settlement records are created
   * 
   * @param pg
   *          the payment group
   * @param pOrderNumber
   *          the order number associated with the order
   * @param pOrderId
   *          the internal order id associated with the order
   * @param pSettlementAmount
   *          the settlement amount to be debited
   * @param pSettlementMap
   *          the settlement map to be populated
   * 
   * @return the balance amount.
   * @throws RepositoryException
   */
  protected double createDebitSettlement(PaymentGroup pg, String pOrderNumber, String pOrderId, double pSettlementAmount, HashMap<String, Double> pSettlementMap) throws RepositoryException {

    vlogDebug("Entering createDebitSettlement : pg, pInvoiceId, pSettlementAmount");

    double lBalanceAmount = pSettlementAmount;
    double lFundsAvailableForSettlement;
    double lAmtToSettle = 0.0d;

    vlogDebug("Trying to settle the amount {0} using the payment group {1}", pSettlementAmount, pg.getId());

    lFundsAvailableForSettlement = calcAmountAvailableToDebit(pg);

    vlogDebug("Funds available for settlement {0}", lFundsAvailableForSettlement);

    if (lFundsAvailableForSettlement > 0.0d && pSettlementAmount > 0.0d) {

      if (lFundsAvailableForSettlement >= pSettlementAmount) {
        lBalanceAmount = 0.0d;
        lAmtToSettle = pSettlementAmount;
      } else {
        lBalanceAmount = lBalanceAmount - lFundsAvailableForSettlement;
        lAmtToSettle = lFundsAvailableForSettlement;
      }
    }

    if (lAmtToSettle > 0.0d) {
      if (pg instanceof CreditCard) {
        createCreditCardSettlement((CreditCard) pg, pOrderNumber, pOrderId, lAmtToSettle, pSettlementMap);

      } else {

        createGiftCardSettlement((MFFGiftCardPaymentGroup) pg, pOrderNumber, pOrderId, lAmtToSettle, pSettlementMap);
      }

      // create the payment settlement records
      createPaymentSettlement(pg, lAmtToSettle, pOrderNumber);
    }
    vlogDebug("Exiting createDebitSettlement : pg, pInvoiceId, pSettlementAmount");
    lBalanceAmount = roundPrice(lBalanceAmount);
    return lBalanceAmount;
  }

  /**
   * The following method attempts to credit an amount from a payment group. The
   * method determines the amount available for settlement and based on this it
   * credits the right amount. If the payment group contains enough funds to
   * cover the settlement amount it will credits the complete amount and returns
   * zero. If can cover only partial amount then it credits funds available and
   * returns the balance amount. If there are no funds are available then no
   * settlement records are created
   * 
   * @param pg
   *          the payment group
   * @param pOrderNumber
   *          the order number associated with the order
   * @param pOrderId
   *          the internal order id associated with the order
   * @param pSettlementAmount
   *          the settlement amount to be credited
   * @return the balance amount.
   * @throws RepositoryException
   */
  public double createCCCreditSettlements(CreditCard pg, String pOrderNumber, String pOrderId, double pSettlementAmount, HashMap<String, Double> pSettlementMap) throws RepositoryException {

    vlogDebug("Entering createCreditSettlements : pg, pInvoiceId, pSettlementAmount");

    double lBalanceAmount = pSettlementAmount;
    double lFundsAvailableForSettlement;
    double lAmtToSettle = 0.0d;

    vlogDebug("Trying to credit the amount {0} using the payment group {1}", pSettlementAmount, pg.getId());

    lFundsAvailableForSettlement = calcAmountAvailableForRefund(pg);

    vlogDebug("Funds available for settlement {0}", lFundsAvailableForSettlement);

    if (lFundsAvailableForSettlement > 0.0d && pSettlementAmount > 0.0d) {

      if (lFundsAvailableForSettlement >= pSettlementAmount) {
        lBalanceAmount = 0.0d;
        lAmtToSettle = pSettlementAmount;
      } else {
        lBalanceAmount = lBalanceAmount - lFundsAvailableForSettlement;
        lAmtToSettle = lFundsAvailableForSettlement;
      }
    }

    if (lAmtToSettle > 0.0d) {

      // in settlement world the credits are indicated by a negative value
      lAmtToSettle = -1 * lAmtToSettle;

      createCreditCardSettlement(pg, pOrderNumber, pOrderId, lAmtToSettle, pSettlementMap);

      // finally create a payment settlement. The payment settlement is
      // used to determine the funds available for settlement on a payment
      // group
      createPaymentSettlement(pg, lAmtToSettle, pOrderNumber);

    }
    vlogDebug("Exiting createCreditSettlements : pg, pInvoiceId, pSettlementAmount");
    lBalanceAmount = roundPrice(lBalanceAmount);
    return lBalanceAmount;
  }

  /**
   * The following method credits the customer uses a gift card. This method is
   * called after crediting all available funds on the credit cards. Hence if
   * the settlement amount is more than available funds then we throw a
   * CommerceException
   * 
   * @param pOrder
   *          the order
   * @param pSettlementAmount
   *          the amount that is being credited
   * @throws CommerceException
   *           when there is not enough funds available for credit or when there
   *           is a repository exception
   */
  protected void createGCCreditSettlements(Order pOrder, double pSettlementAmount) throws CommerceException {

    double lGCFundsAvailableForSettlement;

    vlogDebug("Trying to credit the amount {0} using a gift card", pSettlementAmount);

    lGCFundsAvailableForSettlement = calcAvailableGCFundsForRefund(pOrder);

    // The assumption:
    // We have credited all the funds available for credit before we try to use
    // credits from gift card
    if (pSettlementAmount > lGCFundsAvailableForSettlement) {
      vlogError("There are not enough funds available to perform the credit order id {0} amount = {1}, gift card funds available = {2}", pOrder.getId(), pSettlementAmount, lGCFundsAvailableForSettlement);
      throw new CommerceException("Insufficient funds available for credit");
    }

    vlogDebug("Funds available for settlement {0}", lGCFundsAvailableForSettlement);

    if (lGCFundsAvailableForSettlement > 0.0d && pSettlementAmount > 0.0d) {
      createPaymentSettlementsForGiftCardRefund(pOrder, pSettlementAmount);
    }
  }

  public double roundPrice(double pNumber) {
    BigDecimal bd = new BigDecimal(Double.toString(pNumber));
    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
    return bd.doubleValue();
  }

  /**
   * Returns the amount available for refund for a credit card.
   * 
   * @param pPaymentGroup
   *          the credit card
   * @return the amount available for refund (a positive value)
   */
  public double calcAmountAvailableForRefund(CreditCard pPaymentGroup) {
    return getSettlementTotal(pPaymentGroup);
  }

  /**
   * The following method should only be used internally and hence protected.
   * For all purposes the method {@link #calcAvailableGCFundsForRefund(Order)}
   * should be used to determine the funds available for refund.
   * 
   * @param pPaymentGroup
   * @return the amount available for refund (a positive value)
   */
  protected double calcAmountAvailableForRefund(MFFGiftCardPaymentGroup pPaymentGroup) {
    return getSettlementTotal(pPaymentGroup);
  }

  /**
   * This method sums up the list of settlements associated with a specific
   * payment group.
   * 
   * @param pPaymentGroup
   *          the payment group
   * @return the amount available a positive value indicates funds available for
   *         credit. A negative value will be returned if the pPaymentGroup is a
   *         refund payment group.
   */
  @SuppressWarnings("unchecked")
  protected double getSettlementTotal(PaymentGroup pPaymentGroup) {
    double fundsAvailable = 0.0d;
    List<RepositoryItem> pgSettles = (List<RepositoryItem>) ((PaymentGroupImpl) pPaymentGroup).getPropertyValue(PAYMENTGROUP_PROP_SETTLEMENTS);
    if (pgSettles != null) {
      for (RepositoryItem paySettlement : pgSettles) {
        fundsAvailable += (Double) paySettlement.getPropertyValue(PG_SETTLEMENT_PROP_SETTLEMENT_AMOUNT);
        vlogDebug("Settlement id {0}, total funds available {1}", paySettlement.getRepositoryId(), fundsAvailable);
      }
    }
    
    fundsAvailable = roundPrice(fundsAvailable);
    vlogDebug("Total available funds for payment group {0}, amount = {1}", pPaymentGroup.getId(), fundsAvailable);
    return fundsAvailable;
  }

  /**
   * The following method creates a <code>pgSettlement</code> repository item
   * and associates it with the payment group that is being settled against. The
   * payment settlement keeps track the list of settlements that has been
   * created against a payment group (both credits and debits). This method is
   * called for all credits and debits for a credit card. For gift cards the
   * debit is against the original payment group that was created in the
   * checkout process. Whereas in the case of a credit, the payment group is
   * nothing but the newly created refund payment group that represents the new
   * gift card that is about to be activated.
   * 
   * @param pPaymentGroup
   *          the payment group
   * @param settleAmount
   *          the amount for which the item is being settled. A positive value
   *          indicates a debit and a negative value indicates a credit
   *          operation.
   * @param invoiceId
   *          the order number associated with the payment group
   * @throws RepositoryException
   */
  @SuppressWarnings("unchecked")
  protected void createPaymentSettlement(PaymentGroup pPaymentGroup, double settleAmount, String invoiceId) throws RepositoryException {

    vlogDebug("Entering createPaymentSettlement - pg, settleAmount, invoiceId");
    vlogDebug("Payment group id = {0}, settlement amount = {1}, invoice id = {2}", pPaymentGroup.getId(), settleAmount, invoiceId);

    PaymentGroupImpl pPaymentGroupImpl = (PaymentGroupImpl) pPaymentGroup;

    MutableRepository lOrderRep = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
    MutableRepositoryItem pgSettleItem = lOrderRep.createItem(PG_SETTLEMENT_ITEM_DESCRIPTOR);

    pgSettleItem.setPropertyValue(PG_SETTLEMENT_PROP_INVOICEID, invoiceId);
    pgSettleItem.setPropertyValue(PG_SETTLEMENT_PROP_DATE, new Date());
    pgSettleItem.setPropertyValue(PG_SETTLEMENT_PROP_SETTLEMENT_AMOUNT, settleAmount);
    lOrderRep.addItem(pgSettleItem);

    List<RepositoryItem> pgSettles = (List<RepositoryItem>) pPaymentGroupImpl.getPropertyValue(PAYMENTGROUP_PROP_SETTLEMENTS);
    if (pgSettles == null) {
      pgSettles = new ArrayList<RepositoryItem>();
    }
    pgSettles.add(pgSettleItem);

    vlogDebug("Adding the pgSettlement record {0} to the payment group {1}", pgSettleItem.getRepositoryId(), pPaymentGroup.getId());

    pPaymentGroupImpl.setPropertyValue(PAYMENTGROUP_PROP_SETTLEMENTS, pgSettles);

    vlogDebug("Exiting createPaymentSettlement - pg, settleAmount, invoiceId");
  }

  /**
   * The following method is used to determine the amount that needs to be
   * activated for a given gift card payment group that was created as part of a
   * credit settlement. This method will always return a positive value.
   * 
   * @param pGiftCardPaymentGroup
   *          the gift card payment group
   * @return the final amount that needs to be added to the card
   * @throws CommerceException
   */
  @SuppressWarnings("unchecked")
  public double getRefundAmount(MFFGiftCardPaymentGroup pGiftCardPaymentGroup) throws CommerceException {
    vlogDebug("Entering getRefundAmount : pGiftCardPaymentGroup");
    double lRefundAmount = 0.0;

    List<RepositoryItem> settlements = (List<RepositoryItem>) pGiftCardPaymentGroup.getPropertyValue(PAYMENTGROUP_PROP_SETTLEMENTS);
    if (settlements == null || !(settlements.size() > 0)) {
      throw new CommerceException("Expected at least payment settlement but none found for payment group id " + pGiftCardPaymentGroup.getId());
    }

    for (RepositoryItem pgSettlement : settlements) {

      Double settlementAmt = (Double) pgSettlement.getPropertyValue(PG_SETTLEMENT_PROP_SETTLEMENT_AMOUNT);

      if (settlementAmt != null) {
        lRefundAmount = lRefundAmount + Math.abs(settlementAmt.doubleValue());
      }
    }

    lRefundAmount = roundPrice(lRefundAmount);

    vlogDebug("The refund amount is set to {0}", lRefundAmount);
    vlogDebug("Exiting getRefundAmount : pGiftCardPaymentGroup");
    return lRefundAmount;

  }

  public RepositoryItem[] getGiftCardRefundsToBeActivated(int startIndex, int numberOfRecords) throws RepositoryException {
    vlogDebug("Entering getGiftCardRefundsToBeActivated : startIndex, numberOfRecords");
    Repository omsOrderRepository = orderManager.getOmsOrderRepository();
    RepositoryItemDescriptor paymentGroupItemDesc = omsOrderRepository.getItemDescriptor(ITEM_DESCRIPTOR_PAYMENT_GROUP);
    RepositoryView paymentGroupView = paymentGroupItemDesc.getRepositoryView();
    RqlStatement statement = RqlStatement.parseRqlStatement(REFUND_PAYMENT_GROUP_RQL_QUERY);
    Object params[] = new Object[3];

    params[0] = MFFPaymentGroupStates.TO_BE_ACTIVATED;
    params[1] = startIndex;
    params[2] = numberOfRecords;
    RepositoryItem[] giftCardRefunds = statement.executeQuery(paymentGroupView, params);

    vlogDebug("Exiting getGiftCardRefundsToBeActivated : startIndex, numberOfRecords");
    return giftCardRefunds;
  }

  public void settleGiftCardRefund(MFFGiftCardPaymentGroup pGiftCardPaymentGroup, String pGiftCardNumber) throws CommerceException {
    vlogDebug("Entering activateGiftCard : pGiftCardPaymentGroup, pGiftCardNumber, pEan");

    if (StringUtils.isBlank(pGiftCardNumber)) {
      throw new CommerceException("The gift card cannot be null for the payment group id" + pGiftCardPaymentGroup.getId());
    }

    pGiftCardPaymentGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.SETTLED));
    pGiftCardPaymentGroup.setCardNumber(pGiftCardNumber);

    vlogDebug("Exiting activateGiftCard : pGiftCardPaymentGroup, pGiftCardNumber, pEan");
  }

  /**
   * The following method creates the repository item
   * <code>pgGcSettlement</code> repository items and associates it with the
   * appropriate payment group. This records the gift card that is shipped in
   * response to an order containing a gift card.
   * 
   * @param pg
   *          the payment group used for settlement
   * @param gCNum
   *          the gift card number
   * @param settleAmount
   *          the amount that was settled against the payment group
   * @throws RepositoryException
   */
  @SuppressWarnings("unchecked")
  protected void createGiftCardPaymentSettlement(MutableRepositoryItem pg, String gCNum, double settleAmount) throws RepositoryException {
    vlogDebug("Entering createGiftCardPaymentSettlement : pg, gCNum, settleAmount");

    MutableRepository lOrderRep = (MutableRepository) getOrderManager().getOrderTools().getOrderRepository();
    MutableRepositoryItem pgSettleItem = lOrderRep.createItem(PG_GC_SETTLEMENT_ITEM_DESCRIPTOR);

    pgSettleItem.setPropertyValue(PG_GC_SETTLEMENT_PROP_GC_NUMBER, gCNum);
    pgSettleItem.setPropertyValue(PG_GC_SETTLEMENT_PROP_DATE, new Date());
    pgSettleItem.setPropertyValue(PG_GC_SETTLEMENT_SETTLEMENT_AMOUNT, settleAmount);

    lOrderRep.addItem(pgSettleItem);

    List<RepositoryItem> pgGcSettles = (List<RepositoryItem>) pg.getPropertyValue(PAYMENTGROUP_GC_SETTLEMENTS);

    if (pgGcSettles == null) {
      pgGcSettles = new ArrayList<RepositoryItem>();
    }

    vlogDebug("Adding the pgGCSettlement ID {0} to the payment group {1}", pgSettleItem.getRepositoryId(), pg.getRepositoryId());

    pgGcSettles.add(pgSettleItem);
    pg.setPropertyValue(PAYMENTGROUP_GC_SETTLEMENTS, pgGcSettles);

    vlogDebug("Exiting createGiftCardPaymentSettlement : pg, gCNum, settleAmount");
  }

  /**
   * The following method is used to determine the amount available for refund
   * using a gift card. This is the sum of all settlements performed against all
   * gift card payments. The order might contain multiple gift card payments but
   * refunds will be issued only one gift card at a time.
   * 
   * @param pOrder
   *          the associated order
   * @return the amount (positive value) available for refund
   */
  @SuppressWarnings("unchecked")
  public double calcAvailableGCFundsForRefund(Order pOrder) {
    vlogDebug("Entering calcAvailableGCFundsForRefund : pOrder");
    double lAvailableFunds = 0.0d;

    List<PaymentGroup> payGroups = pOrder.getPaymentGroups();
    for (PaymentGroup pg : payGroups) {
      if (pg instanceof MFFGiftCardPaymentGroup) {
        lAvailableFunds = lAvailableFunds + calcAmountAvailableForRefund((MFFGiftCardPaymentGroup) pg);
      }
    }
    vlogDebug("Exiting calcAvailableGCFundsForRefund : pOrder");
    return lAvailableFunds;
  }

  /**
   * The following method is used to determine the total funds available for
   * refund including credit card payments and gift card payments
   * 
   * @param pOrder
   *          the order for which the the available funds needs to be computed.
   * @return the amount available for credit or refund
   */
  @SuppressWarnings("unchecked")
  public double calcAvailableFundsForRefund(Order pOrder) {
    vlogDebug("Entering calcAvailableFundsForRefund : pOrder");

    double fundsAvailable = 0.0d;

    List<PaymentGroupImpl> payGroups = pOrder.getPaymentGroups();
    for (PaymentGroupImpl payGroup : payGroups) {
      if (!(payGroup instanceof MFFGiftCardPaymentGroup)) {
        fundsAvailable = fundsAvailable + calcAmountAvailableForRefund((CreditCard) payGroup);
      }

      fundsAvailable = fundsAvailable + calcAvailableGCFundsForRefund(pOrder);
    }
    vlogDebug("Funds available for settlement = {0}", fundsAvailable);

    vlogDebug("Exiting calcAvailableFundsForRefund : pOrder");
    return roundPrice(fundsAvailable);
  }

  protected boolean isGiftCardAvailableForAdditionalRefunds(MFFGiftCardPaymentGroup giftCard, boolean pReturnsProcess) {
    vlogDebug("Entering isGiftCardAvailableForAdditionalRefunds : giftCard");
    boolean retValue = false;
    if (pReturnsProcess) {
      if ((giftCard.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_FULFILLED)) || (giftCard.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.INITIAL))) {
        retValue = true;
      }
    } else {
      if ((giftCard.getState() == StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_FULFILLED))) {
        retValue = true;
      }
    }
    vlogDebug("Exiting isGiftCardAvailableForAdditionalRefunds : giftCard");
    return retValue;
  }

  /**
   * The following method is used to create a new gift card payment group and
   * associated it with the order. If a gift card exists that needs to be
   * activated then the existing gift card is returned by this method.
   * 
   * @param pOrder
   *          the order
   * @param pReturnsProcess
   *          if this method is called within the context of a returns process
   * @return the newly created gift card payment group or the existing gift card
   *         payment group that is yet to be activated
   * @throws CommerceException
   *           when an exception occurs while creating a gift card
   */
  @SuppressWarnings("unchecked")
  public MFFGiftCardPaymentGroup createGiftCardPaymentGroupForRefund(Order pOrder, boolean pReturnsProcess) throws CommerceException {
    vlogDebug("Entering createGiftCardPaymentGroupForRefund : pOrder");
    MFFGiftCardPaymentGroup refundPaymentGroup = null;
    List<PaymentGroup> paymentGroups = pOrder.getPaymentGroups();

    for (PaymentGroup paymentGroup : paymentGroups) {
      vlogDebug("Checking to see if the following payment group is a refund gift card {0}", paymentGroup.getId());
      if (paymentGroup instanceof MFFGiftCardPaymentGroup) {

        // it is a gift card. Now let us see if this is yet to be activated
        if (isGiftCardAvailableForAdditionalRefunds((MFFGiftCardPaymentGroup) paymentGroup, pReturnsProcess)) {
          refundPaymentGroup = (MFFGiftCardPaymentGroup) paymentGroup;
          break;
        }
      }
    }

    if (refundPaymentGroup != null) {
      vlogDebug("We found an existing payment group id = {0}", refundPaymentGroup.getId());
    } else {
      refundPaymentGroup = (MFFGiftCardPaymentGroup) getOrderManager().getPaymentGroupManager().createPaymentGroup(MFFOrderTools.GIFT_CARD_PAYMENT_TYPE);
      if (pReturnsProcess)
        refundPaymentGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.INITIAL));
      else
        refundPaymentGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_FULFILLED));
      refundPaymentGroup.setAmount(0.0d);
      refundPaymentGroup.setSubmittedDate(new Date());
      vlogDebug("Adding the following refund payment group {0} to order {1}", refundPaymentGroup.getId(), pOrder.getId());
      
      getOrderManager().getPaymentGroupManager().addPaymentGroupToOrder(pOrder, refundPaymentGroup);
      
    }

    vlogDebug("Exiting createGiftCardPaymentGroupForRefund : pOrder");
    return refundPaymentGroup;
  }

  /**
   * The following method is responsible for creating a new gift card and
   * associating it with the order. This gift card is in response to a credit
   * offered to a customer as a result of a return or cancellation.
   * 
   * We assume that the funds are available for credit. This check should be
   * performed prior to calling this method.
   * 
   * @param pOrder
   *          the order
   * @param pSettlementAmount
   *          the amount associated with the order
   * @throws CommerceException
   *           if a repository exception occurs
   */
  protected void createPaymentSettlementsForGiftCardRefund(Order pOrder, double pSettlementAmount) throws CommerceException {

    vlogDebug("Entering createPaymentSettlementsForGiftCardRefund : pOrder, pSettlementAmount");
    // first create the gift card payment group and associate with the order
    MFFGiftCardPaymentGroup lRefundPaymentGroup = createGiftCardPaymentGroupForRefund(pOrder, true);

    // create the payment settlements
    createPaymentSettlementsForGiftCardRefund(pOrder, pSettlementAmount, lRefundPaymentGroup);

    vlogDebug("Exiting createPaymentSettlementsForGiftCardRefund : pOrder, pSettlementAmount");

  }

  /**
   * The following method is responsible for creating payment settlements for a
   * given gift card payment group. The payment settlement is used to determine
   * the amount that is to refunded.
   * 
   * @param pOrder
   *          the order associated with the order
   * @param pSettlementAmount
   *          the settlement amount
   * @param pGiftCardPaymentGroup
   *          the gift card payment group that will be used as the refund
   * @throws CommerceException
   */
  public void createPaymentSettlementsForGiftCardRefund(Order pOrder, double pSettlementAmount, MFFGiftCardPaymentGroup pGiftCardPaymentGroup) throws CommerceException {
    vlogDebug("Entering createPaymentSettlementsForGiftCardRefund : pOrder, pSettlementAmount, pGiftCardPaymentGroup");

    if (!(pSettlementAmount > 0.0d)) {
      vlogError("Invalid settlement amount {0} for the order id {1}", pSettlementAmount, pOrder.getId());
      throw new CommerceException("Invalid settlement amount");
    }

    double settlementAmount = -1 * roundPrice(Math.abs(pSettlementAmount));

    try {
      createPaymentSettlement(pGiftCardPaymentGroup, settlementAmount, ((MFFOrderImpl) pOrder).getOrderNumber());
      pGiftCardPaymentGroup.setState(StateDefinitions.PAYMENTGROUPSTATES.getStateValue(MFFPaymentGroupStates.TO_BE_FULFILLED));
    } catch (RepositoryException e) {
      vlogError("An error occurred while creating a payment settlement for the order id {0}, for the amount {1}", pOrder.getId(), pSettlementAmount);
      throw new CommerceException("Error creating the gift card settlement", e);
    }

    vlogDebug("Exiting createPaymentSettlementsForGiftCardRefund : pOrder, pSettlementAmount, pGiftCardPaymentGroup");
  }

  /**
   * The following method is used to compute the amount available for debit
   * after considering all credits
   * 
   * @param pPaymentGroup
   *          the paymentgroup
   * @return the amount that is available for debit (a positive value)
   */
  @SuppressWarnings("unchecked")
  protected double calcAmountAvailableToDebit(PaymentGroup pPaymentGroup) {
    vlogDebug("Entering calcAmountAvailableToDebit : pg");

    double amtAuth = pPaymentGroup.getAmount();
    double debitAmt = 0;
    PaymentGroupImpl paymentGroupImpl;

    paymentGroupImpl = (PaymentGroupImpl) pPaymentGroup;
    List<RepositoryItem> pgSettlements = (List<RepositoryItem>) paymentGroupImpl.getPropertyValue(PAYMENTGROUP_PROP_SETTLEMENTS);

    for (RepositoryItem pgSettleItem : pgSettlements) {
      double amt = (Double) pgSettleItem.getPropertyValue(PG_SETTLEMENT_PROP_SETTLEMENT_AMOUNT);
      if (amt > 0) {
        debitAmt += amt;
      }
    }

    if (isLoggingDebug()) vlogDebug("Auth amount = {0}", amtAuth);
    if (isLoggingDebug()) vlogDebug("Debit Amount = {0}", debitAmt);

    double amtAvailable = amtAuth - debitAmt;

    amtAvailable = roundPrice(amtAvailable);

    if (isLoggingDebug()) vlogDebug("Amount available = {0}", amtAvailable);

    vlogDebug("Exiting calcAmountAvailableToDebit : pg");
    return amtAvailable;
  }

  /**
   * The following method creates the relationship between the payment group and
   * GC settlements performed against the payment.
   * 
   * @param paymentToSettlementAmountRel
   *          the relationship between the payment ids and the settlement amount
   *          generated by the method
   *          {@link #createDebitSettlements(Order, String, String)}
   * @param pCommerceItem
   *          the commerce item id for of the egift card
   * @param pGiftCardDetails
   *          the gift card details
   * @throws CommerceException
   *           when a repository exception occurs or the relation map does not
   *           contain data
   */
  protected void createGCDebitSettlements(HashMap<String, Double> paymentToSettlementAmountRel, String pCommerceItem, Map<String, List<String>> pGiftCardDetails) throws CommerceException {

    vlogDebug("Entering createGCDebitSettlements - pOrder, listOfPayments");
    if (paymentToSettlementAmountRel == null || !(paymentToSettlementAmountRel.size() > 0)) {
      vlogInfo("No payments were found for creating the GC settlements");
      return;

    }

    vlogDebug("Creating the GC settlements for commerce item id =  {0} ", pCommerceItem);
    try {
      for (Map.Entry<String, Double> entry : paymentToSettlementAmountRel.entrySet()) {

        String lPaymentID = entry.getKey();
        double lSettlementAmount = entry.getValue().doubleValue();

        lSettlementAmount = roundPrice(lSettlementAmount);
        vlogDebug("Creating gift card credit settlements for payment id {0}, settlement amount {1}", lPaymentID, lSettlementAmount);

        MutableRepositoryItem mutablePgItem = ((MutableRepository) getOrderManager().getOmsOrderRepository()).getItemForUpdate(lPaymentID, ITEM_DESCRIPTOR_PAYMENT_GROUP);
        List<String> gcDetails = (List<String>) pGiftCardDetails.get(pCommerceItem);
        String giftCardNo = gcDetails.get(0);
        String giftCardPin = gcDetails.get(1);
        vlogDebug("Gift card number {0} gift card pin {1}", giftCardNo, giftCardPin);
        createGiftCardPaymentSettlement(mutablePgItem, giftCardNo, lSettlementAmount);
      }

    } catch (RepositoryException e) {
      vlogError("Could not process the GC debit settlement for order id {0} commerce item id", pCommerceItem);
      vlogError(e, "Repository exception occurred while creating the GC payment settlement relationship");
      throw new CommerceException(e);
    }

    vlogDebug("Exiting createGCDebitSettlements - pOrder, listOfPayments");
  }

  
  
  /**
   * The following method updates the settlement map based on the settlement
   * record that is being created. The settlement map is mapping between the
   * payment group id and the amount that is being settled. This map is
   * primarily used to generate the Sales Audit
   * 
   * @param pSettlementMap
   *          the map containing the settlements. If set to null no operation is
   *          performed.
   * @param pPaymentGroupId
   *          the payment group id
   * @param pAmount
   *          the amount to be settled
   */
  protected void updateSettlementMap(HashMap<String, Double> pSettlementMap, String pPaymentGroupId, Double pAmount) {

    vlogDebug("Entering updateSettlementMap : pSettlementMap, pPaymentGroupId, pAmount");

    double currentAmount = 0.0d;
    double updatedAmount = 0.0d;

    if (pSettlementMap != null) {
      if (pSettlementMap.containsKey(pPaymentGroupId)) {
        Double lAmount = pSettlementMap.get(pPaymentGroupId);
        if (lAmount != null) {
          currentAmount = lAmount.doubleValue();
        }
      }
      updatedAmount = currentAmount + pAmount;

      updatedAmount = roundPrice(updatedAmount);
      pSettlementMap.put(pPaymentGroupId, new Double(updatedAmount));
      
      if (isLoggingDebug()) {
        StringBuilder sb = new StringBuilder();
        sb.append(MFFUtils.LINE_FEED);
        sb.append("******************************************************************");
        sb.append(MFFUtils.LINE_FEED);
        
        for (String key : pSettlementMap.keySet()) {
          sb.append("   Payment Group ID = " + key + " amount = " + pSettlementMap.get(key));
          sb.append(MFFUtils.LINE_FEED);
        }
        sb.append("******************************************************************");
        sb.append(MFFUtils.LINE_FEED);
        logDebug(sb.toString());
      }
    }

    vlogDebug("Exiting updateSettlementMap : pSettlementMap, pPaymentGroupId, pAmount");
  }

  
  private boolean isSettlementAmtEqualsAuthorizedAmt(double amountAuthorized, double pAmount)throws CommerceException{
	  vlogDebug("Entering validateSettlementForDebit");
	  vlogInfo("Authorized amount: "+amountAuthorized);
	  vlogInfo("Authorized tobe Settled: "+pAmount);
	  double differenceAmount=roundPrice(amountAuthorized)-roundPrice(pAmount);
	  vlogDebug("Exiting validateSettlementForDebit - pOrder, pPaymentGroup, pAmount");
	  if(differenceAmount>0){
		 return false;
	  }
	  else {
		  return true;
	  }	  
  }
  

  public SettlementFileGenerator getSettlementFileGenerator() {
    return settlementFileGenerator;
  }

  public void setSettlementFileGenerator(SettlementFileGenerator pSettlementFileGenerator) {
    settlementFileGenerator = pSettlementFileGenerator;
  }  
  /**
   * @return the settlementTypeStringMap
   */
  public Map<String, String> getSettlementTypeStringMap() {
    return settlementTypeStringMap;
  }

  /**
   * @param pSettlementTypeStringMap
   *          the settlementTypeStringMap to set
   */
  public void setSettlementTypeStringMap(Map<String, String> pSettlementTypeStringMap) {
    settlementTypeStringMap = pSettlementTypeStringMap;
  }

  public OMSOrderManager getOrderManager() {
    return orderManager;
  }

  public void setOrderManager(OMSOrderManager pOrderManager) {
    orderManager = pOrderManager;
  }

  public PGPEncryptionService getPGPEncryptionService() {
    return mPGPEncryptionService;
  }

  public void setPGPEncryptionService(PGPEncryptionService pPGPEncryptionService) {
    mPGPEncryptionService = pPGPEncryptionService;
  }



  public boolean isEnablePGPEncryption() {
    return enablePGPEncryption;
  }

  public void setEnablePGPEncryption(boolean pEnablePGPEncryption) {
    enablePGPEncryption = pEnablePGPEncryption;
  }

  public boolean isCheckForEmptySettlementFile() {
    return checkForEmptySettlementFile;
  }

  public void setCheckForEmptySettlementFile(boolean pCheckForEmptySettlementFile) {
    checkForEmptySettlementFile = pCheckForEmptySettlementFile;
  }



  public String getSettlementId() {
    return mSettlementId;
  }



  public void setSettlementId(String pSettlementId) {
    mSettlementId = pSettlementId;
  }

  public boolean isCheckSettlementStatus() {
    return checkSettlementStatus;
  }

  public void setCheckSettlementStatus(boolean pCheckSettlementStatus) {
    checkSettlementStatus = pCheckSettlementStatus;
  }

  public boolean isDeleteEmptyFiles() {
    return deleteEmptyFiles;
  }

  public void setDeleteEmptyFiles(boolean pDeleteEmptyFiles) {
    deleteEmptyFiles = pDeleteEmptyFiles;
  }

}
