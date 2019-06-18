
<%--
 This page defines the customer link template
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/customerLink.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
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
  <dspel:getvalueof var="ticketId" param="ticketId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="otherContext" param="otherContext"/>
  <dspel:getvalueof var="resourceBundle" param="resourceBundle"/>
  <dspel:getvalueof var="customerId" bean="/atg/userprofiling/ActiveCustomerProfile.repositoryId"/>

  <c:set var="strings" value="atg.svc.agent.WebAppResources" />
  <c:if test="${not empty resourceBundle}"><c:set var="strings" value="${resourceBundle}"/></c:if>
  <dspel:layeredBundle basename="${strings}">
      <dspel:getvalueof var="customer" bean="/atg/userprofiling/ActiveCustomerProfile"/>
      <c:if test="${not empty customerId}">      
        <dspel:getvalueof var="customer_firstName" bean="/atg/userprofiling/ActiveCustomerProfile.firstName"/>
        <dspel:getvalueof var="customer_lastName" bean="/atg/userprofiling/ActiveCustomerProfile.lastName"/>
        <c:choose>
        <c:when test="${not empty customer_firstName or not empty customer_lastName}">
          <c:set var="customerName"><dspel:valueof bean="/atg/userprofiling/ActiveCustomerProfile.firstName"/> <dspel:valueof bean="/atg/userprofiling/ActiveCustomerProfile.lastName"/></c:set>
          <c:set var="customerFunction" value="viewCurrentCustomer('customersTab')"/>
        </c:when>
        <c:otherwise>
          <c:set var="customerName"><fmt:message key="link-customer" /></c:set>
          <c:set var="customerFunction" value="showCustomerSearch()"/>
        </c:otherwise>
        </c:choose>
        <div id="customerLink" class="tabValue">
          <a href="#" onclick="<dspel:valueof value='${customerFunction}'/>;event.cancelBubble=true; return false;" class="globalTicket">
            <c:out value="${customerName}" escapeXml="false"/>
          </a>
        </div>
      </c:if>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/customerLink.jsp#1 $$Change: 946917 $--%>
