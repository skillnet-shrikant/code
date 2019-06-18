<%@ include file="/include/top.jspf" %>

<%--
  The default activity detail page. This page renders one or
  more properties from the activity in the detail area. The
  list of properties to render is specified in the 'options' 
  map property of the ActivityInfo object, assocated with
  the 'detailPageProperties' key. For example, to render the
  activity's creationTime and its associated ticket's creation
  time, you would use:

    detailPageProperties=creationTime,,ticket.creationTime

  Note the use of commas in the example

  Parameters in page scope
  --------------------
  activity        The to-mapped activity item
  activityItem    The activity repository item
  activityInfo    The ActivityInfo object for this activity type

  The ticket to which this activity belongs is available via
  ${activity.ticket}

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultDetailPage.jsp#2 $$Change: 1192807 $
@updated $DateTime: 2015/09/02 10:43:13 $$Author: jsiddaga $

--%>

<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.ActivityInfoResources">
    <tr class="inlineEditor">
      <td colspan="5">
        <div class="editArea">
          <div class="currentActivity">
            <h5><fmt:message key="defaultDetailPage.activityDetails"/></h5>
            <dl class="status">
              <c:forEach var="propertyName" items="${activityInfo.options.detailPageProperties}">
                <strong>
                  <c:out escapeXml="true" value="${propertyName}"/>
                </strong> :
                <c:out escapeXml="true" value="${activity[propertyName]}"/>
                <br />
              </c:forEach>
            </dl>
          </div>
        </div>
      </td>
    </tr>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultDetailPage.jsp#2 $$Change: 1192807 $$DateTime: 2015/09/02 10:43:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/defaultDetailPage.jsp#2 $$Change: 1192807 $--%>
