<!-- ticketSummaryEditable.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummaryEditable.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">          

  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>
  <dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <div class="atg-csc-base-table">
    <form name="ticketForm" id="ticketForm">

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left">
          <span class="formLabel"><fmt:message key="status-label"/></span>
        </div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
            <dspel:param name="descriptionId" value="${ticketingFormHandler.viewStatus}"/>
            <dspel:param name="baseName" value="STATUS"/>
            <dspel:param name="elementName" value="description"/>
            <dspel:oparam name="output">
              <dspel:getvalueof var="description" param="description"/>
              <c:out value="${description}"/>
            </dspel:oparam>
          </dspel:droplet>
          <dspel:tomap var="substatus" value="${ticketingFormHandler.values.subStatus}"/>
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
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left">
          <span><fmt:message key="priority-label"/></span>
        </div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <select id="atg_service_ticketing_ticketForm_priority" name="prioritySelect" class="tickets atg-csc-base-length-four">
            <c:forEach var="priority" begin="${ticketingFormHandler.priorityMin}" end="${ticketingFormHandler.priorityMax}">
              <c:if test="${priority == ticketingFormHandler.values.priority}">
                <option value='<c:out value="${priority}"/>' selected="selected">
                  <c:out value="${priority}"/>
                </option>
              </c:if>
              <c:if test="${priority != ticketingFormHandler.values.priority}">
                <option value='<c:out value="${priority}"/>'>
                  <c:out value="${priority}"/>
                </option>
              </c:if>
            </c:forEach>
          </select>
        </div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left">
          <span><fmt:message key="group-label"/></span>
        </div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <select id="atg_service_ticketing_ticketForm_group" name="ticketQueueSelect" class="tickets">
            <option value=""></option>
            <c:forEach items="${ticketingTools.ticketQueues}" var="ticketQueue">
              <c:if test="${ticketingFormHandler.ticketQueue.id == ticketQueue.id}">
                <option selected="selected" value="<c:out value="${ticketQueue.id}"/>"><c:out value="${ticketQueue.itemDisplayName}"/></option>
              </c:if>
              <c:if test="${ticketingFormHandler.ticketQueue.id != ticketQueue.id}">
                <option value="<c:out value="${ticketQueue.id}"/>"><c:out value="${ticketQueue.itemDisplayName}"/></option>
              </c:if>
            </c:forEach>
          </select>
        </div>
        <%--
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="pushable-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left">
          <input type="radio" name="pushable" value="true" <c:if test='${true == ticketingFormHandler.values.pushable}'> checked </c:if> /><label for="pushable"><fmt:message key="yes"/></label>
          <input type="radio" name="pushable" value="false" <c:if test='${false == ticketingFormHandler.values.pushable || empty ticketingFormHandler.values.pushable }'> checked </c:if>/><label for="pushable"><fmt:message key="no"/></label>
        </div>
        --%>
      </div>

      <div class="atg-csc-base-table-row atg-csc-base-table-hr-margin-one">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><span><fmt:message key="description-label"/></span></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two" colspan="3"><input type="text" maxlength="253" id="atg_service_ticketing_ticketForm_description" name="description" class="tickets" value='<c:out escapeXml="false" value="${ticketingFormHandler.values.description}"/>'/></div>
      </div>

      <div class="atg-csc-base-table-row atg-csc-base-table-hr"></div>

      <div class="atg-csc-base-table-row atg-csc-base-table-hr-margin-two">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="first-name-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two"><input type="text" maxlength="39" id="atg_service_ticketing_ticketForm_firstName" name="firstName" class="tickets" value='<c:out value="${ticketingFormHandler.customerInfoMap.firstName}"/>'/></div>
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="last-name-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two"><input type="text" maxlength="39" id="atg_service_ticketing_ticketForm_lastName" name="lastName" class="tickets" value='<c:out value="${ticketingFormHandler.customerInfoMap.lastName}"/>'/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="phone-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two"><input type="text" maxlength="253" id="atg_service_ticketing_ticketForm_phone" class="tickets" name="phone" value='<c:out value="${ticketingFormHandler.customerInfoMap.phone}"/>'/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="email-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two"><input type="text" class="tickets" id="atg_service_ticketing_ticketForm_email" maxlength="253" name="email" value='<c:out value="${ticketingFormHandler.customerInfoMap.email}"/>'/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="home-address-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two" colspan="3">
          <input type="text" maxlength="253" class="tickets" id="atg_service_ticketing_ticketForm_homeAddress" name="address" value='<c:out value="${ticketingFormHandler.customerInfoMap.address}"/>'/>
        </div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="city-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two" colspan="3">
          <input type="text" maxlength="253" class="tickets" id="atg_service_ticketing_ticketForm_city" name="city" value='<c:out value="${ticketingFormHandler.customerInfoMap.city}"/>'/>
        </div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="state-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <input type="text" maxlength="253" class="tickets atg-csc-base-length-six" id="atg_service_ticketing_ticketForm_state" name="state" value='<c:out value="${ticketingFormHandler.customerInfoMap.state}"/>'/>
        </div>
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="country-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <input type="text" maxlength="253" id="atg_service_ticketing_ticketForm_country" name="country" class="tickets" value='<c:out value="${ticketingFormHandler.customerInfoMap.country}"/>'/>
        </div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell formLabel atg-csc-base-spacing-two-left"><fmt:message key="postal-code-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-two">
          <input type="text" maxlength="253" id="atg_service_ticketing_ticketForm_postalCode" class="tickets atg-csc-base-length-six" name="postalCode" value='<c:out value="${ticketingFormHandler.customerInfoMap.postalCode}"/>'/>
        </div>
      </div>
    </form>
  </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummaryEditable.jsp#1 $$Change: 946917 $--%>
