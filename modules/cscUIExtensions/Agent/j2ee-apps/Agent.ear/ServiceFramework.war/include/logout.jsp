<%@ include file="/include/top.jspf" %>
<dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />

<dspel:page xml="true">
  <div dojoType="dojox.Dialog" class="popupwindow" id="logoutBox"
       titleBarDisplay="false"
       adjustPaths="false"
       extractContent="false"
       duration="1"
       style="display:none; width: 300; height: 120;">
    <div style="padding: 5px;">
      <div>
        <h4><fmt:message key="logout.question" /></h4>
      </div>
      <fmt:message var="yes" key="logout.yes" />
      <fmt:message var="no" key="logout.no" />
      <div class="popupwindowbuttons">
        <input type="button" class="buttonFixed" id="logoutYes" onclick="atg.service.logout();" value="${yes}"/>
        <input type="button" class="buttonFixed" id="logoutNo" value="${no}" onclick="atg.service.reloadResult(true);return false;" />
      </div>
    </div>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/logout.jsp#1 $$Change: 946917 $--%>
