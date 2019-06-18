<%-- Used To generate debug info for panels.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelDebugInfo.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ page errorPage="/error.jsp"
%><%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"
%><%@ page pageEncoding="UTF-8"
%><%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
%><%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt"
%>
<dspel:page xml="true">
  <dspel:getvalueof var="panelStackId" param="panelStackId"/>
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="panelDefinition" param="panelDefinition"/>
  <dspel:getvalueof var="connectId" param="connectId"/> 
  
  <c:set var="strings" value="atg.svc.agent.WebAppResources" />
  <c:if test="${! empty panelDefinition.resourceBundle}"><c:set var="strings" value="${panelDefinition.resourceBundle}"/></c:if>
  
  <dspel:layeredBundle basename="${strings}">
    <button id="${connectId}" dojoType="dijit.form.Button" iconClass="informationImage" showLabel="false">
	  <span>Info</span>
	  <script type="dojo/method" event="onClick">
        dijit.byId("${panelId}_debugDialog").show();
      </script>      
	</button>
	
	<c:set var="panelTitle">
	  <fmt:message key="${panelDefinition.titleKey}"/>
	</c:set>
	<div id="${panelId}_debugDialog" dojoType="dijit.Dialog" title="${panelTitle}"/>
	  <div dojoType="dijit.layout.TabContainer" doLayout="false">
        <div dojoType="dijit.layout.ContentPane" title="Properties" selected="true">
          <dspel:include page="../include/debugInfo/propertyTable.jsp">
            <dspel:param name="panelId" param="panelId"/>
			<dspel:param name="panelStackId" param="panelId"/>
			<dspel:param name="panelDefinition" param="panelDefinition"/>
			<dspel:param name="strings" value="${strings}"/>
          </dspel:include>
	    </div>
	    <div dojoType="dijit.layout.ContentPane" title="Panel source" class="panelSource">
	      <dspel:include page="../include/debugInfo/panelSource.jsp">
			<dspel:param name="panelDefinition" param="panelDefinition"/>
            <dspel:param name="panelId" param="panelId"/>
          </dspel:include>
	    </div>
        <div dojoType="dijit.layout.ContentPane" title="Panel Definition" class="panelDefinition">
	      <dspel:include page="../include/debugInfo/panelDefinition.jsp">
            <dspel:param name="panelId" param="panelId"/>
          </dspel:include>
        </div>
	  </div>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelDebugInfo.jsp#1 $$Change: 946917 $--%>
