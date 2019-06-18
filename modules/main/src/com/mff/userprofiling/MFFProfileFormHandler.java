package com.mff.userprofiling;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import mff.MFFEnvironment;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.projects.b2cstore.B2CProfileFormHandler;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.IdentityManager;
import atg.security.SecurityException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.PasswordChangeException;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.address.AddressTools;

import com.aci.commerce.service.AciService;
import com.aci.constants.AciConstants;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.aci.pipeline.exception.AciPipelineException;
import com.mff.commerce.order.MFFOrderDetailHelper;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.purchase.MFFCheckoutManager;
import com.mff.commerce.payment.creditcard.MFFExtendableCreditCardTools;
import com.mff.commerce.profile.MFFPropertyManager;
import com.mff.commerce.util.MFFAddressValidator;
import com.mff.constants.MFFConstants;
import com.mff.droplet.InlineFormErrorSupport;
import com.mff.droplet.MFFInlineDropletFormException;
import com.mff.email.MFFEmailManager;
import com.mff.locator.StoreLocatorTools;
import com.mff.password.reset.PasswordResetTokenException;
import com.mff.password.reset.RepositoryResetTokenManager;
import com.mff.userprofiling.util.TaxExemptionInfo;
import com.mff.util.MFFUtils;
import com.mff.zip.MFFZipcodeHelper;

public class MFFProfileFormHandler extends B2CProfileFormHandler implements InlineFormErrorSupport {

	public static String MFF_RESOURCE_BUNDLE = "resource_en";

	/**
	 * Invalid password length message key.
	 */
	static final String MSG_INVALID_PASSWORD_LENGTH = "invalidPasswordLength";
	/**
	 * Invalid e-mail address message key.
	 */

	static final String TOO_MANY_ATTEMPTS = "Too many login attempts. Try again later.";
	static final String MSG_INVALID_EMAIL = "invalidEmailAddress";
	static final String MSG_EMAIL_EXISTS = "User already exists with provided email";
	static final String MSG_EMAIL_NOT_MATCH = "Confirm email is not same exactly email.";
	protected static final String SECURITY_STATUS_PROPERTY_NAME = "securityStatus";
	protected static final String NO = "no";
	protected static final String YES = "yes";
	protected static final String RESOURCE_BUNDLE = "atg.commerce.profile.UserMessages";
	protected static final String MSG_ERR_CREATING_ADDRESS = "errorCreatingAddress";
	protected static final String MSG_ERR_CREATING_TAX_EXMP = "errorCreatingTaxExmp";
	protected static final String MSG_ERR_DELETING_ADDRESS = "errorDeletingAddress";
	protected static final String MSG_ERR_UPDATING_ADDRESS = "errorUpdatingAddress";
	protected static final String MSG_DUPLICATE_ADDRESS_NICKNAME = "errorDuplicateNickname";
	protected static final String MSG_NICKNAME_MAX_LEN = "error.maxlength.nickname";
	protected static final String MSG_ERR_MODIFYING_NICKNAME = "errorModifyingNickname";
	protected static final String MSG_DUPLICATE_USER = "userAlreadyExists";
	protected static final String DATE_FORMAT = "M/d/yyyy";
	protected static final String EDIT_VALUE = "editValue";
	static final String MSG_MISSING_REQUIRED_PROPERTY = "missingRequiredProperty";
	static final String MSG_INVALID_ZIP = "invalidZip";
	static final String ZIP_CODE_PATTERN = "\\d{5}(-\\d{4})?";
	static final String CONST_INV_PWD_ATMPT_CNT = "invalidPwdAttemptCount";

	private Map mBillAddrValue = new HashMap();
	private String mNicknameValueMapKey = "nickname";
	private String mAddressIdValueMapKey = "addressId";
	private String mNewNicknameValueMapKey = "newNickname";
	private String mShippingAddressNicknameMapKey = "shippingAddrNickname";
	private String[] mAddressProperties = new String[] {"firstName", "middleName",
		    "lastName", "address1","address2", "city", "state", "postalCode", "country","ownerId"};
	private String[] mTaxExmpnProperties = new String[] {"nickName", "classificationId",
		    "taxId", "orgName","businessDesc", "merchandise", "taxCity", "taxState", "organizationAddress"};

	private String[] mRequiredAddressProperties;
	private String[] mRequiredTaxExmpProperties;

	private Map<String, Object> mEditValue = new HashMap<String, Object>();
	private boolean mTaxExemptionAgreed;
	private String mRemoveAddressKey;
	private String mEditAddress;
	private String mDefaultShippingAddress;
	private String mDefaultCard;
	private boolean mUseShippingAddressAsDefault;
	private String mLoginEmailAddress;
	private String mEmailAddress;
	private String mUpdateEmailSuccessURL;
	private String mUpdateEmailErrorURL;
	private String mNewCustomerEmailAddress;
	private Order mOrder;
	private String mAnonymousEmailAddress;
	private String mPreviousEmailAddress;
	private String mNewAddressSuccessURL;
	private String mNewAddressErrorURL;
	private String mUpdateAddressSuccessURL;
	private String mUpdateAddressErrorURL;
	private String mRemoveAddressSuccessURL;
	private String mRemoveAddressErrorURL;
	private ShippingGroupMapContainer mShippingGroupMapContainer;
	private String mNewAddressId;
	private MFFOrderDetailHelper mOrderDetailHelper;
	private MFFExtendableCreditCardTools mCreditCardTools;
	private Map<String, String> mCreditCardTypes;
	private String mEditTaxExemption;
	private String mDefaultTaxExemption;
	private String mRemoveTaxExemption;
	private String mAddTaxExmpSuccessURL;
	private String mAddTaxExmpErrorURL;
	private String mUpdateTaxExmpSuccessURL;
	private String mUpdateTaxExmpErrorURL;
	private String mRemoveTaxExmpSuccessURL;
	private String mRemoveTaxExmpErrorURL;
	private String mAssignDefaultTaxExmpSuccessUrl;
	private String mAssignDefaultTaxExmpErrorUrl;
	private String mUpdateCardSuccessURL;
	private String mUpdateCardErrorURL;
	private String mExpressCheckoutPreferencesSuccessURL;
	private String mExpressCheckoutPreferencesErrorURL;

	private String mTrackOrderErrorURL;
	private String mTrackOrderSuccessURL;

	private boolean mAddressMatched;
	private boolean mCityStateZipMatched = true;
	private Map mSuggestedAddresses;
	private MFFEmailManager mEmailManager;
	private AciService mAciService;
	private MFFCheckoutManager mCheckoutManager;
	private boolean mCartMerged;
	private long mItemCountPreLogin;
	private RepositoryResetTokenManager mResetTokenManager;
	private int mAllowedInvalidAttempts;
	private MFFEnvironment mEnvironment;
	private MFFZipcodeHelper mZipCodeHelper;
	private MFFFormExceptionGenerator mFormExceptionGenerator;
	private StoreLocatorTools mStoreLocatorTools;
	private String mHomeStoreChosen;
	private String mUpdateHomeStoreSuccessURL;
	private String mUpdateHomeStoreErrorURL;
	private MFFAddressValidator mAddressValidator;
	private String mForceResetPasswordMessage;
	private EmployeeManager employeeManager;
	
	
	public EmployeeManager getEmployeeManager() {
		return employeeManager;
	}

	public void setEmployeeManager(EmployeeManager pEmployeeManager) {
		employeeManager = pEmployeeManager;
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
	 * @return the addressMatched
	 */
	public boolean isAddressMatched() {
		return mAddressMatched;
	}

	/**
	 * @param pAddressMatched the addressMatched to set
	 */
	public void setAddressMatched(boolean pAddressMatched) {
		mAddressMatched = pAddressMatched;
	}

	/**
	 * @return the addressVerified
	 */
	public boolean isAddressVerified() {
		return mAddressVerified;
	}

	/**
	 * @param pAddressVerified the addressVerified to set
	 */
	public void setAddressVerified(boolean pAddressVerified) {
		mAddressVerified = pAddressVerified;
	}

	private boolean mAddressVerified;


	/**
	 * @param pCreditCardTypes the creditCardTypes to set
	 */
	public void setCreditCardTypes(Map<String, String> pCreditCardTypes) {
		mCreditCardTypes = pCreditCardTypes;
	}

	/**
	 * @return the creditCardTools
	 */
	public MFFExtendableCreditCardTools getCreditCardTools() {
		return mCreditCardTools;
	}

	/**
	 * @param pCreditCardTools the creditCardTools to set
	 */
	public void setCreditCardTools(MFFExtendableCreditCardTools pCreditCardTools) {
		mCreditCardTools = pCreditCardTools;
	}

	/**
	 * Override OOTB method so that the user's email address is copied over to
	 * their login field.
	 *
	 */
	public RepositoryItem createUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		ProfileTools profileTools = getProfileTools();

		// Copy over user email to their login field
		PropertyManager propertyManager = profileTools.getPropertyManager();

		String emailPropertyName = propertyManager.getEmailAddressPropertyName();
		String email = getStringValueProperty(emailPropertyName);

		// Store login in lower case to support case-insensitive logins
		String loginPropertyName = propertyManager.getLoginPropertyName();
		setValueProperty(loginPropertyName, email.toLowerCase());

		return super.createUser(pRequest, pResponse);
	}

	/**
	 * Operation called just before the user creation process is started.
	 *
	 * @param pRequest DynamoHttpServletRequest
	 * @param pResponse DynamoHttpServletResponse
	 * @exception ServletException
	 * @exception IOException
	 */
	protected void preCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		super.preCreateUser(pRequest, pResponse);
		// validation
		validateEmail(pRequest);
		validatePassword(getStringValueProperty(getPropertyManager().getPasswordPropertyName()), pRequest, pResponse);

	}


	/**
	 * This is used to create the Invalid login attempts aitem with the ProfileId.
	 *
	 * @param pRequest DynamoHttpServletRequest
	 * @param pResponse DynamoHttpServletResponse
	 * @exception ServletException
	 * @exception IOException
	 */
	protected void postCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		super.postCreateUser(pRequest, pResponse);
		if(!getFormError()){
			getEmailManager().sendAccountCreation(getProfileItem(), pRequest.getLocale());
		}
		getEmployeeManager().isValidateEmployee(getProfile());
	}

	/**
	 * @param pRequest
	 *
	 */
	private void updateLoginAttemptCount(DynamoHttpServletRequest pRequest) {

		String login = getStringValueProperty(getPropertyManager().getLoginPropertyName());
		String password = getStringValueProperty(getPropertyManager().getPasswordPropertyName());

		boolean authSuccessful = false;
        IdentityManager identityManager = getUserLoginManager().getIdentityManager(pRequest);
		RepositoryItem user = getProfileTools().getItemFromEmail(login.toLowerCase());

		if (user != null) {

			try {
				authSuccessful = identityManager.checkAuthenticationByPassword(login, password);

				int loginattempts = 0;
				if (authSuccessful) {
					Object obj= getProfile().getPropertyValue(CONST_INV_PWD_ATMPT_CNT);
					int count=0;
					if(obj!=null){
						count=((Integer)obj).intValue();
					}
					else {
						count=0;
					}
					vlogInfo ("User Id: " +user.getRepositoryId()+" LoginAttemptCount: "+count+" LoginAttempt Status: Success.");
					getProfile().setPropertyValue(CONST_INV_PWD_ATMPT_CNT, 0);
				} else {
					Object obj = getProfile().getPropertyValue(CONST_INV_PWD_ATMPT_CNT);
					if(obj != null){
						loginattempts = ((Integer)obj).intValue();
						loginattempts ++;
						vlogInfo ("User Id: " +user.getRepositoryId()+" LoginAttemptCount: "+loginattempts+" LoginAttempt Status: Failure.");
						if (loginattempts == getAllowedInvalidAttempts()) {
							addFormException(new DropletException(TOO_MANY_ATTEMPTS));
							return;
						}
					}
					vlogInfo ("User Id: " +user.getRepositoryId()+" LoginAttemptCount: "+loginattempts+" LoginAttempt Status: Failure.");
					getProfile().setPropertyValue(CONST_INV_PWD_ATMPT_CNT, loginattempts);
					addFormException(new DropletException("The entered username or password is invalid"));
				}

			} catch (SecurityException e) {
				vlogError("updateLoginAttemptCount(): SecurityException during preLogin: " + e, e);
			}
		}
	}


	/**
	 * Validates email address
	 *
	 * @param pRequest
	 * @param propertyManager
	 */
	private void validateEmail(DynamoHttpServletRequest pRequest) {
		String email = getStringValueProperty(getPropertyManager().getEmailAddressPropertyName());
		MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();

		if (StringUtils.isBlank(email) || !profileTools.validateEmailAddress(email)) {
			addFormException(new DropletException(MSG_INVALID_EMAIL));
		}
	}

	/**
	 * Validates password. If password is not valid adds form exception.
	 *
	 * @param pPassword
	 *            password to check
	 * @param pRequest
	 *            http request
	 * @param pResponse
	 *            http response
	 * @return true if password
	 */
	protected boolean validatePassword(String pPassword, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
		if (pPassword.length() != 0) {
			if (!(profileTools.isValidPasswordLength(pPassword))) {
				addFormException(new DropletException(MSG_INVALID_PASSWORD_LENGTH));
				return false;
			}
		}
		return true;
	}


	/**
	 * This method is overridden to count the invalid password attempts and lock the account based on the count
	 */
	@Override
	protected void preLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String login = getStringValueProperty(getPropertyManager().getLoginPropertyName());
		validateEmail(login);
		setValueProperty(getPropertyManager().getLoginPropertyName(), login.toLowerCase());

		if(isPasswordResetRequired(login, pRequest, pResponse)){
			return;
		}
		if (isResetRequiredForLegacyUser(login, pRequest, pResponse)){
			return;
		}

		updateLoginAttemptCount(pRequest);

		if (!getFormError()) {
			//set item count before login
			//this property will be used in postLogin to decide on merge cart
			setItemCountPreLogin(getCheckoutManager().getOrder().getTotalCommerceItemCount());
			super.preLoginUser(pRequest, pResponse);
		}
	}

	
	private boolean isResetRequiredForLegacyUser(String pLogin, DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug ("isResetRequiredForLegacyUser(): pLogin: " + pLogin);

		boolean resetRequired = false;
		RepositoryItem userItem = getProfileTools().getItem(pLogin, null);

		vlogDebug ("isResetRequiredForLegacyUser(): userItem: " + userItem);

		if (userItem != null) {
			boolean isLegacy = (boolean) userItem.getPropertyValue("isLegacy");

			vlogDebug("isResetRequiredForLegacyUser(): isLegacy: " + isLegacy);

			if (isLegacy) {

				String password = (String) userItem.getPropertyValue(getPropertyManager().getPasswordPropertyName());

				if (StringUtils.isBlank(password)){
					vlogDebug("isResetRequiredForLegacyUser(): password is null.");
					resetRequired = true;
					boolean emailSent = false;

					try {
						emailSent = sendResetPasswordEmail(pLogin);
					} catch (PasswordResetTokenException e) {
						vlogError("isResetRequiredForLegacyUser(): Exception while generating reset password:" + e,e);
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_RESET_TOKEN_EMPTY,null)));
					}
					if (emailSent){
						if(getForceResetPasswordMessage()!=null){
							addFormException(new DropletException(getForceResetPasswordMessage()));
						}
						else {
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_LEGACY_FORCE_PWD_RESET,null)));
						}
					} else{
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_RESET_TOKEN_EMPTY,null)));
					}
				}
			}

		}
		return resetRequired;
	}

	private boolean isPasswordResetRequired(String pLogin, DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug ("isPasswordResetRequired(): pLogin: " + pLogin);

		boolean resetRequired = false;
		RepositoryItem userItem = getProfileTools().getItem(pLogin, null);

		vlogDebug ("isPasswordResetRequired(): userItem: " + userItem);

		if (userItem != null) {
			boolean forceResetPassword = (boolean) userItem.getPropertyValue("forcePasswordReset");

			vlogDebug("isPasswordResetRequired(): forceResetPassword: " + forceResetPassword);

			if (forceResetPassword) {
				resetRequired = true;
				boolean emailSent = false;

				try {
					emailSent = sendResetPasswordEmail(pLogin);
				} catch (PasswordResetTokenException e) {
					vlogError("isPasswordResetRequired(): Exception while generating reset password:" + e,e);
					addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_RESET_TOKEN_EMPTY,null)));
				}
				if (emailSent){
					if(getForceResetPasswordMessage()!=null){
						addFormException(new DropletException(getForceResetPasswordMessage()));
					}
					else {
					addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_LEGACY_FORCE_PWD_RESET,null)));
					}
					
				} else{
					addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_RESET_TOKEN_EMPTY,null)));
				}
				
			}

		}
		return resetRequired;
	}

	
	protected boolean sendResetPasswordEmail(String pLogin) throws PasswordResetTokenException {

		boolean sentEmail = false;
		String resetToken = getResetTokenManager().generateToken(pLogin);

		if (StringUtils.isNotBlank(resetToken)) {
			getEmailManager().sendResetPasswordMail(pLogin, resetToken);
			sentEmail = true;
		} else {
			vlogWarning("sendResetPasswordEmail(): resetToken is null.");
		}
		return sentEmail;
	}

	/**
	 * Overriding this method to set the autoLogin property to profile
	 */
	@Override
	protected void postLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		super.postLoginUser(pRequest, pResponse);
		getCheckoutManager().resetCheckoutValues();
		//verify item count after login, if count changes mark it as cart merge
	    long lItemCountPostLogin = getCheckoutManager().getOrder().getTotalCommerceItemCount();
	    vlogDebug("Existing Cart Count:{0}, Current Cart Count:{1}",getItemCountPreLogin(), lItemCountPostLogin);
	    if(getItemCountPreLogin() > 0 && getItemCountPreLogin() != lItemCountPostLogin){
	    	vlogDebug("ProfileFormHandler: After Login: CartMerged");
	      setCartMerged(true);
	      getCheckoutManager().setCartMessage(MFFConstants.getEXTNResourcesMessage(pRequest,MFFConstants.MSG_CART_MERGED));
	    }
/*		String phoneNumber = getStringValueProperty("phoneNumber");
		if(!StringUtils.isBlank(phoneNumber)){
			phoneNumber = phoneNumber.replaceAll("-", "");
			getValue().put("phoneNumber", phoneNumber);
		}*/
		
		// check if SOM Card is being entered
		// if entered, lets validate that it is good
	    getEmployeeManager().isValidateEmployee(getProfile());
	}

	/**
	 * handle method to call  reset password with new password
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleResetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("in handleResetPassword");
		preResetPassword(pRequest,pResponse);
		if(!getFormError()){
			resetPassword(pRequest,pResponse);
		}
		if(!getFormError()){
			postResetPassword(pRequest,pResponse);
		}


		return checkFormRedirect(getChangePasswordSuccessURL(), getChangePasswordErrorURL(), pRequest, pResponse);
	}

	/**
	 * handle login override
	 */
	@Override
	public boolean handleLogin(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("In MFFProfileFormHandler: handleLogin: Start");
		String pLogin = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		boolean retValue=false;
		retValue=super.handleLogin(pRequest, pResponse);
		if(!getFormError()){
			vlogDebug("In MFFProfileFormHandler: handleLogin: Login successful");
			RepositoryItem lProfileRepoItem =  getProfileTools().getItemFromEmail(pLogin);
			try {
				vlogDebug("In MFFProfileFormHandler: handleLogin: Assign last activity date to login date start");
				MFFProfileTools mfTools=(MFFProfileTools)getProfileTools();
				mfTools.setLastActivityDate(lProfileRepoItem);
				vlogDebug("In MFFProfileFormHandler: handleLogin: Assign last activity date to login date end");
			} catch (RepositoryException exc) {
				String msg = formatUserMessage("errorUpdatingProfile", pRequest);
				addFormException(new DropletException(msg, exc, "errorUpdatingProfile"));
				logError(exc);
			} catch (NullPointerException exc) {
				addFormException(new DropletException("Problem updating Profile"));
				logError(exc);
			} catch (PropertyNotFoundException exc) {
				String msg = formatUserMessage("noSuchProfileProperty", pRequest);
				 addFormException(new DropletFormException(msg, exc, "noSuchProfileProperty"));
				 logError(exc);
			} 
			catch (Exception e) {
				 addFormException(new DropletFormException("Error setting last activity date",e,"Exception"));
				 logError(e);
			}
		}
		vlogDebug("In MFFProfileFormHandler: handleLogin: End");
		return retValue;
	}
	
	/**
	 * handle create override
	 */
	@Override
	public boolean handleCreate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("In MFFProfileFormHandler: handleCreate: Start");
		boolean retValue=false;
		String pLogin = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		retValue=super.handleCreate(pRequest, pResponse);
		if(!getFormError()){
			vlogDebug("In MFFProfileFormHandler: handleCreate: Create successful");
			RepositoryItem lProfileRepoItem =  getProfileTools().getItemFromEmail(pLogin);
			try {
				vlogDebug("In MFFProfileFormHandler: handleCreate: Assign last activity date to create date start");
				MFFProfileTools mfTools=(MFFProfileTools)getProfileTools();
				vlogDebug("In MFFProfileFormHandler: handleCreate: Assign last activity date to create date end");
				mfTools.setLastActivityDate(lProfileRepoItem);
			} catch (RepositoryException exc) {
				String msg = formatUserMessage("errorUpdatingProfile", pRequest);
				addFormException(new DropletException(msg, exc, "errorUpdatingProfile"));
				logError(exc);
			} catch (NullPointerException exc) {
				addFormException(new DropletException("Problem updating Profile"));
				logError(exc);
			} catch (PropertyNotFoundException exc) {
				String msg = formatUserMessage("noSuchProfileProperty", pRequest);
				 addFormException(new DropletFormException(msg, exc, "noSuchProfileProperty"));
				 logError(exc);
			} 
			catch (Exception e) {
				 addFormException(new DropletFormException("Error setting last activity date",e,"Exception"));
				 logError(e);
			}
		}
		vlogDebug("In MFFProfileFormHandler: handleCreate: End");
		return retValue;
	}
	
	/**
	 * Pre reset method call to perform validations
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	protected Boolean preResetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
		vlogDebug("in preResetPassword");
		vlogDebug("value dictionary -- {0}", getValue());
		String login = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		String password = getStringValueProperty(getProfileTools().getPropertyManager().getPasswordPropertyName());
		String confirmPassword = getStringValueProperty("CONFIRMPASSWORD");
		vlogDebug("in validations login[{0}], password[{1}],confirm_password[{2}]", login,password,confirmPassword);
		if(StringUtils.isBlank(login)){
			addFormException(new DropletException(MSG_INVALID_EMAIL));
			return false;
		}
		if(StringUtils.isBlank(password)){
			String msg = formatUserMessage("missingPassword", pRequest);
			String propertyPath = generatePropertyPath(getProfileTools().getPropertyManager().getPasswordPropertyName());
			addFormException(new DropletFormException(msg, propertyPath, "missingPassword"));
			return false;
		}
		if(StringUtils.isBlank(confirmPassword) || !password.equals(confirmPassword)){
			String msg = formatUserMessage("passwordsDoNotMatch", pRequest);
			addFormException(new DropletException(msg, "passwordsDoNotMatch"));
			return false;
		}
		/*if(getProfileTools().getPasswordRuleChecker().checkRules(password, null)){
			addFormException(new DropletException(getProfileTools().getPasswordRuleChecker().getLastRuleCheckedDescription()));
			return false;
		}*/
		vlogDebug("out preResetPassword");
		return true;
	}

	/**
	 * function call where the password is reset with new password
	 * @param pRequest
	 * @param pResponse
	 */
	protected void resetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
		vlogDebug("in ResetPassword");
		String pLogin = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		String pPassword = getStringValueProperty(getProfileTools().getPropertyManager().getPasswordPropertyName());
		String confirmPassword = getStringValueProperty("CONFIRMPASSWORD");
		RepositoryItem lProfileRepoItem =  getProfileTools().getItemFromEmail(pLogin);
		try {
			getProfileTools().changePassword(lProfileRepoItem, pLogin, pPassword, confirmPassword, null, true, false);
			MFFProfileTools mfTools=(MFFProfileTools)getProfileTools();
			mfTools.setForcePasswordReset(lProfileRepoItem);
			mfTools.setLastActivityDate(lProfileRepoItem);
		} catch (RepositoryException exc) {
			String msg = formatUserMessage("errorUpdatingProfile", pRequest);
			addFormException(new DropletException(msg, exc, "errorUpdatingProfile"));
			logError(exc);
		} catch (NullPointerException exc) {
			addFormException(new DropletException("Problem updating Profile"));
			logError(exc);
		} catch (PropertyNotFoundException exc) {
			String msg = formatUserMessage("noSuchProfileProperty", pRequest);
			 addFormException(new DropletFormException(msg, exc, "noSuchProfileProperty"));
			 logError(exc);
		} catch (PasswordChangeException e) {
			 addFormException(new DropletFormException("Error setting new password",e,"PasswordChangeException"));
			 logError(e);
		}
		catch (Exception e) {
			 addFormException(new DropletFormException("Error setting new password",e,"Exception"));
			 logError(e);
		}
		vlogDebug("out ResetPassword");
	}

	/**
	 * post reset method where emailis sent to inform email has been changed
	 * @param pRequest
	 * @param pResponse
	 */
	protected void postResetPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
		vlogDebug("in postResetPassword");
		String pLogin = getStringValueProperty(getProfileTools().getPropertyManager().getLoginPropertyName());
		getEmailManager().sendPasswordUpdateEmail(getProfileTools().getItemFromEmail(pLogin), pRequest.getLocale());
	}

	public boolean handleUpdateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		MutableRepository repository = getProfileTools().getProfileRepository();
		IdentityManager identityManager = getUserLoginManager().getIdentityManager(pRequest);
		boolean authSuccessful = false;
		try {
			String login = (String) getProfile().getPropertyValue(getPropertyManager().getLoginPropertyName());
			String password = (String) getEditValue().get("currentPassword");
			String email = (String) getEditValue().get("newEmail");
			String confirmEmail = (String) getEditValue().get("confirmEmail");

			//email rules validation
			validateEmail(email);

			if(!email.equalsIgnoreCase(confirmEmail)){
				addFormException(new DropletException(MSG_EMAIL_NOT_MATCH));
			}
			RepositoryItem lProfileitem= getProfileTools().getItemFromEmail(email);
			if(lProfileitem!=null){
				addFormException(new DropletException(MSG_EMAIL_EXISTS));
			}
			authSuccessful = identityManager.checkAuthenticationByPassword(login, password);
			if(!authSuccessful){
				addFormException(new DropletException("Password is not matched."));
			}
			if(getFormError()){
				return checkFormRedirect(getUpdateEmailSuccessURL(), getUpdateEmailErrorURL(), pRequest, pResponse);
			}
			MutableRepositoryItem userItem = repository.getItemForUpdate(getProfile().getRepositoryId(), getCreateProfileType());
			userItem.setPropertyValue("email", email);
			userItem.setPropertyValue("login", email);
			repository.updateItem(userItem);
			getEmailManager().sendEmailUpdateEmail(userItem,  pRequest.getLocale());
		} catch (RepositoryException e) {
			vlogError("RepositoryException in handleUpdateEmail: "+e, e);
		} catch (SecurityException e) {
			vlogError("SecurityException occurred handleUpdateEmail: "+e, e);
		}

		return checkFormRedirect(getUpdateEmailSuccessURL(), getUpdateEmailErrorURL(), pRequest, pResponse);
	}


	/**
	 * This method is used to validate the email rules
	 * @param pEmail
	 */
	private void validateEmail(String pEmail) {
		String email = pEmail;
		MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();

		if (StringUtils.isBlank(email) || !profileTools.validateEmailAddress(email)) {
			addFormException(new DropletException(MSG_INVALID_EMAIL));
		}

	}

	protected void preNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		Map<String, Object> newAddress = getEditValue();

		if (!validateRequiredAddressFields(newAddress, pRequest, pResponse)) {
			return;
		}

		if (!validateAddress(newAddress, pRequest, pResponse)) {
			return;
		}

		if (!getZipCodeHelper().isValidateCityStateZipCombination(newAddress)) {
			getFormExceptionGenerator().generateInlineException(getErrorMessage(
					MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION, null), getZipCodeHelper().getCityStateZipErrorField(), this, pRequest);

			return;
		}
	}

	  /**
	   * Creates a new shipping address using the entries entered in the editValue
	   * map. The address will be indexed using the nickname provided by the user.
	   *
	   * @param pRequest The current HTTP request
	   * @param pResponse The current HTTP response
	   * @return boolean returns true/false for success
	   * @throws ServletException if there was an error while executing the code
	   * @throws IOException if there was an error with servlet io
	   */
	  public boolean handleNewAddress(DynamoHttpServletRequest pRequest,
	    DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		  vlogDebug("handleNewAddress(): Called.");

		  if(!isAddressVerified()){
			  preNewAddress(pRequest, pResponse);

			  if (getFormError()) {
				  return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
			  }
		  }

		  Map<String, Object> newAddress = getEditValue();

		  // Get the current user Profile and the ProfileTools bean and the values
		  // entered into the new address form by the user.
		  MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
		  Profile profile = getProfile();

		  String nickname = (String) newAddress.get(getNicknameValueMapKey());

		  vlogDebug("handleNewAddress(): isAddressVerified: " + isAddressVerified());

		  // validation with avs
			if(!isAddressVerified()){

				Map addressInfo = profileTools.validateAddress(pRequest, newAddress);
				if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
					setAddressMatched(true);
				} else {
					setAddressMatched(false);

					addressInfo.put("nickname", nickname);
					addressInfo.put("fname", (String) newAddress.get("firstName"));
					addressInfo.put("lname", (String) newAddress.get("lastName"));
					addressInfo.put("phoneNumber", (String) newAddress.get("phoneNumber"));
					addressInfo.put("method", "addNewAddress");

					setSuggestedAddresses(addressInfo);

					vlogDebug("handleNewAddress(): avsResponseURL: " + getNewAddressSuccessURL());
					return checkFormRedirect(getNewAddressSuccessURL(), getNewAddressErrorURL(), pRequest, pResponse);
				}
			}

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      try {
	        // Create an Address object from the values the user entered.
	        Address addressObject = AddressTools.createAddressFromMap(newAddress,
	          profileTools.getShippingAddressClassName());

	        // Create an entry in the secondaryAddress map on the profile, for this
	        // new address. Set this new Id as the newAddressId so it can be picked
	        // up on the success page (used to select it in a dropdown).
	        String newAddressId =
	          profileTools.createProfileRepositorySecondaryAddress(profile, nickname, addressObject);

	        if(newAddressId != null){
	          setNewAddressId(newAddressId);
	        }

	        // Check to see Profile.shippingAddress is null, if it is,
	        // add the new address as the default shipping address
	        RepositoryItem defaultShippingAddress = profileTools.getDefaultShippingAddress(profile);
	        vlogDebug("handleNewAddress(): defaultShippingAddress: " + defaultShippingAddress);

			if (defaultShippingAddress == null) {
	        	profileTools.setDefaultShippingAddress(profile, nickname);
	        }

	        // empty out the map
	        newAddress.clear();
	      }
	      catch (RepositoryException repositoryExc) {
	        addFormException(MSG_ERR_CREATING_ADDRESS, new String[] { nickname },
	          repositoryExc, pRequest);

	        if (isLoggingError()) {
	          logError(repositoryExc);
	        }

	        // Failure, redirect to the error URL
	        return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
	      }
	      catch (InstantiationException ex) {
	        throw new ServletException(ex);
	      }
	      catch (IllegalAccessException ex) {
	        throw new ServletException(ex);
	      }
	      catch (ClassNotFoundException ex) {
	        throw new ServletException(ex);
	      }
	      catch (IntrospectionException ex) {
	        throw new ServletException(ex);
	      }

	      setAddressMatched(true);
	      // Success, redirect to the success URL
	      return checkFormRedirect(getNewAddressSuccessURL(),
	        getNewAddressErrorURL(), pRequest, pResponse);
	    }
	    catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    }
	    finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      }
	      catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("Can't end transaction ", e);
	        }
	      }
	    }
	  }

	  /**
	   * Validates new address fields entered by user:
	   * <ul>
	   *  <li>all required fields are specified for new address
	   *  <li>country/state combination is valid for new address
	   *  <li>not duplicate address nickname is used for create address or update
	   *      address operation
	   * </ul>
	   * @param pRequest http request
	   * @param pResponse http response
	   *
	   * @return true is validation succeeded
	   * @exception ServletException if there was an error while executing the code
	   * @exception IOException if there was an error with servlet io
	   */
	  protected boolean validateAddress(Map pNewAddress, DynamoHttpServletRequest pRequest,
	                                    DynamoHttpServletResponse pResponse) throws ServletException,
	      IOException {

		  vlogDebug ("validateAddress(): Called.");
		  ResourceBundle bundle = ResourceUtils.getBundle(MFF_RESOURCE_BUNDLE, getLocale(pRequest));

	    // return false if there were missing required properties
	    if (getFormError()) {
	      return false;
	    }

	    // Validate address nickname or new nickname if it's  update address operation
	    String nickname = (String) pNewAddress.get(getNicknameValueMapKey());
	    String newNickname = (String) pNewAddress.get(getNewNicknameValueMapKey());

	    vlogDebug ("validateAddress(): nickname: " + nickname);
	    vlogDebug ("validateAddress(): newNickname: " + newNickname);

	    if (!StringUtils.isBlank(newNickname)) {
	      // Editing an address, in this case we want to allow them to change the casing of the nickname
	      // so we remove the original nickname from the duplicate check by adding it to the ignore list.
	      // We still need to perform the check incase they change it to another name thats in the list.
	    	  if(newNickname.trim().length() > 42) {
	    		  getFormExceptionGenerator().generateInlineException(bundle.getString(MSG_NICKNAME_MAX_LEN), "address-name", this, pRequest);
	    	  }
	    	  
	    	if (!newNickname.equals(nickname)) {
	    	  vlogDebug ("validateAddress(): case: nick name edited.");

	    	  List ignore = new ArrayList();
	    	  ignore.add(nickname);
		        boolean duplicateNickname =
		          ((MFFProfileTools)getProfileTools()).isDuplicateAddressNickname(getProfile(), newNickname, ignore);

		        if(duplicateNickname){
		        	addFormException(new DropletException(getErrorMessage(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { newNickname })));
		        }
	      	}
	    }
	    else {
	    	vlogDebug ("validateAddress(): case: nick name not edited.");
	      //It's new address so validate nickname against all nicknames
	      if (!StringUtils.isBlank(nickname)) {
	    	  
	    	  if(nickname.trim().length() > 42) {
	    		  getFormExceptionGenerator().generateInlineException(bundle.getString(MSG_NICKNAME_MAX_LEN), "address-name", this, pRequest);
	    	  }
	        boolean duplicateNickname =
	          ((MFFProfileTools)getProfileTools()).isDuplicateAddressNickName(getProfile(), nickname);

	        if(duplicateNickname){
	          addFormException(new DropletException(getErrorMessage(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { nickname })));
	        }
	      }
	    }

	    if (getFormError()) {
	      return false;
	    }

	    ContactInfo address = new ContactInfo();
	    address.setFirstName((String)pNewAddress.get("firstName"));
	    address.setLastName((String)pNewAddress.get("lastName"));
	    address.setAddress1((String)pNewAddress.get("address1"));
	    address.setAddress2((String)pNewAddress.get("address2"));
	    address.setCity((String)pNewAddress.get("city"));
	    address.setState((String)pNewAddress.get("state"));
	    address.setPostalCode((String)pNewAddress.get("postalCode"));
	    address.setCountry((String)pNewAddress.get("country"));
	    address.setPhoneNumber((String)pNewAddress.get("phoneNumber"));

	    getAddressValidator().validateAddress(address, pRequest, this);

	    @SuppressWarnings("unchecked")
	    List<DropletException> errorList = getFormExceptions();

	    vlogDebug("ProfileFormHandler: Error List Size:{0}", errorList.size());

	    if (errorList != null && !errorList.isEmpty()) {
	      for (int i = 0; i < errorList.size(); i++) {
	        vlogDebug("ProfileFormHandler: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
	       return false;
	      }
	    }
	    //all validation passed successfully so return true
	    return true;
	  }

	protected boolean validateCardNickNameForDuplicate(String pCurrentName, String pNewNickname) {

		vlogDebug("validateCardNickNameForDuplicate(): Called.");
		vlogDebug("validateCardNickNameForDuplicate(): pCurrentName: " + pCurrentName + " ,pNewNickname: " + pNewNickname);

		if (getFormError()) {
			return false;
		}

		if (!StringUtils.isEmpty(pNewNickname) && !pNewNickname.equals(pCurrentName)) {
			boolean duplicateNickname = ((MFFProfileTools) getProfileTools())
					.isDuplicateCreditCardNickname(getProfile(), pNewNickname);

			if (duplicateNickname) {
				addFormException(new DropletException(getErrorMessage(
						MFFConstants.MSG_DUPLICATE_CARD_NICKNAME,
						new String[] { pNewNickname })));
			}
		}

		if (getFormError()) {
			return false;
		}

		// all validation passed successfully so return true
		return true;
	}

	  protected boolean validateRequiredAddressFields(Map pAddress,
				DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {

			boolean isSuccess = true;

			String[] addressProperties = getRequiredAddressProperties();
			ResourceBundle bundle = ResourceUtils.getBundle(MFF_RESOURCE_BUNDLE, getLocale(pRequest));
			Object propertyValue = null;
			String propertyName = null;

			for (int i = 0; i < addressProperties.length; i++) {
				propertyName = addressProperties[i];
				propertyValue = pAddress.get(propertyName);

				if (StringUtils.isEmpty((String)propertyValue)) {
					vlogDebug("validateRequiredAddressFields(): " + propertyName + " is null or empty.");
					isSuccess = false;
					break;
				} else if (((String) propertyValue).length() == 0) {
					vlogDebug("validateRequiredAddressFields(): " + propertyName + " length is zero.");
					isSuccess = false;
					break;
				}
			}

			if (!isSuccess){
				addFormException(new DropletException(bundle.getString(MSG_MISSING_REQUIRED_PROPERTY)));
			}

			// validate zip code pattern
			String postalCode = (String) pAddress.get("postalCode");
			if (!StringUtils.isEmpty(postalCode)) {
				if (!postalCode.matches(ZIP_CODE_PATTERN)) {
					addFormException(new DropletException(bundle.getString(MSG_INVALID_ZIP)));
					isSuccess = false;
				}
			}

			return isSuccess;
		}

	  protected boolean validateTaxExemptionInfo(Map pTaxExmpInfo,
				DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {

		    String nickname = (String) pTaxExmpInfo.get(getNicknameValueMapKey());
		    String newNickname = (String) pTaxExmpInfo.get(getNewNicknameValueMapKey());

		    vlogDebug ("validateTaxExemptionInfo(): nickname: " + nickname);
		    vlogDebug ("validateTaxExemptionInfo(): newNickname: " + newNickname);

		    if (!StringUtils.isBlank(newNickname)) {
		    	if (!newNickname.equals(nickname)) {
		    		vlogDebug ("validateTaxExemptionInfo(): case: nick name edited.");
			    	List ignore = new ArrayList();
			    	ignore.add(nickname);
			        boolean duplicateNewNickname =
			          ((MFFProfileTools)getProfileTools()).isDuplicateTaxExmpNickName(getProfile(), newNickname, ignore);
			        vlogDebug ("validateTaxExemptionInfo(): duplicatenewNickname: " + duplicateNewNickname);
			        if(duplicateNewNickname){
			          addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_DUPLICATE_TAX_EXMP_NICKNAME, new String[] { newNickname })));
			          return false;
			        }
		    	}

		    } else if (!StringUtils.isBlank(nickname)) {
		    	boolean duplicateNickname =
				          ((MFFProfileTools)getProfileTools()).isDuplicateTaxExmpNickName(getProfile(), nickname, null);
		    	vlogDebug ("validateTaxExemptionInfo(): duplicateNickname: " + duplicateNickname);
		        if(duplicateNickname){
		          addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_DUPLICATE_TAX_EXMP_NICKNAME, new String[] { nickname })));
		          return false;
		        }
		    }

		  	boolean isSuccess = true;
			String[] reqTaxExmpProperties = getRequiredTaxExmpProperties();
			ResourceBundle bundle = ResourceUtils.getBundle(MFF_RESOURCE_BUNDLE, getLocale(pRequest));
			Object propertyValue = null;
			String propertyName = null;
			for (int i = 0; i < reqTaxExmpProperties.length; i++) {
				propertyName = reqTaxExmpProperties[i];
				propertyValue = pTaxExmpInfo.get(propertyName);
				if (StringUtils.isEmpty((String) propertyValue)) {
					vlogDebug("validateTaxExemptionInfo(): " + propertyName + " is null or empty.");
					isSuccess = false;
					break;
				} else if (((String) propertyValue).length() == 0) {
					vlogDebug("validateTaxExemptionInfo(): " + propertyName + " length is zero.");
					isSuccess = false;
					break;
				}
			}

			if (!isSuccess){
				addFormException(new DropletException(bundle.getString(MSG_MISSING_REQUIRED_PROPERTY)));
			} else {

				String postalCode = (String) pTaxExmpInfo.get("postalCode");
				if (!StringUtils.isEmpty(postalCode)) {
					if (!postalCode.matches(ZIP_CODE_PATTERN)) {
						addFormException(new DropletException(bundle.getString(MSG_INVALID_ZIP)));
						isSuccess = false;
					}
				}
			}

			return isSuccess;
		}

	  protected void preUpdateAddress(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		Map edit = getEditValue();

		if (!validateRequiredAddressFields(edit, pRequest, pResponse)) {
			return;
		}

		if (!validateAddress(edit, pRequest, pResponse)) {
			return;
		}

		if (!getZipCodeHelper().isValidateCityStateZipCombination(edit)) {
			getFormExceptionGenerator().generateInlineException(getErrorMessage(
					MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION, null), getZipCodeHelper().getCityStateZipErrorField(), this, pRequest);
			return;
		}
	  }

	  /**
	   * Update the secondary address as modified by the user.
	   *
	   * @param pRequest
	   *            the servlet's request
	   * @param pResponse
	   *            the servlet's response
	   * @exception ServletException
	   *                if there was an error while executing the code
	   * @exception IOException
	   *                if there was an error with servlet io
	   * @exception RepositoryException
	   *                if there was an error accessing the repository
	   * @return true for successful address update, false - otherwise
	   */
	  public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest,
	                                     DynamoHttpServletResponse pResponse)
	      throws RepositoryException, ServletException, IOException {

		  vlogDebug("handleUpdateAddress(): Called.");

		  if (!isAddressVerified()){

			  preUpdateAddress(pRequest, pResponse);

			  if (getFormError()){
				  return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
			  }
		  }

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();
	    MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      Profile profile = getProfile();
	      Map edit = getEditValue();

	      String nickname = (String) edit.get(getNicknameValueMapKey());
	      String newNickname = ((String) edit.get(getNewNicknameValueMapKey()));

	      vlogDebug("handleUpdateAddress(): nickname: " + nickname);
	      vlogDebug("handleUpdateAddress(): newNickname: " + newNickname);
	      vlogDebug("handleUpdateAddress(): isAddressVerified: " + isAddressVerified());

		  // validation with avs
			if(!isAddressVerified()){

				Map addressInfo = profileTools.validateAddress(pRequest, edit);
				if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
					setAddressMatched(true);
				} else {
					setAddressMatched(false);

					addressInfo.put("nickname", nickname);
					addressInfo.put("newNickname", newNickname);
					addressInfo.put("fname", (String) edit.get("firstName"));
					addressInfo.put("lname", (String) edit.get("lastName"));
					addressInfo.put("phoneNumber", (String) edit.get("phoneNumber"));
					addressInfo.put("method", "updateAddress");

					setSuggestedAddresses(addressInfo);

					vlogDebug("handleUpdateAddress(): avsResponseURL: " + getUpdateAddressSuccessURL());
					return checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressErrorURL(), pRequest, pResponse);
				}
			}

	      try {

	        //Populate Address object data entered by user
	        Address addressObject = AddressTools.createAddressFromMap(edit,
	                                                                  profileTools.getShippingAddressClassName());
	        // Get address repository item to be updated
	        RepositoryItem oldAddress = profileTools.getProfileAddress(profile, nickname);

	        // Update address repository item
	        profileTools.updateProfileRepositoryAddress(oldAddress, addressObject);

	        // Check if nickname should be changed
	        if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname)) {
	          profileTools.changeSecondaryAddressName(profile, nickname, newNickname);
	        }

	        if(isUseShippingAddressAsDefault()){
	          ((MFFProfileTools) getProfileTools()).setDefaultShippingAddress(profile, newNickname);
	        }

	        // update secondary properties of the address in the order (e.g phone num)
	        Order currentOrder = getShoppingCart().getCurrent();
	        if(currentOrder != null){
	          List shippingGroupList = currentOrder.getShippingGroups();
	          for(Object shippingGroup : shippingGroupList){
	            if(shippingGroup instanceof HardgoodShippingGroup){
	              Address orderAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
	              if(MFFAddressTools.compare(addressObject, orderAddress)){
	                  updateSecondaryInfo((ContactInfo)orderAddress,
	                                      (ContactInfo)addressObject);
	              }
	            }
	          }
	        }
	      } catch (RepositoryException repositoryExc) {
	        addFormException(MSG_ERR_UPDATING_ADDRESS, repositoryExc, pRequest);

	        if (isLoggingError()) {
	          logError(repositoryExc);
	        }

	        return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
	      } catch (InstantiationException ex) {
	        throw new ServletException(ex);
	      } catch (IllegalAccessException ex) {
	        throw new ServletException(ex);
	      } catch (ClassNotFoundException ex) {
	        throw new ServletException(ex);
	      } catch (IntrospectionException ex) {
	        throw new ServletException(ex);
	      }

	      edit.clear();
	      setAddressMatched(true);
	      return checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressErrorURL(), pRequest,
	                               pResponse);
	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("Can't end transaction ", e);
	        }
	      }
	    }
	  }

	  /**
	   * Updates the properties of an address that don't affect where
	   * the item is shipped to - e.g the phone number.
	   *
	   * @param pTargetAddress The target Address
	   * @param pSourceAddress The source Address
	   */
	  protected void updateSecondaryInfo(Address pTargetAddress, Address pSourceAddress){
	    ContactInfo source = null;
	    ContactInfo dest = null;

	    if(pTargetAddress instanceof ContactInfo && pSourceAddress instanceof ContactInfo){
	      source = (ContactInfo) pSourceAddress;
	      dest = (ContactInfo) pTargetAddress;
	    }
	    else{
	      return;
	    }

	    /*
	     * Make sure we check the destination values aren't the same as
	     * the source so we don't waste time writing to the repository.
	     */

	    // Phone number (required address property)
	    String destPhone = dest.getPhoneNumber();
	    String sourcePhone = source.getPhoneNumber();
	    if(destPhone == null || !destPhone.equals(sourcePhone)){
	      dest.setPhoneNumber(sourcePhone);
	    }
	  }

	  //---------------------------------------
	  // handleRemoveAddress
	  //---------------------------------------

	  /**
	   * This handler deletes a secondary address named in the removeAddress
	   * property.
	   *
	   * @param pRequest
	   *            the servlet's request
	   * @param pResponse
	   *            the servlet's response
	   * @return boolean true/false for success
	   * @exception ServletException
	   *                if there was an error while executing the code
	   * @exception IOException
	   *                if there was an error with servlet io
	   */
	  public boolean handleRemoveAddress(DynamoHttpServletRequest pRequest,
	                                     DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		// Get nick name of the address to be removed
	      String nickname = getRemoveAddressKey();

	      if ((nickname == null) || (nickname.trim().length() == 0)) {
	        if (isLoggingDebug()) {
	          vlogDebug("A null or empty nickname was provided to handleRemoveAddress");
	        }
	        // if no nickname provided, do nothing.
	        return true;
	      }

	      	Profile profile = getProfile();
	      	MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
	      // Remove the Address from the Profile
	      RepositoryItem purgeAddress = profileTools.getProfileAddress(profile, nickname);

	      if (isAddressOnCard(profile, purgeAddress)){
	    	  return checkFormRedirect(getRemoveAddressSuccessURL(), getRemoveAddressErrorURL(), pRequest, pResponse);
	      }

	    // Stop execution if we have form errors
	    if (getFormError()) {
	      return true;
	    }

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      boolean expressCheckoutOff= false;

	      if(purgeAddress != null){

	    	  String removingAddrId = purgeAddress.getRepositoryId();
	    	vlogDebug("purgeAddress id: " + removingAddrId);

	        profileTools.removeProfileRepositoryAddress(profile, nickname, true);

	        RepositoryItem profileBillingAddrItem = profileTools.getDefaultBillingAddress(profile);
	        vlogDebug("profileBillingAddrItem id: " + profileBillingAddrItem);

	        if (profileBillingAddrItem!=null && profileBillingAddrItem.getRepositoryId().equalsIgnoreCase(removingAddrId)){
	        	profileTools.updateProperty(getMFFPropertyManager().getBillingAddressPropertyName(), (Object) null, profile);
	        }

	        MutableRepository mutRepository = profileTools.getProfileRepository();
	        mutRepository.removeItem(removingAddrId, getMFFPropertyManager().getContactInfoItemDescriptorName());

			RepositoryItem defaultShippingAddress = profileTools.getDefaultShippingAddress(profile);
			vlogDebug("defaultShippingAddress: " + defaultShippingAddress);

			if (defaultShippingAddress!=null && defaultShippingAddress.getRepositoryId().equalsIgnoreCase(removingAddrId)){

				boolean defaultAddressSet = profileTools.setDefaultShippingAddress(profile);

				if (!defaultAddressSet) {
					vlogDebug("defaultShippingAddress is being removed, hence setting expressChecout to be false.");
					expressCheckoutOff = true;
				}
			}
	      }

	      // Get the shipping group id that contains the Address
	      String shippingGroupId = null;
	      Map shippingGroupMap = getShippingGroupMapContainer().getShippingGroupMap();

	      if(shippingGroupMap != null){
	        if(shippingGroupMap.containsKey(nickname)){

	          if (getProfile().isTransient()) {
	            vlogDebug("Removing transient user's shipping group: {0}", nickname);

	            shippingGroupMap.remove(nickname);
	            return true;
	          }

	          shippingGroupId = ((ShippingGroup)(shippingGroupMap.get(nickname))).getId();
	        }
	      }

	      // Remove the Address from the Order
	      if(shippingGroupId != null){
	        Order currentOrder = getOrder();
	        if(currentOrder instanceof MFFOrderImpl){
	          boolean purged = ((MFFOrderImpl)currentOrder).removeAddress(shippingGroupId);
	          if(!purged){
	            if(isLoggingDebug()){
	              vlogDebug("The Address could not be removed from order " + currentOrder.getId());
	            }
	          }
	        }
	      }

	      if (expressCheckoutOff) {
	    	  vlogDebug("Setting expressChecout to false.");
	    	  this.getProfile().setPropertyValue("expressCheckout", Boolean.FALSE);
	      }
	      return checkFormRedirect(getRemoveAddressSuccessURL(), getRemoveAddressErrorURL(), pRequest, pResponse);

	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } catch (RepositoryException repositoryExc) {
	      if (isLoggingError()){
	        logError(repositoryExc);
	      }
	    } finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("Can't end transaction ", e);
	        }
	      }
	    }

	    return false;
	  }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean isAddressOnCard(Profile pProfile, RepositoryItem pAddress) {

		vlogDebug("isAddressOnCard(): Address to be removed: " + pAddress.getRepositoryId());
		boolean addressOnCard = false;

		Map creditCards = ((CommerceProfileTools) getProfileTools()).getUsersCreditCardMap(pProfile);

		if (creditCards.size() > 0) {
			Collection<RepositoryItem> cards = creditCards.values();
			for (RepositoryItem card : cards) {
				RepositoryItem cardAddress = (RepositoryItem) card.getPropertyValue("billingAddress");

				if (cardAddress != null){
					vlogDebug("isAddressOnCard(): Card address repo ID: " + cardAddress.getRepositoryId());
					if(cardAddress.getRepositoryId().equalsIgnoreCase(pAddress.getRepositoryId())) {
						addressOnCard = true;
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_ADDRESS_ON_CARD,null)));
						break;
					}
				}
			}
		}
		return addressOnCard;
	}

	  //---------------------------------------
	  // handleDefaultShippingAddress
	  //---------------------------------------

	  /**
	   * This sets the default shipping address.
	   * @param pRequest
	   *            DynamoHttpServletRequest
	   * @param pResponse
	   *            DynamoHttpServletResponse
	   * @return true for success, false - otherwise
	   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
	   * @throws ServletException if there was an error while executing the code
	   */
	  public boolean handleDefaultShippingAddress(DynamoHttpServletRequest pRequest,
	                                              DynamoHttpServletResponse pResponse)
	      throws RepositoryException, ServletException {

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      Profile profile = getProfile();
	      MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
	      String addressNickname = getDefaultShippingAddress();

	      if (StringUtils.isBlank(addressNickname)) {

	        if (isLoggingDebug()) {
	          vlogDebug("A null or empty nickname was provided to handleDefaultShippingAddress");
	        }
	        // if no nickname provided, do nothing.
	        return true;
	      }

	      // Set requested shipping address as default
	      profileTools.setDefaultShippingAddress(profile, addressNickname);

	      return true;
	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("Can't end transaction ", e);
	        }
	      }
	    }
	  }

	  /**
	   * Makes the credit card identified by a nickname, default in the given
	   * profile.
	   * @param pRequest
	   *            DynamoHttpServletRequest
	   * @param pResponse
	   *            DynamoHttpServletResponse
	   * @return boolean true/false for success
	   * @throws RepositoryException
	   *             if there was an error accessing the repository
	   * @throws ServletException
	   *             if there was an error while executing the code
	   * @throws IOException
	   *             if there was an error with servlet io
	   */
	  public boolean handleDefaultCard(DynamoHttpServletRequest pRequest,
	                                   DynamoHttpServletResponse pResponse) throws RepositoryException,
	      ServletException, IOException {

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      // Get nickname of the credit card that should be done default
	      String targetCard = getDefaultCard();

	      if (StringUtils.isBlank(targetCard)) {

	        if (isLoggingDebug()) {
	          vlogDebug("A null or empty nickname was provided to handleDefaultCard");
	        }

	        // if no nickname provided, do nothing.
	        return true;
	      }

	      Profile profile = getProfile();
	      MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
	      profileTools.setDefaultCreditCard(profile, targetCard);

	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("Can't end transaction ", e);
	        }
	      }
	    }
	    return true;
	  }

	  //---------------------------------------
	  // handleEditAddress
	  //---------------------------------------

	  /**
	   * Copy the named address into the editValue map, allowing the user to edit it.
	   *
	   * @param pRequest the servlet's request
	   * @param pResponse the servlet's response
	   * @return true
	   * @exception ServletException if there was an error while executing the code
	   * @exception IOException if there was an error with servlet io
	   * @throws PropertyNotFoundException If a property is not found
	   */
	  public boolean handleEditAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	      throws ServletException, IOException {

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();
	    CommercePropertyManager propertyManager = getMFFPropertyManager();

	    try {

	      if (tm != null){
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      String nickname = getEditAddress();
	      if (nickname == null || nickname.trim().length() == 0) {
	        return true;
	      }

	      // If we dont find the address on the profiles secondaryAddresses property check in the
	      // shipping group map. This will occur when a user is unregistered (as we dont store addresses
	      // on the profile for security reasons) or when a registered user tries to edit an address
	      // that is only saved in the order (e.g anon user with order logging in).
	      Profile profile = getProfile();
	      Map secondaryAddress = (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
	      Object theAddress = secondaryAddress.get(nickname);

	      if(theAddress == null){
	        Map shippingGroupMap = getShippingGroupMapContainer().getShippingGroupMap();
	        if(shippingGroupMap != null){
	          if(shippingGroupMap.containsKey(nickname)){
	            HardgoodShippingGroup hgsg = (HardgoodShippingGroup) getShippingGroupMapContainer().getShippingGroup(nickname);
	            theAddress = hgsg.getShippingAddress();
	          }
	        }
	      }

	      // We should never get here, but just incase
	      if(theAddress == null){
	        if(isLoggingError()){
	          logError("Could not find the address " + nickname +
	              " on the profile or in the shippingGroupMap");
	        }
	        return false;
	      }

	      String[] addressProps = getAddressProperties();
	      Object property = null;
	      Map edit = getEditValue();

	      // Add Address properties to the edit Map
	      edit.put(getNicknameValueMapKey(), nickname);
	      edit.put(getNewNicknameValueMapKey(), nickname);

	      // RepositoryItem Object
	      if(theAddress instanceof RepositoryItem){
	        edit.put(getAddressIdValueMapKey(), ((MutableRepositoryItem)theAddress).getRepositoryId());
	        for (int i = 0; i < addressProps.length; i++) {
	          property = ((RepositoryItem)theAddress).getPropertyValue(addressProps[i]);
	          if (property != null){
	            edit.put(addressProps[i], property);
	          }
	        }
	      }
	      // Address Object
	      else{
	        for (int i = 0; i < addressProps.length; i++) {
	          try {
				property = DynamicBeans.getPropertyValue(theAddress, addressProps[i]);
			} catch (PropertyNotFoundException e) {
				vlogError("handleEditAddress(): PropertyNotFoundException occurred for " + addressProps[i] + " :"+ e, e);
			}
	          if (property != null){
	            edit.put(addressProps[i], property);
	          }
	        }
	      }

	      return true;

	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null){
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingDebug()){
	          vlogDebug("Ignoring exception", e);
	        }
	      }
	    }
	  }

	  //---------------------------------------
	  // handleClear
	  //---------------------------------------

	  /**
	   * Override to prevent clear if there are form errors.
	   * {@inheritDoc}
	   * <p>
	   * This is here because postLogin will do a clear of the value dictionary even if the
	   * login fails
	   */
	  public boolean handleClear(DynamoHttpServletRequest pRequest,
	      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	    if (getFormError()) {
	      return true;
	    } else {
	      return super.handleClear(pRequest, pResponse);
	    }
	  }

	public boolean handleClearForm(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (mEditValue != null) {
			mEditValue.clear();
		}
		if (mBillAddrValue != null) {
			mBillAddrValue.clear();
		}

		resetFormExceptions();
		return super.handleClear(pRequest, pResponse);
	}


		  //---------------------------------------
		  // Utility methods
		  //---------------------------------------

		  /**
		   * Utility method to retrieve the StorePropertyManager.
		   * @return property manager
		   */
		  protected CommercePropertyManager getMFFPropertyManager() {
		    return (CommercePropertyManager) getProfileTools().getPropertyManager();
		  }

		  /**
		   * Create a form exception, by looking up the exception code in a resource
		   * file identified by the RESOURCE_BUNDLE constant (defined above).
		   *
		   * @param pWhatException
		   *            String description of exception
		   * @param pRepositoryExc
		   *            RepositoryException
		   * @param pRequest
		   *            DynamoHttpServletRequest
		   */
		  protected void addFormException(String pWhatException, RepositoryException pRepositoryExc,
		                                       DynamoHttpServletRequest pRequest) {
		    addFormException(pWhatException,null,pRepositoryExc,pRequest);
		  }

		  /**
		   * Create a form exception, by looking up the exception code in a resource
		   * file identified by the RESOURCE_BUNDLE constant (defined above).
		   *
		   * @param pWhatException
		   *            String description of exception
		   * @param pArgs
		   *            String array with arguments used message formatting
		   * @param pRepositoryExc
		   *            RepositoryException
		   * @param pRequest
		   *            DynamoHttpServletRequest
		   */
		  protected void addFormException(String pWhatException, Object[] pArgs, RepositoryException pRepositoryExc,
		                                       DynamoHttpServletRequest pRequest) {
		    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
		    String errorStr = bundle.getString(pWhatException);
		    if (pArgs != null && pArgs.length > 0){
		      errorStr = (new MessageFormat(errorStr)).format(pArgs);
		    }
		    addFormException(new DropletFormException(errorStr, pRepositoryExc, pWhatException));
		  }

	protected void addFormException(String pWhatException, Object[] pArgs,
			String pPath, DynamoHttpServletRequest pRequest) {
		ResourceBundle bundle = LayeredResourceBundle.getBundle(
				RESOURCE_BUNDLE, getLocale(pRequest));
		String errorStr = bundle.getString(pWhatException);

		if (pArgs != null && pArgs.length > 0) {
			errorStr = (new MessageFormat(errorStr)).format(pArgs);
		}

		addFormException(new DropletFormException(errorStr, pPath,
				pWhatException));
	}

	private String getErrorMessage(String pWhatException,Object[] pArgs){
		String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(pWhatException));
		if (pArgs != null && pArgs.length > 0) {
			resourceMsg = (new MessageFormat(resourceMsg)).format(pArgs);
		}
		return resourceMsg;
	}

	/**
	 * Creates a form exception, by looking up the exception code in a resource
	 * file identified by the RESOURCE_BUNDLE constant (defined above).
	 *
	 * @param pWhatException
	 *            String description of exception
	 * @param pPath
	 *            Full path to form handler property associated with the
	 *            exception
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 */
	protected void addFormException(String pWhatException, String pPath,
			DynamoHttpServletRequest pRequest) {
		addFormException(pWhatException, null, pPath, pRequest);
	}

	/**
	 * Determine the user's current locale, if available.
	 *
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @return Locale Request's Locale
	 */
	protected Locale getLocale(DynamoHttpServletRequest pRequest) {
		RequestLocale reqLocale = pRequest.getRequestLocale();

		if (reqLocale == null) {
			reqLocale = getRequestLocale();
		}

		if (reqLocale == null) {
			return null;
		} else {
			return reqLocale.getLocale();
		}
	}

	/**
	 * Operation called to get the Date on the basis of locale format.
	 *
	 * @param pDate
	 *            getting from database
	 * @param pFormat
	 *            Date Format get by database
	 * @return date in specified format
	 */
	protected String getDateByFormat(Object pDate, String pFormat) {
		DateFormat df;
		if (pFormat == null)
			df = new SimpleDateFormat(DATE_FORMAT);
		else
			df = new SimpleDateFormat(pFormat);

		return df.format(((java.util.Calendar) pDate).getTime());
	}

	/**
	 * Return a String message specific for the given locale.
	 *
	 * @param pKey
	 *            the identifier for the message to retrieve out of the
	 *            ResourceBundle
	 * @param pLocale
	 *            the locale of the user
	 * @return the localized message
	 */
	public static String getString(String pKey, Locale pLocale) {
		return (getResourceBundle(pLocale).getString(pKey));
	}

	/**
	 * Returns a ResourceBundle specific for the given locale.
	 *
	 * @param pLocale
	 *            the locale of the user
	 * @return ResourcerBundle
	 * @throws MissingResourceException
	 *             ResourceBundle could not be located
	 */
	public static ResourceBundle getResourceBundle(Locale pLocale)
			throws MissingResourceException {
		return (LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, pLocale));
	}

	/**
	 * Returns the Locale for the user given the request.
	 *
	 * @param pRequest
	 *            the request object which can be used to extract the user's
	 *            locale
	 * @return Locale
	 */
	protected Locale getUserLocale(DynamoHttpServletRequest pRequest) {
		if (pRequest != null) {
			RequestLocale reqLocale = pRequest.getRequestLocale();

			if (reqLocale != null) {
				return reqLocale.getLocale();
			}
		}

		return null;
	}

	@Override
	 protected int checkFormError(String pErrorURL, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
             throws ServletException, IOException {
		if(getProfileTools() == null)
			addFormException(new DropletException(formatUserMessage("missingProfileTools", pRequest)));
		if(getFormError()){
			String redirectURL = pErrorURL;
			if(redirectURL != null){
				if(isLoggingDebug())
					vlogDebug((new StringBuilder()).append("error - redirecting to: ").append(redirectURL).toString());
					redirectOrForward(pRequest, pResponse, redirectURL);
					return 2;
			} else{
				return 1;
			}
		} else{
			return 0;
		}
	}

	@Override
	 protected boolean checkFormSuccess(String pSuccessURL, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
             throws ServletException, IOException {
		String redirectURL = pSuccessURL;
		if(redirectURL != null){
			if(isLoggingDebug()){
				vlogDebug((new StringBuilder()).append("success - redirecting to: ").append(redirectURL).toString());
			}
			redirectOrForward(pRequest, pResponse, redirectURL);
			return false;
		}
		if(isLoggingDebug())
        {
			vlogDebug((new StringBuilder()).append("Successfully completed: ").append(pSuccessURL).append(" contents of value are:").toString());
			Dictionary valuesDict = getValue();
			for(Enumeration e = valuesDict.keys(); e.hasMoreElements();)
			{
				String key = (String)e.nextElement();
				Object value = getValueProperty(key);
				if(value != null)
					vlogDebug((new StringBuilder()).append("name=").append(key).append(" value=").append(value).append(" class=").append(value.getClass().getName()).toString());
				else
					vlogDebug((new StringBuilder()).append("name=").append(key).append(" value=null").toString());
			}
        }
		return true;
	}


	/**
	   * This handler creates new tax exemption info item on the profile.
	   *
	   * @param pRequest
	   * @param pResponse
	   * @return boolean
	   * @throws ServletException
	   * @throws IOException
	   */
	  public boolean handleAddTaxExemption(DynamoHttpServletRequest pRequest,
	    DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		  vlogDebug("handleAddTaxExemption: Called: isTaxExemptionAgreed: " + isTaxExemptionAgreed());

		  Map<String, Object> taxExmpInputMap = getEditValue();

		  if (!isAddressVerified()){
			  if (!isTaxExemptionAgreed()){
				  return checkFormRedirect(null, getAddTaxExmpErrorURL(), pRequest, pResponse);
			  }

			  if (!validateTaxExemptionInfo(taxExmpInputMap, pRequest, pResponse )) {
				  return checkFormRedirect(null, getAddTaxExmpErrorURL(), pRequest, pResponse);
			  }
		  }

	    Profile profile = getProfile();
	    MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();

	    String nickname = (String) taxExmpInputMap.get(getNicknameValueMapKey());

	    vlogDebug("handleAddTaxExemption: nickname: " + nickname);

	    TaxExemptionInfo taxExmpInfo = new TaxExemptionInfo();

	    vlogDebug("handleAddTaxExemption(): isAddressVerified: " + isAddressVerified());

		  // validation with avs
			if(!isAddressVerified()){

				Map addressInfo = profileTools.validateAddress(pRequest, taxExmpInputMap);
				if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
					setAddressMatched(true);
				} else {
					setAddressMatched(false);

					addressInfo.put("nickname", nickname);
					addressInfo.put("fname", (String) taxExmpInputMap.get("firstName"));
					addressInfo.put("lname", (String) taxExmpInputMap.get("lastName"));
					addressInfo.put("classificationId", (String) taxExmpInputMap.get("classificationId"));
					addressInfo.put("taxId",(String) taxExmpInputMap.get("taxId"));
					addressInfo.put("orgName",(String) taxExmpInputMap.get("orgName"));
					addressInfo.put("businessDesc",(String) taxExmpInputMap.get("businessDesc"));
					addressInfo.put("merchandise",(String) taxExmpInputMap.get("merchandise"));
					addressInfo.put("taxCity",(String) taxExmpInputMap.get("taxCity"));
					addressInfo.put("taxState",(String) taxExmpInputMap.get("taxState"));
					addressInfo.put("method", "addTaxExemption");

					setSuggestedAddresses(addressInfo);

					vlogDebug("handleAddTaxExemption(): avsResponseURL: " + getAddTaxExmpSuccessURL());
					return checkFormRedirect(getAddTaxExmpSuccessURL(), getAddTaxExmpErrorURL(), pRequest, pResponse);
				}
			}

	    taxExmpInfo.setNickName(nickname);

	    String classificationId = (String) taxExmpInputMap.get("classificationId");
	    vlogDebug("handleAddTaxExemption: classification: " + classificationId);
	    taxExmpInfo.setClassificationId(classificationId);

	    String [] classificationInfo = this.getClassificationInfo(profileTools, classificationId);

	    taxExmpInfo.setClassificationCode(classificationInfo[0]);
	    taxExmpInfo.setClassificationName(classificationInfo[1]);

	    taxExmpInfo.setTaxId((String) taxExmpInputMap.get("taxId"));
	    taxExmpInfo.setOrgName((String) taxExmpInputMap.get("orgName"));
	    taxExmpInfo.setBusinessDesc((String) taxExmpInputMap.get("businessDesc"));
	    taxExmpInfo.setMerchandise((String) taxExmpInputMap.get("merchandise"));
	    taxExmpInfo.setTaxCity((String) taxExmpInputMap.get("taxCity"));
	    taxExmpInfo.setTaxState((String) taxExmpInputMap.get("taxState"));

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      try {
	        // Create an Address object from the values the user entered.

	    	  Address addressObject = AddressTools.createAddressFromMap(taxExmpInputMap,
	          profileTools.getShippingAddressClassName());

	          profileTools.createTaxExemptions(profile, taxExmpInfo, addressObject);
	          profileTools.setDefaultTaxExemption(profile, nickname);

	          taxExmpInputMap.clear();
	      }
	      catch (RepositoryException repositoryExc) {
	        addFormException("Unable to create Tax Exemption item.", new String[] { nickname },
	          repositoryExc, pRequest);

	        if (isLoggingError()) {
	          logError(repositoryExc);
	        }

	        // Failure, redirect to the error URL
	        return checkFormRedirect(null, getAddTaxExmpErrorURL(), pRequest, pResponse);
	      }
	      catch (InstantiationException ex) {
	        throw new ServletException(ex);
	      }
	      catch (IllegalAccessException ex) {
	        throw new ServletException(ex);
	      }
	      catch (ClassNotFoundException ex) {
	        throw new ServletException(ex);
	      }
	      catch (IntrospectionException ex) {
	        throw new ServletException(ex);
	      }

	      setAddressMatched(true);
	      return checkFormRedirect(getAddTaxExmpSuccessURL(), getAddTaxExmpErrorURL(), pRequest, pResponse);
	    }
	    catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    }
	    finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      }
	      catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("handleAddTaxExemption(): Can't end transaction ", e);
	        }
	      }
	    }
	  }

	  private String[] getClassificationInfo(MFFProfileTools pProfileTools, String pClassificationId) {
		  return pProfileTools.getClassificationInfo(pClassificationId);
	  }

	  /**
	   * Fill the editValue map with tax exemption info selected by user to edit.
	   *
	   * @param pRequest the servlet's request
	   * @param pResponse the servlet's response
	   * @return true
	   * @exception ServletException if there was an error while executing the code
	   * @exception IOException if there was an error with servlet io
	   * @throws PropertyNotFoundException If a property is not found
	   */
	  public boolean handleEditTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	      throws ServletException, IOException {

		  vlogDebug("handleEditTaxExemption: Called.");

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();
	    CommercePropertyManager propertyManager = getMFFPropertyManager();

	    try {

	      if (tm != null){
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      String nickname = getEditTaxExemption();
	      vlogDebug("handleEditTaxExemption: nickname: " + nickname);

	      if (nickname == null || nickname.trim().length() == 0) {
	        return true;
	      }

	      Profile profile = getProfile();
	      Map taxExemptions = (Map) profile.getPropertyValue("taxExemptions");
	      Object taxExmpItem = taxExemptions.get(nickname);

	      String[] taxExmpnProps = getTaxExmpnProperties();
	      String[] addressProps = getAddressProperties();

	      Object property = null;
	      Map edit = getEditValue();

	      // Add Tax exemption properties to the edit Map
	      edit.put(getNicknameValueMapKey(), nickname);
	      edit.put(getNewNicknameValueMapKey(), nickname);

	      if(taxExmpItem instanceof RepositoryItem){
	        edit.put(getAddressIdValueMapKey(), ((MutableRepositoryItem)taxExmpItem).getRepositoryId());

	        for (int i = 0; i < taxExmpnProps.length; i++) {

	          property = ((RepositoryItem)taxExmpItem).getPropertyValue(taxExmpnProps[i]);
	          vlogDebug("handleEditTaxExemption: " + taxExmpnProps[i] + " : " + property);

	          if (property != null){
					if ("organizationAddress".equalsIgnoreCase(taxExmpnProps[i])) {

						RepositoryItem addressItem = (RepositoryItem) property;

		        		  for (int j = 0; j < addressProps.length; j++) {
		        			  vlogDebug("handleEditTaxExemption: " + addressProps[j] + " : " + addressItem.getPropertyValue(addressProps[j]));
		        			  edit.put(addressProps[j], addressItem.getPropertyValue(addressProps[j]));
		        		  }

						}else {
							edit.put(taxExmpnProps[i], property);
						}
	          		}
	        	}
	      }

	      vlogDebug("handleEditTaxExemption: Done.");

	      return true;

	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null){
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingDebug()){
	          vlogDebug("Ignoring exception", e);
	        }
	      }
	    }
	  }

	  /**
	   * This handler updates an existing tax exemption item on the profile.
	   *
	   * @param pRequest
	   * @param pResponse
	   * @return boolean
	   * @throws ServletException
	   * @throws IOException
	   */
	  public boolean handleUpdateTaxExemption(DynamoHttpServletRequest pRequest,
	    DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		  	vlogDebug("handleUpdateTaxExemption: Called.");

		  	Map taxExmpInputMap = getEditValue();

		  	if (!isAddressVerified()){

			  	if (!isTaxExemptionAgreed()){
					  return checkFormRedirect(null, getUpdateTaxExmpErrorURL(), pRequest, pResponse);
			  	}

			    if (!validateTaxExemptionInfo(taxExmpInputMap, pRequest, pResponse )) {
			      return checkFormRedirect(null, getUpdateTaxExmpErrorURL(), pRequest, pResponse);
			    }
		  	}

		    MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
		    String nickname = (String) taxExmpInputMap.get(getNicknameValueMapKey());
		    String newNickname = ((String) taxExmpInputMap.get(getNewNicknameValueMapKey()));

		    vlogDebug("handleUpdateTaxExemption: nickname: " + nickname);
		    vlogDebug("handleUpdateTaxExemption: newNickname: " + newNickname);

		    // validation with avs
			if(!isAddressVerified()){

				Map addressInfo = profileTools.validateAddress(pRequest, taxExmpInputMap);
				if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
					setAddressMatched(true);
				} else {
					setAddressMatched(false);

					addressInfo.put("nickname", nickname);
					addressInfo.put("newNickname", newNickname);
					addressInfo.put("fname", (String) taxExmpInputMap.get("firstName"));
					addressInfo.put("lname", (String) taxExmpInputMap.get("lastName"));
					addressInfo.put("classificationId", (String) taxExmpInputMap.get("classificationId"));
					addressInfo.put("taxId",(String) taxExmpInputMap.get("taxId"));
					addressInfo.put("orgName",(String) taxExmpInputMap.get("orgName"));
					addressInfo.put("businessDesc",(String) taxExmpInputMap.get("businessDesc"));
					addressInfo.put("merchandise",(String) taxExmpInputMap.get("merchandise"));
					addressInfo.put("taxCity",(String) taxExmpInputMap.get("taxCity"));
					addressInfo.put("taxState",(String) taxExmpInputMap.get("taxState"));
					addressInfo.put("method", "updateTaxExemption");

					setSuggestedAddresses(addressInfo);

					vlogDebug("handleUpdateTaxExemption(): avsResponseURL: " + getUpdateTaxExmpSuccessURL());
					return checkFormRedirect(getUpdateTaxExmpSuccessURL(), getUpdateTaxExmpErrorURL(), pRequest, pResponse);
				}
			}

	    TransactionManager tm = getTransactionManager();
	    TransactionDemarcation td = getTransactionDemarcation();

	    try {
	      if (tm != null) {
	        td.begin(tm, TransactionDemarcation.REQUIRED);
	      }

	      Profile profile = getProfile();

	      try {

	    	  	TaxExemptionInfo taxExmpInfo = new TaxExemptionInfo();

	  	    	taxExmpInfo.setNickName(newNickname);

	  	    	String classificationId = (String) taxExmpInputMap.get("classificationId");
		  	    vlogDebug("handleUpdateTaxExemption: classificationId: " + classificationId);

		  	    taxExmpInfo.setClassificationId(classificationId);

		  	    String [] classificationInfo = this.getClassificationInfo(profileTools, classificationId);

			    taxExmpInfo.setClassificationCode(classificationInfo[0]);
			    taxExmpInfo.setClassificationName(classificationInfo[1]);

		  	    taxExmpInfo.setTaxId((String) taxExmpInputMap.get("taxId"));
		  	    taxExmpInfo.setOrgName((String) taxExmpInputMap.get("orgName"));
		  	    taxExmpInfo.setBusinessDesc((String) taxExmpInputMap.get("businessDesc"));
		  	    taxExmpInfo.setMerchandise((String) taxExmpInputMap.get("merchandise"));
		  	    taxExmpInfo.setTaxCity((String) taxExmpInputMap.get("taxCity"));
		  	    taxExmpInfo.setTaxState((String) taxExmpInputMap.get("taxState"));

		        Address addressObject = null;
				try {
					addressObject = AddressTools.createAddressFromMap(taxExmpInputMap,
					  profileTools.getShippingAddressClassName());
				} catch (InstantiationException e) {
					vlogError("handleUpdateTaxExemption(): InstantiationException occurred: " +  e, e);
					throw new ServletException(e);
				} catch (IllegalAccessException e) {
					vlogError("handleUpdateTaxExemption(): IllegalAccessException occurred: " +  e, e);
					throw new ServletException(e);
				} catch (ClassNotFoundException e) {
					vlogError("handleUpdateTaxExemption(): ClassNotFoundException occurred: " +  e, e);
					throw new ServletException(e);
				} catch (IntrospectionException e) {
					vlogError("handleUpdateTaxExemption(): IntrospectionException occurred: " +  e, e);
					throw new ServletException(e);
				}

		        profileTools.updateTaxExemptionInfo(profile, nickname, newNickname, taxExmpInfo, addressObject);

	      } catch (RepositoryException repositoryExc) {
	        addFormException("Exception while updating Tax exemption.", repositoryExc, pRequest);

	        if (isLoggingError()) {
	          logError(repositoryExc);
	        }

	        return checkFormRedirect(null, getUpdateTaxExmpErrorURL(), pRequest, pResponse);
	      }

	      taxExmpInputMap.clear();
	      setAddressMatched(true);
	      return checkFormRedirect(getUpdateTaxExmpSuccessURL(), getUpdateTaxExmpErrorURL(), pRequest, pResponse);
	    } catch (TransactionDemarcationException e) {
	      throw new ServletException(e);
	    } finally {
	      try {
	        if (tm != null) {
	          td.end();
	        }
	      } catch (TransactionDemarcationException e) {
	        if (isLoggingError()) {
	          logError("handleUpdateTaxExemption: Can't end transaction ", e);
	        }
	      }
	    }

	  }


	  /**
	   * This handler deletes an existing tax exemption item on the profile.
	   *
	   * @param pRequest
	   * @param pResponse
	   * @return boolean
	   * @throws ServletException
	   * @throws IOException
	   */
	  public boolean handleRemoveTaxExemption(DynamoHttpServletRequest pRequest,
	    DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		  vlogDebug("handleRemoveTaxExemption: Called.");

		    TransactionManager tm = getTransactionManager();
		    TransactionDemarcation td = getTransactionDemarcation();

		    try {
		      if (tm != null) {
		        td.begin(tm, TransactionDemarcation.REQUIRED);
		      }
		      String removingNickname = getRemoveTaxExemption();

		      vlogDebug("handleRemoveTaxExemption: removingNickname: " +removingNickname);

		      if ((removingNickname == null) || (removingNickname.trim().length() == 0)) {
		        if (isLoggingDebug()) {
		          vlogDebug("A null or empty nickname was provided to handleRemoveTaxExemption.");
		        }
		        return true;
		      }

		      Profile profile = getProfile();

		      Map taxExmpns = (Map) profile.getPropertyValue("taxExemptions");
		      taxExmpns.remove(removingNickname);

		      /** RepositoryItem defaultExmp = (RepositoryItem) profile.getPropertyValue("defaultTaxExemption");
		      String defaultTaxExmpNickName = (String) defaultExmp.getPropertyValue("nickName");

		  	  if (defaultTaxExmpNickName.equalsIgnoreCase(removingNickname) && taxExmpns.size() > 0){

		  		vlogDebug("handleRemoveTaxExemption: default tax exmp was removed, and there is a tax exmp avaialble on profile.");
		  		RepositoryItem availableTaxExmp = (RepositoryItem) taxExmpns.get(0);

				if (availableTaxExmp != null) {

					MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
					String availableTaxExmpNickName = (String) availableTaxExmp.getPropertyValue("nickName");
					vlogDebug("handleRemoveTaxExemption: availableTaxExmpNickName: " + availableTaxExmpNickName);

					try {
						profileTools.setDefaultTaxExemption(profile, availableTaxExmpNickName);
					} catch (RepositoryException e) {

						if (isLoggingError()) {
							logError("handleRemoveTaxExemption(): Repository Exception while setting default tax exemption: " + e, e);
						}
					}
				}
		  	  } */

		  	return checkFormRedirect(getRemoveTaxExmpSuccessURL(), getRemoveTaxExmpErrorURL(), pRequest, pResponse);

		    } catch (TransactionDemarcationException e) {
		      throw new ServletException(e);
		    } finally {
		      try {
		        if (tm != null) {
		          td.end();
		        }
		      } catch (TransactionDemarcationException e) {
		        if (isLoggingError()) {
		          logError("Can't end transaction ", e);
		        }
		      }
		    }
	  }

	  /**
	   * This handler makes a tax exemption item as default, on the profile.
	   *
	   * @param pRequest
	   * @param pResponse
	   * @return boolean
	   * @throws ServletException
	   * @throws IOException
	   */
	  public boolean handleDefaultTaxExemption(DynamoHttpServletRequest pRequest,
			    DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		  vlogDebug("handleDefaultTaxExemption: Called.");

		    TransactionManager tm = getTransactionManager();
		    TransactionDemarcation td = getTransactionDemarcation();

		    try {
		      if (tm != null) {
		        td.begin(tm, TransactionDemarcation.REQUIRED);
		      }

		      Profile profile = getProfile();
		      MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
		      String taxExmpNickname = getDefaultTaxExemption();

		      if (StringUtils.isBlank(taxExmpNickname)) {

		        if (isLoggingDebug()) {
		          vlogDebug("A null or empty nickname was provided to handleDefaultTaxExemption");
		        }

		        return true;
		      }

		      // Set requested tax exemption as default
		      try {
				profileTools.setDefaultTaxExemption(profile, taxExmpNickname);
			} catch (RepositoryException e) {

				if (isLoggingError()){
					logError("Repository Exception while setting tax exemption as default: " + e, e);
				}
			}

		      return true;
		    } catch (TransactionDemarcationException e) {
		      throw new ServletException(e);
		    } finally {
		      try {
		        if (tm != null) {
		          td.end();
		        }
		      } catch (TransactionDemarcationException e) {
		        if (isLoggingError()) {
		          logError("Can't end transaction ", e);
		        }
		      }
		    }
		  }

	/**
	 * Creates a new credit card using the entries entered in the editValue map
	 *
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 * @return true if success, false - otherwise
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleCreateNewCreditCard(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = getTransactionDemarcation();
		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
		MFFProfileTools profileTools =  (MFFProfileTools) getProfileTools();

		preCreateNewCreditCard(pRequest, pResponse);

		if(getFormError()){
			return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
		}

		Profile profile = getProfile();

		try {
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}

			Map secondaryAddress = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

			Map newCard = (HashMap) getEditValue();
			Map address = (HashMap) getBillAddrValue();

			try {
				String newBillAddress = (String) address.get("addrNickname");

				vlogDebug ("handleCreateNewCreditCard(): newBillAddress: " + newBillAddress);

				Boolean isSavedAddress  = ((CommerceProfileTools)getProfileTools()).isDuplicateAddressNickName(profile, newBillAddress);
				vlogDebug ("handleCreateNewCreditCard(): isSavedAddress: " + isSavedAddress);
				String newCreditCard = null;
				Object defaultCard = profile.getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());

				if(isSavedAddress){
					Object addressObj = secondaryAddress.get(newBillAddress);

					//tokenize credit card and store it in map.
					ContactInfo existingAddressObj = (ContactInfo) ((CommerceProfileTools)getProfileTools()).getAddressFromRepositoryItem((RepositoryItem)addressObj);
					getAddressValidator().validateAddress(existingAddressObj, pRequest, this);
			    List<DropletException> errorList = getFormExceptions();

			    vlogDebug("handleCreateNewCreditCard: Error List Size:{0}", errorList.size());

			    if (errorList != null && !errorList.isEmpty()) {
			      for (int i = 0; i < errorList.size(); i++) {
			        vlogDebug("handleCreateNewCreditCard: Error:{0}, : Message:{1}", i, errorList.get(i).getMessage());
			        // For reg user, if there is form field error add it to the main error as well
		          DropletException dException = errorList.get(i);
		          if(dException instanceof MFFInlineDropletFormException)
		            getFormExceptionGenerator().generateException(dException.getMessage(), this);
			      }
			    }
					if(getFormError()){
            return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
          }

					String tokenNumber = tokenizeCreditCard(pRequest, pResponse, newCard, existingAddressObj);

					if (StringUtils.isEmpty(tokenNumber)) {
						vlogDebug("handleCreateNewCreditCard() with saved address: token empty, returning.");
			        	addFormException(new DropletException("Invalid credit card data. Please retry with correct details."));
		        		return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
					}

					newCard.put(cpmgr.getCreditCardNumberPropertyName(), profileTools.getLastFourForCreditCard((String) newCard.get(cpmgr.getCreditCardNumberPropertyName())));
					newCreditCard = profileTools.createProfileCreditCardWithExistingAddress(profile, newCard, (String)newCard.get("creditCardNickname"), (RepositoryItem)addressObj);

				} else {

					// new address logic
					if(!StringUtils.isBlank(newBillAddress)){

						// validating for required fields
						validateRequiredAddressFields(newCard, pRequest, pResponse);
						validateAddress(newCard, pRequest, pResponse);

						if(getFormError()){
							return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
						}

						//invoking validate address
						if(!isAddressVerified()){

							if (!getZipCodeHelper().isValidateCityStateZipCombination(newCard)){
								getFormExceptionGenerator().generateInlineException(getErrorMessage(
										MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION, null), getZipCodeHelper().getCityStateZipErrorField(), this, pRequest);
								return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
							}

							Map addressInfo = profileTools.validateAddress(pRequest, newCard);
							if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
								setAddressMatched(true);
							} else {
								setAddressMatched(false);
								setSuggestedAddresses(addressInfo);
								// sending all user entered info back to response page
								setEditValue(newCard);
								return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
							}
						}

						address = MFFAddressTools.copyAddress(newCard, address);

					    // Generate unique nickname if it is not provided by the user
					    String nickname = (String) address.get(getNicknameValueMapKey());
					    if (StringUtils.isBlank(nickname)) {
					      nickname = ((MFFProfileTools) getProfileTools()).getUniqueShippingAddressNickname(address, profile, null);
					    }
					    // Create an Address object from the values the user entered.
				        Address addressObject = AddressTools.createAddressFromMap(address, ((MFFProfileTools) getProfileTools()).getShippingAddressClassName());

					    // tokenize credit card and store it in map.
						String tokenNumber = tokenizeCreditCard(pRequest, pResponse, newCard, (ContactInfo)addressObject);

						if (StringUtils.isEmpty(tokenNumber)) {
							vlogDebug("handleCreateNewCreditCard() with new address: token empty, returning.");
				        	addFormException(new DropletException("Invalid credit card data. Please retry with correct details."));
			        		return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
						}

						newCard.put(cpmgr.getCreditCardNumberPropertyName(), ((MFFProfileTools)getProfileTools()).getLastFourForCreditCard((String) newCard.get(cpmgr.getCreditCardNumberPropertyName())));
				        newCreditCard = ((CommerceProfileTools)getProfileTools()).createProfileCreditCard(profile, newCard, (String)newCard.get("creditCardNickname"), addressObject);

				        /*get Creditcard item to get the contactinfo item object Id
				         * set ownerid to contactinfo item
				         * set address item to profile secondary addresses */
				        RepositoryItem lCreditCardRepoItem = ((CommerceProfileTools)getProfileTools()).getCreditCardByNickname(newCreditCard, profile);
				        RepositoryItem lAddressRepoItem = (RepositoryItem)lCreditCardRepoItem.getPropertyValue("billingAddress");
				        MutableRepositoryItem lMutableAddressRepoItem =((MutableRepository) profile.getRepository()).getItemForUpdate(lAddressRepoItem.getRepositoryId(), lAddressRepoItem.getItemDescriptor().getItemDescriptorName());
				        lMutableAddressRepoItem.setPropertyValue("ownerId", getProfileItem().getRepositoryId());
				        ((MutableRepository) profile.getRepository()).updateItem(lMutableAddressRepoItem);
				        ((CommerceProfileTools)getProfileTools()).addProfileRepositoryAddress(getProfileItem(), nickname, lAddressRepoItem);
					} else {
						// none of the address selected.
						addFormException(new DropletException("Please select billing address"));
					}
				}
				if (!StringUtils.isEmpty(newCreditCard) && defaultCard == null) {
					((CommerceProfileTools) getProfileTools()).setDefaultCreditCard(profile, newCreditCard);
				}
				newCard.clear();
				address.clear();
				setAddressMatched(true);
				return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
			} catch (RepositoryException | IntrospectionException | PropertyNotFoundException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				addFormException(new DropletException("Please try again"));
				vlogError(e, String.format("An error occurred while trying to update the payment information for the profile {0}", getProfile().getRepositoryId()));
			}

			return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
		} catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		} finally {
			try {
				if (tm != null) {
					td.end();
				}
			} catch (TransactionDemarcationException e) {
			}
		}
	}




	/**
	 * This method call ACI pipeline to get token number.
	 * @param pRequest
	 * @param pResponse
	 * @param newCard
	 * @param addressObject
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private String tokenizeCreditCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map newCard,
			ContactInfo addressObject) throws ServletException, IOException {

		vlogDebug("tokenizeCreditCard(): Called.");

		String tokenNumber = null;
		try {
			AciCreditCardInfo aciCardInfo = generateCreditCardInfo(newCard, addressObject);
			getAciService().tokenizeCreditCard(aciCardInfo);
			tokenNumber = aciCardInfo.getTokenNumber();
			if (!StringUtils.isEmpty(tokenNumber)){
				newCard.put(AciConstants.PROPERTY_CC_TOKEN_NUMBER, tokenNumber);
				String mopType=aciCardInfo.getMopTypeCd();
				if (!StringUtils.isEmpty(mopType)){
					newCard.put(AciConstants.PROPERTY_ACI_MOP_TYPE_CODE, mopType);
				}
			}
		} catch (AciPipelineException e) {
			vlogError("Exception occured during tokenizing card.");
		}

		return tokenNumber;
	}

	private AciCreditCardInfo generateCreditCardInfo (Map pInputCard, ContactInfo pBillingAddress){
		AciCreditCardInfo ccInfo = new AciCreditCardInfo();
		ccInfo.setCreditCardNumber((String) pInputCard.get("creditCardNumber"));
		ccInfo.setSecurityCode(((String) pInputCard.get("cvv")));
		ccInfo.setCreditCardType((String) pInputCard.get("creditCardType"));
		ccInfo.setExpirationMonth((String)pInputCard.get("expirationMonth"));
		ccInfo.setExpirationYear((String)pInputCard.get("expirationYear"));
		ccInfo.setOnlineOrder(true);
		ccInfo.setBillingAddress(pBillingAddress);
		return ccInfo;
	}


	/**
	 * This method is used to validate the credit card info and billing info.
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preCreateNewCreditCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
		ResourceBundle bundle = ResourceUtils.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
		HashMap newCard = (HashMap) getEditValue();

		String ccNumber = (String) newCard.get(cpmgr.getCreditCardNumberPropertyName());
		String ccExpiryMonth = (String) newCard.get(cpmgr.getCreditCardExpirationMonthPropertyName());
		String ccExpiryYear = (String) newCard.get(cpmgr.getCreditCardExpirationYearPropertyName());

		if (((MFFProfileTools) getProfileTools()).isCreditCardExistInProfile(ccNumber, getProfile(), ccExpiryMonth + ccExpiryYear)){

			addFormException(new DropletException("Card is already added to the account."));
			return;
		}

		validateCardInfo(cpmgr, newCard);

		if(getFormError()){
			return;
		}
		validateCreditCard(newCard, bundle);

	}

	@SuppressWarnings({ "rawtypes" })
	private void validateCardInfo(CommercePropertyManager cpmgr, HashMap newCard) {
		Iterator cardPropertyIterator = getCardPropertyList().iterator();
		Object property = null;
		String propertyName = null;

		while (cardPropertyIterator.hasNext()) {
			propertyName = (String) cardPropertyIterator.next();
			property = newCard.get(propertyName);
			if ((property != null) && (((String) property).length() != 0))
				continue;
			addFormException(new DropletFormException("missingCreditCardProperty " ,propertyName ));
		}
	}

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	@Override
	protected boolean validateCreditCard(HashMap card, ResourceBundle bundle) {
		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();

		String cardNickname = (String) card.get(cpmgr.getCreditCardNicknamePropertyName());
		if(StringUtils.isBlank(cardNickname)){
			addFormException(new DropletException("The payment name is a required field for a credit card entry"));
		}
		String editCardName = (String) card.get(("editCardName"));
		validateCardNickNameForDuplicate(editCardName, cardNickname);

		String cardType = (String) card.get(cpmgr.getCreditCardTypePropertyName());
		if(StringUtils.isBlank(cardType)){
			addFormException(new DropletException("The card type is a required field for a credit card entry"));
		}

		String cvv = (String) card.get("cvv");
		if(StringUtils.isBlank(cvv)){
			addFormException(new DropletException("The cvv is a required field for a credit card entry"));
		}
		for (int i = 0; i < cvv.length(); ++i) {
			char c = cvv.charAt(i);
			if ((((c < '0') || (c > '9'))) && (c != ' ')) {
				addFormException(new DropletException("The CVV must consist only of digits"));
			}

		}

		AciCreditCardInfo ccInfo = new AciCreditCardInfo();
		ccInfo.setExpirationYear((String) card.get(cpmgr.getCreditCardExpirationYearPropertyName()));
		ccInfo.setExpirationMonth((String) card.get(cpmgr.getCreditCardExpirationMonthPropertyName()));

		String ccNumber = (String) card.get(cpmgr.getCreditCardNumberPropertyName());

		if (ccNumber != null) {
			ccNumber = StringUtils.removeWhiteSpace(ccNumber);
		}

		ccInfo.setCreditCardNumber(ccNumber);
		ccInfo.setCreditCardType((String) card.get(cpmgr.getCreditCardTypePropertyName()));

		/*// verify card exp date
		int ccreturn = ((MFFProfileTools) getProfileTools())
				.verifyCreditCardDate(ccInfo.getExpirationMonth(),
						ccInfo.getExpirationDayOfMonth(),
						ccInfo.getExpirationYear());

		if (ccreturn != getCreditCardTools().SUCCESS) {
			String msg = getCreditCardTools().getStatusCodeMessage(ccreturn);
			addFormException(new DropletFormException(msg, null));

			return false;
		}*/


		//To fix bug 2364
    // verify card number & card type etc.
    int ccreturn = getCreditCardTools().verifyCreditCard(ccInfo);

    if (ccreturn != getCreditCardTools().SUCCESS) {
      String msg = getCreditCardTools().getStatusCodeMessage(ccreturn);
      addFormException(new DropletFormException(msg, null));
      return false;
    }

    /*
     * The following is to ensure that the credit card type is set to Fleet Rewards VISA if the following criteria is met:
     * the user selected VISA
     * the user entered a Fleet Rewards VISA card number
     */
      if (cardType.equalsIgnoreCase("visa")) {
        ccInfo.setCreditCardType("millsVisa");
        if (getCreditCardTools().verifyCreditCard(ccInfo) == getCreditCardTools().SUCCESS) {
          card.put(cpmgr.getCreditCardTypePropertyName(),"millsVisa");
        }
      }

		return true;
	}


	/**
	 * This method is used to get the credit card types and codes.
	 * @return
	 */
	public Map<String, String> getCreditCardTypes() {
        Map<Object, Object> cardTypes = getCreditCardTools().getCardTypesMap();
        Map<Object, Object> cardCodes = getCreditCardTools().getCardCodesMap();
        Map<String, String> cards = new HashMap<String, String>();

        if(!cardTypes.isEmpty() && !cardCodes.isEmpty()){
        	Set<Object> commonKeys = cardTypes.keySet();
        	String cardType = null;
        	String cardCode = null;
        	for (Object key : commonKeys) {
        		cardCode = (String)cardCodes.get(key);
        		cardType = (String) cardTypes.get(key);
        		cards.put(cardCode, cardType);
        		cardCode = null;
        		cardType = null;
			}
        }
        return cards;
    }

	/**
	 * Updates the credit card as modified by the user.
	 *
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 * @exception RepositoryException
	 *                if there was an error accessing the repository
	 * @return true if success, false - otherwise
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleUpdateCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

		preUpdateCard(pRequest, pResponse);

		if(getFormError()){
			return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
		}

		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = getTransactionDemarcation();
		MFFProfileTools profiletools = (MFFProfileTools) getProfileTools();
		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();

		try {
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}

			Profile profile = getProfile();

			Map editValue = getEditValue();
			Map billAddrValue = getBillAddrValue();

			Map secondaryAddress = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
			String addressName = (String) billAddrValue.get("addrNickname");

			Boolean isSavedAddress  = ((CommerceProfileTools)getProfileTools()).isDuplicateAddressNickName(profile, addressName);

			try {

				String newNickname = (String) editValue.get("creditCardNickname");
				Object defaultCard = profile.getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());

				// Get credit card to update
				RepositoryItem cardToUpdate = findCurrentCreditCard();

				boolean isCardNumberChanged = profiletools.isCardNumberChanged(cardToUpdate, editValue, cpmgr);
				vlogDebug("handleUpdateCard(): isCardNumberChanged: " + isCardNumberChanged);

				boolean isCardTypeChanged = false;
				boolean isExpChanged = false;

				if (!isCardNumberChanged){
					isExpChanged = profiletools.isExpDateChanged(cardToUpdate, editValue, cpmgr);
					vlogDebug("handleUpdateCard(): isExpChanged: " + isExpChanged);
				}
				if (!isCardNumberChanged && !isExpChanged){
					isCardTypeChanged = profiletools.isCardTypeChanged(cardToUpdate, editValue, cpmgr);
					vlogDebug("handleUpdateCard(): isCardTypeChanged: " + isCardTypeChanged);
				}
				boolean isTokenRequired = isCardNumberChanged || isCardTypeChanged || isExpChanged;

				if (isTokenRequired && profiletools.isMaskedCardNumber(editValue, cpmgr)){
					vlogDebug("handleUpdateCard(): Tokenization is required, but card is masked, returning to get full card number.");
					addFormException(new DropletException("Please enter full credit card number to modify card number or Card type or Expiry date."));
					return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
				}

				if(isSavedAddress){
					RepositoryItem billingItem = (RepositoryItem) secondaryAddress.get(addressName);

					//tokenize credit card and store it in map.
					ContactInfo existingAddressObj = (ContactInfo) ((CommerceProfileTools)getProfileTools()).getAddressFromRepositoryItem(billingItem);

					if (isTokenRequired){
						String tokenNumber = tokenizeCreditCard(pRequest, pResponse, editValue, existingAddressObj);

						if (StringUtils.isEmpty(tokenNumber)) {
							vlogDebug("handleUpdateCard() with saved address: token empty, returning.");
							addFormException(new DropletException("Invalid credit card data. Please retry with correct details."));
							return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
						}
					}

					editValue.put(cpmgr.getCreditCardNumberPropertyName(), ((MFFProfileTools)getProfileTools()).getLastFourForCreditCard((String) editValue.get(cpmgr.getCreditCardNumberPropertyName())));
					profiletools.updateProfileCreditCard(cardToUpdate, profile, editValue, newNickname, billingItem);

				} else {
					// validating for required fields
					validateRequiredAddressFields(editValue, pRequest, pResponse);
					validateAddress(editValue, pRequest, pResponse);

					if(getFormError()){
						return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
					}
					//invoking validate address
					if(!isAddressVerified()){

						if (!getZipCodeHelper().isValidateCityStateZipCombination(editValue)){
							getFormExceptionGenerator().generateInlineException(getErrorMessage(
									MFFConstants.MSG_INVALID_CITY_STATE_ZIP_COMBINATION, null), getZipCodeHelper().getCityStateZipErrorField(), this, pRequest);
							return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
						}

						Map addressInfo = profiletools.validateAddress(pRequest, editValue);
						if(Boolean.parseBoolean(addressInfo.get("addressMatch").toString())){
							setAddressMatched(true);
						} else {
							setAddressMatched(false);
							setSuggestedAddresses(addressInfo);
							// sending all user entered info back to response page
							setEditValue(editValue);
							return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
						}
					}

					billAddrValue = MFFAddressTools.copyAddress(editValue, billAddrValue);
					billAddrValue.remove("nickname");
					// Update credit card
					//profiletools.updateProfileCreditCard(cardToUpdate, profile, editValue, newNickname, billAddrValue, profiletools.getBillingAddressClassName());

					billAddrValue = MFFAddressTools.copyAddress(editValue, billAddrValue);

				    // Generate unique nickname if it is not provided by the user
				    String nickname = (String) billAddrValue.get(getNicknameValueMapKey());
				    if (StringUtils.isBlank(nickname)) {
				      nickname = ((MFFProfileTools) getProfileTools()).getUniqueShippingAddressNickname(billAddrValue, profile, null);
				    }
				    // Create an Address object from the values the user entered.
			        Address addressObject = AddressTools.createAddressFromMap(billAddrValue, ((MFFProfileTools) getProfileTools()).getShippingAddressClassName());

			        // Create an entry in the secondaryAddress map on the profile, for this
			        // new address. Set this new Id as the newAddressId so it can be picked
			        // up on the success page (used to select it in a dropdown).
			        String newAddressId = ((MFFProfileTools) getProfileTools()).createProfileRepositorySecondaryAddress(profile, nickname, addressObject);
			        setNewAddressId(newAddressId);

			        if (isTokenRequired) {
			        	//tokenize credit card and store it in map.
				        String tokenNumber = tokenizeCreditCard(pRequest, pResponse, editValue, (ContactInfo)addressObject);

				        if (StringUtils.isEmpty(tokenNumber)) {
				        	vlogDebug("handleUpdateCard() with new address: token empty, returning.");
				        	addFormException(new DropletException("Invalid credit card data. Please retry with correct details."));
			        		return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
						}
			        }

			        editValue.put(cpmgr.getCreditCardNumberPropertyName(), ((MFFProfileTools)getProfileTools()).getLastFourForCreditCard((String) editValue.get(cpmgr.getCreditCardNumberPropertyName())));
					profiletools.updateCardWithNewAddress(cardToUpdate, profile, editValue, newNickname, newAddressId);
				}

				// save this card as default if needed
				if (!StringUtils.isEmpty(newNickname) && defaultCard == null) {
					((CommerceProfileTools) getProfileTools()).setDefaultCreditCard(profile, newNickname);
				}


			} catch (RepositoryException e) {
				vlogError("RepositoryException occurred in handleUpdateCard: "+e, e);
				return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
			} catch (InstantiationException ex) {
				throw new ServletException(ex);

			} catch (IllegalAccessException ex) {
				throw new ServletException(ex);

			} catch (ClassNotFoundException ex) {
				throw new ServletException(ex);
			} catch (IntrospectionException ex) {
				throw new ServletException(ex);
			}
			billAddrValue.clear();
			editValue.clear();
			setAddressMatched(true);
			return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest, pResponse);
		} catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		} finally {
			try {
				if (tm != null) {
					td.end();
				}
			} catch (TransactionDemarcationException e) {
			}
		}
	}

	/**
	 * Called before handleUpdateCard logic is applied. Adds properties listed
	 * in immutableCardProperties property to editValue map.
	 *
	 * @param pRequest
	 *            current request
	 * @param pResponse
	 *            current response
	 * @throws RepositoryException
	 *             if unable to obtain user card's properties.
	 */
	protected void preUpdateCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RepositoryException {

		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
		ResourceBundle bundle = ResourceUtils.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
		HashMap newCard = (HashMap) getEditValue();

		validateCardInfo(cpmgr, newCard);

		if(getFormError()){
			return;
		}
		validateCreditCard(newCard, bundle);

	}

	/**
	 * Searches current user's credit card by nick-name from editValue
	 * properties.
	 *
	 * @return credit card if found.
	 */
	private RepositoryItem findCurrentCreditCard() {
		String cardId = (String) getEditValue().get("cardId");
		RepositoryItem cardToUpdate = ((CommerceProfileTools) getProfileTools()).getCreditCardById(cardId);
		return cardToUpdate;
	}


	/**
	 * Updates the profile as modified by the user.
	 *
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 * @exception ServletException
	 *                if there was an error while executing the code
	 * @exception IOException
	 *                if there was an error with servlet io
	 * @exception RepositoryException
	 *                if there was an error accessing the repository
	 * @return true if success, false - otherwise
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleUpdateProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {
		vlogDebug("handleUpdateProfile(): Called.");
		preUpdateProfile(pRequest, pResponse);
		if(getFormError()){
			return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
		}
		Profile profile = getProfile();

		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = getTransactionDemarcation();

		try {
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}

			MutableRepository repository = (MutableRepository) profile.getRepository();
			MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
			updateProfileAttributes(mutableItem, getValue(), pRequest, pResponse);
			repository.updateItem(mutableItem);

			return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
		} catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		} finally {
			try {
				if (tm != null) {
					td.end();
				}
			} catch (TransactionDemarcationException e) {
			}
		}
	}

	/**
	 * This method is used to update the profile data of user.
	 * @param pRequest
	 * @param pResponse
	 */
	@SuppressWarnings("unchecked")
	private void preUpdateProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		String firstName = getStringValueProperty(getPropertyManager().getFirstNamePropertyName());
		if(StringUtils.isBlank(firstName)){
			addFormException(new DropletException("Enter firstName"));
		}
		String lastName = getStringValueProperty(getPropertyManager().getLastNamePropertyName());
		if(StringUtils.isBlank(lastName)){
			addFormException(new DropletException("Enter LastName"));
		}
		String phoneNumber = getStringValueProperty("phoneNumber");
		if(!StringUtils.isBlank(phoneNumber)){
			phoneNumber = phoneNumber.replaceAll("-", "");
			getValue().put("phoneNumber", phoneNumber);
		}
		
		// check if SOM Card is being entered
		// if entered, lets validate that it is good
		String somCard = getStringValueProperty( ((MFFPropertyManager)getPropertyManager()).getSomCardPropertyName());
		if(!StringUtils.isBlank(somCard)){
			// validate employee
			
			if(StringUtils.isBlank(phoneNumber)){
				addFormException(new DropletException("Employees must have a valid phone on the account."));
			} else {
				boolean empProfileExists = false;
				RepositoryItem employee = getEmployeeManager().findEmployee(somCard,phoneNumber);
				if(employee != null) {
					
					// check that the employee info is not on another profile
					MFFRepositoryProfileItemFinder itemFinder = (MFFRepositoryProfileItemFinder) getProfileTools().getProfileItemFinder();
					RepositoryItem [] profiles = itemFinder.findByEmployeeInfo(somCard,phoneNumber,(String)employee.getPropertyValue("employeeId"));
					if(profiles != null) {
						// more than one profile found
						if (profiles.length > 1) {
							getValue().put("employee", false);
							getValue().put("validated", false);
							getValue().put("employeeId","");
							getValue().put("somCard","");
							addFormException(new DropletException("Unable to validate employee."));							
						} else if(profiles.length == 1) {
							// check if the emp record belongs to the current profile
							if (!profiles[0].getRepositoryId().equalsIgnoreCase(getProfile().getRepositoryId())) {
								getValue().put("employee", false);
								getValue().put("validated", false);
								getValue().put("employeeId","");
								getValue().put("somCard","");
								addFormException(new DropletException("This Employee Card # is saved to another account. Employee Card # can be associated with only one online account."));								
							} else {
								getValue().put("employee", true);
								getValue().put("validated", true);
								getValue().put("employeeId", (String)employee.getPropertyValue("employeeId"));
							}
						}
					} else { 
						getValue().put("employee", true);
						getValue().put("validated", true);
						getValue().put("employeeId", (String)employee.getPropertyValue("employeeId"));
					}
				} else {
					getValue().put("employee", false);
					getValue().put("validated", false);
					getValue().put("employeeId","");
					getValue().put("somCard","");					
					addFormException(new DropletException("Unable to validate employee."));
				}
			}
		} else {
			getValue().put("employee", false);
			getValue().put("validated", false);
			getValue().put("employeeId","");
			getValue().put("somCard","");			
		}
		
	}


	/**
	 * THis method is used to set the success & error URLs
	 */
	public boolean handleSetExpressCheckoutPreferences(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		super.handleSetExpressCheckoutPreferences(pRequest, pResponse);

		MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
		Profile profile = getProfile();

		RepositoryItem defaultShippingAddress = profileTools.getDefaultShippingAddress(profile);
		RepositoryItem defaultPaymentMethod = profileTools.getDefaultCreditCard(profile);
		String defaultShippingMethod = profileTools.getDefaultShippingMethod(profile);

		if (defaultShippingAddress == null || defaultPaymentMethod == null || StringUtils.isEmpty(defaultShippingMethod)){
			String resourceMsg = MFFUtils.splitMessageFromKey(MFFConstants.getEXTNResources().getString(MFFConstants.MSG_EXP_CHECKOUT_INCOMPLETE));
			addFormException(new DropletException(resourceMsg));
		}

		return checkFormRedirect(getExpressCheckoutPreferencesSuccessURL(), getExpressCheckoutPreferencesErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to get the details of the order
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleTrackOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,IOException {

		String email = getStringValueProperty(getPropertyManager().getEmailAddressPropertyName());
		String orderNumber = (String) getEditValue().get("orderNumber");
		vlogDebug("handleTrackOrder(): Called with orderNumber: " + orderNumber);

		preTrackOrder(email, orderNumber);

		if(getFormError()){
			return checkFormRedirect(getTrackOrderSuccessURL(), getTrackOrderErrorURL(), pRequest, pResponse);
		}

		boolean isRegularOrder = false;
		RepositoryItem orderItem = null;
		try {

			ArrayList orderPrefixes = getOrderDetailHelper().getOrderPrefixes();

			for(int j = 0; j < orderPrefixes.size(); j++){
	    	  String prefixId = (String) orderPrefixes.get(j);
	    	  if (StringUtils.toUpperCase(orderNumber).startsWith(prefixId)){
	    		  orderNumber = StringUtils.toUpperCase(orderNumber);
	    		  isRegularOrder = true;
	    		  break;
	    	  }
			}
			orderItem = getOrderDetailHelper().getOrderItemForTracking(orderNumber, isRegularOrder);
		} catch (CommerceException e) {
			vlogError(e, "CommerceException while fetching order: ");
			addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
		} catch (RepositoryException e) {
			vlogError(e, "RepositoryException while fetching order: ");
			addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
		}
		String orderId = null;
		if(orderItem == null){
			addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
		} else {
			orderId=orderItem.getRepositoryId();
			vlogDebug("handleTrackOrder(): orderId: " + orderId);
			vlogDebug("handleTrackOrder(): email: " + email);

			if (isRegularOrder){
				vlogDebug("handleTrackOrder(): Regular Order.");
				String orderEmail =(String) orderItem.getPropertyValue(MFFConstants.PROPERTY_ORDER_CONTACT_EMAIL);
				vlogDebug("handleTrackOrder(): userEmail: " + orderEmail);
				if (StringUtils.isBlank(orderEmail) || (StringUtils.isNotBlank(orderEmail) && !orderEmail.equalsIgnoreCase(email))) {
					addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
				}
			} else {

				vlogDebug("handleTrackOrder(): Legacy Order.");

				String lgcOrderEmail =(String) orderItem.getPropertyValue(MFFConstants.PROPERTY_ORDER_CONTACT_EMAIL);
				vlogDebug("handleTrackOrder(): lgcOrderEmail: " + lgcOrderEmail);

				if (!StringUtils.isBlank(lgcOrderEmail)){
					if((StringUtils.isNotBlank(lgcOrderEmail) && !lgcOrderEmail.equalsIgnoreCase(email))) {
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
					}
				} else {

					RepositoryItem profileitem= getProfileTools().getItemFromEmail(email);
					String profileIdByEmail = null;
					if (profileitem != null) {
						profileIdByEmail = profileitem.getRepositoryId();
						vlogDebug("handleTrackOrder(): profileIdByEmail: " + profileIdByEmail);

						if(profileIdByEmail.startsWith(getEnvironment().getLegacyPrefix())){
							vlogDebug("Legacy User id.");
							String [] profileIdValues = profileIdByEmail.split(getEnvironment().getLegacyPrefix());
							profileIdByEmail = profileIdValues[1];
							vlogDebug("ProfileId to compare with legacy order profile id: "+profileIdByEmail);
						}
					}

					String orderProfileId = (String) orderItem.getPropertyValue(MFFConstants.PROP_PROFILE_ID);
					vlogDebug("handleTrackOrder(): orderProfileId: " + orderProfileId);

					if (StringUtils.isBlank(profileIdByEmail) || (StringUtils.isNotBlank(profileIdByEmail) && !profileIdByEmail.equalsIgnoreCase(orderProfileId))) {
						addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_TRACK_ORDER_ERR_MSG,null)));
					}
				}
			}
		}
		return checkFormRedirect(getTrackOrderSuccessURL()+"?orderId="+orderId, getTrackOrderErrorURL(), pRequest, pResponse);
	}


	/**
	 * THis is used to validate the user entered information
	 * @param pRequest
	 * @param pResponse
	 */
	private void preTrackOrder(String pEmail, String pOrderNumber) {
		validateEmail(pEmail);

		if(StringUtils.isBlank(pOrderNumber)){
			addFormException(new DropletException(getErrorMessage(MFFConstants.MSG_INVALID_ORDER_NUMBER,null)));
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean handleRemoveCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug("handleRemoveCard(): Start.");
	    String removingCardName = getRemoveCard();
	    CommercePropertyManager cpmgr = getMFFPropertyManager();
	    MFFProfileTools profileTools = (MFFProfileTools) getProfileTools();
	    Map creditCards = (Map) getProfile().getPropertyValue(cpmgr.getCreditCardPropertyName());
	    RepositoryItem creditCardItem = (RepositoryItem) creditCards.get(removingCardName);
	    RepositoryItem defaultCreditCardItem = (RepositoryItem) getProfile().getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());
	    String defaultCardNickName = profileTools.getCreditCardNickname(getProfile(), defaultCreditCardItem);
	    if (creditCardItem != null && defaultCreditCardItem != null && defaultCreditCardItem.getRepositoryId().equalsIgnoreCase(creditCardItem.getRepositoryId())) {
	      try {
	    	  profileTools.updateProperty(cpmgr.getDefaultCreditCardPropertyName(), null, getProfileItem());
	    	  this.getProfile().setPropertyValue("expressCheckout", Boolean.FALSE);
	      } catch (RepositoryException e) {
	          vlogError("handleRemoveCard: RepositoryException: "+ e, e);
	      }
	    }

	    vlogDebug("handleRemoveCard(): Card removed for customer with id: "+getProfile().getRepositoryId());

	    super.handleRemoveCard(pRequest, pResponse);

	    profileTools.setDefaultPaymentMethod(getProfile(), removingCardName, defaultCardNickName);

	    vlogDebug("handleRemoveCard(): End.");
	    return checkFormRedirect(getRemoveCardSuccessURL(), getRemoveCardErrorURL(), pRequest, pResponse);
	 }

	@SuppressWarnings("unchecked")
	private void removeAddressFromMap(String pAddressId) {

		vlogDebug ("removeAddressFromMap(): pAddressId: " + pAddressId);
		CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();

		Map<String, RepositoryItem> secondaryAddresses = (Map<String, RepositoryItem>) getProfile().getPropertyValue(
				cpmgr.getSecondaryAddressPropertyName());

		Set<String> keys = secondaryAddresses.keySet();
		vlogDebug ("removeAddressFromMap(): keys: " + keys);

		Collection<RepositoryItem> values = secondaryAddresses.values();
		vlogDebug ("removeAddressFromMap(): values: " + values);

		for (String key : keys) {
			RepositoryItem address = secondaryAddresses.get(key);
			vlogDebug ("removeAddressFromMap(): address: " + address);
			if (address.getRepositoryId().equals(pAddressId)) {
				vlogDebug ("removeAddressFromMap(): Found match.");
				secondaryAddresses.remove(key);
				break;
			}
		}
	}

	/**
	 * Overriding the  method to send an email after updating the password
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void postChangePassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)  throws ServletException, IOException{
		getEmailManager().sendPasswordUpdateEmail(getProfileItem(), pRequest.getLocale());
	}

	public boolean handleUpdateMyHomeStore(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String selectedStoreId = getHomeStoreChosen();

		vlogDebug("handleUpdateMyHomeStore(): Invoked with selectedStoreId: " + selectedStoreId);

		if (StringUtils.isBlank(selectedStoreId)){
			return false;
		}

		if (!getFormError()) {

			TransactionManager tm = getTransactionManager();
			TransactionDemarcation td = getTransactionDemarcation();
			try {
				if (tm != null) {
					td.begin(tm, TransactionDemarcation.REQUIRED);
				}

				RepositoryItem storeItem = getStoreLocatorTools().getStoreByLocationId(selectedStoreId);
				((MFFProfileTools) getProfileTools()).setMyHomeStore(getProfile(), storeItem);

			} catch (Exception e) {
				throw new ServletException(e);
			} finally {
				try {
					if (tm != null)
						td.end();
				} catch (TransactionDemarcationException e) {
				}
			}
		}
		return checkFormRedirect(getUpdateHomeStoreSuccessURL(), getUpdateHomeStoreErrorURL(), pRequest, pResponse);
	}

	public String getPreviousEmailAddress() {
		return mPreviousEmailAddress;
	}

	public void setPreviousEmailAddress(String pPreviousEmailAddress) {
		mPreviousEmailAddress = pPreviousEmailAddress;
	}

	public void setNewAddressSuccessURL(String pNewAddressSuccessURL) {
		mNewAddressSuccessURL = pNewAddressSuccessURL;
	}

	public String getNewAddressSuccessURL() {
		return mNewAddressSuccessURL;
	}

	public void setNewAddressErrorURL(String pNewAddressErrorURL) {
		mNewAddressErrorURL = pNewAddressErrorURL;
	}

	public String getNewAddressErrorURL() {
		return mNewAddressErrorURL;
	}

	public void setUpdateAddressSuccessURL(String pUpdateAddressSuccessURL) {
		mUpdateAddressSuccessURL = pUpdateAddressSuccessURL;
	}

	public String getUpdateAddressSuccessURL() {
		return mUpdateAddressSuccessURL;
	}

	public void setUpdateAddressErrorURL(String pUpdateAddressErrorURL) {
		mUpdateAddressErrorURL = pUpdateAddressErrorURL;
	}

	public String getUpdateAddressErrorURL() {
		return mUpdateAddressErrorURL;
	}

	public void setShippingGroupMapContainer(
			ShippingGroupMapContainer pShippingGroupMapContainer) {
		mShippingGroupMapContainer = pShippingGroupMapContainer;
	}

	public ShippingGroupMapContainer getShippingGroupMapContainer() {
		return mShippingGroupMapContainer;
	}

	/**
	 * @return The id of the new address created by this form handler.
	 */
	public String getNewAddressId() {
		return mNewAddressId;
	}

	/**
	 * @param pNewAddressId
	 *            Set the id of the newly created address.
	 */
	public void setNewAddressId(String pNewAddressId) {
		mNewAddressId = pNewAddressId;
	}

	/**
	 * Utility method to retrieve the VCSPropertyManager.
	 *
	 * @return property manager
	 */
	protected PropertyManager getPropertyManager() {
		return getProfileTools().getPropertyManager();
	}

	public String getNewCustomerEmailAddress() {
		return mNewCustomerEmailAddress;
	}

	public void setNewCustomerEmailAddress(String pNewCustomerEmailAddress) {
		mNewCustomerEmailAddress = pNewCustomerEmailAddress;
	}

	public String getAnonymousEmailAddress() {
		return mAnonymousEmailAddress;
	}

	public void setAnonymousEmailAddress(String pAnonymousEmailAddress) {
		mAnonymousEmailAddress = pAnonymousEmailAddress;
	}

	public void setOrder(Order pOrder) {
		mOrder = pOrder;
	}

	public Order getOrder() {
		if (mOrder != null) {
			return mOrder;
		} else {
			return getShoppingCart().getCurrent();
		}
	}

	public Map getBillAddrValue() {
		return mBillAddrValue;
	}

	public String getNicknameValueMapKey() {
		return mNicknameValueMapKey;
	}

	public void setNicknameValueMapKey(String pNicknameValueMapKey) {
		mNicknameValueMapKey = pNicknameValueMapKey;
	}

	public String getAddressIdValueMapKey() {
		return mAddressIdValueMapKey;
	}

	public void setAddressIdValueMapKey(String pAddressIdValueMapKey) {
		mAddressIdValueMapKey = pAddressIdValueMapKey;
	}

	public String getNewNicknameValueMapKey() {
		return mNewNicknameValueMapKey;
	}

	public void setNewNicknameValueMapKey(String pNewNicknameValueMapKey) {
		mNewNicknameValueMapKey = pNewNicknameValueMapKey;
	}

	public String getShippingAddressNicknameMapKey() {
		return mShippingAddressNicknameMapKey;
	}

	public void setShippingAddressNicknameMapKey(
			String pShippingAddressNicknameMapKey) {
		mShippingAddressNicknameMapKey = pShippingAddressNicknameMapKey;
	}

	public void setDefaultShippingAddress(String pDefaultShippingAddress) {
		mDefaultShippingAddress = pDefaultShippingAddress;
	}

	public String getDefaultShippingAddress() {
		return mDefaultShippingAddress;
	}

	public void setDefaultCard(String pDefaultCard) {
		mDefaultCard = pDefaultCard;
	}

	public String getDefaultCard() {
		return mDefaultCard;
	}

	public boolean isUseShippingAddressAsDefault() {
		return mUseShippingAddressAsDefault;
	}

	public void setUseShippingAddressAsDefault(
			boolean pUseShippingAddressAsDefault) {
		mUseShippingAddressAsDefault = pUseShippingAddressAsDefault;
	}

	public String getLoginEmailAddress() {
		return mLoginEmailAddress;
	}

	public void setLoginEmailAddress(String pLoginEmailAddress) {
		mLoginEmailAddress = pLoginEmailAddress;
	}

	public String getEmailAddress() {
		return mEmailAddress;
	}

	public void setEmailAddress(String pEmailAddress) {
		mEmailAddress = pEmailAddress;
	}

	public Map<String, Object> getEditValue() {
		return mEditValue;
	}

	public void setAddressProperties(String[] pAddressProperties) {
		mAddressProperties = pAddressProperties;
	}

	public String[] getAddressProperties() {
		return mAddressProperties;
	}

	public String getRemoveAddressKey() {
		return mRemoveAddressKey;
	}

	public void setRemoveAddressKey(String pRemoveAddressKey) {
		mRemoveAddressKey = pRemoveAddressKey;
	}

	public void setEditAddress(String pEditAddress) {
		mEditAddress = pEditAddress;
	}

	public String getEditAddress() {
		return mEditAddress;
	}

	public String getAddTaxExmpSuccessURL() {
		return mAddTaxExmpSuccessURL;
	}

	public void setAddTaxExmpSuccessURL(String pAddTaxExmpSuccessURL) {
		this.mAddTaxExmpSuccessURL = pAddTaxExmpSuccessURL;
	}

	public String getAddTaxExmpErrorURL() {
		return mAddTaxExmpErrorURL;
	}

	public void setAddTaxExmpErrorURL(String pAddTaxExmpErrorURL) {
		this.mAddTaxExmpErrorURL = pAddTaxExmpErrorURL;
	}

	public String getUpdateTaxExmpSuccessURL() {
		return mUpdateTaxExmpSuccessURL;
	}

	public void setUpdateTaxExmpSuccessURL(String pUpdateTaxExmpSuccessURL) {
		this.mUpdateTaxExmpSuccessURL = pUpdateTaxExmpSuccessURL;
	}

	public String getUpdateTaxExmpErrorURL() {
		return mUpdateTaxExmpErrorURL;
	}

	public void setUpdateTaxExmpErrorURL(String pUpdateTaxExmpErrorURL) {
		this.mUpdateTaxExmpErrorURL = pUpdateTaxExmpErrorURL;
	}

	public String getRemoveTaxExmpSuccessURL() {
		return mRemoveTaxExmpSuccessURL;
	}

	public void setRemoveTaxExmpSuccessURL(String pRemoveTaxExmpSuccessURL) {
		this.mRemoveTaxExmpSuccessURL = pRemoveTaxExmpSuccessURL;
	}

	public String getRemoveTaxExmpErrorURL() {
		return mRemoveTaxExmpErrorURL;
	}

	public void setRemoveTaxExmpErrorURL(String pRemoveTaxExmpErrorURL) {
		this.mRemoveTaxExmpErrorURL = pRemoveTaxExmpErrorURL;
	}

	public String getAssignDefaultTaxExmpSuccessUrl() {
		return mAssignDefaultTaxExmpSuccessUrl;
	}

	public void setAssignDefaultTaxExmpSuccessUrl(
			String pAssignDefaultTaxExmpSuccessUrl) {
		this.mAssignDefaultTaxExmpSuccessUrl = pAssignDefaultTaxExmpSuccessUrl;
	}

	public String getAssignDefaultTaxExmpErrorUrl() {
		return mAssignDefaultTaxExmpErrorUrl;
	}

	public void setAssignDefaultTaxExmpErrorUrl(
			String pAssignDefaultTaxExmpErrorUrl) {
		this.mAssignDefaultTaxExmpErrorUrl = pAssignDefaultTaxExmpErrorUrl;
	}
	public String getEditTaxExemption() {
		return mEditTaxExemption;
	}

	public void setEditTaxExemption(String pEditTaxExemption) {
		this.mEditTaxExemption = pEditTaxExemption;
	}

	public String[] getTaxExmpnProperties() {
		return mTaxExmpnProperties;
	}

	public void setTaxExmpnProperties(String[] pTaxExmpnProperties) {
		this.mTaxExmpnProperties = pTaxExmpnProperties;
	}

	public String getRemoveTaxExemption() {
		return mRemoveTaxExemption;
	}

	public void setRemoveTaxExemption(String pRemoveTaxExemption) {
		this.mRemoveTaxExemption = pRemoveTaxExemption;
	}

	public boolean isTaxExemptionAgreed() {
		return mTaxExemptionAgreed;
	}

	public void setTaxExemptionAgreed(boolean pTaxExemptionAgreed) {
		this.mTaxExemptionAgreed = pTaxExemptionAgreed;
	}

	public String getDefaultTaxExemption() {
		return mDefaultTaxExemption;
	}

	public void setDefaultTaxExemption(String pDefaultTaxExemption) {
		this.mDefaultTaxExemption = pDefaultTaxExemption;
	}

	/**
	 * @return the updateCardSuccessURL
	 */
	public String getUpdateCardSuccessURL() {
		return mUpdateCardSuccessURL;
	}

	/**
	 * @param pUpdateCardSuccessURL the updateCardSuccessURL to set
	 */
	public void setUpdateCardSuccessURL(String pUpdateCardSuccessURL) {
		mUpdateCardSuccessURL = pUpdateCardSuccessURL;
	}

	/**
	 * @return the updateCardErrorURL
	 */
	public String getUpdateCardErrorURL() {
		return mUpdateCardErrorURL;
	}

	/**
	 * @param pUpdateCardErrorURL the updateCardErrorURL to set
	 */
	public void setUpdateCardErrorURL(String pUpdateCardErrorURL) {
		mUpdateCardErrorURL = pUpdateCardErrorURL;
	}

	/**
	 * @return the expressCheckoutPreferencesSuccessURL
	 */
	public String getExpressCheckoutPreferencesSuccessURL() {
		return mExpressCheckoutPreferencesSuccessURL;
	}

	/**
	 * @param pExpressCheckoutPreferencesSuccessURL the expressCheckoutPreferencesSuccessURL to set
	 */
	public void setExpressCheckoutPreferencesSuccessURL(
			String pExpressCheckoutPreferencesSuccessURL) {
		mExpressCheckoutPreferencesSuccessURL = pExpressCheckoutPreferencesSuccessURL;
	}

	/**
	 * @return the expressCheckoutPreferencesErrorURL
	 */
	public String getExpressCheckoutPreferencesErrorURL() {
		return mExpressCheckoutPreferencesErrorURL;
	}

	/**
	 * @param pExpressCheckoutPreferencesErrorURL the expressCheckoutPreferencesErrorURL to set
	 */
	public void setExpressCheckoutPreferencesErrorURL(
			String pExpressCheckoutPreferencesErrorURL) {
		mExpressCheckoutPreferencesErrorURL = pExpressCheckoutPreferencesErrorURL;
	}

	public String[] getRequiredAddressProperties() {
		return mRequiredAddressProperties;
	}

	public void setRequiredAddressProperties(String[] pRequiredAddressProperties) {
		this.mRequiredAddressProperties = pRequiredAddressProperties;
	}

	public String[] getRequiredTaxExmpProperties() {
		return mRequiredTaxExmpProperties;
	}

	public void setRequiredTaxExmpProperties(String[] pRequiredTaxExmpProperties) {
		this.mRequiredTaxExmpProperties = pRequiredTaxExmpProperties;
	}

	public String getRemoveAddressSuccessURL() {
		return mRemoveAddressSuccessURL;
	}

	public void setRemoveAddressSuccessURL(String pRemoveAddressSuccessURL) {
		this.mRemoveAddressSuccessURL = pRemoveAddressSuccessURL;
	}

	public String getRemoveAddressErrorURL() {
		return mRemoveAddressErrorURL;
	}

	public void setRemoveAddressErrorURL(String pRemoveAddressErrorURL) {
		this.mRemoveAddressErrorURL = pRemoveAddressErrorURL;
	}

	/**
	 * @return the trackOrderErrorURL
	 */
	public String getTrackOrderErrorURL() {
		return mTrackOrderErrorURL;
	}

	/**
	 * @param pTrackOrderErrorURL the trackOrderErrorURL to set
	 */
	public void setTrackOrderErrorURL(String pTrackOrderErrorURL) {
		mTrackOrderErrorURL = pTrackOrderErrorURL;
	}

	/**
	 * @return the trackOrderSuccessURL
	 */
	public String getTrackOrderSuccessURL() {
		return mTrackOrderSuccessURL;
	}

	/**
	 * @param pTrackOrderSuccessURL the trackOrderSuccessURL to set
	 */
	public void setTrackOrderSuccessURL(String pTrackOrderSuccessURL) {
		mTrackOrderSuccessURL = pTrackOrderSuccessURL;
	}

	public Map getSuggestedAddresses() {
		return mSuggestedAddresses;
	}

	public void setSuggestedAddresses(Map pSuggestedAddresses) {
		this.mSuggestedAddresses = pSuggestedAddresses;
	}
	public void setEditValue(Map<String, Object> pEditValue){
		mEditValue = pEditValue;
	}

	public AciService getAciService(){
		return mAciService;
	}

	public void setAciService(AciService pAciService){
		mAciService=pAciService;
	}

	public String getUpdateEmailSuccessURL() {
		return mUpdateEmailSuccessURL;
	}

	public void setUpdateEmailSuccessURL(String pUpdateEmailSuccessURL) {
		mUpdateEmailSuccessURL = pUpdateEmailSuccessURL;
	}

	public String getUpdateEmailErrorURL() {
		return mUpdateEmailErrorURL;
	}

	public void setUpdateEmailErrorURL(String pUpdateEmailErrorURL) {
		mUpdateEmailErrorURL = pUpdateEmailErrorURL;
	}

	public MFFCheckoutManager getCheckoutManager() {
		return mCheckoutManager;
	}

	public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
		mCheckoutManager = pCheckoutManager;
	}

	public boolean isCartMerged() {
		return mCartMerged;
	}

	public void setCartMerged(boolean pCartMerged) {
		mCartMerged = pCartMerged;
	}

	public long getItemCountPreLogin() {
		return mItemCountPreLogin;
	}

	public void setItemCountPreLogin(long pItemCountPreLogin) {
		mItemCountPreLogin = pItemCountPreLogin;
	}

	public RepositoryResetTokenManager getResetTokenManager() {
		return mResetTokenManager;
	}

	public void setResetTokenManager(
			RepositoryResetTokenManager pResetTokenManager) {
		mResetTokenManager = pResetTokenManager;
	}

	public int getAllowedInvalidAttempts() {
		return mAllowedInvalidAttempts;
	}

	public void setAllowedInvalidAttempts(int pAllowedInvalidAttempts) {
		mAllowedInvalidAttempts = pAllowedInvalidAttempts;
	}

	public MFFOrderDetailHelper getOrderDetailHelper() {
		return mOrderDetailHelper;
	}

	public void setOrderDetailHelper(MFFOrderDetailHelper pOrderDetailHelper) {
		mOrderDetailHelper = pOrderDetailHelper;
	}

	public MFFEnvironment getEnvironment() {
		return mEnvironment;
	}

	public void setEnvironment(MFFEnvironment pEnvironment) {
		mEnvironment = pEnvironment;
	}

	public MFFZipcodeHelper getZipCodeHelper() {
		return mZipCodeHelper;
	}

	public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
		mZipCodeHelper = pZipCodeHelper;
	}

	public boolean isCityStateZipMatched() {
		return mCityStateZipMatched;
	}

	public void setCityStateZipMatched(boolean pCityStateZipMatched) {
		mCityStateZipMatched = pCityStateZipMatched;
	}

	public MFFFormExceptionGenerator getFormExceptionGenerator() {
		return mFormExceptionGenerator;
	}

	public void setFormExceptionGenerator(
			MFFFormExceptionGenerator pFormExceptionGenerator) {
		mFormExceptionGenerator = pFormExceptionGenerator;
	}

	public List<MFFInlineDropletFormException> getFormFieldExceptions() {
		return getFormExceptionGenerator().getFormFieldExceptions(
				getFormExceptions());
	}

	public List<DropletException> getNonFormFieldExceptions() {
		return getFormExceptionGenerator().getNonFormFieldExceptions(
				getFormExceptions());
	}

	public String getHomeStoreChosen() {
		return mHomeStoreChosen;
	}

	public void setHomeStoreChosen(String pHomeStoreChosen) {
		mHomeStoreChosen = pHomeStoreChosen;
	}

	public StoreLocatorTools getStoreLocatorTools() {
		return mStoreLocatorTools;
	}

	public void setStoreLocatorTools(StoreLocatorTools pStoreLocatorTools) {
		mStoreLocatorTools = pStoreLocatorTools;
	}

	public String getUpdateHomeStoreSuccessURL() {
		return mUpdateHomeStoreSuccessURL;
	}

	public void setUpdateHomeStoreSuccessURL(String pUpdateHomeStoreSuccessURL) {
		mUpdateHomeStoreSuccessURL = pUpdateHomeStoreSuccessURL;
	}

	public String getUpdateHomeStoreErrorURL() {
		return mUpdateHomeStoreErrorURL;
	}

	public void setUpdateHomeStoreErrorURL(String pUpdateHomeStoreErrorURL) {
		mUpdateHomeStoreErrorURL = pUpdateHomeStoreErrorURL;
	}

	public MFFAddressValidator getAddressValidator() {
    return mAddressValidator;
  }

  public void setAddressValidator(MFFAddressValidator pAddressValidator) {
    mAddressValidator = pAddressValidator;
  }

public String getForceResetPasswordMessage() {
	return mForceResetPasswordMessage;
}

public void setForceResetPasswordMessage(String pForceResetPasswordMessage) {
	mForceResetPasswordMessage = pForceResetPasswordMessage;
}
}
