<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dspel:page>
  <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/PanelDebugFormHandler" var="PanelDebugFormHandler"/>  
  <dspel:importbean bean="/atg/svc/agent/ui/servlets/PanelDebuggerDroplet" var="PanelDebuggerDroplet"/>
  
  <dspel:getvalueof var="panelId" param="panelId"/>
  <dspel:getvalueof var="panelDefinition" param="panelDefinition"/>
  
  <dspel:droplet name="/atg/svc/agent/ui/servlets/PanelDebuggerDroplet">
	<dspel:param name="src" value="${panelDefinition.contentUrl}"/>
	<dspel:param name="otherContext" value="${panelDefinition.otherContext}"/>
	<dspel:oparam name="output">
      <dspel:getvalueof param="content" var="content"/>
      <dspel:getvalueof param="jspIncludes" var="jspIncludes"/>
      <dspel:getvalueof param="srcPath" var="srcPath"/>
      <dspel:getvalueof param="localRequest" var="localRequest"/>
    </dspel:oparam>
  </dspel:droplet>
  
  <div dojoType="dijit.layout.TabContainer" doLayout="false">
    <div dojoType="dijit.layout.ContentPane" title="Source" selected="true">
	  <c:if test="${localRequest && not empty srcPath && PanelDebugFormHandler.srcEditAllowed}">
        <span class="openInEditor">
	      <a href="#" onclick="atgSubmitAction({form: document.getElementById('${panelId}_DebugForm')});return false;">
          <img src="/agent/images/icons/icon_propertyEdit.gif"/>Open in System default editor</a>
        </span>
        <dspel:form name="${panelId}_DebugForm" id="${panelId}_DebugForm" formid="${panelId}_DebugForm">
          <dspel:input bean="PanelDebugFormHandler.srcPath" value="${srcPath}" type="hidden" />
          <dspel:input bean="PanelDebugFormHandler.editSrc" type="hidden" priority="-10" value=""/>
        </dspel:form>
      </c:if>
    
      <textarea wrap="off" readonly="yes" >
        <c:out value="${content}" >Can not get access to source file</c:out>
      </textarea>
    </div>
  
    <div dojoType="dijit.layout.ContentPane" style="width:500px" title="Used JSPs">
      <ul>
        <c:forEach items="${jspIncludes}" var="jspInclude">
          <li>${fn:escapeXml(jspInclude)}</li>
        </c:forEach>
      </ul>  
    </div>
  </div>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/debugInfo/panelSource.jsp#1 $$Change: 946917 $--%>
