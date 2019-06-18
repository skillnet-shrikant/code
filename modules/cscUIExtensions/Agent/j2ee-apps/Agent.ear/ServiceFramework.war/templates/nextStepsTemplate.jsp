 <%--
 This page defines the next steps template
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/nextStepsTemplate.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%-- The odd formatting is deliberate, it eliminates the junk whitespace JSP emits --%>
<%@ page errorPage="/error.jsp"
%><%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"
%><%@ page pageEncoding="UTF-8"
%><%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fn"         uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="caf"       uri="http://www.atg.com/taglibs/caf"
%><%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
%><%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fw-beans" uri="http://www.atg.com/taglibs/svc/svcFrameworkBeansTaglib1_0"
%><%@ taglib prefix="svc-ui"    uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0"
%><%@ taglib prefix="svc-agent" uri="http://www.atg.com/taglibs/svc/svc-agentTaglib1_0"
%><%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"
%>
<dspel:page xml="true">
  <dspel:getvalueof var="actionId" param="actionId"/>
  <dspel:getvalueof var="actionJavaScript" param="actionJavaScript"/>
  <dspel:getvalueof var="imageUrl" param="imageUrl"/>
  <dspel:getvalueof var="labelKey" param="labelKey"/>
  <dspel:getvalueof var="labelText" param="labelText"/>
  <dspel:getvalueof var="isSeparator" param="isSeparator"/>
  <dspel:getvalueof var="formhandler" param="formhandler"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <c:set var="onClickString" value=""/>
    <%-- if you don't have an actionId, you have to put the form where the action is called --%>
  <c:if test="${not empty actionJavaScript}">
    <c:set var="onClickString" value="${onClickString}${actionJavaScript};"/>
  </c:if>
  <c:if test="${not empty actionId}">
      <c:set var="actionString">${actionId}('${actionId}Form');</c:set>
    <c:set var="onClickString" value="${onClickString}${actionString}"/>
      <c:if test="${not empty formhandler}">
        <dspel:form style="display:none" id="${actionId}Form" action="#">
          <dspel:input type="hidden" value="" bean="${formhandler}"/>
        </dspel:form>
      </c:if>
  </c:if>
  <table border="0" cellpadding="0" cellspacing="0" class="layoutTable">
    <c:choose>
    <c:when test="${not empty isSeparator and isSeparator eq true}">
    <%-- separator --%>
    <tr><td colspan="2"></td></tr>
    </c:when>
    <%-- Menu item --%>
    <c:otherwise>
      <tr class="nextStepsMenuItem" onmouseover="this.className='nextStepsMenuItemMouseOver';" onmouseout="this.className='nextStepsMenuItem';">
        <%--
        BUG CSC-169559: Removing all Next Steps icons from workspace
        <td class="nextStepsMenuItem" onclick="${onClickString}" width="30">
          <dspel:img src="${imageUrl}" width="25" height="22" />
        </td>
        --%>
        <td class="nextStepsMenuItem" onclick="${onClickString}" style="line-height:22px;padding-left:9px">
          <a class="atg_navigationHighlight nextStepsLink" tabindex="0" href="#">
            <c:choose>
              <c:when test="${not empty labelText}">${labelText}</c:when>
              <c:otherwise><fmt:message key="${labelKey}"/></c:otherwise>
            </c:choose>
          </a>
        </td>
      </tr>
    </c:otherwise>
    </c:choose>
  </table>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/nextStepsTemplate.jsp#1 $$Change: 946917 $--%>
