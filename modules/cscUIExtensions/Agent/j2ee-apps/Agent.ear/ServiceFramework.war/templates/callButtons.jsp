<%--
 Call Buttons and Service Center Title
 This file renders the call buttons and title for Service Center
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/callButtons.jsp#2 $$Change: 1179550 $
 @updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:importbean var="callState" bean="/atg/svc/agent/environment/CallState"/>
  
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <div id="callBar">
    <ul id="callActions">

      <c:if test="${!callState.callActive}">
        <li class="ca_play first" title="<fmt:message key='globalTicketContext.startCall'/>"><a id="globalContextStartCall" href="#" onclick="atg.service.framework.startCall();return false;">Play</a></li>
      </c:if>

      <c:if test="${callState.callActive}">
        <li class="ca_stop first" title="<fmt:message key='globalTicketContext.endCall'/>"><a id="globalContextEndCall"  href="#" onclick="atg.service.framework.endCall();return false;">Stop</a></li>
        <li class="ca_record" title="<fmt:message key='globalTicketContext.endStartCall'/>"><a id="globalContextEndAndStartCall"  href="#" onclick="atg.service.framework.endAndStartCall();return false;">Record</a></li>
      </c:if>

      <li class="ca_list" title="<fmt:message key='globalTicketContext.addNote'/>"><a href="#" onclick="atg.service.ticketing.addNotePrompt();atg.service.framework.cancelEvent(event); return false;">List</a></li>
      <li class="ca_phone last" title="<fmt:message key='globalTicketContext.addCallNote'/>"><a href="#" onclick="atg.service.ticketing.addCallActivityPrompt();atg.service.framework.cancelEvent(event);return false;">List</a></li>    

    </ul> 

    <div class="gcn_btn_label">&nbsp;</div>
  </div>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/callButtons.jsp#2 $$Change: 1179550 $--%>
