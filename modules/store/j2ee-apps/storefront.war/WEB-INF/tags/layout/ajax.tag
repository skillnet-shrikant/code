<%--
  Tag for ajax page wrapper. Responsible for wrapping contents of AJAX response.
--%>
<%@ include file="/sitewide/fragments/tags.jspf" %>
<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="section"%>
<%@ attribute name="pageType"%>

<%-- Set page vars --%>
<c:set var="jsController" value="${section}" scope="request"/>
<c:set var="jsAction" value="${pageType}" scope="request"/>

<%-- set controller to proxy if HTTPS in modal on HTTP page --%>
<c:if test="${isProxy}">
	<c:set var="jsController" value="proxy" scope="request"/>
</c:if>

<div class="ajax-wrapper" data-controller="${jsController}" data-action="${jsAction}">
	<jsp:doBody />
</div>
