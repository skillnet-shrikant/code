<%--
 Ticket Data Column Renderer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/ticket/columnRenderer.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="colIndex" param="colIndex"/>
  <dsp:getvalueof var="ticketItemMap" param="ticketItemMap"/>
  <dsp:tomap var="userItemMap" value="${ticketItemMap.user}"/>

  <c:choose>
  <c:when test="${field == 'id'}">
    "id":"${ticketItemMap.id}"
  </c:when>

  <c:when test="${field == 'viewLink'}">
    "viewLink":"<a href=\"#\" class=\"blueU\" onclick=\"ticketHistoryViewTicket(\'orderRelatedTicketsViewTicketForm\',\'${ticketItemMap.id}\');return false;\">${ticketItemMap.id}</a>"
  </c:when>

  <c:when test="${field == 'selectLink'}">
    "selectLink":"<a href=\"#\" class=\"blueU\" onclick=\"ticketHistoryWorkTicket(\'orderRelatedTicketsWorkTicketForm\',\'${ticketItemMap.id}\');return false;\"><fmt:message key='relatedTickets.workOnTicket'/></a>"
  </c:when>

  <c:when test="${field == 'description'}">
    <c:choose>
      <c:when test="${fn:length(ticketItemMap.description) > 100}">
        "description":"<c:out value='${fn:substring(ticketItemMap.description, 0, 100)}' escapeXml='true'/><fmt:message key="text.ellipsis"/>"
      </c:when>
      <c:otherwise>
        "description":"${fn:escapeXml(ticketItemMap.description)}"
      </c:otherwise>
    </c:choose>
  </c:when>

  <c:when test="${field == 'status'}">
    <dsp:tomap var="status" value="${ticketItemMap.subStatus}"/>
    <c:set var="statusString">
      <fmt:message key="table.tickets.statusValue">
        <fmt:param>${status.parentStatus}</fmt:param>
        <fmt:param>${status.subStatusName}</fmt:param>
      </fmt:message>
    </c:set>
    "status":"${fn:escapeXml(statusString)}"
  </c:when>

  <c:when test="${field == 'creationDate'}">
    <web-ui:formatDate type="both" value="${ticketItemMap.creationTime}" dateStyle="short" timeStyle="short" var="creationDate"/>
    "creationDate":"${creationDate}"
  </c:when>

  <c:when test="${field == 'firstName'}">
    "firstName":"${fn:escapeXml(userItemMap.firstName)}"
  </c:when>

  <c:when test="${field == 'lastName'}">
    "lastName":"${fn:escapeXml(userItemMap.lastName)}"
  </c:when>

  <c:when test="${field == 'email'}">
    "email":"${fn:escapeXml(userItemMap.email)}"
  </c:when>

  <c:when test="${field == 'address'}">
    "address":"${userItemMap.homeAddress}"
  </c:when>

  <c:when test="${field == 'phoneNumber'}">
    "phoneNumber":"${userItemMap.phoneNumber}"
  </c:when>

  <c:when test="${field == 'postalCode'}">
    "postalCode":"${userItemMap.postalCode}"
  </c:when>

  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 946917 $$DateTime: 2015/01/26 17:26:27 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/ticket/columnRenderer.jsp#1 $$Change: 946917 $--%>