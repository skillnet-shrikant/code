<%--
 This page defines the content header template
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/contentHeaderTemplate.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
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
%>
<dspel:page xml="true">
  <dspel:getvalueof var="contentPanelStackId" param="contentPanelStackId"/>

  <fw-beans:panelStackDefinitionFindByPanelStackId appId="workspace" 
                                                   panelStackId="${contentPanelStackId}"
                                                   var="panelStackDefinition"/>
  <c:set var="resourceBundle" value="atg.svc.agent.WebAppResources"/>
  <c:if test="${!empty panelStackDefinition.resourceBundle}">
    <c:set var="resourceBundle" value="${panelStackDefinition.resourceBundle}"/>
  </c:if>
  <dspel:layeredBundle basename="${resourceBundle}">

    <h2 class="header2 ${contentPanelStackId}">
      <fmt:message key="${panelStackDefinition.titleKey}"/>
    </h2>          
    <script type="text/javascript">
      _container_.onLoadDeferred.addCallback(function () {
        dijit.byId("column1").layout();
      });
    </script>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/contentHeaderTemplate.jsp#1 $$Change: 946917 $--%>
