<!-- ticketSummaryPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummaryPanel.jsp#2 $$Change: 953229 $
    @updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">

  <dspel:importbean var="ticketingFormHandler" bean="/atg/svc/ui/formhandlers/TicketingFormHandler"/>
  <dspel:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dspel:importbean bean="/atg/commerce/custsvc/multisite/IsRealmAccessibleDroplet"/>
  <c:set var="values" value="${ticketingFormHandler.viewTicket}"/>
  <dspel:form action="#" id="addViewTicketNoteForm" formid="addViewTicketNoteForm">
    <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.addNote"/>
    <dspel:input type="hidden" name="noteText" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.noteText"/>
    <dspel:input type="hidden" name="noteType" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.noteType"/>
    <dspel:input type="hidden" name="inbound" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap.inbound"/>
    <dspel:input type="hidden" name="share" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.share"/>
    <dspel:input type="hidden" name="viewTicketNote" value="" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicketNote"/>
  </dspel:form>
  <dspel:form style="display:none" action="#" id="ticketSummaryViewTicketForm" formid="ticketSummaryViewTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.viewTicket"/>
    <dspel:input type="hidden" value="" name="ticketId" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.ticketId"/>
  </dspel:form>
  <dspel:form style="display:none" action="#" id="beginEditTicketForm" formid="beginEditTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.beginEditTicket"/>
  </dspel:form>
  <dspel:form style="display:none" action="#" id="endEditTicketForm" formid="endEditTicketForm">
    <dspel:input type="hidden" value="" priority="-10" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.endEditTicket"/>
    <dspel:input type="hidden" value="" converter="map" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.parameterMap"/>
    <dspel:input type="hidden" value="" id="description" bean="/atg/svc/ui/formhandlers/TicketingFormHandler.description"/>
  </dspel:form>
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    
    <ul class="atg_svc_panelToolBar">
      <li class="atg_svc_header">
        <fmt:message key="id-label"/>
        <dspel:getvalueof var="transient" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket.transient"/>
        <c:if test="${transient}">
          <fmt:message key="new-ticket"/>
        </c:if>
        <c:if test="${not transient}">
          <c:out value="${values.id}"/>
        </c:if>
      </li>
      <dspel:getvalueof var="currentTicket" bean="/atg/svc/ticketing/TicketHolder.currentTicket.id"/>
      <dspel:getvalueof var="currentTicketIsEditing" bean="/atg/svc/ticketing/TicketHolder.editing"/> 
      <dspel:getvalueof var="viewTicket" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket.id"/>
      <dspel:getvalueof var="currentIsMergeable" bean="/atg/svc/ticketing/TicketHolder.currentTicket.mergeable"/>
 
      <c:if test="${!isPrinting && (currentTicket == viewTicket)}"> 

        <li class="atg_svc_last">
          <fmt:message var="associateTicket" key="associate-ticket-title"/>
          <c:choose>
            <c:when test="${transient}">
              <span title="${associateTicket}">
    	          <c:out value="${associateTicket}"/>
    	        </span>
            </c:when>
            <c:otherwise>
              <a href="#" onclick="atg.service.ticketing.associateTicketPrompt();" title="${associateTicket}">
                <c:out value="${associateTicket}"/>
              </a> 
            </c:otherwise>
          </c:choose>
        </li>
        <c:if test="${currentTicketIsMergeable}">
          <li>
            <fmt:message var="mergeTicket" key="merge-ticket"/>
            <a href="#" onclick="atg.service.ticketing.mergeTicketPrompt();" title="${mergeTicket}">
              <c:out value="${mergeTicket}"/>
            </a>
          </li>
        </c:if>
        <li>
          <fmt:message var="closeDuplicate" key="close-as-duplicate-title"/>
          <a href="#" onclick="atg.service.ticketing.closeAsDuplicateTicketPrompt();" title="${closeDuplicate}">            
            <c:out value="${closeDuplicate}"/>
          </a>
        </li>

          <c:if test="${(viewTicket == currentTicket) && currentTicketIsEditing}">
            <fmt:message var="saveTicket" key="save-changes-title"/>
            <li>
              <a href="#" onclick="endEditTicket();return false;" title="${saveTicket}">
                <c:out value="${saveTicket}"/>
              </a>
            </li>
          </c:if>
          
          <c:if test="${(viewTicket == currentTicket) && !currentTicketIsEditing}">
            <li>
              <fmt:message var="editTicketTitle" key="edit-ticket-title"/>  
              <a href="#" onclick="beginEditTicket();return false;" title="${editTicketTitle}">
                <c:out value="${editTicketTitle}"/>
              </a>
            </li>
          </c:if>
        </c:if>
    </ul>


    <div id="atg_service_ticketing_ticketSummary_headSubPanel" class="bgLightGrayBorder atg-csc-base-table">
      <div class="atg-csc-base-spacing-two-left">
        <div class="atg-csc-base-table-row">
          <div class="atg-csc-base-table-cell"><fmt:message key="source-label"/></div><div class="atg-csc-base-table-cell atg-csc-base-spacing-one-left atg-csc-ticket-table-cell-width"><c:out value="${values.application}"/><c:if test="${values.externalTicketId != null}"><c:out value=" (${values.externalTicketId})"/></c:if></div>
          <div class="atg-csc-base-table-cell"><fmt:message key="assigned-to-label"/></div>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-one-left atg-csc-ticket-table-cell-width"><c:out value="${ticketingFormHandler.viewAssignedAgent}"/>
            <c:if test="${values.hasPendingOwnership == true && empty values.pendingTime}">
              &nbsp;<fmt:message key="ownership-pending"/>
            </c:if>
            <c:if test="${values.hasPendingOwnership == true && not empty values.pendingTime}">
              &nbsp;
              <fmt:message key="ownership-pending-until">
                <fmt:param>
                  <fmt:formatDate value="${values.pendingTime}" type="both" dateStyle="short" timeStyle="short"/>
                </fmt:param>
              </fmt:message>
            </c:if>
          </div>
          <div class="atg-csc-base-table-cell"><fmt:message key="escalation-level"/></div><div class="atg-csc-base-table-cell atg-csc-base-spacing-one-left"><fmt:message key="${values.escalationLevel}"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="atg-csc-base-table-cell"><fmt:message key="created-label"/></div><div class="atg-csc-base-table-cell atg-csc-base-spacing-one-left atg-csc-ticket-table-cell-width"><fmt:formatDate value="${values.creationTime}" type="both" dateStyle="medium" timeStyle="short"/></div>
          <div class="atg-csc-base-table-cell"><fmt:message key="lastmodified-label"/></div><div class="atg-csc-base-table-cell atg-csc-base-spacing-one-left atg-csc-ticket-table-cell-width"><fmt:formatDate value="${values.lastModified}" type="both" dateStyle="medium" timeStyle="short"/></div>
        </div>
      </div>
    </div>
    <c:if test="${(viewTicket == currentTicket) && currentTicketIsEditing && !isPrinting }">
      <dspel:include src="/panels/ticketSummaryEditable.jsp" otherContext="${UIConfig.contextRoot}"/>
    </c:if>
    <c:if test="${isPrinting || (viewTicket != currentTicket) || ((viewTicket == currentTicket) && !currentTicketIsEditing)}">
      <dspel:include src="/panels/ticketSummary.jsp" otherContext="${UIConfig.contextRoot}"/>
    </c:if>
    <hr />
    <c:if test="${!isPrinting}">
    <h5 class="trigger">
      <a name="Tickets" class="atg_svc_ticketInfoToggle" id="Tickets" href="#" onclick="toggle('associatedTicketsDiv', 'associatedTicketsArrow');return false;">
        <dspel:img id="associatedTicketsArrow" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><span><fmt:message key="associated-tickets-label"/></span>
      </a>
    </h5> 
    <div id="associatedTicketsDiv" class="datatable" style="display: none;">
    </c:if>
    <c:if test="${isPrinting}">
    <h5 class="trigger">
      <fmt:message key="associated-tickets-label"/>
    </h5> 
    <div id="associatedTicketsDiv" class="datatable">
    </c:if>
      <c:if test="${values.relatedTickets != null && not empty values.relatedTickets}">
        <table class="data tickets">
          <tr>
            <th><fmt:message key="type"/></th><th><fmt:message key="description"/></th><th><fmt:message key="age"/></th><th><fmt:message key="claimed"/></th><th><fmt:message key="status"/></th><th><fmt:message key="created"/></th>
          </tr>
<c:forEach var="associatedTicketItem" items="${values.relatedTickets}" varStatus="index">
  <dspel:tomap var="associatedTicket" value="${associatedTicketItem}"/>
  <c:if test="${index.index % 2 == 1}">
          <tr class="bgWhite">
  </c:if>
  <c:if test="${index.index % 2 == 0}">
          <tr class="bgBlue">
</c:if>
            <td>
  <c:choose>
		<c:when test='${associatedTicket.creationChannel == "web"}'>
		  <%-- Currently we do not have an image for web. Replace with correct image later. --%>
		  <fmt:message var="selfServiceEsc" key="tooltip.self-service-esc" />
		</c:when>
		<c:when test='${associatedTicket.creationChannel == "email"}'>
		  <fmt:message var="email" key="tooltip.email" />
		</c:when>
		<c:when test='${associatedTicket.creationChannel == "telephony"}'>
		  <fmt:message var="phoneCall" key="tooltip.phonecall" />
		</c:when>
		<c:when test='${associatedTicket.creationChannel == "SMS"}'>
		  <fmt:message var="sms" key="tooltip.sms" />
		</c:when>
		<c:when test='${associatedTicket.creationChannel == "MMS"}'>
		  <fmt:message var="mms" key="tooltip.mms" />
		</c:when>
		<c:otherwise>
		  <%-- Currently we do not have an image for unknown. Replace with correct image later. --%>
			<fmt:message var="custChannel" key="tooltip.customer-channel" />
		</c:otherwise>
              </c:choose>
              <!-- Only allow access to associated tickets which are in realms that agent can access -->
              <c:choose>
                <c:when test ="${envTools.siteAccessControlOn == 'true' }">
                  <dspel:droplet name="IsRealmAccessibleDroplet">
                    <dspel:param name="realmId" value="${associatedTicket.realmId}"/>
                      <dspel:oparam name="true">
                        &nbsp;
                          <a href="#" onclick="document.getElementById('ticketSummaryViewTicketForm').ticketId.value=<c:out value='${associatedTicket.id}'/>;viewTicket('ticketSummaryViewTicketForm');" class="blueU"><c:out value="${associatedTicket.id}"/></a>
                        &nbsp;
                      </dspel:oparam>
                      <dspel:oparam name="false">
                        &nbsp;
                          <c:out value="${associatedTicket.id}"/>
                        &nbsp;  
                      </dspel:oparam>
                    </dspel:droplet>
                  </c:when>
                <c:otherwise>
                  &nbsp;
                    <a href="#" onclick="document.getElementById('ticketSummaryViewTicketForm').ticketId.value=<c:out value='${associatedTicket.id}'/>;viewTicket('ticketSummaryViewTicketForm');" class="blueU"><c:out value="${associatedTicket.id}"/></a>
                  &nbsp;
                </c:otherwise>
              </c:choose>
            </td>
            <td><c:out escapeXml="false" value="${associatedTicket.description}"/></td>
            <td>
              <span class="textLeft_iconRight"><c:out value="${associatedTicket.ageInDays}"/><fmt:message key="day-char"/><c:out value="${associatedTicket.ageInHours - (associatedTicket.ageInDays * 24)}"/><fmt:message key="hour-char"/></span>
            </td>
            <td class="center">
<c:if test="${associatedTicket.agentAssignmentActivity != null}">
              <img src="<c:out value='${imageLocation}/iconcatalog/21x21/table_icons/icon_status_assigned.gif'/>" width="19" height="19" title="<fmt:message key='tooltip.claim-staus-assigned'/>" align="absmiddle"/>
</c:if>
<c:if test="${associatedTicket.agentAssignmentActivity == null}">
              <img src="<c:out value='${imageLocation}/iconcatalog/21x21/table_icons/icon_status_available.gif'/>" width="19" height="19" title="<fmt:message key='tooltip.claim-staus-available'/>" align="absmiddle"/>
</c:if>
            </td>
            <td>
              <dspel:tomap var="status" value="${associatedTicket.subStatus}"/>
              <dspel:droplet name="/atg/ticketing/TicketStatusDescription">
              <dspel:param name="descriptionId" value="${status.subStatusName}"/>
              <dspel:param name="baseName" value="SUBSTATUS"/>
              <dspel:param name="elementName" value="description"/>
              <dspel:oparam name="output">
                <dspel:getvalueof var="description" param="description"/>
                <c:out value="${description}"/>
              </dspel:oparam>
            </dspel:droplet>
            </td>
            <td>
              <span class="textLeft_iconRight"><fmt:formatDate value="${associatedTicket.creationTime}" type="both" dateStyle="short" timeStyle="short"/></span>
            </td>
          </tr>
</c:forEach>  
        </table>
      </c:if>
      <c:if test="${values.relatedTickets == null || empty values.relatedTickets}">
        <fmt:message key="no-tickets"/>
      </c:if>
    </div>
    <c:if test="${!isPrinting}">
    <h5 class="trigger">
      <a name="Notes" class="atg_svc_ticketInfoToggle" id="Notes" href="#" onclick="toggle('ticketNotesDiv', 'ticketNotesArrow');return false;"><dspel:img id="ticketNotesArrow" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><span><fmt:message key="ticket-notes-label"/></span></a></h5> 
    <div id="ticketNotesDiv" style="display: none;">
      <c:if test="${(viewTicket == currentTicket)}">
      <form name="ticketNotes" id="ticketNotes">
        <table width="100%"  border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="left">
              <input name="isPublic" id="ticketIsPublic" type="checkbox" value="checkbox" checked="checked" /> 
              <fmt:message key="share-with-customer-label"/>                            
            </td>
          </tr>
          <tr>
            <td>
              <textarea name="ticketNoteText" id="ticketNoteTextId" class="border w98p"></textarea>
            </td>
          </tr>
          <tr>
            <td class="left">
              <a class="buttonSmall go"
                 href="#"
                 onclick="atg.service.ticketing.addViewTicketNote(dojo.byId('ticketNoteTextId').value,dojo.byId('ticketIsPublic').checked,'addViewTicketNoteForm');"
                 title="<fmt:message key='add-label'/>">
                <span><fmt:message key="add-label"/></span>
              </a>
            </td>
          </tr>
        </table>
      </form>
      </c:if>
    </c:if>
    <c:if test="${isPrinting}">
    <h5 class="trigger"><fmt:message key="ticket-notes-label"/></h5>
    <div id="ticketNotesDiv">
    </c:if>    
        <table width="100%" class="data">
<c:forEach items="${ticketingFormHandler.viewNotes}" var="note" varStatus="counter">
<c:if test="${counter.index % 2 == 1}">
            <tr class="row">
</c:if>
<c:if test="${counter.index % 2 == 0}">
            <tr class="alternateRow">
</c:if>
              <td>
                <span class="bold">
                  <fmt:formatDate value="${note.creationTime}" type="both" dateStyle="short" timeStyle="short"/> - <c:out value="${note.agentProfile.login}"/>
                </span>
                <br />
                <c:out value="${note.heading}"/>
              </td>
            </tr>
</c:forEach>
        </table>
    </div>
  </dspel:layeredBundle>
</dspel:page>
<!-- end ticketSummaryPanel.jsp -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketSummaryPanel.jsp#2 $$Change: 953229 $--%>
