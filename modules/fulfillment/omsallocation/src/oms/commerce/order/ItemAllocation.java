/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.order;

public class ItemAllocation {

	/** Order Id **/
	private String mOrderId;
	public String getOrderId() {
		return mOrderId;
	}
	public void setOrderId(String pOrderId) {
		this.mOrderId = pOrderId;
	}
	
	/** Order Number **/
/*	private String mOrderNumber;
	public String getOrderNumber() {
		return mOrderNumber;
	}
	public void setOrderNumber(String pOrderNumber) {
		this.mOrderNumber = pOrderNumber;
	}*/
	
	/** Commerce Item Id **/
	private String mCommerceItemId;
	public String getCommerceItemId() {
		return mCommerceItemId;
	}
	public void setCommerceItemId(String pCommerceItemId) {
		this.mCommerceItemId = pCommerceItemId;
	}
	
	/** Quantity **/
	private long mQuantity;
	public long getQuantity() {
		return mQuantity;
	}
	public void setQuantity(long pQuantity) {
		this.mQuantity = pQuantity;
	}
	
	/** Sku Id **/
	private String mSkuId;
	public String getSkuId() {
		return mSkuId;
	}
	public void setSkuId(String pSkuId) {
		this.mSkuId = pSkuId;
	}

	/** Fulfillment store **/
	private String mFulfillmentStore;
	public String getFulfillmentStore() {
		return mFulfillmentStore;
	}
	public void setFulfillmentStore(String pFulfillmentStore) {
		this.mFulfillmentStore = pFulfillmentStore;
	}	
	
	/** Gift Card Fulfillment **/
  private boolean mGCFulfillment;
  public boolean isGCFulfillment() {
    return mGCFulfillment;
  }
  public void setGCFulfillment(boolean pGCFulfillment) {
    mGCFulfillment = pGCFulfillment;
  }
  
  /** Force Allocate */
  private boolean mForceAllocate;
  public boolean isForceAllocate() {
    return mForceAllocate;
  }
  public void setForceAllocate(boolean pForceAllocate) {
    this.mForceAllocate = pForceAllocate;
  }
  
  /** SplitItem */
  private boolean mSplitItem;
  public boolean isSplitItem() {
    return mSplitItem;
  }
  public void setSplitItem(boolean mSplitItem) {
    this.mSplitItem = mSplitItem;
  }
  
  /** DropShipItem */
  private boolean mDropShipItem;
  public boolean isDropShipItem() {
    return mDropShipItem;
  }
  public void setDropShipItem(boolean pDropShipItem) {
    this.mDropShipItem = pDropShipItem;
  }
  
  public String toString() {
		String lNewline = System.getProperty("line.separator");
		StringBuffer lStringBuffer = new StringBuffer();
		lStringBuffer.append("-----------------------------------------------"					+ lNewline);
		lStringBuffer.append("Order Id ............................ " + getOrderId()			+ lNewline);
		lStringBuffer.append("Commerce Item Id .................... " + getCommerceItemId()		+ lNewline);
		lStringBuffer.append("Quantity ............................ " + getQuantity()			+ lNewline);
		lStringBuffer.append("Fulfillment Store ................... " + getFulfillmentStore()	+ lNewline);		
		lStringBuffer.append("Gift Card Fulfillment................ " + isGCFulfillment()		+ lNewline);
		lStringBuffer.append("ForceAllocate ....................... " + isForceAllocate()    + lNewline);
		lStringBuffer.append("SplitItem ........................... " + isSplitItem()    + lNewline);
		lStringBuffer.append("DropShipItem......................... " + isDropShipItem()    + lNewline);
		lStringBuffer.append("-----------------------------------------------");
		return lStringBuffer.toString();
	}

}