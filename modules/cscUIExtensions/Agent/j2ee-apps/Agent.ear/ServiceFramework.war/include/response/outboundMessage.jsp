<%--

This file gets and sets the data relating to a response message.
If there is an existing message in Window State then its values
are displayed when the user visits this page.  If no message object
exists, an empty one is created.  When the user performs a Send Email
action, the data from this page is sent to the OutboundMessageFormHandler
for processing.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/outboundMessage.jsp#2 $$Change: 953229 $

@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $

--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
      var="mainFormHandler" />
    <dspel:importbean bean="/atg/svc/agent/email/EmailTemplateTools"
      var="emailTemplateToools" />
    <dspel:importbean bean="/atg/arm/service/channel/ChannelService"
      var="channelService" />

    <%-- Get reference to unfinished message from current ticket --%>
    <dspel:getvalueof bean="/atg/svc/ticketing/TicketHolder.currentTicket" var="currentTicket"/>
    <svc-agent:getUnfinishedMessage ticket="${currentTicket}" var="msg"/>
    <svc-agent:getOutboundWorkInProgressActivity ticket="${currentTicket}" var="outboundActivity"/>

    <%-- JW TODO - i18n for fieldName for validation --%>
    <%-- JW TODO - email address list validation --%>

    <%-- START OF PANEL BODY--%>

    <div class="editMessage atg-csc-base-table">

    

    <div id="responseErrorMessages">
      <caf:validationMessage css="error" />
    </div>

    <%-- Insert email template form --%>
    <dspel:form style="display:none" formid="insertEmailTemplateFormId" id="insertEmailTemplateFormId" action="#">
      <dspel:input priority="-10" id="insertTemplate" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.insertTemplate"/>
      <dspel:input id="templateName" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.templateName"/>
      <dspel:input id="successURL" type="hidden" value="include/response/template.jsp" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.successURL"/>
    </dspel:form>

    <dspel:form
      action="#"
      formid="outMessageForm"
      id="outMessageForm"
      name="outMessageForm"
      method="post">
<%-- From address--%>

    <div id="senderDetails" class="atg-csc-base-table-row-group">
      <dspel:include src="/include/response/senderDetails.jsp" otherContext="${UIConfig.contextRoot}">
      </dspel:include>
    </div>


      <%@ include file="/include/response/addressFields.jspf"%>

      <dl class="subjectForm atg-csc-base-table-row-group">
        <div class="atg-csc-base-table-row">
          <dt class="atg-csc-base-table-cell"><fmt:message key="response.compose.subject.label" /></dt>
          <dd class="atg-csc-base-table-cell"><dspel:input
               bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
               type="text"
               id="subject"
               size="80"
               maxlength="254"
               value="${msg.subject}" />
          </dd>
        </div>
        <div class="atg-csc-base-table-row">
          <dt class="atg-csc-base-table-cell">
            <fmt:message key="response.compose.template"/>
          </dt>
          <dd class="atg-csc-base-table-cell">
            <c:set value="${emailTemplateToools.templateListing}" var="emailTemplates" />
            <select name="emailTemplate" id="emailTemplate">
              <c:choose>
                <c:when test="${not empty emailTemplates}">
                  <option value=""><fmt:message key="response.compose.select.template"/></option>
                  <c:forEach begin="0" var="emailTemplate" items="${emailTemplates}" varStatus="loop">
                    <option value='<c:out value="${emailTemplate}"/>'><c:out value="${emailTemplate}"/></option>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <option value=""><fmt:message key="response.compose.no.templates"/></option>
                </c:otherwise>
              </c:choose>
            </select>
            <input type="button" onClick="ResponseGetTemplateAndInsert('emailTemplate')" title="<fmt:message key='tooltip.insert-template'/>"
              value="<fmt:message key='response.message.insert.label'/>" style="width: auto !important;"/>
          </dd>
        </div>
      </dl>

      <div id="sendAsText_div" style="display:none;">
        <fmt:message key="response.send.as.text.label"/>
        <dspel:input
          bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
          id="textOnly"
          name="textOnly"
          type="checkbox"
          checked="${msg.textOnly}"/>
      </div>

      <%-- Rich Editor - Hidden field used to contain body element - this is copied from rich editor into this
           field by ResponseHandleFocusChange() in response.js  --%>
      <dspel:input
        bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
        type="hidden"
        name="htmlBody"
        id="htmlBody"
        value="" xssFiltering="false"/>

      <dspel:input
        bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
        type="hidden"
        name="textBody"
        id="textBody"
        value="" />

      <%-- Hidden field used to hold the channel id for the message  --%>
      <dspel:input
        bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
        type="hidden"
        name="channelId"
        id="channelId"
        value="${msg.channel.id}" />

      <%-- Hidden field used to hold the ticket id for the message  --%>
      <dspel:input
        bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
        type="hidden"
        name="ticketId"
        id="ticketId"
        value="${currentTicket.repositoryId}" />

      <%-- Hidden field used to hold the attachment id to be added to the message  --%>
      <dspel:input
        bean="atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
        type="hidden"
        name="attachmentId"
        id="attachmentId"
        value="" />

      </div> <%-- /editMessage --%>
      <div id="richTextEditorContent"></div>
    </dspel:form>

    <dspel:form style="display:none" formid="saveEmailForm" id="saveEmailForm" action="#">
      <dspel:input priority="-10" id="save" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.save"/>
      <dspel:input name="htmlBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.htmlBody"/>
      <dspel:input name="textBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textBody"/>
      <dspel:input name="subject" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.subject"/>
      <dspel:input name="to" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.to"/>
      <dspel:input name="cc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.cc"/>
      <dspel:input name="bcc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.bcc"/>
      <dspel:input name="textOnly" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textOnly"/>
      <dspel:input name="channelId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.channelId"/>
      <dspel:input name="ticketId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.ticketId"/>
    </dspel:form>

    <%-- ATTACHMENTS --%>
    <%-- div to hold the attachment details --%>
    <div id="attachments_div" style="display:block;">
      <c:if test="${not empty msg.attachments}">
        <dspel:include src="/include/response/attachments.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="attachments" value="${msg.attachments}"/>
          <dspel:param name="containerId" value="msgAttachments_div"/>
          <dspel:param name="panelType" value="outbound"/>
          <dspel:param name="ticketId" value="${currentTicket.repositoryId}"/>
          <dspel:param name="activityId" value="${outboundActivity.repositoryId}"/>
        </dspel:include>
      </c:if>
    </div>
    <div id="attachmentPrompt_div">
    <%-- END ATTACHMENTS --%>

    <%-- ADD AGENT ATTACHMENTS --%>
    <c:if test="${msg.channel.attachmentsAllowed}">
      <c:set var="containerId" value="agentAttachment"/>
      <c:set var="arrowId" value="${containerId}_arrow"/>

      <dspel:include src="/include/response/uploadingPrompt.jsp" otherContext="${UIConfig.contextRoot}"/>

      <dl>
        <dt class="trigger">
          <div id="atg_arm_addAttachment">
            <a href="#" onclick="toggle('<c:out value="${containerId}"/>', '<c:out value="${arrowId}"/>');return false;">
              <dspel:img id="${arrowId}" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/>
              <fmt:message key="response.message.addAgentAttachment.label" />
            </a>
          </div>
        </dt>
        <dd></dd>
      </dl>

      <div id="<c:out value="${containerId}"/>" style="display:none;" >
        <dspel:form action="completeAttachmentRefresh.jsp" enctype="multipart/form-data"
          method="post" target="agentAttachmentIframe" id="agentAttachmentForm">

          <dspel:input type="hidden" bean="OutboundMessageFormHandler.windowId"
                        name="${stateHolder.windowIdParameterName}"
                        value="" id="uploadWindowId"/>

          <div class="inserttable atg_arm_messageAttachment atg-csc-base-table">
            <div class="atg-csc-base-table-row">
              <div class="label atg-csc-base-table-cell atg-base-table-customer-search-first-label">
                <span>
                  <fmt:message key="editor.insert.file"/>
                </span>
              </div>
              <div align="left atg-csc-base-table-cell">
                 <dspel:input type="file" size="40"
                            bean="OutboundMessageFormHandler.uploadedFile"
                            id="uploadedAttachment"
                            value=""
                            onchange="ReponseSetAgentAttachmentDisplayName()"/><br/>
              </div>
            </div>
            <div class="atg-csc-base-table-row">
              <div class="label atg-csc-base-table-cell atg-base-table-customer-search-first-label">
                <fmt:message key="editor.insert.text"/>
              </div>
              <div align="left atg-csc-base-table-cell">
                <dspel:input bean="OutboundMessageFormHandler.attachmentDisplayName"
                         type="text"
                         id="attachmentDisplayName"
                         size="53"
                         maxlength="254"
                         value=""/>
                <dspel:input type="submit" bean="OutboundMessageFormHandler.addAgentAttachment" value="Attach File" id="atg_arm_uploadAttachment" style="display:none"/>
                <input type="button"  onclick="ResponseAddAgentAttachment();"  value="<fmt:message key='contentBrowser.attachment.button'/>"/>
              </div>
            </div>
          </div>
       </dspel:form>
     </div> <%-- End Hidden div for details --%>

      <div id="agentAttachmentTarget_div" style="display:none;">
        <dspel:iframe src="blank.html"
                    frameborder="0"
                    name="agentAttachmentIframe"/>
      </div>
    </c:if>

    <%-- Refresh attachments form --%>
    <dspel:form style="display:none" formid="attachmentRefreshForm" id="attachmentRefreshForm" action="#">
      <dspel:input priority="-10" id="attachmentRefresh" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.attachmentRefresh"/>
      <dspel:input id="successURL" type="hidden" value="include/response/handleAttachments.jsp" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.successURL"/>
    </dspel:form>
    <%-- Remove attachments form --%>
    <dspel:form style="display:none" formid="removeAttachmentForm" id="removeAttachmentForm" action="#">
      <dspel:input priority="-10" id="removeAttachment" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.removeAttachment"/>
      <dspel:input id="successURL" type="hidden" value="include/response/handleAttachments.jsp" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.successURL"/>
      <dspel:input id="attachmentId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.attachmentId"/>
    </dspel:form>
    <%-- Add system attachment form --%>
    <dspel:form style="display:none" formid="addSystemAttachmentForm" id="addSystemAttachmentForm" action="#">
      <dspel:input priority="-10" id="addSystemAttachment" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.addSystemAttachment"/>
      <dspel:input id="htmlBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.htmlBody"/>
      <dspel:input id="textBody" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textBody"/>
      <dspel:input id="subject" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.subject"/>
      <dspel:input id="to" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.to"/>
      <dspel:input id="cc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.cc"/>
      <dspel:input id="bcc" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.bcc"/>
      <dspel:input id="textOnly" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.textOnly"/>
      <dspel:input id="channelId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.channelId"/>
      <dspel:input id="successURL" type="hidden" value="include/response/handleAttachments.jsp" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.successURL"/>
      <dspel:input id="attachmentId" type="hidden" value="" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler.attachmentId"/>
    </dspel:form>
    <%-- Add spell check form --%>
    <div id="spellCheckDiv" style="display: none"></div>
    <dspel:form name="spellCheckForm" action="#" formid="spellCheckForm" id="spellCheckForm">
      <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler.spellCheck"/>
      <dspel:input type="hidden" name="spellCheckString" value="" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler.spellCheckString"/>
      <dspel:input type="hidden" name="solutionLanguage" value="" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler.solutionLanguage"/>
      <dspel:input type="hidden" name="errorURL" value="error.jsp" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler.errorURL"/>
      <dspel:input type="hidden" name="successURL" value="include/response/editor/plugins/spell/spellCheck.jsp" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler.successURL"/>
    </dspel:form>


    </div>

    <%-- END ADD AGENT ATTACHMENTS --%>

    <%-- END OF MESSAGE BODY--%>

    <%-- Hidden DIV to temporarily contain content that needs to be inserted elsewhere.
         This is used to hold message content on page load before Rich Editor is initialized and copied into the
         rich editor by ResponseInitPanel() call.
         Also holds the content of an email template after the agent selects one from the templates list.
    --%>
    <div style="display:none;" id="responseTempTextContent_div"><c:out value="${msg.textBody}" escapeXml="true"/></div>
    <div style="display:none;" id="responseTempHTMLContent_div"><c:out value="${msg.htmlBody}" escapeXml="true"/></div>

    <%-- Call Javascript initialisation functions in script/response.js --%>
    <c:set var="initPanelJavascript"
      value="ResponseInitPanel();"/>
    <script type="text/javascript">      

       //determine if the language is supported for spell check
       var languageSupported = false;

       <dspel:droplet name="/atg/svc/ui/util/IsLanguageSupportedForSpellCheck">
         <dspel:oparam name="true">
           languageSupported = true;
         </dspel:oparam>
       </dspel:droplet>
       
    
       // The FCK Editor has a bug in which the rich text editor crashes Internet Explorer when focus is
       // placed inside the editor and atgSubmitAction is called. To correct this issue, we use the publish/subscribe
       // feature to send out a notification that this function is being called - and we can then reset the page
       // focus before the panel is unloaded.
       var handle = dojo.subscribe("atgSubmitAction", function(message){
          console.debug("atgSubmitAction topic received: Removing focus from FCKEditor");
          var subject = dojo.byId('subject');
          if (subject) {
            console.debug("Setting focus on the 'subject' element");
            subject.focus();
          }
          
          dojo.unsubscribe(handle);
          console.debug("Unsubscribing atgSubmitAction");
       });
      
      _container_.onLoadDeferred.addCallback(function () {
        <c:out value="${initPanelJavascript}"/>
      });
      
      _container_.onUnloadDeferred.addCallback(function () {
      });
    </script>
  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/outboundMessage.jsp#2 $$Change: 953229 $$DateTime: 2015/02/26 10:47:28 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/outboundMessage.jsp#2 $$Change: 953229 $--%>
