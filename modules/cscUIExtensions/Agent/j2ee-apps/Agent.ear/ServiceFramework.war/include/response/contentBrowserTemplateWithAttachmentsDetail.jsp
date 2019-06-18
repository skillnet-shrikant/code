<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTemplateWithAttachmentsDetail.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <c:set var="template" value="${content.data}"/>
    <h5><fmt:message key="contentBrowser.template.label"/> <c:out value="${content.name}"/></h5>
    
    <svc-agent:convertTextToHtml var="convertedTextBody" text="${template}"/>
    
    <div class="scrollContent">
      <c:out value="${convertedTextBody}" escapeXml="false"/>
    </div>
    <h5><fmt:message key="contentBrowser.associatedAttachment.label"/></h5>
    <div class="scrollContent">
      <dl class="attachmentList">
        <c:set var="attachments" value="${content.attachments}"/>
        <c:set var="attachmentIds" value=""/>
        <c:set var="attachmentCount" value="0"/>
        <c:forEach var="attachment" items="${attachments}" varStatus="status">
          <c:choose>
            <c:when test="${attachmentCount != 0}">
              <c:set var="attachmentIds" value="${attachmentIds}${','}${attachment.id}"/>
            </c:when>
            <c:otherwise>
              <c:set var="attachmentIds" value="${attachment.id}"/>
            </c:otherwise>
          </c:choose>
          <c:set var="attachmentCount" value="${attachmentCount+1}"/>
          <c:set var="attachmentType" value="LiteAttachment"/>
    	  <c:if test="${UIConfig.armInstalled}">
    	    <c:set var="attachmentType" value="System"/>
          </c:if>
          <c:url value="/attachmentServlet" var="downloadUrl">
            <c:param name="attachmentId" value="${attachment.id}"/>
            <c:param name="attachmentType" value="${attachmentType}"/>
          </c:url>
          <dt>
          	<a name="ArrowRight" id="ArrowRight" href="#" onclick="toggle('<c:out value="${attachmentCount}"/>','arrowKey<c:out value="${attachmentCount}"/>');return false;">
              <dspel:img id="${'arrowKey'}${attachmentCount}" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14px" height="14px" align="middle"/>
              </a>
              <c:set var = "fileName" value="${attachment.name}" />
              <%@ include file="/include/response/fileIcons.jspf"%>
              <dspel:img src="${imageRef}" width="21px" height="21px" align="middle" title="${imageTooltip}"/>
              <dspel:a href="${downloadUrl}"><c:out value="${attachment.name}"/></dspel:a>
          </dt>
          <dd>
              <%-- File size --%>
              <c:set var="fileSize" value="${attachment.length}"/>
              <%@ include file="/include/response/fileSize.jspf"%>

              <c:out value="${friendlyFileSize}"/>
              <c:out value="${friendlyFileSizeLabel}"/>
          </dd>
          <div id='<c:out value="${attachmentCount}"/>' style="display:none;">
            <dt><fmt:message key="contentBrowser.attachment.filename.label"/></dt>
            <dd><c:out value="${attachment.name}"/></dd>
            <dt><fmt:message key="contentBrowser.attachment.description.label"/></dt>
            <dd><c:out value="${attachment.description}"/></dd>
          </div>
        </c:forEach>
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
          <span id="textToInsert" style="display:none"><c:out value="${convertedTextBody}" escapeXml="true"/></span>
          <form name="addAttachmentForm">
            <input type=hidden id="attachmentId" name="attachmentId" value="<c:out value = '${attachmentIds}' />" />

            <a href="#" onclick="RespInsrtAtCaretAddSysAttachment('textToInsert');return false;" class="flushRight buttonSmall go" title='<fmt:message key="contentBrowser.templateAttachment.button"/>'>
             <span>
             <fmt:message key="contentBrowser.templateAttachment.button"/>
             </span>
            </a>

            <div class="floatClear"></div>

          </form>
       </c:otherwise>
     </c:choose>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTemplateWithAttachmentsDetail.jsp#1 $$Change: 946917 $--%>
