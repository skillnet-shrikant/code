<%--
 A page fragment that takes SKU change input

 @param product - The product item
 @param commerceItem - The commerce item (required)
 @param commerceItemId - The commerce item ID
 @param productId - The product ID
 @param sku - The SKU item belonging to the product (required)
 @param skuId - The (optional) ID of the SKU to appear selected initially
 @param property - The property name of the SKU to display 
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)
 @param loopTagStatus - The status object of the loop tag

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuRadioSelect.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="commerceItem" param="commerceItem"/>
      <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="status" param="loopTagStatus"/>
      <dsp:getvalueof var="skuId" param="skuId"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
          <dsp:tomap var="sku" param="sku"/>
          <c:choose>
            <c:when test="${ not empty skuId }">
              <c:set var="checked" value="${sku.id == skuId}"/>
              <c:set var="ssradioname" value="sku-select"/>
            </c:when>
            <c:otherwise>
              <c:set var="checked" 
                value="${commerceItem.catalogRefId == sku.id}"/>
              <c:set var="ssradioname" 
                value="SKUCNG:${commerceItemId}"/>
            </c:otherwise>
          </c:choose>
          <input type="radio" name="${ssradioname}"
            <c:if test="${checked}">
              checked
            </c:if>
            value="${sku.id}"/>
        </c:when>
        <c:when test="${area == 'header'}">
          <fmt:message key="genericRenderer.select"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuRadioSelect.jsp#1 $$Change: 946917 $--%>
