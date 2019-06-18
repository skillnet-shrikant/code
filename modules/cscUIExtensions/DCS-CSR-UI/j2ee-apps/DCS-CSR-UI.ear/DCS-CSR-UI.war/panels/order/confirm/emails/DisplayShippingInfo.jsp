<%--
This page defines the shipping info

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayShippingInfo.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
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

  <dsp:getvalueof var="order" param="order"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <%-- ######################### Shipping ######################### --%>
    <div class="atg_commerce_csr_subPanel">
      <div class="atg_commerce_csr_subPanelHeader">
        <ul class="atg_commerce_csr_panelToolBar">
          <li class="atg_commerce_csr_header">
            <h4>
              <fmt:message key='finishOrder.shippingSummary.header'/>
            </h4>
          </li>
        </ul>
      </div>

      <dsp:droplet name="IsNull">
        <dsp:param name="value" param="order.shippingGroups"/>
        <dsp:oparam name="true">
          <p>
            <fmt:message key="confirmOrder.emails.shippingInfo.noShippingGroups"/>
          </p>
        </dsp:oparam>
        <dsp:oparam name="false">

          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="order.shippingGroups"/>
            <dsp:param name="elementName" value="shippingGroup"/>
            <dsp:oparam name="empty">
              <fmt:message key="confirmOrder.emails.shippingInfo.wereNoShippingGroups"/>
              <br>
            </dsp:oparam>
            <dsp:oparam name="output">
              <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
              <dsp:include src="/include/order/shippingGroupReadView.jsp" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="shippingGroup" value="${shippingGroup}"/>
              </dsp:include>
              <dsp:include src="/include/order/displayCommerceItem.jsp" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="currentOrder" value="${order}"/>
                <dsp:param name="currentShippingGroup" value="${shippingGroup}"/>
                <dsp:param name="isExistingOrderView" value="${false}"/>
                <dsp:param name="isEmailView" value="${true}"/>
              </dsp:include>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/emails/DisplayShippingInfo.jsp#1 $$Change: 946917 $--%>
