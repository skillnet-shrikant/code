package com.googleadwords.scheduler;


import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.googleadwords.beans.Channel;
import com.googleadwords.beans.Item;
import com.googleadwords.beans.Rss;
import com.googleadwords.beans.Shipping;

import atg.commerce.catalog.CatalogTools;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import org.dom4j.CDATA;
import org.dom4j.DocumentHelper;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;



/**
 * Class provides methods for handling import of CSV feed files
 *  - Optionally pull down feeds from the FTP server
 *  - Process the feeds by invoking a DB stored proc
 *  
 *  Currenly this is being used to process price and inventory feeds
 *  
 * @author KnowledgePath
 *
 */
public class GoogleFeedCreator extends GenericService {
	
	
  /* Return Codes */
	public static final int		SUCCESS				= 0;
	public static final int		WARN				= 1;
	public static final int		ERROR				= 2;
	
	protected static final String FOLDER_SEPERATOR = "/";
	
	private static final String CATEGORY_ID="CATEGORY_ID";
	private static final String GOOGLE_CATEGORY_ID="GOOGLE_CATEGORY_ID";
	private static final String GOOGLE_CATEGORY_NAME="GOOGLE_CATEGORY_NAME";
	private static final String PRODUCT_ID="PRODUCT_ID";
	private static final String BRAND="BRAND";
	private static final String PRODUCT_DISPLAY_NAME="PRODUCT_DISPLAY_NAME";
	private static final String PRODUCT_SELLING_POINTS="PRODUCT_SELLING_POINTS";
	private static final String PRODUCT_NUM_IMAGES="PRODUCT_NUM_IMAGES";
	private static final String SKU_ID="SKU_ID";
	private static final String UPCS="UPCS";
	private static final String ON_SALE="ON_SALE";
	private static final String CLEARANCE="CLEARANCE";
	private static final String STOCK_LEVEL="STOCK_LEVEL";
	private static final String LIST_PRICE="LIST_PRICE";
	private static final String PROMO_ID="PROMO_ID";
	private static final String IS_HIDE_PRICE="IS_HIDE_PRICE";
	private static final String PRODUCT_DESCRIPTION="PRODUCT_DESCRIPTION";
	
	private static final String IN_STOCK="In Stock";
	private static final String OUT_OF_STOCK="Out of Stock";
	private static final String CONDITION="new";
	private static final String USD="USD";
	
	private static final String COLOR="COLOR";
	private static final String SKU_SIZE="SKU_SIZE";
	private static final String AGE_GROUP="AGE_GROUP";
	private static final String GENDER="GENDER";
	
	
	
	
	
	
	
	private static final String LTL="LTL";
	private static final String SKU_LENGTH="SKU_LENGTH";
	private static final String GIRTH="GIRTH";
	private static final String WIDTH="WIDTH";
	private static final String OVERSIZED="OVERSIZED";
	private static final String WEIGHT="WEIGHT";
	private static final String RESTRICT_AIR="RESTRICT_AIR";
	private static final String LTL_FUEL_SURCHARGE="LTL_FUEL_SURCHARGE";
	private static final String EDS="EDS";
	private static final String LTL_LIFT_GATE="LTL_LIFT_GATE";
	private static final String FREIGHT_CLASS="FREIGHT_CLASS";
	private static final String LTL_RES_DELIVERY="LTL_RES_DELIVERY";
	private static final String SKU_DEPTH="SKU_DEPTH";
	private static final String LONG_LIGHT="LONG_LIGHT";
	private static final String SHIPPING_SURCHARGE_QNTY_RANGE="SHIPPING_SURCHARGE_QNTY_RANGE";
	private static final String FREE_SHIPPING="FREE_SHIPPING";
	private static final String IS_FFL="IS_FFL";
	private static final String MINIMUM_AGE="MINIMUM_AGE";
	
	private static final int LTL_FLAG_INDEX=0;
	private static final int FREESHIPPING_FLAG_INDEX=1;
	private static final int OVERSIZED_FLAG_INDEX=2;
	private static final int RESTRICTEDAIR_FLAG_INDEX=3;
	private static final int FFL_FLAG_INDEX=4;
	private static final int WEIGHT_FLAG_INDEX=5;
	private static final int MINIMUMAGE_FLAG_INDEX=6;
	private static final int ADDITIONAL_HANDLING_FLAG_INDEX=7;
	
	private static final String SIGNATURE_REQUIRED="SignatureRequired";
	private static final String OVER_SIZED="Oversized";
	private static final String ADDITIONAL_HANDLING="AdditionalHandling";
	private static final String SATUARDAY="Satuarday";
	

	
	//Title for xml Channel attribute
	
	private String mChannelTitle;
	
	private String mChannelLink;
	
	private String mChannelDescription;
	
	private String mStandardShipping;
	
	private String mStandardShippingPrice;
	
	private String mLtlShipping;
	
	private String mLtlShippingPrice;
	
	private String mCountry;
	
	// Collect is hide
	private boolean mCollectHideData;
	
	// Collect is hide in new file
	private boolean mCollectHideDataInNew;
	
	// Local dir where the files from ftp should be downloaded to
	private String localDir;
	
	// The SFTP dir may contain other files. Ideally it would not
	// so we only look for files with certain prefixes
	private String fileName;
	
	// The main stored proc that processes the import
	private String storedProcName;
	
	// CATFEED datasource component
	private DataSource feedDataSource;
	
	// Connection to schema
	protected Connection conn;
		
	// Query timeout
	int queryTimeout;

	// Job return code
	int	jobReturnCode;
	
	private boolean mEnable;
	
	private String priceRange1;
	private String priceRange2;
	private String priceRange3;
	private String priceRange4;
	private String priceRange5;
	
	
	private String	runServer;

	private String onSaleTableName;
	
	private String regularTableName;
	
	private String taskName;
	
	private String mFeedVersion;
	
	private String mOnSaleItemsQueryNoHide;
	
	private String mRegularItemsQueryNoHide;
	
	private String mOnSaleItemsQueryHide;
	
	private String mRegularItemsQueryHide;
	
	private boolean mUSeCdata;
	private boolean mUseCdataLib;
	
	private String exceptionMsg;
	private String processedCountOnSaleHideQuery;
	private String processedCountRegularHideQuery;
	private String processedCountOnSaleNoHide;
	private String processedCountRegularNoHide;
	
	private boolean useProductType;
	
	private Pattern pattern;
	private String urlPrefix;
	
	private CatalogTools catalogTools;
	
	private String imageUrlPrefix;
	
	private String imageUrlParentFolder;
	
	private boolean mUseSizeAgeGenderColorForAll;
	
	private String mDefaultSizeValue;
	
	private boolean mUseShipping;
	
	private String[] mStandardShippingRanges;
	
	private double mDefaultStandardShippingPrice;
	
	private String[] mLtlShippingRanges;
	
	private String[] mSecondDayShippingRanges;
	
	private double mDefaultLtlShippingPrice;
	
	private Map<String,String> mUpCharges = new HashMap<String,String>();
	
	private boolean mUseSkuLevelFlagsForShippingCalculation;
	
	private String mOverSizedShipping;
	
	private String mAdditionalHandlingShipping;
	
	private boolean mUseGoogleCategory;
	
	private boolean mCollectData;
	
	private boolean mUseOverSizedFlag;
	
	private boolean mUsePriceUnitAsPrefix;
	
	int oversizedSingleSideMax = 0;
	int oversizedTwoSideMax = 0;
	int oversizedWeightMax = 0;
	boolean factorQtyForOversizedActualWeight;
	boolean factorQtyForOversizedDimensionWeight;
	int dimensionWeightDivisor;
	
	int addlnHandlingMaxLength;
	int addlnHandlingMaxHeight;
	int addlnHandlingMaxWidth;
	int addlnHandlingMaxWeight;	
	
	private boolean mUseStorePickupTemplate;
	private String mStorePickupTemplatePrefix;

	private boolean includeDimWeightForAddlHandling;
	
	public boolean isUsePriceUnitAsPrefix() {
		return mUsePriceUnitAsPrefix;
	}

	public void setUsePriceUnitAsPrefix(boolean pUsePriceUnitAsPrefix) {
		mUsePriceUnitAsPrefix = pUsePriceUnitAsPrefix;
	}

	public boolean isIncludeDimWeightForAddlHandling() {
		return includeDimWeightForAddlHandling;
	}

	public void setIncludeDimWeightForAddlHandling(boolean pIncludeDimWeightForAddlHandling) {
		includeDimWeightForAddlHandling = pIncludeDimWeightForAddlHandling;
	}

	public boolean isUseOverSizedFlag() {
		return mUseOverSizedFlag;
	}

	public void setUseOverSizedFlag(boolean pUseOverSizedFlag) {
		mUseOverSizedFlag = pUseOverSizedFlag;
	}

	public String getStorePickupTemplatePrefix(){
		return mStorePickupTemplatePrefix;
	}
	
	public void setStorePickupTemplatePrefix(String pStorePickupTemplatePrefix){
		mStorePickupTemplatePrefix=pStorePickupTemplatePrefix;
	}

	public boolean isUseStorePickupTemplate(){
		return mUseStorePickupTemplate;
	}
	
	public void setUseStorePickupTemplate(boolean pUseStorePickupTemplate){
		mUseStorePickupTemplate=pUseStorePickupTemplate;
	}
	
	public int getAddlnHandlingMaxLength() {
		return addlnHandlingMaxLength;
	}
	public void setAddlnHandlingMaxLength(int pAddlnHandlingMaxLength) {
		addlnHandlingMaxLength = pAddlnHandlingMaxLength;
	}
	public int getAddlnHandlingMaxHeight() {
		return addlnHandlingMaxHeight;
	}
	public void setAddlnHandlingMaxHeight(int pAddlnHandlingMaxHeight) {
		addlnHandlingMaxHeight = pAddlnHandlingMaxHeight;
	}
	public int getAddlnHandlingMaxWidth() {
		return addlnHandlingMaxWidth;
	}
	public void setAddlnHandlingMaxWidth(int pAddlnHandlingMaxWidth) {
		addlnHandlingMaxWidth = pAddlnHandlingMaxWidth;
	}
	public int getAddlnHandlingMaxWeight() {
		return addlnHandlingMaxWeight;
	}
	public void setAddlnHandlingMaxWeight(int pAddlnHandlingMaxWeight) {
		addlnHandlingMaxWeight = pAddlnHandlingMaxWeight;
	}
	
	public int getDimensionWeightDivisor() {
		return dimensionWeightDivisor;
	}
	public void setDimensionWeightDivisor(int pDimensionWeightDivisor) {
		dimensionWeightDivisor = pDimensionWeightDivisor;
	}


	public int getOversizedSingleSideMax() {
		return oversizedSingleSideMax;
	}
	public void setOversizedSingleSideMax(int pOversizedSingleSideMax) {
		oversizedSingleSideMax = pOversizedSingleSideMax;
	}
	public int getOversizedTwoSideMax() {
		return oversizedTwoSideMax;
	}
	public void setOversizedTwoSideMax(int pOversizedTwoSideMax) {
		oversizedTwoSideMax = pOversizedTwoSideMax;
	}
	public int getOversizedWeightMax() {
		return oversizedWeightMax;
	}
	public void setOversizedWeightMax(int pOversizedWeightMax) {
		oversizedWeightMax = pOversizedWeightMax;
	}	
	
	public boolean isCollectData() {
		return mCollectData;
	}

	public void setCollectData(boolean pCollectData) {
		mCollectData = pCollectData;
	}

	public boolean isUseGoogleCategory() {
		return mUseGoogleCategory;
	}

	public void setUseGoogleCategory(boolean pUseGoogleCategory) {
		mUseGoogleCategory = pUseGoogleCategory;
	}

	public String getOverSizedShipping() {
		return mOverSizedShipping;
	}

	public void setOverSizedShipping(String pOverSizedShipping) {
		mOverSizedShipping = pOverSizedShipping;
	}

	public String getAdditionalHandlingShipping() {
		return mAdditionalHandlingShipping;
	}

	public void setAdditionalHandlingShipping(String pAdditionalHandlingShipping) {
		mAdditionalHandlingShipping = pAdditionalHandlingShipping;
	}

	public boolean isUseSkuLevelFlagsForShippingCalculation() {
		return mUseSkuLevelFlagsForShippingCalculation;
	}

	public void setUseSkuLevelFlagsForShippingCalculation(boolean pUseSkuLevelFlagsForShippingCalculation) {
		mUseSkuLevelFlagsForShippingCalculation = pUseSkuLevelFlagsForShippingCalculation;
	}

	public Map<String,String> getUpCharges() {
		return mUpCharges;
	}

	/**
	 * @param pUpCharges the upCharges to set
	 */
	public void setUpCharges(Map<String,String> pUpCharges) {
		mUpCharges = pUpCharges;
	}
	
	public String[] getSecondDayShippingRanges() {
		return mSecondDayShippingRanges;
	}

	public void setSecondDayShippingRanges(String[] pSecondDayShippingRanges) {
		mSecondDayShippingRanges = pSecondDayShippingRanges;
	}
	
	public double getDefaultLtlShippingPrice() {
		return mDefaultLtlShippingPrice;
	}

	public void setDefaultLtlShippingPrice(double pDefaultLtlShippingPrice) {
		mDefaultLtlShippingPrice = pDefaultLtlShippingPrice;
	}

	public String[] getLtlShippingRanges() {
		return mLtlShippingRanges;
	}

	public void setLtlShippingRanges(String[] pLtlShippingRanges) {
		mLtlShippingRanges = pLtlShippingRanges;
	}

	public void setDefaultStandardShippingPrice(double pDefaultStandardShippingPrice){
		mDefaultStandardShippingPrice=pDefaultStandardShippingPrice;
	}
  
	public double getDefaultStandardShippingPrice(){
		return  mDefaultStandardShippingPrice;
	}
	
	public String[] getStandardShippingRanges(){
		return mStandardShippingRanges; 
	}
	
	public void setStandardShippingRanges(String pStandardShippingRanges[]){
		mStandardShippingRanges=pStandardShippingRanges;
	}
	
	public boolean isUseShipping() {
		return mUseShipping;
	}

	public void setUseShipping(boolean pUseShipping) {
		mUseShipping = pUseShipping;
	}

	public String getDefaultSizeValue() {
		return mDefaultSizeValue;
	}

	public void setDefaultSizeValue(String pDefaultSizeValue) {
		mDefaultSizeValue = pDefaultSizeValue;
	}

	public boolean isUseSizeAgeGenderColorForAll() {
		return mUseSizeAgeGenderColorForAll;
	}

	public void setUseSizeAgeGenderColorForAll(boolean pUseSizeAgeGenderColorForAll) {
		mUseSizeAgeGenderColorForAll = pUseSizeAgeGenderColorForAll;
	}

	
	  public String getImageUrlParentFolder() {
			return imageUrlParentFolder;
		}

		public void setImageUrlParentFolder(String pImageUrlParentFolder) {
			imageUrlParentFolder = pImageUrlParentFolder;
		}
	
		public void setImageUrlPrefix(String pImageUrlPrefix) {
			imageUrlPrefix = pImageUrlPrefix;
		}
		
		public String getImageUrlPrefix(){
			return imageUrlPrefix;
		}
		
		public CatalogTools getCatalogTools() {
			return catalogTools;
		}
	
		public void setCatalogTools(CatalogTools pCatalogTools) {
			catalogTools = pCatalogTools;
		}
	
		public boolean isUseProductType() {
			return useProductType;
		}
	
		public void setUseProductType(boolean pUseProductType) {
			useProductType = pUseProductType;
		}
	
		public String getProcessedCountRegularNoHide() {
			return processedCountRegularNoHide;
		}
	
		public void setProcessedCountRegularNoHide(String pProcessedCountRegularNoHide) {
			processedCountRegularNoHide = pProcessedCountRegularNoHide;
		}
	
		public String getProcessedCountOnSaleHideQuery() {
			return processedCountOnSaleHideQuery;
		}
	
		public void setProcessedCountOnSaleHideQuery(String pProcessedCountOnSaleHideQuery) {
			processedCountOnSaleHideQuery = pProcessedCountOnSaleHideQuery;
		}
	
		public String getProcessedCountRegularHideQuery() {
			return processedCountRegularHideQuery;
		}
	
		public void setProcessedCountRegularHideQuery(String pProcessedCountRegularHideQuery) {
			processedCountRegularHideQuery = pProcessedCountRegularHideQuery;
		}
	
		public String getProcessedCountOnSaleNoHide() {
			return processedCountOnSaleNoHide;
		}
	
		public void setProcessedCountOnSaleNoHide(String pProcessedCountOnSaleNoHide) {
			processedCountOnSaleNoHide = pProcessedCountOnSaleNoHide;
		}
	
		public boolean isEnable() {
			return mEnable;
		}
	
		public void setEnable(boolean pEnable) {
			mEnable = pEnable;
		}
	
		
		public String getPriceRange1() {
			return priceRange1;
		}
	
		public void setPriceRange1(String pPriceRange1) {
			priceRange1 = pPriceRange1;
		}
	
		public String getPriceRange2() {
			return priceRange2;
		}
	
		public void setPriceRange2(String pPriceRange2) {
			priceRange2 = pPriceRange2;
		}
	
		public String getPriceRange3() {
			return priceRange3;
		}
	
		public void setPriceRange3(String pPriceRange3) {
			priceRange3 = pPriceRange3;
		}
	
		public String getPriceRange4() {
			return priceRange4;
		}
	
		public void setPriceRange4(String pPriceRange4) {
			priceRange4 = pPriceRange4;
		}
	
		public boolean isUseCdata() {
			return mUSeCdata;
		}
	
		public void setUseCdata(boolean pUSeCdata) {
			mUSeCdata = pUSeCdata;
		}
	
		public boolean isUseCdataLib() {
			return mUseCdataLib;
		}
	
		public void setUseCdataLib(boolean pUseCdataLib) {
			mUseCdataLib = pUseCdataLib;
		}
	
		public String getPriceRange5() {
			return priceRange5;
		}
	
		public void setPriceRange5(String pPriceRange5) {
			priceRange5 = pPriceRange5;
		}
		
		public String getOnSaleItemsQueryHide() {
			return mOnSaleItemsQueryHide;
		}
	
		public void setOnSaleItemsQueryHide(String pOnSaleItemsQueryHide) {
			mOnSaleItemsQueryHide = pOnSaleItemsQueryHide;
		}
	
		public String getRegularItemsQueryHide() {
			return mRegularItemsQueryHide;
		}
	
		public void setRegularItemsQueryHide(String pRegularItemsQueryHide) {
			mRegularItemsQueryHide = pRegularItemsQueryHide;
		}
	
		public String getOnSaleItemsQueryNoHide() {
			return mOnSaleItemsQueryNoHide;
		}
	
		public void setOnSaleItemsQueryNoHide(String pOnSaleItemsQueryNoHide) {
			mOnSaleItemsQueryNoHide = pOnSaleItemsQueryNoHide;
		}
	
		public String getRegularItemsQueryNoHide() {
			return mRegularItemsQueryNoHide;
		}
	
		public void setRegularItemsQueryNoHide(String pRegularItemsQueryNoHide) {
			mRegularItemsQueryNoHide = pRegularItemsQueryNoHide;
		}
	
		public String getFeedVersion() {
			return mFeedVersion;
		}
	
		public void setFeedVersion(String pFeedVersion) {
			mFeedVersion = pFeedVersion;
		}
	
		public String getStandardShipping() {
			return mStandardShipping;
		}
	
		public void setStandardShipping(String pStandardShipping) {
			mStandardShipping = pStandardShipping;
		}
	
		public String getStandardShippingPrice() {
			return mStandardShippingPrice;
		}
	
		public void setStandardShippingPrice(String pStandardShippingPrice) {
			mStandardShippingPrice = pStandardShippingPrice;
		}
	
		public String getLtlShipping() {
			return mLtlShipping;
		}
	
		public void setLtlShipping(String pLtlShipping) {
			mLtlShipping = pLtlShipping;
		}
	
		public String getLtlShippingPrice() {
			return mLtlShippingPrice;
		}
	
		public void setLtlShippingPrice(String pLtlShippingPrice) {
			mLtlShippingPrice = pLtlShippingPrice;
		}
	
		public String getCountry() {
			return mCountry;
		}
	
		public void setCountry(String pCountry) {
			mCountry = pCountry;
		}
	
		
		public String getChannelTitle() {
			return mChannelTitle;
		}
	
		public void setChannelTitle(String pChannelTitle) {
			mChannelTitle = pChannelTitle;
		}
	
		public String getChannelLink() {
			return mChannelLink;
		}
	
		public void setChannelLink(String pChannelLink) {
			mChannelLink = pChannelLink;
		}
	
		public String getChannelDescription() {
			return mChannelDescription;
		}
	
		public void setChannelDescription(String pChannelDescription) {
			mChannelDescription = pChannelDescription;
		}
	
	
	
		public boolean isCollectHideDataInNew() {
			return mCollectHideDataInNew;
		}
	
		public void setCollectHideDataInNew(boolean pCollectHideDataInNew) {
			mCollectHideDataInNew = pCollectHideDataInNew;
		}
		
	
		public void setCollectHideData(boolean pCollectHideData){
			mCollectHideData=pCollectHideData;
		}
		
		public boolean isCollectHideData(){
			return mCollectHideData;
		}
		
		public int getQueryTimeout() {
			return queryTimeout;
		}
	
		public void setQueryTimeout(int pQueryTimeout) {
			queryTimeout = pQueryTimeout;
		}
	
		public String getTaskName() {
			return taskName;
		}
	
		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}
	
		public String getRegularTableName() {
			return regularTableName;
		}
	
		public void setRegularTableName(String tableName) {
			this.regularTableName = tableName;
		}
		
		public String getOnSaleTableName() {
			return onSaleTableName;
		}
	
		public void setOnSaleTableName(String tableName) {
			this.onSaleTableName = tableName;
		}
		
		public String getRunServer() {
			return runServer;
		}
		public void setRunServer(String runServer) {
			this.runServer = runServer;
		}
		
		public int getJobReturnCode() {
			return jobReturnCode;
		}
		
		public void setJobReturnCode(int jobReturnCode) {
			logInfo("Setting Return Code [" + jobReturnCode + "]");
			this.jobReturnCode = jobReturnCode;
		}
			
		public DataSource getFeedDataSource() {
			return feedDataSource;
		}
	
		public void setFeedDataSource(DataSource feedDataSource) {
			this.feedDataSource = feedDataSource;
		}
	
		public String getStoredProcName() {
			return storedProcName;
		}
	
		public void setStoredProcName(String storedProcName) {
			this.storedProcName = storedProcName;
		}
	
		public String getFileName() {
			return fileName;
		}
	
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	
		public String getLocalDir() {
			return localDir;
		}
	
		public void setLocalDir(String localDir) {
			this.localDir = localDir;
		}
		
		public String getUrlPrefix() {
			return urlPrefix;
		}
	
		public void setUrlPrefix(String urlPrefix) {
			this.urlPrefix = urlPrefix;
		}
	
		public Pattern getPattern() { return pattern; }
	
		public void setPattern(Pattern pPattern) {
			pattern = pPattern;
		}
	
		
		@Override
		public void doStartService() throws ServiceException {
			super.doStartService();
		}
		
		/*
		 * Constructs callableStatement for creating feeds
		 */
		public CallableStatement getCallableStmt() throws SQLException {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " creating callable statement - "
						+ "proc name is " + getStoredProcName());
			}
			
			CallableStatement cs;
			cs = conn.prepareCall(getStoredProcName());
			return cs;
		}


		/*
		 * (non-Javadoc)
		 *
		 * 
		 * Primary method invoked from TaskScheduler. 
		 * This inturn invokes other methods in create creation
		 * 
		 */
		
		public void doTask() {
			vlogInfo("Start: GoogleFeedCreator: doTask()");
			if(isEnable()){
				vlogInfo("Google Feed Creator task is enabled");
				performtask();
			}
			else {
				vlogInfo("Google Feed Creator task is not enabled");
			}
			vlogInfo("End: GoogleFeedCreator: doTask()");
	
		}
		

	
	  private void performtask() {
		  setJobReturnCode(SUCCESS);
		  vlogInfo("Start: GoogleFeedCreator: performtask()");
		  //Invoke the configured stored proc
		  if(isCollectData()){
			  collectData(getStoredProcName());
		  }
		  // If success create onsale and regular product feed
		  if(getJobReturnCode() ==SUCCESS) {
			  createXmlFeed(getLocalDir(),getFileName(),isCollectHideData(),isCollectHideDataInNew());
		  }
		  vlogInfo("End: GoogleFeedCreator: performtask()");
	  }
		
		public void createXmlFeed(String dir,String fileName,boolean hidePrice,boolean separateFile){
			vlogInfo("Start: GoogleFeedCreator: createXmlFeed()");
			try {
				String filePath=dir+FOLDER_SEPERATOR+fileName;
				Rss rssFeed=new Rss();
				Channel ch=new Channel();
				ch.setTitle(getChannelTitle());
				ch.setLink(getChannelLink());
				ch.setDescription(getChannelDescription());
				rssFeed.setVersion(Double.parseDouble(getFeedVersion()));
				if(hidePrice){
					if(separateFile){
						filePath=dir+"/hide_"+fileName;
					}
					ch=createXmlItemsForOnSale(ch,true);
					ch=createXmlItemsForRegular(ch,true);
				}
				else {
					ch=createXmlItemsForOnSale(ch,false);
					ch=createXmlItemsForRegular(ch,false);
				}
				
				rssFeed.setChannel(ch);
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
				file=new File(filePath);
				JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				marshaller.setProperty("com.sun.xml.internal.bind.characterEscapeHandler",
		                new CharacterEscapeHandler() {
		                   
							@Override
							public void escape(char[] pAc, int pI, int pJ, boolean pFlag, Writer pWriter)
									throws IOException {
								pWriter.write(pAc, pI, pJ);
							}
		                });
				
				
				marshaller.marshal( rssFeed, file);
			}
			catch(Exception ex){
				if(isLoggingError())
					logError(ex);
				setJobReturnCode(ERROR);
			}
			vlogInfo("End: GoogleFeedCreator: createXmlFeed()");
			
		}
		
		public Channel createXmlItemsForOnSale(Channel ch,boolean hideData) throws SQLException{
			vlogInfo("Start: GoogleFeedCreator: createXmlItemsForOnSale()");
			if(hideData){
				ch=runQuery(getOnSaleItemsQueryHide(),ch,true);
			}
			else {
				ch=runQuery(getOnSaleItemsQueryNoHide(),ch,true);
			}
			vlogInfo("End: GoogleFeedCreator: createXmlItemsForOnSale()");
			return ch;
		}
		
		public Channel createXmlItemsForRegular(Channel ch,boolean hideData) throws SQLException{
			vlogInfo("Start: GoogleFeedCreator: createXmlItemsForRegular()");
			if(hideData){
				ch=runQuery(getRegularItemsQueryHide(),ch,false);
			}
			else {
				ch=runQuery(getRegularItemsQueryNoHide(),ch,false);
			}
			vlogInfo("End: GoogleFeedCreator: createXmlItemsForRegular()");
			return ch;
		}
		
		public Channel runQuery(String query,Channel ch,boolean isSale) throws SQLException{
			vlogInfo("Start: GoogleFeedCreator: runQuery()");
			Connection conn = feedDataSource.getConnection();
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					String productCategoryId=rs.getString(CATEGORY_ID);
					String google_category_name=rs.getString(GOOGLE_CATEGORY_NAME);
					String product_id=rs.getString(PRODUCT_ID);
					String brand=rs.getString(BRAND);
					String title=rs.getString(PRODUCT_DISPLAY_NAME);
					String description=rs.getString(PRODUCT_SELLING_POINTS);
					int imageNum=rs.getInt(PRODUCT_NUM_IMAGES);
					String sku_id=rs.getString(SKU_ID);
					String upcs=rs.getString(UPCS);
					int stockLevel =rs.getInt(STOCK_LEVEL);
					double price =rs.getDouble(LIST_PRICE);
					int clearance=rs.getInt(CLEARANCE);
					String linkdescription=rs.getString(PRODUCT_DESCRIPTION);
					String color=rs.getString(COLOR);
					String size=rs.getString(SKU_SIZE);
					String age_group=rs.getString(AGE_GROUP);
					String gender=rs.getString(GENDER);
					int isLtl=rs.getInt(LTL);
					int isFreeShipping=rs.getInt(FREE_SHIPPING);
					int isOverSized=rs.getInt(OVERSIZED);
					double weight=rs.getDouble(WEIGHT);
					double length=rs.getDouble(SKU_LENGTH);
					double depth=rs.getDouble(SKU_DEPTH);
					double width=rs.getDouble(WIDTH);
					int isRestrictedAir=rs.getInt(RESTRICT_AIR);
					int isFFL=rs.getInt(IS_FFL);
					int minimumAge=rs.getInt(MINIMUM_AGE);
					String shippingSurchargeByItemRange=rs.getString(SHIPPING_SURCHARGE_QNTY_RANGE);
					
					boolean isAdditionalShipping=isAdditionalHandling(sku_id,length, depth, width, weight,isOverSized);
					boolean overSized=isOverSized(sku_id,length, depth, width, weight,isOverSized);
					
					

					
					
					
					
					Object[] shippingCalculationFlags={isLtl,isFreeShipping,overSized,isRestrictedAir,isFFL,weight,minimumAge,isAdditionalShipping};
					
					
					Item item=new Item();
					item.setId(sku_id);
					if(title!=null){
						item.setTitle(encloseCdata(title));
						if(linkdescription!=null){
							item.setLink(constructProductLink(linkdescription,product_id));
						}
						else {
							item.setLink(constructProductLink(title,product_id));
						}
					}
					if(description!=null){
						item.setDescription(encloseCdata(description));
					}
					if(imageNum>0){
						item.setImageLink(constructImageLink(product_id,1));
						
					}
					if(stockLevel>0){
						item.setAvailability(IN_STOCK);
					}
					else {
						item.setAvailability(OUT_OF_STOCK);
					}
					item.setCondition(CONDITION);
					
					if(price !=0.0){
						if(price <10.0){
							item.setCustomLabel0(encloseCdata(getPriceRange1()));
						}
						else if(price>=10.0 && price <25.0){
							item.setCustomLabel0(encloseCdata(getPriceRange2()));
						}
						else if(price>=25.0 && price <50.0){
							item.setCustomLabel0(encloseCdata(getPriceRange3()));
						}
						else if(price>=50.0 && price <100.0){
							item.setCustomLabel0(encloseCdata(getPriceRange4()));
						}
						else {
							item.setCustomLabel0(encloseCdata(getPriceRange5()));
						}
						
						if(isUsePriceUnitAsPrefix()){
							String stringPrice="USD "+price;
							item.setPrice(stringPrice);
						}
						else {
							String stringPrice=price+" "+USD;
							item.setPrice(stringPrice);
						}
						
					}
					
					if(isUseStorePickupTemplate()){
						item.setPickupLinkTemplate(constructPickupLink(title, product_id));
						item.setMobilePickupLinkTemplate(constructPickupLink(title, product_id));
					}
					
					if(isUseShipping()){
						addShippingInfo(item,shippingCalculationFlags,price);
					}
					
					if(upcs!=null){
						if(!upcs.contains("|")){
							item.setGtin(upcs);
						}
						else {
							upcs=upcs.replace("|",",");
							String[] gitns=upcs.split(",");
							if(isLoggingDebug()){
								logDebug("Multiple upcs present for item: "+item.getId()+": Number of Upcs present: "+gitns.length+": Upcs are: "+upcs);
								int count=1;
								for(String gitn:gitns){
									logDebug("UPC : "+count+" : "+gitn);
									count++;
								}
							}
							item.setGtin(gitns[0]);
						}
					}
					
					if(brand!=null){
						item.setBrand(encloseCdata(brand));
					}
					
					if(isSale){
						if(clearance!=0){
							item.setCustomLabel2(encloseCdata("Clearance"));
						}
						else {
							item.setCustomLabel2(encloseCdata("Sale"));
						}
					}
					else {
						item.setCustomLabel2("");
					}
					
					if(isUseGoogleCategory()){
						if(google_category_name!=null){
							item.setGoogleProductCategory(encloseCdata(google_category_name));
						}
					}
					if(isUseProductType()){
						if(productCategoryId!=null){
							String categoryPath=calculatePath(productCategoryId);
							item.setProductType(encloseCdata(categoryPath));
						}
					}
					item.setCustomLabel3("");
					item.setCustomLabel4("");
					
					if(!isEmptyString(color)){
						item.setColor(color);
					}
					
					if(!isEmptyString(size)){
						item.setSize(size);
					}
					else if(isEmptyString(size)){
						item.setSize(getDefaultSizeValue());
					}
					
					if(!isEmptyString(age_group)){
						item.setAgeGroup(age_group);
					}
					
					if(!isEmptyString(gender)){
						item.setGender(gender);
					}
					
					ch.getItem().add(item);
	
				}
				if(isLoggingInfo())
					logInfo("Items processed for query: "+query);
					
				rs.close();
			}
			catch ( SQLException e) {
				logError("Couldn't execute item query "+query, e);
			}
			catch ( RepositoryException e) {
				logError("Cannot find category for query "+query,e);
			}
			finally {
				try {
					if ( stmt != null) stmt.close();
				}
				catch ( SQLException e) {
					logError("There was a problem closing the connection", e);
				}
				closeConnection(conn);
			}
			vlogInfo("End: GoogleFeedCreator: runQuery()");
			return ch;
			
		}
		
		private void addShippingInfo(Item item,Object[] shippingCalculationFlags,double price){
			vlogDebug("Start: GoogleFeedCreator: addShippingInfo()");
			Shipping shipping=new Shipping();
			String service=getStandardShipping();
			String customLabel=getStandardShipping();
			shipping.setCountry(getCountry());
			if(((int)shippingCalculationFlags[LTL_FLAG_INDEX])==0){
				if((int)shippingCalculationFlags[FREESHIPPING_FLAG_INDEX]==0){
					double shippingPrice=calculateShippingPrice(getStandardShippingRanges(), price, getDefaultStandardShippingPrice());
					double additionalAmount=0.00;
					vlogDebug("Additional handling: "+((boolean)shippingCalculationFlags[ADDITIONAL_HANDLING_FLAG_INDEX]));
					if((boolean)shippingCalculationFlags[ADDITIONAL_HANDLING_FLAG_INDEX]){
						vlogDebug("Over sized flag Index: "+((boolean)shippingCalculationFlags[OVERSIZED_FLAG_INDEX]));
						if((boolean)shippingCalculationFlags[OVERSIZED_FLAG_INDEX]){
							service=getOverSizedShipping();
							customLabel=getOverSizedShipping();
							String chargeAmount=getUpCharges().get(OVER_SIZED);
							if(chargeAmount!=null){
								try
								{
									additionalAmount= Double.parseDouble(chargeAmount);
								}
								catch(NumberFormatException e)
								{
									additionalAmount=0.00;
								}
								
							}
						}
						else {
							customLabel=getAdditionalHandlingShipping();
							service=getAdditionalHandlingShipping();
							String chargeAmount=getUpCharges().get(ADDITIONAL_HANDLING);
							if(chargeAmount!=null){
								try
								{
									additionalAmount= Double.parseDouble(chargeAmount);
								}
								catch(NumberFormatException e)
								{
									additionalAmount=0.00;
								}
								
							}
						}
					}
					else {
						if((int)shippingCalculationFlags[RESTRICTEDAIR_FLAG_INDEX]==1){
							customLabel=getAdditionalHandlingShipping();
							service=getAdditionalHandlingShipping();
							shippingPrice=calculateShippingPrice(getSecondDayShippingRanges(),price,getDefaultStandardShippingPrice());
						}
					}
					vlogDebug("Shipping price: "+shippingPrice +" Additional Amount: "+additionalAmount);
					shippingPrice=shippingPrice+additionalAmount;
					vlogDebug("Shipping price: "+shippingPrice +" Additional Amount: "+additionalAmount);
					shipping.setPrice(shippingPrice+"");
				}
				else {
					shipping.setPrice("0.00");
				}
				
			}
			else {
				service=getLtlShipping();
				customLabel=getLtlShipping();
				if((int)shippingCalculationFlags[FREESHIPPING_FLAG_INDEX]==0){
					double shippingPrice=calculateShippingPrice(getLtlShippingRanges(), ((double)shippingCalculationFlags[WEIGHT_FLAG_INDEX]), getDefaultLtlShippingPrice());
					shipping.setPrice(shippingPrice+"");
				}
				else {
					shipping.setPrice("0.00");
				}
				
			}
			shipping.setService(service);
			item.setCustomLabel1(encloseCdata(customLabel));
			item.getShipping().add(shipping);
			vlogDebug("End: GoogleFeedCreator: addShippingInfo()");
		}
		
		private String calculatePath(String categoryId) throws RepositoryException{
			vlogDebug("Start: GoogleFeedCreator: calculatePath()");
			RepositoryItem category=getCatalogTools().findCategory(categoryId);
			String displayName=(String)category.getPropertyValue("displayName");
			Set<RepositoryItem> parentCategories=(Set<RepositoryItem>)category.getPropertyValue("fixedParentCategories");
			ArrayList<String> displayNames=new ArrayList<String>();
			displayNames.add(displayName);
			displayNames=calculateCategoryPath(category,displayNames);
			String str="";
			for(int i=displayNames.size()-1;i>=0;i--){
				if(i==0){
					str=str+displayNames.get(i);
				}
				else {
					str=str+displayNames.get(i)+" > ";
				}
			}
			vlogDebug("End: GoogleFeedCreator: calculatePath()");
			return str;
		}
		
		private ArrayList<String> calculateCategoryPath(RepositoryItem category,ArrayList<String> names){
			vlogDebug("Start: GoogleFeedCreator: calculateCategoryPath()");
			Set<RepositoryItem> parentCategories=(Set<RepositoryItem>)category.getPropertyValue("fixedParentCategories");
			if(parentCategories ==null || parentCategories.size()==0){
				names=names;
			}
			else{
				for(RepositoryItem item:parentCategories){
					if(!isCategoryActive(item)){
						continue;
					}
					else {
						String displayName=(String)item.getPropertyValue("displayName");
						if(displayName.toLowerCase().contains("root category")){
							displayName="Home";
						}
						names.add(displayName);
						names=calculateCategoryPath(item,names);
						break;
					}
				}
			}
			vlogDebug("End: GoogleFeedCreator: calculateCategoryPath()");
			return names;
		}
		
		 private boolean isCategoryActive(RepositoryItem lProductItem){
			 vlogDebug("Start: GoogleFeedCreator: isCategoryActive()");
		      if(lProductItem!=null){
		        Date endDate = (Date) lProductItem.getPropertyValue("endDate");
		        Date startDate = (Date) lProductItem.getPropertyValue("startDate");
		        Date curDate = Calendar.getInstance().getTime();
		        if (startDate == null) {
		          vlogDebug("End: GoogleFeedCreator: isCategoryActive()");
		          return false;
		        }
		        else if ( endDate == null) {
		          // Return true if the current date is after start date
		        	vlogDebug("End: GoogleFeedCreator: isCategoryActive()");
		          return (curDate.compareTo(startDate) >= 0);
		        }
		        else {
		          // Return true if current date is between start and end dates, inclusively
		        	vlogDebug("End: GoogleFeedCreator: isCategoryActive()");
		          return ((curDate.compareTo(startDate) >= 0) && (curDate.compareTo(endDate) <= 0));
		        }
		      }
		      vlogDebug("End: GoogleFeedCreator: isCategoryActive()");
		      return false;
		    }
		
		private String constructImageLink(String productId, int num){
			vlogDebug("Start: GoogleFeedCreator: constructImageLink()");
			StringBuffer url=new StringBuffer();
			url.append(getImageUrlPrefix());
			url.append(productId).append(getImageUrlParentFolder()+num+".jpg");
			if(url.toString().indexOf('-')==0){
				url.deleteCharAt(0);
			}
			vlogDebug("End: GoogleFeedCreator: constructImageLink()");
			return encloseCdata(url.toString());
		}
		
		private String constructProductLink(String displayName,String productId){
			vlogDebug("Start: GoogleFeedCreator: constructProductLink()");
			/* Product URL */
			StringBuffer url = new StringBuffer();
			url.append(getUrlPrefix());
			if (null != displayName) {
				url.append(format(displayName.toLowerCase()));
			}
			url.append(FOLDER_SEPERATOR).append(productId);
			if(url.toString().indexOf('-')==0){
				url.deleteCharAt(0);
			}
			vlogDebug("End: GoogleFeedCreator: constructProductLink()");
			return encloseCdata(url.toString());
		}
		
		private String constructPickupLink(String displayName,String productId){
			vlogDebug("Start: GoogleFeedCreator: constructPickupLink()");
			/* Product URL */
			StringBuffer url = new StringBuffer();
			url.append(getStorePickupTemplatePrefix());
			if (null != displayName) {
				url.append(format(displayName.toLowerCase()));
			}
			url.append(FOLDER_SEPERATOR).append(productId).append(FOLDER_SEPERATOR).append("{store_code}");
			if(url.toString().indexOf('-')==0){
				url.deleteCharAt(0);
			}
			vlogDebug("End: GoogleFeedCreator: constructPickupLink()");
			return encloseCdata(url.toString());
		}
		
		private boolean isEmptyString(String str){
			return str == null || str.length() == 0;
		}
		
		private String encloseCdata(String origString){
			vlogDebug("Start: GoogleFeedCreator: encloseCdata()");
			if(isUseCdata()){
				if(isUseCdataLib()){
					CDATA cdata = DocumentHelper.createCDATA(origString);
					vlogDebug("End: GoogleFeedCreator: encloseCdata()");
					return cdata.asXML();
				}
				else {
					vlogDebug("End: GoogleFeedCreator: encloseCdata()");
					return "<![CDATA["+origString+"]]>";
				}
			}
			else {
				vlogDebug("End: GoogleFeedCreator: encloseCdata()");
				return origString;
			}
				
		}
			
		public String getExceptionMsg() {
			return exceptionMsg;
		}
	
		public void setExceptionMsg(String exceptionMsg) {
			this.exceptionMsg = exceptionMsg;
		}
		
		protected int processedItemCount(String query)throws SQLException {
			vlogInfo("Start: GoogleFeedCreator: processedItemCount()");
			int processedCount=-1;
			Connection conn = feedDataSource.getConnection();
			Statement stmt = null;
			try{
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					processedCount = rs.getInt("processed_count");
				}
				if(isLoggingInfo())
					logInfo("There processed count for query: "+query+":"+ processedCount +" item(s) processed");
				rs.close();
			}
			catch (SQLException e) {
				logError("Couldn't execute item query "+query, e);
			}
			finally {
				try {
					if ( stmt != null) stmt.close();
				}
				catch ( SQLException e) {
					logError("There was a problem closing the connection to catfeed", e);
				}
			}
			closeConnection(conn);
			vlogInfo("End: GoogleFeedCreator: processedItemCount()");
			return processedCount;
		}
		
		protected int getProcessedItemCount () throws SQLException {
			vlogInfo("Start: GoogleFeedCreator: getProcessedItemCount()");
			int totalProcessedItems=0;
			
			if(isCollectHideData()){
				if(isCollectHideDataInNew()){
					int onSaleItemsCount=processedItemCount(getProcessedCountOnSaleHideQuery());
					int regularItemsCount=processedItemCount(getProcessedCountRegularHideQuery());
					totalProcessedItems=onSaleItemsCount+regularItemsCount;
				}
			}
			else {
				int onSaleItemsCount=processedItemCount(getProcessedCountOnSaleNoHide());
				int regularItemsCount=processedItemCount(getProcessedCountRegularNoHide());
				totalProcessedItems=onSaleItemsCount+regularItemsCount;
			}
			vlogInfo("End: GoogleFeedCreator: getProcessedItemCount()");
			return totalProcessedItems;
		}
	
	
		/*
		 * Process the downloaded feed files by invoking a DB stored proc
		 * The stored procedure accepts the input dir on the commerce server
		 * where the feeds to be processed are placed
		 * 
		 */
		protected void collectData(String pStoredProc) {
			vlogInfo("Start: GoogleFeedCreator: collectData()");
			boolean hasError = false;
			CallableStatement cs;
			try (Connection conn = feedDataSource.getConnection()) {
				this.conn = conn;
				conn.setAutoCommit(false);
	
				cs = getCallableStmt();
				if(getQueryTimeout() > 0) {
					cs.setQueryTimeout(getQueryTimeout());
				}
				
				if(isLoggingInfo()) {
					logInfo(getTaskName() + " Executing import stored proc");
				}
				
				boolean retValue =cs.execute();
				conn.commit();
	
				if(isLoggingInfo()) 
					logInfo(getTaskName() + " StoreProc completed with return code " + retValue);
			}
			catch (Throwable e) {
				hasError = true;
				if(isLoggingError())
					logError(e);
				
				setJobReturnCode(ERROR);
			}
			finally {
				if (!hasError) {
					if(isLoggingInfo())
						logInfo(getTaskName() + " Scheduled Task  collect data completed Successfully");
				}
				else {
					if(isLoggingError())
						logError(getTaskName() + " Scheduled Task collect data completed with Error");
				}
				closeConnection(this.conn);
				vlogInfo("End: GoogleFeedCreator: collectData()");
			}		
		}
	
		
		
		public String format(String pString) {
			vlogDebug("Start: GoogleFeedCreator: format()");
			Matcher matcher = getPattern().matcher(pString);
			vlogDebug("End: GoogleFeedCreator: format()");
			return matcher.replaceAll("-");
		}
	
		/**
		 * Needless to say... closes the connection
		 * @param pConnection
		 */
		protected void closeConnection(Connection pConnection) {
			vlogInfo("Start: GoogleFeedCreator: closeConnection()");
			try {
				if (pConnection != null)
					pConnection.close();
			} catch (SQLException sqle) {
				if (isLoggingError())
					logError(sqle);
				setJobReturnCode(ERROR);
			}
			vlogInfo("End: GoogleFeedCreator: closeConnection()");
		}
		
		protected double calculateShippingPrice(String[] ranges, double dimension,double defaultShippingPrice){
			vlogDebug("Start: GoogleFeedCreator: calculateShippingPrice()");
			double shippingPrice=defaultShippingPrice;
			vlogDebug("Default Shipping Price:"+ defaultShippingPrice);
			if(ranges != null){
				int length = ranges.length;
				for(int i = 0; i < length; i++){
					String subParts[] = StringUtils.splitStringAtCharacter(ranges[i], ':');
					vlogDebug("Subparts:"+ ranges[i]);
					if(subParts.length == 3){
						try{
							double lowRange=Double.parseDouble(subParts[0]);
							double highRange=1.7976931348623157E+308D;
							if(!subParts[1].equalsIgnoreCase("MAX_VALUE")){
								highRange= Double.parseDouble(subParts[1]);
							}
							vlogDebug("lowRange:highRange:amount"+ lowRange+":"+highRange+":"+subParts[2]);
							if(lowRange <= dimension && highRange >=dimension){
								shippingPrice=Double.parseDouble(subParts[2]);
								break;
							}
						}
						catch(Exception ex){
							shippingPrice= defaultShippingPrice;
						}
					}
					else {
						shippingPrice= defaultShippingPrice;
					}
				}
			}
			else {
				shippingPrice= defaultShippingPrice;
			}
			vlogDebug("End: GoogleFeedCreator: calculateShippingPrice()");
			return shippingPrice;
		}
		
		protected boolean isAdditionalHandling(String skuId,double length, double height, double width, double weight,int overSized) {
			vlogDebug("Start: GoogleFeedCreator: isAdditionalHandling()");
			boolean returnFlag = false;
			double dimensionWeight = (length * width * height)/getDimensionWeightDivisor();
			
			if(isOverSized(skuId,length, height, width, weight,overSized)) {
				returnFlag= true;
			}			
			else if(length > getAddlnHandlingMaxLength() ){
				returnFlag= true;
			}

			else if(height > getAddlnHandlingMaxHeight()){
				returnFlag= true;
			}

			else if (weight > getAddlnHandlingMaxWeight()) {
				returnFlag= true;
			}

			else if (width > getAddlnHandlingMaxWidth()) {
				returnFlag= true;
			}
			
			if(isIncludeDimWeightForAddlHandling()) {
				if(dimensionWeight > getAddlnHandlingMaxWeight()) {
					returnFlag= true;
				}
			}
			
/*			
			BZ 3063
			if(objLength >  48){
				vlogDebug("GoogleFeedCreator: isAdditionalHandling(): Length greater than 48: Requires Additional Handling");
				vlogDebug("End: GoogleFeedCreator: isAdditionalHandling()");
				return true;
			}
			if(objDepth > 30){
				vlogDebug("GoogleFeedCreator: isAdditionalHandling(): Depth greater than 30: Requires Additional Handling");
				vlogDebug("End: GoogleFeedCreator: isAdditionalHandling()");
				return true;
			}
			if(objWeight > 70) {
				vlogDebug("GoogleFeedCreator: isAdditionalHandling(): Weight greater than 70: Requires Additional Handling");
				vlogDebug("End: GoogleFeedCreator: isAdditionalHandling()");
				return true;
			}
			if (objWidth > 30){
				vlogDebug("GoogleFeedCreator: isAdditionalHandling(): Widthh greater than 30: Requires Additional Handling");
				vlogDebug("End: GoogleFeedCreator: isAdditionalHandling()");
				return true;
				
			}*/
			vlogDebug("End: GoogleFeedCreator: isAdditionalHandling()");
			return returnFlag;
		}
		
		protected boolean isOverSized(String skuId,double length, double height, double width, double weight,int isOverSized) {
			
			vlogDebug("Start: GoogleFeedCreator: isOverSized()");
			boolean retValue=false;
/*			
 			BZ 3063 
 			double itemDimension=objLength+2*(objDepth+objWidth);
			
			if(objWeight <= 150 && itemDimension > 130){
				vlogDebug("GoogleFeedCreator: weight < 150 and dimension > 130. The object is oversized");
				vlogDebug("End: GoogleFeedCreator: isOverSized()");
				return true;
			}
			vlogDebug("End: GoogleFeedCreator: isOverSized()");*/
			
			double dimensionWeight = 0.0;
			dimensionWeight = (length * width * height)/getDimensionWeightDivisor();
			// Rule #1
			if(length > getOversizedSingleSideMax() || 
					width > getOversizedSingleSideMax() || 
					height > getOversizedSingleSideMax()) {
				retValue= true;
			}

			// Rule #2
			else if( (length + width) > getOversizedTwoSideMax() || 
					(width + height) > getOversizedTwoSideMax() || 
					(height+length) > getOversizedTwoSideMax()) {
				retValue= true;
			}
			
			
			
			// Rule #3
			else if (weight > getOversizedWeightMax() || 
					dimensionWeight > getOversizedWeightMax()) {
					retValue= true;
				
			}
			
			// Rule # 4
			else if(isOverSized==1){
				if(isUseOverSizedFlag()){
					retValue= true;
				}
				
			}
			vlogDebug("SkuId: "+skuId+" Length: "+length+" Width: "+width+" Height: "+height+" Weight: "+weight+" OverSizedFlag: "+isOverSized+" ReturnValue: "+retValue);
			vlogDebug("End: GoogleFeedCreator: isOverSized()");
			return retValue;
		}

				
		
	}
