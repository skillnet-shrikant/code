<%--
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/shippingMethod.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin ordersummary/modify/shippingMethod.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
<dsp:importbean var="shippingGroupFormHandler" bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler" />

<dsp:getvalueof var="step" param="step"/>

    <%-- If there is no hard good shipping groups, we do not need to display the shipping method link
    --%>
      <dt>
      <a href="#" onclick="atg.commerce.csr.openPanelStack('cmcShippingMethodPS');return false;"><fmt:message key="orderSummary.shippingMethod"/></a>
    </dt>
    <dd>
      <span id="atg_csc_ordersummary_shippingMethodComplete">
        <c:if test="${empty step.completeWhenInStepsList || cfn:contains(step.completeWhenInStepsList, param.panel)}">
          <fmt:message key="orderSummary.complete"/>
        </c:if>
      </span>
    </dd>
</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/modify/shippingMethod.jsp#1 $$Change: 946917 $--%>
