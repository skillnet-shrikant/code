<%--

  Implements the checkbox to set a given address to the default of
  some type. The type is determined by the
  DefaultAddressReferenceManger's address.

@param options the defaultAddressEditorOptions map
@param formHandlerPath the component path of the AddressBookFormHandler
@param formHandler the form handler varible from the dspel:importbean tag

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/defaultFlagEditor.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:getvalueof var="fhp" param="formHandlerPath"/>
  <dsp:getvalueof var="fh" param="formHandler"/>
  <dsp:getvalueof var="opts" param="options"/>
  <dsp:getvalueof var="optsKey" param="optionsKey"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

      <dsp:input type="checkbox" checked="${opts.set}"
        disabled="${opts['default'] or not opts.canSetDefault}"
        bean="${fhp}.addressMetaInfo.params.${optsKey}SetAddress"/>
      <fmt:message key="${opts.resourceMessageKey}"/>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/defaultFlagEditor.jsp#1 $$Change: 946917 $--%>
