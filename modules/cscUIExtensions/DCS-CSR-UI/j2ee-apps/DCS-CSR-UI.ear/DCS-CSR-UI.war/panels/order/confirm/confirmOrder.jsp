<%--
This file displays confirmation details about the order. The following sections
are shown

Order Confirmation Number - the order number.
Send Order Confirmation - send a confirmation email.
Create New Account - save the customer profile.
Start New Order - start a new order for the current customer.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="userShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ConfirmationInfo"  var="confirmationInfo"/>

  <dsp:importbean bean="/atg/commerce/custsvc/repository/CustSvcRepositoryItemServlet"/>
  <dsp:importbean bean="/atg/userprofiling/ActiveCustomerProfile" var="activeCustomerProfile"/>
  <dsp:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler" var="CustomerProfileFormHandler" />
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnsDataHolder" var="returnsDataHolder"/>
  
  <dsp:getvalueof var="order" value="${confirmationInfo.order}"/>
  
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    
  <dsp:droplet name="/atg/commerce/custsvc/approvals/order/IsOrderPendingApprovalDroplet">
    <dsp:param name="orderId" value="${order.id}"/>
    <dsp:param name="elementName" value="approval"/>
    <dsp:oparam name="true">
      <dsp:getvalueof var="approval" param="approval"/>
      <dsp:tomap var="approvalMap" param="approval"/>
      
    <%@ include file="/include/order/confirm/approvalRequiredConfirm.jspf"%>
    
    <%@ include file="/include/order/confirm/sendApprovalConfirm.jspf"%>
    </dsp:oparam>
    <dsp:oparam name="false">
    <%@ include file="/include/order/confirm/viewOrder.jspf"%>
    
    <%@ include file="/include/order/confirm/sendConfirm.jspf"%>
    </dsp:oparam>
  </dsp:droplet>

  
  <%@ include file="/include/order/confirm/createNewProfile.jspf"%>
  
  <dsp:getvalueof param="panelStackId" var="currentPanel"/>
  <%/* only the standard confirm stack shows this option*/ %>
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param value="${currentPanel}" name="value"/>
  <dsp:oparam name="cmcConfirmOrderPS">
    <%@ include file="/include/order/confirm/startNewOrder.jspf"%>
  </dsp:oparam>
  </dsp:droplet>
 
<%--#########  Order Confirmation end  #########################--%>
  </dsp:layeredBundle>

</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/confirmOrder.jsp#1 $$Change: 946917 $--%>
