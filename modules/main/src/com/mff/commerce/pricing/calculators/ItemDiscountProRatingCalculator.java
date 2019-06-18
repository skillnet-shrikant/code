package com.mff.commerce.pricing.calculators;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.pricing.MFFQualifierService;
import com.mff.commerce.promotion.GWPPromoHelper;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.ItemPricingCalculator;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.definition.PricingModelElem;
import atg.commerce.pricing.definition.QualifierElem;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * The Class ItemDiscountProRatingCalculator.
 */
public class ItemDiscountProRatingCalculator extends GenericService implements ItemPricingCalculator {

	/** The qualifier service. */
	private MFFQualifierService qualifierService;
	
	private GWPPromoHelper gwpPromoHelper;

	public GWPPromoHelper getGwpPromoHelper() {
		return gwpPromoHelper;
	}

	public void setGwpPromoHelper(GWPPromoHelper pGwpPromoHelper) {
		gwpPromoHelper = pGwpPromoHelper;
	}

	/** The pricing tools. */
	private PricingTools pricingTools;

	private boolean isProrate;
	private boolean isSplitDiscounts;
	
	public boolean isSplitDiscounts() {
		return isSplitDiscounts;
	}

	public void setSplitDiscounts(boolean pIsSplitDiscounts) {
		isSplitDiscounts = pIsSplitDiscounts;
	}

	public boolean isProrate() {
		return isProrate;
	}
	
	
	// giftValueMap - Stores the value of each gift item keyed by commerce item id
	private HashMap<String, Double> giftValueMap;
	
	// splitDiscountMap - Stores the gift discount share keyed by commerce item id
	private HashMap<String, Double> splitDiscountMap;


	HashMap<String, Double> proratedDiscounts = new HashMap<String, Double>();
	
	public HashMap<String, Double> getProratedDiscounts() {
		return proratedDiscounts;
	}

	public void setProratedDiscounts(HashMap<String, Double> pProratedDiscounts) {
		proratedDiscounts = pProratedDiscounts;
	}

	public HashMap<String, Double> getEffectivePrices() {
		return effectivePrices;
	}

	public void setEffectivePrices(HashMap<String, Double> pEffectivePrices) {
		effectivePrices = pEffectivePrices;
	}

	HashMap<String, Double> effectivePrices = new HashMap<String, Double>();
	
	public HashMap<String, Double> getGiftValueMap() {
		return giftValueMap;
	}

	public HashMap<String, Double> getSplitDiscountMap() {
		return splitDiscountMap;
	}

	public void setSplitDiscountMap(HashMap<String, Double> pSplitDiscountMap) {
		splitDiscountMap = pSplitDiscountMap;
	}

	public void setGiftValueMap(HashMap<String, Double> pGiftValueMap) {
		giftValueMap = pGiftValueMap;
	}

	public void setProrate(boolean pIsProrate) {
		isProrate = pIsProrate;
	}

	protected boolean requiresProration(RepositoryItem promotion) throws PricingException {
		boolean lReturnValue = false;
		try {
			PricingModelElem model = (PricingModelElem) getQualifierService().getPMDLCache().get(promotion);
			QualifierElem qualifier = model.getQualifier();
			if(qualifier.getSubElements() != null && qualifier.getSubElements().length != 0 && qualifier.getSubElements()[0] != null) {
				lReturnValue = true;
			}
		} catch (Exception e) {
			throw new PricingException(MessageFormat.format("Could not determine the proration for the following promotion %s",  promotion.getRepositoryId()), e);
		}
		return lReturnValue;
	}

	/**
	 * Pro-rate discounts.
	 *
	 * @param pPriceQuotes the price quotes
	 * @param pItems the items
	 * @param pOrder the order
	 * @param pProfile the profile
	 * @param pLocale the locale
	 * @param paramMap the param map
	 * @throws PricingException the pricing exception
	 * @throws RepositoryException the repository exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void prorateDiscounts(List pPriceQuotes, List pItems, Order pOrder, RepositoryItem pProfile, Locale pLocale, Map paramMap) throws PricingException, RepositoryException {

		vlogDebug("Entering prorateDiscounts : pPriceQuotes, pItems, pOrder, pProfile, pLocale, paramMap");

		List<CommerceItem> commerceItems = pOrder.getCommerceItems();
		RepositoryItem promotion = null;

		List<ProratedDiscountBean> discountBeanList = new ArrayList<ProratedDiscountBean>();
		List<CommerceItem> nonDiscountedItems = new ArrayList<CommerceItem>();
		HashMap<String, Double> itemPriceMap = new HashMap<String, Double>();

		double adjustedPrice = 0.0;
		for (CommerceItem item : commerceItems) {
			vlogDebug("Commerce item set to {0}", item.getId());
			promotion = getPromotion(item);

			if (promotion != null && requiresProration(promotion)) {

				vlogDebug("promotion id set to {0}", promotion.getRepositoryId());
				ProratedDiscountBean pdBean = getPromotionDiscountBean(discountBeanList, promotion);

				if (pdBean == null) {
					ProratedDiscountBean aBean = new ProratedDiscountBean();
					aBean.setPromotion(promotion);
					List discountedItems = new ArrayList();
					discountedItems.add(item);
					aBean.setDiscountedItems(discountedItems);
					discountBeanList.add(aBean);
				} else {
					List discountedItems = pdBean.getDiscountedItems();
					discountedItems.add(item);
					pdBean.setDiscountedItems(discountedItems);
				}
			} else {
				vlogDebug("Adding the following to the non discounted item {0}", item.getId());
				nonDiscountedItems.add(item);
			}
		}
		if (discountBeanList.size() > 0 && nonDiscountedItems.size() > 0) {

			for (ProratedDiscountBean aBean : discountBeanList) {
				promotion = aBean.getPromotion();

				for (CommerceItem pItem : nonDiscountedItems) {
					vlogDebug("Non discounted commerce item id {0}", pItem.getId());

					PricingContext pricingContext = getPricingTools().getPricingContextFactory().createPricingContext(commerceItems, promotion, pProfile, pLocale, pOrder, null);

					List singleItem = new ArrayList();

					FilteredCommerceItem fItem = new FilteredCommerceItem(pItem);
					fItem.setPriceQuote(pItem.getPriceInfo());
					fItem.setDiscountable(pItem.getPriceInfo().getDiscountable());
					singleItem.add(fItem);

					Object returnValue = qualifierService.evaluateQualifier(pricingContext, paramMap, singleItem);
					if (returnValue != null) {
						vlogDebug("Item Disc Calc Returned " + returnValue);
					} else {
						vlogDebug("Item Disc Calc Returned null");
					}

					if (returnValue != null) {
						List qualifierList = null;

						if (returnValue instanceof Boolean) {

							Boolean isItemQualifer = (Boolean) returnValue;
							vlogDebug("isItemQualifer set to {0}", isItemQualifer.booleanValue());

							if (aBean.getQualifierItems() != null && !aBean.getQualifierItems().isEmpty()) {
								qualifierList = aBean.getQualifierItems();
							} else {
								qualifierList = new ArrayList();
							}

							if (isItemQualifer) {

								qualifierList.add(pItem);
							}

						} else if (returnValue instanceof Object) {
							// MatchingObject mo = (MatchingObject)returnValue;
							qualifierList = aBean.getQualifierItems();
							if (qualifierList == null) {
								qualifierList = new ArrayList();
							}
							qualifierList.add(pItem);
						}

						aBean.setQualifierItems(qualifierList);
					} else {

						// 2414: The qualifierService call above seems suspect
						// In certain case, the qualfierService returns the right value
						// but this calc (postCalc) sees it as null. This needs to be triaged
						// Also there may be multiple qualifiers (spend X in Y Get Z)
						MFFCommerceItemImpl mi = (MFFCommerceItemImpl) pItem;
						List qualifierList = null;
						qualifierList = aBean.getQualifierItems();
						if (mi.getGwpPromoId() != null) {
							// && qualifierList != null && !qualifierList.contains(pItem)
							if (qualifierList != null && !qualifierList.contains(pItem)) {
								aBean.getQualifierItems().add(mi);
							}
							if (qualifierList == null) {
								qualifierList = new ArrayList();
								qualifierList.add(mi);
								aBean.setQualifierItems(qualifierList);
							}
						}
					}
				}
			}
			for (ProratedDiscountBean aBean : discountBeanList) {

				for (CommerceItem pItem : aBean.getDiscountedItems()) {
					adjustedPrice = adjustedPrice + getAdjustedDiscountAmount(aBean.getPromotion(), pItem);
				}

				List<CommerceItem> totalItems = new ArrayList<>();
				for (ProratedDiscountBean pBean : discountBeanList) {
					if (pBean.getQualifierItems() != null && !pBean.getQualifierItems().isEmpty()) {
						totalItems.addAll(pBean.getQualifierItems());
					}

					if (pBean.getDiscountedItems() != null && !pBean.getDiscountedItems().isEmpty()) {
						totalItems.addAll(pBean.getDiscountedItems());
					}
				}
				for (CommerceItem pItem : totalItems) {
					populatePriceMap(discountBeanList, aBean, pItem, itemPriceMap, adjustedPrice);
				}

			}

		}

		java.util.ListIterator priceQuoteIte = pPriceQuotes.listIterator();
		java.util.ListIterator itemsIter = pItems.listIterator();
		int i = 0;
		while (priceQuoteIte.hasNext() && itemsIter.hasNext() && itemPriceMap.size() > 0) {
			MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) priceQuoteIte.next();
			CommerceItem pItem = (CommerceItem) itemsIter.next();
			Double proratedPrice = itemPriceMap.get(pItem.getId());
			if (proratedPrice != null) {
				proratedPrice = this.getPricingTools().round(proratedPrice);
				populateSalePriceItemId(pItem, itemPriceInfo);
				itemPriceInfo.setProratedDiscountPrice(proratedPrice);

				pItem.setPriceInfo(itemPriceInfo);
				adjustedPrice = adjustedPrice - proratedPrice;
				// penny round the total for last item.
				if (itemPriceMap.size() - 1 == i && adjustedPrice > 0) {
					itemPriceInfo.setProratedDiscountPrice(proratedPrice + (adjustedPrice));
				}
			}

			i++;
		}
	}

	/**
	 * Populate sale price item id.
	 *
	 * @param pItem the item
	 * @param itemPriceInfo the item price info
	 */
	private void populateSalePriceItemId(CommerceItem pItem, MFFItemPriceInfo itemPriceInfo) {
		String sku = pItem.getCatalogRefId();
		String product = pItem.getAuxiliaryData().getProductId();
		RepositoryItem price = null;
		try {
			RepositoryItem priceList = itemPriceInfo.getPriceList();

			if (priceList != null) {
				price = getPricingTools().getPriceListManager().getPrice(priceList, product, sku);
				if (price != null) {
					itemPriceInfo.setSalePriceListId(price.getRepositoryId());
				}
			}

		} catch (PriceListException pe) {
			if (isLoggingError())
				logError("Some pricing exception", pe);
		}
	}

	/**
	 * Populate price map.
	 *
	 * @param discountBeanList the discount bean list
	 * @param aBean the a bean
	 * @param pItem the item
	 * @param itemPriceMap the item price map
	 * @param adjustedPrice the adjusted price
	 */
	private void populatePriceMap(List<ProratedDiscountBean> discountBeanList, ProratedDiscountBean aBean,
			CommerceItem pItem, Map<String, Double> itemPriceMap, double adjustedPrice) {
		double totalPrice = 0.0;
		// if( (Boolean)((CommerceItemImpl)pItem).getPropertyValue("onSale")) {
		if (pItem.getPriceInfo().isOnSale()) {
			totalPrice = pItem.getPriceInfo().getSalePrice();
		} else {
			totalPrice = pItem.getPriceInfo().getRawTotalPrice();
		}
		// double rawTotal = pItem.getPriceInfo().getRawTotalPrice();
		double factor = 0.0;

		factor = getProRateFactor(discountBeanList, pItem);
		Double totalProratePrice = totalPrice - (adjustedPrice * factor);
		itemPriceMap.put(pItem.getId(), totalProratePrice);
	}

	/**
	 * Gets the pro rate factor.
	 *
	 * @param discountBeanList the discount bean list
	 * @param pItem the item
	 * @return the pro rate factor
	 */
	private double getProRateFactor(List<ProratedDiscountBean> discountBeanList, CommerceItem pItem) {
		double factor = 0.0;
		double totalRawPrice = 0.0;

		List<CommerceItem> totalItems = new ArrayList<>();
		for (ProratedDiscountBean aBean : discountBeanList) {

			if (aBean.getQualifierItems() != null) {
				totalItems.addAll(aBean.getQualifierItems());
			}
			if (aBean.getDiscountedItems() != null) {
				totalItems.addAll(aBean.getDiscountedItems());
			}
		}
		for (CommerceItem item : totalItems) {
			if (item.getPriceInfo().isOnSale()) {
				totalRawPrice += item.getPriceInfo().getSalePrice();
			} else {
				totalRawPrice += item.getPriceInfo().getRawTotalPrice();
			}

		}
		vlogDebug("Prorating totalRawPrice:" + totalRawPrice);
		factor = pItem.getPriceInfo().getRawTotalPrice() / totalRawPrice;
		vlogDebug("Prorating factor :" + factor);
		return factor;
	}

	/**
	 * Gets the adjusted discount amount.
	 *
	 * @param pPromo the promo
	 * @param pItem the item
	 * @return the adjusted discount amount
	 */
	private double getAdjustedDiscountAmount(RepositoryItem pPromo, CommerceItem pItem) {
		double adjustedAmount = 0.0;
		List adjustments = pItem.getPriceInfo().getAdjustments();
		for (Object adj : adjustments) {
			PricingAdjustment adjustment = null;
			if (adj instanceof PricingAdjustment) {
				adjustment = (PricingAdjustment) adj;
				RepositoryItem promo = adjustment.getPricingModel();

				if (promo != null && pPromo.getRepositoryId().equals(promo.getRepositoryId())) {
					// 2414: For certain promotions, may be multiple quantities offered as discount
					// Total adjustment holds the right adjusted value. Unit adjustment will not work
					// Hence changing it to totalAdjustment to get the total discounted value.
					// adjustedAmount = adjustment.getAdjustment();
					adjustedAmount = adjustment.getTotalAdjustment();
					break;
				}
			}
		}

		return (adjustedAmount * -1);
	}

	/**
	 * Gets the promotion discount bean.
	 *
	 * @param discountBeanList the discount bean list
	 * @param promotion the promotion
	 * @return the promotion discount bean
	 */
	private ProratedDiscountBean getPromotionDiscountBean(List<ProratedDiscountBean> discountBeanList, RepositoryItem promotion) {

		if (discountBeanList != null && discountBeanList.size() > 0) {
			for (ProratedDiscountBean aBean : discountBeanList) {
				if (aBean.getPromotion() == promotion) {
					return aBean;
				}
			}
		}
		return null;
	}
	
	/**
	 * Update priceInfos for Non-GWP Item Discounts like Buy 1 Get 1 Free
	 * 
	 * @param pOrder
	 * @param pPriceQuotes
	 * @param pItems
	 */
	public void updateNonGWPItemDiscounts(Order pOrder, List pPriceQuotes, List pItems) {
		java.util.ListIterator priceQuoteIte = pPriceQuotes.listIterator();
		java.util.ListIterator itemsIter = pItems.listIterator();
		double discountAmount = 0.0;
		double effectivePrice = 0.0;

		while (priceQuoteIte.hasNext() && itemsIter.hasNext()) {
		
			MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) priceQuoteIte.next();
			MFFCommerceItemImpl item = (MFFCommerceItemImpl) itemsIter.next();
			
			// BOGO items - Say Buy 3 Get 1 Free
			// these will have a Item Discount price adjustment
			// but will not have other qualifiers
			// Note: The "amount" attribute of itemPriceInfo will already have the discounted amount
			// setting just the discountValue & effectivePrice to facilitate
			// ReSA file generation & returns
			// We need the prorated discount value set. The value of the free item
			// should be prorated between all 4 (incl. the free item).
			vlogDebug("Setting Price for item {0} isGwp {1} gwpPromoId {2} ", item.getId(), item.isGwp(), item.getGwpPromoId());
			
			if(!item.isGwp() && itemPriceInfo.isDiscounted()) {
				discountAmount = getGwpPromoHelper().getNonGWPDiscountValue(item);
				itemPriceInfo.setDiscountAmount(discountAmount);
				logDebug("Setting effective price to " + itemPriceInfo.getAmount());
				itemPriceInfo.setEffectivePrice(itemPriceInfo.getAmount());
				item.setPriceInfo(itemPriceInfo);
			}
		}

	}
	
	public void splitDiscounts(Order pOrder, List pPriceQuotes, List pItems) {
		// Get the gwp Items in the order
		ArrayList<MFFCommerceItemImpl> gwpItems = getGwpPromoHelper().getGWPItems(pOrder);
		HashMap<String, Double> splitDiscountMap = new HashMap<String, Double>();
		HashMap<String, Double> giftValueMap = new HashMap<String, Double>();
		if(gwpItems!=null) {
			
			for(MFFCommerceItemImpl giftItem : gwpItems) {
				
				vlogInfo("******************** Splitting discounts for gift commerce item {0} ****************", giftItem.getId());
				
				// Get the value of the gift
				double giftValue = getGwpPromoHelper().getGiftValue(giftItem);
				giftValueMap.put(giftItem.getId(), giftValue);
				vlogInfo("-----------------------------> Gift Value = {0}", giftValue);
				
				// get the promo id for this gift item
				String gwpPromoId = getGwpPromoHelper().getGiftPromoId(giftItem);
				
				vlogInfo("-----------------------------> GWP Promo Id = {0}", gwpPromoId);

				if(gwpPromoId != null) {
					
					// get list of qualifiers for this gift item
					ArrayList<MFFCommerceItemImpl> qualifiers = getGwpPromoHelper().getQualifiersForGWP(pOrder, gwpPromoId, false);
					
					int splitSize = qualifiers == null ? 0 : qualifiers.size();
					vlogInfo("-----------------------------> Qualifier Count for this gift = {0}", splitSize);
					
					double giftShare = 0;
					if(splitSize > 0) {
						//giftShare = getPricingTools().round(giftValue / splitSize);
						HashMap<Integer,Double> splitDiscountShares = getGwpPromoHelper().prorateOnQuantity(giftValue, splitSize);
						vlogInfo("-----------------------------> Share for each qualifier = {0}", giftShare);
						
						// Loop thru each qualifier & set their gift share
						int iCtr = 1;
						for(MFFCommerceItemImpl qualifier : qualifiers) {
							if(!splitDiscountMap.containsKey(qualifier.getId())){
								splitDiscountMap.put(qualifier.getId(), splitDiscountShares.get(iCtr));
							} else {
								double existingDiscount = splitDiscountMap.get(qualifier.getId());
								existingDiscount += splitDiscountShares.get(iCtr);
								splitDiscountMap.put(qualifier.getId(), existingDiscount);
							}
							iCtr++;
						}
					} else {
						// Probably an item discount like spend $200 get something free
						// splitting discount between all items
						splitSize = pItems.size() - gwpItems.size();
						HashMap<Integer,Double> splitDiscountShares = getGwpPromoHelper().prorateOnQuantity(giftValue, splitSize);
						int iCtr = 1;
						Iterator<MFFCommerceItemImpl> itemsIter = pItems.iterator();
						while(itemsIter.hasNext()) {
							MFFCommerceItemImpl item = itemsIter.next();

							if(!splitDiscountMap.containsKey(item.getId())){
								splitDiscountMap.put(item.getId(), splitDiscountShares.get(iCtr));
							} else {
								double existingDiscount = splitDiscountMap.get(item.getId());
								existingDiscount += splitDiscountShares.get(iCtr);
								splitDiscountMap.put(item.getId(), existingDiscount);
							}
							iCtr++;
						}						
					}
				}
			}
		
		}
		setSplitDiscountMap(splitDiscountMap);
		setGiftValueMap(giftValueMap);
		
		updatePriceInfos(pPriceQuotes, pItems);
	}
	
	public void updateSplitDiscountPriceInfos(List pPriceQuotes, List pItems) {
		java.util.ListIterator priceQuoteIte = pPriceQuotes.listIterator();
		java.util.ListIterator itemsIter = pItems.listIterator();
		double discountAmount = 0.0;
		double effectivePrice = 0.0;

		while (priceQuoteIte.hasNext() && itemsIter.hasNext() && getSplitDiscountMap() != null && getSplitDiscountMap().size() > 0) {
		
			MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) priceQuoteIte.next();
			MFFCommerceItemImpl item = (MFFCommerceItemImpl) itemsIter.next();
			
			if(item.isGwp()) {
				// check if we have it in the giftValue map
				if (getGiftValueMap() != null && getGiftValueMap().get(item.getId()) != null) {
					
					// The gift sku can be added as a separate item by the user
					// in that case, there will be a non-zero amount on the GWP item
					// Effective price will be amount+discountAmount
					discountAmount = getGiftValueMap().get(item.getId());
					effectivePrice = itemPriceInfo.getAmount() + discountAmount;
					//itemPriceInfo.setDiscountAmount(discountAmount);
					itemPriceInfo.setEffectivePrice(effectivePrice);
					
					vlogDebug("Item {0} is a gift. Discount Amount set to {1} and effective price set to {2}", item.getId(), discountAmount, effectivePrice);
					item.setPriceInfo(itemPriceInfo);
				}
				
			} else {
				// this could be a qualifier if we find it in the splitDiscount map
				
				Double discountShare = getSplitDiscountMap().get(item.getId());

				if (discountShare != null) {
					discountShare = this.getPricingTools().round(discountShare);
					effectivePrice = itemPriceInfo.getAmount() - discountShare;
					itemPriceInfo.setDiscountAmount(discountShare);
					itemPriceInfo.setEffectivePrice(effectivePrice);
					item.setPriceInfo(itemPriceInfo);
					vlogDebug("Item {0} is a qualifier. Discount Amount set to {1} and effective price set to {2}", item.getId(), discountShare, effectivePrice);
				} else {
					// update priceInfos for other items
					// that arent qualifier or gwp items
					// These may have simple item discount promos (like Buy 1 Get 1 Free)
					effectivePrice = itemPriceInfo.getAmount();
					discountAmount = getGwpPromoHelper().getNonGWPDiscountValue(item);
					itemPriceInfo.setDiscountAmount(discountAmount);
					itemPriceInfo.setEffectivePrice(effectivePrice);
					item.setPriceInfo(itemPriceInfo);
					vlogDebug("Item {0} is neither a gift nor a qualifier. Effective price set to {1}", item.getId(), effectivePrice);
					
				}
				
			}

		}		
		
	}
	
	public void updateProratedDiscountPriceInfos(List pPriceQuotes, List pItems) {
		java.util.ListIterator priceQuoteIte = pPriceQuotes.listIterator();
		java.util.ListIterator itemsIter = pItems.listIterator();
		double discountAmount = 0.0;
		double effectivePrice = 0.0;
		vlogInfo("Updating PriceInfos");
		while (priceQuoteIte.hasNext() && itemsIter.hasNext()) {
		
			MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo) priceQuoteIte.next();
			MFFCommerceItemImpl item = (MFFCommerceItemImpl) itemsIter.next();
			
			// does this item need to be prorated
			if(getProratedDiscounts() != null && getProratedDiscounts().get(item.getId()) != null
					&& getEffectivePrices() != null && getEffectivePrices().get(item.getId()) != null) {
				
				
				discountAmount = getProratedDiscounts().get(item.getId());
				effectivePrice = getEffectivePrices().get(item.getId());
				itemPriceInfo.setDiscountAmount(discountAmount);
				itemPriceInfo.setEffectivePrice(effectivePrice);
				vlogInfo(">>> Item {0} Discount Amount {1} Effective Price {2}", item.getId(),discountAmount, effectivePrice);
				item.setPriceInfo(itemPriceInfo);
				
				// if item is a gift and the gift is NOT a gift card
				// add the promoId to the item. This will be needed to 
				// create Sales Audit lineDiscount records
				if(item.isGwp() && !item.isGiftCard()) {
					String gwpPromoId = getGwpPromoHelper().getGiftPromoId(item);
					if(gwpPromoId != null) {
						item.setGwpPromoId(gwpPromoId);
					}
				}
			} 
		}
	}
	public void updatePriceInfos(List pPriceQuotes, List pItems) {
		
		
		if(isSplitDiscounts) {
			updateSplitDiscountPriceInfos(pPriceQuotes,pItems);
		} else {
			updateProratedDiscountPriceInfos(pPriceQuotes,pItems);
		}
	}

	/**
	 * Price items.
	 *
	 * @param pPriceQuotes the price quotes
	 * @param pItems the items
	 * @param pPricingModel the pricing model
	 * @param pLocale the locale
	 * @param pProfile the profile
	 * @param pOrder the order
	 * @param paramMap the param map
	 * @throws PricingException the pricing exception
	 */
	@Override
	public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Order pOrder, Map paramMap) throws PricingException {
		// 2414: Commenting out prorate disc calculator
		// The discounts do not appear to be calculcated correctly
		// There arent any discounts prorated as of 10/2/2017 in PROD
		// Will be taken up when 2149 is prioritized

		/*
		 * try { this.prorateDiscounts(pPriceQuotes, pItems, pOrder, pProfile,
		 * pLocale, paramMap); logDebug("Here"); } catch (RepositoryException e)
		 * { if (isLoggingError()) { logError("Some  problem with pro-ration" +
		 * e); } }
		 */
		//computeGwpProperties(pOrder, pItems);
		
		if (isSplitDiscounts) {
			vlogDebug("Split Discounts turned on");
			splitDiscounts(pOrder, pPriceQuotes, pItems);
			//updateNonGWPItemDiscounts(pOrder, pPriceQuotes, pItems);
		}
		if(isProrate) {
			prorateDiscounts(pOrder, pPriceQuotes, pItems);
			updateNonGWPItemDiscounts(pOrder, pPriceQuotes, pItems);
		}

	}

	public double getTotal (ArrayList <MFFCommerceItemImpl> pQualifiers) {
		double total = 0.0;
		
		for(MFFCommerceItemImpl qualifier : pQualifiers) {
			total += qualifier.getPriceInfo().getAmount();
		}
		return total;
	}
	
	public void prorateDiscounts (Order pOrder, List pPriceQuotes, List pItems) {
		// get the gift items to calculate the discount amount that is to be prorated
		ArrayList<MFFCommerceItemImpl> gwpItems = getGwpPromoHelper().getGWPItems(pOrder);
		ArrayList<DiscountInfo> discountInfos = new ArrayList<DiscountInfo>();

		if(gwpItems!=null && gwpItems.size() > 0) {
			
			for(MFFCommerceItemImpl giftItem : gwpItems) {
				vlogInfo("******************** Prorating discount from gift commerce item {0} ****************", giftItem.getId());
				vlogInfo(">>> Gift Item priceInfo.amount = {0}", giftItem.getPriceInfo().getAmount());
				
				double giftValue = getGwpPromoHelper().getGiftValue(giftItem);
				vlogInfo(">>> Adjustment = {0}", giftValue);
				
				String gwpPromoId = getGwpPromoHelper().getGiftPromoId(giftItem);
				vlogInfo(">>> GWP Promo Id = {0}", gwpPromoId);
				
				if(gwpPromoId != null) {
					// get list of qualifiers for this gift item
					ArrayList<MFFCommerceItemImpl> qualifiers = getGwpPromoHelper().getQualifiersForGWP(pOrder, gwpPromoId, false);
					vlogInfo(">>> Qualifier count = {0}", qualifiers.size());
					RepositoryItem promotion = getGwpPromoHelper().getGiftPromo(giftItem);
					if(promotion != null) {
						vlogInfo(">>> Promotion is NOT null");
						String template = (String)promotion.getPropertyValue("template");
						vlogInfo(">>> Promo Template = --{0}--", template);
						if(template != null && template.equalsIgnoreCase("/item/spendYGetGWP.pmdt")) {
							qualifiers = getGwpPromoHelper().getQualifiersForGWP(pOrder, gwpPromoId, true);
							vlogInfo(">>> Spend X Adjusted Qualifier count = {0}", qualifiers.size());
						}
					}
					
					// if gift is a gift card, then prorate only between qualifiers
					// GC should be $10
					// $10 should be prorated between the qualifiers
					
					double totalQualifierAmount = getTotal (qualifiers);
					double total = totalQualifierAmount;
					
					// setup discountInfo object
					
					DiscountInfo discountInfo = new DiscountInfo();
					discountInfo.setGiftItem(giftItem);
					discountInfo.setDiscountAmount(giftValue);
					
					vlogInfo(">>> Is gift a gift card? = {0}", giftItem.isGiftCard());
					if(giftItem.isGiftCard()) {
						if(isLoggingInfo()) {
							vlogInfo(">>> Has {0} qualifiers", qualifiers.size());
							for (MFFCommerceItemImpl qualifier : qualifiers) {
								vlogInfo(">>>>>>>> {0}", qualifier.getId());
							}
						}
						vlogInfo(">>> Total {0}", total);
						discountInfo.setQualifierItems(qualifiers);
						discountInfo.setTotalQualifierAmount(total);
					} else {
						// if gift is another sku, then prorate between gift & qualifiers
						// $39 gift item... $39 split between qualifiers and gifts
						
						// consider the gift item in proration calculations
						// total will include the giftItem value
						// and any non-zero amount on the item as well
						// say user added the gift as a separate item to the cart
						// that item will be priced at its original cost
						// qualifiers will include the gift item as well
						total = totalQualifierAmount + giftItem.getPriceInfo().getAmount() + giftValue;
						qualifiers.add(giftItem);

						if(isLoggingInfo()) {
							vlogInfo(">>> Has {0} qualifiers including itself", qualifiers.size());
							for (MFFCommerceItemImpl qualifier : qualifiers) {
								vlogInfo(">>>>>>>> {0}", qualifier.getId());
							}
						}
						vlogInfo(">>> Total {0}", total);
						
						discountInfo.setQualifierItems(qualifiers);
						discountInfo.setTotalQualifierAmount(total);						
					}
					discountInfos.add(discountInfo);
				}
			}
			prorateItems(pOrder, discountInfos);
			updatePriceInfos(pPriceQuotes, pItems);
		}
		
	}
	
	public void prorateItems(Order pOrder, List<DiscountInfo> pDiscountInfos) {
		HashMap<String, Double> lProratedDiscounts = new HashMap<String, Double>();
		HashMap<String, Double> lEffectivePrices = new HashMap<String, Double>();

		double prorateFactor = 0.0;
		double totalAmount = 0.0;
		double itemAmount = 0.0;
		double giftValue = 0.0;
		double proratedDiscount = 0.0;
		double effectivePrice = 0.0;
		
		for(DiscountInfo discountInfo : pDiscountInfos) {
			
			MFFCommerceItemImpl giftItem = discountInfo.getGiftItem();
			String gwpPromoId = getGwpPromoHelper().getGiftPromoId(giftItem);
			totalAmount = discountInfo.getTotalQualifierAmount();
			giftValue = discountInfo.getDiscountAmount();
			double remainingGiftValue = giftValue;
			double totalProratedDiscount = 0.0;
			
			// get list of qualifiers to be updated
			List<MFFCommerceItemImpl> qualifiers = discountInfo.getQualifierItems();
			int lCtr = 1;
			for(MFFCommerceItemImpl qualifier : qualifiers) {
				
				itemAmount = qualifier.getPriceInfo().getAmount();
				
				if(qualifier.isGwp()) {
					itemAmount = qualifier.getPriceInfo().getAmount() + giftValue;
				}
				
				prorateFactor = itemAmount / totalAmount;

				// handle rounding issues here
				// the last qualifier get the remaining gift value
				if(lCtr == qualifiers.size()) {
					proratedDiscount = remainingGiftValue;
				} else {
					proratedDiscount = this.getPricingTools().round(giftValue * prorateFactor);
				}
				totalProratedDiscount += proratedDiscount;
				remainingGiftValue -= proratedDiscount;
				
				// Add any existing discounts
				if(lProratedDiscounts.get(qualifier.getId()) != null) {
					proratedDiscount += lProratedDiscounts.get(qualifier.getId());
				}
				effectivePrice = itemAmount - proratedDiscount;

				lProratedDiscounts.put(qualifier.getId(), proratedDiscount);
				lEffectivePrices.put(qualifier.getId(), effectivePrice);
				
				if(qualifier.getGwpPromoId() == null || qualifier.getGwpPromoId().equalsIgnoreCase("")) {
					qualifier.setGwpPromoId(gwpPromoId);
				}
				lCtr++;
			}
			vlogInfo("$$$$$$$$$$$$$$$$$$$$$$$$$ ---->>>>> Total discount of {0}. Prorated Total {1}. Remaining {2}", giftValue,totalProratedDiscount, remainingGiftValue);
			if(giftItem.isGiftCard()) {
				lProratedDiscounts.put(giftItem.getId(), giftValue);
				lEffectivePrices.put(giftItem.getId(), giftValue);
			}
		}
		setProratedDiscounts(lProratedDiscounts);
		setEffectivePrices(lEffectivePrices);
	}
	
	
	public void computeGwpProperties(Order pOrder, List pItems) {
		Iterator<MFFCommerceItemImpl> itemIter = pItems.iterator();
		Map<String, Double> itemGiftProps = new HashMap<String, Double>();
		while (itemIter.hasNext()) {
			MFFCommerceItemImpl item = itemIter.next();

			// Qualifier
			if (item != null && item.getGwpPromoId() != null) {
				// item.setGwpValue(getGiftValue(pOrder, item.getGwpPromoId()));
				setGiftValues(pOrder, item, itemGiftProps);
			}
		}
	}

	private void setGiftValues(Order pOrder,MFFCommerceItemImpl pQualiferItem, Map pItemGiftProps) {
		double giftAmount=0.0;
		MFFCommerceItemImpl currItem=null;
		vlogDebug("Setting gift values for item {0}", pQualiferItem.getId());

		Iterator<CommerceItem> itemIter = pOrder.getCommerceItems().iterator();
		while (itemIter.hasNext()) {
			currItem = (MFFCommerceItemImpl)itemIter.next();
			vlogDebug("Checking item {0}", currItem.getId());

			// current item in the order is a gift item
			// set some of this values as we iterate over the items
			if((Boolean)currItem.getPropertyValue("gwp")) {

				vlogDebug("Item {0} is a gwp", currItem.getId());

				double itemGiftValue=0.0; // tracks value of current item's adjustments

				Iterator adjIter = currItem.getPriceInfo().getAdjustments().iterator();
				while(adjIter.hasNext()) {
					PricingAdjustment adj = (PricingAdjustment) adjIter.next();
					if(adj.getPricingModel()!= null && adj.getPricingModel().getRepositoryId().equalsIgnoreCase(pQualiferItem.getGwpPromoId())) {

						vlogDebug("Item {0} is a gift for qualifer item {1}", currItem.getId(), pQualiferItem.getId());

						giftAmount += adj.getTotalAdjustment();
						itemGiftValue=-1*adj.getTotalAdjustment();

						// if current item is a gift card, then this is a GWP with a free gift card
						if(currItem.getAuxiliaryData() != null && currItem.getAuxiliaryData().getProductId()!= null && 
								((MFFCatalogTools)getPricingTools().getCatalogTools()).isGCProduct(currItem.getAuxiliaryData().getProductId())) {
							vlogDebug("Item {0} is a gift card. Marking qualifier flag gwpGiftCardPromo", currItem.getId());
							pQualiferItem.setGwpGiftCardPromo(true);
							if(itemGiftValue > 0.0) {
								currItem.setGwpValue(itemGiftValue);
								currItem.setGwpGiftCardValue(itemGiftValue);
							}

						}
					}
				}
				if(itemGiftValue > 0.0) {
					currItem.setGwpValue(itemGiftValue);
				}			  

			} else {
				vlogDebug("Item {0} is not a gwp", currItem.getId());
			}
		}
		pQualiferItem.setGwpValue(-1*giftAmount);
		if(pQualiferItem.isGwpGiftCardPromo()) {
			pQualiferItem.setGwpGiftCardValue(-1*giftAmount);
		}
		//return -1 * giftAmount;	  
	}

	/**
	 * Gets the promotion.
	 *
	 * @param item the item
	 * @return the promotion
	 */
	@SuppressWarnings("rawtypes")
	private RepositoryItem getPromotion(CommerceItem item) {

		MFFCommerceItemImpl mffItem = (MFFCommerceItemImpl) item;
		RepositoryItem promo = null;

		if (!mffItem.isGiftCard() && null != item.getPriceInfo()) {
			List adjments = item.getPriceInfo().getAdjustments();
			for (Object adj : adjments) {
				PricingAdjustment adjustment = null;
				if (adj instanceof PricingAdjustment) {
					adjustment = (PricingAdjustment) adj;
					promo = adjustment.getPricingModel();

					if (promo != null) {
						if (promo.getRepositoryId().contains("_b") || promo.getRepositoryId().contains("_a")) {
							return null;
						}
						break;
					}
				}
			}
		}

		return promo;
	}

	/**
	 * Gets the pricing tools.
	 *
	 * @return the pricing tools
	 */
	public PricingTools getPricingTools() {
		return pricingTools;
	}

	/**
	 * Sets the pricing tools.
	 *
	 * @param pricingTools the new pricing tools
	 */
	public void setPricingTools(PricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	/**
	 * Gets the qualifier service.
	 *
	 * @return the qualifier service
	 */
	public MFFQualifierService getQualifierService() {
		return qualifierService;
	}

	/**
	 * Sets the qualifier service.
	 *
	 * @param qualifierService the new qualifier service
	 */
	public void setQualifierService(MFFQualifierService qualifierService) {
		this.qualifierService = qualifierService;
	}

	@Override
	public void priceEachItem(List paramList1, List paramList2, RepositoryItem paramRepositoryItem1, Locale paramLocale, RepositoryItem paramRepositoryItem2, Map paramMap) throws PricingException {
	}

	@Override
	public void priceItem(ItemPriceInfo paramItemPriceInfo, CommerceItem paramCommerceItem, RepositoryItem paramRepositoryItem1, Locale paramLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
	}

}
