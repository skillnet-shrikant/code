<%--
This page defines a row for site picker's tables
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPickerRow.jsp#2 $$Change: 953229 $
@updated $DateTime: 2015/02/26 10:47:28 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:importbean bean="/atg/multisite/Site" var="currentSite"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
  
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

    <dsp:getvalueof var="siteIcon" param="site.favicon"/>
    <dsp:getvalueof var="siteIconHover" param="site.name"/>
    <c:choose>
      <c:when test="${!empty siteIcon}">
        <c:set var="siteIconURL" value="${siteIcon}"/>
      </c:when>
      <c:otherwise>
        <c:set var="siteIconURL" value="${CSRConfigurator.defaultSiteIconURL}"/>
      </c:otherwise>
    </c:choose>

    <dsp:getvalueof var="index" param="index"/>
    <tr class="${index % 2 == 0 ? 'odd' : 'even'}">
      <td class="atg_commerce_csr_storeIcon">
        <img src="${siteIconURL}" alt="<dsp:valueof param='site.name'/>" title="${siteIconHover}">
      </td>
      <td class="atg_commerce_csr_catalogName">
        <dsp:valueof param="site.name"/>
      </td>
      <td class="atg_commcerce_csr_multisiteSelect">
        <c:choose>
          <c:when test="${envTools.siteAccessControlOn == 'true'}">      
            <dsp:getvalueof var="siteId" param="site.id"/>
            <dsp:droplet name="IsSiteAccessibleDroplet">
              <dsp:param name="siteId" value="${siteId}"/>
              <dsp:oparam name="true">
                <a href="#" onclick="atg.commerce.csr.common.setSite('<dsp:valueof param="site.id"/>','${currentSite.id}');"><fmt:message key="global.multisiteSelectionPicker.select"/></a>
              </dsp:oparam>
              <dsp:oparam name="false">
                &nbsp;
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
            <a href="#" onclick="atg.commerce.csr.common.setSite('<dsp:valueof param="site.id"/>','${currentSite.id}');"><fmt:message key="global.multisiteSelectionPicker.select"/></a>
          </c:otherwise>  
        </c:choose>
      </td>
      <td></td>
    </tr>

  </dsp:layeredBundle>
</dsp:page>
<!-- $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPickerRow.jsp#2 $$Change: 953229 $$DateTime: 2015/02/26 10:47:28 $ -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPickerRow.jsp#2 $$Change: 953229 $--%>
