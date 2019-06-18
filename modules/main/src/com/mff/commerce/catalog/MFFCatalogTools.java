package com.mff.commerce.catalog;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mff.constants.MFFConstants;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;


/**
 *
 * This class implements access to Repository items and collections used for
 * Product Catalog support.
 *
 */
public class MFFCatalogTools extends CustomCatalogTools {

	private Repository mCatalogRepository;
	private String mGiftCardProductId;
	private List<String> mGiftCardProductIds;

	public Repository getCatalogRepository() {
		return mCatalogRepository;
	}

	public void setCatalogRepository(Repository pCatalogRepository) {
		mCatalogRepository = pCatalogRepository;
	}


	 /**
	  * Helper method to verify if product is available to buy online (PPS/Bopis)
	  * @param pSkuId
	  * @return
	  */
	 public boolean isProductAvailableOnline(String pProductId){

	   RepositoryItem lProductItem=null;
	    try {
	      lProductItem = findProduct(pProductId);
	    } catch (RepositoryException e) {
	      if(isLoggingError()) {
	        logError("Repository exception while determining if the product is instore only: " + pProductId, e);
	      }
	    }

	    //fulfillment method on product is set to null then the product is available to buy online as shipToHome/Bopis
	    if(lProductItem!=null){
	      Object fulfillmentMethod = lProductItem.getPropertyValue(MFFConstants.PRODUCT_FULFILLMENT_METHOD);
	      //if fulfillment method is not null & set to 5 then product is not available for buy online and ship to home/bopis
  	      if(fulfillmentMethod != null){
  	      int fulfillmentType = (int) fulfillmentMethod;
  	      if(fulfillmentType == MFFConstants.PRODUCT_INSTORE_ONLY){
  	        return false;
  	      }
	      }
	    }
	    return true;
	  }
	 
   /**
    * Helper method to verify if product is active or not
    * @param pSkuId
    * @return
    */
   public boolean isProductActive(String pProductId){

     RepositoryItem lProductItem=null;
      try {
        lProductItem = findProduct(pProductId);
      } catch (RepositoryException e) {
        if(isLoggingError()) {
          logError("Repository exception while determining if the product is inactive: " + pProductId, e);
        }
      }
      
      if(lProductItem!=null){
        Date endDate = (Date) lProductItem.getPropertyValue(MFFConstants.PRODUCT_END_DATE);
        Date startDate = (Date) lProductItem.getPropertyValue(MFFConstants.PRODUCT_START_DATE);
        Date curDate = Calendar.getInstance().getTime();
        if (startDate == null) {
          return false;
        }
        else if ( endDate == null) {
          // Return true if the current date is after start date
          return (curDate.compareTo(startDate) >= 0);
        }
        else {
          // Return true if current date is between start and end dates, inclusively
          return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
        }
      }
      return false;
    }
   
   public boolean isProductTeaserActive(String pProductId){

     RepositoryItem lProductItem=null;
      try {
        lProductItem = findProduct(pProductId);
      } catch (RepositoryException e) {
        if(isLoggingError()) {
          logError("Repository exception while determining if the product event is inactive: " + pProductId, e);
        }
      }
      
      if(lProductItem!=null){
        Date endDate = (Date) lProductItem.getPropertyValue(MFFConstants.PRODUCT_TEASER_END_DATE);
        Date startDate = (Date) lProductItem.getPropertyValue(MFFConstants.PRODUCT_TEASER_START_DATE);
        Date curDate = Calendar.getInstance().getTime();
        if (startDate == null) {
          return false;
        }
        else if ( endDate == null) {
          // Return true if the current date is after start date
          return (curDate.compareTo(startDate) >= 0);
        }
        else {
          // Return true if current date is between start and end dates, inclusively
          return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
        }
      }
      return false;
    }
   
   /**
    * Helper method to verify if product is active or not
    * @param pSkuId
    * @return
    */
   public boolean isProductActive(RepositoryItem pProduct){

      if(pProduct!=null){
        Date endDate = (Date) pProduct.getPropertyValue(MFFConstants.PRODUCT_END_DATE);
        Date startDate = (Date) pProduct.getPropertyValue(MFFConstants.PRODUCT_START_DATE);
        Date curDate = Calendar.getInstance().getTime();
        if (startDate == null) {
          return false;
        }
        else if ( endDate == null) {
          // Return true if the current date is after start date
          return (curDate.compareTo(startDate) >= 0);
        }
        else {
          // Return true if current date is between start and end dates, inclusively
          return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
        }
      }
      return false;
    }
   
   public boolean isSkuActive(RepositoryItem pSku){

     if(pSku!=null){
       Date endDate = (Date) pSku.getPropertyValue(MFFConstants.PRODUCT_END_DATE);
       Date startDate = (Date) pSku.getPropertyValue(MFFConstants.PRODUCT_START_DATE);
       Date curDate = Calendar.getInstance().getTime();
       if (startDate == null) {
         return false;
       }
       else if ( endDate == null) {
         // Return true if the current date is after start date
         return (curDate.compareTo(startDate) >= 0);
       }
       else {
         // Return true if current date is between start and end dates, inclusively
         return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
       }
     }
     return false;
   }

	 
   /**
    * Helper method to verify if product is available only for in store pickup
    * @param pSkuId
    * @return
    */
   public boolean isBopisOnlyProduct(String pProductId){

     RepositoryItem lProductItem=null;
      try {
        lProductItem = findProduct(pProductId);
      } catch (RepositoryException e) {
        if(isLoggingError()) {
          logError("Repository exception while determining if the product is instore only: " + pProductId, e);
        }
      }

      boolean bopisOnlyProd = false;

      if(lProductItem!=null){
        Object fulfillmentMethodObj = lProductItem.getPropertyValue(MFFConstants.PRODUCT_FULFILLMENT_METHOD);
        vlogDebug("In CatalogTools: isBopisOnlyProduct: ProductId:{0}",pProductId);
        if(fulfillmentMethodObj != null){
          int fulfillmentMethod = (int)fulfillmentMethodObj;
          if(fulfillmentMethod==MFFConstants.PRODUCT_BOPIS_ONLY){
            vlogDebug("In CatalogTools: isBopisOnlyProduct: ProductId:{0}, is bopis only product",pProductId);
            bopisOnlyProd=true;
          }
        }
      }
      return bopisOnlyProd;
    }

	public boolean isFFLProduct(String pProductId){

		RepositoryItem lProductItem=null;
		try {
			lProductItem = findProduct(pProductId);
		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError("Repository exception while determining if the product is FFL: " + pProductId, e);
			}
		}

		if(lProductItem!=null){
		  return isFFLProduct(lProductItem);
		}

		return false;
	}

	public boolean isFFLProduct(RepositoryItem pProduct){
    boolean isFFL=false;

    if(pProduct != null){
      Object isFFLObj = pProduct.getPropertyValue(MFFConstants.PRODUCT_FFL);
      if (isFFLObj!=null){
        isFFL = (boolean)isFFLObj;
      }
    }
    return isFFL;
	}

	/*
   * This method will return a boolean whether the product is a GidftCard or not
   */
   public boolean isGCProduct(String pProductId){
	 boolean isGCProd = false;
     if(pProductId!=null && pProductId.equalsIgnoreCase(getGiftCardProductId())){
    	 isGCProd = true;
     }
     //Below is to check the current product falls in new set of gc product or not.
     if(getGiftCardProductIds() != null && getGiftCardProductIds().contains(pProductId)){
    	 isGCProd = true;
     }
    return isGCProd;
   }

	public boolean isDropShipSku(String pSkuId){

    RepositoryItem lSkuItem = null;
    try {
      lSkuItem = findSKU(pSkuId);
    } catch (RepositoryException e) {
      if(isLoggingError()) {
        logError("Repository exception while determining if the sku is DropShip: " + pSkuId, e);
      }
    }

    if(lSkuItem != null){
      return isDropShipSku(lSkuItem);
    }

    return false;
  }

	public boolean isDropShipSku(RepositoryItem pSku){
    boolean isDropShip = false;

    if(pSku != null){
      Object isDropShipObj = pSku.getPropertyValue(MFFConstants.SKU_EDS);
      if (isDropShipObj != null){
        isDropShip = (boolean)isDropShipObj;
      }
    }

    return isDropShip;
  }

	/*
	 * This method will return the minimum age required for the item
	 * if minimum age is set on the product it will override all the SKU value
	 */
	public Integer getMinimumAge(String pSkuId){
	  RepositoryItem lProductItem = getParentProductOfSkuId(pSkuId);
    if(lProductItem!=null){
      Object minimumAgeObj = lProductItem.getPropertyValue(MFFConstants.PROPERTY_MINIMUM_AGE);
      if (minimumAgeObj!=null){
        Integer minAge = (Integer)minimumAgeObj;
        vlogDebug("Minimum Age of the skuId:{0} is : {1}",pSkuId, minAge);
        return minAge;
      }
    }
    return null;
	}

	/*
	 * This method will return the minimum age required for the item
	 * if minimum age is set on the product it will override all the SKU value
	 */
	public Integer getBvReviews(String pSkuId){
	  RepositoryItem lProductItem = getParentProductOfSkuId(pSkuId);
	    if(lProductItem!=null){
	      Object bvReviews = lProductItem.getPropertyValue(MFFConstants.PROPERTY_BV_REVIEWS);
	      if (bvReviews!=null){
	        Integer reviews = (Integer)bvReviews;
	        vlogDebug("Minimum Age of the skuId:{0} is : {1}",pSkuId, reviews);
	        return reviews;
	      }
	    }
	    return null;
	}
	
	/**
	 * Get parent product for the skuId.
	 *
	 * @param pSkuItem
	 * @return
	 */
	public RepositoryItem getParentProductOfSkuId(String pSkuId){

		try {
			RepositoryItem skuItem = findSKU(pSkuId);

			if(skuItem != null){
				return getParentProductOfSku(skuItem);
			}
		} catch (RepositoryException e) {
			vlogError(e,"Error looking up sku {0}",pSkuId);
		}

		return null;
	}

	/**
	 * Get parent product of sku . Picks the first product from the list
	 * of parentProducts.
	 *
	 * @param pSkuItem
	 * @return
	 */
	public RepositoryItem getParentProductOfSku(RepositoryItem pSkuItem){

		RepositoryItem retValue = null;
		@SuppressWarnings("unchecked")
		Set<RepositoryItem> parentProducts = (Set<RepositoryItem>)pSkuItem.getPropertyValue(MFFConstants.PARENT_PRODUCTS);
		if((parentProducts != null) && (parentProducts.size() > 0)){
			Iterator<RepositoryItem> it = parentProducts.iterator();
			retValue = it.next();
		}

		if(isLoggingDebug()){
			logDebug("getParentProduct: child = " + pSkuItem.getRepositoryId() + " parent = "
          + ((null == retValue) ? "null" : retValue.getRepositoryId()));
		}
		return retValue;
	}
	/**
	 *
	 * Get parent category of product . If the parentCategory property
	 * is not set, get the first of the parentCategories, if available.
	 *
	 * @param item
	 *          - product
	 * @return parentCategory or the first of parentCategories. If neither is
	 *         found, returns null
	 */
	public RepositoryItem getParentCategoryOfProduct(RepositoryItem pItem, String catalog){

    @SuppressWarnings("rawtypes")
    Map parentCats = (Map) pItem.getPropertyValue(MFFConstants.CATALOG_PARENT_CATEGORY);
    RepositoryItem retValue = (RepositoryItem) parentCats.get(catalog);

		if(isLoggingDebug()){
			logDebug("----> getParentCategoryOfProduct: child = " + pItem.getRepositoryId() + " parent = "
          + ((null == retValue) ? "null" : retValue.getRepositoryId()));
		}
		return retValue;
	}

	public int numberOfImages(String productId){
		try {
			RepositoryItem product=getCatalogRepository().getItem(productId,MFFConstants.PRODUCT_ITEM_DESCRIPTOR);

			Integer numImages=(Integer)product.getPropertyValue(MFFConstants.PROPERTY_PRODUCT_NUM_IMAGES);
			if(numImages==null){
				return 0;
			}
			else {
				return numImages.intValue();
			}
		}
		catch(Exception ex){
			return 0;
		}

	}

  public String getGiftCardProductId() {
    return mGiftCardProductId;
  }

  public void setGiftCardProductId(String pGiftCardProductId) {
    mGiftCardProductId = pGiftCardProductId;
  }

public List<String> getGiftCardProductIds() {
	return mGiftCardProductIds;
}

public void setGiftCardProductIds(List<String> pGiftCardProductIds) {
	mGiftCardProductIds = pGiftCardProductIds;
}

}
