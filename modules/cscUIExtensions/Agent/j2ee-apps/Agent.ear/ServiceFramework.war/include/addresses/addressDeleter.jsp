<%--
Address Deleter

@addressId - The ID of an address to to delete

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressDeleter.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:importbean var="abfh"
    bean="/atg/svc/agent/profile/AddressBookFormHandler"/>
  <svc-ui:frameworkPopupUrl var="url"
    value="/include/addresses/addressDeleter.jsp"
    context="/agent"
    addressId="${param.addressId}"
    windowId="${windowId}"/>
  <svc-ui:frameworkPopupUrl var="successUrl"
    value="/include/addresses/addressDeleter.jsp"
    context="/agent"
    addressId="${param.addressId}"
    success="true"
    windowId="${windowId}"/>
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <h4>
      <fmt:message key="address.deleter.confirmDelete"/>
    </h4>
    <div class="atg_commerce_csr_popupPanel atg_commerce_csr_addressFormPopup">
      <dspel:form method="post" id="profileAddressDeleterForm"
        formid="profileAddressDeleterForm">
        <dspel:setvalue bean="AddressBookFormHandler.addressId"
          value="${param.addressId}"/>
        <dspel:input type="hidden"
          bean="AddressBookFormHandler.addressId"
          value="${param.addressId}"/>
        <dspel:input type="hidden" value="${successUrl}" 
          bean="AddressBookFormHandler.successURL"/>
        <dspel:input type="hidden" value="${url}" 
          bean="AddressBookFormHandler.errorURL"/>
        <dspel:input type="hidden" value="--" priority="-100"
          id="deleteAddressAction"
          bean="AddressBookFormHandler.deleteAddress"/>
        <dl class="atg_svc_shipAddress">
          <dt>
            ${fn:escapeXml(abfh.addressMetaInfo.nicknames)}
          </dt>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.prefix)}
            ${fn:escapeXml(abfh.addressMetaInfo.address.firstName)}
            ${fn:escapeXml(abfh.addressMetaInfo.address.middleName)}
            ${fn:escapeXml(abfh.addressMetaInfo.address.lastName)}
            ${fn:escapeXml(abfh.addressMetaInfo.address.suffix)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.address1)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.address2)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.address3)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.city)},
            ${fn:escapeXml(abfh.addressMetaInfo.address.state)}
            ${fn:escapeXml(abfh.addressMetaInfo.address.postalCode)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.country)}
          </dd>
          <dd>
            ${fn:escapeXml(abfh.addressMetaInfo.address.phoneNumber)}
          </dd>
        </dl>
        <div class="atg_commerce_csr_panelFooter">
          <input value="<fmt:message key='address.delete.label'/>" 
            type="button" id="deleteChoice"
            onClick="
              atgSubmitPopup({url: '${url}', 
                form: document.getElementById('profileAddressDeleterForm'),
                popup: getEnclosingPopup('deleteAddressAction')});
              return false;"/>
          <input value="<fmt:message key='address.cancel.label'/>" 
            type="button" id="cancelChoice"
            onClick="hidePopupWithResults('deleteAddressAction', {result:'cancel'}); 
            return false;"/>
        </div>
      </dspel:form>
    </div>
    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults( 'deleteAddressAction', {result : 'delete'});
      </script>
    </c:if>
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressDeleter.jsp#1 $$Change: 946917 $--%>
