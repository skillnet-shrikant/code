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

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/inventoryStatus.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <script type="text/javascript">
		if (!dijit.byId("storeInventoryPopupFloatingPane")) {
			new dojox.Dialog({
				id : "storeInventoryPopupFloatingPane",
				cacheContent : "false",
				executeScripts : "true",
				scriptHasHooks : "true",
				duration : 100,
				"class" : "atg_commerce_csr_popup"
			});
		}
	</script>
	<span dojoType="dojox.Dialog" id="storeInventoryPopupFloatingPane" style="top:69px;width:250px;margin-right:80px;"></span>
      <dsp:getvalueof var="area" param="area"/>
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
	  <dsp:getvalueof var="product" param="product"/>
      <dsp:getvalueof var="giftCardProduct" bean="/atg/commerce/catalog/CatalogTools.giftCardProductId"/>
      <c:choose>
        <c:when test="${area == 'cell'}">
			<c:choose>
        		<c:when test="${product.id == giftCardProduct}">
        			N/A
            	</c:when>
    			<c:otherwise>
		          <dsp:getvalueof var="sku" param="sku"/>
					<dsp:droplet name="/com/mff/commerce/order/MFFWebAvailabilityForSku">
						<dsp:param name="skuid" value="${sku.id}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof param="elements" var="skuAvailability"/>
							<c:url var="storeInventoryPopupURL"
								context="${CSRConfigurator.contextRoot}"
								value="/renderers/order/sku/skuStoresStockLevel.jsp">
								<c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
								<c:param name="skuId" value="${sku.id}"/>
							</c:url>
				   			<a href="#" style="text-decoration: none" onclick="atg.commerce.csr.common.showPopupWithReturn({ popupPaneId: 'storeInventoryPopupFloatingPane',
																								title: 'Store Inventory',
																								url: '${storeInventoryPopupURL}'});return false;">${skuAvailability.stockLevel}</a>
						</dsp:oparam>
					</dsp:droplet>
        		</c:otherwise>
  			</c:choose>
        </c:when>
        <c:when test="${area == 'header'}">
          Online Stock
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/inventoryStatus.jsp#1 $$Change: 946917 $--%>
