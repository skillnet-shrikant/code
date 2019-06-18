<%--

This file is for sendig a message.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/communicateSendMessagePanel.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<%-- Send Message panel contents --%>
  
	<dspel:include src="/include/response/outboundMessage.jsp" otherContext="${UIConfig.contextRoot}"/>

</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/communicateSendMessagePanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/communicateSendMessagePanel.jsp#1 $$Change: 946917 $--%>
