package com.mff.commerce.pricing;

import java.util.List;

import com.mff.commerce.catalog.MFFCatalogTools;

import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.repository.RepositoryItem;

public class MFFPricingTools extends PricingTools {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected void generatePriceListPrices(RepositoryItem pProduct, RepositoryItem pPriceList, List pPrices) {
    
    MFFCatalogTools catalogTools = (MFFCatalogTools)getCatalogTools();
    List childSKUs = (List) pProduct.getPropertyValue(getChildSKUsPropertyName());
    
    if (childSKUs != null) {
      for (int i = 0; i < childSKUs.size(); i++) {
        
        RepositoryItem sku = (RepositoryItem) childSKUs.get(i);
        if(catalogTools.isSkuActive(sku)){
          try {
            RepositoryItem price = getPriceListManager().getPrice(pPriceList, pProduct, sku);
            if (price != null) pPrices.add(price);
            continue;
          } catch (PriceListException e) {
            if (isLoggingError()) {
             logError("Cannot get price for sku - " + sku.getRepositoryId(), e);
            }
          }
        }else{
          vlogDebug("Skipped Sku {0} as is not active ",sku.getRepositoryId() );
        }
      }

    }
  }

}
