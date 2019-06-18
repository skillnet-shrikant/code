package com.mff.browse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.constants.MFFConstants;
import com.mff.email.MFFEmailManager;
import com.mff.userprofiling.MFFProfileTools;

public class MFFEmailFormHandler extends GenericFormHandler {

	private String mFriendEmail;
	private String mYourName;
	private String mYourEmail;
	private String mMessage;
	private String mSkuId;
	private String mProductId;
	private String mSendPDPEmailSuccessURL;
	private String mSendPDPEmailErrorURL;
	private String mSendBackInStockEmailSuccessURL;
	private String mSendBackInStockEmailErrorURL;
	private MFFCatalogTools mCatalogTools;
	private MFFEmailManager mEmailManager;
	private MFFProfileTools mProfileTools;
	
	/**
	 * @return the profileTools
	 */
	public MFFProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param pProfileTools the profileTools to set
	 */
	public void setProfileTools(MFFProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	/**
	 * @return the friendEmail
	 */
	public String getFriendEmail() {
		return mFriendEmail;
	}

	/**
	 * @param pFriendEmail the friendEmail to set
	 */
	public void setFriendEmail(String pFriendEmail) {
		mFriendEmail = pFriendEmail;
	}

	/**
	 * @return the yourName
	 */
	public String getYourName() {
		return mYourName;
	}

	/**
	 * @param pYourName the yourName to set
	 */
	public void setYourName(String pYourName) {
		mYourName = pYourName;
	}

	/**
	 * @return the yourEmail
	 */
	public String getYourEmail() {
		return mYourEmail;
	}

	/**
	 * @param pYourEmail the yourEmail to set
	 */
	public void setYourEmail(String pYourEmail) {
		mYourEmail = pYourEmail;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * @param pMessage the message to set
	 */
	public void setMessage(String pMessage) {
		mMessage = pMessage;
	}

	public String getSkuId() {
		return mSkuId;
	}

	public void setSkuId(String pSkuId) {
		mSkuId = pSkuId;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return mProductId;
	}

	/**
	 * @param pProductId the productId to set
	 */
	public void setProductId(String pProductId) {
		mProductId = pProductId;
	}

	/**
	 * @return the sendPDPEmailSuccessURL
	 */
	public String getSendPDPEmailSuccessURL() {
		return mSendPDPEmailSuccessURL;
	}

	/**
	 * @param pSendPDPEmailSuccessURL the sendPDPEmailSuccessURL to set
	 */
	public void setSendPDPEmailSuccessURL(String pSendPDPEmailSuccessURL) {
		mSendPDPEmailSuccessURL = pSendPDPEmailSuccessURL;
	}

	/**
	 * @return the sendPDPEmailErrorURL
	 */
	public String getSendPDPEmailErrorURL() {
		return mSendPDPEmailErrorURL;
	}

	/**
	 * @param pSendPDPEmailErrorURL the sendPDPEmailErrorURL to set
	 */
	public void setSendPDPEmailErrorURL(String pSendPDPEmailErrorURL) {
		mSendPDPEmailErrorURL = pSendPDPEmailErrorURL;
	}

	public String getSendBackInStockEmailSuccessURL() {
		return mSendBackInStockEmailSuccessURL;
	}

	public void setSendBackInStockEmailSuccessURL(
			String pSendBackInStockEmailSuccessURL) {
		mSendBackInStockEmailSuccessURL = pSendBackInStockEmailSuccessURL;
	}

	public String getSendBackInStockEmailErrorURL() {
		return mSendBackInStockEmailErrorURL;
	}

	public void setSendBackInStockEmailErrorURL(
			String pSendBackInStockEmailErrorURL) {
		mSendBackInStockEmailErrorURL = pSendBackInStockEmailErrorURL;
	}

	/**
	 * @return the catalogTools
	 */
	public MFFCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(MFFCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the emailManager
	 */
	public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}

	/**
	 * @param pEmailManager the emailManager to set
	 */
	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

	/**
	 * This method is used to send email of Product to Friend
	 *    
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean handleSendPDPEmil(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("MFFEmailFormHandler :: handleSendPDPEmil :: START");
		preSendPDPEmil(pRequest, pResponse);
		if(getFormError()){
			return checkFormRedirect(getSendPDPEmailSuccessURL(), getSendPDPEmailErrorURL(), pRequest, pResponse);
		}
		Map emailData = new HashMap();
		RepositoryItem product = null;
		try {
			vlogDebug("ProductId :: "+getProductId());
			product = getCatalogTools().findProduct(getProductId(), MFFConstants.PRODUCT);
			emailData.put("productId", getProductId());
			emailData.put("productName", product.getPropertyValue("description"));
			emailData.put("OnlineItem", product.getRepositoryId());
			emailData.put("friendEmail", getFriendEmail());
			emailData.put("yourEmail", getYourEmail());
			emailData.put("yourName", getYourName());
			emailData.put("message", getMessage());
			
			getEmailManager().sendAFriendEmail(emailData);
			
		} catch (RepositoryException e) {
			vlogError("RepositoryException occurred :: "+e.getMessage());
		}/* catch (PriceListException e) {
			vlogError("PriceListException occurred :: "+e.getMessage());
		}*/
		vlogDebug("MFFEmailFormHandler :: handleSendPDPEmil :: END");
		return checkFormRedirect(getSendPDPEmailSuccessURL(), getSendPDPEmailErrorURL(), pRequest, pResponse);
	}


	private void preSendPDPEmil(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		vlogDebug("MFFEmailFormHandler :: preSendPDPEmil :: START");
		boolean isValidEmail = true;
		if(StringUtils.isBlank(getProductId())){
			addFormException(new DropletException(MFFConstants.PRODUCT_ID_MISSING));
		}
		if(StringUtils.isBlank(getFriendEmail())){
			addFormException(new DropletException(MFFConstants.FRIEND_EMAIL_MISSING));
		} else{
			isValidEmail = getProfileTools().validateEmailAddress(getFriendEmail());
			if(!isValidEmail){
				addFormException(new DropletException(MFFConstants.INVALID_FRIEND_EMAIL));
			}
		}
		if(StringUtils.isBlank(getYourEmail())){
			addFormException(new DropletException(MFFConstants.YOUR_EMAIL_MISSING));
		} else{
			isValidEmail = getProfileTools().validateEmailAddress(getYourEmail());
			if(!isValidEmail){
				addFormException(new DropletException(MFFConstants.INVALID_YOUR_EMAIL));
			}
		}
		if(StringUtils.isBlank(getYourName())){
			addFormException(new DropletException(MFFConstants.YOUR_NAME_MISSING));
		}
		vlogDebug("MFFEmailFormHandler :: preSendPDPEmil :: END");
	}
	
	public boolean handleSendBackInStockEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if(!getFormError()){
			preSendBackInStockEmail(pRequest, pResponse);
		}
		if(!getFormError()){
			sendBackInStockEmail(pRequest, pResponse);
		}
		if(!getFormError()){
			postSendBackInStockEmail(pRequest, pResponse);
		}
		return checkFormRedirect(getSendBackInStockEmailSuccessURL(), getSendBackInStockEmailErrorURL(), pRequest, pResponse);
	}

	
	private void sendBackInStockEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		MutableRepository lProfileRepository = getProfileTools().getProfileRepository();
		try {
			MutableRepositoryItem lNotifyItem=lProfileRepository.createItem("backInStockNotifyItem");
			lNotifyItem.setPropertyValue("emailAddress", getYourEmail());
			lNotifyItem.setPropertyValue("catalogRefId", getSkuId());
			lNotifyItem.setPropertyValue("productId", getProductId());
			lProfileRepository.addItem(lNotifyItem);
		} catch (RepositoryException e) {
			vlogError("Error while setting backInStockNotifyItem : error {0}", e.getMessage());
			addFormException(new DropletException(e.getMessage()));
		}
	}

	private void preSendBackInStockEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {

		if (StringUtils.isBlank(getYourEmail()) || !getProfileTools().validateEmailAddress(getYourEmail())) {
			addFormException(new DropletException("Invalid Email Address"));
		}
	}
	private void postSendBackInStockEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		
	}

}
