<%--
 Used To generate the global ticket context panel. 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTabs.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean var="callState" bean="/atg/svc/agent/environment/CallState"/>  
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="ticketId" bean="/atg/svc/ticketing/TicketHolder.currentTicket.id"/>

  
  
<%-- What is the pre-defined default state of the panel? --%>
<fw-beans:panelStackDefinitionFindByPanelStackId appId="workspace"
                                                 panelStackId="${panelStackId}"
                                                 var="panelStackDefinition"/>
                                                  
<fw-beans:panelDefinitionFindByPanelId appId="workspace"
                                       panelId="${panelId}"
                                       var="panelDefinition"/>
                                       
<%-- What is the current state of the panel for the user? --%>
<c:set value="${framework.panelInstances[panelDefinition.panelId]}" var="panelInstance"/>
<%-- Which panel is the selected tab? --%>
<c:set value="${panelInstance.currentPanelId}" var="selectedPanelId"/>

<%-- Which content URL to include in the panel? --%>
<c:set value="${panelDefinition.contentUrl}" var="contentUrl"/>
<c:set value="${panelDefinition.otherContext}" var="otherContext"/>

<%-- Panel template --%>
<c:set var="strings" value="atg.svc.agent.WebAppResources" />
<c:if test="${not empty panelDefinition.resourceBundle}"><c:set var="strings" value="${panelDefinition.resourceBundle}"/></c:if>

<dspel:layeredBundle basename="${strings}">
  <c:if test="${panelDefinition.tabHolderYn or not panelInstance.tabbedYn}">
    <dspel:form style="display:none" action="#" id="globalViewTicketForm" formid="globalViewTicketForm">
      <dspel:input type="hidden" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket" priority="-10"/>
      <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
    </dspel:form>
    <dspel:form style="display:none" action="#" id="globalViewCustomerForm" formid="globalViewCustomerForm">
      <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.changeUser"/>
      <dspel:input type="hidden" name="customerId" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.customerId"/>
      <dspel:input type="hidden" name="viewMode" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.viewMode"/>
    </dspel:form> 
       
    <div tabindex="0" class="atg_keyboard_top_level_identifier">
      <ul>
        <c:choose>
          <c:when test="${panelInstance.panelId eq selectedPanelId}">
            <c:set var="className" value="current"/>
            <c:set value="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');" var="onClickHandler"/>
          </c:when>
          <c:otherwise>
            <c:set var="className" value=""/>
            <c:if test="${!panelInstance.contentOpenYn}">
              <c:set value="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');atg.service.framework.selectTabbedPanel('${panelInstance.panelId}');atg.service.framework.cancelEvent(event);" var="onClickHandler"/>
            </c:if>
            <c:if test="${panelInstance.contentOpenYn}">
              <c:set value="atg.service.framework.selectTabbedPanel('${panelInstance.panelId}');atg.service.framework.cancelEvent(event);" var="onClickHandler"/>
            </c:if>
          </c:otherwise>
        </c:choose>
        
        <li id="${panelDefinition.panelId}" title="<fmt:message key="${panelDefinition.titleKey}"/>" class="atg_navigationHighlight ${className}"
            onclick="${onClickHandler}">
          <div class="tabLabel"><fmt:message key="${panelDefinition.titleKey}"/></div>
          <c:if test="${not empty panelDefinition.templates.linkTemplate}">
            <dspel:include src="${panelDefinition.templates.linkTemplate.url}" 
                           otherContext="${panelDefinition.templates.linkTemplate.otherContext}">
              <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
              <dspel:param name="panelStackId" value="${panelStackId}"/>
              <dspel:param name="otherContext" value="${panelDefinition.templates.linkTemplate.otherContext}"/>
              <dspel:param name="resourceBundle" value="${panelDefinition.templates.linkTemplate.resourceBundle}"/>
              <dspel:param name="ticketId" value="${ticketId}"/>
            </dspel:include>
          </c:if>
        </li>
        <c:forEach items="${panelInstance.tabbedPanelIds}" var="tabbedPanelId">
          <fw-beans:panelDefinitionFindByPanelId appId="workspace" panelId="${tabbedPanelId}"
                                                 var="tabbedPanelDefinition"/>
                                                  
          <c:choose>
            <c:when test="${tabbedPanelId eq selectedPanelId}">
              <c:set var="className" value="current"/>
              <c:set value="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');" var="onClickHandler"/>
            </c:when>
            <c:otherwise>
              <c:set var="className" value=""/>
              <c:if test="${!panelInstance.contentOpenYn}">
                <c:set value="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');atg.service.framework.selectTabbedPanel('${tabbedPanelId}');atg.service.framework.cancelEvent(event);" var="onClickHandler"/>
              </c:if>
              <c:if test="${panelInstance.contentOpenYn}">
                <c:set value="atg.service.framework.selectTabbedPanel('${tabbedPanelId}');atg.service.framework.cancelEvent(event);" var="onClickHandler"/>
              </c:if>
            </c:otherwise>
          </c:choose>  

          <c:set var="tabStrings" value="atg.svc.agent.WebAppResources" />
          <c:if test="${!empty tabbedPanelDefinition.resourceBundle}"><c:set var="tabStrings" value="${tabbedPanelDefinition.resourceBundle}"/></c:if>
          <dspel:layeredBundle basename="${tabStrings}">
            <li id="${tabbedPanelDefinition.panelId}" title="<fmt:message key='${tabbedPanelDefinition.titleKey}'/>" class="atg_navigationHighlight ${className}" 
                onclick="${onClickHandler}">
              <div class="tabLabel"><fmt:message key="${tabbedPanelDefinition.titleKey}" /></div>
              <c:if test="${not empty tabbedPanelDefinition.templates.linkTemplate}">
                <dspel:include src="${tabbedPanelDefinition.templates.linkTemplate.url}" 
                               otherContext="${tabbedPanelDefinition.templates.linkTemplate.otherContext}">
                  <dspel:param name="panelId" value="${tabbedPanelDefinition.panelId}"/>
                  <dspel:param name="panelStackId" value="${panelStackId}"/>
                  <dspel:param name="otherContext" value="${otherContext}"/>
                  <dspel:param name="resourceBundle" value="${tabbedPanelDefinition.templates.linkTemplate.resourceBundle}"/>
                  <dspel:param name="ticketId" value="${ticketId}"/>
                </dspel:include>
              </c:if>
            </li>
          </dspel:layeredBundle>
        </c:forEach>
        
        <li class="gcaActions">
          <div tabindex="0" class="atg_keyboard_top_level_identifier">
            <ul class="horizontalList">

             <li>
                <a id="globalContextAddNote" href="#" class="atg_navigationHighlight icon_addNote" title="<fmt:message key='globalTicketContext.addNote'/>"
                   onclick="atg.service.ticketing.addNotePrompt(); atg.service.framework.cancelEvent(event); return false;">
                </a>
              </li>
              <li>
                <a id="globalContextAddCallNote" href="#" class="atg_navigationHighlight icon_recordCall" title="<fmt:message key='globalTicketContext.addCallNote'/>"
                   onclick="atg.service.ticketing.addCallActivityPrompt(); atg.service.framework.cancelEvent(event);return false;">
                </a>
              </li>
 				<li>
                <c:choose>
                  <c:when test="${panelInstance.contentOpenYn}">
                    <a id="globalContextMinMaxTickets" href="#" class="atg_navigationHighlight icon_minimizeTickets" 
                       title="<fmt:message key='panels.tips.minimize'/>" 
                       onclick="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');">
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a id="globalContextMinMaxTickets" href="#" class="atg_navigationHighlight icon_maximizeTickets" 
                       title="<fmt:message key='panels.tips.maximize'/>" 
                       onclick="atg.service.framework.togglePanelContent('${panelDefinition.panelId}');">
                    </a>
                  </c:otherwise>
                </c:choose>
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </div>

  </c:if>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTabs.jsp#1 $$Change: 946917 $--%>
