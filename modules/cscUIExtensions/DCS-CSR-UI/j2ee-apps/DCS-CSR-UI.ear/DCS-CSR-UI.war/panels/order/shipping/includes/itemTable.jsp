<%--
This page defines the address view
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/itemTable.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:getvalueof var="shippingGroupIndex" param="shippingGroupIndex"/>
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
  <dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>


  <dsp:importbean bean="/atg/multisite/Site"/> 
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>

    <table class="atg_dataTable atg_commerce_csr_innerTable">
      <thead>
      <%-- Site Icon Heading --%>
      <c:if test="${isMultiSiteEnabled == true}">
        <th class="atg_commerce_csr_siteIcon"></th>
      </c:if>
      <th style="width:60%">
        <fmt:message key="shippingSummary.commerceItem.header.itemDesc"/>
      </th>
      <th style="width:20%">
        <fmt:message key="shippingSummary.commerceItem.header.status"/>
      </th>
      <th class="atg_numberValue" style="width:15%">
        <fmt:message key="shippingSummary.commerceItem.header.qty"/>
      </th>
      </thead>
      <c:forEach items="${order.shippingGroups[shippingGroupIndex].commerceItemRelationships}" var="cisiItem">
        <dsp:tomap var="sku" value="${cisiItem.commerceItem.auxiliaryData.catalogRef}"/>
        <tr>
          <c:if test="${isMultiSiteEnabled == true}">
            <c:set var="siteId" value="${cisiItem.commerceItem.auxiliaryData.siteId}"/>
            <td class="atg_commerce_csr_siteIcon">
              <csr:siteIcon siteId="${siteId}" />
            </td>
          </c:if>
          <td>
            <ul class="atg_commerce_csr_itemDesc">
              <c:choose>
                <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                  <li><a title="<fmt:message key='cart.items.quickView' />" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn(
                    {popupPaneId: 'productQuickViewPopup',
                     title: '${fn:escapeXml(sku.displayName)}',
                     url: '${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&commerceItemId=${cisiItem.commerceItem.id}&siteId=${siteId}',
                     onClose: function(args) { }});return false;">${fn:escapeXml(sku.displayName)}</a></li>
                </c:when>
                <c:otherwise>
                  <li><a title="<fmt:message key='cart.items.quickView' />" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn(
                    {popupPaneId: 'productQuickViewPopup',
                     title: '${fn:escapeXml(sku.displayName)}',
                     url: '${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&commerceItemId=${cisiItem.commerceItem.id}',
                     onClose: function(args) {}});return false;">${fn:escapeXml(sku.displayName)}</a></li>
                </c:otherwise>
              </c:choose>
            <li>${cisiItem.commerceItem.catalogRefId}</li>
            <!-- Render Giftlist information -->
            <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" value="${order.shippingGroups[shippingGroupIndex].handlingInstructions}" />
              <dsp:oparam name="output">
                <c:if test="${fe.element.handlingInstructionClassType == 'giftlistHandlingInstruction'}">
                  <c:if test="${fe.element.commerceItemId == cisiItem.commerceItem.id}">
                    <dsp:droplet name="/atg/commerce/gifts/GiftlistLookupDroplet">
                      <dsp:param name="id" value="${fe.element.giftlistId}" />
                      <dsp:oparam name="output">
                        <li class="atg_commerce_csr_giftwishListName">
                          <dsp:setvalue paramvalue="element" param="giftlist" />
                          <dsp:getvalueof var="eventName" vartype="java.lang.String" param="giftlist.eventName" />
                          <dsp:valueof param="giftlist.owner.firstName" />&nbsp;
                          <dsp:valueof param="giftlist.owner.lastName" />, <c:out value="${eventName}" />
                        </li>
                      </dsp:oparam>
                    </dsp:droplet><%-- End Switch --%>
                  </c:if>
                </c:if>
              </dsp:oparam>
            </dsp:droplet><%-- End forEach --%>
          </ul>
          </td>
          <td>
            <csr:inventoryStatus commerceItemId="${cisiItem.commerceItem.catalogRefId}"/>
          </td>

          <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemRelationshipQuantityDroplet">
            <dsp:param name="itemRelationship" value="${cisiItem}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="cisiItemQuantity" param="quantity"/>
            </dsp:oparam>
          </dsp:droplet>

          <td class="atg_numberValue">
            <web-ui:formatNumber value="${cisiItemQuantity}"/>
          </td>
        </tr>
      </c:forEach>
    </table>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/itemTable.jsp#2 $$Change: 1179550 $--%>