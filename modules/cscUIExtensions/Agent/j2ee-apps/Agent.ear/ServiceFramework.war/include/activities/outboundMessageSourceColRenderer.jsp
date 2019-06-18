<%@  include file="/include/top.jspf" %>

<%--
Renders the source column for OutboundMessage activity type in Ticketing Activity table.
   
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<dspel:page xml="true">
  <span class="textLeft_iconRight">
    <dspel:img src="${UIConfig.contextRoot}/${activityInfo.sourceIcon}" width="21" height="21" alt="Agent" align="absmiddle"/>
    <dspel:tomap var="agent" value="${activity.agentProfile}"/>
    <c:out value="${agent.firstName} ${agent.lastName}"/>
  </span>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/outboundMessageSourceColRenderer.jsp#1 $$Change: 946917 $--%>
