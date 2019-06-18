<!-- ticketHistoryPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
<div id="result">

<%--   implementation will be done after beta1 release

        <dspel:select name="filter" class="tickets">
          <dspel:option>All Activities</dspel:option>
        </dspel:select>
--%>

        <a href="#" class="buttonSmall go" title="<fmt:message key='refresh-label'/>"> 
          <span><fmt:message key='filter-label'/></span> 
        </a> 
<hr>
<dspel:include src="/panels/ticketHistoryListPanel.jsp" otherContext="${UIConfig.contextRoot}"/>
</div> 
</dspel:layeredBundle>
</dspel:page>
<!-- end ticketHistoryPanel.jsp -->
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketHistoryPanel.jsp#1 $$Change: 946917 $--%>
