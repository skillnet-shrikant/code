package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingModelHolder;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;

public class OrderItemPriceDroplet extends DynamoServlet {
	
	private PricingModelHolder mUserPricingModels;
	private String mPricingOp;
	private Profile mProfile;
	private MFFOrderImpl mOrder;
	private PurchaseProcessHelper mPurchaseProcessHelper;
	
	private static final String COMMERCE_ITEM_PARAM = "commerceItem";
	private static final String REGULAR_PRICE = "regularPrice";
	private static final String SELLING_PRICE = "sellingPrice";
	private static final String PRICE_SAVINGS = "priceSavings";
	private static final String REGULAR_PRICE_AMOUNT = "regularPriceAmount";
	private static final String PRORATED_AMOUNT = "proratedAmount";
	private static final String PRORATED_PRICE = "proratedPrice";
	private static final String DISCOUNT_AMOUNT = "discountAmount";
	private static final String DISCOUNT_PERCENT = "discountPercent";
	
	public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		double regularPrice=0, sellingPrice=0, priceSavings=0, regularPriceAmount=0, proratedAmount=0, proratedPrice=0, discountAmount=0, discountPercent=0;
		// Get the input param
		CommerceItem commerceItem = (CommerceItem)pRequest.getObjectParameter(COMMERCE_ITEM_PARAM);
		if (commerceItem!=null){
			ItemPriceInfo priceInfo = (ItemPriceInfo)commerceItem.getPriceInfo();
			if (priceInfo==null){
				// reprice the order
				try{
					runRepriceOrder(pRequest);
				}
				catch (RunProcessException e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
			}
			
			if (priceInfo!=null){
				long quantity = commerceItem.getQuantity();
				regularPrice = priceInfo.getListPrice();
				sellingPrice = priceInfo.getSalePrice();
				regularPriceAmount = regularPrice * quantity;
				
				//TODO : Change this to proper calculations
				priceSavings = regularPrice - sellingPrice;
				
				proratedAmount = priceInfo.getAmount();
				discountAmount = regularPriceAmount - proratedAmount; //priceInfo.getDiscountAmount();
				// Also add pro-rated item level discount to the discountAmount
				double orderDiscountShare = priceInfo.getOrderDiscountShare();
				if (orderDiscountShare>0){
					discountAmount+=orderDiscountShare;
					proratedAmount-=orderDiscountShare;
				}
				// Round the discount amount to 2 digits, there are times when the discountAmount has more digits, for example 0.00000000000001 
				discountAmount = roundToDecimals(discountAmount);
				//calculate prorated price
				proratedPrice = proratedAmount/quantity;
				if (discountAmount > 0) {
					discountPercent = (discountAmount/(regularPrice * quantity))*100;
					DecimalFormat df = new DecimalFormat("###.##");
					discountPercent = Double.valueOf(df.format(discountPercent));
				}
				if (isLoggingDebug()) {
					logDebug ("CommerceItem:"+commerceItem.getId()+", regularPrice:" + regularPrice + ", sellingPrice:" + sellingPrice + ", priceSavings:" + priceSavings + "proratedAmount:"+proratedAmount+", discountAmount:"+discountAmount+", discountPercent:"+discountPercent);
				}
			}
		} else {
			if (isLoggingWarning()) logWarning("Commerce Item is null, hence could not display price");
		}

		pRequest.setParameter(REGULAR_PRICE, regularPrice);
		pRequest.setParameter(SELLING_PRICE, sellingPrice);
		pRequest.setParameter(PRICE_SAVINGS, priceSavings);	
		pRequest.setParameter(REGULAR_PRICE_AMOUNT, regularPriceAmount);
		pRequest.setParameter(PRORATED_AMOUNT, proratedAmount);
		pRequest.setParameter(PRORATED_PRICE, proratedPrice);
		pRequest.setParameter(DISCOUNT_AMOUNT, discountAmount);
		pRequest.setParameter(DISCOUNT_PERCENT, discountPercent);
		pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
	}
	
	public double roundToDecimals(double d){
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Double.valueOf(df.format(d));
	}
	
	private void runRepriceOrder(DynamoHttpServletRequest pRequest) throws RunProcessException {
		if (isLoggingWarning()) {
			logWarning("Price info is null, hence repricing to get the price back");
		}
		RequestLocale requestLocale = pRequest.getRequestLocale();
		Locale locale = null;
		if (requestLocale != null) {
			locale = requestLocale.getLocale();
		}

		getPurchaseProcessHelper().runProcessRepriceOrder(getPricingOp(), getOrder(), getUserPricingModels(), locale, getProfile(), null, null);
	}

	public PricingModelHolder getUserPricingModels() {
		return mUserPricingModels;
	}

	public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
		this.mUserPricingModels = pUserPricingModels;
	}

	public String getPricingOp() {
		return mPricingOp;
	}

	public void setPricingOp(String pPricingOp) {
		this.mPricingOp = pPricingOp;
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		this.mProfile = pProfile;
	}

	public MFFOrderImpl getOrder() {
		return mOrder;
	}

	public void setOrder(MFFOrderImpl pOrder) {
		this.mOrder = pOrder;
	}

	public PurchaseProcessHelper getPurchaseProcessHelper() {
		return mPurchaseProcessHelper;
	}

	public void setPurchaseProcessHelper(
			PurchaseProcessHelper pPurchaseProcessHelper) {
		this.mPurchaseProcessHelper = pPurchaseProcessHelper;
	}

}
