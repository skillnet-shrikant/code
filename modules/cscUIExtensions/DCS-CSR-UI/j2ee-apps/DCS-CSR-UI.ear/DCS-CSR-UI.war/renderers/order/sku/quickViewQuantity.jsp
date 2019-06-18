<%--
 
 This page renders the quantity column header and cells of the SKU table on the
 Product Quick View Popup.

 @param product - The product item
 @param sku - The SKU item belonging to the product
 @param property - The property name of the SKU to display
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)
 @param loopTagStatus - The status object of the loop tag

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/quickViewQuantity.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

      <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
      <c:set var="fractionalUnitDecimalPlaces" value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
      <c:set var="fractionalUnitPattern" value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
      <c:set var="fractionalValidationMessage" value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />

      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="status" param="loopTagStatus"/>
      <dsp:tomap var="sku" param="sku"/>
      <dsp:tomap var="product" param="product"/>
	  <c:choose>
		<c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 5}">
			Available in select stores only
		</c:when>
		<c:otherwise>
	      <c:choose>
	        <c:when test="${area == 'cell'}">
	          <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
	            <dsp:param name="product" value="${product.id}"/>
	            <dsp:param name="sku" value="${sku.id}"/>
	            <dsp:oparam name="output">
	              <dsp:getvalueof var="isFractional" param="fractional"/>
	            </dsp:oparam>
	          </dsp:droplet>
	
	          <c:set var="inputSize" value="5"/>
	          <c:set var="inputMaxLength" value="5"/>
	          <c:set var="inputInvalidMessage" value="Please enter valid value."/>
	          <c:set var="inputConstraints" value="{places:0, pattern: '#####'}"/>
	
	          <c:if test="${isFractional eq true}">
	            <c:set var="inputSize" value="9"/>
	            <c:set var="inputMaxLength" value="9"/>
	            <c:set var="inputInvalidMessage" value="${fractionalValidationMessage}"/>
	            <c:set var="inputConstraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
	          </c:if>
	
	          <input id="sku-${sku.id}-quantity" value="0"
	            size="${inputSize}"
	            maxlength="${inputMaxLength}"
	            type="text"
	            dojoType="dijit.form.NumberTextBox"
	            constraints="${inputConstraints}"
	            invalidMessage="${inputInvalidMessage}"/>
	
	        </c:when>
	        <c:when test="${area == 'header'}">
	          <fmt:message key="genericRenderer.qty"/>
	        </c:when>
	      </c:choose>
	  </c:otherwise>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/quickViewQuantity.jsp#2 $$Change: 1179550 $--%>
