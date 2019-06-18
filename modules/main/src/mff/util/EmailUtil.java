package mff.util;

import atg.nucleus.GenericService;
import atg.nucleus.spring.NucleusResolverUtil;
import atg.repository.RepositoryItem;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.EmailListener;
import atg.service.email.MimeMessageUtils;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailSender;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Utility sevice that sends template based emails. The caller is responsible for setting all the
 * parameters that are used on the email template.
 */
public class EmailUtil extends GenericService implements EmailListener {

    public static final String COMPONENT_NAME = "/mff/loader/utils/EmailUtil";

    private static EmailUtil instance;

    private Vector<EmailListener> mEmailListeners;
    private TemplateEmailSender templateEmailSender;
    private boolean sendingEmailsInSeparateThread;
    private boolean mailingPersisted;

    private ProfileTools profileTools;

    private static final int SOME_EMAIL_TYPE = 0;
    
    private String prefixURL;
    private String serverName;

    public EmailUtil() {
        mEmailListeners = new Vector<EmailListener>();
    }


    public static EmailUtil getInstance() {
        if (instance == null) {
            synchronized (EmailUtil.class) {
                if (instance == null) {
                    instance = (EmailUtil) NucleusResolverUtil.resolveName(COMPONENT_NAME);
                }
            }
        }
        return instance;
    }

    /**
     * Send an email using email event.
     *
     * (non-Javadoc)
     * @see atg.service.email.EmailListener#sendEmailEvent(atg.service.email.EmailEvent)
     */
    public void sendEmailEvent(EmailEvent event) throws EmailException {
        if (isLoggingTrace()) {
            logTrace("sendEmailEvent::invoked");
        }
        broadcastEmailEvent(event);
    }

    private void broadcastEmailEvent(EmailEvent event) throws EmailException {
        for (EmailListener listener : mEmailListeners) {
            try {
                listener.sendEmailEvent (event);
            } catch (EmailException ex) {
                if (isLoggingError()) {
                    logError("broadcastEmailEvent::EmailException occurred [message=" + ex.getMessage() + "]");
                }
                logError(ex);
                throw ex;
            }
        }
    }

    public void addEmailListener(EmailListener listener) {
        mEmailListeners.addElement(listener);
        if (isLoggingTrace()) {
            logTrace("addEmailListener::listener added. Listener count=" + mEmailListeners.size());
        }
    }

    public void removeEmailListener(EmailListener listener) {
        mEmailListeners.removeElement(listener);
    }

    public EmailListener[] getEmailListeners() {
        EmailListener[] ret = new EmailListener[mEmailListeners.size ()];
        mEmailListeners.copyInto(ret);
        return ret;
    }


    /**
     * Wrapper method to send emails using TemplateEmailSender.
     * @param recipient email address of the recipient
     * @param siteId
     * @param emailType numeric code of the email to send
     * @param parameters map of parameters to be used on the email template
     * @param senderEmail - optional parameter but if present will override the default sender email
     * @return
     */
    public boolean sendEmail(String recipient, String siteId, int emailType,
        Map<String, Object> parameters, String senderEmail) {

    	if ( isLoggingTrace() ) {
    		logTrace("[sendEmail]::invoked");
    		logTrace("[sendEmail]::recipient=" + recipient);
    		logTrace("[sendEmail]::emailType=" + emailType);
    		logTrace("[sendEmail]::senderEmail=" + senderEmail);
    		logTrace("[sendEmail]::siteId=" + siteId);
    	}

        TemplateEmailInfoImpl templateEmailInfo = new TemplateEmailInfoImpl();
        switch (emailType) {
        	// Plug in your own email types here, based on requirements and site ID
            case SOME_EMAIL_TYPE:             	
                //getSomeTemplateEmailInfo().copyProperties2(templateEmailInfo);
        		break;
            default:
                return false;
        }

        //templateEmailInfo.setTemplateURL1(siteId);
        setTemplateURL(templateEmailInfo, siteId);

        setMessageSubject(templateEmailInfo, siteId);

        if (senderEmail != null && senderEmail.length() > 0) {
            templateEmailInfo.setMessageFrom(senderEmail);
        }

        if (parameters == null) {
            parameters = new HashMap<String, Object>();
        }
        parameters.put("prefixURL", getPrefixURL());
        templateEmailInfo.setTemplateParameters(parameters);

        List<String> recipients = new LinkedList<String>();
        recipients.add(recipient);
        try {
            getTemplateEmailSender().sendEmailMessage(templateEmailInfo, recipients,
                isSendingEmailsInSeparateThread(), isMailingPersisted());
            return true;
        } catch (TemplateEmailException temailException) {
            if (isLoggingError()) {
                logError(temailException);
            }
        }
        return false;
    }

    /**
     * Set the appropriate template URL on the passed in TemplateEmailInfoImpl obj
     * based on the siteID.  Should be change to meet requirements, if needed.
     * @param templateEmailInfo
     * @param siteId
     */
    private void setTemplateURL(TemplateEmailInfoImpl templateEmailInfo, final String siteId) {

        if ( siteId == null) {
        	// Do something site non-specific here
            //templateEmailInfo.setTemplateURL(something);
        } else {
        	// Do something site specific here
            //templateEmailInfo.setTemplateURL(something);
        }
    }


    /**
     * Set the appropriate message subject on the passed in TemplateEmailInfoImpl obj
     * based on the siteID.  Should be change to meet requirements, if needed.
     * @param templateEmailInfo
     * @param siteId
     */
    private void setMessageSubject(TemplateEmailInfoImpl templateEmailInfo, String siteId) {
        if (siteId == null) {
        	// Do something site non-specific here
        	//templateEmailInfo.setMessageSubject(something);
        }
        else {
        	// DO something site specific here
            //templateEmailInfo.setMessageSubject(something);
        }
    }

    //getters & setters

    public TemplateEmailSender getTemplateEmailSender() {
        return templateEmailSender;
    }

    public void setTemplateEmailSender(TemplateEmailSender templateEmailSender) {
        this.templateEmailSender = templateEmailSender;
    }

    public boolean isSendingEmailsInSeparateThread() {
        return sendingEmailsInSeparateThread;
    }

    public void setSendingEmailsInSeparateThread(boolean sendingEmailsInSeparateThread) {
        this.sendingEmailsInSeparateThread = sendingEmailsInSeparateThread;
    }

    public ProfileTools getProfileTools() {
        return profileTools;
    }

    public void setProfileTools(ProfileTools profileTools) {
        this.profileTools = profileTools;
    }

    public boolean isMailingPersisted() {
        return mailingPersisted;
    }

    public void setMailingPersisted(boolean mailingPersisted) {
        this.mailingPersisted = mailingPersisted;
    }

    public String getPrefixURL() {
        return prefixURL;
    }

    public void setPrefixURL(String prefixURL) {
        this.prefixURL = prefixURL;
    }

    public static void sendJobNotificationEmail ( String subject, String body ) {
    	
    }

    /**
     * Send password reset email to the specified user.
     *
     * @param user
     * @param newPassword
     * @throws EmailException
     */
    public static void sendPasswordResetEmail(RepositoryItem user, String newPassword) throws EmailException {

        EmailEvent emailEvent = new EmailEvent();
        String login = (String) user.getPropertyValue("login");
        String userEmail = (String) user.getPropertyValue("email");
        String subject = "myaccount.passwordReset.subject";
        String fromAddress = "site.email.defaultFrom";
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("login", login);
        model.put("password", newPassword);


        String emailBody = null;

        Message msg;

        try {
            msg = MimeMessageUtils.createMessage(fromAddress, subject);
            MimeMessageUtils.setRecipient(msg, Message.RecipientType.TO, userEmail);
            ContentPart[] content = { new ContentPart(emailBody, "text/plain") };
            MimeMessageUtils.setContent(msg, content);
            emailEvent.setMessage(msg);

            emailEvent.setRecipient(userEmail);
            emailEvent.setFrom(fromAddress);
            emailEvent.setBody(emailBody);
            emailEvent.setSubject(subject);

            EmailUtil emailService = EmailUtil.getInstance();
            emailService.sendEmailEvent(emailEvent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }


	public String getServerName() {
		return serverName;
	}


	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}
