<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/cart.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<!-- begin ordersummary/modify/cart.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemsCountDroplet">
  <dsp:param name="order" value="${order}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="totalCommerceItemCount" param="count"/>
  </dsp:oparam>
</dsp:droplet>

<dt>
  <a href="#" id="keyboardShortcutShoppingCart" onclick="<dsp:include src="/include/order/currentOrderViewAction.jsp" otherContext="${CSRConfigurator.contextRoot}"/>event.cancelBubble=true;return false;"><fmt:message key="orderSummary.shoppingCart"/></a> 
</dt>
<dd>
  <span id="atg_csc_ordersummary_shoppingCartItems">
    <fmt:message key="orderSummary.numItems">
      <fmt:param>
        <web-ui:formatNumber value="${totalCommerceItemCount}"/>
      </fmt:param>
    </fmt:message>
  </span>
</dd>
<dt>
  <em><fmt:message key="orderSummary.orderTotal"/></em>
</dt>
<dd>
  <csr:getCurrencyCode order="${order}">
			<c:set var="currencyCode" value="${currencyCode}" scope="request" />
		</csr:getCurrencyCode> 
		<span id="atg_csc_ordersummary_orderTotalAmount">
    <csr:formatNumber value="${order.priceInfo.total}" type="currency" currencyCode="${currencyCode}"/>
  </span>
</dd>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/cart.jsp#2 $$Change: 1179550 $--%>
