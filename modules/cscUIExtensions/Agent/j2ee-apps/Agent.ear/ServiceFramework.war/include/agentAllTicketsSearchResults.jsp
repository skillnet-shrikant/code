<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

  <dspel:importbean scope="request"
                    var="agentAllTicketsSearchFormHandler"
                    bean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler" />

  <%-- Search results top control bar --%>

 <%-- KATERINA CHANGED THE SHOW ALWAYS VALUE to TRUE --%>
  <svc-ui:controlBar controlBarId="agentAllTicketSearchResultsControlBar"
                     treeTableBean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler"
                     treeTableId="agentAllTicketsSearchResultsTable"
                     showAlways="true"
                     varHighIndex="highIndex"
                     varOffset="offset"
                     varTotal="total">

    <table border="0"
           cellpadding="0"
           cellspacing="0"
           class="w100p">
      <tr>
        <td>
          <%@ include file="/include/navigate.jsp" %>
        </td>
      </tr>
    </table>

    <br/>
  </svc-ui:controlBar>

  <%-- Search results table --%>

  <svc-ui:treeTable selectionMode="none"
                    treeTableBean="/atg/svc/ui/formhandlers/SearchAgentAllTicketsFormHandler"
                    treeTableId="agentAllTicketsSearchResultsTable" >

    <svc-ui:head style="bgGray" showAlways="true">
      <fmt:message key="table-id-label" var="columnTitle"/>
      <svc-ui:column defaultSortDirection="ascending"
                  isSortable="true"
                  key="ticketId"
                  sortField="ticketId"
                  sortIgnoreCase="true"
                  style="column left w10p gridResultCell"
                  styleDown="columnDown left w10p gridResultCell"
                  styleHover="columnHover left w10p gridResultCell"
                  styleSorted="columnSorted left w10p gridResultCell"
                  title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
    
<%--    <fmt:message key="table-activities-title" var="columnTitle"/>
    <svc-ui:column isSortable="false"
                    key="ticketActivityCount"
                    style="column left w5p"
                    styleDown="columnDown left w5p"
                    styleHover="columnHover left w5p"
                    title="${columnTitle}">
      <fmt:message key="table-activities-label" var="columnTitle"/>
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
--%>    
    <fmt:message key="table-description-label" var="columnTitle"/>
    <svc-ui:column defaultSortDirection="ascending" key="taskDescription"
                    isSortable="true"
                    sortField="ticketDescription"
                    sortIgnoreCase="true"
                    style="column left gridResultCell"
                    styleDown="columnDown left gridResultCell"
                    styleHover="columnHover left gridResultCell"
                    styleSorted="columnSorted left gridResultCell"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>

    <fmt:message key="table-created-label" var="columnTitle"/>
    <svc-ui:column defaultSortDirection="ascending" key="creationTime"
                    isSortable="true"
                    sortField="creationTime"
                    style="column left w10p gridResultCell"
                    styleDown="columnDown left w10p gridResultCell"
                    styleHover="columnHover left w10p gridResultCell"
                    styleSorted="columnSorted left w10p gridResultCell"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>

    <fmt:message key="table-age-label" var="columnTitle"/>
    <svc-ui:column defaultSortDirection="ascending" key="age"
                    isSortable="true"
                    sortField="age"
                    style="column left w10p gridResultCell"
                    styleDown="columnDown left w10p gridResultCell"
                    styleHover="columnHover left w10p gridResultCell"
                    styleSorted="columnSorted left w10p gridResultCell"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>

<%--    <fmt:message key="table-group-label" var="columnTitle"/>     
    <svc-ui:column defaultSortDirection="ascending" key="ticketGroup"
                    isSortable="true"
                    sortField="ticketGroup"
                    sortIgnoreCase="true"
                    style="column left w15p"
                    styleDown="columnDown left w15p"
                    styleHover="columnHover left w15p"
                    styleSorted="columnSorted left w15p"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
    
    <fmt:message key="table-assigned-label" var="columnTitle"/>
    <svc-ui:column key="ticketAssignee"
                      style="column left w15p"
                      styleDown="columnDown left w15p"
                      styleHover="columnHover left w15p"
                      title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
    
    <fmt:message key="table-escalation-title" var="columnTitle"/>
    <svc-ui:column key="ticketEscLevel" defaultSortDirection="ascending"
                    isSortable="true"
                    sortField="ticketEscLevel"
                    style="column left w5p"
                    styleDown="columnDown left w5p"
                    styleHover="columnHover left w5p"
                    styleSorted="columnSorted left w5p"
                    title="${columnTitle}">
      <fmt:message key="table-escalation-label" var="columnTitle"/>
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
--%>
    <fmt:message key="table-status-label" var="columnTitle"/>
    <svc-ui:column defaultSortDirection="ascending" key="ticketStatus"
                    isSortable="true"
                    sortField="ticketStatus"
                    style="column left w15p gridResultCell"
                    styleDown="columnDown left w15p gridResultCell"
                    styleHover="columnHover left w15p gridResultCell"
                    styleSorted="columnSorted left w15p gridResultCell"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>

<%--    <fmt:message key="table-duedate-label" var="columnTitle"/>
    <svc-ui:column defaultSortDirection="ascending" key="ticketDueDate"
                    isSortable="true"
                    sortField="ticketDueDate"
                    style="column left w10p"
                    styleDown="columnDown left w10p"
                    styleHover="columnHover left w10p"
                    styleSorted="columnSorted left w10p"
                    title="${columnTitle}">
      <span class="bold blue"><c:out value="${columnTitle}"/></span>
    </svc-ui:column>
--%>
     <svc-ui:column defaultSortDirection="ascending"
                  isSortable="false"
                  key="ticketWorkOn"
                  style="column left w10p gridResultCell"
                  styleDown="columnDown left w10p gridResultCell"
                  styleHover="columnHover left w10p gridResultCell"
                  styleSorted="columnSorted left w10p gridResultCell"
                  title="${columnTitle}">
      <span class="bold blue"></span>
    </svc-ui:column>
    
  </svc-ui:head>

  <svc-ui:itemStyle styleName="row"/>
  <svc-ui:itemStyle styleName="alternateRow"/>

    <svc-ui:body items="${agentAllTicketsSearchFormHandler.viewItems}"
                 noItemsUrl="/include/noSearch.jsp"
                 scope="request"
                 varItem="result">
      <svc-ui:itemTemplate key="${result.item.id}"
                           noWrap="'false"
                           overflow="wrap">

        <%--<svc-ui:field columnKey="" iclass="w5p">
          <c:choose>
            <c:when test='${result.item.creationChannel == "web"}'>
              <-- Currently we do not have an image for web. Replace with correct image later. -->
              <fmt:message var="selfServiceEsc" key="tooltip.self-service-esc" />
              <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_selfServiceEscalation.gif" width="21" height="21" align="absmiddle" title="${selfServiceEsc}" alt="${selfServiceEsc}" />
            </c:when>
            <c:when test='${result.item.creationChannel == "email"}'>
              <fmt:message var="email" key="tooltip.email" />
              <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_emailChannel.gif" width="21" height="21" align="absmiddle" title="${email}" alt="${email}" />
            </c:when>
            <c:when test='${result.item.creationChannel == "telephony"}'>
              <fmt:message var="phoneCall" key="tooltip.phonecall" />
              <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_phone.gif" width="21" height="21" align="absmiddle" title="${phoneCall}" alt="${phoneCall}" />
            </c:when>
            <c:when test='${result.item.creationChannel == "SMS"}'>
              <fmt:message var="sms" key="tooltip.sms" />
              <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_smsChannel.gif" width="21" height="21" align="absmiddle" title="${sms}" alt="${sms}" />
            </c:when>
            <c:when test='${result.item.creationChannel == "MMS"}'>
              <fmt:message var="mms" key="tooltip.mms" />
              <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_mmsChannel.gif" width="21" height="21" align="absmiddle" title="${mms}" alt="${mms}" />
            </c:when>
            <c:when test='${result.item.creationChannel == "chat"}'>
	      <fmt:message var="chat" key="tooltip.chat" />
	      <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_chat.gif" width="21" height="21" align="absmiddle" title="${chat}" alt="${chat}" />
	    </c:when>
	    <c:otherwise>
	      <c:choose>
	        <c:when test='${result.item.defaultOutboundChannel == "email"}'>
	          <fmt:message var="email" key="tooltip.email" />
	          <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_emailChannel.gif" width="21" height="21" align="absmiddle" title="${email}" alt="${email}" />
	        </c:when>
	        <c:otherwise>
	    	  <!-- Currently we do not have an image for unknown. Replace with correct image later. -->
	          <fmt:message var="custChannel" key="tooltip.customer-channel" />
	          <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_customChannel.gif" width="21" height="21" align="absmiddle" title="${custChannel}" alt="${custChannel}" />
	        </c:otherwise>
	      </c:choose>
            </c:otherwise>
          </c:choose>
        </svc-ui:field>--%>
        <svc-ui:field columnKey="" iclass="w10p gridResultCell">
          <a href="#" onclick="document.getElementById('agentAllTicketsViewTicketForm').ticketId.value=<c:out value='${result.item.id}'/>;viewTicket('agentAllTicketsViewTicketForm')" class="blueU"><c:out value="${result.item.id}"/></a>         
        </svc-ui:field>
        
        <%-- For Number of Activities for the Tickets --%>
        <%--<svc-ui:field columnKey="ticketNumActCol" iclass="w5p">
          <c:out value="${result.managerData.numberOfActivities}"/>
        </svc-ui:field>--%>
        
        <%-- For Description of the ticket --%>
        <svc-ui:field columnKey="ticketDescCol">
          <c:out value="${result.item.description}"/>
        </svc-ui:field>
        
        <%-- For Group --%>
        <%--<svc-ui:field columnKey="ticketOwningGroup" iclass="w15p">
          <span class="textLeft_iconRight">
	          <c:if test="${not empty result.managerData.group}">			  
    	      	<c:out value="${result.managerData.group}"/>
    	      </c:if>	
          </span>
        </svc-ui:field>--%>
        
        <%-- For Assignee --%>
        <%--<svc-ui:field columnKey="ticketAssignee" iclass="w15p">
          <c:choose>
            <c:when test="${not empty result.managerData.assigned && not empty result.managerData.agent}">			  
              <c:out value="${result.managerData.agent}" />			  
            </c:when>
            <c:otherwise>
              <fmt:message key="unassigned"/>
            </c:otherwise>
	  </c:choose>
        </svc-ui:field>--%>
        
        <%-- For Escalation Level --%>
        <%--<svc-ui:field columnKey="" iclass="w5p">
          <c:if test="${not empty result.item.escalationLevel && result.item.escalationLevel != agentAllTicketsSearchFormHandler.startingEscalationLevel}" >
            <fmt:message var="escalated" key="tooltip.escalated" />
            <dspel:img src="${imageLocation}/iconcatalog/25x22/agent_utilities/icon_escalate.gif" width="25" height="22" align="absmiddle" alt="${escalated}" title="${escalated}" />
          </c:if>
          &nbsp;
        </svc-ui:field> --%>

        <%-- For CreationTime  --%>        
        <svc-ui:field columnKey="creationTime" iclass="w10p gridResultCell">
          <fmt:formatDate type="both" value="${result.item.creationTime}" dateStyle="short" timeStyle="short"/>
        </svc-ui:field>
        
        <%-- For Age  --%>        
        <svc-ui:field columnKey="age" iclass="w10p gridResultCell">
          <c:out value="${result.managerData.age}"/>
        </svc-ui:field>
        
        <%-- For Status  --%>
        <svc-ui:field columnKey="ticketStatus" iclass="w15p gridResultCell">
          <c:out value="${result.managerData.status}"/>
          <dspel:tomap var="substatus" value="${result.item.subStatus}"/>
          <c:out value="(${substatus.subStatusName})"/>
        </svc-ui:field>
        
        <%-- For Work on --%>
        <svc-ui:field columnKey="ticketWorkOn" iclass="w10p gridResultCell">
        <a href="#" class="blueU" onclick="workActiveTicket('workActiveTicketForm','<c:out value='${result.item.id}'/>');return false;">Work on</a>
        </svc-ui:field>

      </svc-ui:itemTemplate>
    </svc-ui:body>
  </svc-ui:treeTable>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/agentAllTicketsSearchResults.jsp#1 $$Change: 946917 $--%>
