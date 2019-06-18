<%--

This file is used for displaying Next Panel.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/nextStepsPanel.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <dspel:include src="/include/viewNextStepsForms.jsp"  otherContext="${UIConfig.contextRoot}"/>
  <div parseWidgets="false" class="atg_next_steps_ns_panel">
  <%-- Are next steps defined for the current request? --%>
  <c:if test="${framework.finalNextStepsMenuId != null}">  
    <dspel:include src="/include/nextSteps/${framework.finalNextStepsMenuId}.jsp" otherContext="${UIConfig.contextRoot}"/>
  </c:if>
  </div>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/nextStepsPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/nextStepsPanel.jsp#1 $$Change: 946917 $--%>
