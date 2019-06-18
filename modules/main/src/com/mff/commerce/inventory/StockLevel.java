package com.mff.commerce.inventory;


public class StockLevel {
	
	/**
	 * default constructor
	 */
	public StockLevel () {
		super();
		setSkuId		("default");
		setStockLevel   (0);	
		setStoreAvailability(false);
		
		setSource 		("ATG");
	}
	
	@Override
	public String toString() {
		String lCRLF = System.getProperty("line.separator");
		String lReturn = null;
		lReturn = 	"StockLevel" 													+ lCRLF + 
					"SkuId: " 						+ mSkuId						+ lCRLF +
					"Stock Level: " 				+ mStockLevel					+ lCRLF +
					"StoreAvailability: "			+ mStoreAvailability			+ lCRLF +
					"Source: " 						+ mSource						+ lCRLF +
					"Shipped: "						+ mShipped						+ lCRLF +
					"Sold: "						+ mSold							+ lCRLF +
					"Allocated: "					+ mAllocated;
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
	 * Flag to indicate whether item is available in stores
	 */
	private boolean mStoreAvailability;
	
	public boolean isStoreAvailability() {
		return mStoreAvailability;
	}

	public void setStoreAvailability(boolean mStoreAvailability) {
		this.mStoreAvailability = mStoreAvailability;
	}

	/**
	 * The Stock level for this item
	 */
	private long mStockLevel;
	public long getStockLevel() {
		return mStockLevel;
	}
	public void setStockLevel(long pStockLevel) {
		this.mStockLevel = pStockLevel;
	}
	
	private long mSold;
	public long getSold() {
		return mSold;
	}

	public void setSold(long pSold) {
		mSold = pSold;
	}
	
	private long mAllocated;
	
	public long getAllocated() {
		return mAllocated;
	}

	public void setAllocated(long pAllocated) {
		mAllocated = pAllocated;
	}

	private long mShipped;
	public long getShipped() {
		return mShipped;
	}

	public void setShipped(long pShipped) {
		mShipped = pShipped;
	}
	
	/**
	 * Source for the inventory numbers
	 */
	private String mSource;
	public String getSource() {
		return mSource;
	}
	public void setSource(String pSource) {
		this.mSource = pSource;
	}


}	
