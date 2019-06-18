<%--
 In-Store Pickup Locations
 This page displays the proximity search inputs
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/shippingAddressPickupLocations.jsp#1 $$Change: 946917 $
 @updated $DateTime: 2015/01/26 17:26:27 $$Author: jsiddaga $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:importbean var="customerPanelConfig" bean="/atg/svc/agent/customer/CustomerPanelConfig" />
<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler" />
<dsp:importbean var="inStorePickupDistanceList" bean="/atg/commerce/order/purchase/InStorePickupDistanceList" />
<dsp:importbean var="storeLocatorFormHandler" bean="/atg/commerce/locations/StoreLocatorFormHandler" />
<dsp:importbean var="distanceUnits" bean="/atg/commerce/units/DistanceUnits" />
<c:set var="distanceUnitsVar" value="${distanceUnits.baseUnit}" />
<dsp:layeredBundle basename="atg.commerce.units.UnitOfMeasureResources">
  <fmt:message key="${distanceUnitsVar}" var="distanceUnitsVarLabel"/>
</dsp:layeredBundle>

<div style="width:100%;margin-top:10px">
    <svc-ui:frameworkPopupUrl var="successURL"
      value="/include/catalog/shippingLocationsSearchResults.jsp?productId=${productId}&skuId=${skuId}&quantity=${quantity}"
      context="${CSRConfigurator.contextRoot}"
      success="true"
      windowId="${windowId}"/>


  <dsp:form method="post" id="shippingAddress_inStorePickupSearchForm" name="shippingAddress_inStorePickupSearchForm">   
    <c:choose>
      <c:when test="${!empty storeLocatorFormHandler.geoLocatorService.provider}">      
        <c:set var="tableStyle" value="" />
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${storeLocatorFormHandler.distance > 0}">
            <input type="hidden" value="${storeLocatorFormHandler.distance}" name="geoLocatorServiceProviderEmpty" />
          </c:when>
          <c:otherwise>
            <input type="hidden" value="-1" name="geoLocatorServiceProviderEmpty" />
          </c:otherwise>
        </c:choose>
        <c:set var="tableStyle" value="style='display:none'" />
      </c:otherwise>
    </c:choose>
  
    <dsp:input type="hidden" value="" name="countryCode" bean="StoreLocatorFormHandler.countryCode" />
    <dsp:input type="hidden" value="" name="state" bean="StoreLocatorFormHandler.state" />
    <dsp:input type="hidden" value="" name="city" bean="StoreLocatorFormHandler.city"/>
    <dsp:input type="hidden" value="" name="postalCode" bean="StoreLocatorFormHandler.postalCode"/>    
    <dsp:input type="hidden" value="" name="distance" bean="StoreLocatorFormHandler.distance" />
    <dsp:input type="hidden" value="${successURL}" name="successURL" bean="StoreLocatorFormHandler.successURL" />
    <dsp:input priority="-10" type="hidden" value="Locate Stores" bean="StoreLocatorFormHandler.locateItems" />
  </dsp:form>
  <table border="0" cellpadding="0" cellspacing="3" id="inStorePickupSearchContainer" ${tableStyle}>
    <tr>
      <td colspan="4">
        <div dojoType="dojo.data.ItemFileReadStore" jsId="countryStore" url="${customerPanelConfig.countryDataUrl}?${stateHolder.windowIdParameterName}=${windowId}"></div>
        <c:choose>
          <c:when test="${true}">
            <input id="inStorePickupCountry" dojoType="atg.widget.form.FilteringSelect" validate="return true;" autoComplete="true" searchAttr="name" store="countryStore" name="countryField" style="width:100%" onChange="atg.commerce.csr.catalog.pickupInStoreCountryChange('${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&isOrderSearch=true&countryCode=');" />
          </c:when>
          <c:otherwise>
            <input id="inStorePickupCountry" dojoType="atg.widget.form.FilteringSelect" validate="return true;" autoComplete="true" searchAttr="name" store="countryStore" name="countryField" style="width:100%" />
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
        <fmt:message key="catalogBrowse.inStorePickup.search.postalCode" var="postalCodeLabel"/>
        <input type="text" id="inStorePickupPostalCode" value="${postalCodeLabel}" class="gray" onclick="if (this.value == '${postalCodeLabel}') {this.value='';this.className='';}"></input>
      </td>
      <c:if test="${true}">
        <td><fmt:message key="catalogBrowse.inStorePickup.search.or"/></td>
        <td>
          <div dojoType="dojo.data.ItemFileReadStore" jsId="stateStore" url="${customerPanelConfig.stateDataUrl}?${stateHolder.windowIdParameterName}=${windowId}&countryCode=${countryCode}&isOrderSearch=true"></div>
          <input id="inStorePickupState" dojoType="atg.widget.form.FilteringSelect" validate="return true;" autoComplete="true" searchAttr="name" store="stateStore" name="stateField" />
        </td>
        <td>
          <fmt:message key="catalogBrowse.inStorePickup.search.city" var="cityLabel"/>
          <input type="text" id="inStorePickupPostalCity" value="${cityLabel}" class="gray" onclick="if (this.value == '${cityLabel}') {this.value='';this.className='';}"></input>
        </td>
      </c:if>
    </tr>
    <tr>
      <td>
        <select id="inStorePickupProximity">
          <c:forEach var="distance" items="${inStorePickupDistanceList.distances}">
            <option value="${distance}">< ${distance}${distanceUnitsVarLabel}</option>
          </c:forEach>
        </select>
      </td>
      <td colspan="3" style="text-align:right">
        <input id="inStorePickupSearch" type="button" value="<fmt:message key='catalogBrowse.inStorePickup.search.search' />" onclick="atg.commerce.csr.catalog.pickupInStoreSearchStores()" style="text-align:right" />
      </td>
    </tr>
  </table>
  <c:choose>
    <c:when test="${!empty storeLocatorFormHandler.geoLocatorService.provider}">
    </c:when>
    <c:otherwise>
      <script defer="defer">
          atg.commerce.csr.catalog.pickupInStoreSearchStores();
      </script>
    </c:otherwise>
  </c:choose>
</div>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/shippingAddressPickupLocations.jsp#1 $$Change: 946917 $--%>