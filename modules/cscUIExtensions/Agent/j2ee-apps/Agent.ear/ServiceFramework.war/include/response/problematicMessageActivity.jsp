<%--

Activity detail display for an inbound message activity.
This JSP will be included in the Ticket Activity view when a user clicks the expand icon for an inbound message activity.

Expected params are:
activity       The to-mapped activity repository item
activityItem   The actual activity repository item
activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageActivity.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- Get reference to Message object to render--%>
  <svc-agent:getMessage activity="${activityItem}" var="msg"/>

  <%-- Create vars for the div IDs for hidden areas. These must be unique for each activity. --%>

  <div class="editArea">

    <!-- MESSAGE BODY -->
    <div class="viewMessage">
    	<svc-agent:convertTextToHtml var="convertedTextBody" text="${msg.rawMessageAsText}"/>
			<c:out value="${convertedTextBody}" escapeXml="false" />
    </div>
  </div>
  <!-- END MESSAGE BODY -->

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageActivity.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageActivity.jsp#1 $$Change: 946917 $--%>
