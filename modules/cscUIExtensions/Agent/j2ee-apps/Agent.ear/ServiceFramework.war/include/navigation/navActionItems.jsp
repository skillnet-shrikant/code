<%--
 Generic Navigation Action Sub-Component Renderer
 This file renders the action sub-component of the nav item
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navActionItems.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:importbean bean="/atg/dynamo/droplet/ForEach" />
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
<dspel:getvalueof var="navActions" param="navActions"/>

<%-- Set up droplet to iterate over nav action items and render each one individually --%>
<dspel:droplet name="ForEach">
  <dspel:param name="array" param="navActions"/>
  <dspel:getvalueof var="navAction" param="element"/>
    
  <%-- Create structure for nav item popup --%>
  <dspel:oparam name="outputStart">
    <ul>
  </dspel:oparam>

  <%-- Extract nav action and render it within list --%>
  <dspel:oparam name="output">
    <c:choose>
      <%-- Check for the disabled flag and render appropriately --%>
      <c:when test="${navAction.enabled == false}">
        <li><a href="#" style="color:#C0C0C0;background-color:#EFEFEF;cursor:default"><c:out value="${navAction.label}"/></a></li>
      </c:when>
      <c:otherwise>
        <li><a href="#" onclick="<c:out value="${navAction.javaScriptFunctionCall}"/>"><c:out value="${navAction.label}"/></a></li>
      </c:otherwise>
    </c:choose>
  </dspel:oparam>

  <%-- Close off nav item popup --%>
  <dspel:oparam name="outputEnd">
    </ul>
  </dspel:oparam>

</dspel:droplet>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/navigation/navActionItems.jsp#1 $$Change: 946917 $--%>