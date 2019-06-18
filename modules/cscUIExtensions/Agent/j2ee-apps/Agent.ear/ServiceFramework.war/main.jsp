<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%--
The first file for loading the entire Agent GUI.
@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/main.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
<dspel:page xml="true">
<dspel:demarcateTransaction var="demarcateXA">
  <%@ include file="/include/hasServiceRole.jspf" %>
  <c:if test="${!hasRole}">
    <c:redirect url="accessDenied.jsp" />
  </c:if>

  <dspel:importbean bean="/atg/svc/repository/service/StateHolderService"
                    scope="request"
                    var="stateHolder"/>
  <dspel:importbean bean="/atg/svc/ui/formhandlers/UserOptionsFormHandler" />
  <dspel:getvalueof id="op" param="op" />
  <dspel:getvalueof id="ticketId" param="ticketId" />
  <dspel:importbean bean="/atg/svc/agent/customer/CustomerPanelConfig" var="customerPanelConfig"/>

  <svc-agent:setCRMAgentParams/>

  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <%@ include file="/include/head.jspf" %>
      </head>
      <body class="atg">
        <div id="splashScreen"><div id="splashImage"></div></div>

        <%-- do this first, other things need the MapTagConverter that this registers --%>
        <dspel:form action="#"
                    formid="dummyTicketingForm"
                    id="dummyTicketingForm"
                    method="post"
                    name="dummyTicketingForm"
                    style="display:none">
          <span>
            <dspel:input type="submit" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket" />
          </span>
        </dspel:form>

        <%-- Keyboard Navigation Notification Window --%>
        <div dojoType="dojox.widget.Toaster" id="keyboardNavNotificationObject" messageTopic="keyboardNavNotificationTopic" showDelay="2000" clipCssClass="atg_keyboard_notificationClip" containerCssClass="atg_keyboard_notificationContainer" contentCssClass="atg_keyboard_notificationContent" messageCssClass="atg_keyboard_notificationMessage" positionDirection="br-left"></div>

        <%-- Keyboard Navigation Help Dialog --%>
        <div id="atgKeyboardDialogContainer" style="display:none;">
          <div dojoType="dojox.Dialog" id="keyboardNavHelpObject" duration="100">
            <div class="atg_keyboard_helpDialogTitle"><fmt:message key="keyboard.popup.title"/></div>
            <div class="atg_keyboard_helpDialogContainer" id="atg_keyboard_keyboardNavHelpWindowDiv"></div>
            <div class="atg_keyboard_helpDialogCloseButton"><input type="button" id="keyboardNavigationHider" value='<fmt:message key="keyboard.popup.close"/>' onclick="dijit.byId('keyboardNavHelpObject').hide();"/></div>
          </div>
        </div>

        <%-- Loading Dialog Notification Window --%>
        <div id="pageLoadingNotificationContainer">
          <div dojoType="dojox.widget.Toaster" id="pageLoadingNotificationObject" clipCssClass="atg_keyboard_notificationClip" containerCssClass="atg_keyboard_notificationContainer" contentCssClass="atg_keyboard_notificationContent" messageCssClass="atg_keyboard_notificationMessage" positionDirection="tr-left" duration="0"></div>
        </div>
        
        <%-- Loading Dialog Opaque Background --%>
        <iframe id="opaqueBackground" class="opaqueBackground" style="margin:0px;width:100%;height:100%;background-color:#EFEFEF;opacity:.5;filter: alpha(opacity=50);-moz-opacity: .5;z-index:100;position:absolute;top:0px;left:0px;display:none;" src="empty.htm" scrolling="no" frameborder="0"></iframe>

        <div id="atgLoadingDialogContainer" style="display:none;">

          <div dojoType="atg.widget.simpledialog.SimpleDialog" id="atgLoadingDialogWidget"
               bgColor="white" duration="1">
            <div class="loadingInfo">
              <div class="loadingIcon"></div>
              <fmt:message key="text.loading"/><fmt:message key="text.ellipsis"/>
            </div>
          </div>
        </div>

        <div id="atgSessionTimeoutDialogContainer" style="display:none;">
          <div dojoType="dojox.Dialog" id="atgSessionTimeoutDialog" bgColor="white" bgOpacity="0.01" timerNode="atgSessionTimerNode" duration="100">
            <div>
              <fmt:message key="sessionTimeOut.label"/>
            </div>
            <div class="atgSessionTimeoutDialogButton"><input type="button" value="<fmt:message key='continueWorking.label' />" onclick="atgSubmitAction({form: dojo.byId('transformForm')});dijit.byId('atgSessionTimeoutDialog').hide();" /></div>
          </div>
        </div>

        <caf:curtain curtainId="mainCurtain" />

        <%-- iframe shim to allow popups to go over top of selects in IE --%>
        <iframe id="divShim" src="empty.htm" scrolling="no" frameborder="0" style="position:absolute; top:0px; left:0px; display:none;"></iframe>

        <%-- Include action definitions --%>
        <c:set var="debugElementId" scope="request" value=""/>
        <%-- Error page --%>
        <c:set var="errorUrl" scope="request" value="${UIConfig.contextRoot}/error.jsp?${stateHolder.windowIdParameterName}=${windowId}"/>
        <%-- Framework form handler bean --%>
        <c:set var="frameworkFormHandlerBean" scope="request" value="/atg/svc/ui/formhandlers/FrameworkBaseFormHandler"/>
        <%-- Framework success URL --%>
        <c:set var="frameworkSuccessUrl" scope="request" value="framework.jsp"/>
        
        
        <%-- Logout layer --%>
        <dspel:include src="/include/logout.jsp" otherContext="${UIConfig.contextRoot}"/>

        <%-- Ticketing --%>
        <div id="ticketPromptPane"
             dojoType="dojox.Dialog"
             titleBarDisplay="false"
             scriptHasHooks="true"
             cacheContent="true"
             executeScripts="true"
             adjustPaths="false"
             duration="1"
             extractContent="false"></div>

        <%-- Agent presentation layer framework --%>
        <fw-beans:frameworkDefinitionFindByFrameworkId appId="workspace"
                                                        frameworkId="workspaceFramework"
                                                        var="workspaceFramework"/>

        <c:choose>
        <c:when test="${!empty workspaceFramework.templates.layoutTemplate.otherContext}">
          <dspel:include src="${workspaceFramework.templates.layoutTemplate.url}" 
                         otherContext="${workspaceFramework.templates.layoutTemplate.otherContext}" />
        </c:when>
        <c:otherwise>
          <dspel:include src="${workspaceFramework.templates.layoutTemplate.url}" />
        </c:otherwise>
        </c:choose>

      <div id="dataArea" style="display: none;">
        <dspel:include src="/include/frameworkForms.jsp" otherContext="${UIConfig.contextRoot}">
          <dspel:param name="op" value="${op}"/>
          <dspel:param name="ticketId" value="${ticketId}"/>
        </dspel:include>
        <dspel:include src="/include/viewForms.jsp" otherContext="${UIConfig.contextRoot}"/>

        <dspel:getvalueof var="globalIncludes" bean="/atg/svc/agent/ui/AgentUIConfiguration.globalPageIncludes" />
        <c:if test="${fn:length(globalIncludes) > 0}">
          <c:forEach var="globalPageInclude" items="${globalIncludes}">
            <dspel:include src="${globalPageInclude.URL}" otherContext="${globalPageInclude.servletContext}">
            </dspel:include>
          </c:forEach>
        </c:if>
      </div>
      
      <%-- 
          Get any custom page inclusions
          These inclusions may be from other contexts, so they will be of the form
          [context:]url
          So, split, test for context prefix and use accordingly.  
      --%>
      <c:set var="customIncludes" scope="request" value="${UIConfig.customIncludes}"/>
      <c:if test="${fn:length(customIncludes) > 0}">
        <c:forEach var="customInclude" items="${customIncludes}">
          <c:choose>
            <c:when test="${fn:contains(customInclude,':')}">
                <c:set var="cstmArr" value="${fn:split(customInclude,':')}"/>
                <c:set var="cstmCtx" scope="request" value="${cstmArr[0]}"/>
                <c:set var="cstmUrl" scope="request" value="${cstmArr[1]}"/>
            </c:when>
            <c:otherwise>
                <c:set var="cstmUrl" scope="request" value="${customInclude}"/>
                <c:set var="cstmCtx" scope="request" value=""/>
            </c:otherwise>
          </c:choose>
          <dspel:include src="${cstmUrl}" otherContext="${cstmCtx}">
          </dspel:include>
        </c:forEach>
      </c:if>

	  <div dojoType="dojo.data.ItemFileReadStore" jsId="stateStore"
		url="${customerPanelConfig.stateDataUrl}?countryCode=US"></div>
	
      <script type="text/javascript">
        var atgPageLoadingNotificationMessage = "<img src='images/atg_loading_icon.gif'><br><span class='atg_keyboard_notificationMessageHeading'>" + "<fmt:message key='pageload.notification.title'/>" + "</span><br /><span class='atg_keyboard_notificationMessageSubHeading'>" + "<fmt:message key='pageload.notification.message'/>" + "</span>";

        var hideLoader = function() {
          dojo.style("splashScreen", "display", "none");
        }
        var pageLinksInit = function() {
          dojo.subscribe("CloseHelpDialog", null, function() { dojo.byId("keyboardNavigationHider").onclick(); });
        }
        var initTab = function() {
          <%-- Handle CRM integration --%>
          computeTab('${op}', '${ticketId}');
          atg.service.framework.isResearchBarOpen = ${framework.cellInstances.researchColumn.cellOpenYn};
          atg.service.framework.isSideBarOpen = ${framework.cellInstances.sidebarColumn.cellOpenYn};
          atg.service.framework.showResearch = false;
          if (("${framework.windowTab}" == "contributeTab") && atg.service.framework.researchColumnAccess) {
            atg.service.framework.showResearch = true;
          }
        }
        <dspel:droplet name="/atg/svc/security/droplet/HasAccessRight">
          <dspel:param name="accessRight" value="ticketsTab"/>
          <dspel:oparam name="accessGranted">
            <dspel:importbean scope="request"
              var="activeTicketsSearchFormHandler"
              bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler" />
            <c:if test="${activeTicketsSearchFormHandler.pollInterval > 0}">
              var startTicketPolling = function(){
                if (!window.ticketPoll){
                  window.ticketPoll=setInterval("activeTicketsSearch('refresh', null, null, true);agentSearchTicket('refresh',null,null,true);", <c:out value="${activeTicketsSearchFormHandler.pollInterval}"/>);
                }
              }
            </c:if>
          </dspel:oparam>
        </dspel:droplet>

        window.currentAction = null;
        <c:choose>
        <c:when test="${UIConfig.dojoTimeoutSeconds > 0}">
          window.atgXhrTimeout = ${UIConfig.dojoTimeoutSeconds * 1000};  // set to specific interval
        </c:when>
        <c:when test="${UIConfig.dojoDebug}">
          window.atgXhrTimeout = 0;  // make it "infinite" to help debugging
        </c:when>
        <c:otherwise>
          window.atgXhrTimeout = 30000;
        </c:otherwise>
        </c:choose>
        window.cellIdParamName    = "${framework.cellIdParameterName}";
        window.panelIdParamName   = "${framework.panelIdParameterName}";
        window.windowId           = "${param[stateHolder.windowIdParameterName]}";
        window.windowIdParamName  = "${stateHolder.windowIdParameterName}";
        window.contextPath        = "${pageContext.request.contextPath}";
        window.sessionid          = dojo.cookie.isSupported() ? "" : ";jsessionid=" + "${pageContext.session.id}";
        window.nextStepsMenuId    = {};
        window.confirmLogout      = true;
        window.requestid          = "${param._requestid}";
        <c:catch>
          <dspel:getvalueof var="confirmLogout" bean="UserOptionsFormHandler.options.TrylogOut.value"/>
          <c:if test="${not empty confirmLogout}">
            window.confirmLogout  = ("${confirmLogout}" == "true");
          </c:if>
        </c:catch>
        <dspel:droplet name="/atg/svc/security/droplet/HasAccessRight">
          <dspel:param name="accessRight" value="researchColumn"/>
          <dspel:oparam name="accessDenied">
            atg.service.framework.researchColumnAccess = false;
          </dspel:oparam>
          <dspel:oparam name="accessGranted">
            atg.service.framework.researchColumnAccess = true;
          </dspel:oparam>
        </dspel:droplet>
        atg.service.framework.preSessionExpiryWarningSeconds = (${pageContext.session.maxInactiveInterval} > ${UIConfig.preSessionExpiryWarningSeconds}) ? ${UIConfig.preSessionExpiryWarningSeconds} : Math.floor(${pageContext.session.maxInactiveInterval} / 2);
        atg.service.framework.sessionTimeout = (${pageContext.session.maxInactiveInterval} - atg.service.framework.preSessionExpiryWarningSeconds) * 1000;
        atg.service.framework.myLastRequestTime = "";

        dojo.addOnLoad(function () {
          frameworkSetColumnStyles();
          hideLoader();
          pageLinksInit();
          //dojo.connect(window, "onload", atgInitLoadingDialog);
          atgInitLoadingDialog();
          dojo.connect(window, 'onresize', dojo.hitch(dijit.byId("wholeWindow"), 'resize'));
          dojo.connect(window, 'onresize', dojo.hitch(dijit.byId("wholeWindow"), 'resize'));
          atg.service.openWindow();
          <dspel:droplet name="/atg/svc/security/droplet/HasAccessRight">
            <dspel:param name="accessRight" value="ticketsTab"/>
            <dspel:oparam name="accessGranted">
              <dspel:importbean scope="request"
                  var="activeTicketsSearchFormHandler"
                  bean="/atg/svc/ui/formhandlers/SearchAgentTicketsFormHandler" />
              <c:if test="${activeTicketsSearchFormHandler.pollInterval > 0}">
                startTicketPolling();
              </c:if>
            </dspel:oparam>
          </dspel:droplet>
          atg.service.framework.initSlider();
          if (atg.service.framework.sessionTimeout > 0) {
            atg.service.framework.sessionExpiryTimer = setTimeout(atg.service.framework.sessionExpiryPromptFunction, atg.service.framework.sessionTimeout);
          }
          dojo.connect(dojo, "xhrPost", atg.service.framework.bindListener);
          atg.keyboard.init();
          showGlobalAlerts();
        });
        function showGlobalAlerts(){
        	 <dspel:droplet name="/com/mff/globalmessages/GetGlobalMessagesDroplet">
             <dspel:param name="msgDestination" value="CSC"/>
	             <dspel:oparam name="output">
	             <dspel:getvalueof var="globalAlertList" param="globalMessages.alerts"/>
	             <dspel:getvalueof var="globalMsgsList" param="globalMessages.messages"/>
	             	console.log('Global Messages : ${globalAlertList}');
	             	 console.log('${globalMsgsList}');
			        	dijit.byId('messageBar').addMessage({type:"warning", summary:"Global Message", details:[
			        		<c:choose>
				    	        <c:when test ="${not empty globalMsgsList}">
					    	        <c:forEach var="globalMsg" items="${globalMsgsList}" varStatus="msgCounter">
					    				{description: "${globalMsg.messageStartDate}: ${globalMsg.messageText}"}
						    			<c:if test ="${msgCounter.index < globalMsgsList.size()}">
						    				,
						    			</c:if>
				    				</c:forEach>
				    	        </c:when>
				    	        <c:otherwise>
				    	        	{description: "There currently aren't any messages"}
					    		</c:otherwise>
						    </c:choose>		
			    		]});
			        	dijit.byId('messageBar').addMessage({type:"error", summary:"Global Alert", details:[
			        		 <c:choose>
			        	        <c:when test ="${not empty globalAlertList}">
				        	        <c:forEach var="globalAlert" items="${globalAlertList}" varStatus="alertCounter">
					        			{description: "${globalAlert.messageStartDate}: ${globalAlert.messageText}"}
					        			<c:if test ="${alertCounter.index < globalAlertList.size()}">
					        				,
					        			</c:if>
				        			</c:forEach>
			        	        </c:when>
			        	        <c:otherwise>
			        	        	{description: "There currently aren't any messages"}
				        		</c:otherwise>
				        	</c:choose>	
			        		]});
			        	
		         </dspel:oparam>
	             <dspel:oparam name="empty">
	             	 <c:set var="globalAlertList" scope="request" value=""/>
	             	 <c:set var="globalMsgsList" scope="request" value=""/>
	             		dijit.byId('messageBar').addMessage({type:"warning", summary:"Global Message", details:[{description: "There currently aren't any messages"}]});
	             		dijit.byId('messageBar').addMessage({type:"error", summary:"Global Alert", details:[{description: "There currently aren't any messages"}]});
	             </dspel:oparam>
	      	 </dspel:droplet>	
       	
        }
        
        </script>

        <script type="text/javascript">
          window.onbeforeunload = function()  { 
              if (window.sessioninvalid != true) {
                  return "<fmt:message key='systemMessage.resetSession'/>";
                }
          }
        </script>
        
      </body>
    </html>
  </dspel:layeredBundle>
</dspel:demarcateTransaction>
</dspel:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="Caught exception in main.jsp: ${exception}" /><br />
  <%
    Throwable error = (Throwable)pageContext.getAttribute("exception");
    String stack = atg.core.exception.StackTraceUtils.getStackTrace(error, 10, 10);
    stack = atg.core.util.StringUtils.replace(stack, '\n', "<br/>");
    stack = atg.core.util.StringUtils.replace(stack, '\t', "&nbsp;&nbsp;&nbsp;");
    out.println(stack);
  %>
</c:if>
<!-- main.jsp -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/main.jsp#1 $$Change: 946917 $--%>
