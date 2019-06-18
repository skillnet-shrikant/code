<%-- This page provides the option to split the shipping quantity.

Expected params
workingindex : The current working index. In the parent page, user selects an commerce item to split.
That index helps to figure on which item, the user is trying to split

success : this page internally adds success paramter to close the floating pane.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/splitShippingQuantity.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean
      bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean var="container"
                  bean="/atg/commerce/custsvc/order/ShippingGroupContainerService"/>
  <dsp:importbean
      bean="/atg/commerce/custsvc/order/ApplicableShippingGroups"/>


  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
  <dsp:getvalueof var="mode" param="mode"/>
  <dsp:getvalueof var="workingindex" param="workingindex"/>
  <dsp:getvalueof var="success" param="success"/>

  <c:url var="successErrorURL" context="${CSRConfigurator.contextRoot}"
         value="/panels/order/shipping/splitShippingQuantity.jsp">
    <c:param name="mode" value="${mode}"/>
    <c:param name="workingindex" value="${workingindex}"/>
    <c:param name="${stateHolder.windowIdParameterName}"
             value="${windowId}"/>
    <c:param name="success" value="true"/>
  </c:url>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
    <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
    <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />
    <div class="atg_commerce_csr_popupPanel atg_commerce_csr_splitPopup">
      <div>
        <dsp:droplet name="Switch">
          <dsp:param bean="ShippingGroupFormHandler.formError" name="value"/>
          <dsp:oparam name="true">
            &nbsp;<br/>
            <br/>
				<span class="atg_commerce_csr_common_content_alert"><fmt:message
            key="common.error.header"/></span>
            <br>
				<span class="atg_commerce_csr_common_content_alert">
				<UL>
          <dsp:droplet name="ErrorMessageForEach">
            <dsp:param bean="ShippingGroupFormHandler.formExceptions"
                       name="exceptions"/>
            <dsp:oparam name="output">
              <LI>
                <dsp:valueof param="message"/>
            </dsp:oparam>
          </dsp:droplet>
        </UL>
				</span>
          </dsp:oparam>
          <dsp:oparam name="false">
            <c:if test="${success}">
              <script language="JavaScript" type="text/javascript">
                atg.commerce.csr.order.shipping.cancelSplitQtyPrompt('${mode}');
              </script>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </div>

      <dsp:getvalueof var="csrSplitShippingGroupQty" value="csrSplitShippingGroupQty"/>

      <dsp:form method="POST" id="csrSplitShippingGroupQty"
                formid="${csrSplitShippingGroupQty}">
        <dsp:input type="hidden" priority="-10" value=""
                   bean="ShippingGroupFormHandler.splitShippingInfos"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="ShippingGroupFormHandler.splitShippingInfosErrorURL"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="ShippingGroupFormHandler.splitShippingInfosSuccessURL"/>

        <table class="atg_dataTable">
          <thead>
          <th>
            <fmt:message
                key="multipleShipping.commerceItem.header.itemDesc"/>
          </th>
          <th>
            <fmt:message
                key="multipleShipping.commerceItem.header.status"/>
          </th>
          <th class="atg_numberValue">
            <fmt:message
                key="multipleShipping.commerceItem.header.qty"/>
          </th>
          <th>
            <fmt:message
                key="multipleShipping.commerceItem.header.qtyToSplit"/>
          </th>
          <th>
            <fmt:message
                key="multipleShipping.commerceItem.header.shipTo"/>
          </th>

          <dsp:droplet name="ForEach">
            <dsp:param
                bean="ShippingGroupContainerService.allCommerceItemShippingInfos"
                name="array"/>
            <dsp:param name="elementName" value="cisiItem"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="cisiItem" param="cisiItem"/>
              <dsp:getvalueof var="index" param="index"/>
              <dsp:tomap var="sku"
                         value="${cisiItem.commerceItem.auxiliaryData.catalogRef}"/>

              <c:if test="${workingindex == index}">
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
                the Split Shipping Quantity button should not be enabled. --%>

                <c:if test="${!validAddressFound}">
                  <script type="text/javascript">
                    atg.commerce.csr.common.enableDisable({}, {form:'${csrSplitShippingGroupQty}', name:'handleSplitShippingQuantity'});
                  </script>
                </c:if>

                <tr>
                  <td>
                    <ul class="atg_commerce_csr_itemDesc">
                      <li>${fn:escapeXml(sku.displayName)}</li>
                      <li>${fn:escapeXml(cisiItem.commerceItem.catalogRefId)}</li>
                    </ul>
                  </td>
                  <td>
                    <csr:inventoryStatus
                        commerceItemId="${cisiItem.commerceItem.catalogRefId}"/>
                  </td>

                  <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                    <dsp:param name="item" value="${cisiItem.commerceItem}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="isFractional" param="fractional"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <dsp:droplet name="/atg/commerce/custsvc/order/purchase/GetCommerceItemShippingInfoQuantityDroplet">
                    <dsp:param name="commerceItemShippingInfo" value="${cisiItem}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="cisiItemQuantity" param="quantity"/>
                    </dsp:oparam>
                  </dsp:droplet>

                  <td class="atg_numberValue">
                    <web-ui:formatNumber
                        value="${cisiItemQuantity}"/>
                  </td>
                  <td>
                    <dsp:getvalueof var="maxQuantity" value="${cisiItemQuantity-1}" idtype="java.lang.String"/>
                    <c:choose>
                      <c:when test="${isFractional == true}">
                        <dsp:input type="text"
                                   id="${csrSplitShippingGroupQty}_splitQuantity"
                                   bean="ShippingGroupContainerService.allCommerceItemShippingInfos[param:index].splitQuantityWithFraction"
                                   size="9" maxlength="9">
                          <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                          <dsp:tagAttribute name="trim" value="true" />
                          <dsp:tagAttribute name="constraints" value="{min:0,max:${cisiItemQuantity}, pattern:${fractionalUnitPattern}}"/>
                          <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                        </dsp:input>
                      </c:when>
                      <c:otherwise>
                        <dsp:input type="text"
                                   id="${csrSplitShippingGroupQty}_splitQuantity"
                                   bean="ShippingGroupContainerService.allCommerceItemShippingInfos[param:index].splitQuantity"
                                   size="5" maxlength="5">
                          <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                          <dsp:tagAttribute name="constraints" value="{min:1,max:${maxQuantity},places:0,pattern: '#####'}" />
                        </dsp:input>
                      </c:otherwise>
                    </c:choose>

                  </td>
                  <td>
                    <c:if test="${!validAddressFound}">
                      <fmt:message key="newOrderSingleShipping.header.addNewAddress"/>
                    </c:if>
                    <c:if test="${validAddressFound}">
                      <dsp:include
                          src="/panels/order/shipping/includes/shippingGroupList.jsp">
                        <dsp:param name="beanString"
                                   value="ShippingGroupContainerService.allCommerceItemShippingInfos[${index}].splitShippingGroupName"/>
                        <dsp:param name="cisiItem" value="${cisiItem}"/>
                        <dsp:param name="itemShippingGroups"
                                   value="${itemShippingGroups}"/>
                      </dsp:include>
                    </c:if>
                  </td>
                </tr>
              </c:if>
            </dsp:oparam>
          </dsp:droplet>
        </table>
        <div class="atg_commerce_csr_panelFooter" style="width:540px"><input type="button"
                                                                             value="<fmt:message key='multipleShipping.splitShippingQuantity.button.split'/>"
                                                                             id="handleSplitShippingQuantity"
                                                                             name="handleSplitShippingQuantity"
                                                                             onclick="atg.commerce.csr.order.shipping.splitShippingGroupQty('${successErrorURL}');return false;"/>
          <input type="button" value="<fmt:message key='common.cancel.title'/>"
                 onclick="atg.commerce.csr.order.shipping.cancelSplitQtyPrompt('${mode}');return false;"/>
        </div>
      </dsp:form>
    </div>
  </dsp:layeredBundle>
  <script type="text/javascript">
    var ${csrSplitShippingGroupQty}Validate = function () {
      var disable = false;
      if (!dijit.byId("csrSplitShippingGroupQty_splitQuantity").isValid()) disable = true;
      dojo.byId('${csrSplitShippingGroupQty}').handleSplitShippingQuantity.disabled = disable;
    };
    _container_.onLoadDeferred.addCallback(function () {
      ${csrSplitShippingGroupQty}Validate();
      atg.service.form.watchInputs('${csrSplitShippingGroupQty}', ${csrSplitShippingGroupQty}Validate);
    });
    _container_.onUnloadDeferred.addCallback(function () {
      atg.service.form.unWatchInputs('${csrSplitShippingGroupQty}');
    });
  </script>

</dsp:page>
<%-- end of multipleShipping.jsp --%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/splitShippingQuantity.jsp#2 $$Change: 1179550 $--%>
