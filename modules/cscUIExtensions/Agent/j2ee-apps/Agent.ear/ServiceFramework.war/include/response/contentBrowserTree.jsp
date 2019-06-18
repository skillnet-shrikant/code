<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTree.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>


<dspel:page xml="true">
  <dspel:importbean var="webUIConfig" bean="/atg/web/Configuration"/>
 	<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- A hidden form whose values will be submitted by the 'contentBrowserContentDetails' action --%>
  <div style="display:none">
    <dspel:form style="display:none" formid="contentBrowserContentDetailsForm" id="contentBrowserContentDetailsForm" action="#">
      <dspel:input priority="-10" id="invokeContentDetails" type="hidden" value="" bean="/atg/arm/ui/content/ContentBrowserFormHandler.invokeContentDetails"/>
      <dspel:input id="successURL" type="hidden" value="include/response/contentBrowserRight.jsp" bean="/atg/arm/ui/content/ContentBrowserFormHandler.successURL"/>
      <dspel:input id="contentBrowserType" type="hidden" value="" bean="/atg/arm/ui/content/ContentBrowserFormHandler.type"/>
      <dspel:input id="contentBrowserId" type="hidden" value="" bean="/atg/arm/ui/content/ContentBrowserFormHandler.id"/>
    </dspel:form>
  </div>


  <div class="sideBySide contentBrowser">

    <h5><div id="atg_arm_contentBrowserTitle"><fmt:message key="panel.communicateContentBrowser.label"/></div></h5>
      <!-- Content Browser Tree View Here -->
      <c:url var="treeURL" context="${webUIConfig.contextRoot}" value="/tree/treeFrame.jsp">
        <c:param name="styleSheet"         value="${UIConfig.contextRoot}${UIConfig.cssLocation}/workspace-sprite.css"/>
        <c:param name="treeComponent"      value="/atg/arm/ui/content/ContentBrowserTreeState"/>
        <c:param name="onSelect"           value="nodeSelectedCallback"/>
        <c:param name="onLoad"             value="nodeSelectOnOPageReturn"/>
        <c:param name="onSelectProperties" value="id,type"/>
        <c:param name="nodeIconRoot"       value="${UIConfig.contextRoot}${UIConfig.imageLocation}/iconcatalog/21x21/wsywig_inserts/"/>
        <c:param name="emptyTreeView" value="emptyTreeView.jsp"/>
        <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
      </c:url>
      <dspel:iframe iclass="contentBrowserTreeFrame" src="${treeURL}" frameborder="0"/>


  </div>
  </dspel:layeredBundle>

</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTree.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTree.jsp#1 $$Change: 946917 $--%>
