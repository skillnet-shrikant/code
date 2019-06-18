<%@ include file="/include/top.jspf" %>

<%-- 
	Martin Samm - 5/oct/2009
	This is a replacement for the one in /service/Agent
 	Added to stop JS errors.
--%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <div id="column2" dojoType="dijit.layout.LayoutContainer" layoutAlign="right">
      <div class="columnHeader" style="overflow-x: hidden;" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="top">
      </div>
	</div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/researchColumn.jsp#1 $$Change: 946917 $--%>
