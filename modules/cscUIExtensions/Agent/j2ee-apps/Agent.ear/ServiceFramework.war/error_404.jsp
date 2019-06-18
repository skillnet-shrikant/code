<%--

This file is for displaying and handeling HTTP 404 Error.


@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/error_404.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="/include/top.jspf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<%-- Self Service UI Config --%>
<dspel:importbean var="UIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>

<jsp:useBean id="now" class="java.util.Date" />

<html>
<head>
<title><fmt:message key="error404.window.title"/></title>
<link href="/service/css/outerStyles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div id="topBanner"><dspel:img src="${imageLocation}/banner/banner60-left.png" width="654" height="60" /></div>


<dspel:img src="${imageLocation}/clear.gif" width="100%" height="1" style=" border-bottom:1px solid #98b2e0;" />  <br />


 <table class="borderBlue w75"  align="center" cellpadding="0">
  <tr>

          <td><b><fmt:message key="error404.header"/></b></td>

  </tr>
  <tr>
    <td colspan="2" class="leftPad5 bgWhite">
      <br />
      <fmt:message key="error404.message.pagegone"/><br />
      <br />
      <br />

      <%-- HOME PAGE LINK --%>   
      <svc-ui:getOptionAsString  var="homePage"   optionName="SiteURL" />
       <c:if test="${ ! empty homePage}">
        <dspel:a page='${homePage}' ><fmt:message key="error404.message.homepage.linktext"/></dspel:a>
        <br>
        <br />
       </c:if>
      <%-- DATE --%>
      <br /> 
      <web-ui:formatDate value="${now}" type="both" dateStyle="medium" timeStyle="medium" />


    </td>
  </tr>
</table>
</body>
</html>

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/error_404.jsp#1 $ $Change: 946917 $ $DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/error_404.jsp#1 $$Change: 946917 $--%>
