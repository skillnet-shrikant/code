<%--
Address Editor 

@addressId - Optional, the ID of an address to edit. If not supplied,
  creates a new address.

@version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressEditor.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">
    <c:set var="addressBookFormHandlerPath"
      value="/atg/svc/agent/profile/AddressBookFormHandler"/>
    <dspel:importbean var="abfh" bean="${addressBookFormHandlerPath}"/>
    <dspel:importbean var="addressForm" bean="/atg/svc/agent/ui/fragments/AddressForm"/>
    <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
    <svc-ui:frameworkPopupUrl var="url"
      value="/include/addresses/addressEditor.jsp"
      context="/agent"
      addressId="${param.addressId}"
      windowId="${windowId}"/>
    <svc-ui:frameworkPopupUrl var="successUrl"
      value="/include/addresses/addressEditor.jsp"
      context="/agent"
      addressId="${param.addressId}"
      success="true"
      windowId="${windowId}"/>
    <div class="atg_svc_popupPanel">
    
      <div>
        <%--When there is an error, display the error on the page. --%>
        <dspel:droplet name="Switch">
          <dspel:param bean="/atg/svc/agent/profile/AddressBookFormHandler.formError" name="value"/>
          <dspel:oparam name="true">
	          <font size="1" color="#FF0000">
	          <UL>
	            <dspel:droplet name="ErrorMessageForEach">
	              <dspel:param bean="/atg/svc/agent/profile/AddressBookFormHandler.formExceptions"
	                         name="exceptions"/>
	              <dspel:oparam name="output">
	                <LI>
	                  <dspel:valueof param="message"/>
	              </dspel:oparam>
	            </dspel:droplet>
	          </UL>
	          </font>
          </dspel:oparam>
          <dspel:oparam name="false">
          </dspel:oparam>
        </dspel:droplet>
      </div>
       	
      <c:set var="formId" value="profileAddressEditorForm"/>
      <dspel:form action="#" method="post" id="${formId}"
        formid="${formId}">
        <h3 id="atg_commerce_csr_customerinfo_popNewAddress">
          <c:choose>
            <c:when test="${not empty param.addressId}">
              <dspel:setvalue bean="AddressBookFormHandler.addressId"
                value="${param.addressId}"/>
              <dspel:input type="hidden" value="${param.addressId}"
                priority="1000"
                bean="AddressBookFormHandler.addressId"/>
            </c:when>
            <c:otherwise>
            </c:otherwise>
          </c:choose>
        </h3>
        <div class="atg_commerce_csr_popupPanelCloseButton">
        </div>
        <div class="atg-csc-base-table atg-base-table-customer-address-add-form">

            <c:choose>
              <%-- when the address meta info has no param.nickname ... --%>
              <c:when test="${empty abfh.addressMetaInfo.nickname}">
                <%-- arbitrarily pick first nickname --%>
                <c:forEach var="n" end="1" items="${abfh.addressMetaInfo.nicknames}">
                  <c:set var="nickname" value="${n}"/>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <c:set var="nickname" value="${abfh.addressMetaInfo.nickname}"/>
              </c:otherwise>
            </c:choose>

			<div class="atg_commerce_csr_lastName atg-csc-base-table-row">
				<span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
					<label class="atg_messaging_requiredIndicator">
						Address Name
					</label>
					<span class="requiredStar">*</span>
				</span>

				<div class="atg-csc-base-table-cell atg-base-table-customer-address-add-input">
	          		<dspel:input id="addrEditNickname" type="text" tabindex="3" iclass="atg-base-table-customer-address-add-form-input-dojo" 
	          			bean="AddressBookFormHandler.addressMetaInfo.nickname" size="25" maxlength="40" value="${nickname}">
	            		<dspel:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
	            		<dspel:tagAttribute name="required" value="true" />
	            		<dspel:tagAttribute name="trim" value="true" />
	            		<dspel:tagAttribute name="promptMessage" value="${lastNameMissing}"/>
	          		</dspel:input>
	        	</div>
	      	</div>

          <dspel:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
            <dspel:param name="formId" value="${formId}"/>
            <dspel:param name="addressBean" value="/atg/svc/agent/profile/AddressBookFormHandler.addressMetaInfo.address"/>
            <dspel:param name="submitButtonId" value="saveChoice"/>
			<dspel:param name="formHandler" value="${addressBookFormHandlerPath}"/>
          </dspel:include>
          
        </div>

        <div class="atg_svc_saveProfile">
          <c:forEach var="custEditor" items="${abfh.addressMetaInfo.params.managerEditorOptions}">
            <c:if test="${not empty custEditor.value.customEditorPage}">
            <div>
              <dspel:include otherContext="${custEditor.value.customEditorContext}"
                src="${custEditor.value.customEditorPage}">
                <dspel:param name="optionsKey" value="${custEditor.key}"/>
                <dspel:param name="options" value="${custEditor.value}"/>
                <dspel:param name="formHandlerPath" value="${addressBookFormHandlerPath}"/>
                <dspel:param name="formHandler" value="${abfh}"/>
              
              </dspel:include>
            </div>
            </c:if>
          </c:forEach>
        </div>

        <div class="atg_svc_formActions">
          <dspel:input type="hidden" value="${successUrl}" 
            bean="AddressBookFormHandler.successURL"/>
          <dspel:input type="hidden" value="${url}" 
            bean="AddressBookFormHandler.errorURL"/>
          <dspel:input type="hidden" value="--" priority="-100"
            id="addAddressAction"
            bean="AddressBookFormHandler.addAddress"/>
          <dspel:input type="hidden" value="--" priority="-100"
            id="updateAddressAction"
            bean="AddressBookFormHandler.updateAddress"/>
          <div>
            <input id="saveChoice" name="saveChoice" value="<fmt:message key='address.save.label'/>" type="button"
                   onclick="if ( ${empty param.addressId} ) {
                              dojo.byId( 'addAddressAction' )['disabled'] = false;
                              dojo.byId( 'updateAddressAction' )['disabled'] = true;
                            }
                            else {
                              dojo.byId( 'addAddressAction' )['disabled'] = true;
                              dojo.byId( 'updateAddressAction' )['disabled'] = false;
                            }
                            atgSubmitPopup({url: '${url}', 
                              form: dojo.byId('${formId}'),
                              popup: getEnclosingPopup('addrEditNickname')});
                            return false;"/>
            <input value="<fmt:message key='address.cancel.label'/>" 
              type="button" id="cancelChoice"
              onClick="hidePopupWithResults('addrEditNickname', {result:'cancel'}); 
              return false;"/>
          </div>
        </div>

      </dspel:form>
    </div>
    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults( 'addrEditNickname', {result : 'save'});
      </script>
    </c:if>
    
    <script type="text/javascript">
      _container_.onLoadDeferred.addCallback(function () {
        document.getElementById("saveChoice").disabled=true;
      });
    </script>
    
  </dspel:layeredBundle>
</dspel:page>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/include/addresses/addressEditor.jsp#1 $$Change: 946917 $--%>
