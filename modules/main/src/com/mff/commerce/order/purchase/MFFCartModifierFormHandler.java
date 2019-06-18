package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.gifts.MFFGiftlistFormHandler;
import com.mff.commerce.gifts.MFFGiftlistTools;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;
import com.mff.droplet.InlineFormErrorSupport;
import com.mff.droplet.MFFInlineDropletFormException;
import com.mff.locator.StoreLocatorTools;
import com.mff.util.MFFUtils;

import atg.adapter.gsa.ChangeAwareSet;
import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.promotion.PromotionException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.NumberUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import mff.MFFEnvironment;
import mff.util.DateUtil;

/**
 * Extension of OOTB CartModifierFormHandler
 */
public class MFFCartModifierFormHandler extends CartModifierFormHandler implements InlineFormErrorSupport {
	
	protected static ResourceBundle sResourceBundle = LayeredResourceBundle
			.getBundle("atg.commerce.gifts.GiftlistResources",
					LangLicense.getLicensedDefault());

  private MFFCatalogTools mCatalogTools;
  private MFFCheckoutManager mCheckoutManager;
  private FFRepositoryInventoryManager mInventoryManager;
  private MFFGiftlistFormHandler giftListFormHandler;
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private MFFGiftlistTools mGiftlistTools;
  private StoreLocatorTools mStoreLocatorTools;
  
  // Properties
  private String mProdType;
  private HashMap<String, Long> mMaxQuantities;

  private MFFEnvironment mEnvironment;
  private boolean mAddFromWishList;
  private String mAddItemToGiftlistSuccessURL;
  private String mAddItemToGiftlistErrorURL;
  private String mWishListId;

  private boolean mStorePickup;
  private String mBopisStore;
  private String mChooseStoreSuccessURL;
  private String mChooseStoreErrorURL;
  private String mShipMyOrderSuccessURL;
  private String mShipMyOrderErrorURL;
  private String mTaxExemptionSuccessURL;
  private String mTaxExemptionErrorURL;

  private boolean mFromProduct;
  private double mGiftCardMaxDenommination;
  private double mGiftCardMinDenommination;
  private double mMaxGifCardsValueInCart;

  private String mTaxExempSelected;

  private boolean mFromCheckout;
  private String mCouponCode;
  private String mAppliedCouponDescription;
  private String mApplyCouponSuccessURL;
  private String mApplyCouponErrorURL;

  private String mRemoveCouponCode;
  private String mRemoveCouponSuccessURL;
  private String mRemoveCouponErrorURL;
  private String mEditCartItem;
  private Map<String, Object> mEditValue = new HashMap<String, Object>();
  private String mAddActionType;
  
  private boolean editMode;


  public boolean isEditMode() {
	  return editMode;
  }

  public void setEditMode(boolean pEditMode) {
	  editMode = pEditMode;
  }
/**
   * Validate skus and products data
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  public void preAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    resetMaxQuantities();

    if (getProdType() != null && getProdType().equalsIgnoreCase("table")) {

      vlogDebug("preAddItemToOrder: Adding Multiple Items to the cart");

      if (getItems() != null && getItems().length > 0) {
        // setProduct Id here
        String lProdId = getItems()[0].getProductId();
        if (lProdId == null) {
          vlogError("preAddItemTOOrder: No ProductId(s) were specified in AddToCart");
          getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_MISSING, true, this, pRequest);
          return;
        }

        if (!validateItemCounts(pRequest, pResponse, true, false)) return;

        vlogDebug("preAddItemToOrder:Add Product:{0} to cart - orderId:{0}", getProductId(), getOrder().getId());

        List<AddCommerceItemInfo> filteredItems = new ArrayList<AddCommerceItemInfo>();

        // filter out skus here
        for (AddCommerceItemInfo lItem : getItems()) {
          if (lItem != null) {
            String lSkuId = lItem.getCatalogRefId();
            long quantity = lItem.getQuantity();
            if (quantity > 0) {
              vlogDebug("preAddItemTOOrder: validateMultiAdd: order id ({0}) ,profile id ({1}), product id ({2}), sku id ({3}), quantity ({4})", getOrder().getId(), getProfile().getRepositoryId(), lProdId, lSkuId, quantity);
              boolean validSku = validateMultiAdd(lProdId, lSkuId, quantity, pRequest, pResponse);
              if (validSku) {
                vlogDebug("preAddItemToOrder: validateMultiAdd: valid sku: {0}", lSkuId);
                filteredItems.add(lItem);
              } else {
                vlogError("Unable to add skuId:{0} to the cart with quantity:{1} - orderId:{2}", lSkuId, quantity, getOrder().getId());
              }
            }
          }
        }

        if (filteredItems.size() != getOptedItemCount(getItems())) getCheckoutManager().setCartMessage("Unable to add all your selections, Please review your cart!");

        setAddItemCount(filteredItems.size());

        int i = 0;
        for (AddCommerceItemInfo lItem : filteredItems) {
          getItems()[i] = lItem;
          i++;
        }
        if (getItems() != null){
        	vlogDebug("filtered items length:{0}", getItems().length);
        }
      }

      // perform FFL validation if there are any items in the cart already
      if (getOrder() != null && getOrder().getCommerceItemCount() > 0 && !isStorePickup() && getItems() != null) {
        validateFFL(getItems()[0].getProductId(), pRequest, pResponse);
      }

    } else {
      vlogDebug("preAddItemToOrder: Adding Single Item to the cart - order:{0}", getOrder().getId());
      // validate sku & product data
      
      // In the edit flow, these validations are already performed.
      // (preRemoveAndAddItemsToOrder is called first and then preAddItemToOrder is called.)
      if(!isEditMode()) {
    	  validateSkusAndProduct(pRequest, pResponse);
      }

      if (getFormError()) {
        vlogError("Form errors detected in preAddItemToOrder - order:{0}", getOrder().getId());
        return;
      }

      // perform FFL validation if there are any items in the cart already
      if (getOrder() != null && getOrder().getCommerceItemCount() > 0 && !isStorePickup() && getItems() != null) {
        validateFFL(getProductId(), pRequest, pResponse);
      }

      if (getFormError()) {
        vlogError("Form errors detected in preAddItemToOrder - order:{0}", getOrder().getId());
        return;
      }
    }

    if (getFormError()) {
      vlogError("Form errors detected in preAddItemToOrder - order:{0}", getOrder().getId());
      return;
    }
    super.preAddItemToOrder(pRequest, pResponse);
  }

  @SuppressWarnings("unchecked")
  public void postAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    boolean fflOrder = false;
    if (getOrder() != null && getOrder().getCommerceItemCount() > 0) {
      List<CommerceItem> lItems = getOrder().getCommerceItems();
      // check if cart contains ffl items if yes then set this flag to true
      for (CommerceItem lItem : lItems) {
        if (((MFFCommerceItemImpl) lItem).getFFL()) {
          fflOrder = true;
          break;
        }
        vlogDebug("((MFFCommerceItemImpl) lItem) getGiftCardDenomination ::" + ((MFFCommerceItemImpl) lItem).getGiftCardDenomination());
        
        String catalogRefId = lItem.getCatalogRefId();
        boolean lIsGiftCard = false;
        if (catalogRefId.equalsIgnoreCase(getEnvironment().getGiftCardSkuId())) {
          lIsGiftCard = true;
        }
        // Overriding PriceInfo for GiftCard product
        if (lIsGiftCard) {
          double giftCardDenomination = 0;
          if (getValue().get("giftCardDenomination") != null && !((String) getValue().get("giftCardDenomination")).equals("")) {
            giftCardDenomination = Double.parseDouble((String) getValue().get("giftCardDenomination"));
          }
          if (((MFFCommerceItemImpl) lItem).getGiftCardDenomination() <= 0 && giftCardDenomination > 0) {
            ((MFFCommerceItemImpl) lItem).setGiftCardDenomination(giftCardDenomination);
          }
          ItemPriceInfo itemPrice = ((MFFCommerceItemImpl) lItem).getPriceInfo();
          itemPrice.setAmountIsFinal(true);
          itemPrice.setAmount(((MFFCommerceItemImpl) lItem).getGiftCardDenomination());
          itemPrice.setListPrice(((MFFCommerceItemImpl) lItem).getGiftCardDenomination());
          itemPrice.setSalePrice(((MFFCommerceItemImpl) lItem).getGiftCardDenomination());
          itemPrice.setRawTotalPrice(((MFFCommerceItemImpl) lItem).getGiftCardDenomination());
          ((MFFCommerceItemImpl) lItem).setPriceInfo(itemPrice);
        }
      }
      
      // 2628 - Limit Order Qty by SKU - BOPIS: validate each item for BOPIS max quantity and set cartMessage, so that the same will
   	  // be displayed on Cart page. Scenario: an item was added as ship to home first.
      // then a 2nd item was added as BOPIS, so the 1st item should be validated for BOPIS limit.
      boolean isBopis = ((MFFOrderImpl)getOrder()).isBopisOrder();
      String quantityMessage = checkItemsQuantityLimit(isBopis);
	  if (!StringUtils.isEmpty(quantityMessage)) {
        	getCheckoutManager().setCartMessage(quantityMessage);
      }
    }
    ((MFFOrderImpl) getOrder()).setFFLOrder(fflOrder);
  }

  /**
   * Validate the ffl status of the existing items in the cart, If current
   * product user trying to add is FFL, then remove any non-ffl items in the
   * cart Non-FFL, then remove any ffl items in the cart
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  private void validateFFL(String pProductId, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    // check if the Current Product is FFL or not
    Boolean isCurrentProdFFL = getCatalogTools().isFFLProduct(pProductId);

    if (isCurrentProdFFL)
      vlogDebug("CartModifierFormHandler: validateFFL : Product Id:{0} isFFL:{1}, we will be removing any non-ffl items from the cart", pProductId, isCurrentProdFFL);
    else
      vlogDebug("CartModifierFormHandler: validateFFL : Product Id:{0} isFFL:{1}, we will be removing any ffl items from the cart", pProductId, isCurrentProdFFL);

    // check if cart already have any items added
    if (getOrder() != null && getOrder().getCommerceItemCount() > 0) {

      List<String> lIdsToRemove = new ArrayList<String>();
      List<CommerceItem> lItems = getOrder().getCommerceItems();

      // iterate through existing commerce items
      for (CommerceItem lItem : lItems) {
        boolean lIsFFL = false;
        MFFCommerceItemImpl mffItem = (MFFCommerceItemImpl) lItem;
        lIsFFL = mffItem.getFFL();
        vlogDebug("CartModifierFormHandler: validateFFL : ItemId:{0}, Product Id:{1} isFFL:{2}", mffItem.getId(), mffItem.getProductId(), lIsFFL);

        // if current product is ffl, check if cart has any non ffl items and
        // add them to removal list
        // if current product is non-ffl, check if cart has any ffl items and
        // add them to removal list
        if (isCurrentProdFFL) {
          // if non-ffl add it to the removal list
          if (!lIsFFL) lIdsToRemove.add(lItem.getId());
        } else {
          // if ffl add it to the removal list
          if (lIsFFL) lIdsToRemove.add(lItem.getId());
        }
        vlogDebug("CartModifierFormHandler: validateFFL : Adding Commerce Item with Item Id :{0}, ProductId:{1} and FFL Status:{2} to removal list", mffItem.getId(), mffItem.getProductId(), lIsFFL);
      }

      // copy non-ffl items to removal item list
      if (lIdsToRemove.size() > 0) {
        String[] ids = new String[lIdsToRemove.size()];
        lIdsToRemove.toArray(ids);
        setRemovalCommerceIds(ids);
        if (isLoggingDebug()) vlogDebug("CartModifierFormHandler: validateFFL : Items in the cart that will be removed are: " + getRemovalCommerceIds());

        setRemoveItemFromOrderSuccessURL(getAddItemToOrderSuccessURL());
        setRemoveItemFromOrderErrorURL(getAddItemToOrderErrorURL());

        // call removeAndAddItemToOrder,
        // this method will take care of removal of existing non-ffl/ffl items
        handleRemoveItemFromOrder(pRequest, pResponse);

      } else
        vlogDebug("CartModifierFormHandler: validateFFL : No Items found for Removal");
    } else
      vlogDebug("CartModifierFormHandler: validateFFL : No Items found");
    return;
  }

  /**
   * Over riding the AddItemToOrder to provide proper error messages to the UI
   * 
   * @param pRequest
   * @param pResponse
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    vlogDebug("Begin handleAddItemToOrder");

    // Additional debugging
    // vlogDebug("Request Dump := "+pRequest);

    vlogInfo("Action: AddItemToOrder for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddItemToOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        // If any form errors found, redirect to error URL:
        if (getFormError()) {
          vlogDebug("handleAddItemToOrder: found form errors even before proceeding, returning");
          return checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse);
        }

        if (getOrder() == null) {
          String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
          throw new ServletException(msg);
        }

        synchronized (getOrder()) {
          preAddItemToOrder(pRequest, pResponse);

          // If any form errors found, redirect to error URL:
          if (getFormError()) {
            vlogDebug("handleAddItemToOrder: Form Error generated in preAddItemToOrder, returning");
            return checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse);
          }
          boolean isGCProduct = getCatalogTools().isGCProduct(getProductId());
          if (isGCProduct) {
            long tmpQuantity = getQuantity();
            setQuantity(1);
            for (int lI = 1; lI <= tmpQuantity; lI++) {
              addItemToOrder(pRequest, pResponse);
              setAddItemCount(0);
            }
          } else {
            addItemToOrder(pRequest, pResponse);
          }

          // If any form errors found, redirect to error URL:
          if (getFormError()) {
            vlogDebug("handleAddItemToOrder: form errors generated in addItemToOrder, returning");
            return checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse);
          }

          postAddItemToOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        if (isAddFromWishList()) {

          removeItemsFromWishList(pRequest, pResponse);
        }
      } finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    } else {
      return false;
    }

    return checkFormRedirect(getAddItemToOrderSuccessURL(), getAddItemToOrderErrorURL(), pRequest, pResponse);
  }
  
	public boolean handleEditCartItem(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		vlogDebug("handleEditCartItem: Called.");

		String commerceItemId = getEditCartItem();
		vlogDebug("handleEditCartItem: commerceItemId: " + commerceItemId);

		List<MFFCommerceItemImpl> items = getOrder().getCommerceItems();
		for (MFFCommerceItemImpl ciItem : items) {
			if (ciItem.getId().equalsIgnoreCase(commerceItemId)) {
				Map edit = getEditValue();
				long ciQuantity = ciItem.getQuantity();
				vlogDebug("handleEditCartItem: cItemQuantity: " + ciQuantity);

				String skuId = ciItem.getCatalogRefId();
				vlogDebug("handleEditCartItem: skuId: " + skuId);
				// Add previous item properties to the edit Map
				edit.put("commerceItemId", commerceItemId);
				edit.put("skuId", skuId);
				edit.put("quantity", ciQuantity);

				break;
			}
		}

		vlogDebug("handleEditCartItem: Done.");

		return true;

	}
	
	
	public void preRemoveAndAddItemToOrder(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		vlogDebug("preRemoveAndAddItemToOrder Called: addActionType: " + getAddActionType());
		
		if ("editTablePicker".equalsIgnoreCase(getAddActionType())) {
	
			if (getItems() != null && getItems().length > 0) {
				
				setProdType("table");
	
				List<String> removeCommerceItemsIds = new ArrayList<String>();
	
				for (AddCommerceItemInfo lItem : getItems()) {
					if (lItem != null) {
						String inputSkuId = lItem.getCatalogRefId();
						vlogDebug("preRemoveAndAddItemToOrder: inputSkuId: " + inputSkuId);
						
						
						if (StringUtils.isNotBlank(inputSkuId)) {
							vlogDebug("preRemoveAndAddItemToOrder: inputSku Qnty: " + lItem.getQuantity());
	
							List<MFFCommerceItemImpl> items = getOrder().getCommerceItems();
							for (MFFCommerceItemImpl ciItem : items) {
								String ciSkuId = ciItem.getCatalogRefId();
								vlogDebug("preRemoveAndAddItemToOrder: ciSkuId: "+ ciSkuId);
								if (StringUtils.isNotBlank(ciSkuId)) {
									if (inputSkuId.equalsIgnoreCase(ciSkuId)) {
										vlogDebug("preRemoveAndAddItemToOrder: removing: "+ ciSkuId);
										removeCommerceItemsIds.add(ciItem.getId());
									}
								}
							}
						}
					}
				}
				if (!validateItemCounts(pRequest, pResponse, true, false)) return;
				
				setRemovalCommerceIds(removeCommerceItemsIds.toArray(new String[removeCommerceItemsIds.size()]));
			}
		}else {
			// validate requested skus & quantities before removing them from the cart
			// store orig qty for display
		      
			// 2523 & 2402 - During edit flow, prior to removing items
			// retain the original itm qty. THis is to simply display the original qty on
			// the UI and not the qty that failed validation
			if(getEditValue() != null && getEditValue().get("previousQnty") != null)
				getEditValue().put("previousQnty", getEditValue().get("previousQnty"));
			
			// 2523 & 2402 - During edit flow, prior to removing items
			// requested quantities should be validated. Else the item is removed from the cart
			// and the updates arent made. Acts like a delete item instead of an update

			validateSkusAndProduct(pRequest, pResponse);
			
			if (getFormError()) {
				vlogError("Form errors detected in preAddItemToOrder - order:{0}", getOrder().getId());
				return;
			}
		}

	}
	
	/*
	 * BZ 2523
	 * Given a SKU & its current qty, checks if it exceeds the per order limit
	 * set for the SKU in the catalog
	 */
	private boolean validateItemLimitForSku(String pSkuId, int pQuantity, boolean pIsBopisOrder) {
		try {
			int limit = getLimitForSku(pSkuId, pIsBopisOrder);
			if(limit > 0 && pQuantity > limit) {
				return false;
			}
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			return false;
		} 		
		return true;
	}

  /**
   * This method remove items which are added to cart from user's wish list.
   * 
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  private void removeItemsFromWishList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (isLoggingDebug()) {
      vlogDebug("handleAddItemToOrder() : Item added from wishlist, hence will be removed from wishlist.");
    }

    RepositoryItem wishList = (RepositoryItem) getProfile().getPropertyValue("wishlist");
    String giftListId = null;
    String removalItemId = null;

    if (wishList != null) {
      giftListId = wishList.getRepositoryId();
      removalItemId = findGiftListItemToRemove(wishList);

      if (!StringUtils.isEmpty(giftListId) && !StringUtils.isEmpty(removalItemId)) {

        if (isLoggingDebug()) {
          vlogDebug("handleAddItemToOrder() : Removing item: " + removalItemId + " from wish list: " + giftListId);
        }

        getGiftListFormHandler().setGiftlistId(giftListId);
        String removalGiftListItems[] = new String[1];
        removalGiftListItems[0] = removalItemId;

        getGiftListFormHandler().setRemoveGiftitemIds(removalGiftListItems);

        try {
          getGiftListFormHandler().removeItemsFromGiftlist(pRequest, pResponse);
        } catch (CommerceException e) {
          vlogError(e, "handleAddItemToOrder(): Exception while removing item from wishlist for order:{0}", getOrder().getId());
        }
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private String findGiftListItemToRemove(RepositoryItem pWishList) {
	  
	vlogDebug("findGiftListItemToRemove() : pWishList: " + pWishList);

    List giftListItems = (List) pWishList.getPropertyValue("giftlistItems");
    vlogDebug("findGiftListItemToRemove() : giftListItems: " + giftListItems);

    RepositoryItem giftListItem = null;
    String giftListProductId = null;
    String giftListSkuId = null;
    
	if (getProdType() != null && getProdType().equalsIgnoreCase("table")) {
    	
    	boolean removeGiftList = false;
    	
    	for (int i = 0; i < giftListItems.size(); i++) {

			giftListItem = (RepositoryItem) giftListItems.get(i);
			giftListProductId = (String) giftListItem.getPropertyValue("productId");
			giftListSkuId = (String) giftListItem.getPropertyValue("catalogRefId");
			  
			vlogDebug("findGiftListItemToRemove() : giftListItem: " + giftListItem);
			vlogDebug("findGiftListItemToRemove() : giftListProductId: " + giftListProductId);
			vlogDebug("findGiftListItemToRemove() : giftListSkuId: " + giftListSkuId);

	      if (!StringUtils.isEmpty(giftListProductId) && !StringUtils.isEmpty(giftListSkuId)) {
	    	  
	    	  for (AddCommerceItemInfo lItem : getItems()) {
			      if (lItem != null) {
			    	  String lProdId = lItem.getProductId();
			    	  String lSkuId = lItem.getCatalogRefId();
			    	  long quantity = lItem.getQuantity();
			    	  
			    	  vlogDebug("findGiftListItemToRemove() : lProdId: " + lProdId);
			    	  vlogDebug("findGiftListItemToRemove() : lSkuId: " + lSkuId);
			    	  vlogDebug("findGiftListItemToRemove() : quantity: " + quantity);

			        if (giftListProductId.equalsIgnoreCase(lProdId) && giftListSkuId.equalsIgnoreCase(lSkuId) && quantity > 0) {
			        	vlogDebug("findGiftListItemToRemove() : removeGiftList: TRUE.");
			        	removeGiftList = true;
			        } else {
			        	removeGiftList = false;
			        }
			      }
			  }
	      }
		}
    	
    	if (removeGiftList){
    		vlogDebug("findGiftListItemToRemove() : removeGiftList: " + giftListItem.getRepositoryId());
    		return giftListItem.getRepositoryId();
    	}
    	
	} else {

	    for (int i = 0; i < giftListItems.size(); i++) {
	
	      giftListItem = (RepositoryItem) giftListItems.get(i);
	      giftListProductId = (String) giftListItem.getPropertyValue("productId");
	      giftListSkuId = (String) giftListItem.getPropertyValue("catalogRefId");
	      
	      vlogDebug("findGiftListItemToRemove() : giftListItem: " + giftListItem);
	      vlogDebug("findGiftListItemToRemove() : giftListProductId: " + giftListProductId);
	      vlogDebug("findGiftListItemToRemove() : giftListSkuId: " + giftListSkuId);
	
	      if (!StringUtils.isEmpty(giftListProductId) && !StringUtils.isEmpty(giftListSkuId)) {
	    	  
	    	  vlogDebug("findGiftListItemToRemove() : getProductId(): " + getProductId());
	    	  vlogDebug("findGiftListItemToRemove() : getCatalogRefIds(): " + getCatalogRefIds());
	
	        if (giftListProductId.equalsIgnoreCase(getProductId()) &&giftListSkuId.equalsIgnoreCase(getCatalogRefIds()[0])) {
	        	return giftListItem.getRepositoryId();
	        }
	      }
	    }
    }

    return null;
  }
  @Override
  public void preMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		  throws ServletException, IOException {
	  
	  // BZ 2523 - Verifies if all items in the order are under the per order limit defined for SKUs in the catalog
	  // We do this here to handle scenarios like merge order
	  boolean isBopis = ((MFFOrderImpl)getOrder()).isBopisOrder();
	  if (!isOrderShippable(true, isBopis)) {
        	getFormExceptionGenerator().generateException(MFFConstants.MSG_ORDER_NOT_SHIPPABLE, true, this, pRequest);
      }
	  super.preMoveToPurchaseInfo(pRequest, pResponse);
  }
  @Override
  public boolean handleMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (getOrder() == null) {
      logError("handleMoveToPurchaseInfo: form errors generated in handleMoveToPurchaseInfo, returning");
      return checkFormRedirect(getMoveToPurchaseInfoSuccessURL(), getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
    }
    if(getFormError()) {
    	return checkFormRedirect(getMoveToPurchaseInfoSuccessURL(), getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
    }

    vlogDebug("CartModifierFormHandler: handleMoveToPurchaseInfo: no of com items in order:{0} is: {1}", getOrder().getId(), getOrder().getCommerceItemCount());
    return super.handleMoveToPurchaseInfo(pRequest, pResponse);
  }
  
  @Override
  public void postMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    getCheckoutManager().resetCheckoutValues();
    getCheckoutManager().authorizeShippingStep();
    super.postMoveToPurchaseInfo(pRequest, pResponse);
  }
  
  /**
   * Override the super class method to set inventory result and error messages for display in cart
   * @param pError
   * @param pErrorKey
   * @return
   */
  @Override
  public void handlePipelineError(Object pError, String pErrorKey) {
    vlogError("CartModifierFormHandler pipeline error, order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());
    if(pErrorKey.equalsIgnoreCase("inventoryError")){
      vlogWarning("Found inventory errors while proceeding to checkout, adjusted the cart for order:{0}",getOrder().getId());
      MFFInventoryMessage msg = (MFFInventoryMessage) pError;
      super.handlePipelineError(msg.getErrorMessage(), pErrorKey);
    }else
      super.handlePipelineError(pError, pErrorKey);
  }

  public boolean handleApplyTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("handleApplyTaxExemption - Entering");
    vlogDebug("handleApplyTaxExemption - {0}, {1}", getTaxExemptionSuccessURL(), getTaxExemptionErrorURL());

    if (getOrder() == null) {
      logError("handleApplyTaxExemption: No Order found");
      getFormExceptionGenerator().generateException(MFFConstants.MSG_TAX_EXEMPTION_ERROR, true, this, pRequest);
      return checkFormRedirect(getTaxExemptionSuccessURL(), getTaxExemptionErrorURL(), pRequest, pResponse);
    }

    if (!StringUtils.isEmpty(getTaxExempSelected())) {
      preApplyTaxExemption(pRequest, pResponse);
    }

    if (getFormError()) {
      return checkFormRedirect(getTaxExemptionSuccessURL(), getTaxExemptionErrorURL(), pRequest, pResponse);
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleApplyTaxExemption";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        synchronized (getOrder()) {
          vlogDebug("handleApplyTaxExemption: taxExempSelected: {0}", getTaxExempSelected());
          ((MFFOrderImpl) getOrder()).setTaxExemptionName(getTaxExempSelected());
          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      } catch (Exception ex) {
        vlogError(ex, "Exception while applying Tax Exemption");
        getFormExceptionGenerator().generateException(MFFConstants.MSG_TAX_EXEMPTION_ERROR, true, this, pRequest);
        return checkFormRedirect(null, getTaxExemptionErrorURL(), pRequest, pResponse);
      } finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    }
    vlogDebug("handleApplyTaxExemption - Exiting");
    return checkFormRedirect(getTaxExemptionSuccessURL(), getTaxExemptionErrorURL(), pRequest, pResponse);
  }

  @SuppressWarnings("rawtypes")
  public void preApplyTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    RepositoryItem profile = getProfile();
    Map profileTaxExemptions = (Map) profile.getPropertyValue(MFFConstants.PROPERTY_TAX_EXEMPTIONS);
    boolean taxExmpOnProfile = false;

    Iterator taxExmpEntries = profileTaxExemptions.entrySet().iterator();
    while (taxExmpEntries.hasNext()) {
      Entry taxExmp = (Entry) taxExmpEntries.next();
      Object taxExmpValue = taxExmp.getValue();
      vlogDebug("preApplyTaxExemption(): taxExmpValue: " + taxExmpValue);

      if (taxExmpValue != null && taxExmpValue instanceof RepositoryItem) {
        String taxExmpCode = (String) ((RepositoryItem) taxExmpValue).getPropertyValue(MFFConstants.PROPERTY_TAX_EXEMP_CODE);
        vlogDebug("preApplyTaxExemption(): taxExmpCode: " + taxExmpCode);

        if (!StringUtils.isEmpty(taxExmpCode) && getTaxExempSelected().equals(taxExmpCode)) {
          taxExmpOnProfile = true;
          break;
        }
      }
    }

    if (!taxExmpOnProfile) {
      getFormExceptionGenerator().generateException(MFFConstants.MSG_TAX_EXEMPTION_ERROR, true, this, pRequest);
    }
  }

  @Override
  public void preSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.preSetOrderByCommerceId(pRequest, pResponse);
    printSkuToQuantityMap("Action: preSetOrderByCommerceId", getOrder());
    if (!validateItemCounts(pRequest, pResponse, false, true)) return;
  }

  @Override
  public void postSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.postSetOrderByCommerceId(pRequest, pResponse);
    updateCartInfo(pRequest, pResponse);
    printSkuToQuantityMap("Action: postSetOrderByCommerceId", getOrder());
  }

  @SuppressWarnings("unchecked")
  private HashMap<String, Long> printSkuToQuantityMap(String pMessage, Order pOrder) {

    HashMap<String, Long> idToQuantityMap = new HashMap<String, Long>();
    List<CommerceItem> items = pOrder.getCommerceItems();
    for (CommerceItem item : items) {
      String itemId = item.getId();
      if (idToQuantityMap.containsKey(itemId)) {
        idToQuantityMap.put(itemId, idToQuantityMap.get(itemId) + item.getQuantity());
      } else {
        idToQuantityMap.put(itemId, item.getQuantity());
      }
    }

    vlogDebug("Inside getSkuToQuantityMap - {0} - for order ({1}) - {2}", pMessage, pOrder.getId(), idToQuantityMap);
    return idToQuantityMap;
  }
  
  /**
   * BZ 2523
   * Checks if all items in the order are under the limit per sku per order defined in the catalog
   * 
   * bAddExceptions - On the PDP we show a generic msg if the order cannot be shipped
   * 	because items are over the ship limit
   * 	On the cart - we will display an error for each item that exceeds the threshold
   * 
   * Called when an order is switched from BOPIS to "Ship to home" in the cart page
   * @return
   */
  private boolean isOrderShippable(boolean bAddExceptions, boolean pIsBopis) {
	  boolean orderItemsWithinThreshold = true;
	  List<CommerceItem> items = getOrder().getCommerceItems();
			  
	  for (CommerceItem item : items) {
		  if(!validateItemLimitForSku(item.getCatalogRefId(), (int) item.getQuantity(), pIsBopis)) {
			  if(bAddExceptions) {
				Object[] msgArgs = new Object[2];
				msgArgs[0] = item.getCatalogRefId();
				try {
					msgArgs[1] = getLimitForSku(item.getCatalogRefId(), pIsBopis);
				} catch (RepositoryException e) {
					e.printStackTrace();
					msgArgs[1]=0;
				}
				String resourceMsg="The item ({0}) is restricted to ({1}) per order.";
				String msg = MessageFormat.format(resourceMsg, msgArgs);
				getFormExceptionGenerator().generateException(msg, false, this, null);
			  }
				
				orderItemsWithinThreshold=false;
		  }
	  }
	  return orderItemsWithinThreshold;
  }
  
  private String checkItemsQuantityLimit(boolean pIsBopis) {
	  String msg = "";
	  List<CommerceItem> items = getOrder().getCommerceItems();
			  
	  for (CommerceItem item : items) {
		  if(!validateItemLimitForSku(item.getCatalogRefId(), (int) item.getQuantity(), pIsBopis)) {
			  
				Object[] msgArgs = new Object[2];
				msgArgs[0] = item.getCatalogRefId();
				try {
					msgArgs[1] = getLimitForSku(item.getCatalogRefId(), pIsBopis);
				} catch (RepositoryException e) {
					e.printStackTrace();
					msgArgs[1]=0;
				}
				String resourceMsg="The item ({0}) is restricted to ({1}) per order.";
				msg = MessageFormat.format(resourceMsg, msgArgs);
		  }
	  }
	  return msg;
  }

  /**
   * Apply the item count restrictions
   * <ul>
   * <li>limit total item quantity in the order</li>
   * <li>limit total number of items in the order</li>
   * </ul>
   * 
   * @param pRequest
   * @param pResponse
   * @param isMultiAdd
   * @param isFromCart
   * @return
   */
  @SuppressWarnings("unchecked")
  private boolean validateItemCounts(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, 
		  boolean isMultiAdd, boolean isFromCart) {
	  boolean isCartWithinThreshold=true;
	  boolean isBopis = ((MFFOrderImpl)getOrder()).isBopisOrder();
	  
	  // Validating for multi add
	  if (getItems() != null) {
		  int totalSkuQty=0;
		  isCartWithinThreshold=true;
		  for(AddCommerceItemInfo itemInfo:getItems()){
			  if(itemInfo.getQuantity() > 0){
				  // current qty + qty already in order (during Add mode)
				  // current qty only (during edit mode)
				  totalSkuQty = (int) itemInfo.getQuantity();
		          CommerceItem ci = getCommerceItemForSku(itemInfo.getCatalogRefId());
		          if (ci != null) {
		        	  if(!isEditMode()) {
		        		  totalSkuQty = (int) (ci.getQuantity() + itemInfo.getQuantity());
		        	  }
		          }
				  if(!validateItemLimitForSku(itemInfo.getCatalogRefId(), totalSkuQty, isBopis) ) {
						Object[] msgArgs = new Object[2];
						msgArgs[0] = itemInfo.getCatalogRefId();
						try {
							msgArgs[1] = getLimitForSku(itemInfo.getCatalogRefId(), isBopis);
						} catch (RepositoryException e) {
							e.printStackTrace();
							msgArgs[1]=0;
						}
						String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_OVER_QTY_LIMIT));
						String msg = MessageFormat.format(resourceMsg, msgArgs);
						getFormExceptionGenerator().generateException(msg, false, this, pRequest);
						isCartWithinThreshold=false;
				  }
			  }
		  }
		  return isCartWithinThreshold;
	  }
	  // validating from cart page
	  isCartWithinThreshold=true;
	  if(isFromCart) {
	      if (getOrder().getCommerceItemCount() > 0) {
	          List<CommerceItem> items = getOrder().getCommerceItems();		  
	          for (CommerceItem item : items) {
	              long updateQty = 0;
	              try {
	                updateQty = getQuantity(item.getId(), pRequest, pResponse);
	                vlogDebug("Update Quantity for the item:{0} on the order{1} is: {2}", item.getId(), getOrder().getId(), updateQty);
					if(!validateItemLimitForSku(item.getCatalogRefId(), (int) updateQty, isBopis)) {
						Object[] msgArgs = new Object[2];
						msgArgs[0] = item.getCatalogRefId();
						try {
							msgArgs[1] = getLimitForSku(item.getCatalogRefId(), isBopis);
						} catch (RepositoryException e) {
							e.printStackTrace();
							msgArgs[1]=0;
						}
						String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_OVER_QTY_LIMIT));
						String msg = MessageFormat.format(resourceMsg, msgArgs);
						getFormExceptionGenerator().generateException(msg, false, this, pRequest);
						isCartWithinThreshold=false;
					} 
	              } catch (NumberFormatException e) {
	                vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
	                getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
	                return false;
	              } catch (ServletException e) {
	                vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
	                getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
	                return false;
	              } catch (IOException e) {
	                vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
	                getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
	                return false;
	              }
	            }
	          return isCartWithinThreshold;
	      }
	      return isCartWithinThreshold;
	  }
	  
	  if (!isMultiAdd & !isFromCart) {
		  //if(!validateItemLimitForSku(getCa, pQuantity))
		  String skuIds[] = getCatalogRefIds();
		  int totalQty=0;
		  if(skuIds != null && skuIds.length > 0) {
			  for(int i=0; i < skuIds.length; i++) {
				  totalQty=0;
				  CommerceItem ci = getCommerceItemForSku(skuIds[i]);
				  
				  // 2523 & 2402 - Validate item quantities during edit flow as well
				  // in edit flow getQty will have the update qty requested
				  if(ci != null && !isEditMode()) {
					  totalQty = (int) (ci.getQuantity()+getQuantity());
				  }else{
					  totalQty=(int) getQuantity();
				  }
				  if(!validateItemLimitForSku(skuIds[i], totalQty, isBopis)) {
						Object[] msgArgs = new Object[2];
						try {
							msgArgs[0] = skuIds[i];
							msgArgs[1] = getLimitForSku(skuIds[i], isBopis);
							
						} catch (RepositoryException e) {
							e.printStackTrace();
							msgArgs[1]=0;
						}
						String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_OVER_QTY_LIMIT));
						String msg = MessageFormat.format(resourceMsg, msgArgs);
						getFormExceptionGenerator().generateException(msg, false, this, pRequest);
						return false;					  
				  }
			  }
		  }
	  }

    // verify if the total items count in the order is under allowed limit
    if (getEnvironment().isEnableMaxItemCountPerOrder()) {
      long reqItemCount = 1;
      if (isMultiAdd)
        reqItemCount = getOptedItemCount(getItems());
      else if (isFromCart) reqItemCount = 0;

      // total item count including the one we are trying to add
      long totalItemsCount = getOrder().getCommerceItemCount() + reqItemCount;
      vlogDebug("Current Item count on the order:{0} is {1} and number of items requested to add:{2}", getOrder().getId(), getOrder().getCommerceItemCount(), reqItemCount);
      if (totalItemsCount > getEnvironment().getMaxItemCountPerOrder()) {
        vlogError("Can not add item as the maxItemCountPerOrder is reached for order:{0}", getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_ITEM_COUNT_LMT_REACHED, true, this, pRequest);
        return false;
      }
    }

    // verify if the total qty of all items in the order is under allowed limit
    if (getEnvironment().isEnableMaxTotalQtyPerOrder()) {
      long totalItemQtyInOrder = 0;
      if (getOrder().getCommerceItemCount() > 0) {
        List<CommerceItem> items = getOrder().getCommerceItems();

        if (isFromCart) {
          for (CommerceItem item : items) {
            long updateQty = 0;
            try {
              updateQty = getQuantity(item.getId(), pRequest, pResponse);
              vlogDebug("Update Quantity for the item:{0} on the order{1} is: {2}", item.getId(), getOrder().getId(), updateQty);
            } catch (NumberFormatException e) {
              vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
              getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
              return false;
            } catch (ServletException e) {
              vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
              getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
              return false;
            } catch (IOException e) {
              vlogError(e, "There is an issue while getting quantity for order:{0}", getOrder().getId());
              getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
              return false;
            }
            totalItemQtyInOrder += updateQty;
          }
        } else {
          for (CommerceItem item : items) {
            totalItemQtyInOrder += item.getQuantity();
          }
        }

        long reqQty = 0;
        // if multi add get quantity from each item
        if (isMultiAdd) {
          if (getItems() != null) {
            for (int i = 0; i < getItems().length; i++)
              reqQty += getItems()[i].getQuantity();
          }
        } else
          reqQty = getQuantity();

        vlogDebug("Current Total Item Qty count on the order:{0} is {1} and number of items requested to add:{2}", getOrder().getId(), totalItemQtyInOrder, reqQty);

        // add the requested qty to existing qty
        totalItemQtyInOrder += reqQty;

        vlogDebug("Total Item Qty on Order:{0} including requested qty is:{1}", getOrder().getId(), totalItemQtyInOrder);
        if (totalItemQtyInOrder > getEnvironment().getMaxTotalQtyPerOrder()) {
          vlogError("Can not add item as the getMaxTotalQtyPerOrder is reached for order:{0}", getOrder().getId());
          getFormExceptionGenerator().generateException(MFFConstants.MSG_ITEM_COUNT_LMT_REACHED, true, this, pRequest);
          return false;
        }
      }
    }
    return true;
  }
  
  private int getLimitForSku(String pSkuId, boolean pIsBopisOrder) throws RepositoryException  {
	  int limit=0;
      RepositoryItem sku = getCatalogTools().findSKU(pSkuId);
      
      vlogDebug("isStorePickup:  " + isStorePickup());
      vlogDebug("pIsBopisOrder:  " + pIsBopisOrder);
      
      if (pIsBopisOrder || isStorePickup()){
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
   * Gets the opted item count.
   *
   * @param pItems the items
   * @return the opted item count
   */
  private long getOptedItemCount(AddCommerceItemInfo[] pItems){
	  long reqItemCount = 0;
	  if(pItems != null){
		  for(AddCommerceItemInfo itemInfo:pItems){
			  if(itemInfo.getQuantity() > 0){
				  reqItemCount = reqItemCount + 1;
			  }
		  }
	  }
	return reqItemCount;
  }

  /**
   * Method to validate if the required values (skuId and productId) are passed,
   * validate if sku is available, and has inventory
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private void validateSkusAndProduct(DynamoHttpServletRequest pRequest, 
		  DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering validateSkusAndProduct");
    String prodId = getProductId();
    if (prodId == null) {
      vlogError("No ProductId(s) were specified in AddToCart - Order:{0}", getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_MISSING, true, this, pRequest);
      return;
    }

    vlogDebug("AddToCart-Product Id  : " + prodId);
    boolean isGCProduct = getCatalogTools().isGCProduct(prodId);
    vlogDebug("Is Gift Card Product- {0} : {1}", prodId, isGCProduct);
    vlogDebug("IsGCProduct --" + isGCProduct + "----Max Denomination Allowed--->" + getGiftCardMaxDenommination());
    // validation for Gift Card denominations.
    if (isGCProduct) {

      double giftCardDenomination = 0.0;
      if (getValue().get("giftCardDenomination") != null && !((String) getValue().get("giftCardDenomination")).equals("") && NumberUtils.isValidFloatingPointFormat((String) getValue().get("giftCardDenomination"))) {
        giftCardDenomination = Double.parseDouble((String) getValue().get("giftCardDenomination"));
      }
      vlogDebug("---Denomination Selected -->{0}", giftCardDenomination);
      if (giftCardDenomination < getGiftCardMinDenommination()) {
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_GC_INVALID_DENOMINATION));
        String msg = MessageFormat.format(resourceMsg, getGiftCardMinDenommination());
        getFormExceptionGenerator().generateException(msg, false, this, pRequest);
        return;
      }
      if (giftCardDenomination > getGiftCardMaxDenommination()) {
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_GC_MAXDENOMINATION_ERROR));
        String msg = MessageFormat.format(resourceMsg, getGiftCardMaxDenommination());
        getFormExceptionGenerator().generateException(msg, false, this, pRequest);
        return;
      }
      if (getOrder() != null && (((MFFOrderImpl) getOrder()).getGCTotalDenominations() + getQuantity() * giftCardDenomination) > getMaxGifCardsValueInCart()) {
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_GC_MAXVALUE_ERROR));
        String msg = MessageFormat.format(resourceMsg, getMaxGifCardsValueInCart());
        getFormExceptionGenerator().generateException(msg, false, this, pRequest);
        return;
      }
    }

    // check item counts restrictions
    if (!validateItemCounts(pRequest, pResponse, false, false)) return;

    // verify quantity requested is less than zero
    long currentQuantityRequested = getQuantity();
    if (currentQuantityRequested <= 0) {
      vlogError("Quantity requested cannot be zero - Order:{0}", getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
      return;
    }

    // check if product is available to buy online
    boolean productAvailableOnline = getCatalogTools().isProductAvailableOnline(prodId);
    if (!productAvailableOnline) {
      vlogError("Product:{0} is not avilable to buy online - Order:{1}", prodId, getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_INSTORE_ONLY, true, this, pRequest);
      return;
    }

    boolean isFFL = getCatalogTools().isFFLProduct(prodId);
    if (isFFL && isStorePickup()) {
      vlogError("Product:{0} is FFL and can not be picked up in store:{1} - Order:{2}", prodId, isStorePickup(), getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_FFL_BOPIS_ERROR, true, this, pRequest);
      return;
    }

    // bopis only validation
    if (!isStorePickup()) {
      // check if product is available to ship to home
      boolean isBopisOnlyProduct = getCatalogTools().isBopisOnlyProduct(prodId);

      // We will be allowing user to ship FFL items even if they are marked as
      // BopisOnly
      // This change is required as currently we do not support BOPIS for FFL
      // items
      if (isFFL && isBopisOnlyProduct) {
        isBopisOnlyProduct = false;
      }
      if (isBopisOnlyProduct) {
        vlogError("Product:{0} is only available for Pick Up in Store - Order:{1} ", prodId, getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_BOPIS_ONLY, true, this, pRequest);
        return;
      }
    }

    String[] skuIds = getCatalogRefIds();
    if (skuIds == null || skuIds.length == 0) {
      vlogError("No SkuId was specified in AddToCart - Order:{0}", getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_SKU_MISSING, true, this, pRequest);
    } else {
      for (int i = 0; i < skuIds.length; i++) {
        String skuId = skuIds[i];
        if (StringUtils.isEmpty(skuId)) {
          vlogError("SkuId is missing in AddToCart - Order:{0}", getOrder().getId());
          getFormExceptionGenerator().generateException(MFFConstants.MSG_SKU_MISSING, true, this, pRequest);
          return;
        }

        vlogDebug("Action: AddItemToOrder.validateSkusAndProduct, order id ({0}) ,profile id ({1}), product id ({2}), sku id ({3})", getOrder().getId(), getProfile().getRepositoryId(), prodId, skuId);

        // if store pick up selected, skip inventory check as we already
        // performed inventory check.
        // And also, if GC product is selected skip inventory check
        if (!isStorePickup() && !isGCProduct) {
          // Validate inventory for this sku before adding it
          boolean validForInventory = validateInventoryForSku(pRequest, skuId, getQuantity());
          if (!validForInventory) {
            return;
          }

          // validate for max qty
          boolean validForMaxQty = validateSkuForMaxQty(pRequest, skuId, getQuantity());
          if (!validForMaxQty) {
            if (isLoggingDebug()) {
              vlogDebug("Sku is not validForMaxQty for AddToCart:" + skuId);
            }
            getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_LMT_REACHED, true, this, pRequest);
            return;
          }
        }
      }
    }

    vlogDebug("Exiting validateSkusAndProduct");
  }

  /**
   * Method to validate if the required values (skuId and productId) are passed,
   * validate if sku is available, and has inventory
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws ServletException
   * @throws IOException
   */
  private boolean validateMultiAdd(String lProdId, String pSkuId, long pQuantity, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    // check if product is available to ship to home
    boolean productAvailableOnline = getCatalogTools().isProductAvailableOnline(lProdId);
    if (!productAvailableOnline) {
      vlogError("Sku is not available to buy online:{0} - order:{1}", lProdId, getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_INSTORE_ONLY, true, this, pRequest);
      return false;
    }

    boolean isFFL = getCatalogTools().isFFLProduct(lProdId);
    if (isFFL && isStorePickup()) {
      vlogDebug("Product:{0} is FFL and can not be picked up in store:{1} - order:{2}", lProdId, isStorePickup(), getOrder().getId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_FFL_BOPIS_ERROR, true, this, pRequest);
      return false;
    }

    // bopis only validation
    if (!isStorePickup()) {
      // check if product is available to ship to home
      boolean isBopisOnlyProduct = getCatalogTools().isBopisOnlyProduct(lProdId);

      // We will be allowing user to ship FFL items even if they are marked as
      // BopisOnly
      // This change is required as currently we do not support BOPIS for FFL
      // items
      if (isFFL && isBopisOnlyProduct) {
        isBopisOnlyProduct = false;
      }

      if (isBopisOnlyProduct) {
        vlogError("Product:{0} is only available for Pick Up in Store - Order:{1} ", lProdId, getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_BOPIS_ONLY, true, this, pRequest);
        return false;
      }
    }

    if (pSkuId == null || pSkuId.isEmpty()) {
      vlogError("No SkuId was specified in AddToCart - Order:{0}", getOrder().getId());
      return false;
    } else {
      // Validate inventory for this sku before adding it
      boolean validForInventory = validateInventoryForSku(pRequest, pSkuId, pQuantity);
      if (!validForInventory) {
        vlogError("Sku is notValidForInventory for AddToCart: {0} - Order:{1}", pSkuId, getOrder().getId());
        return false;
      }

      // validate for max qty
      boolean validForMaxQty = validateSkuForMaxQty(pRequest, pSkuId, pQuantity);
      if (!validForMaxQty) {
        vlogError("Sku is not validForMaxQty for AddToCart:{0}", pSkuId);
        return false;
      }
    }
    return true;
  }

  /**
   * @param pRequest
   * @param pResponse
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public boolean handleMoveItemToWishlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {

    String[] removalCommerceIds = getRemovalCommerceIds();
    MFFCommerceItemImpl itm = null;

    if (removalCommerceIds != null && removalCommerceIds.length > 0) {
      try {
        CommerceItem ci = (CommerceItem) getOrder().getCommerceItem(removalCommerceIds[0]);
        itm = (MFFCommerceItemImpl) ci;

      } catch (CommerceItemNotFoundException e) {
        logError(e.getMessage());
      } catch (InvalidParameterException e) {
        logError(e.getMessage());
      }
    }

    if (itm != null) {
      // Now add to wish list
      try {
        String[] skuIds = new String[1];

        skuIds[0] = itm.getCatalogRefId();
        getGiftListFormHandler().setCatalogRefIds(skuIds);
        getGiftListFormHandler().setGiftlistId(getWishListId());
        getGiftListFormHandler().setProductId(itm.getAuxiliaryData().getProductId());
        getGiftListFormHandler().setItemIds(removalCommerceIds);

        getGiftListFormHandler().setQuantity(1);

        getGiftListFormHandler().handleMoveItemsFromCart(pRequest, pResponse);
        getGiftListFormHandler().setMoveItemsFromCartSuccessURL(getAddItemToGiftlistSuccessURL());
        getGiftListFormHandler().setMoveItemsFromCartErrorURL(getAddItemToGiftlistErrorURL());

      } catch (CommerceException e) {
        logError(e.getMessage());
      }
    }
    // getCheckoutManager().setCartMessage(null);

    return checkFormRedirect(getAddItemToGiftlistSuccessURL(), getAddItemToGiftlistErrorURL(), pRequest, pResponse);
  }

  /**
   * This method will be invoked when user clicks on choose bopis store button
   * This button will be presented to user at various points of the cart &
   * checkout whenever bopis modal is displayed 1) Add to cart when
   * pickUpinStore selected on the PDP Page 2) Change store option selected on
   * Cart or PDP or Checkout etc.. 3) We will invoke addToCart method only if
   * this method called from PDP.
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public boolean handleChooseBopisStore(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {

    vlogDebug("Inside handleChooseBopisStore");

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleChooseBopisStore";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      if (getBopisStore().isEmpty()) {
        vlogError("CartModifierFormHandler: handleChooseBopisStore: There is an error while selecting the store");
        getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_NO_STORE, true, this, pRequest);
        return checkFormRedirect(null, getChooseStoreErrorURL(), pRequest, pResponse);
      }

      if (getOrder() == null) {
        String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
        vlogError(msg);
        return checkFormRedirect(null, getChooseStoreErrorURL(), pRequest, pResponse);
      }
      Transaction tr = ensureTransaction();

      if (isFromProduct()) {
        // this flag will be used to skip few validations while adding item to
        // cart
        setStorePickup(true);
        // implement add to cart here
        if(isEditMode() || (getRemovalCommerceIds() != null && getRemovalCommerceIds().length > 0)) {
        	handleRemoveAndAddItemToOrder(pRequest, pResponse);
        } else {
        	handleAddItemToOrder(pRequest, pResponse);
        }
      }

      // update bopis info on the order, set bopisOrder to true and storeId
      try {
    	  boolean setBopisOrderFlag = true;
    	  if (getFormError()) {
    		  vlogDebug ("form error detected, hence bopis order flag will be set as false on order.");
    		  setBopisOrderFlag = false;
    	  }
    	  
    	  ((MFFOrderManager) getOrderManager()).updateBopisOrderInfo(getOrder(), setBopisOrderFlag, getBopisStore());
    	  RepositoryItem storeItem = getStoreLocatorTools().getStoreByLocationId(getBopisStore());
    	  ((MutableRepositoryItem) ((Profile) getProfile()).getDataSource()).setPropertyValue("myHomeStore", storeItem);

      } catch (CommerceException e) {
        vlogError(e, "There is an error while updating the order:{0} with bopis information", getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_ORDER_UPDATE_ERROR, true, this, pRequest);
        return checkFormRedirect(null, getChooseStoreErrorURL(), pRequest, pResponse);
      } finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    }
    return checkFormRedirect(getChooseStoreSuccessURL(), getChooseStoreErrorURL(), pRequest, pResponse);
  }

  /**
   * This method is invoked when user clicks on shipMyOrder Instead option Set
   * the bopisOrderFalg on the order to false and sets the bopisStore to null
   * 
   * @param pRequest
   * @param pResponse
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public boolean handleShipMyOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {

    vlogDebug("Inside handleShipMyOrder");

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleShipMyOrder";
    
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {

      if (getOrder() == null) {
        String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
        vlogError(msg);
        return checkFormRedirect(null, getChooseStoreErrorURL(), pRequest, pResponse);
      }

      // on PDP show a generic message when validation fails
      // on cart show a message for each item that fails validation
      // passing false for isBopis, because here user is trying to switch to ship to home, hence non-BOPIS quntity limit should be checked.
      if(!isOrderShippable(isFromProduct()?false:true , false)) {
    	  if(isFromProduct()) 
    		  getFormExceptionGenerator().generateException(MFFConstants.MSG_ORDER_NOT_SHIPPABLE, true, this, pRequest);
    	  return checkFormRedirect(null, getShipMyOrderErrorURL(), pRequest, pResponse);
      }
      Transaction tr = null;
      // update bopis info, set bopisOrder to false and bopis store to null
      // cart will be defaulted to shipToHome
      try {
        tr = ensureTransaction();
        ((MFFOrderManager) getOrderManager()).shipMyOrderInstead(getOrder());
      } catch (CommerceException e) {
        vlogError(e, "There is an error while updating the order with bopis information");
        getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_ORDER_UPDATE_ERROR, true, this, pRequest);
        return checkFormRedirect(null, getChooseStoreErrorURL(), pRequest, pResponse);
      } finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    }

    return checkFormRedirect(getShipMyOrderSuccessURL(), getShipMyOrderErrorURL(), pRequest, pResponse);
  }

  public boolean handleAddItemToWishlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {

    vlogDebug("handleAddItemToWishlist(): request wishlist Id: " + getWishListId());
    
    String profileId = getProfile().getRepositoryId(); 
    
    try {
		if (StringUtils.isBlank(getWishListId())) {
			String profileWishlistId = getGiftlistManager().getWishlistId(profileId);
			vlogDebug("handleAddItemToWishlist(): profileWishlistId: "+ profileWishlistId);

			if (StringUtils.isBlank(profileWishlistId)) {

				RepositoryItem newWishListItem = getGiftlistTools().createDefaultGiftlist(profileId, false, null,
						null, "other", (String) null, (String) null, null, null, (String) null);
				
				if (newWishListItem != null) {
					String wishListId = newWishListItem.getRepositoryId();
					vlogDebug("handleAddItemToWishlist(): newly created wishListId: "+ wishListId);
					this.setWishListId(newWishListItem.getRepositoryId());
					GiftlistTools tools = this.getGiftlistManager().getGiftlistTools();
					MutableRepositoryItem profileItem = tools.getProfile(profileId);
					profileItem.setPropertyValue("wishlist", newWishListItem);
				}
			}
		}

		// Now add to wish list
      getGiftListFormHandler().setCatalogRefIds(getCatalogRefIds());
      getGiftListFormHandler().setGiftlistId(getWishListId());
      getGiftListFormHandler().setProductId(getProductId());
      getGiftListFormHandler().setQuantity(1);
      getGiftListFormHandler().handleAddItemToGiftlist(pRequest, pResponse);
      
    } catch (CommerceException e) {
		vlogError("handleAddItemToWishlist(): Exception while adding item to wishlist: " + e, e);
		addFormException(new DropletException("Unable to add item to wishlist."));
    }
    return checkFormRedirect(getAddItemToGiftlistSuccessURL(), getAddItemToGiftlistErrorURL(), pRequest, pResponse);
  }

  protected void preClaimCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    // Validate the promo code
    if (StringUtils.isBlank(getCouponCode())) {

      addFormException(new DropletException("Please enter valid coupon code."));
    }
  }

  @SuppressWarnings("rawtypes")
  public boolean handleApplyCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (isLoggingDebug()) vlogDebug("Inside apply coupon.");

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleApplyCoupon";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      // Validate the promo code
      preClaimCoupon(pRequest, pResponse);
      if (getFormError()) {
        return checkFormRedirect(getApplyCouponSuccessURL(), getApplyCouponErrorURL(), pRequest, pResponse);
      }

      Transaction tr = ensureTransaction();

      String couponClaimCode = getCouponCode().trim();
      try {
        try {
          String profileId = getProfile().getRepositoryId();
          getClaimableManager().claimCoupon(profileId, couponClaimCode);
          RepositoryItem couponItem = getClaimableManager().findAndClaimCoupon(couponClaimCode);
          if (couponItem != null) {
            if (couponItem.getPropertyValue("promotions") != null) {
              ChangeAwareSet promotions = (ChangeAwareSet) couponItem.getPropertyValue("promotions");
              if (promotions != null && promotions.size() > 0) {
                RepositoryItem promotion = null;
                Iterator itr = promotions.iterator();
                while (itr.hasNext()) {
                  promotion = (RepositoryItem) itr.next();
                }

                if (promotion != null) {
                  if (promotion.getPropertyValue("description") != null) {
                    setAppliedCouponDescription((String) promotion.getPropertyValue("description"));
                  }
                }
              }
            }
          }
        } catch (ClaimableException ce) {

          Throwable sourceException = ce.getSourceException();
          if (sourceException instanceof PromotionException) {
            PromotionException pe = (PromotionException) sourceException;
            addFormException(new DropletException(pe.getMessage()));
          }

          if (StringUtils.isNotBlank(ce.getMessage())) {
            addFormException(new DropletException(ce.getMessage()));
          }
        }

        if (!getFormError()) {
          // reprice the order
          PurchaseProcessHelper pph = getPurchaseProcessHelper();
          try {
            synchronized (getOrder()) {
              addCouponToOrder(getOrder(), couponClaimCode);
              pph.runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_TOTAL, getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null, this);
              getOrderManager().updateOrder(getOrder());
              if(isFromCheckout()){
                vlogInfo("Promo Applied in the checkout for Order:{0}, update payment groups",getOrder().getId());
                getCheckoutManager().updatePaymentGroups(false);
                getCheckoutManager().isOrderRequiresCreditCard();
              }
            }
          } catch (RunProcessException ce) {
            if (isLoggingError()) {
              String msg = "handleClaimCoupon: ce = " + ce;
              logError(msg, ce);
            }

            addFormException(new DropletException("Some Exception occured in " + "upateing the order with coupon."));
          }
        }
      } catch (Exception e) {
        // Rollback the transaction if we get this...
        try {
          setTransactionToRollbackOnly();
        } catch (SystemException e1) {
          if (isLoggingError()) {
            String msg = "handleApplyCoupon: ";
            logError(msg, e1);
          }
        }
        if (isLoggingError()) {
          logError(e);
        }
        addFormException(new DropletException("Some transaction Exception" + " occured in applying coupon."));
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }

        if (rrm != null) {
          rrm.removeRequestEntry(myHandleMethod);
        }
      }

      return checkFormRedirect(getApplyCouponSuccessURL(), getApplyCouponErrorURL(), pRequest, pResponse);
    }

    return false;
  }

  public boolean handleRemoveCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (isLoggingDebug()) {
      vlogDebug("Remove active coupon from the profile and reprice Order.");
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleRemoveCoupon";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      try {
        removeActivePromotion();
      } finally {
        rrm.removeRequestEntry(myHandleMethod);
      }
    }

    return checkFormRedirect(getRemoveCouponSuccessURL(), getRemoveCouponErrorURL(), pRequest, pResponse);
  }

  @SuppressWarnings("rawtypes")
  private RepositoryItem getPromotionByCouponCode(String pCouponCode) throws ClaimableException {
    RepositoryItem promotion = null;
    RepositoryItem couponItem = getClaimableManager().findAndClaimCoupon(pCouponCode);
    if (couponItem != null) {
      if (couponItem.getPropertyValue("promotions") != null) {
        ChangeAwareSet promotions = (ChangeAwareSet) couponItem.getPropertyValue("promotions");
        if (promotions != null && promotions.size() > 0) {
          Iterator itr = promotions.iterator();
          while (itr.hasNext()) {
            promotion = (RepositoryItem) itr.next();
            break;
          }
        }
      }
    }

    return promotion;
  }

  @SuppressWarnings("rawtypes")
  private void removeActivePromotion() {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = null;
    td = new TransactionDemarcation();
    boolean rollback = true;
    try {
      td.begin(tm);

      Collection activePromotions = (Collection) getProfile().getPropertyValue("activePromotions");

      MFFPurchaseProcessHelper pph = (MFFPurchaseProcessHelper) getPurchaseProcessHelper();
      if (activePromotions != null) {

        Iterator iter = activePromotions.iterator();

        while (iter.hasNext()) {
          RepositoryItem promoStatus = (RepositoryItem) iter.next();
          RepositoryItem promo = (RepositoryItem) promoStatus.getPropertyValue("promotion");

          if (promo != null) {
            synchronized (getOrder()) {
              pph.removeDiscount((MFFOrderImpl) getOrder(), promo, (MutableRepositoryItem) getProfile(), getUserLocale(), getUserPricingModels(), null, null);
              
              if(isFromCheckout()){
                vlogInfo("Promo Applied in the checkout for Order:{0}, update payment groups",getOrder().getId());
                getCheckoutManager().updatePaymentGroups(false);
                getCheckoutManager().isOrderRequiresCreditCard();
              }
            }

          }
        }
      }

      removeCouponsFromOrder(getOrder());

      rollback = false;
    } catch (TransactionDemarcationException tde) {
      if (isLoggingError()) logError(tde);
    } catch (CommerceException e) {
      logError(e);
      try {
        setTransactionToRollbackOnly();
      } catch (SystemException e1) {
        logError(e1);
      }
      addFormException(new DropletException("Database error"));
    } finally {
      try {
        td.end(rollback);
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) logError(tde);
      }
    }
  }

  /**
   * Method to validate sku for Max quantity, helper method for
   * validateSkusAndProduct
   * 
   * @param pRequest
   * @param pSkuId
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  private boolean validateSkuForMaxQty(DynamoHttpServletRequest pRequest, String pSkuId, long pQuantity) {

    long defaultMaxQty = getEnvironment().getMaxQtyPerItemInOrder();
    long maxQty = defaultMaxQty;

    long currentQuantityRequested = pQuantity;

    long qtyAlreadyInCart = 0;

    CommerceItem ci = getCommerceItemForSku(pSkuId);
    if (ci != null) {
      qtyAlreadyInCart = ci.getQuantity();
    }
    long totalQtyRequested = currentQuantityRequested + qtyAlreadyInCart;
    if (isLoggingDebug()) {
      vlogDebug("currentQuantityRequested=" + currentQuantityRequested);
      vlogDebug("qtyAlreadyInCart=" + qtyAlreadyInCart);
      vlogDebug("Total Qty Requested:" + totalQtyRequested);
      vlogDebug("Max Qty Allowed:" + maxQty);
    }
    if (totalQtyRequested > maxQty) {
      // Used by cartError.jsp
      long balanceAllowedQty = maxQty - qtyAlreadyInCart;
      addMaxQuantityForSku(pSkuId, balanceAllowedQty);

      Object[] msgArgs = new Object[1];
      msgArgs[0] = maxQty;
      String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_ADD_QTY_ERROR));
      String msg = MessageFormat.format(resourceMsg, msgArgs);
      vlogError("{0} - Order:{1}", msg, getOrder().getId());
      getFormExceptionGenerator().generateException(msg, false, this, pRequest);

      return false;
    }
    return true;
  }

  /**
   * Method to Validate inventory before adding an item to cart, helper method
   * for validateSkusAndProduct
   * 
   * @param pRequest
   * @param pSkuId
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  private boolean validateInventoryForSku(DynamoHttpServletRequest pRequest, String pSkuId, long pQuantity) {
    try {
      // get the environment max limit
      long defaultMaxQty = getEnvironment().getMaxQtyPerItemInOrder();
      long maxQty = defaultMaxQty;
      
/*      RepositoryItem sku = getCatalogTools().findSKU(pSkuId);
      if(sku != null && sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER) != null && ((Integer)sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER) > 0)) {
    	  int limitPerOrder = ((Integer)sku.getPropertyValue(MFFConstants.SKU_LIMIT_PER_ORDER)).intValue();
          if(isLoggingDebug()) {
        	  logDebug("Sku limit per order " + limitPerOrder);
          }
          if(getQuantity() > limitPerOrder) {
              Object[] msgArgs = new Object[1];
              msgArgs[0] = pSkuId;
              msgArgs[1] = limitPerOrder;
              String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_OVER_QTY_LIMIT));
              String msg = MessageFormat.format(resourceMsg, msgArgs);
              getFormExceptionGenerator().generateException(msg, false, this, pRequest);
        	  return false;
          }    	  
      }*/
      StockLevel lStockLevel = null;
      if( ((MFFOrderImpl)getOrder()).isBopisOrder()) {
    	  lStockLevel = getInventoryManager().queryStoreSkuStockLevel(pSkuId, ((MFFOrderImpl)getOrder()).getBopisStore());  
      } else {
    	  lStockLevel = getInventoryManager().querySkuStockLevel(pSkuId);
      }
      
      
      

      long availableQty = 0;

      if (lStockLevel != null && lStockLevel.getStockLevel() > 0) availableQty = lStockLevel.getStockLevel();

      long currentQuantityRequested = pQuantity;

      // This item is out of stock and so cannot be added to cart
      if (availableQty <= 0) {
        // Used by cartError.jsp
        addMaxQuantityForSku(pSkuId, availableQty);
        vlogError("Available Quantity for skuId:{0} is zero - Order:{1}", pSkuId, getOrder().getId());
        getFormExceptionGenerator().generateException(MFFConstants.PRODUCT_OUT_OF_STOCK, true, this, pRequest);
        return false;
      } else if (availableQty > maxQty) { // at this point the messaging if any
                                          // must be related to enviroment max
                                          // limit
        return true; // returning true will flow the control into
                     // validateSkuForMaxQty(...)
      }

      long qtyAlreadyInCart = 0;

      CommerceItem ci = getCommerceItemForSku(pSkuId);
      if (ci != null) {
        qtyAlreadyInCart = ci.getQuantity();
      }

      vlogDebug("Current Qty Requested:" + currentQuantityRequested);
      vlogDebug("Available Qty:" + availableQty);

      // Requested Qty greater than available Qty, so cannot add this one to
      // cart.
      if (currentQuantityRequested > availableQty) {
        // Used by cartError.jsp
        long balanceAllowedQty = availableQty - qtyAlreadyInCart;
        addMaxQuantityForSku(pSkuId, balanceAllowedQty);

        Object[] msgArgs = new Object[2];
        msgArgs[0] = currentQuantityRequested;
        msgArgs[1] = availableQty;
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_QTY_NOT_AVAILABLE));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        vlogError("{0} - Order:{1}", msg, getOrder().getId());
        getFormExceptionGenerator().generateException(msg, false, this, pRequest);

        return false;
      }

      // if the sum of the requested quantity and item quantity in cart is
      // greater than available qty then generate an exception
      long totalQty=0;
      if(!isEditMode()) {
    	  totalQty = (currentQuantityRequested + qtyAlreadyInCart);
      } else {
    	  totalQty=currentQuantityRequested;
      }
      if (totalQty > availableQty) {

        Object[] msgArgs = new Object[2];
        msgArgs[0] = currentQuantityRequested;
        msgArgs[1] = availableQty;
        String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources(pRequest).getString(MFFConstants.MSG_ADD_QTY_CART_NOT_AVAILABLE));
        String msg = MessageFormat.format(resourceMsg, msgArgs);
        vlogError("{0} - Order:{1}", msg, getOrder().getId());
        getFormExceptionGenerator().generateException(msg, false, this, pRequest);
        return false;
      }

    } catch (InventoryException e) {
      vlogError(e, "There is an issue while querying inventory for skuId:{0} - Order:{1}", pSkuId, getOrder().getId());
    } 
    return true;
  }

  /**
   * Customizing OOTB functionality to set Bopis information when an item is
   * removed This will default the customer to shipTohome when customer removes
   * all items in the cart
   */
  @Override
  public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.postRemoveItemFromOrder(pRequest, pResponse);
    updateCartInfo(pRequest, pResponse);
  }

  /**
   * Update order Bopis, FFL flags based on the cart content
   * 
   * @param pRequest
   * @param pResponse
   */
  public void updateCartInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    // only update the bopis info only if there are no items in the cart
    if (getOrder().getCommerceItemCount() <= 0) {
      // only update the bopis info if it is bopisOrder
      if (((MFFOrderImpl) getOrder()).isBopisOrder()) {

        // update bopis info, set bopisOrder to false and bopis store to null
        // cart will be defaulted to shipToHome
        try {
          ((MFFOrderManager) getOrderManager()).shipMyOrderInstead(getOrder());
        } catch (CommerceException e) {
          vlogError(e, "There is an error while updating the order:{0} with bopis information", getOrder().getId());
          getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_ORDER_UPDATE_ERROR, true, this, pRequest);
        }
      }

      if (((MFFOrderImpl) getOrder()).isFFLOrder()) {
        // update ffl info, set fflOrder to false
        // cart will be defaulted to non-ffl order
        try {
          ((MFFOrderManager) getOrderManager()).updateFFLOrder(getOrder());
        } catch (CommerceException e) {
          vlogError(e, "There is an error while updating the order:{0} with ffl information", getOrder().getId());
          getFormExceptionGenerator().generateException(MFFConstants.MSG_BOPIS_ORDER_UPDATE_ERROR, true, this, pRequest);
        }
      }
    }else{
      if(getOrder().getCommerceItemCount() > 0){
    	  if (!((MFFOrderImpl) getOrder()).isBopisOrder()) {
  	        List<CommerceItem> lItems = getOrder().getCommerceItems();
  	        boolean isLTL = false;
  	        boolean isFreeFreightShipping = false;
  	        MFFPricingUtil util = new MFFPricingUtil();
  	        for (CommerceItem lItem : lItems) {
  	          RepositoryItem lSku = (RepositoryItem) lItem.getAuxiliaryData().getCatalogRef();
  	          isLTL = (Boolean) lSku.getPropertyValue(MFFConstants.SKU_LTL);
  	          isFreeFreightShipping = util.isFreeFreightItem(lItem);
  	          if(isLTL && !isFreeFreightShipping)
  	            break;
  	        }
  	        
  	        // 2427 - Mark as LTL only if there are LTL items that arent marked for free freight
  	        // else this kicks off the LTL calculators
  	        if(isLTL && !isFreeFreightShipping){
  	          ((MFFHardgoodShippingGroup) getOrder().getShippingGroups().get(0)).setShippingMethod("LTL-Truck");
  	        }else{
  	          ((MFFHardgoodShippingGroup) getOrder().getShippingGroups().get(0)).setShippingMethod("Standard");
  	        }
    	  }
    	  
      }
    }
  }
  
  @Override
  public boolean handleSetOrderByCommerceId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    if(getEnvironment().isEnableSetOrderByCommerceIdDebug()){
        vlogInfo("Inside handleSetOrderByCommerceId : OrderId - {0}  Date - {1} ", getOrder().getId(), DateUtil.getDateTimeNow());
    }
    return super.handleSetOrderByCommerceId(pRequest, pResponse);
    
  }

  /**
   * Helper method to look up the commerce item for a particular sku.
   * 
   * @param pSkuId
   * @return CommerceItem
   */
  public CommerceItem getCommerceItemForSku(String pSkuId) {
    if (pSkuId == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    List<MFFCommerceItemImpl> commerceItems = getOrder().getCommerceItems();
    Iterator<MFFCommerceItemImpl> iter = commerceItems.iterator();
    while (iter.hasNext()) {
      MFFCommerceItemImpl ci = (MFFCommerceItemImpl) iter.next();
      if (pSkuId.equals(ci.getCatalogRefId())) {
        return ci;
      }
    }
    return null;
  }

  public void addFormException(DynamoHttpServletRequest pRequest, DropletException exc) {
    if (pRequest != null) vlogInfo("");
    super.addFormException(exc);
  }

  /**
   * Helper method to reset Max quantities
   * 
   * @return
   */
  private void resetMaxQuantities() {
    if (mMaxQuantities == null) {
      mMaxQuantities = new HashMap<String, Long>();
    } else {
      mMaxQuantities.clear();
    }
  }

  private void addMaxQuantityForSku(String pSkuId, long pMaxQty) {
    if (mMaxQuantities == null) {
      resetMaxQuantities();
    }

    mMaxQuantities.put(pSkuId, Long.valueOf(pMaxQty));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void addCouponToOrder(Order pOrder, String pCoupon) throws CommerceException {
    if (isLoggingDebug()) {
      vlogDebug("Adding coupon:" + pCoupon + " to Order:" + pOrder);
    }

    MFFOrderImpl order = (MFFOrderImpl) pOrder;
    List coupons = order.getCouponCodes();
    if (coupons == null) {
      coupons = new ArrayList();
    }
    coupons.add(pCoupon);
    order.setCouponCodes(coupons);
    ((MFFOrderManager) getOrderManager()).updateOrder(order, "addCouponToOrder:" + pCoupon);
  }

  @SuppressWarnings({ "rawtypes" })
  private void removeCouponsFromOrder(Order pOrder) throws CommerceException {
    MFFOrderImpl order = (MFFOrderImpl) pOrder;
    List coupons = order.getCouponCodes();
    if (coupons != null) {
      synchronized (getOrder()) {
        order.setCouponCodes(null);
        ((MFFOrderManager) getOrderManager()).updateOrder(order, "After RemoveCouponFromOrder");
      }
    }
  }

  /**
   * @return the mMaxQuantities
   */
  public HashMap<String, Long> getMaxQuantities() {
    return mMaxQuantities;
  }

  public String getProdType() {
    return mProdType;
  }

  public void setProdType(String pProdType) {
    mProdType = pProdType;
  }

  public List<MFFInlineDropletFormException> getFormFieldExceptions() {
    return null;
  }

  public List<DropletException> getNonFormFieldExceptions() {
    return null;
  }

  public MFFGiftlistFormHandler getGiftListFormHandler() {
    return giftListFormHandler;
  }

  public void setGiftListFormHandler(MFFGiftlistFormHandler pGiftListFormHandler) {
    giftListFormHandler = pGiftListFormHandler;
  }

  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  public MFFEnvironment getEnvironment() {
    return mEnvironment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
    mEnvironment = pEnvironment;
  }

  public boolean isAddFromWishList() {
    return mAddFromWishList;
  }

  public void setAddFromWishList(boolean pAddFromWishList) {
    this.mAddFromWishList = pAddFromWishList;
  }

  public String getAddItemToGiftlistSuccessURL() {
    return mAddItemToGiftlistSuccessURL;
  }

  public void setAddItemToGiftlistSuccessURL(String pAddItemToGiftlistSuccessURL) {
    mAddItemToGiftlistSuccessURL = pAddItemToGiftlistSuccessURL;
  }

  public String getAddItemToGiftlistErrorURL() {
    return mAddItemToGiftlistErrorURL;
  }

  public void setAddItemToGiftlistErrorURL(String pAddItemToGiftlistErrorURL) {
    mAddItemToGiftlistErrorURL = pAddItemToGiftlistErrorURL;
  }

  public String getWishListId() {
    return mWishListId;
  }

  public void setWishListId(String pWishListId) {
    mWishListId = pWishListId;
  }

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

  public String getChooseStoreSuccessURL() {
    return mChooseStoreSuccessURL;
  }

  public void setChooseStoreSuccessURL(String pChooseStoreSuccessURL) {
    mChooseStoreSuccessURL = pChooseStoreSuccessURL;
  }

  public String getChooseStoreErrorURL() {
    return mChooseStoreErrorURL;
  }

  public void setChooseStoreErrorURL(String pChooseStoreErrorURL) {
    mChooseStoreErrorURL = pChooseStoreErrorURL;
  }

  public boolean isStorePickup() {
    return mStorePickup;
  }

  public void setStorePickup(boolean pStorePickup) {
    mStorePickup = pStorePickup;
  }

  public String getBopisStore() {
    return mBopisStore;
  }

  public void setBopisStore(String pBopisStore) {
    mBopisStore = pBopisStore;
  }

  public String getShipMyOrderSuccessURL() {
    return mShipMyOrderSuccessURL;
  }

  public void setShipMyOrderSuccessURL(String pShipMyOrderSuccessURL) {
    mShipMyOrderSuccessURL = pShipMyOrderSuccessURL;
  }

  public String getShipMyOrderErrorURL() {
    return mShipMyOrderErrorURL;
  }

  public void setShipMyOrderErrorURL(String pShipMyOrderErrorURL) {
    mShipMyOrderErrorURL = pShipMyOrderErrorURL;
  }

  public boolean isFromProduct() {
    return mFromProduct;
  }

  public void setFromProduct(boolean pFromProduct) {
    mFromProduct = pFromProduct;
  }

  public double getGiftCardMaxDenommination() {
    return mGiftCardMaxDenommination;
  }

  public void setGiftCardMaxDenommination(double pGiftCardMaxDenommination) {
    mGiftCardMaxDenommination = pGiftCardMaxDenommination;
  }

  public double getMaxGifCardsValueInCart() {
    return mMaxGifCardsValueInCart;
  }

  public void setMaxGifCardsValueInCart(double pMaxGifCardsValueInCart) {
    mMaxGifCardsValueInCart = pMaxGifCardsValueInCart;
  }

  public String getTaxExempSelected() {
    return mTaxExempSelected;
  }

  public void setTaxExempSelected(String pTaxExempSelected) {
    mTaxExempSelected = pTaxExempSelected;
  }

  public String getTaxExemptionSuccessURL() {
    return mTaxExemptionSuccessURL;
  }

  public void setTaxExemptionSuccessURL(String pTaxExemptionSuccessURL) {
    mTaxExemptionSuccessURL = pTaxExemptionSuccessURL;
  }

  public String getTaxExemptionErrorURL() {
    return mTaxExemptionErrorURL;
  }

  public void setTaxExemptionErrorURL(String pTaxExemptionErrorURL) {
    mTaxExemptionErrorURL = pTaxExemptionErrorURL;
  }

  public String getCouponCode() {
    return mCouponCode;
  }

  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }

  public String getApplyCouponSuccessURL() {
    return mApplyCouponSuccessURL;
  }

  public void setApplyCouponSuccessURL(String pApplyCouponSuccessURL) {
    mApplyCouponSuccessURL = pApplyCouponSuccessURL;
  }

  public String getApplyCouponErrorURL() {
    return mApplyCouponErrorURL;
  }

  public void setApplyCouponErrorURL(String pApplyCouponErrorURL) {
    mApplyCouponErrorURL = pApplyCouponErrorURL;
  }

  public String getAppliedCouponDescription() {
    return mAppliedCouponDescription;
  }

  public void setAppliedCouponDescription(String pAppliedCouponDescription) {
    mAppliedCouponDescription = pAppliedCouponDescription;
  }

  public double getGiftCardMinDenommination() {
    return mGiftCardMinDenommination;
  }

  public void setGiftCardMinDenommination(double pGiftCardMinDenommination) {
    mGiftCardMinDenommination = pGiftCardMinDenommination;
  }

  public String getRemoveCouponSuccessURL() {
    return mRemoveCouponSuccessURL;
  }

  public void setRemoveCouponSuccessURL(String pRemoveCouponSuccessURL) {
    mRemoveCouponSuccessURL = pRemoveCouponSuccessURL;
  }

  public String getRemoveCouponErrorURL() {
    return mRemoveCouponErrorURL;
  }

  public void setRemoveCouponErrorURL(String pRemoveCouponErrorURL) {
    mRemoveCouponErrorURL = pRemoveCouponErrorURL;
  }

  public String getRemoveCouponCode() {
    return mRemoveCouponCode;
  }

  public void setRemoveCouponCode(String pRemoveCouponCode) {
    mRemoveCouponCode = pRemoveCouponCode;
  }
  
	public MFFGiftlistTools getGiftlistTools() {
		return mGiftlistTools;
	}

	public void setGiftlistTools(MFFGiftlistTools pGiftlistTools) {
		mGiftlistTools = pGiftlistTools;
	}

  public boolean isFromCheckout() {
    return mFromCheckout;
  }

  public void setFromCheckout(boolean pFromCheckout) {
    mFromCheckout = pFromCheckout;
	}

	public void setEditValue(Map<String, Object> pEditValue) {
		mEditValue = pEditValue;
	}

	public Map<String, Object> getEditValue() {
		return mEditValue;
	}

	public String getEditCartItem() {
		return mEditCartItem;
	}

	public void setEditCartItem(String pEditCartItem) {
		mEditCartItem = pEditCartItem;
	}
	
	public String getAddActionType() {
		return mAddActionType;
	}

	public void setAddActionType(String pAddActionType) {
		mAddActionType = pAddActionType;
	}
	
	public StoreLocatorTools getStoreLocatorTools() {
		return mStoreLocatorTools;
	}

	public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
		mStoreLocatorTools = pStoreLocatorTools;
	}

}
