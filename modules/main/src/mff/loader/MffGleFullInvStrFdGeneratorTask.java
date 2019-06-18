package mff.loader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.googleadwords.scheduler.GoogleStoreInvFeedCreator;

import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import mff.util.EmailUtil;
import mff.util.StringUtil;

public class MffGleFullInvStrFdGeneratorTask extends GoogleStoreInvFeedCreator {

	
	
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
		// Notification E-Mails.
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: sendEmailMessage()");
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
		vlogInfo("End: MffGleFullInvStrFdGeneratorTask: sendEmailMessage()");
	}
	
	public void sendTextNotificationEmail(String subject, String body, String to) throws EmailException {
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: sendTextNotificationEmail()");
		String type = "text/plain";
		sendNotificationEmail(subject, body, to, type);
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: sendTextNotificationEmail()");
	}
	
	public void sendNotificationEmail(String subject, String body, String to, String type) throws EmailException {
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: sendNotificationEmail()");
        if (disableEmail) {
        	logInfo("Email is disabled");
            vlogInfo("End: MffGleFullInvStrFdGeneratorTask: sendNotificationEmail()");
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
		vlogInfo("End: MffGleFullInvStrFdGeneratorTask: sendNotificationEmail()");
	}	
	

	public String createEmailBody(){
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: createEmailBody()");
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
			emailBody += "The google full store inventory feed generator process failed failed due to an unexpected error.  Check the server logs for details.\n";
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
		vlogInfo("End: MffGleFullInvStrFdGeneratorTask: createEmailBody()");
		return emailBody;
	}	
	
	@Override
	public void doTask() {
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: doTask()");
		if(isEnable()){
			if(isFileTriggerEnabled()){
				if(isTriggerFileExist()){
					vlogInfo("Google Full Store Inventory Feed Creator task ready to create feed: Google Store Product Feed Ready file found");
					super.doTask();
					if(!isDisableEmail()){
						sendEmailMessage();
					}
					try {
						  removeTriggerFile();
					}
					catch (IOException e) {
						logError("Error deleting google store product feed ready file",e);
					}
					try {
						if(isReadyFileExist()){
							removeReadyFile();
						}
						generateGoogleFeedReadyFile();
					} 
					catch (IOException e) {
						logError("Error generating google full store inventory feed ready file",e);
					}
				}
				else {
					vlogInfo("Google Full Store Product Feed Creator waiting for google store product ready file to process: Google Store Product Ready File not found");
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
			vlogInfo("Google Full Store Product Feed Creator task is not enabled");
			if(isFileTriggerEnabled()){
				try {
					 removeTriggerFile();
				}
				catch (IOException e) {
					logError("Error deleting google store product feed ready file",e);
				}
				try {
					if(isReadyFileExist()){
						removeReadyFile();
					}
					generateGoogleFeedReadyFile();
				} 
				catch (IOException e) {
					logError("Error generating google full store inventory feed ready file",e);
				}
			}
		}
		vlogInfo("End: MffGleFullInvStrFdGeneratorTask: doTask()");
	}
	
	
	private boolean generateGoogleFeedReadyFile() throws IOException {
		vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: generateGoogleFeedReadyFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  File lFile = new File(lFileNameWithPath);
		  boolean isFileCreated=lFile.createNewFile();
		  if(isFileCreated){
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: Ready file is created: "+lFileNameWithPath);  
		  }
		  else {
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: Ready file is not created: "+lFileNameWithPath);
		  }
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: generateGoogleFeedReadyFile()");
	    return isFileCreated;
	  }
	
	 private boolean removeFile(String lFileNameWithPath) throws IOException {
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: removeTriggerFile()");
		  File lFile = new File(lFileNameWithPath);
		  boolean isFileDeleted=lFile.delete();
		  if(isFileDeleted){
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: removeFile(): File is removed: "+lFileNameWithPath);
		  }
		  else{
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: removeFile(): File is not removed: "+lFileNameWithPath);
		  }
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: removeFile()");
		  return isFileDeleted;
	  }
	
	 private boolean isFileExist(String lFileNameWithPath){
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: isFileExist()");
		  File lFile = new File(lFileNameWithPath);
		  boolean ifFileExists=lFile.exists();
		  if(ifFileExists){
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: File already exists: "+lFileNameWithPath);
		  }
		  else{
			  vlogInfo("MffGleFullInvStrFdGeneratorTask: File does not exists: "+lFileNameWithPath);
		  }
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: isFileExist()");
	    return ifFileExists;
	 }
	 
	  private boolean removeTriggerFile() throws IOException {
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: removeTriggerFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getInventoryReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getInventoryReadyFileName();
		  boolean fileExists=removeFile(lFileNameWithPath);
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: removeTriggerFile()");
	    return fileExists;
	  }
	  
	  private boolean removeReadyFile() throws IOException {
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: removeReadyFile()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  boolean fileExists=removeFile(lFileNameWithPath);
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: removeReadyFile()");
	    return fileExists;
	  }
	 
	  private boolean isTriggerFileExist() {
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: isTriggerFileExist()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getInventoryReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getInventoryReadyFileName();
		  boolean fileExists=isFileExist(lFileNameWithPath);
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: isTriggerFileExist()");
	    return fileExists;
	  }
	  
	  private boolean isReadyFileExist() {
		  vlogInfo("Start: MffGleFullInvStrFdGeneratorTask: isReadyFileExist()");
		  String lFileNameWithPath = getReadyFileDir().endsWith(FOLDER_SEPERATOR)?getReadyFileDir()+getGoogleFeedReadyFileName():getReadyFileDir()+FOLDER_SEPERATOR+getGoogleFeedReadyFileName();
		  boolean fileExists=isFileExist(lFileNameWithPath);
		  vlogInfo("End: MffGleFullInvStrFdGeneratorTask: isReadyFileExist()");
	    return fileExists;
	  }
	
}
