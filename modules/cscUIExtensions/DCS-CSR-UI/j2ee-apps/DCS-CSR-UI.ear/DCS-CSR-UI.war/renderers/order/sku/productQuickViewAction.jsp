<%--

 Renders the action bits of the Product Quick View Popup

 @renderInfo - The RenderInfo
 @product - The product

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/productQuickViewAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="productItem" param="product"/>
      <dsp:tomap var="product" value="${productItem}"/>
      <script language="JavaScript" type="text/javascript">
        // Get the return object and fill in SKU quantity input values
        atg.commerce.csr.cart.getProductObjectWithQuantities = function ()
        {
          var result = atg.commerce.csr.cart.getProductObject();
          for ( var ii=0; ii < result.skus.length; ++ii ) {
            var sku = result.skus[ ii ];
            var input = document.getElementById( "sku-" + sku.id + "-quantity" );
            sku.quantity = input.value;
          }
          return result;
        }

        atg.commerce.csr.cart.getProductObject = function ()
        {
          return eval (
            <json:object prettyPrint="true">
              <c:forTokens var="property" items="id,name,description" delims=",">
                <json:property name="${property}" value="${product[property]}"/>
              </c:forTokens>
              <json:array name="skus" var="skuItem" items="${product.childSKUs}">
                <dsp:tomap var="sku" value="${skuItem}"/>
                <json:object>
                  <c:forTokens var="property" items="id,displayName,listPrice" delims=",">
                    <json:property name="${property}" value="${sku[property]}"/>
                  </c:forTokens>
                </json:object>
              </json:array>
            </json:object>);
        }
      </script>
      <div class="atg_dataTableActions">
        <div class="atg_actionTo">
          <input value="<fmt:message key='genericRenderer.ok'/>" 
            type="button" id="skuPickOK"
            onClick="atg.commerce.csr.common.hidePopupWithReturn('skuPickOK', { 
                result : 'ok', 
                product : atg.commerce.csr.cart.getProductObjectWithQuantities()});
              return false;">
          <input value="<fmt:message key='genericRenderer.cancel'/>" 
            type="button" id="skuPickCancel"
            onClick="atg.commerce.csr.common.hidePopupWithReturn('skuPickCancel', 
              {result:'cancel'})
              return false;"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/productQuickViewAction.jsp#1 $$Change: 946917 $--%>
