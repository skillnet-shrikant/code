<%--
 This page defines the existing order panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/existingOrder.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    
    <dsp:include src="/panels/order/finish/existingOrderView.jsp" otherContext="${CSRConfigurator.contextRoot}">      
    </dsp:include>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/existingOrder.jsp#1 $$Change: 946917 $--%>
