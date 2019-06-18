package com.mff.commerce.order.purchase;

import java.util.Locale;
import java.util.Map;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.OrderManager;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.util.PipelineErrorHandler;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

/**
 * Extensions for PurchaseProcessHelper
 */
public class MFFPurchaseProcessHelper extends PurchaseProcessHelper {

  private static final String LAST="last";
  private static final String FIRST="first";
	
  private MFFCatalogTools mCatalogTools;
  String mRequestHeaderName = "X-Forwarded-For";
  char mHeaderValueSeperator;
  private PromotionTools mPromotionTools;
  private String mTrueIpElement;
  private String mDummyIpAddress;
  private boolean mUseDummyIpAddress;

  public String getDummyIpAddress(){
	return mDummyIpAddress;
  }

  public void setDummyIpAddress(String pDummyIpAddress){
	mDummyIpAddress=pDummyIpAddress;
  }

  public boolean isUseDummyIpAddress(){
	return mUseDummyIpAddress;
  }

  public void setUseDummyIpAddress(boolean pUseDummyIpAddress){
	mUseDummyIpAddress=pUseDummyIpAddress;
  }
  
 
  /*protected CommerceItem createCommerceItem(AddCommerceItemInfo pItemInfo, String pCatalogKey, Order pOrder) throws CommerceException {
    CommerceItemManager lItemManager = getCommerceItemManager();
    String siteId = pItemInfo.getSiteId();
    if (StringUtils.isBlank(siteId)) {
      siteId = null;
    }
    CommerceItem ci = lItemManager.createCommerceItem(pItemInfo.getCommerceItemType(), pItemInfo.getCatalogRefId(), null,
        pItemInfo.getProductId(), null, pItemInfo.getQuantity(), pCatalogKey, null, siteId, null);

    //Retrieve minimum age of a SKU
    Integer minAge = getCatalogTools().getMinimumAge(pItemInfo.getCatalogRefId());

    Boolean isFFL = getCatalogTools().isFFLProduct(pItemInfo.getProductId());
    
    Boolean isDropShip = getCatalogTools().isDropShipProduct(pItemInfo.getProductId());
    
    //set custom properties on the commerce item
    MFFCommerceItemImpl mffCi = (MFFCommerceItemImpl) ci;
    mffCi.setMinimumAge(minAge);
    mffCi.setFFL(isFFL);
    mffCi.setDropShip(isDropShip);

    vlogDebug("Item created with ID:{0} and Minimum Age is set: {1} and isFFL set to: {2}, isDropShip {3}", ci.getId(), minAge,isFFL,isDropShip);
    CommerceItem lItem = lItemManager.addItemToOrder(pOrder, ci);
    return lItem;
  }*/
  
  public String getIPAddress(DynamoHttpServletRequest pRequest) {

		if (isLoggingDebug()) {
			logDebug("getIPAddress(): Called.");
			}

		if(isUseDummyIpAddress()){
			String ipAddress=getDummyIpAddress();
			return ipAddress;
		}
		// use X-Forwarded-For as the value from the header
		String headerValue = pRequest.getHeader(getRequestHeaderName());

		if (isLoggingDebug()) {
			logDebug("getIPAddress(); header value from request:" + headerValue);
		}
		
		String ipAddress = null;
		
		// check for null, try to use remote address if no value so far
		if (StringUtils.isEmpty(headerValue)) {
			ipAddress = pRequest.getRemoteAddr();
		} else {
			if(!StringUtils.isEmpty(getTrueIpElement())){
				
				String separator=getHeaderValueSeperator()+"";
				if(headerValue.contains(separator)){
					String[] ipAddresses=headerValue.split(separator);
					if(getTrueIpElement().trim().equalsIgnoreCase(LAST)){
						ipAddress=ipAddresses[ipAddresses.length-1];
					}
					else if(getTrueIpElement().trim().equalsIgnoreCase(FIRST)){
						ipAddress=ipAddresses[0];
					}
					else {
						int seperatorIndex = headerValue.indexOf(getHeaderValueSeperator());
						if (seperatorIndex > -1) {
							
							ipAddress = headerValue.substring(0, seperatorIndex);
						}
					}
					
				}else {
					int seperatorIndex = headerValue.indexOf(getHeaderValueSeperator());
					if (seperatorIndex > -1) {
						
						ipAddress = headerValue.substring(0, seperatorIndex);
					}
				}
			}
			else {
				int seperatorIndex = headerValue.indexOf(getHeaderValueSeperator());
				if (seperatorIndex > -1) {
					
					ipAddress = headerValue.substring(0, seperatorIndex);
				}
			}
			
			
		}
		
		if (isLoggingDebug()) {
			logDebug("getIPAddress(); IP obtained from header variable:" + ipAddress);
		}
		
		return ipAddress;
	}
  
  //Capture Device Id
  public String getDeviceId(DynamoHttpServletRequest pRequest){
	  if (isLoggingDebug()) {
			logDebug("getDeviceId(): Called.");
		}

		// get device id param
		String deviceId = pRequest.getParameter("captureRedShieldDeviceId");

		if(StringUtils.isEmpty(deviceId)){
			deviceId=pRequest.getPostParameter("captureRedShieldDeviceId");
		}
		
		if(!StringUtils.isEmpty(deviceId)){
			if (isLoggingDebug()) {
				logDebug("getDeviceId(); header value from request:" + deviceId);
			}
		    return deviceId;
		}
		return deviceId;
		
  }
  
  public void removeDiscount(MFFOrderImpl order, RepositoryItem promotion,
      MutableRepositoryItem profile, Locale locale,
      PricingModelHolder userPricingModels,
      PipelineErrorHandler errorHandler, Map extraParameters)
      throws CommerceException {

    OrderManager oManager = getOrderManager();
    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      synchronized (order) {

      getPromotionTools().removePromotion(profile, promotion, true);
      userPricingModels.initializePricingModels();

      // Reprice the order
      runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING, order,
          userPricingModels, locale, profile, extraParameters,
          errorHandler);

      // Update the order
      oManager.updateOrder(order);
      rollback = false;
      }
    } catch (Exception e) {
      throw new CommerceException("Error removing discount to order", e);
    } finally {
      try {
        td.end(rollback);
      } catch (TransactionDemarcationException tde) {
        throw new CommerceException(tde);
      }
    }
  }
	public MFFCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
  
  	public String getRequestHeaderName() {
		return mRequestHeaderName;
	}

	public void setRequestHeaderName(String pRequestHeaderName) {
		this.mRequestHeaderName = pRequestHeaderName;
	}

	public char getHeaderValueSeperator() {
		return mHeaderValueSeperator;
	}

	public void setHeaderValueSeperator(char pHeaderValueSeperator) {
		this.mHeaderValueSeperator = pHeaderValueSeperator;
	}
	
	public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  public void setPromotionTools(PromotionTools promotionTools) {
    this.mPromotionTools = promotionTools;
  }

public String getTrueIpElement() {
	return mTrueIpElement;
}

public void setTrueIpElement(String pTrueIpElement) {
	mTrueIpElement = pTrueIpElement;
}

}
