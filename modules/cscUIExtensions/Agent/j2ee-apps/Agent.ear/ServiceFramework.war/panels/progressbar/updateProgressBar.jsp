<%--
 This page defines the progress bar panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/progressbar/updateProgressBar.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@  include file="/include/top.jspf"%>
<dspel:page xml="true">
  <caf:outputXhtml targetId="progressBarPanel">
  <dspel:include otherContext="/agent" src="/panels/progressbar/progressBar.jsp"/>
  </caf:outputXhtml>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/progressbar/updateProgressBar.jsp#1 $$Change: 946917 $--%>
