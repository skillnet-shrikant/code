<%--
 This page defines the complete order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/complete.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

<%--
    $File: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/complete.jsp $<br/>

    <a dojoType="FrameworkLink"
       panelStack="cmcBillingPS"><fmt:message key='backto.billing'/></a>
    &nbsp;
    <a dojoType="FrameworkLink"
       panelStack="cmcConfirmOrderPS"><fmt:message key='goto.confirmOrder'/></a>
    &nbsp;
    <a dojoType="FrameworkLink"
       panelStack="cmcConfirmReturnPS"><fmt:message key='goto.confirmReturn'/></a>
    &nbsp;
    <a dojoType="FrameworkLink"
       panelStack="cmcConfirmExchangePS"><fmt:message key='goto.confirmExchange'/></a>

--%>

  <dsp:include src="/panels/order/finish/finishOrder.jsp" otherContext="${CSRConfigurator.contextRoot}">
  </dsp:include>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/complete.jsp#1 $$Change: 946917 $--%>
