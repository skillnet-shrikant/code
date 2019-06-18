package com.mff.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import atg.nucleus.GenericService;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
/**
 * An implementation of google's recaptcha service.
 */
public class RecaptchaProcessor extends GenericService {

  static final String PERF_MONITOR_OP_NAME = "ReCaptcha";
  static final String PERF_MONITOR_VERIFICATION_CALL = "Captcha Verification Call with the recaptcha servers";

  private String mSiteKey;
  private String mSecretKey;
  private String mEndPoint;

  public boolean doProcessCaptcha(DynamoHttpServletRequest pRequest, String remoteAddr, String captchaResponse) throws Exception{
    vlogDebug("Entering: doProcessCaptcha");
    
    CloseableHttpClient client = HttpClients.createDefault();
    CloseableHttpResponse response = null;
    
    
    try {
      PerformanceMonitor.startOperation(PERF_MONITOR_OP_NAME, PERF_MONITOR_VERIFICATION_CALL);
      HttpPost post = new HttpPost(getEndPoint());
      post.addHeader("User-Agent", pRequest.getHeader("User-Agent"));
      
      //create url param list
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("secret", getSecretKey()));
      params.add(new BasicNameValuePair("response", captchaResponse));
      params.add(new BasicNameValuePair("remoteip", remoteAddr));
     
      //set url params
      HttpEntity urlParams = new UrlEncodedFormEntity(params);
      post.setEntity(urlParams);
     
      //make request here
      response = client.execute(post);
      
      //if response code is not 200 throw an exception
      int responseCode = response.getStatusLine().getStatusCode();
      if(responseCode!=200){
        vlogError("Failed request with HTTP status: " + responseCode);
        throw new Exception("Failed request with HTTP status: " + responseCode);
      }
      
      //read the response from instream
      BufferedReader lReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      String lLine;
      StringBuffer responseContent = new StringBuffer();
      
      while ((lLine = lReader.readLine()) != null) {
        responseContent.append(lLine);
      }
      lReader.close();
      
      //parse the json response
      Gson gson = new Gson();
      RecaptchaResponse recaptchaResponse = gson.fromJson(responseContent.toString(), RecaptchaResponse.class);
      vlogDebug("RecaptchaResponse:{0} for remotAddr:{1}",recaptchaResponse.isSuccess(),remoteAddr);
      //verify if there are any errors in the response, if yes thrown an exception
      if (recaptchaResponse.getErrorCodes() != null && recaptchaResponse.getErrorCodes().length > 0) {
        vlogError("Failed request with error codes: " + Arrays.toString(recaptchaResponse.getErrorCodes()));
        throw new Exception("Failed request with error codes: " + Arrays.toString(recaptchaResponse.getErrorCodes()));
      }
      
      client.close();
      response.close();
      PerformanceMonitor.endOperation(PERF_MONITOR_OP_NAME, PERF_MONITOR_VERIFICATION_CALL);
      vlogDebug("Exiting: doProcessCaptcha");
      //return the response
      return recaptchaResponse.isSuccess();
      
    } catch (Exception e) {
      PerformanceMonitor.cancelOperation(PERF_MONITOR_OP_NAME, PERF_MONITOR_VERIFICATION_CALL);
      vlogError(e,"Captcha Exception");
      client.close();
      response.close();
      throw new Exception("Captcha Exception", e);
    }finally{
      client.close();
      response.close();
    }
  }
  
  public String getEndPoint() {
    return mEndPoint;
  }

  public void setEndPoint(String pEndPoint) {
    mEndPoint = pEndPoint;
  }

  public String getSiteKey() {
    return mSiteKey;
  }

  public void setSiteKey(String pSiteKey) {
    mSiteKey = pSiteKey;
  }

  public String getSecretKey() {
    return mSecretKey;
  }

  public void setSecretKey(String pSecretKey) {
    mSecretKey = pSecretKey;
  }
}