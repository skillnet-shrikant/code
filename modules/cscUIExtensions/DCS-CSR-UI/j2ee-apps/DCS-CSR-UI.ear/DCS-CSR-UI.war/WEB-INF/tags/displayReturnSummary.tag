<%@ tag language="java"%>
<%@ attribute name="returnRequest" required="true" type="atg.commerce.csr.returns.ReturnRequest"%>
<%@ attribute name="isDisplayBalanceDue" required="false" %>

<%@ taglib prefix="dsp"
  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags"  %>

<dsp:page xml="true">
  <dsp:importbean var="agentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools" />
  <dsp:setLayeredBundle basename="atg.commerce.csr.returns.WebAppResources"/>

  <div class="atg_commerce_csr_orderSummary atg_commerce_csr_returnOrderSummary">
  <table class="atg_dataForm">
    <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnExchange">
     <dsp:oparam name="true">
      <dsp:getvalueof var="processName" param="returnProcessName"/>
        <c:choose>
          <c:when test="${processName == 'Return'}">
            <c:choose>
             <c:when test="${isDisplayBalanceDue == true}">
             <tr>
                <td><fmt:message key="returnItems.refundType.table.footer.total.title" /></td>
                <td class="atg_numberValue">
                  <csr:formatNumber value="${-returnRequest.totalRefundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
                </td>
              <tr>
              <td><span class="atg_commerce_csr_orderSummaryTotal">
                <fmt:message key="displayReturnSummary.creditDue" />
              </span></td>
              <td class="atg_numberValue">
              <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance" id="displayCSRCustomerPaymentBalance">
                <csr:formatNumber value="${returnRequest.totalRefundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
              </span>
              </td>
              </tr>
             </c:when>
             <c:otherwise>
             <tr>
              <td>
              <span class="atg_commerce_csr_orderSummaryTotal">
               <fmt:message key="returnItems.refundType.table.footer.total.title" />
              </span>
              </td>
              <td class="atg_numberValue">
               <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance">
                <csr:formatNumber value="${-returnRequest.totalRefundAmount}" type="currency" currencyCode="${returnRequest.order.priceInfo.currencyCode}"/>
               </span>
              </td>
              </tr>
             </c:otherwise>
             </c:choose>
          </c:when>
          <c:when test="${processName == 'Exchange'}">
            <tr>
              <td colspan="2">
              <csr:displayOrderSummary order="${returnRequest.replacementOrder}"
                isShowHeader="false"
                  isDisplayBalanceDue="true"
              />
              </td>
            </tr>
          </c:when>
        </c:choose>
      </dsp:oparam>
    </dsp:droplet>
  </table>
  </div>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayReturnSummary.tag#2 $$Change: 1179550 $--%>
