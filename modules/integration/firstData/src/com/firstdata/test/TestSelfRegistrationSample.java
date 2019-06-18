package com.firstdata.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.datawire.vxn3.SelfRegistration;
import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import net.datawire.vxn3.VXNException;
import atg.nucleus.GenericService;

public class TestSelfRegistrationSample extends GenericService {
  
 // private String mid = "99910719999";
  private String mid = "99910719996";//Sept 23
  
  private String tid = "00000004000";//Sept 23
  
  private String svcid = "104";
  
  private String applicationID = "FLEETWHLVLKJAVA2";
  
  //private String did="00013320434959074124";
  private String did="00013845193648187019";
  
  private String sampleData = "SV.999107199991C4024001C121300001C1390720161C429991071999940001C7077770859349358401CEA311C151000000000000000";
  
  private String formatNumber = "0";
  
  private String transactionCode = "2400";
                                          
  private String merchantAndTerminalID = "999107199964000";
  
  private String giftCardNumber = "7777085934935840";
  
  private String eaVal = "31";
  
  private String transRefNumber = "1000000000000000";
  
  private String versionNumber = "4";
  
  private String datawireUrl1 = "https://staging1.datawire.net/sd";
  
  private String datawireUrl2 = "https://staging2.datawire.net/sd";
  
  
  //00013845193648187019
  //00013845193648187019

  public void testSelfRegistration() {
    
    List<String> sdUrls = new ArrayList<String>();
    sdUrls.add(datawireUrl1);
    sdUrls.add(datawireUrl2);
    
    List sdUrlList = null;
    
    try {
      VXN.setSSLProtocols("TLSv1,TLSv1.1,TLSv1.2", false);
      SelfRegistration srs = new SelfRegistration(sdUrls, mid, tid, svcid, "test java api");
      srs.setMaxRegisterAttempts(3);
      srs.setRegisterAttemptsWaitMilliseconds(30000);
      srs.setApplication(applicationID);
      did = srs.registerMerchant();
      
      srs.activateMerchant();
      sdUrlList = srs.getUrlList();
      vlogInfo("DID = {0}", did);
      if (sdUrlList != null) {
        for (Object o : sdUrlList) {
          vlogInfo ("sd url list {0}", o);
        }
      }
    } catch (VXNException e) {
      vlogError(e, "this is a test");
    }
  }
  
  public void testGCBalance() throws VXNException {
    List<String> sdUrls = new ArrayList<String>();
    sdUrls.add("https://staging1.datawire.net/sd");
    sdUrls.add("https://staging2.datawire.net/sd");    
    
    VXN vxn = VXN.getInstance(sdUrls, did, mid, tid, svcid, applicationID);
    SimpleTransaction tr = vxn.newSimpleTransaction("clientRefId");
    setTransactionCode("2400");
    char payload[] = generatePayloadForGC();
    vlogInfo("Request Payload {0}", new String(payload));
    tr.setPayload(payload);
    tr.executeXmlRequest();
    char respPayLoad[];
    respPayLoad = tr.getPayload();
    vlogInfo("respPayLoad --> {0}", respPayLoad);
    vlogInfo("respPayLoad Length--> {0}", respPayLoad.length);
    String resPay=new String(respPayLoad);
    vlogInfo ("Response = {0}", resPay);
    char [] FS = {28};
    vlogInfo("Rersponse delimeter {0}", resPay.contains(new String(FS)));
  }
  
  public void testGCBalanceWithMultiLock() throws VXNException {
    List<String> sdUrls = new ArrayList<String>();
    sdUrls.add("https://staging1.datawire.net/sd");
    sdUrls.add("https://staging2.datawire.net/sd");    
    
    VXN vxn = VXN.getInstance(sdUrls, did, mid, tid, svcid, applicationID);
    SimpleTransaction tr = vxn.newSimpleTransaction("clientRefId");
    setTransactionCode("2408");
    char payload[] = generatePayloadForGC();
    char [] FS = {28};
    payload=concatCharArray(payload, FS);
    payload=concatCharArray(payload, "04".toCharArray());
    payload=concatCharArray(payload, "1000".toCharArray());
    tr.setPayload(payload);
    tr.executeXmlRequest();
    char respPayLoad[];
    respPayLoad = tr.getPayload();
    vlogInfo ("Response = {0}", new String(respPayLoad));
  }
  
  public char[] generatePayloadForGC () {
    Date now = new Date();
    SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
    SimpleDateFormat d = new SimpleDateFormat("MMddyyyy");
    char[] svcChars = {'S', 'V', 46};
    char[] merchID = mid.toCharArray();
    char [] FS = {28};
    char [] versionNumberArray = versionNumber.toCharArray();
    char [] formatNumberArray = formatNumber.toCharArray();
    char [] transactionRequestArray = transactionCode.toCharArray();
    char [] transactionTimeArray = sf.format(now).toCharArray();
    char [] transactionDateArray = d.format(now).toCharArray();
    char [] merchantAndTerminalIDArray = merchantAndTerminalID.toCharArray();
    char [] giftCardNumberArray = giftCardNumber.toCharArray();
    char [] eaValArray = eaVal.toCharArray();
    char [] transRefNumberArray = transRefNumber.toCharArray();
    
    
    
    char [] payload = svcChars;
    
    payload = concatCharArray(payload, merchID);
    
    
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, versionNumberArray);
    payload = concatCharArray(payload, formatNumberArray);
    payload = concatCharArray(payload, transactionRequestArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "12".toCharArray());
    payload = concatCharArray(payload, transactionTimeArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "13".toCharArray());
    payload = concatCharArray(payload, transactionDateArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "42".toCharArray());
    payload = concatCharArray(payload, merchantAndTerminalIDArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "70".toCharArray());
    payload = concatCharArray(payload, giftCardNumberArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "EA".toCharArray());
    payload = concatCharArray(payload, eaValArray);
    payload = concatCharArray(payload, FS);
    payload = concatCharArray(payload, "15".toCharArray());
    payload = concatCharArray(payload, transRefNumberArray);
    return payload;
  }  
  public char[] concatCharArray(char[] c1, char[] c2) {
    char[] concat = new char[c1.length + c2.length];
    System.arraycopy(c1, 0, concat, 0, c1.length);
    System.arraycopy(c2, 0, concat, c1.length, c2.length);
    
    return concat;
  }  
  
  
  public String getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(String versionNumber) {
    this.versionNumber = versionNumber;
  }

  public String getFormatNumber() {
    return formatNumber;
  }

  public void setFormatNumber(String formatNumber) {
    this.formatNumber = formatNumber;
  }

  public String getTransactionCode() {
    return transactionCode;
  }

  public void setTransactionCode(String transactionCode) {
    this.transactionCode = transactionCode;
  }

  public String getMerchantAndTerminalID() {
    return merchantAndTerminalID;
  }

  public void setMerchantAndTerminalID(String merchantAndTerminalID) {
    this.merchantAndTerminalID = merchantAndTerminalID;
  }

  public String getGiftCardNumber() {
    return giftCardNumber;
  }

  public void setGiftCardNumber(String giftCardNumber) {
    this.giftCardNumber = giftCardNumber;
  }

  public String getEaVal() {
    return eaVal;
  }

  public void setEaVal(String eaVal) {
    this.eaVal = eaVal;
  }

  public String getTransRefNumber() {
    return transRefNumber;
  }

  public void setTransRefNumber(String transRefNumber) {
    this.transRefNumber = transRefNumber;
  }

  public String getSampleData() {
    return sampleData;
  }

  public void setSampleData(String sampleData) {
    this.sampleData = sampleData;
  }

  public String getDid() {
    return did;
  }

  public void setDid(String did) {
    this.did = did;
  }

  public String getApplicationID() {
    return applicationID;
  }

  public void setApplicationID(String applicationID) {
    this.applicationID = applicationID;
  }

  public String getSvcid() {
    return svcid;
  }

  public void setSvcid(String svcid) {
    this.svcid = svcid;
  }

  public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }

  public String getMid() {
    return mid;
  }

  public void setMid(String mid) {
    this.mid = mid;
  }

  public String getDatawireUrl1() {
    return datawireUrl1;
  }

  public void setDatawireUrl1(String pDatawireUrl1) {
    datawireUrl1 = pDatawireUrl1;
  }

  public String getDatawireUrl2() {
    return datawireUrl2;
  }

  public void setDatawireUrl2(String pDatawireUrl2) {
    datawireUrl2 = pDatawireUrl2;
  }
  
}
