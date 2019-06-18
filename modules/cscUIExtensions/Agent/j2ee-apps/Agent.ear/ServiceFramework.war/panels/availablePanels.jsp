<%--

Displays Available Panels panel contents

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/availablePanels.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>


<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

  <%-- Available Panels panel contents --%>

  <table>
  <c:forEach items="${framework.availablePanels}"
             var="availablePanel">

    <fw-beans:panelDefinitionFindByPanelId appId="workspace"
                                           panelId="${availablePanel.panelId}"
                                           var="panelDefinition"/>
    <c:set value="atg.svc.agent.WebAppResources"
           var="strings"/>
    <c:if test="${not empty panelDefinition.resourceBundle}">
      <c:set value="${panelDefinition.resourceBundle}"
             var="strings"/>
    </c:if>

    <dspel:layeredBundle basename="${strings}">
      <tr class="atg_navigationHighlight availablePanelsLink"
          onclick="atg.service.framework.togglePanel('<c:out value="${availablePanel.panelId}"/>');">
        <td>
  	      <div class="availablePanelsIcon"></div>
        </td>
        <td>
          <fmt:message key="${availablePanel.titleKey}">
            <fmt:param value="${availablePanel.panelItemCount}"/>
          </fmt:message>
        </td>
      </tr>
    </dspel:layeredBundle>
  </c:forEach>
  </table>

</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/availablePanels.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/availablePanels.jsp#1 $$Change: 946917 $--%>
