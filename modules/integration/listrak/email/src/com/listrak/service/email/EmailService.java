package com.listrak.service.email;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listrak.service.AuthService;
import com.listrak.service.ListrakService;
import com.listrak.service.constants.ListrakConstants;
import com.listrak.service.email.client.SendMessageResponse;




public class EmailService extends ListrakService {

	
	private Map<String,String> mFiledIdToNameMap;
	private List<String> mMessageStatusCode;
	private AuthService mAuthService;
	private Map<String,String> mEmailTypeToCode;
	
	
	
	public Map<String, String> getEmailTypeToCode() {
		return mEmailTypeToCode;
	}

	public void setEmailTypeToCode(Map<String, String> pEmailTypeToCode) {
		mEmailTypeToCode = pEmailTypeToCode;
	}

	public AuthService getAuthService() {
		return mAuthService;
	}

	public void setAuthService(AuthService pAuthService) {
		mAuthService = pAuthService;
	}

	public Map<String, String> getFiledIdToNameMap() {
		return mFiledIdToNameMap;
	}

	public void setFiledIdToNameMap(Map<String, String> pFiledIdToNameMap) {
		mFiledIdToNameMap = pFiledIdToNameMap;
	}

	public List<String> getMessageStatusCode() {
		return mMessageStatusCode;
	}

	public void setMessageStatusCode(List<String> pMessageStatusCode) {
		mMessageStatusCode = pMessageStatusCode;
	}

	@Override
	public void processResponse(HttpResponse pResponse,CloseableHttpClient httpClient) {
		// TODO Auto-generated method stub
		try {
			String statusCode=""+pResponse.getStatusLine().getStatusCode();
			vlogInfo("Status Code: "+statusCode);
			Header[] headers=pResponse.getAllHeaders();
			for(int i=0;i<headers.length;i++){
				Header header=headers[i];
				vlogInfo(header.getName());
				vlogInfo(header.getValue());
			}
			HttpEntity entity=pResponse.getEntity();
			String json = EntityUtils.toString(entity, "UTF-8");
			ObjectMapper mapper = new ObjectMapper();
			SendMessageResponse response=mapper.readValue(json, SendMessageResponse.class);
			System.out.println(json);
			httpClient.close();
			if(getSuccessCodes().contains(statusCode)){
				if(response.getError()==null || response.getError().isEmpty()){
					if(response.getStatus()!=null){
						if(getMessageStatusCode().contains((response.getStatus().doubleValue()+"").trim())){
							return;
						}
						else {
							throw new Exception("Failed sending mesasage");
						}
					}
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	
	@Override
	public HttpResponse processRequest(CloseableHttpClient httpClient,String input,Map<String,String> headers,String urlExtension){
		
		HttpResponse response=null;
		try{
			if(input==null || input.isEmpty()){
				return null;
			}
			else {
				
				response=processJsonHttpRequest(input,httpClient,headers,urlExtension);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
			return response;
	}

	public void sendEmail(String json,Map<String,String> headers,String urlExtension){
		/**
		long timeOutValueInMills=0;
		if(getAuthService().getExpiryTimeInSeconds()!=null)
		{
			timeOutValueInMills=getAuthService().getExpiryTimeInSeconds().longValue();
		}
		long lastTokenUpdatedTime=getAuthService().getTimeInSecondsLastTokenUpdated();
		long currentTimeMills=System.currentTimeMillis();
		long timeDelta=currentTimeMills-lastTokenUpdatedTime;
		if(lastTokenUpdatedTime==0){
			
		}
		else if(timeDelta<=timeOutValueInMills){
			getAuthService().performAuth();
		}
		**/
		getAuthService().performAuth();
		String recentToken=getAuthService().getRecentToken();
		if(recentToken==null||recentToken.isEmpty()){
			getAuthService().performAuth();
			recentToken=getAuthService().getRecentToken();
		}
		String headerName=ListrakConstants.AUTHORIZATION;
		String headerValue="Bearer "+recentToken.trim();
		headers.put(headerName, headerValue);
		CloseableHttpClient httpClient=HttpClientBuilder.create().build();
		processResponse(processRequest(httpClient,json,headers,urlExtension),httpClient);
		
	}
	
	
	
	
}
