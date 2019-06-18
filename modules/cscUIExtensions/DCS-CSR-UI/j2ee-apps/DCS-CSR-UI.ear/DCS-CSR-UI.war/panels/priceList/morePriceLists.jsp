<%--
 This page defines the more price lists panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/priceList/morePriceLists.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $

 @param previousPanelStackId - ID of previously viewed panel stack
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/pricing/priceLists/MorePriceListsSearch" var="morePriceListsSearch"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:getvalueof param="previousPanelStackId" var="previousPanelStackId"/>
  <dsp:setvalue bean="MorePriceListsSearch.resultSetSize" value="20"/>
  <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
  <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <script type="text/javascript">
      dojo.declare("atg.commerce.csr.pricing.priceLists.morePriceLists");

      atg.commerce.csr.pricing.priceLists.morePriceListsSearchSetFormSortProperty = function (/*int*/columnIndex, /*bool*/sortDesc) {
        var theForm = dojo.byId(atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.formId);
    //    TODO: sort
        /*if (columnIndex != -1) {
          theForm.sortProperty.value = atg.commerce.csr.pricing.priceLists.morePriceListsSearchProperties[columnIndex].property;
          theForm.sortDirection.value = sortDesc ? "desc" : "asc";
        }
        else
        {
          theForm.sortProperty.value = "displayName";
          theForm.sortDirection.value = "asc";
        }*/
      }

      atg.commerce.csr.pricing.priceLists.morePriceListsSearchFields = [
        { name: '<fmt:message key="global.morePriceLists.name"/>'},
        { name: '<fmt:message key="global.morePriceLists.creationDate"/>'},
        { name: '<fmt:message key="global.morePriceLists.description"/>'},
        { name: '<fmt:message key="global.morePriceLists.select"/>'}
      ];

      atg.commerce.csr.pricing.priceLists.morePriceListsSearchProperties = [
        { property: "displayName" },
        { property: "creationDate" },
        { property: "description"},
        { property: "select" }
      ];

      /*atg.commerce.csr.pricing.priceLists.morePriceListsSearchColumns = [
        [ turbo.grid.columns.basic, { readonly: true } ],
        [ turbo.grid.columns.basic, { readonly: true }],
        [ turbo.grid.columns.basic, { readonly: true }],
        [ turbo.grid.columns.html, { readonly: true }]
      ];*/

      atg.commerce.csr.pricing.priceLists.morePriceLists.searchLayout = [
        { cells: [[
          { name: '<fmt:message key="global.morePriceLists.name"/>'},
          { name: '<fmt:message key="global.morePriceLists.creationDate"/>'},
          { name: '<fmt:message key="global.morePriceLists.description"/>'},
          { name: '<fmt:message key="global.morePriceLists.select"/>', width: "10em"}
        ]]}
      ];

      atg.commerce.csr.pricing.priceLists.morePriceListsPagedData =
        new atg.data.FormhandlerData(atg.commerce.csr.pricing.priceLists.morePriceListsSearchFields,"${CSRConfigurator.contextRoot}/include/priceList/morePriceListsSearchResults.jsp");
      <dsp:getvalueof var="rowsPerPage" bean="MorePriceListsSearch.maxResultsPerPage"/>
      atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.rowsPerPage = ${rowsPerPage};
      atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.formId = 'priceListSearchForm';
      atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.setCurrentPageNumber = function(inRowIndex) {
        var currentPage = Math.floor(inRowIndex / this.rowsPerPage) + 1;
        var form = dojo.byId(this.formId);
        if (form) {
          form[this.formCurrentPageField].value = currentPage;
        }
      };

      atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.rows = function(inRowIndex, inData) {
        for (var i=0, l=inData.results.length; i<l; i++) {
          var newRow = [
            inData.results[i].displayName,
            inData.results[i].creationDate,
            inData.results[i].description,
            inData.results[i].select
          ];
          this.setRow(newRow, inRowIndex + i);
        }
      };

      atg.commerce.csr.pricing.priceLists.morePriceLists.searchRefreshGrid = function () {
        atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.fetchRowCount(
        {
          callback: function(inRowCount) {
            if (inRowCount.resultLength > 0) {
              document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceListsTable").style.display = '';
              document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceLists_noResults").style.display = 'none';
            } else {
              document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceListsTable").style.display = 'none';
              document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceLists_noResults").style.display = '';
            }
            dijit.byId("atg_commerce_csr_pricing_priceLists_morePriceListsTable").editCell = null;
            document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceLists_noSearchPerformed").style.display = 'none';
            atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.clearData();
            atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.count = inRowCount.resultLength;
            atg.commerce.csr.pricing.priceLists.morePriceListsPagedData.rowCount = inRowCount.resultLength;
            dijit.byId("atg_commerce_csr_pricing_priceLists_morePriceListsTable").updateRowCount(inRowCount.resultLength);
          }
        });
      };

      atg.commerce.csr.pricing.priceLists.morePriceListsAddPagination = function() {
        dijit.byId("atg_commerce_csr_pricing_priceLists_morePriceListsTable").setStructure(atg.commerce.csr.pricing.priceLists.morePriceLists.searchLayout);
        document.getElementById("atg_commerce_csr_pricing_priceLists_morePriceListsTable").style.display = 'none';
      }

      _container_.onLoadDeferred.addCallback( function() {
        atg.commerce.csr.pricing.priceLists.morePriceListsAddPagination();
        atg.keyboard.registerFormDefaultEnterKey("priceListSearchForm", "morePriceListsSearchButton");
      });

      _container_.onUnloadDeferred.addCallback(function () {
        // clean up after ourselves
        atg.commerce.csr.pricing.priceLists.morePriceListsPagedData = undefined;
        atg.keyboard.unRegisterFormDefaultEnterKey("priceListSearchForm");
      });
    </script>

    <ul class="atg_commerce_csr_panelToolBar">
      <li class="atg_commerce_csr_last">
        <c:if test="${empty previousPanelStackId}">
          <c:set var="previousPanelStackId" value="cmcCatalogPS"/>
        </c:if>
       
      </li>
    </ul>
    <div>
      <dsp:form method="post" id="priceListSearchForm" name="priceListSearchForm">
        <%-- the following input will trigger both setCurrentResultPageNum and handleCurrentResultPageNum --%>
        <dsp:input type="hidden" priority="-10" name="currentResultPageNum" bean="MorePriceListsSearch.currentResultPageNum" beanvalue="MorePriceListsSearch.currentResultPageNum"/>
        <%--<dsp:input type="hidden" name="sortProperty" bean="MorePriceListsSearch.sortProperty" beanvalue="MorePriceListsSearch.sortProperty"/>--%>
        <%--<dsp:input type="hidden" name="sortDirection" bean="MorePriceListsSearch.sortDirection" beanvalue="MorePriceListsSearch.sortDirection"/>--%>

        <fmt:message key="global.morePriceLists.startDate.tooltip" var="startDateTooltip"/>
        <fmt:message key="global.morePriceLists.endDate.tooltip" var="endDateTooltip"/>
        <div class="atg-csc-base-table">
          <div class="atg_commerce_csr_commentsArea atg-csc-base-table-row">
            <label class="atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="commentsTxt">
              <fmt:message key="global.morePriceLists.containsKeyword"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <dsp:input bean="MorePriceListsSearch.textInput"
                         iclass=""
                         type="text"
                         id="displayName"
                         name="displayName"
                         size="50"
                         maxlength="50"/>
            </div>
          </div>

          <div class="atg_inlineField atg-csc-base-table-row " id="morePriceListsStartDateLi">
            <label class="atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="startDate">
              <fmt:message key="global.morePriceLists.startDate"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <input type="text" 
                  class="startDate" 
                  id="morePriceListsStartDateInput" 
                  name="morePriceListsStartDateInput" 
                  size="10" 
                  maxlength="10" 
                  dojoType="dijit.form.DateTextBox"
                  value="${userPreferredLocale.datePattern}"
                  constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
                  onupdate="atg.commerce.csr.pricing.priceLists.morePriceLists.validate()"/>
               <dsp:input type="hidden"
                          bean="MorePriceListsSearch.advancedSearchPropertyRanges.priceList.creationDate.min"
                          id="morePriceListsStartDate"
                          name="morePriceListsStartDate"
                          size="10"
                          maxlength="10"
                          converter="date"
                          date="${userPreferredLocale.datePattern}"/>
               <dsp:img src="/CAF/images/calendar/calendar.gif"
                 id="morePriceListsStartDateIcon"
                 align="absmiddle"
                 title="${startDateTooltip}"
                 style="cursor:pointer; cursor:hand"
                 onclick="dojo.byId('morePriceListsStartDateInput').focus()"/>
            </div>
          </div>
          <div class="atg_inlineField atg-csc-base-table-row" id="morePriceListsEndDateLi">
            <label class="atg_inlineLabel atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="endDate">
              <fmt:message key="global.morePriceLists.endDate"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <input 
                type="text" 
                class="endDate" 
                id="morePriceListsEndDateInput" 
                name="morePriceListsEndDateInput" 
                size="10" 
                maxlength="10" 
                dojoType="dijit.form.DateTextBox"
                value="${userPreferredLocale.datePattern}"
                constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
                onupdate="atg.commerce.csr.pricing.priceLists.morePriceLists.validate()"/>
              <dsp:input type="hidden"
                         bean="MorePriceListsSearch.advancedSearchPropertyRanges.priceList.creationDate.max"
                         id="morePriceListsEndDate"
                         name="morePriceListsEndDate"
                         size="10"
                         maxlength="10"
                         converter="date"
                         date="${userPreferredLocale.datePattern}"/>
              <dsp:img src="/CAF/images/calendar/calendar.gif"
                id="morePriceListsEndDateIcon"
                align="absmiddle"
                title="${endDateTooltip}"
                style="cursor:pointer; cursor:hand"
                onclick="dojo.byId('morePriceListsEndDateInput').focus()"/>
            </div>
          </div>
          <div class="atg_commerce_csr_more_morePricelistSearchButton atg-csc-base-table-row">
            <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label">
            </div>
            <input id="morePriceListsSearchButton" class="atg-csc-base-table-cell" type="button" name="searchButton" value="<fmt:message key='global.morePriceLists.search'/>" onclick="atg.commerce.csr.pricing.priceLists.searchForPriceLists('${userPreferredLocale.datePattern}');"/>
          </div>
        </div>
      </dsp:form>
      <div id="atg_commerce_csr_pricing_priceLists_morePriceLists_noSearchPerformed">
        <b>
          <fmt:message key="catalogBrowse.searchResults.noSearchPerformed"/>
        </b>
      </div>
      <div id="atg_commerce_csr_pricing_priceLists_morePriceLists_noResults" class="atg_commerce_csr_priceListsNoResults" style="display:none;">
        <b>
          <fmt:message key="global.morePriceLists.noResults"/>
        </b>
      </div>
      <!-- <div id="atg_commerce_csr_pricing_priceLists_morePriceListsPager" dojoType="TurboPageButtons" onPageChange="atg.commerce.csr.pricing.priceLists.morePriceListsPageChange"></div>-->
      <div id="atg_commerce_csr_pricing_priceLists_morePriceListsTable"
           dojoType="dojox.Grid"
           multiSelect="false"
           model="atg.commerce.csr.pricing.priceLists.morePriceListsPagedData"
           style="height: 300px"
           onMouseOverRow="atg.noop()"
           onRowClick="atg.noop()"
           onCellClick="atg.noop()">
      </div>
    </div>
    <script type="text/javascript">
      dojo.require("dijit.form.DateTextBox");
      form = dojo.byId('priceListSearchForm');
      dojo.provide("atg.commerce.csr.pricing.priceLists.morePriceLists");
      atg.commerce.csr.pricing.priceLists.morePriceLists.isFormFilled = function () {
        return !(atg.service.form.isFormEmpty('priceListSearchForm') ||
                (form.displayName.value == '' && 
                (form.morePriceListsStartDateInput.value == "<c:out value='${userPreferredLocale.datePattern}'/>" || form.morePriceListsStartDateInput.value == "")&& 
                (form.morePriceListsEndDateInput.value == "<c:out value='${userPreferredLocale.datePattern}'/>" || form.morePriceListsEndDateInput.value == "")));
      }
      atg.commerce.csr.pricing.priceLists.morePriceLists.validate = function () {
        var form = dojo.byId('priceListSearchForm');
        if (!atg.commerce.csr.pricing.priceLists.morePriceLists.isFormFilled()) {
          form.searchButton.disabled = true;
        }
        else {
          form.searchButton.disabled = false;
        }
      }
      _container_.onLoadDeferred.addCallback(function () {
        if (!atg.commerce.csr.pricing.priceLists.morePriceLists.isFormFilled()) {
          form.searchButton.disabled = true;
        }
        atg.service.form.watchInputs('priceListSearchForm', atg.commerce.csr.pricing.priceLists.morePriceLists.validate);
      });
      _container_.onUnloadDeferred.addCallback(function () {
        atg.service.form.unWatchInputs('priceListSearchForm');
      });
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/priceList/morePriceLists.jsp#1 $$Change: 946917 $--%>
