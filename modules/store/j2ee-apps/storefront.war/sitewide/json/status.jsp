<%--
- File Name: status.jsp
- Author(s): jjensen
- Copyright Notice:
- Description: Creates a json message for updating UI when user logs in or adds an item to their cart
- Parameters:
-   full - indicates if we should inclide request for footer content
--%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<json:object name="profile">
		<json:property name="cartCount" escapeXml="false">
			<dsp:valueof bean="ShoppingCart.current.totalCommerceItemCount" />
		</json:property>
		<json:property name="statusValue" escapeXml="false">
			<dsp:valueof bean="Profile.loginStatus" />
		</json:property>
		<json:property name="firstname" escapeXml="false">
			<dsp:valueof bean="Profile.firstName" />
		</json:property>
		<json:property name="lastname" escapeXml="false">
			<dsp:valueof bean="Profile.lastName" />
		</json:property>
	</json:object>

</dsp:page>
