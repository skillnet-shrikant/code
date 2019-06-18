package com.mff.commerce.order;

import java.util.List;

import com.mff.commerce.catalog.MFFCatalogTools;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.pricing.ItemPriceInfo;
import atg.repository.RepositoryItem;

public class MFFCommerceItemManager extends CommerceItemManager {

  public static final Integer ASR_21 = 21;
  

  
  
  

  /**
   * Override this method to set custom properties on the commerce item
   */
  @Override
  public CommerceItem createCommerceItem(String pItemType, String pCatalogRefId, Object pCatalogRef, String pProductId, Object pProductRef, long pQuantity, double pQuantityWithFraction, String pCatalogKey, String pCatalogId, String pSiteId, ItemPriceInfo pPriceInfo) throws CommerceException {

    CommerceItem ci = super.createCommerceItem(pItemType, pCatalogRefId, pCatalogRef, pProductId, pProductRef, pQuantity, pQuantityWithFraction, pCatalogKey, pCatalogId, pSiteId, pPriceInfo);

    MFFCatalogTools ctools = (MFFCatalogTools) getOrderTools().getCatalogTools();
    boolean isGiftCard = ctools.isGCProduct(pProductId);
    // Retrieve minimum age of a SKU
    Integer minAge = ctools.getMinimumAge(pCatalogRefId);

    Boolean isFFL = Boolean.FALSE;
    Boolean isDropShip = Boolean.FALSE;
    if (ci != null && ci.getAuxiliaryData() != null && ci.getAuxiliaryData().getProductRef() != null) {
      RepositoryItem product = (RepositoryItem) ci.getAuxiliaryData().getProductRef();
      isFFL = ctools.isFFLProduct(product);
    } else {
      isFFL = ctools.isFFLProduct(pProductId);
    }

    if (ci.getAuxiliaryData() != null && ci.getAuxiliaryData().getCatalogRef() != null) {
      RepositoryItem sku = (RepositoryItem) ci.getAuxiliaryData().getCatalogRef();
      isDropShip = ctools.isDropShipSku(sku);
    } else {
      isDropShip = ctools.isDropShipSku(pCatalogRefId);
    }

    // set custom properties on the commerce item
    MFFCommerceItemImpl mffCi = (MFFCommerceItemImpl) ci;

    // if minimum age is not set for an ffl item, default the minimum age to 21
    if (isFFL && minAge == null)
      mffCi.setMinimumAge(ASR_21);
    else
      mffCi.setMinimumAge(minAge);

    mffCi.setFFL(isFFL);
    mffCi.setDropShip(isDropShip);
    mffCi.setGiftCard(isGiftCard);

    vlogDebug("Item created with ID:{0} and Minimum Age is : {1}, isFFL : {2}, isDropShip : {3}", ci.getId(), minAge, isFFL, isDropShip);
    return mffCi;
  }

  /**
   * Override this method to create separate line items for required product
   * types
   */
  @Override
  protected boolean shouldMergeItems(CommerceItem pExistingItem, CommerceItem pNewItem) {
    if (pExistingItem.getClass() != pNewItem.getClass()) {
      return false;
    }

    String existing_id = pExistingItem.getAuxiliaryData().getProductId();
    String new_id = pNewItem.getAuxiliaryData().getProductId();
    if (existing_id == null) {
      if (new_id != null) {
        return false;
      }
    } else if (!(existing_id.equals(new_id))) {
      return false;
    }
    MFFCatalogTools ctools = (MFFCatalogTools) getOrderTools().getCatalogTools();
    boolean isGCProduct = ctools.isGCProduct(existing_id);
    if (isGCProduct) {
      return false;
    } else {
      return super.shouldMergeItems(pExistingItem, pNewItem);
    }
  }

  /**
   * Override to capture the gift card denomination
   */  
  @SuppressWarnings("unchecked")
  @Override
  protected CommerceItem mergeOrdersCopyCommerceItem(Order pSrcOrder, Order pDstOrder, CommerceItem pItem) throws CommerceException {
    
    CommerceItem item = super.mergeOrdersCopyCommerceItem(pSrcOrder, pDstOrder, pItem);
    
    if (isLoggingDebug()) {
      logDebug("Source commerce items --------------------");
      List<MFFCommerceItemImpl> commerceItems = pSrcOrder.getCommerceItems();
      for (MFFCommerceItemImpl cmrcItm : commerceItems){
        logDebug(cmrcItm.toString());
      }
      logDebug("Destination commerce items ++++++++++++++++++++");
      List<MFFCommerceItemImpl> dstCommerceItems = pDstOrder.getCommerceItems();
      for (MFFCommerceItemImpl cmrcItm : dstCommerceItems){
        logDebug(cmrcItm.toString());
      }
    }
    MFFCommerceItemImpl oldItem = (MFFCommerceItemImpl) pItem;
    MFFCommerceItemImpl copiedItem = (MFFCommerceItemImpl) item;
    double lGiftCardDenomination = oldItem.getGiftCardDenomination();
    
    vlogDebug("Old item gift card denomination = {0}", lGiftCardDenomination);
    
    if (lGiftCardDenomination > 0.0d) {
      
      copiedItem.setGiftCardDenomination(lGiftCardDenomination);
      vlogDebug("Setting the new gift card denom to {0} for commerce item id {1}", copiedItem.getGiftCardDenomination(), copiedItem.getId());
      
      //setting the price info similar to the way we do it in cart modifier form handler
      ItemPriceInfo itemPriceInfo = copiedItem.getPriceInfo();
      itemPriceInfo.setAmountIsFinal(true);
      itemPriceInfo.setAmount(lGiftCardDenomination);
      itemPriceInfo.setListPrice(lGiftCardDenomination);
      itemPriceInfo.setSalePrice(lGiftCardDenomination);
      itemPriceInfo.setRawTotalPrice(lGiftCardDenomination);
      itemPriceInfo.setCurrencyCode(getOrderTools().getProfileTools().getPricingTools().getCurrencyCode(pSrcOrder, null, null));
      
    }
    return item;
  }

}
