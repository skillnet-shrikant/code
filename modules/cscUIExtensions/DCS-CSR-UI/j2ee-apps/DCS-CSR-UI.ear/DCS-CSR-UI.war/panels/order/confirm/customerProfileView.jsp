<%--
This file displays a read only view of the customer
profile data.

@version $Id:
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">
<dsp:page xml="true">
  <dsp:tomap var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

  <%-- Div that contains a read only view of the profile. --%>
  <div id="atg_commerce_csr_displayCustomer_div" class="atg_commerce_csr_coreCustomerInfo">
    <h3 class="atg_svc_subSectionTitle" id="atg_commerce_csr_orderconfirm_createNewAccount">
      <fmt:message key='confirmOrder.newCustomerProfile.header'/>
    </h3>    
     
    <ul class="atg_dataForm atg_commerce_csr_customerInfo">
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="fName"><fmt:message key='confirmOrder.newCustomerProfile.firstName.label'/></label></span>
        <c:out value="${customerProfile.firstName}"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="phoneNumber"><fmt:message key='confirmOrder.newCustomerProfile.phoneNumber.label'/></label></span>
        <dsp:valueof bean="/atg/userprofiling/ActiveCustomerProfile.homeAddress.phoneNumber"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="middleName"><fmt:message key='confirmOrder.newCustomerProfile.middleName.label'/></label></span>
        <c:out value="${customerProfile.middleName}"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="emailAddress"><fmt:message key='confirmOrder.newCustomerProfile.email.label'/></label></span>
        <c:out value="${customerProfile.email}"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="lName"><fmt:message key='confirmOrder.newCustomerProfile.lastName.label'/></label></span>
        <c:out value="${customerProfile.lastName}"/>
      </li> 
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label for="loginName"><fmt:message key='confirmOrder.newCustomerProfile.loginName.label'/></label></span>
        <c:out value="${customerProfile.login}"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label><fmt:message key='confirmOrder.newCustomerProfile.profileId'/></label></span>
        <c:out value="${customerProfile.repositoryId}"/>
      </li>
      <li>
        <span  class="atg_commerce_csr_fieldTitle"><label><fmt:message key='confirmOrder.newCustomerProfile.creationDate'/></label></span>
        <dsp:valueof date="MMM d, yyyy" value="${customerProfile.registrationDate}"/>
      </li>
    </dl>
  </div>
  </dsp:layeredBundle>
</dsp:page>

</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/confirm/customerProfileView.jsp#1 $$Change: 946917 $--%>
