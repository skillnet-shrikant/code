<%@  include file="/include/top.jspf" %>

<%--
   Accepts a possibly comma delimited list of properties
   of the activity object, displays those properties in
   the order they're listed.

   Property may be specified with "dot" notation, for 
   example: "owningAgent.firstName,owningAgent.lastName"

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <c:if test="${ ! empty activityInfo.sourceIcon }">
    <dspel:img src="${UIConfig.contextRoot}${activityInfo.sourceIcon}" width="21" height="21" align="absmiddle"/>
  </c:if>
  <c:if test="${ empty activityInfo.sourceIcon }">
    <div class="atgServiceFrameworkClearIcon"></div>  
  </c:if>
  <c:forEach var="property" items="${activityInfo.sourcePropertyName}">
    <c:out value="${activity[property]}"/>
  </c:forEach>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultSourceColRenderer.jsp#1 $$Change: 946917 $--%>
