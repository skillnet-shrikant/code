<%--
This page defines the address view

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/addressTable.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean var="container" bean="/atg/commerce/custsvc/order/ShippingGroupContainerService"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ApplicableShippingGroups"/>
  <dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean var="shippingAddressNextStep" bean="/atg/commerce/custsvc/ui/fragments/order/MultipleShippingAddressNextStep"/>

  <dsp:importbean bean="/atg/multisite/Site"/> 
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>

  <dsp:getvalueof var="commerceItemShippingInfos" bean="ShippingGroupContainerService.allCommerceItemShippingInfos"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingAddressPS"/>

    <c:set var="multiFormId" value="csrMultipleShippingAddressForm"/>
    <dsp:form id="${multiFormId}" formid="${multiFormId}">
      <dsp:input name="errorURL" value="${errorURL}" type="hidden"
                 bean="ShippingGroupFormHandler.multipleShippingGroupCheckoutErrorURL"/>
      <dsp:input type="hidden" priority="-10" value="" id="csrHandleApplyShippingGroups"
                 bean="ShippingGroupFormHandler.multipleShippingGroupCheckout"/>
      <dsp:input type="hidden" priority="-10" value="" id="csrPreserveUserInputOnServerSide"
                 bean="ShippingGroupFormHandler.preserveUserInputOnServerSide"/>
      <dsp:input type="hidden" value="false" id="persistOrder" name="persistOrder"
                 bean="ShippingGroupFormHandler.persistOrder"/>
      <dsp:include page="${shippingAddressNextStep.URL}" otherContext="${shippingAddressNextStep.servletContext}">
      </dsp:include>

      <table class="atg_dataTable atg_commerce_csr_innerTable">
        <thead>
        <%-- Site Icon Heading --%>
        <c:if test="${isMultiSiteEnabled == true}">
          <th class="atg_commerce_csr_siteIcon"></th>
        </c:if>
        <th>
          <fmt:message key="multipleShipping.commerceItem.header.itemDesc"/>
        </th>
        <th class="atg_numberValue">
          <fmt:message key="multipleShipping.commerceItem.header.qty"/>
        </th>
        <th class="atg_numberValue">
          <fmt:message key="multipleShipping.commerceItem.header.splitQty"/>
        </th>
        <th>
          <fmt:message key="multipleShipping.commerceItem.header.shipTo"/>
        </th>
        </thead>

        <c:forEach var="cisiItem" items="${commerceItemShippingInfos}" varStatus="status">

          <dsp:droplet name="/atg/commerce/custsvc/order/purchase/GetCommerceItemShippingInfoQuantityDroplet">
            <dsp:param name="commerceItemShippingInfo" value="${cisiItem}"/>
              <dsp:oparam name="output">
              <dsp:getvalueof var="cisiItemQuantity" param="quantity"/>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
            <dsp:param name="item" value="${cisiItem.commerceItem}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="cisiItemIsFractional" param="fractional"/>
            </dsp:oparam>
          </dsp:droplet>

          <%-- This will be used to display appropriate images. --%>
          <dsp:droplet name="ApplicableShippingGroups">
            <dsp:param name="sgMapContainer" value="${container}"/>
            <dsp:param name="commerceItem" value="${cisiItem.commerceItem}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="sgTypeList" param="allShippingGroupTypes"/>
              <dsp:getvalueof var="itemShippingGroups" param="shippingGroups"/>
            </dsp:oparam>
          </dsp:droplet>
          <c:set var="validAddressFound" value="false"/>
          <c:if test="${!empty itemShippingGroups}">
            <c:set var="validAddressFound" value="true"/>
          </c:if>

          <%-- If there is no valid address found for any commerce item,
          the ship to multiple button should not be enabled. --%>
          <c:if test="${!validAddressFound}">
            <script type="text/javascript">
              atg.commerce.csr.common.enableDisable({}, {form:'${multiFormId}', name:'handleShipToMultiple'});
            </script>
          </c:if>

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
                <li>
                  <c:choose>
                    <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                      <a title="<fmt:message key='common.quickView' />" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn(
                      {popupPaneId: 'productQuickViewPopup',
                       title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                       url: '${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&commerceItemId=${cisiItem.commerceItem.id}&siteId=${siteId}',
                       onClose: function(args) { }});return false;">${fn:escapeXml(sku.displayName)}</a>
                    </c:when>
                    <c:otherwise>
                      <a title="<fmt:message key='common.quickView' />" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn(
                      {popupPaneId: 'productQuickViewPopup',
                       title: '<c:out value="${fn:escapeXml(sku.displayName)}"/>',
                       url: '${CSRConfigurator.contextRoot}/include/order/product/productReadOnly.jsp?_windowid=${windowId}&commerceItemId=${cisiItem.commerceItem.id}',
                       onClose: function(args) {}});return false;">${fn:escapeXml(sku.displayName)}</a>
                    </c:otherwise>
                  </c:choose>                    
                </li>
                <li>${fn:escapeXml(cisiItem.commerceItem.catalogRefId)}</li>
              </ul>
            </td>
            <td class="atg_numberValue">
                <web-ui:formatNumber value="${cisiItemQuantity}"/>
            </td>
            <td class="atg_numberValue">
              <%-- Display the split quantity link only if there is a valid address found and if the item quantity is
              greater than 1

              Note also when detailing with fractional quantites a quantity can be less than 1 and still be split.
              --%>
              <c:set var="itemQuantityValid" value="${cisiItemQuantity > 1 || (cisiItemIsFractional == true && cisiItemQuantity > 0)}"/>
              <c:if test="${validAddressFound && itemQuantityValid}">
                <a href="#"
                   onclick="javascript:atg.commerce.csr.order.shipping.splitQuantity('${itemQuantityValid ? status.index : -1}');return false;"
                    >
                  <fmt:message key="multipleShipping.link.splitQty"/>
                </a>
              </c:if>

            </td>
            <td>
              <c:if test="${!validAddressFound}">
                <fmt:message key="newOrderSingleShipping.header.addNewAddress"/>
              </c:if>
              <c:if test="${validAddressFound}">
                <dsp:include src="/panels/order/shipping/includes/shippingGroupList.jsp">
                  <dsp:param name="beanString"
                             value="ShippingGroupContainerService.allCommerceItemShippingInfos[${status.index}].shippingGroupName"/>
                  <dsp:param name="cisiItem" value="${cisiItem}"/>
                  <dsp:param name="itemShippingGroups" value="${itemShippingGroups}"/>
                </dsp:include>
              </c:if>
            </td>
          </tr>
          <!-- Render Giftlist information -->
          <dsp:droplet var="fe" name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array"
              value="${cisiItem.handlingInstructionInfos}" />
            <dsp:oparam name="output">
              <tr>
                <c:if
                  test="${fe.element.handlingInstruction.handlingInstructionClassType == 'giftlistHandlingInstruction'}">
                  <dsp:droplet
                    name="/atg/commerce/gifts/GiftlistLookupDroplet">
                    <dsp:param name="id"
                      value="${fe.element.handlingInstruction.giftlistId}" />
                    <dsp:oparam name="output">
                      <td></td>
                      <td>
                          <ul class="atg_commerce_csr_itemDesc">
                            <li class="atg_commerce_csr_giftwishListName">
                              <dsp:setvalue paramvalue="element"
                                param="giftlist" /> <dsp:getvalueof
                                var="eventName" vartype="java.lang.String"
                                param="giftlist.eventName" /> <dsp:valueof
                                param="giftlist.owner.firstName" />&nbsp; <dsp:valueof
                                param="giftlist.owner.lastName" />, <c:out
                                value="${eventName}" />
                              </li>
                            </ul>
                        </td>
                    </dsp:oparam>
                  </dsp:droplet>
                  <td></td>
                  <td class="atg_numberValue">
                    <dsp:droplet name="/atg/commerce/custsvc/order/purchase/GetHandlingInstructionInfoQuantityDroplet">
                      <dsp:param name="handlingInstruction" value="${fe.element.handlingInstruction}"/>
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="handlingInstructionInfoQuantity" param="quantity"/>
                      </dsp:oparam>
                    </dsp:droplet>
                    <web-ui:formatNumber value="${handlingInstructionInfoQuantity}"/>
                  </td>
                </c:if>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
        </c:forEach>
      </table>
      <div class="atg_commerce_csr_tableControls">
        <fmt:message var="shipToMultipleAddress" key="newOrderSingleShipping.link.shipToMultiple"/>
      
        <input type="button" value="${shipToMultipleAddress }"
        name="handleShipToMultiple"
        onclick="atg.commerce.csr.order.shipping.applyMultipleShippingGroup({form:'${multiFormId}}'});return false;"/>
      </div>

    </dsp:form>

<%-- This function saves the form data and serves the split quantity popup page --%>
<script type="text/javascript">
  atg.commerce.csr.order.shipping.splitQuantity = function(index) {
    document.getElementById("${multiFormId}")["successURL"].value = "";
    atg.commerce.csr.common.enableDisable('csrPreserveUserInputOnServerSide',
      'csrHandleApplyShippingGroups');
    var deferred = atgSubmitAction({form:dojo.byId("${multiFormId}"), handleAs: 'json'});
    deferred.addCallback(function(results) {
      if (results) {
        var value = results.error;
        if (!value) {
          console.debug("There is no results error." + value);
          atg.commerce.csr.order.shipping.splitQtyPrompt(
              "${CSRConfigurator.contextRoot}/panels/order/shipping/splitShippingQuantity.jsp?${stateHolder.windowIdParameterName}=${windowId}&workingindex=" + index, //url
              '<fmt:message key="multipleShipping.splitShippingQuantity.header"/>'); // title
          return results;
        }
      }
    });
  }
</script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/addressTable.jsp#2 $$Change: 1179550 $--%>
