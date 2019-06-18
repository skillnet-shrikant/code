<%--

This file is for prompting before adding a note.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/addCallActivityPrompt.jsp#3 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $

--%>
<%@  include file="../top.jspf" %>
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />

<dspel:page xml="true">
  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>  
    <dspel:form action="#" id="addCallActivityForm" formid="addCallActivityForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.addNote"/>
    <dspel:input type="hidden" name="noteText" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.noteText" xssFiltering="false"/>
    <dspel:input type="hidden" name="share" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.share"/>
    <dspel:input type="hidden" name="noteType" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.noteType"/>
    <dspel:input type="hidden" name="inbound" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.inbound"/>
    <div class="popupwindow">
      <input type="checkbox" id="noteDirection" name="noteDirection" value="inbound" checked="checked"/><label for="noteDirection"><fmt:message key="customer-called-us"/></label>
      <h4><fmt:message key="enter-note"/></h4>
      <textarea name="addCallActivityNote" id="addCallActivityNote" cols="50" rows="5" style="width:97%;height:70px;"></textarea>
      <input type="checkbox" id="addCallActivityShare" name="addCallActivityShare" value="share" /><label for="addCallActivityShare"><fmt:message key="share-with-customer-label"/></label>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="addCallActivityOk"
           onclick="window.inbound=document.getElementById('addCallActivityForm').noteDirection.checked;window.noteType='call';window.noteText=document.getElementById('addCallActivityForm').addCallActivityNote.value;window.isPublic=document.getElementById('addCallActivityForm').addCallActivityShare.checked;globalAddTicketNote('addCallActivityForm'); atg.service.ticketing.cancelAddCallActivityPrompt(); return false;">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="addCallActivityCancel" 
           onclick="atg.service.ticketing.cancelAddCallActivityPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/addCallActivityPrompt.jsp#3 $$Change: 1179550 $--%>
