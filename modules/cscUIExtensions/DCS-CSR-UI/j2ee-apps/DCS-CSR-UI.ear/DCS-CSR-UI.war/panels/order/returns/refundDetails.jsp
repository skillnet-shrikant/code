 <%--
 This page allows the user to specify the various amounts that are to be refunded
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/refundDetails.jsp#2 $
 @updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@  include file="/include/top.jspf"%>

<c:catch var="exception">
  <dsp:page xml="true">

<dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/NonReturnItemDetailsDroplet"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnManager" var="returnManager"/>
<dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnItemQuantityInfoDroplet"/>

<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

<%-- this param determines if this page allows updates to the refund values --%>
<dsp:getvalueof var="modifiable" param="modifiable"/>
<dsp:getvalueof var="returnRequest" param="returnRequest"/>

  <c:if test="${returnRequest.processName eq 'Exchange'}">
    <svc-ui:frameworkUrl var="refundURL" panelStacks="cmcRefundTypePS"/>
    <svc-ui:frameworkUrl var="paymentURL" panelStacks="cmcBillingPS" init="true"/>
    <svc-ui:frameworkUrl var="washURL" panelStacks="cmcBillingPS"  init="true"/>
    <svc-ui:frameworkUrl var="errorURL" panels="returnDetails"/>
  </c:if>
  <c:if test="${returnRequest.processName eq 'Return'}">
    <svc-ui:frameworkUrl var="refundURL" panelStacks="cmcRefundTypePS"/>
    <svc-ui:frameworkUrl var="paymentURL" panelStacks="cmcRefundTypePS"/>
    <svc-ui:frameworkUrl var="washURL" panelStacks="cmcRefundTypePS"/>
    <svc-ui:frameworkUrl var="errorURL" panels="returnDetails"/>
  </c:if>

  <dsp:importbean bean="/atg/commerce/custsvc/returns/ModifyRefundValuesFormHandler"/>

  <dsp:form method="post" formid="modifyRefundValuesForm" id="modifyRefundValuesForm" action="#">
  <dsp:input bean="ModifyRefundValuesFormHandler.modifyRefundValuesErrorURL" type="hidden" value="${errorURL}"/>
  <dsp:input bean="ModifyRefundValuesFormHandler.refundPageURL" type="hidden" value="${refundURL}"/>
  <dsp:input bean="ModifyRefundValuesFormHandler.paymentPageURL" type="hidden" value="${paymentURL}"/>
  <dsp:input bean="ModifyRefundValuesFormHandler.washPageURL" type="hidden" value="${washURL}"/>


  <div class="atg_commerce_csr_corePanelData">
<dsp:droplet name="/atg/dynamo/droplet/ForEach">
<dsp:param value="${returnRequest.returnItemList}" name="array" />
<dsp:oparam name="outputStart">
  <table class="atg_dataTable atg_commerce_csr_returnDetailsTable">
  <thead>
    <tr>
      <c:if test="${isMultiSiteEnabled == true}">
        <th class="atg_commerce_csr_siteIcon"></th>                     
      </c:if>
      <th class="atg_commerce_csr_returnItemName"><fmt:message key="refundDetails.returnItems"/></th>
      <th><fmt:message key="refundDetails.quantity"/></th>                     
      <th class="atg_commerce_csr_returnItemAmount">Amount</th>                     
    </tr>
  </thead>
  <tbody>
</dsp:oparam>
<dsp:oparam name="outputEnd">
  </tbody>
  </table>
</dsp:oparam>
<dsp:oparam name="output">
  <dsp:getvalueof var="returnItem" param="element"/>


  <tr class="atg_commerce_csr_returnedItem">
    <c:if test="${isMultiSiteEnabled == true}">
      <td class="atg_commerce_csr_siteIcon"><csr:siteIcon siteId="${returnItem.commerceItem.auxiliaryData.siteId}" /></td>                  
    </c:if>
  
  <td class="atg_commerce_csr_returnItemName">
  
    <ul class="atg_commerce_csr_itemDesc">
      <li><dsp:tomap var="productRef" value="${returnItem.commerceItem.auxiliaryData.productRef}"/>${fn:escapeXml(productRef.displayName)}

      </li>
      <li class="atg_commerce_csr_returnItemSku"><dsp:valueof param="element.commerceItem.catalogRefId"/>
      </li>

      <c:if test="${returnManager.doShippingRefunds == true}">
        <li class="atg_commerce_csr_returnItemSuggestedShipping"><fmt:message key="refundDetails.suggestedShipping"/>&nbsp; <dsp:getvalueof var="suggestedShippingRefundShare" param="element.suggestedShippingRefundShare"/><csr:formatNumber value="${-suggestedShippingRefundShare}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
        </li>
      </c:if>
    </ul>
  
  </td>
  <td class="atg_commerce_csr_returnQty">
    <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
      <dsp:param name="item" param="element"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="quantityToReturn" param="quantityToReturn"/>
        <web-ui:formatNumber value="${quantityToReturn}"/>
      </dsp:oparam>
    </dsp:droplet>
  </td>
  <td class="atg_commerce_csr_returnTotal">
	<csr:formatNumber value="${-returnItem.refundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
  </td>
  </tr>
</dsp:oparam>
</dsp:droplet>

<dsp:droplet name="NonReturnItemDetailsDroplet">
<dsp:param value="${returnRequest}" name="returnRequest" />
<dsp:oparam name="outputStart">

  <table class="atg_dataTable atg_commerce_csr_returnDetailsTable atg_commerce_csr_nonReturns">
  <thead>
    <tr class="atg_commerce_csr_nonReturnedItemHeader">
      <c:if test="${isMultiSiteEnabled == true}">
        <th></th>                     
      </c:if>
      <th class="atg_commerce_csr_returnItemName"><fmt:message key="refundDetails.nonReturnItems"/></th>
      <th><fmt:message key="refundDetails.quantity"/></th>                     
      <th class="atg_commerce_csr_returnTotal"></th>
    </tr>
  </thead>
  <tbody>   

  </dsp:oparam>
<dsp:oparam name="outputEnd">

  </tbody>
  </table>

</dsp:oparam>
<dsp:oparam name="outputEmpty">
</dsp:oparam>
<dsp:oparam name="returnDetail">

<dsp:getvalueof var="totalAdjustment" param="totalAdjustment"/>
<dsp:getvalueof var="item" param="commerceItem"/>

  <tr class="atg_commerce_csr_nonReturnedItem">
  <c:if test="${isMultiSiteEnabled == true}">
    <td class="atg_commerce_csr_siteIcon"><csr:siteIcon siteId="${item.auxiliaryData.siteId}" /></td>                  
  </c:if>
  <td class="atg_commerce_csr_returnItemName">
  
    <ul class="atg_commerce_csr_itemDesc">
  
      <li><dsp:tomap var="productRef" value="${item.auxiliaryData.productRef}"/>${fn:escapeXml(productRef.displayName)}
      </li>
      <li class="atg_commerce_csr_returnItemSku"><dsp:valueof param="commerceItem.catalogRefId"/>
      </li>
    </ul>
  </td>
  <td class="atg_commerce_csr_returnQty">
    <dsp:getvalueof var="quantityAdjusted" param="quantityAdjusted"/>
    <web-ui:formatNumber value="${quantityAdjusted}"/>
  </td>
  <td class="atg_commerce_csr_returnTotal atg_numberValue">
    <dsp:getvalueof var="totalAdjustment" param="totalAdjustment"/>
    <csr:formatNumber value="${totalAdjustment}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
  </td>
  </tr>

</dsp:oparam>
</dsp:droplet>


  <dsp:include src="/panels/order/returns/promotionValueChange.jsp" otherContext="${CSRConfigurator.contextRoot}">
    <dsp:param name="returnRequest" value="${returnRequest}"/>
    <dsp:param name="currencyCode" value="${returnRequest.order.priceInfo.currencyCode}"/>
  </dsp:include>

  <div class="atg_commerce_csr_shoppingCartSummary">
    <div class="atg_commerce_csr_orderSummary atg_commerce_csr_returnDetailSummary">

      <table class="atg_dataForm" id="atg_commerce_csr_neworder_orderSummaryData">
        <tr>
          <td><fmt:message key="refundDetails.subTotal"/></td>
          <td class="arg_commerce_csr_orderSummaryAmount"><csr:formatNumber value="${-returnRequest.totalItemRefund}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/></td>
        </tr>
        <tr>
          <td><fmt:message key="refundDetails.tax"/></td>
          <td class="arg_commerce_csr_orderSummaryAmount"><csr:formatNumber value="${-returnRequest.actualTaxRefund}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/></td>
        </tr>
        <tr>
          <td><fmt:message key="refundDetails.shippingAdjustment"/></td>
          <td class="arg_commerce_csr_orderSummaryAmount">
            <c:choose>
            <c:when test="${modifiable eq 'true'}">
              <dsp:input type="text" bean="ModifyRefundValuesFormHandler.shippingAdjustment" size="12">
                <dsp:tagAttribute name="maxlength" value="10" />
              </dsp:input>
            </c:when>
            <c:otherwise>
              <csr:formatNumber value="${-returnRequest.actualShippingRefund}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
            </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <c:if test="${modifiable eq 'false'}">
            <td><fmt:message key="refundDetails.otherAdjustment"/></td>
            <td class="arg_commerce_csr_orderSummaryAmount">
              <csr:formatNumber value="${-returnRequest.otherRefund}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
          </c:if>
        </td>
      </tr>
       <tr>
         <td><fmt:message key="refundDetails.lessReturnFee"/></td>
         <td class="arg_commerce_csr_orderSummaryAmount"><csr:formatNumber value="${returnRequest.returnFee}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/></td>
       </tr>
       <tr>
         <td>
           <label class="atg_commerce_csr_orderSummaryTotal">
           <fmt:message key="refundDetails.returnCredit"/>
           </label>
          </td>
          <td class="arg_commerce_csr_orderSummaryAmount">
            <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance">
            <csr:formatNumber value="${-returnRequest.totalRefundAmount}" type="currency"  currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
            </span>
          </td>
        </tr>
      </table>
      <dsp:input type="hidden" priority="-10" bean="ModifyRefundValuesFormHandler.modifyRefundValues" value="" />
      </dsp:form>
     </div>
     <div class="atg_commerce_csr_panelFooter">
     
       <c:if test="${modifiable eq 'true'}">
       <input type="submit" onclick="atg.commerce.csr.order.returns.resetRefundValues();return false;" value="<fmt:message key='refundDetails.resetDefaults'/>"/>
         <input type="submit" onclick="atg.commerce.csr.order.returns.modifyRefundValues();return false;" value="<fmt:message key='refundDetails.updateAmounts'/>"/>
         <dsp:form method="post" formid="resetRefundValues" id="resetRefundValues" action="#">
         <dsp:input bean="ModifyRefundValuesFormHandler.resetRefundValuesErrorURL" type="hidden" value="${errorURL}"/>
         <dsp:input bean="ModifyRefundValuesFormHandler.refundPageURL" type="hidden" value="${refundURL}"/>
         <dsp:input bean="ModifyRefundValuesFormHandler.paymentPageURL" type="hidden" value="${paymentURL}"/>
         <dsp:input bean="ModifyRefundValuesFormHandler.washPageURL" type="hidden" value="${washURL}"/>
         <dsp:input type="hidden" priority="-10" bean="ModifyRefundValuesFormHandler.resetRefundValues" value="" />
         </dsp:form>
       </c:if>
     </div>
   </div>

</dsp:layeredBundle>

</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}" />
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/refundDetails.jsp#2 $$Change: 1179550 $--%>
