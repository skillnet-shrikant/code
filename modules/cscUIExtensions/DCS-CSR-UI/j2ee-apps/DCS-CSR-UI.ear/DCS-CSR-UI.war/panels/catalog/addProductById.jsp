<%--
 This page defines the add product by ID panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/addProductById.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

  <dsp:importbean bean="atg/commerce/custsvc/catalog/AddProductsByIdConfigurator" var="addProductsByIdConfigurator"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="csrAgentTools"/>

  <c:set var="order" value="${shoppingCart.current}"/>

  <script type="text/javascript">
    _container_.onLoadDeferred.addCallback( function() {
      atg.commerce.csr.catalog.restoreAddProductByIdData('${addProductsByIdConfigurator.addProductByIdProductsFromWindowScope}');
    });

    _container_.onUnloadDeferred.addCallback(function () {
      atg.commerce.csr.catalog.storeAddProductByIdData();
    });
  </script>

    <div class="atg_commerce_csr_corePanelData">
      <div class="atg_commerce_csr_coreProductAddByID">

        <input type="hidden" id="atg_commerce_csr_catalog_tmpProductId" value=""/>
        <input type="hidden" id="atg_commerce_csr_catalog_tmpSkuId" value=""/>
        <input type="hidden" id="atg_commerce_csr_catalog_productNotFoundError" value="<fmt:message key="catalogBrowse.addProductsById.productNotFoundError"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_skuNotFoundError" value="<fmt:message key="catalogBrowse.addProductsById.skuNotFoundError"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_addProducts" value="<fmt:message key="catalogBrowse.productViewPopup.addProducts"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_edit" value="<fmt:message key="catalogBrowse.addProductsById.edit"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_editTooltip" value="<fmt:message key="catalogBrowse.addProductsById.editTooltip"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_editLineItem" value="<fmt:message key="catalogBrowse.addProductsById.editLineItem"/>"/>
        
        <input type="hidden" id="atg_commerce_csr_catalog_delete" value="<fmt:message key="catalogBrowse.addProductsById.delete"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_deleteTooltip" value="<fmt:message key="catalogBrowse.addProductsById.deleteTooltip"/>"/>
        <input type="hidden" id="atg_commerce_csr_catalog_dontStore" value="0"/>
        <input type="hidden" id="atg_commerce_csr_catalog_products" value=""/>

        <input type="hidden" id="atg_commerce_csr_catalog_showProductEntryField" value="${csrAgentTools.CSRConfigurator.useProductId}"/>
        <input type="hidden" id="atg_commerce_csr_catalog_showSKUEntryField" value="${csrAgentTools.CSRConfigurator.useSKUId}"/>

        <input type="hidden" id="atg_commerce_csr_catalog_isMultiSiteEnabled" value="${isMultiSiteEnabled}"/>

        <input type="hidden" id="atg_commerce_csr_catalog_quantityInputTagMaxlength" value="${csrAgentTools.CSRConfigurator.quantityInputTagMaxlength}"/>
        <input type="hidden" id="atg_commerce_csr_catalog_quantityInputTagSize" value="${csrAgentTools.CSRConfigurator.quantityInputTagSize}"/>

        <%-- support for currency formatting in JavaScript --%>
        <input type="hidden" id="atg_commerce_csr_catalog_activeCurrencyCode" value='<dsp:valueof value="${csrAgentTools.activeCustomerCurrencyCode}" />'/>

        <%-- support for currency formatting in JavaScript --%>
        <input type="hidden" id="atg_commerce_csr_catalog_currentOrderCurrencySymbol" value='<dsp:valueof value="${csrAgentTools.currentOrderCurrencySymbolInFormattingLocale}" />'/>

        <%-- support for currency number of decimal places formatting in JavaScript --%>
        <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
          <dsp:param name="currencyCode" value="${csrAgentTools.activeCustomerCurrencyCode}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
            </dsp:oparam>
        </dsp:droplet>

        <input type="hidden" id="atg_commerce_csr_catalog_activeCurrencyCodeNumberOfDecimalPlaces" value='<dsp:valueof value="${currencyDecimalPlaces}" />'/>

        <c:set var="isOrderModifiable" value="false"/>
        <c:if test="${!empty shoppingCart.originalOrder}">
          <dsp:droplet name="OrderIsModifiable">
            <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
            <dsp:oparam name="true">
              <c:set var="isOrderModifiable" value="true"/>
            </dsp:oparam>
            <dsp:oparam name="false">
              <c:set var="isOrderModifiable" value="false"/>
            </dsp:oparam>
          </dsp:droplet>
        </c:if>

<script type="text/javascript">
  if (!dijit.byId("atg_commerce_csr_catalog_atg_commerce_csr_catalog_productReadOnlyPopup")) {
    new dojox.Dialog({ id: "atg_commerce_csr_catalog_atg_commerce_csr_catalog_productReadOnlyPopup",
                       cacheContent: "false", 
                       executeScripts:"true",
                       scriptHasHooks: "true",
                       duration: 100,
                       "class":"atg_commerce_csr_popup"});
  }                     
</script>
        <c:url var="productReadOnlyURL" context="${CSRConfigurator.contextRoot}" value="/include/order/product/productReadOnly.jsp">
           <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
           <c:param name="productId" value=""/>
        </c:url>
        <input type="hidden" id="atg_commerce_csr_catalog_productReadOnly" value="<c:out value='${productReadOnlyURL}'/>"/>

<script type="text/javascript">
  if (!dijit.byId("atg_commerce_csr_catalog_productQuickViewPopup")) {
    new dojox.Dialog({ id:"atg_commerce_csr_catalog_productQuickViewPopup",
                       cacheContent:"false", 
                       executeScripts:"true",
                       scriptHasHooks:"true",
                       duration: 100,
                       "class":"atg_commerce_csr_popup"});
  }
</script>
        <c:url var="productQuickViewURL" context="${CSRConfigurator.contextRoot}" value="/include/order/product/productQuickView.jsp">
           <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
           <c:param name="productId" value=""/>
        </c:url>
        <input type="hidden" id="atg_commerce_csr_catalog_productQuickViewURL" value="<c:out value='${productQuickViewURL}'/>"/>

  <script type="text/javascript">
    if (!dijit.byId("editLineItemPopup")) {
      new dojox.Dialog({ id:"editLineItemPopup",
                       cacheContent:"false", 
                       executeScripts:"true",
                       scriptHasHooks:"true",
                       duration: 100,
                       "class": "atg_commerce_csr_popup"});
    }
  </script>
        <c:url var="productEditLineItemURL" context="${CSRConfigurator.contextRoot}" value="/include/order/editProductSKU.jsp">
           <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
           <c:param name="mode" value="return"/>
           <c:param name="skuId" value="SKUIDPLACEHOLDER"/>
           <c:param name="productId" value=""/>
        </c:url>
        <input type="hidden" id="atg_commerce_csr_catalog_productEditLineItem" value="<c:out value='${productEditLineItemURL}'/>"/>
        <c:url var="readProductJsonURL" context="${CSRConfigurator.contextRoot}" value="/panels/catalog/addProductByIdOnProduct.jsp">
           <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
        </c:url>
        <input type="hidden" id="atg_commerce_csr_catalog_readProductJsonURL" value="<c:out value='${readProductJsonURL}'/>"/>

        <dsp:form formid="addProductsByIdForm" style="display:block;" id="atg_commerce_csr_catalog_addProductsByIdForm">
          <table cellpadding="0" cellspacing="0" class="atg_dataTable" id="atg_commerce_csr_catalog_addProductsByIdTable">
            <thead>
              <tr id="atg_commerce_csr_catalog_addProductsByIdTr0">
                <c:if test="${csrAgentTools.CSRConfigurator.useProductId}">
                  <th scope="col" class="atg_numberValue">
                    <fmt:message key="catalogBrowse.addProductsById.id"/>
                  </th>
                </c:if>
                <c:if test="${csrAgentTools.CSRConfigurator.useSKUId}">
                  <th scope="col" class="atg_numberValue">
                    <fmt:message key="catalogBrowse.addProductsById.sku"/>
                  </th>
                </c:if>
                <th scope="col" class="atg_numberValue">
                  <fmt:message key="catalogBrowse.addProductsById.qty"/>
                </th>
                <th scope="col">
                  <fmt:message key="catalogBrowse.addProductsById.name"/>
                </th>
                <th scope="col">
                  <fmt:message key="catalogBrowse.addProductsById.status"/>
                </th>
                <th scope="col" class="atg_numberValue">
                  <fmt:message key="catalogBrowse.addProductsById.priceEach"/>
                </th>
                <th scope="col" class="atg_numberValue">
                  <fmt:message key="catalogBrowse.addProductsById.totalPrice"/>
                </th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
              </tr>
            </thead>

            <svc-ui:frameworkUrl var="url"/>
            <dsp:input bean="CartModifierFormHandler.addItemCount" name="atg_commerce_csr_catalog_addItemCount" type="hidden" value="1"/>
            <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL" type="hidden" value="${url}" />
            <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL" type="hidden" value="${url}" />
            <dsp:input bean="CartModifierFormHandler.addMulpitleItemsToOrder" type="hidden" value="" priority="-10"/>

          </table>
        </dsp:form>
        <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="1"/>
        <div style="display:none;" id="atg_commerce_csr_catalog_addToCartContainer"></div>
      </div>
      <div class="atg_commerce_csr_panelFooter">
        <input type="hidden" name="atg_commerce_csr_catalog_isOrderModifiableHiddenValue" id="atg_commerce_csr_catalog_isOrderModifiableHiddenValue" value="<c:out value='${isOrderModifiable}'/>"/>
        <input type="button" value="<fmt:message key='catalogBrowse.addProductsById.addToShoppingCart'/>" onclick="atg.commerce.csr.catalog.addProductsByIdToShoppingCart();" disabled="disabled" id="atg_commerce_csr_catalog_addToShoppingButton"/>
      </div>
    </div>    
    <script type="text/javascript">
      atg.progress.update('cmcCatalogPS');
      _container_.onLoadDeferred.addCallback( function() {
        atg.keyboard.registerFormDefaultEnterKey("atg_commerce_csr_catalog_addProductsByIdForm", "atg_commerce_csr_catalog_addToShoppingButton");      
      });
      _container_.onUnloadDeferred.addCallback( function() {
        atg.keyboard.unRegisterFormDefaultEnterKey("atg_commerce_csr_catalog_addProductsByIdForm");      
      });
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/addProductById.jsp#2 $$Change: 1179550 $--%>
