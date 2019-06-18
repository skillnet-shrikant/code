<%--
 This page defines the customer selection popup
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/customerSelection.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/CustomerSelectionPopup">
      <jsp:attribute name="setPageData">
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}"/>
      </jsp:body>
    </csr:renderer>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/customerSelection.jsp#1 $$Change: 946917 $--%>
