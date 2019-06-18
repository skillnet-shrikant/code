<%--

 This is the top level page for the product view (read only) popup.
 
 @param siteId - The Site ID of the product
 @param categoryId - The current product category (optional)
 @param productId - The ID of the product

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/productReadOnly.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <csr:renderer name="/atg/commerce/custsvc/ui/renderers/ProductReadOnlyPopup">
        <jsp:attribute name="setPageData">
          <c:set target="${pageData}" property="product" value="${productMap}"/>
        </jsp:attribute>
        <jsp:body>
          <dsp:include src="${renderInfo.url}" otherContext="${renderInfo.contextRoot}">
            <dsp:param name="categoryId" param="categoryId"/>
            <dsp:param name="productId" param="productId"/>
            <dsp:param name="siteId" param="siteId"/>
            <dsp:param name="commerceItemId" param="commerceItemId"/>
            <dsp:param name="panelId" value="productReadOnly"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/product/productReadOnly.jsp#1 $$Change: 946917 $--%>
