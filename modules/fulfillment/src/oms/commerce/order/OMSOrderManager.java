/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.pricing.MFFTaxPriceInfo;
import com.mff.commerce.promotion.MffPromotionTools;
import com.mff.commerce.states.MFFShippingGroupStates;

import atg.adapter.gsa.ChangeAwareSet;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupCommerceItemRelationship;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.states.CommerceItemStates;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ContactInfo;
import atg.core.util.Range;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import oms.commerce.payment.MFFPaymentManager;
import oms.commerce.states.OMSOrderStates;

public class OMSOrderManager extends SimpleOrderManager {

	private static final String itemDescriptorPgName = "paymentGroup";

	private PricingTools mPricingTools;

	private int mRemorsePeriod;

	private String omsAgentProfileId;

	private String omsOrderCommentPrefix;

	private String orderCommentItemDescriptorName;

	private Repository omsOrderRepository;

	@SuppressWarnings("rawtypes")
	private HashMap orderExclusionMap;

	HashMap<String, String> orderCloneExclusionsMap;

	private String mGiftCardProductID;

	protected String LINE_SEPARATOR = System.getProperty("line.separator");

	private MFFPaymentManager paymentManager;

	private List<String> mDamagedReasonCodes = new ArrayList<String>();

	private MffPromotionTools mPromotionTools;
	
	private boolean splitOrderEnabled;
	
	private boolean processPriceOverrides;

	public boolean isProcessPriceOverrides() {
		return processPriceOverrides;
	}

	public void setProcessPriceOverrides(boolean pProcessPriceOverrides) {
		processPriceOverrides = pProcessPriceOverrides;
	}

	/**
	 * Default Constructor
	 */
	public OMSOrderManager() {
		super();
	}

	/**
	 * Returns the value of the gwp item(s) added by promoid pGiftPromoId
	 * We do this only for gift card items (related to 2414)
	 * Intent is to subtract the value of the gift from the qualifier
	 * so we know the exact amount to return to the user
	 * when either the qualifier is returned or the gift is returned
	 * or both are returned
	 * @param pOrder
	 * @param pGiftPromoId
	 * @return
	 */
	public double getGiftValueForPromo_1 (MFFOrderImpl pOrder, String pGiftPromoId, boolean gcGWPOnly) {
		double giftAmount = 0.0;
		MFFCommerceItemImpl item=null;
		Iterator<CommerceItem> itemIter = pOrder.getCommerceItems().iterator();

		while (itemIter.hasNext()) {
			item = (MFFCommerceItemImpl)itemIter.next();
			if(gcGWPOnly) {
				if((Boolean)item.getPropertyValue("gwp") && isGiftCardItem(item)) {
					Iterator adjIter = item.getPriceInfo().getAdjustments().iterator();
					while(adjIter.hasNext()) {
						PricingAdjustment adj = (PricingAdjustment) adjIter.next();
						if(adj.getPricingModel()!= null && adj.getPricingModel().getRepositoryId().equalsIgnoreCase(pGiftPromoId)) {
							//return item;
							giftAmount += adj.getTotalAdjustment();
						}
					}
				}
			} else {
				if((Boolean)item.getPropertyValue("gwp")) {
					Iterator adjIter = item.getPriceInfo().getAdjustments().iterator();
					while(adjIter.hasNext()) {
						PricingAdjustment adj = (PricingAdjustment) adjIter.next();
						if(adj.getPricingModel()!= null && adj.getPricingModel().getRepositoryId().equalsIgnoreCase(pGiftPromoId)) {
							//return item;
							giftAmount += adj.getTotalAdjustment();
						}
					}
				}
				
			}
 		}
		return -1 * giftAmount;		
	}
	
	public ArrayList<MFFCommerceItemImpl> getGiftItemsForPromo(MFFOrderImpl pOrder, String pGiftPromoId) {
		ArrayList <MFFCommerceItemImpl> giftItems = new ArrayList<MFFCommerceItemImpl>();
		MFFCommerceItemImpl item=null;
		Iterator<CommerceItem> itemIter = pOrder.getCommerceItems().iterator();

		while (itemIter.hasNext()) {
			item = (MFFCommerceItemImpl)itemIter.next();
			if((Boolean)item.getPropertyValue("gwp")) {
				Iterator adjIter = item.getPriceInfo().getAdjustments().iterator();
				while(adjIter.hasNext()) {
					PricingAdjustment adj = (PricingAdjustment) adjIter.next();
					if(adj.getPricingModel()!= null && adj.getPricingModel().getRepositoryId().equalsIgnoreCase(pGiftPromoId)
							&& !giftItems.contains(item)) {
						//return item;
						giftItems.add(item);
					}
				}
			}
		}
		return giftItems;	  
	}
	public boolean isGiftCardPromo_1(MFFOrderImpl pOrder, String pGiftPromoId) {
		boolean isGiftCardPromo = false;
		ArrayList <MFFCommerceItemImpl> giftItems = new ArrayList<MFFCommerceItemImpl>();
		MFFCommerceItemImpl item=null;
		Iterator<CommerceItem> itemIter = pOrder.getCommerceItems().iterator();

		while (itemIter.hasNext()) {
			item = (MFFCommerceItemImpl)itemIter.next();
			if((Boolean)item.getPropertyValue("gwp")) {
				Iterator adjIter = item.getPriceInfo().getAdjustments().iterator();
				while(adjIter.hasNext()) {
					PricingAdjustment adj = (PricingAdjustment) adjIter.next();
					if(adj.getPricingModel()!= null && adj.getPricingModel().getRepositoryId().equalsIgnoreCase(pGiftPromoId)
							&& !giftItems.contains(item)) {
						//return item;
						giftItems.add(item);
						if(isGiftCardItem(item)) {
							isGiftCardPromo = true;
						}
						
					}
				}
			}
		}
		return isGiftCardPromo;	  
	}
	/**
	 * This function will take an order and split all the commerceItems. It will
	 * also clone the shippingGroup to preserve the original order. The cloned
	 * shippingGroup and Item's state will be set to ATG OOTB "REMOVED" state.
	 *
	 * @param pOrder
	 * @throws CommerceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void splitCommerceItems(OrderImpl pOrder) throws CommerceException {
		if (isLoggingDebug()) {
			logDebug("OrderManager:::inside splitCommerceItems");
		}
		try {
			List items = pOrder.getCommerceItems();
			List itemsToSplit = new ArrayList();
			List itemsNotToSplit = new ArrayList();
			Map clonedShipGroups = new HashMap();

			Iterator itemsIter = items.iterator();
			// identify the items to split
			while (itemsIter.hasNext()) {
				CommerceItemImpl commerceItem = (CommerceItemImpl) itemsIter.next();
				long quantity = commerceItem.getQuantity();
				// Only need to split for Gift Card CommerceItems
				if (quantity > 1 && isGiftCardItem(commerceItem)) {
					itemsToSplit.add(commerceItem);
				} else {
					itemsNotToSplit.add(commerceItem);
				}
			}
			// iterate and split commerceItems
			Iterator splitIter = itemsToSplit.iterator();

			while (splitIter.hasNext()) {
				splitItem(pOrder, (CommerceItemImpl) splitIter.next(), clonedShipGroups);
			}
			// Now move other items from the removed shipping group.
			Iterator noSplitIt = itemsNotToSplit.iterator();
			if (isLoggingDebug()) {
				logDebug("number of items we didnt split:::" + itemsNotToSplit.size());
			}
			while (noSplitIt.hasNext()) {
				CommerceItem item = (CommerceItem) noSplitIt.next();
				ShippingGroupCommerceItemRelationship shipRelation = (ShippingGroupCommerceItemRelationship) item.getShippingGroupRelationships().get(0);
				HardgoodShippingGroup shipGroup = (HardgoodShippingGroup) clonedShipGroups.get(shipRelation.getShippingGroup());
				if (shipGroup != null) {
					// if its not null it means the shipgroup is cloned so move
					// the item to new ship group
					if (isLoggingDebug()) {
						logDebug("commerceItem with id::" + item.getId() + " is moving from ::" + shipRelation.getShippingGroup().getId() + " to :::" + shipGroup.getId());
					}
					this.moveItemToShippingGroup(pOrder, item, item.getQuantity(), shipRelation.getShippingGroup(), shipGroup);
					getCommerceItemManager().generateRangeForItem(item);
				} else {
					if (isLoggingDebug()) {
						logDebug("commerceItem with id::" + item.getId() + " is not moving");
					}
				}
			}
			this.updateOrder(pOrder);
		} catch (Exception e) {
			if (isLoggingDebug()) {
				logError("splitCommerceItems Exception::", e);
			}
			throw new CommerceException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void splitItem(OrderImpl pOrder, CommerceItemImpl pItem, Map clonedShipGroups) throws Exception {

		int totalItemsCloned = 0;
		List clonedItems = new ArrayList();
		List removedItems = new ArrayList();

		if (isLoggingDebug()) {
			logDebug("comm item id is:::" + pItem.getId());
			logDebug("comm item's qunatity is is:::" + pItem.getQuantity());
		}
		// pItem.setStateAsString(CommerceItemStates.REMOVED);
		pItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(CommerceItemStates.REMOVED));
		removedItems.add(pItem);
		Iterator relIter = pItem.getShippingGroupRelationships().iterator();
		while (relIter.hasNext()) {
			ShippingGroupCommerceItemRelationship shipRelation = (ShippingGroupCommerceItemRelationship) relIter.next();
			HardgoodShippingGroup cuShipGroup = (HardgoodShippingGroup) shipRelation.getShippingGroup();
			if (isLoggingDebug()) {
				logDebug("ship group id iss:::" + shipRelation.getShippingGroup().getId());
			}
			// find out if this ship group is already cloned if not clone one
			HardgoodShippingGroup shipGroup = (HardgoodShippingGroup) clonedShipGroups.get(shipRelation.getShippingGroup());
			if (shipGroup == null) {
				if (isLoggingDebug()) {
					logDebug("shipGroup is not already cloned so clone now");
				}
				// clone shipping group
				shipGroup = cloneShippingGroup(cuShipGroup, false);
				// cuShipGroup.setStateAsString(ShippingGroupStates.REMOVED);
				cuShipGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.REMOVED));
				// mark removed shipping group price to zero and update the raw
				// ship price just for the record
				double shipAmnt = cuShipGroup.getPriceInfo().getAmount();
				cuShipGroup.getPriceInfo().setAmount(0d);
				cuShipGroup.getPriceInfo().setRawShipping(shipAmnt);
				// add into cloned map
				clonedShipGroups.put(cuShipGroup, shipGroup);
				// add this ship group to order
				this.getShippingGroupManager().addShippingGroupToOrder(pOrder, shipGroup);
			}
			if (isLoggingDebug()) {
				logDebug("quantity with this ship group is:::" + shipRelation.getQuantity());
			}
			long quantity = shipRelation.getQuantity();
			for (int i = 1; i <= quantity; i++) {
				if (isLoggingDebug()) {
					logDebug("creating item #:::" + i);
				}
				// clone comm item
				CommerceItemImpl newItem = cloneCommerceItem(pItem);
				// copy priceInfo objects
				copyPriceInfo(newItem, pItem, i);
				if (isLoggingDebug()) {
					logDebug("adding to order");
				}
				// Copy promo amount map.
				//newItem.setPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP, pItem.getPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP));
				this.getCommerceItemManager().addAsSeparateItemToOrder(pOrder, newItem);
				
				
				/*
				 *  When there is a gwp gift item in the original order
				 *  and it has a qty > 1, then the original item will be removed
				 *  and GWP Gift Card items of qty 1 each will be created to match the
				 *  original qty 
				 *  				 *  
				 *  We have to ensure that the gwpMarkers from the original gift commerce item
				 *  are copied over. (Note: this processing is only when the gwp is a gift card
				 *  and qty is > 1)
				 */
				 
				MFFCommerceItemImpl mffNewItem = (MFFCommerceItemImpl)newItem;
				if (mffNewItem.isGiftCard()) {
					copyGwpMarkers(pItem,mffNewItem);
				}
				

				this.getCommerceItemManager().addItemQuantityToShippingGroup(pOrder, newItem.getId(), shipGroup.getId(), newItem.getQuantity());

				// update the ship item range values
				// OOTB returns will error out if these are not set
				getCommerceItemManager().generateRangeForItem(mffNewItem);

				clonedItems.add(newItem);
				totalItemsCloned++;
			}
		}
		if (isLoggingDebug()) {
			logDebug("totalItemsCloned:::" + totalItemsCloned);
		}
		// now split pricing
		splitPriceInfos(clonedItems, (MFFCommerceItemImpl)pItem);
		
		if(isProcessPriceOverrides()) {
			splitPriceOverrides(clonedItems, (MFFCommerceItemImpl)pItem);
		}
		
		// Now split promotion amount in promo amount map.
		//if (pItem.getPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP) != null)
			//splitPromoAmount(pOrder, clonedItems, pItem);

		// Now delete the removed Items
		Iterator remIter = removedItems.iterator();
		while (remIter.hasNext()) {
			CommerceItemImpl item = (CommerceItemImpl) remIter.next();
			if (isLoggingDebug()) {
				logDebug("removing item from order with Id::" + item.getId());
			}
			getCommerceItemManager().removeItemFromOrder(pOrder, item.getId());
		}
	}

	private MFFCommerceItemImpl copyGwpMarkers(CommerceItemImpl pSrcItem, MFFCommerceItemImpl pNewItem) {
		
		if(pSrcItem == null || pNewItem == null)
			return null;
		
		Collection markers = (Collection) pSrcItem.getPropertyValue("gwpMarkers");
		
		if (markers != null) {
			Iterator markerIterator = markers.iterator();
			while (markerIterator.hasNext()) {
				Object markerItem = (Object) markerIterator.next();
				if (markerItem instanceof MutableRepositoryItem) {
					RepositoryItem clonedItem=null;
					try {
						clonedItem = RepositoryUtils.cloneItem((MutableRepositoryItem)markerItem, true, null, null, (MutableRepository)omsOrderRepository, null);
						((MutableRepositoryItem)clonedItem).setPropertyValue("owner", pNewItem.getId());
						((MutableRepositoryItem)clonedItem).setPropertyValue("key", "atg.gwp");
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
					((ChangeAwareSet)pNewItem.getPropertyValue("gwpMarkers")).add(clonedItem);
				} else {
					vlogWarning("Could not cast the marker to repository item to update the commerce item. Dest commerce item id set to {0}", pNewItem.getId());
				}
			}
		} else {
			vlogWarning("Could not determine the markers for the commerce item {0}", pSrcItem.getId());
		}		
		return pNewItem;
	}
	
	public void splitPriceOverrides(List pItemsList, MFFCommerceItemImpl pItem) throws CommerceException {
		if (isLoggingDebug()) {
			logDebug("inside splitPriceOverrides");
		}
		int listSize = pItemsList.size();
		MFFItemPriceInfo mffItemPriceInfo = (MFFItemPriceInfo)pItem.getPriceInfo();
		if(mffItemPriceInfo.getFinalReasonCode() != null && mffItemPriceInfo.getFinalReasonCode().equals("manuallyApplied")) {
			double[] priceOverrideDiscount = splitAmount(getPriceOverrideDiscount(mffItemPriceInfo), listSize);
			
			Iterator iter = pItemsList.iterator();
			int itemNum = 1;
			while (iter.hasNext()) {
				MFFCommerceItemImpl newItem = (MFFCommerceItemImpl) iter.next();
				MFFItemPriceInfo priceInfo = (MFFItemPriceInfo) newItem.getPriceInfo();
				if (itemNum != listSize) {
					
					PricingAdjustment adjustment = new PricingAdjustment("Agent price override", null, null,
							priceOverrideDiscount[0], 1);
					priceInfo.getAdjustments().add(adjustment);

				} else {
					PricingAdjustment adjustment = new PricingAdjustment("Agent price override", null, null,
							priceOverrideDiscount[0] + priceOverrideDiscount[1], 1);
					priceInfo.getAdjustments().add(adjustment);
				}
				itemNum++;
			}

		}
	}
	
	public double getPriceOverrideDiscount(MFFItemPriceInfo pItemPriceInfo) {
		double lPriceOverride = 0.0;
		  List <PricingAdjustment> lPricingAdjustments = pItemPriceInfo.getAdjustments();
		  for (PricingAdjustment lPricingAdjustment : lPricingAdjustments) {
			  if (lPricingAdjustment.getAdjustmentDescription() != null && lPricingAdjustment.getAdjustmentDescription().equals("Agent price override")) {
				  lPriceOverride += lPricingAdjustment.getTotalAdjustment();
			  }
		  }
		  return lPriceOverride;
	}
	/**
	 * This function will take an item and split it's price equally into
	 * multiple(list) of items
	 *
	 * @param pItemsList
	 * @param pItem
	 * @throws CommerceException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void splitPriceInfos(List pItemsList, MFFCommerceItemImpl pItem) throws CommerceException {
		if (isLoggingDebug()) {
			logDebug("inside splitPriceInfos");
		}
		try {
			int listSize = pItemsList.size();
			MFFItemPriceInfo mffItemPriceInfo = (MFFItemPriceInfo)pItem.getPriceInfo();
			// split item's priceInfo's amount
			double[] priceInfoAmt = splitAmount(mffItemPriceInfo.getAmount(), listSize);
			// split item's priceInfo's discountAmount
			double[] priceInfoDiscountAmount = splitAmount(mffItemPriceInfo.getDiscountAmount(), listSize);
			// split item's priceInfo's effectivePrice
			double[] priceInfoEffectivePrice = splitAmount(mffItemPriceInfo.getEffectivePrice(), listSize);			
			// split item's discount share
			double[] discountShare = splitAmount(mffItemPriceInfo.getOrderDiscountShare(), listSize);
			// splt item's tax amount
			double[] taxInfoAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getAmount() : 0.0d,listSize);
			// splt item's raw total amount
			double[] rawTotalAmt = splitAmount(mffItemPriceInfo.getRawTotalPrice(), listSize);

			// splt item's city tax amount
			double[] cityTaxAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getCityTax() : 0.0d,listSize);
			// splt item's county tax amount
			double[] countyTaxAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getCountyTax() : 0.0d,listSize);
			// splt item's state tax amount
			double[] stateTaxAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getStateTax() : 0.0d,listSize);
			// splt item's country tax amount
			double[] countryTaxAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getCountryTax() : 0.0d,listSize);
			// splt item's district tax amount
			double[] disTaxAmt = splitAmount(pItem.getTaxPriceInfo() != null ? pItem.getTaxPriceInfo().getDistrictTax() : 0.0d,listSize);
			// splt item's other tax amount
			// TODO: double[] otherTaxAmt =
			// splitAmount(((TaxPriceInfo)pItem.getTaxPriceInfo()).getOtherTax(),listSize);
			// split Item's every Adjustment
			List adjusList = mffItemPriceInfo.getAdjustments();
			Iterator adjusIter = adjusList.iterator();
			List adjusSplitAmts = new ArrayList();
			while (adjusIter.hasNext()) {
				boolean neg = false;
				PricingAdjustment pricingAdjustment = (PricingAdjustment) adjusIter.next();
				double totalAdjustment = 0.0d;
				totalAdjustment = pricingAdjustment.getTotalAdjustment();
				if (totalAdjustment < 0) {
					neg = true;
				}
				totalAdjustment = Math.abs(totalAdjustment);
				double[] amounts = splitAmount(totalAdjustment, listSize);
				if (neg) {
					amounts[0] = -(amounts[0]);
					amounts[1] = -(amounts[1]);
				}
				adjusSplitAmts.add(amounts);
			}

			// set split amounts among items
			Iterator iter = pItemsList.iterator();
			int itemNum = 1;
			while (iter.hasNext()) {
				MFFCommerceItemImpl newItem = (MFFCommerceItemImpl) iter.next();
				MFFItemPriceInfo priceInfo = (MFFItemPriceInfo) newItem.getPriceInfo();
				if (itemNum != listSize) {
					priceInfo.setAmount(priceInfoAmt[0]);
					priceInfo.setOrderDiscountShare(discountShare[0]);
					// TODO:priceInfo.setOrigOrderDiscountShare(discountShare[0]);
					priceInfo.setRawTotalPrice(rawTotalAmt[0]);
					priceInfo.setEffectivePrice(priceInfoEffectivePrice[0]);
					priceInfo.setDiscountAmount(priceInfoDiscountAmount[0]);
					// set all tax amounts
				  newItem.getTaxPriceInfo().setCityTax(cityTaxAmt[0]);
				  newItem.getTaxPriceInfo().setCountyTax(countyTaxAmt[0]);
				  newItem.getTaxPriceInfo().setStateTax(stateTaxAmt[0]);
				  newItem.getTaxPriceInfo().setCountryTax(countryTaxAmt[0]);
				  newItem.getTaxPriceInfo().setDistrictTax(disTaxAmt[0]);
					 /* ((TaxPriceInfo)newItem.getTaxPriceInfo()).setOtherTax(otherTaxAmt[0]);
				  newItem.getTaxPriceInfo().setAmount(taxInfoAmt[0]);*/
					double amount = cityTaxAmt[0] + countyTaxAmt[0] + stateTaxAmt[0] + countryTaxAmt[0] + disTaxAmt[0];
					newItem.getTaxPriceInfo().setAmount(amount);

					Iterator adjusIterer = priceInfo.getAdjustments().iterator();
					int x = 0;
					while (adjusIterer.hasNext()) {
						PricingAdjustment srcAdjust = (PricingAdjustment) adjusIterer.next();
						srcAdjust.setTotalAdjustment(((double[]) adjusSplitAmts.get(x))[0]);
						x++;
					}
				} else {
					priceInfo.setAmount(priceInfoAmt[0] + priceInfoAmt[1]);
					priceInfo.setRawTotalPrice(rawTotalAmt[0] + rawTotalAmt[1]);
					priceInfo.setOrderDiscountShare(discountShare[0] + discountShare[1]);
					priceInfo.setEffectivePrice(priceInfoEffectivePrice[0]+priceInfoEffectivePrice[1]);
					priceInfo.setDiscountAmount(priceInfoDiscountAmount[0]+priceInfoDiscountAmount[1]);
					
					// priceInfo.setOrigOrderDiscountShare(discountShare[0] +discountShare[1]);

					newItem.getTaxPriceInfo().setCityTax(cityTaxAmt[0]+cityTaxAmt[1]);
					newItem.getTaxPriceInfo().setCountyTax(countyTaxAmt[0]+countyTaxAmt[1]);
					newItem.getTaxPriceInfo().setStateTax(stateTaxAmt[0]+stateTaxAmt[1]);
					newItem.getTaxPriceInfo().setCountryTax(countryTaxAmt[0]+countryTaxAmt[1]);
					newItem.getTaxPriceInfo().setDistrictTax(disTaxAmt[0]+disTaxAmt[1]);
					/* ((TaxPriceInfo)newItem.getTaxPriceInfo()).setOtherTax(otherTaxAmt[0]+otherTaxAmt[1]);
					   newItem.getTaxPriceInfo().setAmount(taxInfoAmt[0]+taxInfoAmt[1]); */

					double amount = cityTaxAmt[0] + cityTaxAmt[1] + countyTaxAmt[0]  + countyTaxAmt[1] + stateTaxAmt[0] + stateTaxAmt[1] + countryTaxAmt[0] + countryTaxAmt[1]
					                + disTaxAmt[0] + disTaxAmt[1];
          newItem.getTaxPriceInfo().setAmount(amount);

					Iterator adjusIterer = priceInfo.getAdjustments().iterator();
					int j = 0;
					while (adjusIterer.hasNext()) {
						PricingAdjustment srcAdjust = (PricingAdjustment) adjusIterer.next();
						double[] adjusAmts = (double[]) adjusSplitAmts.get(j);
						srcAdjust.setTotalAdjustment(adjusAmts[0] + adjusAmts[1]);
						j++;
					}
				}
				itemNum++;
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("splitPriceInfos Exception::" + e);
			}
			throw new CommerceException(e);
		}
	}

	/**
	 * This function will copy the priceInfo objects from orignal Item to cloned
	 * Item.
	 *
	 * @param destPriceInfo
	 * @param srcPriceInfo
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void copyPriceInfo(CommerceItemImpl pDestItem, CommerceItemImpl pSrcItem, int currentItemNumber) {
		if (isLoggingDebug()) {
			logDebug("inside copyPriceInfo");
		}
		long qtyAdjusted=0;
		// copy ItemPriceInfo
		MFFItemPriceInfo destPriceInfo = (MFFItemPriceInfo) pDestItem.getPriceInfo();
		MFFItemPriceInfo srcPriceInfo = (MFFItemPriceInfo) pSrcItem.getPriceInfo();
		destPriceInfo.setAmount(srcPriceInfo.getAmount());
		destPriceInfo.setCurrencyCode(srcPriceInfo.getCurrencyCode());
		destPriceInfo.setDiscounted(srcPriceInfo.isDiscounted());
		destPriceInfo.setAmountIsFinal(srcPriceInfo.isAmountIsFinal());
		destPriceInfo.setListPrice(srcPriceInfo.getListPrice());
		destPriceInfo.setSalePrice(srcPriceInfo.getSalePrice());
		destPriceInfo.setRawTotalPrice(srcPriceInfo.getRawTotalPrice());
		destPriceInfo.setOnSale(srcPriceInfo.isOnSale());
		destPriceInfo.setOrderDiscountShare(srcPriceInfo.getOrderDiscountShare());
		// TODO:destPriceInfo.setOrigOrderDiscountShare(srcPriceInfo.getOrderDiscountShare());
		destPriceInfo.setQuantityAsQualifier(srcPriceInfo.getQuantityAsQualifier());
		destPriceInfo.setQuantityDiscounted(srcPriceInfo.getQuantityDiscounted());
		destPriceInfo.setDiscountAmount(srcPriceInfo.getDiscountAmount());
		destPriceInfo.setEffectivePrice(srcPriceInfo.getEffectivePrice());
		destPriceInfo.setFinalReasonCode(srcPriceInfo.getFinalReasonCode());

		Iterator itemAdIter = srcPriceInfo.getAdjustments().iterator();
		while (itemAdIter.hasNext()) {
			PricingAdjustment srcAdjust = (PricingAdjustment) itemAdIter.next();
			if (!srcAdjust.getAdjustmentDescription().equals(OMSOrderConstants.AGETNT_PRICE_ADJUS_DESC)) {
				
				// 2414 - When giftCard skus are in the order, they are broken into separate commerce items
				// if the gift card is from a promo, then the qty adjusted should be handled
				if(((MFFCommerceItemImpl)pSrcItem).isGiftCard()) {
					qtyAdjusted=1;
				} else {
					qtyAdjusted = srcAdjust.getQuantityAdjusted();
				}
				PricingAdjustment adjustment = new PricingAdjustment(srcAdjust.getAdjustmentDescription(), srcAdjust.getPricingModel(), srcAdjust.getManualPricingAdjustment(),
						srcAdjust.getTotalAdjustment(), qtyAdjusted);
				destPriceInfo.getAdjustments().add(adjustment);
			} else {
				logError("Not implemented");
				/*
				 * TODO: CSRPricingAdjustment adjustment =new
				 * CSRPricingAdjustment(srcAdjust.getAdjustmentDescription(),
				 * srcAdjust.getPricingModel(),
				 * srcAdjust.getManualPricingAdjustment(),
				 * srcAdjust.getTotalAdjustment(),
				 * srcAdjust.getQuantityAdjusted(),
				 * ((CSRPricingAdjustment)srcAdjust).getAdjustmentReasonCode());
				 * //adjustment.setAdjustmentReasonCode(((CSRPricingAdjustment)
				 * srcAdjust).getAdjustmentReasonCode());
				 * destPriceInfo.getAdjustments().add(adjustment);
				 */
			}
		}
		// copy item level tax info
		Iterator itemPriceDetailsIter = srcPriceInfo.getCurrentPriceDetails().iterator();

		while (itemPriceDetailsIter.hasNext()) {
			if (isLoggingDebug()) {
				logDebug("Adding detailed item price info to Item Price Info:");
			}
			DetailedItemPriceInfo srcDetailedItemPriceInfo = (DetailedItemPriceInfo) itemPriceDetailsIter.next();

			DetailedItemPriceInfo detailedItemPriceInfo = new DetailedItemPriceInfo();

			detailedItemPriceInfo.copyDetailProperties(srcDetailedItemPriceInfo);
			detailedItemPriceInfo.setQuantity(1);

			// set tax
			// split tax amounts:
			double[] amounts = splitAmount(srcDetailedItemPriceInfo.getTax(), (int) pSrcItem.getQuantity());

			BigDecimal destDetailedItemTax;
			int totalQuantity = (int) pSrcItem.getQuantity();
			if (currentItemNumber == totalQuantity) {
			  // Add the remaining amount to the last item.
				destDetailedItemTax = new BigDecimal(amounts[1] + amounts[0]).setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				destDetailedItemTax = new BigDecimal(amounts[0]).setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			detailedItemPriceInfo.setTax(destDetailedItemTax.doubleValue());

			// set amount.
			BigDecimal destDetailedAmount = new BigDecimal(srcDetailedItemPriceInfo.getAmount() / pSrcItem.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP);
			detailedItemPriceInfo.setAmount(destDetailedAmount.doubleValue());

			// Commerce item are split down to quantity 1, so setting range to
			// (0,0)
			// as the size((getHighBound() - getLowBound()) + 1L;) needs to be
			// equal to quantity
			Range range = new Range(0, 0);
			vlogDebug("range " + range.toString());
			detailedItemPriceInfo.setRange(range);
			destPriceInfo.getCurrentPriceDetails().add(detailedItemPriceInfo);

		}
		// copy the taxPriceInfo
		TaxPriceInfo destTaxInfo=(TaxPriceInfo)((MFFCommerceItemImpl)pDestItem).getTaxPriceInfo();
		TaxPriceInfo srcTaxInfo=(TaxPriceInfo)((MFFCommerceItemImpl)pSrcItem).getTaxPriceInfo();
		if(srcTaxInfo != null){
      destTaxInfo.setAmount(srcTaxInfo.getAmount());
      destTaxInfo.setCurrencyCode(srcTaxInfo.getCurrencyCode());
      destTaxInfo.setDiscounted(srcTaxInfo.isDiscounted());
      destTaxInfo.setAmountIsFinal(srcTaxInfo.isAmountIsFinal());
      destTaxInfo.setCityTax(srcTaxInfo.getCityTax());
      destTaxInfo.setCountyTax(srcTaxInfo.getCountyTax());
      destTaxInfo.setStateTax(srcTaxInfo.getStateTax());
      destTaxInfo.setCountryTax(srcTaxInfo.getCountryTax());
      ((MFFTaxPriceInfo)destTaxInfo).setCountyTaxInfo(((MFFTaxPriceInfo)srcTaxInfo).getCountyTaxInfo());
      /*destTaxInfo.setEffectiveRate(srcTaxInfo.getEffectiveRate());
      destTaxInfo.setStateTaxRate(srcTaxInfo.getStateTaxRate());
      destTaxInfo.setDistrictTaxRate(srcTaxInfo.getDistrictTaxRate());
      destTaxInfo.setCityTaxRate(srcTaxInfo.getCityTaxRate());
      destTaxInfo.setCountyTaxRate(srcTaxInfo.getCountyTaxRate());
      destTaxInfo.setOtherTaxRate(srcTaxInfo.getOtherTaxRate());*/
      destTaxInfo.getShippingItemsTaxPriceInfos().putAll(srcTaxInfo.getShippingItemsTaxPriceInfos());
		}
	}

	/**
	 * @return A map of item descriptor names and the properties to exclude
	 *         while cloning order
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getOrderExclusionsMap() {
		if (getOrderCloneExclusionsMap() == null)
			return null;

		if (orderExclusionMap == null)
			orderExclusionMap = new HashMap();
		/*
		 * TODO: Uncomment once development is complete else return
		 * orderCloneExclusionMap;
		 */

		Iterator<String> it = getOrderCloneExclusionsMap().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String[] val = getOrderCloneExclusionsMap().get(key).split("~");
			List<String> valList = Arrays.asList(val);
			orderExclusionMap.put(key, valList);
		}

		return orderExclusionMap;
	}

	/**
	 * splitAmount
	 *
	 * @param orginalAmount
	 *            Amount to be split
	 * @param quantity
	 * @param amount
	 *            each items split amount
	 * @param leftOverAmount
	 *            any left over amount
	 */
	public double[] splitAmount(double orginalAmount, int quantity) {
		if (isLoggingDebug()) {
			logDebug("Entering split Amount");
		}

		BigDecimal bigOrginalAmount = new BigDecimal(orginalAmount);
		double ratio = 1.0d / quantity;
		BigDecimal bigRatio = new BigDecimal(ratio);
		BigDecimal bigShare = bigOrginalAmount.multiply(bigRatio);
		bigShare = format(bigShare, 2);

		BigDecimal bigTotalShare = new BigDecimal(0);
		for (int i = 0; i < quantity; i++)
			bigTotalShare = bigTotalShare.add(bigShare);

		bigTotalShare = format(bigTotalShare, 2);

		BigDecimal bigLeftOver = bigOrginalAmount.subtract(bigTotalShare);
		bigLeftOver = format(bigLeftOver, 2);

		double amounts[] = new double[2];
		amounts[0] = bigShare.doubleValue();
		amounts[1] = bigLeftOver.doubleValue();

		if (isLoggingDebug()) {
			logDebug("-----orginalAmount: " + orginalAmount + " quantity: " + quantity + " amount: " + amounts[0] + " leftOverAmount: " + amounts[1]);
			logDebug("Exiting split Amount");
		}
		return amounts;
	}

	public boolean isGiftCardItem(CommerceItem pCommerceItem) {
    MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pCommerceItem;
    vlogDebug("commerceItem id {0} , ProductId {1}", lCommerceItem.getId(), lCommerceItem.getProductId());
    if (lCommerceItem.isGiftCard())
    {
      vlogDebug("commerceItem id {0} : is a Gift card Item.", lCommerceItem.getId());
      return true;
    }
    else
    {
      MFFCatalogTools catTools = (MFFCatalogTools) getCatalogTools();
      if(catTools.isGCProduct(lCommerceItem.getProductId()))
      {
          return true;
      }
    }
    return false;
  }

	/**
	 *
	 * @param n
	 * @param prec
	 * @return
	 */
	static BigDecimal format(BigDecimal n, int prec) {
		return n.setScale(prec, BigDecimal.ROUND_HALF_UP);
	}

	/**
   * Pro ration logic based on quantity
   *
   * @param totalAmount
   * @param pQuantityToSplit
   * @return
   */
  public HashMap<Integer, Double> prorateOnQuantity(double totalAmount, double pQuantityToSplit) {

    vlogDebug("prorateOnQuantity : totalAmount - {0},pQuantityToSplit - {1} ", totalAmount, pQuantityToSplit);

    if (Double.isNaN(totalAmount)) {
      totalAmount = 0.0d;
    }

    HashMap<Integer, Double> proratedAmounts = new HashMap<Integer, Double>();
    BigDecimal runningAmount = new BigDecimal(totalAmount);
    BigDecimal quantity = new BigDecimal(pQuantityToSplit);

    if (pQuantityToSplit == 1) {
      proratedAmounts.put(1, totalAmount);
      return proratedAmounts;
    }

    for (int i = 1; i <= pQuantityToSplit; i++) {

      BigDecimal price = runningAmount.divide(quantity, 2, RoundingMode.HALF_UP);
      proratedAmounts.put(i, price.doubleValue());

      runningAmount = runningAmount.subtract(price);
      quantity = quantity.subtract(BigDecimal.ONE);

    }

    vlogDebug("prorateOnQuantity : proratedAmounts - {0}", proratedAmounts);
    return proratedAmounts;
  }

	/**
	 * This function makes a copy of given shipping group
	 *
	 * @param pShipGroup
	 * @return
	 * @throws CommerceException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public HardgoodShippingGroup cloneShippingGroup(HardgoodShippingGroup pShipGroup, boolean isBackOrder) throws CommerceException, InstantiationException, IllegalAccessException {
		if (isLoggingDebug()) {
			logDebug("Entering cloneShippingGroup");
		}
		HardgoodShippingGroup shipGroup = (HardgoodShippingGroup) getShippingGroupManager().cloneShippingGroup(pShipGroup);
		((ContactInfo)shipGroup.getShippingAddress()).setPhoneNumber(((ContactInfo)pShipGroup.getShippingAddress()).getPhoneNumber());
		ShippingPriceInfo spi = (ShippingPriceInfo) getOrderTools().getDefaultShippingPriceInfoClass().newInstance();
		spi.setAmountIsFinal(true);
		if (isBackOrder) {
			// As the ATG OOTB description is the id of the shipping group, so
			// in this
			// case it's the id of parent shipping group, override with new one
			// coz
			// we don't want tax on cloned one. As shipping is one time
			shipGroup.setDescription(shipGroup.getId());
			// TODO:
			// shipGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(ShippingGroupStates.BACK_ORDERED));
			shipGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.PENDING_MERCHANT_ACTION));
			spi.setAmount(0d);
			spi.setCurrencyCode("USD");
			shipGroup.setPriceInfo(spi);
		} else {
			spi.setAmount(pShipGroup.getPriceInfo().getAmount());
			spi.setCurrencyCode(pShipGroup.getPriceInfo().getCurrencyCode());
			spi.setDiscounted(pShipGroup.getPriceInfo().isDiscounted());
			// It is assumed these objects won't be modified unless reloaded.
			spi.getAdjustments().addAll(pShipGroup.getPriceInfo().getAdjustments());
			spi.setRawShipping(pShipGroup.getPriceInfo().getRawShipping());
			shipGroup.setPriceInfo(spi);
			// shipGroup.setPriceInfo(pShipGroup.getPriceInfo());
		}
		// manually add pricing attributes, as I'm copying the objects as it is,
		/*
		 * shipGroup.setGiftBoxTaxPriceInfo(pShipGroup.getGiftBoxTaxPriceInfo());
		 * shipGroup
		 * .setGiftWrapTaxPriceInfo(pShipGroup.getGiftWrapTaxPriceInfo());
		 * shipGroup.setGiftBox(pShipGroup.isGiftBox());
		 * shipGroup.setGiftWrap(pShipGroup.isGiftWrap());
		 * shipGroup.setTaxAreaCode(pShipGroup.getTaxAreaCode());
		 */
		return shipGroup;
	}

	/**
	 * This function takes commerceItem and returns a cloned copy of it.
	 *
	 * @param pItem
	 * @return
	 * @throws CommerceException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
  public CommerceItemImpl cloneCommerceItem(CommerceItemImpl pItem) throws CommerceException, InstantiationException, IllegalAccessException {
		if (isLoggingDebug()) {
			logDebug("Entering cloneCommerceItem");
		}
		MFFCommerceItemImpl newItem = (MFFCommerceItemImpl) this.getCommerceItemManager().createCommerceItem(getOrderTools().getDefaultCommerceItemType(), pItem.getCatalogRefId(),
				pItem.getAuxiliaryData().getCatalogRef(), pItem.getAuxiliaryData().getProductId(), pItem.getAuxiliaryData().getProductRef(), 1, pItem.getCatalogKey(), pItem.getCatalogId(), null);

		TaxPriceInfo tpi = (TaxPriceInfo)getOrderTools().getDefaultTaxPriceInfoClass().newInstance();
    newItem.setTaxPriceInfo((MFFTaxPriceInfo)tpi);
    
    // Set Previous Allocation
    Set<String> prevAllocation = ((MFFCommerceItemImpl) pItem).getPreviousAllocation();
    if(prevAllocation != null && prevAllocation.size() > 0){
      ((MFFCommerceItemImpl) newItem).setPreviousAllocation(prevAllocation);
    }
    
		// Set the MinAge
		if(((MFFCommerceItemImpl) pItem).getMinimumAge() != null){
  		int lMinAge = ((MFFCommerceItemImpl) pItem).getMinimumAge();
  		((MFFCommerceItemImpl) newItem).setMinimumAge(lMinAge);
		}

		// Set the FFL flag
		boolean lFFlFlag = ((MFFCommerceItemImpl) pItem).getFFL();
		((MFFCommerceItemImpl) newItem).setFFL(lFFlFlag);

		// Set DropShip flag
		boolean lDropShip = ((MFFCommerceItemImpl) pItem).getDropShip();
		((MFFCommerceItemImpl) newItem).setDropShip(lDropShip);
    
		// 2414: copy over the GWP flag
		newItem.setPropertyValue("gwp", pItem.getPropertyValue("gwp"));

		String gwpPromoId = ((MFFCommerceItemImpl) pItem).getGwpPromoId();
		((MFFCommerceItemImpl) newItem).setGwpPromoId(gwpPromoId);

    // Set Gift Card Flag
    boolean isGiftCardFlag = ((MFFCommerceItemImpl) pItem).isGiftCard();
    ((MFFCommerceItemImpl) newItem).setGiftCard(isGiftCardFlag);

		return newItem;
	}

	/**
	 *
	 * @param pOrder
	 */
	@SuppressWarnings({ "rawtypes" })
	public void updateOrderState(OrderImpl pOrder) {
		// removed
		boolean ignore = false;
		// initial(highly unlikely), backorder, sent_to_wms, resend_to_wms,
		// error_from_wms
		boolean active = false;
		// cancelled
		boolean cancel = false;
		// shipped
		boolean shipped = false;

		// Iterate through order's shippin groups and change order state
		// accordingly
		List shipGroups = pOrder.getShippingGroups();
		Iterator it = shipGroups.iterator();
		while (it.hasNext()) {
			HardgoodShippingGroup shipGroup = (HardgoodShippingGroup) it.next();
			{
				if (shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.REMOVED)) {
					ignore = true;
				} else if (shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.CANCELLED)) {
					cancel = true;
				} else if (shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.SHIPPED)) {
					shipped = true;
				} /*else if (shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.INITIAL) || shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.BACK_ORDERED)
						|| shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.SENT_TO_WMS) || shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.RESEND_TO_WMS)
						|| shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.ERROR_FROM_WMS)) {
					active = true;
				}*/else if (shipGroup.getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.INITIAL)) {
					active = true;
				}
			}
		}
		if (shipped && active) {
			pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.PARTIALLY_SHIPPED));
		} else if (shipped && !active) {
			pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.SHIPPED));
		} /*else if (active) {
			if (!(pOrder.getStateAsString().equalsIgnoreCase(OMSOrderStates.PRE_SENT_WMS)))
				pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.PROCESSING_WMS));
		}*/ else if (cancel) {
			pOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(OMSOrderStates.CANCELLED));
			// TODO cancel reason code
		}
		if (isLoggingDebug()) {
			logDebug("ignore is:::" + ignore);
			logDebug("active is:::" + active);
			logDebug("cancel is:::" + cancel);
			logDebug("shipped is:::" + shipped);
			logDebug("order state is:::" + pOrder.getStateAsString());
		}
	}

	/**
	 * This funciton makes a copy of given shipping group
	 *
	 * @param pShipGroup
	 * @return
	 * @throws CommerceException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public HardgoodShippingGroup cloneExchangeShippingGroup(HardgoodShippingGroup pShipGroup) throws CommerceException, InstantiationException, IllegalAccessException {
		if (isLoggingDebug()) {
			logDebug("Entering clone Exchange ShippingGroup");
		}
		HardgoodShippingGroup shipGroup = (HardgoodShippingGroup) getShippingGroupManager().cloneShippingGroup(pShipGroup);
		ShippingPriceInfo spi = (ShippingPriceInfo) getOrderTools().getDefaultShippingPriceInfoClass().newInstance();
		spi.setAmountIsFinal(true);
		shipGroup.setShippingMethod(pShipGroup.getShippingMethod());

		shipGroup.setDescription(shipGroup.getId());
		spi.setAmount(0d);
		spi.setCurrencyCode("USD");
		shipGroup.setPriceInfo(spi);

		return shipGroup;
	}


	/**
	 * The following method creates the relationship between the payment group
	 * and GC settlements performed against the payment.
	 *
	 * @param paymentToSettlementAmountRel
	 *            the relationship between the payment ids and the settlement
	 *            amount generated by the method
	 *            {@link #createDebitSettlements(Order, String, String)}
	 * @param pCommerceItem
	 *            the commerce item id for of the egift card
	 * @param pGiftCardDetails
	 *            the gift card details
	 * @throws CommerceException
	 *             when a repository exception occurs or the relation map does
	 *             not contain data
	 */
	public void createGCDebitSettlements(HashMap<String, Double> paymentToSettlementAmountRel, String pCommerceItem, Map<String, List<String>> pGiftCardDetails) throws CommerceException {

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

				lSettlementAmount = getPricingTools().round(lSettlementAmount);
				vlogDebug("Creating gift card credit settlements for payment id {0}, settlement amount {1}", lPaymentID, lSettlementAmount);
				MutableRepositoryItem mutablePgItem = ((MutableRepository) getOmsOrderRepository()).getItemForUpdate(lPaymentID, itemDescriptorPgName);
				List<String> gcDetails = (List<String>) pGiftCardDetails.get(pCommerceItem);
				String giftCardNo = gcDetails.get(0);
				String giftCardPin = gcDetails.get(1);
				vlogDebug("Gift card number {0} gift card pin {1}", giftCardNo, giftCardPin);
				createGiftCardPaymentSettlement(mutablePgItem, giftCardNo, giftCardPin, lSettlementAmount);
			}

		} catch (RepositoryException e) {
			vlogError("Could not process the GC debit settlement for order id {0} commerce item id", pCommerceItem);
			vlogError(e, "Repository exception occurred while creating the GC payment settlement relationship");
			throw new CommerceException(e);
		}

		vlogDebug("Exiting createGCDebitSettlements - pOrder, listOfPayments");
	}


	public void createPaymentSettlementsForGiftCard(Order pOrder, double pAmountToBeIssued, PaymentGroup pPaymentGroup, String pInvoiceId) throws CommerceException {
		vlogDebug("Entering issueNewGiftCardAndSettlements - pOrder, pAmountToBeIssued, pPaymentGroup, pInvoiceId");
		if (pAmountToBeIssued <= 0) {
			vlogError("Amount should be positive {0}", pAmountToBeIssued);
			throw new CommerceException("Amount should be greater than zero");
		}

		try {
			createPaymentSettlement((PaymentGroupImpl) pPaymentGroup, -1 * pAmountToBeIssued, pInvoiceId);
			// Now call the GC allocation instead of the settlement manager

		} catch (RepositoryException e) {
			vlogError(e, "A repository exception occurred while creating a credit card settlement credit card id = {0} amount to settle = {1}", pPaymentGroup.getId(), pAmountToBeIssued);
			throw new CommerceException("Error creating the settlement record", e);
		}
		vlogInfo("Creating new gc allocation entry and settlement records for the following order id {0}, amountToBeIssued {1}, PaymentGroup id {2}, return request id = {3}", pOrder.getId(),
				pAmountToBeIssued, pPaymentGroup.getId(), pInvoiceId);

		vlogDebug("Exiting issueNewGiftCardAndSettlements - pOrder, pAmountToBeIssued, pPaymentGroup, pInvoiceId");
	}

	@SuppressWarnings("unchecked")
	public void createPaymentSettlement(PaymentGroupImpl pg, double settleAmount, String invoiceId) throws RepositoryException {

		vlogDebug("Entering createPaymentSettlement - pg, settleAmount, invoiceId");
		vlogDebug("Payment group id = {0}, settlement amount = {1}, invoice id = {2}", pg.getId(), settleAmount, invoiceId);

		MutableRepository lOrderRep = (MutableRepository) getOrderTools().getOrderRepository();
		MutableRepositoryItem pgSettleItem = lOrderRep.createItem("pgSettlement");
		pgSettleItem.setPropertyValue("invoiceId", invoiceId);
		pgSettleItem.setPropertyValue("date", new Date());
		pgSettleItem.setPropertyValue("settlementAmount", settleAmount);
		lOrderRep.addItem(pgSettleItem);

		List<RepositoryItem> pgSettles = (List<RepositoryItem>) pg.getPropertyValue("settlements");
		if (pgSettles == null) {
			pgSettles = new ArrayList<RepositoryItem>();
		}
		pgSettles.add(pgSettleItem);
		pg.setPropertyValue("settlements", pgSettles);
		vlogDebug("Exiting createPaymentSettlement - pg, settleAmount, invoiceId");
	}

	@SuppressWarnings("unchecked")
	public void createGiftCardPaymentSettlement(MutableRepositoryItem pg, String gCNum, String gCPin, double settleAmount) throws RepositoryException {
		MutableRepository lOrderRep = (MutableRepository) getOrderTools().getOrderRepository();
		MutableRepositoryItem pgSettleItem = lOrderRep.createItem("pgGcSettlement");
		pgSettleItem.setPropertyValue("giftCardNumber", gCNum);
		pgSettleItem.setPropertyValue("giftCardPin", gCPin);
		pgSettleItem.setPropertyValue("date", new Date());
		pgSettleItem.setPropertyValue("settlementAmount", settleAmount);
		lOrderRep.addItem(pgSettleItem);
		List<RepositoryItem> pgGcSettles = (List<RepositoryItem>) pg.getPropertyValue("giftCardSettlements");
		if (pgGcSettles == null) {
			pgGcSettles = new ArrayList<RepositoryItem>();
		}
		pgGcSettles.add(pgSettleItem);
		pg.setPropertyValue("giftCardSettlements", pgGcSettles);

	}

	/**
	/**
	 * This method returns the Id of the hard good shipping group used by EXTN.
	 * It is assumed that EXTN will have only single hardgood shipping group. It
	 * also excludes shipping groups in removed state.
	 *
	 * @param order
	 *            The OMS Order
	 * @return Id of the order's shipping group
	 */
	@SuppressWarnings("unchecked")
	public String getEXTNHGShippingGroupId(Order order) {
		String sgId = null;
		List<ShippingGroup> sgs = order.getShippingGroups();
		for (ShippingGroup sg : sgs) {
			if (sg instanceof HardgoodShippingGroup) {
				if (((HardgoodShippingGroup) sg).getStateAsString() != null && ((HardgoodShippingGroup) sg).getStateAsString().equalsIgnoreCase(MFFShippingGroupStates.REMOVED)) {
					vlogDebug("Shipping Group state is removed skipping this");
					continue;
				} else {
					sgId = sg.getId();
					vlogDebug("Found a shipping group that is not removed state sgId = {0}", sgId);
					break;
				}
			} else {
				vlogDebug("Skipping the shipping group since it is not a hard good shipping group {0}", sg.getId());
			}
		}
		vlogDebug("Returning the following sg id {0}", sgId);
		return sgId;
	}

	 /* Split order level discount across commerce items
	 *
	 * @param pItemsList
	 * @param pItem
	 * @throws CommerceException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void splitPromoAmount(OrderImpl pOrder, List pItemsList, CommerceItemImpl pItem) throws CommerceException {
		if (pItemsList == null || pItem == null) {
			return;
		}
		Map promoMap = (Map) pItem.getPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP);
		if (promoMap == null) {
			return;
		}

		Map promoMapOne = new HashMap();
		Map promoMapLast = new HashMap();

		if (promoMap != null) {
			Set promotions = promoMap.entrySet();
			Iterator promotionsIter = promotions.iterator();
			while (promotionsIter.hasNext()) {
				Map.Entry entry = (Map.Entry) promotionsIter.next();
				double promoAmount = ((Double) entry.getValue()).doubleValue();
				if (isLoggingDebug()) {
					logDebug("Order level promo discount amount is :" + promoAmount);
				}
				double[] amounts = splitAmount(promoAmount, (int) pItem.getQuantity());
				if (isLoggingDebug()) {
					logDebug("Order level promo discount amount is :" + promoAmount);
					logDebug("Order level promo discount per item is: " + amounts[0]);
					logDebug("Order level promo discount per item remaining amount is :" + amounts[1]);
				}
				promoMapOne.put(entry.getKey(), amounts[0]);
				promoMapLast.put(entry.getKey(), amounts[0] + amounts[1]);

			}
		}

		for (int i = 0; i < pItemsList.size(); i++) {
			if (i < (pItemsList.size() - 1)) {
				((CommerceItemImpl) pItemsList.get(i)).setPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP, promoMapOne);
			} else {
				((CommerceItemImpl) pItemsList.get(i)).setPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP, promoMapLast);
			}
		}
	}

	/**
	 * The following method adds a note to the CSR using the agent profile
	 * defined by omsAgentProfileId.
	 *
	 * @param pOrder
	 *            the order for which the note is to be added
	 * @param pComment
	 *            the comment to be added
	 * @throws RepositoryException
	 *             when it fails to persist the order.
	 */
	public void createOrderComment(Order pOrder, String pComment) throws RepositoryException {
	  createOrderComment(pOrder, pComment,null, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createOrderComment(Order pOrder, String pComment, RepositoryItem pAgentProfile, String storeNumber) throws RepositoryException {
	  
	  vlogDebug("Entering createOrderComment - pOrder, pComment");

    if (pOrder != null && pComment != null) {

      StringBuilder sb = new StringBuilder();
      sb.append(getOmsOrderCommentPrefix());
      sb.append(" : ");
      sb.append(pComment);
      /*sb.append(" - Previous Order State: ");
      sb.append(StateDefinitions.ORDERSTATES.getStateString(pOrder.getState()));*/
      String lComment = sb.toString();

      MutableRepository orderRepository = (MutableRepository) getOrderTools().getOrderRepository();

      MutableRepositoryItem comment = null;

      comment = orderRepository.createItem(getOrderCommentItemDescriptorName());
      if (comment != null) {
        Date creationDate = new Date();
        comment.setPropertyValue("comment", lComment);
        comment.setPropertyValue("creationDate", creationDate);
        comment.setPropertyValue("owner", pOrder.getId());
        if(pAgentProfile != null){
          comment.setPropertyValue("agent", pAgentProfile.getRepositoryId());
        }else{
          comment.setPropertyValue("agent", getOmsAgentProfileId());
        }
        if(storeNumber !=null) {
        	comment.setPropertyValue("storeId", storeNumber);
        }
        vlogDebug("createOrderComment - pOrder :{0}, pComment :{1}, pStoreNumber :{2}",pOrder,pComment,storeNumber);
        Collection comments = (Collection) ((OrderImpl) pOrder).getRepositoryItem().getPropertyValue("comments");
        comments.add(comment);
      }
    }
    vlogDebug("Exiting createOrderComment - pOrder, pComment");
	}

	/**
	 * Split the shipping charge and tax across all of the commerce items.
	 *
	 * @param pOrder
	 *            ATG Order
	 */
	/*@SuppressWarnings("unchecked")
	public void splitShippingCharge(Order pOrder) {
		long lCtr = 0;

		vlogDebug("Begin splitShippingCharge ...");
		MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;

	// Sum the amount for all items on the order
    double lOrderTotal = 0;
    double lGiftCardTotal=0;
    int giftCardCount = 0;

		// Get the Shipping Charge for this order
		double lShipping = pOrder.getPriceInfo().getShipping();
		double lShippingRemainder = lShipping;

		// Get ShippingDiscount
		double lShippingDiscount = getShippingDiscount(lOrder);
		double lShippingDiscountRemainder = lShippingDiscount;

		HashMap<String,Double> shipTaxMap = getShippingTaxMap(lOrder);
		// Get shipping Tax
		double lShippingTax = 0.0d;
		if(shipTaxMap != null && shipTaxMap.get("shippingTax") != null){
		  lShippingTax = shipTaxMap.get("shippingTax");
		}
		double lShippingTaxRemainder = lShippingTax;

		double lShippingCityTax = 0.0d;
    if(shipTaxMap != null && shipTaxMap.get("shippingCityTax") != null){
      lShippingCityTax = shipTaxMap.get("shippingCityTax");
    }
    double lShippingCityTaxRemainder = lShippingCityTax;

    double lShippingCountyTax = 0.0d;
    if(shipTaxMap != null && shipTaxMap.get("shippingCountyTax") != null){
      lShippingCountyTax = shipTaxMap.get("shippingCountyTax");
    }
    double lShippingCountyTaxRemainder = lShippingCountyTax;

    double lShippingStateTax = 0.0d;
    if(shipTaxMap != null && shipTaxMap.get("shippingStateTax") != null){
      lShippingStateTax = shipTaxMap.get("shippingStateTax");
    }
    double lShippingStateTaxRemainder = lShippingStateTax;

    double lShippingDistrictTax = 0.0d;
    if(shipTaxMap != null && shipTaxMap.get("shippingDistrictTax") != null){
      lShippingDistrictTax = shipTaxMap.get("shippingDistrictTax");
    }
    double lShippingDistrictTaxRemainder = lShippingDistrictTax;

    double lShippingCountryTax = 0.0d;
    if(shipTaxMap != null && shipTaxMap.get("shippingCountryTax") != null){
      lShippingCountryTax = shipTaxMap.get("shippingCountryTax");
    }
    double lShippingCountryTaxRemainder = lShippingCountryTax;

		vlogDebug("Processing order number {0} - Shipping {1}, ShippingTax {2}, ShippingCityTax {3}, ShippingCountyTax {4}, ShippingStateTax {5}, ShippingDistrictTax {6} ShippingCountryTax {7}",
		              lOrder.getOrderNumber(), lShipping, lShippingTax,lShippingCityTax,lShippingCountyTax,lShippingStateTax,lShippingDistrictTax,lShippingCountryTax);

		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		for (CommerceItem lCommerceItem : lCommerceItems) {
			// add amount to Order Total
		  boolean isGiftCardItem = isGiftCardItem(lCommerceItem);
		  if(!isGiftCardItem){
				double lAmount = lCommerceItem.getPriceInfo().getAmount();
				lOrderTotal = lOrderTotal + lAmount;
			} else{
				double lAmount = lCommerceItem.getPriceInfo().getAmount();
				lGiftCardTotal = lGiftCardTotal + lAmount;
				giftCardCount++;
			}

		}
		vlogDebug("OrderId {0} total: {1}", lOrder.getId(), lOrderTotal);

		vlogDebug("Number of GiftCards in the Order: {0}", giftCardCount);

		// split charges between giftcards if Order only contain gift cards
		if (giftCardCount == lCommerceItems.size()) {

			if (lShipping > 0 || lShippingTax > 0) {
				vlogWarning("Order {0} is a GiftCard Only Order, Splitting Shipping Charges between {1} giftCards", lOrder.getId(), giftCardCount);
				// Calculate each commerce items share of the shipping/tax
				for (CommerceItem lCommerceItem : lCommerceItems) {
					MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
					lCtr++;
					double lAmount = lCommerceItem.getPriceInfo().getAmount();
					double lRatio = lAmount / lGiftCardTotal;
					double lCIShipping = getPricingTools().round(lShipping * lRatio);
					double lCIShippingDiscount = getPricingTools().round(lShippingDiscount * lRatio);
					double lCIShippingTax = getPricingTools().round(lShippingTax * lRatio);
					double lCIShippingCityTax = getPricingTools().round(lShippingCityTax * lRatio);
					double lCIShippingCountyTax = getPricingTools().round(lShippingCountyTax * lRatio);
					double lCIShippingStateTax = getPricingTools().round(lShippingStateTax * lRatio);
					double lCIShippingDistrictTax = getPricingTools().round(lShippingDistrictTax * lRatio);
					double lCIShippingCountryTax = getPricingTools().round(lShippingCountryTax * lRatio);

					lShippingRemainder = lShippingRemainder - lCIShipping;
					lShippingDiscountRemainder = lShippingDiscountRemainder - lCIShippingDiscount;
					lShippingTaxRemainder = lShippingTaxRemainder - lCIShippingTax;
					lShippingCityTaxRemainder = lShippingCityTaxRemainder - lCIShippingCityTax;
		      lShippingCountyTaxRemainder =  lShippingCountyTaxRemainder - lCIShippingCountyTax;
		      lShippingStateTaxRemainder =  lShippingStateTaxRemainder - lCIShippingStateTax;
		      lShippingDistrictTaxRemainder = lShippingDistrictTaxRemainder - lCIShippingDistrictTax;
		      lShippingCountryTaxRemainder = lShippingCountryTaxRemainder - lCIShippingCountryTax;

					// Set Shipping and Tax charges
					lEXTNCommerceItem.setShipping(lCIShipping);
					lEXTNCommerceItem.setShippingDiscount(lCIShippingDiscount);
					lEXTNCommerceItem.setShippingTax(lCIShippingTax);
					lEXTNCommerceItem.setShippingCityTax(lCIShippingCityTax);
	        lEXTNCommerceItem.setShippingCountyTax(lCIShippingCountyTax);
	        lEXTNCommerceItem.setShippingStateTax(lCIShippingStateTax);
	        lEXTNCommerceItem.setShippingDistrictTax(lCIShippingDistrictTax);
	        lEXTNCommerceItem.setShippingCountryTax(lCIShippingCountryTax);
					lEXTNCommerceItem.setChanged(true);

					vlogDebug("order: {0} Item: {1} Shipping: {2} ShippingTax: {3}  ShippingCityTax {4}, ShippingCountyTax {5}, ShippingStateTax {6}, ShippingDistrictTax {7} ShippingCountryTax {8}, ShippingDiscount {9}",
	            lOrder.getId(), lCommerceItem.getId(), lCIShipping, lCIShippingTax,lCIShippingCityTax,lCIShippingCountyTax,lCIShippingStateTax,lCIShippingDistrictTax,lCIShippingCountryTax,lCIShippingDiscount);

					// Account for the shipping remainder
					if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingRemainder != 0) {
						double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShipping() + lShippingRemainder);
						vlogDebug("Adjusting Shipping of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingRemainder, lRoundedAmt);
						lEXTNCommerceItem.setShipping(lRoundedAmt);
					}

					if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingDiscountRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDiscount() + lShippingDiscountRemainder);
            vlogDebug("Adjusting ShippingDiscount of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDiscountRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingDiscount(lRoundedAmt);
          }

					// Account for the shipping tax remainder
					if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingTaxRemainder != 0) {
						double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingTax() + lShippingTaxRemainder);
						vlogDebug("Adjusting Shipping of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingTaxRemainder, lRoundedAmt);
						lEXTNCommerceItem.setShippingTax(lRoundedAmt);
					}

					if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingCityTaxRemainder != 0) {
	          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCityTax() + lShippingCityTaxRemainder);
	          vlogDebug("Adjusting ShippingCityTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCityTaxRemainder, lRoundedAmt);
	          lEXTNCommerceItem.setShippingCityTax(lRoundedAmt);
	        }

	        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingCountyTaxRemainder != 0) {
	          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountyTax() + lShippingCountyTaxRemainder);
	          vlogDebug("Adjusting ShippingCountyTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountyTaxRemainder, lRoundedAmt);
	          lEXTNCommerceItem.setShippingCountyTax(lRoundedAmt);
	        }

	        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingStateTaxRemainder != 0) {
	          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingStateTax() + lShippingStateTaxRemainder);
	          vlogDebug("Adjusting ShippingStateTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingStateTaxRemainder, lRoundedAmt);
	          lEXTNCommerceItem.setShippingStateTax(lRoundedAmt);
	        }

	        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingDistrictTaxRemainder != 0) {
	          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDistrictTax() + lShippingDistrictTaxRemainder);
	          vlogDebug("Adjusting ShippingDistrictTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDistrictTaxRemainder, lRoundedAmt);
	          lEXTNCommerceItem.setShippingDistrictTax(lRoundedAmt);
	        }

	        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingCountryTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountryTax() + lShippingCountryTaxRemainder);
            vlogDebug("Adjusting ShippingCountryTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountryTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingCountryTax(lRoundedAmt);
          }

				}
			}
			else{
				vlogDebug("No need distribute the tax as {0} is a gift card only order and no shipping or shipping tax found", lOrder.getId());
			}
		} else {

			// Calculate each commerce items share of the shipping/tax
			for (CommerceItem lCommerceItem : lCommerceItems) {
				MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
				double lCIShipping = 0.0;
				double lCIShippingDiscount = 0.0;
				double lCIShippingTax = 0.0;
				double lCIShippingCityTax = 0.0;
				double lCIShippingCountyTax = 0.0;
				double lCIShippingStateTax = 0.0;
				double lCIShippingDistrictTax = 0.0;
				double lCIShippingCountryTax = 0.0;
				//if (!(lEXTNCommerceItem.getAuxiliaryData().getProductId()).equalsIgnoreCase(getGiftCardProductID())) {
				if (!isGiftCardItem(lEXTNCommerceItem)) {
					double lAmount = lCommerceItem.getPriceInfo().getAmount();
					double lRatio = lAmount / lOrderTotal;
					lCIShipping = getPricingTools().round(lShipping * lRatio);
					lCIShippingDiscount = getPricingTools().round(lShippingDiscount * lRatio);
					lCIShippingTax = getPricingTools().round(lShippingTax * lRatio);
					lCIShippingCityTax = getPricingTools().round(lShippingCityTax * lRatio);
					lCIShippingCountyTax = getPricingTools().round(lShippingCountyTax * lRatio);
					lCIShippingStateTax = getPricingTools().round(lShippingStateTax * lRatio);
					lCIShippingDistrictTax = getPricingTools().round(lShippingDistrictTax * lRatio);
					lCIShippingCountryTax = getPricingTools().round(lShippingCountryTax * lRatio);
					lCtr++;
				}
				lShippingRemainder = lShippingRemainder - lCIShipping;
				lShippingDiscountRemainder = lShippingDiscountRemainder - lCIShippingDiscount;
				lShippingTaxRemainder = lShippingTaxRemainder - lCIShippingTax;
				lShippingCityTaxRemainder = lShippingCityTaxRemainder - lCIShippingCityTax;
				lShippingCountyTaxRemainder =  lShippingCountyTaxRemainder - lCIShippingCountyTax;
				lShippingStateTaxRemainder =  lShippingStateTaxRemainder - lCIShippingStateTax;
				lShippingDistrictTaxRemainder = lShippingDistrictTaxRemainder - lCIShippingDistrictTax;
				lShippingCountryTaxRemainder = lShippingCountryTaxRemainder - lCIShippingCountryTax;
				// Set Shipping and Tax charges
				lEXTNCommerceItem.setShipping(lCIShipping);
				lEXTNCommerceItem.setShippingDiscount(lCIShippingDiscount);
				lEXTNCommerceItem.setShippingTax(lCIShippingTax);
				lEXTNCommerceItem.setShippingCityTax(lCIShippingCityTax);
        lEXTNCommerceItem.setShippingCountyTax(lCIShippingCountyTax);
        lEXTNCommerceItem.setShippingStateTax(lCIShippingStateTax);
        lEXTNCommerceItem.setShippingDistrictTax(lCIShippingDistrictTax);
        lEXTNCommerceItem.setShippingCountryTax(lCIShippingCountryTax);
				lEXTNCommerceItem.setChanged(true);

				vlogDebug("order: {0} Item: {1} Shipping: {2} ShippingTax: {3}  ShippingCityTax {4}, ShippingCountyTax {5}, ShippingStateTax {6}, ShippingDistrictTax {7} ShippingCountryTax {8},ShippingDiscount {9}",
				    lOrder.getId(), lCommerceItem.getId(), lCIShipping, lCIShippingTax,lCIShippingCityTax,lCIShippingCountyTax,lCIShippingStateTax,lCIShippingDistrictTax,lCIShippingCountryTax,lCIShippingDiscount);

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingRemainder != 0) {
					double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShipping() + lShippingRemainder);
					vlogDebug("Adjusting Shipping of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingRemainder, lRoundedAmt);
					lEXTNCommerceItem.setShipping(lRoundedAmt);
				}

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingDiscountRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDiscount() + lShippingDiscountRemainder);
          vlogDebug("Adjusting ShippingDiscount of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDiscountRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingDiscount(lRoundedAmt);
        }

				// Account for the shipping tax remainder
				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingTaxRemainder != 0) {
					double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingTax() + lShippingTaxRemainder);
					vlogDebug("Adjusting ShippingTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingTaxRemainder, lRoundedAmt);
					lEXTNCommerceItem.setShippingTax(lRoundedAmt);
				}

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingCityTaxRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCityTax() + lShippingCityTaxRemainder);
          vlogDebug("Adjusting ShippingCityTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCityTaxRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingCityTax(lRoundedAmt);
        }

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingCountyTaxRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountyTax() + lShippingCountyTaxRemainder);
          vlogDebug("Adjusting ShippingCountyTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountyTaxRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingCountyTax(lRoundedAmt);
        }

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingStateTaxRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingStateTax() + lShippingStateTaxRemainder);
          vlogDebug("Adjusting ShippingStateTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingStateTaxRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingStateTax(lRoundedAmt);
        }

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingDistrictTaxRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDistrictTax() + lShippingDistrictTaxRemainder);
          vlogDebug("Adjusting ShippingDistrictTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDistrictTaxRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingDistrictTax(lRoundedAmt);
        }

				if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingCountryTaxRemainder != 0) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountryTax() + lShippingCountryTaxRemainder);
          vlogDebug("Adjusting ShippingDistrictTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountryTaxRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingCountryTax(lRoundedAmt);
        }

			}
		}

		// Adjust Shipping group Pricing
		adjustShippingGroupPricing (pOrder);

		vlogDebug("End splitShippingCharge ...");
	}*/

	/**
   * Split the shipping charge and tax across all of the commerce items.
   *
   * @param pOrder
   *            ATG Order
   */
  @SuppressWarnings("unchecked")
  public void splitShippingCharge(Order pOrder) {
    long lCtr = 0;

    vlogDebug("Begin splitShippingCharge ...");
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;

  // Sum the amount for all items on the order
    double lOrderTotal = 0;
    double lGiftCardTotal=0;
    int giftCardCount = 0;

    // Get the Shipping Charge for this order
    double lShipping = pOrder.getPriceInfo().getShipping();
    double lShippingRemainder = lShipping;

    // Get ShippingDiscount
    double lShippingDiscount = getShippingDiscount(lOrder);
    double lShippingDiscountRemainder = lShippingDiscount;

    double lTotalShippingChargeForShippingTax = 0;
    List<ShippingTaxGroupHolder> lShippingTaxGroupHolderList = new ArrayList<ShippingTaxGroupHolder>();

    vlogDebug("Processing order number {0} - Shipping {1} ShippingDiscount {2}",
                  lOrder.getOrderNumber(), lShipping, lShippingDiscount);

    List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
    RepositoryItem sku=null;
    
    for (CommerceItem lCommerceItem : lCommerceItems) {
    	sku = (RepositoryItem)lCommerceItem.getAuxiliaryData().getCatalogRef();
    	MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
    	
    	boolean shippingPromo = ((MFFCommerceItemImpl)lCommerceItem).isFreeShippingPromo();
    	boolean freeShipItem = sku != null ? (Boolean)sku.getPropertyValue("freeShipping") : false;

    	if(shippingPromo || freeShipItem) {
    		vlogDebug("Item {0} : Free shipping promo {1} Free Shipping Flag {2} ", lCommerceItem.getId(), 
    				shippingPromo, freeShipItem);
    		continue;
    	}
      // add amount to Order Total
      boolean isGiftCardItem = isGiftCardItem(lCommerceItem);
      
      MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) lCommerceItem.getPriceInfo();
      if(!isGiftCardItem){
        //double lAmount = lCommerceItem.getPriceInfo().getAmount();
        double lAmount = 0.0d;

        if(itemPriceInfo.getEffectivePrice() > 0) {
        	lAmount =  itemPriceInfo.getEffectivePrice();
        } else {
        	lAmount =  lCommerceItem.getPriceInfo().getAmount();
        }
 
        lOrderTotal = lOrderTotal + lAmount;
        vlogDebug("Item {0} not a gift card. Amount {1} order total {2}", lCommerceItem.getId(), lAmount, lOrderTotal);
      } else{
    	  double lAmount = 0.0;
    	  if(itemPriceInfo.getEffectivePrice() > 0) {
    		  lAmount =  itemPriceInfo.getEffectivePrice();
    	  } else {
          	lAmount =  lCommerceItem.getPriceInfo().getAmount();
          }
        lGiftCardTotal = lGiftCardTotal + lAmount;
        vlogDebug("Item {0} is a gift card. Amount {1} gift card total {2}", lCommerceItem.getId(), lAmount, lGiftCardTotal);
        giftCardCount++;
      }

    }
    vlogDebug("OrderId {0} total: {1} , GiftCards in Order: {2}", lOrder.getId(), lOrderTotal, giftCardCount);

    // split charges between gift cards if Order only contain gift cards
    if (giftCardCount == lCommerceItems.size()) {

      if (lShipping > 0 ) {
        vlogWarning("Order {0} is a GiftCard Only Order, Splitting Shipping Charges between {1} giftCards", lOrder.getId(), giftCardCount);
        // Calculate each commerce items share of the shipping/tax
        for (CommerceItem lCommerceItem : lCommerceItems) {
          MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
          lCtr++;
          double lAmount = lCommerceItem.getPriceInfo().getAmount();
          double lRatio = lAmount / lGiftCardTotal;
          double lCIShipping = getPricingTools().round(lShipping * lRatio);
          double lCIShippingDiscount = getPricingTools().round(lShippingDiscount * lRatio);

          lShippingRemainder = lShippingRemainder - lCIShipping;
          lShippingDiscountRemainder = lShippingDiscountRemainder - lCIShippingDiscount;

          // Set Shipping and Tax charges
          lEXTNCommerceItem.setShipping(lCIShipping);
          lEXTNCommerceItem.setShippingDiscount(lCIShippingDiscount);
          lEXTNCommerceItem.setChanged(true);

          vlogDebug("order: {0} Item: {1} Shipping: {2}, ShippingDiscount {3}", lOrder.getId(), lCommerceItem.getId(), lCIShipping,lCIShippingDiscount);

          // Account for the shipping remainder
          if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShipping() + lShippingRemainder);
            vlogDebug("Adjusting Shipping of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShipping(lRoundedAmt);
          }

          if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() && lShippingDiscountRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDiscount() + lShippingDiscountRemainder);
            vlogDebug("Adjusting ShippingDiscount of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDiscountRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingDiscount(lRoundedAmt);
          }

           if(lEXTNCommerceItem.getTaxPriceInfo() != null && lEXTNCommerceItem.getTaxPriceInfo().getAmount() > 0
               && lEXTNCommerceItem.getShipping() > 0){

             ShippingTaxGroupHolder shippingTaxHolder = new ShippingTaxGroupHolder();
             lTotalShippingChargeForShippingTax = lTotalShippingChargeForShippingTax + lEXTNCommerceItem.getShipping();
             shippingTaxHolder.setProratedShipping(lEXTNCommerceItem.getShipping());
             shippingTaxHolder.setCommerceItem(lEXTNCommerceItem);
             lShippingTaxGroupHolderList.add(shippingTaxHolder);
            }

        }
      }
      else{
        vlogDebug("No need distribute the tax as {0} is a gift card only order and no shipping or shipping tax found", lOrder.getId());
      }
    } else {

      // Calculate each commerce items share of the shipping/discount
      for (CommerceItem lCommerceItem : lCommerceItems) {
        MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
        double lCIShipping = 0.0;
        double lCIShippingDiscount = 0.0;
        boolean isFreeFreight=false;
        boolean isGiftCardItem = isGiftCardItem(lEXTNCommerceItem);
        sku = (RepositoryItem)lCommerceItem.getAuxiliaryData().getCatalogRef();
        if(sku!=null) {
        	isFreeFreight = (Boolean)sku.getPropertyValue("freeShipping");
        }
        if (!isGiftCardItem && !lEXTNCommerceItem.isFreeShippingPromo() && !isFreeFreight) {
        //if (!isGiftCardItem(lEXTNCommerceItem)) {
          double lAmount = 0.0d;
          
          MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) lEXTNCommerceItem.getPriceInfo();
          if(itemPriceInfo.getEffectivePrice() > 0) {
        	  lAmount = itemPriceInfo.getEffectivePrice();
          } else {
              lAmount = lCommerceItem.getPriceInfo().getAmount();
          }
          
          double lRatio = lAmount / lOrderTotal;
          lCIShipping = getPricingTools().round(lShipping * lRatio);
          lCIShippingDiscount = getPricingTools().round(lShippingDiscount * lRatio);
          lCtr++;
        }
        lShippingRemainder = lShippingRemainder - lCIShipping;
        lShippingDiscountRemainder = lShippingDiscountRemainder - lCIShippingDiscount;

        // Set Shipping and ShippingDiscount charges
        lEXTNCommerceItem.setShipping(lCIShipping);
        lEXTNCommerceItem.setShippingDiscount(lCIShippingDiscount);
        lEXTNCommerceItem.setChanged(true);

        vlogDebug("order: {0} Item: {1} Shipping: {2} ShippingDiscount {3}", lOrder.getId(), lCommerceItem.getId(), lCIShipping, lCIShippingDiscount);

        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingRemainder != 0 && !isGiftCardItem) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShipping() + lShippingRemainder);
          vlogDebug("Adjusting Shipping of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShipping(lRoundedAmt);
        }

        if (lCommerceItems.size() > 1 && lCtr == lCommerceItems.size() - giftCardCount && lShippingDiscountRemainder != 0 && !isGiftCardItem) {
          double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDiscount() + lShippingDiscountRemainder);
          vlogDebug("Adjusting ShippingDiscount of {0} by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDiscountRemainder, lRoundedAmt);
          lEXTNCommerceItem.setShippingDiscount(lRoundedAmt);
        }

        if(lEXTNCommerceItem.getTaxPriceInfo() != null && lEXTNCommerceItem.getTaxPriceInfo().getAmount() > 0
            && lEXTNCommerceItem.getShipping() > 0){

          ShippingTaxGroupHolder shippingTaxHolder = new ShippingTaxGroupHolder();
          lTotalShippingChargeForShippingTax = lTotalShippingChargeForShippingTax + lEXTNCommerceItem.getShipping();
          shippingTaxHolder.setProratedShipping(lEXTNCommerceItem.getShipping());
          shippingTaxHolder.setCommerceItem(lEXTNCommerceItem);
          lShippingTaxGroupHolderList.add(shippingTaxHolder);
        }

      }
    }

    //assign shipping tax
    assignShippingTaxToItem(lOrder, lShippingTaxGroupHolderList, lTotalShippingChargeForShippingTax);

    // Adjust Shipping group Pricing
    adjustShippingGroupPricing (pOrder);

    vlogDebug("End splitShippingCharge ...");
  }

	@SuppressWarnings("unchecked")
  private HashMap<String,Double> getShippingTaxMap(MFFOrderImpl pOrderImpl){
	  Map<String,TaxPriceInfo> shippingTaxPriceInfos = pOrderImpl.getTaxPriceInfo().getShippingItemsTaxPriceInfos();
	  double shippingTax = 0.0;
	  double shippingCityTax = 0.0;
	  double shippingCountyTax = 0.0;
	  double shippingStateTax = 0.0;
	  double shippingDistrictTax = 0.0;
	  double shippingCountryTax = 0.0;
	  if ((shippingTaxPriceInfos != null) && (shippingTaxPriceInfos.size() > 0)){
      Collection<TaxPriceInfo> taxInfoValues = shippingTaxPriceInfos.values();
      for (TaxPriceInfo taxPriceInfo : taxInfoValues){
        if (taxPriceInfo != null){
          shippingTax += taxPriceInfo.getAmount();
          shippingCityTax += taxPriceInfo.getCityTax();
          shippingCountyTax += taxPriceInfo.getCountyTax();
          shippingStateTax += taxPriceInfo.getStateTax();
          shippingDistrictTax += taxPriceInfo.getDistrictTax();
          shippingCountryTax += taxPriceInfo.getCountryTax();
        }
      }
    }
	 HashMap<String,Double> shipTaxMap = new HashMap<String,Double>();
	 shipTaxMap.put("shippingTax", shippingTax);
	 shipTaxMap.put("shippingCityTax", shippingCityTax);
	 shipTaxMap.put("shippingCountyTax", shippingCountyTax);
	 shipTaxMap.put("shippingStateTax", shippingStateTax);
	 shipTaxMap.put("shippingDistrictTax", shippingDistrictTax);
	 shipTaxMap.put("shippingCountryTax", shippingCountryTax);

	 return shipTaxMap;
	}

	private double getShippingDiscount(MFFOrderImpl pOrderImpl){
	  double shippingDiscount = 0.0d;

	  if(pOrderImpl.getShippingGroups() != null && pOrderImpl.getShippingGroups().size() > 0){
	    ShippingGroup sg = (ShippingGroup)pOrderImpl.getShippingGroups().get(0);
	    if(sg != null){
	      shippingDiscount = getPromotionTools().getShippingDiscount(pOrderImpl, sg);
	    }
	  }

	  vlogDebug("getShippingDiscount - shippingDiscount {0}",shippingDiscount);
	  return shippingDiscount;
	}

	private void assignShippingTaxToItem(MFFOrderImpl pOrderImpl,
      List<ShippingTaxGroupHolder> pShippingTaxGroupHolderList,double pTotalShippingChargeForShippingTax){

    if(pShippingTaxGroupHolderList != null && pShippingTaxGroupHolderList.size() > 0){

      HashMap<String,Double> shipTaxMap = getShippingTaxMap(pOrderImpl);
      // Get shipping Tax
      /*double lShippingTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingTax") != null){
        lShippingTax = shipTaxMap.get("shippingTax");
      }
      double lShippingTaxRemainder = lShippingTax;*/

      double lShippingCityTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingCityTax") != null){
        lShippingCityTax = shipTaxMap.get("shippingCityTax");
      }
      double lShippingCityTaxRemainder = lShippingCityTax;

      double lShippingCountyTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingCountyTax") != null){
        lShippingCountyTax = shipTaxMap.get("shippingCountyTax");
      }
      double lShippingCountyTaxRemainder = lShippingCountyTax;

      double lShippingStateTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingStateTax") != null){
        lShippingStateTax = shipTaxMap.get("shippingStateTax");
      }
      double lShippingStateTaxRemainder = lShippingStateTax;

      double lShippingDistrictTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingDistrictTax") != null){
        lShippingDistrictTax = shipTaxMap.get("shippingDistrictTax");
      }
      double lShippingDistrictTaxRemainder = lShippingDistrictTax;

      double lShippingCountryTax = 0.0d;
      if(shipTaxMap != null && shipTaxMap.get("shippingCountryTax") != null){
        lShippingCountryTax = shipTaxMap.get("shippingCountryTax");
      }
      double lShippingCountryTaxRemainder = lShippingCountryTax;

      double lTotalShippingChargeForShippingTaxRem = pTotalShippingChargeForShippingTax;
      long lCtr = 0;

      vlogDebug("order: {0} TotalShippingChargeForShippingTax: {1} ShippingTaxGroupHolder size: {2}",
          pOrderImpl.getId(), lTotalShippingChargeForShippingTaxRem, pShippingTaxGroupHolderList.size());

      for (ShippingTaxGroupHolder shipDiscountHolder : pShippingTaxGroupHolderList) {
          lCtr++;
          MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl)shipDiscountHolder.getCommerceItem();
          double prorateShipping = shipDiscountHolder.getProratedShipping();

          double lRatio = prorateShipping / lTotalShippingChargeForShippingTaxRem;
          //double lCIShippingTax = getPricingTools().round(lShippingTaxRemainder * lRatio);
          double lCIShippingCityTax = getPricingTools().round(lShippingCityTaxRemainder * lRatio);
          double lCIShippingCountyTax = getPricingTools().round(lShippingCountyTaxRemainder * lRatio);
          double lCIShippingStateTax = getPricingTools().round(lShippingStateTaxRemainder * lRatio);
          double lCIShippingDistrictTax = getPricingTools().round(lShippingDistrictTaxRemainder * lRatio);
          double lCIShippingCountryTax = getPricingTools().round(lShippingCountryTaxRemainder * lRatio);

          //lShippingTaxRemainder = lShippingTaxRemainder - lCIShippingTax;
          lShippingCityTaxRemainder = lShippingCityTaxRemainder - lCIShippingCityTax;
          lShippingCountyTaxRemainder =  lShippingCountyTaxRemainder - lCIShippingCountyTax;
          lShippingStateTaxRemainder =  lShippingStateTaxRemainder - lCIShippingStateTax;
          lShippingDistrictTaxRemainder = lShippingDistrictTaxRemainder - lCIShippingDistrictTax;
          lShippingCountryTaxRemainder = lShippingCountryTaxRemainder - lCIShippingCountryTax;

          lTotalShippingChargeForShippingTaxRem = lTotalShippingChargeForShippingTaxRem - prorateShipping;

          //lEXTNCommerceItem.setShippingTax(lCIShippingTax);
          lEXTNCommerceItem.setShippingCityTax(lCIShippingCityTax);
          lEXTNCommerceItem.setShippingCountyTax(lCIShippingCountyTax);
          lEXTNCommerceItem.setShippingStateTax(lCIShippingStateTax);
          lEXTNCommerceItem.setShippingDistrictTax(lCIShippingDistrictTax);
          lEXTNCommerceItem.setShippingCountryTax(lCIShippingCountryTax);
          lEXTNCommerceItem.setChanged(true);

          vlogDebug("order: {0} Item: {1}, ShippingCityTax {2}, ShippingCountyTax {3}, ShippingStateTax {4}, ShippingDistrictTax {5} ShippingCountryTax {6}",
              pOrderImpl.getId(), lEXTNCommerceItem.getId(), lCIShippingCityTax,lCIShippingCountyTax,lCIShippingStateTax,lCIShippingDistrictTax,lCIShippingCountryTax);

          /*if (lCtr == pShippingTaxGroupHolderList.size() && lShippingTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingTax() + lShippingTaxRemainder);
            vlogDebug("Adjusting ShippingTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingTax(lRoundedAmt);
          }*/

          if (lCtr == pShippingTaxGroupHolderList.size() && lShippingCityTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCityTax() + lShippingCityTaxRemainder);
            vlogDebug("Adjusting ShippingCityTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCityTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingCityTax(lRoundedAmt);
          }

          if (lCtr == pShippingTaxGroupHolderList.size() && lShippingCountyTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountyTax() + lShippingCountyTaxRemainder);
            vlogDebug("Adjusting ShippingCountyTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountyTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingCountyTax(lRoundedAmt);
          }

          if (lCtr == pShippingTaxGroupHolderList.size() && lShippingStateTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingStateTax() + lShippingStateTaxRemainder);
            vlogDebug("Adjusting ShippingStateTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingStateTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingStateTax(lRoundedAmt);
          }

          if (lCtr == pShippingTaxGroupHolderList.size() && lShippingDistrictTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingDistrictTax() + lShippingDistrictTaxRemainder);
            vlogDebug("Adjusting ShippingDistrictTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingDistrictTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingDistrictTax(lRoundedAmt);
          }

          if (lCtr == pShippingTaxGroupHolderList.size() && lShippingCountryTaxRemainder != 0) {
            double lRoundedAmt = getPricingTools().round(lEXTNCommerceItem.getShippingCountryTax() + lShippingCountryTaxRemainder);
            vlogDebug("Adjusting ShippingCountryTax of {0} Tax by {1} to yield {2}", lEXTNCommerceItem.getId(), lShippingCountryTaxRemainder, lRoundedAmt);
            lEXTNCommerceItem.setShippingCountryTax(lRoundedAmt);
          }
          
          double lCIShippingTax = lEXTNCommerceItem.getShippingCityTax() + lEXTNCommerceItem.getShippingCountyTax() + lEXTNCommerceItem.getShippingStateTax() + 
              lEXTNCommerceItem.getShippingDistrictTax() + lEXTNCommerceItem.getShippingCountryTax();
          
          vlogDebug("order: {0} Item: {1}, ShippingTax: {2}", pOrderImpl.getId(), lEXTNCommerceItem.getId(),lCIShippingTax);
          
          lEXTNCommerceItem.setShippingTax(getPricingTools().round(lCIShippingTax));

      }
    }

  }

	/**
	 * Re-calculate the shipping charges for each shipping group based on the
	 * shipping charges for the items in the group.
	 *
	 * @param pOrder		ATG Order
	 */
	@SuppressWarnings("unchecked")
	protected void adjustShippingGroupPricing (Order pOrder) {
		double lShipping 	 	= 0;
		double lOrigShipping 	= 0;
		String lOrderNumber  	= "";
		String lShippingGroupId = "";
		String lCommerceItemId  = "";

		MFFOrderImpl lOrder 	= (MFFOrderImpl) pOrder;
		lOrderNumber 			= lOrder.getOrderNumber();

		vlogDebug ("Adjust shipping for OrderNumber {0} orderId {1}", lOrderNumber,pOrder.getId());
		List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
		for (ShippingGroup lShippingGroup : lShippingGroups) {
			List<CommerceItemRelationship> lRelationships = lShippingGroup.getCommerceItemRelationships();
			lShipping 			= 0;
			lOrigShipping		= lShippingGroup.getPriceInfo() != null ? lShippingGroup.getPriceInfo().getAmount() : 0;
			lShippingGroupId 	= lShippingGroup.getId();
			vlogDebug ("Processing Order {0} Shipping Group {1} Orig Shipping group Amount {2}", lOrderNumber, lShippingGroupId, lOrigShipping);
			for (CommerceItemRelationship lRelationship : lRelationships) {
				CommerceItem lCommerceItem 				= lRelationship.getCommerceItem();
				MFFCommerceItemImpl lEXTNCommerceItem 	= (MFFCommerceItemImpl) lCommerceItem;
				lShipping 		= lShipping + lEXTNCommerceItem.getShipping();
				lCommerceItemId = lEXTNCommerceItem.getId();
				vlogDebug ("Adding Commerce Item {0} Order {1} Shipping Group {2} for Amount {3}", lCommerceItemId, lOrderNumber, lShippingGroupId, lEXTNCommerceItem.getShipping());
			}
			vlogDebug ("Adjust Order {0} Shipping Group {1} Shipping from {2} Shipping To {3} ", lOrderNumber, lShippingGroupId, lOrigShipping, lShipping);
			ShippingPriceInfo lShippingPriceInfo = lShippingGroup.getPriceInfo();
			lShippingPriceInfo.setAmount(lShipping);
		}
	}

	/** Add a payment ship relations record for each shipping group in the order.
	 *
	 * @param pOrder
	 */
	@SuppressWarnings("unchecked")
	public void addPayShipRelationships(Order pOrder) {
		vlogDebug("Begin addPayShipRelationships");
		PaymentGroupCommerceItemRelationship lRel = null;
		List<PaymentGroup> lPaymentGroups = pOrder.getPaymentGroups();
		List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
		List<CommerceItemPayGroupRelationship> lRelationships = new ArrayList<CommerceItemPayGroupRelationship>();

		// Commerce Item/Amount funded hash table
		Hashtable<String, Double> lItemHash = new Hashtable<String, Double>();

		// Loop through all of the payment amounts and distribute to the
		// commerce items
		for (PaymentGroup lPaymentGroup : lPaymentGroups) {
			double lPGAmount = lPaymentGroup.getAmount();
			double lPGRemainder = lPGAmount;
			vlogDebug("Processing payment group {0} for amount: {1}", lPaymentGroup.getId(), lPGAmount);

			// Distribute this amount to each commerce item
			for (CommerceItem lCommerceItem : lCommerceItems) {
				MFFCommerceItemImpl lEXTNCommerceItem = (MFFCommerceItemImpl) lCommerceItem;
				String lCommerceItemId = lCommerceItem.getId(); // Commerce item id
				double lCIAmount = lCommerceItem.getPriceInfo().getAmount() +
									lEXTNCommerceItem.getShipping() 		+
									lEXTNCommerceItem.getShippingTax();

				// Remove order level discounts from the amount
				double lOrderDiscountShare = lCommerceItem.getPriceInfo().getOrderDiscountShare();
				lCIAmount = lCIAmount - lOrderDiscountShare;
				vlogDebug("Removed {0} order discount from pay group {1} Commerce Item {2} yielding pre-tax amount {3}", lOrderDiscountShare, lPaymentGroup.getId(), lCommerceItemId, lCIAmount);

				// Get Tax for this item
				List<DetailedItemPriceInfo> lDetailedItemPriceInfos = lCommerceItem.getPriceInfo().getCurrentPriceDetails();
				double lTax = 0.00;
				for (DetailedItemPriceInfo lDetailedItemPriceInfo : lDetailedItemPriceInfos) {
					lTax = lTax + lDetailedItemPriceInfo.getTax();
				}
				lCIAmount = lCIAmount + lTax;
				vlogDebug("Add Tax {0} to Commerce Item {1} yielding taxable amount {2}", lTax, lCommerceItemId, lCIAmount);

				double lOtherAmount = 0; // Funding from previous payment group
				vlogDebug("Processing pay group {0} Commerce Item {1} for amount {2}", lPaymentGroup.getId(), lCommerceItemId, lCIAmount);

				// Remove any funding from a previous pay group
				if (lItemHash.get(lCommerceItemId) != null) {
					lOtherAmount = lItemHash.get(lCommerceItemId);
					lCIAmount = lCIAmount - lOtherAmount;
					vlogDebug("Reduced funding for Commerce Item {0} by {1} to {2}", lCommerceItemId, lOtherAmount, lCIAmount);
				}

				// Nothing left for this payment group
				if (lPGRemainder == 0) {
					vlogDebug("Pay group {0} has been exhausted ... continue with next pay group", lPaymentGroup.getId());
					continue;
				}

				// Add the pay group/item relationship
				CommerceItemPayGroupRelationship lCiPgRel = new CommerceItemPayGroupRelationship();
				if (lPGRemainder >= lCIAmount) {
					lCiPgRel.setAmount(getPricingTools().round(lCIAmount));
					lCiPgRel.setPaymentGroupId(lPaymentGroup.getId());
					lCiPgRel.setCommerceItemId(lCommerceItem.getId());
					lPGRemainder = lPGRemainder - lCIAmount;
					if (lItemHash.contains(lCommerceItemId))
						lItemHash.put(lCommerceItemId, lCIAmount + lOtherAmount);
					else
						lItemHash.put(lCommerceItemId, lCIAmount);
					vlogDebug("Rule #1 (Fund Pay Group) - Added Commerce item {0} to Pay Group {1} for {2}", lCommerceItemId, lPaymentGroup.getId(), lCIAmount);
				} else {
					lCiPgRel.setAmount(getPricingTools().round(lPGRemainder));
					lCiPgRel.setPaymentGroupId(lPaymentGroup.getId());
					lCiPgRel.setCommerceItemId(lCommerceItem.getId());
					lItemHash.put(lCommerceItemId, lPGRemainder);
					if (lItemHash.contains(lCommerceItemId))
						lItemHash.put(lCommerceItemId, lPGRemainder + lOtherAmount);
					else
						lItemHash.put(lCommerceItemId, lPGRemainder);
					vlogDebug("Rule #2 (Exhaust Pay Group) - Added Commerce item {0} to Pay Group {1} for {2}", lCommerceItemId, lPaymentGroup.getId(), lPGRemainder);
					lPGRemainder = 0;
				}
				lRelationships.add(lCiPgRel);
			}
		}

		// Add all of the pay relationships to the repository
		for (CommerceItemPayGroupRelationship lRelationship : lRelationships) {
			String lCommerceItemId = lRelationship.getCommerceItemId();
			String lPaymentGroupId = lRelationship.getPaymentGroupId();
			double lAmount = lRelationship.getAmount();
			vlogDebug("Adding relationship Commerce item: {0} Pay Group: {1} Amount: {2}", lCommerceItemId, lPaymentGroupId, lAmount);
			try {
				// getCommerceItemManager().addItemAmountToPaymentGroup(pOrder,
				// lCommerceItemId, lPaymentGroupId, lAmount);
				PaymentGroup lPaymentGroup = pOrder.getPaymentGroup(lPaymentGroupId);
				CommerceItem lCommerceItem = pOrder.getCommerceItem(lCommerceItemId);
				lRel = (PaymentGroupCommerceItemRelationship) createRelationship("paymentGroupCommerceItem");
				lRel.setRelationshipType(200);
				lRel.setPaymentGroup(lPaymentGroup);
				lRel.setCommerceItem(lCommerceItem);
				lRel.setAmount(lAmount);
				pOrder.addRelationship(lRel);
			} catch (CommerceException e) {
				vlogError(e, "Unable to add Commerce item: {0} Pay Group: {1} Amount: {2}", lCommerceItemId, lPaymentGroupId, lAmount);
			}
		}
		vlogDebug("End addPayShipRelationships");
	}



	/**
	 *
   * Get the orderId by Order Number.
   *
	 * @param pOrderNumber
	 * @return OrderID for Order Number
	 * @throws CommerceException
	 */
  public String getOrderIdByOrderNumber (String pOrderNumber) throws CommerceException{

    vlogDebug ("Begin getOrderIdByOrderNumber for Order No: {0}", pOrderNumber);
    RepositoryItem [] lItems       = null;
    try {
      // Get the repository item for this order number
      Object [] lParams         = new String[2];
      lParams[0]                = pOrderNumber;
      RepositoryView lView      = getOrderTools().getOrderRepository().getView(getOrderItemDescriptorName());
      RqlStatement lStatement   = RqlStatement.parseRqlStatement("OrderNumber EQUALS ?0");
      lItems              = lStatement.executeQuery(lView, lParams);
    }
    catch (RepositoryException e) {
      String lErrorMessage = String.format("getOrderIdByOrderNumber - Error locating order %s to update", pOrderNumber);
      vlogError (e, lErrorMessage);
      throw new CommerceException (lErrorMessage);
    }

    if (lItems == null || lItems.length < 1) {
      String lErrorMessage = String.format("getOrderIdByOrderNumber - Unable to find order %s to update", pOrderNumber);
      vlogError (lErrorMessage);
      throw new CommerceException (lErrorMessage);
    }
    vlogDebug ("End getOrderIdByOrderNumber for Order No: {0} OrderId : {1}", pOrderNumber,lItems[0].getRepositoryId());
    return lItems[0].getRepositoryId();
  }

  /**
   * Get a list of the items for each of shipping groups that are part of this shipment.  Each
   * of the shipping groups will be a separate invoice.
   *
   * @param pOrder            ATG Order
   * @param pItemsToShip      List of commerce items to ship
   * @return                  Hashtable of shipping groups and commerce items
   * @throws CommerceException
   */
  @SuppressWarnings("unchecked")
  public Hashtable <String, List<String>> getItemForShipGroups (Order pOrder, List<String> pItemsToShip)
      throws CommerceException {

    int lShipmentGroupCount = 0;
    int lCommerceItemCount  = 0;
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();
    Hashtable <String, List<String>> lShippingGroupItems = new Hashtable <String, List<String>>();

    for (String lItemToShip : pItemsToShip) {

      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lItemToShip);

      List <ShippingGroupCommerceItemRelationship> lShippingGroupCommerceItemRelationships = lCommerceItem.getShippingGroupRelationships();

      for (ShippingGroupCommerceItemRelationship lShippingGroupCommerceItemRelationship : lShippingGroupCommerceItemRelationships) {

        String lCommerceItemId      = lShippingGroupCommerceItemRelationship.getCommerceItem().getId();
        String lShippingGroupId     = lShippingGroupCommerceItemRelationship.getShippingGroup().getId();

        if (lShippingGroupItems.containsKey(lShippingGroupId)) {
          lCommerceItemCount++;
          lShippingGroupItems.get(lShippingGroupId).add(lCommerceItemId);
        }
        else {
          lShipmentGroupCount++;
          lCommerceItemCount++;
          List<String> lItem = new Vector <String> ();
          lItem.add(lCommerceItemId);
          lShippingGroupItems.put(lShippingGroupId, lItem);
        }
      }
    }
    vlogDebug ("+++++ Order {0}/{1} has {2} shipment groups with {3} Items", lOrderNumber, lOrderId, lShipmentGroupCount, lCommerceItemCount);
    return lShippingGroupItems;
  }
  
  @SuppressWarnings("unchecked")
  public Hashtable <String, List<String>> getItemForShipGroupsForSplitOrder (Order pOrder, List<String> pItemsToShip)
      throws CommerceException {

    int lShipmentGroupCount = 0;
    int lCommerceItemCount  = 0;
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    String lOrderNumber = lOrder.getOrderNumber();
    String lOrderId     = lOrder.getId();
    Hashtable <String, List<String>> lShippingGroupItems = new Hashtable <String, List<String>>();
    
    if(pItemsToShip != null && pItemsToShip.size() > 0){
      
      MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(pItemsToShip.get(0));
      List <ShippingGroupCommerceItemRelationship> lShippingGroupCommerceItemRelationships = lCommerceItem.getShippingGroupRelationships();
      
      // Pick the shippingGroupId of the first item
      String lShippingGroupId = "";
      for (ShippingGroupCommerceItemRelationship lShippingGroupCommerceItemRelationship : lShippingGroupCommerceItemRelationships) {
        lShippingGroupId = lShippingGroupCommerceItemRelationship.getShippingGroup().getId();
        break;
      }
      
      for (String lItemToShip : pItemsToShip) {
        
        if (lShippingGroupItems.containsKey(lShippingGroupId)) {
          lCommerceItemCount++;
          lShippingGroupItems.get(lShippingGroupId).add(lItemToShip);
        }
        else {
          List<String> lItem = new Vector <String> ();
          lItem.add(lItemToShip);
          lShippingGroupItems.put(lShippingGroupId, lItem);
        }
      }
      
    }
    
    vlogDebug ("+++++ Order {0}/{1} has {2} shipment groups with {3} Items", lOrderNumber, lOrderId, lShipmentGroupCount, lCommerceItemCount);
    return lShippingGroupItems;
  }
    
  public boolean isSplitOrderEnabled() {
    return splitOrderEnabled;
  }

  public void setSplitOrderEnabled(boolean pSplitOrderEnabled) {
    splitOrderEnabled = pSplitOrderEnabled;
  }

	public HashMap<String, String> getOrderCloneExclusionsMap() {
		return orderCloneExclusionsMap;
	}

	public void setOrderCloneExclusionsMap(HashMap<String, String> orderCloneExclusionsMap) {
		this.orderCloneExclusionsMap = orderCloneExclusionsMap;
	}

	public int getRemorsePeriod() {
		return mRemorsePeriod;
	}

	public void setRemorsePeriod(int pRemorsePeriod) {
		this.mRemorsePeriod = pRemorsePeriod;
	}

	public Repository getOmsOrderRepository() {
		return omsOrderRepository;
	}

	public void setOmsOrderRepository(Repository omsOrderRepository) {
		this.omsOrderRepository = omsOrderRepository;
	}

	public PricingTools getPricingTools() {
		return mPricingTools;
	}

	public void setPricingTools(PricingTools pricingTools) {
		this.mPricingTools = pricingTools;
	}

	public String getOmsAgentProfileId() {
		return omsAgentProfileId;
	}

	public void setOmsAgentProfileId(String omsAgentProfileId) {
		this.omsAgentProfileId = omsAgentProfileId;
	}

	public String getOmsOrderCommentPrefix() {
		return omsOrderCommentPrefix;
	}

	public void setOmsOrderCommentPrefix(String omsOrderCommentPrefix) {
		this.omsOrderCommentPrefix = omsOrderCommentPrefix;
	}

	public String getOrderCommentItemDescriptorName() {
		return orderCommentItemDescriptorName;
	}

	public void setOrderCommentItemDescriptorName(String orderCommentItemDescriptorName) {
		this.orderCommentItemDescriptorName = orderCommentItemDescriptorName;
	}

  public String getGiftCardProductID() {
		return mGiftCardProductID;
	}

	public void setGiftCardProductID(String pGiftCardProductID) {
		mGiftCardProductID = pGiftCardProductID;
	}

  public class CommerceItemPayGroupRelationship {
    String  mPaymentGroupId;
    String  mCommerceItemId;
    double  mAmount;

    CommerceItemPayGroupRelationship() {
      super();
    }

    CommerceItemPayGroupRelationship(String pPaymentGroupId, String pCommerceItemId, double pAmount) {
      super();
      setPaymentGroupId(pPaymentGroupId);
      setCommerceItemId(pCommerceItemId);
      setAmount(pAmount);
    }

    public String getPaymentGroupId() {
      return mPaymentGroupId;
    }

    public void setPaymentGroupId(String pPaymentGroupId) {
      this.mPaymentGroupId = pPaymentGroupId;
    }

    public String getCommerceItemId() {
      return mCommerceItemId;
    }

    public void setCommerceItemId(String pCommerceItemId) {
      mCommerceItemId = pCommerceItemId;
    }

    public double getAmount() {
      return mAmount;
    }

    public void setAmount(double pAmount) {
      this.mAmount = pAmount;
    }

  }

  public MFFPaymentManager getPaymentManager() {
    return paymentManager;
  }

  public void setPaymentManager(MFFPaymentManager pPaymentManager) {
    paymentManager = pPaymentManager;
  }

  public List<String> getDamagedReasonCodes() {
    return mDamagedReasonCodes;
  }

  public void setDamagedReasonCodes(List<String> pDamagedReasonCodes) {
    mDamagedReasonCodes = pDamagedReasonCodes;
  }

  public MffPromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  public void setPromotionTools(MffPromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

}