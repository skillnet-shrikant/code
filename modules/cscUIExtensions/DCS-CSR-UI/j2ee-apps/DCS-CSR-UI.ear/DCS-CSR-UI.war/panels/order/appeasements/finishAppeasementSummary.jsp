<%--
Display the appropriate details for the payment groups of the appeasements

Expected params
appeasement - required - the appeasement object used for rendering the details on the page
displayPaymentTitle - optional - if specified a title will be displayed when only one
                                 payment group exists

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:getvalueof var="appeasement" param="appeasement"/>
  <dsp:getvalueof var="displayPaymentTitle" param="displayPaymentTitle"/>
  <c:set var="currencyCode" value="${appeasement.originatingOrder.priceInfo.currencyCode }"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.appeasement.WebAppResources">

    <%-- 
    We have to count how many methods will actually be displayed so we know whether we need to
    itemize with a header 
    --%>
    <c:set var="displayableMethodCount" value="${0}"/>
    <c:forEach items="${appeasement.refundList}" var="refundMethod" varStatus="rmIndex">
      <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
      <c:set var="displayPaymentOption" value="true"/>
  
      <c:if test="${refundMethod.amount == 0.0}">
        <c:set var="displayPaymentOption" value="false"/>
      </c:if>
      
      <c:if test="${displayPaymentOption}">
        <c:set var="displayableMethodCount" value="${displayableMethodCount + 1}"/>
      </c:if>

    </c:forEach>


    <c:set var="paymentOptionCount" value="${0}"/>
    <%-- In case of an exchange, a store credit is used to balance the exchange order total.
    We need to eliminate the balancing store credit from the display. The application used the store
    credit to pay for the exchange order.
    --%>
    <c:forEach items="${appeasement.refundList}" var="refundMethod" varStatus="rmIndex">

      <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
      <c:set var="displayPaymentOption" value="true"/>

      <c:if test="${refundMethod.amount == 0.0}">
        <c:set var="displayPaymentOption" value="false"/>
      </c:if>
      
      <c:if test="${displayPaymentOption}">
        <c:set var="paymentOptionCount" value="${paymentOptionCount + 1}"/>
        <c:if test="${displayableMethodCount > 1}">
          <fieldset>
            <legend>
              <fmt:message
                key="appeasements.summary.refundTypes.PaymentGroupNumber">
                <fmt:param value="${paymentOptionCount}"/>
              </fmt:message>
            </legend>
        </c:if>
        <c:if test="${not empty displayPaymentTitle}">
          <c:if test="${displayPaymentTitle}">
            <c:if test="${displayableMethodCount == 1}">
              <%-- Display a title when there is only one payment group and
                   a flag has been passed in to display a title. A title
                   will already be displayed when there is more than 1. --%>
              <fieldset>
                <legend>
                  <fmt:message key="appeasements.summary.refundTypes.PaymentTitle"/>
                </legend>
            </c:if>
          </c:if>
        </c:if>




        <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
        <c:if test="${pgTypeConfig != null && pgTypeConfig.displayRefundMethodPageFragment != null}">

          <div class="atg_commerce_csr_statusView">
            <h4>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul>
              <li>
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  <dsp:param name="propertyName" value="value1"/>
                  <dsp:param name="displayValue" value="${true}"/>
                </dsp:include>
              </li>
            </ul>
          </div>

          <div class="atg_commerce_csr_statusView">
            <h4>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul>
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  <dsp:param name="propertyName" value="value2"/>
                  <dsp:param name="displayValue" value="${true}"/>
                </dsp:include>
            </ul>
          </div>

          <div class="atg_commerce_csr_addressView">
            <h4>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul class="atg_svc_shipAddress">
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${appeasement.originatingOrder}"/>
                  <dsp:param name="propertyName" value="value3"/>
                  <dsp:param name="displayValue" value="${true}"/>
                </dsp:include>
            </ul>
          </div>


          <div
            class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
            <h4>
              <fmt:message
                key='appeasements.summary.refundTypes.table.header.amount.title'/>
            </h4>
            <ul>
              <li>
                <csr:formatNumber value="${refundMethod.amount}"
                                  type="currency" currencyCode="${currencyCode}"/>
              </li>
            </ul>
          </div>

        </c:if>
        <c:if test="${fn:length(appeasement.refundList) > 1}">
          </fieldset>
        </c:if>
        <%-- Also need to close fieldset when there is one refund method
             and the title was forced to display --%>
        <c:if test="${not empty displayPaymentTitle}">
          <c:if test="${displayPaymentTitle}">
            <c:if test="${fn:length(appeasement.refundList) == 1}">
              </fieldset>
            </c:if>
          </c:if>
        </c:if>
      </c:if>
    </c:forEach>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/appeasements/finishAppeasementSummary.jsp#1 $$Change: 1179550 $--%>