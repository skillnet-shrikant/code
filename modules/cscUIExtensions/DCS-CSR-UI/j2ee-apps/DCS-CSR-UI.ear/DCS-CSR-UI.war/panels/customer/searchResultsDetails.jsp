<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/searchResultsDetails.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<div class="atg_svc_gridDetail">

<dsp:include src="/panels/customer/customerProfileSummaryHeader.jsp" otherContext="agent" />

<dsp:include src="/panels/customer/ticketHistoryShort.jsp" otherContext="agent">
  <dsp:param name="rangeNumTickets" value="3"/>
  <dsp:param name="rangeStartIndex" value="1"/>
</dsp:include>

<dsp:include src="/panels/customer/orderHistoryShort.jsp" otherContext="${CSRConfigurator.contextRoot}">
  <dsp:param name="rangeNumOrders" value="3"/>
  <dsp:param name="rangeStartIndex" value="1"/>
</dsp:include>

</div>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/searchResultsDetails.jsp#1 $$Change: 946917 $--%>
