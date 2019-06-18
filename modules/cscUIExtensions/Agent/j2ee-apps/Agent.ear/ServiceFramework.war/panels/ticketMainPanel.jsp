<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketMainPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>

<dspel:page xml="true">
  <dspel:include src="/panels/mainTicketActivityPanel.jsp" otherContext="${UIConfig.contextRoot}">
    <dspel:param name="ticketHolderName" value="/atg/svc/ticketing/TicketHolder"/>
  </dspel:include>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketMainPanel.jsp#1 $$Change: 946917 $--%>
