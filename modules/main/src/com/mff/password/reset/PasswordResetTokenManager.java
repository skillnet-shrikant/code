package com.mff.password.reset;

import atg.nucleus.GenericService;
import atg.security.PasswordHasher;

/**
 * Abstract base class for working with password reset tokens
 * 
 * @author grahammather
 *
 */
public abstract class PasswordResetTokenManager extends GenericService {
  private int tokenLifetimeInMinutes = 60;
  private PasswordHasher passwordHasher;
  
  //=======================
  
  /**
   * delete all password reset tokens older than tokenAgeInMinutes
   * 
   * @throws PasswordResetTokenException
   */
  public abstract void deleteExpiredTokens(int tokenAgeInMinutes) throws PasswordResetTokenException;
  
  /**
   * delete the entry for the given token from the DB
   * 
   * @param urlSafeUuid
   * @throws PasswordResetTokenException
   */
  public abstract void deleteToken(String urlSafeUuid) throws PasswordResetTokenException;
  
  /**
   * Find the token for the given string
   * 
   * @param urlSafeUuid
   * @return
   * @throws PasswordResetTokenException if the token has expired, if the token is not found, or if the email address doesn't match
   */
  public abstract ResetToken getToken(String urlSafeUuid) throws PasswordResetTokenException;
  
  /**
   * Generate a password reset token for this email, and store the token with 
   * the email and expiration time in the database.
   * 
   * @param email
   * @return a password reset token
   */
  public abstract String generateToken(String email) throws PasswordResetTokenException;
  
  //=======================
  
  public int getTokenLifetimeInMinutes() {
    return tokenLifetimeInMinutes;
  }

  public void setTokenLifetimeInMinutes(int tokenLifetimeInMinutes) {
    this.tokenLifetimeInMinutes = tokenLifetimeInMinutes;
  }

  public PasswordHasher getPasswordHasher() {
    return passwordHasher;
  }

  public void setPasswordHasher(PasswordHasher passwordHasher) {
    this.passwordHasher = passwordHasher;
  }
  
  public class ResetToken {
    private String email;
    private boolean expired;
    
    protected ResetToken(String email, boolean expired) {
      this.email = email;
      this.expired = expired;
    }
    
    public String getEmail() {
      return email;
    }
    
    public boolean isExpired() {
      return expired;
    }
  }
}
