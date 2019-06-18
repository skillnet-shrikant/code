<%--
This page defines the address view in the single shipping address page.

This page is basically used in the Ship To Address section which is the top page in the 
shipping address page.

Expected params
shippingGroup : The shipping group from which the shipping group information is going to be retrieved.
addressKey : The current shipping shipping group name or the map key.
formId : the current form id in which the form elements are rendered.
selectedNickname : the current shipping group name which is used to ship all the items. If all commerce
                   items in the order is pointing to the same shipping group name, then that shipping address
                   should be pre selected in UI. This shipping group name allows us to identify that shipping
                   group.
commonShippingGroupTypes : This parameter tells the common shipping group types (intersection of all commerce items 
                           shipping group types) for the entire order.


@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/addressView.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"/>

  <dsp:getvalueof var="addressKey" param="addressKey"/>
  <dsp:getvalueof var="commonShippingGroupTypes" param="commonShippingGroupTypes"/>
  <dsp:getvalueof var="formId" param="formId"/>
  <dsp:getvalueof var="selectedNickname" param="selectedNickname"/>
  <dsp:getvalueof var="shippingGroup" param="shippingGroup"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">
    <svc-ui:frameworkUrl var="successURL" panelStacks="cmcShippingMethodPS" init="true"/>
    <svc-ui:frameworkUrl var="electronicShippingGroupSuccessURL" panelStacks="cmcBillingPS" init="true"/>

    <dsp:getvalueof var="sgType" param="shippingGroup.shippingGroupClassType"/>
    <dsp:contains var="found" values="${commonShippingGroupTypes}" object="${sgType}"/>

    <c:if test="${!found && sgType != 'inStorePickupShippingGroup'}">
      <script type="text/javascript">
        _container_.onLoadDeferred.addCallback(function() {
          //dijit.byId('${shippingGroup.id}').disableButton();
        });
      </script>
    </c:if>


    <div class="atg_commerce_csr_addressView">

      <fmt:message var="shipToSingleAddress" key="newOrderSingleShipping.button.shipToAddress"/>
      <dsp:getvalueof var="sgType" param="shippingGroup.shippingGroupClassType"/>
      <c:if test="${sgType != 'inStorePickupShippingGroup'}">
        <c:choose>
          <c:when test="${!empty addressKey && selectedNickname eq addressKey }">
            <c:set var="selectedAddressKey" value="${addressKey}" scope="request"/>
            <%-- Evaluated on shipping address page for the selected address --%>
            <ul id="atg_commerce_csr_neworder_ShippingAddressHome"
                class="atg_svc_shipAddress addressSelect currentSelected">
          </c:when>
          <c:when test="${!(selectedNickname eq addressKey)}">
            <%-- Evaluated on shipping address page for an unselected address --%>
            <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
          </c:when>
          <c:otherwise>
            <%-- Evaluated on shipping method page --%>
            <ul id="atg_commerce_csr_neworder_ShippingAddressHome" class="atg_svc_shipAddress addressSelect">
          </c:otherwise>
        </c:choose>
      </c:if>

      <dsp:getvalueof var="sgTypeConfig" bean="CSRConfigurator.shippingGroupTypeConfigurationsAsMap.${sgType}"/>
      <c:if test="${sgTypeConfig != null && sgTypeConfig.displayPageFragment != null}">
        <dsp:include src="${sgTypeConfig.displayPageFragment.URL}"
                     otherContext="${sgTypeConfig.displayPageFragment.servletContext}">
          <dsp:param name="shippingGroup" param="shippingGroup"/>
          <dsp:param name="propertyName" value="value1"/>
          <dsp:param name="displayValue" value="${true}"/>
        </dsp:include>
      </c:if>

      <dsp:getvalueof var="sgTypeConfig" bean="CSRConfigurator.shippingGroupTypeConfigurationsAsMap.${sgType}"/>
      <c:if test="${sgTypeConfig != null && sgTypeConfig.editPageFragment != null}">
        <c:url var="editAddressURL" context="/${sgTypeConfig.editPageFragment.servletContext}"
               value="${sgTypeConfig.editPageFragment.URL}">
          <c:param name="nickname" value="${addressKey}"/>
          <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
        </c:url>

        <li class="atg_commerce_csr_editAddress">
        <span class="atg_svc_shipAddressControls">
          <a class="atg_tableIcon atg_propertyEdit" style="min-width:100px" title="<fmt:message key="common.address.edit.mouseover"/>" href="#"
               onclick="atg.commerce.csr.common.showPopupWithReturn({
                        popupPaneId: 'csrEditAddressFloatingPane',
                        title: '<fmt:message key="common.edit"/>',
                        url: '${editAddressURL}',
                        onClose: function( args ) {
                          if ( args.result == 'ok' ) {
                            atgSubmitAction({
                              panelStack :[ 'cmcShippingAddressPS','globalPanels'],
                              form : document.getElementById('transformForm')
                            });
                          }
                        }});return false;">
             <fmt:message key="common.edit"/>
          </a>
        </span>
        </li>
      </c:if>
      <c:if test="${sgType != 'inStorePickupShippingGroup'}">
        <li class="atg_commerce_csr_shippingControls">
          <input type="button" name="shipToButton" value="${shipToSingleAddress}"
          onclick="atg.commerce.csr.order.shipping.shipToAddress('${addressKey}', ${sgType == 'electronicShippingGroup'});return false;"
          dojoType="atg.widget.validation.SubmitButton"
          id="${shippingGroup.id}"/>
        </li>
      </c:if>
      </ul>
    </div>
    
  </dsp:layeredBundle>
</dsp:page>
<script type="text/javascript">
  atg.commerce.csr.order.shipping.shipToAddress = function(addressKey, isElectronicShippingGroup) {
    document.getElementById("${formId}")["/atg/commerce/custsvc/order/ShippingGroupFormHandler.shipToAddressNickname"].value = addressKey;
    document.getElementById("defaultShippingGroupName").value=addressKey;
    if (isElectronicShippingGroup) {
      document.getElementById("${formId}")["successURL"].value = "${electronicShippingGroupSuccessURL}";
      document.getElementById("${formId}").removeChild(document.getElementById("${formId}")["successURL2"]);
    } else {
      document.getElementById("${formId}")["successURL"].value = "${successURL}";
      document.getElementById("${formId}")["successURL2"].value = "${successURL}";
    }
    atg.commerce.csr.order.shipping.applySingleShippingGroup({form:'singleShippingAddressForm'});
  };
  dojo.addOnLoad(function() {
    var theForm = dojo.byId("${formId}");
    if ((theForm != null) && (theForm.shipToButton != null) && (theForm.shipToButton.focus)) {
      theForm.shipToButton.focus();
    }
  });
</script>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/includes/addressView.jsp#1 $$Change: 946917 $--%>