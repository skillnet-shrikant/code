package com.listrak.service;



import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.listrak.configuration.ListrakConfiguration;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.nucleus.GenericService;

public abstract class ListrakService extends GenericService {
	
	private String mEndPoint;
	private String mHttpMethodType;
	private String mHttpContentType;
	private boolean mEndPointOnSSL;
	private ListrakConfiguration mListrakConfiguration;
	private List<String> mSuccessCodes;
	private boolean mTurnOnTimeOut;
	private int mSocketTimeout;
	private int mConnectionTimeout;
	private int mConnectionRequestTimeout;
	
	
	public boolean isTurnOnTimeOut() {
		return mTurnOnTimeOut;
	}

	public void setTurnOnTimeOut(boolean pTurnOnTimeOut) {
		mTurnOnTimeOut = pTurnOnTimeOut;
	}

	public int getSocketTimeout() {
		return mSocketTimeout;
	}

	public void setSocketTimeout(int pSocketTimeout) {
		mSocketTimeout = pSocketTimeout;
	}

	public int getConnectionTimeout() {
		return mConnectionTimeout;
	}

	public void setConnectionTimeout(int pConnectionTimeout) {
		mConnectionTimeout = pConnectionTimeout;
	}

	public int getConnectionRequestTimeout() {
		return mConnectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int pConnectionRequestTimeout) {
		mConnectionRequestTimeout = pConnectionRequestTimeout;
	}

	public String getEndPoint() {
		return mEndPoint;
	}

	public void setEndPoint(String pEndPoint) {
		mEndPoint = pEndPoint;
	}
	
	public String getHttpMethodType() {
		return mHttpMethodType;
	}

	public void setHttpMethodType(String pHttpMethodType) {
		mHttpMethodType = pHttpMethodType;
	}

	public String getHttpContentType() {
		return mHttpContentType;
	}

	public void setHttpContentType(String pHttpContentType) {
		mHttpContentType = pHttpContentType;
	}
	
	public boolean isEndPointOnSSL() {
		return mEndPointOnSSL;
	}

	public void setEndPointOnSSL(boolean pIsEndPointOnSSL) {
		mEndPointOnSSL = pIsEndPointOnSSL;
	}
	
	public ListrakConfiguration getListrakConfiguration() {
		return mListrakConfiguration;
	}

	public void setListrakConfiguration(ListrakConfiguration pListrakConfiguration) {
		mListrakConfiguration = pListrakConfiguration;
	}


	public List<String> getSuccessCodes() {
		return mSuccessCodes;
	}

	public void setSuccessCodes(List<String> pSuccessCodes) {
		mSuccessCodes = pSuccessCodes;
	}

	public HttpResponse processJsonHttpRequest(String jsonInput,CloseableHttpClient httpClient,Map<String,String> additionalHeaders, String urlExtension){
		HttpResponse response=null;
		try {
			
			
			
			if(getEndPoint()==null || getEndPoint().isEmpty()){
				
			}
			else if(getHttpMethodType()==null || getHttpMethodType().isEmpty()){
				
			}
			else if(getHttpMethodType()==null || getHttpMethodType().isEmpty()){
				
			}
			else if(jsonInput==null || jsonInput.isEmpty()){
				
			}
			else {
				String finalEndPoint=getEndPoint();
				if(urlExtension!=null&&!urlExtension.isEmpty()){
					finalEndPoint=finalEndPoint+urlExtension;
				}
				HttpPost post = new HttpPost(finalEndPoint);
				post.setHeader("Content-Type",getHttpContentType());
				if(additionalHeaders.size()!=0){
					vlogDebug("Additional headers: ");
					for(String key: additionalHeaders.keySet()){
						String value=additionalHeaders.get(key);
						if(value!=null && !value.isEmpty()){
							post.setHeader(key,value);
						}
						
					}
				}
				StringEntity params = new StringEntity(jsonInput);
				post.setEntity(params);
				
				if(isTurnOnTimeOut()){
					int connectionTimeOut=getConnectionTimeout();
					int connectionRequestTimeout=getConnectionRequestTimeout();
					int socketTimeOut=getSocketTimeout();
					RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeOut).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeOut).build();
					post.setConfig(config);
				}
				response = httpClient.execute(post);

			}	
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}
	
	public HttpResponse processFormHttpRequest(BasicNameValuePair[] inputParams,CloseableHttpClient httpClient){
		HttpResponse response=null;
		try {
			
			if(getEndPoint()==null || getEndPoint().isEmpty()){
				
			}
			else if(getHttpMethodType()==null || getHttpMethodType().isEmpty()){
				
			}
			else if(getHttpMethodType()==null || getHttpMethodType().isEmpty()){
				
			}
			else if(inputParams==null || inputParams.length==0){
				
			}
			else {
				HttpPost post = new HttpPost(getEndPoint());
				post.setHeader("Content-Type",getHttpContentType());
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				for(int i=0;i<inputParams.length;i++){
					urlParameters.add(inputParams[i]);
				}
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				response = httpClient.execute(post);
			}	
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}
	
	public abstract void processResponse(HttpResponse response,CloseableHttpClient httpClient);
	public abstract HttpResponse processRequest(CloseableHttpClient httpClient,String input,Map<String,String> headers,String urlExtension);
	

}
