<%@  include file="/include/top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler"/>
  <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
  <dsp:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dsp:importbean var="CSRAgentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools"/>

  <dsp:getvalueof var="refundMethod" param="refundMethod" />
  <dsp:getvalueof var="refundMethodIndex" param="refundMethodIndex" />
  <dsp:getvalueof var="widgetId" param="widgetId" />
  
  <dsp:getvalueof var="remainingAmount" param="refundMethod.maximumRefundAmount" vartype="java.lang.String"/>
  <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
  <c:set var="currencyCode" value="${returnRequest.order.priceInfo.currencyCode }"/>

  <dsp:droplet name="/atg/commerce/pricing/CurrencyDecimalPlacesDroplet">
    <dsp:param name="currencyCode" value="${currencyCode}"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="currencyDecimalPlaces" param="currencyDecimalPlaces"/>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
    <dsp:getvalueof var="pgType" param="refundMethod.refundType"/>
    <c:choose>
    <c:when test="${pgType == 'creditCard'}">
       <td class="atg_numberValue"><csr:formatNumber value="${refundMethod.creditCard.amountDebited}" type="currency" currencyCode="${currencyCode}" /> </td>
       <td class="atg_numberValue"><csr:formatNumber value="${refundMethod.creditCard.amountCredited}" type="currency" currencyCode="${currencyCode}" /></td>
    </c:when>
    <c:when test="${pgType == 'storeCredit'}">
      <td class="atg_numberValue"><fmt:message key="common.notApplicable" /></td>
      <td class="atg_numberValue"><fmt:message key="common.notApplicable" /></td>
    </c:when>
    <c:otherwise>
      <td class="atg_numberValue"><fmt:message key="common.notApplicable" /></td>
      <td class="atg_numberValue"><fmt:message key="common.notApplicable" /></td>
    </c:otherwise>
    </c:choose>

    <td class="atg_numberValue atg_messaging_requiredIndicator atg_commerce_csr_billingAmount" id="${widgetId}Alert">
    <a href="#"
      class="atg_commerce_csr_iconApplyRemainder"
      title="<fmt:message key="returnItems.refundType.link.applyRemainder.title" />"
      onclick="atg.commerce.csr.order.billing.applyRemainder
      (
        {
          pmtWidget: dijit.byId('${widgetId}'), pmtGroup: '${widgetId}'
        }
      );">
      </a>
      <dsp:input type="text"
        id="${widgetId}"
        onkeyup="atg.commerce.csr.order.billing.recalculatePaymentBalance({pmtWidget: this});"
        bean="ReturnFormHandler.sortedRefundMethodList[param:refundMethodIndex].amount"
        maxlength="20" size="10"
        iclass="atg_numberValue">
          <dsp:tagAttribute name="dojoType" value="atg.widget.validation.CurrencyTextboxEx" />
          <dsp:tagAttribute name="inlineIndicator" value="${widgetId}Alert" />
          <dsp:tagAttribute name="required" value="true" />
          <dsp:tagAttribute name="min" value="0"/>
          <dsp:tagAttribute name="max" value="${remainingAmount}"/>
	      <dsp:tagAttribute name="currency" value="${currencyCode}" />
          <dsp:tagAttribute name="locale" value="${agentUIConfig.javaScriptFormattingLocale}"/>
          <dsp:tagAttribute name="currencySymbol" value="${CSRAgentTools.currentOrderCurrencySymbolInFormattingLocale}"/>
          <dsp:tagAttribute name="constraints" value="{places:${currencyDecimalPlaces}}"/>
      </dsp:input>
      </td>
</dsp:layeredBundle>
</dsp:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/displayPmtCenterFrag.jsp#2 $$Change: 1179550 $--%>
