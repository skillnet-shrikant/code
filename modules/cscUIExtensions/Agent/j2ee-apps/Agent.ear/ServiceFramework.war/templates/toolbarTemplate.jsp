<%--
 Used To generate Panels. 
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/toolbarTemplate.jsp#1 $$Change: 946917 $
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
%><%@ taglib prefix="fw-beans"  uri="http://www.atg.com/taglibs/svc/svcFrameworkBeansTaglib1_0"
%><%@ taglib prefix="svc-ui"    uri="http://www.atg.com/taglibs/svc/svc-uiTaglib1_0"
%><%@ taglib prefix="svc-agent" uri="http://www.atg.com/taglibs/svc/svc-agentTaglib1_0"
%><%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"
%>
<dspel:page xml="true">
<dspel:getvalueof var="panelStackId" param="panelStackId"/>
<dspel:getvalueof var="panelId" param="panelId"/>
<%-- What is the pre-defined default state of the panel? --%>
<fw-beans:panelStackDefinitionFindByPanelStackId appId="workspace"
                                                 panelStackId="${panelStackId}"
                                                 var="panelStackDefinition"/>
<fw-beans:panelDefinitionFindByPanelId appId="workspace"
                                       panelId="${panelId}"
                                       var="panelDefinition"/>
<%-- Does panel specify a resource bundle? Otherwise use default bundle --%>
<c:set value="atg.svc.agent.WebAppResources" var="strings"/>
<c:if test="${not empty panelDefinition.resourceBundle}">
  <c:set value="${panelDefinition.resourceBundle}" var="strings"/>
</c:if>
<dspel:layeredBundle basename="${strings}">
  <c:set value="${framework.panelInstances[panelDefinition.panelId]}" var="panelInstance"/>
  <c:if test="${panelInstance.panelOpenYn}">
    <c:set value="${panelDefinition.contentUrl}" var="contentUrl"/>
    <c:if test="${panelDefinition.tabHolderYn or not panelInstance.tabbedYn}">
      <div class="panel">
        <div class="panelHeader">
          <h3 class="header3"><fmt:message key='toolbar.history' /></h3>
          <div class="panelIcons">
            <ul class="horizontalList">
              <li><a href="#" class="icon_help" title="<fmt:message key='panels.tips.help' />">?</a></li>
              <li><a href="#" class="icon_arrow" title="<fmt:message key='panels.tips.paneltotab' />">-</a></li>
              <li><a href="#" class="icon_minimize" title="<fmt:message key='panels.tips.minimize' />">-</a></li>
            </ul>
          </div>
        </div>
        <div class="content"><div class="panelContent"></div></div>
      </div>
    </c:if>
  </c:if>
  
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/toolbarTemplate.jsp#1 $$Change: 946917 $--%>
