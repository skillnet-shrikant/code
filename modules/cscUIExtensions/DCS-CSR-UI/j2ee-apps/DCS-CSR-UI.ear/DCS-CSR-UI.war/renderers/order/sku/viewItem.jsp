<%--
 A page fragment that displays something to click on to view the spcifiec SKU)

 @param product - The product item
 @param skuItem - The SKU item belonging to the product
 @param property - The property name of the SKU to display
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/viewItem.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:tomap var="skuMap" param="skuItem"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="imageUrl" param="skuItem.smallImage.url"/>
      <dsp:getvalueof var="panelId" param="panelId"/>
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
          <a href="#" class="atg_tableIcon atg_propertyView" 
            title="<fmt:message key='genericRenderer.viewProperty'/>"
            onclick="atg.commerce.csr.catalog.viewSkuDescription('<c:out value="${fn:escapeXml(skuMap.id)}"/>','<c:out value="${fn:escapeXml(productId)}"/>','<c:out value="${fn:escapeXml(skuMap.displayName)}"/>','<c:out value="${fn:escapeXml(skuMap.description)}"/>','${fn:escapeXml(imageUrl)}','${fn:escapeXml(panelId)}')">
            <fmt:message key="genericRenderer.view"/>
          </a>
        </c:when>
        <c:when test="${area == 'header'}">
          &nbsp;
        </c:when>
      </c:choose>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/viewItem.jsp#1 $$Change: 946917 $--%>
