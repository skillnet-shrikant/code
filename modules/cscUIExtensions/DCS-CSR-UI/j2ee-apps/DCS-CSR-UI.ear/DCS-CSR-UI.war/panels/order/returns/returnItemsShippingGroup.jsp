<%--
This page displays the return items by shipping group. Return Items could be belong to one or muliple shipping groups.
In order to get a clear picture for the refund amount, we need to break it down by the shipping group.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsShippingGroup.jsp#2 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="false">
  <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>

  <svc-ui:frameworkUrl var="returnProcessSuccessURL" panelStacks="cmcRefundTypePS,globalPanels"/>
  <svc-ui:frameworkUrl var="exchangeProcessSuccessURL" selectTabbedPanels="cmcProductCatalogSearchP" panelStacks="cmcCatalogPS,globalPanels"/>

  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

    <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
    <c:set var="currencyCode" value="${returnRequest.order.priceInfo.currencyCode }"/>

    <svc-ui:frameworkUrl var="successURL" panelStacks="cmcReturnsPS"/>
    <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcReturnsPS"/>
    <dsp:form id="csrSelectReturnItems" formid="csrSelectReturnItems" method="post">
    <dsp:input id="startReturnSuccessURL" bean="ReturnFormHandler.startReturnProcessSuccessURL" value="${returnProcessSuccessURL}" type="hidden" />
    <dsp:input id="startExchangeSuccessURL" bean="ReturnFormHandler.startExchangeProcessSuccessURL" value="${exchangeProcessSuccessURL}" type="hidden" />
      <dsp:input id="processName" bean="ReturnFormHandler.selectItemsProcessName" value="" type="hidden"/>
      <dsp:input id="selectItemsSuccessURL" bean="ReturnFormHandler.selectItemsSuccessURL" value="${successURL}" type="hidden"/>
      <dsp:input bean="ReturnFormHandler.selectItemsErrorURL" value="${errorURL}" type="hidden"/>
      <dsp:input bean="ReturnFormHandler.selectItems" type="hidden" priority="-10" value=""/>

      <div class="atg_commerce_csr_subPanel">
        <p>
          <fmt:message key="returnItems.returnItemsShippingGroup.header.title"/>
        </p>

        <%--
        /*---------------------------------------------------------------------------
        * loop through all of the return groups
        *---------------------------------------------------------------------------*/
        --%>

        <dsp:getvalueof var="returnShippingGroupList" bean="ShoppingCart.returnRequest.shippingGroupList"/>


        <dsp:droplet name="ForEach">
          <dsp:param value="${returnShippingGroupList}"
                     name="array"/>
          <dsp:param name="elementName" value="returnShippingGroup"/>
          <dsp:param name="indexName" value="shippingGroupIndex"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="returnShippingGroup" param="returnShippingGroup"/>

            <c:if test="${fn:length(returnShippingGroupList) > 1}">
              <fieldset>
                <legend>
                  <fmt:message key="returnItems.returnItemsShippingGroup.header.shipment"/>
                  &nbsp;
                  <dsp:valueof param="count"/>
                </legend>
            </c:if>
            <dsp:getvalueof var="sgType" param="returnShippingGroup.shippingGroup.shippingGroupClassType"/>
            <dsp:getvalueof var="sgTypeConfig" bean="CSRConfigurator.shippingGroupTypeConfigurationsAsMap.${sgType}"/>
            <c:if test="${sgTypeConfig != null && sgTypeConfig.displayPageFragment != null}">
              <c:choose>
                <c:when test="${sgType == 'inStorePickupShippingGroup'}">
                  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
                    <fmt:message key="shippingSummary.inStorePickup.header" />
                    <br />
                    <br />
                    <fmt:message key="shippingSummary.inStorePickup.storeAddress" />
                    <br />
                  </dsp:layeredBundle>
                  <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                    <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                    <dsp:param name="propertyName" value="value1"/>
                    <dsp:param name="displayHeading" value="${true}"/>
                    <dsp:param name="displaySelectButton" value="${false}"/>
                  </dsp:include>
                </c:when>
                <c:otherwise>
                  <div class="atg_commerce_csr_addressView">
                    <h4>
                      <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                        <dsp:param name="propertyName" value="value1"/>
                        <dsp:param name="displayHeading" value="${true}"/>
                      </dsp:include>
                    </h4>
                    <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
                      <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                        <dsp:param name="propertyName" value="value1"/>
                        <dsp:param name="displayValue" value="${true}"/>
                      </dsp:include>
                    </ul>
                  </div>

                  <div class="atg_commerce_csr_shippingMethod">
                    <h4>
                      <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                        <dsp:param name="propertyName" value="value2"/>
                        <dsp:param name="displayHeading" value="${true}"/>
                      </dsp:include>
                    </h4>
                    <ul>
                      <li>
                        <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                                     otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
                          <dsp:param name="shippingGroup" value="${returnShippingGroup.shippingGroup}"/>
                          <dsp:param name="propertyName" value="value2"/>
                          <dsp:param name="displayValue" value="${true}"/>
                        </dsp:include>
                      </li>
                    </ul>
                  </div>
                </c:otherwise>
              </c:choose>
            </c:if>
            <div class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
              <h4>
                <fmt:message key="returnItems.returnItemsShippingGroup.tax.header"/>
              </h4>
              <ul>
                <li>
                  <dsp:getvalueof var="shippingGroupId" param="returnShippingGroup.shippingGroup.id"/>
                  <dsp:getvalueof var="sgTaxPriceInfo"
                                  bean="ShoppingCart.returnRequest.order.taxPriceInfo.shippingItemsTaxPriceInfos.${shippingGroupId}"/>
                  <csr:formatNumber value="${sgTaxPriceInfo.amount}" type="currency" currencyCode="${currencyCode}"/>
                </li>
              </ul>
            </div>
            <div class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
              <h4>
                <fmt:message key="returnItems.returnItemsShippingGroup.shipping.header"/>
              </h4>
              <ul>
                <li>
                  <csr:formatNumber value="${returnShippingGroup.shippingGroup.priceInfo.rawShipping}" type="currency"
                                    currencyCode="${currencyCode}"/>
                </li>
              </ul>
            </div>
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="returnShippingGroup.itemList"/>
              <dsp:param name="elementName" value="item"/>
              <dsp:param name="indexName" value="itemIndex"/>

              <dsp:oparam name="outputStart">
                <table class="atg_dataTable">
                  <thead>
                  <tr>
                    <c:if test="${isMultiSiteEnabled == true}">
                      <th></th>
                    </c:if>
                    <th class="atg_numberValue atg_commerce_csr_abbrData">
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.qtyOrdered"/>
                      <br>
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.qtyReturned"/>
                    </th>
                    <th class="atg_numberValue">
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.qtyTo"/>
                    </th>
                    <th>
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.sku"/>
                    </th>
                    <th>
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.name"/>
                    </th>
                    <th>
                      <fmt:message
                        key="returnItems.returnItemsShippingGroup.commerceItem.header.reason"/>
                    </th>
					<th>
                      Store (optional)
                    </th>
                  </tr>
                  </thead>
              </dsp:oparam>
              <dsp:oparam name="output">
										<csr:renderer
											name="/atg/commerce/custsvc/ui/renderers/ReturnsLineItem">
											<jsp:attribute name="setPageData">
              </jsp:attribute>
											<jsp:body>
                    <dsp:include src="${renderInfo.url}"
													otherContext="${renderInfo.contextRoot}">
                      <dsp:param name="item" param="item" />
                      <dsp:param name="itemIndex" param="itemIndex" />
                    </dsp:include>
                  </jsp:body>
										</csr:renderer>
									</dsp:oparam>
              <dsp:oparam name="outputEnd">
                </table>
                <c:if test="${fn:length(returnShippingGroupList) > 1}">
                  </fieldset>
                </c:if>
              </dsp:oparam>
            </dsp:droplet>
            <%-- End of inner ForEach --%>
          </dsp:oparam>
          <dsp:oparam name="outputEnd">
          </dsp:oparam>
        </dsp:droplet>
        <%-- End of outer ForEach --%>
    </dsp:form>
    <div class="atg_commerce_csr_panelFooter">
      <input type="button" onclick="atg.commerce.csr.order.returns.startReturnProcess();return false;"
             value="<fmt:message key="returnItems.button.processReturn.title" />" />
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsShippingGroup.jsp#2 $$Change: 1179550 $--%>
