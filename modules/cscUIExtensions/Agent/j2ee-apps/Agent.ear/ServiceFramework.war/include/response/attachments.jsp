<%--
Attachments list for inbound/outbound activities

Expected params
attachments : The collection of attachments to display (required)
containerId : The ID of the container to use for expandable area (required)
panelType: The panel that is including the attachments - should be one of
           'inbound' or 'outbound' or 'problematic'
ticketId: The ID of the ticket
activityId: the ID of the activity (optional - only required for inbound attachments)

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/attachments.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- Get EL reference to dspel:param parameters --%>
  <dspel:getvalueof var="attachments" param="attachments"/>
  <dspel:getvalueof var="containerId" param="containerId"/>
  <dspel:getvalueof var="panelType"   param="panelType"/>
  <dspel:getvalueof var="ticketId"    param="ticketId"/>
  <dspel:getvalueof var="activityId"  param="activityId"/>
  <c:set var="arrowId" value="${containerId}_arrow"/>

  <c:set var="inboundPanelType" value="inbound"/>
  <c:set var="outboundPanelType" value="outbound"/>
  <c:set var="SystemAttachmentType" value="System"/>

  <%-- Create display label for attachments when area is collapsed.
  This should display "Attachment: file1.txt, file2.doc, fileN.pdf" --%>
  <c:set var="attachmentsListLabel">
    <c:forEach items="${attachments}" var="attachment" varStatus="status">
      <c:out value="${attachment.filename}"/>
      <c:if test="${not status.last}">, </c:if>
    </c:forEach>
  </c:set>

    <dl class="statusLeft">
      <dt class="trigger">
        <a href="#" onclick="toggle('<c:out value="${containerId}"/>', '<c:out value="${arrowId}"/>');ResponseToggleDisplayTo('<c:out value="${containerId}_list"/>','inline');return false;">
          <dspel:img id="${arrowId}" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/>
          <fmt:message key="response.compose.attachments.label" />
          <span id="<c:out value='${containerId}_list'/>" style="display:inline;">
            <c:out value="${attachmentsListLabel}" />
          </span>
        </a>
      </dt>

      <dd id="<c:out value='${containerId}'/>" style="display:none;" >
      <table class="data">
        <tr>
          <th></th>
          <th><fmt:message key="response.message.attachments.filename.label"/></th>
          <th><fmt:message key="response.message.attachments.size.label"/></th>
          <th><fmt:message key="response.message.attachments.mimetype.label"/></th>
          <c:if test="${panelType eq outboundPanelType }">
            <th><fmt:message key="response.message.attachments.source.label"/></th>
            <th class="w5p"><fmt:message key="response.message.attachments.remove.label"/></th>
          </c:if>
        </tr>
        <c:forEach items="${attachments}" var="attachment" varStatus="status">
          <%-- Set alternate table row styles --%>
          <c:choose>
            <c:when test="${(status.count mod 2) == 0}">
              <c:set var="styleClass" value="alt"/>
            </c:when>
            <c:otherwise>
              <c:set var="styleClass" value=""/>
            </c:otherwise>
          </c:choose>

          <%-- Use correct attachment Id depending on type --%>
          <c:choose>
  		    <c:when test="${attachment.type eq  SystemAttachmentType}">
  		      <c:set var="attachmentId" value="${attachment.contentId}"/>
  		    </c:when>
  		    <c:otherwise>
  			  <c:set var="attachmentId" value="${attachment.id}"/>
  		    </c:otherwise>
	      </c:choose>

          <c:url value="/attachmentServlet" var="downloadUrl">
            <c:param name="attachmentId" value="${attachmentId}"/>
            <c:param name="attachmentType" value="${attachment.type}"/>
            <c:param name="ticketId" value="${ticketId}"/>
            <c:param name="activityId" value="${activityId}"/>
          </c:url>

          <tr class="<c:out value='${styleClass}'/>">
            <td>
              <c:set var="fileName" value="${attachment.filename}"/>
              <%@ include file="/include/response/fileIcons.jspf"%>
              <dspel:a href="${downloadUrl}"><dspel:img src="${imageRef}" width="21px" height="21px" align="middle" title="${imageTooltip}"/></dspel:a>
            </td>
            <td><dspel:a href="${downloadUrl}"><c:out value="${attachment.filename}"/></dspel:a></td>
            <td>
              <%-- File size --%>
              <c:set var="fileSize" value="${attachment.size}"/>
              <%@ include file="/include/response/fileSize.jspf"%>

              <c:out value="${friendlyFileSize}"/>
              <c:out value="${friendlyFileSizeLabel}"/>
            </td>
            <td>
              <c:out value="${attachment.mimeType}"/>
            </td>
            <c:if test="${panelType eq outboundPanelType }">
              <c:set var="typeKey" value="response.message.attachments.type.${attachment.type}"/>
              <td><fmt:message key="${typeKey}"/></td>
              <td>
                <a href="#" onclick="ResponseRemoveAttachment('<c:out value="${attachment.id}"/>')">
                <div class="atgServiceFrameworkDeleteIcon"></div>
                </a>
              </td>
            </c:if>
          </tr>
        </c:forEach>

      </table>
      </dd>

    </dl>

  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/attachments.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/attachments.jsp#1 $$Change: 946917 $--%>
