/**
 * 
 */
package com.mff.repository.property;

import java.util.Collection;
import java.util.Map;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

import com.mff.constants.MFFConstants;

/**
 * @author Boris
 *
 */
public class OrderShippingTaxPropertyDescriptor extends RepositoryPropertyDescriptor{

  private static final long serialVersionUID = 6139492421142961121L;

  @SuppressWarnings("unchecked")
  public Object getPropertyValue(RepositoryItemImpl pTaxPriceInfo, Object pCachedPropertyValue)
  {
    Double retValue = (Double)pCachedPropertyValue;

    if (null == retValue)
    {
      double shippingTax = 0.0;
      Map<Object, RepositoryItem> shippingTaxPriceInfos = 
        (Map<Object, RepositoryItem>)pTaxPriceInfo.getPropertyValue(MFFConstants.PROPERTY_SHIPPING_ITEMS_TAX_PRICES_INFOS);
      if ((shippingTaxPriceInfos != null) && (shippingTaxPriceInfos.size() > 0))
      {
        Collection<RepositoryItem> c = shippingTaxPriceInfos.values();
        for (RepositoryItem el: c)
        {
          if (el != null)
          {
            shippingTax += (Double)el.getPropertyValue(MFFConstants.AMOUNT);
          }
        }
      }
      retValue = new Double(shippingTax);
      pTaxPriceInfo.setPropertyValueInCache(this, retValue);
    }

    return retValue;
  }
}
