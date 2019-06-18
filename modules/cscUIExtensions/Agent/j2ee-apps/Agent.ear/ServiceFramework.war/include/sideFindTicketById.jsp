<%--
  Page fragment for finding ticket by id
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/sideFindTicketById.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>

<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <dspel:importbean var="modManager" bean="/atg/svc/configuration/ModuleDependencyManager" />
    <div parseWidgets="false">
      <svc-ui:getOptionAsBoolean var="CCOD" optionName="CallCenterMode" />
      <c:if test="${CCOD}">
      <dspel:droplet name="HasAccessRight">
        <dspel:param name="accessRight" value="ticketsTab"/>
        <dspel:oparam name="accessGranted">
          <div>
            <form name="OPTBID" action="#" id="OPTBID" class="atg-csc-base-table">
              <div class="atg-csc-base-table-row">
                <div class="atg-csc-base-table-cell">
                  <label class="ticketLabel" nowrap="true">
                    <fmt:message key="sidePanel.openById.ticket.label"/>
                  </label>
                  <input type="text"
                     id="atg_next_steps_find_by_id_ticket_input"
                     name="OPBIDTicketText"
                     class="atg_navigationHighlight inputTxtField" style="max-width:70px !important;margin-right: 5px;"
                     onkeydown="eventTicketFind(event);"/>
                </div>
                <div class="atg-csc-base-table-cell">
                  <input type="button" value="<fmt:message key='sidePanel.openById.ticket.findButton.label'/>"
                     class="atg_next_steps_find_by_id_button"
                     id="atg_next_steps_find_by_id_ticket_button"
                     onclick="openByIdTicket(escape(document.getElementById('OPTBID').OPBIDTicketText.value));"/>
                </div>
              </div>
            </form>
          </div>
         </dspel:oparam>
      </dspel:droplet>
      </c:if>
   </div>
 </dspel:layeredBundle>
</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/sideFindTicketById.jsp#1 $$Change: 946917 $--%>
