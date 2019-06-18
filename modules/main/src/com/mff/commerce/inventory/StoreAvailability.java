package com.mff.commerce.inventory;

public class StoreAvailability {
	
	/**
	 * default constructor
	 */
	public StoreAvailability () {
		super();
		setSkuId		("default");
		setStoreNo 		("00000");
		setAvailabilityStatus ("Available");
		setStoreStockLevel(0);
	}
	
	@Override
	public String toString() {
		String lCRLF = System.getProperty("line.separator");
		String lReturn = null;
		lReturn = 	"StoreAvailability" 										+ lCRLF + 
					"SkuId: "	 					+ mSkuId					+ lCRLF +
					"Store No: " 					+ mStoreNo 					+ lCRLF +
					"Available Status: " 			+ mAvailabilityStatus		+ lCRLF +
					"Store Stock Level: "			+ mStoreStockLevel;
		return lReturn;
	}
	
	// *******************************************************************
	//                            Getter/Setters 
	// *******************************************************************
	
	/**
	 * SkuId for this stock result
	 */
	private String mSkuId;		
	public String getSkuId() {
		return mSkuId;
	}
	public void setSkuId(String pSkuId) {
		this.mSkuId = pSkuId;
	}
	
	/**
	 * The Store number
	 */
	private String mStoreNo;
	public String getStoreNo() {
		return mStoreNo;
	}
	public void setStoreNo(String pStoreNo) {
		this.mStoreNo = pStoreNo;
	}
	
	/**
	 * The Availability Status
	 */
	private String mAvailabilityStatus;
	public String getAvailabilityStatus() {
		return mAvailabilityStatus;
	}
	public void setAvailabilityStatus(String pAvailabilityStatus) {
		this.mAvailabilityStatus = pAvailabilityStatus;
	}
	
	private long mStoreStockLevel;
	public long getStoreStockLevel() {
		return mStoreStockLevel;
	}

	public void setStoreStockLevel(long pStoreStockLevel) {
		mStoreStockLevel = pStoreStockLevel;
	}
	
}