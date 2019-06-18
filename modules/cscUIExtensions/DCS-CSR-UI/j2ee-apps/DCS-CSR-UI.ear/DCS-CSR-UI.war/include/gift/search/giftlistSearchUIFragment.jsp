<%--

This page fragment defines the gift list search input fields

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/search/giftlistSearchUIFragment.jsp#1 $$Change: 946917 $
@updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $

--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/gifts/GiftlistSearch" var="giftlistSearch"/>
  <dsp:importbean bean="/atg/core/i18n/LocaleTools"/>
  
  <dsp:getvalueof var="propertyValuesByTypeList" vartype="java.lang.Object" bean="GiftlistSearch.propertyValuesByType" />
  <dsp:getvalueof var="formId" param="formId" />
  <dsp:setvalue bean="LocaleTools.dateStyle" value="short"/>
  <dsp:getvalueof var="userPreferredLocale" bean="LocaleTools.userFormattingLocaleHelper"/>

  <dsp:layeredBundle basename="atg.commerce.csr.Messages">
    <div id="${formId}_atg_commerce_csr_searchGiftListForm">
      <p class="atg-csc-base-spacing-two-left"><fmt:message key="giftlists.search.search-instruction" /></p>
      <table cellspacing="0" cellpadding="0" border="0" class="atg-commerce-csr-giftlist-search">
        <tr>
          <td><label><fmt:message key="giftlists.search.lastName" /></label></td>
          <td><dsp:input bean="GiftlistSearch.propertyValues.lastName" type="text" iclass="atg-csc-base-table-cell atg-base-table-product-catalog-search-input" id="${formId}_atg_csr_lastNameInput" onkeyup="giftlistSearchFormValidate()"/></td>
          <td class="atg-commerce-csr-giftlist-separator"><label><fmt:message key="giftlists.search.firstName" /></label></td>
          <td><dsp:input bean="GiftlistSearch.propertyValues.firstName" type="text" iclass="atg-csc-base-table-cell atg-base-table-product-catalog-search-input" id="${formId}_atg_csr_firstNameInput" onkeyup="giftlistSearchFormValidate()" /></td>
        </tr>
        <tr>
            <c:forEach var="propertyValueByType" items="${propertyValuesByTypeList}">
              <c:if test="${propertyValueByType.key == 'eventType' }">
                <td><label><fmt:message key="giftlists.search.eventType" /></label></td>
                <td>
                  <dsp:select bean="GiftlistSearch.propertyValues.eventType" id="${formId}_atg_csr_eventTypeSelect" iclass="atg-csc-base-table-cell atg-base-table-product-catalog-search-input" onchange="giftlistSearchFormValidate()" >
                    <dsp:option value="">
                      <fmt:message key="giftlists.search.option.any" />
                    </dsp:option>
                    <dsp:droplet name="/atg/dynamo/droplet/PossibleValues">
                      <dsp:param name="repository" value="${giftlistSearch.giftlistRepository}" />
                      <dsp:param name="itemDescriptorName" value="gift-list" />
                      <dsp:param name="propertyName" value="eventType" />
                      <dsp:param name="returnValueObjects" value="true" />
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="values" param="values" />
                          <c:forEach items="${values}" var="option">
                            <dsp:option value="${option.settableValue}">${fn:escapeXml(option.localizedLabel)}</dsp:option>    
                          </c:forEach>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:select>
                </td>
              </c:if>
            </c:forEach>
          <td class="atg-commerce-csr-giftlist-separator"><label><fmt:message key="giftlists.search.eventDate" /></label></td>
          <td>
            <input type="text" 
              id="${formId}_atg_csr_eventDate" 
              size="25" 
              maxlength="10" 
              class="atg-commerce-csr-giftlist-search-event-date"
              value="${userPreferredLocale.datePattern}" 
              dojoType="dijit.form.DateTextBox"
              constraints="{datePattern:'${userPreferredLocale.datePattern}'}"
              onkeyup="giftlistSearchFormValidate()"
              onchange="giftlistSearchFormValidate()"/>
    
            <dsp:input type="hidden" bean="GiftlistSearch.propertyValues.eventDate" id="${formId}_atg_csr_eventDate_hidden" size="25" maxlength="10" converter="date" date="${userPreferredLocale.datePattern}" nullable="true"/>
            <fmt:message key="giftlists.create.eventDate.tooltip" var="eventDateTooltip" /> 
            <dsp:img 
              src="/CAF/images/calendar/calendar.gif" 
              id="${formId}_eventDateIcon" 
              title="${eventDateTooltip}" 
              style="cursor:pointer;" 
              onclick="dojo.byId('${formId}_atg_csr_eventDate').focus()"/> 
          </td>
        </tr>
      </table>
    </div>

    <script type="text/javascript">
      dojo.require("dijit.form.DateTextBox");
      //function to validate the search form
      function giftlistSearchFormValidate() {
        var disable = false;
        if (WithoutContent(dojo.byId("${formId}_atg_csr_eventDate").value)) {
          dojo.byId("${formId}_atg_csr_eventDate_hidden").value = "";
        }

        if (WithoutContent(dojo.byId("${formId}_atg_csr_lastNameInput").value) && WithoutContent(dojo.byId("${formId}_atg_csr_firstNameInput").value) 
            && (WithoutContent(dojo.byId("${formId}_atg_csr_eventDate").value) || (dojo.byId("${formId}_atg_csr_eventDate").value == '${userPreferredLocale.datePattern}'))
            && WithoutContent(dojo.byId("${formId}_atg_csr_eventTypeSelect").value)  ) {
          disable = true;  
        }
        
        if (!WithoutContent(dojo.byId("${formId}_atg_csr_eventDate").value) && (dojo.byId("${formId}_atg_csr_eventDate").value != '${userPreferredLocale.datePattern}')) {
          if (!atg.commerce.csr.order.gift.validateGiftlistEventDate(dojo.byId("${formId}_atg_csr_eventDate").value,"${userPreferredLocale.datePattern}")) {
              disable = true;
          } else {
              dojo.byId("${formId}_atg_csr_eventDate_hidden").value = dojo.byId("${formId}_atg_csr_eventDate").value;
          }
        }
              
        dojo.byId("${formId}_giftlistSearchButton").disabled = disable;
      }

      //function to check if a form field is empty
      function WithoutContent(formFieldValue) {
        if(formFieldValue.length > 0) { return false; }
        return true;
      }
      
      _container_.onLoadDeferred.addCallback(function () {
        giftlistSearchFormValidate();
      });
      _container_.onUnloadDeferred.addCallback(function () {
      });
      </script> 
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/search/giftlistSearchUIFragment.jsp#1 $$Change: 946917 $--%>