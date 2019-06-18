package com.mff.password.reset;

import atg.nucleus.GenericService;
import com.mff.password.reset.PasswordResetTokenException;
import com.mff.password.reset.PasswordResetTokenManager;
import com.mff.password.reset.PasswordResetTokenManager.ResetToken;

public class TestPasswordResetTokenManager extends GenericService {
  private String email;
  private PasswordResetTokenManager tokenManager;
  private boolean deleteToken;
  private int tokenAgeInMinutes;

  public void test() {
    try {
      String token = tokenManager.generateToken(getEmail());
      logInfo("generated token for sending in emails: " + token);

      ResetToken resetToken = tokenManager.getToken(token);

      if (resetToken != null) {
        logInfo("found token");
        logInfo("token is expired = " + resetToken.isExpired());
      }

      if (isDeleteToken()) {
        tokenManager.deleteToken(token);
        logInfo("successfully deleted token");
      }

    } catch (Exception e) {
      vlogError("token manager test failed", e);
    }
  }

  public void testDeleteExpiredTokens() {
    try {
      tokenManager.deleteExpiredTokens(getTokenAgeInMinutes());
    } catch (PasswordResetTokenException e) {
      vlogError("token manager testDeleteExpiredTokens failed", e);
    }
  }

  public PasswordResetTokenManager getTokenManager() {
    return tokenManager;
  }

  public void setTokenManager(PasswordResetTokenManager tokenManager) {
    this.tokenManager = tokenManager;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isDeleteToken() {
    return deleteToken;
  }

  public void setDeleteToken(boolean deleteToken) {
    this.deleteToken = deleteToken;
  }

  public int getTokenAgeInMinutes() {
    return tokenAgeInMinutes;
  }

  public void setTokenAgeInMinutes(int tokenAgeInMinutes) {
    this.tokenAgeInMinutes = tokenAgeInMinutes;
  }
}
