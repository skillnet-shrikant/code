<%@ include file="/include/top.jspf" %>

<dspel:page xml="true">
  <dspel:getvalueof var="tabId" param="tabId"/>
  <dspel:getvalueof var="tabInstance" param="tabInstance"/>
  <c:set value="${tabId}" var="tabId"/>
  <c:set value="${tabInstance}" var="tabInstance"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

      <c:if test="${not empty tabId}">
        <dspel:form formid="tabsForm" id="tabsForm" action="#">
          <dspel:input id="changeTab" type="hidden" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.changeTab"/>
          <dspel:input type="hidden" converter="map" value="" bean="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler.parameterMap"/>
        </dspel:form>
        <ul>
          <c:forEach items="${framework.frameworkInstance.tabIds}" var="currentTabId">
            <fw-beans:tabDefinitionFindByTabId appId="workspace"
                                                tabId="${currentTabId}"
                                                var="tabDefinition"/>

            <c:set value="atg.svc.agent.WebAppResources" var="strings"/>
            <c:if test="${not empty tabDefinition.resourceBundle}">
              <c:set var="strings" value="${tabDefinition.resourceBundle}"/>
            </c:if>
            <dspel:layeredBundle basename="${strings}">
              <c:set value="${tabDefinition.tabId}" var="tabElementId"/>

              <%-- Only render the chat tab if this is a chat window --%>
              <c:set var="renderThisTab" value="${true}"/>
              <c:set var="isChatTab" value="${tabElementId eq 'chatTab'}"/>
              <c:if test="${isChatTab and (not chatUiStatus.chatWindow)}">
                <c:set var="renderThisTab" value="${false}"/>
              </c:if>

              <c:if test="${renderThisTab}">
                <li id="${tabElementId}" class="${(tabId == currentTabId) ? 'current' : 'tab'}">
                </li>
              </c:if>
            </dspel:layeredBundle>
          </c:forEach>
        </ul>
      </c:if>

  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/templates/workspaceTabs.jsp#1 $$Change: 946917 $--%>
