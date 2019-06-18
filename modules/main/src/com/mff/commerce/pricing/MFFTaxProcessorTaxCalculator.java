/**
 * 
 */
package com.mff.commerce.pricing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.payment.MFFDummyTaxProcessor;
import com.mff.commerce.payment.tax.MFFTaxStatusImpl;
import com.mff.commerce.payment.tax.MFFTaxStatusItem;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.AddressVerificationTaxProcessorTaxCalculator;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.payment.tax.ShippingDestination;
import atg.payment.tax.TaxRequestInfo;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;

/**
 * @author VishnuVardhan
 * 
 */
public class MFFTaxProcessorTaxCalculator extends AddressVerificationTaxProcessorTaxCalculator {

	private boolean	useVertex	= false;
	private MFFDummyTaxProcessor mDummyTaxProcessor;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void calculateTax(TaxRequestInfo pTRI, TaxPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {

		if (isUseVertex()) {
			ShippingDestination[] mShippingDestination = pTRI.getShippingDestinations();
			if (0 == mShippingDestination.length) {
				vlogDebug("Not making Tax call to Vertrx, as there is No Shipping Address associated with Cart.");
				return;
			}

			for (ShippingDestination shippingDestination : mShippingDestination) {
				Address address = shippingDestination.getShippingAddress();
				if (address == null || StringUtils.isBlank(address.getCity()) || StringUtils.isBlank(address.getState())) {
					if (isLoggingDebug()) {
						logDebug("Not making Tax call to Vertrx, as there is No Shipping Address associated with Cart.");
					}
					return;
				}
				
				// do not call vertex if country is not US
				if(address!=null && !StringUtils.isEmpty(address.getCountry()) && !address.getCountry().equalsIgnoreCase("US")){
					if (isLoggingDebug()) {
						logDebug("Not making Tax call to Vertrx, as country is not US");
					}
					return;
				}
			}
			
			vlogDebug("VertexTaxService: Calculate Tax - Start");
			PerformanceMonitor.startOperation("VertexTaxService","REGULAR");
			MFFTaxStatusImpl mEXTNTaxStatus = (MFFTaxStatusImpl) getTaxProcessor().calculateTax(pTRI);
			PerformanceMonitor.endOperation("VertexTaxService","REGULAR");
			vlogDebug("VertexTaxService: Calculate Tax - End");

			if (null != mEXTNTaxStatus) {
				// Calculate the rounded Tax Amount
				double roundedTaxAmount = getPricingTools().round(mEXTNTaxStatus.getAmount());
				if (isLoggingDebug()) {
					logDebug("Total Order Tax : " + roundedTaxAmount);
				}

				// Set Tax Price Info for the Order
				pPriceQuote.setAmount(roundedTaxAmount);
				pPriceQuote.setCityTax(mEXTNTaxStatus.getCityTax());
				pPriceQuote.setCountyTax((mEXTNTaxStatus.getCountyTax() + mEXTNTaxStatus.getDistrictTax()));
				pPriceQuote.setStateTax(mEXTNTaxStatus.getStateTax());
				pPriceQuote.setCountryTax(mEXTNTaxStatus.getCountryTax());
				((MFFTaxPriceInfo)pPriceQuote).setCountyTaxInfo(mEXTNTaxStatus.getCountyTaxInfo());
				
				assignItemTaxAmounts(pOrder, mEXTNTaxStatus);

				assignShippingCharges(pOrder, mEXTNTaxStatus, pPriceQuote);
			}
		}
		else
		{
			setTaxProcessor(getDummyTaxProcessor());
			super.calculateTax(pTRI, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void calculateTaxByShipping(TaxRequestInfo pTRI, TaxPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		if(!isUseVertex())
		{
			setTaxProcessor(getDummyTaxProcessor());
		}
		vlogDebug("VertexTaxService: Calculate Tax By Shipping - Start");
		PerformanceMonitor.startOperation("VertexTaxService","Shipping");
		super.calculateTaxByShipping(pTRI, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
		PerformanceMonitor.endOperation("VertexTaxService","Shipping");
		vlogDebug("VertexTaxService: Calculate Tax By Shipping - End");
	}

	protected void assignItemTaxAmounts(Order pOrder, MFFTaxStatusImpl pEXTNTaxStatus) {
		int lCtr = 0;
		
		Map<String, MFFTaxStatusItem> lEXTNTaxStatusItems = pEXTNTaxStatus.getEXTNTaxStatusItems();

		@SuppressWarnings("unchecked")
		List<MFFCommerceItemImpl> lCommerceItems = pOrder.getCommerceItems();
		for (MFFCommerceItemImpl lCommerceItem : lCommerceItems) {
			MFFTaxStatusItem lEXTNTaxStatusItem = lEXTNTaxStatusItems.get(lCommerceItem.getId());
			double lTax = (lEXTNTaxStatusItem!=null)?lEXTNTaxStatusItem.getTax():0.0;
			double lTaxRemainder = lTax;
			double lTaxableAmt = getPricingTools().round(lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare());
			ItemPriceInfo lItemPriceInfo = lCommerceItem.getPriceInfo();
			@SuppressWarnings("unchecked")
			List<DetailedItemPriceInfo> lDetailedItemPriceInfos = lItemPriceInfo.getCurrentPriceDetails();
			// reset counter for each commerce item.
			lCtr = 0;
			//Not required as we are sending order discount share to vertex --- A 10/21/2016 Need a verification
			for (DetailedItemPriceInfo lDetailedItemPriceInfo : lDetailedItemPriceInfos) {
				lCtr++;
				double lAmount = lDetailedItemPriceInfo.getAmount();
				double lOrderDiscountShare = lDetailedItemPriceInfo.getOrderDiscountShare();
				lAmount = lAmount - lOrderDiscountShare;
				double lRatio = (lTaxableAmt > 0) ? lAmount / lTaxableAmt : 0.0;
				double lItemTax = getPricingTools().roundDown(lTax * lRatio);
				lTaxRemainder = lTaxRemainder - lItemTax;
				lDetailedItemPriceInfo.setTax(lItemTax);
				// Account for remainder
				if (lCtr == lDetailedItemPriceInfos.size() && lTaxRemainder != 0) {
					double lRoundedAmt = (new BigDecimal(lDetailedItemPriceInfo.getTax() + lTaxRemainder).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
					lDetailedItemPriceInfo.setTax(lRoundedAmt);
				}
			}
			
			double lItemTax = lEXTNTaxStatusItem.getTax();
			double lItemCountyTax = lEXTNTaxStatusItem.getCountyTax();
			double lItemgCityTax = lEXTNTaxStatusItem.getCityTax();
			double lItemDistrictTax = lEXTNTaxStatusItem.getDistrictTax();
			double lItemStateTax = lEXTNTaxStatusItem.getStateTax();
			
			vlogDebug("Total City Tax : " + lItemCountyTax);
			vlogDebug("Total State Tax : " + lItemgCityTax);
			vlogDebug("Total County Tax : " + lItemDistrictTax);
			vlogDebug("Total District Tax : "+lItemStateTax);
			
			MFFTaxPriceInfo itemLevelTaxPriceInfo = (MFFTaxPriceInfo)lCommerceItem.getTaxPriceInfo();
			MFFTaxPriceInfo lTaxPriceInfo = new MFFTaxPriceInfo();
			lTaxPriceInfo.setAmount(lItemTax);
			lTaxPriceInfo.setCountyTax((lItemCountyTax + lItemDistrictTax));
			lTaxPriceInfo.setCityTax(lItemgCityTax);
			lTaxPriceInfo.setStateTax(lItemStateTax);
			lTaxPriceInfo.setCountyTaxInfo(lEXTNTaxStatusItem.getCountyTaxInfo());
			lCommerceItem.setTaxPriceInfo(lTaxPriceInfo);
			itemLevelTaxPriceInfo= lTaxPriceInfo;
			
			
		}
	}

	private void assignShippingCharges(Order pOrder, MFFTaxStatusImpl pEXTNTaxStatus, TaxPriceInfo pPriceQuote) {
		Map<String, MFFTaxStatusItem> lEXTNTaxStatusItems = pEXTNTaxStatus.getEXTNTaxStatusItems();

		Map<String, TaxPriceInfo> mShippingItemsTaxPriceInfos = new HashMap<String, TaxPriceInfo>();
		MFFTaxStatusItem lEXTNShippingTaxStatusItem = lEXTNTaxStatusItems.get("FREIGHT");
		double lShippingTax = lEXTNShippingTaxStatusItem.getTax();
		double lShippingCountyTax = lEXTNShippingTaxStatusItem.getCountyTax();
		double lShippingCityTax = lEXTNShippingTaxStatusItem.getCityTax();
		double lShippingDistrictTax = lEXTNShippingTaxStatusItem.getDistrictTax();
		double lShippingStateTax = lEXTNShippingTaxStatusItem.getStateTax();

		@SuppressWarnings("unchecked")
		List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
		for (ShippingGroup lShippingGroup : lShippingGroups) {
			if (lShippingGroup instanceof HardgoodShippingGroup) {
				TaxPriceInfo lTaxPriceInfo = new TaxPriceInfo();
				lTaxPriceInfo.setAmount(lShippingTax);
				lTaxPriceInfo.setCountyTax((lShippingCountyTax + lShippingDistrictTax));
				lTaxPriceInfo.setCityTax(lShippingCityTax);
				lTaxPriceInfo.setStateTax(lShippingStateTax);
				mShippingItemsTaxPriceInfos.put(lShippingGroup.getId(), lTaxPriceInfo);
			}
		}
		pPriceQuote.setShippingItemsTaxPriceInfos(mShippingItemsTaxPriceInfos);

	}

	/**
	 * @return the useVertex
	 */
	public boolean isUseVertex() {
		return useVertex;
	}

	/**
	 * @param pUseVertex
	 *            the useVertex to set
	 */
	public void setUseVertex(boolean pUseVertex) {
		useVertex = pUseVertex;
	}

	/**
	 * @return the dummyTaxProcessor
	 */
	public MFFDummyTaxProcessor getDummyTaxProcessor() {
		return mDummyTaxProcessor;
	}

	/**
	 * @param pDummyTaxProcessor the dummyTaxProcessor to set
	 */
	public void setDummyTaxProcessor(MFFDummyTaxProcessor pDummyTaxProcessor) {
		mDummyTaxProcessor = pDummyTaxProcessor;
	}

}
