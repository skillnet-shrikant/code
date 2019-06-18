<!-- ticketSummary.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummary.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>
  <c:set var="values" value="${ticketingFormHandler.viewTicket}"/>
  <c:set var="customerInfo" value="${ticketingFormHandler.viewCustomerInfoMap}"/>
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    <div class="atg-csc-base-table">
      <div>
        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left">
            <span><fmt:message key="status-label"/></span>
          </div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three">
            <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
              <dspel:param name="descriptionId" value="${ticketingFormHandler.viewStatus}"/>
              <dspel:param name="baseName" value="STATUS"/>
              <dspel:param name="elementName" value="description"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="description" param="description"/>
                <c:out value="${description}"/>
              </dspel:oparam>
            </dspel:droplet>
            <dspel:tomap var="substatus" value="${values.subStatus}"/>
            <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
              <dspel:param name="descriptionId" value="${substatus.subStatusName}"/>
              <dspel:param name="baseName" value="SUBSTATUS"/>
              <dspel:param name="elementName" value="description"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="description" param="description"/>
                (<c:out value="${description}"/>)
              </dspel:oparam>
            </dspel:droplet>
          </div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left">
            <span><fmt:message key="priority-label"/></span>
          </div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${values.priority}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left">
            <span><fmt:message key="group-label"/></span>
          </div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${ticketingFormHandler.viewTicketQueue.name}"/></div>
          <%--
          <div class="formLabel w20p"><span class="formLabel"><fmt:message key="pushable-label"/></span></div>
          <div>
            <c:if test='${true == ticketingFormHandler.values.pushable}'><fmt:message key="yes"/></c:if>
            <c:if test='${false == ticketingFormHandler.values.pushable}'><fmt:message key="no"/></c:if>
          </div>
          --%>
        </div>

        <div class="atg-csc-base-table-row atg-csc-base-spacing-two-left atg-csc-base-table-hr-margin-one">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left">
            <span><fmt:message key="description-label"/></span>
          </div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three" colspan="3"><c:out escapeXml="false" value="${values.description}"/></div>
        </div>

        <div class="atg-csc-base-table-row atg-csc-base-table-hr"></div>

        <div class="atg-csc-base-table-row atg-csc-base-table-hr-margin-two">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="first-name-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.firstName}"/></div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="last-name-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.lastName}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="phone-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.phone}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="email-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.email}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="home-address-label"/></div>
          <div colspan="3" class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.address}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="city-label"/></div>
          <div colspan="3" class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.city}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="state-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.state}"/></div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="country-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.country}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="postal-code-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-three"><c:out value="${customerInfo.postalCode}"/></div>
        </div>
      </div>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummary.jsp#1 $$Change: 946917 $--%>
