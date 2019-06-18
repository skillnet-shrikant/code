<%--
 This page defines the global order panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/orderMainPanel.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
      <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart" />
      <c:set var="order" value="${cart.current}"/>
      <dsp:droplet name="HasAccessRight">
        <dsp:param name="accessRight" value="commerce-custsvc-adjust-price-privilege"/>
        <dsp:oparam name="accessGranted">
          <c:set var="adjustPricePriv" value="true"/>
        </dsp:oparam>
        <dsp:oparam name="accessDenied">
          <c:set var="adjustPricePriv" value="false"/>
        </dsp:oparam>
      </dsp:droplet>
      <div class="atg_commerce_csr_corePanelData">
        <div class="atg_commerce_csr_globalOrderView">
<script type="text/javascript">
  dojo.addOnLoad(
    function(){
      if (!dijit.byId("orderMainItemPopup")) {
        new dojox.Dialog({ id: "orderMainItemPopup",
                           cacheContent: "false",
                           executeScripts: "true",
                           scriptHasHooks: "true",
                           duration: 100,
                           "class": "atg_commerce_csr_popup"});
      }
  });
</script>

          <table class="atg_dataTable" cellpadding="0" cellspacing="0">
            <thead>
              <tr>
                <th scope="col"><fmt:message key="cart.items.itemDescription"/></th>
                <th scope="col"><fmt:message key="cart.items.inventoryStatus"/></th>
                <th scope="col" class="atg_numberValue"><fmt:message key="cart.items.qty"/></th>
                <th scope="col" class="atg_numberValue"><fmt:message key="cart.items.priceEach"/></th>
                <th scope="col" class="atg_numberValue"><fmt:message key="cart.items.totalPrice"/></th>
                <c:if test="${adjustPricePriv}">
                  <th scope="col" class="atg_numberValue"><fmt:message key="cart.items.finalPrice"/></th>
                </c:if>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${order.commerceItems}" var="item">
                <dsp:tomap var="sku" value="${item.auxiliaryData.catalogRef}"/>
                <dsp:tomap var="product" value="${item.auxiliaryData.productRef}"/>
                <tr>
                  <td>
                    <ul class="atg_commerce_csr_itemDesc">
                      <li>
                        <svc-ui:frameworkPopupUrl var="testPopup"
                          value="/include/order/product/productReadOnly.jsp"
                          context="${CSRConfigurator.contextRoot}"
                          windowId="${windowId}"
                          commerceItemId="${item.id}"/>
                        <a title="<fmt:message key='cart.items.quickView'/>"
                          href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                            popupPaneId: 'orderMainItemPopup',
                            title: '${fn:escapeXml(sku.displayName)}',
                            url: '${testPopup}',
                            onClose: function( args ) {  }} );return false;">
                          ${fn:escapeXml(sku.displayName)}
                        </a>
                      </li>
                      <li>
                        ${fn:escapeXml(item.catalogRefId)}
                      </li>
                    </ul>
                  </td>
                  <td><csr:inventoryStatus commerceItemId="${item.catalogRefId}"/></td>
                  <td class="atg_numberValue"><web-ui:formatNumber value="${item.quantity}"/></td>
                  <td class="atg_numberValue">${item.quantity + item.returnedQuantity} 
                    <c:if test="${item.returnedQuantity > 0}">
                      (${item.returnedQuantity})
                    </c:if>
                  </td>
                  <td class="atg_numberValue">
                    <csr:formatNumber value="${item.priceInfo.listPrice}" type="currency" currencyCode="${cart.current.priceInfo.currencyCode}"/>
                  </td>
                  <td class="atg_numberValue">
                    <c:set var="pi" value="${item.priceInfo}"/>
                    <ul class="atg_commerce_csr_itemDesc">
                      <li><csr:formatNumber value="${pi.amount}" type="currency" currencyCode="${cart.current.priceInfo.currencyCode}"/></li>
                      <c:if test="${pi.rawTotalPrice != pi.amount}">
                        <li class="oldPrice">
                          <csr:formatNumber value="${pi.rawTotalPrice}"
                            type="currency"
                            currencyCode="${cart.current.priceInfo.currencyCode}"/>
                        </li>
                      </c:if>
                   </ul>
                  </td>
                  <c:if test="${adjustPricePriv}">
                    <td class="atg_numberValue">
                     <c:if test="${item.priceInfo.amountIsFinal}">
                      <csr:formatNumber value="${item.priceInfo.amount}" type="currency" currencyCode="${cart.current.priceInfo.currencyCode}"/>
                     </c:if>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/orderMainPanel.jsp#2 $$Change: 1179550 $--%>
