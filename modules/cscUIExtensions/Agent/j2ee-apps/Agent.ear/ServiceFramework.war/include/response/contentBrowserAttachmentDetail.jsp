<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserAttachmentDetail.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <c:set var="attachment" value="${content}"/>
    <c:set var="attachmentType" value="LiteAttachment"/>
    <c:if test="${UIConfig.armInstalled}">
    	<c:set var="attachmentType" value="System"/>
    </c:if>
    <c:url value="/attachmentServlet" var="downloadUrl">
      <c:param name="attachmentId" value="${attachment.id}"/>
      <c:param name="attachmentType" value="${attachmentType}"/>
    </c:url>
    <h5><fmt:message key="contentBrowser.attachment.label" /> <dspel:a href="${downloadUrl}"><c:out value="${attachment.name}"/></dspel:a></h5>
    <div class="scrollContent">
      <dl class="attachmentList">
        <dt><fmt:message key="contentBrowser.attachment.filename.label"/></dt>
        <dd>
          <dspel:a href="${downloadUrl}"><c:out value="${attachment.name}"/></dspel:a>
        </dd>
        <dt><fmt:message key="contentBrowser.attachment.size.label"/></dt>
        <dd>
          <%-- File size --%>
          <c:set var="fileSize" value="${attachment.length}"/>
          <%@ include file="/include/response/fileSize.jspf"%>

          <c:out value="${friendlyFileSize}"/>
          <c:out value="${friendlyFileSizeLabel}"/>
        </dd>
        <dt><fmt:message key="contentBrowser.attachment.type.label"/></dt>
        <dd>
          <c:set var="fileName" value="${attachment.name}"/>
          <%@ include file="/include/response/fileIcons.jspf"%>
          <dspel:a href="${downloadUrl}">
            <dspel:img src="${imageRef}" width="21px" height="21px" align="middle" title="${imageTooltip}"/>
          </dspel:a>
        </dd>
        <dt><fmt:message key="contentBrowser.attachment.description.label"/></dt>
        <dd>
          <c:out value="${attachment.description}" />
        </dd>
      </dl>
    </div>
    <div class="formButtons">
    <%-- Get reference to unfinished message from current ticket --%>
    <dspel:getvalueof bean="/atg/svc/ticketing/TicketHolder.currentTicket" var="currentTicket"/>
    <svc-agent:getUnfinishedMessage ticket="${currentTicket}" var="msg"/>
    <c:set var="isAttachmentsAllowed" value="${msg.channel.attachmentsAllowed}"/>
    <c:choose>
      <c:when test="${isAttachmentsAllowed == false}">
	<div>
        <fmt:message key="contentBrowser.attachmentsAllowed.message">
	  <fmt:param value="${msg.channel.name}"/>
	</fmt:message>
	</div>
      </c:when>
      <c:otherwise>
        <c:set var="isAttachedAttachment" value="${false}"/>
        <c:forEach var="messageAttachments" items="${msg.attachments}" >
          <c:set var="msgAttachId" value="${messageAttachments.id}"/>
          <c:set var="attachmentId" value="${attachment.id}"/>
          <c:if test="${attachmentId == msgAttachId}">
            <c:set var="isAttachedAttachment" value="${true}"/>
          </c:if>
        </c:forEach>

        <%-- Generate id's for the div elements based on the attachments id --%>
        <c:set var="attachmentDivId" value="contentBrowserAttachment${attachment.id}"/>
        <c:set var="attachmentAttachedDivId" value="contentBrowserAttachmentAlreadyAttached${attachment.id}"/>

        <div id="<c:out value='${attachmentDivId}'/>" style="display:none;">
          <form name="addAttachmentForm">
            <input type=hidden id="attachmentId" name="attachmentId" value="<c:out value='${attachment.id}' />" />

            <a href="#" onclick="ResponseAddAttachment(<c:out value='${attachment.id}'/>);return false;" class="flushRight buttonSmall go" title='<fmt:message key="contentBrowser.attachment.button"/>'>
              <span>
              <fmt:message key="contentBrowser.attachment.button"/>
              </span>
            </a>

            <div class="floatClear"></div>


          </form>
        </div>
        <div id="<c:out value='${attachmentAttachedDivId}'/>" style="display:none;">
          <fmt:message key="contentBrowser.attachment.message"/>
        </div>

<%--	Removed to stop incorrect error message appearing
        <c:choose>
          <c:when test="${isAttachedAttachment == true}">
--%>
            <%-- Attachment is already attached --%>
<%--            <script type="text/javascript">
            ResponseToggleDisplay("<c:out value='${attachmentAttachedDivId}'/>");
            </script>
          </c:when>
          <c:otherwise>
--%>            <%-- Attachment isn't attached, show buttons --%>
            <script type="text/javascript">
            ResponseToggleDisplay("<c:out value='${attachmentDivId}'/>");
            </script>
<%--          </c:otherwise>
        </c:choose>
--%>
      </c:otherwise>
    </c:choose>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserAttachmentDetail.jsp#1 $$Change: 946917 $--%>
