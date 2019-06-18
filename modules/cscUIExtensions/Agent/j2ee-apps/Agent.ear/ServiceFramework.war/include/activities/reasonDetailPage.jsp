<%@  include file="/include/top.jspf" %>

<%--
  Parameters in page scope
  --------------------
  activity        The to-mapped activity item
  activityItem    The activity repository item
  activityInfo    The ActivityInfo object for this activity type

  The ticket to which this activity belongs is available via
  ${activity.ticket}

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/reasonDetailPage.jsp#2 $$Change: 1192807 $
@updated $DateTime: 2015/09/02 10:43:13 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <dspel:tomap var="reason" value="${activity.reason}"/>
    <tr class="inlineEditor">
      <td colspan="5">
        <div class="editArea">
          <div class="currentActivity">
            <dl class="status">
              <dd>
                <fmt:message key="activity.reason"/><c:out escapeXml="true" value="${reason.reasonName}"/>
              </dd>
            </dl>
          </div>
        </div>
      </td>
    </tr>
    <tr>
    <td colspan="5">
        <div class="editArea">
          <div class="currentActivity">
            <dl class="status">
              <dd>
      <fmt:message key="note.label"/>
      <c:if test="${activity['public'] == true}"><fmt:message key="note.public"/></c:if>
      <c:if test="${activity['public'] == false}"><fmt:message key="note.notpublic"/></c:if>
      <c:out escapeXml="true" value="${activity.textContent}"/>
      </dd>
      </dl>
      </div>
      </div>
      </td>
    </tr>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/reasonDetailPage.jsp#2 $$Change: 1192807 $$DateTime: 2015/09/02 10:43:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/reasonDetailPage.jsp#2 $$Change: 1192807 $--%>
