
<%@  include file="/include/top.jspf" %>

<%--
   Renders the subject of an activity, assuming the 
   activity has a subject property. Falls back to the abstract if subject
   isn't available.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageSubjectRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <c:choose>
    <c:when test="${empty activity.subject}">
      <c:out value="${activity['abstract']}" escapeXml="false"/>
    </c:when>
    <c:otherwise>
      <c:out value="${activity.subject}" escapeXml="false"/>
    </c:otherwise>
  </c:choose>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageSubjectRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageSubjectRenderer.jsp#1 $$Change: 946917 $--%>
