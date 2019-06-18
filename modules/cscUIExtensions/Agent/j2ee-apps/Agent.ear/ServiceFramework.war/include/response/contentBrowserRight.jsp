<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserRight.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%-- Commented out until ARM is moved into Wisdom --%>
<%-- page import="atg.arm.respond.bean.ContentType" --%>

<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
  <caf:outputXhtml targetId="contentBrowserContentDetails">
    <div class="sideBySide contentView">
      <dspel:getvalueof var="contentType" param="contentType"/>
      <dspel:getvalueof var="contentId" param="contentId"/>
      <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
        <c:choose>
          <c:when test="${contentType == 'Content'}">
            <dspel:importbean var="armConfiguration"  bean="/atg/arm/contentresources/RMContentResourcesBeanManager"/>
            <svc-agent:getContent var="content" primaryKey="${contentId}"/>
            <c:set var="content" value="${content}" scope="request"/>
            <% /**
                * once ARM is moved into wisdom, change:
                * "Template" to ContentType.TEMPLATE
                * and
                * "Attachment" to ContentType.ATTACHMENT
                * and
                * "TemplateWithAttachments" to ContentType.TEMPLATE_WITH_ATTACHMENT
                **/
              pageContext.setAttribute("templateType", "template");
              pageContext.setAttribute("attachmentType", "attachment");
              pageContext.setAttribute("templateWithAttachmentType", "TemplateWithAttachments");
            %>
            <c:choose>
              <c:when test="${content.typeAsString == templateType}">
              <caf:size var="size" collection="${content.attachments}"/>
              <c:choose>
              	<c:when test="${size == null}">
	                <dspel:include src="/include/response/contentBrowserTemplateDetail.jsp"  otherContext="${UIConfig.contextRoot}"/>
				</c:when>
              	<c:when test="${size == 0}">
	                <dspel:include src="/include/response/contentBrowserTemplateDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
				</c:when>
              	<c:when test="${size > 0}">
	                <dspel:include src="/include/response/contentBrowserTemplateWithAttachmentsDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
				</c:when>
			  </c:choose>
              </c:when>
              <c:when test="${content.typeAsString == attachmentType}">
                <dspel:include src="/include/response/contentBrowserAttachmentDetail.jsp" otherContext="${UIConfig.contextRoot}"/>
              </c:when>
            </c:choose>
          </c:when>
          <c:otherwise>
            <!-- currently unsupported or undisplayed node type -->
          </c:otherwise>
        </c:choose>
      </dspel:layeredBundle>
    </div>
  </caf:outputXhtml>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserRight.jsp#1 $$Change: 946917 $--%>
