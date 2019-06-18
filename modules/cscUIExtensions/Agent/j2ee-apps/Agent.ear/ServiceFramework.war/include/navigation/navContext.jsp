<%--
 Generic Navigation Context Sub-Component Renderer
 This file renders the context sub-component of the nav item
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navContext.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:getvalueof var="navContext" param="navContext"/>
<dspel:getvalueof var="navItemId" param="navItemId"/>

<%-- Embed the context label and tooltip into the context component --%>
<a href="#" id="navContext_<c:out value="${navItemId}"/>" onclick="<c:out value="${navContext.javaScriptFunctionCall}"/>" class="gcn_btn_context" title="<c:out value="${navContext.toolTip}"/>"><c:out value="${navContext.label}"/></a>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navContext.jsp#1 $$Change: 946917 $--%>