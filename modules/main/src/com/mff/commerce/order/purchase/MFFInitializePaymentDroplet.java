package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.pricing.PricingTools;
import atg.commerce.profile.CommerceProfileTools;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import mff.MFFEnvironment;


public class MFFInitializePaymentDroplet extends DynamoServlet {

	private final static ParameterName NEWCARD = ParameterName.getParameterName("newCard");
	private final static ParameterName REPRICE = ParameterName.getParameterName("reprice");
	private final static ParameterName REPRICE_OPERATION = ParameterName.getParameterName("repriceOperation");

	private final static ParameterName SUCCESS = ParameterName.getParameterName("success");
	private final static ParameterName ERROR = ParameterName.getParameterName("error");
	
	private MFFPaymentGroupFormHandler mPaymentGroupFormHandler;
	private RepositoryItem mProfile;
	private MFFCheckoutManager mCheckoutManager;
	private CommerceProfileTools mProfileTools;
	OrderHolder mShoppingCart;
  private PaymentGroupManager mPaymentGroupManager;
  private MFFOrderManager mOrderManager;
  private TransactionManager mTransactionManager;
  private MFFEnvironment mMffEnvironment;

  @SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		try {
			
			getCheckoutManager().setCreditCardPaymentGroup(null);
				
			boolean creditCardPaymentGroupExists = checkPaymentGroupExists((MFFOrderImpl)getOrder(),"creditCard");
			
			String authorizedStep = getCheckoutManager().getCheckoutStepAuthorized().getName();
			
			vlogDebug("payment group exist:{0}, step:{1}",creditCardPaymentGroupExists,authorizedStep);

			if(isLoggingDebug()){
				List<PaymentGroupRelationship> relList = getOrder().getPaymentGroupRelationships();
				for(PaymentGroupRelationship rel :relList){
					if(rel instanceof PaymentGroupOrderRelationship){
						logDebug("Rel ID : " + rel.getId());
						logDebug("Rel PaymentGroup : " + rel.getPaymentGroup());
					}
				}
			}
			
			double pendingAmount = 0;
			try 
			{
				pendingAmount = getOrderRemainingAmount((MFFOrderImpl)getOrder());
			} 
			catch (Exception e) 
			{
				logError(e.getMessage());
			}		

			if((!creditCardPaymentGroupExists && pendingAmount > 0)){
				createCreditCardPaymentGroupAndFillIn((MFFOrderImpl)getOrder());
				getCheckoutManager().isOrderRequiresCreditCard();
				if(isLoggingDebug()) logDebug("Successfully set the checkoutmanager reference to null & created a new credit card group");
			}			

			MFFCheckoutManager cm = getCheckoutManager();

			String isNewCard = pRequest.getParameter(NEWCARD);
			if (Boolean.valueOf(isNewCard)) {
				cm.clearOutCreditCardData();
			}

			String repriceParam = pRequest.getParameter(REPRICE);
			if (Boolean.valueOf(repriceParam)) {

				String repriceOperationParam = pRequest.getParameter(REPRICE_OPERATION);
				if (repriceOperationParam == null) {
					repriceOperationParam = "ORDER_TOTAL";
				}

				boolean success = getCheckoutManager().repriceOrder(repriceOperationParam, getProfileTools().getUserLocale(pRequest, pResponse));
				if (!success) {
					pRequest.serviceParameter(ERROR, pRequest, pResponse);
					return;
				}
			}

		} catch (MFFOrderUpdateException exc) {
			if (isLoggingError()) {
				logError("An exception was caught in MFFInitializePaymentDroplet. Will service the ERROR param. Exc:" + exc);
			}
			pRequest.serviceParameter(ERROR, pRequest, pResponse);
			return;
		}

		getCheckoutManager().isOrderTotalCovered();
		
		if(isLoggingDebug()){
      List<PaymentGroupRelationship> relList = getOrder().getPaymentGroupRelationships();
      for(PaymentGroupRelationship rel :relList){
        if(rel instanceof PaymentGroupOrderRelationship){
          vlogDebug("PaymentGroup:{0} and Relation ID :{1} ",rel.getPaymentGroup(), rel.getId());
        }
      }
    }
		
		pRequest.serviceParameter(SUCCESS, pRequest, pResponse);
	}
	
	private boolean checkPaymentGroupExists(MFFOrderImpl order, String paymentType)
	{
		boolean paymentGroupExists = false;
		
		for (Object obj : order.getPaymentGroups()) 
		{
			if(obj != null)
			{
				PaymentGroup paymentGroup = (PaymentGroup) obj;
				if((paymentGroup.getPaymentMethod().equalsIgnoreCase(paymentType)))
				{
					paymentGroupExists = true;
				}
			}
		}
		
		return paymentGroupExists;
	} 
	
	private void createCreditCardPaymentGroupAndFillIn(MFFOrderImpl order) 
			throws ServletException
	{
		
		TransactionDemarcation td = new TransactionDemarcation();
        boolean rollback = false;
		
		try
		{
			td.begin(getTransactionManager(),TransactionDemarcation.REQUIRES_NEW);
			
			synchronized(order)
			{
				clearPaymentGroups(order);
				
				double amount = getOrderRemainingAmount(order);
				CreditCard newCC = (CreditCard) getPaymentGroupManager().createPaymentGroup("creditCard");
				newCC.setAmount(amount);
				getPaymentGroupManager().addPaymentGroupToOrder(order, newCC);
				getOrderManager().addRemainingOrderAmountToPaymentGroup(order, newCC.getId());
				((MFFOrderManager) getOrderManager()).updateOrder(order, "After creating a CC Payment Group and assigned amount");
				
				vlogDebug("Successfully added a new credit card with amount : " + amount);
			}
		}
		catch (MFFOrderUpdateException roe) 
		{
			logError("Errors encountered with creating CC payment group and remaining relationship:" + roe);
			rollback = true;
			throw new ServletException("An error was encountered while updating your order.  Please try again.", roe);
		}
		catch (Exception exc) 
		{
			logError("Errors encountered with creating CC payment group and remaining relationship:" + exc);
			rollback = true;
			throw new ServletException("An error was encountered while updating your order.  Please try again.", exc);
		}
		finally 
		{
			if(!rollback)
			{
				try 
				{
					getTransactionManager().getTransaction().commit();
				} 
				catch (Exception e) 
				{
					logError("Caught Exception while trying to commit transaction :" + e.getMessage());
					if(isLoggingDebug()) e.printStackTrace();
				}
			}
			try 
			{	
			   td.end(rollback);
			} 
			catch (TransactionDemarcationException e) 
			{
				logError("Caught Exception while trying end transaction via Transaction Demarcation :" + e.getMessage());
				if(isLoggingDebug()) e.printStackTrace();
			}
		}
	}
	
	private void clearPaymentGroups(Order order) throws ServletException
	{
		vlogDebug("Inside clearPaymentGroups");
		try
		{
			for (Object obj : order.getPaymentGroups()) 
			{
				PaymentGroup paymentGroup = (PaymentGroup) obj;
			
				if (paymentGroup.getPaymentMethod().equalsIgnoreCase("creditCard")) 
				{
					getPaymentGroupManager().removePaymentGroupFromOrder(order, paymentGroup.getId());
					vlogDebug("Removed the following payment group from the order : {0}",paymentGroup.getId());
					break;
				}
			}
		}
		catch (CommerceException e) 
		{
			throw new ServletException(e);
		}
	} 

	private double getOrderRemainingAmount(Order order) throws Exception
	{
		PricingTools pricingTools = getOrderManager().getOrderTools().getProfileTools().getPricingTools();
	    double orderTotal = order.getPriceInfo().getTotal();
	    double totalGiftCardAmount = 0;
	    double orderRemainingAmount;
	    
	    List<MFFGiftCardPaymentGroup> giftCards = getCheckoutManager().getGiftCardPaymentGroups();
		if (giftCards != null) {
			for (MFFGiftCardPaymentGroup giftCard : giftCards) {
				totalGiftCardAmount += giftCard.getAmount();
			}
		}

		orderRemainingAmount = pricingTools.round(orderTotal - totalGiftCardAmount);
		vlogDebug("Remaining Order Amount not covered by GC (if any) is:{0}",orderRemainingAmount);

		return orderRemainingAmount;
		
	}
	
	public CommerceProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(CommerceProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	public Order getOrder() {
		return getShoppingCart().getCurrent();
	}

	public void setShoppingCart(OrderHolder pShoppingCart) {
		mShoppingCart = pShoppingCart;
	}

	public OrderHolder getShoppingCart() {
		return mShoppingCart;
	}

	public RepositoryItem getProfile() {
		return mProfile;
	}

	public void setProfile(RepositoryItem pProfile) {
		mProfile = pProfile;
	}

  public MFFPaymentGroupFormHandler getPaymentGroupFormHandler() {
    return mPaymentGroupFormHandler;
  }

  public void setPaymentGroupFormHandler(MFFPaymentGroupFormHandler pPaymentGroupFormHandler) {
    mPaymentGroupFormHandler = pPaymentGroupFormHandler;
  }

  public MFFOrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(MFFOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

  
  public MFFEnvironment getMffEnvironment() {
    return mMffEnvironment;
  }

  public void setMffEnvironment(MFFEnvironment pMffEnvironment) {
    mMffEnvironment = pMffEnvironment;
  }

}
