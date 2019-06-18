<%--

This file is for displaying and handling Errors.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/error.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%-- Can't use top.jspf, since it declares errorPage --%>
<%@ page contentType="text/html; charset=UTF-8"  isELIgnored="false" %>
<%@ page pageEncoding="UTF-8"%>
<%@ page  isErrorPage="true"
          errorPage="errorstatic.html"
          import="atg.servlet.DynamoHttpServletRequest,
                  atg.servlet.jsp.ContainerJspException,
                  atg.servlet.DynamoHttpServletRequest,
                  atg.servlet.ServletUtil,
                  atg.core.exception.StackTraceUtils,
                  atg.core.util.StringUtils,
                  atg.nucleus.Nucleus" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"       uri="http://www.atg.com/taglibs/caf" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fw-beans"  uri="http://www.atg.com/taglibs/svc/svcFrameworkBeansTaglib1_0" %>
<%@ taglib prefix="svc-ui"    uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0" %>
<%@ taglib prefix="svc-agent" uri="http://www.atg.com/taglibs/svc/svc-agentTaglib1_0" %>
<dspel:page xml="true">
<dspel:importbean var="profileFormHandler" bean="/atg/userprofiling/InternalProfileFormHandler" />
<dspel:importbean var="agentProfileFormHandler" bean="/atg/agent/userprofiling/AgentProfileFormHandler" />
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile" />
<c:if test="${not agentProfileFormHandler.profile['transient']}">
 <dspel:getvalueof id="login" bean="/atg/userprofiling/Profile.login" />
 <dspel:getvalueof id="fName" bean="/atg/userprofiling/Profile.firstName" />
 <dspel:getvalueof id="lName" bean="/atg/userprofiling/Profile.lastName" />
</c:if>
<%-- Agent UI Config --%>
<dspel:importbean var="UIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration" />
<%-- CAF UI Config. Used for importing javascript from CAF --%>
<dspel:importbean var="CAFUIConfig" bean="/atg/web/ui/UIConfiguration" />
<c:choose>
 <c:when test="${empty UIConfig.styleRoot}"><c:set var="styleRoot" value="${UIConfig.contextRoot}" /></c:when>
 <c:otherwise><c:set var="styleRoot" value="${UIConfig.styleRoot}" /></c:otherwise>
</c:choose>
<c:choose>
 <c:when test="${empty UIConfig.theme}">
  <c:set var="cssPath" value="${styleRoot}${UIConfig.cssLocation}" />
  <c:set var="imageLocation" value="${styleRoot}${UIConfig.imageLocation}" />
 </c:when>
 <c:otherwise>
  <c:set var="cssPath" value="${styleRoot}${UIConfig.cssLocation}/${UIConfig.theme}" />
  <c:set var="imageLocation" value="${styleRoot}${UIConfig.imageLocation}/${UIConfig.theme}" />
 </c:otherwise>
</c:choose>
<svc-ui:restoreState var="chatUiStatus" key="chatUiStatus" scope="request"/>
<c:if test="${(not empty chatUiStatus) and (chatUiStatus.chatWindow)}"><c:set var="isChatWindow" value="${true}"/></c:if>
<c:set var="cssPath" value="${pageContext.request.contextPath}${UIConfig.cssLocation}"/>
<c:set var="stylesheetPath" value="${UIConfig.cssLocation}/${UIConfig.contentStylesheet}"/>
<c:set var="imageLocation" value="${pageContext.request.contextPath}${UIConfig.imageLocation}"/>
<c:set var="commonUILocation" value="${UIConfig.commonUILocation}"/>
<c:set var="extensionsRoot" value="${UIConfig.extensionsRoot}"/>
<c:set var="mainScriptPath" value="${pageContext.request.contextPath}${UIConfig.scriptsLocation}/${UIConfig.mainScript}" />
<dspel:getvalueof id="thisPage" bean="/OriginatingRequest.requestURI" />
<dspel:getvalueof id="thisURL" bean="/OriginatingRequest.requestURIwithQueryString" />

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <c:catch var="errorPageException">

    <dspel:importbean var="UIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
    <c:url var="imageLocation" value="${UIConfig.imageLocation}" />
    <jsp:useBean id="now" class="java.util.Date" />

  <caf:outputXhtml targetId="errorPanelContent">

  <table class="w98p">
    <tr>
      <td class="errorImage" rowspan="4">
        <dspel:img src="${imageLocation}/iconcatalog/global/icon_alertBig.gif" width="35" height="28"/>
      </td>
      <td class="w100p">
        <dspel:include src="/include/errorDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <a class="buttonSmall" href="#" onclick="hideErrorDetails('errorPanel');"
           title="<fmt:message key='error.hide'/>"><span><fmt:message key="text.OK"/></span></a>
      </td>
    </tr>
  </table>

  </caf:outputXhtml>
<%--
  Disable the error panel since this has been superceded by the message bar.
  Currently, all exceptions are being sent to the message bar.
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
--%>
<html>
<head></head>
<body>
  <table class="w98p">
    <tr>
      <td class="errorImage" rowspan="4">
        <dspel:img src="${imageLocation}/iconcatalog/global/icon_alertBig.gif" width="35" height="28"/>
      </td>
      <td class="w100p">
        <dspel:include src="/include/errorDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <a class="buttonSmall" href="#" onclick="hideErrorDetails('errorPanel');"
           title="<fmt:message key='error.hide'/>"><span><fmt:message key="text.OK"/></span></a>
      </td>
    </tr>
  </table>
</body>
</html>  

  </c:catch>
  <c:if test="${not empty errorPageException}">
    <c:out value="${errorPageException.message}"/>
  </c:if>
  </dspel:layeredBundle>

</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/error.jsp#1 $$Change: 946917 $--%>
