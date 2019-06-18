<%--
 Customer returnable items
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsHistory.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@  include file="/include/top.jspf"%>

<dsp:page xml="true">
<div style="height:100%">
<dsp:getvalueof var="viewProfileId" bean="/atg/userprofiling/ServiceCustomerProfile.repositoryId"/>
  <dsp:include src="/panels/customer/purchasedItemsHistorySearchResults.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
  <dsp:param name="profileId" value="${viewProfileId}"/>
  </dsp:include>
   
</div>
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/purchasedItemsHistory.jsp#1 $$Change: 946917 $--%>
