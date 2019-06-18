<%--
 Generic Navigation Search Sub-Component Renderer
 This file renders the search sub-component of the nav item
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navSearch.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:getvalueof var="navSearch" param="navSearch"/>
<dspel:getvalueof var="navItemId" param="navItemId"/>

<%-- Embed the JavaScript function call and tooltip into the search component --%>
<a href="#" id="navSearch_<c:out value="${navItemId}"/>" onclick="<c:out value="${navSearch.javaScriptFunctionCall}"/>" class="gcn_btn_search" title="<c:out value="${navSearch.toolTip}"/>"><span><c:out value="${navSearch.toolTip}"/></span></a>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navSearch.jsp#1 $$Change: 946917 $--%>