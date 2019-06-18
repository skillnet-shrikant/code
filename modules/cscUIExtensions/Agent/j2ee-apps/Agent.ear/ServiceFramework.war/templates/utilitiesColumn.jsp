<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:getvalueof var="sidePanelStackId" param="sidePanelStackId"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <div id="column3" tabindex="0" class="atg_keyboard_top_level_identifier" dojoType="dijit.layout.LayoutContainer" layoutAlign="right">
      <div class="columnHeader" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="top">
        <c:if test="${not empty sidePanelStackId}">
          <fw-beans:panelStackDefinitionFindByPanelStackId appId="workspace" panelStackId="${sidePanelStackId}"
                                                            var="panelStackDefinition"/>
          <a href="#" class="collapse" onclick="frameworkCloseSidebar()">C</a>
          <h2 class="header2"><fmt:message key="${panelStackDefinition.titleKey}"/></h2>
        </c:if>
      </div>

      <div class="columnContent" id="sidebarColumn" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="client"
                  executeScripts="true"
                  scriptHasHooks="true"
                  cacheContent="false"
                  adjustPaths="false"
                  extractContent="false"
                  onclick="frameworkOpenSidebar()">
        <%-- Do this with dojo.addOnLoad below --%>
      </div>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/utilitiesColumn.jsp#1 $$Change: 946917 $--%>
