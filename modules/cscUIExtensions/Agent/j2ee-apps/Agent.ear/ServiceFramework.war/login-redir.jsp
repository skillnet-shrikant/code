<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/include/top.jspf" %>   
<dspel:page xml="true">
<html>
  <head>
    <meta http-equiv="refresh" content="1;${UIConfig.contextRoot}/login.jsp" />
  </head>
  <body></body>
</html>
<c:if test="${param.sessioninvalid and param.ppr}">
  <caf:outputStatus statusKey="sessioninvalid"
    redirectUrl="${UIConfig.contextRoot}/login.jsp?${stateHolder.windowIdParameterName}=${windowId}"/>
</c:if>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/login-redir.jsp#1 $$Change: 946917 $--%>
