<%--

Contains the serach results for the customer selection pop up

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/customerSelectionPopupResults.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $ --%>
<%@  include file="/include/top.jspf"%>
<dsp:page xml="true">

  <caf:outputXhtml targetId="customerSearchResults">
        <dsp:include src="/panels/customer/searchResults.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="showCustomerIdsAsText" value="true"/>
              <dsp:param name="isPopup" value="true"/>
              <dsp:param name="additionalSelectActions" value="atg.commerce.csr.common.hidePopupWithReturn('atg_commerce_csr_catalog_customerSelectionPopup');"/>
        </dsp:include>
  </caf:outputXhtml>

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/customer/customerSelectionPopupResults.jsp#1 $$Change: 946917 $--%>
