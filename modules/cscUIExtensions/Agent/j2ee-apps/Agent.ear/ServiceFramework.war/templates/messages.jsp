<%--
 Messages Button and Pop-Up Window
 This file renders the message button in the global content area
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/messages.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <div class="gcn_btn">
      <div class="gcn_btn_core">
        <div class="gcn_btn_coreInner">

          <div dojoType="atg.widget.messaging.SmallMessageBar"
               class="atg_navigationHighlight"               
               targetDialog="messageDetailWidget"
               targetFader="messageFaderWidget"
               hideLinkText=""
               showLinkText=""
               confirmationMessage="<fmt:message key="userMessaging.defaultSuccessMessage"/>"
               errorMessage="<fmt:message key="userMessaging.defaultFailureMessage"/>"
               initialText="<fmt:message key="userMessaging.summary"/>"
               id="messageBar">
          </div>

        </div>
      </div>
      <div class="gcn_btn_label">&nbsp;</div>
    </div>

<script type="text/javascript">
  function messageButtonClick() {
      var messageBar = dojo.byId("messageDetailWidget");
      if (messageBar.style.display=="none") {
        messageBar.style.display="block";
      }
      else {
        messageBar.style.display="none";
      }
  }
</script>


</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/messages.jsp#1 $$Change: 946917 $--%>