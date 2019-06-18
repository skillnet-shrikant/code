package mff.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author vsingh
 *
 */
public class StoreOrderItem {
	
	private String commerceItemId;
	private String storeId;
	private String skuId;
	private long quantity;
	private String productDescription;
	private String upcs;
	private String vpn;
	private String ppsMessage;
	private Map<String,String> skuAttributes = new HashMap<String,String>(); 
	private Date allocationDate;
	private boolean inPicking;
	private String planoGramInfo;
	
	
	

	public String getCommerceItemId() {
		return commerceItemId;
	}
	public void setCommerceItemId(String commerceItemId) {
		this.commerceItemId = commerceItemId;
	}
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	
	public String getUpcs() {
    return upcs;
  }
  public void setUpcs(String pUpcs) {
    upcs = pUpcs;
  }
  public String getVpn() {
    return vpn;
  }
  public void setVpn(String pVpn) {
    vpn = pVpn;
  }
  public String getPpsMessage() {
    return ppsMessage;
  }
  public void setPpsMessage(String pPpsMessage) {
    ppsMessage = pPpsMessage;
  }
  public Map<String, String> getSkuAttributes() {
    return skuAttributes;
  }
  public void setSkuAttributes(Map<String, String> pSkuAttributes) {
    skuAttributes = pSkuAttributes;
  }
  	
	public Date getAllocationDate() {
		return allocationDate;
	}
	public void setAllocationDate(Date allocationDate) {
		this.allocationDate = allocationDate;
	}
	
	public boolean isInPicking() {
    return inPicking;
  }
  public void setInPicking(boolean pInPicking) {
    inPicking = pInPicking;
  }
  @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
  
	public String getPlanoGramInfo() {
		return planoGramInfo;
	}
	public void setPlanoGramInfo(String pPlanoGramInfo) {
		planoGramInfo = pPlanoGramInfo;
	}	

}
