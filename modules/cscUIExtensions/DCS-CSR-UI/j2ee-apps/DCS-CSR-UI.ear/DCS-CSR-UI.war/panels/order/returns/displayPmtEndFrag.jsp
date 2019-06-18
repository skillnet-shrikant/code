<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">
	<dsp:importbean var="CSRConfigurator"
		bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean var="pgConfig"
    bean="/atg/commerce/custsvc/ui/CreditCardConfiguration"/>
		
	<dsp:getvalueof var="refundMethod" param="refundMethod" />

	<dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">

		<dsp:getvalueof var="pgType" param="refundMethod.refundType" />
  <dsp:getvalueof var="refundMethodIndex" param="refundMethodIndex" />
		<dsp:getvalueof var="pgTypeConfig"
			bean="CSRConfigurator.paymentGroupTypeConfigurationsAsMap.${pgType}" />

		<%--If the page fragment is configured for a specific payment type, then display the edit link.
        Also before displaying the pop up page, save the user input data such as amount entered in
        the payment method and then serve the pop up page. --%>

		<c:if
			test="${!empty pgTypeConfig && !empty pgTypeConfig.editRefundMethodPageFragment}">
			
      <dsp:layeredBundle basename="${pgConfig.resourceBundle}">
       <fmt:message var="editPageFragmentTitle" key="${pgConfig.editRefundMethodPageFragmentTitleKey}"/>
      </dsp:layeredBundle>
      
			
			<c:url var="editPaymentOptionURL"
				context="/${pgTypeConfig.editRefundMethodPageFragment.servletContext}"
				value="${pgTypeConfig.editRefundMethodPageFragment.URL}">
     <c:param name="refundMethodIndex" value="${refundMethodIndex}" />
				<c:param name="${stateHolder.windowIdParameterName}"
					value="${windowId}" />
			</c:url>
			<fmt:message key="common.edit.title" var="editLabel" />
			<a href="#" class="atg_tableIcon atg_propertyEdit"
				title="<fmt:message key='common.edit' />"
				onclick="atg.commerce.csr.common.showPopupWithReturn({
                popupPaneId: 'editPaymentOptionFloatingPane',
                title: '<fmt:message key="common.edit"/> <c:out value="${editPageFragmentTitle}"/>',
                url: '${editPaymentOptionURL}',
                onClose: function( args ) {
                  if ( args.result == 'ok' ) {
                    atgSubmitAction({
                    panelStack : 'cmcRefundTypePS',
                    form : document.getElementById('transformForm')
                  })}}});return false;">
			<fmt:message key='common.edit' /></a>
		</c:if>
	</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/displayPmtEndFrag.jsp#1 $$Change: 946917 $--%>
