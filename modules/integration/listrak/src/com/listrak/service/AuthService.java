package com.listrak.service;


import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.listrak.service.client.AuthResponse;

import atg.nucleus.ServiceException;


public class AuthService extends ListrakService {

		
		
		private String mGrantTypeParameter;
		private String mRecentToken;
		private String mLastToken;
		private long mTimeInSecondsLastTokenUpdated;
		
		private Integer mExpiryTimeInSeconds;
		private String mAuthType;
		
		

		public String getGrantTypeParameter() {
			return mGrantTypeParameter;
		}

		public void setGrantTypeParameter(String pGrantTypeParameter) {
			mGrantTypeParameter = pGrantTypeParameter;
		}

		public String getRecentToken() {
			return mRecentToken;
		}

		public void setRecentToken(String pRecentToken) {
			mRecentToken = pRecentToken;
		}

		public String getLastToken() {
			return mLastToken;
		}

		public void setLastToken(String pLastToken) {
			mLastToken = pLastToken;
		}

		public long getTimeInSecondsLastTokenUpdated() {
			return mTimeInSecondsLastTokenUpdated;
		}

		public void setTimeInSecondsLastTokenUpdated(long pTimeInSecondsLastTokenUpdated) {
			mTimeInSecondsLastTokenUpdated = pTimeInSecondsLastTokenUpdated;
		}

		public String getAuthType() {
			return mAuthType;
		}

		public void setAuthType(String pAuthType) {
			mAuthType = pAuthType;
		}

		public Integer getExpiryTimeInSeconds() {
			return mExpiryTimeInSeconds;
		}

		public void setExpiryTimeInSeconds(Integer pExpiryTimeInSeconds) {
			mExpiryTimeInSeconds = pExpiryTimeInSeconds;
		}
		
		@Override 
		public void doStartService() throws ServiceException
		 {
			performAuth();
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
				AuthResponse response=mapper.readValue(json, AuthResponse.class);
				System.out.println(json);
				httpClient.close();
				
				if(getSuccessCodes().contains(statusCode)){
					if(response.getError()==null || response.getError().isEmpty()){
						if(response.getAccessToken()!=null){
							if(getLastToken()==null){
								setLastToken(response.getAccessToken());
								setRecentToken(response.getAccessToken());
							}
							else {
								setLastToken(getRecentToken());
								setRecentToken(response.getAccessToken());
							}
							
							if(response.getExpiresIn()!=null){
								setExpiryTimeInSeconds(response.getExpiresIn());
							}
							else {
								setExpiryTimeInSeconds(3599);
							}
							if(response.getTokenType()!=null||!response.getTokenType().isEmpty()){
								setAuthType(response.getTokenType());
							}
							else {
								setAuthType("bearer");
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
				if(getGrantTypeParameter()==null || getGrantTypeParameter().isEmpty()){
					return null;
				}
				else if(getListrakConfiguration().getClientId()==null || getListrakConfiguration().getClientId().isEmpty()){
					return null;
				}
				else if(getListrakConfiguration().getClientSecret()==null || getListrakConfiguration().getClientSecret().isEmpty()){
					return null;
				}
				BasicNameValuePair[] inputParams;
				BasicNameValuePair grant_type=new BasicNameValuePair("grant_type",getGrantTypeParameter().trim());
				BasicNameValuePair client_id=new BasicNameValuePair("client_id",getListrakConfiguration().getClientId().trim());
				BasicNameValuePair client_secret=new BasicNameValuePair("client_secret",getListrakConfiguration().getClientSecret().trim());
				inputParams=new BasicNameValuePair[] {grant_type,client_id,client_secret};
				long currentTimeMilliSeconds=System.currentTimeMillis();
				mTimeInSecondsLastTokenUpdated=currentTimeMilliSeconds;
				response=processFormHttpRequest(inputParams,httpClient);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
				return response;
		}
		
		public void performAuth(){
			CloseableHttpClient httpClient=HttpClientBuilder.create().build();
			String input="";
			
			processResponse(processRequest(httpClient,input,null,input),httpClient);
		}


	
}
