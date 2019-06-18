<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/mff/MFFEnvironment"/>
	<dsp:getvalueof bean="MFFEnvironment.storePickUpRadius" var="distance"/>
	<div class="field-group">
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.distance" id="bopis-distance-modal" name="bopis-distance-modal" value="${distance}"/>
		<dsp:input type="tel" bean="StoreLocatorFormHandler.postalCode" id="bopis-zip-modal" name="bopis-zip-modal" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="Zip Code"/>
		<dsp:input type="submit" bean="StoreLocatorFormHandler.findStores" id="update-store-submit-modal" name="bopis-search-submit-modal" class="button primary" value="Find Stores" />
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.errorURL" value="${contextPath}/browse/json/bopisSearchError.jsp" />
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.successURL" value="${contextPath}/sitewide/json/homeStoreSearchSuccess.jsp" />
	</div>

</dsp:page>
