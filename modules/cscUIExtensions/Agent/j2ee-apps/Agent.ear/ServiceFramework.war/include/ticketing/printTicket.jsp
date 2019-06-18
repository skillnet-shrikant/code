<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/printTicket.jsp#1 $$Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:importbean bean="/atg/svc/repository/service/StateHolderService"
                  scope="request"
                  var="stateHolder"/>
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <dspel:importbean var="handler" scope="request" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler" />

  <%--Extract report name--%>
  <c:set var="ticket" value="${handler.parameters}" />
  <c:set var="ticketParametersContentDiv" value="reportPanel_${report}_reportParameters"/>
  <c:set var="reportResultsContentDiv" value="reportPanel_${report}_reportResults"/>

  <caf:outputJavaScript>
    var win = window.open("<c:out value='${UIConfig.contextRoot}/include/ticketing/printTicketPage.jsp?${stateHolder.windowIdParameterName}=${windowId}' />",
		  "ticketPreview", "location=no, menubar=no, status=no, titlebar=no, resizable=yes, scrollbars=yes");
  </caf:outputJavaScript>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/printTicket.jsp#1 $$Change: 946917 $--%>