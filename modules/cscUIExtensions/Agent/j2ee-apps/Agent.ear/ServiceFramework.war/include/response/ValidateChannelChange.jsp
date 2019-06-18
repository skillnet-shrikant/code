<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dspel:page xml="true">
	<dspel:importbean var="outboundFormHandler" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"/>
	<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
	<%
	atg.svc.agent.ui.formhandlers.OutboundMessageFormHandler handler = 
		(atg.svc.agent.ui.formhandlers.OutboundMessageFormHandler)pageContext.findAttribute("outboundFormHandler");
	
	pageContext.setAttribute("errors", handler.getValidationErrors());
	%>
	
	<json:object>
	  <json:property name="errors" value="${errors}"/>
	</json:object>
	
	<c:if test="${!empty mainFormHandler.validationErrors}">
	  <span class="error">      
	      <c:forEach var="errorItem" items='${mainFormHandler.validationErrors}' varStatus="loop">
	        <fmt:message key="${errorItem.messageKey}" >        
	          <c:forEach var="errorItemParam" items='${errorItem.params}' varStatus="loop">
	            <fmt:param value="${errorItemParam}" />
	          </c:forEach>
	        </fmt:message>
	        <br/>
	      </c:forEach>
	      <br /> 
	  </span>
	</c:if>
	</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/ValidateChannelChange.jsp#1 $$Change: 946917 $--%>
