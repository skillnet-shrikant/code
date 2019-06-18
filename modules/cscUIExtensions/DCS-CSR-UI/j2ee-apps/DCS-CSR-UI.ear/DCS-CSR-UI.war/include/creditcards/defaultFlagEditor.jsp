<%--

Implements the checkbox to set a given credit card to the default of
some type. That type is determined by the
DefaultCreditCardReferneceManager's credit card property.

@param options the defaultCreditCardEditorOptions map
@param formHandlerPath the component path of the CreditCardFormHandler
@param formHandler the form handler varible from the dsp:importbean tag

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/defaultFlagEditor.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:getvalueof var="fhp" param="formHandlerPath"/>
  <dsp:getvalueof var="fh" param="formHandler"/>
  <dsp:getvalueof var="opts" param="options"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:input type="checkbox" checked="${opts['default']}"
        disabled="${opts['default'] or not opts.canSetDefault}"
        bean="${fhp}.creditCardMetaInfo.params.defaultCreditCardSetDefault"/>
      <fmt:message key="creditCard.makeDefaultCreditCard"/>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/creditcards/defaultFlagEditor.jsp#1 $$Change: 946917 $--%>
