<%--
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResultsDetails.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:importbean bean="/atg/userprofiling/ServiceCustomerProfile" var="profile"/>

<div class="atg_svc_gridDetail">

<dspel:include src="/panels/customer/customerProfileSummaryHeader.jsp" otherContext="${UIConfig.contextRoot}"/>

<dspel:include src="/panels/customer/ticketHistoryShort.jsp" otherContext="${UIConfig.contextRoot}">
  <dspel:param name="rangeNumTickets" value="3"/>
  <dspel:param name="rangeStartIndex" value="1"/>
</dspel:include>

</div>

</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/searchResultsDetails.jsp#1 $$Change: 946917 $--%>
