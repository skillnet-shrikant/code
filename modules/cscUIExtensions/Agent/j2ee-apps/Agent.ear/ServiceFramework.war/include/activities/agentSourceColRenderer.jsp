<%@  include file="/include/top.jspf" %>

<%--

   Renders the agent's name from the owningAgent property of the
   activity, or noAgentIcon and (noAgentResource || noAgentProperty)

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/agentSourceColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <c:choose>
      <%-- 
        Display agent name and agent icon if an agent is associated
        with this activity
      --%>
      <c:when test="${ ! empty activity.agentProfile }">
        <dspel:img src="${UIConfig.contextRoot}/${activityInfo.sourceIcon}" 
          width="21" height="21" alt="<fmt:message key='activity.agent'/>" title="<fmt:message key='activity.agent'/>" align="absmiddle" />
        <c:forEach var="property" items="${activityInfo.sourcePropertyName}">
          <c:out value="${activity[property]}"/>
        </c:forEach>
      </c:when>
      <%-- 
        If no agent is associated with this activity, displsy the icon and
        resource string from the options map instead
      --%>
      <c:otherwise>
        <dspel:img src="${UIConfig.contextRoot}/${activityInfo.options.noAgentIcon}" 
          width="21" height="21" alt="<fmt:message key='activity.agent'/>" title="<fmt:message key='activity.agent'/>" align="absmiddle"/>
        <fmt:message key="${activityInfo.options.noAgentResource}"/>
      </c:otherwise>
    </c:choose>
  </dspel:layeredBundle>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/agentSourceColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/agentSourceColRenderer.jsp#1 $$Change: 946917 $--%>
