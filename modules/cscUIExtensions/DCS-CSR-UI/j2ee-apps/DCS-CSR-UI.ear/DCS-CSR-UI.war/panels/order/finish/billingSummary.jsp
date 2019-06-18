<%--
Display details of all the payment groups in the order. Use the
finishOrderBillingLineItem.jsp to render each line item.

Expected params
currentOrder : The order that the payment group details are retrieved from.

@version $Id:
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>
  <dsp:getvalueof var="order" param="currentOrder"/>
  <dsp:getvalueof var="isExistingOrderView" param="isExistingOrderView"/>

  <c:if test="${empty isExistingOrderView}">
    <c:set var="isExistingOrderView" value="false"/>
  </c:if>
  
  <csr:getCurrencyCode order="${order}">
   <c:set var="currencyCode" value="${currencyCode}" scope="request" />
  </csr:getCurrencyCode> 
  
  <%-- Order may contain payment groups with amount=0.00. We do not want to display
       payment groups with amount zero. We can't remove all payment groups with zero amount.
       The payment groups may contain remaining payment amount type and it may be assigned 0.00 amount.
       Thus we are deciding the multi payment group view based on the non-zero payment groups.  
  --%>
   <c:set var="multiplePaymentGroupsView" value="${false}" scope="request" />
   <c:set var="nonZeroPaymentGroupsCount" value="${0}" scope="request" />
  <c:choose>
    <c:when test="${order.paymentGroupCount > 0}">
      <c:forEach items="${order.paymentGroups}"
                 var="paymentGroup" varStatus="paymentGroupIndex">
       <c:if test="${paymentGroup.amount > 0}">
        <c:set var="nonZeroPaymentGroupsCount" value="${nonZeroPaymentGroupsCount + 1}"/>
        <c:if test="${nonZeroPaymentGroupsCount > 1}">
          <c:set var="multiplePaymentGroupsView" value="${true}"/>
        </c:if>
       </c:if>
      </c:forEach>
    </c:when>
  </c:choose>

  <c:choose>
    <c:when test="${order.paymentGroupCount > 0}">
      <c:forEach items="${order.paymentGroups}"
                 var="paymentGroup" varStatus="paymentGroupIndex">

        <c:if test="${multiplePaymentGroupsView && paymentGroup.amount > 0}">
          <fieldset>
            <legend>
              <fmt:message key="finishOrder.billingSummary.PaymentGroupNumber">
                <fmt:param value="${paymentGroupIndex.count}"/>
              </fmt:message>
            </legend>
        </c:if>
       <c:if test="${paymentGroup.amount > 0}">
        <div class="atg_commerce_csr_statusView">
          <h4>
            <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="paymentGroup" value="${paymentGroup}"/>
              <dsp:param name="propertyName" value="value1"/>
              <dsp:param name="displayHeading" value="${true}"/>
              <dsp:param name="order" value="${order}"/>
            </dsp:include>
          </h4>
          <ul>
            <li>
              <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                <dsp:param name="propertyName" value="value1"/>
                <dsp:param name="displayValue" value="${true}"/>
                <dsp:param name="order" value="${order}"/>
              </dsp:include>
            </li>
          </ul>
        </div>

        <c:if test="${paymentGroup.paymentGroupClassType != 'inStorePayment'}">
          <div class="atg_commerce_csr_statusView">
            <h4>
              <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                <dsp:param name="propertyName" value="value2"/>
                <dsp:param name="displayHeading" value="${true}"/>
                <dsp:param name="order" value="${order}"/>
              </dsp:include>
            </h4>
            <ul>
              <li>
                <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                  <dsp:param name="propertyName" value="value2"/>
                  <dsp:param name="displayValue" value="${true}"/>
                  <dsp:param name="order" value="${order}"/>
                </dsp:include>
              </li>
            </ul>
          </div>
        </c:if>

        <div class="atg_commerce_csr_addressView">
          <h4>
            <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="paymentGroup" value="${paymentGroup}"/>
              <dsp:param name="propertyName" value="value3"/>
              <dsp:param name="displayHeading" value="${true}"/>
              <dsp:param name="order" value="${order}"/>
            </dsp:include>
          </h4>
          <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
              <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                <dsp:param name="propertyName" value="value3"/>
                <dsp:param name="displayValue" value="${true}"/>
                <dsp:param name="order" value="${order}"/>
              </dsp:include>
          </ul>
        </div>

        <%-- In order to get the right alignment, we need to display the amount first and the status comes next, but the
        browser
        knows to render status first and amount next. atg_commerce_csr_statusTabularView is responsible for this
        layout--%>
        <div class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
          <h4>
            <c:choose>
              <c:when test="${paymentGroup.paymentGroupClassType != 'inStorePayment'}">
                <fmt:message key='billingSummary.commerceItem.header.amount'/>
              </c:when>
              <c:otherwise>
                <fmt:message key='billingSummary.commerceItem.header.maxAmount'/>
              </c:otherwise>
            </c:choose>
          </h4>
          <ul>
            <li>
              <csr:formatNumber value="${paymentGroup.amount}" type="currency"
                                currencyCode="${currencyCode}"/>
            </li>
          </ul>
        </div>

        <c:if test="${isExistingOrderView}">
          <div class="atg_commerce_csr_statusView">
            <h4>
              <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                <dsp:param name="propertyName" value="status"/>
                <dsp:param name="displayHeading" value="${true}"/>
                <dsp:param name="order" value="${order}"/>
              </dsp:include>
            </h4>
            <ul>
              <li>
                <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" flush="false" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                  <dsp:param name="propertyName" value="status"/>
                  <dsp:param name="displayValue" value="${true}"/>
                  <dsp:param name="order" value="${order}"/>
                </dsp:include>
              </li>
            </ul>
          </div>
        </c:if>
        
        </c:if>
        <c:if test="${multiplePaymentGroupsView && paymentGroup.amount > 0}">
          </fieldset>
        </c:if>
      </c:forEach>
    </c:when>
  </c:choose>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/finish/billingSummary.jsp#2 $$Change: 1179550 $--%>
