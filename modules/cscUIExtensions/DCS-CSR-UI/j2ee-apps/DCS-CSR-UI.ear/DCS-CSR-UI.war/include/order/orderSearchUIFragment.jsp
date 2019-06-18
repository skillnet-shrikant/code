<%--
 This UI fragment defines the Order Search Panel that is shipped with the product, 
 but may be replaced by a customer at their discretion. Customers need simply point the configuration file
 to the new JSP snippet for this to take effect.
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/orderSearchUIFragment.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  
<dsp:layeredBundle basename="atg.commerce.csr.Messages">
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderSearchTreeQueryFormHandler"/> 
<dsp:importbean var="customerPanelConfig" bean="/atg/svc/agent/customer/CustomerPanelConfig" />
<dsp:importbean var="orderSearchUIConfiguration" bean="/atg/commerce/custsvc/order/OrderSearchUIConfiguration"/> 
<dsp:importbean bean="/atg/commerce/states/OrderStates"/>
<dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/custsvc/order/OrderStateDescriptions"/>
<dsp:importbean var="agentSearchTools" bean="/atg/svc/search/AgentSearchTools"/>
<dsp:importbean bean="/atg/multisite/ProfileRealmManager"/>
<dsp:importbean bean="atg/commerce/custsvc/util/CSRAgentTools"/>

<script type="text/javascript">
  dojo.require("dijit.form.DateTextBox");
</script>
<dsp:importbean bean="/atg/svc/agent/ui/OriginatingPage"/>
<dsp:setvalue bean="OriginatingPage.pageName" value="orderSearch"/>

  <div class="atg_commerce_csr_content">
    <div id="ea_csc_order_search" class="atg_ea_container"></div>
    <div class="atg_commerce_csr_searchOrder atg-csc-base-table">
      <div class="atg-csc-base-table-row"><label for="order" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='custom-order-number-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderNumberName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[0].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderNumberOp" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[0].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderNumberValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[0].value" 
                 beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[0].value"       size="30" maxlength="40" iclass=""/>
        </div>
        <label for="email" class="atg-csc-base-table-cell atg-base-table-order-search-spacing"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='email-address-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEmailName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[1].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEmailOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[1].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEmailValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[1].value"
                beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[1].value"    size="35" maxlength="254" iclass=""/>
        </div>
      </div> 
      
      
      <div class="atg-csc-base-table-row"><label for="fName" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='first-name-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchFirstNameName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[2].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchFirstNameOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[2].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchFirstNameValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[2].value"
                 beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[2].value"    size="35" maxlength="40" iclass=""/>
        </div>
        <label for="lName" class="atg-csc-base-table-cell atg-base-table-order-search-spacing"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='last-name-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLastNameName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[3].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLastNameOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[3].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLastNameValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[3].value"
              beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[3].value"    size="35" maxlength="40" iclass=""/>
        </div>
      </div>
      
      <div class="atg-csc-base-table-row"><label for="login" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key="login-label" /></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLoginName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[4].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLoginOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[4].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchLoginValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[4].value" 
                beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[4].value" size="35" maxlength="40" iclass=""/>
        </div>
        <label for="sku" class="atg-csc-base-table-cell atg-base-table-order-search-spacing"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='contains-sku-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchContainsSkuName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[5].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchContainsSkuOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[5].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchContainsSkuValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[5].value"
               beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[5].value"     size="35" maxlength="40" iclass=""/>
        </div>
      </div>
          
          
      <div class="orderStatus atg-csc-base-table-row">
      	<label for="oStatus" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='order-status-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStatusName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[6].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStatusOp" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[6].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStatusValue" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[6].value" 
            beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[6].value" type="hidden"/>
          <select id="atg_commerce_order_searchStatus" name="statusField">
            <dsp:droplet name="ForEach">
              <dsp:param bean="OrderStates.stateStringMap" name="array"/>
              <dsp:param name="elementName" value="orderState"/>
              <dsp:oparam name="outputStart">
                <option selected="true" value=""><fmt:message key='all-statuses-value'/></option>
              </dsp:oparam>
              <dsp:oparam name="output">
              <dsp:getvalueof var="orderStateString" param="orderState" idtype="java.lang.String"/>
                <c:if test="${orderStateString != 'INCOMPLETE'}">
                  <option value="${orderStateString}">
                  <dsp:droplet name="OrderStateDescriptions">
                  <dsp:param name="state" value="${orderStateString}"/>
                  <dsp:param name="elementName" value="stateDescription"/>
                  <dsp:oparam name="output">
                    <dsp:valueof param="stateDescription"></dsp:valueof>
                  </dsp:oparam>
                  </dsp:droplet>
                  </option>
                </c:if>
             </dsp:oparam>
            </dsp:droplet> 
          
          </select>
        </div>
      	<label for="oNumber" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='order-atg-id-label'/></span></label>
      	<div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderIdName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[7].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderIdOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[7].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchOrderIdValue" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[7].value"
               beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[7].value"     size="35" maxlength="40" iclass=""/>
        </div>
      </div>
      
      <div class="atg-csc-base-table-row"><label for="oStatus" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='item-status-label'/></span></label>  
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchItemStatusName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[8].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchItemStatusOp" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[8].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchItemStatusValue" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[8].value" 
            beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[8].value" type="hidden"/>
          	<select id="atg_commerce_order_searchItemStatus" name="statusField">
                 <dsp:droplet name="ForEach">
		              <dsp:param bean="CSRAgentTools.commerceItemSearchableStates" name="array"/>
		              <dsp:param name="elementName" value="itemState"/>
		              <dsp:oparam name="outputStart">
		                <option selected="true" value=""><fmt:message key='all-statuses-value'/></option>
		              </dsp:oparam>
		              <dsp:oparam name="output">
		              	<dsp:getvalueof var="itemStateString" param="itemState" idtype="java.lang.String"/>
		             	<option value="${itemStateString}">${itemStateString}</option>
		             </dsp:oparam>
	             </dsp:droplet>
          	</select>
        </div>
        <label for="oStatus" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='shipping-method-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchShipMethodStatusName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[9].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchShipMethodStatusOp" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[9].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchShipMethodStatusValue" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[9].value" 
            beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[9].value" type="hidden"/>
          	<select id="atg_commerce_order_searchShipMethodStatus" name="statusField">
                 <dsp:droplet name="ForEach">
		              <dsp:param bean="CSRAgentTools.shippingMethodSearchableStates" name="array"/>
		              <dsp:param name="elementName" value="shipMethodState"/>
		              <dsp:oparam name="outputStart">
		                <option selected="true" value=""><fmt:message key='all-statuses-value'/></option>
		              </dsp:oparam>
		              <dsp:oparam name="output">
		              	<dsp:getvalueof var="shipMethodStateString" param="shipMethodState" idtype="java.lang.String"/>
		             	<option value="${shipMethodStateString}">${shipMethodStateString}</option>
		             </dsp:oparam>
	             </dsp:droplet>
          	</select>
        </div>
      </div>
         
    </div>
    <ul  class="atg_commerce_csr_dateRange">

      <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
      <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
      <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>
       
      <p class="atg-base-table-order-search-p-margin-bottom"><fmt:message key='search-date-start-label'/></p>
      <div class="atg-csc-base-table">
        <li class="startDate atg-csc-base-table-row"><label for="startDate" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='start-date-label'/></span></label>
         
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStartDateName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[10].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStartDateOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[10].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchStartDateValue" type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[10].value"
                     beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[10].value"/>
          <div class="atg-csc-base-table-cell">
            <input type="text" id="atg_commerce_order_searchStartDate" maxlength="10" size="10" name="startDateField" dojoType="dijit.form.DateTextBox" constraints="{datePattern:'${userPreferredLocale.datePattern}'}"/>
            <img id="startDateImg"
                src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                width="16"
                height="16"
                border="0"
                title="<fmt:message key='start-date-alt'/>"
                onclick="dojo.byId('atg_commerce_order_searchStartDate').focus()"/>
          </div >
        </li>

        <li class="endDate atg-csc-base-table-row"><label for="endDate" class="atg-csc-base-table-cell"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='end-date-label'/></span></label>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEndDateName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[11].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEndDateOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[11].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchEndDateValue" type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[11].value"
                    beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[11].value"/>
          <div class="atg-csc-base-table-cell">
            <input type="text" id="atg_commerce_order_searchEndDate" maxlength="10" size="10" name="endDateField" dojoType="dijit.form.DateTextBox" constraints="{datePattern:'${userPreferredLocale.datePattern}'}"/>
            <img id="endDateImg"
                 src="<c:url context='/CAF' value='/images/calendar/calendar.gif'/>"
                 width="16"
                 height="16"
                 border="0"
                 title="<fmt:message key='end-date-alt'/>"
                 onclick="dojo.byId('atg_commerce_order_searchEndDate').focus()"/>
          </div>
        </li>
      </div>
      <p class="atg-base-table-order-search-p-margin-top"><fmt:message key='search-date-end-label'/></p>
    </ul>
  </div>
  

  <div id="atg_commerce_csr_advSearch_pane" class="atg_commerce_csr_togglePanel"
         dojoType="dijit.TitlePane" open="false"
       title="<fmt:message key='advanced-search-button-label'/>">
       
  <div id="atg_commerce_csr_advSearchDiv" class="atg_commerce_csr_advancedSearch ">

    <ul class="atg-csc-base-table atg-base-table-order-search-advanced-form">
      <li class="atg_commerce_csr_searchIn atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><fmt:message key='search-in-label'/></span>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding">
          <dsp:input bean="OrderSearchTreeQueryFormHandler.addressPicker" id="addressPickerBS" type="radio" value="BS" checked="true"/>
          <fmt:message key='billing-and-shipping-label'/>
            <dsp:input bean="OrderSearchTreeQueryFormHandler.addressPicker" id="addressPickerB" type="radio" value="B"/>
          <fmt:message key='billing-only-label'/>
            <dsp:input bean="OrderSearchTreeQueryFormHandler.addressPicker" id="addressPickerS" type="radio" value="S"/>
          <fmt:message key='shipping-only-label'/>
        </div>
      </li>
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='first-name-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedFirstNameName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[12].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedFirstNameOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[12].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedFirstNameValue" iclass="atg-base-table-product-catalog-search-input" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[12].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[12].value"     size="35" maxlength="40"/>
        </div>
      </li>
      
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='last-name-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedLastNameName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[13].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedLastNameOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[13].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedLastNameValue" iclass="atg-base-table-product-catalog-search-input" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[13].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[13].value"     size="35" maxlength="40"/>
        </div>
      </li>
        
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key="country-label" /></span></label>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding">
          <div dojoType="dojo.data.ItemFileReadStore" jsId="countryStore"
            url="${customerPanelConfig.countryDataUrl}?${stateHolder.windowIdParameterName}=${windowId}"></div>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedCountryName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[14].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedCountryOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[14].op" type="hidden"/>                 
          <dsp:input converter="nullable" type="hidden" id="atg_commerce_order_searchAdvancedCountryValue" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[14].value"
            beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[14].value"/>
            
          <input id="atg_commerce_order_searchAdvancedCountry" class="atg-base-table-order-search-advanced-form-input-size-one" dojoType="atg.widget.form.FilteringSelect" 
          autoComplete="true" searchAttr="name" store="countryStore" name="countryField"/>
        </div>
      </li>
          
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='address.address1'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedAddressLine1Name" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[15].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedAddressLine1Op"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[15].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedAddressLine1Value" iclass="atg-base-table-product-catalog-search-input atg-base-table-order-search-advanced-form-input-size" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[15].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[15].value"     size="35" maxlength="50"/>
        </div>
      </li>
      
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='address.address2'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedAddressLine2Name" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[16].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedAddressLine2Op"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[16].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedAddressLine2Value" iclass="atg-base-table-product-catalog-search-input atg-base-table-order-search-advanced-form-input-size" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[16].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[16].value"     size="35" maxlength="50"/>
        </div>
      </li>
      
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='town-city-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedCityName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[17].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedCityOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[17].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedCityValue" iclass="atg-base-table-product-catalog-search-input" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[17].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[17].value"     size="35" maxlength="40"/>
        </div>
      </li>
      
      <li  class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='state-label'/></span></label>
        <div class="atg-csc-base-table-cell atg-base-table-product-catalog-search-input-padding">
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedStateName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[18].name" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedStateOp" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[18].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedStateValue" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[18].value" type="hidden"
          beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[18].value"/>
           
          <div dojoType="dojo.data.ItemFileReadStore" jsId="stateStore"
                                 url="${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&countryCode=${countryCode}&isOrderSearch=true"></div>
                               
          <input id="atg_commerce_order_searchAdvancedState" dojoType="atg.widget.form.FilteringSelect" autoComplete="true"
             searchAttr="name" store="stateStore" name="stateField"/>
        </div>
      </li>
       
        
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='postal-code-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedPostalCodeName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[19].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedPostalCodeOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[19].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedPostalCodeValue" iclass="atg-base-table-product-catalog-search-input atg-base-table-order-search-advanced-form-input-size-two" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[19].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[19].value"     size="35" maxlength="40"/>
        </div>
      </li>
      
      <li class=" atg-csc-base-table-row"><label class="atg-csc-base-table-cell atg-base-table-product-catalog-search-label"><span class="atg_commerce_csr_fieldTitle"><fmt:message key='phone-label'/></span></label>
        <div class="atg-csc-base-table-cell">
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedPhoneName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[20].name" type="hidden"/>
          <dsp:input converter="nullable"  id="atg_commerce_order_searchAdvancedPhoneOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[20].op" type="hidden"/>
          <dsp:input  converter="nullable" id="atg_commerce_order_searchAdvancedPhoneValue" iclass="atg-base-table-product-catalog-search-input" type="text" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[20].value"
                   beanvalue="OrderSearchTreeQueryFormHandler.previousSearchRequest.fields[20].value"     size="35" maxlength="40"/>
        </div>
      </li>
        
    </ul>
      
  </div>
      
    <%-- REALMS --%>
    <dsp:getvalueof bean="ProfileRealmManager.profileRealmsEnabled" var="profileRealmsEnabled" />
    <c:if test="${profileRealmsEnabled}">
      <dsp:getvalueof bean="ProfileRealmManager.currentlyDefaultRealm" var="currentlyDefaultRealm" />
      <c:set var="realmId" value="__null__" />
      <c:if test="${not currentlyDefaultRealm}">
        <dsp:getvalueof bean="ProfileRealmManager.currentRealm.repositoryId" var="realmId" />
      </c:if>
      <dsp:input  converter="nullable" id="atg_commerce_order_searchRealmName" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[21].name" type="hidden" value="site.realmId"/>
      <dsp:input  converter="nullable" id="atg_commerce_order_searchRealmOp"   bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[21].op" type="hidden" value="equal"/>
      <dsp:input  converter="nullable" id="atg_commerce_order_searchRealmValue" type="hidden" bean="OrderSearchTreeQueryFormHandler.searchRequest.fields[21].value" value="${realmId}"/>
    </c:if>
    <%-- END REALMS --%>
    
</div>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/order/orderSearchUIFragment.jsp#1 $$Change: 946917 $--%>
