<%--
This page fragment defines the default form for the create gift list panel. 

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistCreateUIFragment.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/dynamo/droplet/Compare" />
  <dsp:importbean var="addressBook" bean="/atg/svc/agent/profile/AddressBook"/>
  <dsp:importbean bean="/atg/userprofiling/ServiceCustomerProfile" var="profile" />
  <dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet" />
  <dsp:importbean bean="/atg/multisite/Site" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean var="addressForm" bean="/atg/svc/agent/ui/fragments/AddressForm"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  <dsp:importbean var="glfh" bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"/>
  
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
  <dsp:getvalueof var="formId" param="formId" />
  <dsp:getvalueof var="giftlistMap" param="giftlistMap" />
  <dsp:getvalueof var="giftlistFormHandler" param="giftlistFormHandler" />
  <dsp:getvalueof var="submitButtonId" param="submitButtonId" />
  <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
  <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>

  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <fmt:message var="eventDateErrorMessage" key="giftlists.create.validation.error.eventDate"><fmt:param value="${userPreferredLocale.datePattern}"/></fmt:message>
    <div class="atg_svc_content">
    <div class="atg-csc-base-table">
      <%-- Display Gift list owner name --%>
      <div class="atg_commerce_csr_eventOwner atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label"><label><fmt:message key="giftlists.create.owner" /></label></span> 
        <div class="atg-csc-base-table-cell">
          <dsp:droplet name="/atg/targeting/RepositoryLookup">
            <dsp:param name="url" value="${profile.repositoryId}" />
            <dsp:oparam name="output">
              <dsp:tomap var="currentCustomer" param="element" />
            </dsp:oparam>
          </dsp:droplet> <dsp:droplet name="ProfileRepositoryItemServlet">
            <dsp:param name="id" value="${profile.repositoryId}" />
            <dsp:oparam name="output">
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="item" />
                <dsp:oparam name="true">
                </dsp:oparam>
                <dsp:oparam name="false">
                  <dsp:valueof param="item.firstname" />
                  <dsp:valueof param="item.middlename" />
                  <dsp:valueof param="item.lastname" />
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </div>
      </div>
      
      <%-- Input: Event Name --%>
      <div class="atg_commerce_csr_eventName atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label"><label class="atg_messaging_requiredIndicator"><fmt:message key="giftlists.create.eventName" /> </label> <span class="requiredStar">*</span> </span> 
        <div class="atg-csc-base-table-cell">
          <dsp:input
            bean="${giftlistFormHandler}.eventName" value="${giftlistMap.eventName}" size="27" maxlength="64" type="text" style="position:relative !important;" id="${formId}_eventName" required="<%=true%>" iclass="required">
            <dsp:tagAttribute name="dojoType" value="atg.widget.form.ValidationTextBox" />
            <dsp:tagAttribute name="required" value="true" />
            <dsp:tagAttribute name="trim" value="true" />
            <dsp:tagAttribute name="autocomplete" value="off" />
          </dsp:input>
        </div>
      </div>
      
      <%-- Input: Event Type --%>
      <div class="atg_commerce_csr_eventType atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label"><label class="atg_messaging_requiredIndicator"> <fmt:message key="giftlists.create.eventType" /> </label> <span class="requiredStar">*</span></span> 
        <div class="atg-csc-base-table-cell">
          <dsp:select bean="${giftlistFormHandler}.eventType" id="${formId}_eventType" required="true" iclass="custom_select" onchange="${formId}DisableSubmit()">
            <dsp:option value="-1">
              <fmt:message key="giftlists.create.option.select-a-type" />
            </dsp:option>
            
            <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
              <dsp:param name="repository" value="${glfh.giftlistRepository}" />
              <dsp:param name="itemDescriptorName" value="gift-list" />
              <dsp:param name="propertyName" value="eventType" />
              <dsp:param name="returnValueObjects" value="true" />
              <dsp:oparam name="output">
                <dsp:getvalueof var="values" param="values" />
                <c:forEach items="${values}" var="option">
                  <c:choose> 
                    <c:when test="${option.settableValue == giftlistMap.eventType}" > 
                      <dsp:option value="${option.settableValue}" selected="true">${fn:escapeXml(option.localizedLabel)}</dsp:option>
                    </c:when>
                    <c:otherwise>
                      <dsp:option value="${option.settableValue}">${fn:escapeXml(option.localizedLabel)}</dsp:option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:select>
        </div>
      </div>
      
      <%-- Input: Event Date --%>
      <div class="atg_commerce_csr_eventDate atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label">
          <label class="atg_messaging_requiredIndicator">
            <fmt:message key="giftlists.create.eventDate" />
          </label>
          <span class="requiredStar">*</span> 
        </span>
        <div class="atg-csc-base-table-cell">
          <c:if test="${not empty giftlistMap.eventDate}">
            <web-ui:formatDate type="date" value="${giftlistMap.eventDate}" dateStyle="short" timeStyle="short" var="eventDate" />
          </c:if> 
          <dsp:input 
              type="hidden" 
              bean="${giftlistFormHandler}.eventDate" 
              id="${formId}_eventDateHidden" 
              size="25" 
              maxlength="10" 
              value="${eventDate}" 
              onchange="${formId}DisableSubmit()"/> 
          <input 
              type="text" 
              id="${formId}_eventDate" 
              size="25" 
              maxlength="10" 
              style="position:relative !important;"
              value="${eventDate}" 
              dojoType="dijit.form.DateTextBox"
              constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
              onchange="dojo.byId('${formId}_eventDateHidden').value = dojo.byId('${formId}_eventDate').value;"/>

          <dsp:input type="hidden" bean="${giftlistFormHandler}.eventDate" id="${formId}_eventDate_hidden" size="25" maxlength="10" converter="date" date="${userPreferredLocale.datePattern}"/> 
          <fmt:message key="giftlists.create.eventDate.tooltip" var="eventDateTooltip" /> 
          <dsp:img 
              src="/CAF/images/calendar/calendar.gif" 
              id="eventDateIcon" 
              align="absmiddle" 
              title="${eventDateTooltip}" 
              style="cursor:pointer; cursor:hand"
              onclick="dojo.byId('${formId}_eventDate').focus()"
              /> 
          <script type="text/javascript">
            function setDate(calendar) {
                var date = calendar.date;
                var hiddenEventDate = document.getElementById("${formId}_eventDate_hidden");
                var eventDate = document.getElementById("${formId}_eventDate");
                hiddenEventDate.value = date.print("${userPreferredLocale.datePattern}");
                eventDate.value = hiddenEventDate.value;

                if (atg.commerce.csr.order.gift.validateGiftlistEventDate(eventDate.value,"${userPreferredLocale.datePattern}")) {
                  hiddenEventDate.value = eventDate.value;
                  ${formId}Validate();
                }
              }
          </script>
        </div>
      </div>
        
      <%-- Input: Site --%>
      <c:if test="${isMultiSiteEnabled == true}">
        <div class="atg_commerce_csr_eventSite atg-csc-base-table-row">
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label"> <label><fmt:message key="giftlists.create.site" /></label></span> 
          <div class="atg-csc-base-table-cell">
            <c:choose>
              <c:when test="${empty giftlistMap}">
                <dsp:droplet name="SharingSitesDroplet">
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="sites" param="sites" />
                    <dsp:select bean="${giftlistFormHandler}.siteId" id="${formId}siteId" required="true" iclass="custom_select">
                      <c:forEach var="site" items="${sites}" varStatus="siteStatus">
                        <dsp:tomap var="siteMap" value="${site}" />
                        <c:if test="${siteMap.id == currentSiteId}">
                          <c:set var="siteSelected" value="true"/>
                        </c:if>
                        <c:if test="${siteMap.id != currentSiteId}">
                          <c:set var="siteSelected" value="false"/>
                        </c:if>
                        <dsp:option value="${siteMap.id}" selected="${siteSelected }">
                          ${siteMap.name}
                        </dsp:option>
                      </c:forEach>
                    </dsp:select>
                  </dsp:oparam>
                </dsp:droplet>
              </c:when>
              <c:otherwise>
                <dsp:getvalueof var="siteId" param="giftlistMap.siteId" />
                <dsp:droplet name="/atg/dynamo/droplet/multisite/GetSiteDroplet">
                  <dsp:param name="siteId" value="${siteId}" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof param="site" var="site" />
                    <dsp:getvalueof var="siteName" param="site.name" />
                      <span class="plainText atg-csc-base-table-cell"><c:out value="${siteName}"/></span>
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:input type="hidden" bean="${giftlistFormHandler}.siteId" id="${formId}_siteId" value="${siteId}" /> 
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </c:if>
      
      <%-- Input: Event Status (Private/Pulic) --%>
      <div class="atg_commerce_csr_eventStatus atg-csc-base-table-row">
        <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label"> <fmt:message key="giftlists.create.status" /> </span>
        
        <label class="atg-csc-base-table-cell"> 
          <dsp:input bean="${giftlistFormHandler}.isPublished" iclass="atg-base-table-customer-gift-list-radio-button" type="radio" value="false" name="status" id="${formId}_private" /> 
          <span class="atg-base-table-customer-gift-list-radio-button"><fmt:message key="giftlists.create.status.private" /></span>
        </label>
      </div>
      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell"></div>
        <label class="atg-csc-base-table-cell"> <dsp:input bean="${giftlistFormHandler}.isPublished" type="radio" value="true"  name="status" id="${formId}_public" /> <fmt:message key="giftlists.create.status.public" /> </label>
      </div>
       
      

      <%-- Input: Address --%>
      <c:set target="${addressBook}" property="profile" value="${profile}" />
      <div class="atg_commerce_csr_giftListAddressForm atg-csc-base-table-row">
        <c:choose>
          <c:when test="${addressBook.containsNonBlankAddress}">
            <c:set var="hasNonBlankAddresses" value="true" />
            <%-- customer has valid addresses --%>
          </c:when>
          <c:otherwise>
            <c:set var="hasNonBlankAddresses" value="false" />
            <%-- customer has no valid addresses --%>
            <dsp:input type="hidden" id="${formId}_shippingAddressId_hidden" bean="${giftlistFormHandler}.shippingAddressId" value="null" />
            <script type="text/javascript">
              
            </script>
          </c:otherwise>
        </c:choose> 
      
        
        
          <span class="atg_commerce_csr_fieldTitle atg-csc-base-table-cell atg-base-table-customer-create-first-label atg-base-table-customer-gift-list-spacing-three-top"> <label class="atg_messaging_requiredIndicator atg_commerce_csr_billingAddress"> <fmt:message key="giftlists.create.shippingAddress" /> </label> </span>
          <div class="atg-csc-base-table-cell">
            <dsp:input id="${formId}_addressRadioSelection_none" type="radio" iclass="atg-base-table-customer-gift-list-radio-button" bean="${giftlistFormHandler}.isNewAddress" checked="true" value="false" onclick="dojo.style(dojo.byId('${formId}_addressArea'),'display','none');atg.commerce.csr.order.gift.disableExistingAddress('${formId }','${hasNonBlankAddresses}')" />
            <span class="atg-base-table-customer-gift-list-radio-button">
              <fmt:message key="giftlists.create.no-address" />
            </span>
          </div>

      </div>
      <div class="atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label"></div>
        <div class="atg-csc-base-table-cell">
          <dsp:input id="${formId}_addressRadioSelection_existing" type="radio" bean="${giftlistFormHandler}.isNewAddress" iclass="atg-base-table-customer-gift-list-radio-button" value="false" checked="false" onclick="dojo.style(dojo.byId('${formId}_addressArea'),'display','none');atg.commerce.csr.order.gift.disableExistingAddress('${formId }','${hasNonBlankAddresses}');"/> 
          <span id="${formId}_addressRadioSelection_existing_text">
            <fmt:message key="giftlists.create.use-existing-address" />
          </span>

          <dsp:select id="${formId}_existingAddressList" bean="${giftlistFormHandler}.shippingAddressId" iclass="atg-csc-base-spacing-left">
            <dsp:option value="null">
              <fmt:message key="giftlists.create.selectAddress" />
            </dsp:option>
            <c:set target="${addressBook}" property="profile" value="${profile}"/>
            <%-- Display default addresses first --%>
            <div class="atg_svc_addresses">
              <c:forEach var="ami" items="${addressBook.addressMetaInfos}">
                <c:if test="${not ami.value.blank}">
                  <c:if test="${not empty ami.value.params.defaultOptions}">
                      <dsp:option value="${ami.value.addressRepositoryItem.repositoryId}">
                        <%--Display the first line of the address --%>
                        ${fn:escapeXml(ami.value.address.address1)}${!empty ami.value.address.address2 ? ' ' : '' }${!empty ami.value.address.address2 ? fn:escapeXml(ami.value.address.address2) : '' }
                      </dsp:option>
                  </c:if>
                </c:if>
              </c:forEach>
            <%-- Display any non-default addresses --%>
              <c:forEach var="ami" items="${addressBook.addressMetaInfos}">
                <c:if test="${not ami.value.blank}">
                  <c:if test="${empty ami.value.params.defaultOptions}">
                      <dsp:option value="${ami.value.addressRepositoryItem.repositoryId}">
                        <%--Display the first line of the address --%>
                        ${fn:escapeXml(ami.value.address.address1)}${!empty ami.value.address.address2 ? ' ' : '' }${!empty ami.value.address.address2 ? fn:escapeXml(ami.value.address.address2) : '' }
                      </dsp:option>
                  </c:if>
                </c:if>
              </c:forEach>
            </div>
          </dsp:select>
        </div>
      </div>
      <div class="atg_commerce_csr_createNewAddress atg-csc-base-table-row">
        <div class="atg-csc-base-table-cell atg-base-table-customer-create-first-label"></div>
        <div class="atg-csc-base-table-cell">
          <dsp:input type="radio" bean="${giftlistFormHandler}.isNewAddress" value="true" iclass="atg-base-table-customer-gift-list-radio-button" id="${formId}_addressRadioSelection_new"  onclick="existingGiftlistAddressPicker();atg.commerce.csr.order.gift.disableExistingAddress('${formId}','${hasNonBlankAddresses}');" />
          <fmt:message key="giftlists.create.create-new-address" />
        </div>
        <c:set var="giftlistAddressBean" value="${giftlistFormHandler}.newAddress" />
      </div>

    </div>
    <div id="${formId}_addressArea" style="display: none;">
      <div class="atg-csc-base-table atg-base-table-customer-address-add-form">
        <dsp:include src="${addressForm.URL}" otherContext="${addressForm.servletContext}">
          <dsp:param name="formId" value="${formId}" />
          <dsp:param name="addressBean" value="${giftlistAddressBean}" />
          <dsp:param name="submitButtonId" value="${submitButtonId}" />
          <dsp:param name="isDisableSubmit" value="${formId}DisableSubmit" />
          <dsp:param name="validateIf" value="dojo.byId('${formId}_addressRadioSelection_new').checked==true" />
        </dsp:include>
      </div>
    </div>
    </div>
      <script type="text/javascript">
        dojo.require("dijit.form.DateTextBox");
        /**
         * existingGiftlistAddressPicker
         *
         * Shows or Hides the address input area
         */
        existingGiftlistAddressPicker = function(){
            dojo.style(dojo.byId('${formId}_addressArea'),'display','block');
            dojo.byId("${formId}_existingAddressList").value = "null";
        };
          
        /**
         * ${formId}DisableSubmit
         *
         * disabled the submit button if form fields are not satisfied
         * @returns true if the submit button should be dissabled, false if it should not.
         */
        var ${formId}DisableSubmit = function () {
          var disable = false;
          if (!dijit.byId("${formId}_eventName").isValid()) disable = true;
          if (!atg.commerce.csr.order.gift.validateEventType(dojo.byId("${formId}_eventType").value)) disable = true;
          if (!atg.commerce.csr.order.gift.validateGiftlistEventDate(dojo.byId("${formId}_eventDate").value,"${userPreferredLocale.datePattern}")) disable = true;
          return disable;
        };
        
        /**
         * setFormFields
         *
         */
        function setFormFields() {
          if('${giftlistMap.isPublished}'!=null)
          {
            if('${giftlistMap.published}'=='true')
            {
              dojo.byId('${formId}_public').checked=true;
            }
            if('${giftlistMap.published}'=='false')
            {
              dojo.byId('${formId}_private').checked=true;
            }
          }
          <dsp:tomap var="giftlistshippingAddressMap" param="giftlistMap.shippingAddress"/>
          if('${giftlistshippingAddressMap.id}'!='')
          {
            dojo.byId('${formId}_addressRadioSelection_existing').checked=true;
            dojo.byId('${formId}_existingAddressList').value='${giftlistshippingAddressMap.id}';
          }
          if('${giftlistshippingAddressMap.id}'=='')
          {
            dojo.byId('${formId}_addressRadioSelection_none').checked=true;
          }
        }       
        
        _container_.onLoadDeferred.addCallback(function () {
          setFormFields();
          atg.commerce.csr.order.gift.disableExistingAddress('${formId}','${addressBook.containsNonBlankAddress}');
          ${formId}DisableSubmit;
          dojo.byId("${formId}_eventDate").value = "${eventDate}";
        });
      </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlist/giftlistCreateUIFragment.jsp#1 $$Change: 946917 $--%>
