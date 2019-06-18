<%-- This page is used to edit hard good shipping group.
param - nickname
This parameter is used to initialize shipping group from the ShippingGroupMapContainer.

param - success
This parameter is used to close the popup panel and refresh the parent page. This parameter is
added to the request on edit form submission.

--%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean var="updateShippingGroupFormHandler"
                  bean="/atg/commerce/custsvc/order/UpdateHardgoodShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean var="sgConfig"
                  bean="/atg/commerce/custsvc/ui/HardgoodShippingGroupConfiguration"/>
  <dsp:importbean var="addressForm" bean="/atg/svc/agent/ui/fragments/AddressForm"/>

  <dsp:getvalueof var="nickname" param="nickname"/>
  <dsp:getvalueof var="success" param="success"/>

  <c:url var="successErrorURL" context="/${sgConfig.editPageFragment.servletContext}"
         value="${sgConfig.editPageFragment.URL}">
    <c:param name="nickname" value="${nickname}"/>
    <c:param name="${stateHolder.windowIdParameterName}"
             value="${windowId}"/>
    <c:param name="success" value="true"/>
  </c:url>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <div id="atg_commerce_csr_editShippingAddress"
         class="atg_commerce_csr_popupPanel atg_commerce_csr_addressFormPopup atg_svc_popupPanel">
      <dsp:layeredBundle basename="${sgConfig.resourceBundle}">
       <fmt:message var="editPageFragmentTitle" key="${sgConfig.editPageFragmentTitleKey}"/>
      </dsp:layeredBundle>
      <font size="1">
        <b><c:out value="${editPageFragmentTitle}"/></b>
      </font>
      <div>
        <%--When there is an error, display the error on the page. --%>
        <dsp:droplet name="Switch">
          <dsp:param bean="UpdateHardgoodShippingGroupFormHandler.formError"
                     name="value"/>
          <dsp:oparam name="true">
          <font size="1" color="#FF0000">
          <UL>
            <dsp:droplet name="ErrorMessageForEach">
              <dsp:param bean="UpdateHardgoodShippingGroupFormHandler.formExceptions"
                         name="exceptions"/>
              <dsp:oparam name="output">
                <LI>
                  <dsp:valueof param="message"/>
              </dsp:oparam>
            </dsp:droplet>
          </UL>
          </font>
          </dsp:oparam>
          <dsp:oparam name="false">
            <c:if test="${success}">
              <%--When there is no error on the page submission, close the popup page and refresh the parent page.
              the parent page only will refresh if the result parameter value is ok. --%>
              <script type="text/javascript">
                hidePopupWithResults('atg_commerce_csr_editShippingAddress', {result : 'ok'});
              </script>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </div>

      <c:set var="formId" value="csrEditShippingAddressForm"/>
      <dsp:form id="${formId}"
                formid="${formId}">

        <dsp:input type="hidden" priority="-10" value=""
                   bean="UpdateHardgoodShippingGroupFormHandler.updateHardgoodShippingGroup"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="UpdateHardgoodShippingGroupFormHandler.updateHardgoodShippingGroupErrorURL"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="UpdateHardgoodShippingGroupFormHandler.updateHardgoodShippingGroupSuccessURL"/>

        <dsp:input type="hidden" bean="UpdateHardgoodShippingGroupFormHandler.shippingGroupByNickname"
                   value="${fn:escapeXml(nickname) }" priority="5"/>

        <c:if test="${empty updateShippingGroupFormHandler.hardgoodShippingGroup }">
          <dsp:setvalue bean="UpdateHardgoodShippingGroupFormHandler.shippingGroupByNickname"
                        value="${fn:escapeXml(nickname) }"/>
        </c:if>

        <div class="atg_dataForm atg_commerce_csr_addressForm atg-csc-base-table atg-base-table-customer-address-add-form">

          <dsp:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
            <dsp:param name="formId" value="${formId}"/>
            <dsp:param name="formHandler" value="/atg/commerce/custsvc/order/UpdateHardgoodShippingGroupFormHandler"/>
            <dsp:param name="addressBean"
                       value="/atg/commerce/custsvc/order/UpdateHardgoodShippingGroupFormHandler.workingHardgoodShippingGroup.shippingAddress"/>
            <dsp:param name="submitButtonId" value="${formId}SaveButton"/>
          </dsp:include>

          <div class="atg_svc_saveProfile atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            <!--
   <span class="atg_commerce_csr_fieldTitle">
              &nbsp;
            </span>-->

            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="UpdateHardgoodShippingGroupFormHandler.shippingAddressExistsInProfile"
                         name="value"/>
              <dsp:oparam name="true">
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param
                    bean="/atg/userprofiling/ActiveCustomerProfile.transient"
                    name="value"/>
                  <dsp:oparam name="false">
                    <dsp:input type="checkbox" checked="${true}"
                               bean="UpdateHardgoodShippingGroupFormHandler.updateProfile" name="updateToProfile"/>
                    <fmt:message key="newOrderSingleShipping.editShippingAddress.field.saveToProfile"/>
                  </dsp:oparam>
                  <dsp:oparam name="true">
                    <dsp:input type="hidden" bean="UpdateHardgoodShippingGroupFormHandler.updateProfile" value="false"/>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:input type="hidden" bean="UpdateHardgoodShippingGroupFormHandler.updateProfile" value="false"/>
              </dsp:oparam>
            </dsp:droplet>
          </div>
          <div class="atg_svc_formActions atg-csc-base-table-cell">
            <div class="atg_commerce_csr_panelFooter">
              <input type="button"
                     name="${formId}SaveButton"
                     value="<fmt:message key='common.save' />"
                     onclick="atg.commerce.csr.order.shipping.editShippingAddress('${successErrorURL}');return false;"
                     dojoType="atg.widget.validation.SubmitButton"/>
              <%-- When the user clicks on the cancel button, just hide the popup panel. --%>
              <input type="button"
                     value="<fmt:message key='common.cancel'/>"
                     onclick="hidePopupWithResults( 'atg_commerce_csr_editShippingAddress', {result : 'cancel'});return false;"/>
            </div>
          </div>
        </div>

      </dsp:form>
      <%-- end of editShippingAddressForm --%>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/editHardgoodShippingGroup.jsp#1 $$Change: 946917 $--%>
