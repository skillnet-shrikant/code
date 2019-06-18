package com.mff.commerce.order;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.constants.MFFConstants;
import com.mff.locator.StoreLocatorTools;
import com.mff.util.MFFUtils;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;
import mff.MFFEnvironment;

public class MFFOrderTools extends OrderTools {

  private String mDefaultShippingMethod;
  private MFFEnvironment mEnvironment;
  private IdGenerator mIdGenerator;
  private StoreLocatorTools storeLocatorTools;
  


Map<String, String> mOrderTypeToIdSpaceNameMap = new HashMap();
  Map<String, String> mOrderTypeToPrefixMap = new HashMap();
  private double mWeightLimitForAPOFPO;
  private double mLengthLimitForAPOFPO;
  
  
  public static final String GIFT_CARD_PAYMENT_TYPE = "giftCard";
  /**
   * @return the defaultShippingMethod
   */
  public String getDefaultShippingMethod() {
    return mDefaultShippingMethod;
  }

  /**
   * @param pDefaultShippingMethod
   *          the defaultShippingMethod to set
   */
  public void setDefaultShippingMethod(String pDefaultShippingMethod) {
    mDefaultShippingMethod = pDefaultShippingMethod;
  }

  public StoreLocatorTools getStoreLocatorTools() {
	  return storeLocatorTools;
  }

  public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
	  storeLocatorTools = pStoreLocatorTools;
  }
  
  public ShippingGroup createShippingGroup(String pType, ShippingPriceInfo pShippingPriceInfo) throws CommerceException {
    ShippingGroup group = super.createShippingGroup(pType, pShippingPriceInfo);
    group.setShippingMethod(getDefaultShippingMethod());
    return group;
  }

  /**
   * Method to Validate inventory for sku
   *  
   * @param pSkuId
   * @param pQuantityRequested
   * @param pOrder
   * @return
   */
  public List<String> validateInventoryForSku(String pSkuId, long pQuantityRequested, Order pOrder, boolean isValidateMaxInvForSku) {

    List<String> errorMessages = new ArrayList<String>();
    
    try {
      // get the environment max limit
      long defaultMaxQty = getEnvironment().getMaxQtyPerItemInOrder();
      long maxQty = defaultMaxQty;
      
      MFFOrderImpl order = (MFFOrderImpl)pOrder;
      //boolean includeBopisOnlyInventoryChecks = false;
      
      StockLevel lStockLevel = null;

      if(order.isBopisOrder()) {
    	  //includeBopisOnlyInventoryChecks = getStoreLocatorTools().isBOPISOnlyStore(order.getBopisStore());
    	  lStockLevel = ((FFRepositoryInventoryManager) getInventoryManager()).queryStoreSkuStockLevel(pSkuId, order.getBopisStore());
      } else {
    	  lStockLevel = ((FFRepositoryInventoryManager) getInventoryManager()).querySkuStockLevel(pSkuId);
      }

      long availableQty = lStockLevel.getStockLevel();

      long currentQuantityRequested = pQuantityRequested;

      // This item is out of stock and so cannot be added to cart
      if (availableQty <= 0) {
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.PRODUCT_OUT_OF_STOCK));
        errorMessages.add(resourceMsg);
        return errorMessages;
      } 
      
      long qtyAlreadyInCart = 0;

      CommerceItem ci = getCommerceItemForSku(pOrder, pSkuId);
      if (ci != null) {
        qtyAlreadyInCart = ci.getQuantity();
      }
      
      long totalQtyRequested = currentQuantityRequested + qtyAlreadyInCart;
      
      vlogDebug("validateInventoryForSku : Available Qty: {0} Current Qty Requested: {1} qtyAlreadyInCart: {2}, totalQtyRequested; {3}",
          availableQty,currentQuantityRequested,qtyAlreadyInCart,totalQtyRequested);
      
      // if totalQuantity requested is greater than the max quantity then add an exception
      if (isValidateMaxInvForSku && totalQtyRequested > maxQty) {
        Object[] msgArgs = new Object[1];// { maxQty };
        msgArgs[0] = maxQty;
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_ADD_QTY_ERROR));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        errorMessages.add(msg);
        return errorMessages;
      }

      // Requested quantity greater than available quantity, so cannot add this one to cart
      if (currentQuantityRequested > availableQty) {
        Object[] msgArgs = new Object[2];// { currentQuantityRequested,availableQty };
        msgArgs[0] = currentQuantityRequested;
        msgArgs[1] = availableQty;
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_QTY_NOT_AVAILABLE));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        errorMessages.add(msg);
        return errorMessages;
      }

      // if the sum of the requested quantity and item quantity in cart is
      // greater than available quantity then generate an exception
      if (totalQtyRequested > availableQty) {
        Object[] msgArgs = new Object[1];// { availableQty };
        msgArgs[0] = availableQty;
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_QTY_LMT_REACHED));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        errorMessages.add(msg);
        return errorMessages;
      }

    } catch (InventoryException e) {
      if (isLoggingError()) {
        logError(e);
      }
    }
    return errorMessages;
  }

  /**
   * Helper method to look up the commerce item for a particular sku.
   * 
   * @param pSkuId
   * @return CommerceItem
   */
  private CommerceItem getCommerceItemForSku(Order pOrder, String pSkuId) {
    if (pSkuId == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    List<MFFCommerceItemImpl> commerceItems = pOrder.getCommerceItems();
    Iterator<MFFCommerceItemImpl> iter = commerceItems.iterator();
    while (iter.hasNext()) {
      MFFCommerceItemImpl ci = (MFFCommerceItemImpl) iter.next();
      if (pSkuId.equals(ci.getCatalogRefId())) {
        return ci;
      }
    }
    return null;
  }
  
	public void populateIPAddressToOrder(Order pOrder, String pIPAddress) {
		((MFFOrderImpl) pOrder).setBuyerIpAddress(pIPAddress);
	}
	
	public void populateDeviceIdToOrder(Order pOrder, String pDeviceId) {
		((MFFOrderImpl) pOrder).setDeviceId(pDeviceId);
	}
	
	public String getAppInstanceName() {
		return System.getProperty("weblogic.Name");
	}
	
  
	/**
	 * Retrieves the next order number from the ATG id generator by order type.
	 * @param pOrder
	 * @return String
	 * @throws IdGeneratorException
	 */
	public String getNextOrderNumber(String pOrderType)  throws IdGeneratorException {
		
		vlogDebug ("getNextOrderNumber(): pOrderType: " + pOrderType);
		
		String nextOrderNumber = "";
		
		if (!StringUtils.isEmpty(pOrderType)) {
			nextOrderNumber = getIdGenerator().generateStringId(getOrderTypeToIdSpaceNameMap().get(pOrderType));
		}
		
		vlogDebug ("getNextOrderNumber(): nextOrderNumber: " + nextOrderNumber);
		return nextOrderNumber;
	}
	
	public String getOrderType(MFFOrderImpl pOrder) {
		
		String orderType = null;
		String originOfOrder = pOrder.getOriginOfOrder();
		vlogDebug ("getOrderType(): originOfOrder: " + originOfOrder);
				
		if (!StringUtils.isEmpty(originOfOrder)) {
			
			if( MFFConstants.ORDER_SOURCE_CONTACT_CENTER.equalsIgnoreCase(originOfOrder)){
				
				orderType = MFFConstants.ORDER_TYPE_CSC;
				
			} else if( MFFConstants.ORDER_SOURCE_DEFAULT.equalsIgnoreCase(originOfOrder)){
				
				orderType = MFFConstants.ORDER_TYPE_WEB;
				
				if (pOrder.isBopisOrder()){
					orderType = MFFConstants.ORDER_TYPE_BOPIS;
				}
			}
			
		} else {
			if (pOrder.isBopisOrder()){
				orderType = MFFConstants.ORDER_TYPE_BOPIS;
			}
		}
		
		vlogDebug ("getOrderType(): orderType: " + orderType);
		return orderType;
	}
	
	public boolean isCurrentOrderNumPrefixMatchesType(String pType, String pCurrentOrderNumber)  throws IdGeneratorException {
		
		vlogDebug ("isCurrentOrderNumPrefixMatchesType(): pType: " + pType + " , pCurrentOrderNumber: " + pCurrentOrderNumber);
		
		String prefixExpected = getOrderTypeToPrefixMap().get(pType);
		vlogDebug ("isCurrentOrderNumPrefixMatchesType(): prefixExpected: " + prefixExpected);
		
		if (StringUtils.isNotBlank(pCurrentOrderNumber) && pCurrentOrderNumber.startsWith(prefixExpected)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verifies if there are any shipping restrictions need to applied to the order
	 * @param pOrder
	 * @param pAddress
	 * @return
	 */
	public boolean restrictedShippingLocation(Order pOrder, ContactInfo pAddress){
	  
	  if(pOrder == null || pAddress == null)
	    return false;
	  
	  String lState = pAddress.getState();
	  
	  for(Object lItem: pOrder.getCommerceItems()){
	    CommerceItem lComItem = (CommerceItem) lItem;
	    RepositoryItem lSku = (RepositoryItem)lComItem.getAuxiliaryData().getCatalogRef();
	    
	    String restrictedLocList = (String)lSku.getPropertyValue(MFFConstants.RESTRICTED_LOC);
	    vlogDebug("RestrictedShipping: skuId:{0} restricted state list: {1} and State from address is:{2}",lComItem.getCatalogRefId(),restrictedLocList,lState);
	    if(restrictedLocList !=null && !restrictedLocList.isEmpty()){
  	    String lRestrictedLocations[] = restrictedLocList.split("\\^");
  	    //vlogDebug("RestrictedShipping: states:: {0}",lRestrictedLocations.toString());
  	    // iterate through restricted locations
  	    for(String restrictedLoc: lRestrictedLocations){
  	      if(lState.equalsIgnoreCase(restrictedLoc)){
  	        vlogDebug("RestrictedShipping: shiping address state:{0} is a restricted state, will not be able to ship to this location",lState);
  	        return true;
  	      }
  	    }
	    }
	  }
	  return false;
	}
	
	/**
   * Verifies if there are any shipping restrictions need to applied to the order
   * @param pOrder
   * @param pAddress
   * @return
   */
  public boolean shippingRestrictionASR(Order pOrder, String pShipMethod){
    
    if(pOrder == null || pShipMethod == null)
      return true;
    
    for(Object lItem: pOrder.getCommerceItems()){
      MFFCommerceItemImpl lComItem = (MFFCommerceItemImpl) lItem;
      Integer minAge = lComItem.getMinimumAge();
      vlogDebug("Order:{0}, Ci :{1}, minimumAge:{2}, shipMethod:{3}",pOrder.getId(),lComItem.getId(),minAge,pShipMethod);
      if(minAge !=null && minAge >= 18 ){
        if(!pShipMethod.equalsIgnoreCase("Standard")){
          return true;
        }
      }
    }
    return false;
  }
  
  @SuppressWarnings("rawtypes")
  public boolean isOrderWeightValidForAPOFPO(MFFOrderImpl pOrder) {
      double orderWeight = 0.0D;
      List cItems = pOrder.getCommerceItems();
      for(Iterator it = cItems.iterator(); it.hasNext();) {
    	  vlogDebug("isOrderWeightValidForAPOFPO(): orderWeight: " + orderWeight);
          CommerceItem commerceItem = (CommerceItem)it.next();
          RepositoryItem sku = (RepositoryItem)commerceItem.getAuxiliaryData().getCatalogRef();
          Double itemWeight = (Double)sku.getPropertyValue("weight");
          vlogDebug("isOrderWeightValidForAPOFPO(): itemWeight: " + itemWeight);
          if(itemWeight == null){
              itemWeight = Double.valueOf(0.0D);
          }
          long itemQuantity = commerceItem.getQuantity();
          vlogDebug("isOrderWeightValidForAPOFPO(): itemQuantity: " + itemQuantity);
          double itemTotalWeight = itemWeight.doubleValue() * (double)itemQuantity;
          orderWeight += itemTotalWeight;
          
          if(orderWeight >= getWeightLimitForAPOFPO()) {
              vlogDebug("isOrderWeightValidForAPOFPO(): returng false.");
              return false;
          }
      }
      
      vlogDebug("isOrderWeightValidForAPOFPO(): returng true.");
      return true;
  }

  @SuppressWarnings("rawtypes")
  public boolean isItemDimensionsValidForAPOFPO(MFFOrderImpl pOrder) {
      List cItems = pOrder.getCommerceItems();
      for(Iterator it = cItems.iterator(); it.hasNext();) {
          CommerceItem commerceItem = (CommerceItem)it.next();
          RepositoryItem sku = (RepositoryItem)commerceItem.getAuxiliaryData().getCatalogRef();
          Double itemLength = (Double)sku.getPropertyValue("skuLength");
          vlogDebug("isItemDimensionsValidForAPOFPO(): itemLength: " + itemLength);
          Double itemGirth = (Double)sku.getPropertyValue("girth");
          vlogDebug("isItemDimensionsValidForAPOFPO(): itemGirth: " + itemGirth);
          if(itemLength == null){
              itemLength = Double.valueOf(0.0D);
          }
          if(itemGirth == null){
              itemGirth = Double.valueOf(0.0D);
          }
          double itemDimension = itemLength.doubleValue() + itemGirth.doubleValue();
          vlogDebug("isItemDimensionsValidForAPOFPO(): itemDimension: " + itemDimension);
          if(itemDimension > getLengthLimitForAPOFPO()) {
              vlogDebug("isItemDimensionsValidForAPOFPO(): returng false.");
              return false;
          }
      }

      vlogDebug("isItemDimensionsValidForAPOFPO(): returng true.");
      return true;
  }
	
	/**
	 * The IdGenerator component to be used
	 * 
	 * @return the IdGenerator
	 */
	public IdGenerator getIdGenerator() {
		return mIdGenerator;
	}

	/**
	 * Set the IdGenerator component to be used
	 * 
	 * @param idGenerator
	 */
	public void setIdGenerator(IdGenerator idGenerator) {
		this.mIdGenerator = idGenerator;
	}

	public MFFEnvironment getEnvironment() {
		return mEnvironment;
	}

	public void setEnvironment(MFFEnvironment pEnvironment) {
		mEnvironment = pEnvironment;
	}

	/**
	 * The map contains order number generator space names by order type, to be used for generating the order
	 * number. This can be set to different values for web, csc and bopis.
	 * 
	 * @return the generator space name
	 */
	
	public Map<String, String> getOrderTypeToIdSpaceNameMap() {
		return mOrderTypeToIdSpaceNameMap;
	}

	/**
	 * Sets the order number generator space name map.
	 * 
	 * @param orderIdGeneratorSpaceName
	 */
	
	public void setOrderTypeToIdSpaceNameMap(
			Map<String, String> pOrderTypeToIdSpaceNameMap) {
		mOrderTypeToIdSpaceNameMap = pOrderTypeToIdSpaceNameMap;
	}

	public Map<String, String> getOrderTypeToPrefixMap() {
		return mOrderTypeToPrefixMap;
	}

	public void setOrderTypeToPrefixMap(Map<String, String> pOrderTypeToPrefixMap) {
		mOrderTypeToPrefixMap = pOrderTypeToPrefixMap;
	}

	public double getWeightLimitForAPOFPO() {
		return mWeightLimitForAPOFPO;
	}

	public void setWeightLimitForAPOFPO(double pWeightLimitForAPOFPO) {
		mWeightLimitForAPOFPO = pWeightLimitForAPOFPO;
	}

	public double getLengthLimitForAPOFPO() {
		return mLengthLimitForAPOFPO;
	}

	public void setLengthLimitForAPOFPO(double pLengthLimitForAPOFPO) {
		mLengthLimitForAPOFPO = pLengthLimitForAPOFPO;
	}
}
