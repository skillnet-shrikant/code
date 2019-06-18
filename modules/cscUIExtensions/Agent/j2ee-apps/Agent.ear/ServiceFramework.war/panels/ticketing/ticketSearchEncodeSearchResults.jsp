<%--
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchEncodeSearchResults.jsp#2 $$Change: 953229 $
    @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%><%@ page errorPage="/error.jsp"
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
%><dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

<dspel:importbean scope="request" var="ticketSearchFormHandler" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler" />

<json:object prettyPrint="${UIConfig.prettyPrintResponses}">

<json:property name="resultLength" value="${ticketSearchFormHandler.totalItemCount}"/>
  <json:array name="results" items="${ticketSearchFormHandler.viewItems}" var="result">
    <json:object>
      <json:property name="id" value="${result.item.id}" />
      <json:property name="description" escapeXml="false">
        <c:if test="${result.item.description != null}">
          <c:out value="${result.item.description}" />
        </c:if>
        <c:if test="${result.item.description == null}">
          <fmt:message key="no-description-entered" />
        </c:if>
      </json:property>
      <dspel:getvalueof var="currentTicketIsTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient" />
      <json:property name="workOrReopen" escapeXml="false">
        <c:if test="${result.managerData.status != 'Closed'}">
          work
        </c:if>
        <c:if test="${result.managerData.status == 'Closed'}">
          reopen
        </c:if>
      </json:property>
      <json:property name="age" value='${result.managerData.age}'/>
      <json:property name="creationTime" escapeXml="false">
        <fmt:formatDate type="both" value="${result.item.creationTime}" dateStyle="short" timeStyle="short" />
      </json:property>
      <dspel:tomap var="substatus" value="${result.item.subStatus}" />
      <json:property name="status" escapeXml="false">
        <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
          <dspel:param name="descriptionId" value="${result.managerData.status}"/>
          <dspel:param name="baseName" value="STATUS"/>
          <dspel:param name="elementName" value="parentDescription"/>
          <dspel:oparam name="output">
            <dspel:getvalueof var="parentDescription" param="parentDescription"/>
          </dspel:oparam>
        </dspel:droplet>
        <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
          <dspel:param name="descriptionId" value="${substatus.subStatusName}"/>
          <dspel:param name="baseName" value="SUBSTATUS"/>
          <dspel:param name="elementName" value="subDescription"/>
          <dspel:oparam name="output">
            <dspel:getvalueof var="subDescription" param="subDescription"/>
          </dspel:oparam>
        </dspel:droplet>
        <c:out value="${parentDescription} (${subDescription})" />
      </json:property>
    </json:object>
  </json:array>
</json:object>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchEncodeSearchResults.jsp#2 $$Change: 953229 $--%>
