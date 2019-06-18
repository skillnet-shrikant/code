<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<c:choose>
<c:when test="${orderIsModifiable && empty returnRequest}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/ModifyOrder"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
<c:when test="${! empty returnRequest && returnRequest.exchangeProcess == true}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Exchange"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
<c:when test="${orderIsReturnable && empty returnRequest}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Return"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
<c:when test="${orderIsReturnable && !empty returnRequest}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Return"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
<c:when test="${!orderIsModifiable && !orderIsReturnable}">
<dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Return"/>
<dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
<%--  
<c:when test="${orderIsSubmitted}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Submitted"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
--%>
<c:when test="${orderIsTemplate}">
  <dsp:importbean var="next" bean="/atg/commerce/custsvc/ordersummary/Template"/>
  <dsp:include otherContext="${next.context}" page="${next.page}"/>
</c:when>
</c:choose>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/ordersummary/defaultFirstStep.jsp#1 $$Change: 946917 $--%>
