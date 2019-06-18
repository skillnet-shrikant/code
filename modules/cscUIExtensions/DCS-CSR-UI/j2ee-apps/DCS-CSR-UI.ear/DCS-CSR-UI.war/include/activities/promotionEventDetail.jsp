<%@ include file="/include/top.jspf" %>

<%--
  A detail page for profile updates. This page lists change to a
  profile for a ProfileUpdate activity.

  Parameters in page scope
  --------------------
  activity        The to-mapped activity item
  activityItem    The activity repository item
  activityInfo    The ActivityInfo object for this activity type

  The ticket to which this activity belongs is available via
  ${activity.ticket}

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/activities/promotionEventDetail.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<dsp:page xml="true">
  <dsp:layeredBundle basename="${activityInfo.resourceBundleName}">
  <%@ include file="/include/activities/activityPromotionLookup.jspf"%>
  <%@ include file="/include/activities/promotionEventActivityDefaultDetail.jspf"%>
  </dsp:layeredBundle>
</dsp:page>

<!-- $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/activities/promotionEventDetail.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/activities/promotionEventDetail.jsp#1 $$Change: 946917 $--%>