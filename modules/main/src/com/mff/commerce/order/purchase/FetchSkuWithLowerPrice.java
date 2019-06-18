package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.ResourceUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

public class FetchSkuWithLowerPrice extends DynamoServlet {
	
	private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(
			"atg.commerce.pricing.Resources", LangLicense.getLicensedDefault());
	static final String MY_RESOURCE_NAME = "atg.commerce.pricing.Resources";
	public static String PRODUCT_ID = "productId";
	public static String FILTERED_SKUS = "filteredSkus";
	public static String PRICE_LIST = "priceList";
	public static String SALE_PRICE_LIST = "salePriceList";
	public static String LOWEST_PRICE = "lowestPrice";
	public static String SKU_WITH_LOWEST_PRICE = "skuWithLowestPrice";
	public static String OUTPUT = "output";
	public static String LIST_PRICE = "listPrice";
	public static String SALE_PRICE = "salePrice";
	Repository mCatalogRepository;
	String mProductItemType;
	public PricingTools mPricingTools;
	public PriceListManager mPriceListManager;
	CommercePropertyManager mCommercePropertyManager;

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		vlogDebug("Service Called");
		Object productId = pRequest.getObjectParameter(PRODUCT_ID);
		Object priceListObject = pRequest.getObjectParameter(PRICE_LIST);
		Object salePriceListObject = pRequest.getObjectParameter(SALE_PRICE_LIST);
		List filteredChildSKUs = (List) pRequest.getObjectParameter(FILTERED_SKUS);
		
		vlogDebug("filteredChildSKUs: " + filteredChildSKUs);
		
		if (filteredChildSKUs != null && filteredChildSKUs.size() > 0) {
			
			String msg;
			try {
				RepositoryItem productItem = this.getCatalogRepository().getItem(
						productId.toString(), this.getProductItemType());
				vlogDebug("Service: productItem: " +productItem);
				RepositoryItem priceList = null;
				RepositoryItem salePriceList = null;
				if (priceListObject instanceof RepositoryItem) {
					priceList = (RepositoryItem) priceListObject;
				}

				if (salePriceListObject instanceof RepositoryItem) {
					salePriceList = (RepositoryItem) salePriceListObject;
				}

				if (productItem != null) {
					this.retrieveLowestPriceListPrice(pRequest, productItem, filteredChildSKUs, priceList, salePriceList);
				}
			} catch (RepositoryException arg12) {
				if (this.isLoggingError()) {
					msg = ResourceUtils.getMsgResource("cannotGetProdItem",
							"atg.commerce.pricing.Resources", sResourceBundle);
					this.logError(
							MessageFormat.format(msg,
									new Object[]{productId.toString()}), arg12);
				}
			} catch (PriceListException arg13) {
				if (this.isLoggingError()) {
					msg = ResourceUtils.getMsgResource("cannotGetPriceItem",
							"atg.commerce.pricing.Resources", sResourceBundle);
					this.logError(
							MessageFormat.format(msg,
									new Object[]{productId.toString()}), arg13);
				}
			}
		}

		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
	
	public void retrieveLowestPriceListPrice(DynamoHttpServletRequest pRequest,
			RepositoryItem pProduct, List pFilteredChildSKUs,
			RepositoryItem pPriceList, RepositoryItem pSalePriceList)
			throws PriceListException {
		
		vlogDebug("retrieveLowestPriceListPrice Called: " + pPriceList);
		
		if (pPriceList == null) {
			pPriceList = getPricingTools().getPriceList(ServletUtil.getCurrentUserProfile(),
					this.getCommercePropertyManager()
							.getPriceListPropertyName(), true, (Map) null);
		}

		Vector listPrices = new Vector();
		Vector salePrices = new Vector();
		double lowestPrice = 0.0D;
		if (pPriceList != null) {
			this.generatePriceListPrices(pProduct, pFilteredChildSKUs, pPriceList, listPrices);
		}
		
		if (pSalePriceList == null) {
			pSalePriceList = getPricingTools().getPriceList(ServletUtil
					.getCurrentUserProfile(), this.getCommercePropertyManager()
					.getSalePriceListPropertyName(), false, (Map) null);
		}
		RepositoryItem lowsale = null;
		
		if (pSalePriceList != null) {
			this.generatePriceListPrices(pProduct, pFilteredChildSKUs, pSalePriceList, salePrices);
			lowsale = this.getLowestPriceItem(salePrices);
		}
		RepositoryItem lowlist = this.getLowestPriceItem(listPrices);
		
		vlogDebug("retrieveLowestPriceListPrice(): lowlist: " + lowlist);
		vlogDebug("retrieveLowestPriceListPrice(): lowsale: " + lowsale);
		
		Double lowListPrice = null;
		if (lowlist != null) {
			lowListPrice = (Double) lowlist.getPropertyValue(this
						.getPriceListManager().getListPricePropertyName());
		}

		double doubleListPrice = 0.0D;
		if (lowListPrice != null) {
			doubleListPrice = lowListPrice.doubleValue();
		}
		Double lowSalePrice = null;
		if (lowsale != null) {
			lowSalePrice = (Double) lowsale.getPropertyValue(this
					.getPriceListManager().getListPricePropertyName());
		}

		double doubleSalePrice = 0.0D;
		if (lowSalePrice != null) {
			doubleSalePrice = lowSalePrice.doubleValue();
		}
		
		String lowestPriceSkuId = null;
		
		vlogDebug("retrieveLowestPriceListPrice(): doubleListPrice: " + doubleListPrice);
		vlogDebug("retrieveLowestPriceListPrice(): doubleSalePrice: " + doubleSalePrice);
		
		if (doubleSalePrice != 0 && doubleListPrice >= doubleSalePrice){
			vlogDebug("retrieveLowestPriceListPrice(): sale price lower.");
			if (lowsale != null) {
				lowestPriceSkuId = (String)lowsale.getPropertyValue(this
					.getPriceListManager().getSkuIdPropertyName());
			}
			lowestPrice = doubleSalePrice;
		} else{
			vlogDebug("retrieveLowestPriceListPrice(): list price lower.");
			if (lowlist != null) {
				lowestPriceSkuId = (String)lowlist.getPropertyValue(this
					.getPriceListManager().getSkuIdPropertyName());
			}
			lowestPrice = doubleListPrice;
		}
		setHighLowPriceOutputParams(pRequest, lowestPriceSkuId, lowestPrice);
	}
	
	protected void generatePriceListPrices(RepositoryItem pProduct, List pFilteredChildSKUs,
			RepositoryItem pPriceList, List pPrices) {
		
		vlogDebug("generatePriceListPrices Called");
			
		for (int i = 0; i < pFilteredChildSKUs.size(); ++i) {
			RepositoryItem sku = (RepositoryItem) pFilteredChildSKUs.get(i);

			try {
				RepositoryItem priceItem = this.getPriceListManager().getPrice(
						pPriceList, pProduct, sku);
				vlogDebug("generatePriceListPrices: priceItem: " + priceItem);
				if (priceItem != null) {
					pPrices.add(priceItem);
				}
			} catch (PriceListException arg8) {
				if (this.isLoggingError()) {
					String msg = ResourceUtils.getMsgResource(
							"cannotGetPriceForProduct",
							"atg.commerce.pricing.Resources",
							sResourceBundle);
					this.logError(MessageFormat.format(msg,
							new Object[]{pProduct}), arg8);
				}
			}
		}
	}
	
	protected RepositoryItem getLowestPriceItem(List prices) {
		vlogDebug("getLowestPriceItem: prices: " + prices);
		
		RepositoryItem lowestPriceItem = null;
		double lowestPrice = Double.MAX_VALUE;
		RepositoryItem price = null;
		if (prices != null) {
			for (int i = 0; i < prices.size(); ++i) {
				price = (RepositoryItem) prices.get(i);
				if (price != null) {
					Double listprice = (Double) price.getPropertyValue(this
							.getPriceListManager().getListPricePropertyName());
					vlogDebug("getLowestPriceItem: listprice: " + listprice);

					double doublePrice = 0.0D;
					if (listprice != null) {
						doublePrice = listprice.doubleValue();
					}
					vlogDebug("getLowestPriceItem: doublePrice: " + doublePrice);
					if (doublePrice < lowestPrice) {
						lowestPrice = doublePrice;
						lowestPriceItem = price;
					}
				}
			}
		}

		return lowestPriceItem;
	}

	protected void setHighLowPriceOutputParams(
			DynamoHttpServletRequest pRequest, String pLowestPriceSkuId,
			double pLowestPrice) {
			
		vlogDebug("pLowestPriceSkuId: " + pLowestPriceSkuId);
		vlogDebug("pLowestPrice: " + pLowestPrice);
		pRequest.setParameter(LOWEST_PRICE, new Double(pLowestPrice));
		pRequest.setParameter(SKU_WITH_LOWEST_PRICE, pLowestPriceSkuId);
	}
	
	public void setCatalogRepository(Repository pCatalogRepository) {
		this.mCatalogRepository = pCatalogRepository;
	}

	public Repository getCatalogRepository() {
		return this.mCatalogRepository;
	}
	
	public void setChildSKUsPropertyName(String pChildSKUsPropertyName) {
	}

	public String getChildSKUsPropertyName() {
		return this.getPricingTools().getChildSKUsPropertyName();
	}

	public void setProductItemType(String pProductItemType) {
		this.mProductItemType = pProductItemType;
	}

	public String getProductItemType() {
		return this.mProductItemType;
	}

	public PricingTools getPricingTools() {
		return this.mPricingTools;
	}

	public void setPricingTools(PricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}

	public PriceListManager getPriceListManager() {
		return this.mPriceListManager;
	}

	public void setPriceListManager(PriceListManager pPriceListManager) {
		this.mPriceListManager = pPriceListManager;
	}
	
	public CommercePropertyManager getCommercePropertyManager() {
		return this.mCommercePropertyManager;
	}

	public void setCommercePropertyManager(
			CommercePropertyManager pCommercePropertyManager) {
		this.mCommercePropertyManager = pCommercePropertyManager;
	}
}
