package com.mff.commerce.order;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.order.purchase.MFFInventoryMessage;
import com.mff.commerce.order.purchase.MFFPurchaseProcessHelper;
import com.mff.constants.MFFConstants;
import com.mff.locator.StoreLocatorTools;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.util.MFFUtils;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.SimpleOrderManager;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import mff.MFFEnvironment;

public class MFFOrderManager extends SimpleOrderManager {

  public Repository mLegacyOrderRepository = null;
  private FFRepositoryInventoryManager mInventoryManager;
  private MFFProfileTools mProfileTools;
  private MFFPurchaseProcessHelper mPurchaseProcessHelper;
  private static final long INV_MSG_USE_DEFAULT_QUANTITY = -1;
  private final String GLOBAL = "global";
  private final String DESCRIPTION = "description";
  /** The m claimable tools. */
  private ClaimableTools mClaimableTools;
  private StoreLocatorTools storeLocatorTools;
  private MFFEnvironment mEnvironment;
  
  public MFFEnvironment getEnvironment() {
	  return mEnvironment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
	  mEnvironment = pEnvironment;
  }
	  
  public StoreLocatorTools getStoreLocatorTools() {
	return storeLocatorTools;
}

public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
	storeLocatorTools = pStoreLocatorTools;
}

public static final String PROMOTION = "promotion";
  
  public Repository getLegacyOrderRepository() {
    return mLegacyOrderRepository;
  }

  public void setLegacyOrderRepository(Repository pLegacyOrderRepository) {
    this.mLegacyOrderRepository = pLegacyOrderRepository;
  }

  @SuppressWarnings("unchecked")
  public Order loadLegacyOrder(String pOrderId) throws CommerceException {
    HashMap map = new HashMap(13);

    map.put("OrderManager", this);
    map.put("CatalogTools", getOrderTools().getCatalogTools());
    map.put("OrderId", pOrderId);
    map.put("OrderRepository", getLegacyOrderRepository());
    map.put("LoadOrderPriceInfo", Boolean.FALSE);
    map.put("LoadTaxPriceInfo", Boolean.FALSE);
    map.put("LoadItemPriceInfo", Boolean.FALSE);
    map.put("LoadShippingPriceInfo", Boolean.FALSE);
    map.put("LoadCostCenterObjects", Boolean.FALSE);
    PipelineResult result;
    try {
      result = getPipelineManager().runProcess("loadOrder", map);
    } catch (RunProcessException e) {
      throw new CommerceException(e);
    }

    Order order = (Order) map.get("Order");
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderIdParameter", "atg.commerce.order.OrderResources", sResourceBundle));
    }

    return order;
  }

  @SuppressWarnings("unchecked")
  public Order loadOrderByOrderNumber(String pOrderNumber) throws CommerceException {
    HashMap map = new HashMap(13);
    RepositoryItem[] items = null;
    try {
      RepositoryView orderView = getOrderTools().getOrderRepository().getView("order");
      QueryBuilder orderQueryBuilder = orderView.getQueryBuilder();

      QueryExpression orderNumberProperty = orderQueryBuilder.createPropertyQueryExpression(MFFConstants.PROPERTY_ORDER_NUMBER);
      QueryExpression orderNumberValue = orderQueryBuilder.createConstantQueryExpression(pOrderNumber);
      Query orderQuery = orderQueryBuilder.createComparisonQuery(orderNumberProperty, orderNumberValue, QueryBuilder.EQUALS);
      items = orderView.executeQuery(orderQuery);
    } catch (RepositoryException rEx) {
      throw new CommerceException(rEx);
    }
    if (items == null) {
      return null;
    }
    String orderId = null;
    if (items.length > 0) {
      orderId = items[0].getRepositoryId();
    }
    map.put("OrderManager", this);
    map.put("CatalogTools", getOrderTools().getCatalogTools());
    map.put("OrderId", orderId);
    map.put("OrderRepository", getOrderTools().getOrderRepository());
    map.put("LoadOrderPriceInfo", Boolean.FALSE);
    map.put("LoadTaxPriceInfo", Boolean.FALSE);
    map.put("LoadItemPriceInfo", Boolean.FALSE);
    map.put("LoadShippingPriceInfo", Boolean.FALSE);
    map.put("LoadCostCenterObjects", Boolean.FALSE);
    PipelineResult result;
    try {
      result = getPipelineManager().runProcess("loadOrder", map);
    } catch (RunProcessException e) {
      throw new CommerceException(e);
    }

    Order order = (Order) map.get("Order");
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderIdParameter", "atg.commerce.order.OrderResources", sResourceBundle));
    }

    return order;
  }

  /**
   * This method is used to load LegacyOrders
   * 
   * @param pOrderIds
   * @return
   * @throws CommerceException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<Order> loadLegacyOrders(List<String> pOrderIds) throws CommerceException {
    if ((pOrderIds == null) || (pOrderIds.isEmpty())) {
      return Collections.EMPTY_LIST;
    }

    ArrayList orderList = new ArrayList(pOrderIds.size());
    for (String orderId : pOrderIds) {
      orderList.add(loadLegacyOrder(orderId));
    }

    return orderList;
  }

  /**
   * Extended OOTB merge orders to perform below merge operations clear existing
   * order if source or destination order is bopis/FFL This will fix the merge
   * issues for following scenarios
   * <ul>
   * <li>current cart shipping, saved cart bopis</li>
   * <li>current cart bopis, saved cart shipping</li>
   * <li>current cart bopis, saved cart bopis</li>
   * <li>current cart FFL, saved cart bopis</li>
   * <li>current cart bopis, saved cart FFL</li>
   * </ul>
   */
  @Override
  public void mergeOrders(Order pSrcOrder, Order pDestOrder, boolean pMergeShippingGroups, boolean pRemoveSrcOrder) throws CommerceException {

    vlogInfo("MergeOrders  srcOrder: ({0}) and destOrder({1})", pSrcOrder, pDestOrder);

    boolean cartMerged = false;
    boolean clearDestOrder = false;

    // ffl params
    boolean isSrcOrderFFL = ((MFFOrderImpl) pSrcOrder).isFFLOrder();
    boolean isDestOrderFFL = ((MFFOrderImpl) pDestOrder).isFFLOrder();
    boolean isFFL = false;
    // bopis params
    boolean isSrcOrderBopis = ((MFFOrderImpl) pSrcOrder).isBopisOrder();
    boolean isDestOrderBopis = ((MFFOrderImpl) pDestOrder).isBopisOrder();

    if (isSrcOrderBopis || isDestOrderBopis) {
      vlogDebug("Source or Destination order is Bopis, we will remove destination order");
      clearDestOrder = true;
    } else {
      // Below scenarios current cart takes precedence will copy order
      // properties from current order to dest order
      // src order is ffl & dest order is non-ffl
      if (isSrcOrderFFL || isDestOrderFFL) {
        vlogDebug("Source order or current order is FFL, we will clear the Destination Order and copy properties from currentOrder");
        clearDestOrder = true;
      }
    }

    boolean orderModified = false;
    MFFOrderImpl srcOrder = (MFFOrderImpl) pSrcOrder;
    MFFOrderImpl destOrder = (MFFOrderImpl) pDestOrder;
    synchronized (destOrder) {
      if (clearDestOrder) {
        vlogDebug("Clearing existing order and copy properties from existing order");
        removeItemsFormOrder(destOrder);

        // copy order properties from source order to destination
        destOrder.setBopisOrder(srcOrder.isBopisOrder());
        destOrder.setBopisStore(srcOrder.getBopisStore());
        destOrder.setBopisPerson(srcOrder.getBopisPerson());
        destOrder.setBopisEmail(srcOrder.getBopisEmail());
        destOrder.setFFLOrder(srcOrder.isFFLOrder());
        orderModified = true;
      }

      super.mergeOrders(srcOrder, destOrder, pMergeShippingGroups, pRemoveSrcOrder);
      cartMerged = true;

      // copy contact email
      try {
        RepositoryItem destUserProfile = getProfileTools().getProfileForOrder(pDestOrder);
        // Set the contact email in the dest order
        vlogInfo("Action - setContactEmail  for destination order: ({0}) after merge", destOrder.getId());
        String contactEmail = (String) destUserProfile.getPropertyValue("email");
        destOrder.setContactEmail(contactEmail);
        orderModified = true;
      } catch (RepositoryException e) {
        if (isLoggingError()) logError("Repository Exception when updating contact email:", e);
      }

      // After the merge check if there are more than one shipping group, if so
      // remove the groups other than the first one
      List<?> sgList = destOrder.getShippingGroups();

      if (sgList.size() > 1) {
        logWarning("After merge orders the destination order ended up with more than 1 Shipping Group.  Fixing it...");
        ShippingGroup shippingGroupToPreserve = null;
        Iterator<?> sgiter = sgList.iterator();
        Map<String, Long> itemsToRestoreRels = new HashMap<String, Long>();
        Set<String> sgsToRemove = new HashSet<String>();
        while (sgiter.hasNext()) {
          ShippingGroup sg = (ShippingGroup) sgiter.next();

          // We want to take note of the commerce item relationships in all the
          // shipping groups except the last one
          if (sgiter.hasNext()) {
            // Save commerce item ids and quantities
            List<?> ciRels = sg.getCommerceItemRelationships();
            Iterator<?> relIter = ciRels.iterator();
            while (relIter.hasNext()) {
              ShippingGroupCommerceItemRelationship rel = (ShippingGroupCommerceItemRelationship) relIter.next();
              itemsToRestoreRels.put(rel.getCommerceItem().getId(), rel.getQuantity());
            }
            sgsToRemove.add(sg.getId());
          } else {
            shippingGroupToPreserve = sg;
          }
        }
        // Remove shipping groups and relationships
        for (String idToRemove : sgsToRemove) {
          vlogWarning("Will remove this Shipping Group and Relationships from the order.  We only need ONE shipping group. " + idToRemove);
          getShippingGroupManager().removeAllRelationshipsFromShippingGroup(destOrder, idToRemove);
          getShippingGroupManager().removeShippingGroupFromOrder(destOrder, idToRemove);
        }
        // assign remaining commerce items to the remaining shipping group
        for (Map.Entry<String, Long> relEntry : itemsToRestoreRels.entrySet()) {
          if (isLoggingDebug()) {
            logDebug("Adding back " + relEntry.getValue() + " of item " + relEntry.getKey() + " to shipping group " + shippingGroupToPreserve.getId());
          }
          getCommerceItemManager().addItemQuantityToShippingGroup(destOrder, relEntry.getKey(), shippingGroupToPreserve.getId(), relEntry.getValue());
        }
        orderModified = true;
      }
      
      if (orderModified) {
        updateOrder(destOrder, "Update order after cart merge");
      }
      
      Map<String, MFFInventoryMessage> invMessages = new HashMap<String, MFFInventoryMessage>();
      
      //update merged cart with available inventory
      try {
        UpdateCartForInventoryResult result = updateCartForInventory(destOrder);
        if (result != null) {
          if (result.getMessageMap() != null) {
            invMessages.putAll(result.getMessageMap());
          }
        }
      } catch (RepositoryException e) {
        vlogError(e,"There was an error while updating cart for inventory for order:{0}",destOrder.getId());
      }
      
    }
    if (cartMerged) {
      vlogDebug("Successfully put orderid into hashmap..." + destOrder.getId());
    }
  }

  /**
   * This method is called in InitCartPageDroplet to update cart for inventory when viewing a cart page.
   * @param pOrder
   * @param pRequest
   * @return
   * @throws RepositoryException
   * @throws CommerceException
   */
  public UpdateCartForInventoryResult updateCartForInventory(MFFOrderImpl pOrder) 
      throws RepositoryException, CommerceException {
    return updateCartForInventory(pOrder,INV_MSG_USE_DEFAULT_QUANTITY, INV_MSG_USE_DEFAULT_QUANTITY);
  }
  
  private int getLimitForSku(String pSkuId, boolean pIsBopisOrder) throws RepositoryException  {
	  int limit=0;
      RepositoryItem sku = getCatalogTools().findSKU(pSkuId);
      
      //vlogDebug("isStorePickup:  " + isStorePickup());
      vlogDebug("pIsBopisOrder:  " + pIsBopisOrder);
      
      if (pIsBopisOrder){
    	  if(sku != null && sku.getPropertyValue(MFFConstants.SKU_BOPIS_LIMIT_PER_ORDER) != null && ((Integer)sku.getPropertyValue(MFFConstants.SKU_BOPIS_LIMIT_PER_ORDER) > 0)) {
	    	  limit = ((Integer)sku.getPropertyValue(MFFConstants.SKU_BOPIS_LIMIT_PER_ORDER)).intValue();
	          
	    	  vlogDebug("Sku BOPIS limit per order " + limit);
	      }
    	  
      } else {
	      if(sku != null && sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER) != null && ((Integer)sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER) > 0)) {
	    	  limit = ((Integer)sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER)).intValue();
	          
	    	  vlogDebug("Sku limit per order " + limit);
	      }
      }
	  return limit;
  }  
  /**
   * Helper method to update cart for inventory
   * @param pOrder
   * @param pRequest
   * @param pNumRequested
   * @param pNumAdded
   * @return
   * @throws RepositoryException
   * @throws CommerceException
   */
  public UpdateCartForInventoryResult updateCartForInventory(MFFOrderImpl pOrder, long pNumRequested, long pNumAdded) throws RepositoryException, CommerceException {
    UpdateCartForInventoryResult result = new UpdateCartForInventoryResult();
    
    boolean cartUpdated = false;
    boolean quantityAdjusted = false;
    boolean itemRemoved = false;
    long maxGlobalQtyThresh = getEnvironment().getMaxQtyPerItemInOrder();
    int maxSKUQtyThresh = 0;
    
    @SuppressWarnings("unchecked")
    List<MFFCommerceItemImpl> ciList = pOrder.getCommerceItems();
    ListIterator<MFFCommerceItemImpl> ciIterator = ciList.listIterator();
    // Inventory Lookup
    Map<String,StockLevel> skusStockLevel = getStockLevelForSkusInOrder(pOrder);
    
    List<String> removeList = new ArrayList<String>();
    while (ciIterator.hasNext()) {
      MFFCommerceItemImpl ci = (MFFCommerceItemImpl) ciIterator.next();
      String skuId = ci.getCatalogRefId();
      String itemId = ci.getId();
      long currentQty = ci.getQuantity();
      long availableStockLevel = 0;
      maxSKUQtyThresh = getLimitForSku(skuId, pOrder.isBopisOrder());
      MFFInventoryMessage invMessage = new MFFInventoryMessage();
      invMessage.setCommerceItemId(ci.getId());
      invMessage.setSkuId(ci.getCatalogRefId());
      invMessage.setProductId(ci.getAuxiliaryData().getProductId());
      invMessage.setRemoved(false);
      invMessage.setCartQty(currentQty);
      StockLevel stockLevel = skusStockLevel.get(skuId);
      if (stockLevel!=null){
        availableStockLevel = stockLevel.getStockLevel();
      }
      
      vlogDebug("currentQty in CI:" + currentQty + " availableStockLevel :" + availableStockLevel);
      
      // This method checks for the product display rules for view cart
      if (currentQty == 0) {
        // if currentQty is zero we need to remove the item Qty
        cartUpdated = true;
        itemRemoved = true;
        vlogInfo ("Action in updateCartForInventory, will remove sku ({0}) in order ({1}) since current Qty is set as 0", ci.getCatalogRefId(),pOrder.getId()); 
        removeList.add(itemId);
      }
      else if (availableStockLevel <= 0){
        // if availableStockLevel <= zero we need to remove the item
        cartUpdated = true;
        itemRemoved = true;
        vlogInfo ("Action in updateCartForInventory, will remove sku ({0}) in order ({1}) since available stock level is 0", ci.getCatalogRefId(),pOrder.getId()); 
        removeList.add(itemId);
        Object[] msgArgs = new Object[2];// { product name, skuId,current qty };
        msgArgs[0] = ((RepositoryItem)ci.getAuxiliaryData().getProductRef()).getPropertyValue("displayName");
        msgArgs[1] = ci.getCatalogRefId();
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_REMOVED_MESSAGE));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        invMessage.setErrorMessage(msg);
        invMessage.setRemoved(true);
        invMessage.setAvailQty(0L);
        result.getMessageMap().put(itemId, invMessage);
      }
      else if (currentQty > availableStockLevel || currentQty > maxGlobalQtyThresh || (maxSKUQtyThresh > 0 && currentQty > maxSKUQtyThresh)) {
        // If inventory quantity changed we need to update the commerceitem quantity
        vlogInfo ("Action in updateCartForInventory, will change qty for sku ({0}) in order ({1}) from orderedQty ({2}) to availableStockLevel ({3})", ci.getCatalogRefId(),pOrder.getId(),currentQty,availableStockLevel); 
        // order is important here.  PurchaseProcessHelper needs quantity from the item before making the actual
        // quantity update.
        
        long updateQty = (availableStockLevel > maxGlobalQtyThresh) ? maxGlobalQtyThresh : availableStockLevel;
        
        updateQty = (maxSKUQtyThresh > 0 && updateQty > maxSKUQtyThresh) ? maxSKUQtyThresh : updateQty;
        getCommerceItemManager().adjustItemRelationshipsForQuantityChange(pOrder, ci, updateQty);
        ci.setQuantity(updateQty);
        cartUpdated = true;
        quantityAdjusted = true;
        Object[] msgArgs = new Object[3];// { product name, skuId,current qty };
        msgArgs[0] = ((RepositoryItem)ci.getAuxiliaryData().getProductRef()).getPropertyValue("displayName");
        msgArgs[1] = ci.getCatalogRefId();
        msgArgs[2] = (INV_MSG_USE_DEFAULT_QUANTITY == pNumRequested ? currentQty : pNumRequested);
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_REDUCED_MESSAGE));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        invMessage.setErrorMessage(msg);
        invMessage.setAvailQty(availableStockLevel);
        result.getMessageMap().put(itemId, invMessage);
      }
    }
    
    if (removeList!= null && !removeList.isEmpty()) {
      removeItemFromCart(removeList, pOrder);
    }
    // if any qty changed in the cart only we need to update the order
    if (cartUpdated) {
      pOrder.updateVersion();
      updateOrder(pOrder, "Updated Inventory for Cart");
    }
    
    result.setCartUpdated(cartUpdated);
    result.setItemRemoved(itemRemoved);
    result.setQuantityAdjusted(quantityAdjusted);
    return result;
  }

  /**
   * get total discount amount on the order
   * @param pOrder
   * @return
   */
  public double getTotalDiscountAmount(RepositoryItem pOrder){
    double totalDiscountAmount = 0.0d;
    //MFFOrderImpl order = (MFFOrderImpl)loadOrder(pOrder.getRepositoryId());
      totalDiscountAmount = getGlobalPromoDiscountAmount(pOrder) + getCouponDiscountAmount(pOrder);
  
    return totalDiscountAmount;
  }
  
  /**
   * get total global level promo discounts
   * 
   * @param pOrder
   * @return
   */
  public double getGlobalPromoDiscountAmount(RepositoryItem pOrder) {

    double totalDiscountAmount = 0.0d;

    /**
     * First find out Order level promotion
     */
    RepositoryItem priceInfoItem = (RepositoryItem) pOrder.getPropertyValue(MFFConstants.PROP_PRICE_INFO); 
    
	if (priceInfoItem != null) {
      List adjustments = (List) priceInfoItem.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
      if (adjustments != null) {
        Iterator adjIter = adjustments.iterator();
        while (adjIter.hasNext()) {
        	RepositoryItem pricingAdjustment = (RepositoryItem) adjIter.next();
          if (pricingAdjustment == null) {
            continue;
          }
          RepositoryItem promotion = (RepositoryItem) pricingAdjustment.getPropertyValue(MFFConstants.PROP_PRICING_MODEL);
          if (promotion != null) {
            Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);

            if (isGlobal != null && isGlobal.booleanValue()) {
              totalDiscountAmount += ((Double)pricingAdjustment.getPropertyValue(MFFConstants.PROP_TOTAL_ADJUSTMENT)).doubleValue();
            }
          }
        }
      }
    }

    // If that didn't pan out, looks at item-level promotion.
    List commerceItems = (List) pOrder.getPropertyValue(MFFConstants.PROP_COMMERCE_ITEMS);
    if (commerceItems != null) {
      Iterator ciIter = commerceItems.iterator();
      while (ciIter.hasNext()) {
    	RepositoryItem ci = (RepositoryItem) ciIter.next();
    	// 2414 - Dont display GWP promos in the pay stack. They are already priced in the cart
    	if((Boolean)ci.getPropertyValue("gwp")) {
    		continue;
    	}
        RepositoryItem priceInfo = (RepositoryItem) ci.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
        
        if (priceInfo != null) {
          List adjustments = (List) priceInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
          if (adjustments != null) {
            Iterator adjIter = adjustments.iterator();

            while (adjIter.hasNext()) {
            	RepositoryItem pricingAdjustment = (RepositoryItem) adjIter.next();
              if (pricingAdjustment == null) {
                continue;
              }

              RepositoryItem promotion = (RepositoryItem) pricingAdjustment.getPropertyValue(MFFConstants.PROP_PRICING_MODEL);

              if (promotion != null) {
                Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);

                if (isGlobal != null && isGlobal.booleanValue()) {
                  totalDiscountAmount += ((Double)pricingAdjustment.getPropertyValue(MFFConstants.PROP_TOTAL_ADJUSTMENT)).doubleValue();
                }
              }
            }
          }
        }
      }
    }
    return totalDiscountAmount;
  }

  /**
   * get coupn level applied discounts on the order
   * 
   * @param order
   * @return
   */
  public double getCouponDiscountAmount(RepositoryItem pOrder) {

    Map<String, String> orderAppliedPromotions = new HashMap<String, String>();
    double itemDiscountAmount = 0.0d;
    double orderDiscountAmount = 0.0d;
    double shippingDiscountAmount = 0.0d;

    try {
      /**
       * First find out Order level promotion
       */
    	RepositoryItem priceInfoItem = (RepositoryItem) pOrder.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
    	
      if (priceInfoItem != null) {
        List adjustments = (List) priceInfoItem.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
        if (adjustments != null) {
          getPromotionFromAdjustment(adjustments, orderAppliedPromotions);
        }
      }

      List sgList = (List) pOrder.getPropertyValue(MFFConstants.PROP_SHIPPING_GROUPS);
      if (sgList.size() >= 1) {
        Iterator sgiter = sgList.iterator();
        while (sgiter.hasNext()) {
          RepositoryItem sg = (RepositoryItem) sgiter.next();
          RepositoryItem shipPricingInfo = (RepositoryItem) sg.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
          if (shipPricingInfo != null) {
        	  List adjustments = (List) shipPricingInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
            if (adjustments != null) {
              getPromotionFromAdjustment(adjustments, orderAppliedPromotions);
            }
          }
        }
      }
      
      List commerceItems = (List) pOrder.getPropertyValue(MFFConstants.PROP_COMMERCE_ITEMS);
      if (commerceItems != null) {
        Iterator ciIter = commerceItems.iterator();
        while (ciIter.hasNext()) {
        	RepositoryItem ci = (RepositoryItem) ciIter.next();
          RepositoryItem priceInfo = (RepositoryItem) ci.getPropertyValue(MFFConstants.PROP_PRICE_INFO);

          if (priceInfo != null) {

            List adjustments = (List) priceInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
            if (adjustments != null) {
              getPromotionFromAdjustment(adjustments, orderAppliedPromotions);
            }
          }
        }
      }

      // applyRemainingActivePromotions(activePromotions,
      // orderAppliedPromotions);
      itemDiscountAmount = calculateCouponItemDiscountAdjustments(commerceItems);
      orderDiscountAmount = calculateCouponOrderDiscountAdjustments(pOrder);
      shippingDiscountAmount = calculateCouponShippingDiscountAdjustments(sgList);
      
    } catch (ClaimableException e) {
      if (isLoggingError()) {
        logError("Some error in fetching coupons applied.", e);
      }
    }
    
    //As per the bug 2277, combo promos can be applied through coupon, so summing up all the discounts
    return -(itemDiscountAmount + orderDiscountAmount + shippingDiscountAmount);
  }

  /**
   * Gets the promotion from adjustment.
   *
   * @param adjustments
   *          the adjustments
   * @param orderAppliedPromotions
   *          the order applied promotions
   * @return the promotion from adjustment
   * @throws ClaimableException
   *           the claimable exception
   */
  @SuppressWarnings("rawtypes")
  private void getPromotionFromAdjustment(List<?> adjustments, Map<String, String> orderAppliedPromotions) throws ClaimableException {

    RepositoryItem[] couponItems = null;
    Iterator adjIter = adjustments.iterator();

    while (adjIter.hasNext()) {
    	RepositoryItem pricingAdjustment = (RepositoryItem) adjIter.next();
      if (pricingAdjustment == null) {
        continue;
      }
      
      RepositoryItem promotion = (RepositoryItem) pricingAdjustment.getPropertyValue(MFFConstants.PROP_PRICING_MODEL);

      if (promotion != null) {
        Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
        if (isGlobal != null && isGlobal.booleanValue()) continue;

        String description = (String) promotion.getPropertyValue(DESCRIPTION);

        if (StringUtils.isEmpty(description)) {
          description = promotion.getRepositoryId();
        }
        couponItems = getClaimableTools().getCouponsForPromotion(promotion.getRepositoryId());

        if (couponItems != null) {
          orderAppliedPromotions.put(couponItems[0].getRepositoryId(), description);
        } else {
          orderAppliedPromotions.put(promotion.getRepositoryId(), description);
        }

      }
    }

  }

  /**
   * <p>
   * Method to handle calculation of Item Discount by coupon.
   *
   * @param commerceItems
   *          the commerce items
   * @return The discount amount by coupon id.
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponItemDiscountAdjustments(List commerceItems) {
    double discountAdjustment = 0.0d;
    if (commerceItems != null && commerceItems.size() > 0) {
      Iterator iter = commerceItems.iterator();
      while (iter.hasNext()) {
        RepositoryItem pItem = (RepositoryItem) iter.next();
        RepositoryItem priceInfo = (RepositoryItem) pItem.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
        if (priceInfo != null) {
          List adjustments = (List) priceInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
          if (adjustments != null && adjustments.size() > 0) {
            discountAdjustment = getDiscountedAmount(adjustments);
          }
        }
      }
    }
    return discountAdjustment;
  }

  /**
   * Calculate coupon shipping discount adjustments.
   *
   * @param sgList
   *          the sg list
   * @return the double
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponShippingDiscountAdjustments(List<?> sgList) {
    double discountAdjustment = 0.0d;
    if (sgList.size() >= 1) {
      Iterator<?> sgiter = sgList.iterator();
      while (sgiter.hasNext()) {
        RepositoryItem sg = (RepositoryItem) sgiter.next();
        RepositoryItem shipPricingInfo = (RepositoryItem) sg.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
        if (shipPricingInfo != null) {
          List adjustments = (List) shipPricingInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
          if (adjustments != null && adjustments.size() > 0) {
            discountAdjustment = getDiscountedAmount(adjustments);
          }
        }
      }
    }
    return discountAdjustment;
  }

  /**
   * <p>
   * Method to handle calculation of Item Discount by coupon.
   *
   * @param order
   *          the order
   * @return The discount amount by coupon id.
   */
  @SuppressWarnings("rawtypes")
  public double calculateCouponOrderDiscountAdjustments(RepositoryItem pOrder) {
    double discountAdjustment = 0.0d;

    if (pOrder != null) {
    	RepositoryItem orderPriceInfo = (RepositoryItem) pOrder.getPropertyValue(MFFConstants.PROP_PRICE_INFO);
      List orderAdjustments = (List) orderPriceInfo.getPropertyValue(MFFConstants.PROP_ADJUSTMENTS);
      if (orderAdjustments != null && orderAdjustments.size() > 0) {
        discountAdjustment = getDiscountedAmount(orderAdjustments);
      }
    }
    return discountAdjustment;
  }

  /**
   * Gets the discounted amount.
   *
   * @param adjustments
   *          the adjustments
   * @return the discounted amount
   */
  @SuppressWarnings("rawtypes")
  public double getDiscountedAmount(List<?> adjustments) {
    double discountAdjustment = 0.0;
    if (adjustments != null) {
      Iterator itAdj = adjustments.iterator();
      RepositoryItem adj = null;
      RepositoryItem promotion = null;

      while (itAdj.hasNext()) {
        adj = (RepositoryItem) itAdj.next();
        promotion = (RepositoryItem) adj.getPropertyValue(MFFConstants.PROP_PRICING_MODEL);

        if (promotion != null) {

          Boolean isGlobal = (Boolean) promotion.getPropertyValue(GLOBAL);
          if (isGlobal != null && isGlobal.booleanValue()) {
            continue;
          }

          double adjustedAmount = (double) adj.getPropertyValue(MFFConstants.PROP_TOTAL_ADJUSTMENT);
          if (adjustedAmount > 0.0) {
            discountAdjustment += adjustedAmount;
          } else {
            discountAdjustment += (adjustedAmount * -1);
          }

        }
      }
    }
    return discountAdjustment;
  }
   
  /**
   * Value Object to hold inventory result
   *
   */
  public static class UpdateCartForInventoryResult {
    private boolean mCartUpdated;
    private boolean mQuantityAdjusted;
    private boolean mItemRemoved;
    private final Map<String, MFFInventoryMessage> mMessageMap = new HashMap<String, MFFInventoryMessage>();
    public boolean isCartUpdated() {
      return mCartUpdated;
    }
    public void setCartUpdated(boolean pCartUpdated) {
      mCartUpdated = pCartUpdated;
    }
    
    public boolean isQuantityAdjusted() {
      return mQuantityAdjusted;
    }
    public void setQuantityAdjusted(boolean pQuantityAdjusted) {
      mQuantityAdjusted = pQuantityAdjusted;
    }
    
    public boolean isItemRemoved() {
      return mItemRemoved;
    }
    public void setItemRemoved(boolean pItemRemoved) {
      mItemRemoved = pItemRemoved;
    }
    
    public Map<String, MFFInventoryMessage> getMessageMap() {
      return mMessageMap;
    }
  }
  
  /**
   * Helper method to remove items from cart
   * 
   * @param pRemoveList
   * @param pOrder
   * @throws CommerceException
   */
  private void removeItemsFormOrder(Order pOrder) throws CommerceException {
    vlogInfo("Action removeItemFromCart, for order ({0})", pOrder.getId());

    if (pOrder.getCommerceItemCount() > 0) {
      List<CommerceItem> lItems = pOrder.getCommerceItems();
      for (int i = 0; i < lItems.size(); i++) {
        CommerceItem lItem = lItems.get(i);
        if (lItem != null) {
          vlogDebug("Removing Item:{0} from the order:{1}", lItem.getId(), pOrder.getId());
          getCommerceItemManager().removeItemFromOrder(pOrder, lItem.getId());
        }
      }
    }
  }
  
  /**
   * Helper method to remove items from cart
   * @param pRemoveList
   * @param pOrder
   * @throws CommerceException 
   */
  private void removeItemFromCart(List<String> pRemoveList, Order pOrder) throws CommerceException {
    if (isLoggingInfo()) {
      vlogInfo ("Action removeItemFromCart, for order ({0})", pOrder.getId()); 
    }
  
    @SuppressWarnings("rawtypes")
    ListIterator itemIterator = pRemoveList.listIterator();
    while (itemIterator.hasNext()) {
      String ciId = (String) itemIterator.next();
      getCommerceItemManager().removeItemFromOrder(pOrder, ciId);
    }
  }

  /**
   * update bopis info on the order so that order will set to ship to Home
   * 
   * @param pOrder
   * @throws CommerceException
   */
  public void shipMyOrderInstead(Order pOrder) throws CommerceException {
    if (pOrder == null) {
      vlogError("No Order found to update shipMyOrder status");
      throw new CommerceException("There is no Order to update shipMyOrder status");
    }
    updateBopisOrderInfo(pOrder, false, null);
  }

  /**
   * update ffl flag on the order so that order will will be defaulted to
   * regular cart
   * 
   * @param pOrder
   * @throws CommerceException
   */
  public void updateFFLOrder(Order pOrder) throws CommerceException {
    if (pOrder == null) {
      vlogError("No Order found to update ffl Order status");
      throw new CommerceException("There is no Order to update ffl Order status");
    }
    updateFFLOrderInfo(pOrder, false);
  }

  /**
   * Helper method to update the bopis information on the order
   * 
   * @param pOrder
   * @param pBopisOrder
   * @param pBopisStore
   * @param pRequest
   * @param pResponse
   * @throws CommerceException
   */
  public void updateBopisOrderInfo(Order pOrder, boolean pBopisOrder, String pBopisStore) throws CommerceException {

    vlogDebug("Entering updateBopisOrderInfo");
    vlogInfo("Update Bopis Info for order:{0}, with isBopisOrder:{1}, bopisStore:{2}", pOrder.getId(), pBopisOrder, pBopisStore);
    // update bopis info
    synchronized (pOrder) {
      MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
      lOrder.setBopisOrder(pBopisOrder);
      lOrder.setBopisStore(pBopisStore);
      
      // if bopis order is false reset the pick up person info
      // and remove any instore only items from the order
      if (!pBopisOrder) {
        // Loop through the items and add instore only items to removal list,
        // as we are about to convert the order to shipToHome
        if (pOrder.getCommerceItemCount() > 0) {
          List<CommerceItem> lItems = pOrder.getCommerceItems();
          for (int i = lItems.size() - 1; i >= 0; i--) {
            CommerceItem lItem = lItems.get(i);
            if (lItem != null) {
              String prodId = lItem.getAuxiliaryData().getProductId();
              
              //is product available to purchase online?
              boolean webOnly = ((MFFCatalogTools) getCatalogTools()).isProductAvailableOnline(prodId);
              
              //is product available only to purchase online and pick up in store?
              boolean bopisOnly = ((MFFCatalogTools) getCatalogTools()).isBopisOnlyProduct(prodId);
              
              vlogDebug("In updateBopisOrderInfo: productId:{0} and webOnly:{1}", prodId, webOnly);
              // if webonly item or bopis only item remove it as these items can not be shipped
              if (!webOnly || bopisOnly) {
                vlogDebug("Removing Item:{0} from the order:{1} as it is in store only", lItem.getId(), pOrder.getId());
                getCommerceItemManager().removeItemFromOrder(pOrder, lItem.getId());
              }
            }
          }
        }
        
        //change shipping method to standard(default) as we are converting order to shipToHome
        ((MFFHardgoodShippingGroup)lOrder.getShippingGroups().get(0)).setShippingMethod("Standard");
        lOrder.setBopisPerson(null);
        lOrder.setBopisEmail(null);
      }
      vlogDebug("Updating Order:{0} with Bopis Info bopisOrder:{1}, bopisStore:{2} and Order Version is:{3}", lOrder.getOrderNumber(), pBopisOrder, pBopisStore, lOrder.getVersion());
      updateOrder(lOrder, "Called by updateBopisOrderInfo");
      vlogDebug("Updated Order:{0} with Bopis Info bopisOrder:{1}, bopisStore:{2} and Order Version is:{3}", lOrder.getOrderNumber(), pBopisOrder, pBopisStore, lOrder.getVersion());
    } // synchronized

    vlogDebug("Exiting updateBopisOrderInfo");
  }

  /**
   * Helper method to update the ffl information on the order
   * 
   * @param pOrder
   * @param pFFLOrder
   * @param pRequest
   * @param pResponse
   * @throws CommerceException
   */
  public void updateFFLOrderInfo(Order pOrder, boolean pFFLOrder) throws CommerceException {

    // update ffl info
    synchronized (pOrder) {
      MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
      lOrder.setFFLOrder(pFFLOrder);
      ;
      vlogDebug("Updating Order:{0} with Bopis Info bopisOrder:{1}, and Order Version is:{2}", lOrder.getOrderNumber(), pFFLOrder, lOrder.getVersion());
      updateOrder(lOrder, "Called by updateFFLOrderInfo");
      vlogDebug("Updated Order:{0} with Bopis Info bopisOrder:{1},  and Order Version is:{2}", lOrder.getOrderNumber(), pFFLOrder, lOrder.getVersion());
    } // synchronized
  }

  public void updateOrder(Order pOrder, String pCallingLoc) throws CommerceException {
    if (isLoggingDebug()) {
      logDebug("\n\n#####################################################################################################");
      logDebug("    Start of MFFOrderManager.updateOrder from:" + pCallingLoc + ". ORDER:" + pOrder.getId());
      logDebug("#####################################################################################################\n");
    }

    displayOrderVersionInfo(pOrder, "In updateOrder (before) called from:" + pCallingLoc);

    updateOrder(pOrder);

    displayOrderVersionInfo(pOrder, "In updateOrder (after) called from:" + pCallingLoc);

    if (isLoggingDebug()) {
      logDebug("\n\n#####################################################################################################");
      logDebug("    End of MFFOrderManager.updateOrder from:" + pCallingLoc + ". ORDER:" + pOrder.getId());
      logDebug("#####################################################################################################\n");
    }
  }
  
  /**
   * The following method is used to determine if the order contains a GWP GC
   * 
   * @param order
   * @return <code>true</code> if the order contains at least one GWP GC
   */
  @SuppressWarnings("unchecked")
  public boolean orderContainsGWPGC(Order order) {

    vlogDebug("Entering orderContainsGWPGC : order");
    
    vlogDebug("Determining if the order id {0} contains a GWP GC", order.getId());
    
    boolean retvalue = false;
    MFFCatalogTools catalogTools = (MFFCatalogTools) getCatalogTools();
    List<CommerceItem> commerceItems = order.getCommerceItems();
    List<String> giftCardProductIds = catalogTools.getGiftCardProductIds();

    if (commerceItems != null && giftCardProductIds != null) {
      for (CommerceItem item : commerceItems) {
        String productId = item.getAuxiliaryData().getProductId();

        if (giftCardProductIds.contains(productId)) {
          vlogDebug("Order contains the following product that matches the GWP GCs {0}", productId);
          retvalue = true;
          break;
        }
      }
    }
    
    if (isLoggingDebug()) vlogDebug("gift card value set to {0}", retvalue);
    
    vlogDebug("Exiting orderContainsGWPGC : order");
    return retvalue;
  }

  // ---------------------------------------------
  /*
   * This helper method should be called if you get OrderVersionExceptions. It
   * helps you see when the Version number in the Order Wrapper Java class gets
   * off track with the version in the Order Repository item. The exceptions
   * don't always occur exactly where the error really is. They might occur a
   * bit later in the code. If you get those exceptions, add calls to this
   * method to detect when the version numbers get off course. When the order is
   * loaded from the Repository, an Order Wrapper Java class is created. At this
   * time, the version from the Repository and the version in Order Wrapper Java
   * class match. Each time the order is updated those version numbers are
   * "synchronized" together. If the values stop being synchronized, it means
   * your code is buggy - the Order in the repository was updated to a new
   * version, but the Order Wrapper wasn't told. Check your code and make sure
   * you handle your Transactions correctly, and you call UpdateOrder after all
   * updates to any order related entity (Order, commerce items, payment groups,
   * shipping group, relationshipts, etc).
   */
  // ---------------------------------------------
  public void displayOrderVersionInfo(Order pOrder, String pCallingLocation) {
    if (isLoggingDebug()) {
      OrderImpl orderJavaWrapper = (OrderImpl) pOrder;
      int orderJavaWrapperVersionNum = orderJavaWrapper.getVersion();

      MutableRepositoryItem orderRepItem = orderJavaWrapper.getRepositoryItem();
      int orderRepItemVersionNum = 0;

      if (orderRepItem == null) {
        if (isLoggingDebug()) {
          logDebug("###OrderManager." + pCallingLocation + "Order Java Wrapper:" + orderJavaWrapperVersionNum);
          logDebug("###OrderManager." + pCallingLocation + "Order Rep Item    : NULL");
        }
      } else {
        Integer orderRepItemVersionNumObj = (Integer) orderRepItem.getPropertyValue(PropertyNameConstants.VERSION);

        if (orderRepItemVersionNumObj == null) {
          if (isLoggingDebug()) {
            logDebug("###OrderManager." + pCallingLocation + "Order Java Wrapper:" + orderJavaWrapperVersionNum);
            logDebug("###OrderManager." + pCallingLocation + "Order Rep Item    : NULL");
          }
        } else {
          orderRepItemVersionNum = orderRepItemVersionNumObj.intValue();

          // If the numbers are equal, all is good. Display with a logDebug.
          if (orderJavaWrapperVersionNum == orderRepItemVersionNum) {
            if (isLoggingDebug()) {
              logDebug("###OrderManager." + pCallingLocation + "Order Java Wrapper:" + orderJavaWrapperVersionNum);
              logDebug("###OrderManager." + pCallingLocation + "Order Rep Item    :" + orderRepItemVersionNum);
            }
          }

          // If the numbers are NOT equal, we have a problem. Display with a
          // logError to raise a flag.
          else {
            if (isLoggingError()) {
              logError("###OrderManager." + pCallingLocation + "Order Java Wrapper:" + orderJavaWrapperVersionNum);
              logError("###OrderManager." + pCallingLocation + "Order Rep Item    :" + orderRepItemVersionNum);
            }
          }
        }
      }
    }
  }

  public void updateOrder(Order pOrder) throws CommerceException {

    if (pOrder == null && isLoggingError()) {
      logError("Why is updateOrder called with null order?");
    }

    if (isTransactionMarkedAsRollBack() && isLoggingDebug()) {
      logDebug("The order is marked for rollback! OOTB updateOrder will not do anything...");
    }

    super.updateOrder(pOrder);
  }

  /**
   * Helper method to list the skus in order
   * 
   * @param pOrder
   * @return
   */
  public List<String> getSkuIdsInOrder(OrderImpl pOrder) {
    @SuppressWarnings("unchecked")
    List<CommerceItem> ciList = pOrder.getCommerceItems();
    List<String> skuIds = new ArrayList<String>();
    for (CommerceItem ci : ciList) {
    		skuIds.add(ci.getCatalogRefId());
    }
    return skuIds;
  }

  /**
   * Helper method to get stock level for skus in order
   * 
   * @param pOrder
   * @return
   */
  @SuppressWarnings("unchecked")
  public Map<String, StockLevel> getStockLevelForSkusInOrder(OrderImpl pOrder) {
    List<String> skuIds = getSkuIdsInOrder(pOrder);
    List<MFFCommerceItemImpl> ciList = pOrder.getCommerceItems();
    // Get the inventory call for all the sku's in the order
    Map<String, StockLevel> skusStockLevel = new HashMap<String, StockLevel>();
    if (skuIds != null && skuIds.size() > 0) {
      try {
        if(((MFFOrderImpl)pOrder).isBopisOrder()){
        	//if (getStoreLocatorTools().isBOPISOnlyStore(((MFFOrderImpl)pOrder).getBopisStore())) { 
        	//	skusStockLevel = getInventoryManager().queryStoreAvailability(skuIds,((MFFOrderImpl)pOrder).getBopisStore());
        	//} else {
        		if(getInventoryManager().isEnableBopisInventoryFix() && !getStoreLocatorTools().isBOPISOnlyStore(((MFFOrderImpl)pOrder).getBopisStore())){
        			Map <String, StockLevel> invMap = getInventoryManager().querySkusStockLevel(skuIds, ((MFFOrderImpl)pOrder).isBopisOrder());
        			if(invMap != null && invMap.size() > 0){
        				for (MFFCommerceItemImpl ci : ciList) {
        					StockLevel stockLevel = invMap.get(ci.getCatalogRefId()); 
        					if(stockLevel != null && stockLevel.getStockLevel() > 0  && stockLevel.getStockLevel() >= ci.getQuantity() ){
        						// all good continue
        					}else{
        						if(stockLevel != null)
        							vlogInfo("getStockLevelForSkusInOrder : Inventory in mff_inventory is 0 for skuId {0}", stockLevel.getSkuId());
        						else
        							vlogInfo("Stock level is null for " + ci.getCatalogRefId());
        						// return inventory map to indicate the main inventory is invalid
        						
        						if(!ci.isGiftCard())
        							return  invMap;
        					}
        				}
        			} 
        		}
        	//}
          skusStockLevel = getInventoryManager().queryStoreAvailability(skuIds,((MFFOrderImpl)pOrder).getBopisStore());
        }else{
          skusStockLevel = getInventoryManager().querySkusStockLevel(skuIds,((MFFOrderImpl)pOrder).isBopisOrder());
        }
        
       
        for (MFFCommerceItemImpl ci : ciList) {
          if(ci.isGiftCard()){
            StockLevel stockLevel =skusStockLevel.get(ci.getCatalogRefId());
            //if there is no inventory record found, create stockLevel object and set the available stock level to requested qty.
            if(stockLevel == null){
              StockLevel gcStockLevel = new StockLevel();
              gcStockLevel.setSkuId(ci.getCatalogRefId());
              gcStockLevel.setStockLevel(ci.getQuantity());
              skusStockLevel.put(ci.getCatalogRefId(), gcStockLevel);
            }else{
              stockLevel.setStockLevel(ci.getQuantity());
            }
          }
        }
        
      } catch (InventoryException e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    }
    return skusStockLevel;
  }

  /**
   * This method is used to check LegacyOrder exists or not
   * 
   * @param pOrderId
   * @return
   * @throws CommerceException
   */
  public boolean legacyOrderExists(String pOrderId) throws CommerceException {
    if (pOrderId == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderIdParameter", "atg.commerce.order.OrderResources", sResourceBundle));
    }

    Repository rep = getLegacyOrderRepository();
    try {
      return (rep.getItem(pOrderId, getOrderItemDescriptorName()) != null);
    } catch (RepositoryException e) {
      throw new CommerceException(e);
    }
  }

  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  public MFFProfileTools getProfileTools() {
    return mProfileTools;
  }

  public void setProfileTools(MFFProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  public MFFPurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

  public void setPurchaseProcessHelper(MFFPurchaseProcessHelper pPurchaseProcessHelper) {
    mPurchaseProcessHelper = pPurchaseProcessHelper;
  }
  
  /**
   * Sets the claimable tools.
   *
   * @param pClaimableTools
   *            the new claimable tools
   */
  public void setClaimableTools(ClaimableTools pClaimableTools) {
    mClaimableTools = pClaimableTools;
  }

  /**
   * Gets the claimable tools.
   *
   * @return the claimable tools
   */
  public ClaimableTools getClaimableTools() {
    return mClaimableTools;
  }

}
