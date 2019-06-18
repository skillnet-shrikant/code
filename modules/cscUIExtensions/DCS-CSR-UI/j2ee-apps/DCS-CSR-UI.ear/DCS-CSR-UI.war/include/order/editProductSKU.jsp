<%--

  @param productId - The product ID (optional)
  @param commerceItemID - The commerce item ID (optional)
  @param panelId - The ID for the panel

  @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/editProductSKU.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:getvalueof var="panelId" param="panelId"/>
    <c:if test="${empty panelId}">
      <c:set var="panelId" value="editProductSKU"/>
    </c:if>
    <csr:getProduct productId="${param.productId}"
      commerceItemId="${param.commerceItemId}">
      <c:set var="product" value="${product}" scope="request"/>
    </csr:getProduct>
    <csr:renderer name="/atg/commerce/custsvc/ui/renderers/SkuChangePanel">
      <jsp:attribute name="setPageData">
        <dsp:tomap var="productMap" value="${product}"/>
        <c:set target="${pageData}" property="product" value="${productMap}"/>
        <c:set target="${pageData}" property="panelId" value="${panelId}"/>
      </jsp:attribute>
      <jsp:body>
        <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
          <dsp:param name="productId" param="productId"/>
          <dsp:param name="categoryId" param="categoryId"/>
          <%-- 
            formSuffix is used by included pages to build unique form 
            names.
           --%>
          <dsp:param name="formSuffix" value="editSKU"/>
          <dsp:param name="mode" param="mode"/>
          <dsp:param name="skuId" param="skuId"/>
          <dsp:param name="panelId" value="${panelId}"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/editProductSKU.jsp#1 $$Change: 946917 $--%>
