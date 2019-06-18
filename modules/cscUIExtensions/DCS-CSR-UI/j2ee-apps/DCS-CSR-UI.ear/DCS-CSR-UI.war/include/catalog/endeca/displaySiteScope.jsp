<%--
 A page fragment that displays the site scope selection

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySiteScope.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:importbean var="endecaConfig" bean="/atg/commerce/custsvc/catalog/endeca/Configuration"/>
<dsp:importbean  var="contentURLDroplet" bean="/atg/commerce/custsvc/catalog/endeca/ContentRequestURLDroplet"/>
<dsp:importbean  bean="/atg/commerce/custsvc/catalog/endeca/SiteScopeFormHandler"/>
<dsp:importbean bean="/atg/multisite/Site" />

<dsp:droplet name="ContentRequestURLDroplet">
<dsp:param name="url" value="${contentURLDroplet.searchResultPageURL}"/>
<dsp:param name="contentPath" value="${endecaConfig.defaultContentURI}"/>
<dsp:oparam name="output">
  <dsp:getvalueof var="successURL" bean="ContentRequestURLDroplet.url"/>
  </dsp:oparam>
</dsp:droplet>

<dsp:getvalueof bean="SiteScopeFormHandler.siteScope" var="siteScope"/>

<fmt:message key="endeca.allSites.text" var="siteDisplayText"/>
<c:if test="${siteScope == 'current'}"><c:set var="siteDisplayText"><dsp:valueof bean="Site.itemDisplayName" /></c:set></c:if>
<li onclick="$('#atg_csc_catalog_endeca_siteScopeDropdown').toggle();"><c:out value="${siteDisplayText}"/>
  <div class="icon-down">
    <div id="atg_csc_catalog_endeca_siteScopeDropdown">
      <input id="atg_csc_catalog_endeca_siteScopeCheckbox" type="checkbox" 
          onclick="$('#SiteScopeFormHandler_siteScope').val(this.checked? 'atg.shoppingCart' : 'current');atgSubmitAction({formId: 'changeSiteScope'})" 
          ${siteScope == 'current'? "" : "checked='checked'"} />
      <span><fmt:message key="endeca.allSites.selectorValue"/></span>
    </div>
  </div>
</li>

<dsp:form action="#" method="post" name="changeSiteScope" id="changeSiteScope" formid="changeSiteScope">
  <dsp:input bean="SiteScopeFormHandler.changeSiteScopeSuccessURL" value="${successURL}" type="hidden" />
  <dsp:input bean="SiteScopeFormHandler.siteScope" type="hidden" id="SiteScopeFormHandler_siteScope"/>
  <dsp:input value="" bean="SiteScopeFormHandler.changeSiteScope" type="hidden" priority="-10"/>
</dsp:form>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/endeca/displaySiteScope.jsp#1 $$Change: 946917 $--%>
