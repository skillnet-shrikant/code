<%--
 This page defines the Customer Information Panel - read only
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerProfileSummaryHeader.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:getvalueof var="selectedCustomerId" param="customerId"/>

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <div class="atg_commerce_csr_hoverPopupTitle">
    <dspel:droplet name="/atg/userprofiling/CustomerLookup">
      <dspel:param name="id" param="customerId"/>
      <dspel:oparam name="output">
        <a href="#" class="blueU" title="<fmt:message key="view-customer"/>" onclick="viewCustomerFromSearch('<c:out value="${selectedCustomerId}"/>');" id="atg_commerce_csr_customer_viewMoreCustomerInfotheCustomerId">
        <dspel:valueof param="element.firstName"/>&nbsp;
        <dspel:valueof param="element.lastName"/></a>, 
        <dspel:valueof param="element.email"/>     
      </dspel:oparam>
    </dspel:droplet>
  </div>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/customerProfileSummaryHeader.jsp#1 $$Change: 946917 $--%>
