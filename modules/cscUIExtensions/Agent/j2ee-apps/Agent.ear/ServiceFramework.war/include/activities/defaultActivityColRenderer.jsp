<%@  include file="/include/top.jspf" %>

<%--
   Renders the activity column with the data from the property 
   named in the activityPropertyName property of ActivityInfo.
  
   The property name may contain dot notation (as of 2005-09-01)
   thanks to a change to DynamicBeanMap.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultActivityColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <fmt:message key="${activityInfo.displayNameResourceKey}"/>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultActivityColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultActivityColRenderer.jsp#1 $$Change: 946917 $--%>
