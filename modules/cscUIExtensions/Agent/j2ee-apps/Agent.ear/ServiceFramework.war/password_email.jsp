<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/password_email.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core"%>
<dspel:page xml="true">
<%/* render email content in the provided customer display locale */%>
<dspel:getvalueof var="localeString" param="customerLocale"/>
<c:if test="${!empty localeString}">
  <fmt:setLocale value="${localeString}" scope="request"/>
</c:if>
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
<p>
<fmt:message key="resetPassword.emailTemplate.resetInfo"/>
</p>
<p>
<fmt:message key="resetPassword.emailTemplate.loginHeading"/> <dspel:valueof param="login"/>.
<fmt:message key="resetPassword.emailTemplate.passwordHeading"/> <dspel:valueof param="password"/>.
</p>
</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/password_email.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/password_email.jsp#1 $$Change: 946917 $--%>
