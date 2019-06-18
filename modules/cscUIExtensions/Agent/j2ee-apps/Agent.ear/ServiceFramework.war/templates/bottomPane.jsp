<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="tabId" param="tabId"/>
  <dspel:getvalueof var="tabInstance" param="tabInstance"/>
  <dspel:getvalueof var="contentPanelStackId" param="contentPanelStackId"/>
  <dspel:getvalueof var="researchPanelStackId" param="researchPanelStackId"/>
  <dspel:getvalueof var="sidePanelStackId" param="sidePanelStackId"/>
  <div id="bottomPane" dojoType="dijit.layout.LayoutContainer">
    <dspel:include src="/templates/workspaceTabs.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="tabId" value="${tabId}"/>
      <dspel:param name="tabInstance" value="${tabInstance}"/>
    </dspel:include>
    
    <div id="tabContent" dojoType="dojox.layout.ContentPane" parseOnLoad="true" layoutAlign="client">
      <c:choose>
        <c:when test="${not empty tabDefinition.templates.contentTemplate}">
          <dspel:include src="${tabDefinition.templates.contentTemplate.url}" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="tabId" value="${tabId}"/>
            <dspel:param name="contentPanelStackId" value="${contentPanelStackId}"/>
            <dspel:param name="researchPanelStackId" value="${researchPanelStackId}"/>
            <dspel:param name="sidePanelStackId" value="${sidePanelStackId}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <dspel:include src="/templates/defaultTabTemplate.jsp" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="tabId" value="${tabId}"/>
            <dspel:param name="contentPanelStackId" value="${contentPanelStackId}"/>
            <dspel:param name="researchPanelStackId" value="${researchPanelStackId}"/>
            <dspel:param name="sidePanelStackId" value="${sidePanelStackId}"/>
          </dspel:include>
        </c:otherwise>
      </c:choose>
    </div>
  </div><%-- bottom pane --%>
  <script type="dojo/method">
    dojo.addOnLoad(function () {
      atgSubmitAction({
        form: dojo.byId("transformForm"),
        panelStack: ["${contentPanelStackId}", "${sidePanelStackId}"]
      });
    });
  </script>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/bottomPane.jsp#1 $$Change: 946917 $--%>
