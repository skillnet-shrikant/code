<%-- 

 This is the outermost product view panel page. It includes a renderer
 that makes up the actual contents of the product view panel, and it
 also provides a product view history.

 Note: The product view history is currently only used to select the
 last product viewed if no productId parameter is specified. Future
 releases should build a select list from ${productViews.history} to
 allow the user to select from a list of recently viewed products.

 @param productId - The ID of the product (optional)

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productView.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean var="productViews"
      bean="/atg/commerce/custsvc/catalog/ProductViewHistory"/>
    <dsp:getvalueof var="productId" param="productId"/>
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <c:choose>
        <c:when test="${ not empty productId }">
          <%-- remember this product ID in product view history --%>
          <jsp:useBean id="pv" class="java.util.HashMap"/>
          <c:set target="${pv}" property="productId" value="${productId}"/>
          <c:set target="${productViews}" property="top" value="${pv}"/>
        </c:when>
        <c:when test="${empty productId}">
          <%-- no productId specified, use last viewed product if it exists --%>
          <c:set var="pv" value="${productViews.top}"/>
        </c:when>
      </c:choose>
      <c:choose>
        <c:when test="${empty pv}">
          <div id="ea_csc_product_view"></div><br />
        </c:when>
        <c:otherwise>
          <csr:getProduct productId="${pv.productId}"
            commerceItemId="${param.commerceItemId}">
            <c:set var="product" value="${product}" scope="request"/>
          </csr:getProduct>
          <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductViewPanel">
            <jsp:attribute name="setPageData">
              <dsp:tomap var="product" value="${product}"/>
              <c:set target="${pageData}" property="product" value="${product}"/>
            </jsp:attribute>
            <jsp:body>
              <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
                <dsp:param name="productId" value="${pv.productId}"/>
                <dsp:param name="categoryId" param="categoryId"/>
                <%-- Unique value identifying where the contained components are
                     being used --%>
                <dsp:param name="panelId" value="productView"/>
              </dsp:include>
            </jsp:body>
          </csr:renderer>
        </c:otherwise>
      </c:choose>
      <script type="text/javascript">
        atg.progress.update('cmcCatalogPS');
      </script>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/productView.jsp#1 $$Change: 946917 $--%>
