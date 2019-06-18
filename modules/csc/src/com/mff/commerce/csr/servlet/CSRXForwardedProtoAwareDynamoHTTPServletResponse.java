package com.mff.commerce.csr.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class CSRXForwardedProtoAwareDynamoHTTPServletResponse extends DynamoHttpServletResponse {

  public static final String SLASH = "/";

  public static final String HTTPS = "https";
  public static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";

  @Override
  public void sendLocalRedirect(String pLocation, DynamoHttpServletRequest pRequest) throws IOException {
    String redirectLocation = pLocation;
    String proto = pRequest.getHeader(X_FORWARDED_PROTO_HEADER);
    
    //check to make sure that redirects relative to context root
    //are not impacted. For e.g. "main.jsp?..." instead of "/agent/main.jsp?..."
    if (pLocation != null && pLocation.startsWith(SLASH)) {

      // only check for https at this time, to avoid any spoofed headers
      // redirecting to weird protocols
      if (proto != null && HTTPS.equalsIgnoreCase(proto.trim())) {
        URI uri = null;
        try {
          uri = new URI(pLocation);

          // if this uri is relative, build an absolute URL based on
          // the input request
          // if it's absolute, leave it alone - the caller must know
          // what they're doing
          if (!uri.isAbsolute()) {
            redirectLocation = HTTPS + "://" + pRequest.getServerName() + uri;
          }

        } catch (URISyntaxException ex) {
          // pLocation isn't valid, bury it and we'll just stop
          // processing and let the superclass handle it
        }
      }
    }
    super.sendLocalRedirect(redirectLocation, pRequest);
  }
}
