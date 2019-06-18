<%--
 This page defines the related orders panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirmUpdateSchedule.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

  <dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
  <dsp:setvalue bean="OriginatingPage.pageName" value=""/>

  <dsp:importbean bean="/atg/commerce/custsvc/order/ConfirmationInfo"  var="confirmationInfo"/>
  <dsp:importbean bean="/atg/svc/agent/ui/formhandlers/CustomerProfileFormHandler" var="CustomerProfileFormHandler" />

  <dsp:getvalueof var="order" value="${confirmationInfo.order}"/>
  <%@ include file="/include/order/confirm/schedule/viewUpdatedOrder.jspf"%>
  <%@ include file="/include/order/confirm/schedule/scheduleInfo.jspf"%>
  <%@ include file="/include/order/confirm/sendConfirm.jspf"%>
  <%@ include file="/include/order/confirm/createNewProfile.jspf"%>
  <%@ include file="/include/order/confirm/startNewOrder.jspf"%>
 
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirmUpdateSchedule.jsp#1 $$Change: 946917 $--%>
