<%--

 This is the top level content page for the Product Quick View popup. This
 page, by default, displays one or more SKUs for a product in a table and
 allows the user to enter quantities for each SKU. 
 
 @param productId - The ID of the product (optional, but this is probably what
 will be used in this case.)

 @param categoryId - The current product category (optional, probably not used normally))

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/quickView.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <csr:getProduct productId="${param.productId}"
      commerceItemId="${param.commerceItemId}">
      <c:set var="product" value="${product}" scope="request"/>
    </csr:getProduct>
    <dsp:tomap var="productMap" value="${product}"/>
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductInformation">
      <jsp:attribute name="setPageData">
        <c:set target="${pageData}" property="product" value="${productMap}"/>
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="commerceItemId" param="commerceItemId"/>
          <dsp:param name="panelId" value="quickView"/>
        </dsp:include>
      </jsp:body>
    </csr:renderer>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <% 
     Exception ee = (Exception) pageContext.getAttribute("exception"); 
     ee.printStackTrace();
  %>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/quickView.jsp#1 $$Change: 946917 $--%>
