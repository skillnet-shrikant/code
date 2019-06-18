package com.mff.droplet;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.mff.util.MFFUtils;
import java.io.IOException;
import javax.servlet.ServletException;

public class PaginationUrlGenerator
  extends DynamoServlet
{
  private static final String RECSPERPAGE = "recsPerPage";
  private static final String CURRENTPAGE = "currentPage";
  private static final String OUTPUT = "output";
  private static final String ERROR = "error";
  private static final String MESSAGE = "message";
  private static final String REQUESTURI = "requestUri";
  private static final String URL = "url";
  
  public final void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    String lRecsPerPage = pRequest.getParameter(RECSPERPAGE);
    String lCurrentPage = pRequest.getParameter(CURRENTPAGE);
    String lRequestUri = pRequest.getParameter(REQUESTURI);
    if ((StringUtils.isEmpty(lCurrentPage)) || (StringUtils.isEmpty(lRecsPerPage)))
    {
      pRequest.setParameter(MESSAGE, "required parameters are empty");
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
    if (StringUtils.isEmpty(lRequestUri)) {
      lRequestUri = pRequest.getRequestURI();
    }
    try
    {
      Integer lCurrentPageInt = Integer.valueOf(lCurrentPage);
      Integer lRecsPerPageInt = Integer.valueOf(lRecsPerPage);
      Integer lRecordIndex = Integer.valueOf((lCurrentPageInt.intValue() - 1) * lRecsPerPageInt.intValue());
      StringBuilder lUrlBuilder = new StringBuilder();
      if (StringUtils.isEmpty(pRequest.getQueryParameter("No"))) {
        lUrlBuilder.append(lRequestUri).append("?").append(pRequest.getQueryString()).append("&No=").append(lRecordIndex);
      } else {
        lUrlBuilder.append(lRequestUri).append("?").append(MFFUtils.removeParamFromQueryString(pRequest.getQueryString(), "No")).append("&No=").append(lRecordIndex);
      }
      pRequest.setParameter(URL, lUrlBuilder.toString());
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
    catch (NumberFormatException pNumberFormatException)
    {
      pRequest.setParameter(MESSAGE, pNumberFormatException.getMessage());
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    }
  }
}
