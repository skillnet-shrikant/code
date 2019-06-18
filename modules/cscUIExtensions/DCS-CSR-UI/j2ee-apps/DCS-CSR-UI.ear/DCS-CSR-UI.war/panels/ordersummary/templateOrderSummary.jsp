<%--
 This page defines the order summary panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/templateOrderSummary.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<!-- begin templateOrderSummary.jsp -->
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:importbean var="currentStep" bean="/atg/commerce/custsvc/ordersummary/Template"/>

<dl class="atg_commerce_csr_orderSummaryGuide">

<c:forEach var="step" items="${currentStep.steps}">
  <c:if test="${ empty step.visibleWhenInStepsList || cfn:contains(step.visibleWhenInStepsList, param.panel)}">
    <dsp:include otherContext="${step.context}" page="${step.page}">
      <dsp:param name="step" value="${step}"/>
    </dsp:include>
  </c:if>
</c:forEach>

</dl>

</dsp:layeredBundle>
</dsp:page>
<!-- end templateOrderSummary.jsp -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/templateOrderSummary.jsp#1 $$Change: 946917 $--%>
