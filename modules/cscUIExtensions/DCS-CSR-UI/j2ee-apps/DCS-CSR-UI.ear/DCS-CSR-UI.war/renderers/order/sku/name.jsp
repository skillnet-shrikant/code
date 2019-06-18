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

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/name.jsp#1 $
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
		  <dsp:getvalueof var="prd" param="product"/>
          <dsp:tomap var="prdmap" value="${prd}"/>
          <dsp:getvalueof var="sku" param="sku"/>
          <dsp:tomap var="skumap" value="${sku}"/>
          <dl class="atg_commerce_csr_catalog_product_info_layout">
                <dt id="atg_commerce_csr_catalog_product_info_display_name" class="atg_commerce_csr_productVievTitle">
                  ${fn:escapeXml(prdmap.description)}
                </dt>
		  </dl>
		  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array"  param="product.dynamicAttributes"/>
			<dsp:oparam name="outputStart">
				<ul style="padding-left: 10px;">
			</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof var="key" param="key"/>
				<li><dsp:valueof param="element"/>: <dsp:valueof param="sku.dynamicAttributes.${key}"/></li>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</ul>
			</dsp:oparam>
		  </dsp:droplet>
        </c:when>
        <c:when test="${area == 'header'}">
          <fmt:message key="genericRenderer.name"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/name.jsp#1 $$Change: 946917 $--%>
