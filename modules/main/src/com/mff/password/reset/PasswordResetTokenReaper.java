package com.mff.password.reset;

import mff.task.Task;

/**
 * 
 * @author grahammather
 *
 */
public class PasswordResetTokenReaper extends Task {

  private PasswordResetTokenManager passwordResetTokenManager;
  private int tokenAgeInMinutes;
  
  @Override
  public void doTask() {
    try {
      getPasswordResetTokenManager().deleteExpiredTokens(getTokenAgeInMinutes());
    } catch (PasswordResetTokenException e) {
      if (isLoggingError())
        logError("Error deleting the expired tokens", e);
    }
  }

  public int getTokenAgeInMinutes() {
    return tokenAgeInMinutes;
  }

  public void setTokenAgeInMinutes(int tokenAgeInMinutes) {
    this.tokenAgeInMinutes = tokenAgeInMinutes;
  }

  public PasswordResetTokenManager getPasswordResetTokenManager() {
    return passwordResetTokenManager;
  }

  public void setPasswordResetTokenManager(PasswordResetTokenManager passwordResetTokenManager) {
    this.passwordResetTokenManager = passwordResetTokenManager;
  }

}
