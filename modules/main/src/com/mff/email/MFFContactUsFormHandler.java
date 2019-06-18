package com.mff.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.GenericFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.mff.constants.MFFConstants;

public class MFFContactUsFormHandler extends GenericFormHandler {

  private MFFEmailManager mEmailManager;
  private Map<String, Object> mEditValue = new HashMap<String, Object>();
  private String mContactUsEmailSuccessURL;
  private String mContactUsEmailErrorURL;
  private final String AREA_OF_INTEREST = "areaOfInterest";
  private final String TOPIC = "topic";
  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }
  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }
  public Map<String, Object> getEditValue() {
    return mEditValue;
  }
  public void setEditValue(Map<String, Object> pEditValue) {
    mEditValue = pEditValue;
  }
  
  public String getContactUsEmailErrorURL() {
    return mContactUsEmailErrorURL;
  }

  public void setContactUsEmailErrorURL(String pContactUsEmailErrorURL) {
    mContactUsEmailErrorURL = pContactUsEmailErrorURL;
  }
  
  /* For sending contact us email
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleContactUsEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)  throws ServletException, IOException{
	  vlogDebug("handleContactUsEmail(): Start.");
	  String areaOfInterest=(String)getEditValue().get(AREA_OF_INTEREST);
	  String topic=(String)getEditValue().get(TOPIC);
	  vlogDebug("User Selected Area of Interest as {0} and Topic as {1}",areaOfInterest,topic);
	  String emailTo= "";
    
	  if(StringUtils.isNotEmpty(areaOfInterest) && StringUtils.isNotEmpty(topic)){
		  emailTo=getEmailManager().getContactUsEmailMap().get(areaOfInterest+"-"+topic);
	  } else {
		  emailTo=getEmailManager().getDefaultContactUsEmailTo();
	  }
	  vlogDebug("handleContactUsEmail(): Email address:" + emailTo);
    
	  String fromEmail= (String)getEditValue().get(MFFConstants.FLD_EMAIL);
	  String subject= (String)getEditValue().get(MFFConstants.SUBJECT);
	    
	  Map<String, Object> templateParameters = new HashMap<String, Object>();
    
	  templateParameters.put(MFFConstants.FIRST_NAME, (String)getEditValue().get(MFFConstants.FIRST_NAME));
	  templateParameters.put(MFFConstants.LAST_NAME, (String)getEditValue().get(MFFConstants.LAST_NAME));
	  templateParameters.put(MFFConstants.ADDRESS, (String)getEditValue().get(MFFConstants.ADDRESS));
	  templateParameters.put(MFFConstants.ADDRESS_ADDRESS2, (String)getEditValue().get(MFFConstants.ADDRESS_ADDRESS2));
	  templateParameters.put(MFFConstants.ADDRESS_CITY, (String)getEditValue().get(MFFConstants.ADDRESS_CITY));
	  templateParameters.put(MFFConstants.ADDRESS_STATE, (String)getEditValue().get(MFFConstants.ADDRESS_STATE));
	  templateParameters.put(MFFConstants.ADDRESS_POSTAL_CODE, (String)getEditValue().get(MFFConstants.ADDRESS_POSTAL_CODE));
	  templateParameters.put(MFFConstants.ADDRESS_PHONE_NUMBER, (String)getEditValue().get(MFFConstants.ADDRESS_PHONE_NUMBER));
		
	  templateParameters.put(MFFConstants.PARAM_EMAIL, (String)getEditValue().get(MFFConstants.FLD_EMAIL));
	  templateParameters.put(MFFConstants.STORE_NAME, (String)getEditValue().get(MFFConstants.STORE_NAME));
	  templateParameters.put(MFFConstants.COMMENTS, (String)getEditValue().get(MFFConstants.COMMENTS));
    
	  getEmailManager().sendContactUsEmail(emailTo, fromEmail, templateParameters, subject);
	  vlogDebug("handleContactUsEmail(): End.");
	  return checkFormRedirect(getContactUsEmailSuccessURL(), getContactUsEmailErrorURL(), pRequest, pResponse);
  }
  
  public String getContactUsEmailSuccessURL() {
    return mContactUsEmailSuccessURL;
  }
  public void setContactUsEmailSuccessURL(String pContactUsEmailSuccessURL) {
    mContactUsEmailSuccessURL = pContactUsEmailSuccessURL;
  }
}
