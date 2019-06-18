<%@  include file="/include/top.jspf" %>

<%--
  Parameters in page scope
  --------------------
  activity        The to-mapped activity item
  activityItem    The activity repository item
  activityInfo    The ActivityInfo object for this activity type

  The ticket to which this activity belongs is available via
  ${activity.ticket}

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/noteDetailPage.jsp#2 $$Change: 1192807 $
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
              <dt>
                <c:choose>
                  <c:when test="${ activity['public'] }">
                    <fmt:message key="activity.publicNote"/> 
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="activity.privateNote"/> 
                  </c:otherwise>
                </c:choose>
              </dt>
              <dd>
                <c:choose>
                  <c:when test="${! empty activity.textContent}">
                    <c:out escapeXml="true" value="${activity.textContent}"/>
                  </c:when>
                  <c:when test="${! empty activity['abstract']}">
                    <c:out escapeXml="true" value="${activity['abstract']}"/>
                  </c:when>
                  <c:otherwise>
                    <c:out escapeXml="true" value="${activity.textContent}"/>
                  </c:otherwise>
                </c:choose>
              </dd>
            </dl>
          </div>
        </div>
      </td>
    </tr>
  </dspel:layeredBundle>
</dspel:page>


<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/noteDetailPage.jsp#2 $$Change: 1192807 $--%>
