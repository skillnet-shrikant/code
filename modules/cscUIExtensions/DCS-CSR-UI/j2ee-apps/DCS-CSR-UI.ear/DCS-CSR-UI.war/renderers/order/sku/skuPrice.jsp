<%--
 A page fragment that displays the SKU price

 @param product - The product item
 @param sku - The SKU item belonging to the product
 @param property - The property name of the SKU to display
 @param area - The area to render, in "header" | "cell"
 @param renderInfo - The render info object
 @param trId - The DOM ID of the row or <tr> tag
 @param tdId - The DOM ID of the cell, or <td> or (<th> tag)

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuPrice.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
	  <dsp:getvalueof var="status" param="loopTagStatus"/>
	  <dsp:getvalueof var="sku" param="sku"/>
      <dsp:getvalueof var="product" param="product"/>
      <dsp:getvalueof var="giftCardProduct" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
			<c:choose>
        		<c:when test="${product.id == giftCardProduct}">
        		<div class="left">
					<select id="gcValueDropDown" name="gcValueDropDown" onchange="populateGCValue(this)">
						<option value="0.00">Select Value</option>
						<option value="10.00">$10</option>
						<option value="25.00">$25</option>
						<option value="50.00">$50</option>
						<option value="100.00">$100</option>
					</select><BR>
					or enter amount:<BR>
					<dsp:input type="text"
						bean="${renderInfo.pageOptions.formHandler}.giftCardDenomination"
		                id="gcValue" name="gcValue" size="10" maxlength="10" value="">
						<dsp:tagAttribute name="trim" value="true" />
					</dsp:input><BR>
					(Value from $2 - $500)
				</div>
            	</c:when>
    			<c:otherwise>
		          <dsp:getvalueof var="property" param="property"/>
		          <dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}">
		          </dsp:include>
        		</c:otherwise>
  			</c:choose>
         </c:when>
        <c:when test="${area == 'header'}">
			<c:choose>
        		<c:when test="${product.id == giftCardProduct}">
        			<div class="left">
        				Gift Card Value
        			</div>
            	</c:when>
    			<c:otherwise>
					<fmt:message key="genericRenderer.price"/> 
        		</c:otherwise>
  			</c:choose>
        </c:when>
      </c:choose>
	  <script type="text/javascript">  
		populateGCValue = function (dropDownObj) {
			var dropDownValue = dropDownObj.value;
			if ( dropDownValue != '' )
			{
				gcValue.value = dropDownValue;
			}
    	};
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuPrice.jsp#1 $$Change: 946917 $--%>
