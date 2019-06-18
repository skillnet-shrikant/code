<%--
 Order Data Column Renderer
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/purchasedItemsColumnRenderer.jsp#3 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%><%@ include file="/include/top.jspf"%>
<dsp:page>
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
<dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
<dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/GetCommerceItemQuantityDroplet"/>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:getvalueof var="field" param="field"/>
  <dsp:getvalueof var="colIndex" param="colIndex"/>
  <dsp:getvalueof var="commerceItem" param="commerceItem"/>
  <dsp:tomap var="commerceItemMap" value="${commerceItem}"/> 
  <dsp:tomap var="orderItemMap" value="${commerceItemMap.order}"/> 

  <c:choose>
  <c:when test="${field == 'id'}">
    "id":"${commerceItemMap.repositoryId}"
  </c:when>
  <c:when test="${field == 'quantity'}">
    <dsp:droplet name="GetCommerceItemQuantityDroplet">
      <dsp:param name="item" value="${commerceItem}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="itemQuantity" param="quantity"/>
      </dsp:oparam>
    </dsp:droplet>
    "quantity":"<web-ui:formatNumber value="${itemQuantity}"/>"
  </c:when>
  <c:when test="${field == 'purchasedDate'}">
    "purchasedDate":"<web-ui:formatDate type="date" value="${orderItemMap.submittedDate}" dateStyle="short"/>"
  </c:when>
  <c:when test="${field == 'orderId'}">
     <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    "orderId":"<a href=\"#\" class=\"blueU\" title=\"<fmt:message key="view-order"/>\" onclick=\"atg.commerce.csr.order.viewExistingOrder(\'${orderItemMap.id}\',\'${orderItemMap.state}\');return false;\">${orderItemMap.id}</a>"
    </dsp:layeredBundle>
  </c:when>
  <c:when test="${field == 'site'}">
    <c:if test="${isMultiSiteEnabled == true}">
      <dsp:getvalueof var="siteId" param="commerceItem.siteId"/>
      <dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
      <dsp:param name="siteId" value="${siteId}"/>
      <dsp:oparam name="output">
      <dsp:getvalueof param="site" var="site"/>
      <dsp:getvalueof var="siteIcon" param="site.favicon"/>
      <dsp:getvalueof var="siteIconHover" param="site.name"/>
        <c:choose>
          <c:when test="${!empty siteIcon}">
            <c:set var="siteImgURL" value="${siteIcon}"/>
          </c:when>
          <c:otherwise>
            <c:set var="siteImgURL" value="${CSRConfigurator.defaultSiteIconURL}"/>
          </c:otherwise>
        </c:choose>  
      </dsp:oparam>
      </dsp:droplet>  
      "site": "<img src=\"${siteImgURL}\" title=\"${siteIconHover}\" class=\"atg_commerce_csr_storeIcon\"/>"
    </c:if>
    <c:if test="${isMultiSiteEnabled == false}">
      "site": "\&nbsp;"
    </c:if>
  
    
  </c:when>
  
  <c:when test="${field == 'itemDescription'}">
    <dsp:droplet name="/atg/commerce/custsvc/catalog/SKULookup">
    <dsp:param name="id" value="${commerceItemMap.catalogRefId}"/>
    <dsp:oparam name="output">
     <dsp:getvalueof var="sku" param="element"/>
     <dsp:tomap var="skuItemMap" value="${sku}"/> 
     <dsp:getvalueof var="itemSummary" param="element.displayName"/>

      <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
      
      <svc-ui:frameworkPopupUrl var="commerceItemPopup"
        value="/include/order/product/productReadOnly.jsp"
        context="${CSRConfigurator.contextRoot}"
        windowId="${windowId}"
        productId="${commerceItemMap.productId}"/>

      "itemDescription": "<div class=\"atg_commerce_csr_productItemList\"><ul class=\"atg_commerce_csr_itemDesc\"><li><a title=\"<fmt:message key='cart.items.quickView'/>\"  href=\"#\" onclick=\"atg.commerce.csr.common.showPopupWithReturn({popupPaneId: \'productQuickViewPopup\', title: \'<c:out value="${fn:escapeXml(skuItemMap.displayName)}"/>\', url: \'${commerceItemPopup}\', onClose: function( args ) {  }} );return false;\"> ${fn:escapeXml(skuItemMap.displayName)}</a></li><li class=\"atg_commerce_csr_returnItemSku\">${commerceItemMap.catalogRefId}</li></ul></div>  "
      
      </dsp:layeredBundle>
      
    </dsp:oparam>
    </dsp:droplet>
  </c:when>
  <c:when test="${field == 'isReturnable'}">
  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
  <dsp:droplet name="/atg/commerce/custsvc/order/IsItemReturnable">
  <dsp:param name="item" value="${commerceItem}"/>
  <dsp:oparam name="true">
    <c:choose>
      <c:when test ="${envTools.siteAccessControlOn eq 'true' }">
        <dsp:getvalueof var="siteId" param="commerceItem.siteId"/>
        <dsp:droplet name="IsSiteAccessibleDroplet">
          <dsp:param name="siteId" value="${siteId}"/>
          <dsp:oparam name="true">
            "isReturnable":"<a id=\"createReturnExchange\" href=\"#\" onclick=\"atg.commerce.csr.order.returns.initiateReturnProcess({orderId: \'${orderItemMap.id}\'});return false;\"><fmt:message key="start-return"/></a>"
          </dsp:oparam>
          <dsp:oparam name="false">
            "isReturnable":"&nbsp;"
          </dsp:oparam>
        </dsp:droplet>
      </c:when>
      <c:otherwise>
        "isReturnable":"<a id=\"createReturnExchange\" href=\"#\" onclick=\"atg.commerce.csr.order.returns.initiateReturnProcess({orderId: \'${orderItemMap.id}\'});return false;\"><fmt:message key="start-return"/></a>"
      </c:otherwise>
    </c:choose>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:getvalueof param="returnableDescription" var="returnableDescription"/>
    "isReturnable":"${returnableDescription}"
  </dsp:oparam>
  </dsp:droplet>
  </dsp:layeredBundle>
  </c:when>
  <c:otherwise>
  </c:otherwise>
  </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/purchasedItemsColumnRenderer.jsp#3 $$Change: 1179550 $--%>
