<%--

Next steps

--%>

<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:getvalueof var="isTransient" bean="/atg/svc/ticketing/TicketHolder.currentTicket.transient"/>

  <dspel:importbean bean="/atg/svc/ticketing/TicketHolder"/>
  <dspel:getvalueof var="currentTicketItem" bean="/atg/svc/ticketing/TicketHolder.currentTicket"/>
  <dspel:tomap var="currentTicket" value="currentTicketItem"/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  
  <div id="liveChatTicketNextSteps" style="display:none;">
  
  <%-- Chat Next Steps --%>
  <dspel:include src="/templates/nextStepsTemplate.jsp" otherContext="${UIConfig.contextRoot}">
    <%--<dspel:param name="actionId" value="discardEmail"/>--%>
    <dspel:param name="actionJavaScript" value="chat.endChat();return false;"/>
    <dspel:param name="imageUrl" value="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_endChat.gif"/>
    <dspel:param name="labelKey" value="nextSteps.endChat.label"/>
  </dspel:include>
  
  </div>
  
  <div id="notLiveChatTicketNextSteps" style="display:none;">
  
  <%-- Include Ticketing Next Steps --%>
  <dspel:include src="/include/nextSteps/ticketViewNextSteps.jsp" otherContext="${UIConfig.contextRoot}"/>
  
  </div>
  
  <script type="text/javascript">
    if (window.chat){
      chat.setNextStepsDisplayState(chat.isChatTicket(window.ticketId));
    }
  </script>
  
  </dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/chatNextSteps.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/nextSteps/chatNextSteps.jsp#1 $$Change: 946917 $--%>
