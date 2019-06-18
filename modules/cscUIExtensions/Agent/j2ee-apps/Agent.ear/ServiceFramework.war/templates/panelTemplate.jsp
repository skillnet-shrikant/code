<%-- Used To generate Panels.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelTemplate.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%-- The odd formatting is deliberate, it eliminates the junk whitespace JSP emits --%>
<%@ page errorPage="/error.jsp"
%><%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"
%><%@ page pageEncoding="UTF-8"
%><%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fn"         uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="caf"       uri="http://www.atg.com/taglibs/caf"
%><%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
%><%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt"
%><%@ taglib prefix="fw-beans" uri="http://www.atg.com/taglibs/svc/svcFrameworkBeansTaglib1_0"
%><%@ taglib prefix="svc-ui"    uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0"
%><%@ taglib prefix="svc-agent" uri="http://www.atg.com/taglibs/svc/svc-agentTaglib1_0"
%><%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"
%><svc-ui:setFmtLocale/><dspel:page xml="true"><dspel:getvalueof var="panelStackId" param="panelStackId"/><dspel:getvalueof var="panelId" param="panelId"/>
<dspel:getvalueof var="useDebugPanelStackMode" bean="/atg/svc/agent/ui/AgentUIConfiguration.useDebugPanelStackMode" />
<fw-beans:panelStackDefinitionFindByPanelStackId var="panelStackDefinition" appId="workspace" panelStackId="${panelStackId}"/>
<fw-beans:panelDefinitionFindByPanelId var="panelDefinition" appId="workspace" panelId="${panelId}"/>
<%-- Does panel specify a resource bundle? Otherwise use default bundle --%>
<c:set var="strings" value="atg.svc.agent.WebAppResources" />
<c:if test="${! empty panelDefinition.resourceBundle}"><c:set var="strings" value="${panelDefinition.resourceBundle}"/></c:if>
<dspel:layeredBundle basename="${strings}">
<c:set var="panelInstance" value="${framework.panelInstances[panelDefinition.panelId]}"/>
<c:if test="${panelInstance.panelOpenYn}">
 <c:set var="contentUrl" value="${panelDefinition.contentUrl}" />
 <c:set var="otherContext" value="${panelDefinition.otherContext}" />
 <c:set var="toolbarTemplate" value="${panelDefinition.templates.toolbarContent}"/>
 <c:if test="${! empty toolbarTemplate}"><c:set var="toolbarURL" value="${toolbarTemplate.url}"/></c:if>
<%-- <c:if test="${panelDefinition.tabHolderYn || ! panelInstance.tabbedYn}">--%>
  <div class='${(panelDefinition.tabHolderYn && ! empty panelInstance.tabbedPanelIds) ? "panel noBorder" : "panel" }'>
   <div tabindex="0" class="atg_keyboard_panel_identifier">
     <div class="panelHeader">
     <c:choose>
      <c:when test="${panelDefinition.tabHolderYn}">
       <c:choose>
        <c:when test="${ empty panelInstance.tabbedPanelIds}">
         <h3 class="header3"><c:if test="${panelDefinition.showTitleYn}"><fmt:message key="${panelDefinition.titleKey}"><fmt:param value="${panelDefinition.panelItemCount}"/></fmt:message></c:if></h3>
         <c:if test="${useDebugPanelStackMode}"><dspel:include src="/templates/panelDebugInfo.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
          <dspel:param name="panelStackId" value="${panelStackId}"/>
          <dspel:param name="panelDefinition" value="${panelDefinition}"/>
          <dspel:param name="connectId" value="${panelDefinition.panelId}_toggleDebugInfo"/>
         </dspel:include></c:if>
        </c:when>
        <c:otherwise>
        <ul class="panelTabs">
         <c:set var="selectedPanelId" value="${panelInstance.currentPanelId}"/>
         <c:choose>
          <c:when test="${panelInstance.panelId == selectedPanelId}"><c:set var="className" value="current"/></c:when>
          <c:otherwise><c:set var="className" value=""/><c:set value="atg.service.framework.selectTabbedPanel('${panelInstance.panelId}');" var="onClickHandler"/></c:otherwise>
         </c:choose>
         <li class="${className} first" onclick="${onClickHandler}">
          <c:if test="${panelDefinition.showTitleYn}"><fmt:message key="${panelDefinition.titleKey}"/></c:if>
          <c:if test="${useDebugPanelStackMode}"><dspel:include src="/templates/panelDebugInfo.jsp" otherContext="${UIConfig.contextRoot}">
           <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
           <dspel:param name="panelStackId" value="${panelStackId}"/>
           <dspel:param name="panelDefinition" value="${panelDefinition}"/>
           <dspel:param name="connectId" value="${panelDefinition.panelId}_toggleDebugInfo"/>
          </dspel:include></c:if>
         </li>
         <c:forEach items="${panelInstance.tabbedPanelIds}" var="tabbedPanelId">
          <fw-beans:panelDefinitionFindByPanelId var="tabbedPanelDefinition" appId="workspace" panelId="${tabbedPanelId}"/>
          <c:set var="tabStrings" value="atg.svc.agent.WebAppResources"/>
          <c:if test="${! empty tabbedPanelDefinition.resourceBundle}"><c:set var="tabStrings" value="${tabbedPanelDefinition.resourceBundle}"/></c:if>
          <dspel:layeredBundle basename="${tabStrings}">
            <c:choose>
             <c:when test="${tabbedPanelId == selectedPanelId}">
              <c:set var="className" value="current"/>
              <c:set value="${tabbedPanelDefinition.contentUrl}" var="contentUrl"/>
              <c:set value="${tabbedPanelDefinition.otherContext}" var="otherContext"/>
              <c:set var="toolbarTemplate" value="${tabbedPanelDefinition.templates.toolbarContent}"/>
              <c:set var="toolbarURL" value=""/>
              <c:if test="${! empty toolbarTemplate}"><c:set var="toolbarURL" value="${toolbarTemplate.url}"/></c:if>
              <c:set var="onClickHandler" value=""/>
             </c:when>
             <c:otherwise>
              <c:set var="className" value=""/>
              <c:set value="atg.service.framework.selectTabbedPanel('${tabbedPanelId}');" var="onClickHandler"/>
             </c:otherwise>
            </c:choose>
            <li class="${className}" onclick="${onClickHandler}"> 
             <fmt:message key="${tabbedPanelDefinition.titleKey}"/>
             <c:if test="${useDebugPanelStackMode}"><dspel:include src="/templates/panelDebugInfo.jsp" otherContext="${UIConfig.contextRoot}">
              <dspel:param name="panelId" value="${tabbedPanelDefinition.panelId}"/>
              <dspel:param name="panelStackId" value="${panelStackId}"/>
              <dspel:param name="panelDefinition" value="${tabbedPanelDefinition}"/>
              <dspel:param name="connectId" value="${tabbedPanelDefinition.panelId}_toggleDebugInfo"/>
             </dspel:include></c:if>
            </li>
          </dspel:layeredBundle>
         </c:forEach>
        </ul>
        </c:otherwise>
       </c:choose>
      </c:when>
      <c:otherwise>
       <h3 class="header3"><c:if test="${panelDefinition.showTitleYn}"><fmt:message key="${panelDefinition.titleKey}"><fmt:param value="${panelDefinition.panelItemCount}"/></fmt:message></c:if></h3>
       <c:if test="${useDebugPanelStackMode}"><dspel:include src="/templates/panelDebugInfo.jsp" otherContext="${UIConfig.contextRoot}">
        <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
        <dspel:param name="panelStackId" value="${panelStackId}"/>
        <dspel:param name="panelDefinition" value="${panelDefinition}"/>
        <dspel:param name="connectId" value="${panelDefinition.panelId}_toggleDebugInfo"/>
       </dspel:include></c:if>
      </c:otherwise>
     </c:choose>
     <div class="panelIcons">
      <ul class="horizontalList">
       <c:if test="${panelDefinition.allowContentToggleYn}">
         <li id="${panelDefinition.panelId}Minimize"
            <c:if test="${!panelInstance.contentOpenYn}">
              style="display:none;"
            </c:if>>
            <a href="#" id="${panelDefinition.panelId}MinimizeIcon" class="icon_minimize" title="<fmt:message key='panels.tips.minimize'/>" onclick="atg.service.framework.togglePanelContentNoRefresh('${panelDefinition.panelId}');">-</a>
         </li>
         <li id="${panelDefinition.panelId}Maximize"
            <c:if test="${panelInstance.contentOpenYn}">
              style="display:none;"
            </c:if>>
            <a href="#" id="${panelDefinition.panelId}MaximizeIcon" class="icon_maximize" title="<fmt:message key='panels.tips.maximize'/>" onclick="atg.service.framework.togglePanelContentNoRefresh('${panelDefinition.panelId}');">X</a>
         </li>
       </c:if>
       <c:choose>
        <c:when test="${panelDefinition.tabHolderYn}">
         <c:if test="${panelInstance.panelId != selectedPanelId && ! empty panelInstance.tabbedPanelIds && ! panelDefinition.alwaysTabbedYn}">
          <li><a href="#" id="${panelDefinition.panelId}TabToPanel" class="icon_down_arrow" title="<fmt:message key='panels.tips.tabtopanel'/>" onclick="atg.service.framework.togglePanelsToTabs('${selectedPanelId}','${panelStackId}');atg.service.framework.cancelEvent(event);">D</a></li>
         </c:if>
        </c:when>
        <c:otherwise>
         <c:if test="${panelDefinition.allowTabbingYn && ! panelDefinition.alwaysTabbedYn}">
          <li><a href="#" id="${panelDefinition.panelId}PanelToTab" class="icon_arrow" title="<fmt:message key='panels.tips.paneltotab'/>" onclick="atg.service.framework.togglePanelsToTabs('${panelDefinition.panelId}','${panelStackId}');atg.service.framework.cancelEvent(event);">^</a></li>
         </c:if>
        </c:otherwise>
       </c:choose>
       <c:if test="${panelDefinition.allowPanelToggleYn}">
        <li><a class="icon_close" id="${panelDefinition.panelId}Close" onclick="atg.service.framework.togglePanel('${panelDefinition.panelId}');atg.service.framework.cancelEvent(event);" style="cursor:pointer;"></a></li>
       </c:if>
      </ul>
     </div>
    </div>
    <c:if test="${panelInstance.contentOpenYn && ! empty toolbarURL}">
     <dspel:include src="${toolbarURL}" otherContext="${otherContext}">
      <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
      <dspel:param name="panelStackId" value="${panelStackId}"/>
      <dspel:param name="otherContext" value="${otherContext}"/>
     </dspel:include>
    </c:if>
    <div class="content">
      <div class="panelContent"
          id="${panelDefinition.panelId}Content"
          <c:if test="${not panelInstance.contentOpenYn}">
            style="display:none;"
          </c:if>>
        <dspel:include src="${contentUrl}" otherContext="${otherContext}">
          <dspel:param name="panelId" value="${panelDefinition.panelId}"/>
          <dspel:param name="panelStackId" value="${panelStackId}"/>
          <dspel:param name="otherContext" value="${otherContext}"/>
        </dspel:include>
      </div>
    </div>
  </div>
 </div>
<%--</c:if>--%>
</c:if>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelTemplate.jsp#1 $$Change: 946917 $--%>
