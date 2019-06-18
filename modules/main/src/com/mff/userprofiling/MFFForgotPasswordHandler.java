package com.mff.userprofiling;

import java.io.IOException;

import javax.servlet.ServletException;

import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;
import com.mff.password.reset.PasswordResetTokenException;
import com.mff.password.reset.RepositoryResetTokenManager;
import com.mff.util.RecaptchaProcessor;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ForgotPasswordHandler;

public class MFFForgotPasswordHandler extends ForgotPasswordHandler {

	static final String MSG_INVALID_EMAIL = "invalidEmailAddress";
	private MFFFormExceptionGenerator mFormExceptionGenerator;
	private MFFEmailManager mEmailManager;
	private RepositoryResetTokenManager mResetTokenManager;
	private String mResetToken = null;
	private RecaptchaProcessor mCaptchaProcessor;
	public static final String CAPTCHA_PARAM = "g-recaptcha-response";
	
	protected void preResetPassword(DynamoHttpServletRequest pRequest,  DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		String login = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		String email = getStringValueProperty(getProfileTools().getPropertyManager().getEmailAddressPropertyName());
		setValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName(), login.toLowerCase());
		vlogDebug("validating login email in pre reset -- {0}", login);
		validateEmail(login);
		 MutableRepositoryItem[] users = lookupUsers(login, email);
		//Captcha Validation
		validateCaptcha(pRequest);
	    
		if (users == null) {
	      getFormExceptionGenerator().generateException(MFFConstants.MSG_ERROR_PROFILE_NOTFOUND, true, this, pRequest);
	    } 
	}
	
	/**
	 * This method is used to validate the google recaptcha
	 * @param pRequest
	 */
	protected void validateCaptcha(DynamoHttpServletRequest pRequest) {
		String captchaResponse = pRequest.getParameter(CAPTCHA_PARAM);
	    
	    if (captchaResponse == null || StringUtils.isBlank(captchaResponse)) {
	      getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_REQUIRED, true, this, pRequest);
	    }
	    
	    if (!getFormError()) {
		    try {
		      boolean captchaValidated = getCaptchaProcessor().doProcessCaptcha(pRequest, pRequest.getRemoteAddr(), captchaResponse);
		      if(!captchaValidated){
		        getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_VALIDATION_ERROR, true, this, pRequest);
		      }
		    } catch (Exception e) {
		      vlogError(e, "There is an error while validating Captcha");
		      getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_VALIDATION_ERROR, true, this, pRequest);
		    }
	    }
	}
	
	protected void postResetPassword(DynamoHttpServletRequest pRequest,  DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String login = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		if(StringUtils.isNotBlank(login)){
			if(null!=mResetToken){
				getEmailManager().sendResetPasswordMail(login, mResetToken);
			}else{
				getFormExceptionGenerator().generateException(MSG_ERR_SENDING_EMAIL, true, this, pRequest);
			}
			
		}else{
			getFormExceptionGenerator().generateException(MSG_INVALID_EMAIL, false, this, pRequest);
		}
	}
	
	protected void resetPassword(DynamoHttpServletRequest pRequest,  DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String login = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		vlogDebug("email for reset password -- {0}",login);
		if(StringUtils.isNotBlank(login)){
			try {
				mResetToken = getResetTokenManager().generateToken(login);
				vlogDebug("reset token in ForgotPassword -- {0}",mResetToken);
			} catch (PasswordResetTokenException e) {
				vlogError(e.getMessage(), "Error Generating reset token email -- {0}", login);
			}
		}else{
			getFormExceptionGenerator().generateException(MSG_INVALID_EMAIL, false, this, pRequest);
		}
	}
	
	/**
	 * This method is used to validate the email rules
	 * @param pLogin
	 */
	private void validateEmail(String pLogin) {
		MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();

		if (StringUtils.isBlank(pLogin) || !profileTools.validateEmailAddress(pLogin)) {
			addFormException(new DropletException(MSG_INVALID_EMAIL));
		}
	}
	
	public Boolean handleResetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)throws ServletException, IOException{
		
		preResetPassword(pRequest, pResponse);
		if(!getFormError()){
			resetPassword(pRequest, pResponse);
		}
		if(!getFormError()){
			postResetPassword(pRequest, pResponse);
		}
		return checkFormRedirect(getForgotPasswordSuccessURL(), getForgotPasswordErrorURL(), pRequest, pResponse);
	}
	
	public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

	public void setFormExceptionGenerator(
		MFFFormExceptionGenerator pFormExceptionGenerator) {
	    mFormExceptionGenerator = pFormExceptionGenerator;
	}

	public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}

	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

	public RepositoryResetTokenManager getResetTokenManager() {
		return mResetTokenManager;
	}

	public void setResetTokenManager(RepositoryResetTokenManager pResetTokenManager) {
		mResetTokenManager = pResetTokenManager;
	}

	public RecaptchaProcessor getCaptchaProcessor() {
		return mCaptchaProcessor;
	}

	public void setCaptchaProcessor(RecaptchaProcessor pCaptchaProcessor) {
		mCaptchaProcessor = pCaptchaProcessor;
	}

	
}

