<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketHistoryResults.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
  <dspel:importbean scope="request"
                    var="ticketHistoryTableFormHandler"
                    bean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler" />
 
  <%-- Search results top control bar --%>
  <svc-ui:controlBar controlBarId="ticketHistoryListControlBar"
                     treeTableBean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler"
                     treeTableId="ticketHistoryListTable"
                     showAlways="true"
                     varHighIndex="highIndex"
                     varOffset="offset"
                     varTotal="total">

    <table border="0" cellpadding="0" cellspacing="0" class="w100p">
      <tr>

        <td>
          <%@ include file="/include/navigate.jsp" %>
        </td>

        <td align="right">
          <%-- filter form --%>
          <form name="filter">
            <fmt:message key="filter-label"/>
            <select id="ticketStatus" name="ticketStatus" class="tasks"
              onchange="<svc-ui:executeOperation operationName="refresh" treeTableId="ticketHistoryListTable"/>" >
              <option value=""><fmt:message key="all"/></option>
              <c:forEach var="status" items="${ticketHistoryTableFormHandler.statusValues}">
                <option value='<c:out value="${status}"/>' 
                  <c:if test="${status == ticketHistoryTableFormHandler.parameterMap.status}">selected</c:if> 
                >
                  <c:out value="${status}" />
                </option>
              </c:forEach>
            </select>
          </form>
        </td>
      </tr>
    </table>
  </svc-ui:controlBar>

  <%-- Search results table --%>

  <svc-ui:treeTable selectionMode="none"
                    treeTableBean="/atg/svc/ui/formhandlers/TicketHistoryTableFormHandler"
                    treeTableId="ticketHistoryListTable">

    <svc-ui:head showAlways="true">

      <fmt:message key="table-id-label" var="columnTitle"/>
      <svc-ui:column defaultSortDirection="ascending"
                  isSortable="false"
                  key="ticketId"
                  sortField="ticketId"
                  sortIgnoreCase="true"
                  style="w15p center"
                  styleDown="w15p center"
                  styleHover="w15p center"
                  styleSorted="w15p center"
                  title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-activities-title" var="columnTitle"/>
      <svc-ui:column isSortable="false"
                      key="ticketActivityCount"
                      style="w5p center"
                      styleDown="w5p center"
                      styleHover="w5p center"
                      title="${columnTitle}">
        <span class="bold blue"><fmt:message key="table-activities-label"/></span>
      </svc-ui:column>

      <fmt:message key="table-description-label" var="columnTitle"/>
      <svc-ui:column key="taskDescription"
                        style="w20p left"
                        styleDown="w20p left"
                        styleHover="w20p left"
                        title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-priority-label" var="columnTitle"/>
      <svc-ui:column defaultSortDirection="ascending" key="ticketPriority"
                      isSortable="false"
                      sortField="ticketPriority"
                      sortIgnoreCase="true"
                      style="w5p center"
                      styleDown="w5p center"
                      styleHover="w5p center"
                      styleSorted="w5p center"
                      title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-age-label" var="columnTitle"/>
      <svc-ui:column key="ticketAge" defaultSortDirection="ascending"
                      isSortable="false"
                      sortField="ticketAge"
                      style="w5p center"
                      styleDown="w5p center"
                      styleHover="w5p center"
                      styleSorted="w5p center"
                      title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-status-label" var="columnTitle"/>
      <svc-ui:column key="ticketStatus"
                      isSortable="false"
                      sortField="ticketStatus"
                      style="w15p left"
                      styleDown="w15p left"
                      styleHover="w15p left"
                      styleSorted="w15p left"
                      title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-created-label" var="columnTitle"/>
      <svc-ui:column key="ticketCreatedDate"
                      isSortable="false"
                      sortField="ticketCreatedDate"
                      style="w15p left"
                      styleDown="w15p left"
                      styleHover="w15p left"
                      styleSorted="w15p left"
                      title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

      <fmt:message key="table-duedate-label" var="columnTitle"/>
      <svc-ui:column key="ticketDueDate"
                      isSortable="false"
                      sortField="ticketDueDate"
                      style="w15p left"
                      styleDown="w15p left"
                      styleHover="w15p left"
                      styleSorted="w15p left"
                      title="${columnTitle}">
        <span class="bold blue"><c:out value="${columnTitle}"/></span>
      </svc-ui:column>

    </svc-ui:head>

    <svc-ui:itemStyle styleName="row"/>
    <svc-ui:itemStyle styleName="alternateRow"/>

    <svc-ui:body items="${ticketHistoryTableFormHandler.viewItems}"
      noItemsUrl="/include/noTicketResults.jsp"
      scope="request" varItem="result">

      <svc-ui:itemTemplate key="${result.item.id}" noWrap="false" overflow="wrap">

        <%-- ID field --%>
        <svc-ui:field columnKey="ticketIdCol" iclass="w15p" colspan="2">
          <fmt:message var="ticket" key="tooltip.ticket" />
          <c:set var="lastActivity" value="${result.item.lastActivity}"/>
          <%@ include file="lastTicketActivityIcon.jspf" %>
          &nbsp;
          <a href="#" 
            onclick="document.getElementById('ticketHistoryResultsViewTicketForm').ticketId.value=<c:out value='${result.item.id}'/>;viewTicket('ticketHistoryResultsViewTicketForm');" 
            class="blueU">
            <c:out value="${result.item.id}"/>
          </a>
          &nbsp;
          <fmt:message var="ticketId" key="tooltip.copyticketid" />	
          <dspel:img src="${imageLocation}/iconcatalog/25x22/toolbar/icon_copyTicketID.gif" 
            width="25" height="22" align="absmiddle" title="${ticketId}" alt="${ticketId}" />
        </svc-ui:field>

        <%-- number of activities (#A) --%>
        <svc-ui:field columnKey="ticketNumActCol" iclass="w5p center">
          <c:out value="${result.managerData.numberOfActivities}"/>
        </svc-ui:field>

        <%-- description --%>
        <svc-ui:field columnKey="ticketDescCol" iclass="w20p">
          <c:out value="${result.item.description}"/>
        </svc-ui:field>

        <%-- priority --%>
        <svc-ui:field columnKey="ticketPriorityCol" iclass="w5p center">
          <span class="textLeft_iconRight"><c:out value="${result.item.priority}"/></span>
        </svc-ui:field>

        <%-- age --%>
        <svc-ui:field columnKey="ticketAgeCol" iclass="w5p">
          <span class="textLeft_iconRight"><c:out value="${result.managerData.age}"/></span>
        </svc-ui:field>

        <%-- status --%>
        <svc-ui:field columnKey="ticketAssignmentIconCol" iclass="padLeft5 w15p" >
          <c:if test="${result.managerData.assigned}">
            <fmt:message var="statusAssigned" key="tooltip.claim-staus-assigned" />
            <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_status_assigned.gif" 
              width="21" height="21" title="${statusAssigned}" alt="${statusAssigned}" align="absmiddle" />
          </c:if>
          <c:if test="${!result.managerData.assigned}">
            <fmt:message var="statusAvailable" key="tooltip.claim-staus-available" />
            <dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_status_available.gif" 
              width="21" height="21" title="${statusAvailable}" alt="${statusAvailable}" align="absmiddle"/>
          </c:if>
          <dspel:tomap var="status" value="${result.item.subStatus}"/>          
          <c:out value="${status.parentStatus}"/> 
          <c:out value=" (${status.subStatusName})"/>
        </svc-ui:field>

        <%-- created date --%>
        <svc-ui:field columnKey="ticketCreatedCol" iclass="w15p" >
          <span class="textLeft_iconRight">
            <fmt:formatDate type="both" value="${result.item.creationTime}" 
              dateStyle="short" timeStyle="short"/>
          </span>
        </svc-ui:field>

        <%-- due date --%>
        <svc-ui:field columnKey="ticketDueDateCol" iclass="w15p" >
          <span class="textLeft_iconRight">
            <fmt:formatDate type="both" value="${result.item.dueTime}" 
              dateStyle="short" timeStyle="short"/>
          </span>
        </svc-ui:field>
      </svc-ui:itemTemplate>
    </svc-ui:body>
  </svc-ui:treeTable>

</dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/ticketHistoryResults.jsp#1 $$Change: 946917 $--%>
