<%--
 This page defines the order details view
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDetail.jsp#3 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/CommerceItemStateDescriptions"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderItemLookup"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
    <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
    <dsp:importbean bean="/atg/commerce/custsvc/returns/GetCommerceItemRelationshipReturnedQuantityDroplet"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet"/>

    <dsp:importbean bean="/atg/multisite/Site"/> 
    <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
      
    <dsp:getvalueof var="orderId" param="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.parameterMap.orderId"/>
    <dsp:getvalueof var="orderId" param="orderId"/> 
    
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      <dsp:droplet name="OrderItemLookup">
        <dsp:param name="id" value="${orderId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="orderItem" param="element"/>

          <csr:getCurrencyCode orderItem="${orderItem}">
            <c:set var="currencyCode" value="${currencyCode}" scope="request" />
          </csr:getCurrencyCode>

        <dsp:tomap var="orderItemMap" value="${orderItem}"/>
        <dsp:tomap var="orderPriceInfoMap" value="${orderItemMap.priceInfo}"/> 
        <c:set var="totalValue"><csr:formatNumber type="currency" currencyCode="${currencyCode}" value="${orderPriceInfoMap.amount+orderPriceInfoMap.shipping+orderPriceInfoMap.tax}"/></c:set>
          
        <div class="atg_commerce_csr_hoverPopupTitle">
          <a href="#" class="blueU" title="<fmt:message key="view-order"/>" onclick="atg.commerce.csr.order.viewExistingOrder('<c:out value="${orderId}" />','${orderItemMap.state}');return false;"><c:out value="${orderId}" /></a>, 
          <c:out value="${totalValue}" />
        </div>  
        
        <c:set var="numOrders" value="${fn:length(order.commerceItems)}"/>
        <c:set var="commerceItemsDisplayCount" value="3"/>
        
        <h4 style="margin-bottom:0px;padding-bottom:4px"><fmt:message key="orderDetails"/>
          <c:if test="${numOrders > 3}">
            &nbsp;&nbsp; <a href="#" class="atg_commerce_csr_viewAll" onclick="atg.commerce.csr.order.viewExistingOrder('<c:out value="${orderId}" />','${orderItemMap.state}');return false;"><fmt:message key="orderDetailsMore"/></a>
          </c:if>
        </h4>

        <dsp:droplet name="/atg/commerce/custsvc/order/scheduled/IsScheduledOrderTemplate">
          <dsp:param name="order" param="element"/>
          <dsp:oparam name="true">
            <dsp:droplet name="/atg/commerce/order/scheduled/ScheduledOrderLookup">
            <dsp:param name="templateId" value="${orderId}"/>
            <dsp:oparam name="output">
            <dsp:getvalueof var="scheduledOrders" param="scheduledOrders"/>
              <dsp:include src="/panels/order/displaySchedulesTable.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="scheduledOrderItems" value="${scheduledOrders}"/>
              </dsp:include>
              <br>
            </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
          
        <table class="atg_dataTable" cellspacing="0" cellpadding="0" summary="item details">
          <thead>
            <tr>
              <%-- Site Icon Heading --%>
              <c:if test="${isMultiSiteEnabled == true}">
                <th class="atg_commerce_csr_siteIcon"></th>
              </c:if>
              <th><fmt:message key="item-description"/></th>
              <th><fmt:message key="status"/></th>
              <th class="atg_numberValue"><fmt:message key="item-returned"/></th>
              <th class="atg_numberValue"><fmt:message key="price-each"/></th>
              <th class="atg_numberValue"><fmt:message key="total-price"/></th>
            </tr>
          </thead>
          <tbody>
          
          <c:forEach items="${orderItemMap.commerceItems}" begin="0" end="${commerceItemsDisplayCount - 1}" var="item" varStatus="status">
            <dsp:tomap var="commerceItemMap" value="${item}"/>
            <tr>
              <c:if test="${isMultiSiteEnabled == true}">
                <td class="atg_commerce_csr_siteIcon">
                  <c:set var="siteId" value="${commerceItemMap.siteId}"/>                
                  <csr:siteIcon siteId="${siteId}" />
                </td>
              </c:if>
              <td>
                <ul class="atg_commerce_csr_itemDesc">
                  <dsp:droplet name="CSRProductLookup">
                    <dsp:param name="id" value="${commerceItemMap.productId}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="displayName" param="element.displayName"/>
                      <li>
                        <c:choose>
                          <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                            <a href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                                  popupPaneId:'productQuickViewPopup',
                                  title:'<c:out value="${fn:escapeXml(displayName)}"/>',
                                  url:'${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&productId=${commerceItemMap.productId}&siteId=${siteId}',
                                  onClose:function(args) { }});return false;"
                             title="<fmt:message key='quickView'/>">${fn:escapeXml(displayName)}</a>
                          </c:when>
                          <c:otherwise>
                            <a href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                                  popupPaneId:'productQuickViewPopup',
                                  title:'<c:out value="${fn:escapeXml(displayName)}"/>',
                                  url:'${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&productId=${commerceItemMap.productId}',
                                  onClose:function(args) {}});return false;"
                             title="<fmt:message key='quickView'/>">${fn:escapeXml(displayName)}</a>
                          </c:otherwise>
                        </c:choose>  
                      </li>
                    </dsp:oparam>
                  </dsp:droplet>
                  <li>${commerceItemMap.catalogRefId}</li>
                </ul>
              </td>
              <td>
                <dsp:droplet name="IsOrderIncomplete">
                  <dsp:param name="orderItem" param="element"/>
                  <dsp:oparam name="true">
                    <csr:inventoryStatus commerceItemId="${commerceItemMap.catalogRefId}"/>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:droplet name="CommerceItemStateDescriptions">
                      <dsp:param name="state" value="${commerceItemMap.state}"/>
                      <dsp:param name="elementName" value="stateDescription"/>
                      <dsp:oparam name="output">
                        <dsp:valueof param="stateDescription"></dsp:valueof>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
              </td>

              <c:set var="returnedItemCount" value="0"/>
              <%/*figure out total return quantity in the order */%>
              <c:forEach items="${orderItemMap.relationships}"  var="relationship">
                <dsp:tomap var="relationshipMap" value="${relationship}"/>
                <c:if test="${relationshipMap.type eq 'shipItemRel' and  relationshipMap.commerceItem.repositoryId eq commerceItemMap.repositoryId}">
                  <dsp:droplet name="GetCommerceItemRelationshipReturnedQuantityDroplet">
                    <dsp:param name="itemRelationship" value="${relationship}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="returnedQuantity" param="returnedQuantity"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:set var="returnedItemCount" value="${returnedItemCount + returnedQuantity}"/>
                </c:if>
              </c:forEach>
              
              <td class="atg_numberValue">
                <dsp:droplet name="GetCommerceItemQuantityDroplet">
                  <dsp:param name="item" value="${item}"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="itemQuantity" param="quantity"/>
                  </dsp:oparam>
                </dsp:droplet>
                ${itemQuantity + returnedItemCount}
                <c:if test="${returnedItemCount > 0}">
                  (${returnedItemCount})
                </c:if>
              </td>
              <dsp:tomap var="itemPriceInfoMap" value="${commerceItemMap.priceInfo}"/>
              <td class="atg_numberValue">
                <dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="product" value="${commerceItemMap.productId}"/>
                  <dsp:param name="sku" value="${commerceItemMap.catalogRefId}"/>
                </dsp:include>
              </td>
              <td class="atg_numberValue">
                <c:if test="${itemPriceInfoMap.rawTotalPrice != itemPriceInfoMap.amount}">
                  <span class="atg_commerce_csr_common_content_strikethrough">
                    <csr:formatNumber value="${itemPriceInfoMap.rawTotalPrice}" type="currency" currencyCode="${currencyCode}"/>
                  </span>
                  &nbsp;
                </c:if>
                <csr:formatNumber value="${itemPriceInfoMap.amount}" type="currency" currencyCode="${currencyCode}"/>
              </td>
            </tr>
          </c:forEach>
          </tbody>
          </table>
          <!-- Display indicator if there are more commerce items in the order than the default displayed of 3 -->
          <c:if test="${fn:length(orderItemMap.commerceItems) > commerceItemsDisplayCount }">
            <div>
              <div style="float:right">
                (...)
              </div>
            </div>
          </c:if>
        </dsp:oparam>
        <dsp:oparam name="empty">
          <%--  Cannot load order --%>
        </dsp:oparam>
      </dsp:droplet>

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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDetail.jsp#3 $$Change: 1179550 $--%>
