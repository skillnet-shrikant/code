<%@ tag language="java" %>
<%@ attribute name="order" required="true" type="atg.commerce.order.Order"%>
<%@ attribute name="isShowHeader" required="false" %>
<%@ attribute name="isDisplayBalanceDue" required="false" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="csr" tagdir="/WEB-INF/tags"  %>

<dsp:page xml="true">
  <dsp:importbean var="agentTools" bean="/atg/commerce/custsvc/util/CSRAgentTools" />
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <c:set var="isDisplayReturnCredit" value="${false}"/>
    <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnExchange">
      <dsp:oparam name="true">
		<%-- if the replacement order is the same as the input order, display return credit values --%>
        <dsp:getvalueof var="processName" param="returnProcessName"/>
        <c:if test="${processName == 'Exchange'}">
	        <dsp:getvalueof var="returnObject" param="returnRequest"/>
	        <c:if test="${returnObject.replacementOrder.id == order.id}">
		        <c:set var="isDisplayReturnCredit" value="${true}"/>
	        </c:if>
        </c:if>
      </dsp:oparam>
    </dsp:droplet>
    
      <c:if test="${empty isShowHeader || isShowHeader == true}">
        <span class="atg_commerce_csr_orderSummaryHeader"
          id="atg_commerce_csr_neworder_orderSummaryHeader">
          <fmt:message  key="displayOrderSummary.header" />
        </span>
      </c:if>
      <c:if test="${ ! (empty order.priceInfo || empty order.priceInfo.currencyCode) }">
        <table class="atg_dataForm" id="atg_commerce_csr_neworder_orderSummaryData">
          <tr>
            <td>
              <fmt:message  key="displayOrderSummary.subTotal" />
            </td>
            <td class="arg_commerce_csr_orderSummaryAmount">
              <csr:formatNumber value="${order.priceInfo.rawSubtotal}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
            </td>
         </tr>
            <tr>
              <td>
                <fmt:message  key="displayOrderSummary.discount" />
              </td>
              <td class="arg_commerce_csr_orderSummaryAmount">
                <csr:formatNumber value="${-order.priceInfo.discountAmount}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
              </td>
          </tr>
          <tr>
            <td>
              <fmt:message  key="displayOrderSummary.adjustment" />
            </td>
            <td class="arg_commerce_csr_orderSummaryAmount">
              <csr:formatNumber value="${order.priceInfo.manualAdjustmentTotal}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
            </td>
          </tr>
          <tr>
            <td>
              <fmt:message  key="displayOrderSummary.shipping" />
            </td>
            <td class="arg_commerce_csr_orderSummaryAmount">
              <csr:formatNumber value="${order.priceInfo.shipping}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
            </td>
          </tr>
          <tr>
            <td>
              <fmt:message  key="displayOrderSummary.tax" />
            </td>
            <td class="arg_commerce_csr_orderSummaryAmount">
              <csr:formatNumber value="${order.priceInfo.tax}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
            </td>
          </tr>



          <c:choose>
            <c:when test="${isDisplayReturnCredit}">

			  <%-- display order total, followed by return credit --%>
              <tr>
                <td>
                  <fmt:message key="displayOrderSummary.orderTotal" />
                </td>
                <td class="arg_commerce_csr_orderSummaryAmount">
                  <csr:formatNumber value="${order.priceInfo.total}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                </td>
              </tr>
              <tr>
                <td>
                  <fmt:message key="displayOrderSummary.returnCredit" />
                </td>
                <td class="arg_commerce_csr_orderSummaryAmount">
                  <csr:formatNumber value="${-returnObject.totalRefundAmount}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                </td>
              </tr>

              <c:set var="balance" value="${order.priceInfo.total - returnObject.totalRefundAmount}"/>
                <c:choose>

                <c:when test="${isDisplayBalanceDue == true}"> <%-- isDisplayReturnCredit is true, isDisplayBalanceDue is true --%> 
                  <c:choose>
                    <c:when test="${returnObject.returnPaymentState == 'Refund'}"> <%-- getting a refund --%> 
                    <tr>
                      <td>
                        <span class="atg_commerce_csr_orderSummaryTotal">
                        	<fmt:message key="displayOrderSummary.refundAmount" />
                       	</span>
                      </td>
                      <td class="arg_commerce_csr_orderSummaryAmount">
                        <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance" >
                        <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <td>
                          <fmt:message key="displayOrderSummary.balanceDue" />
                       </td>
                      <td class="arg_commerce_csr_orderSummaryAmount" id="displayCSRCustomerPaymentBalance">
                          <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                      </td>
                    </tr>

                  </c:when>
                  <c:otherwise> <%-- have to pay --%>
                    <tr>
                      <td>
                        <span class="atg_commerce_csr_orderSummaryTotal">
                        <fmt:message key="displayOrderSummary.paymentAmount" />
                        </span>
                      </td>
                      <td class="arg_commerce_csr_orderSummaryAmount">
                        <span class="atg_commerce_csr_orderSummaryTotal atg_csc_positiveBalance" >
                        <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                        </span>
                      </td>
                    </tr>
                    <tr>
                      <td>
                          <fmt:message key="displayOrderSummary.balanceDue" />
                      </td>
                      <td class="arg_commerce_csr_orderSummaryAmount" id="displayCSRCustomerPaymentBalance">
                          <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                      </td>
                    </tr>
                  </c:otherwise>
                  </c:choose>
                </c:when>


                <c:otherwise> <%-- isDisplayReturnCredit is true, isDisplayBalanceDue is false --%>
                  <c:choose>
                    <c:when test="${returnObject.returnPaymentState == 'Refund'}">
                    <tr>
                      <td>
                        <span class="atg_commerce_csr_orderSummaryTotal">
                          <fmt:message key="displayOrderSummary.refundAmount" />
                        </span>
                      </td>
                      <td class="arg_commerce_csr_orderSummaryAmount">
                        <span class="atg_commerce_csr_orderSummaryTotal atg_csc_negativeBalance">
                          <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                        </span>
                      </td>
                    </tr>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td>
                        <span class="atg_commerce_csr_orderSummaryTotal">
                          <fmt:message key="displayOrderSummary.paymentAmount" />
                        </span>
                      </td>
                      <td class="arg_commerce_csr_orderSummaryAmount">
                        <span class="atg_commerce_csr_orderSummaryTotal atg_csc_positiveBalance">
                          <csr:formatNumber value="${balance}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                        </span>
                      </td>
                    </tr>
                  </c:otherwise>
                  </c:choose>
                </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise> <%-- isDisplayReturnCredit is false, isDisplayBalanceDue is true --%> 
              <c:choose> 
                <c:when test="${isDisplayBalanceDue == true}">
                  <tr>
                    <td>
                      <fmt:message key="displayOrderSummary.orderTotal" />
                    </td>
                    <td class="arg_commerce_csr_orderSummaryAmount">
                      <csr:formatNumber value="${order.priceInfo.total}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <span class="atg_commerce_csr_orderSummaryTotal">
                        <fmt:message key="displayOrderSummary.balanceDue" />
                      </span>
                    </td>
                    <td class="arg_commerce_csr_orderSummaryAmount">
                      <span class="atg_commerce_csr_orderSummaryTotal" id="displayCSRCustomerPaymentBalance">
                        <csr:formatNumber value="${order.priceInfo.total}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                      </span>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <tr>
                    <td>
                      <span class="atg_commerce_csr_orderSummaryTotal">
                        <fmt:message key="displayOrderSummary.orderTotal" />
                      </span>
                    </td>
                    <td class="arg_commerce_csr_orderSummaryAmount">
                      <span class="atg_commerce_csr_orderSummaryTotal atg_csc_positiveBalance">
                        <csr:formatNumber value="${order.priceInfo.total}" type="currency" currencyCode="${order.priceInfo.currencyCode}"/>
                      </span>
                    </td>
                  </tr>
                </c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
        </table>
      </c:if>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/displayOrderSummary.tag#2 $$Change: 1179550 $--%>
