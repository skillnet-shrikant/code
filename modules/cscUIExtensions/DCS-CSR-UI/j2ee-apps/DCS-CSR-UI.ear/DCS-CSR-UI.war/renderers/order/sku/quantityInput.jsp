<%--
 A page fragment that takes SKU quantity input

 @param product - The product item
 @param sku - The SKU item belonging to the product
 @param property - The property name of the SKU to display
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)
 @param loopTagStatus - The status object of the loop tag

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/quantityInput.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
      <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
      <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
      <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="status" param="loopTagStatus"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
          <dsp:tomap var="product" param="product"/>
          <dsp:tomap var="sku" param="sku"/>
          <dsp:input type="hidden" value="${product.id}"
            bean="${renderInfo.pageOptions.formHandler}.items[${status.index}].productId"/>
          <dsp:input type="hidden" value="${sku.id}"
            bean="${renderInfo.pageOptions.formHandler}.items[${status.index}].catalogRefId"/>

           <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
             <dsp:param name="product" value="${product.id}"/>
             <dsp:param name="sku" value="${sku.id}"/>
             <dsp:oparam name="output">
                <dsp:getvalueof var="isFractional" param="fractional"/>
             </dsp:oparam>
           </dsp:droplet>

          <c:choose>
            <c:when test="${not empty product.fulfillmentMethod && product.fulfillmentMethod == 5}">
				Available in select stores only
            </c:when>
            <c:otherwise>
	          <c:choose>
	            <c:when test="${isFractional == true}">
	              <dsp:input type="text"
	                bean="${renderInfo.pageOptions.formHandler}.items[${status.index}].quantityWithFraction"
	                id="${sku.id}"
	                size="9"
	                maxlength="9"
	                converter="number"
	                value="">
	                <dsp:tagAttribute name="class" value="quantity-input" />
	                <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
	                <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
	                <dsp:tagAttribute name="trim" value="true" />
	                <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
	              </dsp:input>
	            </c:when>
	            <c:otherwise>
	              <dsp:input type="text"
	                bean="${renderInfo.pageOptions.formHandler}.items[${status.index}].quantity"
	                id="${sku.id}"
	                size="3"
	                maxlength="5"
	                converter="number"
	                value="">
	                <dsp:tagAttribute name="class" value="quantity-input"/>
	                  <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
	                  <dsp:tagAttribute name="invalidMessage" value="Please enter valid value."/>
	                  <dsp:tagAttribute name="trim" value="true" />
	                  <dsp:tagAttribute name="constraints" value="{places:0, pattern: '#####'}"/>
	              </dsp:input>
	          </c:otherwise>
	          </c:choose>
          </c:otherwise>
          </c:choose>
        </c:when>
        <c:when test="${area == 'header'}">
          <fmt:message key="genericRenderer.qty"/>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/quantityInput.jsp#2 $$Change: 1179550 $--%>
