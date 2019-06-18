<%--
  - File Name: bopisSearchError.jsp
  - Author(s): jjensen
  - Copyright Notice:
  - Description: Creates a json error message for issues when searching BOPIS inventory.
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:param name="formhandler" bean="StoreLocatorFormHandler" />
	<json:object>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>
</dsp:page>
