<%--
 Order Data Column Renderer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/columnRenderer.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/GetCommerceItemRelationshipReturnedQuantityDroplet"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="colIndex" param="colIndex"/>
  <dsp:getvalueof var="orderItem" param="orderItem"/>
  <dsp:getvalueof var="orderItemMap" param="orderItemMap"/>

  <csr:getCurrencyCode orderItem="${orderItem}">
    <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode>
  
  <c:choose>
  <c:when test="${field == 'id'}">
    "id":"${orderItemMap.id}"
  </c:when>

  <c:when test="${field == 'viewLink'}">
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      "viewLink":"<a href=\"#\" class=\"blueU\" title=\"<fmt:message key="view-order"/>\" onclick=\"atg.commerce.csr.order.viewExistingOrder(\'${orderItemMap.id}\',\'${orderItemMap.state}\');return false;\">${orderItemMap.orderNumber}</a>"
    </dsp:layeredBundle>
  </c:when>

  <c:when test="${field == 'selectLink'}">
    <dsp:layeredBundle basename="atg.commerce.csr.Messages">
      "selectLink":"<a href=\"#\" class=\"blueU\" title=\"<fmt:message key="select-order"/>\" onclick=\"atg.commerce.csr.order.loadExistingOrder(\'${orderItemMap.id}\',\'${orderItemMap.state}\');return false;\"><fmt:message key="table-options-workon"/></a>"
    </dsp:layeredBundle>
  </c:when>

  <c:when test="${field == 'itemCount'}">
    <c:set var="itemCount" value="0"/>
    <c:set var="returnedItemCount" value="0"/>
    <c:forEach items="${orderItemMap.commerceItems}" var="item">
      <dsp:tomap var="itemMap" value="${item}"/>
      <dsp:droplet name="GetCommerceItemQuantityDroplet">
        <dsp:param name="item" value="${item}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="itemQuantity" param="quantity"/>
        </dsp:oparam>
      </dsp:droplet>
      <c:set var="itemCount" value="${itemCount + itemQuantity}"/>
    </c:forEach>
    <c:forEach items="${orderItemMap.relationships}" var="relationship">
      <dsp:tomap var="relationshipMap" value="${relationship}"/>
      <c:if test="${relationshipMap.type eq 'shipItemRel'}">
        <dsp:droplet name="GetCommerceItemRelationshipReturnedQuantityDroplet">
          <dsp:param name="itemRelationship" value="${relationship}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="returnedQuantity" param="returnedQuantity"/>
          </dsp:oparam>
        </dsp:droplet>
        <c:set var="returnedItemCount" value="${returnedItemCount + returnedQuantity}"/>
      </c:if>
    </c:forEach>
    <web-ui:formatNumber var="itemCountDisplay" value="${itemCount}"/>
    <c:if test="${returnedItemCount > 0}">
      <c:set var="itemCount" value="${itemCount + returnedItemCount}"/>
      <c:set var="itemCountDisplay">
        <fmt:message key="table.orders.itemCount">
          <fmt:param><web-ui:formatNumber value="${itemCount}"/></fmt:param>
          <fmt:param><web-ui:formatNumber value="${returnedItemCount}"/></fmt:param>
        </fmt:message>
      </c:set>
    </c:if>
    "itemCount":"${itemCountDisplay}"
  </c:when>

  <c:when test="${field == 'total'}">
    <dsp:tomap var="priceInfo" value="${orderItemMap.priceInfo}"/>
    <%--
    <c:set var="totalValue"><csr:formatNumber value="${priceInfo.amount+priceInfo.shipping+priceInfo.tax}" type="currency" currencyCode="${currencyCode}"/></c:set>
    --%>
    <csr:formatNumber var="totalValue" value="${priceInfo.amount+priceInfo.shipping+priceInfo.tax}" type="currency" currencyCode="${currencyCode}"/>
    "total":"${totalValue}"
  </c:when>

  <c:when test="${field == 'itemSummary'}">
    <c:set var="itemSummary" value=""/>
    <c:if test="${fn:length(orderItemMap.commerceItems) > 0}">
      <dsp:tomap var="firstItem" value="${orderItemMap.commerceItems[0]}"/>
      <dsp:droplet name="CSRProductLookup">
        <dsp:param name="id" value="${firstItem.productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="itemSummary" param="element.displayName"/>
          <c:set var="itemSummary" value="${itemSummary}"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <c:if test="${fn:length(orderItemMap.commerceItems) > 1}">
      <dsp:tomap var="secondItem" value="${orderItemMap.commerceItems[1]}"/>
      <dsp:droplet name="CSRProductLookup">
        <dsp:param name="id" value="${secondItem.productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="itemName" param="element.displayName"/>
          <c:set var="itemSummary" value="${itemSummary}; ${itemName}"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:if>
    <c:if test="${fn:length(orderItemMap.commerceItems) > 2}">
      <fmt:message key="text.ellipsis" var="ellipsis" />
      <c:set var="itemSummary" value="${itemSummary} ${ellipsis}"/>
    </c:if>
    "itemSummary":"${fn:escapeXml(itemSummary)}"
  </c:when>

  <c:when test="${field == 'state' || field == 'dbstate'}">
    <c:set var="orderStateDescription">
      <dsp:droplet name="OrderStateDescriptions">
        <dsp:param name="state" value="${orderItemMap.state}"/>
        <dsp:param name="elementName" value="stateDescription"/>
        <dsp:oparam name="output">
          <dsp:valueof param="stateDescription"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </c:set>
    <c:choose>
    <c:when test="${field == 'dbstate'}">
      "dbstate":"${orderItemMap.state}"
    </c:when>
    <c:when test="${field == 'state'}">
      "state":"${fn:escapeXml(orderStateDescription)}"
    </c:when>
    <c:otherwise>
    </c:otherwise>
    </c:choose>
  </c:when>

  <c:when test="${field == 'originator'}">
    <c:choose>
      <c:when test="${empty orderItemMap.originOfOrder}">
        <fmt:message key='table.orders.originator.unknown' var="origin" />
        "originator":"${origin}"
      </c:when>
      <c:otherwise>
        <fmt:message key='${orderItemMap.originOfOrder}' var="origin" />
        "originator":"${origin}"
      </c:otherwise>
    </c:choose> 
  </c:when>

  <c:when test="${field == 'submittedDate'}">
    "submittedDate":"<web-ui:formatDate type="date" value="${orderItemMap.submittedDate}" dateStyle="short"/>"
  </c:when>

  <c:when test="${field == 'creationDate'}">
    "creationDate":"<web-ui:formatDate type="date" value="${orderItemMap.creationDate}" dateStyle="short"/>"
  </c:when>

  <c:when test="${field == 'profileId'}">
    "profileId":"${orderItemMap.profileId}"
  </c:when>

  <c:when test="${field == 'customerName'}">
    <c:set var="lastName">
      <fmt:message key="no-name"/>
    </c:set>
    <c:set var="customerName">
    <dsp:droplet name="ProfileRepositoryItemServlet">
      <dsp:param name="id" value="${orderItemMap.profileId}"/>
      <dsp:oparam name="output">
        <dsp:droplet name="IsEmpty">
          <dsp:param name="value" param="item"/>
          <dsp:oparam name="true">
            <fmt:message key="no-name"/>
          </dsp:oparam>
          <dsp:oparam name="false">
            <dsp:getvalueof var="profileIsTransient" param="item.transient"/>
            <c:if test="${profileIsTransient}">
              <fmt:message key="no-name"/>
            </c:if>
            <c:if test="${!profileIsTransient}">
              <dsp:getvalueof var="lastname" param="item.lastname"/>
              <dsp:getvalueof var="firstname" param="item.firstname"/>
              <fmt:message key="lastname-firstname">
                <fmt:param value="${! empty lastname ? fn:escapeXml(lastname) : ''}"/>
                <fmt:param value="${! empty firstname ? fn:escapeXml(firstname) : ''}"/>
              </fmt:message>
              <c:set var="lastName" value="${lastname}"/>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
    </c:set>
    "lastName":"${fn:escapeXml(lastName)}"
    "customerName":"${fn:escapeXml(customerName)}"
  </c:when>

  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/columnRenderer.jsp#2 $$Change: 1179550 $--%>