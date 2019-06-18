<%--
  This page displays a link to go back to the edit appeasement panel stack.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/appeasement/appeasementEdit.jsp#1 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin ordersummary/appeasement/appeasementEdit.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="appeasement" bean="/atg/commerce/custsvc/order/ShoppingCart.appeasement" scope="request"/>
    <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current" scope="request"/>

    <dt>
      <a style="text-decoration: underline;" href="#" onclick="atg.commerce.csr.openPanelStack('cmcAppeasementsPS');return false;"><fmt:message key="orderSummary.appeasementEdit"/></a>
    </dt>
    <dd>
      &nbsp
    </dd>
  </dsp:layeredBundle>
</dsp:page>
<!-- ends ordersummary/appeasement/appeasementEdit.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/appeasement/appeasementEdit.jsp#1 $$Change: 1179550 $--%>
