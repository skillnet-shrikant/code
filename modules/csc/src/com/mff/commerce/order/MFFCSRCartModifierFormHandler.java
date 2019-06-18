package com.mff.commerce.order;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.csr.environment.MFFCSREnvironmentTools;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.csr.order.CSRCartModifierFormHandler;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.web.messaging.MessageConstants;
import atg.web.messaging.RequestMessage;
import mff.MFFEnvironment;

/**
 * The class is extended to add inventory validation and any other MFF
 * specific validations for adding/updating items in cart.
 * 
 * @author vsingh
 *
 */
public class MFFCSRCartModifierFormHandler extends CSRCartModifierFormHandler {
  
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private MFFPrevAddressRequest mPrevRequest;
  private double giftCardDenomination; // gift card value selected
  private double giftCardMaxDenomination; // max gift card denomination possible, sanity check
  private double giftCardMinDenomination; // max gift card denomination possible, sanity check
  private double maxTotalGCInCart; // max Total Value of all GCs in cart
  private String taxExempSelected; // selection of a customer tax exemption in the cart
  private String addTaxExemptionSuccessUrl;
  private String addTaxExemptionErrorUrl;
  private double shippingAdjustmentAmount;
  private String shippingAdjustmentSuccessURL;
  private String shippingAdjustmentErrorURL;
  private MFFEnvironment environment;
  private boolean validateMaxInvForSku;
  private MFFCSREnvironmentTools  mCsrEnvironmentTools;
  private String mIdPropertyName;
  private String mSkuPropertyName;
  
  private static final String REQUEST_ENTRY_ADD_TAX_EXEMPTION = "MFFCSRCartModifierFormHandler.handleApplyTaxExemption";
  private static final String REQUEST_ENTRY_SHIPPING_ADJUSTMENT = "MFFCSRCartModifierFormHandler.handleShippingAdjustment";
  
	public String getSkuPropertyName() {
		return mSkuPropertyName;
	}



	public void setSkuPropertyName(String pSkuPropertyName) {
		mSkuPropertyName = pSkuPropertyName;
	}



	public String getIdPropertyName() {
		return mIdPropertyName;
	}



	public void setIdPropertyName(String pIdPropertyName) {
		mIdPropertyName = pIdPropertyName;
	}




	public MFFCSREnvironmentTools getCsrEnvironmentTools() {
		return mCsrEnvironmentTools;
	}
	
	public void setCsrEnvironmentTools(MFFCSREnvironmentTools pCsrEnvironmentTools) {
		mCsrEnvironmentTools=pCsrEnvironmentTools;
	}
  
  @SuppressWarnings("unchecked")
  @Override
  public void preAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("MFFCSRCartModifierFormHandler : Inside preAddItemToOrder ");
    /// Product Id Sku Id validation
	if(getCsrEnvironmentTools()!=null){
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		if( searchVariations!=null&&!searchVariations.isEmpty()){
			if(getCsrEnvironmentTools().isUseProductSearchVariation()){
				
				String[]  skuIds=getCatalogRefIds();
				if(skuIds!=null&&skuIds.length!=0){
					vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify getCatalogRefIds: Start");
					String[] newSkuIds=new String[skuIds.length];
					for(int i=0;i<skuIds.length;i++){
						String oldSkuId=skuIds[i];
						if(getSkuPropertyName()!=null&&!getSkuPropertyName().isEmpty()){
		    				String newSkuId=parsedValue(getSkuPropertyName(), oldSkuId);
		    				newSkuIds[i]=newSkuId;
		    			}
		    			else{
		    				String newSkuId=parsedValue("sku", oldSkuId);
		    				newSkuIds[i]=newSkuId;
		    			}
						
					}
					setCatalogRefIds(newSkuIds);
					vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify getCatalogRefIds: End");
				}
				
				String formProductId=getProductId();
				if(formProductId!=null&&!formProductId.isEmpty()){
					vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify form getProductId: Start");
	    			if(getIdPropertyName()!=null&&!getIdPropertyName().isEmpty()){
	    				String newProductId=parsedValue(getIdPropertyName(), formProductId);
	    				setProductId(newProductId);
	    			}
	    			else{
	    				String newProductId=parsedValue("id", formProductId);
	    				setProductId(newProductId);
	    			}
	    			vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify form getProductId: End");
	    		}
				
				
			    if(getItems()!=null&&getItems().length!=0){
			    	vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify getItems product id sku id block Start");
			    	for(int i=0;i<getItems().length;i++){
			    		String skuId=getItems()[i].getCatalogRefId();
			    		if(skuId!=null&&!skuId.isEmpty()){
			    			if(getSkuPropertyName()!=null&&!getSkuPropertyName().isEmpty()){
			    				String newSkuId=parsedValue(getSkuPropertyName(), skuId);
			    				getItems()[i].setCatalogRefId(newSkuId);
			    			}
			    			else{
			    				String newSkuId=parsedValue("sku", skuId);
			    				getItems()[i].setCatalogRefId(newSkuId);
			    			}
			    		}
			    		String productId=getItems()[i].getProductId();
			    		if(productId!=null&&!productId.isEmpty()){
			    			if(getIdPropertyName()!=null&&!getIdPropertyName().isEmpty()){
			    				String newProductId=parsedValue(getIdPropertyName(), productId);
			    				getItems()[i].setProductId(newProductId);
			    			}
			    			else{
			    				String newProductId=parsedValue("id", productId);
			    				getItems()[i].setProductId(newProductId);
			    			}
			    		}
			    	}
			    	vlogDebug("MFFCSRCartModifierFormHandler : preAddItemToOrder: Modify getItems product id sku id block End");
			    }
				
			}
		}
	}


    
    super.preAddItemToOrder(pRequest, pResponse);
    MFFCSROrderTools orderTools = (MFFCSROrderTools)getOrderManager().getOrderTools();
    if (!getFormError() && getItems() != null) {
      Order order = getOrder();
      AddCommerceItemInfo[] items = getItems();
      if(items!=null){
    	  for(AddCommerceItemInfo itemInfo : items){
    		  if(itemInfo.getCatalogRefId()!=null){
    			  vlogDebug("MFFCSRCartModifierFormHandler: preAddItemToOrder: ItemInfo : skuId: "+itemInfo.getCatalogRefId());
    		  }
    		  if(itemInfo.getProductId()!=null){
    			  vlogDebug("MFFCSRCartModifierFormHandler: preAddItemToOrder: ItemInfo : productId: "+itemInfo.getProductId());
    		  }
    	  }
      }
      for (AddCommerceItemInfo itemInfo : items) {
        if (itemInfo.getQuantity() <= 0L) 
          continue;

        String skuId = itemInfo.getCatalogRefId();
        MFFCatalogTools catTools = (MFFCatalogTools) getCatalogTools();
        // Validate inventory for this sku before adding it unless it is a gift card
        if(!catTools.isGCProduct(itemInfo.getProductId()))
        {
          List<String> errorMessages = orderTools.validateInventoryForSku(skuId, itemInfo.getQuantity(),order,isValidateMaxInvForSku());
          if (errorMessages != null && errorMessages.size() > 0) {
            for(String errorMsg : errorMessages){
              getFormExceptionGenerator().generateException(errorMsg, this);
            }
          }
        }
        else
        {
          // Check denomination min and max allowed
          DecimalFormat df = new DecimalFormat("0.00");
          if(getGiftCardDenomination()>getGiftCardMaxDenomination())
          {
            getFormExceptionGenerator().generateException("The gift card denomination can not exceed $" + df.format(getGiftCardMaxDenomination()), this);
          }
          if(getGiftCardDenomination()<getGiftCardMinDenomination())
          {
            getFormExceptionGenerator().generateException("The gift card denomination can not be less than $" + df.format(getGiftCardMinDenomination()), this);
          }
          // Check max total gift cards already in cart + gift cards being added
          double giftCardsTotal=0.0d;
          giftCardsTotal+=(getPricingTools().round(getGiftCardDenomination()*itemInfo.getQuantity()));
          List<CommerceItem> lCommerceItems = order.getCommerceItems();
          for (CommerceItem lCommerceItem : lCommerceItems) {
            if(catTools.isGCProduct(lCommerceItem.getAuxiliaryData().getProductId()))
            {
              // add amount to Order Total
              double gcAmount = lCommerceItem.getPriceInfo().getAmount();
              giftCardsTotal+= gcAmount;              
            }
          }
          if(giftCardsTotal>getMaxTotalGCInCart())
          {
            getFormExceptionGenerator().generateException("The total amount on gift cards in an order can not exceed $" + df.format(getMaxTotalGCInCart()), this);
          }
        }
      }
    }
  }

  /* (non-Javadoc)
   * @see atg.commerce.csr.order.CSRCartModifierFormHandler#postAddItemToOrder(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void postAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.postAddItemToOrder(pRequest, pResponse);

    boolean fflOrder = false;
    if (getOrder() != null && getOrder().getCommerceItemCount() > 0) 
    {
      List<MFFCommerceItemImpl> lItems = getOrder().getCommerceItems();
      for (MFFCommerceItemImpl lItem : lItems) 
      {
        // check if cart contains ffl items if yes then set this flag to true
        if (!fflOrder && lItem.getFFL())
        {
          fflOrder = true;
        }
        
        // Overriding PriceInfo for GiftCard product
         
        if (lItem.isGiftCard() && lItem.getGiftCardDenomination()==0.0d && getGiftCardDenomination()>0.0d)
        {
          double finalAmount = getPricingTools().round(getGiftCardDenomination()*lItem.getQuantity());
          lItem.setGiftCardDenomination(getGiftCardDenomination());
          lItem.getPriceInfo().setListPrice(lItem.getGiftCardDenomination());
          lItem.getPriceInfo().setSalePrice(lItem.getGiftCardDenomination());
          lItem.getPriceInfo().setAmount(finalAmount);
          lItem.getPriceInfo().setAmountIsFinal(true);
          try {
            runProcessRepriceOrder(getAddItemToOrderPricingOp(), getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), createRepriceParameterMap());
          } catch (RunProcessException e) {
            getFormExceptionGenerator().generateException("There was an unexpected issue repricing the shopping cart, please try again", this);
          }
        }
      }
    }
    ((MFFOrderImpl) getOrder()).setFFLOrder(fflOrder);
  }

  @Override
  public void preSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("MFFCSRCartModifierFormHandler : Inside preSetOrderByCommerceId ");
    super.preSetOrderByCommerceId(pRequest, pResponse);
    
    validateInventory(pRequest,pResponse);

  }
  
  @Override
  public void preMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    vlogDebug("MFFCSRCartModifierFormHandler : Inside preMoveToPurchaseInfo");
    super.preMoveToPurchaseInfo(pRequest, pResponse);
    
    if(((MFFOrderImpl) getOrder()).isFFLOrder()){
      addFormException(new DropletException(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_FFL_ORDER)));
      return;
    }
    validateInventory(pRequest,pResponse);
  }
  
  @SuppressWarnings("unchecked")
  private void validateInventory(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)throws ServletException, IOException{
    MFFCSROrderTools orderTools = (MFFCSROrderTools)getOrderManager().getOrderTools();
    Order order = getOrder();
    
    if (!getFormError()) {
        List<MFFCommerceItemImpl> items = getOrder().getCommerceItems();
        for (MFFCommerceItemImpl ciItem : items) {
          MFFCatalogTools catTools = (MFFCatalogTools) getCatalogTools();
          if(!catTools.isGCProduct(ciItem.getProductId()))
          {
            long ciItemQuantity = ciItem.getQuantity();
            String skuId = ciItem.getCatalogRefId();
            if (isCheckForChangedQuantity()) {
                long quantity = getQuantityByCommerceId(ciItem.getId(), pRequest, pResponse);
                vlogDebug("validateInventory - quantity - {0}, ciItem - {1}", quantity, ciItemQuantity);
                if (quantity != ciItemQuantity && quantity > ciItemQuantity) {
                  // check for inventory for additional quantity that's been updated
                  long quantityDiff = quantity - ciItem.getQuantity();
                  List<String> errorMessages = orderTools.validateInventoryForSku(skuId, quantityDiff,order,isValidateMaxInvForSku());
                  if (errorMessages != null && errorMessages.size() > 0) {
                    for(String errorMsg : errorMessages){
                      getFormExceptionGenerator().generateException(errorMsg, this);
                    }
                  }
                }
            }
          }
        }
    }
  }
  
  @Override
  public void postMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    vlogDebug("MFFCSRCartModifierFormHandler : Inside postMoveToPurchaseInfo");
    super.postMoveToPurchaseInfo(pRequest, pResponse);
    
    if (!getFormError()) {
      getPrevRequest().clearAddress();
    }
  }
  
  public boolean handleApplyTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSRCartModifierFormHandler.handleApplyTaxExemption");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_ADD_TAX_EXEMPTION)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if(null!=getOrder())
        {
          synchronized (getOrder()) {
            vlogDebug("handleApplyTaxExemption: taxExempSelected: {0}", getTaxExempSelected());
            ((MFFOrderImpl) getOrder()).setTaxExemptionName(getTaxExempSelected());
            updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
          }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error adding tax exemption to order");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_ADD_TAX_EXEMPTION);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }
    return checkFormRedirect(getAddTaxExemptionSuccessUrl(), getAddTaxExemptionErrorUrl(), pRequest, pResponse);
  }
  
  @Override
  public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Inside postRemoveItemFromOrder");
    super.postRemoveItemFromOrder(pRequest, pResponse);
    updateCartInfo(pRequest, pResponse);
  }
  
  @Override
  public void postSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Inside postSetOrderByCommerceId");
    super.postSetOrderByCommerceId(pRequest, pResponse);
    updateCartInfo(pRequest, pResponse);
  }
  
  /**
   * Update order FFL flags based on the cart content
   * 
   * @param pRequest
   * @param pResponse
   */
  @SuppressWarnings("unchecked")
  private void updateCartInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
   
    if (getOrder().getCommerceItemCount() <= 0) {
      if (((MFFOrderImpl) getOrder()).isFFLOrder()) {
        // update ffl info, set fflOrder to false
        // cart will be defaulted to non-ffl order
        try {
          ((MFFOrderManager) getOrderManager()).updateFFLOrder(getOrder());
        } catch (CommerceException e) {
          vlogError(e, "There is an error while updating the order:{0} with ffl information", getOrder().getId());
          getFormExceptionGenerator().generateException(MFFConstants.MSG_FFL_ORDER_UPDATE_ERROR, true, this, pRequest);
        }
      }
    }else{
      
      List<MFFCommerceItemImpl> lItems = getOrder().getCommerceItems();
      boolean fflOrder = false;
      for (MFFCommerceItemImpl lItem : lItems) {
        // check if cart contains ffl items if yes then set this flag to true
        if (!fflOrder && lItem.getFFL()){
          fflOrder = true;
          break;
        }
      }
      
      try {
        ((MFFOrderManager) getOrderManager()).updateFFLOrderInfo(getOrder(), fflOrder);
      } catch (CommerceException e) {
        vlogError(e, "There is an error while updating the order:{0} with ffl information", getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_FFL_ORDER_UPDATE_ERROR, true, this, pRequest);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public boolean handleShippingAdjustment(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSRCartModifierFormHandler.handleShippingAdjustment");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_SHIPPING_ADJUSTMENT)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        if(null!=getOrder())
        {
          double adjustmentAmount = getShippingAdjustmentAmount();
          if(adjustmentAmount <= 0.0){
            addFormException(new DropletException("Shipping Amount cannot be equal/less than 0"));
            return checkFormRedirect(getShippingAdjustmentSuccessURL(), getShippingAdjustmentErrorURL(), pRequest, pResponse);
          }
          
          synchronized (getOrder()) {
            String shipGroupId = ((ShippingGroup)(getOrder().getShippingGroups().get(0))).getId();
            getShippingGroupPriceOverrides().put(shipGroupId, getShippingAdjustmentAmount());
            try {
              applyShippingGroupPriceOverrides(getOrder(), getShippingGroupPriceOverrides());
              
              runProcessRepriceOrder(getModifyOrderPricingOp(), getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), createRepriceParameterMap()); 
              updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            } catch (ParseException e) {
              logError(e);
            }catch (RunProcessException e) {
              logError(e);
            }
          }
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException e) {
        // error message to be displayed for the agent
        RequestMessage message = new RequestMessage();
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error doing shipping adjustment on the order");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_SHIPPING_ADJUSTMENT);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }
    return checkFormRedirect(getShippingAdjustmentSuccessURL(), getShippingAdjustmentErrorURL(), pRequest, pResponse);
  }
  
  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(
      MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }
  
  public MFFPrevAddressRequest getPrevRequest() {
    return mPrevRequest;
  }

  public void setPrevRequest(MFFPrevAddressRequest mPrevRequest) {
    this.mPrevRequest = mPrevRequest;
  }

  /**
   * @return the giftCardDenomination
   */
  public double getGiftCardDenomination() {
    return giftCardDenomination;
  }

  /**
   * @param pGiftCardDenomination the giftCardDenomination to set
   */
  public void setGiftCardDenomination(double pGiftCardDenomination) {
    giftCardDenomination = pGiftCardDenomination;
  }

  /**
   * @return the giftCardMaxDenomination
   */
  public double getGiftCardMaxDenomination() {
    return giftCardMaxDenomination;
  }

  /**
   * @param pGiftCardMaxDenomination the giftCardMaxDenomination to set
   */
  public void setGiftCardMaxDenomination(double pGiftCardMaxDenomination) {
    giftCardMaxDenomination = pGiftCardMaxDenomination;
  }

  /**
   * @return the giftCardMinDenomination
   */
  public double getGiftCardMinDenomination() {
    return giftCardMinDenomination;
  }

  /**
   * @param pGiftCardMinDenomination the giftCardMinDenomination to set
   */
  public void setGiftCardMinDenomination(double pGiftCardMinDenomination) {
    giftCardMinDenomination = pGiftCardMinDenomination;
  }

  /**
   * @return the maxTotalGCInCart
   */
  public double getMaxTotalGCInCart() {
    return maxTotalGCInCart;
  }

  /**
   * @param pMaxTotalGCInCart the maxTotalGCInCart to set
   */
  public void setMaxTotalGCInCart(double pMaxTotalGCInCart) {
    maxTotalGCInCart = pMaxTotalGCInCart;
  }

  /**
   * @return the taxExempSelected
   */
  public String getTaxExempSelected() {
    return taxExempSelected;
  }

  /**
   * @param pTaxExempSelected the taxExempSelected to set
   */
  public void setTaxExempSelected(String pTaxExempSelected) {
    taxExempSelected = pTaxExempSelected;
  }

  /**
   * @return the addTaxExemptionSuccessUrl
   */
  public String getAddTaxExemptionSuccessUrl() {
    return addTaxExemptionSuccessUrl;
  }

  /**
   * @param pAddTaxExemptionSuccessUrl the addTaxExemptionSuccessUrl to set
   */
  public void setAddTaxExemptionSuccessUrl(String pAddTaxExemptionSuccessUrl) {
    addTaxExemptionSuccessUrl = pAddTaxExemptionSuccessUrl;
  }

  /**
   * @return the addTaxExemptionErrorUrl
   */
  public String getAddTaxExemptionErrorUrl() {
    return addTaxExemptionErrorUrl;
  }

  /**
   * @param pAddTaxExemptionErrorUrl the addTaxExemptionErrorUrl to set
   */
  public void setAddTaxExemptionErrorUrl(String pAddTaxExemptionErrorUrl) {
    addTaxExemptionErrorUrl = pAddTaxExemptionErrorUrl;
  }

  public MFFEnvironment getEnvironment() {
    return environment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
    environment = pEnvironment;
  }

  public double getShippingAdjustmentAmount() {
    return shippingAdjustmentAmount;
  }

  public void setShippingAdjustmentAmount(double pShippingAdjustmentAmount) {
    shippingAdjustmentAmount = pShippingAdjustmentAmount;
  }

  public String getShippingAdjustmentSuccessURL() {
    return shippingAdjustmentSuccessURL;
  }

  public void setShippingAdjustmentSuccessURL(String pShippingAdjustmentSuccessURL) {
    shippingAdjustmentSuccessURL = pShippingAdjustmentSuccessURL;
  }

  public String getShippingAdjustmentErrorURL() {
    return shippingAdjustmentErrorURL;
  }

  public void setShippingAdjustmentErrorURL(String pShippingAdjustmentErrorURL) {
    shippingAdjustmentErrorURL = pShippingAdjustmentErrorURL;
  }

  public boolean isValidateMaxInvForSku() {
    return validateMaxInvForSku;
  }

  public void setValidateMaxInvForSku(boolean pValidateMaxInvForSku) {
    validateMaxInvForSku = pValidateMaxInvForSku;
  }
  
  private String parsedValue(String propName, String currValue){
		vlogDebug("MFFCSRCartModifierFormHandler : parsedValue: Start");
		String retValue=currValue;
		Map<String,String> searchVariations=getCsrEnvironmentTools().getProductSearchMaxFieldConfig();
		vlogDebug("MFFCSRCartModifierFormHandler : propertyModified: "+propName+" : current value: "+currValue);
		if(searchVariations!=null&&currValue!=null&&!searchVariations.isEmpty() && !currValue.isEmpty()){
			String lengthField=searchVariations.get(propName);
			if(lengthField==null||lengthField.isEmpty()){
				lengthField="0";
			}
			int validLength=Integer.parseInt(lengthField);
			if(validLength!=0){
				
				int valLength=validLength;
				int currLength=currValue.length();
				if(valLength>currLength){
					int zerosToAppend=valLength-currLength;
					String zeros="";
					for(int i=0;i<zerosToAppend;i++){
						zeros+="0";
					}
					retValue=zeros+currValue;
				}
				
			}
			
		}
		vlogDebug("MFFCSRCartModifierFormHandler : propertyModified: "+propName+" : current value: "+retValue);
		vlogDebug("MFFCSRCartModifierFormHandler : parsedValue: End");
		return retValue;
	}
  
}
