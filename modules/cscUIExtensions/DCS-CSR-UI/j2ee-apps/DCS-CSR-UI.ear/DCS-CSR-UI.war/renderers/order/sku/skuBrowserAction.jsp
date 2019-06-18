<%--
 Renders the action bits of the SKU browser

 @renderInfo - The RenderInfo
 @product - The product

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuBrowserAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
      <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
      <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="productItem" param="product"/>
      <dsp:tomap var="product" value="${productItem}"/>
      <svc-ui:frameworkUrl var="url" splitChar="|" 
        panelStacks="${renderInfo.pageOptions.successPanelStacks}"
       />
      <dsp:input type="hidden" value="${fn:length(product.childSKUs)}"
        priority="100"
        bean="${renderInfo.pageOptions.formHandler}.addItemCount"/>
      <dsp:input type="hidden" value="${url}"
        bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.successUrlProperty}"/>
      <dsp:input type="hidden" value="${url}"
        bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.errorUrlProperty}"/>
      <dsp:input type="hidden" value="dummy" priority="-100"
        bean="${renderInfo.pageOptions.formHandler}.addItemToOrder"/>
      <div class="atg_dataTableFooterActions">
        <div class="atg_actionTo">
          <c:set var="isOrderModifiableDisableAttribute" value=" disabled='disabled'"/>
          <c:if test="${!empty shoppingCart.originalOrder}">
            <dsp:droplet name="OrderIsModifiable">
              <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
              <dsp:oparam name="true">
                <c:set var="isOrderModifiableDisableAttribute" value=""/>
              </dsp:oparam>
            </dsp:droplet>
          </c:if>
          <input type="button" id="skuBrowserAction" 
            <c:out value="${isOrderModifiableDisableAttribute}"/> 
            value="<fmt:message key='genericRenderer.addToOrder'/>"
            onClick="atg.commerce.csr.order.skuBrowserAction('${renderInfo.pageOptions.successPanelStacks}', '<dsp:valueof param="productId"/>');return false;">
          <%-- Bug 16050193 fix --%>
          <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function () {
              document.getElementById('skuBrowserAction').onclick=function(){
                atg.commerce.csr.order.skuBrowserAction('${renderInfo.pageOptions.successPanelStacks}', '<dsp:valueof param="productId"/>');
                return false;
              };
            });
          </script>
          <c:if test="${CSRConfigurator.usingInStorePickup}">
            <script type="text/javascript">
              _container_.onLoadDeferred.addCallback(function () {

              window.skuArray = {};
              <c:forEach items="${product.childSKUs}" var="skuItem">
                <dsp:droplet name="/atg/commerce/catalog/OnlineOnlyDroplet">
                  <dsp:param name="product" value="${productItem}"/>
                  <dsp:param name="sku" value="${skuItem}"/>
                  <dsp:oparam name="true">
                    window.skuArray["${skuItem.repositoryId}"] = true;
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    window.skuArray["${skuItem.repositoryId}"] = false;
                  </dsp:oparam>
                </dsp:droplet>
              </c:forEach>

                //assign this function call on change event for quantity fields
                dojo.query(".quantity-input").connect("onkeyup", function() {
                  dojo.forEach(dojo.query(".quantity-input"), function(entry, i){
                    //console.log(entry.value);
                  });
                  atg.commerce.csr.catalog.checkPickupInStoreButton(window.skuArray);
                });                
              });

              if (!dijit.byId("pickupLocationsPopup")) {
                new dojox.Dialog({ id: "pickupLocationsPopup",
                                   cacheContent: false, 
                                   executeScripts:"true",
                                   "class":"atg_commerce_csr_popup"});                
              }
            </script>
            <c:set var="pickupInStoreURL" value="${CSRConfigurator.contextRoot}/panels/catalog/pickupLocations.jsp?_windowid=${windowId}" /> 
            <br />
            <br />
            <span class="atg_commerce_csr_dataHighlight hidden" id="pickupInStoreLabel">
              <span id="ea_csc_instore_pickup_available"></span>
            </span>
            <fmt:message var='pickupInStoreTitle' key='genericRenderer.pickupInStoreTitle'/>
            <input type="button" 
              id="pickupInStoreAction"
              disabled="disabled"
              value="<fmt:message key='genericRenderer.pickupInStore'/>"
              onClick="atg.commerce.csr.catalog.pickupInStoreAction('${pickupInStoreTitle}', '${pickupInStoreURL}', '${product.repositoryId}');return false;">
            <%-- Bug 16050193 fix --%>
            <script type="text/javascript">
              _container_.onLoadDeferred.addCallback(function () {
                document.getElementById('pickupInStoreAction').onclick=function(){
                  atg.commerce.csr.catalog.pickupInStoreAction('${pickupInStoreTitle}', '${pickupInStoreURL}', '${product.repositoryId}');
                  return false;
                };
              });
            </script>
          </c:if>
        </div>
      </div>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
    Exception ee = (Exception) pageContext.getAttribute("exception"); 
    ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuBrowserAction.jsp#1 $$Change: 946917 $--%>