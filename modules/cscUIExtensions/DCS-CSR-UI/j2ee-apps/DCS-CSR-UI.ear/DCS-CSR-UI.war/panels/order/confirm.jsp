<%--
 This page defines the confirm order/return/exchange panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

    <dsp:include src="/panels/order/confirm/confirmOrder.jsp" otherContext="${CSRConfigurator.contextRoot}">
    </dsp:include>

  </dsp:layeredBundle>
  <%-- This snippet is added to highlight the Products in the progress bar.. --%>
  <script type="text/javascript">
    atg.progress.update('cmcCustomerSearchPS');
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm.jsp#1 $$Change: 946917 $--%>
