<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt" %>
<dsp:page>

  <%/* A shopping cart-like display of order information */%>

  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean var="CSRConfigurator" bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:getvalueof var="order" param="order"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <%-- ######################### Billing ########################## --%>
    <div class="atg_commerce_csr_subPanel">
      <div class="atg_commerce_csr_subPanelHeader">
        <ul class="atg_commerce_csr_panelToolBar">
          <li class="atg_commerce_csr_header">
            <h4>
              <fmt:message key='finishOrder.billingSummary.header'/>
            </h4>
          </li>
        </ul>
      </div>

      <dsp:droplet name="IsNull">
        <dsp:param name="value" param="order.paymentGroups"/>
        <dsp:oparam name="true">
          <p>
            <fmt:message key="confirmOrder.emails.paymentInfo.noGroups"/>
        </dsp:oparam>
        <dsp:oparam name="false">
          <table>
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="order.paymentGroups"/>
              <dsp:param name="elementName" value="paymentGroup"/>
              <dsp:oparam name="empty">
                <fmt:message key="confirmOrder.emails.paymentInfo.wereNoPaymentGroups"/>
                <br>
              </dsp:oparam>
              <dsp:oparam name="output">
              <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
              <c:if test="${paymentGroup.amount > 0}">
                <tr>
                  <td>
                    <div class="atg_commerce_csr_statusView">
                      <h4>
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                          <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                          <dsp:param name="propertyName" value="value1"/>
                          <dsp:param name="displayHeading" value="${true}"/>
                          <dsp:param name="order" value="${order}"/>
                        </dsp:include>
                      </h4>
                      <ul>
                        <li>
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                            <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                            <dsp:param name="propertyName" value="value1"/>
                            <dsp:param name="displayValue" value="${true}"/>
                            <dsp:param name="order" value="${order}"/>
                          </dsp:include>
                        </li>
                      </ul>
                    </div>

                    <div class="atg_commerce_csr_statusView">
                      <h4>
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                          <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                          <dsp:param name="propertyName" value="value2"/>
                          <dsp:param name="displayHeading" value="${true}"/>
                          <dsp:param name="order" value="${order}"/>
                        </dsp:include>
                      </h4>
                      <ul>
                        <li>
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                            <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                            <dsp:param name="propertyName" value="value2"/>
                            <dsp:param name="displayValue" value="${true}"/>
                            <dsp:param name="order" value="${order}"/>
                          </dsp:include>
                        </li>
                      </ul>
                    </div>

                    <div class="atg_commerce_csr_addressView">
                      <h4>
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                          <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                          <dsp:param name="propertyName" value="value3"/>
                          <dsp:param name="displayHeading" value="${true}"/>
                          <dsp:param name="order" value="${order}"/>
                        </dsp:include>
                      </h4>
                       <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
                        <dsp:include src="/panels/order/finish/finishOrderBillingLineItem.jsp" 
                                     flush="false"
                                     otherContext="${CSRConfigurator.contextRoot}">
                            <dsp:param name="paymentGroup" value="${paymentGroup}"/>
                            <dsp:param name="propertyName" value="value3"/>
                            <dsp:param name="displayValue" value="${true}"/>
                            <dsp:param name="order" value="${order}"/>
                          </dsp:include>
                      </ul>
                    </div>
                    <div class="atg_commerce_csr_statusView atg_commerce_csr_statusTabularView">
                      <h4>
                        <fmt:message key='billingSummary.commerceItem.header.amount'/>
                      </h4>
                      <ul>
                        <li>
                          <csr:formatNumber value="${paymentGroup.amount}" type="currency"
                                            currencyCode="${order.priceInfo.currencyCode}"/>
                        </li>
                      </ul>
                    </div>
                  <td>
                </tr>
              </c:if>
              </dsp:oparam>
            </dsp:droplet>
          </table>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayPaymentInfo.jsp#2 $$Change: 1179550 $--%>
