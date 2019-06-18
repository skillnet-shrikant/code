<%--
This page defines the shipping address panel
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/shippingAddress.jsp#3 $
@updated $DateTime: 2015/08/28 11:29:26 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ApplicableShippingGroups"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler" var="shippingGroupFormHandler"/>
<dsp:importbean var="shippingAddressNextStep"
                bean="/atg/commerce/custsvc/ui/fragments/order/SingleShippingAddressNextStep"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupDroplet"/>
<dsp:importbean var="container" bean="/atg/commerce/custsvc/order/ShippingGroupContainerService"/>
<dsp:getvalueof var="init" param="init"/>
<dsp:getvalueof var="select" param="select"/>

<script type="text/javascript">
  atg.commerce.csr.order.shipping.onToggleMultipleAddress = function() {
    if (dijit.byId("atg_commerce_csr_shipToMultipleAddresses").open &&
        dijit.byId("addressGrid")) {
      dijit.byId("addressGrid").render();
    }
  };
  _container_.onLoadDeferred.addCallback(function() {
    dojo.connect(dijit.byId("atg_commerce_csr_shipToMultipleAddresses"), "toggle",
      atg.commerce.csr.order.shipping.onToggleMultipleAddress);
    atg.commerce.csr.order.shipping.notifySingleShippingValidators();
  });  
  atg.progress.update('cmcShippingAddressPS');
  if (!dijit.byId("csrMultipleShippingFloatingPane")) {
    new dojox.Dialog({ id: "csrMultipleShippingFloatingPane",
      cacheContent: "false",
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"});
  }
  if (!dijit.byId("csrEditAddressFloatingPane")) {
    new dojox.Dialog({ id: "csrEditAddressFloatingPane",
      cacheContent: "false",
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"});
  }
  if (!dijit.byId("productQuickViewPopup")) {
    new dojox.Dialog({ id: "productQuickViewPopup",
      cacheContent: "false",
      executeScripts: "true",
      scriptHasHooks: "true",
      duration: 100,
      "class": "atg_commerce_csr_popup"});
  }
</script>
<dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

<dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
<c:set var="order" value="${cart.current}" />

<%-- When init param is true, this will clear the shipping groups and commerce item relationships --%>
<dsp:droplet name="ShippingGroupDroplet">
  <%--<dsp:param name="clear" param="init"/>--%>
  <dsp:param name="shippingGroupTypes" bean="CSRConfigurator.shippingGroupTypesToBeInitialized"/>
  <c:if test="${init}">
    <dsp:param name="clearShippingInfos" value="true"/>
    <dsp:param name="initShippingGroups" param="init"/>
    <dsp:param name="initShippingInfos" param="init"/>
    <dsp:param name="initBasedOnOrder" param="init"/>
  </c:if>
  <dsp:oparam name="output">
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="ApplicableShippingGroups">
  <dsp:param name="order" value="${order}"/>
  <dsp:param name="sgMapContainer" value="${container}"/>
  <dsp:param name="cisiContainer" value="${container}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="commonShippingGroupTypes" param="commonShippingGroupTypes"/>
    <dsp:getvalueof var="allShippingGroupTypes" param="allShippingGroupTypes"/>
    <dsp:getvalueof var="applicableShippingGroups" param="shippingGroups"/>
  </dsp:oparam>
</dsp:droplet>
<c:choose>
  <c:when test="${empty applicableShippingGroups}">
    <c:set var="addDestinationSelected" value=" selected='true'" />
    <c:set var="deliverToOnePlaceSelected" value="" />
  </c:when>
  <c:otherwise>
    <c:set var="addDestinationSelected" value="" />
    <c:set var="deliverToOnePlaceSelected" value=" selected='true'" />
  </c:otherwise>
</c:choose>

<c:set var="canBePickupUpInStore" value="false" />
<c:forEach items="${order.commerceItems}" var="item" varStatus="vs">
  <dsp:droplet name="/atg/commerce/catalog/OnlineOnlyDroplet">
    <dsp:param name="sku" value="${item.auxiliaryData.catalogRef}"/>
    <dsp:param name="product" value="${item.auxiliaryData.productRef}"/>
    <dsp:oparam name="false">
      <c:set var="canBePickupUpInStore" value="true" />
    </dsp:oparam>
  </dsp:droplet>
</c:forEach>

<c:choose>
  <c:when test="${fn:length(allShippingGroupTypes) == 1 && allShippingGroupTypes[0] == 'hardgoodShippingGroup'}">
    <c:set var="shippingAddressSelected" value=" selected='true'" />
    <c:set var="emailSelected" value="" />
    <c:set var="pickupSelected" value="" />
  </c:when>
  <c:when test="${fn:length(allShippingGroupTypes) == 1 && allShippingGroupTypes[0] == 'electronicShippingGroup'}">
    <c:set var="shippingAddressSelected" value="" />
    <c:set var="emailSelected" value=" selected='true'" />
    <c:set var="pickupSelected" value="" />
  </c:when>
  <c:when test="${CSRConfigurator.usingInStorePickup && canBePickupUpInStore}">
    <c:set var="shippingAddressSelected" value="" />
    <c:set var="emailSelected" value="" />
    <c:set var="pickupSelected" value=" selected='true'" />
  </c:when>
</c:choose>

<c:set var="shipToMultipleSelected" value="" />
<c:if test="${select eq 'multiple'}">
  <c:set var="shipToMultipleSelected" value=" selected='true'" />
  <c:set var="addDestinationSelected" value="" />
  <c:set var="deliverToOnePlaceSelected" value="" />
</c:if>

<div dojoType="dijit.layout.AccordionContainer" duration="200" sizeMin="20" sizeShare="38">

  <div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderSingleShipping.header.addDestination' />" ${addDestinationSelected}>
    <div dojoType="dijit.layout.TabContainer">
      <dsp:contains var="allowable" values="${allShippingGroupTypes}" object="hardgoodShippingGroup"/>
      <c:if test="${allowable}">
        <div dojoType="dijit.layout.ContentPane" title="<fmt:message key='newOrderSingleShipping.header.shippingAddress' />" ${shippingAddressSelected}>
          <dsp:importbean bean="/atg/commerce/order/purchase/CreateInStorePickupShippingGroupFormHandler" var="createInStorePickupShippingGroupFormHandler"/>
          <dsp:form action="" method="post" id="createInStorePickupShippingGroupForm">
            <dsp:input bean="CreateInStorePickupShippingGroupFormHandler.newInStorePickupShippingGroupSuccessURL" type="hidden" value="" />
            <dsp:input bean="CreateInStorePickupShippingGroupFormHandler.newInStorePickupShippingGroupErrorURL" type="hidden" value="" />
            <dsp:input bean="CreateInStorePickupShippingGroupFormHandler.inStorePickupShippingGroup.locationId" type="hidden"  value="" id="locationId" />
            <dsp:input bean="CreateInStorePickupShippingGroupFormHandler.newInStorePickupShippingGroup" priority="-10" type="hidden" value="Create InStorePickupShippingGroup" onclick="atg.commerce.csr.catalog.createInStorePickupShippingGroupForm()" />
          </dsp:form>


          <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemsCountDroplet">
            <dsp:param name="order" value="${order}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="totalCommerceItemCount" param="count"/>
            </dsp:oparam>
          </dsp:droplet>

          <c:set var="itemCount" value="${totalCommerceItemCount}"/>
          <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingAddressPS"/>
          
          <c:set var="formId" value="singleShippingAddressForm"/>
          <c:set var="shippingGroupCount" value="${0}"/>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" value="${applicableShippingGroups}"/>
            <dsp:param name="elementName" value="shippingGroup"/>
            <dsp:param name="indexName" value="index"/>
            <dsp:oparam name="empty">
              <fmt:message key="newOrderSingleShipping.info.addAddress"/>
            </dsp:oparam>
            <dsp:oparam name="outputStart">
            </dsp:oparam>
            <dsp:oparam name="output">
            </dsp:oparam>
          </dsp:droplet>

          <div id="atg_commerce_csr_addNewShippingAddress">

            <%-- If there is no supported shipping group types available, display the following error message. The
            list of possible
            shipping group types were fetched from ApplicableShippingGroups. --%>
            <c:if test="${fn:length(allShippingGroupTypes) == 0}">
              <fmt:message key="newOrderSingleShipping.noSupportedShippingGroupTypes"/>
            </c:if>

            <c:if test="${fn:length(allShippingGroupTypes)  > 0}">
              <dsp:getvalueof var="sgTypeConfigs" bean="CSRConfigurator.shippingGroupTypeConfigurations"/>

              <%-- Determine how many add fragments are in the shipping group type configs.--%>
              <c:set var="addPageFragmentCount" value="${0}"/>
              <c:forEach var="sgTypeConfig" items="${sgTypeConfigs}">
                <dsp:contains var="allowable" values="${allShippingGroupTypes}" object="${sgTypeConfig.type}"/>
                <c:if
                  test="${allowable&& addPageFragmentCount < 2 && sgTypeConfig!= null && sgTypeConfig.addPageFragment != null }">
                  <c:set var="addPageFragmentCount" value="${addPageFragmentCount + 1}"/>
                </c:if>
              </c:forEach>


              <%-- If there is only one supported shipping group types available, display add new address page
              without tab. --%>

              <c:if test="${addPageFragmentCount == 1}">
                <c:forEach var="sgTypeConfig" items="${sgTypeConfigs}">
                  <dsp:contains var="allowable" values="${allShippingGroupTypes}"
                                object="${sgTypeConfig.type}"/>

                  <c:if test="${allowable && sgTypeConfig.addPageFragment != null && sgTypeConfig.type != 'electronicShippingGroup'}">

                    <dsp:include src="${sgTypeConfig.addPageFragment.URL}"
                                 otherContext="${sgTypeConfig.addPageFragment.servletContext}">
                    </dsp:include>
                  </c:if>
                </c:forEach>
              </c:if>

              <%-- If there is more than one supported shipping group types available, display add new address
              page with tab. --%>

              <c:if test="${addPageFragmentCount > 1}">

                <c:forEach var="sgTypeConfig" items="${sgTypeConfigs}">
                <dsp:contains var="allowable" values="${allShippingGroupTypes}"
                                  object="${sgTypeConfig.type}"/>

                  <c:if test="${allowable && sgTypeConfig.addPageFragment != null &&
                      sgTypeConfig.type != 'electronicShippingGroup'}">

                  <dsp:layeredBundle basename="${sgTypeConfig.resourceBundle}">
                    <fmt:message var="addPageFragmentTitle"
                                 key="${sgTypeConfig.addPageFragmentTitleKey}"/>
                  </dsp:layeredBundle>
                    <div id="${sgTypeConfig.type}" dojoType="dojox.layout.ContentPane"
                         class="atg_commerce_csr_shipAddressTabPane" executeScripts="true"
                         scriptHasHooks="true" title="${addPageFragmentTitle}">
                      <dsp:include src="${sgTypeConfig.addPageFragment.URL}"
                                   otherContext="${sgTypeConfig.addPageFragment.servletContext}">
                      </dsp:include>
                    </div>
                  </c:if>
                </c:forEach>
              </c:if>
            </c:if>
          </div>
        </div>
      </c:if>
      <c:if test="${CSRConfigurator.usingInStorePickup && canBePickupUpInStore}">
        <div dojoType="dijit.layout.ContentPane" title="<fmt:message key='newOrderSingleShipping.header.storePickup' />" ${pickupSelected}>
          <dsp:include src="/panels/catalog/shippingPickupLocations.jsp" otherContext="${CSRConfigurator.contextRoot}" />
        </div>
      </c:if>
      <dsp:contains var="allowable" values="${allShippingGroupTypes}" object="electronicShippingGroup"/>
      <c:if test="${allowable}">
        <div dojoType="dijit.layout.ContentPane" title="<fmt:message key='newOrderSingleShipping.header.email' />" ${emailSelected}>
          <div style="margin-top:20px">
            <dsp:include src="/panels/order/shipping/addElectronicShippingGroup.jsp" otherContext="${CSRConfigurator.contextRoot}" />
          </div>
        </div>
      </c:if>
    </div>
  </div>

  <div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderSingleShipping.header.deliverToOnePlace' />" ${deliverToOnePlaceSelected}>

    <dsp:droplet name="/atg/commerce/custsvc/order/GetCommerceItemsCountDroplet">
      <dsp:param name="order" value="${order}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="totalCommerceItemCount" param="count"/>
      </dsp:oparam>
    </dsp:droplet>

    <c:set var="itemCount" value="${totalCommerceItemCount}"/>
    <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingAddressPS"/>

    <div id="singleShippingAddressContainer">
    <c:set var="formId" value="singleShippingAddressForm"/>
    <dsp:form id="${formId}" formid="${formId}">
      <dsp:input type="hidden" priority="-10" value="" bean="ShippingGroupFormHandler.singleShippingGroupCheckout"/>
      <dsp:input bean="ShippingGroupFormHandler.shipToAddressNickname" type="hidden"/>
      <dsp:input type="hidden" value="false" name="persistOrder" bean="ShippingGroupFormHandler.persistOrder"/>
      <dsp:input type="hidden" value="${errorURL}" name="errorURL" bean="ShippingGroupFormHandler.singleShippingGroupCheckoutErrorURL"/>
      
      <svc-ui:frameworkUrl var="successURL" panelStacks="cmcShippingMethodPS" init="true"/>
      <dsp:input type="hidden" value="${successURL}" name="successURL2" id="successURL2" bean="ShippingGroupFormHandler.singleShippingGroupCheckoutSuccessURL"/>
      
      <dsp:input id="defaultShippingGroupName" bean="ShippingGroupFormHandler.shippingGroupMapContainer.defaultShippingGroupName" type="hidden" value=""/>
      <dsp:input bean="ShippingGroupFormHandler.applyDefaultShippingGroup" type="hidden" value="true"/> 

      <dsp:include src="${shippingAddressNextStep.URL}"
                   otherContext="${shippingAddressNextStep.servletContext}">
      </dsp:include>

      <c:set var="shippingGroupCount" value="${0}"/>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${applicableShippingGroups}"/>
        <dsp:param name="elementName" value="shippingGroup"/>
        <dsp:param name="indexName" value="index"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="shippingGroupClassType" param="shippingGroup.shippingGroupClassType"/>
          <c:set var="shippingGroupCount" value="${shippingGroupCount + 1}"/>
          <c:if test="${shippingGroupClassType == 'inStorePickupShippingGroup'}">                
            <dsp:include src="/panels/order/shipping/includes/addressView.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="shippingGroup" param="shippingGroup"/>
              <dsp:param name="addressKey" param="key"/>
              <dsp:param name="stores" param="${stores}"/>
              <dsp:param name="selectedNickname" param="commonShippingGroupName"/>
              <dsp:param name="formId" value="${formId}"/>
              <dsp:param name="commonShippingGroupTypes" param="commonShippingGroupTypes"/>
            </dsp:include>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
      
      <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${applicableShippingGroups}"/>
        <dsp:param name="elementName" value="shippingGroup"/>
        <dsp:param name="indexName" value="index"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="shippingGroupClassType" param="shippingGroup.shippingGroupClassType"/>
          <c:if test="${shippingGroupClassType != 'inStorePickupShippingGroup'}">
            <dsp:include src="/panels/order/shipping/includes/addressView.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="shippingGroup" param="shippingGroup"/>
              <dsp:param name="addressKey" param="key"/>
              <dsp:param name="stores" param="${stores}"/>
              <dsp:param name="selectedNickname" param="commonShippingGroupName"/>
              <dsp:param name="formId" value="${formId}"/>
              <dsp:param name="commonShippingGroupTypes" param="commonShippingGroupTypes"/>
            </dsp:include>
          </c:if>
        </dsp:oparam>
      </dsp:droplet>
      <c:if test="${shippingGroupCount == 0}">
        <fmt:message key="newOrderSingleShipping.info.addAddress"/>
      </c:if>

      <%-- end of ApplicableShippingGroups --%>
    </dsp:form>
    </div>
  <div class="atg_svc_content atg_commerce_csr_content">
  </div>
    
  </div>
  <%-- 
  <div dojoType="dijit.layout.AccordionPane" title="<fmt:message key='newOrderSingleShipping.header.deliverToMultiplePlaces' />" ${shipToMultipleSelected}>
    <!-- HTML Information Marker to be Removed | Start -->
     <div style="clear:both;padding-top:20px;padding-bottom:20px">
       <c:if test="${itemCount > 1}">
          <c:set var="multishippingopenstate" value="${true}"/>
          <!-- if the order supports only one shipping group type and it has only one shipping group, then the agent may be
               interested in single shipping group. Thus collapse the multi shipping area.
          -->
          <c:if test="${itemCount > 1 && (fn:length(allShippingGroupTypes)  == 1) && (order.shippingGroupCount == 1)}">
            <c:set var="multishippingopenstate" value="${false}"/>
          </c:if>
          <div id="atg_commerce_csr_shipToMultipleAddresses">
            <dsp:include src="includes/addressTable.jsp" otherContext="${CSRConfigurator.contextRoot}"/>
          </div>
        </c:if>
      </div>
  </div>
  --%>
  <div class="atg_commerce_csr_shippingFooter">
    <fmt:message var="goBackLabel" key="common.returnToCart"/>
    <dsp:include src="/include/order/checkoutFooter.jsp" otherContext="${CSRConfigurator.contextRoot}">
      <dsp:param name="goBackLabel" value="${goBackLabel}"/>
      <dsp:param name="goBackStack" value="cmcShoppingCartPS"/>
    </dsp:include>
  </div>
  
</div>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/shippingAddress.jsp#3 $$Change: 1191722 $--%>
