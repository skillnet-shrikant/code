<%--
This page provides the option to add electronic shipping group.
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/addElectronicShippingGroup.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/CreateElectronicShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean var="addElectronicShippingGroup"
                  bean="/atg/commerce/custsvc/ui/fragments/order/AddElectronicShippingGroup"/>
  <dsp:importbean var="electronicShippingGroupConfig"
                  bean="/atg/commerce/custsvc/ui/ElectronicShippingGroupConfiguration"/>                  
  <dsp:importbean var="electronicAddressForm" bean="/atg/commerce/custsvc/ui/fragments/order/ElectronicAddressForm"/>

  <c:set var="formId" value="csrAddElectronicAddress"/>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcShippingAddressPS"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingAddressPS"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <dsp:form id="${formId}" formid="${formId}">
    <dsp:input type="hidden" priority="-10" value=""
               bean="CreateElectronicShippingGroupFormHandler.newElectronicShippingGroup"/>

    <dsp:input type="hidden" value="${errorURL }" name="errorURL"
               bean="CreateElectronicShippingGroupFormHandler.newElectronicShippingGroupErrorURL"/>

    <dsp:input type="hidden" value="${successURL }" name="successURL"
               bean="CreateElectronicShippingGroupFormHandler.newElectronicShippingGroupSuccessURL"/>

    <%-- If the tab container is used by the shipping address page, then this snippet below selects the right
    tab on error. --%>
    <dsp:droplet name="Switch">
      <dsp:param bean="CreateElectronicShippingGroupFormHandler.formError"
                 name="value"/>
      <dsp:oparam name="true">
        <script type="text/javascript">
          _container_.onLoadDeferred.addCallback(function () {
            var sgAddTabContainer = dijit.byId("shippingAddressAddContainer");
            if (sgAddTabContainer) sgAddTabContainer.selectChild("${electronicShippingGroupConfig.type}");
          });
        </script>
      </dsp:oparam>
    </dsp:droplet>

    <ul class="atg_dataForm atg_commerce_csr_addressForm"
        id="atg_commerce_csr_neworder_newShippingAddress">

      <dsp:include src="${electronicAddressForm.URL}" otherContext="${electronicAddressForm.servletContext}">
        <dsp:param name="formId" value="${formId}"/>
        <dsp:param name="addressBean"
                   value="/atg/commerce/custsvc/order/CreateElectronicShippingGroupFormHandler.electronicShippingGroup"/>
        <dsp:param name="submitButtonId" value="electronicShippingAddAddressButton"/>
      </dsp:include>

      <li class="atg_svc_formActions">
        <div class="atg_commerce_csr_panelFooter">
          <input type="button" name="electronicShippingAddAddressButton"
                 id="electronicShippingAddAddressButton"
                 onclick="atg.commerce.csr.order.shipping.addElectronicAddress();return false;"
                 value="<fmt:message key="newOrderSingleShipping.addShippingAddress.button.addAddress"/>"
          form="${formId}"
          dojoType="atg.widget.validation.SubmitButton"
          disabled="disabled"
          />
        </div>
      </li>
    </ul>
  </dsp:form>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/addElectronicShippingGroup.jsp#2 $$Change: 1179550 $--%>

