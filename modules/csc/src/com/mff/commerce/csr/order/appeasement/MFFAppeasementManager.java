package com.mff.commerce.csr.order.appeasement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oms.commerce.order.OMSOrderManager;
import oms.commerce.settlement.SettlementManager;
import atg.commerce.CommerceException;
import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.order.appeasement.AppeasementManager;
import atg.commerce.csr.order.appeasement.AppeasementUserMessage;
import atg.commerce.csr.returns.ReturnException;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.CreditCardRefund;
import atg.commerce.order.Order;
import atg.commerce.order.Refund;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ResourceUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.Profile;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFGiftCardRefundMethod;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.constants.MFFConstants;

/**
 *
 */
public class MFFAppeasementManager extends AppeasementManager {
  
  
  private SettlementManager settlementManager;
  
  public static final String APPEASEMENT_ITEM_TYPE = "items";
  public static final String APPEASEMENT_SHIPPING_TYPE = "shipping";
  public static final String APPEASEMENT_TAXES_TYPE = "taxes";
  
  private OMSOrderManager orderManager;
  
  @SuppressWarnings("rawtypes")
  public double calculateTaxesRefundAdjustedTotal(String pOrderId, String pType) throws CommerceException, RepositoryException {
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateTaxesRefundAdjustedTotal for order: ").append(pOrderId).toString());
    Order order = getCSRAgentTools().getOrderManager().loadOrder(pOrderId);
    double refundAdjustedTaxTotal = 0.0D;
    double taxAppeasementsTotal = 0.0D;
    double returnsTaxRefundAmountTotal = 0.0D;
    List returnRequests = null;
    try {
      returnRequests = getReturnTools().getReturnRequestsByOrderId(pOrderId);
      if (returnRequests != null) {
        Iterator iterator = returnRequests.iterator();
        do {
          if (!iterator.hasNext()) break;
          RepositoryItem returnRequestItem = (RepositoryItem) iterator.next();
          ReturnRequest returnRequest = getReturnTools().loadReturnRequest(returnRequestItem);
          double taxRefund = returnRequest.getActualTaxRefund();
          returnsTaxRefundAmountTotal += taxRefund;
        } while (true);
      }
    } catch (ReturnException e) {
      if (isLoggingDebug()) logDebug((new StringBuilder()).append("Failed to load return request for order : ").append(order.getId()).toString());
      String msg = ResourceUtils.getMsgResource("unableToLoadReturnRequests", AppeasementUserMessage.RESOURCE_BUNDLE, sResourceBundle);
      throw new CommerceException(msg, e);
    }
    List appeasements = null;
    appeasements = getCSRAppeasementTools().getAppeasementsByOrderId(pOrderId);
    if (appeasements != null) {
      Iterator iterator = appeasements.iterator();
      do {
        if (!iterator.hasNext()) break;
        RepositoryItem appeasementItem = (RepositoryItem) iterator.next();
        Appeasement appeasement = getCSRAppeasementTools().loadAppeasement(appeasementItem);
        if (appeasement.getAppeasementType().equalsIgnoreCase("taxes") && appeasement.getAppeasementState().equalsIgnoreCase("complete")) {
          double allocatedShippingRefund = appeasement.getAppeasementAmount();
          taxAppeasementsTotal += allocatedShippingRefund;
        }
      } while (true);
    }
    refundAdjustedTaxTotal = getTotalShippedShippingTaxesAmount(order, false) - returnsTaxRefundAmountTotal - taxAppeasementsTotal;
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("Calculated refund adjusted tax total: ").append(refundAdjustedTaxTotal).toString());
    return getPricingTools().round(refundAdjustedTaxTotal, getCSRAgentTools().getCurrencyCodeForCurrentOrder());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Appeasement initiateAppeasement(Order pOrder, Map pExtraParameters) throws CommerceException {

    vlogDebug("Entering initiateAppeasement : pOrder, pExtraParameters");

    // Out of the box initiate appeasement method creates a store credit
    // MFF implementation will create a new gift card payment group instead
    // if there are funds available to be refunded
    Profile customerProfile = new Profile();
    Appeasement appeasement = new Appeasement();
    appeasement.setOriginatingOrder(pOrder);
    appeasement.setOrderId(pOrder.getId());
    appeasement.setOriginOfAppeasement(getOriginOfAppeasement());

    if (getCSRAgentTools().getOrderManager().getOrderTools().getProfileTools().locateUserFromId(pOrder.getProfileId(), customerProfile)) {
      appeasement.setProfile(customerProfile);
    }

    Profile agentProfile = getCSRAgentTools().getCSREnvironmentTools().getEnvironmentTools().getAgentProfile();

    if (agentProfile != null) {
      appeasement.setAgentId(agentProfile.getRepositoryId());
    }
    List refunds = ((MFFCSRAppeasementTools) getCSRAppeasementTools()).generateRefundMethodsFromOrder(pOrder);
    appeasement.setRefundList(refunds);

    getCSRAgentTools().getCSREnvironmentTools().getOrderHolder().setAppeasement(appeasement);

    vlogDebug("Exiting initiateAppeasement : pOrder, pExtraParameters");
    return appeasement;
     
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void allocateAmountsToRefundsList(Appeasement pAppeasement) throws CommerceException {
    vlogDebug("Entering allocateAmountsToRefundsList : pAppeasement");

    // the following method is overriden to handle the gift card payment group
    // instead of the store credit. The OOTB implementation creates the store
    // credit which we don't need.

    double appeasementAmount = pAppeasement.getAppeasementAmount();
    vlogDebug("The total appeasement amount is set to {0}", appeasementAmount);

    if (appeasementAmount > 0.0d) {
      List refundList = pAppeasement.getRefundList();
      refundList = getCSRAppeasementTools().sortRefundList(refundList);

      double allocatedAmount = allocateRefundAmountForAppeasement(pAppeasement.getOriginatingOrder(), pAppeasement, appeasementAmount, refundList);

      if (allocatedAmount < appeasementAmount) {
        throw new CommerceException("The appeasement amount is more than the available funds for appeasements. Available funds = " + allocatedAmount);
      }
    }
    vlogDebug("Exiting allocateAmountsToRefundsList : pAppeasement");
  }

  @Override
  public boolean settleAppeasement(Appeasement pAppeasement) throws CommerceException {
    vlogDebug("Entering settleAppeasement : pAppeasement");

    List<Refund> refundList = pAppeasement.getRefundList();
    MFFOrderImpl lOrder = (MFFOrderImpl) pAppeasement.getOriginatingOrder();
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId = lOrder.getId();

    // We loop through the refund methods and create the settlement record &
    // payment settlement record for Credit Card
    // For Gift card we only create the payment settlement.
    for (Refund lRefund : refundList) {

      double lRefundAmount = lRefund.getAmount();
      vlogDebug("Processing the refund type of {0} amount = {1} order number = {2}", lRefund, lRefundAmount, lOrderNumber);

      if (lRefund.getRefundType().equals("creditCard")) {
        CreditCardRefund lCreditCardRefund = (CreditCardRefund) lRefund;
        CreditCard lCreditCard = lCreditCardRefund.getCreditCard();
        try {
          getSettlementManager().createCCCreditSettlements(lCreditCard, lOrderNumber, lOrderId, lRefundAmount, null);
        } catch (RepositoryException e) {
          vlogError(e, "Repository exception occurred while creating credit card settlements for the following order {0} for the amount {1}", lOrderNumber, lRefundAmount);
          throw new CommerceException(e);
        }
      } else if (lRefund.getRefundType().equals("giftCard") && lRefundAmount > 0) {
        // Create the payment settlement record for the gift card
        MFFGiftCardRefundMethod lGiftCardRefundMethod = ((MFFGiftCardRefundMethod) lRefund);

        getSettlementManager().createPaymentSettlementsForGiftCardRefund(lOrder, lRefundAmount, lGiftCardRefundMethod.getPaymentGroup());
        // Bug#2624 - commenting as update order is called ProcSettleAppeasement
        //getOrderManager().updateOrder(lOrder);

      }
    }

    vlogDebug("Exiting settleAppeasement : pAppeasement");
    return true;
  }

  @Override
  public double calculateRefundAdjustedTotal(String pOrderId, String pType)
      throws CommerceException, RepositoryException
  {
    if (pType.equalsIgnoreCase(APPEASEMENT_TAXES_TYPE)) {
      return calculateTaxesRefundAdjustedTotal(pOrderId, pType);
    }
   
      if(isLoggingDebug())
          logDebug((new StringBuilder()).append("calculateRefundAdjustedTotal for order: ").append(pOrderId).toString());
      Order order = getCSRAgentTools().getOrderManager().loadOrder(pOrderId);
      double refundAdjustedOrderTotal = 0.0D;
      double refundAdjustedShippingTotal = 0.0D;
      double returnsRefundAmountTotal = 0.0D;
      double orderAppeasementsTotal = 0.0D;
      double shippingAppeasementsTotal = 0.0D;
      double returnsShippingRefundAmountTotal = 0.0D;
      List returnRequests = null;
      try
      {
          returnRequests = getReturnTools().getReturnRequestsByOrderId(pOrderId);
          if(returnRequests != null)
          {
              Iterator iterator = returnRequests.iterator();
              do
              {
                  if(!iterator.hasNext())
                      break;
                  RepositoryItem returnRequestItem = (RepositoryItem)iterator.next();
                  ReturnRequest returnRequest = getReturnTools().loadReturnRequest(returnRequestItem);
                  if(pType.equalsIgnoreCase("items"))
                  {
                      double allocatedOrderRefund = returnRequest.getAllocatedAmount() - returnRequest.getActualShippingRefund() - returnRequest.getActualTaxRefund();
                      returnsRefundAmountTotal += allocatedOrderRefund;
                  }
                  if(pType.equalsIgnoreCase("shipping"))
                  {
                      double shippingRefund = returnRequest.getActualShippingRefund();
                      returnsShippingRefundAmountTotal += shippingRefund;
                  }
              } while(true);
          }
      }
      catch(ReturnException e)
      {
          if(isLoggingDebug())
              logDebug((new StringBuilder()).append("Failed to load return request for order : ").append(order.getId()).toString());
          String msg = ResourceUtils.getMsgResource("unableToLoadReturnRequests", AppeasementUserMessage.RESOURCE_BUNDLE, sResourceBundle);
          throw new CommerceException(msg, e);
      }
      List appeasements = null;
      appeasements = getCSRAppeasementTools().getAppeasementsByOrderId(pOrderId);
      if(appeasements != null)
      {
          Iterator iterator = appeasements.iterator();
          do
          {
              if(!iterator.hasNext())
                  break;
              RepositoryItem appeasementItem = (RepositoryItem)iterator.next();
              Appeasement appeasement = getCSRAppeasementTools().loadAppeasement(appeasementItem);
              if((appeasement.getAppeasementType().equalsIgnoreCase("items") || appeasement.getAppeasementType().equalsIgnoreCase("appreciation")) && appeasement.getAppeasementState().equalsIgnoreCase("complete"))
              {
                  double allocatedOrderRefund = appeasement.getAppeasementAmount();
                  orderAppeasementsTotal += allocatedOrderRefund;
              }
              if(appeasement.getAppeasementType().equalsIgnoreCase("shipping") && appeasement.getAppeasementState().equalsIgnoreCase("complete"))
              {
                  double allocatedShippingRefund = appeasement.getAppeasementAmount();
                  shippingAppeasementsTotal += allocatedShippingRefund;
              }
          } while(true);
      }
      refundAdjustedOrderTotal = getTotalShippedItemsAmount(order) - returnsRefundAmountTotal - orderAppeasementsTotal;
      refundAdjustedShippingTotal = getTotalShippedShippingTaxesAmount(order, true) - returnsShippingRefundAmountTotal - shippingAppeasementsTotal;
      if(isLoggingDebug())
          logDebug((new StringBuilder()).append("Calculated refund adjusted order total: ").append(refundAdjustedOrderTotal).toString());
      if(pType.equalsIgnoreCase("items") || pType.equalsIgnoreCase("appreciation"))
          return getPricingTools().round(refundAdjustedOrderTotal, getCSRAgentTools().getCurrencyCodeForCurrentOrder());
      else
          return getPricingTools().round(refundAdjustedShippingTotal, getCSRAgentTools().getCurrencyCodeForCurrentOrder());
  }

  protected double getTotalShippedItemsAmount(Order pOrder)
  {
      double totalShippedItemAmount = 0.0D;
      List<CommerceItem> commerceItems = pOrder.getCommerceItems();
      if(commerceItems != null && commerceItems.size() > 0)
      {
        for (CommerceItem commerceItem : commerceItems) 
        {
          if(commerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SHIPPED))
          {
            totalShippedItemAmount+=commerceItem.getPriceInfo().getAmount();
          }
        }
        totalShippedItemAmount = getPricingTools().round(totalShippedItemAmount, getCSRAgentTools().getCurrencyCodeForCurrentOrder());
      }
      return totalShippedItemAmount;
  }

  protected double getTotalShippedShippingTaxesAmount(Order pOrder, boolean pShippingAmount)
  {
      double totalShippedShTxAmount = 0.0D;
      List<MFFCommerceItemImpl> commerceItems = pOrder.getCommerceItems();
      if(null!=commerceItems && commerceItems.size() > 0)
      {
        for (MFFCommerceItemImpl commerceItem : commerceItems) 
        {
          if(commerceItem.getState() == StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SHIPPED))
          {
            // Get the list of prorated items for the shipped commerce Item
            List<String> lproratedItemIds = new ArrayList <String> (commerceItem.getReturnItemIds());
            if(null!=lproratedItemIds && lproratedItemIds.size()>0)
            {
              for (String lproratedItemId : lproratedItemIds) {
                RepositoryItem lRepositoryItem = getProrateItemById(lproratedItemId);
                double lamount = 0.0D;
                if(pShippingAmount)
                {
                  lamount = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING);
                }
                else
                {
                  lamount = (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_TAX);
                  lamount += (Double) lRepositoryItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_TAX);
                }
                totalShippedShTxAmount+=lamount;
              }
            }
          }
        }
        totalShippedShTxAmount = getPricingTools().round(totalShippedShTxAmount, getCSRAgentTools().getCurrencyCodeForCurrentOrder());
      }
      return totalShippedShTxAmount;
  }
  
  protected RepositoryItem getProrateItemById(String pId)
  {
      vlogDebug("Begin - Get proratred item for {0}", pId);
      RepositoryItem[] lItems = null;
      RepositoryItem lItem    = null;
      try {
        Object[] lParams        = new Object[2];
        lParams[0]              = pId;
        RepositoryView lView    = getOrderManager().getOmsOrderRepository().getView(MFFConstants.ITEM_DESC_PRORATE_ITEM);
        RqlStatement lStatement = RqlStatement.parseRqlStatement("Id EQUALS ?0");
        lItems                  = lStatement.executeQuery(lView, lParams);
        if (lItems != null && lItems.length > 0)
          lItem = lItems[0];
      } catch (RepositoryException e) {
        String lErrorMessage = String.format("getProrateItemById - Error locating Item Id: %s", pId);
        vlogError(e, lErrorMessage);
      }    
      return lItem;
    }
  
  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }


  public OMSOrderManager getOrderManager() {
    return orderManager;
  }


  public void setOrderManager(OMSOrderManager pOrderManager) {
    orderManager = pOrderManager;
  }

}
