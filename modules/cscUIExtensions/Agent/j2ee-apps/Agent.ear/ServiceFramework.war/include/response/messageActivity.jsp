<%--

Activity detail display for an inbound message activity.
This JSP will be included in the Ticket Activity view when a user clicks the expand icon for an inbound message activity.

Expected params are:
activity       The to-mapped activity repository item
activityItem   The actual activity repository item
activityInfo   The rendering info object for this activity

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/messageActivity.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="../top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

  <%-- Determine if this is an inbound or outbound activity --%>
  <c:set var="inboundActivityKey" value="inboundMessageDisplayName"/>
  <c:set var="problematicActivityKey" value="problematicMessageDisplayName"/>
  <c:set var="inboundType" value="inbound"/>
  <c:choose>
    <c:when test="${activityInfo.displayNameResourceKey eq inboundActivityKey}">
      <c:set var="panelType" value="inboundActivity"/>
    </c:when>
    <c:when test="${activityInfo.displayNameResourceKey eq problematicActivityKey}">
      <c:set var="panelType" value="problematicActivity"/>
    </c:when>
    <c:otherwise>
      <c:set var="panelType" value="outboundActivity"/>
    </c:otherwise>
  </c:choose>

    <%-- Get reference to Message object to render--%>
    <svc-agent:getMessage activity="${activityItem}" var="msg"/>

    <%-- Create vars for the div IDs for hidden areas. These must be unique for each activity. --%>
    <c:set var="headerDetailsContainerId" value="headerDetails_${activity.id}"/>
    <c:set var="attachmentDetailsContainerId" value="attachmentDetails_${activity.id}"/>

    <div class="editArea">

			<div class=="emailHeader">

			<dl class="emailHeader">
        <%-- Addresses --%>
        <table>
	      <!-- TODO - From and ReplyTo addresses -->
	   <tr>
	      <dspel:include src="/include/response/addressList.jsp" otherContext="${UIConfig.contextRoot}">
	        <dspel:param name="addressList" value="${msg.fromAddressList}"/>
	        <dspel:param name="labelKey" value="response.compose.address.from.label"/>
	      </dspel:include>
	   </tr>
	   <tr>
	      <%-- Only display Reply To address is different to From Address --%>
	      <c:if test="msg.fromAddress.address ne msg.replyToAddress.address">
	        <dspel:include src="/include/response/addressList.jsp" otherContext="${UIConfig.contextRoot}">
	          <dspel:param name="addressList" value="${msg.replyToAddressList}"/>
	          <dspel:param name="labelKey" value="response.compose.address.replyto.label"/>
	        </dspel:include>
	      </c:if>
	   </tr>
	   <tr>
	      <dspel:include src="/include/response/addressList.jsp" otherContext="${UIConfig.contextRoot}">
	        <dspel:param name="addressList" value="${msg.toAddressList}"/>
	        <dspel:param name="labelKey" value="response.compose.address.to.label"/>
	      </dspel:include>
	   </tr>
	   <tr>
	      <dspel:include src="/include/response/addressList.jsp" otherContext="${UIConfig.contextRoot}">
	        <dspel:param name="addressList" value="${msg.ccAddressList}"/>
	        <dspel:param name="labelKey" value="response.compose.address.cc.label"/>
	      </dspel:include>
	   </tr>
	   <tr>
	      <dspel:include src="/include/response/addressList.jsp" otherContext="${UIConfig.contextRoot}">
	        <dspel:param name="addressList" value="${msg.bccAddressList}"/>
	        <dspel:param name="labelKey" value="response.compose.address.bcc.label"/>
	      </dspel:include>
	   </tr>
	   <tr>

				<c:if test="${msg.inbound && !empty msg.classifiedResults}">
					<fmt:message var="classificationLabel" key="response.message.classification.label" />
           <td  class="emailActivityHeader">
           <c:out value="${classificationLabel}:"/></td>
           <td>
           		<c:out value="${msg.classifiedResults[0].RMTarget.name}"/>
           </td>
           </tr>
           
	   <tr>
        </c:if>

	      <td  class="emailActivityHeader"><fmt:message key="response.message.date.label" /></td>
	      <td><fmt:formatDate value="${msg.date}" type="both" dateStyle="medium" timeStyle="short"/></td>
	      
	   </tr>
	   <tr>

	      <c:if test="${msg.channel.subjectAllowed}">
	        <td  class="emailActivityHeader"><fmt:message key="response.compose.subject.label"/></td>
	        <td><c:out value="${msg.subject}"/></td>
	      </c:if>
	      
	   </tr>
	   <tr>

	      <c:if test="${not empty msg.encoding && panelType ne inboundType }">
	          <td  class="emailActivityHeader"><fmt:message key="response.message.encoding.label" /></td>
	          <td>
	          	<c:out value="${msg.encoding}"><fmt:message key="response.message.unknown.label"/></c:out>
	          </td>
	       </c:if>
	    
	   </tr>
	 </table>
	    <!-- HEADERS -->

			<c:set var="inboundActivityType" value="inboundActivity"/>
			<c:if test="${panelType eq inboundActivityType}">
			    <dspel:include src="/include/response/headerDetails.jsp" otherContext="${UIConfig.contextRoot}">
			      <dspel:param name="msg" value="${msg}"/>
			      <dspel:param name="containerId" value="${headerDetailsContainerId}"/>
			    </dspel:include>
			</c:if>

	    <!-- END OF HEADERS -->
			</div>


	    <!-- MESSAGE BODY -->
	    <div class="viewMessage">
	    	<c:set var="inbound" value="inbound"/>
	      <c:choose>
	        <c:when test="${panelType eq inbound }">
	          <div class="messageBody ticketMessageActivity textMessageBody">
	            <svc-agent:convertTextToHtml var="convertedTextBody" text="${msg.textBody}"/>
			        <c:out value="${convertedTextBody}" escapeXml="false" />
	          </div>
			    </c:when>
			    <c:otherwise>
						<c:choose>
							<c:when test="${!empty msg.htmlBody}">
			          <div class="messageBody ticketMessageActivity">
			            <c:out value="${msg.htmlBody}" escapeXml="false" />
			          </div>
			        </c:when>
			        <c:otherwise>
			          <div class="messageBody ticketMessageActivity">
			            <svc-agent:convertTextToHtml var="convertedTextBody" text="${msg.textBody}"/>
			            <c:out value="${convertedTextBody}" escapeXml="false" />
			          </div>
			        </c:otherwise>
	      		</c:choose>
	      	</c:otherwise>
	      </c:choose>
      </div>

	    <!-- ATTACHMENTS -->
	    <c:if test="${not empty msg.attachments}">
		      <dspel:include src="/include/response/attachments.jsp" otherContext="${UIConfig.contextRoot}">
		        <dspel:param name="attachments" value="${msg.attachments}"/>
		        <dspel:param name="containerId" value="${attachmentDetailsContainerId}"/>
		        <dspel:param name="panelType" value="${panelType}"/>
		        <dspel:param name="ticketId" value="${activity.ticket.repositoryId}"/>
		        <dspel:param name="activityId" value="${activity.repositoryId}"/>
		      </dspel:include>
	    </c:if>
	    <!-- END ATTACHMENTS -->

    </div>
    <!-- END MESSAGE BODY -->



  </dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/messageActivity.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/messageActivity.jsp#1 $$Change: 946917 $--%>
