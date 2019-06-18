package com.mff.listrak;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.listrak.service.constants.ListrakConstants;
import com.listrak.service.email.OrderEmailService;
import com.listrak.service.email.client.SegmentationFieldValue;
import com.listrak.service.exception.ListrakException;
import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.OrderImpl;
import atg.commerce.pricing.OrderPriceInfo;
import atg.repository.RepositoryItem;

public class MFFOrderEmailService extends OrderEmailService {

	
	@Override
	protected void populateBopisPickupDetails(OrderImpl order,List<SegmentationFieldValue> sfvList,int scenarioNumber) throws ListrakException{
		try {
			vlogInfo("OrderEmailService:populateBopisPickupDetails:Start");
			MFFOrderImpl ordrImpl=(MFFOrderImpl) order;
			Boolean isBopisOrder=ordrImpl.isBopisOrder();
			if(isBopisOrder){
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
				Date readyForPickup=ordrImpl.getSubmittedDate();
				int daysAfterPurchaseToHoldOrder=getListrakConfiguration().getDaysAfterPurchaseToHoldBopis();
				String pickDate="";
				Calendar c=Calendar.getInstance();
				switch(scenarioNumber){
				case 0:
					c.setTime(readyForPickup);
					c.add(Calendar.DATE,daysAfterPurchaseToHoldOrder);
					Date canBePickedUpByDate=c.getTime();
					pickDate=sdf.format(canBePickedUpByDate);
					break;
				case 1:
					c.setTime(readyForPickup);
					c.add(Calendar.DATE,daysAfterPurchaseToHoldOrder);
					canBePickedUpByDate=c.getTime();
					pickDate=sdf.format(canBePickedUpByDate);
					break;
				case 2:
					c.setTime(readyForPickup);
					c.add(Calendar.DATE,daysAfterPurchaseToHoldOrder);
					canBePickedUpByDate=c.getTime();
					pickDate=sdf.format(canBePickedUpByDate);
					break;
				case 3:
					readyForPickup=ordrImpl.getBopisReadyForPickupDate();
					c.setTime(readyForPickup);
					canBePickedUpByDate=c.getTime();
					pickDate=sdf.format(canBePickedUpByDate);
					break;
				default:
					c.setTime(readyForPickup);
					c.add(Calendar.DATE,daysAfterPurchaseToHoldOrder);
					canBePickedUpByDate=c.getTime();
					pickDate=sdf.format(canBePickedUpByDate);
					break;
				}
				
				
				
				if(pickDate!=null){
					SegmentationFieldValue sfv= new SegmentationFieldValue();
					sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.PICKUP_DATE));
					sfv.setValue(pickDate.trim());
					sfvList.add(sfv);
				}
				
			}
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		
		vlogInfo("OrderEmailService:populateBopisPickupDetails:End");
	}
	
	@Override
	protected void populatePriceInfoDetails(OrderImpl order,List<SegmentationFieldValue> sfvList,List<CommerceItem> pItemsShipped,Map<String,String> pTrackingNumberMap) throws ListrakException{
		vlogInfo("OrderEmailService:populatePriceInfoDetails:with multiple params:Start");
		try {
			OrderPriceInfo opi=order.getPriceInfo();
			double subTotal=0.00d;
			double taxPrice=0.00d;
			double shippingTotal=0.00d;
			
			double totalPrice=0.00d;
			
			for(CommerceItem item:pItemsShipped){
				double itemSubTotal=item.getPriceInfo().getRawTotalPrice();
				boolean onSale=item.getPriceInfo().isOnSale();
				if(onSale){
					itemSubTotal=item.getPriceInfo().getSalePrice();
				}
				
				CommerceItemImpl itemImpl=((CommerceItemImpl)item);
				Double shippingPrice=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING)) : new Double(0.00d));
				Double shippingTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_TAX)) : new Double(0.00d));;
				Double shippingCountryTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTRY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTRY_TAX)) : new Double(0.00d));
				Double shippingStateTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_STATE_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_STATE_TAX)) : new Double(0.00d));
				Double shippingDistrictTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISTRICT_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISTRICT_TAX)) : new Double(0.00d));
				Double shippingCountyTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_COUNTY_TAX)) : new Double(0.00d));
				Double shippingCityTax=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_CITY_TAX)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_CITY_TAX)) : new Double(0.00d));
				Double shippingDiscount=(itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISCOUNT)!=null ? ((Double)itemImpl.getPropertyValue(ListrakConstants.ITEM_SHIPPING_DISCOUNT)) : new Double(0.00d));
				//double shippingPriceTotal=shippingPrice.doubleValue()+shippingTax.doubleValue()+shippingCountryTax.doubleValue()+shippingStateTax.doubleValue()+shippingDistrictTax.doubleValue()+shippingCountyTax.doubleValue()+shippingCityTax.doubleValue()-shippingDiscount.doubleValue();
				double shippingPriceTotal=shippingPrice.doubleValue();
				RepositoryItem tpiItem=(itemImpl.getPropertyValue(ListrakConstants.ITEM_TAX_PRICE_INFO)!=null ? (((RepositoryItem)itemImpl.getPropertyValue(ListrakConstants.ITEM_TAX_PRICE_INFO))) : null);
				double itemTaxTotal=0.00d;
				if(tpiItem!=null){
					Double taxPricing=(tpiItem.getPropertyValue(ListrakConstants.TAX_AMOUNT)!=null ? ((Double)tpiItem.getPropertyValue(ListrakConstants.TAX_AMOUNT)) : new Double(0.00d));
					itemTaxTotal=taxPricing.doubleValue();
				}
				
				subTotal+=itemSubTotal;
				taxPrice+=itemTaxTotal;
				shippingTotal+=shippingPriceTotal;
			}
			

			
			totalPrice=subTotal+taxPrice+shippingTotal;

			
			SegmentationFieldValue subTotalFv= new SegmentationFieldValue();
			subTotalFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SUB_TOTAL));
			subTotalFv.setValue(formatDoubleValueToTwodecimals(subTotal));
			sfvList.add(subTotalFv);
			
			SegmentationFieldValue TaxFv= new SegmentationFieldValue();
			TaxFv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.TAX));
			TaxFv.setValue(formatDoubleValueToTwodecimals(taxPrice));
			sfvList.add(TaxFv);
			
			SegmentationFieldValue ShippingFV= new SegmentationFieldValue();
			ShippingFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.SHIPPING_COST));
			ShippingFV.setValue(formatDoubleValueToTwodecimals(shippingTotal));
			sfvList.add(ShippingFV);
			
			SegmentationFieldValue TotalFV= new SegmentationFieldValue();
			TotalFV.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.GRAND_TOTAL));
			TotalFV.setValue(formatDoubleValueToTwodecimals(totalPrice));
			sfvList.add(TotalFV);
			
			
		}
		catch(Exception ex){
			throw new ListrakException(ex);
		}
		
		vlogInfo("OrderEmailService:populatePriceInfoDetails:with multiple params:End");
	}
	
	@Override
	protected void populateTrackingNumbers(OrderImpl order,List<SegmentationFieldValue> sfvList,List<CommerceItem> pItemsShipped,Map<String,String> pTrackingNumberMap) throws ListrakException{
		vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params: Start");
		try {
			
			
			
			StringBuilder fedexTrackingLinks=new StringBuilder();
			
				String prevTrackingNumber="";
				String currentTrackingNumber="";
				if(pTrackingNumberMap.size()!=0){
					for(String key:pTrackingNumberMap.keySet()){
						String trakNo=pTrackingNumberMap.get(key);
						vlogDebug("Get tracking key: "+key+";Get tracking number:"+trakNo);
						if(!prevTrackingNumber.trim().equalsIgnoreCase(trakNo)){
							currentTrackingNumber=trakNo;
							if(!currentTrackingNumber.isEmpty()){
								StringBuilder sb=new StringBuilder();
								Object[] msgArgs = new Object[2];
								msgArgs[0]=currentTrackingNumber;
								msgArgs[1]=currentTrackingNumber;
								String fedexLink=ListrakConstants.getResources().getString(ListrakConstants.FEDEX_TRACKING_LINK);
								String standardTemplate=MessageFormat.format(fedexLink, msgArgs);
								sb.append(standardTemplate);
								fedexTrackingLinks.append(sb.toString());
							}
							prevTrackingNumber=currentTrackingNumber;
						}
					}
				}
				else {
					
				}
				vlogDebug("Fedex Tracking links "+fedexTrackingLinks.toString());
				if(!(fedexTrackingLinks.toString().isEmpty())){
					SegmentationFieldValue sfv= new SegmentationFieldValue();
					sfv.setSegmentationFieldId(getFiledIdToNameMap().get(ListrakConstants.TRACKING_NUMBER));
					sfv.setValue(fedexTrackingLinks.toString());
					sfvList.add(sfv);
				}
				
			
		}
		catch(Exception ex){
			vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params:Exception occurred ");
			throw new ListrakException(ex);
			
		}
		vlogInfo("OrderEmailService:populateTrackingNumbers:with multiple params: End");
		
	}	

}
