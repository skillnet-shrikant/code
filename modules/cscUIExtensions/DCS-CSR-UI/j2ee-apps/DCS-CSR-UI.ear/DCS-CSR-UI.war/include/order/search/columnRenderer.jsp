<%--
 Order Search Data Column Renderer
 
 field - The current field being rendered
 orderItemMap - The current order item being rendered
 resourceBundle - The Resource Bundle from where the resource keys are defined
 resourceKey - The key to that maps to the resource string
 isHeading - Indicates if a heading is to be rendered or not
 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/search/columnRenderer.jsp#3 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderItemLookup" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/CommerceItemStateDescriptions" />
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete" />
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup" />
  <dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet" />
  <dsp:getvalueof var="field" param="field" />
  <dsp:getvalueof var="orderItem" param="orderItem" />
  <dsp:getvalueof var="orderItemMap" param="orderItemMap" />
  <dsp:getvalueof var="profileItem" param="profileItem" />
  <dsp:getvalueof var="resourceBundle" param="resourceBundle" />
  <dsp:getvalueof var="resourceKey" param="resourceKey" />  
  <dsp:getvalueof var="imageClosed" param="imageClosed" />
  <dsp:getvalueof var="imageOpen" param="imageOpen" />
  <dsp:getvalueof var="imagePath" param="imagePath" />
  <dsp:getvalueof var="isHeading" param="isHeading" />
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <c:if test="${empty isHeading}">
      <c:set var="isHeading" value="false" />
    </c:if>  
    <dsp:tomap var="profileItemMap" value="${profileItem}" />
    <csr:getCurrencyCode orderItem="${orderItem}">
      <c:set var="currencyCode" value="${currencyCode}" scope="request" />
    </csr:getCurrencyCode>

    <c:choose>
      <c:when test="${field=='toggle' and isHeading=='false'}">
        <img class="atg_tooltip_icon" id="<c:out value="${orderItemMap.id}"/>"
          src="<c:out value="${imagePath}"/><c:out value="${imageOpen}"/>" />
        <div dojoType="dijit.Tooltip"
          connectId="<c:out value="${orderItemMap.id}"/>"><dsp:importbean
          bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler" /> <dsp:include
          src="${CSRConfigurator.contextRoot}/panels/order/orderDetail.jsp">
          <dsp:param name="orderId" value="${orderItemMap.id}" />
        </dsp:include></div>
      </c:when>

      <c:when test="${field=='viewLink' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:include src="/panels/order/orderSearchResultSortHeading.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="resourceBundle" value="${resourceBundle}"/>
              <dsp:param name="resourceKey" value="${resourceKey}"/>
              <dsp:param name="fieldName" value="id"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
      
      <c:when test="${field=='viewLink' and isHeading=='false'}">
        <a href="#" class="blueU" title="<fmt:message key="view-order"/>"
          onclick="atg.commerce.csr.order.viewExistingOrder('<c:out value="${orderItemMap.id}" />','<c:out value="${orderItemMap.state}" />');return false;"><c:out
          value="${orderItemMap.orderNumber}" /></a>
      </c:when>

      <c:when test="${field=='lastName' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:include src="/panels/order/orderSearchResultSortHeading.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="resourceBundle" value="${resourceBundle}"/>
              <dsp:param name="resourceKey" value="${resourceKey}"/>
              <dsp:param name="fieldName" value="profile.lastName"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
        
      <c:when test="${field=='lastName'and isHeading=='false'}">
        <c:choose>
          <c:when test="${!empty profileItemMap.lastName}">
            <c:out value="${profileItemMap.lastName}" />
          </c:when>
          <c:otherwise>
            <fmt:message var="name" key="no-name" />  
            <c:out value="${name}"/>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='firstName' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:include src="/panels/order/orderSearchResultSortHeading.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="resourceBundle" value="${resourceBundle}"/>
              <dsp:param name="resourceKey" value="${resourceKey}"/>
              <dsp:param name="fieldName" value="profile.firstName"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>      
      </c:when>

      <c:when test="${field=='firstName'and isHeading=='false'}">
        <c:choose>
          <c:when test="${!empty profileItemMap.firstName}">
            <c:out value="${profileItemMap.firstName}" />
          </c:when>
          <c:otherwise>
            <c:out value=" "/>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:when test="${field=='total' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}"/>
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>      
      </c:when>
      
      <c:when test="${field=='total'and isHeading=='false'}">
        <dsp:droplet name="OrderItemLookup">
          <dsp:param name="id" value="${orderItemMap.id}" />
          <dsp:oparam name="output">
            <dsp:tomap var="order" param="element" />
            <dsp:tomap var="orderPriceInfoMap" value="${order.priceInfo}" />
            <csr:formatNumber value="${orderPriceInfoMap.amount+orderPriceInfoMap.shipping+orderPriceInfoMap.tax}" type="currency" currencyCode="${currencyCode}"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:when>

      <c:when test="${field=='itemsReturned' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}"/>
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
        
      <c:when test="${field=='itemsReturned' and isHeading=='false'}">
        <dsp:droplet name="OrderItemLookup">
          <dsp:param name="id" value="${orderItemMap.id}" />
          <dsp:oparam name="output">
            <dsp:tomap var="order" param="element" />
            <c:set var="totalItemCount" value="0" />
            <c:forEach items="${order.commerceItems}" var="item">
              <dsp:tomap var="commerceItemMap" value="${item}" />

              <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet">
                <dsp:param name="item" value="${item}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="itemQuantity" param="quantity"/>
                </dsp:oparam>
              </dsp:droplet>

              <c:set var="totalItemCount"
                value="${totalItemCount + itemQuantity}" />
              <dsp:tomap var="orderItemMap" value="${orderItemMap}" />
              <c:set var="returnedItemCount" value="${0}" scope="request" />
              <%
                /*figure out total return quantity in the order */
              %>
              <c:forEach items="${order.relationships}" var="relationship">
                <dsp:tomap var="relationshipMap" value="${relationship}" />
                <c:if
                  test="${relationshipMap.type eq 'shipItemRel' and  relationshipMap.commerceItem.repositoryId eq commerceItemMap.repositoryId}">
                  <dsp:droplet name="/atg/commerce/custsvc/returns/GetCommerceItemRelationshipReturnedQuantityDroplet">
                    <dsp:param name="itemRelationship" value="${relationship}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="returnedQuantity" param="returnedQuantity"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:set var="returnedItemCount" value="${returnedItemCount + returnedQuantity}" />
                </c:if>
              </c:forEach>
            </c:forEach>
          </dsp:oparam>
        </dsp:droplet>
        <web-ui:formatNumber value="${totalItemCount}"/>
        <c:if test="${returnedItemCount != 0}">
          <fmt:message key="left-bracket" />
          <web-ui:formatNumber value="${returnedItemCount}"/>
          <fmt:message key="right-bracket" />
        </c:if>
      </c:when>

      <c:when test="${field=='dateSubmitted' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:include src="/panels/order/orderSearchResultSortHeading.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="resourceBundle" value="${resourceBundle}"/>
              <dsp:param name="resourceKey" value="${resourceKey}"/>
              <dsp:param name="fieldName" value="submittedDate"/>
            </dsp:include>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>      
      </c:when>
      
      <c:when test="${field=='dateSubmitted' and isHeading=='false'}">
        <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
          <dsp:tomap var="orderItemMap" value="${orderItemMap}" />
          <web-ui:formatDate value="${orderItemMap.submittedDate}" type="date" dateStyle="short"/>
       
        </dsp:layeredBundle>
      </c:when>

      <c:when test="${field=='originator' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}"/>
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>
        
      <c:when test="${field=='originator' and isHeading=='false'}">
        <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
          <dsp:tomap var="orderItemMap" value="${orderItemMap}" />
          <c:choose>
            <c:when test="${empty orderItemMap.originOfOrder}">
              <fmt:message key='table.orders.originator.unknown' />
            </c:when>
            <c:otherwise>
              <fmt:message key='${orderItemMap.originOfOrder}' var="origin" />
              <c:out value="${origin}" />
            </c:otherwise>
          </c:choose>
        </dsp:layeredBundle>
      </c:when>

      <c:when test="${field=='state' and isHeading}">
        <c:choose>
          <c:when test="${not empty resourceKey}">
            <dsp:layeredBundle basename="${resourceBundle}">
              <fmt:message key="${resourceKey}"/>
            </dsp:layeredBundle>
          </c:when>
          <c:otherwise>
          </c:otherwise>
        </c:choose>
      </c:when>  
       
      <c:when test="${field=='state' and isHeading=='false'}">
        <dsp:droplet name="OrderStateDescriptions">
          <dsp:param name="state" value="${orderItemMap.state}" />
          <dsp:param name="elementName" value="stateDescription" />
          <dsp:oparam name="output">
            <dsp:valueof param="stateDescription"></dsp:valueof>
          </dsp:oparam>
        </dsp:droplet>
        <c:out value="${orderStateDescription}" />
      </c:when>


      <c:when test="${field=='workOn' and isHeading=='false'}">
        <a href="#" class="blueU" title="<fmt:message key="select-order"/>"
          onclick="atg.commerce.csr.order.loadExistingOrder('<c:out value="${orderItemMap.id}" />','<c:out value="${orderItemMap.state}" />');return false;"><fmt:message
          key="table-options-workon" /></a>
      </c:when>
    </c:choose>
  </dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/search/columnRenderer.jsp#3 $$Change: 1179550 $--%>