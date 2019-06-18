package com.mff.commerce.order.purchase;

import java.io.IOException;
import javax.servlet.ServletException;

import com.firstdata.payment.MFFGiftCardInfo;
import com.mff.commerce.payment.MFFGiftCardManager;
import com.mff.constants.MFFConstants;
import com.mff.util.RecaptchaProcessor;

import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.droplet.MFFFormExceptionGenerator;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Form handler for checking gift card balance.
 *
 */
public class MFFGiftCardBalanceFormHandler extends GenericFormHandler {

	private MFFGiftCardManager mGiftCardManager = null;
	private MFFFormExceptionGenerator mFormExceptionGenerator;
	private RecaptchaProcessor mCaptchaProcessor;
	
	private String mGiftCardBalanceSuccessURL;
	private String mGiftCardBalanceErrorURL;
  private double mGiftCardBalance;
  private String mGiftCardNumber;
  private String mGiftCardPin;
  
  public static final String CAPTCHA_PARAM = "g-recaptcha-response";
  
	/**
	 * This method is called if the user checks a GC balance.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleCheckGiftCardBalance(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("Entering: handleCheckGiftCardBalance");
	// If GC Number is missing, add error:
    if (getGiftCardNumber() == null || StringUtils.isBlank(getGiftCardNumber())) {
      getFormExceptionGenerator().generateException(MFFConstants.NO_GIFT_CARD_NUMBER, true, this, pRequest);
      return checkFormRedirect(null, getGiftCardBalanceErrorURL(), pRequest, pResponse);
    }

    if (getGiftCardPin() == null || StringUtils.isBlank(getGiftCardPin())) {
      getFormExceptionGenerator().generateException(MFFConstants.NO_GIFT_CARD_PIN, true, this, pRequest);
      return checkFormRedirect(null, getGiftCardBalanceErrorURL(), pRequest, pResponse);
    }

    String captchaResponse = pRequest.getParameter(CAPTCHA_PARAM);
    
    if (captchaResponse == null || StringUtils.isBlank(captchaResponse)) {
      getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_REQUIRED, true, this, pRequest);
      return checkFormRedirect(null, getGiftCardBalanceErrorURL(), pRequest, pResponse);
    }
    
    if (getFormError()) {
      return checkFormRedirect(getGiftCardBalanceSuccessURL(), getGiftCardBalanceErrorURL(), pRequest, pResponse);
    }
    
    try {
      boolean captchaValidated = getCaptchaProcessor().doProcessCaptcha(pRequest, pRequest.getRemoteAddr(), captchaResponse);
      if(!captchaValidated){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_VALIDATION_ERROR, true, this, pRequest);
        return checkFormRedirect(getGiftCardBalanceSuccessURL(), getGiftCardBalanceErrorURL(), pRequest, pResponse);
      }
    } catch (Exception e) {
      vlogError(e, "There is an error while validating Captcha");
      getFormExceptionGenerator().generateException(MFFConstants.MSG_CAPTCHA_VALIDATION_ERROR, true, this, pRequest);
      return checkFormRedirect(getGiftCardBalanceSuccessURL(), getGiftCardBalanceErrorURL(), pRequest, pResponse);
    }
    
    vlogDebug("handleCheckGiftCardBalance: Making GC Balance Inquiry Call");
		MFFGiftCardInfo balanceInquiryResult = getGiftCardManager().checkGiftCardBalance(getGiftCardNumber(), getGiftCardPin());
		
		if(balanceInquiryResult == null || !balanceInquiryResult.isTransactionSuccess()){
		      boolean isMillsMoneyEnabled=false;
		      SiteContextManager.getCurrentSite();
		      Site curSite = SiteContextManager.getCurrentSite();
		      if (curSite != null){
		    	  isMillsMoneyEnabled = (Boolean)curSite.getPropertyValue("isEnableMillsMoney");
		      }
		      if(isMillsMoneyEnabled) {
		    	  getFormExceptionGenerator().generateException(MFFConstants.INVALID_MILLS_MONEY, true, this, pRequest);
		      } else {
		    	  getFormExceptionGenerator().generateException(MFFConstants.INVALID_GIFT_CARD, true, this, pRequest);
		      }
		  
		  return checkFormRedirect(getGiftCardBalanceSuccessURL(), getGiftCardBalanceErrorURL(), pRequest, pResponse);
		}
		
		setGiftCardBalance(balanceInquiryResult.getBalanceAmount());
		
		vlogDebug("Exiting: handleCheckGiftCardBalance");
		return checkFormRedirect(getGiftCardBalanceSuccessURL(), getGiftCardBalanceErrorURL(), pRequest, pResponse);
	}

  public MFFGiftCardManager getGiftCardManager() {
    return mGiftCardManager;
  }

  public void setGiftCardManager(MFFGiftCardManager pGiftCardManager) {
    mGiftCardManager = pGiftCardManager;
  }

  public String getGiftCardNumber() {
    return mGiftCardNumber;
  }

  public void setGiftCardNumber(String pGiftCardNumber) {
    mGiftCardNumber = pGiftCardNumber;
  }

  public String getGiftCardPin() {
    return mGiftCardPin;
  }

  public void setGiftCardPin(String pGiftCardPin) {
    mGiftCardPin = pGiftCardPin;
  }

  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

  public String getGiftCardBalanceSuccessURL() {
    return mGiftCardBalanceSuccessURL;
  }

  public void setGiftCardBalanceSuccessURL(String pGiftCardBalanceSuccessURL) {
    mGiftCardBalanceSuccessURL = pGiftCardBalanceSuccessURL;
  }

  public String getGiftCardBalanceErrorURL() {
    return mGiftCardBalanceErrorURL;
  }

  public void setGiftCardBalanceErrorURL(String pGiftCardBalanceErrorURL) {
    mGiftCardBalanceErrorURL = pGiftCardBalanceErrorURL;
  }

  public double getGiftCardBalance() {
    return mGiftCardBalance;
  }

  public void setGiftCardBalance(double pGiftCardBalance) {
    mGiftCardBalance = pGiftCardBalance;
  }

  public RecaptchaProcessor getCaptchaProcessor() {
    return mCaptchaProcessor;
  }

  public void setCaptchaProcessor(RecaptchaProcessor pCaptchaProcessor) {
    mCaptchaProcessor = pCaptchaProcessor;
  }
}