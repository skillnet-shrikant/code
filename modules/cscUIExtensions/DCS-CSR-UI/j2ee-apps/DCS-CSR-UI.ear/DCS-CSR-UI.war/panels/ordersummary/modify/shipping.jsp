<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/shipping.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin ordersummary/modify/shipping.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:getvalueof var="step" param="step"/>
  <dt>
    <a href="#" id="keyboardShortcutShipping" onclick="atgNavigate({ panelStack : 'cmcShippingAddressPS', queryParams: { init : 'true' }});return false;"><fmt:message key="orderSummary.shippingAddress"/></a> 
  </dt>
  <dd>
    <span id="atg_csc_ordersummary_shippingAddressComplete">
      <c:if test="${empty step.completeWhenInStepsList || cfn:contains(step.completeWhenInStepsList, param.panel)}">
        <fmt:message key="orderSummary.complete"/>
      </c:if>
    </span>
  </dd>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/shipping.jsp#1 $$Change: 946917 $--%>
