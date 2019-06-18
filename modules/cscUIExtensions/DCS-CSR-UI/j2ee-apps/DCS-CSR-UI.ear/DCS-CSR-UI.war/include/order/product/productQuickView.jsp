<%--

 This is the top level page for the product SKU change popup (the product view
 panel in SKU edit mode).
 
 @param categoryId - The current product category (optional)
 @param productId - The ID of the product
 @param panelId - ID of the panel

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/productQuickView.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:getvalueof var="panelId" param="panelId"/>
    <c:if test="${empty panelId}">
      <c:set var="panelId" value="productQuickView"/>
    </c:if>
    <csr:getProduct productId="${param.productId}"
      commerceItemId="${param.commerceItemId}">
      <c:set var="product" value="${product}" scope="request"/>
    </csr:getProduct>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductQuickViewPopup">
        <jsp:attribute name="setPageData">
          <dsp:tomap var="productMap" value="${product}"/>
          <c:set target="${pageData}" property="product" value="${productMap}"/>
          <c:set target="${pageData}" property="panelId" value="${panelId}"/>
        </jsp:attribute>
        <jsp:body>
          <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
            <dsp:param name="productId" param="productId"/>
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:param name="panelId" value="${panelId}"/>
          </dsp:include>
        </jsp:body>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/productQuickView.jsp#1 $$Change: 946917 $--%>
