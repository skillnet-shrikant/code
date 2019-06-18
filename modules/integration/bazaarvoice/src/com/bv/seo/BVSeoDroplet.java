package com.bv.seo;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bazaarvoice.seo.sdk.model.ContentType;
import com.bazaarvoice.seo.sdk.model.SubjectType;
import com.bazaarvoice.seo.sdk.model.BVParameters;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bazaarvoice.seo.sdk.BVManagedUIContent;
import com.bazaarvoice.seo.sdk.BVUIContent;
import com.bazaarvoice.seo.sdk.config.BVClientConfig;
import com.bazaarvoice.seo.sdk.config.BVSdkConfiguration;
import com.bazaarvoice.seo.sdk.config.BVConfiguration;

import com.bv.configuration.BvConfiguration;

public class BVSeoDroplet extends DynamoServlet {
	
	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_PRODUCT_ID = ParameterName.getParameterName("productId");

	/* OUTPUT PARAMETERS */
	public static final String RATINGS= "ratings";
	public static final String REVIEWS= "reviews";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	
	private BvConfiguration mBvConfiguration;
	
	
	public BvConfiguration getBvConfiguration(){
		return mBvConfiguration;
	}
	
	public void setBvConfiguration(BvConfiguration pBvConfiguration){
		mBvConfiguration=pBvConfiguration;
	}
	
	@Override
	public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		throws ServletException, IOException {
		
		 BvConfiguration atgBvConfig=getBvConfiguration();
		 if(atgBvConfig==null){
			 if (isLoggingWarning()) {
					logWarning("ATG BV Configuration is not set");
				}
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
				return;
		 }
		 String cloudKey=atgBvConfig.getCloudKey();
		 
		 if (cloudKey == null || cloudKey.isEmpty()) {
				if (isLoggingWarning()) {
					logWarning("Cloud Key is empty");
				}
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
				return;
		}
		 
		 String bvRootFolder=atgBvConfig.getBvRootFolder();
		 
		 if (bvRootFolder == null || bvRootFolder.isEmpty()) {
				if (isLoggingWarning()) {
					logWarning("bvRootFolder is empty");
				}
				pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
				return;
		}
		 
		 boolean isStaging=atgBvConfig.isStaging();
		 BVConfiguration _bvConfig = new BVSdkConfiguration();
	    _bvConfig.addProperty(BVClientConfig.CLOUD_KEY,cloudKey);
		_bvConfig.addProperty(BVClientConfig.BV_ROOT_FOLDER,bvRootFolder); //adjust this for each locale
		if(isStaging) {
			_bvConfig.addProperty(BVClientConfig.STAGING, "true");
		}
		else {
			_bvConfig.addProperty(BVClientConfig.STAGING, "false");
		}
		
		String productId = (String) pRequest.getObjectParameter(PARAM_PRODUCT_ID);

		if (productId == null || productId.isEmpty()) {
			if (isLoggingWarning()) {
				logWarning("Missing required productId parameter in request: "+pRequest.getRequestURI());
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}
		
		//Prepare pageURL and SubjectID/ProductID values.		
		String pageURL = pRequest.getQueryString() == null ? pRequest.getRequestURI() : pRequest.getRequestURI() + "?" + pRequest.getQueryString();
		String subjectID = productId.trim();	
		
		//Set BV Parameters that are specific to the page and content type.
		BVParameters _bvParam = new BVParameters();
		_bvParam.setBaseURI(pRequest.getRequestURI()); // this value is used to build pagination links
		_bvParam.setPageURI(pageURL); //this value is used to extract the page number from bv URL parameters
		_bvParam.setContentType(ContentType.REVIEWS);
		_bvParam.setSubjectType(SubjectType.PRODUCT);
		_bvParam.setSubjectId(subjectID);
		BVUIContent _bvOutput = new BVManagedUIContent(_bvConfig);
		 
		//Get content and place into strings, which output into the injection divs.
		String sBvOutputSummary = _bvOutput.getAggregateRating(_bvParam);  //getAggregateRating delivers the AggregateRating section only
		String sBvOutputReviews = _bvOutput.getReviews(_bvParam);  //getReviews delivers the review content with pagination only%>
		
		if(sBvOutputSummary.isEmpty()){
			if (isLoggingWarning()) {
				logWarning("No aggregate ratings are found");
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}
		
		if(sBvOutputReviews.isEmpty()){
			if (isLoggingWarning()) {
				logWarning("No reviews are found");
			}
			pRequest.serviceLocalParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		}
		pRequest.setParameter(RATINGS,sBvOutputSummary);
		pRequest.setParameter(REVIEWS,sBvOutputReviews);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}

}
