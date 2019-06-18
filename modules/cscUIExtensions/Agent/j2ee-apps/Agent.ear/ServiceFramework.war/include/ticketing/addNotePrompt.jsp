<%--

This file is for prompting before adding a note.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/addNotePrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@  include file="../top.jspf" %>
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />

<dspel:page xml="true">
  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>  
  <dspel:form action="#" id="addTicketNoteForm" formid="addTicketNoteForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.addNote"/>
    <dspel:input type="hidden" name="noteText" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.noteText"/>
    <dspel:input type="hidden" name="noteType" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.noteType"/>
    <dspel:input type="hidden" name="inbound" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.inbound"/>
    <dspel:input type="hidden" name="share" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.share"/>
    <dspel:input type="hidden" name="viewTicketNote" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicketNote"/>
    <div class="popupwindow">
      <textarea name="addNoteNote" id="addNoteNote" cols="50" rows="5" style="width:97%;height:70px;"></textarea>
      <input type="checkbox" id="addNoteShare" value="share" /><label for="addNoteShare"><fmt:message key="share-with-customer-label"/></label>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="addNoteOk"
           onclick="window.noteType='note';window.noteText=document.getElementById('addTicketNoteForm').addNoteNote.value;window.isPublic=document.getElementById('addTicketNoteForm').addNoteShare.checked;globalAddTicketNote('addTicketNoteForm'); atg.service.ticketing.cancelAddNotePrompt(); return false;">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall" 
           id="addNoteCancel" 
           onclick="atg.service.ticketing.cancelAddNotePrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/addNotePrompt.jsp#1 $$Change: 946917 $--%>
