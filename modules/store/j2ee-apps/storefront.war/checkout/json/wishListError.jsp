<%--
	- File Name: wishListError.jsp
	- Author(s):
	- Copyright Notice:
	- Description: Displays add to wish list error messages (json format).
	- Parameters:
	-
	--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:param name="formhandler" bean="GiftlistFormHandler" />

	<json:object>
		<%@ include file="/sitewide/includes/errors/jsonErrors.jspf" %>
	</json:object>

</dsp:page>
