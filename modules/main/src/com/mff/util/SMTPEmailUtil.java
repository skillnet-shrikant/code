package com.mff.util;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import atg.service.email.SMTPEmailSender;
import mff.MFFEnvironment;

public class SMTPEmailUtil extends GenericService {

  private MFFEnvironment environment;

  private SMTPEmailSender emailSender;
  
  public void sendEmailAlertForJob(String pSubject, String pBody, String pSender, String pEmailDistro) {
    if (getEnvironment().isSendEmailAlerts()) {
      try {
        if (isLoggingInfo()) logInfo(" Sending email alert");
        
        String emailTo = pEmailDistro;
        String sender = pSender;
        
        if(emailTo == null) {
        	emailTo=getEnvironment().getJobAlertsEmailToAddress();
        }
        
        if(sender == null) {
        	sender = getEnvironment().getJobAlertsEmailFromAddress();
        }
        
        if(emailTo != null) {
	        getEmailSender().sendEmailMessage(
	            sender, 
	            emailTo, 
	            pSubject + " - " + getEnvironment().getJobAlertsEmailSubjectSuffix(),
	            pBody);
	        if (isLoggingInfo()) logInfo(" Email alert sent");
        } else {
        	if(isLoggingError()) {
        		logError("No sendTo email address specified. Unable to send email");
        	}
        }
      } catch (Exception e) {
        if (isLoggingError()) logError(e);
      }
    }
  }

  public boolean sendReportEmail(String pSubject, String pBody, String pSender, String pEmailDistro, File[] pFiles) {
    try {
      if (isLoggingInfo()) logInfo(" Sending email alert");
      
      if(pEmailDistro == null) {
        pEmailDistro=getEnvironment().getReportToEmailAddress();
      }
      
      if(pSender == null) {
        pSender = getEnvironment().getReportFromEmailAddress();
      }
      if(StringUtils.isNotBlank(getEnvironment().getReportEmailSubjectSuffix())) {
        pSubject =  pSubject+" - "+ getEnvironment().getReportEmailSubjectSuffix();
      }
      Message msg = MimeMessageUtils.createMessage( pSender, pSubject);
      MimeMessageUtils.setRecipient(msg, Message.RecipientType.TO, pEmailDistro);
      Multipart mp = new MimeMultipart();
      BodyPart lBodyText = new MimeBodyPart();
      lBodyText.setContent( pBody, "text/plain" );
      mp.addBodyPart( lBodyText );
      MimeBodyPart lBodyAttachments = new MimeBodyPart(); 
      for(int i=0;i<pFiles.length;i++) {
        lBodyAttachments.setDataHandler(new DataHandler(new FileDataSource(pFiles[i]))); 
        lBodyAttachments.setFileName( pFiles[i].getName() );
      } 
      mp.addBodyPart( lBodyAttachments );
      msg.setContent( mp ); 
      getEmailSender().sendEmailMessage(msg);
      return true;
    } catch (MessagingException e) {
     vlogError(e.getMessage());
    } catch (EmailException e) {
      vlogError(e.getMessage());
    }
   return false;
    
  }
  
  public MFFEnvironment getEnvironment() {
    return environment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
    environment = pEnvironment;
  }

  public SMTPEmailSender getEmailSender() {
    return emailSender;
  }

  public void setEmailSender(SMTPEmailSender pEmailSender) {
    emailSender = pEmailSender;
  }

}