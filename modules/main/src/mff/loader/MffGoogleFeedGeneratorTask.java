package mff.loader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.googleadwords.scheduler.GoogleFeedCreator;

import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import mff.util.EmailUtil;
import mff.util.StringUtil;

public class MffGoogleFeedGeneratorTask extends GoogleFeedCreator {

	
	
	private boolean disableEmail;
	private String notificationEmailList;
	private String notificationEmailFromAddress;
	private boolean mFileTriggerEnabled;
	private String mReadyFileDir;
	private String mInventoryReadyFileName;
	private String mGoogleFeedReadyFileName;
	
	
	public boolean isFileTriggerEnabled() {
		return mFileTriggerEnabled;
	}

	public void setFileTriggerEnabled(boolean pFileTriggerEnabled) {
		mFileTriggerEnabled = pFileTriggerEnabled;
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
    
  public String getReadyFileDir() {
	    return mReadyFileDir;
	  }
	
	  public void setReadyFileDir(String pReadyFileDir) {
	    mReadyFileDir = pReadyFileDir;
	  }
	
	  public String getInventoryReadyFileName() {
	    return mInventoryReadyFileName;
	  }
	
	  public void setInventoryReadyFileName(String pInventoryReadyFileName) {
	    mInventoryReadyFileName = pInventoryReadyFileName;
	  }
	
	  public String getGoogleFeedReadyFileName() {
	    return mGoogleFeedReadyFileName;
	  }
	
	  public void setGoogleFeedReadyFileName(String pGoogleFeedReadyFileName) {
	    mGoogleFeedReadyFileName = pGoogleFeedReadyFileName;
	  }
	
	protected void sendEmailMessage() {
		 vlogInfo("Start: MffGoogleFeedGeneratorTask: sendEmailMessage()");
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
				logInfo("SEnd Email to Notification List: " + toAddresses);
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
		vlogInfo("End: MffGoogleFeedGeneratorTask: sendEmailMessage()");
	}
	
	public void sendTextNotificationEmail(String subject, String body, String to) throws EmailException {
		vlogInfo("Start: MffGoogleFeedGeneratorTask: sendTextNotificationEmail()");
		String type = "text/plain";
		sendNotificationEmail(subject, body, to, type);
		vlogInfo("End: MffGoogleFeedGeneratorTask: sendTextNotificationEmail()");
	}
	
	public void sendNotificationEmail(String subject, String body, String to, String type) throws EmailException {
		vlogInfo("Start: MffGoogleFeedGeneratorTask: sendNotificationEmail()");
        if (disableEmail) {
            vlogInfo("Email disabled.");
            vlogInfo("Start: MffGoogleFeedGeneratorTask: sendNotificationEmail()");
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
		vlogInfo("End: MffGoogleFeedGeneratorTask: sendNotificationEmail()");
	}	
	

	public String createEmailBody(){
		vlogInfo("Start: MffGoogleFeedGeneratorTask: createEmailBody()");
		String emailBody = getTaskName() +" Job Report\n\n";
		int processedItemCount=-1;
		try {
			processedItemCount = getProcessedItemCount();
		} catch (SQLException e) {
			if(isLoggingError())
				logError(e);
		}

		// Render the list of locked assets, and warnings and errors encountered
		//
		if ( getJobReturnCode() == ERROR) {
			emailBody += "The google feed generator process failed failed due to an unexpected error.  Check the server logs for details.\n";
			emailBody += getExceptionMsg() + "\n\n";
		}
		else {
				if(getJobReturnCode()==WARN) {
					emailBody += "Check the server logs for warnings. There may be problems invalidating the caches.";
				}
				else {
					if (processedItemCount==0) {
						emailBody += "There were no records found to generate google feed\n\n";
					}
					else {
						emailBody += processedItemCount+" records are generated in the google feed\n";
						
						emailBody += "\n\n";
					}
				}
		}

		emailBody += "\n";
		vlogInfo("End: MffGoogleFeedGeneratorTask: createEmailBody()");
		return emailBody;
	}	
	
	@Override
	public void doTask() {
		vlogInfo("Start: MffGoogleFeedGeneratorTask: doTask()");
		if(isEnable()){
			if(isFileTriggerEnabled()){
				if(isTriggerFileExist()){
					vlogInfo("Google Feed Creator task ready to create feed: Inventory ready file found");
					super.doTask();
					if(!isDisableEmail()){
						sendEmailMessage();
					}
					try {
						removeTriggerFile();
					}
					catch (IOException e) {
						logError("Error deleting inventory ready file",e);
					}
					try {
						if(isReadyFileExist()){
							removeReadyFile();
						}
						generateGoogleFeedReadyFile();
					} 
					catch (IOException e) {
						logError("Error generating google feed ready file",e);
					}
				}
				else {
					vlogInfo("Google Feed Creator waiting for inventory ready file to process: Inventory ready file not found");
				}
			}
			else {
				super.doTask();
				if(!isDisableEmail()){
					sendEmailMessage();
				}
			}
			
		}
		else {
			
			vlogInfo("Google Feed Creator task is not enabled in");
		}
		vlogInfo("End: MffGoogleFeedGeneratorTask: doTask()");

	}
	
	
	private boolean generateGoogleFeedReadyFile() throws IOException {
		vlogInfo("Start: MffGoogleFeedGeneratorTask: generateGoogleFeedReadyFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  File lFile = new File(lFileNameWithPath);
		  boolean isFileCreated=lFile.createNewFile();
		  if(isFileCreated){
			  vlogInfo("MffGoogleFeedGeneratorTask: Ready file is created: "+lFileNameWithPath);  
		  }
		  else {
			  vlogInfo("MffGoogleFeedGeneratorTask: Ready file is not created: "+lFileNameWithPath);
		  }
		  vlogInfo("End: MffGoogleFeedGeneratorTask: generateGoogleFeedReadyFile()");
	    return isFileCreated;
	  }
	
	 private boolean removeFile(String lFileNameWithPath) throws IOException {
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: removeTriggerFile()");
		  File lFile = new File(lFileNameWithPath);
		  boolean isFileDeleted=lFile.delete();
		  if(isFileDeleted){
			  vlogInfo("MffGoogleFeedGeneratorTask: removeFile(): File is removed: "+lFileNameWithPath);
		  }
		  else{
			  vlogInfo("MffGoogleFeedGeneratorTask: removeFile(): File is not removed: "+lFileNameWithPath);
		  }
		  vlogInfo("End: MffGoogleFeedGeneratorTask: removeFile()");
		  return isFileDeleted;
	  }
	
	 private boolean isFileExist(String lFileNameWithPath){
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: isFileExist()");
		  File lFile = new File(lFileNameWithPath);
		  boolean ifFileExists=lFile.exists();
		  if(ifFileExists){
			  vlogInfo("MffGoogleFeedGeneratorTask: File already exists: "+lFileNameWithPath);
		  }
		  else{
			  vlogInfo("MffGoogleFeedGeneratorTask: File does not exists: "+lFileNameWithPath);
		  }
		  vlogInfo("End: MffGoogleFeedGeneratorTask: isFileExist()");
	    return ifFileExists;
	 }
	 
	  private boolean removeTriggerFile() throws IOException {
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: removeTriggerFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getInventoryReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getInventoryReadyFileName();
		  boolean fileExists=removeFile(lFileNameWithPath);
		  vlogInfo("End: MffGoogleFeedGeneratorTask: removeTriggerFile()");
	    return fileExists;
	  }
	  
	  private boolean removeReadyFile() throws IOException {
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: removeReadyFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  boolean fileExists=removeFile(lFileNameWithPath);
		  vlogInfo("End: MffGoogleFeedGeneratorTask: removeReadyFile()");
	    return fileExists;
	  }
	 
	  private boolean isTriggerFileExist() {
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: isTriggerFileExist()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getInventoryReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getInventoryReadyFileName();
		  boolean fileExists=isFileExist(lFileNameWithPath);
		  vlogInfo("End: MffGoogleFeedGeneratorTask: isTriggerFileExist()");
	    return fileExists;
	  }
	  
	  private boolean isReadyFileExist() {
		  vlogInfo("Start: MffGoogleFeedGeneratorTask: isReadyFileExist()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  boolean fileExists=isFileExist(lFileNameWithPath);
		  vlogInfo("End: MffGoogleFeedGeneratorTask: isReadyFileExist()");
	    return fileExists;
	  }
	
	
}
