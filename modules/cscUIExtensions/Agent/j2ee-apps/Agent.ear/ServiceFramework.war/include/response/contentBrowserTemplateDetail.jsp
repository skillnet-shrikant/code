<%--

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTemplateDetail.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <c:set var="template" value="${content.data}"/>

    <svc-agent:convertTextToHtml var="convertedTextBody" text="${template}"/>

    <h5><fmt:message key="contentBrowser.template.label"/> <c:out value="${content.name}"/></h5>
    <div class="scrollContent">
      <c:out value="${convertedTextBody}" escapeXml="false"/>
    </div>


      <%-- this will likely need to be escaped, as the '\'' character
      contained in the template will break this javascript --%>
      <span id="textToInsert" style="display:none">
      	<c:out value="${convertedTextBody}" escapeXml="true"/>
      </span>

      <a href="#" onclick="ResponseInsertAtCaret('textToInsert');return false;" class="flushRight buttonSmall go" title='<fmt:message key="contentBrowser.template.button"/>'><span><fmt:message key="contentBrowser.template.button"/></span></a>

      <div class="floatClear"></div>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/response/contentBrowserTemplateDetail.jsp#1 $$Change: 946917 $--%>
