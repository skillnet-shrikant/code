<%--
This page defines explicit site picker
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPicker.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SiteGroupsDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" />
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

    <div id="atg_commerce_csr_multisiteSelectSite">

    <dsp:droplet name="SiteGroupsDroplet">
      <dsp:oparam name="output">
        <dsp:getvalueof var="ungroupedSites" param="ungroupedSites"/>
        <dsp:getvalueof var="siteGroups" param="siteGroups"/>


      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="ungroupedSites"/>
        <dsp:param name="sortProperties" value="+cscDisplayPriority, +name"/>
        <dsp:setvalue param="site" paramvalue="element"/>
          
        <dsp:oparam name="outputStart">
          <table class="atg_csr_singleSites">
            <tr class="atg_commcerce_csr_multisiteSelectorHeader">
              <th>
              <th><fmt:message key="global.multisiteSelectionPicker.name"/></th>
              <th class="atg_commcerce_csr_multisiteSelect"></th>
              <th></th>
            </tr>
        </dsp:oparam>

        <dsp:oparam name="output">
          <dsp:include src="/panels/multisite/multisiteSelectionPickerRow.jsp" otherContext="${CSRConfigurator.contextRoot}">
            <dsp:param name="site" param="site"/>
            <dsp:param name="index" param="index"/>
          </dsp:include>
        </dsp:oparam>
        
        <dsp:oparam name="outputEnd">
          </table>
        </dsp:oparam>
        
      </dsp:droplet>

      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="siteGroups"/>
        <dsp:setvalue param="groupedSites" paramvalue="element"/>
          
        <dsp:oparam name="outputStart">
        </dsp:oparam>

        <dsp:oparam name="output">
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="groupedSites"/>
            <dsp:param name="sortProperties" value="+cscDisplayPriority, +name"/>
            <dsp:setvalue param="site" paramvalue="element"/>
          
            <dsp:oparam name="outputStart">
              <table class="atg_csr_groupedSites">
                <tr>
                  <th colspan="3">
                     <fmt:message key="global.multisiteSelectionPicker.siteGroup"/> <span class="atg_csr_groupSiteName"><dsp:valueof param="key.displayName"/></span>
                  </th>
                  <th></th>
                </tr>
            </dsp:oparam>

            <dsp:oparam name="output">
              <dsp:include src="/panels/multisite/multisiteSelectionPickerRow.jsp" otherContext="${CSRConfigurator.contextRoot}">
                <dsp:param name="site" param="site"/>
                <dsp:param name="index" param="index"/>
              </dsp:include>
            </dsp:oparam>
        
            <dsp:oparam name="outputEnd">
              </table>
            </dsp:oparam>
        
          </dsp:droplet>
        </dsp:oparam>
        
        <dsp:oparam name="outputEnd">
        </dsp:oparam>
        
      </dsp:droplet>

    	
      </dsp:oparam>
    </dsp:droplet>
    	
  </dsp:layeredBundle>
</dsp:page>
<!-- $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPicker.jsp#1 $$Change: 946917 $$DateTime: 2015/01/26 17:26:27 $ -->
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/multisite/multisiteSelectionPicker.jsp#1 $$Change: 946917 $--%>
