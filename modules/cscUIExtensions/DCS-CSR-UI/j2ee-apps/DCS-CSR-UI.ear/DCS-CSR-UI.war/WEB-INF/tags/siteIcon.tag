<%@ tag language="java"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dsp"	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>

<%@ attribute name="siteId" required="false"%>

<%--
Displays the siteIcon for a given siteId. If MultiSite is not enabled, no icon is returned.
If the siteIcon is not specified in the repository, the default site icon is used.

The tag is called as follows: <csr:siteIcon siteId="${item.auxiliaryData.siteId}" />
--%>

<dsp:importbean bean="/atg/dynamo/droplet/multisite/GetSiteDroplet" />
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator"	var="CSRConfigurator" />
<dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools"	var="CSRAgentTools" />
<c:set var="isMultiSiteEnabled"	value="${CSRAgentTools.multiSiteEnabled}" />

<c:if test="${isMultiSiteEnabled == true}">
	<dsp:droplet name="GetSiteDroplet">
		<dsp:param name="siteId" value="${siteId}" />
		<dsp:oparam name="output">
   <dsp:tomap var="site" param="site"/> 
		</dsp:oparam>
	</dsp:droplet>

	<c:choose>
		<c:when test="${!empty site}">
			<c:set var="siteIconHover" value="${site.name}" />
			<c:choose>
				<c:when test="${!empty site.favicon}">
					<img src="${site.favicon}" class="atg_commerce_csr_site_icon" title="${siteIconHover}" />
				</c:when>
				<c:otherwise>
					<img src="${CSRConfigurator.defaultSiteIconURL}" class="atg_commerce_csr_site_icon" title="${siteIconHover}" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<img src="${CSRConfigurator.defaultSiteIconURL}" />
		</c:otherwise>
	</c:choose>

</c:if>

<jsp:doBody />
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/WEB-INF/tags/siteIcon.tag#1 $$Change: 946917 $--%>
