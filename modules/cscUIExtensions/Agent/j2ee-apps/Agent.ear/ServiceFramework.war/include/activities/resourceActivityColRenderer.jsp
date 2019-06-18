<%@  include file="/include/top.jspf" %>

<%--
   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceActivityColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <fmt:message key="${activityInfo.activityPropertyName}"/>
  </dspel:layeredBundle>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceActivityColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceActivityColRenderer.jsp#1 $$Change: 946917 $--%>
