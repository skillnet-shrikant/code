<%--
 This page defines the product catalog panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productCatalog.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <div class="panelContent" id="___panelContent___">
      <div parseWidgets="false" class="atg_commerce_csr_browseTree">
        <dsp:include src="/include/catalog/navigationTree.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
      </div>
      <div class="atg_commerce_csr_coreContent atg_commerce_csr_productSearch" id="atg_commerce_csr_coreContent">
        <dsp:include src="/include/catalog/productCatalogContent.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
      </div>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productCatalog.jsp#1 $$Change: 946917 $--%>
