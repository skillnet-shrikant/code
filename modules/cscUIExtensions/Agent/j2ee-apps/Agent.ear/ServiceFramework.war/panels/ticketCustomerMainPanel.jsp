<!-- ticketCustomerInformationPanel.jsp -->
<%--
  
    @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketCustomerMainPanel.jsp#1 $$Change: 946917 $
    @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf" %>
<dspel:page xml="true">
  

<dspel:layeredBundle basename="atg.svc.agent.ticketing.TicketingResources">

  <table class="layoutTable" cellspacing="0" cellpadding="3" parseWidgets="false">
    <tr>
      <td class="formLabel" style="width: 25%"><fmt:message key="profile-id-label"/></td>
      <td class="padLeft10" style="width: 25%">
        <dspel:getvalueof var="id" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.repositoryId"/>
        <c:out value="${id}"/>
      </td>
      <td class="formLabel" style="width: 25%">&nbsp;</td>
      <td class="padLeft10" style="width: 25%">&nbsp;</td>
    </tr>
    <tr>
      <td class="formLabel"><fmt:message key="first-name-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="firstName" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.firstName"/><c:out value="${firstName}"/>&nbsp;</td>
      <td class="formLabel"><fmt:message key="last-name-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="lastName" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.lastName"/><c:out value="${lastName}"/>&nbsp;</td>
    </tr>
    <tr>
      <td class="formLabel"><fmt:message key="middle-name-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="middleName" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.middleName"/><c:out value="${middleName}"/>&nbsp;</td>
      <td class="formLabel"><fmt:message key="dob-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="birthDate" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.dateOfBirth"/>
       <c:if test="${not empty birthDate}">
         <fmt:formatDate value="${birthDate}" type="date" dateStyle="short"/>
        </c:if>
        &nbsp;
      </td>
    </tr>
    <tr>
      <td class="formLabel"><fmt:message key="email-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="email" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.email"/><c:out value="${email}"/>&nbsp;</td>
      <td class="formLabel"><fmt:message key="phone-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="phone" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.phoneNumber"/><c:out value="${phone}"/>&nbsp;</td>
    </tr>
    <tr>
      <td class="formLabel"><fmt:message key="login-name-label"/></td>
      <td class="padLeft10"><dspel:getvalueof var="login" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.login"/><c:out value="${login}"/>&nbsp;</td>
      <td class="formLabel">&nbsp;</td>
      <td class="padLeft10">&nbsp;</td>
    </tr>
  </table>
  <br /> 
  <h5 class="trigger"><a name="Address" id="Address1" href="#" onclick="toggle('addressDiv1', 'addressArrow1');return false;"><dspel:img id="addressArrow1" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><fmt:message key="address-label"/></a></h5> 
  <div id="addressDiv1" style="display: none;" parseWidgets="false">
    <table class="w98p" style="width: 98%" cellspacing="0" cellpadding="3">
      <tr>
        <td class="formLabel w20p" style="width: 25%"><fmt:message key="address1-label"/></td>
	<td class="padLeft10"><dspel:getvalueof var="address1" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.address1"/><c:out value="${address1}"/>&nbsp;</td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="address2-label"/></td>
        <td class="padLeft10"><dspel:getvalueof var="address2" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.address2"/><c:out value="${address2}"/>&nbsp;</td>
      </tr>
      <tr>
        <td class="formLabel w20p" style="width: 25%"><fmt:message key="city-label"/></td>
        <td class="padLeft10 w20p" style="width: 25%"><dspel:getvalueof var="city" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.city"/><c:out value="${city}"/>&nbsp;</td>
        <td class="formLabel" style="width: 25%"><fmt:message key="state-label"/></td>
        <td class="padLeft10" style="width: 25%"><dspel:getvalueof var="state" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.state"/><c:out value="${state}"/>&nbsp;</td>
      </tr>
      <tr>
        <dspel:getvalueof var="country" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.country"/>
        <td class="formLabel w20p"><fmt:message key="country-label"/></td>
        <td class="padLeft10 w20p"><c:out value="${country}"/>&nbsp;</td>
        <td class="formLabel" style="width: 25%"><fmt:message key="postal-code-label"/></td>
        <td class="padLeft10" style="width: 25%"><dspel:getvalueof var="postal" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.homeAddress.postalCode"/><c:out value="${postal}"/>&nbsp;</td>
      </tr>
    </table>
  </div> 
  <h5 class="trigger"><a name="Membership" id="Membership1" href="#" onclick="toggle('membershipDiv1', 'membershipArrow1');return false;"><dspel:img id="membershipArrow1" src="${imageLocation}/iconcatalog/14x14/bullets/arrowright.gif" width="14" height="14"/><fmt:message key="membership-label"/></a></h5> 
  
  <div id="membershipDiv1" style="display:none;" parseWidgets="false">
    <table class="w98p" style="width: 98%" cellspacing="0" cellpadding="3">
      <tr>
        <td class="formLabel w20p"><fmt:message key="member-label"/></td>
        <dspel:getvalueof var="member" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.member"/>
        <td class="padLeft10 w20p">
          <c:if test="${member == 'true'}">
            <fmt:message key="yes"/>
          </c:if>
          <c:if test="${member != 'true'}">
            <fmt:message key="no"/>
          </c:if>
        </td>
        <td class="formLabel w20p"><fmt:message key="reg-label"/></td>
        <td class="padLeft10">
          <dspel:getvalueof var="registrationDate" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.registrationDate"/>
          <c:if test="${not empty registrationDate}">
         <fmt:formatDate value="${registrationDate}" type="both" dateStyle="medium" timeStyle="short"/>
          </c:if>
          &nbsp;
        </td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="receive-email-label"/></td>
        
        <td class="padLeft10">
          
          <dspel:getvalueof var="receiveEmail" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.receiveEmail"/><c:out value="${receiveEmail}"/>&nbsp;
         
        </td>
        <td class="formLabel"><fmt:message key="last-emailed-label"/></td>
        <td class="padLeft10">
        <dspel:getvalueof var="lastEmailed" bean="/atg/svc/ticketing/TicketHolder.currentTicket.user.lastEmailed"/>
        <c:if test="${not empty lastEmailed}">
          <fmt:formatDate value="${lastEmailed}" type="date" dateStyle="short"/>
        </c:if>
        &nbsp;
        </td>
      </tr>
    </table>
  </div> 
</dspel:layeredBundle>
</dspel:page>
<!-- end ticketCustomerMainPanel.jsp -->

<!-- $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketCustomerMainPanel.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/ticketCustomerMainPanel.jsp#1 $$Change: 946917 $--%>
