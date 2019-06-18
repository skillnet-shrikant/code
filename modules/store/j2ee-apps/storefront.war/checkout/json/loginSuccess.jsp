<%@ include file="/sitewide/fragments/content-type-json.jspf" %>

<dsp:page>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:getvalueof var="cartMerged" bean="ProfileFormHandler.cartMerged"/>

<c:choose>
<c:when test="${cartMerged}">
	<dsp:getvalueof var="redirectUrl" scope="request" value="${contextPath}/checkout/cart.jsp"/>
</c:when>
<c:otherwise>
	<dsp:getvalueof var="redirectUrl" scope="request" value="${contextPath}/checkout/checkout.jsp"/>
</c:otherwise>
</c:choose>

<json:object>
	<json:property name="success">true</json:property>
	<json:property name="url">${redirectUrl}</json:property>
</json:object>

</dsp:page>
