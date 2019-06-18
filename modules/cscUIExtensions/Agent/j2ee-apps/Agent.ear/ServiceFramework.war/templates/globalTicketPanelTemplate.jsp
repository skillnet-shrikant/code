<%--
 Used To generate the global ticket context panel. 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTicketPanelTemplate.jsp#1 $$Change: 946917 $
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
     <dspel:form style="display:none" action="#" id="globalSaveTicketForm" formid="globalSaveTicketForm">
 <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.endEditTicket"/>
 <dspel:input type="hidden" value="" converter="map" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap"/>
    </dspel:form>
    <dspel:form style="display:none" action="#" id="globalViewCustomerForm" formid="globalViewCustomerForm">
      <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.changeUser"/>
      <dspel:input type="hidden" name="customerId" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.customerId"/>
      <dspel:input type="hidden" name="viewMode" value="" bean="/atg/svc/agent/ui/formhandlers/ServiceUIProfileFormHandler.viewMode"/>
    </dspel:form>
    

    <c:if test="${panelInstance.contentOpenYn and not empty selectedPanelId}">
        <fw-beans:panelDefinitionFindByPanelId appId="workspace" panelId="${selectedPanelId}"
                                                var="selectedPanelDefinition"/>
    <div dojoType="dojox.layout.ContentPane"
      id="globalTicketsContent"
      tabindex="0" 
      class="atg_keyboard_top_level_identifier globalTicketsCurrent"
      style="height: 100%; overflow:hidden;"
      executeScripts="true"
      scriptHasHooks="true" 
      parseOnLoad="true" 
      cacheContent="false"
      adjustPaths="false"
      extractContent="false"
      layoutAlign="client">
      <dspel:include src="${selectedPanelDefinition.contentUrl}" otherContext="${selectedPanelDefinition.otherContext}">
        <dspel:param name="panelId" value="${selectedPanelDefinition.panelId}"/>
        <dspel:param name="panelStackId" value="${panelStackId}"/>
        <dspel:param name="otherContext" value="${selectedPanelDefinition.otherContext}"/>
      </dspel:include>
    </div>
    </c:if>
  </c:if>

  <%-- Fire an event that this panel has loaded. Any interested parties can listen for this. --%>
  <script type="text/javascript" defer="defer">
    var panelLoadParams={
      panelId:"${selectedPanelId}",
      isOpen:${panelInstance.contentOpenYn}
    };
    dojo.publish("/agent/globalPanelLoaded",[panelLoadParams]);
  </script>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTicketPanelTemplate.jsp#1 $$Change: 946917 $--%>
