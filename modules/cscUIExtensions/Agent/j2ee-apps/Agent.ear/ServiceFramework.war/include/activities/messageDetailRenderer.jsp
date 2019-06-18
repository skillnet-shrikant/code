
<%@  include file="/include/top.jspf" %>

<%--
   Renders the subject of an activity, assuming the 
   activity has a subject property.

   Request scoped variables available:
   ----------------------------------
   activity       The to-mapped activity repository item
   activityItem   The actual activity repository item
   activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageDetailRenderer.jsp#2 $$Change: 1192807 $
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
                <fmt:message key="activity.subject">
                  <fmt:param value="${activity.subject}"/>
                </fmt:message>
              </dt>
              <dd>
                <c:out escapeXml="true" value="${activity.body}"/>
              </dd>
            </dl>
          </div>
        </div>
      </td>
    </tr>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageDetailRenderer.jsp#2 $$Change: 1192807 $$DateTime: 2015/09/02 10:43:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/activities/messageDetailRenderer.jsp#2 $$Change: 1192807 $--%>
