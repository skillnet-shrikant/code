
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ include file="../top.jspf"%>

<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<dspel:importbean var="outboundFormHandler" bean="/atg/svc/agent/ui/formhandlers/OutboundMessageFormHandler"/>
	<%
	atg.svc.agent.ui.formhandlers.OutboundMessageFormHandler handler = 
		(atg.svc.agent.ui.formhandlers.OutboundMessageFormHandler)pageContext.findAttribute("outboundFormHandler");
	
	pageContext.setAttribute("errors", handler.getValidationErrors());
	%>
	
<c:if test="${!empty errors}">
  <span class="error">
      <c:forEach var="errorItem" items='${errors}' varStatus="loop">
        <fmt:message key="${errorItem.messageKey}" >        
          <c:forEach var="errorItemParam" items='${errorItem.params}' varStatus="loop">
            <fmt:param value="${errorItemParam}" />
          </c:forEach>
        </fmt:message>
        <br/>
      </c:forEach>
     <br/>
  </span>
</c:if>
</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/responseErrors.jsp#1 $$Change: 946917 $--%>
