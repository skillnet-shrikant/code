<%--
This page displays each line item in the shipping group.

Originally this page was an fragement. Since we are trying to use the custom renderers,
we had to switch to a normal jsp file.

@version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsLineItem.jsp#3 $$Change: 1179550 $
@updated $DateTime: 2015/07/10 11:58:13 $$Author: jsiddaga $
--%>
<%@  include file="/include/top.jspf"%>
<c:catch var="exception">

  <dsp:page xml="false">

    <dsp:importbean var="cart" bean="/atg/commerce/custsvc/order/ShoppingCart" />
    <dsp:importbean bean="/atg/commerce/custsvc/returns/ReturnFormHandler" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
    <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
    <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>
    <dsp:importbean bean="/atg/commerce/custsvc/returns/GetReturnItemQuantityInfoDroplet"/>
    <dsp:importbean bean="/atg/svc/agent/ui/AgentUIConfiguration"/>

    <dsp:getvalueof var="returnRequest" bean="ShoppingCart.returnRequest"/>
    <c:set var="currencyCode" value="${returnRequest.order.priceInfo.currencyCode }"/>
    <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
    <c:set var="fractionalUnitPattern" value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
    <c:set var="fractionalValidationMessage" value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />

    <dsp:getvalueof var="item" param="item"/>
    <dsp:getvalueof var="itemIndex" param="itemIndex"/>

    <dsp:layeredBundle basename="atg.commerce.csr.returns.WebAppResources">
    
    <dsp:droplet name="/atg/commerce/custsvc/order/IsItemReturnable">
    <dsp:param name="item" value="${item.commerceItem}"/>
    <dsp:oparam name="true">
      <c:set var="returnable" value="${true}"/>
    </dsp:oparam>
    <dsp:oparam name="false">
      <c:set var="returnable" value="${false}"/>
      <dsp:getvalueof param="returnableDescription" var="returnableDescription"/>
    </dsp:oparam>
    </dsp:droplet>
    

<tr class="${((itemIndex % 2)==0) ? '' : 'atg_altRow'}">
  <c:if test="${isMultiSiteEnabled == true}">
  <td>
    <c:set var="siteId" value="${item.commerceItem.auxiliaryData.siteId}"/>
    <csr:siteIcon siteId="${siteId}" />
  </td>
  </c:if>

  <dsp:droplet name="GetReturnItemQuantityInfoDroplet">
    <dsp:param name="item"  param="item"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="quantityShipped" param="quantityShipped"/>
        <dsp:getvalueof var="quantityReturned" param="quantityReturned"/>
        <dsp:getvalueof var="quantityAvailable" param="quantityAvailable"/>
    </dsp:oparam>
  </dsp:droplet>

  <td class="atg_numberValue">
  <web-ui:formatNumber value="${! empty quantityShipped ? quantityShipped : 0 }" />
  <fmt:message key="common.openbracket" />
    <web-ui:formatNumber value="${! empty quantityReturned ? quantityReturned : 0 }"/>
  <fmt:message key="common.closebracket" />
  </td>

  <dsp:droplet name="Switch">
    <dsp:param name="value" value="${returnable}"/>
    <dsp:oparam name="false">
      <td class="atg_numberValue">
        &nbsp;
      </td>
    </dsp:oparam>
    <dsp:oparam name="true">
      <!-- Site Access control to disable/enable quantity to return as appropriate -->
      <c:set var="displayQuantityToReturnControl" value="true"/>
      <c:set var="siteId" value="${item.commerceItem.auxiliaryData.siteId}"/>
	      <c:if test="${envTools.siteAccessControlOn =='true' }">
	        <dsp:droplet name="IsSiteAccessibleDroplet">
	          <dsp:param name="siteId" value="${siteId}"/>
	          <dsp:oparam name="false">
              <c:set var="displayQuantityToReturnControl" value="false"/>
	          </dsp:oparam>
	        </dsp:droplet>
	      </c:if>
        <c:if test="${displayQuantityToReturnControl eq true}">
          <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
            <dsp:param name="item" value="${item.commerceItem}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="isFractional" param="fractional"/>
            </dsp:oparam>
          </dsp:droplet>

          <c:choose>
            <c:when test ="${isFractional eq true }">
              <td class="atg_commerce_csr_returnQty">
                <dsp:input bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].quantityWithFractionToReturn" type="text" value="0" size="5" maxlength="9">
                  <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                  <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                  <dsp:tagAttribute name="trim" value="true" />
                  <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces},pattern:${fractionalUnitPattern}}"/>
                </dsp:input>
              </td>
            </c:when>
            <c:otherwise>
              <td class="atg_leftValue">
              <dsp:select bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].quantityToReturn">
                <c:forEach var="returnQty" begin="0" end="${!empty quantityAvailable ? quantityAvailable : 0}">
                  <dsp:option value="${returnQty}"><web-ui:formatNumber value="${returnQty}"/></dsp:option>
                </c:forEach>
              </dsp:select>
              </td>
            </c:otherwise>
          </c:choose>
        </c:if>
    </dsp:oparam>
  </dsp:droplet>
  <td><dsp:valueof param="item.commerceItem.catalogRefId"/></td>
  <td><dsp:tomap var="productRef" param="item.commerceItem.auxiliaryData.productRef"/>${fn:escapeXml(productRef.displayName)}</td>
  <td>
  <dsp:droplet name="Switch">
  <dsp:param name="value" value="${returnable}"/>
  <dsp:oparam name="false">
       <c:out value="${returnableDescription}"/>
    </dsp:oparam>
    <dsp:oparam name="true">
      <!-- Site Access control to disable/enable return reason to return as appropriate -->
      <c:choose>
        <c:when test ="${envTools.siteAccessControlOn =='true' }">
          <c:set var="siteId" value="${item.commerceItem.auxiliaryData.siteId}"/>
          <dsp:droplet name="IsSiteAccessibleDroplet">
            <dsp:param name="siteId" value="${siteId}"/>
             <dsp:oparam name="true">
				      <dsp:select bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].returnReason">
				        <dsp:option value=""><fmt:message key="returnItems.reason.option.empty" /></dsp:option>
		            <dsp:droplet name="ForEach">
		              <dsp:param bean="ReturnFormHandler.reasonCodes" name="array"/>
		              <dsp:param name="elementName" value="reasonCode"/>
		              <dsp:param name="sortProperties" value="+description"/>
		              <dsp:oparam name="output">
		                <dsp:option paramvalue="reasonCode.repositoryId">
		                <dsp:valueof param="reasonCode.readableDescription"/>
		                </dsp:option>
		              </dsp:oparam>
		            </dsp:droplet>
				      </dsp:select>
				    </dsp:oparam>
            <dsp:oparam name="false">
              &nbsp;
            </dsp:oparam>
          </dsp:droplet>
        </c:when>
        <c:otherwise>
          <dsp:oparam name="true">
			      <dsp:select bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].returnReason">
			        <dsp:option value=""><fmt:message key="returnItems.reason.option.empty" /></dsp:option>
	            <dsp:droplet name="ForEach">
	              <dsp:param bean="ReturnFormHandler.reasonCodes" name="array"/>
	              <dsp:param name="elementName" value="reasonCode"/>
	              <dsp:param name="sortProperties" value="+description"/>
	              <dsp:oparam name="output">
	                <dsp:option paramvalue="reasonCode.repositoryId">
	                <dsp:valueof param="reasonCode.readableDescription"/>
	                </dsp:option>
	              </dsp:oparam>
	             </dsp:droplet>
			      </dsp:select>
			    </dsp:oparam>
        </c:otherwise>
     </c:choose>
      </dsp:oparam>
  </dsp:droplet>
  </td>
  <td>
  	<dsp:droplet name="Switch">
  	<dsp:param name="value" value="${returnable}"/>
  	<dsp:oparam name="false">
    </dsp:oparam>
    <dsp:oparam name="true">
		<%-- Full list of stores sorted by code id --%>
		<dsp:select bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].storeId">
			<dsp:option>Select a store</dsp:option>
			<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
				<dsp:param name="queryRQL" value="ALL"/>
				<dsp:param name="repository" value="/atg/commerce/locations/LocationRepository"/>
				<dsp:param name="itemDescriptor" value="location"/>
				<dsp:param name="sortProperties" value="+city"/>
				<dsp:param name="elementName" value="locationItem"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="currentStoreId" param="locationItem.locationId"/>
					<dsp:option value="${currentStoreId}" selected="${storeCodeValue == currentStoreId}">
						<dsp:valueof param="locationItem.city"/>${currentStoreId} 
					</dsp:option>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:select>
		<dsp:input type="hidden" id="refundShippingCheckbox${itemIndex}" bean="ShoppingCart.returnRequest.shippingGroupList[param:shippingGroupIndex].itemList[param:itemIndex].returnShipping" />
	</dsp:oparam>
	</dsp:droplet>
  </td>
</tr>
    </dsp:layeredBundle>
  </dsp:page>
</c:catch>
<c:if test="${exception != null}">
  ${exception}
  <%
    Exception ee = (Exception) pageContext.getAttribute("exception");
    ee.printStackTrace();
  %>
</c:if>

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/returns/returnItemsLineItem.jsp#3 $$Change: 1179550 $--%>
