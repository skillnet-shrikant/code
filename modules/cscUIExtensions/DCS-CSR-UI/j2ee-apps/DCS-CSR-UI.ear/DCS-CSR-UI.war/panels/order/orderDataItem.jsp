<%--
 Customer Order History JSON Object Creation
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDataItem.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page>
<dsp:getvalueof var="orderRepositoryItem" param="orderRepositoryItem"/>
<dsp:tomap var="orderItemMap" value="${orderRepositoryItem}"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <json:object>
 
      <json:property name="id" value='${orderItemMap.id}'/>
      <c:set var="itemCount" value="0"/>
      <c:set var="returnedItemCount" value="0"/>
      
      <json:array name="items" items="${orderItemMap.commerceItems}" var="item">
        <dsp:tomap var="itemMap" value="${item}"/>
        <c:set var="itemCount" value="${itemCount + itemMap.quantity}"/>
      </json:array>

      <json:array name="relationships" items="${orderItemMap.relationships}" var="relationship">
        <dsp:tomap var="relationshipMap" value="${relationship}"/>
        <c:if test="${relationshipMap.type eq 'shipItemRel' }">
          <c:set var="returnedItemCount" value="${returnedItemCount + relationshipMap.returnedQuantity}"/>
        </c:if>
      </json:array>
      
      <c:set var="itemCountDisplay" value="${itemCount}"/>
      <c:if test="${returnedItemCount > 0}">
        <c:set var="itemCount" value="${itemCount + returnedItemCount}"/>
        <c:set var="itemCountDisplay" value="${itemCount} (${returnedItemCount})"/>
      </c:if>
      <json:property name="itemCount" value="${itemCountDisplay}"/>
      <dsp:tomap var="priceInfo" value="${orderItemMap.priceInfo}"/>
      <c:set var="totalValue">
      <csr:formatNumber value="${priceInfo.amount+priceInfo.shipping+priceInfo.tax}" type="currency" currencyCode="${priceInfo.currencyCode}"/>
      </c:set>
      <json:property name="total" value="${totalValue}"/>
      
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
      <json:property name="itemSummary" value="${itemSummary}"/>

      
      <c:set var="orderStateDescription">
        <dsp:droplet name="OrderStateDescriptions">
          <dsp:param name="state" value="${orderItemMap.state}"/>
          <dsp:param name="elementName" value="stateDescription"/>
          <dsp:oparam name="output">
            <dsp:valueof param="stateDescription"></dsp:valueof>
          </dsp:oparam>
        </dsp:droplet>
      </c:set>
      <json:property name="dbstate" value='${orderItemMap.state}'/>
      <json:property name="state" value='${orderStateDescription}'/>
      <fmt:message key='${orderItemMap.originOfOrder}' var="origin" />
      <json:property name="originator" value='${origin}'/>
      <json:property name="submittedDate"><web-ui:formatDate type="both" value="${orderItemMap.submittedDate}" dateStyle="short" timeStyle="short"/></json:property>
      <json:property name="creationDate"><web-ui:formatDate type="both" value="${orderItemMap.creationDate}" dateStyle="short" timeStyle="short"/></json:property>
      <json:property name="profileId" value="${orderItemMap.profileId}"/>
    
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
      <json:property name="lastName" value="${lastName}"/>
      <json:property name="customerName" value="${customerName}"/>
      
      
      
      </json:object>
    </dsp:layeredBundle>

    </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/orderDataItem.jsp#2 $$Change: 1179550 $--%>
