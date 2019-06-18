<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/passwordChanged.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <dspel:importbean bean="/atg/svc/agent/ui/formhandlers/ChangePasswordFormHandler" var="changePasswordFormHandler" scope="request"/>
      <c:choose>
        <c:when test="${changePasswordFormHandler.success}">
          <caf:outputJavaScript>
            dijit.byId('messageBar').addMessage( {type:"information", summary:"<fmt:message key='message.password.changed.ok'/>"});
          </caf:outputJavaScript>
        </c:when>
        <c:otherwise>
          <caf:outputJavaScript>
            dijit.byId('messageBar').addMessage( {type:"error",
              summary:"<fmt:message key='message.password.changed.error'/> <c:out value='${changePasswordFormHandler.error}'/>"});
          </caf:outputJavaScript>
        </c:otherwise>
      </c:choose>
</dspel:layeredBundle>
</dspel:page>

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/passwordChanged.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/passwordChanged.jsp#1 $$Change: 946917 $--%>
