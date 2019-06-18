<%--
  General preferences.

  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/prefGeneralPanel.jsp#2 $ $Change: 1179550 $
  @updated $DateTime: 2015/07/10 11:58:13 $ $Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">


<dspel:importbean bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler" />
<dspel:importbean var="modManager" bean="/atg/svc/configuration/ModuleDependencyManager" />
<dspel:form style="display:none" action="#" id="restoreGeneralPanelForm" formid="restoreGeneralPanelForm">
  <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.restoreDefaultOptions"/>
  <dspel:input type="hidden" name="restoreOptions" value="TrylogOut,AgentUserDefaultHomeTab" bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler.restoreOptions"/>
</dspel:form>
<dspel:form action="${thisPage}" method="post" name="prefLoginForm"> 
  <dspel:input bean="AgentProfileFormHandler.loginErrorURL" type="hidden" value="${thisPage}"/>
  <dspel:input bean="AgentProfileFormHandler.loginSuccessURL" type="hidden" value="${thisPage}" />
  <div class="generalPanelRestoreButton">
        <fmt:message var="restore" key="option.input.user.restore" />
        <input type='button' id='generalPanelRestore' value='<c:out value="${restore}"/>' class="buttonSmall"
      onclick='prefGeneralPanelOk();' >
  </div><br/>
  <c:if test="${empty fName}">
    <fmt:message var="preferencesTitle" key="option.input.user.preferences"/>
  </c:if>
  <c:if test="${not empty fName}">
    <fmt:message var="preferencesTitle" key="option.input.user.preferencesWithName">
      <fmt:param value="${fName}"/>
    </fmt:message>
  </c:if>
  <div class="atg-csc-base-spacing-two-bottom" style="font-weight:bold;margin-left:10px"><c:out value="${preferencesTitle}"/></div>
  <div class="prefPanelContent atg-csc-base-table">
    <div class="oldPassword atg-csc-base-table-row">
      <label class="atg-csc-base-table-cell atg-base-table-preferences-first-label"><fmt:message key="option.input.user.password.old"/></label>
      <div class="atg-csc-base-table-cell">
        <input class="loginTxtField" maxlength="20" type="password" id="password"/>
      </div>
    </div>
    <div class="newPassword atg-csc-base-table-row">
      <label class="atg-csc-base-table-cell atg-base-table-preferences-first-label"><fmt:message key="option.input.user.password.new"/></label>
      <div class="atg-csc-base-table-cell">
        <input type="password" id="passwordNew"/>
      </div>

      <label class="atg-csc-base-table-cell atg-base-table-preferences-first-label"><fmt:message key="option.input.user.password.confirm"/></label>
      <div class="confirmPassword atg-csc-base-table-cell">
        <input type="password" id="passwordConfirm"/>
      </div>
    </div>
    <div class="atg-csc-base-table-row">
      <label class="atg-csc-base-table-cell atg-base-table-preferences-first-label"><fmt:message key="option.input.user.confirmation.prompts"/></label>
      <div class="confirmPrompts atg-csc-base-table-cell">
        <fmt:message var="label" key="option.input.user.askme.logout"/>
        <dspel:include src="/include/checkboxItem.jsp"  otherContext="${UIConfig.contextRoot}">
          <dspel:param name="option" value="TrylogOut"/>
          <dspel:param name="label" value="${label}"/>
        </dspel:include>
      </div>
    </div>

    <%-- AGENT USER HOME TAB  --%>

    <div class="defaultLogIn atg-csc-base-table-row">
      <label class="atg-csc-base-table-cell atg-base-table-preferences-first-label"><fmt:message key="option.input.user.logtab"/></label>
      <div class="atg-csc-base-table-cell">
        <c:set var="option" value="AgentUserDefaultHomeTab"/>
        <c:set var="tokenDelim" value=","/>
        <dspel:getvalueof var="selectedTabId" bean="UserOptionsFormHandler.options.${option}.value"/>
        <select id='sel<c:out value="${option}"/>' size='1' class='selectDefault'
            onchange='setOptionIndex("<c:out value='${option}'/>")'>
          <c:forEach items="${framework.frameworkInstance.tabIds}"
                     var="tabId"
                     varStatus="status">
            <fw-beans:tabDefinitionFindByTabId appId="workspace"
                                               tabId="${tabId}"
                                               var="tabDefinition"/>
            <c:set value=""
                   var="selected"/>
            <c:if test="${not empty selectedTabId and tabDefinition.tabId eq selectedTabId}">
              <c:set value="selected"
                     var="selected"/>            
            </c:if>
            <c:set var="strings" value="atg.svc.agent.WebAppResources" />
            <c:if test="${not empty tabDefinition.resourceBundle}">
              <c:set var="strings" value="${tabDefinition.resourceBundle}"/>
            </c:if>
            <dspel:layeredBundle basename="${strings}">
              <option value='<c:out value="${tabDefinition.tabId}"/>' <c:out value="${selected}"/>><fmt:message key="${tabDefinition.titleKey}"/></option>
            </dspel:layeredBundle>
          </c:forEach>
        </select>
        <dspel:input type="hidden" bean="UserOptionsFormHandler.options.${option}.value" 
          id="hid${option}"/>
      </div>
    </div>         
  </div>
</dspel:form> 
</dspel:layeredBundle>
</dspel:page>
<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/prefGeneralPanel.jsp#2 $$Change: 1179550 $$DateTime: 2015/07/10 11:58:13 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/prefGeneralPanel.jsp#2 $$Change: 1179550 $--%>
