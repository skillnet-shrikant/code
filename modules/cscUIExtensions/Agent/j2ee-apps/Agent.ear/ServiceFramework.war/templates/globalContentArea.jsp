<%--
 Global Content Area Navigation Container
 This file is the main container for the GCA navigation functionality
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalContentArea.jsp#2 $$Change: 953229 $
 @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:importbean var="primaryNavContainer" bean="/atg/svc/agent/ui/PrimaryNavContainer" />
<dspel:importbean var="secondaryNavContainer" bean="/atg/svc/agent/ui/SecondaryNavContainer" />
<dspel:importbean var="optionsNavContainer" bean="/atg/svc/agent/ui/OptionsNavContainer" />
<dspel:importbean var="agentUIConfiguration" bean="/atg/svc/agent/ui/AgentUIConfiguration" />
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <div id="logo">
    <img src="${agentUIConfiguration.titleBannerURL}" />
    <span class="logoProduct">
      <span class="logoProductName"><fmt:message key="logo.product.name"/></span> <span class="logoProductTitle"><fmt:message key="logo.product.title"/></span>
    </span>
  </div>
  
  <%-- JSP to render call buttons and page title --%>
    
  <div id="gcn">

    <%-- JSP to render secondary nav container --%>
    <div class="gcnSecondaryNavDiv">
      <dspel:include src="/templates/callButtons.jsp" otherContext="${UIConfig.contextRoot}" />
      
      <div id="optionsContainer">
        <%-- JSP to render messages area --%>
        <dspel:include src="/templates/messages.jsp" otherContext="${UIConfig.contextRoot}" />
        
        <%-- JSP to render options nav container --%>
        <dspel:include src="/include/navigation/navContainer.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="navContainer" value="${optionsNavContainer}" />
        </dspel:include>
        
        <div dojoType="atg.widget.messaging.MessageFader" id="messageFaderWidget" style="top:69px;width:350px;margin-right:80px;">
        </div>
        <div dojoType="atg.widget.messaging.MessagePane"
             resizeDirection="left"
             titleBarDisplay="false"
             initialMessage="<fmt:message key="userMessaging.defaultInitialMessage"/>"
             style="top:69px;width:350px;height:200px;margin-right:80px;display:none;"
             id="messageDetailWidget">
        </div>
      </div>
      
      <div id="secondaryNavContainer">
        <dspel:include src="/include/navigation/navContainer.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="navContainer" value="${secondaryNavContainer}" />
        </dspel:include>
      </div>
    </div>  
    
    <%-- JSP to render primary nav container --%>
    <div id="primaryNavContainer">
      <div class="gcnPrimaryNavDiv">
        <dspel:include src="/include/navigation/navContainer.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="navContainer" value="${primaryNavContainer}" />
        </dspel:include>
      </div>
    </div>

  </div>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/globalContentArea.jsp#2 $$Change: 953229 $--%>
