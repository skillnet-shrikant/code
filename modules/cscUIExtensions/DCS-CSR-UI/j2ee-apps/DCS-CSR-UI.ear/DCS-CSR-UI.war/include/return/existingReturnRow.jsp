<%-- beginning of existingReturnRow.jspf

This jsp is used to display an existing return.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/return/existingReturnRow.jsp#2 $$Change: 1179550 $
--%>
<%@ include file="../top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
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

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />
  <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
  <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
  <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />

  <dsp:importbean bean="/atg/commerce/custsvc/util/AltColor"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler" />
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnStateDescriptions"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnItemStateDescriptions"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/catalog/UnitOfMeasureDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnItemQuantityInfoDroplet"/>
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator" />

  <dsp:getvalueof var="returnObject" param="returnRequest"/>
  <dsp:getvalueof var="returnRequestIndex" param="returnRequestIndex"/>
  <dsp:getvalueof var="readOnlyView" param="readOnlyView" />


  <tr class="${((returnRequestIndex % 2)==0) ? '' : 'atg_altRow'}">
      <c:if test="${!readOnlyView}">
      </c:if>
      <td><a id="atg_commerce_csr_returnRequest_${returnObject.requestId}"
        class="atg_commerce_csr_sectionClosed" href="#" 
        onclick="atg.commerce.csr.common.toggle('atg_commerce_csr_returnRequest_${returnObject.requestId}', 'atg_commerce_csr_returnItems_${returnObject.requestId}', 'atg_commerce_csr_sectionOpen', 'atg_commerce_csr_sectionClosed');return false;"/><c:out value="${returnObject.requestId}"/></a></td>
      <td><web-ui:formatDate value="${returnObject.authorizationDate}" type="date" dateStyle="short" /></td>
      <td><fmt:message key="${returnObject.processName}"/></td>
      <td class="atg_numberValue">
        <c:set var="numberOfItems" value="${0}"/>
        <c:forEach var="returnItem" items="${returnObject.returnItemList}">
          <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
            <dsp:param name="item" value="${returnItem}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="quantityToReturn" param="quantityToReturn"/>
              <dsp:getvalueof var="quantityReceived" param="quantityReceived"/>
            </dsp:oparam>
          </dsp:droplet>
          <c:set var="numberOfItems" value="${numberOfItems + quantityToReturn - quantityReceived}"/>
        </c:forEach>
        <c:choose>
        <c:when test="${numberOfItems > 0}">
          <web-ui:formatNumber value="${numberOfItems}"/>
        </c:when>
        <c:otherwise>
          &nbsp;
        </c:otherwise>
        </c:choose>
      </td>
      <td class="atg_numberValue">
        <csr:formatNumber type="currency" value="${returnObject.totalRefundAmount}" currencyCode="${returnObject.order.priceInfo.currencyCode}"/>
      </td>
      <td>
        <dsp:droplet name="ReturnStateDescriptions">
            <dsp:param name="state" value="${returnObject.state}"/>
            <dsp:param name="elementName" value="returnStateDescription"/>
            <dsp:oparam name="output">
              <dsp:valueof param="returnStateDescription"><fmt:message key="common.notApplicable"/></dsp:valueof>
            </dsp:oparam>
        </dsp:droplet>
      </td>
    </tr>
    
    <%--  Start of nested ReturnItem table --%>
    <%-- <c:if test="${fn:length(returnObject.returnItemList) > 0}"> --%>
      
      <tr id="atg_commerce_csr_returnItems_${returnObject.requestId}" class="hidden_node">
          <td colspan="6">
            <table class="atg_dataTable atg_commmerce_csr_nestedTable" cellpadding="0" cellspacing="0">
              <thead>
                <tr>
                  <th scope="col"><fmt:message key="existingReturns.returnItem.table.header.sku.title"/></th>
                  <th scope="col"><fmt:message key="existingReturns.returnItem.table.header.description.title"/></th>
                  <th scope="col" class="atg_numberValue"><fmt:message key="existingReturns.returnItem.table.header.quantityReturnedReceived.title"/></th>
                  <th scope="col" class="atg_numberValue"><fmt:message key="existingReturns.returnItem.table.header.amount.title"/></th>
                  <th scope="col"><fmt:message key="existingReturns.returnItem.table.header.status.title"/></th>
                  <th scope="col"><fmt:message key="existingReturns.returnItem.table.header.itemDisposition.title"/></th>
                  <th scope="col" class="atg_numberValue"><fmt:message key="existingReturns.returnItem.table.header.quantityToReceive.title"/></th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="returnItem" items="${returnObject.returnItemList}" varStatus="rowCounter">
                  <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
                    <dsp:param name="item" value="${returnItem}"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="quantityToReturn" param="quantityToReturn"/>
                      <dsp:getvalueof var="quantityReceived" param="quantityReceived"/>
                    </dsp:oparam>
                  </dsp:droplet>
                  <c:set scope="page" var="quantity" 
                          value="${quantityToReturn - quantityReceived}"></c:set>
                  <tr class="${((rowCounter.index % 2) == 0) ? '' : 'atg_altRow'}">
                    <td><dsp:tomap var="productRef" value="${returnItem.commerceItem.auxiliaryData.productRef}"/>
                      <svc-ui:frameworkPopupUrl var="commerceItemPopup"
                          value="/include/order/product/productReadOnly.jsp"
                          context="${CSRConfigurator.contextRoot}"
                          windowId="${windowId}"
                          productId="${returnItem.commerceItem.auxiliaryData.productId}"/>
                      <a title="<fmt:message key='existingReturns.returnItem.quickView'/>"
                        href="#"
                        onclick="atg.commerce.csr.common.showPopupWithReturn({
                          popupPaneId: 'productQuickViewPopup',
                          title: '${fn:escapeXml(productRef.displayName)}',
                          url: '${commerceItemPopup}',
                          onClose: function( args ) {  }} );"><c:out value="${returnItem.commerceItem.catalogRefId}"/></a>
                    </td>
                    <td>
                      ${fn:escapeXml(productRef.displayName)}
                    </td>
                    <td class="atg_numberValue"><web-ui:formatNumber value="${quantityToReturn}"/> <fmt:message key="common.openbracket"/><web-ui:formatNumber value="${quantityReceived}"/><fmt:message key="common.closebracket"/></td>
                    <td class="atg_numberValue">
                      <fmt:message key="common.openbracket"/><csr:formatNumber type="currency" value="${returnItem.refundAmount}" currencyCode="${returnObject.order.priceInfo.currencyCode}"/><fmt:message key="common.closebracket"/>
                    </td>
                    <td>
                      <dsp:droplet name="ReturnItemStateDescriptions">
                        <dsp:param name="state" value="${returnItem.state}"/>
                        <dsp:param name="elementName" value="stateDescription"/>
                        <dsp:oparam name="output"><dsp:valueof param="stateDescription"><fmt:message key="common.notApplicable"/></dsp:valueof></dsp:oparam>
                      </dsp:droplet>
                    </td>
                    <td> <%-- Item Disposition --%> 
                      <c:choose>
                        <c:when test="${!readOnlyView}">
                          <c:choose>
                            <c:when test="${quantity > 0}">
                            	<dsp:tomap var="reasonToDispositionCodes" bean="/atg/commerce/custsvc/returns/ReturnManager.returnReasonToDispCode"/>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                  <dsp:param value="${reasonToDispositionCodes}" name="array"/>
                                  <dsp:oparam name="output">
                                  	<dsp:getvalueof var="reasonToDispositionKey" param="key"/>
                                  	<c:if test="${reasonToDispositionKey==returnItem.returnReason}">
                                  		<dsp:getvalueof var="reasonToDisposition" param="element"/>
                                  	</c:if>
                                  </dsp:oparam>
                                </dsp:droplet>
                                <dsp:input bean="ReturnFormHandler.returnItemDispositions.${returnItem.id}" value="${reasonToDisposition}" type="hidden"/>
                                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                                  <dsp:param bean="ReturnFormHandler.dispositionCodes" name="array"/>
                                  <dsp:param name="elementName" value="dispositionCode"/>
                                  <dsp:oparam name="output">
                                    <dsp:tomap var="itemDisposition" param="dispositionCode"/>
										<c:if test="${itemDisposition.repositoryId==reasonToDisposition}">
											<c:out value="${itemDisposition.readableDescription}"/>
										</c:if>
                                  </dsp:oparam>
                                </dsp:droplet>
                            </c:when>
                            <c:otherwise>
                              <dsp:droplet name="/atg/commerce/custsvc/returns/DispositionLookup">
                                <dsp:param name="id" value="${returnItem.disposition}"/>
                                <dsp:param name="elementName" value="dispositionCode"/>
                                <dsp:oparam name="empty">
                                  <fmt:message key="confirmReturn.noReturnReason.label"/>
                                </dsp:oparam>
                                <dsp:oparam name="output">
                                  <dsp:valueof param="dispositionCode.readableDescription"/>
                                </dsp:oparam>
                              </dsp:droplet>
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                        <c:otherwise>
                          <dsp:droplet name="/atg/commerce/custsvc/returns/DispositionLookup">
                            <dsp:param name="id" value="${returnItem.disposition}"/>
                            <dsp:param name="elementName" value="dispositionCode"/>
                            <dsp:oparam name="empty">
                              <fmt:message key="confirmReturn.noReturnReason.label"/>
                            </dsp:oparam>
                            <dsp:oparam name="output">
                              <dsp:valueof param="dispositionCode.description"/>
                            </dsp:oparam>
                          </dsp:droplet>
                        </c:otherwise>
                      </c:choose>
                    </td>

                      <c:choose>
                        <c:when test="${!readOnlyView}">
                          <c:choose>
                            <c:when test="${quantity > 0}">
                              <dsp:droplet name="UnitOfMeasureDroplet">
                                <dsp:param name="item" value="${returnItem.commerceItem}"/>
                                <dsp:oparam name="output">
                                <dsp:droplet name="Switch">
                                  <dsp:param name="value" param="fractional"/>
                                  <dsp:oparam name="false">
                                    <td class="atg_leftValue">
                                      <dsp:select bean="ReturnFormHandler.receiveItemQuantities.${returnItem.id}">
                                        <c:forEach var="returnQuantity" begin="0" end="${quantity}">
                                          <dsp:option value="${returnQuantity}"><web-ui:formatNumber value="${returnQuantity}"/></dsp:option>
                                        </c:forEach>
                                      </dsp:select>
                                      <dsp:input bean="ReturnFormHandler.returnItemsRequests.${returnItem.id}"
                                                 value="${returnObject.requestId}" type="hidden" />
                                    </td>
                                  </dsp:oparam>
                                  <dsp:oparam name="true">
                                    <td class="atg_commerce_csr_returnQty">
                                      <dsp:input bean="ReturnFormHandler.receiveItemQuantities.${returnItem.id}"
                                                 type="text"
                                                 size="9"
                                                 value="0"
                                                 maxlength="9">
                                        <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                                        <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                                        <dsp:tagAttribute name="trim" value="true" />
                                        <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
                                      </dsp:input>

                                      <dsp:input bean="ReturnFormHandler.returnItemsRequests.${returnItem.id}"
                                                 value="${returnObject.requestId}" type="hidden" />
                                    </td>
                                  </dsp:oparam>
                                </dsp:droplet>
                                </dsp:oparam>
                              </dsp:droplet>
                            </c:when>
                            <c:otherwise>
                              <td class="atg_leftValue">
                                <web-ui:formatNumber value="${(quantity < 0) ? 0 : quantity}"/>
                              </td>
                            </c:otherwise>
                          </c:choose>
                        </c:when>
                        <c:otherwise>
                          <td class="atg_leftValue">
                            <web-ui:formatNumber value="${(quantity < 0) ? 0 : quantity}"/>
                          </td>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>

            </table>
          </td>
        </tr>
      <%--</c:if>--%>
      <%--  End of nested ReturnItem table --%>

</dsp:layeredBundle>
      
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/return/existingReturnRow.jsp#2 $$Change: 1179550 $--%>
