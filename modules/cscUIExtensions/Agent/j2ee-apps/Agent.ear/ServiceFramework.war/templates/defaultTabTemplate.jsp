<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="tabId" param="tabId"/>
  <dspel:getvalueof var="contentPanelStackId" param="contentPanelStackId"/>
  <dspel:getvalueof var="researchPanelStackId" param="researchPanelStackId"/>
  <dspel:getvalueof var="sidePanelStackId" param="sidePanelStackId"/>
  <div id="columns" dojoType="dijit.layout.LayoutContainer" layoutAlign="client" layoutChildPriority="left-right" sizeShare="90">
    <dspel:include src="/templates/mainColumn.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="tabId" value="${tabId}"/>
      <dspel:param name="contentPanelStackId" value="${contentPanelStackId}"/>
    </dspel:include>
    <dspel:include src="/templates/utilitiesColumn.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="tabId" value="${tabId}"/>
      <dspel:param name="sidePanelStackId" value="${sidePanelStackId}"/>
    </dspel:include>
    <dspel:include src="/templates/researchColumn.jsp" otherContext="${UIConfig.contextRoot}">
      <dspel:param name="tabId" value="${tabId}"/>
      <dspel:param name="researchPanelStackId" value="${researchPanelStackId}"/>
    </dspel:include>
  </div><%-- columns --%>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/defaultTabTemplate.jsp#1 $$Change: 946917 $--%>
