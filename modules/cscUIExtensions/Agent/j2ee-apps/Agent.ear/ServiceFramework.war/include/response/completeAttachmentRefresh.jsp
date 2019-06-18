<%--
Attachments list for inbound/outbound activities

Expected params
attachments : The collection of attachments to display (required)
containerId : The ID of the container to use for expandable area (required)
panelType: The panel that is including the attachments - should be one of
           'inbound' or 'outbound'
ticketId: The ID of the ticket
activityId: the ID of the activity (optional - only required for inbound attachments)

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/completeAttachmentRefresh.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="../top.jspf"%>
<dspel:page xml="true">
    <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"
      var="mainFormHandler" />
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    	<c:choose>
	      	<c:when test="${not empty mainFormHandler.validationErrors}">
		      	<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
		      		<c:forEach var="errorItem" items='${mainFormHandler.validationErrors}' varStatus="loop">
		      		  <c:if test="${not empty errorMsgs}">
		      		  	<c:set var="errorMsgs" value="${errorMsgs},"/>
		      		  </c:if>
	        			<fmt:message key="${errorItem.messageKey}" var="errorMsg">
	          			<c:forEach var="errorItemParam" items='${errorItem.params}' varStatus="loop">
	            			<fmt:param value="${errorItemParam}"/>
	          			</c:forEach>
	        			</fmt:message>
	        			<c:set var="errorMsgs" value="${errorMsgs}${errorMsg}"/>
	      			</c:forEach>
						</dspel:layeredBundle>



		      	<body onload="parent.ResponseAttachmentErrorsRefresh('<c:out value="${errorMsgs}"/>');">
					  </body>

	      	</c:when>
	      	<c:otherwise>
				    <body onload="parent.ResponseCompleteAttachmentRefresh()" >
				    </body>
				  </c:otherwise>
			</c:choose>

    </head>
  </html>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/completeAttachmentRefresh.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/completeAttachmentRefresh.jsp#1 $$Change: 946917 $--%>

