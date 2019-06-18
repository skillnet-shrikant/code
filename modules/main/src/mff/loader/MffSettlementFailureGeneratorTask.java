package mff.loader;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.sql.DataSource;



import atg.nucleus.GenericService;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import mff.util.EmailUtil;
import mff.util.StringUtil;

public class MffSettlementFailureGeneratorTask extends GenericService {

	  /* Return Codes */
	public static final int	SUCCESS	= 0;
	public static final int	WARN	= 1;
	public static final int	ERROR	= 2;
	
	
	private boolean disableEmail;
	private String notificationEmailList;
	private String notificationEmailFromAddress;
	private boolean mEnable;
	private String	runServer;
	private DataSource feedDataSource;
	protected Connection conn;
	private String mTaskName;
	private int jobReturnCode;
	private String exceptionMessage;
	private String mItemsShippedButNotSettledQuery;
	private String mItemsWithNoAuthCodeQuery;
	private String mCountShippedButNotSettled;
	private String mCountWithNoAuthCode;
	private String storedProcName;
	private int queryTimeout;

	private static String ORDER_NUMBER="ORDER_NUMBER";
	private static String AMOUNT="AMOUNT";
	private static String SUBMITTED_DATE="SUBMITTED_DATE";
	private static String PAYMENT_GROUP_STATE="STATE";
	private static String LAST_EXTRACT_DATE="LAST_EXTRACT_DATE";
	private static String AMOUNT_DEBITED="AMOUNT_DEBITED";
	
	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(int pQueryTimeout) {
		queryTimeout = pQueryTimeout;
	}
	
	public String getCountShippedButNotSettled() {
		return mCountShippedButNotSettled;
	}

	public void setCountShippedButNotSettled(String pCountShippedButNotSettled) {
		mCountShippedButNotSettled = pCountShippedButNotSettled;
	}

	public String getCountWithNoAuthCode() {
		return mCountWithNoAuthCode;
	}

	public void setCountWithNoAuthCode(String pCountWithNoAuthCode) {
		mCountWithNoAuthCode = pCountWithNoAuthCode;
	}	
			
	public String getItemsShippedButNotSettledQuery() {
		return mItemsShippedButNotSettledQuery;
	}

	public void setItemsShippedButNotSettledQuery(String pItemsShippedButNotSettledQuery) {
		mItemsShippedButNotSettledQuery = pItemsShippedButNotSettledQuery;
	}

	public String getItemsWithNoAuthCodeQuery() {
		return mItemsWithNoAuthCodeQuery;
	}

	public void setItemsWithNoAuthCodeQuery(String pItemsWithNoAuthCodeQuery) {
		mItemsWithNoAuthCodeQuery = pItemsWithNoAuthCodeQuery;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String pExceptionMessage) {
		exceptionMessage = pExceptionMessage;
	}

	public String getTaskName() {
		return mTaskName;
	}

	public void setTaskName(String pTaskName) {
		mTaskName = pTaskName;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection pConn) {
		conn = pConn;
	}

	public DataSource getFeedDataSource() {
		return feedDataSource;
	}

	public void setFeedDataSource(DataSource pFeedDataSource) {
		feedDataSource = pFeedDataSource;
	}

	public String getRunServer() {
		return runServer;
	}

	public void setRunServer(String pRunServer) {
		runServer = pRunServer;
	}

	public boolean isEnable() {
		return mEnable;
	}

	public void setEnable(boolean pEnable) {
		mEnable = pEnable;
	}

	public boolean isDisableEmail() {
	  return disableEmail;
	}
	
	public void setDisableEmail(boolean pDisableEmail) {
	  disableEmail = pDisableEmail;
	}
	
	public String getNotificationEmailList() {
		return notificationEmailList;
	}
	public void setNotificationEmailList(String notificationEmailList) {
		this.notificationEmailList = notificationEmailList;
	}
	
	public String getNotificationEmailFromAddress() {
        return notificationEmailFromAddress;
    }

    public void setNotificationEmailFromAddress(String pNotificationEmailFromAddress) {
        notificationEmailFromAddress = pNotificationEmailFromAddress;
    }
    
	public String getStoredProcName() {
		return storedProcName;
	}

	public void setStoredProcName(String storedProcName) {
		this.storedProcName = storedProcName;
	}

    
    
	private Date yesterday() {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
	    return cal.getTime();
	}
	
	protected void sendEmailMessage() {
		
		// Notification E-Mails.
		if (!StringUtil.isEmpty(getNotificationEmailList())) {

			String toAddresses = getNotificationEmailList();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
			String date=sdf.format(yesterday());
			
			String subject = date+": ECOM Settlement Failures";
			String body = "";

			logInfo("Sending Email to Notification List: " + toAddresses);
			try {
				StringBuilder emailHeader = new StringBuilder();
				emailHeader=createEmailMainMessageNotSettled(emailHeader);
				emailHeader=createEmailMainMessageNoAuthCode(emailHeader);
				String emailMessage=emailHeader.toString();
				if (emailMessage != null) {
					body =emailMessage;
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
	
	
	
	public void sendTextNotificationEmail(String subject, String body, String to) throws EmailException {
		String type = "text/html";
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
	
	
	public StringBuilder createEmailBodyHeader(){
		logDebug("Enter creating email body  header function");
		StringBuilder emailBody=new StringBuilder();
		emailBody.append("<div>");
		emailBody.append(getTaskName() +" Job Report</div><br />");
		int processedItemCountForNoSettlementRecord=0;
		int processedItemCountForNoAuthCode=0;
		int totalProcessedItems=0;
		try {
			processedItemCountForNoSettlementRecord = processedItemCount(getCountShippedButNotSettled());
			processedItemCountForNoAuthCode=processedItemCount(getCountWithNoAuthCode());
		} catch (SQLException e) {
			if(isLoggingError())
				logError(e);
				setExceptionMessage(e.getMessage());
				setJobReturnCode(ERROR);
		}
		emailBody.append("<div>");
		if ( getJobReturnCode() == ERROR) {
			emailBody.append("The Mff Settlement Failure Generator Task failed due to an unexpected error.  Check the server logs for details.<br />");
			emailBody.append(getExceptionMessage() + "<br />");
		}
		else {
			totalProcessedItems=processedItemCountForNoSettlementRecord+processedItemCountForNoAuthCode;
			if(getJobReturnCode()==WARN) {
				emailBody.append("Check the server logs for warnings<br />");
			}
			else {
				if (totalProcessedItems==0) {
					emailBody.append("There were no records found to generate google feed<br />");
				}
				else {
					emailBody.append(totalProcessedItems+" records are found<br />");
					
					emailBody.append("<br />");
				}
			}
		}
		emailBody.append("<br />");
		emailBody.append("</div>");
		return emailBody;
	}
	
	public StringBuilder createEmailMainMessageNotSettled(StringBuilder header){

		logDebug("Enter creating email Main Message function");
		header.append("<div>");
		header.append("<table>");
		header.append("<tr>");
		header.append("<th>");
		header.append(ORDER_NUMBER);
		header.append("<th>");
		header.append(AMOUNT);
		header.append("</th>");
		header.append("<th>");
		header.append(SUBMITTED_DATE);
		header.append("</th>");
		header.append("<th>");
		header.append("PAYMENT_GROUP_STATE");
		header.append("</th>");
		header.append("<th>");
		header.append(LAST_EXTRACT_DATE);
		header.append("</th>");
		header.append("</tr>");
		Statement stmt = null;
		try {
			Connection conn = feedDataSource.getConnection();
			stmt = conn.createStatement();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ResultSet rs = stmt.executeQuery(getItemsShippedButNotSettledQuery());
			while(rs.next()) {
				header.append("<tr>");
				String orderNumber=rs.getString(ORDER_NUMBER);
				String state=rs.getString(PAYMENT_GROUP_STATE);
				double amount=rs.getDouble(AMOUNT);
				Timestamp submitted_date=rs.getTimestamp(SUBMITTED_DATE);
				Timestamp last_extract_date=rs.getTimestamp(LAST_EXTRACT_DATE);
				header.append("<td>");
				if(orderNumber==null){
					header.append("null");
				}
				else {
					header.append(orderNumber);
				}
				header.append("</td>");
				header.append("<td>");
				if(amount==0.0){
					header.append("null");
				}
				else {
					header.append(amount);
				}
				header.append("</td>");
				header.append("<td>");
				if(submitted_date==null){
					header.append("null");
				}
				else {
					String submittedDate=sdf.format(submitted_date);
					header.append(submittedDate);
				}
				header.append("</td>");
				header.append("<td>");
				if(state==null){
					header.append("null");
				}
				else {
					header.append(state);
				}
				header.append("</td>");
				header.append("<td>");
				if(last_extract_date==null){
					header.append("null");
				}
				else {
					String lastExtractDate=sdf.format(last_extract_date);
					header.append(lastExtractDate);
				}
				header.append("</td>");
				header.append("</tr>");
				
			}
		} catch (SQLException e) {
			if(isLoggingError())
				logError(e);
				setExceptionMessage(e.getMessage());
				setJobReturnCode(ERROR);
		}
		finally {
			header.append("</table>");
			header.append("</div>");
			try {
				if ( stmt != null) stmt.close();
			}
			catch ( SQLException e) {
				logError("There was a problem closing the connection", e);
			}
			closeConnection(conn);
		}
		return header;
		
	}
	
	public StringBuilder createEmailMainMessageNoAuthCode(StringBuilder header){

		logDebug("Enter creating email Main Message function");
		header.append("<div>");
		header.append("<table>");
		header.append("<tr>");
		header.append("<th>");
		header.append(ORDER_NUMBER);
		header.append("<th>");
		header.append("AMOUNT");
		header.append("</th>");
		header.append("<th>");
		header.append(SUBMITTED_DATE);
		header.append("</th>");
		header.append("</tr>");
		Statement stmt = null;
		try {
			Connection conn = feedDataSource.getConnection();
			stmt = conn.createStatement();
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ResultSet rs = stmt.executeQuery(getItemsWithNoAuthCodeQuery());
			while(rs.next()) {
				header.append("<tr>");
				String orderNumber=rs.getString(ORDER_NUMBER);
				double amount=rs.getDouble(AMOUNT_DEBITED);
				Timestamp submitted_date=rs.getTimestamp(SUBMITTED_DATE);
				header.append("<td>");
				if(orderNumber==null){
					header.append("null");
				}
				else {
					header.append(orderNumber);
				}
				header.append("</td>");
				header.append("<td>");
				if(amount==0.0){
					header.append("null");
				}
				else {
					header.append(amount);
				}
				header.append("</td>");
				header.append("<td>");
				if(submitted_date==null){
					header.append("null");
				}
				else {
					String submittedDate=sdf.format(submitted_date);
					header.append(submittedDate);
				}
				header.append("</td>");
				header.append("</tr>");
				
			}
		} catch (SQLException e) {
			if(isLoggingError())
				logError(e);
				setExceptionMessage(e.getMessage());
				setJobReturnCode(ERROR);
		}
		finally {
			header.append("</table>");
			header.append("</div>");
			try {
				if ( stmt != null) stmt.close();
			}
			catch ( SQLException e) {
				logError("There was a problem closing the connection", e);
			}
			closeConnection(conn);
		}
		return header;
		
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

	
	protected void collectData(String pStoredProc) {
		
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
		}		
	}
	
	
	public void doTask() {
		if(isEnable()){
			//collectData(getStoredProcName());
			if(!isDisableEmail()){
				sendEmailMessage();
			}

		}
		else {
			vlogInfo("Settlement Failure Generator task is not enabled in");
		}

	}
	
	
	protected int processedItemCount(String query)throws SQLException {
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
				logError("There was a problem closing the connection to oms", e);
				setExceptionMessage(e.getMessage());
				setJobReturnCode(ERROR);
			}
		}
		closeConnection(conn);
		return processedCount;
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
			setExceptionMessage(sqle.getMessage());
			setJobReturnCode(ERROR);
		}
	}
	
	public int getJobReturnCode() {
		return jobReturnCode;
	}
	
	public void setJobReturnCode(int jobReturnCode) {
		logInfo("Setting Return Code [" + jobReturnCode + "]");
		this.jobReturnCode = jobReturnCode;
	}
	
	
}
