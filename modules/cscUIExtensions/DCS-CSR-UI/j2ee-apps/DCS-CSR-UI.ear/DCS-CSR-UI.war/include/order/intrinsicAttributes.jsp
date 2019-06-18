<%--
Display the intrinsic attributes of an order.

Expected params
currentOrder : The order.

@version $Id: 
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<c:catch var="exception">

<dsp:page xml="true">
  
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />

  <dsp:importbean var="defaultPageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/OrderViewDefault" /> 
  <dsp:importbean var="extendedPageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/OrderViewExtended" /> 
  
  <c:if test="${not empty defaultPageFragment.URL}">  
    <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
  </c:if>
  
  <c:if test="${not empty extendedPageFragment.URL}">  
    <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
  </c:if>

</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/intrinsicAttributes.jsp#1 $$Change: 946917 $--%>
