<%@  include file="/include/top.jspf" %>

<%--
   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <c:if test="${ ! empty activityInfo.options['profileColIcon'] }">
      <dspel:img src="${UIConfig.contextRoot}${activityInfo.options['profileColIcon']}" width="21" height="21" align="absmiddle"/>
    </c:if>
  <%--
    <c:if test="${ empty activityInfo.sourceIcon }">
      <dspel:img src="${UIConfig.contextRoot}/image/clear.gif" width="21" height="21"/>
    </c:if>
  --%>
    <dspel:droplet name="/atg/targeting/RepositoryLookup">
      <dspel:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository"/>
      <dspel:param name="itemDescriptor" value="user"/>
      <dspel:param name="id" value="${activity[activityInfo.activityPropertyName]}"/>
      <dspel:oparam name="output">
        <dspel:tomap var="user" param="element"/>
        <fmt:message key="${activityInfo.options.profileResourceKey}">
          <fmt:param value="${fn:escapeXml(user.firstName)}"/>
          <fmt:param value="${fn:escapeXml(user.lastName)}"/>
        </fmt:message>
      </dspel:oparam>
    </dspel:droplet>
  </dspel:layeredBundle>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileColRenderer.jsp#1 $$Change: 946917 $--%>
