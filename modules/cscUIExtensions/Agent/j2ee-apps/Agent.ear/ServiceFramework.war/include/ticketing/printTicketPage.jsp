<%--
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/printTicketPage.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <c:set var="ticketContentDiv" value="ticketResults"/>

  <dspel:importbean bean="/atg/svc/repository/service/StateHolderService"
                    scope="request"
                    var="stateHolder"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <html xmlns="http://www.w3.org/1999/xhtml">

      <head>
      <c:forEach var="styleSheet" items="${UIConfig.applicationStyleSheets}">
        <link type="text/css" href="${styleSheet}" rel="stylesheet" type="text/css"/>
      </c:forEach>
		<link type="text/css" media="print" href="<c:url value='/include/ticketing/print.css'/>" rel="stylesheet" />
	  <c:if test="${UIConfig.useDebugPanelStackMode}">
        <link type="text/css" href="css/workspace-sprite.css" rel="stylesheet" type="text/css"/>
      </c:if>
<script type="text/javascript">
  var djConfig = {
    <c:if test="${UIConfig.dojoDebug}">
    isDebug:true,
    popup:true,
    </c:if>
    usePlainJson: true,
    parseOnLoad: true,
    baseUrl:"<c:url context='/dojo-1' value='/dojo/'/>",
    usesApplets: false   // If the app uses an applet, asking styles need to be used
  };
</script>
<c:forEach var="resourceFile" items="${UIConfig.resourceScriptFiles}">
  <script type="text/javascript" src="${resourceFile}?${stateHolder.windowIdParameterName}=${windowId}&op=${op}"></script>
</c:forEach>
<script type="text/javascript" src="${UIConfig.contextRoot}/script/application.jsp"></script>

<script type="text/javascript">
  dojo.addOnLoad(function(){
    var links = document.body.getElementsByTagName("a");
    for (i = 0; i < links.length; i++) {
      var a = links[i];
      var p = a.parentNode;

      var s = document.createElement('SPAN');
      s.innerHTML = a.innerHTML;
      
      p.removeChild(a);
      p.appendChild(s);
    }
  });
</script>

        <script type="text/javascript">
          <c:out escapeXml="false" value="//<![CDATA["/> <%-- hide from websphere --%>
          var act         = null;
          window.cellIdParamName     = "<c:out value='${framework.cellIdParameterName}'/>";
          window.panelIdParamName    = "<c:out value='${framework.panelIdParameterName}'/>";
          window.windowId            = "<c:out value='${windowId}'/>";
          window.windowIdParamName   = "<c:out value='${stateHolder.windowIdParameterName}'/>";
          window.contextPath         = "<c:out value='${pageContext.request.contextPath}'/>";
          <c:out escapeXml="false" value="//]]>"/>
        </script>
        <script type="text/javascript" src="<c:out value='${UIConfig.contextRoot}/script/resources.js.jsp?${stateHolder.windowIdParameterName}=${windowId}'/>"></script>
        <script type="text/javascript" src="<c:out value='${CAFUIConfig.contextRoot}'/>/scripts/application.js"></script>
        <script type="text/javascript" src="<c:out value='${UIConfig.contextRoot}'/>/script/workspace.js"></script>
        <script type="text/javascript" src="<c:out value='${CAFUIConfig.contextRoot}'/>/scripts/application.js"></script>
        <script type="text/javascript" src="<c:out value='${UIConfig.contextRoot}'/>/script/hotfix.js"></script>
        <script type="text/javascript">
          <%-- this is defined in workspace.js but for some reason isn't found --%>
          <c:out escapeXml="false" value="//<![CDATA["/>
            function ticketActivityList(operation,parameters,state){
              var theForm = document.getElementById("altTicketActivityListForm");
              atgSetupTreeTable(theForm, operation, parameters, state);
              theForm.parameters.value=parameters;
              theForm.activityType.value=document.getElementById('altActivityFilterForm').altActivityTypeInput.value;
              atgSubmitAction({
                form: theForm,
                formHandler: "/atg/svc/ui/formhandlers/TicketActivityListFormHandler",
                dynamicIncludes:["include/ticketActivityListResults.jsp"]
              });
            }
          <%-- override FrameworkBodyResize to do nothing in this window --%>
          function FrameworkBodyResize() {}
          <c:out escapeXml="false" value="//]]>"/>
        </script>
        
        <title>
          <fmt:message key="nextSteps.printTicket.label"/>
        </title>
          
      </head>

      <body id="printTicket" style="overflow:auto;">
        <dspel:form action="#" id="altTicketActivityListForm" formid="altTicketActivityListForm">
          <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.list"/>
          <dspel:input type="hidden" name="operation" value="refresh" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.operation"/>
          <dspel:input type="hidden" name="treeTableId" value="mainTicketActivityTable" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.treeTableId"/>
          <dspel:input type="hidden" name="parameters" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.parameters"/>
          <dspel:input type="hidden" name="state" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.state"/>
          <dspel:input type="hidden" name="activityType" value="" bean="/atg/svc/ui/formhandlers/MainTicketActivityListFormHandler.parameterMap.activityType"/>
        </dspel:form>
        <form>
          <input class="dontPrintMe" type="button" value="<fmt:message key="nextSteps.printTicket.label"/>" onclick="window.print();"/>
        </form>
      
        <c:set var="isPrinting" value="true" scope="request"/>
        <div> 
          <div class="panelHeader">
            <fmt:message key="panel.ticketSummaryPanel.label"/>
          </div>
          <dspel:include src="/panels/ticketSummaryPanel.jsp" otherContext="${UIConfig.contextRoot}"/>
          <hr />
          <div class="panelHeader">
            <fmt:message key="panel.ticketCustomerInformationPanel.label"/>
          </div>
          <dspel:include src="/panels/ticketCustomerInformationPanel.jsp" otherContext="${UIConfig.contextRoot}"/>
          <hr />
          <div class="panelHeader">
            <fmt:message key="panel.ticketActivityPanel.label"/>
          </div>
          <dspel:include src="/include/baseTicketActivityListResultsNoTreeTable.jsp" otherContext="${UIConfig.contextRoot}"/>
        </div>
      </body>
    </html>

  </dspel:layeredBundle>
</dspel:page>


<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/printTicketPage.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketing/printTicketPage.jsp#1 $$Change: 946917 $--%>
