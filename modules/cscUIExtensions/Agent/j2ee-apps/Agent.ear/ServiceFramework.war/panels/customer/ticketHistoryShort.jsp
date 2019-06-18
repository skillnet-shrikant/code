<%--
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/ticketHistoryShort.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
<dspel:importbean bean="/atg/commerce/custsvc/ticketing/ChangeTicket" />
<dspel:importbean bean="/atg/commerce/custsvc/ticketing/CSRTicketingTools" />
<dspel:importbean bean="/atg/ticketing/droplet/TicketLookupDroplet" />
<dspel:importbean bean="/atg/dynamo/droplet/Range" />
<dspel:importbean bean="/atg/dynamo/droplet/Switch" />
<dspel:importbean bean="/atg/userprofiling/CustomerProfile" />
<dspel:form style="display:none" action="#" id="ticketHistoryResultsViewTicketForm" formid="ticketHistoryResultsViewTicketForm">
  <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
  <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
</dspel:form>

<dspel:droplet name="TicketLookupDroplet">
  <dspel:param name="searchProperty" value="user" />
  <dspel:param name="value" param="customerId" />
  <dspel:param name="numTickets" value="4" />
  <dspel:oparam name="output">

    <dspel:droplet name="Range">
      <dspel:param name="start" param="rangeStartIndex" />
      <dspel:param name="howMany" param="rangeNumTickets" />
      <dspel:param name="sortProperties" value="-creationTime" />
      <dspel:param name="array" param="result" />

      <dspel:oparam name="outputStart">
        <dspel:getvalueof var="numTickets" param="size"/>
        
        <h4 style="margin-bottom:0px;padding-bottom:4px"><fmt:message key="ticketHistory"/>
        <c:if test="${numTickets > 3}">
          &nbsp;&nbsp;<a href="#" class="atg_commerce_csr_viewAll" onclick="openCustomerInfo('<dspel:valueof param="customerId" />');"><fmt:message key="ticketHistoryMore"/></a>
        </c:if>  
        </h4>
        
        <table summary="Customer Search Results Tickets" class="atg_dataTable" cellpadding="0"
          cellspacing="0" style="width:100%;">
        <thead>
          <tr>
            <th scope="col" style="width:80px;"><fmt:message key="table.tickets.ticketId"/></th>
            <th scope="col"><fmt:message key="table.tickets.description"/></th>
            <th scope="col" style="width:80px;"><fmt:message key="table.tickets.age"/></th>
            <th scope="col"><fmt:message key="table.tickets.status"/></th>
          </tr>
        </thead>
        <tbody>
      </dspel:oparam>

      <dspel:oparam name="output">
        <dspel:getvalueof var="index" param="index"/>
        <dspel:tomap var="elementItem" param="element"/>
        <tr class="${index % 2 == 0 ? '' : 'atg_altRow'}">
        <td>
          <a href="#" onclick="ticketHistoryViewTicket('ticketHistoryResultsViewTicketForm','<dspel:valueof param="element.id" />');return false;">
            <dspel:valueof param="element.id" />
          </a>
        </td>
        <td><dspel:valueof param="element.description" /></td>
        <c:set var="fractionalAgeInHours" value="${elementItem.ageInHours % 24}"/> 
        <td>${! empty elementItem.ageInDays ? elementItem.ageInDays : '0'}<fmt:message key="day-char"/>${fractionalAgeInHours != 0 ? fractionalAgeInHours : '0'}<fmt:message key="hour-char"/></td>
        <dspel:tomap var="status" value="${elementItem.subStatus}"/>
        <c:set var="statusString"><fmt:message key="${status.parentStatus}" /> (<fmt:message key="${status.subStatusName}" />)</c:set>
        <td>${statusString}</td>
      </tr>
      </dspel:oparam>

      </dspel:droplet>
      </dspel:oparam>
      
      <dspel:oparam name="empty">
      <h4 style="padding-bottom:4px"><fmt:message key="ticketHistory"/></h4>

      <table summary="Customer Search Results Tickets" class="atg_dataTable" cellpadding="0"
        cellspacing="0" style="width:100%;">
        <thead>
          <tr>
            <th scope="col" style="width:80px;"><fmt:message key="table.tickets.ticketId"/></th>
            <th scope="col"><fmt:message key="table.tickets.description"/></th>
            <th scope="col" style="width:80px;"><fmt:message key="table.tickets.age"/></th>
            <th scope="col"><fmt:message key="table.tickets.status"/></th>
          </tr>
        </thead>
        <tbody>
        <tr>
          <td colspan="5">
            <fmt:message key="no-tickets-for-customer"/>
          </td>
        </tr>
      </dspel:oparam>
      
      <dspel:oparam name="error">
        <tr valign="top">
          <td colspan="5">
            <dspel:valueof param="errorMsg"><fmt:message key="ticket-history-error"/></dspel:valueof>
          </td>
        </tr>
      </dspel:oparam>
      
    </dspel:droplet>
    <!-- /Tickets Lookup -->
  </tbody>
</table>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/ticketHistoryShort.jsp#2 $$Change: 1179550 $--%>
