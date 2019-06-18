<%--
This page defines the promotions browser panel
@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/promotionsBrowser.jsp#2 $
@updated $DateTime: 2015/08/06 12:57:22 $
--%>
<%@ include file="/include/top.jspf" %>

<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/promotion/AvailablePromotionsGrid" var="walletGridConfig"/>
  <dsp:importbean bean="/atg/commerce/custsvc/ui/tables/promotion/PromotionSearchGrid" var="searchGridConfig"/>
  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionSearch" var="promotionSearch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/promotion/PromotionWalletFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
  <dsp:importbean bean="/atg/svc/security/droplet/HasAccessRight" />
  <dsp:getvalueof var="order" bean="/atg/commerce/custsvc/order/ShoppingCart.current"/>
  <dsp:getvalueof var="agentProfile" bean="/atg/userprofiling/Profile"/>
  <dsp:getvalueof var="customerProfile" bean="/atg/userprofiling/ActiveCustomerProfile"/>
  <dsp:getvalueof var="baseHolder" bean="PromotionWalletFormHandler.baseHolder"/>
  <dsp:getvalueof var="walletHolder" bean="PromotionWalletFormHandler.walletHolder"/>
  <dsp:layeredBundle basename="atg.commerce.csr.order.WebAppResources">

    <dsp:droplet name="HasAccessRight">
      <dsp:param name="accessRight" value="commerce-custsvs-browse-promotions-privilege" />
      <dsp:oparam name="accessGranted">

    <div id="atg_commerce_csr_promotionsBrowserDialog"
       dojoType="dijit.Dialog"
       title="<fmt:message key="promotion.popupTitle"/>"
       >

      <div  id="atg_commerce_csr_promotionsTabContainer"
            dojoType="dijit.layout.TabContainer"
            closable="false">
        <div id="atg_commerce_csr_availablePromotions"
              dojoType="dijit.layout.ContentPane"
              closable="false"
              title="<fmt:message key="promotion.walletTab"/>">
          <div class="atg_commerce_csr_gridMessage" id="availablePromotionsStatus"><fmt:message key='promotion.noSearch'/></div>
          <div id="atg_commerce_csr_availablePromotionsGrid" >
            <dsp:form style="display:none" id="promotionWalletForm" formid="promotionWalletForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.search"/>
              <dsp:input type="hidden" name="currentPage" bean="PromotionWalletFormHandler.currentPage"/>
              <dsp:input type="hidden" name="sortProperty" bean="PromotionWalletFormHandler.sortField"/>
              <dsp:input type="hidden" name="sortDirection" bean="PromotionWalletFormHandler.sortDirection"/>
            </dsp:form>
            <dsp:form style="display:none" id="promotionUpdateForm" formid="promotionUpdateForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.updatePrice"/>
            </dsp:form>
            <dsp:form method="post" id="promotionGrantForm" formid="promotionGrantForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.grantForCustomer"/>
              <dsp:input type="hidden" bean="PromotionWalletFormHandler.promotionId" value=""/>
            </dsp:form>
            <dsp:form method="post" id="promotionRemoveForm" formid="promotionRemoveForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.removeForCustomer"/>
              <dsp:input type="hidden" bean="PromotionWalletFormHandler.stateId" value=""/>
            </dsp:form>
            <dsp:form method="post" id="promotionExcludeForm" formid="promotionExcludeForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.excludeForOrder"/>
              <dsp:input type="hidden" bean="PromotionWalletFormHandler.promotionId" value=""/>
            </dsp:form>
            <dsp:form method="post" id="promotionIncludeForm" formid="promotionIncludeForm">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionWalletFormHandler.includeForOrder"/>
              <dsp:input type="hidden" bean="PromotionWalletFormHandler.promotionId" value=""/>
            </dsp:form>

            <dsp:include src="${walletGridConfig.gridPage.URL}" otherContext="${walletGridConfig.gridPage.servletContext}">
              <dsp:param name="gridConfig" value="${walletGridConfig}"/>
              <dsp:param name="allowSort" value="${false}"/>
              <dsp:param name="resultsMessage" value="availablePromotions.results"/>
              <dsp:param name="noResultsMessage" value="availablePromotions.noResults"/>
            </dsp:include>
          </div>
          <div class="atg_commerce_csr_panelFooter atg_commerce_csr_panelGridFooter" >
            <span class="atg_commerce_csr_appliedIndicator"><fmt:message key="promotion.updateIndicator"/></span>
            <span> <fmt:message key="promotion.updateInstructions"/></span>
            <input type="button" onclick="javascript:atg.commerce.csr.promotion.update(${walletGridConfig.gridWidgetId}_refreshSearchResults, this);"
                   value="<fmt:message key="promotion.update"/>">
          </div>
          <div id="atg_commerce_csr_promotionsOrderSummary" dojoType="dijit.layout.ContentPane" excludeFromTabs="true"
               href="${CSRConfigurator.contextRoot}/panels/promotion/pricingPreview.jsp?_windowid=${windowId}" preventCache="true">
            <csr:displayOrderSummary order="${order}" isShowHeader="${true}"/>
          </div>
        </div> <%-- End available promos content pane --%>

        <%-- Search content pane  --%>
        <div id="atg_commerce_csr_promotionsSearch"
              dojoType="dijit.layout.ContentPane"
              closable="false"
              title="<fmt:message key="promotion.searchTab"/>" >
          <div class="atg_commerce_csr_promoSearchForm">
            <dsp:form method="post" id="promotionSearchForm" formid="promotionSearchForm" 
              onsubmit="javascript:atg.commerce.csr.promotion.search(${searchGridConfig.gridWidgetId}_refreshSearchResults, '${searchGridConfig.gridInstanceId}');return false;">
              <dsp:input type="hidden" priority="-10" value="" bean="PromotionSearch.search"/>
              <dsp:input type="hidden" name="currentPage" bean="PromotionSearch.currentPage"/>
              <dsp:input type="hidden" name="sortProperty" bean="PromotionSearch.sortField"/>
              <dsp:input type="hidden" name="sortDirection" bean="PromotionSearch.sortDirection"/>
              <ul class="atg_dataForm atg_commerce_csr_promosearchTerm">
                <li class="atg_commerce_csr_promoSearchKeyword">
                  <span class="atg_svc_fieldTitle">
                    <label><fmt:message key="promotion.keywordField"/></label>
                   </span>
                  <dsp:input type="text" bean="PromotionSearch.keyword" name="keyword"
                             size="20" maxlength="50"/>
                </li>
                <li class="atg_commerce_csr_promoSearchtype">
                  <span class="atg_svc_fieldTitle">
                    <label><fmt:message key="promotion.availableField"/></label>
                  </span>
                  <dsp:select bean="PromotionSearch.dateOption" iclass="input-large">
                    <dsp:option selected="${true}" value="Today"><fmt:message key="promotion.availableToday"/></dsp:option>
                    <dsp:option value="Start in Next 7 Days"><fmt:message key="promotion.startInNext7Days"/></dsp:option>
                    <dsp:option value="End in Last 7 Days"><fmt:message key="promotion.endInLast7Days"/></dsp:option>
                    <dsp:option value="All Future"><fmt:message key="promotion.allFuture"/></dsp:option>
                    <dsp:option value="All Expired"><fmt:message key="promotion.allExpired"/></dsp:option>
                  </dsp:select>
                </li>
              </ul>
              <ul class="atg_dataForm atg_commerce_csr_promoSearchFilters">
                <li class="atg_commerce_csr_promoSearchtype">
                  <span class="atg_svc_fieldTitle"><label><fmt:message key="promotion.typeField"/></label></span>
                  <dsp:select bean="PromotionSearch.type" iclass="input-small">
                    <dsp:option selected="${true}" value=""><fmt:message key="promotion.allTypes"/></dsp:option>
                    <dsp:option value="Item Discount"><fmt:message key="promotion.itemType"/></dsp:option>
                    <dsp:option value="Order Discount"><fmt:message key="promotion.orderType"/></dsp:option>
                    <dsp:option value="Shipping Discount"><fmt:message key="promotion.shippingType"/></dsp:option>
                    <%--<dsp:option value="Tax Discount"><fmt:message key="promotion.taxType"/></dsp:option>--%>
                  </dsp:select>
                </li>
                <li class="atg_commerce_csr_promoSearchGlobal">
                  <dsp:input type="checkbox" checked="true" default="false" bean="PromotionSearch.hideGlobal"/>
                  <span class="atg_svc_fieldTitle">
                    <label><fmt:message key="promotion.hideGlobal"/></label>
                  </span>
                </li>

                <li class="${isMultiSiteEnabled?'atg_commerce_csr_promoSearchtype':'atg_commerce_csr_promoSearchtypeInactive'}">
                  <span class="atg_svc_fieldTitle">
                    <label><fmt:message key="promotion.siteField"/></label>
                  </span>
                  <dsp:droplet name="SharingSitesDroplet">
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="sites" param="sites"/>
                  </dsp:oparam>
                  </dsp:droplet>
                  <dsp:select bean="PromotionSearch.site" iclass="input-small">
                    <dsp:option selected="${true}" value=""><fmt:message key="promotion.allSites"/></dsp:option>
                    <c:forEach var="siteConfig" items="${sites}">
                      <dsp:tomap var="siteConfigMap" value="${siteConfig}"/>
                      <dsp:option value="${siteConfigMap.id}">${siteConfigMap.name}</dsp:option>
                    </c:forEach>
                  </dsp:select>
                </li>
                <li class="atg_commerce_csr_promoSearchSite">
                  <input type="submit" id="searchButton" value="<fmt:message key="promotion.search"/>"/>
                </li>
              </ul>
            </dsp:form>
          </div>
          <h3><fmt:message key="promotion.searchResultsTitle"/></h3>
          <div class="atg_commerce_csr_promoBrowserSearchResults">
            <div class="atg_commerce_csr_promoResultsHeader">
              <p class="atg_commerce_csr_promoMatchingResults" id="promotionSearchStatus"><fmt:message key='promotion.noSearch'/></p>
              <p class="atg_commerce_csr_promoavailablePromotions" id="promotionInstructions" style="display:none;"><fmt:message key="promotion.addInstructions"/></p>
            </div>
            <div id="atg_commerce_csr_promoSearchResultsGrid" style="height:316px;">
              <dsp:include src="${searchGridConfig.gridPage.URL}" otherContext="${searchGridConfig.gridPage.servletContext}">
                <dsp:param name="gridConfig" value="${searchGridConfig}"/>
                <dsp:param name="allowSort" value="${true}"/>
                <dsp:param name="resultsMessage" value="promotionSearchResults.results"/>
                <dsp:param name="noResultsMessage" value="promotionSearchResults.noResults"/>
              </dsp:include>
            </div>
            <div class="atg_commerce_csr_panelFooter atg_commerce_csr_panelGridFooter">
            </div>
          </div>
        </div>
        <div class="atg_commerce_csr_panelFooter" style="background:white;">
          <dsp:form method="post" id="revertPromotionWalletForm" formid="revertPromotionWalletForm">
            <dsp:input type="hidden" priority="-10" value=""
                       bean="PromotionWalletFormHandler.revertWallet"/>
          </dsp:form>
          <dsp:form method="post" id="savePromotionWalletForm" formid="savePromotionWalletForm">
            <dsp:input type="hidden" priority="-10" value=""
                       bean="PromotionWalletFormHandler.saveWallet"/>
          </dsp:form>
          <input type="button" id="cancelWalletButton" value="<fmt:message key="promotion.cancel"/>"
            onclick="javascript:atg.commerce.csr.promotion.revertWallet(); return false;" style="float:right"/>
          <input type="button" id="saveWalletButton" value="<fmt:message key="promotion.save"/>"
            onclick="javascript:atg.commerce.csr.promotion.saveWallet(); return false;" style="float:right"/>
        </div>
      </div>   <%-- END Tabbed pane --%>
    </div>
    <%-- #############################################################################################################--%>


  <script type="text/javascript">
    dojo.subscribe("atg_commerce_csr_promotionsTabContainer-selectChild", function(child){
      if (child.id == "atg_commerce_csr_availablePromotions") {
        ${walletGridConfig.gridWidgetId}_refreshSearchResults();
      }
    });
  </script>
      </dsp:oparam>
    </dsp:droplet> <%-- Access granted --%>

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/promotion/promotionsBrowser.jsp#2 $$Change: 1185682 $--%>
