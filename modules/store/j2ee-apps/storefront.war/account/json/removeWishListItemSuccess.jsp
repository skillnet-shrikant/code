<%--
  - File Name: removeWishListItemSuccess.jsp
  - Author(s):
  - Copyright Notice:
  - Description: Creates a json success message after successful remove from wish list.
  - Parameters:
  --%>
<%@ include file="/sitewide/fragments/content-type-json.jspf" %>
<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<%-- Page Variables --%>
	<dsp:getvalueof var="wishListItems" bean="Profile.wishlist.giftlistItems"/>
	<dsp:test var="wishListCount" value="${wishListItems}"/>

	<json:object>
		<json:property name="success">true</json:property>
		<json:property name="wishListCount">${wishListCount.size}</json:property>
	</json:object>

</dsp:page>
