<%--
This file is for prompting before ticket deferral.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/deferTicketPrompt.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="../top.jspf" %>
<dspel:page xml="true">
<dspel:setLayeredBundle basename="atg.svc.agent.ticketing.TicketingResources" />
  <dspel:importbean var="reasonManager" bean="/atg/ticketing/ActivityReasonManager"/>
  <dspel:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dspel:form action="#" id="deferTicketForm" formid="deferTicketForm" 
  		onsubmit="atg.service.ticketing.deferTicketFromPopup();return false;">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.changeEnvironment"/>
    <dspel:input type="hidden" value="defer" name="tdoption" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.ticketDispositionOptions.dispositionOption"/>
    <dspel:input type="hidden" value="" name="reasonCode" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.ticketDispositionOptions.reasonCode"/>
    <dspel:input type="hidden" name="date" value="" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.inputParameters.date"/>
    <dspel:input type="hidden" name="retain" value="" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.inputParameters.retain"/>
    <dspel:input type="hidden" value="" name="noteText" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.ticketDispositionOptions.ticketNote"/>
    <dspel:input type="hidden" value="" name="share" bean="/atg/svc/agent/ui/formhandlers/DeferTicket.ticketDispositionOptions.publicNote"/>
    
    <dspel:getvalueof var="datePattern" scope="request" bean="LocaleTools.userFormattingLocaleHelper.shortDatePattern" />  
    <dspel:getvalueof var="timePattern" scope="request" bean="LocaleTools.userFormattingLocaleHelper.shortTimePattern" />  

    <div class="popupwindow">
      <h4><fmt:message key="defer-until"/></h4>
      <input 
        id="deferUntil" 
        name="deferUntil" 
        class="tickets" 
        type="text"
        dojoType="dijit.form.DateTextBox"
        value="now"
        constraints="{datePattern:'${datePattern}'}" 
        maxlength="10"
        onchange="dojo.byId('deferUntilSpan').innerHTML = dojo.byId('deferUntil').value + ' ' + dojo.byId('deferUntil2').value"/>
        <input 
        id="deferUntil2" 
        name="deferUntil2" 
        class="tickets" 
        type="text"
        value="now"
        maxlength="8"
        dojoType="dijit.form.TimeTextBox"
        constraints="{timePattern:'${timePattern}'}"
        onchange="dojo.byId('deferUntilSpan').innerHTML = dojo.byId('deferUntil').value + ' ' + dojo.byId('deferUntil2').value"/>
      <span id="deferUntilSpan"><fmt:message key="defer-until-placeholder"/></span>
      <img 
        id="deferUntilImg" 
        src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>" 
        width="16" 
        height="16" 
        border="0" 
        title="<fmt:message key='tooltip.selectDate'/>"
        onclick="dojo.byId('deferUntil').focus()"/>
        <br />
<script type="text/javascript">
<c:out escapeXml="false" value="//<![CDATA["/>
  dojo.require("dijit.form.DateTextBox");  
  var theAction;
  
  function initializeDeferPane(action) {
    dojo.byId("deferTicketShare").checked=false;
    dojo.byId("deferTicketNote").value="";
    dojo.byId("deferUntil").onchange = function(){
      dojo.byId("deferUntilSpan").innerHTML = dojo.byId("deferUntil").value + " " + dojo.byId("deferUntil2").value;
    };
    var okbutton = dojo.byId("deferTicketOk");
    okbutton.onclick=actionToTake;
    theAction = action;
  };
  
  function actionToTake()  {
    var theForm = dojo.byId("deferTicketForm");
    window.date=dojo.byId('deferUntil').value + ' ' + dojo.byId('deferUntil2').value;
    window.retain=theForm.deferTicketRetain.checked;
    window.reasonCode=theForm.deferReasonCode.value;
    window.comment=theForm.deferTicketNote.value;
    window.share=theForm.deferTicketShare.checked;
    theAction();
    atg.service.ticketing.cancelDeferTicketPrompt();
    return false;
  };
<c:out escapeXml="false" value="//]]>"/>
</script>
      <h4><fmt:message key="retain-ownership"/></h4>
      <input type="checkbox" id="deferTicketRetain" value="retain" checked="checked"/><label for="deferTicketRetain"><fmt:message key="yes"/></label>
      <h4><fmt:message key="reason-code"/></h4>
      <select id="deferReasonCode">
      <c:set var="reasonCodes" value="${reasonManager.contextNameToReasonNamesMap}"/>
        <c:forEach items="${reasonCodes['Ticketing.DeferForLaterPrompt']}" var="reason">
          <option value="<c:out value='${reason}'/>">
            <dspel:droplet name="/atg/ticketing/ActivityReasonDescription">
              <dspel:param name="descriptionId" value="${reason}"/>
              <dspel:param name="elementName" value="description"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="description" param="description"/>
                <c:out value="${description}"/>
              </dspel:oparam>
            </dspel:droplet>
          </option>
        </c:forEach>
      </select>
      <h4><fmt:message key="enter-note"/></h4>
      <textarea name="deferTicketNote" id="deferTicketNote" cols="50" rows="5" style="height:70px;"></textarea>
<%@  include file="noteTable.jspf" %>
     <div>
        <input type="checkbox" id="deferTicketShare" value="share"/><label for="deferTicketShare"><fmt:message key="share-with-customer-label"/></label>
        </div>
      <div class="popupwindowbuttons">
        <a href="#"
           class="buttonSmall"
           id="deferTicketOk">
          <fmt:message key="ok"/>
        </a>
        &nbsp;
        <a href="#"
           class="buttonSmall"
           id="deferCancel"
           onclick="atg.service.ticketing.cancelDeferTicketPrompt();return false;">
          <fmt:message key="cancel"/>
        </a>
      </div>
    </div>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/deferTicketPrompt.jsp#1 $$Change: 946917 $--%>
