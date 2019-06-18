<%--
This file is for prompting before assigning to another agent.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/environment/confirm.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/environment/confirm.jsp#1 $$Change: 946917 $--%>
<dspel:page xml="true">

  <dspel:importbean var="changeState" bean="/atg/svc/agent/environment/EnvironmentChangeState"/>
  <dspel:importbean var="changeFormHandler" bean="${changeState.changeFormHandlerPath}"/>
  <dspel:importbean bean="/atg/dynamo/droplet/ForEach" />
  
  <c:set var="confirmPromptURL" value="${changeFormHandler.confirmPromptURL}"/>
  <caf:outputJavaScript>
    var confirmPromptURL = "${confirmPromptURL}";
    var repostParams = {};
    <c:forEach var="mapEntry" items="${pageContext.request.parameterMap}">
      <c:if test="${mapEntry.key ne '_windowid'}">
        repostParams["${mapEntry.key}"] = [<c:forEach var="value" items="${mapEntry.value}" varStatus="s"><c:if test="${s.index gt 0}">,</c:if>"${value}"</c:forEach>];
      </c:if>
    </c:forEach>
    atg.service.environment.showChangePrompt(confirmPromptURL, repostParams);
  </caf:outputJavaScript>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/environment/confirm.jsp#1 $$Change: 946917 $--%>
