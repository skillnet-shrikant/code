<%--
 Used To generate the global ticket context panel. 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTabsStack.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>  
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- Global Content Area Navigation --%>
  <div id="gcaNavigation" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="top" style="z-index:50;overflow:hidden">
    <dspel:include src="/templates/globalContentArea.jsp" otherContext="${UIConfig.contextRoot}">
  </dspel:include>
  </div>

  <div dojoType="dojox.layout.ContentPane"
       id="globalTickets"
       parseOnLoad="true" 
       executeScripts="true"
       scriptHasHooks="true" 
       cacheContent="false"
       adjustPaths="false"
       extractContent="false"
       layoutAlign="top">
    <c:set value="${framework.panelStackInstances[panelStackId]}" var="panelStackInstance"/>

    <%-- Panel instance state --%>

    <c:set value="${framework.panelInstances[panelTargetElement.panelId]}"
           var="panelInstance"/>
    <!-- Panel ID: <c:out value='${currentPanelDefinition.panelId}'/> -->
    
    <c:forEach items="${panelStackInstance.panelTargetElements}"
      var="panelTargetElement">
      <fw-beans:panelDefinitionFindByPanelId appId="workspace"
         panelId="${panelTargetElement.panelId}"
         var="currentPanelDefinition"/>

      <%-- don't want to use otherContext here, as that gets messed up by the order tab.  Just use the same context that we're in now --%>
<%--      
      <dspel:include src="/templates/globalTabs.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="contentUrl" value="${currentPanelDefinition.contentUrl}"/>
        <dspel:param name="otherContext" value="${currentPanelDefinition.otherContext}"/>
        <dspel:param name="panelId" value="${panelTargetElement.panelId}"/>
        <dspel:param name="panelStackId" value="${panelStackId}"/>
        <dspel:param name="titleKey" value="${currentPanelDefinition.titleKey}"/>
      </dspel:include>
--%>

    </c:forEach> 
  </div>

  
  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTabsStack.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalTabsStack.jsp#1 $$Change: 946917 $--%>
