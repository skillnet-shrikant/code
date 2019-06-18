package mff.allocation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author vsingh
 *
 */
public class StoreAllocationInput {
	
	private String commerceItemId;
	private String reasonCode;
	private String trackingNumber;
	
	public String getCommerceItemId() {
		return commerceItemId;
	}
	public void setCommerceItemId(String commerceItemId) {
		this.commerceItemId = commerceItemId;
	}
	
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	

}
