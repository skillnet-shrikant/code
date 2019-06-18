<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketActivityListResults.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>

<dspel:page xml="true">

  <%--
    Set variables used on the included page to connect so that 
    that code uses the correct values for the ticketActivityList
    action 
  --%>

  <c:set var="actvPre" value="alt" scope="request"/>
  <c:set var="actvCtrlBarId" value="ticketActivityListControlBar" scope="request"/>
  <c:set var="actvTableId" value="ticketActivityTable" scope="request"/>
  <c:set var="actvStateKey" value="ticketActivityListTableState" scope="request"/>
  <c:set var="actvFormHandler" scope="request"
    value="/atg/svc/ui/formhandlers/TicketActivityListFormHandler"/>

  <dspel:include src="/include/baseTicketActivityListResults.jsp" otherContext="${UIConfig.contextRoot}"/>

</dspel:page>

<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketActivityListResults.jsp#1 $$Change: 946917 $--%>
