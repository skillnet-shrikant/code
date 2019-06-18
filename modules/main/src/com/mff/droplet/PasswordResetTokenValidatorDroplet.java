package com.mff.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.ProfileTools;
import com.mff.password.reset.PasswordResetTokenException;
import com.mff.password.reset.PasswordResetTokenManager;
import com.mff.password.reset.PasswordResetTokenManager.ResetToken;

public class PasswordResetTokenValidatorDroplet extends DynamoServlet {
  private static final ParameterName TRUE = ParameterName.getParameterName("true");
  private static final ParameterName FALSE = ParameterName.getParameterName("false");
  
  private PasswordResetTokenManager passwordResetTokenManager;
  private ProfileTools profileTools;
  private String validatedToken;
  private String validatedEmail;

  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    String urlSafeUuid = pRequest.getParameter("urlSafeUuid");
    ResetToken token = null;
    
    try {
      token = getPasswordResetTokenManager().getToken(urlSafeUuid);
    } catch (PasswordResetTokenException e) {
      vlogError(e, "Failed to get password reset token for token string: " + urlSafeUuid);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
    
    if (token != null && !token.isExpired()) {
      validatedToken = urlSafeUuid;
      validatedEmail = token.getEmail();
      pRequest.setParameter("email", validatedEmail);
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }

  
  public PasswordResetTokenManager getPasswordResetTokenManager() {
    return passwordResetTokenManager;
  }

  public void setPasswordResetTokenManager(PasswordResetTokenManager passwordResetTokenManager) {
    this.passwordResetTokenManager = passwordResetTokenManager;
  }

  public ProfileTools getProfileTools() {
    return profileTools;
  }
  
  public void setProfileTools(ProfileTools profileTools) {
    this.profileTools = profileTools;
  }

  public String getValidatedToken() {
    return validatedToken;
  }

  public String getValidatedEmail() {
    return validatedEmail;
  }
}
