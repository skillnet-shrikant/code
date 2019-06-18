<%@ include file="/include/top.jspf" %>

<%--
  A detail page for profile updates. This page lists change to a
  profile for a ProfileUpdate activity.

  Parameters in page scope
  --------------------
  activity        The to-mapped activity item
  activityItem    The activity repository item
  activityInfo    The ActivityInfo object for this activity type

  The ticket to which this activity belongs is availaber via
  ${activity.ticket}

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileUpdateDetail.jsp#2 $$Change: 1192807 $
@updated $DateTime: 2015/09/02 10:43:13 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="${activityInfo.resourceBundleName}">
    <tr class="inlineEditor">
      <td colspan="5">
        <div class="editArea">
          <div class="currentActivity">
            <h5><fmt:message key="profileUpdateDetail.changedProperties"/></h5>
            <div class="activityView">
              <dl class="status">
                <table>
                  <tr>
                    <td><fmt:message key="profileUpdateDetail.property"/></td>
                    <td><fmt:message key="profileUpdateDetail.oldValue"/></td>
                    <td><fmt:message key="profileUpdateDetail.newValue"/></td>
                  </tr>
                  <c:forEach var="changedItem" items="${activity.propertyUpdates}">
                    <dspel:tomap var="change" value="${changedItem}"/>
                    <tr>
                      <td>
                        <c:out escapeXml="true" value="${change['propertyName']}"/>
                      </td>
                      <td>
                      <c:if test="${change.updateType == 1}">
                      	<fmt:message key="profileUpdateDetail.removedItem"/>
                      </c:if>
                        <c:out escapeXml="true" value="${change['oldValue']}"/>
                      </td>
                      <td>
                        <c:if test="${change.updateType == 2}">
                      	<fmt:message key="profileUpdateDetail.addedItem"/>
                        </c:if>
                        <c:out escapeXml="true" value="${change['newValue']}"/>
                      </td>
                    </tr>
                  </c:forEach>
                </table>
              </dl>
            </div> <!-- activityView -->
          </div> <!-- currentActivity -->
        </div> <!-- editArea -->
      </td>
    </tr>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileUpdateDetail.jsp#2 $$Change: 1192807 $$DateTime: 2015/09/02 10:43:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/profileUpdateDetail.jsp#2 $$Change: 1192807 $--%>
