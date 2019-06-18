package mff.loader;

import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import oracle.sql.TIMESTAMP;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

import javax.sql.DataSource;

//import com.express.ftp.SecureFtpService;
//import com.express.services.cache.TargetCacheInvalidatorHelper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.mff.services.cache.TargetCacheInvalidatorHelper;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import mff.util.ftp.SFTPClientImpl;
import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.GSARepository;

public class ProductLoaderService extends VersionedFeedLoader {

	private static final String CALL_STRING_START = "{ call ";
	private static final String CALL_STRING_END = " }";

	private static final int SCHEMA_CATA = 0;
	private static final int SCHEMA_CATB = 1;
	private static final int SCHEMA_CATA_STAGING = 2;
	private static final int SCHEMA_CATB_STAGING = 3;
	private static final int SCHEMA_PUB = 4;
	private static final int SCHEMA_CATFEED = 5;

	private DataSource mDataSourceCata = null;
	private DataSource mDataSourceCatb = null;
	private DataSource mDataSourceCataStaging = null;
	private DataSource mDataSourceCatbStaging = null;
	private DataSource mDataSourcePub = null;
	private DataSource mDataSourceCatfeed = null;

	// private String mCatfeedSchemaName = null;
	private String mCataSchemaName = null;
	private String mCatbSchemaName = null;
	private String mCataStagingSchemaName = null;
	private String mCatbStagingSchemaName = null;
	private String mPubSchemaName = null;

	private String mProductionTargetName = null;
	private String mStagingTargetName = null;

	private List<Map<String, Object>> mChangedSkus = null;
	private List<Map<String, Object>> mChangedProducts = null;
	private List<Map<String, Object>> mChangedOriginalProducts = null;
	private List<Map<String, Object>> mChangedPrices = null;
	private List<Map<String, Object>> mChangedCategories = null;
	private List<Map<String, Object>> mNewCatgs = null;
	private List<Map<String, Object>> mNewSkus = null;
	private List<Map<String, Object>> mNewProducts = null;
	private List<Map<String, Object>> mProduct = null;
	private List<Map<String, Object>> mNewListPrices = null;
	private List<Map<String, Object>> mNewSalePrices = null;
	private List<Map<String, Object>> mProductsWithNewSkus = null;
	private List<Map<String, Object>> mProductSellingPoints = null;
	private List<Map<String, Object>> mProductPickers = null;
	private List<Map<String, Object>> mSkuPickers = null;
	private List<Map<String, Object>> mParentProdForNewSkus = null;

	private String dataImportFilePattern;
	private String dataImportFileLocalArchiveDir;
	SFTPClientImpl sftpService;
	private String dataImportSFTPUserID;
	private String dataImportSFTPPassword;
	private String remoteSourceDirForDataImportFiles;
	private String remoteArchiveDirForDataImportFiles;
	private boolean remoteArchive;
	private int sftpPort;
	private String sftpHostName;
	boolean enableLoadFilesToSharedLoc;
	boolean enableArchiveFilesInFTPLoc;

	// category attribute maps
	Map catgStringAttributeMap;
	Map catgIntegerAttributeMap;
	// Map catgDoubleAttributeMap;
	// Map catgBooleanAttributeMap;
	Map catgTimestampAttributeMap;

	// product attribute maps
	Map prodStringAttributeMap;
	Map prodIntegerAttributeMap;
	Map prodDoubleAttributeMap;
	Map prodBooleanAttributeMap;
	Map prodTimestampAttributeMap;

	// sku attribute maps
	Map skuStringAttributeMap;
	Map skuIntegerAttributeMap;
	Map skuDoubleAttributeMap;
	Map skuBooleanAttributeMap;
	Map skuTimestampAttributeMap;

	public Map getCatgStringAttributeMap() {
		return catgStringAttributeMap;
	}

	public void setCatgStringAttributeMap(Map catgStringAttributeMap) {
		this.catgStringAttributeMap = catgStringAttributeMap;
	}

	public Map getCatgIntegerAttributeMap() {
		return catgIntegerAttributeMap;
	}

	public void setCatgIntegerAttributeMap(Map catgIntegerAttributeMap) {
		this.catgIntegerAttributeMap = catgIntegerAttributeMap;
	}

	public Map getCatgTimestampAttributeMap() {
		return catgTimestampAttributeMap;
	}

	public void setCatgTimestampAttributeMap(Map catgTimestampAttributeMap) {
		this.catgTimestampAttributeMap = catgTimestampAttributeMap;
	}

	public Map getProdStringAttributeMap() {
		return prodStringAttributeMap;
	}

	public void setProdStringAttributeMap(Map pProdStringAttributeMap) {
		prodStringAttributeMap = pProdStringAttributeMap;
	}

	public Map getProdIntegerAttributeMap() {
		return prodIntegerAttributeMap;
	}

	public void setProdIntegerAttributeMap(Map pProdIntegerAttributeMap) {
		prodIntegerAttributeMap = pProdIntegerAttributeMap;
	}

	public Map getProdDoubleAttributeMap() {
		return prodDoubleAttributeMap;
	}

	public void setProdDoubleAttributeMap(Map pProdDoubleAttributeMap) {
		prodDoubleAttributeMap = pProdDoubleAttributeMap;
	}

	public Map getProdBooleanAttributeMap() {
		return prodBooleanAttributeMap;
	}

	public void setProdBooleanAttributeMap(Map pProdBooleanAttributeMap) {
		prodBooleanAttributeMap = pProdBooleanAttributeMap;
	}

	public Map getProdTimestampAttributeMap() {
		return prodTimestampAttributeMap;
	}

	public void setProdTimestampAttributeMap(Map pProdTimestampAttributeMap) {
		prodTimestampAttributeMap = pProdTimestampAttributeMap;
	}

	public Map getSkuTimestampAttributeMap() {
		return skuTimestampAttributeMap;
	}

	public void setSkuTimestampAttributeMap(Map pSkuTimestampAttributeMap) {
		skuTimestampAttributeMap = pSkuTimestampAttributeMap;
	}

	public Map getSkuStringAttributeMap() {
		return skuStringAttributeMap;
	}

	public void setSkuStringAttributeMap(Map pSkuStringAttributeMap) {
		skuStringAttributeMap = pSkuStringAttributeMap;
	}

	public Map getSkuIntegerAttributeMap() {
		return skuIntegerAttributeMap;
	}

	public void setSkuIntegerAttributeMap(Map pSkuIntegerAttributeMap) {
		skuIntegerAttributeMap = pSkuIntegerAttributeMap;
	}

	public Map getSkuDoubleAttributeMap() {
		return skuDoubleAttributeMap;
	}

	public void setSkuDoubleAttributeMap(Map pSkuDoubleAttributeMap) {
		skuDoubleAttributeMap = pSkuDoubleAttributeMap;
	}

	public Map getSkuBooleanAttributeMap() {
		return skuBooleanAttributeMap;
	}

	public void setSkuBooleanAttributeMap(Map pSkuBooleanAttributeMap) {
		skuBooleanAttributeMap = pSkuBooleanAttributeMap;
	}

	/**
	 * @return the enableArchiveFilesInFTPLoc
	 */
	public boolean isEnableArchiveFilesInFTPLoc() {
		return enableArchiveFilesInFTPLoc;
	}

	/**
	 * @param enableArchiveFilesInFTPLoc
	 *            the enableArchiveFilesInFTPLoc to set
	 */
	public void setEnableArchiveFilesInFTPLoc(boolean enableArchiveFilesInFTPLoc) {
		this.enableArchiveFilesInFTPLoc = enableArchiveFilesInFTPLoc;
	}

	/**
	 * @return the enableLoadFilesToSharedLoc
	 */
	public boolean isEnableLoadFilesToSharedLoc() {
		return enableLoadFilesToSharedLoc;
	}

	/**
	 * @param enableLoadFilesToSharedLoc
	 *            the enableLoadFilesToSharedLoc to set
	 */
	public void setEnableLoadFilesToSharedLoc(boolean enableLoadFilesToSharedLoc) {
		this.enableLoadFilesToSharedLoc = enableLoadFilesToSharedLoc;
	}

	/**
	 * @return the dataImportFilePattern
	 */
	public String getDataImportFilePattern() {
		return dataImportFilePattern;
	}

	/**
	 * @param dataImportFilePattern
	 *            the dataImportFilePattern to set
	 */
	public void setDataImportFilePattern(String dataImportFilePattern) {
		this.dataImportFilePattern = dataImportFilePattern;
	}

	/**
	 * @return the dataImportFileLocalArchiveDir
	 */
	public String getDataImportFileLocalArchiveDir() {
		return dataImportFileLocalArchiveDir;
	}

	/**
	 * @param dataImportFileLocalArchiveDir
	 *            the dataImportFileLocalArchiveDir to set
	 */
	public void setDataImportFileLocalArchiveDir(String dataImportFileLocalArchiveDir) {
		this.dataImportFileLocalArchiveDir = dataImportFileLocalArchiveDir;
	}

	/**
	 * @return the sftpService
	 */
	public SFTPClientImpl getSftpService() {
		return sftpService;
	}

	/**
	 * @param sftpService
	 *            the sftpService to set
	 */
	public void setSftpService(SFTPClientImpl sftpService) {
		this.sftpService = sftpService;
	}

	/**
	 * @return the dataImportSFTPUserID
	 */
	public String getDataImportSFTPUserID() {
		return dataImportSFTPUserID;
	}

	/**
	 * @param dataImportSFTPUserID
	 *            the dataImportSFTPUserID to set
	 */
	public void setDataImportSFTPUserID(String dataImportSFTPUserID) {
		this.dataImportSFTPUserID = dataImportSFTPUserID;
	}

	/**
	 * @return the dataImportSFTPPassword
	 */
	public String getDataImportSFTPPassword() {
		return dataImportSFTPPassword;
	}

	/**
	 * @param dataImportSFTPPassword
	 *            the dataImportSFTPPassword to set
	 */
	public void setDataImportSFTPPassword(String dataImportSFTPPassword) {
		this.dataImportSFTPPassword = dataImportSFTPPassword;
	}

	/**
	 * @return the remoteSourceDirForDataImportFiles
	 */
	public String getRemoteSourceDirForDataImportFiles() {
		return remoteSourceDirForDataImportFiles;
	}

	/**
	 * @param remoteSourceDirForDataImportFiles
	 *            the remoteSourceDirForDataImportFiles to set
	 */
	public void setRemoteSourceDirForDataImportFiles(String remoteSourceDirForDataImportFiles) {
		this.remoteSourceDirForDataImportFiles = remoteSourceDirForDataImportFiles;
	}

	/**
	 * @return the remoteArchiveDirForDataImportFiles
	 */
	public String getRemoteArchiveDirForDataImportFiles() {
		return remoteArchiveDirForDataImportFiles;
	}

	/**
	 * @param remoteArchiveDirForDataImportFiles
	 *            the remoteArchiveDirForDataImportFiles to set
	 */
	public void setRemoteArchiveDirForDataImportFiles(String remoteArchiveDirForDataImportFiles) {
		this.remoteArchiveDirForDataImportFiles = remoteArchiveDirForDataImportFiles;
	}

	/**
	 * @return the remoteArchive
	 */
	public boolean isRemoteArchive() {
		return remoteArchive;
	}

	/**
	 * @param remoteArchive
	 *            the remoteArchive to set
	 */
	public void setRemoteArchive(boolean remoteArchive) {
		this.remoteArchive = remoteArchive;
	}

	/**
	 * @return the sftpPort
	 */
	public int getSftpPort() {
		return sftpPort;
	}

	/**
	 * @param sftpPort
	 *            the sftpPort to set
	 */
	public void setSftpPort(int sftpPort) {
		this.sftpPort = sftpPort;
	}

	/**
	 * @return the sftpHostName
	 */
	public String getSftpHostName() {
		return sftpHostName;
	}

	/**
	 * @param sftpHostName
	 *            the sftpHostName to set
	 */
	public void setSftpHostName(String sftpHostName) {
		this.sftpHostName = sftpHostName;
	}

	@Override
	public List<String> loadFilesToSharedLocation() {
		List<String> fileNames = new ArrayList<String>();
		try {
			fileNames = getSftpService().downloadFeedFiles(getRemoteSourceDirForDataImportFiles(),
					getDefaultInputFileDir(), getDataImportFilePattern());
		} catch (IOException e) {
			vlogError(e, "IOException occurred");
		}
		return fileNames;
		/*
		 * try{ if(isEnableLoadFilesToSharedLoc()) //fileNames =
		 * getSftpService().getFiles(getDataImportSFTPUserID(),
		 * getDataImportSFTPPassword(), getSftpHostName(),
		 * getRemoteSourceDirForDataImportFiles(), getSftpPort(),
		 * getDataImportFilePattern(), getDefaultInputFileDir());
		 * getSftpClient().downloadFeedFiles(
		 * getRemoteSourceDirForDataImportFiles(), getDefaultInputFileDir(),
		 * getDataImportFilePattern()); } catch (IOException e) { vlogError(e,
		 * "IOException occurred"); } catch (JSchException e) { vlogError(e,
		 * "JSchException occurred"); } catch (SftpException e) { vlogError(e,
		 * "SftpException occurred"); }
		 */

	}

	@Override
	public void archiveFilesInFTPLocation(List<String> fileNames) {
		super.archiveFilesInFTPLocation(fileNames);
		/*
		 * try{ if(isEnableArchiveFilesInFTPLoc())
		 * getSftpService().archiveFiles(fileNames, getDataImportSFTPUserID(),
		 * getDataImportSFTPPassword(), getSftpHostName(), getSftpPort(),
		 * getRemoteArchiveDirForDataImportFiles(), getDefaultInputFileDir());
		 * }catch (IOException e) { vlogError(e, "IOException occurred"); }
		 * catch (JSchException e) { vlogError(e, "JSchException occurred"); }
		 * catch (SftpException e) { vlogError(e, "SftpException occurred"); }
		 */

	}

	private boolean mDeleteInsteadOfArchiving = false;

	public void setDeleteInsteadOfArchiving(boolean pDeleteInsteadOfArchiving) {
		mDeleteInsteadOfArchiving = pDeleteInsteadOfArchiving;
	}

	public boolean getDeleteInsteadOfArchiving() {
		return mDeleteInsteadOfArchiving;
	}

	// Load the file contents into the DB, via Oracle
	public void processFeedFile(File file) throws Exception {
		loadXMLFile(file);
	}

	// This will never get called, since we're pulling the file list from Oracle
	public boolean loadThisFile(File file) {
		return false;
	}

	// Completely override the base implementation to generate the file list via
	// Oracle
	protected ArrayList<File> findFeedFiles() {
		// If the feed file list is not empty, do NOT regenerate it
		if (getFeedFiles() != null && getFeedFiles().size() > 0) {
			return getFeedFiles();
		}
		// Call the SP to generate the file list
		// This call will cause the filenames to be dropped in the table
		// tmp_xml_filenames,
		// with the appropriate sequence_num based on date based ordering of the
		// files
		if (isLoggingDebug())
			logDebug("Loading available XML extract filenames in Oracle...");
		try {
			execProcedure("mff_catalog_loader.load_xml_filenames", SCHEMA_CATFEED, getDefaultInputFileDir());
		} catch (SQLException e) {
			logError("There was a problem attempting to load XML filenames in Oracle", e);
			return null;
		}

		// Get the list of files from Oracle, ordered by the assigned sequence
		if (isLoggingDebug())
			logDebug("Getting the list of XML filenames...");
		Connection conn = getConnection(SCHEMA_CATFEED);
		Statement stmt = null;
		ArrayList<File> xmlfiles = new ArrayList<File>();
		try {
			String selSql = "SELECT filename from tmp_xml_filenames order by sequence_num";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(selSql);
			while (rs.next()) {
				String filename = rs.getString("filename");
				xmlfiles.add(new File(filename));
				if (isLoggingDebug())
					logDebug("Will load file " + filename);
			}
			rs.close();
		} catch (SQLException e) {
			logError("Couldn't get the list of filenames from catfeed", e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				return null;
			}
			closeConnection(conn);
		}
		return xmlfiles;
	}

	// Call SP to load XML file contents into tmp tables
	private void loadXMLFile(File pFile) throws SQLException {

		if (isLoggingDebug())
			logDebug("Loading XML file data " + pFile.getName() + " via Oracle...");
		execProcedure("mff_catalog_loader.load_file_into_tmp", SCHEMA_CATFEED, pFile.getName());
	}

	protected void sortFilesByDate(List<File> files) {
		// The files are coming in presorted by Oracle SPs. Since the files are
		// on the Oracle
		// server, and we don't have access to them, we can't do any date-based
		// sorting.
		// Just use the order from the query.
	}

	// Call SP to post process XML file data in tmp tables
	private void postProcessXMLData() throws SQLException {

		if (isLoggingDebug())
			logDebug("Post-processing XML file data via Oracle...");
		execProcedure("mff_catalog_loader.post_process_tmp_data", SCHEMA_CATFEED);
	}

	// Completely override the default implementation. File archiving is handled
	// by Oracle
	protected void archiveFeedFiles() {
		try {
			if (mFeedFiles != null) {
				/*
				 * if (mDeleteInsteadOfArchiving) { logDebug(
				 * "Deleting current list of XML files in Oracle...");
				 * execProcedure("exp_loader.delete_xml_files", SCHEMA_CATFEED,
				 * getDefaultInputFileDir()); } else { logDebug(
				 * "Archiving current list of XML files in Oracle...");
				 * execProcedure("exp_loader.archive_xml_files", SCHEMA_CATFEED,
				 * getDefaultInputFileDir()); }
				 */
				// archive_xml_files
				logDebug("Archiving current list of XML files in Oracle...");
				execProcedure("mff_catalog_loader.archive_xml_files", SCHEMA_CATFEED, getDefaultInputFileDir());
			}
		} catch (SQLException e) {
			logError("There was a problem archiving/deleting XML files in Oracle", e);
		}
	}

	// Call SP to setup catfeed for the import
	private void setupCatfeed() {
		// Call the SP to set up catfeed
		if (isLoggingDebug())
			logDebug("Setting up catfeed in Oracle...");
		try {
			execProcedure("mff_catalog_loader.setup_catfeed", SCHEMA_CATFEED);

			// We want to force all changes into a single TX, so package up all
			// the schema names
			// into a list and call a single SP that will handle the deployment
			// setupDeploySchemas();
		} catch (SQLException e) {
			logError("There was a problem setting up the catfeed schema in Oracle", e);
		}
	}

	// Cleanup catfeed TMP data, via SP
	protected void cleanupCatfeed() {
		// Call the SP to clean up catfeed
		if (isLoggingDebug())
			logDebug("Cleaning up catfeed in Oracle...");
	}

	protected void processFeedFiles() throws Exception {
		// Calling this will simply load the data for all current XML files into
		// catfeed
		// We still need to process everything into cat and pub schemas after
		// this
		super.processFeedFiles();
		if (isLoggingDebug())
			logDebug("Finished loading all current XML files into catfeed");

		// We've finished loading the raw data into tmp catfeed tables
		// Now, do any post processing before we move the data out to catfeed
		// cata copies
		// The stored procedure this calls will:
		// - Perform any final scrubbing of the data that didn't happen during
		// the initial raw load
		// - Set all operational flags denoting which assets are new, existing,
		// to be updated, etc
		//
		postProcessXMLData();

		// At this point, we've successfully loaded all current XML file data
		// into TMP tables
		// and it's ready to be deployed out. But, before we do that, let's make
		// all Project-based
		// changes first. The rationale is that we don't want to start
		// committing direct DB changes
		// to catfeed/cat/pub schemas until we've absolutely done all possible
		// work that we can do.
		// And, Project-based changes can easily be deleted if there is a
		// problem with the DB-based
		// updates to follow... and we REALLY need those final DB updates to be
		// completely done within
		// a single TX.
		//
		makeProjectBasedChanges();

		// And now, let's go ahead and "deploy" our changes to all the cat
		// schemas via Oracle SPs.
		// This is all done within a single TX so that we can back it all out if
		// needed.
		// If an Exception is thrown, Oracle will have already rolled everything
		// back... and then
		// VersionedFeedLoader will pick up the Exception and blow away the
		// Project and clean everything
		// up without deploying anything
		//
		deployChanges();

		// And, if we've made it this far, incrementally invalidate caches
		// across the board on all assets
		// that were updated directly via an Oracle stored procedure
		//
		invalidateCaches();
	}

	protected void exit() {
		// Call the superclass method to do base cleanup stuff
		super.exit();

		mChangedSkus = null;
		mChangedProducts = null;
		mChangedOriginalProducts = null;
		mChangedPrices = null;
		mNewCatgs = null;
		mNewSkus = null;
		mNewProducts = null;
		mNewListPrices = null;
		mNewSalePrices = null;
		mProductsWithNewSkus = null;
		mProductSellingPoints = null;
		mProductPickers = null;
		mSkuPickers = null;
		mChangedCategories = null;
		mParentProdForNewSkus = null;
		// Now we can drop all the temp tables, etc., so call the SQL cleanup
		// method
		cleanupCatfeed();
	}

	protected void init() {
		if (isLoggingDebug())
			logDebug("Init works from herqsdfsdsdfqssddsadadse!!!!!222!!!www!");
		super.init();

		mChangedSkus = null;
		mChangedProducts = null;
		mChangedOriginalProducts = null;
		mChangedPrices = null;
		mNewCatgs = null;
		mNewSkus = null;
		mNewProducts = null;
		mNewListPrices = null;
		mNewSalePrices = null;
		mProductsWithNewSkus = null;
		mProductSellingPoints = null;
		mProductPickers = null;
		mSkuPickers = null;
		mChangedCategories = null;
		mParentProdForNewSkus = null;
		resetTimings();

		// Perform catfeed setup
		setupCatfeed();
	}

	private void makeProjectBasedChanges() throws RepositoryException {
		// Make all changes that need to be Project-based... This includes:
		// - New SKUs
		// - New Products
		// - New Categories

		// They are created in this order so when the time
		// comes to create relationships cat->child products
		// and prod->child skus... the associated objects are already in place

		createNewSkus();
		createNewProducts();
		createNewCategories();

		updateCatWithNewProducts();
		updateProdWithNewSkus();

		// updateProductsWithNewSkus();
		// addNewSkusToProducts();
		updateCatChildCategories();
		// updateCategoryProductRels();
		// createNewPrices();
	}

	@Override
	public String createEmailBody() {
		// TODO Auto-generated method stub
		return super.createEmailBody();
	}

	// Update asset counts to include assets updated via SQL
	protected TreeMap<String, ChangedAsset> countProjectAssets() {
		// The base implementation counts up the changes by item type for the
		// current Project.... so, let's get that and add to it
		TreeMap<String, ChangedAsset> assetCounts = super.countProjectAssets();

		// Get the count of SKUs updated via SQL and add the count
		int count = getChangedSkuCount();
		if (count > 0) {
			ChangedAsset skuCount = new ChangedAsset();
			skuCount.itemType = "sku (update via direct SQL1)";
			skuCount.numChanged = count;
			assetCounts.put("sku (update via direct SQL)", skuCount);
		}

		// Get the count of products updated via SQL and add the count
		count = getChangedProductCount();
		if (count > 0) {
			ChangedAsset prodCount = new ChangedAsset();
			prodCount.itemType = "product (update via direct SQL)";
			prodCount.numChanged = count;
			assetCounts.put("product (updatevia direct SQL)", prodCount);
		}

		// Get the count of price items updated via SQL and add the count
		/*
		 * count = getChangedPriceCount(); if ( count > 0) { ChangedAsset
		 * priceCount = new ChangedAsset(); priceCount.itemType =
		 * "price (update via direct SQL)"; priceCount.numChanged = count;
		 * assetCounts.put("price (update via direct SQL)", priceCount); }
		 */

		// Get the count of categories updated via SQL and add the count
		/*
		 * count = getCategoriesWithRemovedProductsCount(); if (count > 0) {
		 * ChangedAsset catCount = new ChangedAsset(); catCount.itemType =
		 * "category (update via direct SQL)"; catCount.numChanged = count;
		 * assetCounts.put("category (update via direct SQL)", catCount); }
		 */

		return assetCounts;
	}

	// =========================
	// DB Item retrieval methods
	// =========================

	// ---------

	// Get the full data for all Categories that are new from the DB
	private List<Map<String, Object>> getNewCategories() {
		if (isLoggingDebug())
			logDebug("GetNewCategories called");
		if (mNewCatgs == null) {
			mNewCatgs = findItemsByQuery("select * from tmp_catg_data where catg_exists=0");
		} else {
			if (isLoggingDebug())
				logDebug("mNewCatgs is not null " + mNewCatgs.toString());
		}
		return mNewCatgs;
	}

	// Get the full data for all SKUs that are new from the DB
	private List<Map<String, Object>> getNewSkus() {
		if (isLoggingDebug())
			logDebug("GetNewSkus called");
		if (mNewSkus == null) {
			mNewSkus = findItemsByQuery("select * from tmp_sku_data where sku_exists=0");
		} else {
			if (isLoggingDebug())
				logDebug("mNewSkus is not null " + mNewSkus.toString());
		}
		return mNewSkus;
	}

	// Get the full data for all SKUs that are new from the DB
	private List<Map<String, Object>> getParentProdsForNewSkus() {
		if (isLoggingDebug())
			logDebug("getParentProdsForNewSkus called");
		if (mParentProdForNewSkus == null) {
			mParentProdForNewSkus = findItemsByQuery(
					"select distinct product_id from tmp_sku_data where create_new_sku=1");
		} else {
			if (isLoggingDebug())
				logDebug("mNewSkus is not null " + mNewSkus.toString());
		}
		return mParentProdForNewSkus;
	}

	// Get the list of SKU IDs that were changed via direct SQL update.
	// We need only the ID for incremental cache invalidation
	private List<Map<String, Object>> getChangedSkus() {
		if (mChangedSkus == null) {
			mChangedSkus = findItemsByQuery("select sku_id from tmp_sku_data where sku_change=1");
		}
		return mChangedSkus;
	}

	// Return the count of changed SKUs for reporting
	private int getChangedSkuCount() {
		return (getChangedSkus() == null ? 0 : getChangedSkus().size());
	}

	// ---------

	// Get the full data for all products that are new from the DB
	private List<Map<String, Object>> getNewProducts() {
		if (mNewProducts == null) {
			mNewProducts = findItemsByQuery("select * from tmp_prod_data where prod_exists=0");
		}
		return mNewProducts;
	}

	// Get the full data for all products that point to new SKUs from the DB
	private List<Map<String, Object>> getProductsWithNewSkus() {
		if (mProductsWithNewSkus == null) {
			mProductsWithNewSkus = findItemsByQuery("select * from tmp_sku_data where prod_exists=1 and sku_exists=0");
		}
		return mProductsWithNewSkus;
	}

	// Get the list of product IDs that were changed via direct SQL update from
	// the feed.
	// We need only the ID for incremental cache invalidation
	// Products that are changed via direct SQL will have either prod_change or
	// prod_skus_change set to 1, and
	// must have no new SKUs (as those products are updated via Project due to
	// the dependency on new assets)
	private List<Map<String, Object>> getChangedProducts() {
		if (mChangedProducts == null) {
			mChangedProducts = findItemsByQuery(
					"select product_id from tmp_prod_data where (prod_change=1 or prod_skus_change=1) and prod_has_new_skus=0");
		}
		return mChangedProducts;
	}

	// Get the list of product IDs that were changed because a SKU was moved to
	// a clearance product from the feed.
	// Since these changes are made via direct SQL update, we need only the ID
	// for incremental cache invalidation
	private List<Map<String, Object>> getChangedOriginalProducts() {
		if (mChangedOriginalProducts == null) {
			mChangedOriginalProducts = findItemsByQuery("select o.product_id orig_product,s.product_id new_product " +
															"from tmp_sku_orig_prod o, tmp_sku_data s " +
																"where o.sku_id=s.sku_id");
		}
		return mChangedOriginalProducts;
	}

	//
	private List<Map<String, Object>> getProduct(String pProductId) {
		// if(mProductPickers == null) {
		mProduct = findItemsByQuery("select * from tmp_prod_data where product_id='" + pProductId + "'");
		// }
		return mProduct;
	}

	private List<Map<String, Object>> getPickersForProduct(String pProductId) {
		// if(mProductPickers == null) {
		mProductPickers = findItemsByQuery("select * from tmp_prod_picker_data where product_id='" + pProductId + "'");
		// }
		return mProductPickers;
	}

	private List<Map<String, Object>> getPickersForSku(String pSkuId) {
		// if(mProductPickers == null) {
		mSkuPickers = findItemsByQuery("select * from tmp_sku_picker_data where sku_id='" + pSkuId + "'");
		// }
		return mSkuPickers;
	}

	private List<Map<String, Object>> getSellingPointsForProduct(String pProductId) {
		if (mProductSellingPoints == null) {
			mProductSellingPoints = findItemsByQuery(
					"select * from tmp_prod_selling_point_data where product_id='" + pProductId + "'");
		}
		return mProductSellingPoints;
	}

	// getChildSkusForProd
	private List<String> getChildSkusForProd(String pProductId) {
		// List<Map<String,Object>> skus = findItemsByQuery("select sku_id from
		// tmp_sku_data where product_id='"+pProductId+"' and prod_exists=1 and
		// sku_exists=0");
		List<Map<String, Object>> skus = findItemsByQuery(
				"select sku_id from tmp_chld_skus where product_id='" + pProductId + "'");
		List<String> skuIds = new ArrayList<String>();
		for (Map<String, Object> sku : skus) {
			skuIds.add(getStringValue("sku_id", sku));
		}
		return skuIds;
	}

	// Get the list of SKU IDs from the feed that are associated with the
	// product with the given ID
	// Only return active SKUs
	private List<String> getSkuIdsForProduct(String pProductId) {
		// List<Map<String,Object>> skus = findItemsByQuery("select sku_id from
		// tmp_sku_data where product_id='"+pProductId+"'");
		List<Map<String, Object>> skus = findItemsByQuery(
				"select sku_id from tmp_chld_skus where product_id='" + pProductId + "'");

		List<String> skuIds = new ArrayList<String>();
		for (Map<String, Object> sku : skus) {
			skuIds.add(getStringValue("sku_id", sku));
		}
		return skuIds;
	}

	// Return the full count of products changed via direct SQL
	private int getChangedProductCount() {
		// return ( (getChangedProducts() == null ? 0 :
		// getChangedProducts().size()) + (getChangedOriginalProducts() == null
		// ? 0 : getChangedOriginalProducts().size()));
		return (getChangedProducts() == null ? 0 : getChangedProducts().size());
	}

	// ---------

	// Get the list of category IDs that were updated via direct SQL to delete
	// removed child products
	// We need this for incremental cache invalidation
	private List<Map<String, Object>> getChangedCategories() {
			mChangedCategories = findItemsByQuery("select category_id from tmp_catg_data where catg_change=1");
		return mChangedCategories;
	}

	private List<Map<String, Object>> getCategoriesWithRemovedProducts() {
			mChangedCategories = findItemsByQuery("select distinct category_id from tmp_cat_prod_rel_change");
		return mChangedCategories;
	}

	private List<Map<String, Object>> getChangedCatChildCats() {
		mChangedCategories = findItemsByQuery("select distinct category_id from tmp_changed_cats");
	return mChangedCategories;
}
	private List<Map<String, Object>> getNewChildProdsForCat(String pCategoryId) {
		// List<Map<String,Object>> cats = findItemsByQuery("select category_id
		// from tmp_prod_cat_data where product_id='"+pProductId+"'");
		String concatCategories = null;
		String[] parentCategories = null;
		// List<Map<String,Object>> cats = findItemsByQuery("select
		// parent_category from tmp_prod_data where
		// product_id='"+pProductId+"'");
		// List<Map<String,Object>> prods = findItemsByQuery("select product_id
		// from tmp_prod_catg_data where category_id='"+pCategoryId+"' and
		// catg_exists=1 and prod_exists=0");

		// ** Modifying the query to fix error in the case where category is in
		// the import project.. and includes new products as well as prod
		// changes affecting the catg
		// List<Map<String,Object>> prods = findItemsByQuery("select product_id
		// from tmp_prod_catg_data where category_id='"+pCategoryId+"' and
		// catg_exists=1 and prod_exists=0");
		List<Map<String, Object>> prods = findItemsByQuery(
				"select product_id from tmp_prod_catg_data where category_id='" + pCategoryId
						+ "' and catg_exists = 1 and (prod_exists=0 or (prod_exists=1 and catg_change=1))");

		/*
		 * List<String> catIds = new ArrayList<String>(); String catId = null;
		 * for ( Map<String,Object> cat : cats) { //concatCategories =
		 * getStringValue("parent_category", cat); catId =
		 * getStringValue("parent_category", cat); logDebug(
		 * "Categories found for product " + pProductId + ". Parent Catg - " +
		 * catId); //parentCategories = concatCategories.split("\\^");
		 * //for(String parentCategory:parentCategories) { logDebug("Adding " +
		 * catId); catIds.add(catId); //} }
		 */
		return prods;
	}

	private List<Map<String, Object>> getChildProdsForCat(String pCategoryId) {
		// List<Map<String,Object>> cats = findItemsByQuery("select category_id
		// from tmp_prod_cat_data where product_id='"+pProductId+"'");
		String concatCategories = null;
		String[] parentCategories = null;
		// List<Map<String,Object>> cats = findItemsByQuery("select
		// parent_category from tmp_prod_data where
		// product_id='"+pProductId+"'");
		// List<Map<String,Object>> prods = findItemsByQuery("select product_id
		// from tmp_prod_catg_data where category_id='"+pCategoryId+"' and
		// catg_exists=1 and prod_exists=0");
		List<Map<String, Object>> prods = findItemsByQuery(
				"select product_id from tmp_prod_catg_data where category_id='" + pCategoryId + "'");

		/*
		 * List<String> catIds = new ArrayList<String>(); String catId = null;
		 * for ( Map<String,Object> cat : cats) { //concatCategories =
		 * getStringValue("parent_category", cat); catId =
		 * getStringValue("parent_category", cat); logDebug(
		 * "Categories found for product " + pProductId + ". Parent Catg - " +
		 * catId); //parentCategories = concatCategories.split("\\^");
		 * //for(String parentCategory:parentCategories) { logDebug("Adding " +
		 * catId); catIds.add(catId); //} }
		 */
		return prods;
	}

	private List<Map<String, Object>> getCategoriesForUpdate() {
		// List<Map<String,Object>> cats = findItemsByQuery("select category_id
		// from tmp_prod_cat_data where product_id='"+pProductId+"'");
		String concatCategories = null;
		String[] parentCategories = null;
		// List<Map<String,Object>> cats = findItemsByQuery("select
		// parent_category from tmp_prod_data where
		// product_id='"+pProductId+"'");
		List<Map<String, Object>> cats = findItemsByQuery(
				"select distinct category_id from tmp_prod_catg_data where catg_exists=1 and prod_exists=0");

		/*
		 * List<String> catIds = new ArrayList<String>(); String catId = null;
		 * for ( Map<String,Object> cat : cats) { //concatCategories =
		 * getStringValue("parent_category", cat); catId =
		 * getStringValue("parent_category", cat); logDebug(
		 * "Categories found for product " + pProductId + ". Parent Catg - " +
		 * catId); //parentCategories = concatCategories.split("\\^");
		 * //for(String parentCategory:parentCategories) { logDebug("Adding " +
		 * catId); catIds.add(catId); //} }
		 */
		return cats;
	}

	// Get the list of category IDs from the feed that are associated with the
	// product with the given ID
	private List<Map<String, Object>> getCategoriesForProduct(String pProductId) {
		// List<Map<String,Object>> cats = findItemsByQuery("select category_id
		// from tmp_prod_cat_data where product_id='"+pProductId+"'");
		String concatCategories = null;
		String[] parentCategories = null;
		// List<Map<String,Object>> cats = findItemsByQuery("select
		// parent_category from tmp_prod_data where
		// product_id='"+pProductId+"'");
		List<Map<String, Object>> cats = findItemsByQuery(
				"select category_id,primary_catg from tmp_prod_catg_data where product_id='" + pProductId + "'");

		/*
		 * List<String> catIds = new ArrayList<String>(); String catId = null;
		 * for ( Map<String,Object> cat : cats) { //concatCategories =
		 * getStringValue("parent_category", cat); catId =
		 * getStringValue("parent_category", cat); logDebug(
		 * "Categories found for product " + pProductId + ". Parent Catg - " +
		 * catId); //parentCategories = concatCategories.split("\\^");
		 * //for(String parentCategory:parentCategories) { logDebug("Adding " +
		 * catId); catIds.add(catId); //} }
		 */
		return cats;
	}

	// Return the count of categories changed via SQL
	private int getCategoriesWithRemovedProductsCount() {
		return (getCategoriesWithRemovedProducts() == null ? 0 : getCategoriesWithRemovedProducts().size());
	}

	// ---------

	// Get the full data for the list price items that we need to create
	private List<Map<String, Object>> getNewListPrices() {
		if (mNewListPrices == null) {
			mNewListPrices = findItemsByQuery(
					"select sku_id,list_price price,on_sale from tmp_sku_data where list_price_exists=0 and prod_active_flag=1 and active_flag=1 and prod_delete_flag=0");
		}
		return mNewListPrices;
	}

	// Get the full data for the sale price items that we need to create
	private List<Map<String, Object>> getNewSalePrices() {
		if (mNewSalePrices == null) {
			mNewSalePrices = findItemsByQuery(
					"select sku_id,sale_price price,on_sale from tmp_sku_data where sale_price_exists=0 and prod_active_flag=1 and active_flag=1 and prod_delete_flag=0");
		}
		return mNewSalePrices;
	}

	// Get the list of price item IDs that were changed via direct SQL so that
	// we can
	// do incremental cache invalidation
	private List<Map<String, Object>> getChangedPrices() {
		if (mChangedPrices == null) {
			mChangedPrices = findItemsByQuery("select price_id from tmp_price_change");
		}
		return mChangedPrices;
	}

	// Return the count of price items changed via direct SQL
	private int getChangedPriceCount() {
		return (getChangedPrices() == null ? 0 : getChangedPrices().size());
	}

	// ---------
	// This utility method returns the result set of an Oracle query as a List
	// of Maps...
	// where each entry in the List is a row in the result set, containing a Map
	// of column name to
	// the value... which will get converted using the default datatype->Java
	// class mappings for the JDBC driver
	private List<Map<String, Object>> convertResultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		while (rs.next()) {
			HashMap<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
			}
			list.add(row);
		}

		return list;
	}

	// -----------
	// These are a bunch of utility methods for pulling data from the data
	// structure returned by convertResultSetToList()
	// -----------
	private Timestamp getTimestampValue(String propName, Map<String, Object> item) {
		// Convert the value of the given property to an Integer
		if (!item.containsKey(propName)) {
			if (isLoggingDebug())
				logDebug("Couldn't find propert1y --" + propName + "--");
			return null;
		}

		Object value = item.get(propName);
		if (value == null)
			return null;
		else if (value instanceof Timestamp)
			return (Timestamp) value;
		else if (value instanceof TIMESTAMP) {
			try {
				return ((TIMESTAMP) value).timestampValue();
			} catch (SQLException e) {
				logError("There was a problem converting an Oracle TIMESTAMP to a Java Timestamp", e);
			}
		} else if (value instanceof Date)
			return new Timestamp(((Date) value).getTime());

		if (isLoggingDebug())
			logDebug("Couldn't convert property " + propName + " of type " + value.getClass().getName()
					+ " to a Timestamp");
		return null;
	}

	private String getStringValue(String propName, Map<String, Object> item) {
		// Convert the value of the given property to a String
		if (!item.containsKey(propName)) {
			if (isLoggingDebug())
				logDebug("Couldn't find property --" + propName + "--");
			return null;
		}

		Object value = item.get(propName);
		if (value == null) {
			return null;
		} else if (value instanceof String)
			return (String) value;
		else if (value instanceof Clob)
			try {
				return ((Clob) value).getSubString(1, (int) ((Clob) value).length());
			} catch (SQLException e) {
				logError(e);
				return null;
			}
		else
			return value.toString();
	}

	private Boolean getBooleanValue(String propName, Map<String, Object> item) {
		// Convert the value of the given property to a Boolean
		if (!item.containsKey(propName)) {
			if (isLoggingDebug())
				logDebug("Couldn't find property --" + propName + "--");
			return Boolean.FALSE;
		}

		Object value = item.get(propName);
		if (value == null)
			return Boolean.FALSE;
		else if (value instanceof BigDecimal) {
			if (((BigDecimal) value).intValueExact() == 0)
				return Boolean.FALSE;
			else
				return Boolean.TRUE;
		} else if (value instanceof String)
			return Boolean.parseBoolean((String) value);

		if (isLoggingDebug())
			logDebug("Couldn't convert property " + propName + " of type " + value.getClass().getName()
					+ " to a Boolean");
		return null;
	}

	private Integer getIntegerValue(String propName, Map<String, Object> item) {
		// Convert the value of the given property to an Integer
		if (!item.containsKey(propName.trim())) {
			if (isLoggingDebug())
				logDebug("Couldn't find property --" + propName + "--");
			return null;
		}

		Object value = item.get(propName.trim());
		if (value == null)
			return null;
		else if (value instanceof BigDecimal)
			return new Integer(((BigDecimal) value).intValue());
		else if (value instanceof Integer)
			return (Integer) value;
		else if (value instanceof Double)
			return new Integer(((Double) value).intValue());
		else if (value instanceof String)
			return Integer.parseInt((String) value);

		if (isLoggingDebug())
			logDebug("Couldn't convert property " + propName + " of type " + value.getClass().getName()
					+ " to an Integer");
		return null;
	}

	private Double getDoubleValue(String propName, Map<String, Object> item) {
		// Convert the value of the given property to a Double
		if (!item.containsKey(propName.trim())) {
			if (isLoggingDebug())
				logDebug("Couldn't find property --" + propName.trim() + "--");
			return null;
		}

		Object value = item.get(propName.trim());
		if (value == null)
			return null;
		else if (value instanceof BigDecimal)
			return new Double(((BigDecimal) value).doubleValue());
		else if (value instanceof Integer)
			return new Double(((Integer) value).doubleValue());
		else if (value instanceof Double)
			return (Double) value;
		else if (value instanceof String)
			return Double.parseDouble((String) value);

		if (isLoggingDebug())
			logDebug("Couldn't convert property " + propName + " of type " + value.getClass().getName()
					+ " to a Double");
		return null;
	}

	// This helper method takes a SQL query, executes the query via JDBC, and
	// converts the
	// result set to our handy dandy List of Maps
	private List<Map<String, Object>> findItemsByQuery(String pQuery) {
		// Get the list of items from Oracle and dump it into a generic
		// structure
		if (isLoggingDebug())
			logDebug("Getting items via " + pQuery);
		Connection conn = getConnection(SCHEMA_CATFEED);
		Statement stmt = null;
		List<Map<String, Object>> items = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(pQuery);
			items = convertResultSetToList(rs);
			if (isLoggingDebug())
				logDebug("There are " + (items != null ? items.size() : 0) + " items in the result set");
			rs.close();
		} catch (SQLException e) {
			logError("Couldn't execute item query " + pQuery, e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				return items;
			}
			closeConnection(conn);
		}
		return items;
	}

	// =====================================
	// Here's all the Project based logic
	// =====================================

	// Create all SKUs marked earmarked as new by exp_loader... add them to the
	// current Project
	private void createNewCategories() throws RepositoryException {
		// First, get a list of the new SKUs that we're creating
		List<Map<String, Object>> newCatgs = getNewCategories();
		MutableRepository productCatalog = getCatalogRepository();

		// Now create them...
		String thisCategoryId = null;
		HashSet<String> createdCategoryIds = new HashSet<String>();
		try {
			if (newCatgs != null) {
				for (Map<String, Object> newCatg : newCatgs) {
					thisCategoryId = getStringValue("category_id", newCatg);
					if (!createdCategoryIds.contains(thisCategoryId)) {
						MutableRepositoryItem mutCatgItem = productCatalog.createItem(thisCategoryId,
								LoaderConstants.CAT_ITEM_DESCR);
						setCategoryAttributes(mutCatgItem, newCatg);
						createdCategoryIds.add(thisCategoryId);

						// List<RepositoryItem> fixedProducts =
						// (List<RepositoryItem>)
						// catItem.getPropertyValue("fixedChildProducts");
						List<Map<String, Object>> childProds = getChildProdsForCat(thisCategoryId);
						List<RepositoryItem> fixedChildProducts = new ArrayList<RepositoryItem>();
						for (Map<String, Object> childProd : childProds) {
							RepositoryItem prod = productCatalog.getItem(getStringValue("product_id", childProd),
									LoaderConstants.PROD_ITEM_DESCR);
							fixedChildProducts.add(prod);
							Collection<RepositoryItem> parentCatgColl = getCatalogTools().getProductsCategories(prod,
									getCatalogRepository());
						}
						mutCatgItem.setPropertyValue("fixedChildProducts", fixedChildProducts);
						productCatalog.addItem(mutCatgItem);
					}
				}

			}
		} catch (RepositoryException e) {
			logError("Problem when creating new category with ID " + thisCategoryId);
			throw e;
		}
	}

	// Create all SKUs marked earmarked as new by exp_loader... add them to the
	// current Project
	private void createNewSkus() throws RepositoryException {
		// First, get a list of the new SKUs that we're creating
		List<Map<String, Object>> newSkus = getNewSkus();
		MutableRepository productCatalog = getCatalogRepository();

		// Now create them...
		String thisSkuId = null;
		HashSet<String> createdSkuIds = new HashSet<String>();
		try {
			if (newSkus != null) {
				for (Map<String, Object> newSku : newSkus) {
					thisSkuId = getStringValue("sku_id", newSku);
					if (!createdSkuIds.contains(thisSkuId)) {
						MutableRepositoryItem mutSkuItem = productCatalog.createItem(thisSkuId,
								LoaderConstants.SKU_ITEM_DESCR);
						setSkuAttributes(mutSkuItem, newSku);
						createdSkuIds.add(thisSkuId);
						productCatalog.addItem(mutSkuItem);
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when creating new sku with ID " + thisSkuId);
			throw e;
		}
	}

	/**
	 * Setting the sku attributes
	 *
	 * @param mutSkuRepItem
	 * @param sku
	 */
	private void setSkuAttributes(MutableRepositoryItem mutSkuRepItem, Map<String, Object> sku) {
		long start = System.currentTimeMillis();
		String skuId = getStringValue("sku_id", sku);
		// logDebug("How about noww!asdfasdfsdssAAA! swap is KNOTTTTTT
		// saasasdddddAAALLLLLNOT workingggggwwweewwqwqwdd......!!!! C'mon
		// baby!!!!!!!!!!!!");
		/*
		 * Iterator it = getSkuStringAttributeMap().entrySet().iterator(); while
		 * (it.hasNext()) { Map.Entry pair = (Map.Entry)it.next(); //
		 * System.out.println(pair.getKey() + " = " + pair.getValue()); //
		 * it.remove(); // avoids a ConcurrentModificationException }
		 */

		// mutSkuRepItem.setPropertyValue("displayName", getStringValue("name",
		// sku));

		Iterator it = getSkuStringAttributeMap().entrySet().iterator();
		String ptyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("SKU String Attribute - repoPtyName is : " +
			// pair.getKey() + " and SQL Column is : " + pair.getValue());
			ptyValue = getStringValue((String) pair.getValue(), sku);
			if (ptyValue != null) {
				mutSkuRepItem.setPropertyValue((String) pair.getKey(), ptyValue);
			}
		}

		/*
		 * it = getSkuIntegerAttributeMap().entrySet().iterator(); Integer
		 * intPtyValue=null; while (it.hasNext()) { Map.Entry pair =
		 * (Map.Entry)it.next(); //logDebug(
		 * "Integer Attribute - repoPtyName is : " + pair.getKey() +
		 * " and SQL Column is : " + pair.getValue()); intPtyValue =
		 * getIntegerValue((String)pair.getValue(), sku); if(intPtyValue !=
		 * null) { mutSkuRepItem.setPropertyValue((String)pair.getKey(),
		 * intPtyValue); } }
		 */

		it = getSkuBooleanAttributeMap().entrySet().iterator();
		Boolean boolPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Boolean Attribute - repoPtyName is : " + pair.getKey()
			// + " and SQL Column is : " + pair.getValue());
			boolPtyValue = getBooleanValue((String) pair.getValue(), sku);
			if (boolPtyValue != null) {
				mutSkuRepItem.setPropertyValue((String) pair.getKey(), boolPtyValue);
			}
		}

		it = getSkuDoubleAttributeMap().entrySet().iterator();
		Double dblPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Double Attribute - repoPtyName is : " + pair.getKey() +
			// " and SQL Column is : " + pair.getValue());
			dblPtyValue = getDoubleValue((String) pair.getValue(), sku);
			if (dblPtyValue != null) {
				mutSkuRepItem.setPropertyValue((String) pair.getKey(), dblPtyValue);
			}
		}

		List<Map<String, Object>> pickerLabels = getPickersForSku(skuId);
		// List<String> newPickerLabels = new ArrayList<String>();
		HashMap<String, String> skuPickers = new HashMap<String, String>();
		if (pickerLabels != null) {
			for (Map<String, Object> pickerLabel : pickerLabels) {
				// thisProdId = getStringValue("product_id", sellingPoint);
				skuPickers.put(getStringValue("seq_num", pickerLabel), getStringValue("picker_label", pickerLabel));
			}
			mutSkuRepItem.setPropertyValue("dynamicAttributes", skuPickers);
		}

		// Boolean onSale = getBooleanValue( "on_sale", sku);

		it = getSkuTimestampAttributeMap().entrySet().iterator();
		Timestamp tsPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			tsPtyValue = getTimestampValue((String) pair.getValue(), sku);
			if (tsPtyValue != null) {
				mutSkuRepItem.setPropertyValue((String) pair.getKey(), tsPtyValue);
			}
		}

		// setting the sku SizeName
		/*
		 * Object sizeName = getStringValue("size_name", sku); if (sizeName !=
		 * null) { mutSkuRepItem.setPropertyValue("SizeName", sizeName); }
		 *
		 * // setting the product displayPrice String displayPrice =
		 * getStringValue("display_price", sku); if (displayPrice != null &&
		 * !StringUtils.isEmpty(displayPrice)) {
		 * mutSkuRepItem.setPropertyValue("displayPrice", displayPrice); }
		 */

		long end = System.currentTimeMillis();
		this.XXX_setSKUAttributes_times += end - start;
		XXX_setSKUAttributes_number += 1;
	}

	private void updateCatChildCategories() throws RepositoryException {
		// get list of "new" categories that have to be updated
		// First, get a list of the new SKUs that we're creating
		// List<Map<String, Object>> newCatgs = getNewCategories();
		List<Map<String, Object>> newCatgs = findItemsByQuery(
				"select * from tmp_catg_data where catg_exists=0 or "
				+ "(catg_exists=1 and parent_catg_change=1 and parent_catg_is_new=0 and parent_has_new_products=1) or"
				+ "(catg_exists=1 and parent_catg_change=1 and parent_catg_is_new=1)");
		MutableRepository productCatalog = getCatalogRepository();

		// Now create them...
		String thisCategoryId = null;
		String parentCatgId = null;
		// HashSet<String> createdCategoryIds = new HashSet<String>();
		try {
			if (newCatgs != null) {
				for (Map<String, Object> newCatg : newCatgs) {
					thisCategoryId = getStringValue("category_id", newCatg);
					parentCatgId = getStringValue("parent_id", newCatg);
					// getIntegerValue("primary, item)
					// if ( !createdCategoryIds.contains(thisCategoryId)) {
					if (thisCategoryId != null && thisCategoryId.equalsIgnoreCase("00000")) {
						continue;
					}
					if (parentCatgId != null && parentCatgId.equalsIgnoreCase("rootCategory")) {
						parentCatgId = "00000";
					}
					MutableRepositoryItem parentCatItem = productCatalog.getItemForUpdate(parentCatgId,
							LoaderConstants.CAT_ITEM_DESCR);
					RepositoryItem childCatgItem = productCatalog.getItem(thisCategoryId,
							LoaderConstants.CAT_ITEM_DESCR);
					List<RepositoryItem> fixedCategories = (List<RepositoryItem>) parentCatItem
							.getPropertyValue("fixedChildCategories");
					if (fixedCategories != null && !fixedCategories.isEmpty()) {
						fixedCategories.add(childCatgItem);
						parentCatItem.setPropertyValue("fixedChildCategories", fixedCategories);
					} else {
						List<RepositoryItem> fixedChildCategories = new ArrayList<RepositoryItem>();
						// prodId = getStringValue("product_id", childProd);
						// RepositoryItem prod = productCatalog.getItem(prodId,
						// LoaderConstants.PROD_ITEM_DESCR);
						fixedChildCategories.add(childCatgItem);

						parentCatItem.setPropertyValue("fixedChildCategories", fixedChildCategories);
					}
					productCatalog.updateItem(parentCatItem);

					// }
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when creating new category with ID " + thisCategoryId);
			throw e;
		}
	}

	private void updateCatWithNewProducts() throws RepositoryException {
		List<Map<String, Object>> cats = getCategoriesForUpdate();
		MutableRepository productCatalog = getCatalogRepository();
		String prodId = null;

		if (cats != null && cats.size() > 0) {
			String catId = null;
			boolean primaryCatg = false;
			for (Map<String, Object> cat : cats) {
				MutableRepositoryItem catItem = null;
				try {
					catId = getStringValue("category_id", cat);
					catItem = (MutableRepositoryItem) productCatalog.getItemForUpdate(catId,
							LoaderConstants.CAT_ITEM_DESCR);
				} catch (RepositoryException e) {
					reportWarning("category", catId, null, "The Category " + catId + " could not be updated.");
					catItem = null;
				}
				if (catItem != null) {
					List<RepositoryItem> fixedProducts = (List<RepositoryItem>) catItem
							.getPropertyValue("fixedChildProducts");

					// First compile list of products to ADD
					List<Map<String, Object>> childProds = getNewChildProdsForCat(catId);

					// Compile list of products to remove
					List<Map<String, Object>> prodsToRemove = findItemsByQuery(
							"select product_id from tmp_cat_prod_rel_change where category_id='" + catId
									+ "' and action='DELETE'");
					List<String> prodIdsToRemove = new ArrayList<String>();
 					for (Map<String, Object> prodToRemove : prodsToRemove) {
 						prodIdsToRemove.add(getStringValue("product_id", prodToRemove));
 					}


					if (fixedProducts != null && !fixedProducts.isEmpty()) {

						// loop and add the new products
						for (Map<String, Object> childProd : childProds) {
							prodId = getStringValue("product_id", childProd);
							RepositoryItem prod = productCatalog.getItem(prodId, LoaderConstants.PROD_ITEM_DESCR);
							fixedProducts.add(prod);
							Collection<RepositoryItem> parentCatgColl = getCatalogTools().getProductsCategories(prod,
									getCatalogRepository());
						}

						List<RepositoryItem> prodItemsToRemove = new ArrayList<RepositoryItem>();
						for (RepositoryItem fixedProduct : fixedProducts) {
							if (prodIdsToRemove.contains("" + fixedProduct.getRepositoryId())) {
								prodItemsToRemove.add(fixedProduct);
							}
						}
						fixedProducts.removeAll(prodItemsToRemove);

					} else {
						List<RepositoryItem> fixedChildProducts = new ArrayList<RepositoryItem>();
						for (Map<String, Object> childProd : childProds) {
							prodId = getStringValue("product_id", childProd);
							RepositoryItem prod = productCatalog.getItem(prodId, LoaderConstants.PROD_ITEM_DESCR);
							fixedChildProducts.add(prod);
						}
						catItem.setPropertyValue("fixedChildProducts", fixedChildProducts);
					}
					//
					List<Map<String, Object>> catg = findItemsByQuery(
							"select * from tmp_catg_data where category_id='" + catId
									+ "' and catg_exists=1 and catg_change=1");
					if(catg.size() > 0) {
						setCategoryAttributes(catItem, catg.get(0));
					}
					// catItem here could be an old parent category
					// its old cat-child cat relationships may need to be
					// updated
					// since its already in the BCC, we make changes to its
					// fixedChildCategories property here

					// check if there are changes to this catg's
					// fixedChildCategories

					// select * from tmp_catg_data where
					// orig_parent_id=thisCatg.id and PARENT_CATG_CHANGE=1 and
					// ORIG_PARENT_HAS_NEW_PRODUCTS=1
					List<Map<String, Object>> removalChildCats = findItemsByQuery(
							"select category_id from tmp_catg_data where orig_parent_id='" + catId
									+ "' and parent_catg_change=1 and orig_parent_has_new_products=1");
					List<String> removalChildCatIds = new ArrayList<String>();
					for (Map<String, Object> removalChildCat : removalChildCats) {
						removalChildCatIds.add(getStringValue("category_id", removalChildCat));
					}
					if (removalChildCatIds.size() > 0) {
						// if(above query returns values) then do the below
						List<RepositoryItem> fixedChildCatgs = (List<RepositoryItem>) catItem
								.getPropertyValue("fixedChildCategories");
						List<RepositoryItem> childCatgsToRemove = new ArrayList<RepositoryItem>();
						for (RepositoryItem fixedChildCatg : fixedChildCatgs) {
							if (removalChildCatIds.contains("" + fixedChildCatg.getRepositoryId())) {
								childCatgsToRemove.add(fixedChildCatg);
							}
						}
						fixedChildCatgs.removeAll(childCatgsToRemove);
						// iterator
						catItem.setPropertyValue("fixedChildCategories", fixedChildCatgs);
						// logic to remove
						// iterate fixedCC
						// if item exists in listofitemsto be removed
						// listof items to be remove
						// select category_id from tmp_catg_data where
						// orig_parent_id=thisCatg.id and PARENT_CATG_CHANGE=1
						// and ORIG_PARENT_HAS_NEW_PRODUCTS=1
					}
					productCatalog.updateItem(catItem);
				}
			}
		}
	}

	// Create all products marked earmarked as new by exp_loader... add them to
	// the current Project
	private void createNewProducts() throws RepositoryException {
		// First, get a list of the new Products that we're creating
		long start = System.currentTimeMillis();
		List<Map<String, Object>> newProds = getNewProducts();
		MutableRepository productCatalog = getCatalogRepository();

		// Now create them...
		String thisProdId = null;
		HashSet<String> createdProdIds = new HashSet<String>();
		try {
			if (newProds != null) {
				for (Map<String, Object> newProd : newProds) {
					thisProdId = getStringValue("product_id", newProd);
					if (!createdProdIds.contains(thisProdId)) {
						MutableRepositoryItem mutProdItem = productCatalog.createItem(thisProdId,
								LoaderConstants.PROD_ITEM_DESCR);
						// HashMap<String, String> dynAttr = new
						// HashMap<String,String>();
						// dynAttr.put(key, value)
						// mutProdItem.setPropertyValue("dynamicAttributes",
						// dynAttr);
						setProductAttributes(mutProdItem, newProd);
						createdProdIds.add(thisProdId);

						// Updating the sku's
						List<String> skuIds = getSkuIdsForProduct(thisProdId); // Returns
																				// all
																				// active
																				// SKU
																				// ids
																				// for
																				// the
																				// given
																				// product
						ArrayList<RepositoryItem> childSkus = new ArrayList<RepositoryItem>();
						if (skuIds != null && skuIds.size() > 0) {
							for (String skuId : skuIds) {
								// Every SKU exists... no need to pre-check
								RepositoryItem sku = productCatalog.getItem(skuId, LoaderConstants.SKU_ITEM_DESCR);
								if (sku != null) {
									childSkus.add(sku);
								}
							}
						}
						mutProdItem.setPropertyValue(LoaderConstants.CHILD_SKUS, childSkus);

						RepositoryItem prodItem = productCatalog.addItem(mutProdItem);
						// Add the new product to categories, if we have any to
						// assign it to
						// List<Map<String, Object>> cats =
						// getCategoriesForProduct(thisProdId);
						/*
						 * List<Map<String,Object>> sellingPoints =
						 * getSellingPointsForProduct(prodId); List<String>
						 * newSellingPoints = new ArrayList<String>(); if (
						 * sellingPoints != null) { for ( Map<String,Object>
						 * sellingPoint : sellingPoints) { //thisProdId =
						 * getStringValue("product_id", sellingPoint);
						 * newSellingPoints.add(getStringValue("selling_point",
						 * sellingPoint)); } }
						 */
						// Block commented 10/8/16
						// To see if parentCatg update is a bottleneck
						/*
						 * if ( cats != null && cats.size() > 0) { String catId
						 * = null; boolean primaryCatg=false; for (
						 * Map<String,Object> cat : cats) {
						 * MutableRepositoryItem catItem = null; try { catId =
						 * getStringValue("category_id", cat); primaryCatg =
						 * getBooleanValue("primary_catg", cat); catItem =
						 * (MutableRepositoryItem)
						 * productCatalog.getItemForUpdate(catId,
						 * LoaderConstants.CAT_ITEM_DESCR); } catch (
						 * RepositoryException e) { reportWarning("category",
						 * catId, null, "The Product "+prodItem+
						 * " could not be added to this category because the category does not exist yet."
						 * ); catItem = null; } if ( catItem != null) {
						 * List<RepositoryItem> fixedProducts =
						 * (List<RepositoryItem>)
						 * catItem.getPropertyValue("fixedChildProducts"); if
						 * (fixedProducts != null && !fixedProducts.isEmpty()) {
						 * fixedProducts.add(prodItem); } else {
						 * List<RepositoryItem> fixedChildProducts = new
						 * ArrayList<RepositoryItem>();
						 * fixedChildProducts.add(prodItem);
						 * catItem.setPropertyValue("fixedChildProducts",
						 * fixedChildProducts); }
						 * productCatalog.updateItem(catItem); } } }
						 */
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when creating new Product with ID " + thisProdId);
			throw e;
		}

		long end = System.currentTimeMillis();
		XXX_createProductItem_times += end - start;
		XXX_createProductItem_number += createdProdIds.size();
	}

	private void addNewSkusToProducts() throws RepositoryException {
		// First, get a list of the Products that we're updating
		long start = System.currentTimeMillis();
		// List<Map<String,Object>> updProds = getProductsWithNewSkus();
		List<Map<String, Object>> parentProds = getParentProdsForNewSkus();
		// List<Map<String,Object>> newSkus = getNewSkus();
		MutableRepository productCatalog = getCatalogRepository();
		ChangeAwareList childSkus;

		// Now update them...
		String parentProdId = null;
		String newSkuId = null;
		try {
			if (parentProds != null) {
				for (Map<String, Object> parentProd : parentProds) {
					// Get the product and update it
					parentProdId = getStringValue("product_id", parentProd);
					// newSkuId = getStringValue("sku_id", newSku);
					MutableRepositoryItem mutProdItem = productCatalog.getItemForUpdate(parentProdId,
							LoaderConstants.PROD_ITEM_DESCR);
					if (mutProdItem != null) {
						// setProductAttributes( mutProdItem, updProd);
						List<String> skuIds = getSkuIdsForProduct(parentProdId); // Returns
																					// all
																					// active
																					// SKU
																					// ids
																					// for
																					// the
																					// given
																					// product
						// ChangeAwareList childSkus = new ChangeAwareList();
						childSkus = (ChangeAwareList) mutProdItem.getPropertyValue(LoaderConstants.CHILD_SKUS);

						if (skuIds != null && skuIds.size() > 0) {
							for (String skuId : skuIds) {
								// Every SKU exists... no need to pre-check
								RepositoryItem sku = productCatalog.getItem(skuId, LoaderConstants.SKU_ITEM_DESCR);
								if (sku != null) {
									childSkus.add(sku);
								}
							}
						}

						mutProdItem.setPropertyValue(LoaderConstants.CHILD_SKUS, childSkus);
						productCatalog.updateItem(mutProdItem);
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when updating Product with ID " + parentProdId + " with sku id " + newSkuId);
			throw e;
		}

		long end = System.currentTimeMillis();
		XXX_updateMainProductItem_times += end - start;
		XXX_updateMainProductItem_number += (parentProds == null ? 0 : parentProds.size());
	}

	private void updateProdWithNewSkus() throws RepositoryException {
		// First, get a list of the Products that we're updating
		long start = System.currentTimeMillis();
		List<Map<String, Object>> updProds = getProductsWithNewSkus();
		List<Map<String, Object>> prod = null;
		// List<Map<String,Object>> updProds = getChildSkusForProd(thisProdId);
		MutableRepository productCatalog = getCatalogRepository();

		// Now update them...
		String thisProdId = null;
		try {
			if (updProds != null) {
				for (Map<String, Object> updProd : updProds) {
					// Get the product and update it
					thisProdId = getStringValue("product_id", updProd);
					MutableRepositoryItem mutProdItem = productCatalog.getItemForUpdate(thisProdId,
							LoaderConstants.PROD_ITEM_DESCR);
					// check if the product data is in the feed
					// if there is data in the feed, then we update the product
					// attributes
					prod = getProduct(thisProdId);
					if (prod != null && prod.size() > 0) {
						setProductAttributes(mutProdItem, prod.get(0));
					}

					// List<Map<String,Object>> childSkus =
					// getChildSkusForProd(thisProdId);
					List<String> skuIds = getChildSkusForProd(thisProdId); // Returns
																			// all
																			// active
																			// SKU
																			// ids
																			// for
																			// the
																			// given
																			// product
					ArrayList<RepositoryItem> childSkus = new ArrayList<RepositoryItem>();
					if (skuIds != null && skuIds.size() > 0) {
						for (String skuId : skuIds) {
							// Every SKU exists... no need to pre-check
							RepositoryItem sku = productCatalog.getItem(skuId, LoaderConstants.SKU_ITEM_DESCR);
							if (sku != null) {
								childSkus.add(sku);
							}
						}
					}
					mutProdItem.setPropertyValue(LoaderConstants.CHILD_SKUS, childSkus);
					/*
					 *
					 * if (existingSkus != null && !existingSkus.isEmpty()) { if
					 * ( skuIds != null && skuIds.size() > 0) { for ( String
					 * skuId : skuIds) { // Every SKU exists... no need to
					 * pre-check RepositoryItem sku = productCatalog.getItem(
					 * skuId, LoaderConstants.SKU_ITEM_DESCR); if ( sku != null)
					 * { existingSkus.add(sku); } }
					 * mutProdItem.setPropertyValue(LoaderConstants.CHILD_SKUS,
					 * existingSkus); } } else { ArrayList<RepositoryItem>
					 * childSkus = new ArrayList<RepositoryItem>(); if ( skuIds
					 * != null && skuIds.size() > 0) { for ( String skuId :
					 * skuIds) { // Every SKU exists... no need to pre-check
					 * RepositoryItem sku = productCatalog.getItem( skuId,
					 * LoaderConstants.SKU_ITEM_DESCR); if ( sku != null) {
					 * childSkus.add(sku); } } }
					 * mutProdItem.setPropertyValue(LoaderConstants.CHILD_SKUS,
					 * childSkus); }
					 */

					if (mutProdItem != null) {
						productCatalog.updateItem(mutProdItem);
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when creating new Product with ID " + thisProdId);
			throw e;
		}

		long end = System.currentTimeMillis();
		XXX_updateMainProductItem_times += end - start;
		XXX_updateMainProductItem_number += (updProds == null ? 0 : updProds.size());
	}

	// Update any products that point to new SKUs... These updates can't be
	// performed via
	// SQL because the SKUs haven't hit cata and catfeed yet. This MUST be
	// called after createNewSkus()
	private void updateProductsWithNewSkus() throws RepositoryException {
		// First, get a list of the Products that we're updating
		long start = System.currentTimeMillis();
		List<Map<String, Object>> updProds = getProductsWithNewSkus();
		MutableRepository productCatalog = getCatalogRepository();

		// Now update them...
		String thisProdId = null;
		try {
			if (updProds != null) {
				for (Map<String, Object> updProd : updProds) {
					// Get the product and update it
					thisProdId = getStringValue("product_id", updProd);
					MutableRepositoryItem mutProdItem = productCatalog.getItemForUpdate(thisProdId,
							LoaderConstants.PROD_ITEM_DESCR);
					if (mutProdItem != null) {
						setProductAttributes(mutProdItem, updProd);
						productCatalog.updateItem(mutProdItem);
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Problem when creating new Product with ID " + thisProdId);
			throw e;
		}

		long end = System.currentTimeMillis();
		XXX_updateMainProductItem_times += end - start;
		XXX_updateMainProductItem_number += (updProds == null ? 0 : updProds.size());
	}

	// setCategoryAttributes
	/**
	 * Setting the category attributes
	 *
	 * @param mutCatgRepItem
	 * @param catg
	 */
	private void setCategoryAttributes(MutableRepositoryItem mutCatgRepItem, Map<String, Object> catg)
			throws RepositoryException {
		long start = System.currentTimeMillis();
		String catgId = getStringValue("category_id", catg);
		// try {
		// Set the displayName

		Iterator it = getCatgStringAttributeMap().entrySet().iterator();
		String ptyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("String Attribute - repoPtyName is : " + pair.getKey() +
			// " and SQL Column is : " + pair.getValue());
			ptyValue = getStringValue((String) pair.getValue(), catg);
			if (ptyValue != null) {
				mutCatgRepItem.setPropertyValue((String) pair.getKey(), ptyValue);
			}
		}

		it = getCatgIntegerAttributeMap().entrySet().iterator();
		Integer intPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Integer Attribute - repoPtyName is : " + pair.getKey()
			// + " and SQL Column is : " + pair.getValue());
			intPtyValue = getIntegerValue((String) pair.getValue(), catg);
			if (intPtyValue != null) {
				mutCatgRepItem.setPropertyValue((String) pair.getKey(), intPtyValue);
			}
		}

		it = getCatgTimestampAttributeMap().entrySet().iterator();
		Timestamp tsPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Timestamp Attribute - repoPtyName is : " +
			// pair.getKey() + " and SQL Column is : " + pair.getValue());
			tsPtyValue = getTimestampValue((String) pair.getValue(), catg);
			if (tsPtyValue != null) {
				mutCatgRepItem.setPropertyValue((String) pair.getKey(), tsPtyValue);
			}
		}

		/*
		 * // setting the product startDate Timestamp activate_date =
		 * getTimestampValue("activate_date", prod); if (activate_date != null)
		 * { mutProdRepItem.setPropertyValue("startDate", activate_date); }
		 */

		/*
		 * p.prod_id product_id, p.description description, p.selling_point_1
		 * selling_point_1, p.selling_point_2 selling_point_2, p.parent_category
		 * parent_category, p.weight_description weight_description,
		 * p.dimension_description dimension_description,
		 * to_date(p.activate_date,'YYYY-MM-DD') activate_date,
		 * p.no_of_alt_images no_of_alt_images,
		 *
		 */
		// setting the product deleteFlag
		/*
		 * Boolean deleteFlag = getBooleanValue("delete_flag", prod); if
		 * (deleteFlag != null) { mutProdRepItem.setPropertyValue("deleteFlag",
		 * deleteFlag); }
		 */

		/*
		 * // Setting Product Template String templateId =
		 * getStringValue("template", prod); Repository productCatalog =
		 * getCatalogRepository(); RepositoryItem prodTemplate =
		 * productCatalog.getItem(templateId, "media"); if ( prodTemplate ==
		 * null) { logDebug("prodTemplate is null in create"); } else {
		 * logDebug("prodTemplate is " + prodTemplate.getRepositoryId());
		 * mutProdRepItem.setPropertyValue("template", prodTemplate); }
		 */
		/*
		 * // Updating the sku's List<String> skuIds =
		 * getSkuIdsForProduct(prodId); // Returns all active SKU ids for the
		 * given product ArrayList<RepositoryItem> childSkus = new
		 * ArrayList<RepositoryItem>(); if ( skuIds != null && skuIds.size() >
		 * 0) { for ( String skuId : skuIds) { // Every SKU exists... no need to
		 * pre-check RepositoryItem sku = productCatalog.getItem( skuId,
		 * LoaderConstants.SKU_ITEM_DESCR); if ( sku != null) {
		 * childSkus.add(sku); } } }
		 * mutProdRepItem.setPropertyValue(LoaderConstants.CHILD_SKUS,
		 * childSkus);
		 */
		// }
		/*
		 * catch (RepositoryException e) { logError(
		 * "Unable to set/update product having id : " + prodId, e); throw e; }
		 */

		long end = System.currentTimeMillis();
		XXX_setProductAttributes_times += end - start;
		XXX_setProductAttributes_number += 1;
	}

	/**
	 * Setting the product attributes
	 *
	 * @param mutProdRepItem
	 * @param prod
	 */
	private void setProductAttributes(MutableRepositoryItem mutProdRepItem, Map<String, Object> prod)
			throws RepositoryException {
		long start = System.currentTimeMillis();
		String prodId = getStringValue("product_id", prod);
		// try {
		// Set the displayName

		Iterator it = getProdStringAttributeMap().entrySet().iterator();
		String ptyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("String Attribute - repoPtyName is : " + pair.getKey() +
			// " and SQL Column is : " + pair.getValue());
			ptyValue = getStringValue((String) pair.getValue(), prod);
			if (ptyValue != null) {
				mutProdRepItem.setPropertyValue((String) pair.getKey(), ptyValue);
			}
		}

		it = getProdIntegerAttributeMap().entrySet().iterator();
		Integer intPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Integer Attribute - repoPtyName is : " + pair.getKey()
			// + " and SQL Column is : " + pair.getValue());
			intPtyValue = getIntegerValue((String) pair.getValue(), prod);
			if (intPtyValue != null) {
				mutProdRepItem.setPropertyValue((String) pair.getKey(), intPtyValue);
			}
		}

		it = getProdTimestampAttributeMap().entrySet().iterator();
		Timestamp tsPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Timestamp Attribute - repoPtyName is : " +
			// pair.getKey() + " and SQL Column is : " + pair.getValue());
			tsPtyValue = getTimestampValue((String) pair.getValue(), prod);
			if (tsPtyValue != null) {
				mutProdRepItem.setPropertyValue((String) pair.getKey(), tsPtyValue);
			}
		}

		it = getProdBooleanAttributeMap().entrySet().iterator();
		Boolean boolPtyValue = null;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// logDebug("Boolean Attribute - repoPtyName is : " + pair.getKey()
			// + " and SQL Column is : " + pair.getValue());
			boolPtyValue = getBooleanValue((String) pair.getValue(), prod);
			if (boolPtyValue != null) {
				mutProdRepItem.setPropertyValue((String) pair.getKey(), boolPtyValue);
			}
		}

		/*
		 * // setting the product startDate Timestamp activate_date =
		 * getTimestampValue("activate_date", prod); if (activate_date != null)
		 * { mutProdRepItem.setPropertyValue("startDate", activate_date); }
		 */

		/*
		 * p.prod_id product_id, p.description description, p.selling_point_1
		 * selling_point_1, p.selling_point_2 selling_point_2, p.parent_category
		 * parent_category, p.weight_description weight_description,
		 * p.dimension_description dimension_description,
		 * to_date(p.activate_date,'YYYY-MM-DD') activate_date,
		 * p.no_of_alt_images no_of_alt_images,
		 *
		 */
		// setting the product deleteFlag
		/*
		 * Boolean deleteFlag = getBooleanValue("delete_flag", prod); if
		 * (deleteFlag != null) { mutProdRepItem.setPropertyValue("deleteFlag",
		 * deleteFlag); }
		 */

		/*
		 * // Setting Product Template String templateId =
		 * getStringValue("template", prod); Repository productCatalog =
		 * getCatalogRepository(); RepositoryItem prodTemplate =
		 * productCatalog.getItem(templateId, "media"); if ( prodTemplate ==
		 * null) { logDebug("prodTemplate is null in create"); } else {
		 * logDebug("prodTemplate is " + prodTemplate.getRepositoryId());
		 * mutProdRepItem.setPropertyValue("template", prodTemplate); }
		 */
		// Repository productCatalog = getCatalogRepository();
		// Updating the sku's
		/*
		 * List<String> skuIds = getSkuIdsForProduct(prodId); // Returns all
		 * active SKU ids for the given product ArrayList<RepositoryItem>
		 * childSkus = new ArrayList<RepositoryItem>(); if ( skuIds != null &&
		 * skuIds.size() > 0) { for ( String skuId : skuIds) { RepositoryItem
		 * sku = productCatalog.getItem( skuId, LoaderConstants.SKU_ITEM_DESCR);
		 * if ( sku != null) { childSkus.add(sku); } } }
		 * mutProdRepItem.setPropertyValue(LoaderConstants.CHILD_SKUS,
		 * childSkus);
		 */

		/*
		 * List<Map<String,Object>> sellingPoints =
		 * getSellingPointsForProduct(prodId); List<String> newSellingPoints =
		 * new ArrayList<String>(); if ( sellingPoints != null) { for (
		 * Map<String,Object> sellingPoint : sellingPoints) { //thisProdId =
		 * getStringValue("product_id", sellingPoint);
		 * newSellingPoints.add(getStringValue("selling_point", sellingPoint));
		 * } } if(newSellingPoints != null && newSellingPoints.size() > 0) {
		 * String[] arrSellingPoints = new String[newSellingPoints.size()];
		 * newSellingPoints.toArray(arrSellingPoints);
		 * mutProdRepItem.setPropertyValue("sellingPoints", arrSellingPoints); }
		 */
		List<Map<String, Object>> pickerLabels = getPickersForProduct(prodId);
		// List<String> newPickerLabels = new ArrayList<String>();
		HashMap<String, String> prodPickers = new HashMap<String, String>();
		if (pickerLabels != null) {
			for (Map<String, Object> pickerLabel : pickerLabels) {
				// thisProdId = getStringValue("product_id", sellingPoint);
				prodPickers.put(getStringValue("seq_num", pickerLabel), getStringValue("picker_label", pickerLabel));
			}
			mutProdRepItem.setPropertyValue("dynamicAttributes", prodPickers);
		}
		/*
		 * if(newPickerLabels != null && newPickerLabels.size() > 0) { String[]
		 * arrPickerLabels = new String[newPickerLabels.size()];
		 * newPickerLabels.toArray(arrPickerLabels);
		 * mutProdRepItem.setPropertyValue("pickerLabels", arrPickerLabels); }
		 */
		// }
		/*
		 * catch (RepositoryException e) { logError(
		 * "Unable to set/update product having id : " + prodId, e); throw e; }
		 */

		long end = System.currentTimeMillis();
		XXX_setProductAttributes_times += end - start;
		XXX_setProductAttributes_number += 1;
	}

	// Create all missing Prices within the current Project
	private void createNewPrices() throws RepositoryException {
		// Start by creating the list prices we need to create
		long start = System.currentTimeMillis();
		List<Map<String, Object>> newListPrices = getNewListPrices();
		createPricesInList("ExpressUSListPrices", newListPrices);

		// Then by creating the list prices we need to create
		List<Map<String, Object>> newSalePrices = getNewSalePrices();
		createPricesInList("ExpressUSSalePrices", newSalePrices);

		long end = System.currentTimeMillis();
		this.XXX_createUpdatePriceList_times += end - start;
		XXX_createUpdatePriceList_number += (newListPrices == null ? 0 : newListPrices.size())
				+ (newSalePrices == null ? 0 : newSalePrices.size());
	}

	// Create prices in the price list with the given ID
	private void createPricesInList(String priceListId, List<Map<String, Object>> prices) throws RepositoryException {
		if (prices == null || prices.size() == 0)
			return;

		String curSkuId = null;
		PriceListManager priceListMgr = getPriceListManager();
		MutableRepository priceListRep = priceListMgr.getPriceListRepository();
		try {
			// Get the price list, for starters
			RepositoryItem priceList = getPriceListManager().getPriceListRepository().getItem(priceListId, "priceList");

			// Now, go through and create the prices
			for (Map<String, Object> price : prices) {
				curSkuId = getStringValue("sku_id", price);
				Double skuPrice = getDoubleValue("price", price);
				MutableRepositoryItem priceItem = (MutableRepositoryItem) priceListMgr.createPrice(priceList,
						priceListMgr.getListPricePropertyName(), null, curSkuId);
				priceItem.setPropertyValue(priceListMgr.getListPricePropertyName(), skuPrice);
				priceItem.setPropertyValue(priceListMgr.getPriceListPropertyName(), priceList);
				priceListRep.addItem(priceItem);
			}
		} catch (RepositoryException e) {
			logError("There was a problem creating a price in price list " + priceListId + " for SKU " + curSkuId);
			throw e;
		} catch (PriceListException e) {
			logError("There was a problem creating a price in price list " + priceListId + " for SKU " + curSkuId);
			throw new RepositoryException(e);
		}
	}

	/**
	 * price list manager
	 */
	private PriceListManager priceListManager;

	/**
	 * @return the priceListManager
	 */
	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	/**
	 * @param priceListManager
	 *            the priceListManager to set
	 */
	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	// ==============================
	// Methods for deployment and cache invalidation
	// ==============================

	private void deployChanges() throws SQLException {
		// Sync changes out to target schemas in the order established by
		// "setupDeploySchemas()"
		// If anything goes wrong, the entire transaction will be rolled back

		// Now call the SP to deploy everywhere
		if (isLoggingDebug())
			logDebug("Calling SP to deploy all updates to targets!!!");
		try {
			// Make sure we disable auto-commit for this call. We don't want a
			// half-baked deployment.
			execProcedure("mff_catalog_loader.copy_tmp_to_all", SCHEMA_CATFEED, false);
		} catch (SQLException e) {
			logError("There was a problem deploying SQL updates in Oracle", e);
			throw e;
		}

	}

	private void setupDeploySchemas() throws SQLException {
		// Install the list of schema names to deploy to into the DB in the
		// correct order
		Connection conn = getConnection(SCHEMA_CATFEED);
		int index = 1;
		conn.setAutoCommit(false);
		if (mPubSchemaName != null) {
			installSchemaName(conn, "pub", mPubSchemaName, index, true);
			index++;
		}
		if (mCataStagingSchemaName != null) {
			installSchemaName(conn, "catastg", mCataStagingSchemaName, index, false);
			index++;
		}
		if (mCatbStagingSchemaName != null) {
			installSchemaName(conn, "catbstg", mCatbStagingSchemaName, index, false);
			index++;
		}
		if (mCataSchemaName != null) {
			installSchemaName(conn, "cata", mCataSchemaName, index, false);
			index++;
		}
		if (mCatbSchemaName != null) {
			installSchemaName(conn, "catb", mCatbSchemaName, index, false);
			index++;
		}
		conn.commit();
		closeConnection(conn);
	}

	private void installSchemaName(Connection conn, String id, String name, int index, boolean versioned)
			throws SQLException {
		Statement stmt = null;
		try {
			String sql = "insert into tmp_deploy_to_schemas values('" + id + "','" + name + "'," + index + ","
					+ (versioned ? 1 : 0) + ")";
			if (isLoggingDebug())
				logDebug("Installing schema name via SQL: " + sql);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			logError("Couldn't insert schema name", e);
			throw e;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				throw e;
			}
		}
	}

	// This method incrementally invalidates the list of assets changed via
	// direct SQL updates of the cata/b schemas
	// We use our home-grown TargetCacheInvalidator, which sends the
	// invalidation list via RMI to every
	// JBoss agent listed for the Production and Staging deployment targets
	private void invalidateCaches() {
		// First, generate a list of all asset URIs to invalidate
		ArrayList<String> uris = new ArrayList<String>();

		// Add SKUs
		List<Map<String, Object>> skus = getChangedSkus();
		for (Map<String, Object> sku : skus) {
			uris.add("atgrep:/ProductCatalog/sku/" + getStringValue("sku_id", sku));
		}
		logDebug("Adding " + (skus == null ? 0 : skus.size()) + " SKUs to invalidation list");

		// Add Products
				List<Map<String, Object>> prods = getChangedProducts();
		for (Map<String, Object> prod : prods) {
			uris.add("atgrep:/ProductCatalog/product/" + getStringValue("product_id", prod));
		}
		logDebug("Adding " + (prods == null ? 0 : prods.size()) + " products to invalidation list");

		// Add Categories
		List<Map<String, Object>> cats = getChangedCategories();
		for (Map<String, Object> cat : cats) {
			uris.add("atgrep:/ProductCatalog/category/" + getStringValue("category_id", cat));
		}
		logDebug("Adding " + (cats == null ? 0 : cats.size()) + " categories to invalidation list");

		prods = getChangedOriginalProducts();
		for (Map<String, Object> prod : prods) {
			uris.add("atgrep:/ProductCatalog/product/" + getStringValue("orig_product", prod));
			uris.add("atgrep:/ProductCatalog/product/" + getStringValue("new_product", prod));
		}
		logDebug("Adding " + (prods == null ? 0 : prods.size()) + " non-clearance products to invalidation list");

		cats = getCategoriesWithRemovedProducts();
		for (Map<String, Object> cat : cats) {
			uris.add("atgrep:/ProductCatalog/category/" + getStringValue("category_id", cat));
		}
		logDebug("Adding " + (cats == null ? 0 : cats.size()) + " categories to invalidation list");

		cats = getChangedCatChildCats();
		for (Map<String, Object> cat : cats) {
			uris.add("atgrep:/ProductCatalog/category/" + getStringValue("category_id", cat));

		}
		logDebug("Adding " + (cats == null ? 0 : cats.size()) + " categories to invalidation list");

		// Now, invalidate each target
		if (uris.size() == 0) {
			logDebug("No assets to invalidate");
			return;
		}

		logDebug("There are " + uris.size() + " assets to invalidate");
		if (mCataSchemaName != null || mCatbSchemaName != null) {
			// Invalidate production
			logDebug("Invalidating against target " + getProductionTargetName());
			getCacheInvalidator().invalidateItemsForTargetName(getProductionTargetName(),uris);
		}
		if (mCataStagingSchemaName != null || mCatbStagingSchemaName != null) {
			// Invalidate production
			logDebug("Invalidating against target " + getStagingTargetName());
			getCacheInvalidator().invalidateItemsForTargetName(getStagingTargetName(),uris);
		}
		if (mPubSchemaName != null) {
			// Invalidate the versioned catalog and price lists... full
			// invalidation is fine for these
			logDebug("Invalidating versioned Catalog caches");
			getCatalogRepository().invalidateCaches();
		}
	}


    private TargetCacheInvalidatorHelper cacheInvalidator = null;

    public TargetCacheInvalidatorHelper getCacheInvalidator() {
        return cacheInvalidator;
    }

    public void setCacheInvalidator(TargetCacheInvalidatorHelper cacheInvalidator) {
        this.cacheInvalidator = cacheInvalidator;
    }



	public String getProductionTargetName() {
		return mProductionTargetName;
	}

	public void setProductionTargetName(String pProductionTargetName) {
		this.mProductionTargetName = pProductionTargetName;
	}

	public String getStagingTargetName() {
		return mStagingTargetName;
	}

	public void setStagingTargetName(String pStagingTargetName) {
		this.mStagingTargetName = pStagingTargetName;
	}

	// ============================
	// Stored procedure helpers
	// ============================

	public String getCataSchemaName() {
		return mCataSchemaName;
	}

	public void setCataSchemaName(String pCataSchemaName) {
		this.mCataSchemaName = pCataSchemaName;
	}

	public String getCatbSchemaName() {
		return mCatbSchemaName;
	}

	public void setCatbSchemaName(String pCatbSchemaName) {
		this.mCatbSchemaName = pCatbSchemaName;
	}

	public String getCataStagingSchemaName() {
		return mCataStagingSchemaName;
	}

	public void setCataStagingSchemaName(String pCataStagingSchemaName) {
		this.mCataStagingSchemaName = pCataStagingSchemaName;
	}

	public String getCatbStagingSchemaName() {
		return mCatbStagingSchemaName;
	}

	public void setCatbStagingSchemaName(String pCatbStagingSchemaName) {
		this.mCatbStagingSchemaName = pCatbStagingSchemaName;
	}

	public String getPubSchemaName() {
		return mPubSchemaName;
	}

	public void setPubSchemaName(String pPubSchemaName) {
		this.mPubSchemaName = pPubSchemaName;
	}

	public DataSource getDataSourceCata() {
		return mDataSourceCata;
	}

	public void setDataSourceCata(DataSource pDataSourceCata) {
		this.mDataSourceCata = pDataSourceCata;
	}

	public DataSource getDataSourceCatb() {
		return mDataSourceCatb;
	}

	public void setDataSourceCatb(DataSource pDataSourceCatb) {
		this.mDataSourceCatb = pDataSourceCatb;
	}

	public DataSource getDataSourceCataStaging() {
		return mDataSourceCataStaging;
	}

	public void setDataSourceCataStaging(DataSource pDataSourceCataStaging) {
		this.mDataSourceCataStaging = pDataSourceCataStaging;
	}

	public DataSource getDataSourceCatbStaging() {
		return mDataSourceCatbStaging;
	}

	public void setDataSourceCatbStaging(DataSource pDataSourceCatbStaging) {
		this.mDataSourceCatbStaging = pDataSourceCatbStaging;
	}

	public DataSource getDataSourcePub() {
		return mDataSourcePub;
	}

	public void setDataSourcePub(DataSource pDataSourcePub) {
		this.mDataSourcePub = pDataSourcePub;
	}

	public DataSource getDataSourceCatfeed() {
		return mDataSourceCatfeed;
	}

	public void setDataSourceCatfeed(DataSource pDataSourceCatfeed) {
		this.mDataSourceCatfeed = pDataSourceCatfeed;
	}

	/**
	 * Returns a JDBC connection from the data source.
	 *
	 * @return JDBC connection.
	 */
	private java.sql.Connection getConnection(int pSchema) {
		try {
			switch (pSchema) {
			case SCHEMA_CATA:
				return mDataSourceCata.getConnection();
			case SCHEMA_CATB:
				return mDataSourceCatb.getConnection();
			case SCHEMA_CATA_STAGING:
				return mDataSourceCataStaging.getConnection();
			case SCHEMA_CATB_STAGING:
				return mDataSourceCatbStaging.getConnection();
			case SCHEMA_PUB:
				return mDataSourcePub.getConnection();
			case SCHEMA_CATFEED:
				return mDataSourceCatfeed.getConnection();
			}
		} catch (SQLException sqle) {
			if (isLoggingError())
				logError(sqle);
			return null;
		}
		return null;
	}

	/**
	 * call procedure with one optional parameters
	 *
	 * @param pName
	 *            the procedure to call
	 * @param pSchema
	 *            schema identifier
	 * @param pParam
	 *            the optional parameter
	 */
	private void execProcedure(String pName, int pSchema) throws SQLException {
		execProcedure(pName, pSchema, true, null);
	}

	private void execProcedure(String pName, int pSchema, boolean autoCommit) throws SQLException {
		execProcedure(pName, pSchema, autoCommit, null);
	}

	private void execProcedure(String pName, int pSchema, String pParam) throws SQLException {
		execProcedure(pName, pSchema, true, pParam);
	}

	private void execProcedure(String pName, int pSchema, boolean autoCommit, String pParam) throws SQLException {
		if (isLoggingDebug()) {
			logDebug("execProcedure is called " + pName);
		}

		Connection conn = getConnection(pSchema);
		conn.setAutoCommit(autoCommit);
		CallableStatement cs;
		String call = getCallString(pName, pParam);
		if (isLoggingInfo()) {
			logInfo("about to call " + call);
		}

		try {
			// Call a procedure with output parameters
			cs = conn.prepareCall(call);
			if (pParam != null) {
				cs.setString(1, pParam);
			}
			boolean retValue = cs.execute();
			if (!autoCommit)
				conn.commit();
			if (isLoggingDebug()) {
				logDebug("execute returned " + retValue);
			}
		} catch (SQLException e) {
			if (isLoggingError())
				logError("Exception during SP call... rolling back: " + e.getErrorCode() + e.getMessage(), e);
			try {
				conn.rollback();
				throw e;
			} catch (SQLException e2) {
				if (isLoggingError())
					logError("Exception during SP rollback: " + e2.getErrorCode() + e2.getMessage(), e2);
				throw e2;
			}
		} catch (Exception e) {
			if (isLoggingError())
				logError("Exception during SP call... rolling back: " + e.getMessage(), e);
			try {
				conn.rollback();
				throw new SQLException(e);
			} catch (SQLException e2) {
				if (isLoggingError())
					logError("Exception during SP rollback: " + e2.getErrorCode() + e2.getMessage(), e2);
				throw e2;
			}
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * Closes the given JDBC connection.
	 *
	 * @param pConnection
	 *            JDBC connection to close.
	 */
	private void closeConnection(java.sql.Connection pConnection) {
		try {
			if (pConnection != null)
				pConnection.close();
		} catch (SQLException sqle) {
			if (isLoggingError())
				logError(sqle);
		}
	}

	/**
	 * @param pName
	 * @param pParam
	 * @return
	 */
	private String getCallString(String pName, String pParam) {
		StringBuffer buf = new StringBuffer(CALL_STRING_START + pName + (pParam == null ? "()" : "(?)"));
		buf.append(CALL_STRING_END);
		return buf.toString();
	}

	public void displayInfo() {

		vlogInfo("==createProductItem==:");
		vlogInfo("==time==:" + XXX_createProductItem_times);
		vlogInfo("==number==:" + XXX_createProductItem_number);

		vlogInfo("==setSKUAttributes_times==:");
		vlogInfo("==time==:" + XXX_setSKUAttributes_times);
		vlogInfo("==number==:" + XXX_setSKUAttributes_number);

		vlogInfo("==createUpdatePriceList==:");
		vlogInfo("==time==:" + XXX_createUpdatePriceList_times);
		vlogInfo("==number==:" + XXX_createUpdatePriceList_number);

		vlogInfo("==setProductAttributes==:");
		vlogInfo("==time==:" + XXX_setProductAttributes_times);
		vlogInfo("==number==:" + XXX_setProductAttributes_number);

		vlogInfo("==updateMainProductItem==:");
		vlogInfo("==time==:" + XXX_updateMainProductItem_times);
		vlogInfo("==number==:" + XXX_updateMainProductItem_number);
	}

	// Time calculation
	public void resetTimings() {
		XXX_createProductItem_times = 0;
		XXX_createProductItem_number = 0;

		XXX_setSKUAttributes_times = 0;
		XXX_setSKUAttributes_number = 0;

		XXX_createUpdatePriceList_times = 0;
		XXX_createUpdatePriceList_number = 0;

		XXX_setProductAttributes_times = 0;
		XXX_setProductAttributes_number = 0;

		XXX_updateMainProductItem_times = 0;
		XXX_updateMainProductItem_number = 0;
	}

	private long XXX_createProductItem_times = 0;
	private long XXX_createProductItem_number = 0;

	private long XXX_setSKUAttributes_times = 0;
	private long XXX_setSKUAttributes_number = 0;

	private long XXX_createUpdatePriceList_times = 0;
	private long XXX_createUpdatePriceList_number = 0;

	private long XXX_setProductAttributes_times = 0;
	private long XXX_setProductAttributes_number = 0;

	private long XXX_updateMainProductItem_times = 0;
	private long XXX_updateMainProductItem_number = 0;

	@Override
	public String getErrorCode() {
		return null;
	}

	@Override
	public String getBCCDeploymentErrorCode() {
		return null;
	}

}
