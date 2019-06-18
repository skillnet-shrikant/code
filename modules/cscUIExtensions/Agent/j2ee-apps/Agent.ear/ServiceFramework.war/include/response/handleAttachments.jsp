<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/handleAttachments.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

  Handles rendering attachments via an action.

--%>

<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">

  <caf:outputXhtml targetId="responseTempHTMLContent_div">

    <div id="attachmentInner_div">

      <dspel:getvalueof bean="/atg/svc/ticketing/TicketHolder.currentTicket"
        var="currentTicket"/>

      <svc-agent:getUnfinishedMessage ticket="${currentTicket}" var="msg"/>
      <svc-agent:getOutboundWorkInProgressActivity ticket="${currentTicket}" var="outboundActivity"/>

      <c:choose>
        <c:when test="${not empty msg.attachments}">
          <dspel:include src="/include/response/attachments.jsp" otherContext="${UIConfig.contextRoot}">
            <dspel:param name="attachments" value="${msg.attachments}"/>
            <dspel:param name="containerId" value="msgAttachments_div"/>
            <dspel:param name="panelType" value="outbound"/>
            <dspel:param name="ticketId" value="${currentTicket.repositoryId}"/>
            <dspel:param name="activityId" value="${outboundActivity.repositoryId}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
        &nbsp;
           <!--  Need to render some content here for some reason.  If we
                 don't then the javascript functions never get invoked.  -->
        </c:otherwise>
      </c:choose>
    </div>
    
    </caf:outputXhtml>
    
    <caf:outputXhtml targetId="javascript">
      <c:choose>
        <c:when test="${not empty msg.attachments}">
          ResponseReplaceContentWith("attachments_div", "attachmentInner_div");
          divSetVisible("attachments_div");
        </c:when>
        <c:otherwise>
          ResponseHideContainer("attachments_div");
        </c:otherwise>
      </c:choose>

  </caf:outputXhtml>

</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/handleAttachments.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/handleAttachments.jsp#1 $$Change: 946917 $--%>
