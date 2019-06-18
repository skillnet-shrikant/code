<!-- ticketSearchPanel.jsp -->
<%--
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSearchPanel.jsp#2 $$Change: 1179550 $
    @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<jsp:useBean id="localeUtils" class="atg.core.i18n.LocaleUtils" scope="request"/>
<dspel:page xml="true">
<dspel:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler" />
  <dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>
  <dspel:importbean var="environmentTools" bean="/atg/svc/agent/environment/EnvironmentTools"/>  
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    <dspel:form id="ticketSearchForm" action="#" onsubmit="document.getElementById('quickViewButton').click();return false;" formid="ticketSearchForm">
      <dspel:input id="quickSearch" priority="-10" type="hidden" value="" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.search"/>
      <dspel:input type="hidden" name="currentPage" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.currentPage"/>
      <dspel:input type="hidden" name="sortProperty" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.sortField"/>
      <dspel:input type="hidden" name="sortDirection" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.sortDirection"/>
      <dspel:input name="parameterMap.id" type="hidden" value="" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.id"/>
  <div class="svc_ticketSearch atg-csc-base-background-light">
    <table class="w100p atg-csc-base-background-light" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="2"><span class="headerLabel"><fmt:message key="quick-search-label" /></span>
        <p class="ticketSearchDesc"><fmt:message key="quick-search-help"/></p></td>
      </tr>
      <tr>
        <td>
          <dspel:input type="text" id="quickViewTicketTextEntry" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.id" /> 
          &nbsp;&nbsp; 
          <input type="button" id="quickViewButton" name="quickViewButton" onclick="if (document.getElementById('quickViewTicketTextEntry').value=='') return false;atg.service.ticketing.search.ticketRefreshGrid()"
             value='<fmt:message key="view-label"/>'/>
         </td>
      </tr>
    </table>
    <script type="text/javascript">
  //<![CDATA[
    _container_.onLoadDeferred.addCallback(function() {getElt("quickViewTicketTextEntry").focus()});
  //]]>
  </script>
    <table
      class="w100p atg-csc-base-background-light ticketAdvancedSearch"
      cellpadding="0"
      cellspacing="0">
      <tr>
        <td
          colspan="4"
          class="w10p" onclick="toggle('ticketSearchAdvanced', 'advSearchArrow');">
          <dspel:img
            id="advSearchArrow"
            src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif"
            width="14" height="14" />
          <span class="headerLabel">
            <fmt:message key="advanced-search-label" />
          </span>
        </td>
      </tr>
    </table>
    <div id="ticketSearchAdvanced" style="display: none;">
      <div class="atg-csc-base-table atg-csc-base-background-light">
        <div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="status-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:select
                id="atg_service_ticketing_ticketSearchAdvanced_status"
                name="statusSelect"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.subStatus_subStatusName">
                  <dspel:option value=''>&nbsp;</dspel:option>
                <c:forEach var="status" items="${ticketingFormHandler.statusValues}">
                  <dspel:option value='${status}'><fmt:message key="${status}" /></dspel:option>
                </c:forEach>
              </dspel:select>
            </div>
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="group-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:select
                id="atg_service_ticketing_ticketSearchAdvanced_group"
                name="ticketQueueSelect"
                iclass="tickets"
                bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.ticketQueue_id">
                  <dspel:option value=''>&nbsp;</dspel:option>
                <c:forEach
                  items="${ticketingTools.ticketQueues}"
                  var="ticketQueue">
                    <dspel:option value="${ticketQueue.id}">
                      <c:out value="${ticketQueue.itemDisplayName}" />
                    </dspel:option>
                </c:forEach>
              </dspel:select>
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="escalation-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:select
                id="atg_service_ticketing_ticketSearchAdvanced_escalationLevel"
                name="escalationSelect"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.escalationLevel">
                  <dspel:option value=''>&nbsp;</dspel:option>
                  <dspel:droplet name="/atg/dynamo/droplet/PossibleValues">
                    <dspel:param name="repository" value="${ticketingTools.ticketingManager.ticketingRepository}" />
                    <dspel:param name="itemDescriptorName" value="ticket" />
                    <dspel:param name="propertyName" value="escalationLevel" />
                    <dspel:param name="returnValueObjects" value="true" />
                    <dspel:oparam name="output">
                      <dspel:getvalueof var="values" param="values" />
                      <c:forEach items="${values}" var="option">
                        <dspel:option value="${option.settableValue}">${fn:escapeXml(option.localizedLabel)}</dspel:option>
                      </c:forEach>
                    </dspel:oparam>
                  </dspel:droplet>
              </dspel:select>
            </div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left" colspan="2">
              &nbsp;
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">&nbsp;</div>
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">&nbsp;</div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="first-name-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:input
                id="atg_service_ticketing_ticketSearchAdvanced_firstName"
                type="text"
                name="firstName"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.customerDetails_firstName"/>
            </div>
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="last-name-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:input
                id="atg_service_ticketing_ticketSearchAdvanced_lastName"
                type="text"
                name="lastName"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.customerDetails_lastName" />
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="phone-label" /></div>
            <div  class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:input
                id="atg_service_ticketing_ticketSearchAdvanced_phone"
                type="text"
                name="phone"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.customerDetails_phone" />
            </div>
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              &nbsp;
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="email-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              <dspel:input
                id="atg_service_ticketing_ticketSearchAdvanced_email"
                type="text"
                name="email"
                iclass="tickets"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.customerDetails_email" />
            </div>
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five">
              &nbsp;
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="description-label" /></div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five" colspan="3">
              <dspel:input
                id="atg_service_ticketing_ticketSearchAdvanced_description"
                type="text"
                name="description"
                iclass="tickets"
                style="width: 90%;" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.description" />
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div colspan="4" class="atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell"><fmt:message key="assigned-agent-label"/></div>
            <div colspan="3" class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-five">
              <dspel:select
                id="atg_service_ticketing_ticketSearchAdvanced_assignedAgent"
                name="agentSelect"
                iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.owningAgentId">
                  <dspel:option value=''>&nbsp;</dspel:option>
                  <c:forEach
                    items="${ticketingTools.allAgents}"
                    var="agent">
                      <dspel:tomap var="agentMap" value="${agent}"/>
                      <c:set var="login" value=""/>
                      <c:set var="firstName" value=""/>
                      <c:set var="lastName" value=""/>
                      <c:if test="${agentMap.login != null}"><c:set var="login" value="${agentMap.login}"/></c:if>
                      <c:if test="${agentMap.firstName != null}"><c:set var="firstName" value="${agentMap.firstName}"/></c:if>
                      <c:if test="${agentMap.lastName != null}"><c:set var="lastName" value="${agentMap.lastName}"/></c:if>
                      <c:choose>
                        <c:when test="${(lastName != null && not empty lastName) || (firstName != null && not empty firstName)}">
                          <fmt:message var="agentName" key="agent-full-name">
                            <fmt:param value="${login}"/>
                            <fmt:param value="${firstName}"/>
                            <fmt:param value="${lastName}"/>
                          </fmt:message>
                        </c:when>
                        <c:otherwise>
                          <c:set var="agentName" value="${login}"/>
                        </c:otherwise>
                      </c:choose>
                      <dspel:option value="${agentMap.id}">
                        <c:out value="${agentName}" />
                      </dspel:option>
                  </c:forEach>
              </dspel:select>
            </div>
          </div>
          <div class="atg-csc-base-table-row">
            <div class="formLabel atg-csc-base-spacing-two-left atg-csc-base-table-cell">&nbsp;</div>
            <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-five" colspan="3">&nbsp;</div>
          </div>
        </div>
      </div>

      <table width="75%">
        <tr>
          <td>
            <fieldset>
              <legend class="fGray">
                <dspel:input type="checkbox" id="byCreatedDate" name="byCreatedDate" value="true"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_byCreatedDate" />
                <label for="byCreatedDate"><fmt:message key="created-date-label" /></label>
              </legend>
              <div class="atg-csc-base-table">
                <div class="atg-csc-base-table-row">
                  <div class="atg-csc-ticket-table-cell-padding formLabel atg-csc-base-table-cell atg-csc-ticket-table-cell-width-six">
                    <dspel:input
                      id="atg_service_ticketing_ticketSearchAdvanced_createdDate_radio_past"
                      name="pastOrFromTo"
                      type="radio"
                      value="past" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_pastOrFromTo" onclick="atg.service.ticketing.updateCheckBox('byCreatedDate')"/>
                    &nbsp; <fmt:message key="in-the-past-label" />&nbsp;
                    <dspel:select id="atg_service_ticketing_ticketSearchAdvanced_createdDate_past" name="past"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_past" onchange="atg.service.ticketing.updateCheckBox('byCreatedDate')">
                      <dspel:option value=""></dspel:option>
                      <dspel:option value="day"><fmt:message key="day"/></dspel:option>
                      <dspel:option value="week"><fmt:message key="week"/></dspel:option>
                      <dspel:option value="month"><fmt:message key="month"/></dspel:option>
                      <dspel:option value="quarter"><fmt:message key="quarter"/></dspel:option>
                      <dspel:option value="year"><fmt:message key="year"/></dspel:option>
                    </dspel:select>
                  </div>
                  <div class="formLabel atg-csc-base-table-cell">
                    <dspel:input
                      id="atg_service_ticketing_ticketSearchAdvanced_createdDate_radio_date"
                      name="pastOrFromTo"
                      type="radio"
                      value="fromTo" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_pastOrFromTo" onclick="atg.service.ticketing.updateCheckBox('byCreatedDate')"/>
                    &nbsp;
                  </div>
                  <div class="atg-csc-base-table-cell"><fmt:message key="from-label" /></div>
                  <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-six">
                    <dspel:getvalueof var="datePattern" bean="LocaleTools.userFormattingLocaleHelper.datePatterns.short" scope="request" />
                    <dspel:input 
                      type="hidden" 
                      name="fromDateHidden" 
                      id="fromDateHidden" 
                      iclass="tickets" 
                      bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_fromDate"
                      converter="date"
                      date="${datePattern}"/> 
                    <input 
                      type="text" 
                      id="fromDate" 
                      name="fromDate" 
                      dojoType="dijit.form.DateTextBox"
                      maxlength="10"
                      class="tickets" 
                      constraints="{datePattern:'${datePattern}'}"
                      onchange="dojo.byId('fromDateHidden').value = dojo.byId('fromDate').value;atg.service.ticketing.updateCheckBox('byCreatedDate');"/>
                    <img
                        id="tsFromDateImg"
                        src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                        width="16"
                        height="16"
                        border="0"
                        title="<fmt:message key='tooltip.selectDate'/>"
                        onclick="dojo.byId('fromDate').focus()"/>
                  </div>
                </div>
                <div class="atg-csc-base-table-row">
                  <div class="atg-csc-ticket-table-cell-padding formLabel atg-csc-base-table-cell atg-csc-ticket-table-cell-width-six">&nbsp;</div>
                  <div class="atg-csc-base-table-cell"></div>
                  <div class="formLabel atg-csc-base-table-cell"><fmt:message key="to-label" /></div>
                  <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-six">
                    <dspel:input
                      type="hidden"
                      name="toDateHidden"
                      id="toDateHidden"
                      iclass="tickets"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_toDate"
                      converter="date"
                      date="${datePattern}" />
                    <input 
                      type="text"
                      id="toDate" 
                      name="toDate"
                      dojoType="dijit.form.DateTextBox"
                      maxlength="10"
                      class="tickets" 
                      constraints="{datePattern:'${datePattern}'}"
                      onchange="dojo.byId('toDateHidden').value = dojo.byId('toDate').value;atg.service.ticketing.updateCheckBox('byCreatedDate');"/>
                    <img
                      id="tsToDateImg"
                      src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                      width="16"
                      height="16"
                      border="0"
                      title="<fmt:message key='tooltip.selectDate'/>"
                      onclick="dojo.byId('toDate').focus()"/>
                  </div>
                </div>
              </div>
              <br/>
            </fieldset>
          </td>
        </tr>

        <tr>
          <td>
            <fieldset>
              <legend class="fGray">
                <dspel:input type="checkbox" id="byLastModified" name="byLastModified" value="true"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_byLastModified"/>
                <label for="byLastModified"><fmt:message key="last-modified-label" /></label>
              </legend>
              <div class="atg-csc-base-table">
                <div class="atg-csc-base-table-row">
                  <div class="atg-csc-ticket-table-cell-padding formLabel atg-csc-base-table-cell atg-csc-ticket-table-cell-width-six">
                    <dspel:input
                      id="atg_service_ticketing_ticketSearchAdvanced_lastModified_radio_past"
                      name="pastOrFromTo2"
                      type="radio"
                      value="past" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_pastOrFromTo2" onclick="atg.service.ticketing.updateCheckBox('byLastModified');"/>
                    &nbsp; <fmt:message key="in-the-past-label" />&nbsp;
                    <dspel:select id="atg_service_ticketing_ticketSearchAdvanced_lastModified_past" name="past2"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_past2" onchange="atg.service.ticketing.updateCheckBox('byLastModified');">
                      <dspel:option value=""></dspel:option>
                      <dspel:option value="day"><fmt:message key="day"/></dspel:option>
                      <dspel:option value="week"><fmt:message key="week"/></dspel:option>
                      <dspel:option value="month"><fmt:message key="month"/></dspel:option>
                      <dspel:option value="quarter"><fmt:message key="quarter"/></dspel:option>
                      <dspel:option value="year"><fmt:message key="year"/></dspel:option>
                    </dspel:select>
                  </div>
                  <div class="formLabel atg-csc-base-table-cell">
                    <dspel:input
                      id="atg_service_ticketing_ticketSearchAdvanced_lastModified_radio_date"
                      name="pastOrFromTo2"
                      type="radio"
                      value="fromTo"  bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_pastOrFromTo2" onclick="atg.service.ticketing.updateCheckBox('byLastModified');"/>
                    &nbsp;
                  </div>
                  <div class="atg-csc-base-table-cell"><fmt:message key="from-label" /></div>
                  <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-six">
                    <dspel:input
                      type="hidden"
                      name="modifiedFromHidden"
                      id="modifiedFromHidden"
                      iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_modifiedFrom"/>
                    <input 
                      type="text"
                      id="modifiedFrom" 
                      name="modifiedFrom"
                      dojoType="dijit.form.DateTextBox"
                      maxlength="10"
                      class="tickets" 
                      constraints="{datePattern:'${datePattern}'}"
                      onchange="dojo.byId('modifiedFromHidden').value = dojo.byId('modifiedFrom').value;atg.service.ticketing.updateCheckBox('byLastModified');"/>
                    <img
                      id="tsModifiedFromImg"
                      src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                      width="16"
                      height="16"
                      border="0"
                      title="<fmt:message key='tooltip.selectDate'/>"
                      onclick="dojo.byId('modifiedFrom').focus()"/>
                  </div>
                </div>
                <div class="atg-csc-base-table-row">
                  <div class="atg-csc-ticket-table-cell-padding formLabel atg-csc-base-table-cell atg-csc-ticket-table-cell-width-six">&nbsp;</div>
                  <div class="atg-csc-base-table-cell"></div>
                  <div class="formLabel atg-csc-base-table-cell">
                    <fmt:message key="to-label" />
                  </div>
                  <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-six">
                    <dspel:input
                      type="hidden"
                      name="modifiedToHidden"
                      id="modifiedToHidden"
                      iclass="tickets" bean="/atg/svc/ui/formhandlers/TicketSearchFormHandler.parameterMap.dates_modifiedTo" />
                    <input 
                      type="text"
                      id="modifiedTo" 
                      name="modifiedTo"
                      dojoType="dijit.form.DateTextBox"
                      maxlength="10"
                      class="tickets" 
                      constraints="{datePattern:'${datePattern}'}"
                      onchange="dojo.byId('modifiedToHidden').value = dojo.byId('modifiedTo').value;atg.service.ticketing.updateCheckBox('byLastModified');"/>
                    <img
                      id="tsModifiedToImg"
                      src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                      width="16"
                      height="16"
                      border="0"
                      title="<fmt:message key='tooltip.selectDate'/>"
                      onclick="dojo.byId('modifiedTo').focus()"/>
                  </div>
                </div>
              </div>
              <br/>
            </fieldset>
          </td>
        </tr>
        <tr>
          <td align="right">
            <input type="button" id="advSearchButton" name="advSearchButton"
              onclick="atg.service.ticketing.getPastOrFromToValue();atg.service.ticketing.getPastOrFromTo2Value();atg.service.ticketing.search.ticketRefreshGrid();"
              value='<fmt:message key="view-label"/>'/>
          </td>
        </tr>
      </table>
    </div>
  </div>
  </dspel:form>
<script type="text/javascript">
  dojo.require("dijit.form.DateTextBox");

  dojo.provide("atg.service.ticket.search");
  atg.service.ticket.search.validate = function () {
    if (atg.service.form.isFormEmpty('ticketSearchForm')) {
      dojo.byId('ticketSearchForm').quickViewButton.disabled = true;
      dojo.byId('ticketSearchForm').advSearchButton.disabled = true;
    }
    else {
      dojo.byId('ticketSearchForm').quickViewButton.disabled = false;
      dojo.byId('ticketSearchForm').advSearchButton.disabled = false;
    }
  };
  _container_.onLoadDeferred.addCallback(function () {
    if (atg.service.form.isFormEmpty('ticketSearchForm')) {
      dojo.byId('ticketSearchForm').quickViewButton.disabled = true;
      dojo.byId('ticketSearchForm').advSearchButton.disabled = true;
    }
    atg.service.form.watchInputs('ticketSearchForm', atg.service.ticket.search.validate);
    atg.keyboard.registerDefaultEnterKey("quickViewTicketTextEntry","quickViewButton");
    atg.keyboard.registerDefaultEnterKey("ticketSearchForm","advSearchButton");
  });
  _container_.onUnloadDeferred.addCallback(function () {
    atg.service.form.unWatchInputs('ticketSearchForm');
    atg.keyboard.unRegisterDefaultEnterKey("quickViewTicketTextEntry");
    atg.keyboard.unRegisterDefaultEnterKey("ticketSearchForm");
  });


  atg.service.ticketing.init_ticketSearchForm();


</script>
  </dspel:layeredBundle>
</dspel:page>

<!-- end ticketSearchPanel.jsp -->

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSearchPanel.jsp#2 $$Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSearchPanel.jsp#2 $$Change: 1179550 $--%>
