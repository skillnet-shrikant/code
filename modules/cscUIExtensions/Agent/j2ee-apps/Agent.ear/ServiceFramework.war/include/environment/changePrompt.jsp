<%--
This file is for prompting before assigning to another agent.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/environment/changePrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
  <dspel:setLayeredBundle basename="atg.svc.agent.environment.EnvironmentResources"/>

  <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach"/>    
  <dspel:importbean bean="/atg/svc/agent/environment/EnvironmentTools"/>    
  <dspel:importbean var="changeState" bean="/atg/svc/agent/environment/EnvironmentChangeState"/>
  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:importbean var="ticketStatusManager" bean="/atg/ticketing/TicketStatusManager"/>
  <dspel:importbean var="ticketingManager" bean="/atg/ticketing/TicketingManager"/>

  <div class="atg_svc_popupPanel atg_svc_popupPanel_Ticket_Disposition">
    <dspel:droplet name="Switch">
      <dspel:param bean="EnvironmentChangeState.warnings" name="value"/>
      <dspel:oparam name="true">
        <h2>
          <fmt:message key="warnings.summary"/>
        </h2>
        <dspel:droplet name="ForEach">
          <dspel:param bean="EnvironmentChangeState.allWarnings" name="array"/>
          <dspel:oparam name="output">
            <p><dspel:valueof param="element"/></p>
          </dspel:oparam>
        </dspel:droplet>
      </dspel:oparam>
    </dspel:droplet>

      <dspel:form action="framework.jsp" method="POST" id="envChangeForm" name="envChangeForm">
        <dspel:droplet name="Switch">
          <dspel:param bean="EnvironmentChangeState.environmentChangeFormHandler.showDispositionPrompt" name="value"/>
          <dspel:oparam name="true">
          <h2>
            <fmt:message key="updateTicket">
              <fmt:param><dspel:valueof bean="EnvironmentTools.activeTicketId"/></fmt:param>
            </fmt:message>
          </h2>
          <dl>
           <dt>
              <fmt:message key="disposition.options"/>
            </dt>
            <dd>
              <dspel:input bean="EnvironmentChangeState.ticketDispositionOptions.dispositionOption" type="radio" name="dispoption" value="save"/><fmt:message key="keepActive"/><br>
            </dd>
            <dd>
             <dspel:input type="hidden" value="${ticketingManager.ticketStatusManager.closedSubStatusName}" name="subStatus" bean="EnvironmentChangeState.ticketDispositionOptions.subStatus"/>
             <dspel:input bean="EnvironmentChangeState.ticketDispositionOptions.dispositionOption" type="radio" name="dispoption" value="close"/><fmt:message key="closeTicket"/><br>
            </dd>

            <dl>
              <dt class="reason"><fmt:message key="reason"/></dt>
              <dd class="reason">
                <dspel:select bean="EnvironmentChangeState.ticketDispositionOptions.reasonCode">
                <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
                  <c:forEach items="${reasonCodes['Ticketing.ClosePrompt']}" var="reason">
                    <dspel:option value="${reason}">
                      <dspel:droplet name="/atg/ticketing/ActivityReasonDescription">
                        <dspel:param name="descriptionId" value="${reason}"/>
                        <dspel:param name="elementName" value="description"/>
                        <dspel:oparam name="output">
                          <dspel:getvalueof var="description" param="description"/>
                          <c:out value="${description}"/>
                        </dspel:oparam>
                      </dspel:droplet>
                    </dspel:option>
                  </c:forEach>
                </dspel:select>
              </dd>
            </dl>
      
            <dspel:droplet name="Switch">
              <dspel:param bean="EnvironmentTools.activeTicket.transient" name="value"/>
              <dspel:oparam name="true">
                <dspel:droplet name="/atg/ticketing/droplet/ShouldDiscardTicket">
                  <dspel:param name="immediately" value="false"/>
                  <dspel:param bean="EnvironmentTools.activeTicket" name="ticket"/>
                  <dspel:oparam name="output">
                    <dspel:droplet name="Switch">
                      <dspel:param param="isDiscardable" name="value"/>
                      <dspel:oparam name="true">
                        <dd>
                          <dspel:input bean="EnvironmentChangeState.ticketDispositionOptions.dispositionOption" type="radio" name="dispoption" value="discard"/>
                          <fmt:message key="discardPermanently">
                            <fmt:param><dspel:valueof bean="EnvironmentTools.activeTicketId"/></fmt:param>
                          </fmt:message>
                        </dd>
                      </dspel:oparam>
                    </dspel:droplet>
                  </dspel:oparam>
                </dspel:droplet>
              </dspel:oparam>
            </dspel:droplet>

            <dt><fmt:message key="note"/></dt>
            <dd>
              <dspel:textarea bean="EnvironmentChangeState.ticketDispositionOptions.ticketNote" name="ticketNote" cols="50" rows="5" style="width:97%;height:70px;"></dspel:textarea>
            </dd>
            <dd>
            <dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
             <dspel:input bean="EnvironmentChangeState.ticketDispositionOptions.publicNote" type="checkbox" name="publicNote" checked="true"  value="true"/><fmt:message key="share-with-customer-label"/>
             <dspel:setLayeredBundle basename="atg.svc.agent.environment.EnvironmentResources"/>
            </dd>
            </dl>    
          </dspel:oparam>
        </dspel:droplet>
        <input name="_windowid" value="<dspel:valueof param='_windowid'/>" type="hidden">
        <dspel:input bean="${changeState.changeFormHandlerPath}.${changeState.environmentChangeFormHandler.ticketDispositionChangeHandler}" type="hidden" priority="-10" value=""/>
        <dspel:input bean="${changeState.changeFormHandlerPath}.initChangeState" type="hidden" value="false"/>
        <dspel:input bean="${changeState.changeFormHandlerPath}.doWarnings" type="hidden" value="false"/>
        <dspel:input bean="${changeState.changeFormHandlerPath}.doTicketDispositionPrompt" type="hidden" value="false"/>
        <dspel:input bean="${changeState.changeFormHandlerPath}.successURL" type="hidden" beanvalue="EnvironmentChangeState.environmentChangeFormHandler.successURL"/>
        <dspel:input bean="${changeState.changeFormHandlerPath}.errorURL" type="hidden" beanvalue="EnvironmentChangeState.environmentChangeFormHandler.errorURL"/>
      </dspel:form>
    <div class="atg_svc_panelFooter">
      <a href="#"
         class="buttonSmall"
         id="warningsOk"
         onclick="atg.service.environment.acceptChangePrompt();return false;">
        <fmt:message key="ok"/>
      </a>
      &nbsp;
      <a href="#"
         class="buttonSmall" 
         id="warningsCancel" 
         onclick="atg.service.environment.cancelChangePrompt();return false;">
        <fmt:message key="cancel"/>
      </a>
    </div>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/environment/changePrompt.jsp#1 $$Change: 946917 $--%>
