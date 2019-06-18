package com.mff.password.reset;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class RepositoryResetTokenManager extends PasswordResetTokenManager {

  private static final String PROP_EMAIL = "email";
  private static final String PROP_CREATION_TIME = "creationTime";
  
  private MutableRepository repository;
  private String itemDescriptor;
  
  @Override
  public void deleteExpiredTokens(int tokenAgeInMinutes) throws PasswordResetTokenException {

    // the oldest time for which a token can be unexpired is now minus token lifetime
    // tokens older than that should be deleted
    Calendar oldestTokenTime = Calendar.getInstance();
    oldestTokenTime.add(Calendar.MINUTE, tokenAgeInMinutes * -1);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
    String rqlStatement = PROP_CREATION_TIME + "< datetime(\"" + format.format(oldestTokenTime.getTime()) + "\")";
    try {
      RepositoryView tokenView = getRepository().getView(getItemDescriptor());
      RqlStatement stmt = RqlStatement.parseRqlStatement(rqlStatement);
      Object params[] = new Object[] {format.format(oldestTokenTime.getTime())};
      RepositoryItem[] items = stmt.executeQuery(tokenView, params);
      for (RepositoryItem item : items) {
        getRepository().removeItem(item.getRepositoryId(), getItemDescriptor());
      }
      
    } catch (RepositoryException ex) {
      String errorMessage = "Unable to delete expired reset tokens";
      vlogError(ex, errorMessage);
      throw new PasswordResetTokenException(errorMessage, ex);
    }
  }
  
  @Override
  public void deleteToken(String urlSafeUuid) throws PasswordResetTokenException {
    Base64 decoder = new Base64();
    try {
    	byte[] decodedBytes = (byte[]) decoder.decode(urlSafeUuid);
        String uuid = new String(decodedBytes);
        String hashedUuid = getPasswordHasher().encryptPassword(uuid);
        repository.removeItem(hashedUuid, getItemDescriptor());
    } catch (RepositoryException ex) {
      String errorMessage = "Unable to delete reset token: " + urlSafeUuid;
      vlogError(ex, errorMessage);
      throw new PasswordResetTokenException(errorMessage, ex);
    } catch (DecoderException e) {
    	String errorMessage = "Decode failed";
    	vlogError(e, errorMessage);
	}
  }

  @Override
  public ResetToken getToken(String urlSafeUuid) throws PasswordResetTokenException {
    if (urlSafeUuid == null)
      return null;
    
    Base64 decoder = new Base64();
    String hashedUuid = null;
    
    RepositoryItem tokenItem = null;
    try {
    	byte[] decodedBytes = (byte[]) decoder.decode(urlSafeUuid.getBytes());
        String uuid = new String(decodedBytes);
        hashedUuid = getPasswordHasher().encryptPassword(uuid);
        tokenItem = getRepository().getItem(hashedUuid, getItemDescriptor());
    } catch (RepositoryException e) {
      String cause = "failed to get item for token hash: " + hashedUuid;
      vlogError(e, cause);
      throw new PasswordResetTokenException(cause, e);
    }
    
    if (tokenItem == null)
      return null;
    
    Calendar cal = Calendar.getInstance();
    Date creationTime = (Date) tokenItem.getPropertyValue(PROP_CREATION_TIME);
    if (creationTime == null)
      throw new PasswordResetTokenException("No creation time found for token hash: " + hashedUuid);

    String email = (String) tokenItem.getPropertyValue(PROP_EMAIL);
    if (email == null)
      throw new PasswordResetTokenException("No email found for token hash: " + hashedUuid);
    
    cal.setTime(creationTime);
    cal.add(Calendar.MINUTE, getTokenLifetimeInMinutes());
    Date expirationTime = cal.getTime();
    Date now = new Date();
    
    return new ResetToken(email, now.compareTo(expirationTime) > 0);
  }

  @Override
  public String generateToken(String email) throws PasswordResetTokenException {
    String uuid = UUID.randomUUID().toString();
    String hashedUuid = getPasswordHasher().encryptPassword(uuid);
    
    try {
      MutableRepositoryItem token = getRepository().createItem(hashedUuid, getItemDescriptor());
      token.setPropertyValue(PROP_EMAIL, email);
      token.setPropertyValue(PROP_CREATION_TIME, new Date());
      getRepository().addItem(token);
      
    } catch (RepositoryException e) {
      String cause = "failed to create password token for email " + email;
      vlogError(e, cause);
      throw new PasswordResetTokenException(cause, e);
    }
    
    return  new String(new Base64().encode(uuid.getBytes()));
  }

  public MutableRepository getRepository() {
    return repository;
  }

  public void setRepository(MutableRepository repository) {
    this.repository = repository;
  }

  public String getItemDescriptor() {
    return itemDescriptor;
  }

  public void setItemDescriptor(String itemDescriptor) {
    this.itemDescriptor = itemDescriptor;
  }

}

