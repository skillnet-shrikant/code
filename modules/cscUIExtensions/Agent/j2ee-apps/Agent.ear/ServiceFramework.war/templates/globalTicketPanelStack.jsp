<%--
 Used To generate the global ticket context panel. 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTicketPanelStack.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <c:set value="${framework.panelStackInstances[panelStackId]}"
           var="panelStackInstance"/>
           
    <c:forEach items="${panelStackInstance.panelTargetElements}"
               var="panelTargetElement">
      <fw-beans:panelDefinitionFindByPanelId appId="workspace"
                                              panelId="${panelTargetElement.panelId}"
                                              var="currentPanelDefinition"/>
      <%-- Panel instance state --%>
      <c:set value="${framework.panelInstances[panelTargetElement.panelId]}"
             var="panelInstance"/>
             
             

      <c:if test="${currentPanelDefinition.tabHolderYn or not panelInstance.tabbedYn}">
        <!-- Panel ID: <c:out value='${currentPanelDefinition.panelId}'/> -->
        <div dojoType="dojox.layout.ContentPane"
          id="${panelTargetElement.targetElementId}"
          style="background-color:#fff;overflow:hidden;"
          parseOnLoad="true" 
          executeScripts="true"
          scriptHasHooks="true" 
          cacheContent="false"
          adjustPaths="false"
          extractContent="false"
          layoutAlign="client">
          <dspel:include src="${currentPanelDefinition.templates.panelTemplate.url}"
                         otherContext="${currentPanelDefinition.otherContext}">
            <dspel:param name="contentUrl" value="${currentPanelDefinition.contentUrl}"/>
            <dspel:param name="otherContext" value="${currentPanelDefinition.otherContext}"/>
            <dspel:param name="panelId" value="${panelTargetElement.panelId}"/>
            <dspel:param name="panelStackId" value="${panelStackId}"/>
            <dspel:param name="titleKey" value="${currentPanelDefinition.titleKey}"/>
          </dspel:include>
        </div>
      </c:if>
    </c:forEach>
  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTicketPanelStack.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTicketPanelStack.jsp#1 $$Change: 946917 $--%>
