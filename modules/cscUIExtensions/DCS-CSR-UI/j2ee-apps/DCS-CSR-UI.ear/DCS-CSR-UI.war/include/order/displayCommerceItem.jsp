<%--
Display the appropriate details for the shipping group.

Expected params
shippingGroup : The shipping group.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsOrderIncomplete"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/IsHighlightedState"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShippingRelationshipStateDescriptions"/>
  <dsp:getvalueof var="order" param="currentOrder"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>
  <dsp:getvalueof var="isEmailView" param="isEmailView"/>
  <dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet"/>

    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <dsp:getvalueof var="shippingGroup" param="currentShippingGroup"/>
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

<c:if test="${isExistingOrderView == true}">
      <div id="atg_commerce_csr_finish_shippinglineItemPane" class="atg_commerce_csr_togglePanel"
           dojoType="dijit.TitlePane" open="false"
           title="<fmt:message key="finishOrder.shippingSummary.Items"/>">
</c:if>

    <table class="atg_dataTable">
    <thead>
    <c:if test="${isMultiSiteEnabled == true}">
      <th class="atg_commerce_csr_siteIcon"></th>
    </c:if>
    <th><fmt:message key='shippingSummary.commerceItem.header.itemDesc'/></th>
      <th><fmt:message key='shippingSummary.commerceItem.header.status'/></th>
      <th class="atg_numberValue"><fmt:message key='shippingSummary.commerceItem.header.qty'/></th>
    <c:forEach items="${shippingGroup.commerceItemRelationships}"
                      var="ciRelationship" varStatus="ciIndex">
      <tr>
        <c:if test="${isMultiSiteEnabled == true}">
          <c:set var="siteId" value="${ciRelationship.commerceItem.auxiliaryData.siteId}"/>
          <c:set var="isSiteDeleted" value=""/>

         <td class="atg_commerce_csr_siteIcon">
          <dsp:droplet name="GetSiteDroplet">
            <dsp:param name="siteId" value="${siteId}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof param="site" var="site"/>
              <dsp:getvalueof var="siteIcon" param="site.favicon"/>
              <dsp:getvalueof var="siteIconHover" param="site.name"/>
              <c:choose>
                <c:when test="${!empty isEmailView && isEmailView == true}">
                  <c:out value="${siteIconHover}" />
                </c:when>
                <c:otherwise>
                <csr:siteIcon siteId="${site.id}"/>
                </c:otherwise>
              </c:choose>
            </dsp:oparam>
            <dsp:oparam name="empty">
              <c:set var="isSiteDeleted" value="true"/>
              <csr:siteIcon siteId="${site.id}"/>
            </dsp:oparam>
          </dsp:droplet>  
         </td>


        </c:if>
        <td>
          <ul class="atg_commerce_csr_itemDesc">
            <li>
              <dsp:tomap var="sku" value="${ciRelationship.commerceItem.auxiliaryData.catalogRef}"/>
              <c:choose>
                <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                  <svc-ui:frameworkPopupUrl var="shippingCommerceItemPopup"
                    value="/include/order/product/productReadOnly.jsp"
                    context="${CSRConfigurator.contextRoot}"
                    siteId="${siteId}"
                    windowId="${windowId}"
                    productId="${ciRelationship.commerceItem.auxiliaryData.productId}"/>
                  <c:if test="${empty isEmailView || isEmailView == false}">
                  <a title="<fmt:message key='cart.items.quickView'/>"
                    href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                      popupPaneId: 'productQuickViewPopup',
                      title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                      url: '${shippingCommerceItemPopup}',
                      onClose: function( args ) { }} )">
                    ${fn:escapeXml(sku.displayName)}
                  </a>
                  </c:if>
                </c:when>
                <c:otherwise>
                  <svc-ui:frameworkPopupUrl var="shippingCommerceItemPopup"
                    value="/include/order/product/productReadOnly.jsp"
                    context="${CSRConfigurator.contextRoot}"
                    windowId="${windowId}"
                    productId="${ciRelationship.commerceItem.auxiliaryData.productId}"/>
                  <c:if test="${empty isEmailView || isEmailView == false}">
                  <a title="<fmt:message key='cart.items.quickView'/>"
                    href="javascript:atg.commerce.csr.common.showPopupWithReturn({
                      popupPaneId: 'productQuickViewPopup',
                      title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                      url: '${shippingCommerceItemPopup}',
                      onClose: function( args ) {  }} )">
                    ${fn:escapeXml(sku.displayName)}
                  </a>
                  </c:if>
                </c:otherwise>
              </c:choose>  

              <c:if test="${!empty isEmailView && isEmailView == true}">
              <c:out value="${fn:escapeXml(sku.displayName)}"/>
              </c:if>
            </li>
            <li>
                <c:out value="${ciRelationship.commerceItem.catalogRefId}"/>
            </li>
            <!-- Render Giftlist information -->
            <c:set var="count" value="0"/>
            <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" value="${shippingGroup.handlingInstructions}" />
              <dsp:oparam name="output">
                <c:if test="${fe.array[count].handlingInstructionClassType == 'giftlistHandlingInstruction'}">
                  <c:if test="${fe.array[count].commerceItemId == ciRelationship.commerceItem.id}">
                    <dsp:include src="/include/order/displayGiftlistInformation.jsp" otherContext="${CSRConfigurator.contextRoot}">
                      <dsp:param name="giftlistId" value="${fe.array[count].giftlistId}"/>
                    </dsp:include>
                  </c:if>
                </c:if>
                <c:set var="count" value="${count+1}"/>
              </dsp:oparam>
            </dsp:droplet><%-- End forEach --%>
          </ul>
        </td>
        <td>
        <dsp:droplet name="IsOrderIncomplete">
        <dsp:param name="order" value="${order}"/>
        <dsp:oparam name="true">
          <csr:inventoryStatus commerceItemId="${ciRelationship.commerceItem.catalogRefId}"/>
        </dsp:oparam>
        <dsp:oparam name="false">

          <dsp:droplet name="ShippingRelationshipStateDescriptions">
            <dsp:param name="state" value="${ciRelationship.stateAsString}"/>
            <dsp:param name="elementName" value="stateDescription"/>
            <dsp:oparam name="output">
              <dsp:droplet name="IsHighlightedState">
              <dsp:param name="obj" value="${ciRelationship}"/>
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
        <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemRelationshipQuantityDroplet">
          <dsp:param name="itemRelationship" value="${ciRelationship}"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="ciRelationshipQuantity" param="quantity"/>
          </dsp:oparam>
        </dsp:droplet>
        <td class="atg_numberValue">
          <web-ui:formatNumber value="${ciRelationshipQuantity}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
  <c:if test="${isExistingOrderView == true}">
    </div>
  </c:if>

  </dsp:layeredBundle>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/displayCommerceItem.jsp#2 $$Change: 1179550 $--%>
