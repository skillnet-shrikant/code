package com.mff.util;

import java.io.Serializable;
import java.util.Date;
/**
 * POJO to hold the response of recaptcha
 * @author DMI
 *
 */
public class RecaptchaResponse implements Serializable{
  private static final long serialVersionUID = 2294883044897797805L;
  private boolean success;
	private Date challengeTimestamp;
	private String hostname;
  private String[] errorCodes;
  public boolean isSuccess() {
    return success;
  }
  public void setSuccess(boolean pSuccess) {
    success = pSuccess;
  }
  public Date getChallengeTimestamp() {
    return challengeTimestamp;
  }
  public void setChallengeTimestamp(Date pChallengeTimestamp) {
    challengeTimestamp = pChallengeTimestamp;
  }
  public String getHostname() {
    return hostname;
  }
  public void setHostname(String pHostname) {
    hostname = pHostname;
  }
  public String[] getErrorCodes() {
    return errorCodes;
  }
  public void setErrorCodes(String[] pErrorCodes) {
    errorCodes = pErrorCodes;
  }
}
