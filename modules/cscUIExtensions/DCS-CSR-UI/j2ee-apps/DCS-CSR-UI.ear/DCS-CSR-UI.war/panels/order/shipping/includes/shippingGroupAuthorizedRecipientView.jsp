<%--
This page defines the address view
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupAuthorizedRecipientView.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

  <dsp:page xml="true">
    <dsp:importbean var="pageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/DisplayInStorePickupShippingGroup"/>

    <dsp:getvalueof var="shippingGroupIndex" param="shippingGroupIndex"/>
    <dsp:getvalueof var="order" param="order"/>
    <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
    <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

      <div class="atg_commerce_csr_addressView">
      <h4>
        <dsp:include src="${pageFragment.URL}" otherContext="${pageFragment.servletContext}">
          <dsp:param name="shippingGroup" value="${shippingGroup}"/>
          <dsp:param name="propertyName" value="value1"/>
          <dsp:param name="displayHeading" value="${true}"/>
          <dsp:param name="shippingGroupIndex" value="${shippingGroupIndex}"/>
          <dsp:param name="displaySelectButton" value="${false}"/>
          <dsp:param name="displayAuthorizedForm" value="${true}"/>
        </dsp:include>
      </h4>
      </div>

    </dsp:layeredBundle>
  </dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/shippingGroupAuthorizedRecipientView.jsp#1 $$Change: 946917 $--%>
