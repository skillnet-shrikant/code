<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/template.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<%@ page import= "atg.svc.agent.email.EmailTemplateTools"   %>
<dspel:page xml="true">

	<dspel:getvalueof param="templateName" var="template" />
	<dspel:importbean bean="/atg/svc/agent/email/EmailTemplateTools"
      var="emailTemplateToools"/>	
      
	<%				
		// JW RC TODO This should be removed and replaced with a custom tag.
		String templateName = (String) pageContext.findAttribute("template");		
		
		EmailTemplateTools tools = (EmailTemplateTools) pageContext.findAttribute("emailTemplateToools");
		String content = tools.getTemplateContents(templateName);
	  	pageContext.setAttribute("templateContent", content);		
	%>
    <c:out value="${templateContent}" escapeXml="false" />	

</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/template.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/template.jsp#1 $$Change: 946917 $--%>
