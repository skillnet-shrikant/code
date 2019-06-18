<%--

This file is for displaying and handling Errors.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/errordisplay.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<%@ page  isErrorPage="true"
          import="atg.servlet.DynamoHttpServletRequest,
                  atg.servlet.jsp.ContainerJspException,
                  atg.servlet.DynamoHttpServletRequest,
                  atg.servlet.ServletUtil,
                  atg.core.exception.StackTraceUtils,
                  atg.core.util.StringUtils,
                  atg.nucleus.Nucleus" %>
<%
	// fix bug 113316 - be sure status is 500 for unhandled exceptions
	// NO HTML CAN APPEAR BEFORE THIS CALL!!
        // make sure the response hasn't already been committed first
	if (!response.isCommitted()) response.setStatus(500);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <c:catch var="errorPageException">

    <dspel:importbean var="UIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
    <c:url var="imageLocation" value="${UIConfig.imageLocation}" />
    <jsp:useBean id="now" class="java.util.Date" />

<%
  if(exception == null) {
    exception = (Throwable)atg.svc.repository.service.StateHolderService.getInstance().getAttribute("UNHANDLED_EXCEPTION");
  }
  else {
    atg.svc.repository.service.StateHolderService.getInstance().setAttribute("UNHANDLED_EXCEPTION", exception);
  }

  DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
  if (dynamoRequest != null && dynamoRequest.getLog() != null) {
    dynamoRequest.logError("Error occured while processing page, rendering error page instead.", exception);
  }
  else {
    Nucleus.getGlobalNucleus().logError("Error occured while processing page, rendering error page instead.", exception);
  }
%>

    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>
          <fmt:message key="error.window.title"/>
        </title>
        <link type="text/css" href="<c:out value='${stylesheetPath}'/>" rel="stylesheet"/>
        <script type="text/javascript" src="<c:out value='${UIConfig.contextRoot}'/>/script/error.js"></script>
      </head>
      <body>
        <table class="w98p">
          <tr>
            <td>
              <dspel:include src="/include/errorDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
            </td>
          </tr>
        </table>

        <span style="display:none;">

          <caf:outputJavaScript>
            document.getElementById("loadAction").style.display = "none";
          </caf:outputJavaScript>

          <caf:outputXhtml targetId="errorPanelContent">

            <table class="w98p">
              <tr>
                <td class="errorImage"
                    rowspan="4">
                  <dspel:img src="${imageLocation}/iconcatalog/global/icon_alertBig.gif" width="35" height="28"/>
                </td>
                <td class="w100p">

                  <dspel:include src="/include/errorDetail.jsp" otherContext="${UIConfig.contextRoot}"/>

                </td>
              </tr>
              <tr>
                <td colspan="2">
                  <a class="buttonSmall"
                     href="#"
                     onclick="hideErrorDetails('errorPanel');"
                     title="<fmt:message key='error.hide'/>"><span><fmt:message key="text.OK"/></span></a>
                </td>
              </tr>
            </table>

          </caf:outputXhtml>

          <caf:outputJavaScript>
            try {
              var targetElement = document.getElementById("errorPanel");
              document.getElementById("errorPanel").style.display = "block";
              document.getElementById("errorPanel").scrollIntoView(false);
            }
            catch (e) {
              // No error panel - handled by message bar
              var mb = dijit.byId("messageBar");
              if (mb) {
                mb.retrieveMessages();
              }
            }
          </caf:outputJavaScript>
        </span>

      </body>
    </html>

  </c:catch>
  <c:if test="${not empty errorPageException}">
    <c:out value="${errorPageException.message}"/>
  </c:if>
  </dspel:layeredBundle>

</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/errordisplay.jsp#1 $$Change: 946917 $--%>
