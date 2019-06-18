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
  <dsp:importbean bean="/com/mff/commerce/order/GiftcardShipAddressForLabel" />
  <dsp:importbean bean="/com/mff/commerce/order/RefundableGiftcardAmount"/>
  
  <dsp:getvalueof var="item" param="commerceItem"/>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="currencyCode" param="currencyCode"/>
  <dsp:getvalueof var="commerceItemIndex" param="commerceItemIndex"/>
  <dsp:getvalueof var="itemIndex" param="itemIndex"/>

  <dsp:droplet name="RefundableGiftcardAmount">
	<dsp:param name="paygroup" value="${item}" />
	<dsp:oparam name="true">
		<dsp:getvalueof var="refundAmount" param="elements"/>
	</dsp:oparam>
	<dsp:oparam name="false">
		<dsp:getvalueof var="refundAmount" value="0.0"/>
	</dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="GiftcardShipAddressForLabel">
	<dsp:param name="item" param="commerceItem"/>
	<dsp:param name="order" param="order"/>
	<dsp:param name="payGroupType" value="true"/>
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

 <dsp:getvalueof var="itemQuantity" value="1"/>
 
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
			Refundable Gift Card
        </li>
        <li>
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
      <web-ui:formatNumber value="${itemQuantity}"/>
    </td>
    <td class="atg_numberValue">
		<csr:formatNumber value="${refundAmount}" type="currency" currencyCode="${currencyCode}"/>
    </td>
    <td class="atg_numberValue">
		<csr:formatNumber value="${refundAmount}" type="currency" currencyCode="${currencyCode}"/>
    </td>
    <td class="atg_numberValue atg_commerce_csr_priceOveride">
		<dsp:input type="hidden" id="commerceItemId${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputRGC[${commerceItemIndex}].commerceItemId" value="${item.id}"/>
		<dsp:input type="text" id="giftCardNumber${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputRGC[${commerceItemIndex}].giftCardNumber" value="${item.cardNumber}"/>
    </td>
    <td class="atg_numberValue">
    	<dsp:input type="checkbox" id="itemSelected${commerceItemIndex}" bean="MFFCSCTasksOnOrderFormHandler.lineItemInputRGC[${commerceItemIndex}].itemSelected"/>
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
