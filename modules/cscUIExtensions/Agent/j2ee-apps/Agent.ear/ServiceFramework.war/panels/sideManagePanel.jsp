<%--

Management Tool Box

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideManagePanel.jsp#1 $$Change: 946917 $

@updated $DateTime: 2015/01/26 17:26:27 $$Author:

--%>
<%@  include file="/include/top.jspf" %>

<dspel:page xml="true">

<dspel:importbean var="manageToolbox" bean="/atg/svc/agent/ui/ManagementToolboxConfiguration"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <fmt:message var="lineitem" key="tooltip.lineitem" />
    <table class="layoutTable">
      <tr>
        <td width="10%">
          <div class="managementToolboxIcon"></div>
        </td>
        <td>
          <a class="atg_navigationHighlight nextStepsLink"
             href="#"
             onclick="window.open('<c:out value="${manageToolbox.searchManagementConsoleURL}"/>');">
             <fmt:message key="sidePanel.manageToolbox.search" />
          </a>
        </td>
      </tr>      
      <tr>
        <td colspan="2">
        </td>
      </tr>      
      <tr>
        <td width="10%">
          <div class="managementToolboxIcon"></div>
        </td>
        <td>
          <a class="atg_navigationHighlight nextStepsLink"
             href="#"
             onclick="window.open('<c:out value="${manageToolbox.agentAdminURL}"/>');">
             <fmt:message key="sidePanel.manageToolbox.admin" />
          </a>
        </td>
      </tr>      
    </table>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideManagePanel.jsp#1 $ $Change: 946917 $ $DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/sideManagePanel.jsp#1 $$Change: 946917 $--%>
