

<%--

This file is for getting current Active Tab for the window.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/getWindowTab.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@  include file="/include/top.jspf" %>

<dspel:page xml="true">

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

      <caf:outputJavaScript>
        window['<c:out value="${framework.tabId}"/>']();
      </caf:outputJavaScript>

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/getWindowTab.jsp#1 $ $Change: 946917 $ $DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/getWindowTab.jsp#1 $$Change: 946917 $--%>
