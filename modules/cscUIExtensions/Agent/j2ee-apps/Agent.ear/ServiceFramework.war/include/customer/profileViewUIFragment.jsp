<%--
 This UI fragment defines the View Profile Information Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileViewUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dspel:importbean bean="/atg/userprofiling/ServiceCustomerProfile" var="profile"/>
  <dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
  <dspel:getvalueof var="customerProfileId" bean="ServiceCustomerProfile.repositoryId"/>
 
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
  <ul class="atg_svc_panelToolBar">
    <li class="atg_svc_last"><a href="#" onclick="viewCustomerSelect('${customerProfileId}');return false;"><fmt:message key="customer.view.select.customer.label"/></a></li>
  </ul>
  <div class="atg_svc_coreCustomerInfo">
  <div class="atg-csc-base-table">
    <div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label><fmt:message key="customer.firstName.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.firstName"/>
      </span>

      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-label">
        <label for="phoneNumber"><fmt:message key="customer.phone.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.${agentUIConfig.customerAddressPropertyName}.phoneNumber"/>
      </span>
    </div>
    <div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitleatg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label><fmt:message key="customer.middleName.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.middleName"/>
      </span>

      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-label">
        <label for="emailAddress"><fmt:message key="customer.email.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.email"/>
      </span>
    </div>
    <div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label><fmt:message key="customer.lastName.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.lastName"/>
      </span>

      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-label">
        <label for="loginName"><fmt:message key="customer.login.label"/><fmt:message key="text.field.terminator"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.login"/>
      </span>
    </div>
    <div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label for="dateOfBirth"><fmt:message key="customer.birthDate.label"/></label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:getvalueof var="dateOfBirth" bean="ServiceCustomerProfile.dateOfBirth"/>
        <c:if test="${!empty dateOfBirth}">
          <web-ui:formatDate value="${dateOfBirth}" type="date" dateStyle="short"/>
        </c:if>
      </span>
      
      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-label">
        <label for="creationDate"><fmt:message key="customer.view.creationDate.label"/></label>
      </span>
      <span class="creationDate_data plainText atg-csc-base-table-cell">
        <dspel:getvalueof var="registrationDate" bean="ServiceCustomerProfile.registrationDate"/>
        <c:if test="${!empty registrationDate}">
          <web-ui:formatDate value="${registrationDate}" type="both" dateStyle="medium" timeStyle="short"/>
        </c:if>
      </span>
    </div>
	<div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitleatg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label>Gender:</label>
      </span>
      <span class="plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.gender"/>
      </span>
    </div>
    <div class="atg-csc-base-table-row">
      <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-first-label">
        <label for=""><fmt:message key="customer.view.profileId.label"/></label>    
      </span>

      <span class="profileId_data plainText atg-csc-base-table-cell">
        <dspel:valueof bean="ServiceCustomerProfile.repositoryId"/>
      </span>

      <c:catch var="ex">
        <dspel:getvalueof var="profileGroupsSnapshot" bean="ServiceCustomerProfile.profileGroupsSnapshot"/>
      </c:catch>
      
      <c:if test="${!empty profileGroupsSnapshot && empty ex}">
        
          <span class="atg_svc_fieldTitle atg-csc-base-table-cell atg-base-table-customer-view-label">
            <label><fmt:message key="customer.view.segments.label"/></label>
          </span>
          <c:set var="profileGroupsSnapshotString" value=""/>
          <dspel:droplet name="/atg/dynamo/droplet/ForEach">
            <dspel:param bean="ServiceCustomerProfile.profileGroupsSnapshot" name="array"/>
             <dspel:oparam name="empty">
              <fmt:message key="customer.view.noSegmentsFound" />
             </dspel:oparam>
             <dspel:oparam name="output">
              <dspel:getvalueof param="element" var="elementValue"/>
              <c:set var="profileGroupsSnapshotString">
                ${profileGroupsSnapshotString}
                ${elementValue}
              </c:set>
            </dspel:oparam>
          </dspel:droplet>
          <textarea name="" cols="20" rows="4" class="atg-csc-base-table-cell" readonly="readonly" style="width: 150px;">${profileGroupsSnapshotString}</textarea>
      </c:if>
    </div>
  </div>
  </div>
  </dspel:layeredBundle>

</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/customer/profileViewUIFragment.jsp#1 $$Change: 946917 $--%>
