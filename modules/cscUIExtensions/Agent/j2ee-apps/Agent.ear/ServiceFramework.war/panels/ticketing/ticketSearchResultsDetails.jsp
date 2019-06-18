<%--
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchResultsDetails.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
<dspel:droplet name="/atg/ticketing/droplet/TicketLookupDroplet">
<dspel:param name="ticketId" value="${param.ticketId}"/>
<dspel:oparam name="output">
<dspel:getvalueof var="result" param="result"/>
<dspel:tomap var="resultItem" param="result" recursive="true"/>

<div id="<c:out value='customerInfoDiv${resultItem.id}'/>" class="datatable detailSection" style="display:block;width:100%;">
  <h5>
      <fmt:message key="customer-info-label" />
  </h5>
  <c:if test="${resultItem.user != null}">
  
  <dl class="gridDetailAddress">
    <dt><fmt:message key="full-name-label" /></dt>
    <dd>
      <a href="#" onclick="document.getElementById('ticketSearchResultsViewCustomerForm').customerId.value=<c:out value='${resultItem.user.id}'/>;viewCustomer('ticketSearchResultsViewCustomerForm');">
      <c:set var="firstName"><c:out value="${resultItem.user.firstName}" default="" /></c:set>
      <c:set var="lastName"><c:out value="${resultItem.user.lastName}" default="" /></c:set>
      <fmt:message key="full-name">
        <fmt:param value="${firstName}" />
        <fmt:param value="${lastName}" />
      </fmt:message>
      </a>
    </dd>
    
    <dt><fmt:message key="address-label" /></dt>
    <dd>
      <c:set var="address1"><c:out value="${resultItem.user.homeAddress.address1}" default="" /></c:set>
      <c:set var="address2"><c:out value="${resultItem.user.homeAddress.address2}" default="" /></c:set>
        <fmt:message key="full-address">
          <fmt:param value="${address1}" />
          <c:if test="${address2 ne ''}"><br/></c:if>
          <fmt:param value="${address2}" />
        </fmt:message>
        <br/>
        <c:set var="city"><c:out value="${resultItem.user.homeAddress.city}" default="" /></c:set>
        <c:set var="state"><c:out value="${resultItem.user.homeAddress.state}" default="" /></c:set>
        <c:set var="postalCode"><c:out value="${resultItem.user.homeAddress.postalCode}" default="" /></c:set>
        <c:set var="country"><c:out value="${resultItem.user.homeAddress.country}" default="" /></c:set>        
        <c:set var="phoneNumber"><c:out value="${resultItem.user.homeAddress.phoneNumber}" default="" /></c:set>
        <c:if test="${not empty city || not empty state || not empty postalCode || not empty country}">
          <fmt:message key="rest-of-address">
            <fmt:param value="${city}" />
            <fmt:param value="${state}" />
            <fmt:param value="${postalCode}" />
            <fmt:param value="${country}" />
          </fmt:message>
        </c:if>
    </dd>
    
  </dl>
  <dl class="gridDetailContactInfo">
    
    <dt><fmt:message key="phone-label" /></dt>
    <dd><c:out value="${phoneNumber}" /></dd>
    
    <dt>
      <fmt:message key="email-label" />
      </dt>
    <dd>
        <c:set var="email"><c:out value="${resultItem.user.email}" default="" /></c:set>
        <c:out value="${email}" />
    </dd>
    
    <dt><fmt:message key="login-name-label" /></dt>
    <dd>
      <c:set var="login"><c:out value="${resultItem.user.login}" default="" /></c:set>
      <c:out value="${login}" />
    </dd>

    <dt><fmt:message key="profile-id-label" /></dt>
    <dd>
      <c:out value="${resultItem.user.id}"/>
    </dd>
  </dl>
  
  
  </c:if>
  <c:if test="${resultItem.user == null}">
    <fmt:message key="no-user" />
  </c:if>
</div>
<div id="<c:out value='customerTicketInfoDiv${resultItem.id}'/>" class="datatable detailSection" style="width:100%;display:block;clear:both">
  <h5 >
      <fmt:message key="customer-info-ticket-label" />
  </h5>

  <c:if test="${resultItem.customerDetails != null}">
      <dl class="gridDetailAddress">
        <dt><fmt:message key="full-name-label" /></dt>
        <dd>
           <c:set var="firstName"><c:out value="${resultItem.customerDetails.firstName}" default="" /></c:set>
            <c:set var="lastName"><c:out value="${resultItem.customerDetails.lastName}" default="" /></c:set>
            <c:if test="${not empty firstName || not empty lastName}">
              <fmt:message key="full-name">
                <fmt:param value="${firstName}" />
                <fmt:param value="${lastName}" />
              </fmt:message>
            </c:if>
        </dd>

        <dt><fmt:message key="address-label" /></dt>
        <dd>
            <c:out value="${resultItem.customerDetails.address}" default="" /><br/>
            <c:set var="city"><c:out value="${resultItem.customerDetails.city}" default="" /></c:set>
            <c:set var="state"><c:out value="${resultItem.customerDetails.state}" default="" /></c:set>
            <c:set var="postalCode"><c:out value="${resultItem.customerDetails.postalCode}" default="" /></c:set>
            <c:set var="country"><c:out value="${resultItem.customerDetails.country}" default="" /></c:set>
            <c:set var="phoneNumber"><c:out value="${resultItem.customerDetails.phone}" default="" /></c:set>

              <c:if test="${not empty city || not empty state || not empty postalCode || not empty country}">
                <fmt:message key="rest-of-address">
                  <fmt:param value="${city}" />
                  <fmt:param value="${state}" />
                  <fmt:param value="${postalCode}" /><br/>
                  <fmt:param value="${country}" />
                </fmt:message>
              </c:if>
        </dd>

      </dl>
      <dl class="gridDetailContactInfo">

        <dt><fmt:message key="phone-label" /></dt>
        <dd><c:out value="${phoneNumber}" /></dd>

        <dt>
          <fmt:message key="email-label" />
          </dt>
        <dd>
            <c:set var="email"><c:out value="${resultItem.customerDetails.email}" default="" /></c:set>
            <c:out value="${email}" />
        </dd>

      </dl>
  
  
</c:if>
<c:if test="${resultItem.customerDetails == null}">
  <fmt:message key="no-details" />
</c:if>
</div>

<div id="<c:out value='associatedTicketsDiv${resultItem.id}'/>" class="datatable detailSection"  style="display:block;width:100%;clear:both;">
  <h5 >
    <fmt:message key="associated-tickets-label" />
  </h5>
  <c:if test="${resultItem.relatedTickets != null && not empty resultItem.relatedTickets}">
  <table class="data tickets">
    <tr>
      <th><fmt:message key="table.tickets.ticketId"/></th>
      <th><fmt:message key="table.tickets.description" /></th>
      <th><fmt:message key="table.tickets.created" /></th>
      <th><fmt:message key="table.tickets.age" /></th>
      <th><fmt:message key="table.tickets.status" /></th>
    </tr>
    <c:forEach var="associatedTicketItem" items="${resultItem.relatedTickets}" begin="0" end="2" varStatus="index">
      <dspel:tomap var="associatedTicket" value="${associatedTicketItem}" />
      <c:if test="${index.index % 2 == 1}">
        <tr class="bgWhite">
      </c:if>
      <c:if test="${index.index % 2 == 0}">
        <tr class="bgBlue">
      </c:if>
      <td>

        <a href="#" class="blueU"
          onclick="document.getElementById('ticketSearchResultsViewTicketForm').ticketId.value=<c:out value='${associatedTicket.id}'/>;viewTicket('ticketSearchResultsViewTicketForm');">
          <c:out value="${associatedTicket.id}" />
        </a>
      </td>
      <td><c:out value="${associatedTicket.description}" /></td>
      <td><c:out value="${associatedTicket.creationTime}"/></td>
      <td><span class="textLeft_iconRight"><c:out value="${associatedTicket.ageInDays}" /><fmt:message key="day-char" /><c:out value="${associatedTicket.ageInHours - (associatedTicket.ageInDays * 24)}" /><fmt:message key="hour-char" /></span></td>
      <td><dspel:tomap var="status" value="${associatedTicket.subStatus}" /> <c:out value="${status.subStatusName}" /></td>
   
    </tr>
  </c:forEach>
  <tr class="bgGray">
    <td colspan="7" class="right"><a href="#" onclick="document.getElementById('ticketSearchResultsViewTicketForm').ticketId.value=<c:out value='${resultItem.id}'/>;viewTicket('ticketSearchResultsViewTicketForm');"><fmt:message key="view-all" /></a></td>
  </tr>
  </table>
  </c:if>
  <c:if test="${resultItem.relatedTickets == null || empty resultItem.relatedTickets}">
    <fmt:message key="no-tickets" />
  </c:if>
</div>
</dspel:oparam>
</dspel:droplet>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketing/ticketSearchResultsDetails.jsp#1 $$Change: 946917 $--%>
