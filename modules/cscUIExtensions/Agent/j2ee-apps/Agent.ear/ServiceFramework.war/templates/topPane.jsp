<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">

  <dspel:getvalueof var="canAccessGlobalCell" param="canAccessGlobalCell"/>
  <dspel:getvalueof var="globalPanelStackId" param="globalPanelStackId"/>
  
  <c:set value="${canAccessGlobalCell}" var="canAccessGlobalCell"/>
  <c:set value="${globalPanelStackId}" var="globalPanelStackId"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <svc-ui:getOptionAsBoolean var="CCOD" optionName="CallCenterMode" />
      <c:if test="${CCOD}">
        <c:if test="${canAccessGlobalCell}">
          <div id="globalCell" style="overflow:hidden;" dojoType="dijit.layout.LayoutContainer">
            <c:set value="globalCell" var="cellId"/>
            <script defer="defer" type="text/javascript">
              <%@  include file="/include/panel/panelUnloadDetector.jspf" %>
            </script>

            <dspel:include src="/templates/globalTicketPanelStack.jsp" otherContext="${UIConfig.contextRoot}">
              <dspel:param name="panelStackId" value="${globalPanelStackId}"/>
            </dspel:include>

            <c:set value="${framework.panelStackInstances[globalPanelStackId]}" var="panelStackInstance"/>
            <script defer="defer" type="text/javascript">
            <%@  include file="/include/panel/panelLoadDetector.jspf" %>
          </script>
          </div>
        </c:if>
      </c:if>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/topPane.jsp#1 $$Change: 946917 $--%>
