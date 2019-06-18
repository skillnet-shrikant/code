<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean var="CSRConfigurator"
                  bean="/atg/commerce/custsvc/util/CSRConfigurator"/>
  <dsp:importbean var="pgConfig"
                  bean="/atg/commerce/custsvc/ui/CreditCardConfiguration"/>

  <dsp:getvalueof var="paymentGroup" param="paymentGroup"/>
  <dsp:getvalueof var="paymentGroupKey" param="paymentGroupKey"/>

  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <dsp:getvalueof var="pgType"
                    param="paymentGroup.paymentGroupClassType"/>
    <dsp:getvalueof var="pgTypeConfig"
                    bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}"/>
                    
    <dsp:layeredBundle basename="${pgConfig.resourceBundle}">
      <fmt:message var="editPageFragmentTitle" key="${pgConfig.editPageFragmentTitleKey}"/>
    </dsp:layeredBundle>

    <%--If the page fragment is configured for a specific payment type, then display the edit link.
    Also before displaying the pop up page, save the user input data such as amount entered in
    the payment method and then serve the pop up page. --%>

    <c:if
      test="${pgTypeConfig != null && pgTypeConfig.editPageFragment != null}">
      <c:url var="editPaymentOptionURL" 
             context="/${pgTypeConfig.editPageFragment.servletContext}"
             value="${pgTypeConfig.editPageFragment.URL}">
        <c:param name="nickname" value="${paymentGroupKey}"/>
        <c:param name="${stateHolder.windowIdParameterName}"
                 value="${windowId}"/>
      </c:url>
      <fmt:message key="common.edit.title" var="editLabel"/>
      <a href="#" class="atg_tableIcon atg_propertyEdit"
         title="<fmt:message key='common.paymentOption.edit.mouseover' />"
         onclick="atg.commerce.csr.order.billing.saveUserInputAndShowPopup('${editPaymentOptionURL}');return false;">
        <fmt:message key="common.edit"/>
      </a>
    </c:if>
    <%-- Before displaying the edit payment option popup page, save the user input in the page such as amount.--%>

    <script type="text/javascript">
      atg.commerce.csr.order.billing.saveUserInputAndShowPopup = function(pURL) {
        var returnValue = atg.commerce.csr.order.billing.saveUserInput();
        if (returnValue == true) {
          atg.commerce.csr.common.showPopupWithReturn({
            popupPaneId: 'editPaymentOptionFloatingPane',
            title: '<fmt:message key="common.edit"/> <c:out value="${editPageFragmentTitle}"/>',
            url: pURL,
            onClose: function(args) {
              if (args.result == 'ok') {
                atgSubmitAction({
                  panelStack : ['cmcBillingPS','globalPanels'],
                  form : document.getElementById('transformForm')
                });
              }
            }});
        }
      }
    </script>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/billing/displayPmtEndFrag.jsp#1 $$Change: 946917 $--%>
