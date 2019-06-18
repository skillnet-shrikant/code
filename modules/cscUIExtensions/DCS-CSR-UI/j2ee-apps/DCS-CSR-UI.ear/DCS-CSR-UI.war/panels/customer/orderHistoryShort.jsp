<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/orderHistoryShort.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:importbean bean="/atg/commerce/custsvc/util/AltColor" />
  <dsp:importbean bean="/atg/commerce/order/AdminOrderLookup" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions" />
  <dsp:importbean bean="/atg/dynamo/droplet/Range" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/GetOrderItemsForProfile"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/GetCommerceItemRelationshipReturnedQuantityDroplet"/>

  <dsp:droplet name="GetOrderItemsForProfile">
    <dsp:param name="profileId" param="customerId" />
    <dsp:param name="numOrders" value="4" />
    <dsp:param name="sortBy" value="submittedDate" />
    <dsp:param name="ascending" value="false" />
    <dsp:oparam name="output">

      <dsp:droplet name="Range">
        <dsp:param name="start" param="rangeStartIndex" />
        <dsp:param name="howMany" param="rangeNumOrders" />
        <dsp:param name="sortProperties" value="-submittedDate" />
        <dsp:param name="array" param="orders" />
        <dsp:param name="elementName" value="order" />
        
        <dsp:oparam name="outputStart">
          <dsp:getvalueof var="numOrders" param="size"/>
          
          <h4 style="margin-bottom:0px;padding-bottom:4px"><fmt:message key="orderHistory"/>
          <c:if test="${numOrders > 3}">
            &nbsp;&nbsp; <a href="#" class="atg_commerce_csr_viewAll" onclick="openCustomerInfo('<dsp:valueof param="customerId" />');"><fmt:message key="orderHistoryMore"/></a>
          </c:if>
          </h4>
          
          <table summary="Order History" class="atg_dataTable" cellpadding="0" cellspacing="0">
            <thead>
              <tr>
                <th scope="col" style="width:80px;"><fmt:message key="table.orders.orderId"/></th>
                <th scope="col"><fmt:message key="table.orders.items"/></th>
                <th scope="col"><fmt:message key="table.orders.total"/></th>
                <th scope="col"><fmt:message key="table.orders.submitted"/></th>
                <th scope="col"><fmt:message key="table.orders.status"/></th>
              </tr>
            </thead>
            <tbody>
        </dsp:oparam>
        
        <dsp:oparam name="output">
            <dsp:getvalueof var="index" param="index"/>
            <tr class="${index % 2 == 0 ? '' : 'atg_altRow'}">
            <dsp:tomap var="orderItem" param="order"/>
            <td>
              <a href="#" onclick="atg.commerce.csr.order.viewExistingOrder('${orderItem.repositoryId}');return false;">${orderItem.repositoryId}</a>
            </td>
            <td>
              
              
              <%/*figure out total quantity of items in the order */%>
              <c:set var="itemCount" value="0"/>
              <c:forEach items="${orderItem.commerceItems}"  var="item">
                <dsp:droplet name="GetCommerceItemQuantityDroplet">
                  <dsp:param name="item" value="${item}"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="itemQuantity" param="quantity"/>
                  </dsp:oparam>
                </dsp:droplet>
                <c:set var="itemCount" value="${itemCount + itemQuantity}"/>
              </c:forEach>
              
              <c:set var="returnedItemCount" value="0"/>

              <%/*figure out total return quantity in the order */%>
              <c:forEach items="${orderItem.relationships}"  var="relationship">
                <dsp:tomap var="relationshipMap" value="${relationship}"/>
                <c:if test="${relationshipMap.type eq 'shipItemRel' }">
                  <dsp:droplet name="GetCommerceItemRelationshipReturnedQuantityDroplet">
                    <dsp:param name="itemRelationship" value="${relationship}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="returnedQuantity" param="returnedQuantity"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:set var="returnedItemCount" value="${returnedItemCount + returnedQuantity}"/>
                </c:if>
              </c:forEach>

              <web-ui:formatNumber value="${itemCount + returnedItemCount}"/>
              <c:if test="${returnedItemCount > 0}">
                (<web-ui:formatNumber value="${returnedItemCount}"/>)
              </c:if>fs
            </td>
            <td>
            <c:if test="${!empty orderItem.priceInfo}">
              <dsp:tomap var="orderPriceInfoMap" value="${orderItem.priceInfo}"/>
              <csr:formatNumber value="${orderPriceInfoMap.amount+orderPriceInfoMap.shipping+orderPriceInfoMap.tax}" type="currency" currencyCode="${orderPriceInfoMap.currencyCode}"/>
            </c:if>
              </td>
            <td>
              <dsp:valueof date="h:mma" value="${orderItem.submittedDate}"><fmt:message key="table.orders.notSubmitted"/></dsp:valueof>
              <dsp:valueof date="MMM d, yyyy" value="${orderItem.submittedDate}" />
            </td>
            <td>
              <dsp:droplet name="OrderStateDescriptions">
                <dsp:param name="state" value="${orderItem.state}"/>
                <dsp:param name="elementName" value="stateDescription"/>
                <dsp:oparam name="output">
                  <dsp:valueof param="stateDescription"></dsp:valueof>
                </dsp:oparam>
              </dsp:droplet>
            </td>
          </tr>
        </dsp:oparam>
        			
        <dsp:oparam name="outputEnd">
            </tbody>
          </table>
        </dsp:oparam>

        <dsp:oparam name="empty">
          <tr>
            <td colspan="7">!<fmt:message key="table.orders.noOrdersForTheCustomer"/>!</td>
          </tr>
        </dsp:oparam>
        
      </dsp:droplet>
      <!-- /Range -->

    </dsp:oparam>
    <dsp:oparam name="empty">
      <h4 style="padding-bottom:4px"><fmt:message key="orderHistory"/></h4>
      <table summary="Customer Search Results" class="atg_dataTable" cellpadding="0"
        cellspacing="0">
        <thead>
          <tr>
            <th scope="col" style="width:80px;"><fmt:message key="table.orders.orderId"/></th>
            <th scope="col"><fmt:message key="table.orders.items"/></th>
            <th scope="col"><fmt:message key="table.orders.total"/></th>
            <th scope="col"><fmt:message key="table.orders.submitted"/></th>
            <th scope="col"><fmt:message key="table.orders.status"/></th>
          </tr>
        </thead>
        <tbody>
        <tr>
          <td colspan="7"><fmt:message key="table.orders.noOrdersForTheCustomer"/></td>
        </tr>
        </tbody>
      </table>
    </dsp:oparam>
    <dsp:oparam name="error">
      <table summary="Customer Search Results" class="atg_dataTable" cellpadding="0"
        cellspacing="0">
        <thead>
          <tr>
            <th scope="col" style="width:80px;"><fmt:message key="table.orders.orderId"/></th>
            <th scope="col"><fmt:message key="table.orders.items"/></th>
            <th scope="col"><fmt:message key="table.orders.total"/></th>
            <th scope="col"><fmt:message key="table.orders.submitted"/></th>
            <th scope="col"><fmt:message key="table.orders.status"/></th>
          </tr>
        </thead>
        <tbody>
        <tr valign="top">
          <td colspan="7"><dsp:valueof param="errorMsg"><fmt:message key="table.orders.problemAccessingOrders"/></dsp:valueof></td>
        </tr>
        </tbody>
      </table>
    </dsp:oparam>
  </dsp:droplet>
  <!-- /Order Lookup -->
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/orderHistoryShort.jsp#2 $$Change: 1179550 $--%>
