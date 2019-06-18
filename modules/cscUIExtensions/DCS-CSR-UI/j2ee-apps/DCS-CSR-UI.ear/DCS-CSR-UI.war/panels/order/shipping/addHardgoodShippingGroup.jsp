<%--
This page provides the option to add hard good shipping group.
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/addHardgoodShippingGroup.jsp#2 $
@updated $DateTime: 2015/07/10 11:58:13 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/CreateHardgoodShippingGroupFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean var="addHardgoodShippingGroup"
                  bean="/atg/commerce/custsvc/ui/fragments/order/AddHardgoodShippingGroup"/>
  <dsp:importbean var="hardgoodShippingGroupConfig"
                  bean="/atg/commerce/custsvc/ui/HardgoodShippingGroupConfiguration"/>                  
  <dsp:importbean var="addressForm" bean="/atg/svc/agent/ui/fragments/AddressForm"/>

  <c:set var="formId" value="csrAddShippingAddress"/>
  <svc-ui:frameworkUrl var="successURL" panelStacks="cmcShippingAddressPS,globalPanels"/>
  <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingAddressPS"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <dsp:form id="${formId}" formid="${formId}">
      <dsp:input type="hidden" priority="-10" value=""
                 bean="CreateHardgoodShippingGroupFormHandler.newHardgoodShippingGroup"/>

      <dsp:input type="hidden" value="${errorURL }" name="errorURL"
                 bean="CreateHardgoodShippingGroupFormHandler.newHardgoodShippingGroupErrorURL"/>

      <dsp:input type="hidden" value="${successURL }" name="successURL"
                 bean="CreateHardgoodShippingGroupFormHandler.newHardgoodShippingGroupSuccessURL"/>

      <%-- If the tab container is used by the shipping address page, then this snippet below selects the right
      tab on error. --%>
      <dsp:droplet name="Switch">
        <dsp:param bean="CreateHardgoodShippingGroupFormHandler.formError"
                   name="value"/>
        <dsp:oparam name="true">
          <script type="text/javascript">
            _container_.onLoadDeferred.addCallback(function () {
              var sgAddTabContainer = dijit.byId("shippingAddressAddContainer");
              if (sgAddTabContainer) sgAddTabContainer.selectChild("${hardgoodShippingGroupConfig.type}");
            });
          </script>
        </dsp:oparam>
      </dsp:droplet>

      <div class="atg-csc-base-table atg-base-table-customer-address-add-form"
          id="atg_commerce_csr_neworder_newShippingAddress">
        <dsp:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
          <dsp:param name="formId" value="${formId}"/>
          <dsp:param name="formHandler" value="/atg/commerce/custsvc/order/CreateHardgoodShippingGroupFormHandler"/>
          <dsp:param name="addressBean"
                     value="/atg/commerce/custsvc/order/CreateHardgoodShippingGroupFormHandler.hardgoodShippingGroup.shippingAddress"/>
          <dsp:param name="submitButtonId" value="addShippingAddressButton"/>
        </dsp:include>

      </div>

      <div class="atg_svc_saveProfile">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param
              bean="/atg/userprofiling/ActiveCustomerProfile.transient"
              name="value"/>
          <dsp:oparam name="false">
            <div>
              <dsp:input type="checkbox" checked="${true}"
                        bean="CreateHardgoodShippingGroupFormHandler.addToProfile" name="addToProfile"/>
              <fmt:message key="newOrderSingleShipping.addShippingAddress.field.saveToProfile"/>
            </div>
          </dsp:oparam>
          <dsp:oparam name="true">
            <dsp:input type="hidden" bean="CreateHardgoodShippingGroupFormHandler.addToProfile" value="false"/>
          </dsp:oparam>
        </dsp:droplet>
      </div>

      <div class="atg_svc_formActions">
        <div>
          <input type="button" name="addShippingAddressButton"
                    id="addShippingAddressButton"
                    class="atg_commerce_csr_activeButton"
                    onclick="atg.commerce.csr.order.shipping.addShippingAddress();return false;"
                    value="<fmt:message key="newOrderSingleShipping.addShippingAddress.button.addAddress"/>"
                form="${formId}"
                dojoType="atg.widget.validation.SubmitButton"/>
        </div>
      </div>
     
    </dsp:form>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/addHardgoodShippingGroup.jsp#2 $$Change: 1179550 $--%>

