package com.googleadwords.scheduler;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;



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
public class GoogleStoreInvFeedCreator extends GenericService {
	
	
  /* Return Codes */
	public static final int		SUCCESS				= 0;
	public static final int		WARN				= 1;
	public static final int		ERROR				= 2;
	
	protected static final String FOLDER_SEPERATOR = "/";
	
	private static final String STORE_CODE="STORE_CODE";
	private static final String ITEMID="ITEM_ID";
	private static final String PRICE="PRICE";
	private static final String QUANTITY="QUANTITY";
	private static final String QUANTITY_PAR="QUANTITY_PAR";
	private static final String PICKUP_METHOD="PICKUP_METHOD";
	private static final String PICKUP_SLA="PICKUP_SLA";
	
	
	
	
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
	
	private String	runServer;

	private String onSaleTableName;
	
	private String regularTableName;
	
	private String taskName;
	
	private String mFeedVersion;
	
	private String exceptionMsg;
	
	private String mOnSaleQuery;
	
	private String mRegularQuery;
	
	private String mStoreCodeColumnName;
	
	private String mItemIdColumnName;
	
	private String mPriceColumnName;
	
	private String mQuantityColumnName;
	
	private String mPickupMethodColumnName;
	
	private String mPickupSlaColumnName;
	
	private String mPickupTemplateUrlColumnName;
	
	private boolean mPartialFeed;
	
	private String mProcessedCountOnSale;
	
	private String mProcessedCountRegular;
	
	private String mRecompileOraclePackage;
	
	private boolean mMergeInvParwithFull;
	
	private String mMergeInvStatement;
	
	private boolean mCollectData;
	
	public boolean isCollectData() {
		return mCollectData;
	}

	public void setCollectData(boolean pCollectData) {
		mCollectData = pCollectData;
	}

	public String getMergeInvStatement() {
		return mMergeInvStatement;
	}

	public void setMergeInvStatement(String pMergeInvStatement) {
		mMergeInvStatement = pMergeInvStatement;
	}

	public boolean isMergeInvParwithFull() {
		return mMergeInvParwithFull;
	}

	public void setMergeInvParwithFull(boolean pMergeInvParwithFull) {
		mMergeInvParwithFull = pMergeInvParwithFull;
	}

	public String getRecompileOraclePackage() {
		return mRecompileOraclePackage;
	}

	public void setRecompileOraclePackage(String pRecompileOraclePackage) {
		mRecompileOraclePackage = pRecompileOraclePackage;
	}

	public String getProcessedCountOnSale() {
		return mProcessedCountOnSale;
	}

	public void setProcessedCountOnSale(String pProcessedCountOnSale) {
		mProcessedCountOnSale = pProcessedCountOnSale;
	}

	public String getProcessedCountRegular() {
		return mProcessedCountRegular;
	}

	public void setProcessedCountRegular(String pProcessedCountRegular) {
		mProcessedCountRegular = pProcessedCountRegular;
	}

	public String getStoreCodeColumnName() {
		return mStoreCodeColumnName;
	}

	public void setStoreCodeColumnName(String pStoreCodeColumnName) {
		mStoreCodeColumnName = pStoreCodeColumnName;
	}

	public String getItemIdColumnName() {
		return mItemIdColumnName;
	}

	public void setItemIdColumnName(String pItemIdColumnName) {
		mItemIdColumnName = pItemIdColumnName;
	}

	public String getPriceColumnName() {
		return mPriceColumnName;
	}

	public void setPriceColumnName(String pPriceColumnName) {
		mPriceColumnName = pPriceColumnName;
	}

	public String getQuantityColumnName() {
		return mQuantityColumnName;
	}

	public void setQuantityColumnName(String pQuantityColumnName) {
		mQuantityColumnName = pQuantityColumnName;
	}

	public String getPickupMethodColumnName() {
		return mPickupMethodColumnName;
	}

	public void setPickupMethodColumnName(String pPickupMethodColumnName) {
		mPickupMethodColumnName = pPickupMethodColumnName;
	}

	public String getPickupSlaColumnName() {
		return mPickupSlaColumnName;
	}

	public void setPickupSlaColumnName(String pPickupSlaColumnName) {
		mPickupSlaColumnName = pPickupSlaColumnName;
	}

	public String getPickupTemplateUrlColumnName() {
		return mPickupTemplateUrlColumnName;
	}

	public void setPickupTemplateUrlColumnName(String pPickupTemplateUrlColumnName) {
		mPickupTemplateUrlColumnName = pPickupTemplateUrlColumnName;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String pLocalDir) {
		localDir = pLocalDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String pFileName) {
		fileName = pFileName;
	}

	public String getStoredProcName() {
		return storedProcName;
	}

	public void setStoredProcName(String pStoredProcName) {
		storedProcName = pStoredProcName;
	}

	public DataSource getFeedDataSource() {
		return feedDataSource;
	}

	public void setFeedDataSource(DataSource pFeedDataSource) {
		feedDataSource = pFeedDataSource;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection pConn) {
		conn = pConn;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int pQueryTimeout) {
		queryTimeout = pQueryTimeout;
	}

	public int getJobReturnCode() {
		return jobReturnCode;
	}

	public void setJobReturnCode(int pJobReturnCode) {
		jobReturnCode = pJobReturnCode;
	}

	public boolean isEnable() {
		return mEnable;
	}

	public void setEnable(boolean pEnable) {
		mEnable = pEnable;
	}

	public String getRunServer() {
		return runServer;
	}

	public void setRunServer(String pRunServer) {
		runServer = pRunServer;
	}

	public String getOnSaleTableName() {
		return onSaleTableName;
	}

	public void setOnSaleTableName(String pOnSaleTableName) {
		onSaleTableName = pOnSaleTableName;
	}

	public String getRegularTableName() {
		return regularTableName;
	}

	public void setRegularTableName(String pRegularTableName) {
		regularTableName = pRegularTableName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String pTaskName) {
		taskName = pTaskName;
	}

	public String getFeedVersion() {
		return mFeedVersion;
	}

	public void setFeedVersion(String pFeedVersion) {
		mFeedVersion = pFeedVersion;
	}
	
	public String getOnSaleQuery() {
		return mOnSaleQuery;
	}
	
	public void setOnSaleQuery(String pOnSaleQuery) {
		 this.mOnSaleQuery=pOnSaleQuery;
	}
	
	public String getRegularQuery() {
		return mRegularQuery;
	}
	
	public void setRegularQuery(String pRegularQuery) {
		 this.mRegularQuery=pRegularQuery;
	}
	
	public boolean isPartialFeed(){
		return mPartialFeed;
	}
	
	public void setPartialFeed(boolean pPartialFeed){
		mPartialFeed=pPartialFeed;
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
		
		public CallableStatement getRecompileStmt() throws SQLException {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " creating callable statement - "
						+ "proc name is " + getRecompileOraclePackage());
			}
			
			CallableStatement cs;
			cs = conn.prepareCall(getRecompileOraclePackage());
			return cs;
		}
		
		/*
		 * Constructs callableStatement for creating feeds
		 */
		public CallableStatement getCallableStmt(String pStoreProc) throws SQLException {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " creating callable statement - "
						+ "proc name is " + pStoreProc);
			}
			
			CallableStatement cs;
			cs = conn.prepareCall(pStoreProc);
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
			vlogInfo("Start: GoogleStoreInvFeedCreator: doTask()");
			if(isEnable()){
				vlogDebug("Google Feed Creator task is enabled");
				performtask();
			}
			else {
				vlogInfo("Google Feed Creator task is not enabled");
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: doTask()");
	
	
		}
		

	
	  private void performtask() {
		  setJobReturnCode(SUCCESS);
		  vlogInfo("Start: GoogleStoreInvFeedCreator: performtask()");
		  
		  //Invoke the configured stored proc
		  if(isCollectData()){
			  collectData(getStoredProcName());
		  }
		  // If success create onsale and regular product feed
		  if(getJobReturnCode() ==SUCCESS) {
			  createCsvFeed(getLocalDir(),getFileName(),isPartialFeed());
		  }
		  if(isMergeInvParwithFull()){
			  collectData(getMergeInvStatement());
		  }
		  vlogInfo("End: GoogleStoreInvFeedCreator: performtask()");
	  }
		public void createHeaderCSV(String dir,String fileName,boolean isPartial){
			 vlogInfo("Start: GoogleStoreInvFeedCreator: createHeaderCSV()");
			try{
				String filePath=dir+FOLDER_SEPERATOR+fileName;
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
				createHeaderForFeed(file);
			}
			catch(Exception ex){
				if(isLoggingError())
					logError(ex);
				setJobReturnCode(ERROR);
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: createHeaderCSV()");
		}
		public void createCsvFeed(String dir,String fileName,boolean isPartial){
			vlogInfo("Start: GoogleStoreInvFeedCreator: createCsvFeed()");
			try {
				String filePath=dir+FOLDER_SEPERATOR+fileName;
				File file = new File(filePath);
				if(file.exists()){
					file.delete();
				}
				createHeaderForFeed(file);
				createCsvFeedForOnsale(file,isPartial);
				createCsvFeedForRegular(file,isPartial);
			}
			catch(Exception ex){
				if(isLoggingError())
					logError(ex);
				setJobReturnCode(ERROR);
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: createCsvFeed()");
		}
		
		public void createHeaderForFeed(File file){
			vlogInfo("Start: GoogleStoreInvFeedCreator: createHeaderForFeed()");
			try {
				FileWriter fw = new FileWriter(file);
				writeToFile(fw,getStoreCodeColumnName(),getItemIdColumnName(),getPriceColumnName(),getQuantityColumnName(),getPickupMethodColumnName(),getPickupSlaColumnName());
				fw.flush();
	            fw.close();
			}
			catch(IOException e){
				logError("Couldn't create header for feed ",e);
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: createHeaderForFeed()");
		}
		
		public void createCsvFeedForOnsale(File file,boolean isPartial) throws SQLException{
			vlogInfo("Start: GoogleStoreInvFeedCreator: createCsvFeedForOnsale()");
				runQueryCreateFile(file,getOnSaleQuery(),isPartial);
			vlogInfo("End: GoogleStoreInvFeedCreator: createCsvFeedForOnsale()");
		}
		
		public void createCsvFeedForRegular(File file,boolean isPartial) throws SQLException{
			vlogInfo("Start: GoogleStoreInvFeedCreator: createCsvFeedForRegular()");
			runQueryCreateFile(file,getRegularQuery(),isPartial);
			vlogInfo("End: GoogleStoreInvFeedCreator: createCsvFeedForRegular()");
		}
		
		public boolean runQueryCreateFile(File file,String query,boolean isPartial) throws SQLException{
			vlogInfo("Start: GoogleStoreInvFeedCreator: runQueryCreateFile()");
			Connection conn = feedDataSource.getConnection();
			Statement stmt = null;
			boolean retValue=false;
			try {
				FileWriter fw = new FileWriter(file,true);
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					String storeCode=rs.getString(STORE_CODE);
					String itemId=rs.getString(ITEMID);
					String price=rs.getString(PRICE);
					int quantity=0;
					if(isPartial){
						quantity=rs.getInt(QUANTITY_PAR);
					}
					else{
						quantity=rs.getInt(QUANTITY);
					}
					String quantityStr=""+quantity;
					String pickupMethod=rs.getString(PICKUP_METHOD);
					String pickupSLA=rs.getString(PICKUP_SLA);
					
					if(storeCode==null || itemId==null || price== null || pickupMethod ==null || pickupSLA==null){
						continue;
					}
					else {
						writeToFile(fw,storeCode,itemId,price,quantityStr,pickupMethod,pickupSLA);
					}
					retValue= true;
				}
				fw.flush();
	            fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logError("Couldn't execute entry to file "+query, e);
				retValue=false;
				
			}
			finally {
				try {
					if ( stmt != null) stmt.close();
				}
				catch ( SQLException e) {
					retValue=false;
					logError("There was a problem closing the connection", e);
				}
				closeConnection(conn);
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: runQueryCreateFile()");
			return retValue;
			
		}
		
		private void writeToFile(FileWriter fw,String storeCode,String itemId,String price,String quantity,String pickupMethod, String pickupSLA) throws IOException{
			vlogDebug("Start: GoogleStoreInvFeedCreator: writeToFile()");
			fw.append(storeCode);
			fw.append(',');
			fw.append(itemId);
			fw.append(',');
			fw.append(price);
			fw.append(',');
			fw.append(quantity);
			fw.append(',');
			fw.append(pickupMethod);
			fw.append(',');
			fw.append(pickupSLA);
			fw.append('\n');
			vlogDebug("End: GoogleStoreInvFeedCreator: writeToFile()");
		}

		public String getExceptionMsg() {
			return exceptionMsg;
		}
	
		public void setExceptionMsg(String exceptionMsg) {
			this.exceptionMsg = exceptionMsg;
		}
		
		protected int processedItemCount(String query)throws SQLException {
			vlogInfo("Start: GoogleStoreInvFeedCreator: processedItemCount()");
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
			vlogInfo("End: GoogleStoreInvFeedCreator: processedItemCount()");
			return processedCount;
		}
		
	
	
		/*
		 * Process the downloaded feed files by invoking a DB stored proc
		 * The stored procedure accepts the input dir on the commerce server
		 * where the feeds to be processed are placed
		 * 
		 */
		protected void collectData(String pStoredProc) {
			 vlogInfo("Start: GoogleStoreInvFeedCreator: collectData()");
			boolean hasError = false;
			CallableStatement cs;
			CallableStatement cs2;
			try (Connection conn = feedDataSource.getConnection()) {
				this.conn = conn;
				conn.setAutoCommit(false);
				
				cs = getCallableStmt(pStoredProc);
				cs2=getRecompileStmt();
				if(getQueryTimeout() > 0) {
					cs.setQueryTimeout(getQueryTimeout());
				}
				if(getQueryTimeout() > 0) {
					cs2.setQueryTimeout(getQueryTimeout());
				}
				
				if(isLoggingInfo()) {
					logInfo(getTaskName() + " Executing import stored procs");
				}
				
				boolean retValue2=cs2.execute();
				
				
				if(isLoggingInfo()) {
					logInfo(getTaskName() + cs2+": StoreProc completed with return code: " + retValue2);
				}
				
				boolean retValue =cs.execute();
				conn.commit();
	
				if(isLoggingInfo()) {
					logInfo(getTaskName() + cs+": StoreProc completed with return code: " + retValue);
				}
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
				vlogInfo("End: GoogleStoreInvFeedCreator: collectData()");
			}		
		}
		
		protected int getProcessedItemCount () throws SQLException {
			vlogInfo("Start: GoogleStoreInvFeedCreator: getProcessedItemCount()");
			int totalProcessedItems=0;
			int onSaleItemsCount=processedItemCount(getProcessedCountOnSale());
			int regularItemsCount=processedItemCount(getProcessedCountRegular());
			totalProcessedItems=onSaleItemsCount+regularItemsCount;
			vlogInfo("End: GoogleStoreInvFeedCreator: getProcessedItemCount()");
			return totalProcessedItems;
		}
	

		/**
		 * Needless to say... closes the connection
		 * @param pConnection
		 */
		protected void closeConnection(Connection pConnection) {
			vlogInfo("Start: GoogleStoreInvFeedCreator: closeConnection()");
			try {
				if (pConnection != null)
					pConnection.close();
			} catch (SQLException sqle) {
				if (isLoggingError())
					logError(sqle);
				setJobReturnCode(ERROR);
			}
			vlogInfo("End: GoogleStoreInvFeedCreator: closeConnection()");
		}
		
		
	}
