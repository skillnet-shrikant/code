<%--
 This page defines the ticket link template
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/ticketLink.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%-- The odd formatting is deliberate, it eliminates the junk whitespace JSP emits --%>
<%@ include file="/include/top.jspf"
%>
<dspel:page xml="true">
  <dspel:getvalueof var="ticketId" param="ticketId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="otherContext" param="otherContext"/>
  <dspel:getvalueof var="resourceBundle" param="resourceBundle"/>

  <c:set var="strings" value="atg.svc.agent.WebAppResources" />
  <c:if test="${not empty resourceBundle}"><c:set var="strings" value="${resourceBundle}"/></c:if>
  <dspel:layeredBundle basename="${strings}">

    <div id="ticketLink" class="tabValue">
      <a href="#" onclick="document.getElementById('globalViewTicketForm').ticketId.value='<c:out value="${ticketId}" />';viewTicket('globalViewTicketForm');event.cancelBubble=true;return false;" class="globalTicket">
        <dspel:getvalueof var="transient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>
        <c:if test="${transient}">
          <fmt:message key="globalTicketContext.newticket"/>
        </c:if>
        <c:if test="${not transient}">
          <c:out value="${ticketId}" />
        </c:if>
      </a>
    </div>
    <dspel:form style="display:none" id="globalSaveTicketForm" action="#">
      <dspel:input type="hidden" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.save"/>
    </dspel:form>
    <div id="ticketSave" class="tabActions">
      <a href="#" onclick="globalSaveTicket('globalSaveTicketForm');event.cancelBubble=true;return false;" class="iconSave">Save</a>
    </div>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/ticketLink.jsp#1 $$Change: 946917 $--%>
