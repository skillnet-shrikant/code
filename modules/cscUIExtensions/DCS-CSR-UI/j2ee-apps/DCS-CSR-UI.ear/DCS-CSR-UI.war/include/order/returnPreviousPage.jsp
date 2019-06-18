<%--
Renders the link to return to the previous page

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="../top.jspf"%>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:getvalueof var="order" param="order"/>
<dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
<dsp:tomap var="customerProfileMap" value="${customerProfile}"/>
<dsp:getvalueof var="isProfileTransient" value="${customerProfile['transient']}"/>

<dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
<dsp:getvalueof var="returnToPage" bean="OriginatingPage.pageName"/>
<dsp:getvalueof var="returnToOrderId" bean="OriginatingPage.orderId"/>
<dsp:getvalueof var="returnToCustomerId" bean="OriginatingPage.customerId"/>
<c:choose>
  
  <c:when test="${returnToPage == 'customerInformation'}">
    <%-- Return to Customer Information Page --%> 
    <c:choose>
      <c:when test="${!isProfileTransient}">
        <a href="#" onclick="atg.commerce.csr.order.returnToCustomerInformationPage();return false;"><fmt:message key="link.returnTo.customerInfo" /></a>
      </c:when>
      <c:otherwise>
        <a href="#" onclick="viewCustomerFromSearch('${returnToCustomerId}');"><fmt:message key="link.returnTo.customerInfo" /></a>
      </c:otherwise>
    </c:choose> 
  </c:when>
  
  <c:when test="${returnToPage == 'orderView'}">
    <%-- Return to Order View Page --%>
    <a href="#" onclick="atg.commerce.csr.order.viewExistingOrder('${returnToOrderId}');return false;"><fmt:message key="link.returnTo.orderView" /></a>
  </c:when>
  <c:when test="${returnToPage == 'scheduledOrderView'}">
    <%-- Return to Scheduled Order View Page --%>
    <a href="#" onclick="atg.commerce.csr.order.viewExistingOrder ('${returnToOrderId}','TEMPLATE');return false;"><fmt:message key="link.returnTo.scheduledOrderView" /></a>
  </c:when>
  <c:when test="${returnToPage == 'orderSearch'}">
    <%-- Return to Order Search Page --%>
    <a href="#" onclick="atg.commerce.csr.order.returnToOrderSearchPage();"><fmt:message key="link.returnTo.orderSearchResults" /></a>
  </c:when>
  <c:when test="${returnToPage == 'approvals'}">
    <%-- Return to Approvals Page --%>
    <a href="#" onclick="atg.commerce.csr.openPanelStack('cmcApprovalsPS');"><fmt:message key="link.returnTo.approvals" /></a>
  </c:when>
  
</c:choose>

</dsp:layeredBundle> 
</dsp:page>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/returnPreviousPage.jsp#1 $$Change: 946917 $--%>
