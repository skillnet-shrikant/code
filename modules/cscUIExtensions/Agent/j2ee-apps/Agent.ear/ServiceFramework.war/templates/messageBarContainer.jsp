<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <div dojoType="atg.widget.messaging.MessageFader"
         id="messageFader">
    </div>
    <div dojoType="atg.widget.messaging.MessagePane"
         resizeDirection="left"
         titleBarDisplay="false"
         initialMessage="<fmt:message key="userMessaging.defaultInitialMessage"/>"
         style="top:25px;width:350px;height:200px;right:0px;display:none;"
         id="messageDetail">
    </div>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/messageBarContainer.jsp#1 $$Change: 946917 $--%>
