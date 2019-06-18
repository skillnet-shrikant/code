<!-- ticketAllTicketsPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketAllTicketsPanel.jsp#2 $$Change: 1179550 $
    @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<jsp:useBean id="localeUtils" class="atg.core.i18n.LocaleUtils" scope="request"/>
<dspel:page xml="true">
<dspel:importbean bean="/atg/core/i18n/LocaleTools"/>
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <div id="resultBox">
    <dspel:form style="display:none" action="#" id="agentAllTicketsViewTicketForm" formid="agentAllTicketsViewTicketForm">
      <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
      <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    </dspel:form>
    <dspel:importbean
      scope="request"
      var="agentAllTicketsSearchFormHandler"
      bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler" />

    <%-- Importing TicketSearchFormHandler to get the filters populated --%>
    <dspel:importbean
      scope="request"
      var="ticketingFormHandler"
      bean="/atg/svc/ui/formhandlers/TicketingFormHandler" />
  	
  	<dspel:importbean var="ticketingTools" bean="/atg/svc/agent/ticketing/TicketingTools"/>

    <%-- Shows the filters --%>
    <dspel:form id="searchAllTicketForm" formid="searchAllTicketForm" action="#">
      <dspel:input name="search" type="hidden" value="" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.search" priority="-10"/>
      <dspel:input name="idfield" type="hidden" value="" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.id"/>
      <dspel:input name="treeTableId" type="hidden" value="agentAllTicketsSearchResultsTable" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.treeTableId"/>
      <dspel:input name="parameters" type="hidden" value="" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameters"/>
      <dspel:input name="state" type="hidden" value="" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.state"/>
      <dspel:input name="operation" type="hidden" value="refresh" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.operation"/>
      <table border="0" cellSpacing="0" cellPadding="0" class="collapseBorders" width="100%">
        <tr>
          <td valign="top">
            <table border="0" cellSpacing="0" cellPadding="5" class="collapseBorders">
              <tr valign="top">
                <td class="formLabel" align="top">
                  <fmt:message key="group-label" />
                </td>
                <td class="w20p" align="top">
                  <dspel:select id="groupInput" name="groupInput" iclass="tasks"
                                bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.ticketQueuesType">
                    <dspel:option value=""><fmt:message key="all"/></dspel:option>
                    <c:forEach items="${ticketingTools.ticketQueues}" var="ticketQueue">
                      <dspel:option value="${ticketQueue.id}">
                        <c:out value="${ticketQueue.itemDisplayName}" />
                      </dspel:option>
                    </c:forEach>
                  </dspel:select>
                </td>
              </tr>
              <tr>
                <td class="formLabel" align="top">
                  <fmt:message key="assignee-label" />
                </td>
                <td class="w20p" align="top">
                  <dspel:select id="assignInput" name="assignInput" iclass="tasks"
                                bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.assignStatusType">
                    <dspel:option value=""><fmt:message key="all"/></dspel:option>
                    <dspel:option value="Assigned"><fmt:message key="assigned"/></dspel:option>
                    <dspel:option value="notAssigned" selected="${true}"><fmt:message key="unassigned"/></dspel:option>
                  </dspel:select>
                </td>
              </tr>
              <tr>
                <td class="formLabel" align="top">
                  <fmt:message key="escalation-label" />
                </td>
                <td class="w20p" align="top">
                  <dspel:select id="escalationInput" name="escalationInput" iclass="tasks"
                                bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.escalationType">
                    <dspel:option value=""><fmt:message key="all"/></dspel:option>
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
                </td>
              </tr>
            </table>
          </td>
          <td valign="top">
            <table border="0" cellSpacing="0" cellPadding="5" class="collapseBorders">
              <tr valign="top">
                <td class="formLabel" align="top">
                  <fmt:message key="status-label" />
                </td>
                <td class="w20p" align="top">
                  <dspel:select id="statusInput" name="statusInput" iclass="tasks"
                                bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.statusType">
                    <dspel:option value=""><fmt:message key="all"/></dspel:option>
                    <c:forEach var="status" items="${ticketingFormHandler.statusValues}">
                      <c:if test="${status == 'Open'}">
                        <dspel:option value="${status}" selected="${true}">
                          <c:out value="${status}" />
                        </dspel:option>
                      </c:if>
                      <c:if test="${status != 'Open'}">
                        <dspel:option value="${status}">
                          <c:out value="${status}" />
                        </dspel:option>
                      </c:if>
                    </c:forEach>
                  </dspel:select>
                </td>
              </tr>
              <tr valign="top">
                <td class="formLabel" align="top">
                  <fmt:message key="channel-label" />
                </td>
                <td class="w20p" align="top">
                  <dspel:select id="channelInput" name="channelInput" iclass="tasks"
                                bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.channelType">
                    <dspel:option value=""><fmt:message key="all"/></dspel:option>
                    <c:forEach var="channel" items="${ticketingFormHandler.creationChannels}">
                      <dspel:option value="${channel}">
                        <c:out value="${channel}" />
                      </dspel:option>
                    </c:forEach>
                  </dspel:select>
                </td>
              </tr>
              <tr valign="top">
                <td>&nbsp;</td>
              </tr>
            </table>
          </td>
          <td valign="top">
            <table border="0" cellSpacing="0" cellPadding="0" class="collapseBorders">
              <tr>
                <td class="w7p">
                  <fieldset>
                    <legend class="fGray">
                      <fmt:message key="duedate-label" />
                    </legend>
                    <table>
                      <tr>
                        <dspel:getvalueof var="datePattern" bean="LocaleTools.userFormattingLocaleHelper.datePatterns.short" scope="request" />
                        <td><fmt:message key="date.from.label" /></td>
                        <td>
                          <dspel:input type="hidden" id="atsFromDateInputHidden" name="atsFromDateInputHidden" size="5" iclass="date"
                                     converter="date" date="${datePattern}"  bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.fromDateType"/>
                          <input 
                            type="text" 
                            id="atsFromDateInput" 
                            name="atsFromDateInput" 
                            maxlength="10"
                            dojoType="dijit.form.DateTextBox"
                            constraints="{datePattern:'${datePattern}'}"
                            onchange="dojo.byId('atsFromDateInputHidden').value = dojo.byId('atsFromDateInput').value;"/>
                          <img 
                            id="atsFromDateImg" 
                            src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>" 
                            width="16" 
                            height="16" 
                            border="0" 
                            title="<fmt:message key='tooltip.selectDate'/>"
                            onclick="dojo.byId('atsFromDateInput').focus()"/>
                        </td>
                      </tr>
                      <tr>
                      <td align="right"><fmt:message key="date.to.label" /></td>
                      <td>
                        <dspel:input type="hidden" id="atsToDateInputHidden" name="atsToDateInputHidden" size="5" iclass="date"
                                converter="date" date="${datePattern}" bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler.parameterMap.toDateType"/>
                        <input 
                            type="text" 
                            id="atsToDateInput" 
                            name="atsToDateInput" 
                            maxlength="10"
                            dojoType="dijit.form.DateTextBox"
                            constraints="{datePattern:'${datePattern}'}"
                            onchange="dojo.byId('atsToDateInputHidden').value = dojo.byId('atsToDateInput').value;"/>
                        <img 
                            id="atsToDateImg" 
                            src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>" 
                            width="16" 
                            height="16" 
                            border="0" 
                            title="<fmt:message key='tooltip.selectDate'/>"
                            onclick="dojo.byId('atsToDateInput').focus()"/>
                      </td>
                    </tr>
                    </table>
                  </fieldset>
                </td>
              </tr>
            </table>
          </td>
          <td valign="bottom">
            <table class="w98p" cellspacing="0" cellpadding="0">
              <tr>
                <td>&nbsp;</td>
              </tr>
              <tr>
                <td><%-- Display a Refresh button --%>
                  <a href="#"
                    onclick="agentSearchAllTickets();"
                    class="buttonSmall"
                    title='<fmt:message key="view-label"/>'>
                    <span>
                      <fmt:message key="view-label" />
                    </span>
                  </a>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </dspel:form>
    <%-- Results panel container - hide while loading --%>

    <div id="searchAllResultsPanelContainer"><%-- Restore the tree table state container --%>

      <svc-ui:insertControlBar
        controlBarId="agentAllTicketSearchResultsControlBar"
        treeTableId="agentAllTicketsSearchResultsTable" /> <%-- Loading screen for table --%>

    <%-- Results table container - hide while paging --%>

      <div id="ticketSearchAllResultsTableContainer"><%-- Search results table --%>
        <svc-ui:insertTreeTable
          actionId="agentSearchAllTickets"
          hasPaging="true"
          hasHeader="true"
          initialUrl="/include/noSearch.jsp"
          pageSize="10"
          stateSavingMethod="server"
          treeTableBean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler"
          treeTableId="agentAllTicketsSearchResultsTable"
          width="100%">
          <svc-ui:initialSort defaultSortDirection="ascending" sortField="id" />
          <svc-ui:insertBody
            items="${agentAllTicketsSearchFormHandler.viewItems}"
            varItem="result" itemKey="${result.id}" />
        </svc-ui:insertTreeTable>
      </div>
    </div>
  </div>

<%--
  <script type="text/javascript">
    <svc-ui:executeOperation operationName="refresh" treeTableId="agentAllTicketsSearchResultsTable"/>
  </script>
--%>
</dspel:layeredBundle>
</dspel:page>
<%-- end ticketAllTicketsPanel.jsp --%>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketAllTicketsPanel.jsp#2 $$Change: 1179550 $--%>
