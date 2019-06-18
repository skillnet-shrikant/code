<%--
 This page defines the customer search panel
 @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/search.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>
<c:catch var="exception">
<dspel:page xml="true">
  <dspel:layeredBundle basename="atg.svc.agent.WebAppResources">	
		<dspel:importbean var="formHandler" bean="/atg/svc/agent/ui/formhandlers/CustomerSearchTreeQueryFormHandler"/>
		<dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
		<dspel:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
		<dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
		<dspel:importbean bean="/atg/svc/security/droplet/HasAccessRight"/>
		<dspel:importbean var="agentUIConfig" bean="/atg/svc/agent/ui/AgentUIConfiguration"/>
		<dspel:importbean var="customerProfileSearchUIConfiguration" bean="/atg/svc/agent/ui/CustomerProfileSearchUIConfiguration"/>
		<dspel:importbean var="profileSearchConfiguration" bean="/atg/userprofiling/textsearch/ProfileSearchConfiguration" />		
        <dspel:importbean var="agentSearchTools" bean="/atg/svc/search/AgentSearchTools"/>
		<dspel:importbean var="defaultPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerSearchDefault" />
		<dspel:importbean var="extendedPageFragment" bean="/atg/svc/agent/ui/fragments/customer/CustomerSearchExtended" /> 
         <dspel:importbean var="agentSearchRequestTracker" bean="/atg/svc/agent/ui/AgentProfileSearchRequestTracker" /> 
         <dspel:getvalueof var="searchEnvironmentName" value="${profileSearchConfiguration.searchEnvironmentName}" />
         <svc-agent:isSearchAvailable agentSearchTools="${agentSearchTools}" searchEnvironmentName="searchEnvironmentName" param="profileSearchAvailable"/>

		<dspel:getvalueof var="hideCreateNewLinks" param="hideCreateNewLinks"/>
		<dspel:droplet name="HasAccessRight">
		  <dspel:param name="accessRight" value="Allowed to Create New Profile"/>
		  <dspel:oparam name="accessGranted">
		    <c:set var="createProfilesPriv" value="true"/>
		  </dspel:oparam>
		  <dspel:oparam name="accessDenied">
		    <c:set var="createProfilesPriv" value="false"/>
		  </dspel:oparam>
		</dspel:droplet>

    <svc-agent:getLastSearchRequest searchFormHandler="${formHandler}" requestChainToken="${agentSearchRequestTracker.requestChainToken}"/>
		
		<c:choose>
			<%-- Search available --%>
			<c:when test="${profileSearchAvailable}">
				<%-- Javascript used by the paging controls in searchResultsPaging.jsp--%>
				<script language="JavaScript" type="text/javascript">
				
				    function goToPage(pageNum)
				    {
				    var pageNumber = pageNum -1;
				    
				      dojo.byId("atg_service_customer_searchPageNum").value = pageNumber;
				      atg.service.customer.search.profileSearch();
				      return false;
				    }
				</script>
				<c:if test="${!hideCreateNewLinks}">
				  <ul class="atg_svc_panelToolBar">
				    <c:choose>
				      <c:when test="${createProfilesPriv}">
				        <li class="atg_svc_last"><a href="#" onclick="createNewCustomer();return false;"><fmt:message key="customer.search.createNew"/></a></li>
				        <li><a href="#" onclick="atg.service.customer.search.newSearch();return false;"><fmt:message key="customer.search.newSearch"/></a></li>
				      </c:when>
				      <c:otherwise>
				        <li class="atg_svc_last"><a href="#" onclick="atg.service.customer.search.newSearch();return false;"><fmt:message key="customer.search.newSearch"/></a></li>
				      </c:otherwise>
				    </c:choose>
				  </ul>
				</c:if>
				
				
				<div class="atg_svc_content">
				  <div class="atg_svc_searchForm">
				    <div id="ea_service_customer_search"></div><br />
				<dspel:form id="atg_service_customer_searchForm" method="post">    
          <div class="atg-csc-base-table">
				    <c:if test="${not empty defaultPageFragment.URL}">        
				      <dspel:include src="${defaultPageFragment.URL}" otherContext="${defaultPageFragment.servletContext}" />
				    </c:if> 
				    
				    <c:if test="${not empty extendedPageFragment.URL}">       
				      <dspel:include src="${extendedPageFragment.URL}" otherContext="${extendedPageFragment.servletContext}" />
				    </c:if> 
            <div class="atg-csc-base-table-row">
              <div class="atg-csc-base-table-cell atg-base-table-customer-search-first-label"></div><div class="atg-csc-base-table-cell"></div>
              <div class="atg-csc-base-table-cell atg-base-table-customer-search-label"></div><div class="atg-csc-base-table-cell"></div>
              <div class="atg-csc-base-table-cell atg-base-table-customer-search-label"></div>
              <div class="atg_svc_panelFooter atg-csc-base-table-cell">
                <input type="button" value="<fmt:message key='customer.search.button'/>" name="searchButton" id="searchButton" onclick="dojo.byId('atg_service_customer_searchPageNum').value = '0'; atg.service.customer.search.profileSearch();return false;"/>
              </div>
            </div>
				  </div>  
				  <%-- Note the priority="-10" here, needed so the handleSearch gets called after the setters, not before --%>
				  <dspel:input type="hidden" priority="-10" value="" bean="CustomerSearchTreeQueryFormHandler.search"/>
				  
				  <dspel:input type="hidden" beanvalue="ProfileSearchConfiguration.searchEnvironmentName" bean="CustomerSearchTreeQueryFormHandler.searchRequest.searchEnvironmentName"/>
				  <dspel:input type="hidden" id="atg_service_customer_searchPageNum" bean="CustomerSearchTreeQueryFormHandler.searchRequest.pageNum" value="0"/>
				  <dspel:input type="hidden" bean="CustomerSearchTreeQueryFormHandler.searchRequest.pageSize" beanvalue="CustomerSearchTreeQueryFormHandler.maxSearchResultsPerPage"/>
				  <dspel:input type="hidden" bean="CustomerSearchTreeQueryFormHandler.searchRequest.maxResults" beanvalue="CustomerSearchTreeQueryFormHandler.maxSearchResults"/>
				 
				 
				  <%--<dspel:input type="hidden" id="atg_service_customer_docSortPred" 
				        bean="CustomerSearchTreeQueryFormHandler.searchRequest.docSortPred" value="0"/>--%>
				        
				  <dspel:input type="hidden" id="atg_service_customer_docSort" bean="CustomerSearchTreeQueryFormHandler.searchRequest.docSort" value="strprop"/>
				  
				  <dspel:input type="hidden" bean="CustomerSearchTreeQueryFormHandler.searchRequest.docProps" value="all"/>
				  
				  <dspel:input type="hidden" id="atg_service_customer_docSortProp" bean="CustomerSearchTreeQueryFormHandler.searchRequest.docSortProp" beanvalue="CustomerProfileSearchUIConfiguration.defaultSortField"/>
				  
				  <dspel:input type="hidden" id="atg_service_customer_docSortOrder" 
				        bean="CustomerSearchTreeQueryFormHandler.searchRequest.docSortOrder" beanvalue="CustomerProfileSearchUIConfiguration.defaultSortDirection"/>
				 
          <dspel:input type="hidden" bean="CustomerSearchTreeQueryFormHandler.searchRequest.saveRequest"  value="true" priority="30"/>
          <dspel:input type="hidden" bean="CustomerSearchTreeQueryFormHandler.searchRequest.multiSearchSession"  value="true" priority="30"/>
				</dspel:form>
				</div>
				</div>
				
				<!-- this form is submitted to clear the search request on the form handler -->
                <dspel:form id="atg_service_customer_resetForm" method="post">
                  <dspel:input type="hidden" priority="-10" value="" bean="CustomerSearchTreeQueryFormHandler.clearForm"/>
                </dspel:form>
				<script type="text/javascript">
				
				
				dojo.provide("atg.service.customer.search");
				
				atg.service.customer.search.newSearch = function() {
				  dojo.byId('atg_service_customer_searchForm').reset();
				  dojo.byId('atg_service_customer_searchPageNum').value = '0';
				  atg.service.customer.search.validate();
				  
				  var theForm = dojo.byId('atg_service_customer_resetForm');
          
                  atgSubmitAction({ 
                    form: theForm, 
                    panels: ["customerSearchPanel","customerSearchResultsPanel"],
                    showLoadingCurtain: true
                  });
				
				}
				
				atg.service.customer.search.defaultSortField = "<dspel:valueof bean="/atg/svc/agent/ui/CustomerProfileSearchUIConfiguration.defaultSortField"/>";
				
				atg.service.customer.search.sortFieldArray = new Array(3);
				
				atg.service.customer.search.sortFieldArray[0] = atg.service.customer.search.defaultSortField;
				atg.service.customer.search.sortFieldArray[1] = "";
				atg.service.customer.search.sortFieldArray[2] = "";
				
				
				
				
				atg.service.customer.search.validate = function () {
				  if (atg.service.form.isFormEmpty('atg_service_customer_searchForm', true)) {
				    dojo.byId('atg_service_customer_searchForm').searchButton.disabled = true;
				  }
				  else {
				    dojo.byId('atg_service_customer_searchForm').searchButton.disabled = false;
				  }
				}
				
				/**
				* Function to copy the form fields to the hidden fields
				*/
				atg.service.customer.search.setHiddenSearchFieldValues = function (postfix, hiddenNameValue, hiddenOpValue) {
				
				  var hiddenName = "atg_service_customer_search" + postfix + "Name";
				  var hiddenOp = "atg_service_customer_search" + postfix + "Op";
				  var valueField = "atg_service_customer_search" + postfix + "Value";
				  
				  if(postfix == "Phone") {
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
				* This function checks the search field values, and clears the associated field name
				* and op values, otherwise the search engine throws an error
				*/
				atg.service.customer.search.setFieldValues = function () {
				
				   atg.service.customer.search.setHiddenSearchFieldValues("FirstName", 
                                                              "${formHandler.firstNameProperty}", 
				                                                      "starts");
				                                                      
				    atg.service.customer.search.setHiddenSearchFieldValues("LastName", 
                                                              "${formHandler.lastNameProperty}", 
				                                                      "starts");
				    
				    atg.service.customer.search.setHiddenSearchFieldValues("Login" ,
                                                              "${formHandler.loginProperty}", 
				                                                      "starts");
				                                                      
				    atg.service.customer.search.setHiddenSearchFieldValues("Email", 
                                                              "${formHandler.emailProperty}", 
				                                                      "starts");
				                                                      
				    atg.service.customer.search.setHiddenSearchFieldValues("PostCode", 
                                                              "${agentUIConfig.customerSearchAddressPropertyName}.${formHandler.postalCodeProperty}", 
				                                                      "starts");
				    
				    atg.service.customer.search.setHiddenSearchFieldValues("Phone", 
                                                              "${agentUIConfig.customerSearchAddressPropertyName}.${formHandler.phoneNumberProperty}", 
				                                                      "starts");
				
				    // Call a well-defined function name in the extended panel to restore the default state.
				  // This will only happen if the function exists and the panel is in use.
				  if(typeof atg.service.customer.search.setExtendedDefaultValues == 'function') { 
            atg.service.customer.search.setExtendedDefaultValues(); 
          }
				}
				
				/**
				* This function updates the sort field array as a first in, first out, queue
				*
				*/
				atg.service.customer.search.updateSortFieldArray = function (sortField) {
				
				  //remove the field if it's already in the array, and move the other array
				  //elements backwards
				  if(atg.service.customer.search.sortFieldArray[0] == sortField)
				  {
				    atg.service.customer.search.sortFieldArray[0] = atg.service.customer.search.sortFieldArray[1];
				    atg.service.customer.search.sortFieldArray[1] = atg.service.customer.search.sortFieldArray[2];
				    atg.service.customer.search.sortFieldArray[2] = "";
				  }
				  if(atg.service.customer.search.sortFieldArray[1] == sortField)
				  {
				    atg.service.customer.search.sortFieldArray[1] = atg.service.customer.search.sortFieldArray[2];
				    atg.service.customer.search.sortFieldArray[2] = "";
				  }
				  if(atg.service.customer.search.sortFieldArray[2] == sortField)
				  {
				    atg.service.customer.search.sortFieldArray[2] = "";
				  }
				
				  atg.service.customer.search.sortFieldArray[2] == atg.service.customer.search.sortFieldArray[1];
				  atg.service.customer.search.sortFieldArray[1] == atg.service.customer.search.sortFieldArray[0];
				  atg.service.customer.search.sortFieldArray[0] == sortField;
				
				}
				
				/**
				* Changes the overall sort direction for a search
				*
				*/
				atg.service.customer.search.changeSortDirection = function () {
				  var docSortOrderField = dojo.byId("atg_service_customer_docSortOrder");
				  if(docSortOrderField.value == "ascending")
				    docSortOrderField.value = "descending";
				  else
				    docSortOrderField.value = "ascending";
				}
				
				/**
				* Changes the sort property
				*
				*/
				atg.service.customer.search.changeSortProp = function (sortProp) {
				
				  var docSortOrderProp = dojo.byId("atg_service_customer_docSortProp");
				  docSortOrderProp.value = sortProp;
				
				  var docSortOrderField = dojo.byId("atg_service_customer_docSortOrder");
				  docSortOrderField.value = "<dspel:valueof bean="/atg/svc/agent/ui/CustomerProfileSearchUIConfiguration.defaultSortDirection"/>";
				}
				
				/**
				* Builds the docSortPredicate string
				*
				*/
				atg.service.customer.search.buildAndSetDocSortPredicate = function () {
				  
				  var predString = "";
				  
				  for(i=0;i<3;i++)
				  {
				    if(atg.service.customer.search.sortFieldArray[i] != "")
				    {
				      if(predString != "")
				      predString = predString + "|";
				    
				      predString = predString + "strprop:" 
				               + dojo.byId("atg_service_customer_docSortOrder").value
				               + ":"
				               + atg.service.customer.search.sortFieldArray[i];
				    }
				  }
				  dojo.byId("atg_service_customer_docSortPred").value = predString;
				}
				
				/**
				* handles a sort request
				*
				*/
				atg.service.customer.search.handleSort = function (fieldName) {
				
				  //alert("fieldName = " + fieldName + "atg_service_customer_docSortOrder = " + dojo.byId("atg_service_customer_docSortOrder").value + "atg_service_customer_docSortProp = " + dojo.byId('atg_service_customer_docSortProp').value);
				  
				  if(fieldName == dojo.byId('atg_service_customer_docSortProp').value)
				  {
				    atg.service.customer.search.changeSortDirection();
				  }
				  else
				  {  
				    atg.service.customer.search.changeSortProp(fieldName);
				  }
				  dojo.byId('atg_service_customer_searchPageNum').value = "0";
				  atg.service.customer.search.profileSearch();
				}
				
				/**
				* Performs the search
				*
				*/
				atg.service.customer.search.profileSearch = function () {
				
				  var theForm = dojo.byId("atg_service_customer_searchForm");
				  
				  var elements = dojo.query("input", "atg_service_customer_searchForm");
				  for (var i = 0, length = elements.length; i < length; i++) {
				    var item = elements[i];
				    var type=item.type;
				    if (type == "text" || type == "textarea") {
				      item.value = dojo.trim(item.value);
				    }
				  };

				  atg.service.customer.search.setFieldValues();
				  
				  //atg.service.customer.search.buildAndSetDocSortPredicate();
				  
				  //if the pop up is showing, were in the pop up
				  if(dojo.byId("atg_commerce_csr_catalog_customerSelectionPopup") && dojo.byId("atg_commerce_csr_catalog_customerSelectionPopup").style.display == "block")
				  {
				    atgSubmitAction({ 
				    url: "${CSRConfigurator.contextRoot}/panels/customer/customerSelectionPopupResults.jsp?_windowId=${windowId}",
				    form: theForm, 
				    showLoadingCurtain: false
				    });                                       
				                                          
				  }
				  else
				  {
				    atgSubmitAction({ 
				    form: theForm, 
				    panels: ["customerSearchResultsPanel"],
				    showLoadingCurtain: false
				    }); 
				  }
				}
				
				dojo.byId('atg_service_customer_searchForm').reset = function(){
				  dojo.byId("atg_service_customer_searchFirstNameValue").value = "";
				  dojo.byId("atg_service_customer_searchLastNameValue").value = "";
				  dojo.byId("atg_service_customer_searchLoginValue").value = "";
				  dojo.byId("atg_service_customer_searchEmailValue").value = "";
				  dojo.byId("atg_service_customer_searchPostCodeValue").value = "";
				  dojo.byId("atg_service_customer_searchPhoneValue").value = "";
				  
				  atg.service.customer.search.setFieldValues();
				  
          // Call a well-defined function name in the extended panel to restore the default state.
				  // This will only happen if the function exists and the panel is in use.
				  if(typeof atg.service.customer.search.restoreExtendedState == 'function') { 
            atg.service.customer.search.restoreExtendedState(); 
          }
				  
				};
				
				
				_container_.onLoadDeferred.addCallback(function () {
				  if (atg.service.form.isFormEmpty('atg_service_customer_searchForm')) {
				    dojo.byId('atg_service_customer_searchForm').searchButton.disabled = true;
				  }
				  atg.service.form.watchInputs('atg_service_customer_searchForm', atg.service.customer.search.validate);
				  atg.progress.update('cmcCustomerSearchPS');
				  
				  atg.keyboard.registerFormDefaultEnterKey("atg_service_customer_searchForm", "searchButton");
				  
				});
				_container_.onUnloadDeferred.addCallback(function () {
				  atg.service.form.unWatchInputs('atg_service_customer_searchForm');
				  atg.keyboard.unRegisterFormDefaultEnterKey("atg_service_customer_searchForm");
				});
				</script>
			</c:when>
			<%-- End search available --%>
			
			<%-- When Search is not available --%>
			<c:otherwise>
				<c:if test="${!hideCreateNewLinks}">
				  <ul class="atg_svc_panelToolBar">
				      <c:if test="${createProfilesPriv}">
				        <li class="atg_svc_last"><a href="#" onclick="createNewCustomer();return false;"><fmt:message key="customer.search.createNew"/></a></li>
				      </c:if>
				  </ul>
				</c:if>
				<dspel:include src="/panels/customer/searchUnavailable.jsp" otherContext="${UIConfig.contextRoot}">
				</dspel:include>
			</c:otherwise>
			<%-- End Search is not available --%>
		</c:choose>
  </dspel:layeredBundle>
</dspel:page>
</c:catch>
<c:if test="${exception != null}">
  <c:out value="${exception}"/>
</c:if>
<%-- @version $Id: //application/service-UI/version/11.2/framework/Agent/src/web-apps/ServiceFramework/panels/customer/search.jsp#1 $$Change: 946917 $--%>
