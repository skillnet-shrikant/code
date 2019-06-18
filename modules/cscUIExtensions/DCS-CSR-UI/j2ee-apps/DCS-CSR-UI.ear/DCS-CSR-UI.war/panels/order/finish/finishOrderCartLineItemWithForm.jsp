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
  <dsp:importbean	bean="/com/mff/commerce/order/GiftcardShipAddressForLabel" />

  <dsp:getvalueof var="item" param="commerceItem"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="commerceItemIndex" param="commerceItemIndex"/>
  <dsp:getvalueof var="itemIndex" param="itemIndex"/>

  <dsp:droplet name="GiftcardShipAddressForLabel">
	<dsp:param name="item" param="commerceItem"/>
	<dsp:param name="order" param="order"/>
	<dsp:param name="payGroupType" value="false"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="line1" param="line1"/>
		<dsp:getvalueof var="line2" param="line2"/>
		<dsp:getvalueof var="line3" param="line3"/>
	</dsp:oparam>
  </dsp:droplet>

  <c:url var="printLabelPopupURL"
	context="${CSRConfigurator.contextRoot}"
	value="/renderers/order/mff_printLabelPopup.jsp">
	<c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
	<c:param name="line1" value="${line1}"/>
	<c:param name="line2" value="${line2}"/>
	<c:param name="line3" value="${line3}"/>
  </c:url>
  
  <c:url var="printLabelIconImage"
	context="${CSRConfigurator.contextRoot}"
	value="/images/mff/mff_icon_print.gif">
  </c:url>

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
		<dsp:input type="hidden" id="commerceItemId${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputGC[${commerceItemIndex}].commerceItemId" value="${item.id}"/>
		<dsp:input type="text" id="giftCardNumber${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputGC[${commerceItemIndex}].giftCardNumber" value="${item.giftCardNumber}"/>
    </td>
    <td class="atg_numberValue">
    	<dsp:input type="checkbox" id="itemSelected${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputGC[${commerceItemIndex}].itemSelected"/>
    </td>
	<td class="atg_numberValue">
    	<a href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({ popupPaneId: 'printLabelPopupFloatingPane',
																				title: 'Preview/Print label',
																				url: '${printLabelPopupURL}'});return false;"><img src="${printLabelIconImage}"/></a>
    </td>
  </tr>
  </dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/finishOrderCartLineItem.jsp#2 $$Change: 1179550 $--%>
