<%--
Display the appropriate details for the payment group.

Expected params
paymentGroup : The payment group.
paymentGroupIndex : Used to render alternate rows in the table in different styles.
currencyCode : The order.priceInfo.currencyCode value.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>

  <dsp:getvalueof var="returnRequest" param="returnRequest"/>
  <c:set var="currencyCode"
         value="${returnRequest.order.priceInfo.currencyCode }"/>

  <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

    <%-- 
    We have to count how many methods will actually be displayed so we know whether we need to
    itemize with a header 
    --%>
    <c:set var="displayableMethodCount" value="${0}"/>
    <c:forEach items="${returnRequest.refundMethodList}"
      var="refundMethod" varStatus="rmIndex">
      <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
      <c:set var="displayPaymentOption" value="true"/>
  
      <c:if test="${pgType == 'storeCredit' && refundMethod.appGeneratedStoreCredit}">
        <c:set var="displayPaymentOption" value="false"/>
      </c:if>
  
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
    <c:forEach items="${returnRequest.refundMethodList}"
               var="refundMethod" varStatus="rmIndex">

      <dsp:getvalueof var="pgType" value="${refundMethod.refundType}"/>
      <c:set var="displayPaymentOption" value="true"/>

      <c:if test="${pgType == 'storeCredit' && refundMethod.appGeneratedStoreCredit}">
        <c:set var="displayPaymentOption" value="false"/>
      </c:if>

      <c:if test="${refundMethod.amount == 0.0}">
        <c:set var="displayPaymentOption" value="false"/>
      </c:if>
      
      <c:if test="${displayPaymentOption}">
        <c:set var="paymentOptionCount" value="${paymentOptionCount + 1}"/>
        <c:if test="${displayableMethodCount > 1}">
          <fieldset>
            <legend>
              <fmt:message
                key="finishReturn.refundTypes.PaymentGroupNumber">
                <fmt:param value="${paymentOptionCount}"/>
              </fmt:message>
            </legend>
        </c:if>

        <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
        <c:if test="${pgTypeConfig != null && pgTypeConfig.displayRefundMethodPageFragment != null}">

          <div class="atg_commerce_csr_statusView">
            <h4>
              <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                           otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                <dsp:param name="refundMethod" value="${refundMethod}"/>
                <dsp:param name="order" value="${returnRequest.order}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul>
              <li>
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${returnRequest.order}"/>
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
                <dsp:param name="order" value="${returnRequest.order}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul>
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${returnRequest.order}"/>
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
                <dsp:param name="order" value="${returnRequest.order}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayHeading" value="${true}"/>
              </dsp:include>
            </h4>
            <ul class="atg_svc_shipAddress">
                <dsp:include src="${pgTypeConfig.displayRefundMethodPageFragment.URL}"
                             otherContext="${pgTypeConfig.displayRefundMethodPageFragment.servletContext}">
                  <dsp:param name="refundMethod" value="${refundMethod}"/>
                  <dsp:param name="order" value="${returnRequest.order}"/>
                  <dsp:param name="propertyName" value="value3"/>
                  <dsp:param name="displayValue" value="${true}"/>
                </dsp:include>
            </ul>
          </div>


          <div
            class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
            <h4>
              <fmt:message
                key='finishReturn.refundTypes.table.header.amount.title'/>
            </h4>
            <ul>
              <li>
                <csr:formatNumber value="${refundMethod.amount}"
                                  type="currency" currencyCode="${currencyCode}"/>
              </li>
            </ul>
          </div>

        </c:if>
        <c:if test="${fn:length(returnRequest.refundMethodList) > 1}">
          </fieldset>
        </c:if>
      </c:if>
    </c:forEach>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/finishRefundSummary.jsp#2 $$Change: 1179550 $--%>
