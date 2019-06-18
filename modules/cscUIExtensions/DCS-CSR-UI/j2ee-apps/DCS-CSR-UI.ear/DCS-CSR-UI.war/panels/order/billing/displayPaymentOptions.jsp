<%--
This jsp fragment is used to display all payment methods.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/displayPaymentOptions.jsp#1 $$Change: 946917 $
--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupDroplet"/>
<dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/PaymentGroupFormHandler"/>
<dsp:getvalueof var="userOrder" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<%--
This code walks through the current initialized payment groups and
find the payment group in the order level payment options.
By using the payment groups, this page provides the option to sort the list of payment
groups by the payment type and if it is preferred other properties could be used as the
sorting parameter.
--%>

<%--This variable is to keep track of the payment type that is being served. We need to display the
the headings for the each payment type, then serve the payment options of that type. This variable is
used to keep track of the previous type is served and if the type changes, then we need to display the heading
for the new type. --%>
<c:set var="previousPGType" value=""/>

<dsp:droplet name="ForEach">
<dsp:param name="array" param="paymentGroups"/>
<dsp:param name="elementName" value="paymentGroup"/>
<dsp:param name="sortProperties" value="-paymentGroupClassType"/>
<dsp:oparam name="output">
  <dsp:getvalueof var="pgKey" param="key"/>
  <c:set var="pgFound" value="false"/>

  <dsp:droplet name="ForEach">
    <dsp:param name="array" param="paymentGroupList"/>
    <dsp:param name="elementName" value="listElement"/>
    <dsp:oparam name="output">
      <c:if test="${!pgFound}">
        <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
        <dsp:getvalueof var="cipi" param="listElement"/>

        <dsp:droplet name="Switch">
          <dsp:param name="value" value="${cipi.paymentMethod}"/>
          <dsp:oparam name="${pgKey}">
            <c:set var="pgFound" value="true"/>
            <c:set var="displayPaymentOption" value="${true}"/>

            <dsp:droplet name="/atg/commerce/custsvc/order/PaymentGroupRemainingAmount">
              <dsp:param name="paymentGroup" value="${paymentGroup}"/>
              <dsp:param name="order" value="${userOrder}"/>
              <dsp:oparam name="output">
                <dsp:getvalueof var="maxAllowedAmount" param="maxAllowedAmount" />
              </dsp:oparam>
            </dsp:droplet>
            <%--
            If the payment option amount to be restricted for a maximum payment amount,
            then the maxAllowedAmount should be passed in and payment option amount could
            be assigned to the maximum amount.
            --%>
            <c:choose>
              <c:when test="${paymentGroup.paymentGroupClassType == 'giftCard'}">
                <c:set var="initialAmount" value="${cipi.amount}" />
                <c:set var="amount" value="0" />
              </c:when>
              <c:otherwise>
                <c:set var="initialAmount" value="${cipi.amount}" />
                <c:set var="amount" value="${cipi.amount}" />
              </c:otherwise>
            </c:choose>
            <c:choose>
            <%--If the payment option's maximum allowed amount is zero, then the payment option will not be displayed in the UI.
                BUGS-FIXED: CSC-163676
             --%>
            <c:when test="${!empty maxAllowedAmount && maxAllowedAmount == '0.0'}">
               <c:set var="displayPaymentOption" value="${false}"/>
            </c:when>
            <c:when test="${!empty maxAllowedAmount && maxAllowedAmount != '-1.0'}">
              <script type="text/javascript">
                _container_.onLoadDeferred.addCallback(function() {
                  atg.commerce.csr.order.billing.addPaymentMethod({
                    paymentGroupId: '${paymentGroup.id}',
                    paymentGroupType: '${paymentGroup.paymentGroupClassType}',
                    amount: '${amount}',
                    initialAmount: '${initialAmount}',
                    maxAllowedAmount: '${maxAllowedAmount}'
                  });
                });
              </script>
            </c:when>
            <c:otherwise>
              <script type="text/javascript">
                _container_.onLoadDeferred.addCallback(function() {
                  atg.commerce.csr.order.billing.addPaymentMethod({
                    paymentGroupId: '${paymentGroup.id}',
                    paymentGroupType: '${paymentGroup.paymentGroupClassType}',
                    amount: '${amount}',
                    initialAmount: '${initialAmount}'
                  });
                });
              </script>
            </c:otherwise>
            </c:choose>
            <dsp:droplet name="/atg/commerce/custsvc/returns/IsReturnActive">
              <dsp:oparam name="true">
                <dsp:getvalueof var="processName" param="returnRequest.processName"/>
                <%-- displayPaymentOptions is passed into this page when an exchange is occurring where the refund amount is the
                same as the order total. In which case, we want to hide all payment groups from the display 
                --%>
                <dsp:getvalueof var="displayPaymentOption" param="displayPaymentOptions"/>
                <c:choose>
                  <c:when test="${processName == 'Exchange'}">
                    <dsp:getvalueof var="balancingStoreCredit"
                                    bean="/atg/commerce/custsvc/order/ShoppingCart.returnRequest.generatedStoreCredit"/>
                    <c:if test="${balancingStoreCredit != null && balancingStoreCredit.id == paymentGroup.id}">
                      <c:set var="displayPaymentOption" value="${false}"/>
                    </c:if>
                    
                  </c:when>
                </c:choose>
              </dsp:oparam>
            </dsp:droplet>
            <%-- When the billing payment options displayed for exchange case,
            we want to hide the balancing store credit. We do not want the agent to modify the
            payment amount for the balancing store credit.
            --%>
            <dsp:getvalueof var="pgType" param="paymentGroup.paymentGroupClassType"/>
            <dsp:getvalueof var="pgTypeConfig" bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
            <c:if test="${displayPaymentOption}">

              <%--If there is no previous patyment type or if it is a new payment type, display the headings. --%>
              <c:choose>
                <c:when test="${empty previousPGType}">
                  <table class="atg_dataTable atg_commerce_csr_innerTable atg_commerce_csr_paymentOptions">
                    <thead>
                    <th style="width:15%;">
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value1"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                      </dsp:include>
                    </th>
                    <th>
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value2"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                      </dsp:include>
                    </th>
                    <th>
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value3"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                      </dsp:include>
                    </th>
                    <th class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_securityCode">
                      <%-- The security code is specific to credit card and display the heading only for the credit card
                      type--%>
                      <c:if test="${pgType =='creditCard'}">
                        <fmt:message key="newOrderBilling.displayPaymentMethods.table.header.cvv"/>
                      </c:if>
                    </th>
                    <th style="width:120px;"
                        class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount">
                      <fmt:message key="newOrderBilling.displayPaymentMethods.table.header.amount"/>
                    </th>
                    <th class="atg_iconCell"></th>
                    </thead>
                </c:when>
                <c:when test="${previousPGType != pgType}">
                  </table>
                  <table class="atg_dataTable atg_commerce_csr_innerTable atg_commerce_csr_paymentOptions">
                    <thead>
                    <th style="width:15%;">
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value1"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                        <dsp:param name="index" param="index"/>
                      </dsp:include>
                    </th>
                    <th>
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value2"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                        <dsp:param name="index" param="index"/>
                      </dsp:include>
                    </th>
                    <th>
                      <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                                   otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                        <dsp:param name="paymentGroup" param="paymentGroup"/>
                        <dsp:param name="propertyName" value="value3"/>
                        <dsp:param name="displayHeading" value="${true }"/>
                        <dsp:param name="order" value="${userOrder}"/>
                        <dsp:param name="index" param="index"/>
                      </dsp:include>
                    </th>
                    <th class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_securityCode">
                      <%-- The security code is specific to credit card and display the heading only for the credit card
                      type--%>
                      <c:if test="${pgType =='creditCard'}">
                        <fmt:message key="newOrderBilling.displayPaymentMethods.table.header.cvv"/>
                      </c:if>
                    </th>
                    <th 
                        class="atg_numberValue atg_commerce_csr_validatedField atg_commerce_csr_billingAmount">
                      <fmt:message key="newOrderBilling.displayPaymentMethods.table.header.amount"/>
                    </th>
                    <th class="atg_iconCell"></th>
                    </thead>
                </c:when>
              </c:choose>
              <c:set var="previousPGType" value="${pgType}"/>

              <tr class="atg_commerce_csr_billingGroup">
                <td>
                  <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                    <dsp:param name="paymentGroup" param="paymentGroup"/>
                    <dsp:param name="propertyName" value="value1"/>
                    <dsp:param name="displayValue" value="${true }"/>
                    <dsp:param name="order" value="${userOrder}"/>
                    <dsp:param name="index" param="index"/>
                  </dsp:include>
                </td>
                <td>
                  <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                    <dsp:param name="paymentGroup" param="paymentGroup"/>
                    <dsp:param name="propertyName" value="value2"/>
                    <dsp:param name="displayValue" value="${true }"/>
                    <dsp:param name="order" value="${userOrder}"/>
                    <dsp:param name="index" param="index"/>
                  </dsp:include>
                </td>
                <td>
                  <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
                  <dsp:include src="${pgTypeConfig.displayPageFragment.URL}"
                               otherContext="${pgTypeConfig.displayPageFragment.servletContext}">
                    <dsp:param name="paymentGroup" param="paymentGroup"/>
                    <dsp:param name="propertyName" value="value3"/>
                    <dsp:param name="displayValue" value="${true }"/>
                    <dsp:param name="order" value="${userOrder}"/>
                    <dsp:param name="index" param="index"/>
                  </dsp:include>
                  </ul>
                </td>

                <dsp:include src="/panels/order/billing/displayPmtCenterFrag.jsp"
                             otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="paymentGroup" param="paymentGroup"/>
                  <dsp:param name="index" param="index"/>
                </dsp:include>
                <td class="atg_iconCell editPaymentOptionIcon">
                  <dsp:include src="/panels/order/billing/displayPmtEndFrag.jsp"
                               otherContext="${CSRConfigurator.contextRoot}">
                    <dsp:param name="paymentGroup" param="paymentGroup"/>
                    <dsp:param name="paymentGroupKey" value="${pgKey}"/>
                  </dsp:include>
                </td>
              </tr>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
        <%-- end of Switch --%>

      </c:if>

    </dsp:oparam>
  </dsp:droplet>
  <%-- end of inner for each --%>
</dsp:oparam>
<dsp:oparam name="outputEnd">
  </table>
</dsp:oparam>

<dsp:oparam name="empty">

  <fmt:message key="newOrderBilling.displayPaymentMethods.noPaymentGroups.info"/>
  <script type="text/javascript">
    _container_.onLoadDeferred.addCallback(function() {
      atg.commerce.csr.order.billing.notifyAddNewCreditCardValidators();
    });
  </script>
</dsp:oparam>
</dsp:droplet>
<%-- end of for each payment groups--%>
</table>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/displayPaymentOptions.jsp#1 $$Change: 946917 $--%>
