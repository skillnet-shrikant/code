<%--
 Ticket Data Column Renderer
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/columnRenderer.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%><%@ include file="/include/top.jspf"%>
<dspel:page>

<dspel:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <dspel:getvalueof var="field" param="field"/>
  <dspel:getvalueof var="colIndex" param="colIndex"/>
  <dspel:getvalueof var="ticketItem" param="ticketItem"/>
  <web-ui:formatDate var="tmpCreationTime" value="${ticketItem.item.creationTime}" type="both" dateStyle="short" timeStyle="short" />
  <web-ui:formatDate var="tmpDueTime" value="${ticketItem.item.dueTime}" type="both" dateStyle="short" timeStyle="short" />

  <c:choose>
  <c:when test="${field == 'id'}">
    "id":"${ticketItem.item.id}"
  </c:when>

  <c:when test="${field == 'viewLink'}">
    <dspel:layeredBundle basename="atg.svc.agent.ui.UserMessages">
      "viewLink": "<a href=\"#\" class=\"blueU\" onclick=\"ticketHistoryViewTicket(\'ticketHistoryResultsViewTicketForm\',\'${ticketItem.item.id}\');return false;\">${ticketItem.item.id}</a>"
    </dspel:layeredBundle>
  </c:when>

  <c:when test="${field == 'selectLink'}">
    <dspel:layeredBundle basename="atg.svc.agent.ui.UserMessages">
      "selectLink": "<a href=\"#\" class=\"blueU\" onclick=\"ticketHistoryWorkTicket(\'workTicketNoSwitchForm\',\'${ticketItem.item.id}\');return false;\"><fmt:message key='select-ticket'/></a>"
    </dspel:layeredBundle>
  </c:when>

  <c:when test="${field == 'numberOfActivities'}">
    "numberOfActivities":"${ticketItem.managerData.numberOfActivities}"
  </c:when>

  <c:when test="${field == 'description'}">
    <svc-ui:getEndcodedJavascriptString var="ticketDescription" originalString="${ticketItem.item.description}" />
    "description":"${fn:escapeXml(ticketDescription)}"
  </c:when>

  <c:when test="${field == 'priority'}">
    "priority":"${ticketItem.item.priority}"
  </c:when>

  <c:when test="${field == 'age'}">
    "age":"${ticketItem.managerData.age}"
  </c:when>

  <c:when test="${field == 'assigned'}">
    "assigned":"${ticketItem.managerData.assigned}"
  </c:when>

  <c:when test="${field == 'status'}">
    <dspel:tomap var="status" value="${ticketItem.item.subStatus}"/>
    <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
      <dspel:param name="descriptionId" value="${status.parentStatus}"/>
      <dspel:param name="baseName" value="STATUS"/>
      <dspel:param name="elementName" value="parentDescription"/>
      <dspel:oparam name="output">
        <dspel:getvalueof var="parentDescription" param="parentDescription"/>
      </dspel:oparam>
    </dspel:droplet>
    <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
      <dspel:param name="descriptionId" value="${status.subStatusName}"/>
      <dspel:param name="baseName" value="SUBSTATUS"/>
      <dspel:param name="elementName" value="subDescription"/>
      <dspel:oparam name="output">
        <dspel:getvalueof var="subDescription" param="subDescription"/>
      </dspel:oparam>
    </dspel:droplet>
    <c:set var="statusString">
      <fmt:message key="table.tickets.statusValue">
        <fmt:param>${parentDescription}</fmt:param>
        <fmt:param>${subDescription}</fmt:param>
      </fmt:message>
    </c:set>
    "status":"${statusString}"
  </c:when>

  <c:when test="${field == 'creationTime'}">
    "creationTime":"${tmpCreationTime}"
  </c:when>

  <c:when test="${field == 'dueTime'}">
    "dueTime":"${tmpDueTime}"
  </c:when>

  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dspel:layeredBundle>
</dspel:page>
<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/columnRenderer.jsp#1 $$Change: 946917 $--%>