<%--
Display the appropriate details for each commerce item in the cart.

Expected params
commerceItem : The commerce item.
commerceItemIndex : Used to render alternate rows in the table in different styles.
currencyCode : The order.priceInfo.currencyCode value.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderCartLineItem.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

  <dsp:importbean bean="/atg/commerce/custsvc/util/AltColor"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CommerceItemStateDescriptions"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet"/>
  <dsp:importbean bean="/com/mff/commerce/order/MFFCSCTasksOnOrderFormHandler"/>
  <dsp:importbean bean="/mff/MFFEnvironment"/>
  
  <dsp:getvalueof var="item" param="commerceItem"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="commerceItemIndex" param="commerceItemIndex"/>
  <dsp:getvalueof var="sourceForceAllocation" param="sourceForceAllocation"/>

  <dsp:droplet name="AltColor">
    <dsp:param name="value" param="commerceItemIndex"/>
    <dsp:oparam name="odd">
      <tr class="atg_dataTable_altRow">
    </dsp:oparam>
    <dsp:oparam name="even">
      <tr>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="GetCommerceItemQuantityDroplet">
    <dsp:param name="item" value="${item}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="itemQuantity" param="quantity"/>
    </dsp:oparam>
  </dsp:droplet>

  <c:if test="${isMultiSiteEnabled == true}">
    <c:set var="siteId" value="${item.auxiliaryData.siteId}"/>
    <td class="atg_commerce_csr_siteIcon">
      <csr:siteIcon siteId="${siteId}" />
    </td>
  </c:if>
  <td>
    
  <script type="text/javascript">
    if (!dijit.byId("productQuickViewPopup")) {
      new dojox.Dialog({ id: "productQuickViewPopup",
                         cacheContent: "false",
                         executeScripts: "true",
                         scriptHasHooks: "true",
                         duration: 100,
                         "class": "atg_commerce_csr_popup"});
    }
  </script>

      <ul class="atg_commerce_csr_itemDesc">
        <li>
          <dsp:tomap var="product" value="${item.auxiliaryData.productRef}"/>
          <dsp:tomap var="sku" value="${item.auxiliaryData.catalogRef}"/>
          <c:choose>
            <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
              <svc-ui:frameworkPopupUrl var="commerceItemPopup"
                value="/include/order/product/productReadOnly.jsp"
                siteId="${siteId}"
                context="${CSRConfigurator.contextRoot}"
                windowId="${windowId}"
                productId="${item.auxiliaryData.productId}"/>
              <a title="<fmt:message key='cart.items.quickView'/>"
                href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                  popupPaneId: 'productQuickViewPopup',
                  title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                  url: '${commerceItemPopup}',
                  onClose: function( args ) { }} );return false;">
                ${fn:escapeXml(product.displayName)}
              </a>
            </c:when>
            <c:otherwise>
              <svc-ui:frameworkPopupUrl var="commerceItemPopup"
                value="/include/order/product/productReadOnly.jsp"
                siteId="${siteId}"
                context="${CSRConfigurator.contextRoot}"
                windowId="${windowId}"
                productId="${item.auxiliaryData.productId}"/>
              <a title="<fmt:message key='cart.items.quickView'/>"
                href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                  popupPaneId: 'productQuickViewPopup',
                  title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                  url: '${commerceItemPopup}',
                  onClose: function( args ) {  }} );return false;">
                ${fn:escapeXml(product.displayName)}
              </a>
            </c:otherwise>
          </c:choose>  
        </li>
        <li>
          <c:out value="${item.catalogRefId}"/>          
        </li>
        <li>
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array"  value="${item.auxiliaryData.productRef.dynamicAttributes}"/>
				<dsp:oparam name="outputStart">
					<ul style="padding-left: 10px;">
				</dsp:oparam>
				<dsp:oparam name="output">
					<dsp:setvalue param="skuDynamicAttributes" value="${sku.dynamicAttributes}"/>
					<dsp:getvalueof var="key" param="key"/>
					<li><dsp:valueof param="element"/>: <dsp:valueof param="skuDynamicAttributes.${key}"/></li>
				</dsp:oparam>
				<dsp:oparam name="outputEnd">
					</ul>
				</dsp:oparam>
			</dsp:droplet>            
        </li>
      </ul>
    </td>
    <td>
      <dsp:droplet name="IsOrderIncomplete">
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="true">
          <csr:inventoryStatus commerceItemId="${item.catalogRefId}"/>
        </dsp:oparam>
        <dsp:oparam name="false">
          <dsp:droplet name="CommerceItemStateDescriptions">
            <dsp:param name="state" value="${item.stateAsString}"/>
            <dsp:param name="elementName" value="stateDescription"/>
            <dsp:oparam name="output">
              <dsp:droplet name="IsHighlightedState">
                <dsp:param name="obj" value="${item}"/>
                <dsp:oparam name="true">
                  <span class="atg_commerce_csr_dataHighlight"><dsp:valueof param="stateDescription"></dsp:valueof></span>
                </dsp:oparam>        
                <dsp:oparam name="false">
                	<dsp:valueof param="stateDescription"></dsp:valueof>
                	<c:if test="${not empty item.giftCardNumber}">
						<br/>GC Number : ${item.giftCardNumber}
					</c:if>
					<c:if test="${item.stateAsString == 'CANCELLED'}">
						<br/><span>${item.cancelDescription}</span>
					</c:if>
					<c:if test="${(item.stateAsString == 'SENT_TO_STORE' || item.stateAsString == 'SHIPPED') && not empty item.fulfillmentStore}">
						<span class="atg_commerce_csr_dataHighlight">${item.fulfillmentStore}</span>
					</c:if>
					<c:if test="${item.stateAsString == 'FORCED_ALLOCATION' && not empty item.rejectionReasonCodes}">
						<br/>previousAllocation : ${item.rejectionReasonCodes}
					</c:if>
                </dsp:oparam>        
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </td>
    <td class="atg_numberValue">  
      <%-- Always add the returnedQuantity. This will show the correct data
      for items with returns but won't affect new orders. --%>
      <dsp:droplet name="GetCommerceItemQuantityDroplet">
        <dsp:param name="item" value="${item}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="returnedQuantity" param="returnedQuantity"/>
        </dsp:oparam>
      </dsp:droplet>
      <web-ui:formatNumber value="${itemQuantity + returnedQuantity}"/>
    </td>
    <td class="atg_numberValue">
           <c:if test="${item.priceInfo.salePrice < item.priceInfo.listPrice}">
		   <span class="atg_commerce_csr_common_content_strikethrough"> 
		    <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />
		   </span>
	           <csr:formatNumber value="${item.priceInfo.salePrice}" type="currency" currencyCode="${currencyCode}" />
           </c:if>
           <c:if test="${item.priceInfo.salePrice == item.priceInfo.listPrice}">
   		   <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${currencyCode}" />      
           </c:if>
    </td>
    <td class="atg_numberValue">
      <c:set var="pi" value="${item.priceInfo}"/>
      <c:if test="${pi.rawTotalPrice != pi.amount}">
        <span class="atg_commerce_csr_common_content_strikethrough">
         <csr:formatNumber value="${pi.rawTotalPrice}" type="currency" currencyCode="${currencyCode}"/>
        </span>
        &nbsp;
      </c:if>
      <csr:formatNumber value="${pi.amount}" type="currency" currencyCode="${currencyCode}"/>
    </td>
    <td class="atg_numberValue atg_commerce_csr_priceOveride">
      <c:choose>
        <c:when test="${item.priceInfo.amountIsFinal}" >
          <csr:formatNumber value="${item.priceInfo.amount}" type="currency" currencyCode="${currencyCode}"/>
        </c:when>
        <c:otherwise>
          <c:out value="" />
        </c:otherwise>
      </c:choose>
    </td>
    <td class="atg_numberValue">
		<c:choose>
        	<c:when test="${empty item.trackingNumber}">
				<%-- leave blank --%>
			</c:when>
			<c:otherwise>
				<c:forEach var="splt" items="${fn:split(item.trackingNumber,'|')}">
				    <dsp:getvalueof var="fedExTrackingUrl" bean="MFFEnvironment.fedExTrackingUrl" />
					<a href="${fedExTrackingUrl}${splt}" target="_blank" >${splt}</a><br/>
				</c:forEach>
			</c:otherwise>
		</c:choose>
    </td>
	<td class="atg_numberValue">
		<c:choose>
        	<c:when test="${order.stateAsString == 'IN_REMORSE' || item.stateAsString == 'SENT_TO_STORE' || item.stateAsString == 'PENDING_GC_FULFILLMENT' || ((item.stateAsString == 'FORCED_ALLOCATION' || (item.stateAsString == 'PENDING_GC_FULFILLMENT' && item.gwp)) && sourceForceAllocation)}">
				<dsp:input type="checkbox" priority="-5" id="itemSelected${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInput[${commerceItemIndex}].itemSelected"/>
			</c:when>
			<c:otherwise>
				<dsp:input type="checkbox" disabled="true" id="itemSelected${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInput[${commerceItemIndex}].itemSelected"/>
			</c:otherwise>
		</c:choose>
		<dsp:input type="hidden" id="commerceItemId${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInput[${commerceItemIndex}].commerceItemId" value="${item.id}"/>
    </td>
    <td class="atg_numberValue">
    	<c:if test="${item.stateAsString == 'FORCED_ALLOCATION' && item.quantity > 1 && sourceForceAllocation}">
			<dsp:include src="splitItem.jsp" otherContext="${CSRConfigurator.contextRoot}">
	      		<dsp:param name="commerceItemId" value="${item.id}"/>
	    	</dsp:include>
	    </c:if>
    </td>
    
  </tr>
  <%
    /*
     * Display breakdown if commerce item is associated with a giftlist
     */
  %>
  <c:set var="customerTotalQuantity" value="${itemQuantity}" />
  <c:set var="giftlistTotalQuantity" value="0" />
  <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
    <dsp:param name="commerceItemId" value="${item.id}" />
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="key" param="key" />
      <dsp:getvalueof var="value" param="element" />
      <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
        <dsp:param name="id" param="key" />
        <dsp:oparam name="output">
          <dsp:setvalue paramvalue="element" param="giftlist" />
          <c:set var="customerTotalQuantity" value="${customerTotalQuantity - value }" />
          <c:set var="giftlistTotalQuantity" value="${giftlistTotalQuantity + value }" />
        </dsp:oparam>
      </dsp:droplet><%-- End GiftlistLookupDroplet --%>
    </dsp:oparam>
  </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
  <c:if test="${customerTotalQuantity > '0' && giftlistTotalQuantity > '0'}">
    <tr>
      <c:if test="${isMultiSiteEnabled == true}">
        <td></td>
        <td>
      </c:if>
      <c:if test="${isMultiSiteEnabled == false}">
        <td>
      </c:if>
        <ul class="atg_commerce_csr_itemDesc">
          <li class="atg_commerce_csr_giftwishListName">
            <fmt:message key="shoppingCartSummary.currentCustomer.label"/><%-- Display Current Customer label --%>
          </li>
        </ul>
      </td>
      <td></td>
      <td class="atg_numberValue">
        <web-ui:formatNumber value="${customerTotalQuantity}"/><%-- Display Customer Quantity --%>
      </td>
    </tr>
  </c:if>
  <dsp:droplet name="/atg/commerce/gifts/GiftlistShoppingCartQuantityDroplet">
    <dsp:param name="commerceItemId" value="${item.id}" />
    <dsp:param name="order" value="${order}"/>
    <dsp:oparam name="output">
      <tr>
        <dsp:getvalueof var="key" param="key" />
        <dsp:getvalueof var="value" param="element" />
        <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
          <dsp:param name="id" param="key" />
          <dsp:oparam name="output">
          <c:if test="${isMultiSiteEnabled == true}">
            <td></td>
            <td>
          </c:if>
          <c:if test="${isMultiSiteEnabled == false}">
            <td>
          </c:if>
              <ul class="atg_commerce_csr_itemDesc">
                <li class="atg_commerce_csr_giftwishListName">
                <dsp:setvalue paramvalue="element" param="giftlist" /> 
                <dsp:getvalueof var="eventName" vartype="java.lang.String" param="giftlist.eventName" />
                <dsp:valueof param="giftlist.owner.firstName" />&nbsp; 
                <dsp:valueof param="giftlist.owner.lastName" />, <c:out value="${eventName}" /><%-- Display Gift Recipient Name and Event Name --%>
                </li>
              </ul>
            </td>
            <td></td>
            <td class="atg_numberValue">
              <web-ui:formatNumber value="${value}"/><%-- Display Gift Recipient Quantity --%>
            </td>
          </dsp:oparam>
        </dsp:droplet><%-- End GiftlistLookupDroplet --%>
      </tr>
    </dsp:oparam>
  </dsp:droplet><%-- End GiftlistShoppingCartQuantityDroplet --%>
  </dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderCartLineItem.jsp#2 $$Change: 1179550 $--%>
