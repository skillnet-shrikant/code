<%@ tag language="java" %>

<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ attribute name="name" required="false" type="java.lang.String" %>
<%@ attribute name="renderer" required="false" type="java.lang.String" %>
<%@ attribute name="targeter" required="false" type="java.lang.String" %>
<%@ attribute name="data" required="false" type="java.lang.String" %>
<%@ attribute name="debug" required="false" %>
<%@ attribute name="setPageData" fragment="true" %>

<%@ variable name-given="renderInfo" 
    scope="NESTED"
    variable-class="atg.commerce.csr.rendering.RenderInfo" %>

<%@ variable name-given="pageData" 
    scope="NESTED"
    variable-class="atg.commerce.csr.rendering.ComponentContainer" %>

<%-- 
  Default renderer, targeter, and rule data components are by
  loaded, by convention, by appending "Renderer", "Targeter", and
  "PageData" (respectively) to the renderer "name". The "name" does
  not actually refer to any component.
 --%>

<c:if test="${empty renderer}">
  <c:set var="renderer" value="${name}Renderer"/>
</c:if>

<c:if test="${empty targeter}">
  <c:set var="targeter" value="${name}Targeter"/>
</c:if>

<c:if test="${empty data}">
  <c:set var="pageData" value="${name}PageData"/>
</c:if>

<%--
  renderer = ${renderer}<br />
  targeter = ${targeter}<br />
  pageData = ${pageData}<br />
--%>

<c:if test="${ ! empty pageData }">
  <dsp:importbean var="pageData" bean="${pageData}"/>
  <c:set var="pageData" value="${pageData}" scope="request"/>
  <jsp:invoke fragment="setPageData"/>
</c:if>

<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param name="targeter" bean="${targeter}"/>
  <dsp:oparam name="output">
    <dsp:getvalueof var="renderInfo" param="element"/>
  </dsp:oparam>
  <dsp:oparam name="empty">
    <%-- no targeter results, use default renderer --%>
    <dsp:importbean var="renderInfo" bean="${renderer}"/>
  </dsp:oparam>
</dsp:droplet>

<c:set var="renderInfo" value="${renderInfo}" scope="request"/>
<jsp:doBody/>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/renderer.tag#1 $$Change: 946917 $--%>
