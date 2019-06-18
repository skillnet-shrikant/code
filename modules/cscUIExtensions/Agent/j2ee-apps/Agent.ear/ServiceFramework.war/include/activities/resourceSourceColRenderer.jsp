<%@  include file="/include/top.jspf" %>

<%--
   Renders a value from a resource file. The resource key
   is specified in sourcePropertyName.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <c:if test="${ ! empty activityInfo.sourceIcon }">
      <dspel:img src="${UIConfig.contextRoot}${activityInfo.sourceIcon}" width="21" height="21" align="absmiddle"/>
    </c:if>
    <c:if test="${ empty activityInfo.sourceIcon }">
      <div class="atgServiceFrameworkClearIcon"></div>  
    </c:if>
    <fmt:message key="${activityInfo.sourcePropertyName}"/>
  </dspel:layeredBundle>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/resourceSourceColRenderer.jsp#1 $$Change: 946917 $--%>
