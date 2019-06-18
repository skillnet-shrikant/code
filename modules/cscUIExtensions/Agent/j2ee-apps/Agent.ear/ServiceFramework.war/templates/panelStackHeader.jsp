<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelStackHeader.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
 <dspel:getvalueof var="panelStackId" param="panelStackId"/>
 <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <fw-beans:panelStackDefinitionFindByPanelStackId appId="workspace"
                                                   panelStackId="${panelStackId}"
                                                   var="panelStackDefinition"/>
  <img align="absmiddle" width="30" height="25" src="${imageLocation}${panelStackDefinition.imageUrl}"/>
  <span class="headerLabel"><fmt:message key="${panelStackDefinition.titleKey}"/></span>
 </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/panelStackHeader.jsp#1 $$Change: 946917 $--%>
