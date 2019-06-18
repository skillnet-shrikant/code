
<%@  include file="/include/top.jspf" %>

<%--
   Renders the abstract of an activity.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageAbstractRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <c:out value="${activity.reason}"/>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageAbstractRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageAbstractRenderer.jsp#1 $$Change: 946917 $--%>
