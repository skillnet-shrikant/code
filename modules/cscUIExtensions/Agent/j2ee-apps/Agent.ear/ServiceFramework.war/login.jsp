<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Login page for Agent application.

  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/login.jsp#2 $$Change: 953229 $
  @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
  <dspel:importbean var="profileErrorMessageForEach" bean="/atg/dynamo/droplet/ErrorMessageForEach" />
  <dspel:importbean var="switch" bean="/atg/dynamo/droplet/Switch" />
  <dspel:importbean bean="/atg/svc/repository/service/StateHolderService" var="stateHolder" />

  <dspel:setvalue bean="AgentProfileFormHandler.value.password" value="" />

  <c:choose>
    <c:when test="${param.sessioninvalid and param.ppr}">
      <caf:outputStatus statusKey="sessioninvalid" redirectUrl="${UIConfig.contextRoot}/" />
    </c:when>
    <c:otherwise>
      <dspel:setLayeredBundle basename="atg.svc.agent.WebAppResources" />
      <html>
        <head>
          <link rel="icon" href="<c:url value='/image/favicon.ico'/>" type="image/x-icon" />
          <link rel="shortcut icon" href="<c:url value='/image/favicon.ico'/>" type="image/x-icon" />
          <link type="text/css" href="${cssPath}/workspace-sprite.css" rel="stylesheet" />
          <title><fmt:message key="appName" /></title>
          <script type="text/javascript">
            window.history.forward(1);
            function setFocus() {
              var username = document.getElementById("username");
              if(username.value == "" || username.value == null) {
                username.focus();
              } else {
               document.getElementById("password").focus();
              }
            }
          </script>
        </head>
        <body id="loginBody" onload="setFocus()">
          <div id="loginDiv">
            <div class="logoProduct">
              <span class="logoProductName"><fmt:message key="logo.product.name"/></span> <span class="logoProductTitle"><fmt:message key="logo.product.title"/></span>
              <br>
              <span class="logoProductVersion"><fmt:message key="logo.product.version"/></span>
            </div>
            <dspel:form action="${thisPage}" method="post" name="loginForm" id="loginForm" requiresSessionConfirmation="true">
              <input type="hidden" name="<c:out value='${stateHolder.windowIdParameterName}'/>" value="<c:out value='${windowId}'/>" />
              <dspel:input bean="AgentProfileFormHandler.loginErrorURL" type="hidden" value="${thisPage}?${stateHolder.windowIdParameterName}=${windowId}" />
              <dspel:input bean="AgentProfileFormHandler.loginSuccessURL" type="hidden" value="/agent/main.jsp?${stateHolder.windowIdParameterName}=${windowId}" />
              <fieldset>
                <dspel:droplet name="Switch">
                  <dspel:param bean="AgentProfileFormHandler.formError" name="value" />
                  <dspel:oparam name="true">
                    <fmt:message var="invalidLogin" key="login.invalidLogin" />
                    <fmt:message var="invalidPassword" key="login.invalidPassword" />
                    <p id="loginError">
                      <dspel:droplet name="ErrorMessageForEach">
                        <dspel:param name="messageTable" value="invalidLogin=${invalidLogin},invalidPassword=${invalidPassword}"/>
                        <dspel:param bean="AgentProfileFormHandler.formExceptions"
                         name="exceptions" />
                        <dspel:oparam name="output">
                          <c:set var="displayMessage">
                            <dspel:valueof param="message" converter="valueishtml" />
                          </c:set>
                          ${displayMessage}
                        </dspel:oparam>
                      </dspel:droplet>
                    </p>
                  </dspel:oparam>
                </dspel:droplet>
                <ul>
                  <li style="clear:both">
                    <label for="username">
                      <fmt:message key="login.username"/>:
                    </label>
                    <dspel:input bean="AgentProfileFormHandler.value.login" maxlength="40" id="username" type="text" autocomplete="off"/>
                  </li>
                  <li style="clear:both">
                    <label for="password">
                      <fmt:message key="login.password"/>:
                    </label>
                    <dspel:input bean="AgentProfileFormHandler.value.password" maxlength="40" id="password" type="password"/>
                  </li>
                </ul>
                <div>
                  <fmt:message var="loginButtonLabel" key="login.login" />
                  <dspel:input bean="AgentProfileFormHandler.login" type="SUBMIT" value="${loginButtonLabel}" id="loginFormSubmit" iclass="buttonSmall go" />
                </div>
              </fieldset>
            </dspel:form>
          </div>
        </body>
      </html>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/login.jsp#2 $$Change: 953229 $--%>
