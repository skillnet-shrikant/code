package com.servlet;

import java.net.URI;
import java.net.URISyntaxException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This class overrides the sendLocalRedirect behavior of
 * DynamoHTTPServletRequest in order to enable to send local redirects based on the
 * X-Forwarded-Proto header in the request instead of the protocol that was
 * explicitly set.
 * 
 * To summarize the problem:
 * 
 * When you use the OOTB ATG form handler logic to submit a form, the form
 * handler will redirect to a configurable success or error URL. This URL is an
 * absolute URL that's built from the request that ATG sees, which means that if
 * ATG sees an http request, it will redirect you to http. This is a problem
 * when using an F5 load balancer for SSL offloading (see the first link for why
 * we want to do this), because the appliance only talks HTTP to ATG. Whenever
 * we submit a secure form from an HTTPS page, ATG will redirect to the insecure
 * version of the success/error URL, and the browser will refuse to do this for
 * security reasons. You'll see a "mixed mode" error message of some kind.
 * 
 * @author grahammather
 * 
 */
public class XForwardedProtoAwareDynamoHTTPServletResponse extends DynamoHttpServletResponse {

  public static final String HTTPS = "https";
  public static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
  DynamoHttpServletRequest mRequest;
  
  /**
   * If the request contains the X-Forwarded-Proto header, have its value override the protocol of the request when sending 
   * the local redirect
   */
  
  	@Override
  	public void setStatus(int pCode) {
  		//System.out.println("************** In setStatus :******************");
  		if(this.mRequest!=null){
  			String url=this.mRequest.getRequestURI();
  			//System.out.println("************** Request url in setStatus: "+url+"******************");
  			if(url.toLowerCase().contains("error_404.jsp")){
  				//System.out.println("Contains 404 page");
  				pCode=404;
  			}
  		}
  		super.setStatus(pCode);
  		
	}
  
  	@Override
  	public void setRequest(DynamoHttpServletRequest pRequest) {
  		this.mRequest = pRequest;
  		if(pRequest!=null){
  			String url=this.mRequest.getRequestURI();
			//System.out.println("************** Request url in setRequest : "+url+"******************");
			if(url.toLowerCase().contains("error_404.jsp")){
  				//System.out.println("Contains 404 page in setRequest:");
  				setStatus(404);
  			}
  		}
  		super.setRequest(pRequest);
	 }
  
  @Override
  public void sendLocalRedirect(java.lang.String pLocation, DynamoHttpServletRequest pRequest) throws java.io.IOException {
    String redirectLocation = pLocation;
    String proto = pRequest.getHeader(X_FORWARDED_PROTO_HEADER);

    
    // only check for https at this time, to avoid any spoofed headers redirecting to weird protocols
    if (proto != null && HTTPS.equalsIgnoreCase(proto.trim())) {
      URI uri = null;
      try {
        uri = new URI(pLocation);
        
        // if this uri is relative, build an absolute URL based on the input request
        // if it's absolute, leave it alone - the caller must know what they're doing
        if (!uri.isAbsolute()) {
          redirectLocation = HTTPS + "://" + pRequest.getServerName() + uri;
        }
        
      } catch (URISyntaxException ex) {
        // pLocation isn't valid, bury it and we'll just stop processing and let the superclass handle it
      }
    }
    
    super.sendLocalRedirect(redirectLocation, pRequest);
  }

}
