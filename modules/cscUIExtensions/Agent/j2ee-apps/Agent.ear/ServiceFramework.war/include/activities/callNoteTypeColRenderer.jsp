<%--
   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/callNoteTypeColRenderer.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@  include file="/include/top.jspf" %>


<dspel:page xml="true">
  <span>
    <%-- type icon --%>
    <c:if test="${not empty activityInfo.typeIcon}">
      <dspel:img src="${UIConfig.contextRoot}${activityInfo.typeIcon}" width="21" height="21" align="absmiddle" />
    </c:if>
    <c:if test="${empty activityInfo.typeIcon}">
      <div class="atgServiceFrameworkClearIcon"></div>  
    </c:if>
    <%-- direction icon --%>
    <c:choose>
      <c:when test="${activity.inbound}">
        <c:set var="dirIcon" value="${UIConfig.contextRoot}${activityInfo.options.inboundIcon}"/>
      </c:when>
      <c:otherwise>
        <c:set var="dirIcon" value="${UIConfig.contextRoot}${activityInfo.options.outboundIcon}"/>
      </c:otherwise>
    </c:choose>
    <dspel:img src="${dirIcon}" width="21" height="21" align="absmiddle"/>
  </span>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/callNoteTypeColRenderer.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/callNoteTypeColRenderer.jsp#1 $$Change: 946917 $--%>
