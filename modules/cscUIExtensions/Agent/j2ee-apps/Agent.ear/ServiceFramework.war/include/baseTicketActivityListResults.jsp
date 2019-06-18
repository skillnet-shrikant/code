<%@  include file="/include/top.jspf" %>
<c:catch var="exception">
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">

    <%--
      unused currently
    --%>
    <c:set var="ticketHolder" value="/atg/svc/ticketing/ViewTicketHolder"/>

    <dspel:importbean
      scope="page"
      var="ticketActivityListFormHandler"
      bean="${actvFormHandler}" />

	<dspel:importbean var="globalPanelConfig" bean="/atg/svc/agent/ticketing/GlobalPanelConfiguration"/>

    <%-- List results top control bar --%>

    <svc-ui:controlBar
      controlBarId="${actvCtrlBarId}"
      treeTableBean="${actvFormHandler}"
      treeTableId="${actvTableId}"
      showAlways="true"
      varHighIndex="highIndex"
      varOffset="offset"
      varTotal="total">
      <dspel:form style="display:none" action="#" id="${actvPre}ticketActivityDetailForm" formid="${actvPre}ticketActivityDetailForm">
        <dspel:input type="hidden" priority="-10" value="" bean="/atg/svc/ui/formhandlers/ActivityDetailFormHandler.loadDetails"/>
        <dspel:input type="hidden" name="ticketHolder" value="${ticketHolder}" bean="/atg/svc/ui/formhandlers/ActivityDetailFormHandler.ticketHolder"/>
        <dspel:input type="hidden" value="include/activities/activityDetailContainer.jsp" bean="/atg/svc/ui/formhandlers/ActivityDetailFormHandler.successURL"/>
        <dspel:input type="hidden" name="activityId" value="" bean="/atg/svc/ui/formhandlers/ActivityDetailFormHandler.activityId"/>
        <dspel:input type="hidden" name="containerDivId" value="" bean="/atg/svc/ui/formhandlers/ActivityDetailFormHandler.containerDivId"/>
      </dspel:form>

      <table border="0" cellpadding="0" cellspacing="0" class="w100p">
        <tr>
          <td align="left">
            <%@ include file="/include/navigate.jsp" %>
          </td>

          <td align="right">

            <form id="<c:out value='${actvPre}'/>ActivityFilterForm"
              name="<c:out value='${actvPre}'/>ActivityFilterForm">

              <script type="text/javascript">
                <c:out escapeXml="false" value="//<![CDATA["/>
                function toggleActivityDetail( id, imgid, activityId )
                {
                  document.getElementById("<c:out value='${actvPre}'/>ticketActivityDetailForm").activityId.value = activityId;
                  document.getElementById("<c:out value='${actvPre}'/>ticketActivityDetailForm").containerDivId.value = id;

                  var elt = document.getElementById(id).style;
                  var imgElt = document.getElementById(imgid);
                  if (elt.display == "none") {
                    elt.display = "block";
                    imgElt.src = '<c:out value="${imageLocation}"/>/iconcatalog/14x14/bullets/arrowdown.gif';
          	        ticketActivityDetail("<c:out value='${actvPre}'/>ticketActivityDetailForm");
                  }
                  else {
                    elt.display = "none";
                    imgElt.src = '<c:out value="${imageLocation}"/>/iconcatalog/14x14/bullets/arrowright.gif';
                  }
                }
                window.toggleActivityDetail = toggleActivityDetail;
                <c:out escapeXml="false" value="//]]>"/>
              </script>

              <%-- Activity Type Selector --%>
              <dspel:importbean var="ticketingRepository" bean="/atg/ticketing/TicketingRepository"/>

              <%
                 // get the ticketing repository
                 atg.repository.Repository repository = (atg.repository.Repository)
                   pageContext.findAttribute("ticketingRepository");

                 // Get the base ticketActivity item descriptor
                 atg.adapter.gsa.GSAItemDescriptor activityItemDescriptor = (atg.adapter.gsa.GSAItemDescriptor)
                   repository.getItemDescriptor( "ticketActivity" );

                 java.util.Map typeMap = activityItemDescriptor.getTypeDescriptorMap();

                 // Map that will eventually hold the Display Name -> activity type map
                 pageContext.setAttribute( "nameToTypeMap", new java.util.TreeMap(String.CASE_INSENSITIVE_ORDER) );

                 // Map associating activity type with display name
                 java.util.Map typeToNameMap = new java.util.HashMap();
                 pageContext.setAttribute( "typeToNameMap", typeToNameMap );

                 // Populate typeToNameMap with all display name values configured
                 // in repository definition file
                 if ( typeMap != null ) {
                   java.util.Iterator ii = typeMap.keySet().iterator();
                   while( ii.hasNext() ) {
                     String activityType = (String) ii.next();
                     atg.adapter.gsa.GSAItemDescriptor idesc =
                      (atg.adapter.gsa.GSAItemDescriptor) typeMap.get( activityType );
                     typeToNameMap.put( activityType, "" );
                   }
                 }
              %>

              <%-- add custom activity filters --%>
              <dspel:importbean var="activityFilters" bean="/atg/svc/ui/formhandlers/ActivityFilters"/>
              <c:forEach var="customFilter" items="${activityFilters.customActivityFilters}" varStatus="vs">
                <dspel:layeredBundle basename="${customFilter.resourceBundleName}">
                  <fmt:message var="displayName" key="${customFilter.displayNameResourceKey}" />
                  <c:set target="${typeToNameMap}" property="customActivityFilter${vs.index}"
                    value="${displayName}" />
                </dspel:layeredBundle>
              </c:forEach>

              <%-- add options for specific activity types --%>
              <dspel:droplet name="/atg/dynamo/droplet/PossibleValues">
                <dspel:param name="repository" bean="/atg/ticketing/TicketingRepository"/>
                <dspel:param name="itemDescriptorName" value="ticketActivity"/>
                <dspel:param name="propertyName" value="type"/>
                <dspel:oparam name="output">
                  <dspel:getvalueof var="values" param="values"/>
                  <c:forEach var="activityCode" items="${values}">
                    <dspel:importbean
                      var="ai"
                      bean="/atg/ticketing/activities/${activityCode}"/>
                    <c:choose>
                      <c:when test="${ ! ai.hidden }">
                        <c:if test="${ ! empty ai }">
                          <%--
                             If display name wasn't set in repository definition (obtained
                             above in scriptlet) then try to find a reasonable value for it
                             from the ActivityInfo. If that's not set either, just use the
                             type name.
                          --%>
                          <c:if test="${ empty typeToNameMap[activityCode] }">
                            <c:if test="${ ! empty ai.displayNameResourceKey }">
                              <dspel:layeredBundle basename="${ai.resourceBundleName}">
                                <fmt:message var="displayName" key="${ai.displayNameResourceKey}" />
                                <c:set target="${typeToNameMap}" property="${activityCode}" value="${displayName}" />
                              </dspel:layeredBundle>
                            </c:if>
                          </c:if>
                        </c:if>
                        <c:if test="${ empty typeToNameMap[activityCode] }">
                          <dspel:layeredBundle basename="atg.svc.agent.ticketing.ActivityInfoResources">
                            <c:set var="activityCodeDisplayName" value="${activityCode}DisplayName"/>
                            <c:set var="incorrectActivityCodeDisplayName" value="???${activityCodeDisplayName}???"/>
                            <fmt:message var="displayName" key="${activityCodeDisplayName}" />
                          </dspel:layeredBundle>
                          <c:if test="${displayName == incorrectActivityCodeDisplayName}">
                            <c:set var="displayName" value="${activityCode}"/>
                          </c:if>
                          <c:set target="${typeToNameMap}" property="${activityCode}" value="${displayName}"/>
                        </c:if>
                      </c:when>
                      <c:otherwise>
                        <%-- remove hidden activity type if it exists --%>
                        <c:set target="${typeToNameMap}" property="${activityCode}" value="${null}"/>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </dspel:oparam>
              </dspel:droplet>

              <%-- create an alpha sorted map of Display Name -> activity type --%>
              <c:forEach var="activity" items="${typeToNameMap}">
                <c:set target="${nameToTypeMap}" property="${activity.value}" value="${activity.key}" />
              </c:forEach>

              <%-- populate Activity Type selector --%>
              <fmt:message key="activities.filter"/>
              <select id="<c:out value='${actvPre}'/>ActivityTypeInput"
                name="<c:out value='${actvPre}'/>ActivityTypeInput" class="tickets"
                onchange="document.getElementById('<c:out value='${actvPre}'/>TicketActivityListForm').activityType.value=document.getElementById('<c:out value='${actvPre}'/>ActivityFilterForm').<c:out value='${actvPre}'/>ActivityTypeInput.value;<svc-ui:executeOperation operationName="refresh" treeTableId="${actvTableId}"/>" >
                <option value="" <c:if test="${empty ticketActivityListFormHandler.activityType}">selected</c:if> >
                  <fmt:message key="activities.allActivities"/>
                </option>
                <c:forEach var="activity" items="${nameToTypeMap}">
                  <option value="<c:out value='${activity.value}'/>"
                    <c:if test="${ activity.value == ticketActivityListFormHandler.activityType}">selected</c:if> >
                    <c:out value="${activity.key}"/>
                  </option>
                </c:forEach>
              </select>
            </form>
          </td>
        </tr>
      </table>
    </svc-ui:controlBar>

    <%-- ActivityList results table --%>

    <svc-ui:treeTable selectionMode="none"
      treeTableBean="${actvFormHandler}"
      treeTableId="${actvTableId}">

      <svc-ui:head style="pad5" showAlways="true">

        <%-- Expandomatic --%>
        <%--<fmt:message key="tasks.status.label" var="columnTitle"/>--%>
        <svc-ui:column
          key="expandomatic"
          style="column center"
          isSortable="false"
          styleDown="columnDown center"
          styleHover="columnHover center"
          styleSorted="columnSorted center"
          percentWidth="5%"
          title="">
          &nbsp;
        </svc-ui:column>

        <%-- Type --%>
        <fmt:message key="tasks.activityType" var="columnTitle"/>
        <svc-ui:column
          key="activityType"
          isSortable="false"
          sortField="activityType"
          sortIgnoreCase="true"
          style="column left"
          styleDown="columnDown left"
          styleHover="columnHover left"
          styleSorted="columnSorted left"
          percentWidth="10%"
          sortExpression="type"
          title="${columnTitle}">
          <span class="bold blue"><c:out value="${columnTitle}"/></span>
        </svc-ui:column>

        <%-- Source --%>
        <fmt:message key="tasks.activitySource" var="columnTitle"/>
        <svc-ui:column
           key="activitySource"
           isSortable="false"
           style="column left"
           styleDown="columnDown left"
           styleHover="columnHover left"
  	 percentWidth="20%"
           title="${columnTitle}">
          <%--<c:out value="${columnTitle}"/>--%>
          <dspel:img src="${UIConfig.contextRoot}/image/clear.gif" width="21" height="7" align="absmiddle"/>
          <span class="bold blue"><c:out value="${columnTitle}"/></span>
        </svc-ui:column>

        <%-- 'Activity' (description?) --%>
        <fmt:message key="tasks.activityDescription" var="columnTitle"/>
        <svc-ui:column
           key="activityDescription"
           style="column left"
           isSortable="false"
           styleDown="columnDown left"
           styleHover="columnHover left"
  	 percentWidth="45%"
           title="${columnTitle}">
           <span class="bold blue"><c:out value="${columnTitle}"/></span>
        </svc-ui:column>

        <%-- Date --%>
        <fmt:message key="tasks.activityDate" var="columnTitle"/>
        <svc-ui:column
          key="activityDate"
          defaultSortDirection="descending"
          isSortable="false"
          sortField="creationTime"
          style="column left"
          styleDown="columnDown left"
          styleHover="columnHover left"
          styleSorted="columnSorted left"
          sortExpression="creationTime"
          percentWidth="20%"
          title="${columnTitle}">
          <span class="bold blue"><c:out value="${columnTitle}"/></span>
        </svc-ui:column>
      </svc-ui:head>

      <svc-ui:itemStyle styleName="row"/>
      <svc-ui:itemStyle styleName="alternateRow"/>

      <svc-ui:body items="${ticketActivityListFormHandler.viewItems}"
        noItemsUrl="/include/noActivityListResults.jsp"
        scope="request"
        varItem="result"
        varStatus="counter">

        <dspel:importbean
          scope="request"
          var="activityInfo"
          bean="/atg/ticketing/activities/${result.item.type}"/>

        <dspel:importbean
          scope="request"
          var="defaultActivityInfo"
          bean="/atg/ticketing/activities/default"/>

        <%-- Only display activityInfo items with valid properties --%>
        <c:catch var="e">
          <svc-ui:itemTemplate key="${result.item.id}" noWrap="false" overflow="wrap">
  
            <%-- set these variables so they're available in the various column renderers --%>
            <c:set var="activity" scope="request" value="${result.item}"/>
            <c:set var="activityItem" scope="request" value="${result.repositoryItem}"/>
            <c:set var="imageId" value="${result.item.id}"/>
  
            <%-- Expandomatic --%>
            <svc-ui:field columnKey="expandomaticCol" percentWidth="5%">
  
              <%--
                Changes to how the detail page is found must be reflected in
                activityDetailContainer.jsp
              --%>
              <c:set var="detailPage" value="${activityInfo.detailPage}"/>
              <c:set var="ctx" value="${activityInfo.detailPageContext}"/>
  
              <c:if test="${ ! empty detailPage }">
                <a href='#' onclick='toggleActivityDetail("<c:out value="${actvPre}Close${imageId}"/>", "<c:out value="${actvPre}Open${imageId}"/>","<c:out value="${result.item.id}"/>" );return false;'>
                  <%-- open arrow --%>
                  <dspel:img id="${actvPre}Open${imageId}" width="14" height="14"
                    src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" />
                </a>
              </c:if>
            </svc-ui:field>
  
            <%--
              use defaultActivityInfo for activityInfo if original activityInfo
              is null. Do this after the Expandomatic column, so no detail page
              option shows for activity types with no detail page.
            --%>
            <c:if test="${ empty activityInfo }">
              <c:set var="activityInfo" value="${defaultActivityInfo}" scope="request"/>
            </c:if>
  
            <%-- Type --%>
            <fmt:message key="tasks.activityType" var="columnTitle"/>
            <svc-ui:field columnKey="activityType" iclass="padLeft5" title="${columnTitle}" percentWidth="10%">
              <c:set var="renderer" value="${activityInfo.typeColRenderer}"/>
              <c:set var="ctx" value="${activityInfo.typeColRendererContext}"/>
              <c:if test="${ empty renderer }">
                <c:set var="renderer" value="${defaultActivityInfo.typeColRenderer}"/>
                <c:set var="ctx" value="${defaultActivityInfo.typeColRendererContext}"/>
              </c:if>
              <dspel:include otherContext="${ctx}" src="${renderer}" />
            </svc-ui:field>
  
            <%-- Source --%>
            <fmt:message key="tasks.activitySource" var="columnTitle"/>
            <svc-ui:field columnKey="activitySource" title="${columnTitle}" percentWidth="20%">
              <c:set var="renderer" value="${activityInfo.sourceColRenderer}"/>
              <c:set var="ctx" value="${activityInfo.sourceColRendererContext}"/>
              <c:if test="${ empty renderer }">
                <c:set var="renderer" value="${defaultActivityInfo.sourceColRenderer}"/>
                <c:set var="ctx" value="${defaultActivityInfo.sourceColRendererContext}"/>
              </c:if>
              <dspel:include otherContext="${ctx}" src="${renderer}" />
            </svc-ui:field>
  
            <%-- 'Activity' (Description/heading) --%>
            <fmt:message key="tasks.activityDescription" var="columnTitle"/>
            <svc-ui:field columnKey="activityDescription" title="${columnTitle}" percentWidth="45%">
              <c:set var="renderer" value="${activityInfo.activityColRenderer}"/>
              <c:set var="ctx" value="${activityInfo.activityColRendererContext}"/>
              <c:if test="${ empty renderer }">
                <c:set var="renderer" value="${defaultActivityInfo.activityColRenderer}"/>
                <c:set var="ctx" value="${defaultActivityInfo.activityColRendererContext}"/>
              </c:if>
              <dspel:include src="${renderer}" otherContext="${UIConfig.contextRoot}"/>
            </svc-ui:field>
  
            <%-- Date --%>
            <fmt:message key="tasks.activityDate" var="columnTitle"/>
            <svc-ui:field columnKey="activityDate" title="${columnTitle}" percentWidth="20%">
              <fmt:formatDate value="${result.item.creationTime}"
                type="both" dateStyle="short" timeStyle="short"/>
            </svc-ui:field>
  
          </svc-ui:itemTemplate>
        </c:catch>

        <%-- activity detail rendered to this div --%>
        <c:set var="activityStyle" value="display:none"/>
        <div id="<c:out value="${actvPre}Close${imageId}"/>" class="activityContainer" style="<c:out escapeXml='false' value='${activityStyle}'/>">
          <svc-ui:itemTemplate key="${result.item.id}" noWrap="false" overflow="wrap">
          </svc-ui:itemTemplate>
        </div>
        <c:if test="${globalPanelConfig.expandTopActivity && actvTableId == 'mainTicketActivityTable' && (!empty detailPage) && counter.count == 1 && ((globalPanelConfig.onlyExpandThese == null || empty globalPanelConfig.onlyExpandThese) || (globalPanelConfig.onlyExpandThese != null && result.item.itemDescriptor.itemDescriptorName == globalPanelConfig.onlyExpandThese))}">
          <c:set var="firstItemId" value="${imageId}" scope="page"/>
        </c:if>
      </svc-ui:body>

    </svc-ui:treeTable>
    <c:if test="${actvTableId == 'mainTicketActivityTable' && firstItemId != null}">
      <caf:outputJavaScript>
      toggleActivityDetail("<c:out value="${actvPre}Close${firstItemId}"/>", "<c:out value="${actvPre}Open${firstItemId}"/>","<c:out value="${firstItemId}"/>" );
      </caf:outputJavaScript>
    </c:if>

  </dspel:layeredBundle>
</dspel:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="Caught exception in baseTicketActivityListResults.jsp: ${exception}" /><br />
  <%
    Throwable error = (Throwable)pageContext.getAttribute("exception");
    String stack = atg.core.exception.StackTraceUtils.getStackTrace(error, 10, 10);
    stack = atg.core.util.StringUtils.replace(stack, '\n', "<br/>");
    stack = atg.core.util.StringUtils.replace(stack, '\t', "&nbsp;&nbsp;&nbsp;");
    out.println(stack);
  %>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/baseTicketActivityListResults.jsp#1 $$Change: 946917 $--%>
