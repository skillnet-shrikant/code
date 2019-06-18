<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/editor/plugins/spell/spellCheck.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

<%@ page import="atg.svc.agent.ui.formhandlers.SpellCheckerFormHandler" %>

<dspel:importbean var="editorActionFormHandler" bean="/atg/svc/agent/ui/formhandlers/EditorActionFormHandler"/>

<caf:outputXhtml targetId="spellCheckDiv">
  <c:forEach var="word" items="${editorActionFormHandler.spellSuggestions}" varStatus="loop">
    <div id="spellingError<c:out value='${loop}'/>">
      <div><c:out value='${word.key}'/></div>
      <c:forEach var="listSuggestion" items="${word.value}">
        <span><c:out value='${listSuggestion}'/></span>
      </c:forEach>
    </div>
  </c:forEach>
</caf:outputXhtml>

<caf:outputJavaScript>
  ResponseMarkupSpellingErrors();
</caf:outputJavaScript>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/editor/plugins/spell/spellCheck.jsp#1 $$Change: 946917 $--%>
