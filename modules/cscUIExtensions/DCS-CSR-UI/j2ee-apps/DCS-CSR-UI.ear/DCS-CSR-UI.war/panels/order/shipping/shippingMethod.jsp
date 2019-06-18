<%--
This page defines the shipping method panel
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/shippingMethod.jsp#1 $
@updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShippingGroupFormHandler"/>
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
  <dsp:importbean var="shippingMethodNextStep" bean="/atg/commerce/custsvc/ui/fragments/order/ShippingMethodNextStep"/>

  <dsp:setLayeredBundle basename="atg.commerce.csr.order.WebAppResources"/>

  <c:set var="hardgoodShippingGroupCount" value="${0}"/>
  <c:set var="inStorePickupShippingGroupCount" value="${0}"/>
  <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="status">
    <c:if test="${shippingGroup.shippingGroupClassType == 'hardgoodShippingGroup'}">
      <c:set var="hardgoodShippingGroupCount" value="${hardgoodShippingGroupCount + 1}"/>
      <c:set var="hardgoodShippingGroupIndex" value="${status.index}"/>
      <c:set var="hardgoodShippingGroup" value="${shippingGroup}"/>
    </c:if>
    <c:if test="${shippingGroup.shippingGroupClassType == 'inStorePickupShippingGroup'}">
      <c:set var="inStorePickupShippingGroupCount" value="${inStorePickupShippingGroupCount + 1}"/>
      <c:set var="inStorePickupShippingGroupIndex" value="${status.index}"/>
      <c:set var="inStorePickupShippingGroup" value="${shippingGroup}"/>
    </c:if>
  </c:forEach>
  
  <c:set var="multipleGroups" value="${hardgoodShippingGroupCount + inStorePickupShippingGroupCount > 1}" />

  <div class="atg_svc_content atg_commerce_csr_content">

    <svc-ui:frameworkUrl var="errorURL" panelStacks="cmcShippingMethodPS"/>
    <c:set var="formId" value="csrSelectShippingMethods"/>
    <dsp:form id="${formId}" formid="${formId}">
      <dsp:input type="hidden" value="${errorURL}"
                 bean="ShippingGroupFormHandler.applyShippingMethodsErrorURL"/>
      <dsp:input type="hidden" value="" priority="-10"
                 bean="ShippingGroupFormHandler.applyShippingMethods"/>
      <dsp:input type="hidden" value="false"
                 bean="ShippingGroupFormHandler.persistOrder"/>
      <dsp:include src="${shippingMethodNextStep.URL}" otherContext="${shippingMethodNextStep.servletContext}">
      </dsp:include>

      <p>
        <fmt:message key="newOrderSingleShipping.selectShippingMethod"/>
      </p>
      
      <c:set var="shipmentIndex" value="${0}"/>

      <c:choose>
        <c:when test="${inStorePickupShippingGroupCount == 1}">

          <c:if test="${multipleGroups}">
            <fieldset>
              <legend>
                <fmt:message key="shipping.chooseMethod.shippingGroupNumber">
                  <fmt:param value="${shipmentIndex + 1}"/>
                </fmt:message>
              </legend>
          </c:if>
          <dsp:include src="/panels/order/shipping/includes/shippingGroupAuthorizedRecipientView.jsp" otherContext="${CSRConfigurator.contextRoot}">
            <dsp:param name="shippingGroupIndex" value="${inStorePickupShippingGroupIndex}"/>
            <dsp:param name="order" value="${order}"/>
            <dsp:param name="shippingGroup" value="${inStorePickupShippingGroup}"/>
          </dsp:include>
          
          <c:if test="${multipleGroups}">
            <dsp:include src="/panels/order/shipping/includes/itemTable.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
              <dsp:param name="shippingGroupIndex" value="${shipmentIndex}"/>
            </dsp:include>
            <c:set var="shipmentIndex" value="${shipmentIndex + 1}"/>
            </fieldset>
          </c:if>

        </c:when>

        <c:when test="${inStorePickupShippingGroupCount > 1}">
          <%-- Please note: There are intentionally 2 counters in this loop:
          "status" gets updated for each iteration so that we correctly index
          into the shipping group array in the submitted form.
          "shipmentIndex" gets updated only for hardgood iterations with a physical address --%>
          <c:set var="shipmentIndex" value="${0}"/>
          <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="status">
            <c:if test="${shippingGroup.shippingGroupClassType == 'inStorePickupShippingGroup'}">
              <c:set var="inStorePickupShippingGroup" value="${shippingGroup}"/>
              <fieldset>
                <legend>
                  <fmt:message key="shipping.chooseMethod.shippingGroupNumber">
                    <fmt:param value="${shipmentIndex + 1}"/>
                  </fmt:message>
                </legend>
                <dsp:include src="/panels/order/shipping/includes/shippingGroupAuthorizedRecipientView.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="shippingGroupIndex" value="${status.index}"/>
                  <dsp:param name="order" value="${order}"/>
                  <dsp:param name="shippingGroup" value="${inStorePickupShippingGroup}"/>
                </dsp:include>
                <dsp:include src="/panels/order/shipping/includes/itemTable.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
                  <dsp:param name="shippingGroupIndex" value="${shipmentIndex}"/>
                </dsp:include>
              </fieldset>
              <c:set var="shipmentIndex" value="${shipmentIndex + 1}"/>
            </c:if>
          </c:forEach>

        </c:when>
        <c:otherwise>
          <%-- No shipping groups --%>
        </c:otherwise>
      </c:choose>
      
      <c:choose>
        <c:when test="${hardgoodShippingGroupCount == 1}">

          <c:if test="${multipleGroups}">
            <fieldset>
              <legend>
                <fmt:message key="shipping.chooseMethod.shippingGroupNumber">
                  <fmt:param value="${shipmentIndex + 1}"/>
                </fmt:message>
              </legend>
          </c:if>
          <dsp:include src="/panels/order/shipping/includes/shippingGroupMethodView.jsp" otherContext="${CSRConfigurator.contextRoot}">
            <dsp:param name="shippingGroupIndex" value="${hardgoodShippingGroupIndex}"/>
            <dsp:param name="order" value="${order}"/>
            <dsp:param name="shippingGroup" value="${hardgoodShippingGroup}"/>
          </dsp:include>
          <c:if test="${multipleGroups}">
            <dsp:include src="/panels/order/shipping/includes/itemTable.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
              <dsp:param name="shippingGroupIndex" value="${shipmentIndex}"/>
            </dsp:include>
            <c:set var="shipmentIndex" value="${shipmentIndex + 1}"/>
            </fieldset>
          </c:if>

        </c:when>

        <c:when test="${hardgoodShippingGroupCount > 1}">
          <%-- Please note: There are intentionally 2 counters in this loop:
          "status" gets updated for each iteration so that we correctly index
          into the shipping group array in the submitted form.
          "shipmentIndex" gets updated only for hardgood iterations with a physical address --%>
          <c:set var="shipmentIndex" value="${0}"/>
          <c:forEach items="${order.shippingGroups}" var="shippingGroup" varStatus="status">
            <c:if test="${shippingGroup.shippingGroupClassType == 'hardgoodShippingGroup'}">
              <c:set var="hardgoodShippingGroup" value="${shippingGroup}"/>
              <fieldset>
                <legend>
                  <fmt:message key="shipping.chooseMethod.shippingGroupNumber">
                    <fmt:param value="${shipmentIndex + 1}"/>
                  </fmt:message>
                </legend>
                <dsp:include src="/panels/order/shipping/includes/shippingGroupMethodView.jsp" otherContext="${CSRConfigurator.contextRoot}">
                  <dsp:param name="shippingGroupIndex" value="${status.index}"/>
                  <dsp:param name="order" value="${order}"/>
                  <dsp:param name="shippingGroup" value="${hardgoodShippingGroup}"/>
                </dsp:include>
                <dsp:include src="/panels/order/shipping/includes/itemTable.jsp" otherContext="${CSRConfigurator.contextRoot}"> 
                  <dsp:param name="shippingGroupIndex" value="${shipmentIndex}"/>
                </dsp:include>
              </fieldset>
              <c:set var="shipmentIndex" value="${shipmentIndex + 1}"/>
            </c:if>
          </c:forEach>

        </c:when>
        <c:otherwise>
          <%-- No shipping groups --%>
        </c:otherwise>
      </c:choose>

      <dsp:include src="/panels/order/shipping/includes/promotionsListing.jsp" otherContext="${CSRConfigurator.contextRoot}">
        <dsp:param name="order" value="${order}"/>
      </dsp:include>


    <div class="atg_commerce_csr_shippingFooter">
       <a class="atg_commerce_csr_return" href="#" onclick="atgNavigate({
               panelStack : 'cmcShippingAddressPS',
               queryParams : { 'init': 'true' }});
               return false;">
       <fmt:message key='common.returnToShippingAddress' /> </a>
      <div class="atg_commerce_csr_tableControls">
        <input type="button" name="shippingMethodButton" class="atg_commerce_csr_activeButton"
               value="<fmt:message key="shipping.continueToBilling"/>"
        onclick="atg.commerce.csr.order.shipping.applySelectShippingMethods();return false;"/>
      </div>
    </div>

   </dsp:form>
  </div>



  <script type="text/javascript">
    atg.progress.update("cmcShippingMethodPS");

    dojo.addOnLoad(function() {
      var theForm = dojo.byId("csrSelectShippingMethods");
      if ((theForm != null) && (theForm.shippingMethodButton != null)) {
        theForm.shippingMethodButton.focus();
      }
    });
  </script>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/shipping/shippingMethod.jsp#1 $$Change: 946917 $--%>
