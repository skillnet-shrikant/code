<%--
 Submitted Order History
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTickets.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
<div style="height:100%">
<dsp:getvalueof var="viewOrderId" bean="atg/commerce/custsvc/order/ViewOrderHolder.current.id"/>
<dsp:include src="/panels/order/relatedTicketsSearchResults.jsp" otherContext="${CSRConfigurator.contextRoot}">
<dsp:param name="orderItemId" value="${viewOrderId}"/>
</dsp:include>
   
</div>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/relatedTickets.jsp#1 $$Change: 946917 $--%>
