<%--
This page defines the shipping method picker
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingMethodPicker.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="agentTools"/>
    <dsp:importbean bean="/atg/commerce/custsvc/pricing/AvailablePricedShippingMethodsDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart"/>

    <dsp:getvalueof var="shippingGroupIndex" param="shippingGroupIndex"/>
    <dsp:getvalueof var="order" param="order"/>
    <dsp:getvalueof var="profile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
    <dsp:getvalueof var="shippingPricingModels"
                    bean="/atg/commerce/custsvc/environment/CSREnvironmentTools.currentOrderPricingModelHolder.shippingPricingModels"/>
    <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
    <dsp:getvalueof var="currentShippingMethod"
                    bean="ShoppingCart.current.ShippingGroups[param:shippingGroupIndex].shippingMethod"/>

    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <div class="atg_commerce_csr_shippingMethodPicker">
        <h4>
          <fmt:message key="shipping.chooseMethod.header"/>
        </h4>
        <ul class="shipMethod">

    <csr:getCurrencyCode order="${order}">
      <c:set var="currencyCode" value="${currencyCode}" scope="request" />
    </csr:getCurrencyCode>

          <dsp:droplet name="AvailablePricedShippingMethodsDroplet">
            <dsp:param name="order" value="${order}"/>
            <dsp:param name="profile" value="${profile}"/>
            <dsp:param name="pricingModels" value="${shippingPricingModels}"/>
            <dsp:param name="shippingGroup" value="${shippingGroup}"/>
            <dsp:oparam name="output">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="availablePricedShippingMethods"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="method" param="key"/>
                  <dsp:getvalueof var="priceInfo" param="element"/>
                  <dsp:getvalueof var="methodIndex" param="index"/>
                  <c:choose>
                    <c:when test="${!empty currentShippingMethod && currentShippingMethod != 'hardgoodShippingGroup'}">
                      <c:choose>
                        <c:when test="${method == currentShippingMethod}">
                          <c:set var="checked" value="${true}"/>
                        </c:when>
                        <c:when test="${methodIndex == 0}">
                          <c:set var="checked" value="${true}"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="checked" value=""/>
                        </c:otherwise>
                      </c:choose>
                    </c:when>
                    <c:otherwise>
                      <c:choose>
                        <c:when test="${methodIndex == 0}">
                          <c:set var="checked" value="${true}"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="checked" value=""/>
                        </c:otherwise>
                      </c:choose>
                    </c:otherwise>
                  </c:choose>
                  <li>
                    <dsp:input type="radio" checked="${checked}"
                               bean="ShoppingCart.current.ShippingGroups[param:shippingGroupIndex].shippingMethod"
                               value="${method}"/>
                    &nbsp;${method}&nbsp;
                    <csr:formatNumber value="${priceInfo.amount}" type="currency" currencyCode="${currencyCode}"/>
                  </li>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:getvalueof var="msg" param="errorMessage"/>
              ${fn:escapeXml(msg)}
            </dsp:oparam>
          </dsp:droplet>

        </ul>
      </div>
    </dsp:layeredBundle>
  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingMethodPicker.jsp#2 $$Change: 1179550 $--%>
