<%--
 This page defines the cross sell panel
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/crossSell.jsp#4 $
 @updated $DateTime: 2015/08/27 13:23:06 $
--%>
<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
  <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="cart"/>
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CSRCrossSellFormHandler" var="CSRCrossSellFormHandler"/>
  <dsp:importbean bean="/atg/commerce/custsvc/environment/CSREnvironmentTools" var="envTools"/>
  <dsp:importbean bean="/atg/commerce/custsvc/multisite/IsSiteAccessibleDroplet"/>

  <dsp:importbean bean="/atg/multisite/Site"/> 
  <dsp:getvalueof var="currentSiteId" bean="Site.id"/>

  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">

  <c:set var="fractionalUnitDecimalPlaces"  value="${CSRConfigurator.fractionalUnitsConfiguration.numberOfDecimalPlaces}" />
  <c:set var="fractionalUnitPattern"        value="${CSRConfigurator.fractionalUnitsConfiguration.validationPattern}" />
  <c:set var="fractionalValidationMessage"  value="${CSRConfigurator.fractionalUnitsConfiguration.invalidMessage}" />

<script type="text/javascript">
  if (!dijit.byId("editLineItemPopup")) {
    new dojox.Dialog({ id: "editLineItemPopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                       duration: 100,
                       "class": "atg_commerce_csr_popup"});
  }
</script>

    <c:url var="productEditLineItemURL" context="${CSRConfigurator.contextRoot}" value="/include/order/editProductSKU.jsp">
      <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
      <c:param name="mode" value="return"/>
      <c:param name="skuId" value="SKUIDPLACEHOLDER"/>
      <c:param name="productId" value=""/>
    </c:url>
    <input type="hidden" id="productEditLineItem" value="<c:out value='${productEditLineItemURL}'/>"/>
    <c:url var="readSkuInfoURL" context="${CSRConfigurator.contextRoot}" value="/include/order/getSkuInfo.jsp">
       <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}"/>
    </c:url>
    <input type="hidden" id="readSkuInfoURL" value="<c:out value='${readSkuInfoURL}'/>"/>
    <div class="atg_commerce_csr_content">
      <%--<a href="#" class="atg_commerce_csr_sectionOpen">
        <fmt:message key="cart.crossSell.continueShopping"/>
      </a>--%>
      <div>
        <c:set var="count" value="0"/>
        <dsp:setvalue bean="CSRCrossSellFormHandler.order" value="${cart.current}"/>
        <%-- /include/order/filterCrossSellItems.jsp file sets the following request scoped variables.
            1) filteredCrossSellItems
            2) filteredCrossSellItemsCount
        --%>
        <dsp:include src="/include/order/filterCrossSellItems.jsp" otherContext="${CSRConfigurator.contextRoot}">
         <dsp:param name="relatedProducts" value="${CSRCrossSellFormHandler.crossSellItemsByOrder}"/>
        </dsp:include>
        
        <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="${filteredCrossSellItemsCount}"/>
        <dsp:form id="addCrossSellsToCartForm" formid="addCrossSellsToCartForm" name="addCrossSellsToCartForm">
          <fmt:message key="cart.crossSell.itemsAddedToShoppingCart" var="confirmMsg"/>
          <input id="atg.successMessage" name="atg.successMessage" type="hidden"
                 value="${confirmMsg}"/>
          <svc-ui:frameworkUrl var="url"/>
          <dsp:input bean="CartModifierFormHandler.addItemCount" id="addItemCount" name="addItemCount" type="hidden" value="${filteredCrossSellItemsCount}"/>
          <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" value="${url}" />
          <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${url}" />
          <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-10"/>
          <table class="atg_dataTable">
            <thead>
              <%-- Site Icon Heading --%>
              <c:if test="${isMultiSiteEnabled == true}">
                <th class="atg_commerce_csr_siteIcon" scope="col">
                  <fmt:message key="cart.crossSell.site"/>
                </th>
              </c:if>
              <th class="atg_rowSelector" scope="col"></th>
              <th scope="col">
                <fmt:message key="cart.crossSell.name"/>
              </th>
              <th scope="col">
                <fmt:message key="cart.crossSell.sku"/>
              </th>
              <th scope="col">
                <fmt:message key="cart.crossSell.status"/>
              </th>
              <th class="atg_numberValue" scope="col">
                <fmt:message key="cart.crossSell.priceRange"/>
              </th>
              <th class="atg_numberValue" scope="col" align="center">
                <fmt:message key="cart.crossSell.qty"/>
              </th>
            </thead>
            <c:forEach var="relatedProductItem" items="${filteredCrossSellItems}">
              <dsp:tomap var="relatedProduct" value="${relatedProductItem}"/>
              <dsp:tomap value="${relatedProduct.smallImage}" var="smallImage"/>
              <c:set var="isSiteActive" value=""/>
              <c:set var="isShareable" value="${true}"/>
              <tr>
                <dsp:droplet name="SiteIdForCatalogItem">
                  <dsp:param name="item" value="${relatedProductItem}"/>
                  <dsp:param name="currentSiteFirst" value="true" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="siteId" param="siteId"/>
                    <dsp:getvalueof var="isSiteActive" param="active"/>
                    <dsp:getvalueof var="isShareable" param="inGroup"/>
                  </dsp:oparam>
                </dsp:droplet>
                
                <%-- Do not render cross sell items for disabled or deleted sites --%>
                <c:if test="${(isMultiSiteEnabled == false) || ((isMultiSiteEnabled == true) && (isSiteActive != false) && (isShareable == true))}">
                    <c:if test="${isMultiSiteEnabled == true}">
                      <td class="atg_commerce_csr_siteIcon">
                        <csr:siteIcon siteId="${siteId}" />
                      </td>
                    </c:if>

                  <td>
                    <c:if test="${!empty smallImage.url}">
                      <img src="<c:out value='${smallImage.url}'/>" alt="${fn:escapeXml(relatedProduct.displayName)}"  height="60" />
                    </c:if>
                  </td>
                  <td>
                    ${fn:escapeXml(relatedProduct.displayName)}
                  </td>
                  <td>
                    <dsp:tomap value="${relatedProduct.childSKUs}" var="childSKUs"/>
                    <div id="selectCrossSellSkuLinkContainer<c:out value='${count}'/>">
                       <dsp:droplet name="/atg/commerce/catalog/UnitOfMeasureDroplet">
                         <dsp:param name="product" value="${relatedProduct.id}"/>
                         <dsp:param name="sku" value="${childSKUs.baseList[0].id}"/>
                         <dsp:oparam name="output">
                            <dsp:getvalueof var="isFractional" param="fractional"/>
                         </dsp:oparam>
                       </dsp:droplet>
                      <c:choose>
                        <c:when test="${empty childSKUs.baseList[1]}">
                          <dsp:tomap value="${childSKUs.baseList[0]}" var="singleSKU"/>
                          <c:choose>
                            <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                              <a onclick="atg.commerce.csr.order.selectCrossSellSku('<c:out value="${relatedProduct.id}"/>', '<c:out value="${singleSKU.id}"/>', '<c:out value="${count}"/>', '<c:out value="${relatedProduct.displayName}"/>', '<c:out value="${siteId}"/>', '<c:out value="${currentSiteId}"/>');" href="#">
                                <c:out value="${singleSKU.id}"/>
                              </a>
                            </c:when>
                            <c:otherwise>
                              <a onclick="atg.commerce.csr.order.selectCrossSellSku('<c:out value="${relatedProduct.id}"/>', '<c:out value="${singleSKU.id}"/>', '<c:out value="${count}"/>', '<c:out value="${relatedProduct.displayName}"/>');" href="#">
                                <c:out value="${singleSKU.id}"/>
                              </a>
                            </c:otherwise>
                          </c:choose>  
                        </c:when>
                        <c:otherwise>
                          <c:choose>
                            <c:when test="${(isMultiSiteEnabled == true) && (isSiteDeleted != true)}">
                              <a onclick="javascript:atg.commerce.csr.order.selectCrossSellSku('<c:out value="${relatedProduct.id}"/>', '', '<c:out value="${count}"/>', '<c:out value="${relatedProduct.displayName}"/>', '<c:out value="${siteId}"/>', '<c:out value="${currentSiteId}"/>');" href="#">
                                <fmt:message key="cart.crossSell.select"/>
                              </a>
                            </c:when>
                            <c:otherwise>
                              <a onclick="javascript:atg.commerce.csr.order.selectCrossSellSku('<c:out value="${relatedProduct.id}"/>', '', '<c:out value="${count}"/>', '<c:out value="${relatedProduct.displayName}"/>');" href="#">
                            <fmt:message key="cart.crossSell.select"/>
                          </a>
                            </c:otherwise>
                          </c:choose>  
                        </c:otherwise>
                      </c:choose>
                    </div>
                    <dsp:input bean="CartModifierFormHandler.items[${count}].productId" id="productId${count}" name="productId${count}" type="hidden" value="${relatedProduct.id}"/>
                    <dsp:input bean="CartModifierFormHandler.items[${count}].catalogRefId" id="skuId${count}" name="skuId${count}" type="hidden" value="${singleSKU.id}"/>
                    <dsp:input bean="CartModifierFormHandler.items[${count}].siteId" id="siteId${count}" name="siteId${count}" type="hidden" value="${siteId}"/>
                  </td>
                  <td>
                    <div id="status<c:out value='${count}'/>">
                      <c:choose>
                        <c:when test="${!empty singleSKU}">
                          <dsp:droplet name="InventoryLookup">
                            <dsp:param name="itemId" value="${singleSKU.id}"/>
                            <dsp:param name="useCache" value="true"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
                                <dsp:oparam name="1001">
                                  <fmt:message key="global.product.availabilityStatus.outOfStock"/>
                                </dsp:oparam>
                                <dsp:oparam name="1002">
                                  <fmt:message key="global.product.availabilityStatus.preorder"/>
                                </dsp:oparam>
                                <dsp:oparam name="1003">
                                  <fmt:message key="global.product.availabilityStatus.backorder"/>
                                </dsp:oparam>
                                <dsp:oparam name="1000">
                                  <fmt:message key="global.product.availabilityStatus.inStock"/>
                                </dsp:oparam>
                                <dsp:oparam name="unset">
                                  &nbsp;
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                        </c:when>
                        <c:otherwise>
                          &nbsp;
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </td>
                  <td class="atg_numberValue">
                    <div id="price<c:out value='${count}'/>">
                    <dsp:include src="/include/catalog/displayProductPriceRange.jsp" otherContext="${CSRConfigurator.contextRoot}">
                      <dsp:param name="productToPrice" value="${relatedProductItem}"/>
                    </dsp:include>
                    </div>
                  </td>
                  <td class="atg_numberValue" align="center">
                    <c:choose>
                      <c:when test="${empty childSKUs.baseList[1]}">
                        <c:choose>
                          <c:when test="${envTools.siteAccessControlOn == 'true'}"> 
                            <dsp:droplet name="IsSiteAccessibleDroplet">
                              <dsp:param name="siteId" value="${siteId}"/>
                              <dsp:oparam name="true">
                                <c:choose>
                                  <c:when test="${isFractional == 'true'}">
                                   <dsp:input bean="CartModifierFormHandler.items[${count}].quantityWithFraction"
                                     id="fractqty${count}"
                                     name="fractqty${count}"
                                     type="text"
                                     size="4"
                                     maxlength="9">
                                     <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                                     <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                                     <dsp:tagAttribute name="trim" value="true" />
                                     <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
                                     </dsp:input>
                                  </c:when>
                                  <c:otherwise>
                                   <dsp:input bean="CartModifierFormHandler.items[${count}].quantity"
                                     id="qty${count}"
                                     name="qty${count}"
                                     type="text"
                                     size="4"
                                     maxlength="4">
                                    <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                                    <dsp:tagAttribute name="invalidMessage" value="Please enter valid value."/>
                                    <dsp:tagAttribute name="trim" value="true" />
                                    <dsp:tagAttribute name="constraints" value="{places:0, pattern: '#####'}"/>
                                    </dsp:input>
                                  </c:otherwise>
                                </c:choose>
                              </dsp:oparam>
                              <dsp:oparam name="false">
                                &nbsp;
                              </dsp:oparam>
                            </dsp:droplet>
                          </c:when>
                          <c:otherwise>
                            <c:choose>
                              <c:when test="${isFractional == 'true'}">
                               <dsp:input bean="CartModifierFormHandler.items[${count}].quantityWithFraction"
                                 id="fractqty${count}"
                                 name="fractqty${count}"
                                 type="text"
                                 size="4"
                                 maxlength="9">
                                 <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                                 <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                                 <dsp:tagAttribute name="trim" value="true" />
                                 <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
                                 </dsp:input>

                              </c:when>
                              <c:otherwise>
                               <dsp:input bean="CartModifierFormHandler.items[${count}].quantity"
                                 id="qty${count}"
                                 name="qty${count}"
                                 type="text"
                                 size="4"
                                 maxlength="4">
                                 <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                                 <dsp:tagAttribute name="invalidMessage" value="Please enter valid value."/>
                                 <dsp:tagAttribute name="trim" value="true" />
                                 <dsp:tagAttribute name="constraints" value="{places:0, pattern: '#####'}"/>
                               </dsp:input>
                              </c:otherwise>
                            </c:choose>
                          </c:otherwise>
                        </c:choose>     
                      </c:when>
                      <c:otherwise>
                        <c:choose>
                          <c:when test="${isFractional == 'true'}">
                           <dsp:input bean="CartModifierFormHandler.items[${count}].quantityWithFraction"
                             id="fractqty${count}"
                             name="fractqty${count}"
                             type="text"
                             size="4"
                             maxlength="9"
                             disabled="true">
                             <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                             <dsp:tagAttribute name="invalidMessage" value="${fractionalValidationMessage}"/>
                             <dsp:tagAttribute name="trim" value="true" />
                             <dsp:tagAttribute name="constraints" value="{places:${fractionalUnitDecimalPlaces}, pattern:${fractionalUnitPattern}}"/>
                           </dsp:input>
                          </c:when>
                          <c:otherwise>
                           <dsp:input bean="CartModifierFormHandler.items[${count}].quantity"
                             id="qty${count}"
                             name="qty${count}"
                             type="text"
                             size="4"
                             maxlength="4"
                             disabled="true">
                             <dsp:tagAttribute name="dojoType" value="dijit.form.NumberTextBox" />
                             <dsp:tagAttribute name="invalidMessage" value="Please enter valid value."/>
                             <dsp:tagAttribute name="trim" value="true" />
                             <dsp:tagAttribute name="constraints" value="{places:0, pattern: '#####'}"/>
                           </dsp:input>
                          </c:otherwise>
                        </c:choose>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </c:if>
              </tr>
              <c:set var="count" value="${count + 1}"/>
              <c:remove var="singleSKU"/>
            </c:forEach>
          </table>
        </dsp:form>
        <c:choose>
          <c:when test="${count == 0}">
            <fmt:message key="cart.crossSell.noItems"/>
          </c:when>
          <c:otherwise>
            <div class="atg_dataTableFooterActions">
              <fmt:message key="cart.crossSell.addToShoppingCart" var="addToShoppingCartLabel"/>
              <dsp:droplet name="OrderIsModifiable">
                <dsp:param name="order" value="${cart.originalOrder}"/>
                <dsp:oparam name="true">
                  <input id="crossSellSubmitButton"
                       type="submit"
                       value="<c:out value='${addToShoppingCartLabel}'/>"
                       onclick="atg.commerce.csr.order.addCrossSellsToCart();"/>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <input id="crossSellSubmitButton"
                       type="submit"
                       value="<c:out value='${addToShoppingCartLabel}'/>"
                       onclick="atg.commerce.csr.order.addCrossSellsToCart();"
                       disabled="disabled"/>
                </dsp:oparam>
              </dsp:droplet>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
    <script type="text/javascript">
      _container_.onLoadDeferred.addCallback( function() {
        atg.keyboard.registerFormDefaultEnterKey("addCrossSellsToCartForm", "crossSellSubmitButton");
      });
      _container_.onUnloadDeferred.addCallback( function() {
        atg.keyboard.unRegisterFormDefaultEnterKey("addCrossSellsToCartForm");
      });
    </script>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/panels/order/crossSell.jsp#4 $$Change: 1191436 $--%>
