<!-- ticketCustomerInformationPanel.jsp -->
<%--
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketCustomerInformationPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
<dspel:importbean bean="/atg/userprofiling/ServiceCustomerProfile"/>
<dspel:getvalueof var="currentTicket" bean="/atg/svc/ticketing/TicketHolder.currentTicket.id"/>
<dspel:getvalueof var="viewTicket" bean="/atg/svc/ticketing/ViewTicketHolder.currentTicket.id"/>
<dspel:getvalueof var="isProfileTransient" bean="ServiceCustomerProfile.transient"/>
<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

<dspel:droplet name="/atg/ticketing/droplet/IsOwnerChangeable">
<dspel:param name="ticket" value="${viewTicket}"/>
<dspel:oparam name="output">
  <dspel:getvalueof var="changeable" param="isChangeable"/>
</dspel:oparam>
</dspel:droplet>

<c:if test="${!isPrinting}">
          <ul class="atg_svc_panelToolBar">
          <c:if test="${(currentTicket eq viewTicket)}">
            <c:if test="${!isProfileTransient}"> 
              <li class="atg_svc_last">
                <fmt:message var="edit" key="tooltip.editcustomer" />
                <a href="#" class="atg_svc_ticketViewCustomer" title="${edit}" onclick="viewCurrentCustomer('customersTab'); return false;">                              
                 <c:out value="${edit}"/>
                </a>
              </li>
            </c:if>
             
            <c:if test="${changeable}">
              <li>
              <c:if test="${isProfileTransient}">
                <fmt:message var="linkCustomer" key="tooltip.link-customer" />
              </c:if>
              <c:if test="${!isProfileTransient}">
                <fmt:message var="linkCustomer" key="tooltip.change-customer" />
              </c:if>
                <a href="#" class="atg_svc_ticketSearchCustomer" title="${linkCustomer}" onclick="showCustomerSearch(); return false;">
                <c:out value="${linkCustomer}"/>
                </a>
              </li>
            
            </c:if>              
            <c:if test="${changeable || (!changeable && isProfileTransient)}"> 
              <li>
                <fmt:message var="custProfile" key="tooltip.create-cust-profile" />
                <a href="#" title="${custProfile}" class="atg_svc_ticketCreateCustomer" onclick="createNewCustomer('customersTab');return false;">
                <c:out value="${custProfile}"/>
                </a>
              </li>              
            </c:if>
          </c:if>
        </ul>
</c:if>
  
  <div class="atg-csc-base-table">
    <div>
      <div class="atg-csc-base-table-row">
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="profile-id-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four">
          <c:if test="${!isProfileTransient}">
            <dspel:valueof bean="ServiceCustomerProfile.repositoryId"/>
          </c:if>
        </div>
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="first-name-label"/></div>
        <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.firstName"/></div>
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="last-name-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.lastName"/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="middle-name-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.middleName"/></div>
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="date-of-birth-label"/></div>
        <dspel:getvalueof var="dateOfBirth" bean="ServiceCustomerProfile.dateOfBirth"/>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><fmt:formatDate value="${dateOfBirth}" type="date" dateStyle="short"/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="email-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.email"/></div>
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="phone-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.phoneNumber"/></div>
      </div>

      <div class="atg-csc-base-table-row">
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="login-name-label"/></div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.login"/></div>
        <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left">&nbsp;</div>
        <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four">&nbsp;</div>
      </div>

      <c:if test="${!isPrinting}">  
        <h5 class="trigger atg-csc-base-table-row"><a name="Address" class="atg_svc_ticketInfoToggle atg-csc-base-table-cell atg-csc-base-spacing-two-top" id="Address" href="#" onclick="toggleRowGroup('addressDiv', 'addressArrow');return false;"><dspel:img id="addressArrow" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><span><fmt:message key="address-label"/></span></a></h5> 
        <div id="addressDiv" style="display: none;">
      </c:if>
      <c:if test="${isPrinting}">
        <fmt:message key="address-label"/>
        <div id="addressDiv atg-csc-base-table-row-group">
      </c:if>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="address1-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four" colspan="3"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.address1"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="address2-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four" colspan="3"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.address2"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="city-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.city"/></div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="state-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.state"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="country-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.country"/></div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="postal-code-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><dspel:valueof bean="ServiceCustomerProfile.homeAddress.postalCode"/></div>
        </div>
      </div>

      <c:if test="${!isPrinting}">
        <h5 class="trigger atg-csc-base-table-row"><a name="Membership" class="atg_svc_ticketInfoToggle atg-csc-base-table-cell" id="Membership" href="#" onclick="toggleRowGroup('membershipDiv', 'membershipArrow');return false;"><dspel:img id="membershipArrow" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><span><fmt:message key="membership-label"/></span></a></h5> 
        <div id="membershipDiv" style="display:none;">
      </c:if>
      <c:if test="${isPrinting}">
        <fmt:message key="membership-label"/>
        <div id="membershipDiv atg-csc-base-table-row-group" >
      </c:if>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="member-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four">
            <c:if test="${!isProfileTransient}">
              <dspel:getvalueof var="member" bean="ServiceCustomerProfile.member"/>
              <c:if test="${member == 'true'}">
                <fmt:message key="yes"/>
              </c:if>
              <c:if test="${member != 'true'}">
                <fmt:message key="no"/>
              </c:if>
            </c:if>
          </div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="reg-label"/></div>
          <dspel:getvalueof var="registrationDate" bean="ServiceCustomerProfile.registrationDate"/>
          <div class="atg-csc-base-table-cell atg-csc-base-spacing-two-left atg-csc-ticket-table-cell-width-four"><fmt:formatDate value="${registrationDate}" type="both" dateStyle="medium" timeStyle="short"/></div>
        </div>

        <div class="atg-csc-base-table-row">
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="receive-email-label"/></div>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four">
            <c:if test="${!isProfileTransient}">
              <dspel:getvalueof var="receiveEmail" bean="ServiceCustomerProfile.receiveEmail"/>
              <c:choose>
                <c:when test="${receiveEmail == 'yes'}">
                  <fmt:message key="yes"/>
                </c:when>
                <c:when test="${receiveEmail == 'no'}">
                  <fmt:message key="no"/>
                </c:when>
                <c:otherwise>
                  <c:out value="${receiveEmail}"/>
                </c:otherwise>
              </c:choose>
            </c:if>
          </div>
          <div class="formLabel atg-csc-base-table-cell atg-csc-base-spacing-two-left"><fmt:message key="last-emailed-label"/></div>
          <dspel:getvalueof var="lastEmailed" bean="ServiceCustomerProfile.lastEmailed"/>
          <div class="atg-csc-base-spacing-two-left atg-csc-base-table-cell atg-csc-ticket-table-cell-width-four"><fmt:formatDate value="${lastEmailed}" type="date" dateStyle="short"/></div>
        </div>
      </div>
    </div>
  </div> 
</dspel:layeredBundle>
</dspel:page>
<!-- end ticketCustomerInformationPanel.jsp -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketCustomerInformationPanel.jsp#1 $$Change: 946917 $--%>
