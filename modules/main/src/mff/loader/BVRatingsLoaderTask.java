package mff.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.sql.DataSource;

import com.mff.services.cache.TargetCacheInvalidatorRemoteHelper;

import atg.nucleus.ServiceException;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import mff.MFFException;
import mff.logging.ErrorMessages;
import mff.logging.LogConstants;
import mff.logging.LogLevel;
import mff.logging.Logger;
import mff.task.Task;
import mff.util.EmailUtil;
import mff.util.StringUtil;
import mff.util.ftp.SFTPClientImpl;

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
public class BVRatingsLoaderTask extends Task {

	// The SFTP Client service component
	private SFTPClientImpl sftpClient;
	
	// Remote SFTP directory location
	private String remoteDir;
	
	// Local dir where the files from ftp should be downloaded to
	private String localDir;
	
	// The SFTP dir may contain other files. Ideally it would not
	// so we only look for files with certain prefixes
	private String filePrefix;
	
	// The main stored proc that processes the import
	private String storedProcName;
	
	// store proc that handles archival
	private String archiveStoredProcName;
	
	// stored proc that handles file deletion
	private String deleteStoredProcName;
	
	// flag to determine choice between archive and delete
	private boolean deleteFeeds;
	
	// CATFEED datasource component
	private DataSource feedDataSource;
	
	// Connection to CATFEED schema
	protected Connection conn;

	// Flag specifies if the feeds are to be downloaded from SFTP
	// If turned off, then feeds will be picked up from location
	// specified in localDir property
	private boolean downloadFeedsFromSFTP;
	
	// sci - Selective Cache Invalidation
	// Since we're updating the data directly in the DB, 
	// the repo caches need to be refreshed.
	// Ideally we want to invalidate only updated items 
	// from the repo cache
	// if the count of items updated is high, then
	// we're probably better off invalidating the entire repo cache
	// This is similar to the OOTB SCI concepts
	
	// The prop below specifies the threshold for SCI
	// beyond this value, we invalidate the entire repo cache
	private int sciThreshold;
	
	// JNDI uri to repo items. used in the cache invalidation calls
	private String repoJNDIUri;
	
	// sometimes, we may re-define the IDs for items on commerce side
	// and not use the ones sent by MFF in the feeds
	// These ids are usually derived from the MFF ids
	// for ex: price items have an "lp-" and "sp-" prefix to the sku-id
	// and this makes up the price item's id.
	private String[] idPrefixes;
	
	// In some imports, we may not need to invalidate the caches
	// like inventory imports - Inv Repo is uncached to begin with
	// Flag determines if we need to handle repo cache invalidation.
	private boolean invalidateCaches;
	
	
	// Helper component for handling repo cache invalidation
	private TargetCacheInvalidatorRemoteHelper cacheInvalidator;
	
	// Logger component
	protected Logger logger;
	
	// Job specific error code.. used to prefix errors logged to DB
	// These codes are used to monitor specific jobs
	private String jobErrorCode;
	
	// Job specific error message used for monitoring
	private String jobErrorMsg;

	/* Return Codes */
	public static final int		SUCCESS				= 0;
	public static final int		WARN				= 1;
	public static final int		ERROR				= 2;
	
	// Job return code
	int	jobReturnCode;
	
	private String	notificationEmailList;
	
	private String	runServer;

	private String tableName;
	
	private String taskName;
	
	private String exceptionMsg;
	
	


	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	// constants used during logging messages
	private static String LOG_SERVICE_START = LogConstants.LOG_SERVICE_START;
	private static String LOG_SERVICE_END = LogConstants.LOG_SERVICE_END;
	
	private boolean disableEmail;

	private String  notificationEmailFromAddress = "bogus@whatever.foo";

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
    public String getNotificationEmailFromAddress() {
        return notificationEmailFromAddress;
    }

    public void setNotificationEmailFromAddress(String pNotificationEmailFromAddress) {
        notificationEmailFromAddress = pNotificationEmailFromAddress;
    }
    
	public boolean getDisableEmail() {
	  return disableEmail;
	}
	
	public void setDisableEmail(boolean pDisableEmail) {
	  disableEmail = pDisableEmail;
	}
	
	public String getRunServer() {
		return runServer;
	}
	public void setRunServer(String runServer) {
		this.runServer = runServer;
	}

	public String getNotificationEmailList() {
		return notificationEmailList;
	}
	public void setNotificationEmailList(String notificationEmailList) {
		this.notificationEmailList = notificationEmailList;
	}	
	
	public boolean isDownloadFeedsFromSFTP() {
		return downloadFeedsFromSFTP;
	}

	public void setDownloadFeedsFromSFTP(boolean downloadFeedsFromSFTP) {
		this.downloadFeedsFromSFTP = downloadFeedsFromSFTP;
	}

	public boolean isInvalidateCaches() {
		return invalidateCaches;
	}

	public void setInvalidateCaches(boolean invalidateCaches) {
		this.invalidateCaches = invalidateCaches;
	}

	public int getSciThreshold() {
		return sciThreshold;
	}

	public void setSciThreshold(int sciThreshold) {
		this.sciThreshold = sciThreshold;
	}

	public String getRepoJNDIUri() {
		return repoJNDIUri;
	}

	public void setRepoJNDIUri(String repoJNDIUri) {
		this.repoJNDIUri = repoJNDIUri;
	}

	public String[] getIdPrefixes() {
		return idPrefixes;
	}

	public void setIdPrefixes(String[] idPrefixes) {
		this.idPrefixes = idPrefixes;
	}
	
	public TargetCacheInvalidatorRemoteHelper getCacheInvalidator() {
		return cacheInvalidator;
	}

	public void setCacheInvalidator(TargetCacheInvalidatorRemoteHelper cacheInvalidator) {
		this.cacheInvalidator = cacheInvalidator;
	}	
	
	public int getJobReturnCode() {
		return jobReturnCode;
	}
	public void setJobReturnCode(int jobReturnCode) {
		logInfo("Setting Return Code [" + jobReturnCode + "]");
		this.jobReturnCode = jobReturnCode;
	}
	
	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
	}
	
	/*
	 * Constructs callableStatement for importing feeds
	 */
	public CallableStatement getCallableStmt() throws SQLException {
		if(isLoggingInfo()) {
			logInfo(getTaskName() + " creating callable statement - "
					+ "proc name is " + getStoredProcName()
					+ "passing param " + getLocalDir());
		}
		
		CallableStatement cs;
		cs = conn.prepareCall(getStoredProcName());
		cs.setString(1, getLocalDir());
		return cs;
	}

	/**
	 * Constructs callableStatement for archiving feeds
	 * @return
	 * @throws SQLException
	 */
	public CallableStatement getArchiveCallableStmt(boolean isDeleteFeeds) throws SQLException {
		String procName = getArchiveStoredProcName();
		
		if(isDeleteFeeds) {
			procName=getDeleteStoredProcName();
		}
		
		if(isLoggingInfo()) {
			logInfo(getTaskName() + " creating callable statement - "
					+ "proc name is " + procName
					+ "passing param " + getLocalDir()
					+ " and param " + getFilePrefix() + "%"
					+ " deleteFeeds " + isDeleteFeeds);
		}
		
		CallableStatement cs;
		cs = conn.prepareCall(procName);
		cs.setString(1, getLocalDir());
		cs.setString(2, getFilePrefix() + "%");
		return cs;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
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

	public String getArchiveStoredProcName() {
		return archiveStoredProcName;
	}

	public void setArchiveStoredProcName(String archiveStoredProcName) {
		this.archiveStoredProcName = archiveStoredProcName;
	}

	public String getDeleteStoredProcName() {
		return deleteStoredProcName;
	}

	public void setDeleteStoredProcName(String deleteStoredProcName) {
		this.deleteStoredProcName = deleteStoredProcName;
	}

	public boolean isDeleteFeeds() {
		return deleteFeeds;
	}

	public void setDeleteFeeds(boolean deleteFeeds) {
		this.deleteFeeds = deleteFeeds;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	public SFTPClientImpl getSftpClient() {
		return sftpClient;
	}

	public void setSftpClient(SFTPClientImpl sftpClient) {
		this.sftpClient = sftpClient;
	}

	public String getJobErrorCode() {
		return jobErrorCode;
	}

	public void setJobErrorCode(String jobErrorCode) {
		this.jobErrorCode = jobErrorCode;
	}

	public String getJobErrorMsg() {
		return jobErrorMsg;
	}

	public void setJobErrorMsg(String jobErrorMsg) {
		this.jobErrorMsg = jobErrorMsg;
	}

	/*
	 * (non-Javadoc)
	 * @see mff.task.Task#doTask()
	 * 
	 * Primary method invoked from TaskScheduler. 
	 * This inturn invokes other methods in processing
	 * the feeds
	 */
	@Override
	public void doTask() {
		
		// Log to DB that the service has started
		logInfo(getTaskName() + " Started", LOG_SERVICE_START);
		setJobReturnCode(SUCCESS);
		
		// Determine if we need to download feeds from an FTP server
		if(isDownloadFeedsFromSFTP()) {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " Downloading feeds from FTP server"
						+ "Remote dir " + getRemoteDir()
						+ "Local dir " + getLocalDir()
						+ "File prefix " + getFilePrefix());
			}
			downloadFeedFiles(getRemoteDir(),getLocalDir(), getFilePrefix());
		} else {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " FTPDownload disabled. Using files from " + getLocalDir());
			}
		}
		
		// Invoke the configured stored proc, passing in the location
		// were the feeds to be processed are located
		processImport(getStoredProcName(),getLocalDir());
		
		// Determine if we need to handle repo caches
		if(getJobReturnCode() ==SUCCESS) {
			if(isInvalidateCaches()) {
				if(isLoggingInfo()) {
					logInfo(getTaskName() + " invalidateCaches enabled");
				}
				invalidateCaches();
			} else {
				if(isLoggingInfo()) {
					logInfo(getTaskName() + " invalidateCaches disabled. Skipped call to invalidateCaches()");
				}
			}
		}
		
		// if we're here then all is good... take care of the processed feeds
		if(getJobReturnCode()!=ERROR) {
			archiveFeedFiles();
		}
		
		// send notification email
		sendEmailMessage();
		
		// log to db that service has completed.
		logInfo(getTaskName() + " Scheduled task completed", LOG_SERVICE_END);

	}
	public void sendTextNotificationEmail(String subject, String body, String to) throws EmailException {
		String type = "text/plain";
		sendNotificationEmail(subject, body, to, type);
	}
	public void sendNotificationEmail(String subject, String body, String to, String type) throws EmailException {

        if (disableEmail) {
            logDebug("Email disabled.");
            return;
        }

		EmailEvent emailEvent = new EmailEvent();
		Message msg;
		try {
			EmailUtil emailService = EmailUtil.getInstance();
			msg = MimeMessageUtils.createMessage( getNotificationEmailFromAddress(), subject);
			MimeMessageUtils.setRecipient(msg, Message.RecipientType.TO, to);
			ContentPart[] content = { new ContentPart(body, type) };
			MimeMessageUtils.setContent(msg, content);
			emailEvent.setMessage(msg);

			emailEvent.setRecipient(to);
			emailEvent.setFrom("");
			emailEvent.setBody(body);
			emailEvent.setSubject(subject);

			if (null != emailService) {
				emailService.sendEmailEvent(emailEvent);
			} else {
				logDebug("EmailUtil is not available");
			}
		} catch (MessagingException e) {
			logError("EmailException occurred while trying to send an email to the Notification Email List", e);
		}
	}	
	private void sendEmailMessage() {
		// Notification E-Mails.
		if (!StringUtil.isEmpty(getNotificationEmailList())) {

			String toAddresses = getNotificationEmailList();
			String subject = "";
			String body = "";
			boolean sendEmail = false;
			if (getJobReturnCode() == ERROR) {
				sendEmail = true;
				subject = "ERROR: " + getTaskName();
				body = "A fatal Exception was encountered while running the job titled: " + getTaskName()
						+ ".  Please check the log for more details.\n\n";
			} else if (getJobReturnCode() == WARN ) {
				sendEmail = true;
				subject = "WARNING: " + getTaskName();
				body = "The job titled: " + getTaskName() + " ran successfully, but with Warnings or non-fatal Errors.\n\n";
			} else {
				sendEmail = true;
				subject = "SUCCESS: " + getTaskName();
				body = "The job titled: " + getTaskName() + " ran successfully, with no Warnings or non-fatal Errors.\n\n";				
			}
			if (sendEmail) {
				logInfo("Sending Email to Notification List: " + toAddresses);
				try {
					String emailMessage = createEmailBody();
					if (emailMessage != null) {
						body += emailMessage;
					}
					String server = getRunServer();
					if (server != null) {
						subject = subject + " [" + server + "]";
					}
					sendTextNotificationEmail(subject, body, toAddresses);
				} catch (EmailException ee) {
					logError("EmailException occurred while trying to send an email to the Notification Email List", ee);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
  private int getProcessedItemCount () throws SQLException {
		String query = "select count(*) as processed_count from " + getTableName();
		int processedCount=-1;
		Connection conn = feedDataSource.getConnection();
		Statement stmt = null;
		List<Map<String,Object>> items = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				processedCount = rs.getInt("processed_count");
			}
			

			if(isLoggingDebug())
				logDebug("There were "+ processedCount +" item(s) processed");
			rs.close();
		}
		catch ( SQLException e) {
			logError("Couldn't execute item query "+query, e);
		}
		finally {
			try {
				if ( stmt != null) stmt.close();
			}
			catch ( SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				return processedCount;
			}
	        closeConnection(conn);
		}	
		return processedCount;
	}

	@SuppressWarnings("unused")
  public ArrayList<String> getFeedFiles() throws SQLException {
		String query = "select filename from tmp_xml_filenames where filename like '%" + getFilePrefix() + "%' order by sequence_num";
		ArrayList<String> files = new ArrayList<String>();
		Connection conn = feedDataSource.getConnection();
		Statement stmt = null;
		List<Map<String,Object>> items = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				files.add(rs.getString("filename"));
			}
			if(isLoggingDebug())
				logDebug("There are "+(files != null ? files.size() : 0)+" items in the result set");
			rs.close();
		}
		catch ( SQLException e) {
			logError("Couldn't execute item query "+query, e);
		}
		finally {
			try {
				if ( stmt != null) stmt.close();
			}
			catch ( SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				return files;
			}
	        closeConnection(conn);
		}	
		return files;
	}
	
	public String createEmailBody(){
		logDebug("enter creating email body function");
		String emailBody = getTaskName() +" Job Report\n\n";
		ArrayList<String> processedFeeds=new ArrayList<String>();
		int processedItemCount=-1;
		try {
			processedFeeds = getFeedFiles();
			processedItemCount = getProcessedItemCount();
		} catch (SQLException e) {
			if(isLoggingError())
				logError(e);
		}

		if ( processedFeeds == null || processedFeeds.size() == 0) {
			emailBody += "There were NO files to process this time around\n\n";
		}
		else {
			emailBody += "The following files were processed\n";
			for ( String file : processedFeeds) {
				emailBody += "---> "+file+"\n";
			}
			emailBody += "\n\n";

            //emailBody += generateChangeListBody();

		}

		//
		// Render the list of locked assets, and warnings and errors encountered
		//
		if ( getJobReturnCode() == ERROR) {
			emailBody += "The import failed due to an unexpected error.  Check the server logs for details.\n";
			emailBody += getExceptionMsg() + "\n\n";
		} else {
			emailBody += "Total number of records processed : ---> [ " + processedItemCount + " ]";
			
			emailBody += "\n";
			
			if(getJobReturnCode()==WARN) {
				emailBody += "Check the server logs for warnings. There may be problems invalidating the caches.";
			}
			
			if(getAdditionalInfo()!=null) {
				emailBody += getAdditionalInfo();
			}
		}
	 	
		
		emailBody += "\n";
		
		// Render the list of errors
/*		if( null != reportErrorList && reportErrorList.size() > 0){
			emailBody += "\n\nThe following specific issues were reported\n\n";
			emailBody += padRight("Item Type",20);
			emailBody += padRight("Item ID",15);
			emailBody += padRight("Item Name",50);
			emailBody += "Message\n";
			for ( String filename : reportErrorList.keySet()) {
				if ( filename != null)
					emailBody += "\n---------- Errors reported in file " + filename + " ----------\n\n";
				for ( ReportInfo bean : reportErrorList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() +"\n";
				}
			}
			emailBody += "\n\n";
		}*/

		// Render the list of warnings
/*		if( null != reportWarnList && reportWarnList.size() > 0){
			emailBody += "\n\nThe following specific warnings were reported\n\n";
			emailBody += padRight("Item Type",20);
			emailBody += padRight("Item ID",15);
			emailBody += padRight("Item Name",50);
			emailBody += "Message\n";
			for ( String filename : reportWarnList.keySet()) {
				if ( filename != null)
					emailBody += "\n---------- Warnings reported in file " + filename + " ----------\n\n";
				for ( ReportInfo bean : reportWarnList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() +"\n";
				}
			}
			emailBody += "\n\n";
		}*/
		
		// Render the list of locked assets
/*		if ( lockedAssetList != null && lockedAssetList.size() > 0) {
			emailBody += "\n\nThe following assets were locked and, therefore, not updated\n\n";
			emailBody += padRight("Item Type",20);
			emailBody += padRight("Item ID",15);
			emailBody += padRight("Item Name",50);
			emailBody += "Message\n";
			for ( String filename : lockedAssetList.keySet()) {
				if ( filename != null)
					emailBody += "\n---------- Locked assets reported from file " + filename + " ----------\n\n";
				for ( ReportInfo bean : lockedAssetList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() +"\n";
				}
			}
		}*/
		return emailBody;
	}	
	
	protected String getAdditionalInfo() {
		String additionalInfo=null;
		return additionalInfo;
		
	}

	/*
	 * Uses FTP Client service to pull down the feed files to be processed
	 * pRemoteDir - The directory location of the feeds on the FTP Server
	 * pLocalDir - The directory on the commerce server where the feeds should be downloaded to
	 * pFilePrefix - Only files with this prefix will be downloaded from the FTP server
	 */
	protected void downloadFeedFiles(String pRemoteDir, String pLocalDir, String pFilePrefix) {

		if(isLoggingInfo())
			logInfo(getTaskName() + " Pulling feeds from " + pRemoteDir + " to " + pLocalDir);
		
		try {
			getSftpClient().downloadFeedFiles(pRemoteDir, pLocalDir, pFilePrefix);
		} catch (IOException e) {
			if(isLoggingError())
				logError(e);
			setJobReturnCode(ERROR);
		}
	}

	/*
	 * Process the downloaded feed files by invoking a DB stored proc
	 * The stored procedure accepts the input dir on the commerce server
	 * where the feeds to be processed are placed
	 * 
	 */
	protected void processImport(String pStoredProc,String pInputDir) {

		boolean hasError = false;
		CallableStatement cs;
		try (Connection conn = feedDataSource.getConnection()) {
			this.conn = conn;
			conn.setAutoCommit(false);

			cs = getCallableStmt();
			
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
					logInfo(getTaskName() + " Scheduled Task Completed Successfully");
			}
			else {
				if(isLoggingError())
					logError(getTaskName() + " Scheduled Task Exited with Error");
			}
			closeConnection(this.conn);
		}		
	}

	/**
	 * Get a list of price items that have been modified
	 * Here we assume that the feeds contain only items that have really
	 * been modified. 
	 * This list is used to determine the items that have to be removed
	 * from the underlying repo cache
	 * 
	 * @return
	 */
	private List<String> getChangedItems() {
		
		if(isLoggingInfo()) {
			logInfo(getTaskName() + " getting list of changed items");
		}
		String sqlQuery = "select sku_id from tmp_price_csv";
		List<String> items = new ArrayList<String>();

		try (Connection conn = feedDataSource.getConnection()) {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sqlQuery); 
		
			while (rs.next()) {
				items.add(rs.getString(1));
			}
			
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " There are " + (items != null ? items.size() : 0)
					+ " updated");
			}
		} 
		catch (Throwable e) {
			if(isLoggingError())
				logError(e);
			setJobReturnCode(ERROR);
		}
		finally {
			closeConnection(this.conn);
		}	
		return items;
	}
	
	/**
	 * Needless to say... closes the connection
	 * @param pConnection
	 */
	protected void closeConnection(Connection pConnection) {
		try {
			if (pConnection != null)
				pConnection.close();
		} catch (SQLException sqle) {
			if (isLoggingError())
				logError(sqle);
			setJobReturnCode(ERROR);
		}
	}
	
	/*
	 * Since we're making updates directly in the DB, import process
	 * may need to invalidate updated items that are in the repository cache
	 * 
	 */
	protected void invalidateCaches() {	
		ArrayList<String> uris = new ArrayList<String>();
		
		// Add updated items
		List<String> items = getChangedItems();
		
		// log details about cache related configuration
		if(isLoggingInfo()) {
			logInfo(getTaskName() 
					+ " sciThreshold " + getSciThreshold()
					+ " repoJndiRoot " + getRepoJNDIUri()
					+ " idPrefixes size " + (getIdPrefixes() != null ? getIdPrefixes().length : 0) 
					);
		}

		// We go thru' the exercise of constructing
		// JNDI repo uris to updated items only
		// if there are updated items & their count
		// is less than the scithreshold
		
		if(isLoggingInfo()) {
			logInfo(getTaskName() 
					+ " Constructing URIs of items to invalidate");
		}
		
		if (items != null && items.size() > 0) {
			// Construct the JNDI URI of items to be invalidated
			String url="";
			String jndiRoot = getRepoJNDIUri();
			
			for (String item : items) {
				if(getIdPrefixes().length > 0 ) {
					for(int i=0; i < getIdPrefixes().length; i++) {
						url = jndiRoot + getIdPrefixes()[i] + item;
						if(isLoggingDebug()) {
							logDebug("Updated item " + item + ". ID Prefix " + getIdPrefixes()[i] 
									+ ". repo uri " + jndiRoot
									+ ". Full URI " + url);
						}
						uris.add(url);
					}					
				} else {
					url = jndiRoot + item;
					if(isLoggingDebug()) {
						logDebug("Updated item " + item  
								+ ". repo uri " + jndiRoot
								+ ". Full URI " + url);
					}
					uris.add(url);
				}
			}
		}

		// determine if we're invalidating specific items
		// or dumping the item cache
		
		if (uris.size() > 0) {
			if(uris.size() <= getSciThreshold()) {
				if(isLoggingInfo()) {
					logInfo(getTaskName() + " There are " + uris.size() + " items to invalidate");
				}
				try {
					getCacheInvalidator().invalidateItems(uris);
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					if(isLoggingError())
						logError(e);
					setJobReturnCode(WARN);
				}
			} else {
				if(isLoggingInfo()) {
					logInfo("SciThreshold exeeds.. dumping repo cache - " + getRepoJNDIUri());
				}				
				try {
					getCacheInvalidator().invalidateAllItems(getRepoJNDIUri());
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					if(isLoggingError())
						logError(e);
					setJobReturnCode(WARN);
				}
			}
			
			if(isLoggingInfo()) {
				logInfo(getTaskName() 
						+ "FeedLoader Task - Finished invoking cacheInvalidator invalidateItems");
			}
		}
		else {
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " No assets to invalidate");
			}
		}
		
	}
	
	/*
	 * Archives the feed files in localDir location to an "archive" directory
	 * under localDir
	 * 
	 */
	protected void archiveFeedFiles() {
		boolean hasError = false;
		CallableStatement cs;
		try (Connection conn = feedDataSource.getConnection()) {
			this.conn = conn;
			conn.setAutoCommit(false);

			cs = getArchiveCallableStmt(isDeleteFeeds());
			
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " Executing archive stored proc");
			}
			
			boolean retValue =cs.execute();
			conn.commit();

			if(isLoggingInfo()) 
				logInfo(getTaskName() + " StoreProc completed with return code " + retValue);
		}
		catch (Throwable e) {
			hasError=true;
			if(isLoggingError())
				logError(e);
			setJobReturnCode(ERROR);
		}
		finally {
			if (!hasError) {
				if(isLoggingInfo())
					logInfo(getTaskName() + " Scheduled Task Completed Successfully");
			}
			else {
				if(isLoggingError())
					logError(getTaskName() + " Scheduled Task Exited with Error");
			}
			closeConnection(this.conn);
		}		
		
	}	
	/*
	 * Logging
	 * Overrides oracle's logging. This implementation logs messages to the log file
	 * and optionally writes to the DB as well
	 */
	public void logInfo(String msg, String msgCode) {
		if (isLoggingInfo()) {
			try { 
				Logger.LogEntry logEntry = logger.createLogEntry(this.getName(), 
						LogLevel.INFO, msgCode, msg, null);
				logger.log(logEntry);
			}
			catch(MFFException be) {
				throw new RuntimeException(be);
			}
		}
	}

	@Override
	public void logError(String msg) {
		String msg2 = getErrorMessage() + ": " + msg;
		logError(msg2, null);
	}
	
	@Override
	public void logError(Throwable ex) {
		String msg = getErrorMessage();
		logError(msg, ex);
	}
	
	@Override
	public void logError(String msg, Throwable ex) {
	  logError(msg, getErrorCode(), ex);		
	}
	
	public void logError(String msg, String msgCode, Throwable ex) {
		if (isLoggingError()) {
			try {
				Logger.LogEntry logEntry = logger.createLogEntry(this.getName(), 
						LogLevel.ERROR, msgCode, msg, (Exception) ex);
				logger.log(logEntry);
			}
			catch (MFFException be) {
				throw new RuntimeException(be);
			}
		}   
	}
	
	protected String getErrorMessage() {
		try {
			return (String)ErrorMessages.class.getDeclaredField(getJobErrorMsg()).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logError(e);
			return null;
		}	
	}

	protected String getErrorCode() {
		//return ErrorMessages.MFF_F1000_CRITICAL_CODE;
		try {
			return (String)ErrorMessages.class.getDeclaredField(getJobErrorCode()).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logError(e);
			return null;
		}
	}	
}
