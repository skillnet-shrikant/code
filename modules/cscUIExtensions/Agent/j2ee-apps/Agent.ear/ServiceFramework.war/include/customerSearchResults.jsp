<%--
  @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customerSearchResults.jsp#1 $ $Change: 946917 $
  @updated $DateTime: 2015/01/26 17:26:27 $ $Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">                       
  <dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">
    <dspel:importbean scope="request" var="customerSearchFormHandler" bean="/atg/svc/ui/formhandlers/CustomerSearchFormHandler" />

    <c:catch var="resultsError">
      <%-- Search results top control bar --%>
      <svc-ui:controlBar controlBarId="customerSearchResultsControlBar"
                         treeTableBean="/atg/svc/ui/formhandlers/CustomerSearchFormHandler"
                         treeTableId="customerSearchResultsTable"
                         showAlways="false"
                         varHighIndex="highIndex"
                         varOffset="offset"
                         varTotal="total">
        <%@ include file="/include/customerSearchListHeader.jspf" %>
      </svc-ui:controlBar>
      <%-- Search results table --%>
      <svc-ui:treeTable selectionMode="none"
                        treeTableBean="/atg/svc/ui/formhandlers/CustomerSearchFormHandler"
                        treeTableId="customerSearchResultsTable">
        <svc-ui:itemStyle styleName="row"/>
        <svc-ui:itemStyle styleName="alternateRow"/>
        <svc-ui:body items="${CustomerSearchFormHandler.searchResults.results}"
                     noItemsUrl="/include/noTicketResults.jsp"
                     scope="request"
                     varItem="result">
          <svc-ui:itemTemplate key="${result.item.id}" noWrap="false" overflow="wrap">
            <fmt:message var="customer" key="tooltip.customer" />
            <c:set var="firstName"><c:out escapeXml="false" value="${result.item.firstName}" default="" /></c:set> 
            <c:set var="lastName"><c:out escapeXml="false" value="${result.item.lastName}" default="" /></c:set> 
            <fmt:message var="fullName" key="full-name">
              <fmt:param value="${firstName}"/>
              <fmt:param value="${lastName}"/>
            </fmt:message>
            <svc-ui:field columnKey="ticketIdCol" colspan="2"><a href="#" onclick='openCustomerInfo("<c:out value='${result.item.id}'/>");' class="blueU" name="viewMoreCustomerInfo" id="viewMoreCustomerInfo"><dspel:img src="${imageLocation}/iconcatalog/21x21/table_icons/icon_customer.gif" width="21" height="21" align="absmiddle" alt="${customer}" title="${customer}" /><c:out value="${fullName}"/></a></svc-ui:field>
            <table class="bgCustomerTable" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td>
                  <table class="w80p" cellspacing="0" cellpadding="3">
                    <tr>
                      <td class="formLabel"><fmt:message key="table-profile-id-label"/></td>
                      <td class="padLeft10"><c:out value="${result.item.repositoryId}"/></td>
                      <td class="formLabel"></td>
                      <td class="padLeft10"></td>
                    </tr>
                    <tr>
                      <td class="formLabel"><fmt:message key="login-name-label"/></td>
                      <td class="padLeft10"><c:out escapeXml="false" value="${result.item.login}"/></td>
                      <td class="formLabel"><fmt:message key="email-label"/></td>
                      <td class="padLeft10"><c:out escapeXml="false" value="${result.item.email}"/></td>
                    </tr>
                    <dspel:tomap var="address" value="${result.item.anyAddress}"/>
                      <c:if test="${address != null}">
                      <tr>
                        <td class="formLabel"><fmt:message key="address-label"/></td>
                        <td class="padLeft10" colspan="3"><c:out escapeXml="false" value="${address.address1}"/> <c:out escapeXml="false" value="${address.address2}"/></td>
                      </tr>
                      <tr>
                        <td class="formLabel">&nbsp;</td>
                        <td class="padLeft10" colspan="3"><c:out escapeXml="false" value="${address.city}"/><c:if test="${ ! empty address.city}">,</c:if> <c:out escapeXml="false" value="${address.state}"/> <c:out escapeXml="false" value="${address.postalCode}"/> <c:out escapeXml="false" value="${address.country}"/></td>
                      </tr>
                      <tr>
                        <td class="formLabel"><fmt:message key="phone-label"/></td>
                        <td class="padLeft10"><c:out escapeXml="false" value="${address.phoneNumber}"/></td>
                        <td class="formLabel">&nbsp;</td>
                        <td class="padLeft10">&nbsp;</td>
                      </tr>
                    </c:if>
                  </table>
                  <c:if test="${result.managerData.tickets != null}">
                    <h5 class="trigger">
                      <a href='#' onclick='toggle( "<c:out value='${result.item.id}Tickets'/>", "ticketsArrow" );return false;' name="Tickets" id="Tickets">
                        <dspel:img id="ticketsArrow" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14" align="absmiddle"/>
                        <fmt:message key="tickets-title"/>
                      </a>
                    </h5>
                    <div id="<c:out value='${result.item.id}Tickets'/>" style="display: none;">
                      <table class="data tickets">
                          <thead>
   	                    <tr class="bgGray">
                              <th><fmt:message key="table-id-label"/></th>
                              <th><fmt:message key="table-description-label"/></th>
                              <th><fmt:message key="table-age-label"/></th>
                              <th><fmt:message key="table-claimed-label"/></th>
                              <th><fmt:message key="table-status-label"/></th>
                              <th><fmt:message key="table-created-label"/></th>  
                            </tr>
                          </thead>
                        <c:forEach items="${result.managerData.tickets}" var="ticket">
                          <dspel:tomap var="element" value="${ticket}" />
                          <tr class="bgWhite">
                            <td>
                              <c:choose>
                                <c:when test='${element.creationChannel == "web"}'>
                                  <%-- Currently we do not have an image for web. Replace with correct image later. --%>
                                  <fmt:message var="selfServiceEsc" key="tooltip.self-service-esc" />
                                  <div class="atgServiceFrameworkSelfServiceIcon" title="${selfServiceEsc}"></div>
                                </c:when>
                                <c:when test='${element.creationChannel == "email"}'>
                                  <fmt:message var="email" key="tooltip.email" />
                                  <div class="atgServiceFrameworkEmailChannelIcon" title="${email}"></div>
                                </c:when>
                                <c:when test='${element.creationChannel == "telephony"}'>
                                  <fmt:message var="phoneCall" key="tooltip.phonecall" />
                                  <div class="atgServiceFrameworkPhoneIcon" title="${phoneCall}"></div>
                                </c:when>
                                <c:when test='${element.creationChannel == "SMS"}'>
                                  <fmt:message var="sms" key="tooltip.sms" />
                                  <div class="atgServiceFrameworkSmsChannelIcon" title="${sms}"></div>
                                </c:when>
                                <c:when test='${element.creationChannel == "MMS"}'>
                                  <fmt:message var="mms" key="tooltip.mms" />
                                  <div class="atgServiceFrameworkMmsChannelIcon" title="${mms}"></div>
                                </c:when>
                                <c:otherwise>
                                  <fmt:message var="custChannel" key="tooltip.customer-channel" />	
                                  <div class="atgServiceFrameworkCustomChannelIcon" title="${custChannel}"></div>
                                </c:otherwise>
                              </c:choose>
                              &nbsp;
                              <a href="#" onclick="document.getElementById('customerSearchResultsViewTicketForm').ticketId.value=<c:out value='${element.id}'/>;viewTicket('customerSearchResultsViewTicketForm');" class="blueU"><c:out value="${element.id}"/></a>
                            </td>
                            <td><c:out value="${element.description}"/></td>
                            <td><span class="textLeft_iconRight"><c:out value="${element.ageInDays}"/><fmt:message key="day-char"/><c:out value="${element.ageInHours - (element.ageInDays * 24)}"/><fmt:message key="hour-char"/></span></td>
                            <td>
                              <c:if test="${element.agentAssignmentActivity != null}">
                                <div class="atgServiceStatusAssignedIcon" title="<fmt:message key='tooltip.claim-staus-assigned'/>"></div>
                              </c:if>
                              <c:if test="${element.agentAssignmentActivity == null}">
                                <div class="atgServiceStatusAvailableIcon" title="<fmt:message key='tooltip.claim-staus-available'/>"></div>
                              </c:if>
                            </td>
                            <td class="center">
                              <dspel:tomap var="status" value="${element.subStatus}"/>
                              <c:if test="${status != null}">
                                <c:out value="${status.subStatusName}"/>
                              </c:if>
                            </td>
                            <td>
                              <span class="textLeft_iconRight"><fmt:formatDate type="both" value="${element.creationTime}" dateStyle="short" timeStyle="short"/></span>
                            </td>
                          </tr>
                        </c:forEach>
                          <tr class="bgWhite">
                            <td colspan="8" class="right"><a href="#" onclick="openCustomerInfo('<c:out value="${result.item.id}"/>');" class="blueU">View All</a></td>
                          </tr>
                      </table>  
                    </div> 
                  </c:if>
                  <c:if test="${result.managerData.orders != null}">
                    <h5 class="trigger">
                      <a href='#' onclick='toggle( "<c:out value='${result.item.id}Orders'/>", "ordersArrow" );return false;' name="orders" id="orders">
                        <div class="atgServiceFrameworkArrowRight"></div>
                        <fmt:message key="orders-title"/>
                      </a>
                    </h5>
                    <div id="<c:out value='${result.item.id}Orders'/>" style="display: none;">
                      <table class="data tickets">
                          <thead>
                            <tr class="bgGray">
                              <th><fmt:message key="table-id-label"/></th>
                              <th><fmt:message key="table-numitems-label"/></th>
                              <th><fmt:message key="table-total-label"/></th>
                              <th><fmt:message key="table-item-summary-label"/></th>
                              <th><fmt:message key="table-status-label"/></th>
                              <th><fmt:message key="table-submit-date-label"/></th>
                              <th><fmt:message key="table-created-date-label"/></th>
                            </tr>
                          </thead>
                        <c:forEach items="${result.managerData.orders}" var="order">
                          <%--<dspel:tomap var="element" value="${order}" />--%>
                          <caf:size var="itemCount" collection="${order.commerceItems}"/>
                          <tr class="bgWhite">
                            <td><c:out value="${order.id}"/></td>
                            <td><c:out value="${itemCount}"/></td>
                            <td><c:out escapeXml="false" value="${order.priceInfo.currencyCode}${order.priceInfo.amount}"/></td>
                            <td><c:out escapeXml="false" value="${order.description}"/></td>
                            <td><c:out escapeXml="false" value="${order.state}"/></td>
                            <td><fmt:formatDate type="both" value="${order.submittedDate}" dateStyle="short" timeStyle="short"/></td>
                            <td><fmt:formatDate type="both" value="${order.createdDate}" dateStyle="short" timeStyle="short"/></td>
                          </tr>
                        </c:forEach>
                          <tr class="bgWhite">
                            <td colspan="8" class="right"><a href="#" onclick="openCustomerInfo('<c:out value="${result.item.id}"/>');" class="blueU">View All</a></td>
                          </tr>
                      </table>  
                    </div> 
                  </c:if>
                </td>
              </tr>
            </table>
          </svc-ui:itemTemplate>
        </svc-ui:body>
      </svc-ui:treeTable>
    </c:catch>
 
    <c:if test = "${not empty resultsError}">
      An error occurred <c:out value="${resultsError.message}"/><br>
      Error is of type <c:out value="${resultsError['class']}"/><br>
      Stacktrace: <br>
      <pre>
        <%    
        Throwable t = (Throwable)pageContext.getAttribute("resultsError");
        t.printStackTrace(new java.io.PrintWriter(out));
        %>
      </pre>
    </c:if>
    <%--
    <c:if test = "${empty resultsError}">
    No result Errors
    </c:if>
    --%>  
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customerSearchResults.jsp#1 $$Change: 946917 $--%>
