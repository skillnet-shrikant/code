package oms.commerce.order;

import org.apache.commons.lang3.builder.ToStringBuilder;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.nucleus.GenericService;

/**
 * The class holds information related to pro ration of ShippingTax. It contains reference to
 * DiscountDetails,split line items created from the associated Discount Details
 * and pro rated shipping for the discount detail. 
 * 
 * @author vsingh
 *
 */
public class ShippingTaxGroupHolder extends GenericService{
	
	private CommerceItem commerceItem;
	private double proratedShipping;
	private DetailedItemPriceInfo detailPriceInfo;
	

	public CommerceItem getCommerceItem() {
    return commerceItem;
  }

  public void setCommerceItem(CommerceItem pCommerceItem) {
    commerceItem = pCommerceItem;
  }

  public double getProratedShipping() {
		return proratedShipping;
	}

	public void setProratedShipping(double proratedShipping) {
		this.proratedShipping = proratedShipping;
	}

	public DetailedItemPriceInfo getDetailPriceInfo() {
		return detailPriceInfo;
	}

	public void setDetailPriceInfo(DetailedItemPriceInfo detailPriceInfo) {
		this.detailPriceInfo = detailPriceInfo;
	}
	
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
