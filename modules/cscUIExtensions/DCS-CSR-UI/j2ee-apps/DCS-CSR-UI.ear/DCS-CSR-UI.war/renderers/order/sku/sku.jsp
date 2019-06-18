<%--
 A SKU table page fragment that displays the inventory status of a SKU item

 @param product - The product item
 @param sku - The SKU item belonging to the product
 @param property - The property name of the SKU to display
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)
 @param loopTagStatus - The status object of the loop tag

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/sku.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
          <dsp:getvalueof var="sku" param="sku"/>
          <dsp:tomap var="skumap" value="${sku}"/>
          ${skumap.id}
          <dsp:droplet name="/atg/commerce/catalog/OnlineOnlyDroplet">
            <dsp:param name="sku" value="${sku}"/>
            <dsp:oparam name="true">
              <br />
              <span class="red">
                <fmt:message key="catalogBrowse.inStorePickup.onlineOnly" />
              </span>
            </dsp:oparam>
          </dsp:droplet> 
        </c:when>
        <c:when test="${area == 'header'}">
          <fmt:message key="genericRenderer.sku"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/sku.jsp#1 $$Change: 946917 $--%>
