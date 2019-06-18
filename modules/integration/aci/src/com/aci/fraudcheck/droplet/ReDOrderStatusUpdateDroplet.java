package com.aci.fraudcheck.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.aci.commerce.order.AciOrder;
import com.aci.configuration.AciConfiguration;
import com.aci.payment.creditcard.AciCreditCard;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.StateDefinitions;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * 
 * @author DMI
 *
 */
public class ReDOrderStatusUpdateDroplet extends DynamoServlet {

	private static final String ORDER_ID = "orderId";
	private static final String RED_FRAUD_STATUS = "status";
	private static final String RED_AGENT_ID = "userId";
	private static final String PARAMETER_MESSAGE = "message";
	private static final String OPEN_PARAMETER_EMPTY = "empty";
	private static final String OPEN_PARAMETER_ERROR = "error";
	private static final String COMPONENT_PATH_PROFILE = "/atg/userprofiling/Profile";

	  
	private ArrayList<String> mRedStatusesForUpdate;
	
	private OrderManager mOrderManager;
	private ClientLockManager mLocalLockManager;
	private TransactionManager mTransactionManager;
	private String mOrderIdParameterName;
	private String mStatusParameterName;
	private String mAgentIdParameterName;
	private String mCancelReasonParameterName;
	private String mSubclientIdParameterName;
	private String mNotesParameterName;
	private AciConfiguration mAciConfiguration;
	private String mFraudDenyState;
	private String mFraudAcceptState;
	private String mFraudNoScoreState;
	private String mSystemHoldState;
	private InventoryManager mInventoryManager;
	private TemplateEmailSender mTemplateEmailSender;
	private TemplateEmailInfoImpl mFraudEmailTemplate;
	private boolean mSendEmailInSeparateThread;
  private boolean mPersistEmails;
  
	public InventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	public void setInventoryManager(InventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		try {
			
			vlogDebug("ReDOrderStatusUpdateDroplet:service(): Request method is:" +pRequest.getMethod() + " , Request url : " + pRequest.getRequestURI());
			
			if(getAciConfiguration().isEnableTieBack()){
				String orderIdParameterName=getOrderIdParameterName();
				String statusParameterName=getStatusParameterName();
				String agentIdParameterName=getAgentIdParameterName();
				
				if(StringUtils.isEmpty(orderIdParameterName)){
					orderIdParameterName=ORDER_ID;
				}
				
				if(StringUtils.isEmpty(statusParameterName)){
					statusParameterName=RED_FRAUD_STATUS;
				}
				
				if(StringUtils.isEmpty(agentIdParameterName)){
					agentIdParameterName=RED_AGENT_ID;
				}
				
				String pOrderId = pRequest.getPostParameter(orderIdParameterName);
				String pReDFraudStatus = pRequest.getPostParameter(statusParameterName);
				String pAgentId=pRequest.getPostParameter(agentIdParameterName);
				
				vlogDebug("ReDOrderStatusUpdateDroplet:service(): post param - pOrderId : " +pOrderId + " , pReDFruadStatus : " +pReDFraudStatus+" , pAgentId: "+pAgentId);
				
				if (StringUtils.isBlank(pOrderId) && StringUtils.isBlank(pReDFraudStatus)) {
					
					vlogDebug("ReDOrderStatusUpdateDroplet:post param values for orderId or status was null, hence looking at request params.");
	
						pOrderId=pRequest.getParameter(orderIdParameterName);
						pReDFraudStatus=pRequest.getParameter(statusParameterName);
						pAgentId=pRequest.getParameter(agentIdParameterName);
				}
				
				vlogDebug("ReDOrderStatusUpdateDroplet:service(): Order id: "+pOrderId+" , with Status: "+pReDFraudStatus+" , pAgentId: "+pAgentId);
				
				if (!StringUtils.isBlank(pOrderId) && !StringUtils.isBlank(pReDFraudStatus)) {
						
						pOrderId=pOrderId.trim().toLowerCase();
						pReDFraudStatus=pReDFraudStatus.trim().toUpperCase();
						boolean eligibleForUpdate= false;
						
						for(String eligibleStatus : getRedStatusesForUpdate()) {
						    if(pReDFraudStatus.equalsIgnoreCase(eligibleStatus)) {
						    	eligibleForUpdate = true; 
						    	break;
						    }
						}
						if (eligibleForUpdate){
							updateOrder(pOrderId, pReDFraudStatus, pAgentId, pRequest, pResponse);
						} else {
							vlogDebug("ReDOrderStatusUpdateDroplet:service(): status received from ReD : "+pReDFraudStatus + " is not eligible for update.");
						}
					
				}else{
					
					pRequest.setParameter(PARAMETER_MESSAGE, "Either OrderId or OrderStatus is Empty.");
					pRequest.serviceLocalParameter(OPEN_PARAMETER_EMPTY, pRequest, pResponse);
					pResponse.setStatus(421);
					vlogError("ReDOrderStatusUpdateDroplet:Either OrderId or ReDFraudStatus is Empty");
				}
			}
			vlogDebug("ReDOrderStatusUpdateDroplet - service() :: Exited");
			
		}
		catch (IOException e){
			vlogError(e,"ReDOrderStatusUpdateDroplet:Exception while processing red fraud status: ");
		}
	}
	
	@SuppressWarnings("rawtypes")
  private boolean updateOrder(String pOrderId, String pReDFruadStatus, String pAgentId,DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,IOException {
		
		TransactionDemarcation td = null;
		boolean rollback = false;
		boolean acquireLock = false;
		Profile profile = (Profile)pRequest.resolveName(COMPONENT_PATH_PROFILE);
		
		try {
			
			acquireLock = !getLocalLockManager().hasWriteLock(profile.getRepositoryId(), Thread.currentThread());
			
			if (acquireLock) {
				getLocalLockManager().acquireWriteLock(profile.getRepositoryId(), Thread.currentThread());
			}
			
			td = new TransactionDemarcation();
			td.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
			
			AciOrder pOrder = (AciOrder)getOrderManager().loadOrder(pOrderId);
			
			vlogDebug ("ReDOrderStatusUpdateDroplet:updateOrderStatus: pReDFruadStatus: " + pReDFruadStatus);
			
			String fraudStatusToBeSet = getAciConfiguration().getFraudStatCdMap().get(pReDFruadStatus.trim().toUpperCase()) ;
			vlogDebug ("ReDOrderStatusUpdateDroplet: FraudStatusToBeSet: " + fraudStatusToBeSet);
			
			String orderStateToBeSet=getAciConfiguration().getFraudStateToOrderStateMap().get(fraudStatusToBeSet);
			vlogDebug ("ReDOrderStatusUpdateDroplet: OrderStateToBeSet: " + orderStateToBeSet);
			
			synchronized (pOrder) {
				
				if(!StringUtils.isEmpty(orderStateToBeSet) && orderStateToBeSet.equalsIgnoreCase(getFraudDenyState())){
					 
		       List ciList = pOrder.getCommerceItems();
		       ListIterator ciIterator = ciList.listIterator();
		       while (ciIterator.hasNext()) {
		        CommerceItem ci = (CommerceItem) ciIterator.next();
		        String skuId = ci.getCatalogRefId();
		        long qty = ci.getQuantity();
		        try {
		        	if(!(Boolean)pOrder.getRepositoryItem().getPropertyValue("bopisOrder")) {
		        		getInventoryManager().increaseStockLevel(skuId, qty);
		        	} else {
		        		//((FFRepositoryInventoryManager)getInventoryManager()).fraudRejectStoreAllocated(skuId, (String)pOrder.getRepositoryItem().getPropertyValue("bopisStore"), qty);
		        		getInventoryManager().increaseStockLevel("FRAUD-"+ (String)pOrder.getRepositoryItem().getPropertyValue("bopisStore") + "-" + skuId, qty);
		        	}
		          
		         }catch (InventoryException e) {
		          vlogError(e,"ReDOrderStatusUpdateDroplet: Error updatig inventory for order {0} Sku {0}",pOrder.getId(),skuId);
		        }
		       }
				}
				pOrder.setFraudStat(fraudStatusToBeSet);
				if(!StringUtils.isEmpty(pAgentId)){
					pOrder.setTiebackAgentId(pAgentId);
				}
				if(!pOrder.getStateAsString().equalsIgnoreCase(getSystemHoldState())){
					pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(orderStateToBeSet));
	    	}
				getOrderManager().updateOrder(pOrder);
				if(orderStateToBeSet.equalsIgnoreCase(getFraudDenyState())){
				  // send email to customer
          sendFraudEmail(pOrder);
				}
				
			}
		} catch (TransactionDemarcationException e) {
			pRequest.setParameter(PARAMETER_MESSAGE, "A Transaction Demarcation Exception occurred while updating Order Status");
			pRequest.serviceLocalParameter(OPEN_PARAMETER_ERROR, pRequest, pResponse);
			pResponse.setStatus(422);
			rollback=true;
			vlogError(e,"ReDOrderStatusUpdateDroplet:TransactionDemarcationException");
		} catch (CommerceException e) {
			pRequest.setParameter(PARAMETER_MESSAGE, "A Commerce exception occurred while updating Order Status");
			pRequest.serviceLocalParameter(OPEN_PARAMETER_ERROR, pRequest, pResponse);
			pResponse.setStatus(422);
			rollback=true;
			vlogError(e,"ReDOrderStatusUpdateDroplet:CommerceException ");
		} catch (Exception e) {
			pResponse.setStatus(422);
			rollback=true;
			vlogError(e,"ReDOrderStatusUpdateDroplet:Exception");
			
		} finally {
			if(td != null){
				try {
					td.end(rollback);
				} catch (TransactionDemarcationException e) {
					vlogError(e,"ReDOrderStatusUpdateDroplet:TransactionDemarcationException");
					pResponse.setStatus(422);
				}
			}
			if (acquireLock) {
				try {
					getLocalLockManager().releaseWriteLock(profile.getRepositoryId(), Thread.currentThread(), true);
				} catch (LockManagerException e) {
					vlogError(e,"ReDOrderStatusUpdateDroplet:Exception:exception while releasing write lock: ");
					pResponse.setStatus(422);
				}
			}
		}
		vlogDebug("ReDOrderStatusUpdateDroplet:updateOrderStatus: rollback: " +rollback );
		return rollback;
	}
	
	@SuppressWarnings("rawtypes")
  private void sendFraudEmail(OrderImpl pOrder){

    Map<String, Object> templateParameters = new HashMap<String, Object>();
    templateParameters.put("order", pOrder);
    
    TemplateEmailInfoImpl template = getFraudEmailTemplate();
    RepositoryItem order = pOrder.getRepositoryItem();
    if(order != null){
      String emailAddress = (String)order.getPropertyValue("contactEmail");
      String orderNumber = (String)order.getPropertyValue("orderNumber");
      
      List purchaserNameFields = new ArrayList();
      fillPurchaserNameFields(pOrder, purchaserNameFields);
      templateParameters.put("purchaserFirstName", purchaserNameFields.get(0));
      templateParameters.put("purchaserLastName", purchaserNameFields.get(1));
      
      if (null != template) {
        
        template.setMessageSubject(getMessageSubject(orderNumber));
        
        TemplateEmailInfoImpl templateEmail = new TemplateEmailInfoImpl();
        template.copyPropertiesTo(templateEmail);
        templateEmail.setTemplateParameters(templateParameters);
       
          Object[] recepients = { emailAddress };
          try {
            getTemplateEmailSender().sendEmailMessage(templateEmail, recepients, isSendEmailInSeparateThread(), isPersistEmails());
          } catch (TemplateEmailException e) {
            vlogError(e, "TemplateEmailException occurred while sending fraud email : " + e.getMessage());
          }
      } else {
        vlogDebug("Template Email Info is null, please check components configuration.");
      }
    }
	}
	
	private String getMessageSubject(String pOrderNumber){
	  
	  StringBuffer sb = new StringBuffer();
	  sb.append("Order #");
	  sb.append(pOrderNumber);
	  sb.append(":");
	  sb.append(" Your Order Has Been Cancelled");
	 
	  return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
  private void fillPurchaserNameFields (OrderImpl pOrder, List pPurchaserName) {
    
    String firstName = null;

    // 2505 - Display card holder name in all emails
    if(pOrder != null) {
      List paymentGroups = pOrder.getPaymentGroups();

      PaymentGroup pg = null;
      String nameOnCard=null;
      for (int i = 0; i < paymentGroups.size(); i++) {
        pg = (PaymentGroup) paymentGroups.get(i);
        if (pg instanceof CreditCard) {
          vlogDebug("fillPurchaserNameFields(): Found credit card pg.");
          nameOnCard=((AciCreditCard) pg).getNameOnCard();
          if (StringUtils.isNotBlank(nameOnCard)) {
            pPurchaserName.add(0, nameOnCard);
            pPurchaserName.add(1, "");
            return;
          }
        }
      }     
    }
    
    /*if (pProfile != null) {
      firstName = (String) pProfile.getPropertyValue("firstName");
      if (StringUtils.isNotBlank(firstName)) {
        vlogDebug("fillPurchaserNameFields(): Using name from profile.");
        pPurchaserName.add(0, firstName);
        pPurchaserName.add(1, (String) pProfile.getPropertyValue("lastName"));
        return;
      }
    }*/

    List paymentGroups = pOrder.getPaymentGroups();

    PaymentGroup pg = null;
    for (int i = 0; i < paymentGroups.size(); i++) {
      pg = (PaymentGroup) paymentGroups.get(i);
      if (pg instanceof CreditCard) {
        vlogDebug("fillPurchaserNameFields(): Found credit card pg.");
        Address billingAddr = ((CreditCard) pg).getBillingAddress();
        if (billingAddr!=null){
          firstName = billingAddr.getFirstName();
          if (StringUtils.isNotBlank(firstName)) {
            vlogDebug("fillPurchaserNameFields(): Using name from billing address.");
            pPurchaserName.add(0, firstName);
            pPurchaserName.add(1, billingAddr.getLastName());
            return;
          }
        }
      }
    }
    
    Boolean bopisOrder = (Boolean)pOrder.getPropertyValue("bopisOrder");
    //This case occurs for GC Only Guest BOPIS order.
    if (bopisOrder) {
      vlogDebug("fillPurchaserNameFields(): GC Only payment BOPIS Guest order, Hence using Bopis Pickup person name.");
      String bopisPerson = (String)pOrder.getPropertyValue("bopisPerson");
      pPurchaserName.add(0, bopisPerson);
      pPurchaserName.add(1, "");
      return;
    }
    
    List shippingGroups = pOrder.getShippingGroups();
    for (Object shippingGroup : shippingGroups) {
      if(shippingGroup instanceof HardgoodShippingGroup){
        Address shippingAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
        if (shippingAddress!=null){
          firstName = shippingAddress.getFirstName();
          if (StringUtils.isNotBlank(firstName)) {
            vlogDebug("fillPurchaserNameFields(): Using name from shipping address.");
            pPurchaserName.add(0, firstName);
            pPurchaserName.add(1, shippingAddress.getLastName());
            return;
          }
        }
      }
    }
  }

	  public OrderManager getOrderManager() {
		return mOrderManager;
	}

	public void setOrderManager(OrderManager mOrderManager) {
		this.mOrderManager = mOrderManager;
	}

	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public void setTransactionManager(TransactionManager mTransactionManager) {
		this.mTransactionManager = mTransactionManager;
	}
	
	public ArrayList <String> getRedStatusesForUpdate() {
		return mRedStatusesForUpdate;
	}

	public void setRedStatusesForUpdate(ArrayList <String> pRedStatusesForUpdate) {
		this.mRedStatusesForUpdate = pRedStatusesForUpdate;
	}

	public void setLocalLockManager(ClientLockManager pLocalLockManager) {
	    mLocalLockManager = pLocalLockManager;
	}

	public ClientLockManager getLocalLockManager() {
	    return mLocalLockManager;
	}

	public String getOrderIdParameterName() {
		return mOrderIdParameterName;
	}

	public void setOrderIdParameterName(String pOrderIdParameterName) {
		mOrderIdParameterName = pOrderIdParameterName;
	}

	public String getStatusParameterName() {
		return mStatusParameterName;
	}

	public void setStatusParameterName(String pStatusParameterName) {
		mStatusParameterName = pStatusParameterName;
	}

	public String getAgentIdParameterName() {
		return mAgentIdParameterName;
	}

	public void setAgentIdParameterName(String pAgentIdParameterName) {
		mAgentIdParameterName = pAgentIdParameterName;
	}

	public String getCancelReasonParameterName() {
		return mCancelReasonParameterName;
	}

	public void setCancelReasonParameterName(String pCancelReasonParameterName) {
		mCancelReasonParameterName = pCancelReasonParameterName;
	}

	public String getSubclientIdParameterName() {
		return mSubclientIdParameterName;
	}

	public void setSubclientIdParameterName(String pSubclientIdParameterName) {
		mSubclientIdParameterName = pSubclientIdParameterName;
	}

	public String getNotesParameterName() {
		return mNotesParameterName;
	}

	public void setNotesParameterName(String pNotesParameterName) {
		mNotesParameterName = pNotesParameterName;
	}

	public AciConfiguration getAciConfiguration() {
		return mAciConfiguration;
	}

	public void setAciConfiguration(AciConfiguration pAciConfiguration) {
		mAciConfiguration = pAciConfiguration;
	}

	public String getFraudDenyState() {
		return mFraudDenyState;
	}

	public void setFraudDenyState(String pFraudDenyState) {
		mFraudDenyState = pFraudDenyState;
	}

	public String getFraudAcceptState() {
		return mFraudAcceptState;
	}

	public void setFraudAcceptState(String pFraudAcceptState) {
		mFraudAcceptState = pFraudAcceptState;
	}

	public String getFraudNoScoreState() {
		return mFraudNoScoreState;
	}

	public void setFraudNoScoreState(String pFraudNoScoreState) {
		mFraudNoScoreState = pFraudNoScoreState;
	}

	public String getSystemHoldState() {
		return mSystemHoldState;
	}

	public void setSystemHoldState(String pSystemHoldState) {
		mSystemHoldState = pSystemHoldState;
	}

  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }

  public TemplateEmailInfoImpl getFraudEmailTemplate() {
    return mFraudEmailTemplate;
  }

  public void setFraudEmailTemplate(TemplateEmailInfoImpl pFraudEmailTemplate) {
    mFraudEmailTemplate = pFraudEmailTemplate;
  }

  public boolean isSendEmailInSeparateThread() {
    return mSendEmailInSeparateThread;
  }

  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  public boolean isPersistEmails() {
    return mPersistEmails;
  }

  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }
  
}