<%--
This page defines the address view
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupMethodView.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

  <dsp:page xml="true">
    <dsp:importbean var="pageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/DisplayHardgoodShippingGroup"/>

    <dsp:getvalueof var="shippingGroupIndex" param="shippingGroupIndex"/>
    <dsp:getvalueof var="order" param="order"/>
    <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <dsp:include src="/panels/order/shipping/includes/shippingMethodPicker.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="shippingGroupIndex" value="${shippingGroupIndex}"/>
        <dsp:param name="order" value="${order}"/>
        <dsp:param name="shippingGroup" value="${shippingGroup}"/>
      </dsp:include>

      <div class="atg_commerce_csr_addressView">
      <h4>
        <dsp:include src="${pageFragment.URL}" otherContext="${pageFragment.servletContext}">
          <dsp:param name="shippingGroup" value="${shippingGroup}"/>
          <dsp:param name="propertyName" value="value1"/>
          <dsp:param name="displayHeading" value="${true}"/>
        </dsp:include>
      </h4>
      <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
        <dsp:include src="${pageFragment.URL}" otherContext="${pageFragment.servletContext}">
          <dsp:param name="shippingGroup" value="${shippingGroup}"/>
          <dsp:param name="propertyName" value="value1"/>
          <dsp:param name="displayValue" value="${true}"/>
        </dsp:include>
      </ul>
      </div>

    </dsp:layeredBundle>
  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupMethodView.jsp#1 $$Change: 946917 $--%>
