<%--
 This page defines the Customer Information Panel - read only
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile_view.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/userprofiling/ServiceCustomerProfile" var="profile"/>

  <dspel:importbean var="defaultPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerViewDefault" /> 
  <dspel:importbean var="extendedPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerViewExtended" /> 

  <c:if test="${not empty defaultPageFragment.URL}">			  
    <dspel:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
  </c:if>	
  
  <c:if test="${not empty extendedPageFragment.URL}">			  
    <dspel:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
  </c:if>	

</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/profile_view.jsp#1 $$Change: 946917 $--%>
