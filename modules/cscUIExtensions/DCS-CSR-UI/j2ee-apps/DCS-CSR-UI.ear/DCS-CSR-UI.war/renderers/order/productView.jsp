<%--

 The default outermost product view renderer, this renderer just includes the
 renderers for the three major components that make up the product view panel.

 @param categoryId - The current product category (optional)
 @param productId - The ID of the product
 @param commerceItemId - The ID of the commerce item
 @param panelId - A value that uniquely identifies where in the UI this panel is
  being used

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productView.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <ul class="atg_commerce_csr_panelToolBar">
        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
          <dsp:param bean="/atg/commerce/custsvc/catalog/CSRRecentlyViewedCatalogHistory.recentlyViewedHistory" name="array"/>
          <dsp:oparam name="outputStart">
            <li class="atg_commerce_csr_last atg_commerce_csr_continueShopping">
              <div class="">
                <div dojoType="dijit.form.DropDownButton">
                  <span><fmt:message key="productViewRenderer.continueShopping"/></span>
                  <div dojoType="dijit.Menu" id="atg_commerce_csr_catalog_continueShoppingMenu">
          </dsp:oparam>
          <dsp:oparam name="output">
                      <dsp:include src="/include/catalog/categoriesBreadcrumb.jsp" otherContext="${CSRConfigurator.contextRoot}">
                        <dsp:param name="_windowid" value="${param['_windowid']}"/>
                        <dsp:param name="path" param="element"/>
                        <dsp:param name="whole" value="${true}"/>
                      </dsp:include>  
          </dsp:oparam>
          <dsp:oparam name="outputEnd">
                  </div>
                </div>
              </div>
            </li>
            <li>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <li class="atg_commerce_csr_last">
          </dsp:oparam>

      </dsp:droplet>
          <dsp:droplet name="/atg/commerce/custsvc/order/OrderIsModifiable">
            <dsp:param name="order" value="${currentOrder}"/>
            <dsp:oparam name="true">
              <c:set value="cmcShoppingCartPS" var="orderPanelStack"/>
            </dsp:oparam>
            <dsp:oparam name="false">
              <c:set value="cmcExistingOrderPS" var="orderPanelStack"/>
            </dsp:oparam>
          </dsp:droplet>
          <a href="#"
            onclick="atgNavigate(
              {panelStack : '<c:out value="${orderPanelStack}"/>'});
              return false;">
            <fmt:message key="productViewRenderer.viewShoppingCart"/>
          </a>
        </li>
      </ul>

      <csr:getProduct productId="${param.productId}"
        commerceItemId="${param.commerceItemId}">
        <c:set var="product" value="${product}" scope="request"/>
      </csr:getProduct>
      <dsp:tomap var="productMap" value="${product}"/>
      <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductInformation">
        <c:set target="${pageData}" property="product" value="${productMap}"/>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="commerceItemId" param="commerceItemId"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <dsp:param name="panelId" param="panelId"/>
        </dsp:include>
      </csr:renderer>
      <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductSku">
        <c:set target="${pageData}" property="product" value="${productMap}"/>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="commerceItemId" param="commerceItemId"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <dsp:param name="panelId" param="panelId"/>
        </dsp:include>
      </csr:renderer>
      <csr:renderer name="/atg/commerce/custsvc/ui/renderers/CrossSellItems">
        <c:set target="${pageData}" property="product" value="${productMap}"/>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="commerceItemId" param="commerceItemId"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <dsp:param name="panelId" param="panelId"/>
        </dsp:include>
      </csr:renderer>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productView.jsp#1 $$Change: 946917 $--%>
