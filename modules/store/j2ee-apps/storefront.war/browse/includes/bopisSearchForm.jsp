<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/locations/StoreLocatorFormHandler"/>
	<dsp:importbean bean="/mff/MFFEnvironment"/>
	<dsp:getvalueof bean="MFFEnvironment.storePickUpRadius" var="distance"/>
	<div class="field-group">
		<label for="bopis-zip-modal">Zip Code</label>
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.quantity" id="bopis-quantity-modal" name="bopis-quantity-modal" value=""/>
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.fromProduct" id="bopis-from-product-modal" name="bopis-from-product-modal" value=""/>
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.productId" id="bopis-product-id-modal" name="bopis-product-id-modal" value=""/>
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.catalogRefId" id="bopis-sku-id-modal" name="bopis-sku-id-modal" value=""/>
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.distance" id="bopis-distance-modal" name="bopis-distance-modal" value="${distance}"/>
		<dsp:input type="tel" bean="StoreLocatorFormHandler.postalCode" id="bopis-zip-modal" name="bopis-zip-modal" data-validation="required uspostal" data-fieldname="Zip Code" placeholder="Zip Code" />
		<dsp:input type="submit" bean="StoreLocatorFormHandler.locateItems" id="bopis-search-submit-modal" name="bopis-search-submit-modal" class="button primary" value="Find Stores" />
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.errorURL" value="${contextPath}/browse/json/bopisSearchError.jsp" />
		<dsp:input type="hidden" bean="StoreLocatorFormHandler.successURL" value="${contextPath}/browse/json/bopisSearchSuccess.jsp" />
	</div>

</dsp:page>
