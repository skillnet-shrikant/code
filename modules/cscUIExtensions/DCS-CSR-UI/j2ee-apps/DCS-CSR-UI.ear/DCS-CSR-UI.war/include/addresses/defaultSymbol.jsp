<%--

@param options the defaultAddressEditorOptions map
@param formHandlerPath the component path of the AddressBookFormHandler
@param formHandler the form handler varible from the dspel:importbean tag

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/defaultSymbol.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:getvalueof var="addrMeta" param="addrMeta"/>
  <dsp:getvalueof var="defaultType" param="defaultType"/>

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <span class="atg_svc_defaultAddress"
      title="<fmt:message 
        key='${defaultType.value.symbolMouseoverResource}'/>">
      <fmt:message key="${defaultType.value.symbolResource}"/>
    </span>
  </dsp:layeredBundle>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/addresses/defaultSymbol.jsp#1 $$Change: 946917 $--%>
