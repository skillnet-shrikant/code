package com.mff.commerce.csr.profile;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.mff.email.MFFEmailManager;
import com.mff.password.reset.PasswordResetTokenException;
import com.mff.password.reset.RepositoryResetTokenManager;
import com.mff.userprofiling.MFFProfileTools;
import com.mff.userprofiling.util.TaxExemptionInfo;

import atg.commerce.csr.profile.CSRCustomerProfileFormHandler;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.PropertyUpdate;
import atg.userprofiling.address.AddressTools;
import atg.web.messaging.MessageConstants;
import atg.web.messaging.MessageTools;
import atg.web.messaging.RequestMessage;

/**
 * MFFCSRCustomerProfileFormHandler extends the OOTB
 * CSRCustomerProfileFormHandler to handle Profile Form Handler (create
 * customer, update customer)
 */
public class MFFCSRCustomerProfileFormHandler extends CSRCustomerProfileFormHandler {

  private static final String REQUEST_ENTRY_REMOVE_EXEMPTION = "MFFCSRCustomerProfileFormHandler.handleRemoveTaxExemption";
  private static final String REQUEST_ENTRY_ADD_EXEMPTION = "MFFCSRCustomerProfileFormHandler.handleAddTaxExemption";
  public static final String TAX_EXEMPTIONS_PROP = "taxExemptions";

  private TransactionManager transactionManager;
  private RepeatingRequestMonitor repeatingRequestMonitor;
  private MessageTools messageTools;
  private String nickname; // The exemption nickname
  private Map<String, Object> editValue = new HashMap<String, Object>();

  private boolean taxExemptionAgreed;
  private String exemptionSuccessUrl;
  private String exemptionErrorUrl;
  private RepositoryResetTokenManager resetTokenManager;
  private MFFEmailManager mEmailManager;
  
  @Override
  protected void preUpdateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    ProfileTools profileTools = getProfileTools();
    PropertyManager propertyManager = profileTools.getPropertyManager();

    String customerEmail = (String) getValueProperty(propertyManager.getEmailAddressPropertyName());
    // In cases like adding customer notes not all the customer properties are
    // provided in the form handler
    if (!StringUtils.isEmpty(customerEmail)) {
      // Email to lowercase and set login = email
      setValueProperty(propertyManager.getEmailAddressPropertyName(), customerEmail.toLowerCase());
      setValueProperty(propertyManager.getLoginPropertyName(), customerEmail.toLowerCase());
    }
    super.preUpdateUser(pRequest, pResponse);
  }
  
  @SuppressWarnings({"unchecked" })
  @Override
  protected void postUpdateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    try {
      MutableRepositoryItem profile = getMutableProfileItem();
      if(!profile.isTransient()){
        Collection<PropertyUpdate> propertyUpdates = getPropertyUpdates();
        for(PropertyUpdate propUpdate : propertyUpdates){
          vlogDebug("PropertyUpdate PropertyName - {0}",propUpdate.getPropertyName());
          String propName = propUpdate.getPropertyName();
          if(propName.equalsIgnoreCase("email") || propName.equalsIgnoreCase("login")){
              getEmailManager().sendEmailUpdateEmail(profile,  pRequest.getLocale());
              break;
          }
        }
      }
    } catch (RepositoryException re) {
      logError("Error getting profile for update" + re);
    }
    
    super.postUpdateUser(pRequest, pResponse);
    
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Map generateResetPasswordEmailParameters(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, RepositoryItem pProfile, String pGeneratedClearTextPassword)
  {
    Map params = super.generateResetPasswordEmailParameters(pRequest, pResponse, pProfile, pGeneratedClearTextPassword);
    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    String loginPropertyName = pmgr.getLoginPropertyName();
    String login = (String)pProfile.getPropertyValue(loginPropertyName);
    if(StringUtils.isNotBlank(login))
    {
      try 
      {
        String resetToken = getResetTokenManager().generateToken(login);
        params.put("resetToken", resetToken);
      }
      catch (PasswordResetTokenException e) 
      {
        vlogDebug("PasswordResetTokenException MFFCSRCustomerProfileFormHandler.generateResetPasswordEmailParameters {0}",e.getMessage());
        e.printStackTrace();
      }
    }
    params.put("firstName", pProfile.getPropertyValue("firstName"));
    return params;
  }
  
  protected Map generateNewAccountEmailParameters(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, RepositoryItem pProfile, String pGeneratedClearTextPassword){
    Map params = super.generateNewAccountEmailParameters(pRequest, pResponse, pProfile, pGeneratedClearTextPassword);
    params.put("firstName", pProfile.getPropertyValue("firstName"));
    return params;
  }

  /**
   * This handler deletes an existing tax exemption from the customer profile.
   *
   * @param pRequest
   * @param pResponse
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleRemoveTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSRCustomerProfileFormHandler.handleRemoveTaxExemption");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    RequestMessage message = new RequestMessage();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_REMOVE_EXEMPTION)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        if (!StringUtils.isEmpty(getNickname())) {
          Profile profile = getProfile();
          Map taxExmpns = (Map) profile.getPropertyValue(TAX_EXEMPTIONS_PROP);
          taxExmpns.remove(getNickname());
          // success message to be displayed for the agent in CSC
          message.setType(MessageConstants.TYPE_INFORMATION);
          message.setSummary("Tax exemption has been removed successfully.");
          getMessageTools().addMessage(message);
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException tde) {
        // error message to be displayed for the agent in CSC
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error removing tax exemption");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_REMOVE_EXEMPTION);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getExemptionSuccessUrl(), getExemptionErrorUrl(), pRequest, pResponse);
  }

  /**
   * This handler adds an existing tax exemption to the customer profile.
   *
   * @param pRequest
   * @param pResponse
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleAddTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSRCustomerProfileFormHandler.handleAddTaxExemption");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    RequestMessage message = new RequestMessage();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_ADD_EXEMPTION)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        if (!isTaxExemptionAgreed()) {
          return checkFormRedirect(null, getExemptionErrorUrl(), pRequest, pResponse);
        }

        String nick = getNickname();
        if(validateTaxExemption(message, nick))
        {
          Map<String, Object> taxExmpInputMap = getEditValue();
          MFFProfileTools pTools = (MFFProfileTools) getProfileTools();
          TaxExemptionInfo taxExmpInfo = new TaxExemptionInfo();
          taxExmpInfo.setNickName(nick);
          taxExmpInfo.setClassificationId((String) getEditValue().get("classification"));
          String[] classificationClass = pTools.getClassificationInfo(taxExmpInfo.getClassificationId());
          if(null!=classificationClass && classificationClass.length>0)
          {
            taxExmpInfo.setClassificationCode(classificationClass[0]);
            taxExmpInfo.setClassificationName(classificationClass[1]);          
          }
          taxExmpInfo.setTaxId((String) getEditValue().get("taxId"));
          taxExmpInfo.setOrgName((String) getEditValue().get("orgName"));
          taxExmpInfo.setBusinessDesc((String) getEditValue().get("businessDesc"));
          taxExmpInfo.setMerchandise((String) getEditValue().get("merchandise"));
          taxExmpInfo.setTaxCity((String) getEditValue().get("taxCity"));
          taxExmpInfo.setTaxState((String) getEditValue().get("taxState"));
  
          Address addressObject = AddressTools.createAddressFromMap(taxExmpInputMap,
              pTools.getShippingAddressClassName());
          logDebug("nickname: " + taxExmpInfo.getNickName());
          pTools.createTaxExemptions(getProfile(), taxExmpInfo, addressObject);
        }
        rollbackTransaction = false;
      } catch (TransactionDemarcationException | RepositoryException | InstantiationException 
          | IllegalAccessException | ClassNotFoundException | IntrospectionException tde) {
        // error message to be displayed for the agent in CSC
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error adding tax exemption");
        getMessageTools().addMessage(message);
        vlogError(tde, "An exception occurred while trying to add the customer exemption");
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_ADD_EXEMPTION);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getExemptionSuccessUrl(), getExemptionErrorUrl(), pRequest, pResponse);
  }

  protected boolean validateTaxExemption(RequestMessage pMessage, String pNick)
  {
    boolean exemptionValid=true;
    // check nickname is not null
    if(StringUtils.isEmpty(pNick))
    {
      // error message to be displayed for the agent when exemption nickname is null, sanity check
      addMessageCSC(pMessage, MessageConstants.TYPE_ERROR, "Nickname is required, please select a nickname for your tax exemption");
      vlogError("Nickname is required, please select a nickname for your tax exemption");
      exemptionValid=false;
    }
    
    // validate the same nickname is not used for the customer
    Map<String, Object> taxExemptions = (Map<String, Object>)getProfile().getPropertyValue("taxExemptions");
    for (String key : taxExemptions.keySet()) 
    {
      if(key.equalsIgnoreCase(pNick))
      {
        // error message to be displayed for the agent in CSC
        addMessageCSC(pMessage, MessageConstants.TYPE_ERROR, "Nickname " + key + " already used, please select a different nickname for your tax exemption");
        vlogError("Nickname {0} already used, please select a different nickname for your tax exemption", key);
        exemptionValid=false;
      }
    }
    
    return exemptionValid;
  }
  
  protected void addMessageCSC(RequestMessage pMessage, String pType, String pMessageContent)
  {
    pMessage.setType(pType);
    pMessage.setSummary(pMessageContent);
    getMessageTools().addMessage(pMessage);
  }
  
  /**
   * This handler deletes an existing tax exemption from the customer profile.
   *
   * @param pRequest
   * @param pResponse
   * @return boolean
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleUpdateTaxExemption(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entering MFFCSRCustomerProfileFormHandler.handleRemoveTaxExemption");

    TransactionDemarcation td = null;
    RepeatingRequestMonitor rrm;
    boolean rollbackTransaction = true;
    rrm = getRepeatingRequestMonitor();
    RequestMessage message = new RequestMessage();
    if ((rrm == null) || rrm.isUniqueRequestEntry(REQUEST_ENTRY_REMOVE_EXEMPTION)) {
      try {
        td = new TransactionDemarcation();
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);


        rollbackTransaction = false;
      } catch (TransactionDemarcationException tde) {
        // error message to be displayed for the agent in CSC
        message.setType(MessageConstants.TYPE_ERROR);
        message.setSummary("Unexpected error removing tax exemption");
        getMessageTools().addMessage(message);
      } finally {
        if (rollbackTransaction) {
          try {
            setTransactionToRollbackOnly();
          } catch (SystemException e) {
            vlogError(e, "A system exception occurred while trying to mark the rollback the transaction");
          }
        }
        try {
          if (td != null) td.end();
        } catch (Exception e) {
          vlogError(e, "A transaction exception occurred while trying to end the transaction");
        }
        if (rrm != null) {
          rrm.removeRequestEntry(REQUEST_ENTRY_REMOVE_EXEMPTION);
        }
      }
    } else {
      vlogWarning("Repeating monitor found another request entry registered that has not timed out yet");
    }

    return checkFormRedirect(getExemptionSuccessUrl(), getExemptionErrorUrl(), pRequest, pResponse);
  }

  protected void setTransactionToRollbackOnly() throws SystemException {
    TransactionManager tm = getTransactionManager();
    if (tm != null) {
      tm.setRollbackOnly();
    }
  }

  /**
   * @return the transactionManager
   */
  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  /**
   * @param transactionManager
   *          the transactionManager to set
   */
  public void setTransactionManager(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * @return the repeatingRequestMonitor
   */
  public RepeatingRequestMonitor getRepeatingRequestMonitor() {
    return repeatingRequestMonitor;
  }

  /**
   * @param repeatingRequestMonitor
   *          the repeatingRequestMonitor to set
   */
  public void setRepeatingRequestMonitor(RepeatingRequestMonitor repeatingRequestMonitor) {
    this.repeatingRequestMonitor = repeatingRequestMonitor;
  }

  /**
   * @return the nickname
   */
  public String getNickname() {
    return nickname;
  }

  /**
   * @param nickname
   *          the nickname to set
   */
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  /**
   * @return the exemptionSuccessUrl
   */
  public String getExemptionSuccessUrl() {
    return exemptionSuccessUrl;
  }

  /**
   * @param exemptionSuccessUrl
   *          the exemptionSuccessUrl to set
   */
  public void setExemptionSuccessUrl(String exemptionSuccessUrl) {
    this.exemptionSuccessUrl = exemptionSuccessUrl;
  }

  /**
   * @return the exemptionErrorUrl
   */
  public String getExemptionErrorUrl() {
    return exemptionErrorUrl;
  }

  /**
   * @param exemptionErrorUrl
   *          the exemptionErrorUrl to set
   */
  public void setExemptionErrorUrl(String exemptionErrorUrl) {
    this.exemptionErrorUrl = exemptionErrorUrl;
  }

  /**
   * @return the messageTools
   */
  public MessageTools getMessageTools() {
    return messageTools;
  }

  /**
   * @param messageTools
   *          the messageTools to set
   */
  public void setMessageTools(MessageTools messageTools) {
    this.messageTools = messageTools;
  }

  /**
   * @return the editValue
   */
  public Map<String, Object> getEditValue() {
    return editValue;
  }

  /**
   * @param editValue
   *          the editValue to set
   */
  public void setEditValue(Map<String, Object> editValue) {
    this.editValue = editValue;
  }

  /**
   * @return the taxExemptionAgreed
   */
  public boolean isTaxExemptionAgreed() {
    return taxExemptionAgreed;
  }

  /**
   * @param taxExemptionAgreed
   *          the taxExemptionAgreed to set
   */
  public void setTaxExemptionAgreed(boolean taxExemptionAgreed) {
    this.taxExemptionAgreed = taxExemptionAgreed;
  }

  public RepositoryResetTokenManager getResetTokenManager() {
    return resetTokenManager;
  }

  public void setResetTokenManager(RepositoryResetTokenManager pResetTokenManager) {
    resetTokenManager = pResetTokenManager;
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

}