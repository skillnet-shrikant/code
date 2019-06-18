<%@  include file="/include/top.jspf" %>
<%@ page import= "atg.core.util.StringUtils"   %>

<%--
   Renders from address for an inbound message. This is simlar to customerNameRenderer,
   but if the customer name is empty it should use the from from address of the message.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <c:choose>
    <c:when test="${ ! empty activity[ticket.user.lastName] }">
      <c:set var="customerName" value="${activity[ticket.user.firstName]} ${activity[ticket.user.lastName]}"/>
    </c:when>
    <c:otherwise>
      <c:set var="customerName" value="${activity[ticket.user.password]}"/>
    </c:otherwise>
  </c:choose>
	<dspel:img src="${UIConfig.contextRoot}${activityInfo.sourceIcon}" width="21" height="21" align="absmiddle"/>
    <c:choose>
      <c:when test="${ ! empty customerName }">
	    <c:out value="${customerName}"/>
      </c:when>
      <c:otherwise>
        <%-- <fmt:message key="response.message.unknown.label"/> --%>
        <c:out value="${activity[ticket.user.password]}"/>
      </c:otherwise>
     </c:choose>
  </dspel:layeredBundle>
     

</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/problematicMessageSourceColRenderer.jsp#1 $$Change: 946917 $--%>
