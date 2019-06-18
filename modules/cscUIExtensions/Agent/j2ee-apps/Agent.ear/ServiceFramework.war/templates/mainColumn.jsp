<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:getvalueof var="contentPanelStackId" param="contentPanelStackId"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <div id="column1" tabindex="0" class="atg_keyboard_top_level_identifier" dojoType="dijit.layout.LayoutContainer" layoutAlign="client">
      <div class="columnHeader" id="contentHeader" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="top"
          executeScripts="true"
          scriptHasHooks="true"
          cacheContent="false"
          adjustPaths="false"
          extractContent="false">
      </div>
      <div class="columnContent" id="contentColumn" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="client"
           executeScripts="true"
           scriptHasHooks="true"
           cacheContent="false"
           adjustPaths="false"
           extractContent="false"
           onLoad="(dijit.byId('contentColumn').containerNode || dijit.byId('contentColumn').domNode).scrollTop=0;">
      </div>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/mainColumn.jsp#1 $$Change: 946917 $--%>
