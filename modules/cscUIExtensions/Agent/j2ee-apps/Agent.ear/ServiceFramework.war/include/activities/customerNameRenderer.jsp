
<%@  include file="/include/top.jspf" %>

<%--
   Renders customer name. If the fullName property is not 
   empty, uses that. Otherwise concatenates firstName and
   and lastName.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/customerNameRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <c:choose>
    <c:when test="${ ! empty activity[customerDetails.fullName] }">
      <c:out value="${ activity[customerDetails.fullName] }"/>
    </c:when>
    <c:otherwise>
      <c:out value="${activity[customerDetails.firstName]} ${activity[customerDetails.lastName]}"/>
    </c:otherwise>
  </c:choose>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/customerNameRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/customerNameRenderer.jsp#1 $$Change: 946917 $--%>
