<%@ include file="../top.jspf"%>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:getvalueof bean="/atg/svc/ticketing/TicketHolder.currentTicket" var="currentTicket"/>
<svc-agent:getUnfinishedMessage ticket="${currentTicket}" var="msg"/>

<dl class="senderForm atg-csc-base-table-row">
  <dt class="atg-csc-base-table-cell"><fmt:message key="response.compose.address.from.label" />
  </dt>
  <dd class="atg-csc-base-table-cell">
    <c:choose>
      <c:when test="${not empty msg.fromAddress.personalName}">
        <c:out value="${msg.fromAddress.personalName}" /> - <c:out value="${msg.fromAddress.address}" />
      </c:when>
      <c:otherwise>
	<c:out value="${msg.fromAddress.address}" />
      </c:otherwise>
     </c:choose>
  </dd>
</dl>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/senderDetails.jsp#1 $$Change: 946917 $--%>
