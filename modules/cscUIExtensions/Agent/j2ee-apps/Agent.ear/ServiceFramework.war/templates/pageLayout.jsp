<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
  <dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
  <dspel:importbean var="moduleManager" bean="/atg/svc/configuration/ModuleDependencyManager"/>
  <dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />

  <div id="wholeWindow" dojoType="dijit.layout.LayoutContainer" style="width: 100%;height:100%">

    <!-- Main Content -->
    <c:set var="canAccessGlobalCell" value="false"/>
    <dspel:droplet name="HasAccessRight">
      <dspel:param name="accessRight" value="ticketsTab"/>
      <dspel:oparam name="accessGranted">
        <c:set var="canAccessGlobalCell" value="true"/>
      </dspel:oparam>
      <dspel:oparam name="accessDenied">
        <dspel:droplet name="HasAccessRight">
          <dspel:param name="accessRight" value="customersTab"/>
          <dspel:oparam name="accessGranted">
            <c:set var="canAccessGlobalCell" value="true"/>
          </dspel:oparam>
          <dspel:oparam name="accessDenied">
            <c:set var="canAccessGlobalCell" value="false"/>
          </dspel:oparam>
        </dspel:droplet>
      </dspel:oparam>
    </dspel:droplet>
  
    <c:if test="${canAccessGlobalCell}">
      <div dojoType="dojox.layout.ContentPane" parseOnLoad="true"
           id="globalTabContainer"
           style="position:static !important;background-color:#fff;"
           executeScripts="true"
           scriptHasHooks="true"
           adjustPaths="false"
           extractContent="false"
           cacheContent="false"
           layoutAlign="top">
         <dspel:include src="/templates/globalTabsStack.jsp" otherContext="${UIConfig.contextRoot}">
           <dspel:param name="panelStackId" value="${globalPanelStackId}"/>
         </dspel:include>
      </div>
    </c:if>
  
    <%-- Current tab ID --%>
    <c:set var="tabId" value="${framework.windowTab}"/>
    <c:if test="${not empty tabId}">
      <%-- Requested tab definition --%>
      <fw-beans:tabDefinitionFindByTabId appId="workspace"
                                          tabId="${tabId}"
                                          var="selectedTabDefinition"/>
      <%-- Requested tab instance --%>
      <c:set var="tabInstance" value="${framework.tabInstances[tabId]}"/>
      <c:set var="contentPanelStackId" value="${tabInstance.cellAssignments.contentColumn}"/>
      <c:set var="globalPanelStackId" value="${tabInstance.cellAssignments.globalCell}"/>
      <c:set var="researchPanelStackId" value="${tabInstance.cellAssignments.researchColumn}"/>
      <c:set var="sidePanelStackId" value="${tabInstance.cellAssignments.sidebarColumn}"/>
    </c:if>

    <div id="utilDD" style="visibility: hidden;display:none;z-index:50;">
      <ul>
        <c:if test="${not empty tabId}">
          <li class="preferences"><svc-ui:getOptionAsBoolean var="isDisabled" optionName="DisableUserPreferences"/>
            <a href="#" class="home" onclick="atg.service.showUtilities();warnClient('<fmt:message key="capture.abandonSession.message" />', '${isDisabled}', '<fmt:message key="option.input.user.disable.message" />')" > <fmt:message key="utility.navigation.preferences.label"/></a>
          </li>
        </c:if>
        <c:if test="${moduleManager.isKnowledgeRunning}">
          <dspel:droplet name="HasAccessRight">
            <dspel:param name="accessRight" value="Generic Access to the ATG Reporting Center"/>
            <dspel:oparam name="accessGranted">
              <dspel:importbean bean="/atg/cognos/Configuration" var="configuration"/>
              <li class="reporting"><dspel:a href="${configuration.gatewayURI}" target="_blank" onclick="atg.service.showUtilities();"><fmt:message key="utility.navigation.reports.label"/></dspel:a></li>
            </dspel:oparam>
          </dspel:droplet>
        </c:if>
        <dspel:getvalueof id="showDocumentationLink" bean="/atg/svc/ui/util/UtilitiesMenuItems.showDocumentationLink"/>
        <c:if test="${showDocumentationLink}">
          <c:set var="hrefUrl"><fmt:message key="utility.navigation.docs.url"/></c:set>
          <li class="docs"><dspel:a href="${hrefUrl}" target="_blank" onclick="atg.service.showUtilities();"><fmt:message key="utility.navigation.docs.label"/></dspel:a></li>
        </c:if>
        <c:set var="aboutUrl" value="/agent/about.jsp?${stateHolder.windowIdParameterName}=${windowId}"/>
        <li class="about"><a href="#" onclick="atg.service.showUtilities();atg.service.popup('${aboutUrl}', 'AboutWindow', 400, 440); return false;"><fmt:message key="utility.navigation.about.label"/></a></li>
        <li class="keyboardNav"><a href="#" onclick="atg.service.showUtilities();atg.keyboard.showKeyboardShortcutHelpWindow();return false;"><fmt:message key="utility.navigation.keyboard.label"/></a></li>
      </ul>
    </div>

    <div id="globalWrapper" dojoType="dijit.layout.LayoutContainer" layoutAlign="client">
      <div id="contentWrapper" dojoType="dijit.layout.SplitContainer" layoutAlign="client" orientation="vertical" sizerWidth="0" activeSizing="false">
        <dspel:include src="/templates/topPane.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="canAccessGlobalCell" value="${canAccessGlobalCell}"/>
          <dspel:param name="globalPanelStackId" value="${globalPanelStackId}"/>
        </dspel:include>

        <c:set value="${framework.panelStackInstances[globalPanelStackId]}" var="panelStackInstance"/>

        <dspel:include src="/templates/bottomPane.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="tabId" value="${tabId}"/>
          <dspel:param name="tabInstance" value="${tabInstance}"/>
          <dspel:param name="contentPanelStackId" value="${contentPanelStackId}"/>
          <dspel:param name="researchPanelStackId" value="${researchPanelStackId}"/>
          <dspel:param name="sidePanelStackId" value="${sidePanelStackId}"/>
        </dspel:include>
      </div>
    </div>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/pageLayout.jsp#1 $$Change: 946917 $--%>
