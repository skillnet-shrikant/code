<%-- This page defines the panel stack content
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelStackContent.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>

<dspel:page xml="true"><dspel:getvalueof var="panelStackId" param="panelStackId"/><dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
<c:if test="${not empty panelStackId}">
 <fw-beans:panelStackDefinitionFindByPanelStackId var="panelStackDefinition" appId="workspace" panelStackId="${panelStackId}"/>
 <c:set var="panelStackInstance" value="${framework.panelStackInstances[panelStackId]}"/>
 <c:forEach var="panelTargetElement" items="${panelStackInstance.panelTargetElements}">
  <fw-beans:panelDefinitionFindByPanelId var="currentPanelDefinition" appId="workspace" panelId="${panelTargetElement.panelId}"/>
  <%-- Panel instance state --%>
  <c:set var="panelInstance" value="${framework.panelInstances[panelTargetElement.panelId]}"/>
  <%-- Account for all rendered tabbed panels either as a tab holder or as a current tab of a tab holder 
       in order to render a tabbed panel when its tab holder is disabled --%>
  <c:if test="${currentPanelDefinition.tabHolderYn}"
    ><c:set scope="request" var="tabPanelTally" value="${tabPanelTally}|${currentPanelDefinition.panelId}"
    /><c:forEach items="${panelInstance.tabbedPanelIds}" var="tabPanelId"
      ><c:set scope="request" var="tabPanelTally" value="${tabPanelTally}|${tabPanelId}"
    /></c:forEach
  ></c:if
  ><c:set var="isUntalliedTabPanel" value="${false}"
  /><c:if test="${currentPanelDefinition.alwaysTabbedYn}"
    ><c:set var="isUntalliedTabPanel" value="${true}"
    /><c:set var="tabPanelArray" value="${fn:split(tabPanelTally, '|')}"
    /><c:forEach items="${tabPanelArray}" var="talliedTabPanel"
      ><c:if test="${talliedTabPanel == currentPanelDefinition.panelId}"
        ><c:set var="isUntalliedTabPanel" value="${false}"
      /></c:if
    ></c:forEach
  ></c:if
  ><c:if test="${currentPanelDefinition.tabHolderYn || ! panelInstance.tabbedYn || isUntalliedTabPanel}">
   <!-- Panel ID: ${currentPanelDefinition.panelId} -->
   <div dojoType="dojox.layout.ContentPane" parseOnLoad="true" 
        id="${(panelStackDefinition.errorPanelId != panelTargetElement.panelId) ? panelTargetElement.targetElementId : panelStackDefinition.errorPanelId}"
        style="display:${((! panelInstance.visibleYn) or (! panelInstance.panelOpenYn)) ? 'none' : 'block'};"
        executeScripts="true"
        scriptHasHooks="true" 
        cacheContent="false"
        adjustPaths="false"
        extractContent="false">
   </div>
  </c:if>
 </c:forEach>
</c:if>
</dspel:layeredBundle></dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelStackContent.jsp#1 $$Change: 946917 $--%>
