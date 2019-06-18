<%--
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsAdjustRefund.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" />
    <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
    <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
    <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>

    <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

  <script type="text/javascript">
     dojo.require("atg.widget.validation.CurrencyTextboxEx");
  </script>

    <dsp:getvalueof var="userOrder" bean="ShoppingCart.current"/>
    <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
    <c:set var="currencyCode" value="${returnRequest.order.priceInfo.currencyCode }"/>

    <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
      <dsp:param name="currencyCode" value="${currencyCode}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
      </dsp:oparam>
    </dsp:droplet>

    <fmt:message key="returnItems.invalidCurrency" var="invalidCurrency"/>

    <%--sometimes users could switch from exchange order to the original order, in that case we should refresh the
        global panel --%>
    <svc-ui:frameworkUrl var="returnProcessSuccessURL" panelStacks="cmcRefundTypePS,globalPanels"/>
    <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcReturnsPS,globalPanels"/>
    <svc-ui:frameworkUrl var="exchangeProcessSuccessURL" panelStacks="cmcCatalogPS,globalPanels"/>
    <svc-ui:frameworkUrl var="cancelReturnRequestSuccessURL" panelStacks="cmcExistingOrderPS,globalPanels"/>


      <dsp:droplet name="HasAccessRight">
        <dsp:param name="accessRight"
          value="commerce-custsvc-create-order-adjustment-privilege" />
        <dsp:oparam name="accessGranted">
          <c:set var="createOrderAdjustment" value="true" />
        </dsp:oparam>
        <dsp:oparam name="accessDenied">
          <c:set var="createOrderAdjustment" value="false" />
        </dsp:oparam>
      </dsp:droplet>

      <dsp:form id="csrAdjustRefunds" formid="csrAdjustRefunds" method="post">
      <dsp:input bean="ReturnFormHandler.startReturnProcessSuccessURL" value="${returnProcessSuccessURL}" type="hidden" />
      <dsp:input bean="ReturnFormHandler.startReturnProcessErrorURL"   value="${errorURL}" type="hidden" />
      <dsp:input name="handleStartReturnProcess" bean="ReturnFormHandler.startReturnProcess" type="hidden" priority="-10" value="" />

      <dsp:input bean="ReturnFormHandler.startExchangeProcessSuccessURL" value="${exchangeProcessSuccessURL}" type="hidden" />
      <dsp:input bean="ReturnFormHandler.startExchangeProcessErrorURL"   value="${errorURL}" type="hidden" />
      <dsp:input name="handleStartExchangeProcess" bean="ReturnFormHandler.startExchangeProcess" type="hidden" priority="-10" value="" />

      <dsp:input bean="ReturnFormHandler.cancelReturnRequestSuccessURL" value="${cancelReturnRequestSuccessURL}" type="hidden" />
      <dsp:input bean="ReturnFormHandler.cancelReturnRequestErrorURL"   value="${errorURL}" type="hidden" />
      <dsp:input name="handleCancelReturnRequest" bean="ReturnFormHandler.cancelReturnRequest" type="hidden" priority="-10" value="" />

      <script type="text/javascript">
          atg.commerce.csr.order.returns.initializeRefundContainer({currencyCode:"<c:out value='${currencyCode}'/>"});
      </script>

        <dsp:droplet name="ForEach">
          <dsp:param bean="ShoppingCart.returnRequest.returnItemList" name="array" />
          <dsp:param name="elementName" value="returnItem" />
          <dsp:param name="indexName" value="returnItemIndex" />
          <dsp:oparam name="outputStart">
          <h3><fmt:message key="returnItems.adjustRefund.header.return" /></h3>
            <table class="atg_dataTable">
              <thead>
                <tr>
                  <th class="atg_numberValue"><fmt:message key="returnItems.adjustRefund.returnItem.header.qtyToReturn" /></th>
                  <th><fmt:message key="returnItems.adjustRefund.returnItem.header.sku" /></th>
                  <th><fmt:message key="returnItems.adjustRefund.returnItem.header.name" /></th>
                  <th class="atg_numberValue"><fmt:message key="returnItems.adjustRefund.returnItem.header.suggestedRefundAmount" /></th>
                  <th class="atg_numberValue"><fmt:message key="returnItems.adjustRefund.returnItem.header.actualRefundAmount" /></th>
              <tfoot>
              </tfoot>
          </dsp:oparam>

          <!-- Render details about this return item -->
          <dsp:oparam name="output">
            <dsp:getvalueof var="returnItem" param="returnItem"/>
            <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function(){
                atg.commerce.csr.order.returns.addReturnItem
                (
                  {
                    id:"<c:out value='${returnItem.id}'/>",
                    amount:"<c:out value='${returnItem.suggestedRefundAmount}'/>"
                  }
                );});
            </script>

            <dsp:droplet name="Switch">
              <dsp:param name="value" param="returnItem.quantityToReturn" />
              <dsp:oparam name="0"></dsp:oparam>
              <dsp:oparam name="default">
                <dsp:getvalueof id="itemIndex" param="returnItemIndex" idtype="java.lang.Integer"/>
                  <tr class="${((itemIndex % 2)==0) ? '' : 'atg_altRow'}">
                    <td class="atg_numberValue">
                      <web-ui:formatNumber value="${returnItem.quantityToReturn}" />
                    </td>
                    <td>
                    <dsp:valueof param="returnItem.commerceItem.catalogRefId" /></p>
                    </td>
                    <td>
                    <dsp:valueof param="returnItem.commerceItem.auxiliaryData.catalogRef.displayName" />
                    </td>
                    <td class="atg_numberValue">
                      <csr:formatNumber value="${returnItem.suggestedRefundAmount}" type="currency" currencyCode="${currencyCode}" />
                    </td>
                    <td class="atg_numberValue atg_messaging_requiredIndicator" id="${returnItem.id}Alert">
                      <dsp:input maxlength="20" id="${returnItem.id}"
                      size="10" type="text" bean="ReturnFormHandler.returnRequest.returnItemList[param:returnItemIndex].refundAmount"
                         onkeyup="atg.commerce.csr.order.returns.recalculateTotal({returnItem:this});">
                        <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
                        <dsp:tagAttribute name="required" value="true" />
                        <dsp:tagAttribute name="inlineIndicator" value="${returnItem.id}Alert" />
                        <dsp:tagAttribute name="invalidMessage" value="${invalidCurrency}" />
                        <dsp:tagAttribute name="currency" value="${currencyCode}" />
                        <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                        <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                        <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
                      </dsp:input>
                    </td>
                  </tr>
              </dsp:oparam>
            </dsp:droplet>

          </dsp:oparam>
          <dsp:oparam name="outputEnd">
          <%-- The below snippet transfers the amount to the js object and also formats the
          refund currency widget amount.--%>
            <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function(){
                atg.commerce.csr.order.returns.initializeRefunds
                (
                  {
                   shippingRefund:"<c:out value='${returnRequest.actualShippingRefund}'/>",
                   taxRefund:"<c:out value='${returnRequest.actualTaxRefund}'/>",
                   otherRefund:"<c:out value='${returnRequest.otherRefund}'/>",
                   returnFee:"<c:out value='${returnRequest.returnFee}'/>"
                  });

                atg.commerce.csr.order.returns.formatRefundWidgets
                (
                  {
                   shippingRefund:"actualShippingRefund",
                   taxRefund:"actualTaxRefund",
                   otherRefund:"otherRefund",
                   returnFee:"returnFee"
                  });

                  });
            </script>
            <tr>
              <td colspan="3" class="atg_rightValue atg_positiveTotal"><fmt:message key="returnItems.adjustRefund.subTotal" /></td>
              <td class="atg_numberValue">&nbsp;</td>
              <td class="atg_numberValue atg_numberValueExtraPadding">
                <div id="csrReturnTotalItemRefund">
                  <csr:formatNumber value="${returnRequest.totalItemRefund}" type="currency" currencyCode="${currencyCode}" />
                </div>
              </td>
            </tr>
            <tr class="atg_altRow">
              <td colspan="3" class="atg_rightValue"><fmt:message key="returnItems.adjustRefund.refundForShipping" /></td>
              <td class="atg_numberValue">
               <csr:formatNumber value="${returnRequest.suggestedShippingRefund}" type="currency" currencyCode="${currencyCode}" />
              </td>
              <td class="atg_numberValue atg_messaging_requiredIndicator" id="actualShippingRefundAlert">
              <dsp:input bean="ReturnFormHandler.returnRequest.actualShippingRefund"
                         maxlength="20" size="10" type="text" id="actualShippingRefund"
                         onkeyup="atg.commerce.csr.order.returns.recalculateTotal({shippingRefund:this});">
                <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
                <dsp:tagAttribute name="required" value="true" />
                <dsp:tagAttribute name="inlineIndicator" value="actualShippingRefundAlert" />
                <dsp:tagAttribute name="invalidMessage" value="${invalidCurrency}" />
                <dsp:tagAttribute name="currency" value="${currencyCode}" />
                <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
              </dsp:input>
            </td>
            </tr>
            <tr>
              <td colspan="3" class="atg_rightValue"><fmt:message key="returnItems.adjustRefund.refundForTaxes" /></td>
              <td class="atg_numberValue">
               <csr:formatNumber value="${returnRequest.suggestedTaxRefund}" type="currency" currencyCode="${currencyCode}" />
              </td>
              <td class="atg_numberValue atg_messaging_requiredIndicator" id="actualTaxRefundAlert">
                <dsp:input bean="ReturnFormHandler.returnRequest.actualTaxRefund"
                         maxlength="20" size="10" type="text" id="actualTaxRefund"
                         onkeyup="atg.commerce.csr.order.returns.recalculateTotal({taxRefund:this});">
                <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
                <dsp:tagAttribute name="required" value="true" />
                <dsp:tagAttribute name="inlineIndicator" value="actualTaxRefundAlert" />
                <dsp:tagAttribute name="invalidMessage" value="${invalidCurrency}" />
                <dsp:tagAttribute name="currency" value="${currencyCode}" />
                <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
              </dsp:input>
              </td>
            </tr>
            <tr class="atg_altRow">
              <td colspan="3" class="atg_rightValue"><fmt:message key="returnItems.adjustRefund.refundForOther" /></td>
              <td class="atg_numberValue">&nbsp;</td>
              <td class="atg_numberValue atg_messaging_requiredIndicator" id="otherRefundAlert">
                <c:choose>
                  <c:when test="${createOrderAdjustment}">
                   <dsp:input bean="ReturnFormHandler.returnRequest.otherRefund"
                               maxlength="20" size="10" type="text" id="otherRefund"
                                 onkeyup="atg.commerce.csr.order.returns.recalculateTotal({otherRefund:this});">
                      <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
                      <dsp:tagAttribute name="required" value="true" />
                      <dsp:tagAttribute name="inlineIndicator" value="otherRefundAlert" />
                      <dsp:tagAttribute name="invalidMessage" value="${invalidCurrency}" />
                      <dsp:tagAttribute name="currency" value="${currencyCode}" />
                      <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                      <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                      <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
                    </dsp:input>
                  </c:when>
                  <c:otherwise>
                    <csr:formatNumber value="${returnRequest.otherRefund}" type="currency" currencyCode="${currencyCode}" />
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td colspan="3" class="atg_rightValue atg_positiveTotal"><fmt:message key="returnItems.adjustRefund.refundSubtotal" /></td>
              <td class="atg_numberValue">&nbsp;</td>
              <td class="atg_numberValue atg_numberValueExtraPadding">
               <div id="csrReturnRefundSubtotal">
                 <csr:formatNumber value="${returnRequest.refundSubtotal}" type="currency" currencyCode="${currencyCode}" />
               </div>
              </td>
            </tr>
            <tr class="atg_altRow">
              <td colspan="3" class="atg_rightValue atg_positiveTotal"><fmt:message key="returnItems.adjustRefund.lessReturnFee" /></td>
              <td class="atg_numberValue">&nbsp;</td>
              <td class="atg_numberValue atg_messaging_requiredIndicator" id="returnFeeAlert">
              <dsp:input bean="ReturnFormHandler.returnRequest.returnFee"
                       maxlength="20" size="10" type="text" id="returnFee"
                       onkeyup="atg.commerce.csr.order.returns.recalculateTotal({returnFee:this});">
                      <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
                      <dsp:tagAttribute name="required" value="true" />
                      <dsp:tagAttribute name="inlineIndicator" value="returnFeeAlert" />
                      <dsp:tagAttribute name="invalidMessage" value="${invalidCurrency}" />
                      <dsp:tagAttribute name="currency" value="${currencyCode}" />
                      <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
                      <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
                      <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
                   </dsp:input>
              </td>
            </tr>
            <tr>
              <td colspan="3" class="atg_rightValue atg_positiveTotal"><fmt:message key="returnItems.adjustRefund.totalRefund" /></td>
              <td class="atg_numberValue">&nbsp;</td>
              <td class="atg_numberValue atg_numberValueExtraPadding">
               <div id="csrReturnRefundTotal">
                <csr:formatNumber value="${returnRequest.totalRefundAmount}" type="currency" currencyCode="${currencyCode}" />
               </div>
              </td>
            </tr>
            </table>
            <div class="atg_commerce_csr_panelFooter">
            <div class="atg_actionTo">

            <input name="StartReturnProcess" id="StartReturnProcess" type="button" 
              onclick="atg.commerce.csr.order.returns.startReturnProcess();" 
              value="<fmt:message key="returnItems.button.processReturn.title" />" 
              dojoType="atg.widget.validation.SubmitButton"/>

            <input name="StartExchangeProcess" id="StartExchangeProcess" type="button" 
              onclick="atg.commerce.csr.order.returns.startExchangeProcess();" 
              value="<fmt:message key="returnItems.button.startExchange.title" />" 
              dojoType="atg.widget.validation.SubmitButton"/>

            <csr:displayReturnPanelsCancelButton
              cancelIconOnclickURL="atg.commerce.csr.order.returns.cancelReturnRequest();"
              cancelActionErrorURL="${errorURL}"
              order="${userOrder}" />
              </div>
          </dsp:oparam>
        </dsp:droplet>
        <!-- ForEach returnItem -->

      </dsp:form>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- end of singleShipping.jsp --%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsAdjustRefund.jsp#2 $$Change: 1179550 $--%>
