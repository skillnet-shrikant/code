<%--
 This page defines the more catalogs panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/moreCatalogs.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $

 @param previousPanelStackId - ID of previously viewed panel stack
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/catalog/MoreCatalogsSearch" var="moreCatalogsSearch"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <c:catch var="ex">
  <dsp:getvalueof param="previousPanelStackId" var="previousPanelStackId"/>
  <dsp:setvalue bean="MoreCatalogsSearch.resultSetSize" value="20"/>
  <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
  <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
    <script type="text/javascript">
      dojo.declare("atg.commerce.csr.catalog.moreCatalogs");

      atg.commerce.csr.catalog.moreCatalogsSearchSetFormSortProperty = function (/*int*/columnIndex, /*bool*/sortDesc) {
        var theForm = dojo.byId(atg.commerce.csr.catalog.moreCatalogsPagedData.formId);
    //    TODO: sort
        /*if (columnIndex != -1) {
          theForm.sortProperty.value = atg.commerce.csr.catalog.moreCatalogsSearchProperties[columnIndex].property;
          theForm.sortDirection.value = sortDesc ? "desc" : "asc";
        }
        else
        {
          theForm.sortProperty.value = "displayName";
          theForm.sortDirection.value = "asc";
        }*/
      }

      atg.commerce.csr.catalog.moreCatalogsSearchFields = [
        { name: '<fmt:message key="global.moreCatalogs.name"/>'},
        { name: '<fmt:message key="global.moreCatalogs.creationDate"/>'},
        { name: '<fmt:message key="global.moreCatalogs.select"/>'}
      ];

      atg.commerce.csr.catalog.moreCatalogsSearchProperties = [
        { property: "displayName" },
        { property: "creationDate" },
        { property: "select" }
      ];

      /*atg.commerce.csr.catalog.moreCatalogsSearchColumns = [
        [ turbo.grid.columns.basic, { readonly: true } ],
        [ turbo.grid.columns.basic, { readonly: true }],
        [ turbo.grid.columns.basic, { readonly: true }],
        [ turbo.grid.columns.html, { readonly: true }]
      ];*/

      atg.commerce.csr.catalog.moreCatalogs.searchLayout = [
        { cells: [[
          { name: '<fmt:message key="global.moreCatalogs.name"/>'},
          { name: '<fmt:message key="global.moreCatalogs.creationDate"/>'},
          { name: '<fmt:message key="global.moreCatalogs.select"/>'}
        ]]}
      ];

      atg.commerce.csr.catalog.moreCatalogsPagedData =
        new atg.data.FormhandlerData(atg.commerce.csr.catalog.moreCatalogsSearchFields,"${CSRConfigurator.contextRoot}/include/catalog/moreCatalogsSearchResults.jsp");
      <dsp:getvalueof var="rowsPerPage" bean="MoreCatalogsSearch.maxResultsPerPage"/>
      atg.commerce.csr.catalog.moreCatalogsPagedData.rowsPerPage = ${rowsPerPage};
      atg.commerce.csr.catalog.moreCatalogsPagedData.formId = 'catalogSearchForm';
      atg.commerce.csr.catalog.moreCatalogsPagedData.setCurrentPageNumber = function(inRowIndex) {
        var currentPage = Math.floor(inRowIndex / this.rowsPerPage) + 1;
        var form = dojo.byId(this.formId);
        if (form) {
          if (form[this.formCurrentPageField]){
            form[this.formCurrentPageField].value = currentPage;
          }
        }
      };

      atg.commerce.csr.catalog.moreCatalogsPagedData.rows = function(inRowIndex, inData) {
        for (var i=0, l=inData.results.length; i<l; i++) {
          var newRow = [
            inData.results[i].displayName,
            inData.results[i].creationDate,
            inData.results[i].select
          ];
          this.setRow(newRow, inRowIndex + i);
        }
      };

      atg.commerce.csr.catalog.moreCatalogs.searchRefreshGrid = function () {
        atg.commerce.csr.catalog.moreCatalogsPagedData.fetchRowCount(
        {
          callback: function(inRowCount) {
            if (inRowCount.resultLength > 0) {
              document.getElementById("atg_commerce_csr_catalog_moreCatalogsTable").style.display = '';
              document.getElementById("atg_commerce_csr_catalog_moreCatalogs_noResults").style.display = 'none';
            } else {
              document.getElementById("atg_commerce_csr_catalog_moreCatalogsTable").style.display = 'none';
              document.getElementById("atg_commerce_csr_catalog_moreCatalogs_noResults").style.display = '';
            }
            dijit.byId("atg_commerce_csr_catalog_moreCatalogsTable").editCell = null;
            document.getElementById("atg_commerce_csr_catalog_moreCatalogs_noSearchPerformed").style.display = 'none';
            atg.commerce.csr.catalog.moreCatalogsPagedData.clearData();
            atg.commerce.csr.catalog.moreCatalogsPagedData.count = inRowCount.resultLength;
            atg.commerce.csr.catalog.moreCatalogsPagedData.rowCount = inRowCount.resultLength;
            dijit.byId("atg_commerce_csr_catalog_moreCatalogsTable").updateRowCount(inRowCount.resultLength);
          }
        });
      };

      atg.commerce.csr.catalog.moreCatalogsAddPagination = function() {
        dijit.byId("atg_commerce_csr_catalog_moreCatalogsTable").setStructure(atg.commerce.csr.catalog.moreCatalogs.searchLayout);
        document.getElementById("atg_commerce_csr_catalog_moreCatalogsTable").style.display = 'none';
      }

      _container_.onLoadDeferred.addCallback( function() {
        atg.commerce.csr.catalog.moreCatalogsAddPagination();
        atg.keyboard.registerFormDefaultEnterKey("catalogSearchForm", "moreCatalogSearchButton");
      })

      _container_.onUnloadDeferred.addCallback(function () {
        // clean up after ourselves
        atg.commerce.csr.catalog.moreCatalogsPagedData = undefined;
        atg.keyboard.unRegisterFormDefaultEnterKey("catalogSearchForm");
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
      <dsp:form method="post" id="catalogSearchForm" name="catalogSearchForm">
        <%-- the currentResultPageNum input will trigger both setCurrentResultPageNum and handleCurrentResultPageNum --%>
        <dsp:input type="hidden" priority="-10" name="currentResultPageNum" bean="MoreCatalogsSearch.currentResultPageNum" beanvalue="MoreCatalogsSearch.currentResultPageNum"/>
        <%--<dsp:input type="hidden" name="sortProperty" bean="MoreCatalogsSearch.sortProperty" beanvalue="MoreCatalogsSearch.sortProperty"/>--%>
        <%--<dsp:input type="hidden" name="sortDirection" bean="MoreCatalogsSearch.sortDirection" beanvalue="MoreCatalogsSearch.sortDirection"/>--%>

        <fmt:message key="global.moreCatalogs.startDate.tooltip" var="startDateTooltip"/>
        <fmt:message key="global.moreCatalogs.endDate.tooltip" var="endDateTooltip"/>
        <div class="atg-csc-base-table">
          <div class="atg_commerce_csr_commentsArea atg-csc-base-table-row">
            <label class="atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="commentsTxt">
              <fmt:message key="global.moreCatalogs.containsKeyword"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <dsp:input bean="MoreCatalogsSearch.textInput"
                         type="text"
                         iclass=""
                         name="displayName"
                         size="50"
                         maxlength="50"/>
            </div>
          </div>

          <div class="atg_inlineField atg-csc-base-table-row" id="moreCatalogsStartDateLi">
            <label class="atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="startDate">
              <fmt:message key="global.moreCatalogs.startDate"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <input 
                class="startDate" 
                type="text" 
                id="moreCatalogsStartDateInput" 
                name="moreCatalogsStartDateInput" 
                size="10" 
                maxlength="10" 
                dojoType="dijit.form.DateTextBox"
                value="${userPreferredLocale.datePattern}"
                constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
                onupdate="atg.commerce.csr.catalog.moreCatalogs.validate()"/>
              <dsp:input type="hidden"
                          bean="MoreCatalogsSearch.advancedSearchPropertyRanges.catalog.creationDate.min"
                          name="moreCatalogsStartDate"
                          id="moreCatalogsStartDate"
                          size="10"
                          maxlength="10"
                          converter="date"
                          date="${userPreferredLocale.datePattern}"/>
              <dsp:img src="/CAF/images/calendar/calendar.gif"
                id="moreCatalogsStartDateIcon"
                align="absmiddle"
                title="${startDateTooltip}"
                style="cursor:pointer; cursor:hand"
                onclick="dojo.byId('moreCatalogsStartDateInput').focus()"/>
            </div>
          </div>

          <div class="atg_inlineField atg-csc-base-table-row" id="moreCatalogsEndDateLi">
            <label class="atg_inlineLabel atg-csc-base-table-cell atg-base-table-customer-create-first-label" for="endDate">
              <fmt:message key="global.moreCatalogs.endDate"/>
            </label>
            <div class="atg-csc-base-table-cell">
              <input 
                class="endDate" 
                type="text" 
                id="moreCatalogsEndDateInput" 
                name="moreCatalogsEndDateInput" 
                size="10" 
                maxlength="10" 
                dojoType="dijit.form.DateTextBox"
                value="${userPreferredLocale.datePattern}"
                constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
                onupdate="atg.commerce.csr.catalog.moreCatalogs.validate()"
                />
               <dsp:input type="hidden"
                          bean="MoreCatalogsSearch.advancedSearchPropertyRanges.catalog.creationDate.max"
                          name="moreCatalogsEndDate"
                          id="moreCatalogsEndDate"
                          size="10"
                          maxlength="10"
                          converter="date"
                          date="${userPreferredLocale.datePattern}"/>
              <dsp:img src="/CAF/images/calendar/calendar.gif"
                id="moreCatalogsEndDateIcon"
                align="absmiddle"
                title="${endDateTooltip}"
                style="cursor:pointer; cursor:hand"
                onclick="dojo.byId('moreCatalogsEndDateInput').focus()"/>
            </div>
          </div>
  
          <div class="atg_commerce_csr_moreCatalogSearchButton atg-csc-base-table-row">
            <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label"></div>
            <input id="moreCatalogSearchButton" class="atg-csc-base-table-cell" type="button" name="searchButton" value="<fmt:message key='global.moreCatalogs.search'/>" onclick="atg.commerce.csr.catalog.searchForCatalogs('${userPreferredLocale.datePattern}');"/>
          </div>
        </div>
      </dsp:form>
      <div id="atg_commerce_csr_catalog_moreCatalogs_noSearchPerformed">
        <b>
          <fmt:message key="catalogBrowse.searchResults.noSearchPerformed"/>
        </b>
      </div>
      <div id="atg_commerce_csr_catalog_moreCatalogs_noResults" style="display:none;">
        <b>
          <fmt:message key="global.moreCatalogs.noResults"/>
        </b>
      </div>
      <!-- <div id="atg_commerce_csr_catalog_moreCatalogsPager" dojoType="TurboPageButtons" onPageChange="atg.commerce.csr.catalog.moreCatalogsPageChange"></div>-->
      <div id="atg_commerce_csr_catalog_moreCatalogsTable"
           dojoType="dojox.Grid"
           multiSelect="false"
           model="atg.commerce.csr.catalog.moreCatalogsPagedData"
           style="height: 300px"
           onMouseOverRow="atg.noop()"
           onRowClick="atg.noop()"
           onCellClick="atg.noop()">
      </div>
    </div>
    <script type="text/javascript">
      dojo.require("dijit.form.DateTextBox");
      form = dojo.byId('catalogSearchForm');
      dojo.provide("atg.commerce.csr.catalog.moreCatalogs");
      atg.commerce.csr.catalog.moreCatalogs.isFormFilled = function () {
        return !(atg.service.form.isFormEmpty('catalogSearchForm') ||
                (form.displayName.value == '' && 
                (form.moreCatalogsStartDateInput.value == "<c:out value='${userPreferredLocale.datePattern}'/>" || form.moreCatalogsStartDateInput.value == "") && 
                (form.moreCatalogsEndDateInput.value == "<c:out value='${userPreferredLocale.datePattern}'/>" || form.moreCatalogsEndDateInput.value == "")));
      }
      atg.commerce.csr.catalog.moreCatalogs.validate = function () {
        var form = dojo.byId('catalogSearchForm');
        if (!atg.commerce.csr.catalog.moreCatalogs.isFormFilled()) {
          form.searchButton.disabled = true;
        }
        else {
          form.searchButton.disabled = false;
        }
      }
      _container_.onLoadDeferred.addCallback(function () {
        if (!atg.commerce.csr.catalog.moreCatalogs.isFormFilled()) {
          form.searchButton.disabled = true;
        }
        atg.service.form.watchInputs('catalogSearchForm', atg.commerce.csr.catalog.moreCatalogs.validate);
      });
      _container_.onUnloadDeferred.addCallback(function () {
        atg.service.form.unWatchInputs('catalogSearchForm');
      });
    </script>
  </dsp:layeredBundle>
  </c:catch>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/catalog/moreCatalogs.jsp#1 $$Change: 946917 $--%>
