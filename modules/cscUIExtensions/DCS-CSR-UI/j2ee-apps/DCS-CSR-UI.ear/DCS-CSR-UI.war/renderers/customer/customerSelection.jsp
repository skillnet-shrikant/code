<%--
 This page defines the Customer Selection Popup
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/customer/customerSelection.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/CustomerSearch">
      <jsp:attribute name="setPageData">
      </jsp:attribute>
    <jsp:body>
      <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}"/>
    </jsp:body>
    </csr:renderer>
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/CustomerSearchResults">
      <jsp:attribute name="setPageData">
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="selectLinkPanelStack" value="['globalPanels', 'cmcShoppingCartPS']" />
        </dsp:include>
      </jsp:body>
    </csr:renderer>
    <div align="right">
      <input type="button" onclick="atg.commerce.csr.common.hidePopupWithReturn('atg_commerce_csr_catalog_customerSelectionPopup')" value="<fmt:message key='cart.customerSelection.close'/>"/>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/customer/customerSelection.jsp#1 $$Change: 946917 $--%>
