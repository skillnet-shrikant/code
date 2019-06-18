<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customerMainPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dspel:page xml="true">

<div dojoType="dijit.layout.LayoutContainer" style="height: 100%;">

<div dojoType="dojox.layout.ContentPane" scriptHasHooks="true" parseOnLoad="true" layoutAlign="top" style="padding: 3px; height:100%">

<div id="mainTicketCustomerPanel" style="display: inline-block; overflow:scroll;width:100%;height:100%;">
  <dspel:include src="ticketCustomerMainPanel.jsp" otherContext="${UIConfig.contextRoot}"/>
</div>

</div>
    
</div>
    
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customerMainPanel.jsp#1 $$Change: 946917 $--%>
