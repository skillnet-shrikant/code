<%-- This page is used to edit electronic shipping group.
param - nickname
This parameter is used to initialize shipping group from the ShippingGroupMapContainer.

param - success
This parameter is used to close the popup panel and refresh the parent page. This parameter is
added to the request on edit form submission.

--%>

<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <dsp:importbean var="updateShippingGroupFormHandler"
                  bean="/atg/commerce/custsvc/order/UpdateElectronicShippingGroupFormHandler"/>
  <dsp:importbean var="sgConfig"
                  bean="/atg/commerce/custsvc/ui/ElectronicShippingGroupConfiguration"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  <dsp:importbean var="electronicAddressForm" bean="/atg/commerce/custsvc/ui/fragments/order/ElectronicAddressForm"/>

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
         class="atg_commerce_csr_popupPanel atg_commerce_csr_addressFormPopup">
      <dsp:layeredBundle basename="${sgConfig.resourceBundle}">
       <fmt:message var="editPageFragmentTitle" key="${sgConfig.editPageFragmentTitleKey}"/>
      </dsp:layeredBundle>
      <h2>
        <c:out value="${editPageFragmentTitle}"/>
      </h2>
      <div class="atg_commerce_csr_popupPanelCloseButton"></div>
      <div>
        <%--When there is an error, display the error on the page. --%>
        <dsp:droplet name="Switch">
          <dsp:param bean="UpdateElectronicShippingGroupFormHandler.formError"
                     name="value"/>
          <dsp:oparam name="true">
            &nbsp;<br/><br/>
            <span class="atg_commerce_csr_common_content_alert"><fmt:message key="common.error.header"/></span>
            <br>
          <span class="atg_commerce_csr_common_content_alert">
          <UL>
            <dsp:droplet name="ErrorMessageForEach">
              <dsp:param bean="UpdateElectronicShippingGroupFormHandler.formExceptions"
                         name="exceptions"/>
              <dsp:oparam name="output">
                <LI>
                  <dsp:valueof param="message"/>
              </dsp:oparam>
            </dsp:droplet>
          </UL>
          </span>
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
                   bean="UpdateElectronicShippingGroupFormHandler.updateShippingGroup"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="UpdateElectronicShippingGroupFormHandler.updateShippingGroupErrorURL"/>

        <dsp:input type="hidden" value="${successErrorURL }"
                   bean="UpdateElectronicShippingGroupFormHandler.updateShippingGroupSuccessURL"/>

        <dsp:input type="hidden" bean="UpdateElectronicShippingGroupFormHandler.shippingGroupByNickname"
                   value="${fn:escapeXml(nickname) }" priority="5"/>

        <c:if test="${empty updateShippingGroupFormHandler.shippingGroup }">
          <dsp:setvalue bean="UpdateElectronicShippingGroupFormHandler.shippingGroupByNickname"
                        value="${fn:escapeXml(nickname) }"/>
        </c:if>

        <ul class="atg_dataForm atg_commerce_csr_addressForm">
          <dsp:include src="${electronicAddressForm.URL}" otherContext="${electronicAddressForm.servletContext}">
            <dsp:param name="formId" value="${formId}"/>
            <dsp:param name="addressBean"
                       value="/atg/commerce/custsvc/order/UpdateElectronicShippingGroupFormHandler.workingShippingGroup"/>
            <dsp:param name="submitButtonId" value="${formId}SaveButton"/>
          </dsp:include>
        </ul>
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
      </dsp:form>
      <%-- end of editShippingAddressForm --%>
    </div>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/editElectronicShippingGroup.jsp#1 $$Change: 946917 $--%>
