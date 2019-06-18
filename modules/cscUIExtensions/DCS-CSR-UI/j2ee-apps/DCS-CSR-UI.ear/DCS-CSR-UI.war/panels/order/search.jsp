<%--
 This page defines the order search panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/search.jsp#2 $
 @updated $DateTime: 2015/02/26 10:47:28 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.commerce.csr.Messages">
<dsp:importbean var="formHandler" bean="/atg/commerce/custsvc/order/OrderSearchTreeQueryFormHandler"/> 
<dsp:importbean var="orderSearchConfiguration" bean="/atg/commerce/textsearch/OrderSearchConfiguration"/> 
<dsp:importbean var="orderSearchUIConfiguration" bean="/atg/commerce/custsvc/order/OrderSearchUIConfiguration"/>
<dsp:importbean bean="/atg/commerce/states/OrderStates"/>
<dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
<dsp:importbean var="agentSearchTools" bean="/atg/svc/search/AgentSearchTools"/>
<dsp:importbean var="agentSearchRequestTracker" bean="/atg/commerce/custsvc/order/AgentOrderSearchRequestTracker" /> 
    
<dsp:importbean var="customerPanelConfig" bean="/atg/svc/agent/customer/CustomerSearchPanelConfig" />
<dsp:importbean var="defaultPageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/OrderSearchDefault" /> 
<dsp:importbean var="extendedPageFragment" bean="/atg/commerce/custsvc/ui/fragments/order/OrderSearchExtended" /> 

<fmt:message var="invalidCountry" key="invalidCountry" />

<dsp:getvalueof var="searchEnvironmentName" value="${orderSearchConfiguration.searchEnvironmentName}" />
<svc-agent:isSearchAvailable agentSearchTools="${agentSearchTools}" searchEnvironmentName="searchEnvironmentName" param="orderSearchAvailable"/>  

<svc-agent:getLastSearchRequest searchFormHandler="${formHandler}" requestChainToken="${agentSearchRequestTracker.requestChainToken}"/>


<dsp:tomap var="uiStore" bean="AgentOrderSearchRequestTracker.properties"/>

  <c:choose>
      <%-- Search available --%>
      <c:when test="${orderSearchAvailable}">
        <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
        <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
        <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>
        <fmt:message var="startDateErrorMessage" key="order.search.validation.error.startDate"><fmt:param value="${userPreferredLocale.datePattern}"/></fmt:message>
        <fmt:message var="dateFormatErrorMessage" key="order.search.validation.error.dateFormat"><fmt:param value="${userPreferredLocale.datePattern}"/></fmt:message>
        <fmt:message var="endDateErrorMessage" key="order.search.validation.error.endDate"><fmt:param value="${userPreferredLocale.datePattern}"/></fmt:message>
        <fmt:message var="startDateIsGreaterThanEndDateErrorMessage" key="order.search.validation.error.startDateIsGreaterThanEndDate"/>
        <dsp:form id="atg_commerce_csr_orderSearchForm" method="post">
<script language="JavaScript" type="text/javascript">
        
            function goToPage(pageNum)
            {
            var pageNumber = pageNum -1;
            
              dojo.byId("atg_commerce_order_searchPageNum").value = pageNumber;
              atg.commerce.csr.order.performSearch();
              return false;
            }
        </script>
        <div class="atg_commerce_csr_coreSearchOrders">
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_last"><a onclick="atg.commerce.csr.createOrder();event.cancelBubble=true;return false;" href="#"><fmt:message key='create-new-order'/></a></li>
            
            <li><a href="#" onclick="atg.commerce.csr.order.newSearch();return false;"><fmt:message key='new-search'/></a></li>
          </ul>
          
          <c:if test="${not empty defaultPageFragment.URL}">        
            <dsp:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
          </c:if>  
          
          <c:if test="${not empty extendedPageFragment.URL}">        
            <dsp:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
          </c:if>  
          
            <div class="atg_commerce_csr_panelFooter">
              <input type="button" name="advancedSearch" id="advancedSearch" value="<fmt:message key='search-button-label'/>" onclick="dojo.byId('atg_commerce_order_searchPageNum').value = '0'; atg.commerce.csr.order.performSearch();return false;"/>
            </div>
          </div>
          
          <%-- Note the priority="-10" here, needed so the handleSearch gets called after the setters, not before --%>
          <dsp:input type="hidden" priority="-10" value="" bean="OrderSearchTreeQueryFormHandler.search"/>
          
          <dsp:input type="hidden" beanvalue="OrderSearchConfiguration.searchEnvironmentName" bean="OrderSearchTreeQueryFormHandler.searchRequest.searchEnvironmentName"/>
          
          <dsp:input type="hidden" id="atg_commerce_order_searchPageNum" bean="OrderSearchTreeQueryFormHandler.searchRequest.pageNum" value="0"/>
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.pageSize" beanvalue="OrderSearchTreeQueryFormHandler.maxSearchResultsPerPage"/>
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.maxResults" beanvalue="OrderSearchTreeQueryFormHandler.maxSearchResults"/>
          
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.docProps" value="all"/>
          
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.maxResults" beanvalue="OrderSearchTreeQueryFormHandler.maxSearchResults"/>
        
                
          <dsp:input type="hidden" id="atg_commerce_order_searchDocSort" bean="OrderSearchTreeQueryFormHandler.searchRequest.docSort" value="strprop"/>
          
          <dsp:input type="hidden" id="atg_commerce_order_searchDocSortProp" bean="OrderSearchTreeQueryFormHandler.searchRequest.docSortProp" beanvalue="OrderSearchUIConfiguration.defaultSortField"/>
          <dsp:input type="hidden" id="atg_commerce_order_searchDocSortOrder" bean="OrderSearchTreeQueryFormHandler.searchRequest.docSortOrder" beanvalue="OrderSearchUIConfiguration.defaultSortDirection"/>
          
          <dsp:input type="hidden" id="atg_service_customer_isAdvancedSearch" 
                 bean="OrderSearchTreeQueryFormHandler.advancedSearch" value="${uiStore.isAdvancedSearch}"/>
                 
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.saveRequest"  value="true" priority="30"/>
          <dsp:input type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.multiSearchSession"  value="true" priority="30"/>
         
        </dsp:form>
        <!-- this form is submitted to clear the search request on the form handler -->
        <dsp:form id="atg_commerce_csr_resetForm" method="post">
          <dsp:input type="hidden" priority="-10" value="" bean="OrderSearchTreeQueryFormHandler.clearForm"/>
        </dsp:form>
        <script type="text/javascript">
        
        /**
        * reset the search form, and clear the search on the form handler
        */
        atg.commerce.csr.order.newSearch = function()
        {
          
          dojo.byId('atg_commerce_csr_orderSearchForm').reset();
          dojo.byId('atg_commerce_order_searchPageNum').value = '0';
          atg.commerce.csr.order.searchValidate();
          
          var theForm = dojo.byId('atg_commerce_csr_resetForm');
          
          atgSubmitAction({ 
            form: theForm, 
            panels: ["cmcOrderSearchP","cmcOrderResultsP"],
            showLoadingCurtain: true
          });
        }
        
        dojo.require("atg.widget.form.FilteringSelect");
        
        
        atg.commerce.csr.order.ChangeCountry = function () {
                  if (stateStore) {
                    var states = dijit.byId("atg_commerce_order_searchAdvancedState");
                    var countries = dijit.byId("atg_commerce_order_searchAdvancedCountry");
                    var stateUrl = "${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&isOrderSearch=true&countryCode=";
                    stateUrl += countries.getValue();
                    stateStore = new dojo.data.ItemFileReadStore({url:stateUrl});
                    states.store = stateStore;
                    states.setValue("");
                    dojo.byId("atg_commerce_order_searchAdvancedState").value = "";
                  }
        };
        
        atg.commerce.csr.order.startDateOnClick = dojo.byId('startDateImg').onclick;
        atg.commerce.csr.order.endDateOnClick = dojo.byId('endDateImg').onclick;
        
        atg.commerce.csr.order.toggleDateFieldsAccessibility = function (disable) {
          var startDateOnClickFunction = '';
          var endDateOnClickFunction = '';
          if (!disable) {
            startDateOnClickFunction = atg.commerce.csr.order.startDateOnClick;
            endDateOnClickFunction = atg.commerce.csr.order.endDateOnClick;
          }
          dojo.byId('atg_commerce_order_searchStartDate').disabled = disable;
          dojo.byId('atg_commerce_order_searchEndDate').disabled = disable;
          dijit.byId('atg_commerce_order_searchStartDate').setDisabled(disable);
          dijit.byId('atg_commerce_order_searchEndDate').setDisabled(disable);
          dojo.byId('startDateImg').onclick = startDateOnClickFunction;
          dojo.byId('endDateImg').onclick = endDateOnClickFunction;
        };
        
        atg.commerce.csr.order.searchValidate = function () {
          if (atg.commerce.csr.order.isSearchFormEmpty('atg_commerce_csr_orderSearchForm', true)) {
            dojo.byId('atg_commerce_csr_orderSearchForm').advancedSearch.disabled = true;
            atg.commerce.csr.order.toggleDateFieldsAccessibility(true);
          }
          else {
            dojo.byId('atg_commerce_csr_orderSearchForm').advancedSearch.disabled = false;
            atg.commerce.csr.order.toggleDateFieldsAccessibility(false);
          }
        }
        
        _container_.onLoadDeferred.addCallback(function () {          
          dijit.byId("atg_commerce_order_searchAdvancedState").isValid = function () {return true;};
          dijit.byId("atg_commerce_order_searchAdvancedCountry").isValid = function () {return true;};
          atg.commerce.csr.order.setDefaultValues();
          atg.commerce.csr.order.restoreState();
          if (atg.commerce.csr.order.isSearchFormEmpty('atg_commerce_csr_orderSearchForm', true)) {
            dojo.byId('atg_commerce_csr_orderSearchForm').advancedSearch.disabled = true;
            atg.commerce.csr.order.toggleDateFieldsAccessibility(true);
          }
          atg.service.form.watchInputs('atg_commerce_csr_orderSearchForm', atg.commerce.csr.order.searchValidate);
          atg.keyboard.registerFormDefaultEnterKey("atg_commerce_csr_orderSearchForm", "advancedSearch");
        });
        
        _container_.onUnloadDeferred.addCallback(function () {
          atg.service.form.unWatchInputs('atg_commerce_csr_orderSearchForm');
        });

        /*
        * Sets default values for form fields to empty string. Default values are used on form reset
        */
        atg.commerce.csr.order.setDefaultValues = function () {
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchOrderIdValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchEmailValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchFirstNameValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchLastNameValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchLoginValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchContainsSkuValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchStatusValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchItemStatusValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchShipMethodStatusValue","");
          atg.commerce.csr.order.setDefaultValue("atg_commerce_order_searchOrderNumberValue","");
          // Call a well-defined function name in the extended panel to set the default values.
          // This will only happen if the function exists and the panel is in use.
          if(typeof atg.commerce.csr.order.setExtendedDefaultValues == 'function') { 
            atg.commerce.csr.order.setExtendedDefaultValues(); 
          }

        };

        /*
        * Sets default value for element to provided string.
        */
        atg.commerce.csr.order.setDefaultValue = function (elementId, defaultValue) {
          var element = dojo.byId(elementId);
          if(element){
            var value = element.defaultValue;
            element.defaultValue = defaultValue;
            element.value = value;
          }
        };

        atg.commerce.csr.order.restoreState = function () {
          //reset the order state dropdown
          dojo.byId("atg_commerce_order_searchStatus").value = dojo.byId("atg_commerce_order_searchStatusValue").value;
          dojo.byId("atg_commerce_order_searchItemStatus").value = dojo.byId("atg_commerce_order_searchItemStatusValue").value;
          dojo.byId("atg_commerce_order_searchShipMethodStatus").value = dojo.byId("atg_commerce_order_searchShipMethodStatusValue").value;
          //dojo.byId("atg_commerce_order_searchOrderNumber").value = dojo.byId("atg_commerce_order_searchOrderNumberValue").value;
          //reset the address country and state dropdown
          dijit.byId("atg_commerce_order_searchAdvancedCountry").setValue(dojo.byId("atg_commerce_order_searchAdvancedCountryValue").value);
          dijit.byId("atg_commerce_order_searchAdvancedCountry").onChange = function(){atg.commerce.csr.order.ChangeCountry();};
          
          setTimeout('dijit.byId("atg_commerce_order_searchAdvancedState").setValue(dojo.byId("atg_commerce_order_searchAdvancedStateValue").value)',500); 
          
          //reset the advanced pane
          if(dojo.byId("atg_service_customer_isAdvancedSearch").value == "TRUE")
          {
            dijit.byId("atg_commerce_csr_advSearch_pane").toggle();    
            dojo.byId("addressPicker${uiStore.addressPicker}").checked = true;
          }
          
          
          //reset the date fields
          if(dojo.byId("atg_commerce_order_searchEndDateValue").value != "")
          {
          
            var endDate = new Date();
            endDate.setTime(parseInt(dojo.byId("atg_commerce_order_searchEndDateValue").value) * 1000);

            //dojo.byId("atg_commerce_order_searchEndDate").value = endMonthString + "/" + endDayString + "/" + endDate.getFullYear();
            dijit.byId("atg_commerce_order_searchEndDate").setValue(endDate);
          }
          
          if(dojo.byId("atg_commerce_order_searchStartDateValue").value != "")
          {
           
            var startDate = new Date();
            startDate.setTime(parseInt(dojo.byId("atg_commerce_order_searchStartDateValue").value) * 1000);
            
            
            dijit.byId("atg_commerce_order_searchStartDate").setValue(startDate);
          }
          
          // Call a well-defined function name in the extended panel to restore the default state.
          // This will only happen if the function exists and the panel is in use.
          if(typeof atg.commerce.csr.order.restoreExtendedState == 'function') { 
            atg.commerce.csr.order.restoreExtendedState(); 
          }
          
        };
        
        /**
        * checks the form to see if it's empty, ignoring the address radio button
        */
        atg.commerce.csr.order.isSearchFormEmpty = function (formId, trimInputs) {
          var elements = dojo.query("input", formId);
          for(var i = 0, length = elements.length;i < length; i++)
          {
            var item = elements[i];
            var type = item.type;
            //check the element isn't a radio button
            if(type != "radio" && type != "submit" && type != "hidden" && item.id != "atg_commerce_order_searchStartDate" && item.id != "atg_commerce_order_searchEndDate" && item.name != "startDateField" && item.name != "endDateField" && item.name != "atg_commerce_order_searchAdvancedState")
            {
              //if it has a value, retrun false
              var itemValue = trimInputs ? dojo.trim(item.value) : item.value;
              if (itemValue != "") {return false;}
            }
          }
          
          //do the same for select fields
          elements = dojo.query("select", formId);
          for ( i = 0, length = elements.length; i < length; i++)
          {
            item = elements[i];
            if (item.value != "") {return false;}
          }  
          
          //found nothing, return true
          return true;
        };
        
        /**
        * checks the start and end data fields if they are valid and start data is less than end data
        */
        atg.commerce.csr.order.isDatesCorrect = function (startDateString, endDateString, dateFormat, startDateErrorMessage, endDateErrorMessage, startDateIsGreaterThanEndDateErrorMessage) {
          var datesCorrect = true;
          
          var startDate = dojo.date.locale.parse(startDateString, {datePattern: dateFormat, selector: "date"});
          var endDate = dojo.date.locale.parse(endDateString, {datePattern: dateFormat, selector: "date"});
          if(startDateString!="" && !startDate){
            datesCorrect = false;
              atg.commerce.csr.catalog.showError(startDateErrorMessage);
          }
          if(endDateString!="" && !endDate){
            datesCorrect = false;
            atg.commerce.csr.catalog.showError(endDateErrorMessage);
          }
          if(startDate && endDate && startDate.getTime() > endDate.getTime()){
            datesCorrect = false;
            atg.commerce.csr.catalog.showError(startDateIsGreaterThanEndDateErrorMessage);
          }
          return datesCorrect;
        };
        
        /**
        * Function to copy the form fields to the hidden fields
        */
        atg.commerce.csr.order.setHiddenSearchFieldValues = function (postfix, hiddenNameValue, hiddenOpValue) {
        
          var hiddenName = "atg_commerce_order_search" + postfix + "Name";
          var hiddenOp = "atg_commerce_order_search" + postfix + "Op";
          var valueField = "atg_commerce_order_search" + postfix + "Value";
        
          dojo.byId(valueField).value = dojo.trim(dojo.byId(valueField).value);
          
          if(postfix == "AdvancedPhone") 
          {
		    dojo.byId(valueField).value = dojo.byId(valueField).value.replace(/\D+/g, "");
		  }
          
          if(dojo.byId(valueField).value != "")
          {
            dojo.byId(hiddenName).value = hiddenNameValue;
            dojo.byId(hiddenOp).value = hiddenOpValue;  
          }
          else
          {
            dojo.byId(hiddenName).value = "";
            dojo.byId(hiddenOp).value = "";
            dojo.byId(valueField).value = "";
          }
        };
        
        /**
        * Function to set the dates  to the correct format for a search
        */
        atg.commerce.csr.order.setHiddenSearchDateFieldValues = function (postfix, hiddenNameValue, hiddenOpValue, dateFormat) {
          
          var inputName = "atg_commerce_order_search" + postfix;
          var hiddenName = inputName + "Name";
          var hiddenOp = inputName + "Op";
          var valueField = inputName + "Value";
          
          var value = dijit.byId(inputName).getValue();
          if(value != '')
          {
            value = document.getElementById(inputName).value;
            // Put the date into the correct format, create a formatedDate Date object and convert it into a timestamp
            var formatedDate = dojo.date.locale.parse(value, {datePattern: dateFormat, selector: "date"});

            if (postfix == 'StartDate') {
              formatedDate.setHours('00');
              formatedDate.setMinutes('00');
              formatedDate.setSeconds('00');
              formatedDate.setMilliseconds('00');
            } else if (postfix == 'EndDate') {
              formatedDate.setHours('23');
              formatedDate.setMinutes('59');
              formatedDate.setSeconds('59');
              formatedDate.setMilliseconds('00');
            }
            var intDate = parseInt(formatedDate.getTime()/1000);
            
            dojo.byId(valueField).value = intDate;
            
            dojo.byId(hiddenName).value = hiddenNameValue;
            dojo.byId(hiddenOp).value = hiddenOpValue;  
          }
          else
          {
            dojo.byId(hiddenName).value = "";
            dojo.byId(hiddenOp).value = "";
            dojo.byId(valueField).value = "";
          }
        };
        
        /**
        * This function checks the search field values, and clears the associated field name
        * and op values, otherwise the search engine throws an error
        */
        atg.commerce.csr.order.setFieldValues = function () {
        
          dojo.byId("atg_commerce_order_searchStatusValue").value = dojo.byId("atg_commerce_order_searchStatus").value;
          dojo.byId("atg_commerce_order_searchItemStatusValue").value = dojo.byId("atg_commerce_order_searchItemStatus").value;
          dojo.byId("atg_commerce_order_searchShipMethodStatusValue").value = dojo.byId("atg_commerce_order_searchShipMethodStatus").value;
          if(dijit.byId("atg_commerce_order_searchAdvancedState").valueNode != null)
          {
            dojo.byId("atg_commerce_order_searchAdvancedStateValue").value = dijit.byId("atg_commerce_order_searchAdvancedState").valueNode.value;
          }
          if(dijit.byId("atg_commerce_order_searchAdvancedCountry").valueNode != null)
          {
            dojo.byId("atg_commerce_order_searchAdvancedCountryValue").value = dijit.byId("atg_commerce_order_searchAdvancedCountry").valueNode.value;
          }
          
          if(dijit.byId("atg_commerce_csr_advSearch_pane").open)
          {
            dojo.byId("atg_service_customer_isAdvancedSearch").value = "true";
          }
          else
          {
            dojo.byId("atg_service_customer_isAdvancedSearch").value = "false";
          }
        
        
          atg.commerce.csr.order.setHiddenSearchFieldValues("OrderId", 
                                                            "${formHandler.orderIdProperty}", 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("Status", 
                                                            "${formHandler.orderStatusProperty}", 
                                                            "exact");
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("ItemStatus", 
											                  "${formHandler.commerceItemStateProperty}", 
											                  "exact");
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("ShipMethodStatus", 
											                  "${formHandler.shippingMethodProperty}", 
											                  "exact");
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("OrderNumber", 
											                  "${formHandler.orderNumberProperty}", 
											                  "exact");
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("Login" ,
                                                            "${formHandler.loginProperty}", 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("Email", 
                                                            "${formHandler.emailProperty}", 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("FirstName", 
                                                            "${formHandler.firstNameProperty}",  
                                                            "starts");
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("LastName", 
                                                            "${formHandler.lastNameProperty}", 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("ContainsSku", 
                                                            "${formHandler.containsSKUProperty}", 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchDateFieldValues("StartDate", 
                                                            "${formHandler.submittedDateProperty}", 
                                                            "greatereq", "${userPreferredLocale.datePattern}");
                                                            
          atg.commerce.csr.order.setHiddenSearchDateFieldValues("EndDate", 
                                                            "${formHandler.submittedDateProperty}", 
                                                            "lesseq", "${userPreferredLocale.datePattern}");
          
          var firstNameNameString = "";
          var lastNameNameString = "";
          var addressLineName1String = "";
          var addressLineName2String = "";
          var cityNameString = "";
          var stateNameString = "";
          var postalCodeNameString = "";
          var phoneNameString = "";
          
            
          if(dojo.byId("addressPickerBS").checked)
          {
            firstNameNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedFirstNameProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedFirstNameProperty}";
            lastNameNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedLastNameProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedLastNameProperty}";
            addressLine1NameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedAddressLine1Property},${formHandler.shippingGroupsProperty}.${formHandler.advancedAddressLine1Property}";
            addressLine2NameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedAddressLine2Property},${formHandler.shippingGroupsProperty}.${formHandler.advancedAddressLine2Property}";
            cityNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedCityProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedCityProperty}";
            stateNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedStateProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedStateProperty}";
            countryNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedCountryProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedCountryProperty}";
            postalCodeNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedPostalCodeProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedPostalCodeProperty}";
            phoneNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedPhoneProperty},${formHandler.shippingGroupsProperty}.${formHandler.advancedPhoneProperty}";
          }
          else if(dojo.byId("addressPickerB").checked)
          {
            firstNameNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedFirstNameProperty}";
            lastNameNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedLastNameProperty}";
            addressLine1NameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedAddressLine1Property}";
            addressLine2NameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedAddressLine2Property}";
            cityNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedCityProperty}";
            stateNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedStateProperty}";
            countryNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedCountryProperty}";
            postalCodeNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedPostalCodeProperty}";
            phoneNameString = "${formHandler.paymentGroupsProperty}.${formHandler.advancedPhoneProperty}";
          }
          else if(dojo.byId("addressPickerS").checked)
          {
            firstNameNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedFirstNameProperty}";
            lastNameNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedLastNameProperty}";
            cityNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedCityProperty}";
            addressLine1NameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedAddressLine1Property}";
            addressLine2NameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedAddressLine2Property}";
            stateNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedStateProperty}";
            countryNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedCountryProperty}";
            postalCodeNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedPostalCodeProperty}";
            phoneNameString = "${formHandler.shippingGroupsProperty}.${formHandler.advancedPhoneProperty}";
          }
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedFirstName", 
                                                                firstNameNameString, 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedLastName", 
                                                            lastNameNameString, 
                                                            "starts");                                                  
          
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedAddressLine1", 
                                                             addressLine1NameString, 
                                                            "starts");
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedAddressLine2", 
                                                            addressLine2NameString, 
                                                            "starts");
                 
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedCity", 
                                                             cityNameString, 
                                                            "starts"); 
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedState", 
                                                             stateNameString, 
                                                            "exact");   
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedCountry", 
                                                             countryNameString, 
                                                            "exact");  
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedPostalCode", 
                                                             postalCodeNameString, 
                                                            "starts");  
                                                            
          atg.commerce.csr.order.setHiddenSearchFieldValues("AdvancedPhone",                                                                   
                                                            phoneNameString, 
                                                              "starts"); 
          
        };
        
        /**
        * Changes the overall sort direction for a search
        *
        */
        atg.commerce.csr.order.changeSortDirection = function () {
          var docSortOrderField = dojo.byId("atg_commerce_order_searchDocSortOrder");
          if(docSortOrderField.value == "ascending")
            docSortOrderField.value = "descending";
          else
            docSortOrderField.value = "ascending";
        };
        
        /**
        * Changes the sort property
        *
        */
        atg.commerce.csr.order.changeSortProp = function (sortProp) {
          var docSortOrderProp = dojo.byId("atg_commerce_order_searchDocSortProp");
          docSortOrderProp.value = sortProp;
          
          var defaultSortDirection = "<dsp:valueof bean='/atg/commerce/custsvc/order/OrderSearchUIConfiguration.defaultSortDirection'/>";
          var docSortOrderField = dojo.byId("atg_commerce_order_searchDocSortOrder");
          docSortOrderField.value = defaultSortDirection;

          if("submittedDate" == sortProp){
            dojo.byId("atg_commerce_order_searchDocSort").value = "intprop";
          }else{
            dojo.byId("atg_commerce_order_searchDocSort").value = "strprop";
          }
        };
        
        /**
        * handles a sort request
        *
        */
        atg.commerce.csr.order.handleSort = function (fieldName) {
        
          
          if(fieldName == dojo.byId('atg_commerce_order_searchDocSortProp').value)
          {
            atg.commerce.csr.order.changeSortDirection();
          }
          else
          {  
            atg.commerce.csr.order.changeSortProp(fieldName);
          }
          dojo.byId('atg_commerce_order_searchPageNum').value = "0";
          //alert("fieldName = " + fieldName + " atg_commerce_order_searchDocSortOrder = " + dojo.byId("atg_commerce_order_searchDocSortOrder").value + " atg_commerce_order_searchDocSortProp = " + dojo.byId('atg_commerce_order_searchDocSortProp').value);
          atg.commerce.csr.order.performSearch();
        };
        
        atg.commerce.csr.order.performSearch = function () {
          if (dijit.byId("atg_commerce_order_searchStartDate") && !dijit.byId("atg_commerce_order_searchStartDate").isValid() || dijit.byId("atg_commerce_order_searchEndDate") && !dijit.byId("atg_commerce_order_searchEndDate").isValid()) {  
          dijit.byId('messageBar').addMessage({type:'error', summary:"${dateFormatErrorMessage}"});  
            return;
          }
          
          atg.commerce.csr.order.setFieldValues();
          
          var theForm = dojo.byId("atg_commerce_csr_orderSearchForm");
          
          //alert("submitting : " + dojo.byId("atg_commerce_order_searchOrderNumberValue").value);
            
          var startDateString = dijit.byId("atg_commerce_order_searchStartDate").getValue();
          if (startDateString != ""){
            startDateString = document.getElementById("atg_commerce_order_searchStartDate").value;
          }
          var endDateString = dijit.byId("atg_commerce_order_searchEndDate").getValue();
          if (endDateString != ""){
            endDateString = document.getElementById("atg_commerce_order_searchEndDate").value;
          }
          if(atg.commerce.csr.order.isDatesCorrect(startDateString, endDateString,
              "${userPreferredLocale.datePattern}", "${startDateErrorMessage}", "${endDateErrorMessage}", "${startDateIsGreaterThanEndDateErrorMessage}")){
            atgSubmitAction({ 
                form: theForm, 
                panels: ["cmcOrderResultsP"],
                showLoadingCurtain: false
            }); 
          }
        };
        </script>
      </c:when>
      <%-- End search available --%>
      
      <%-- When Search is not available --%>
      <c:otherwise>
        <div class="atg_commerce_csr_coreSearchOrders">
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_last"><a onclick="atg.commerce.csr.createOrder();event.cancelBubble=true;return false;" href="#"><fmt:message key='create-new-order'/></a></li>
          </ul>  
         </div>     
        <dsp:include src="/panels/order/searchUnavailable.jsp" otherContext="${CSRConfigurator.contextRoot}">
        </dsp:include>
      </c:otherwise>
      <%-- End Search is not available --%>
    </c:choose>
</dsp:layeredBundle>
</dsp:page>
<%-- Version: $Change: 953229 $$DateTime: 2015/02/26 10:47:28 $--%>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/search.jsp#2 $$Change: 953229 $--%>
