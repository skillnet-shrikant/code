<%--
 A page fragment that displays cross sell items for a given product

 @param productId The ID of the product to display

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/crossSellItems.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:importbean bean="/atg/commerce/custsvc/catalog/CSRProductLookup"/>
    <dsp:importbean bean="/atg/dynamo/servlet/RequestLocale"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/OrderIsModifiable"/>
    <dsp:importbean bean="/atg/commerce/custsvc/order/ShoppingCart" var="shoppingCart"/>
    <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet"/>
    <dsp:importbean bean="/atg/commerce/custsvc/util/CSRConfigurator" var="CSRConfigurator"/>
    <dsp:importbean bean="/atg/commerce/multisite/SiteIdForCatalogItem"/>
    <dsp:importbean bean="/atg/commerce/custsvc/util/CSRAgentTools" var="CSRAgentTools"/>

    <dsp:importbean bean="/atg/multisite/Site"/> 
    <dsp:getvalueof var="currentSiteId" bean="Site.id"/>
    
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <div class="atg_commerce_csr_subPanel">
        <div class="atg_commerce_csr_subPanelHeader" >
          <ul class="atg_commerce_csr_panelToolBar">
            <li class="atg_commerce_csr_header">
              <h4 id="atg_commerce_csr_catalogdescriptionview_crossSellItems">
                <fmt:message key="crossSellRenderer.crossSellItems"/> 
              </h4>
            </li>
          </ul>
        </div> 
    <dsp:droplet name="SharingSitesDroplet">
    <dsp:oparam name="output">
      <dsp:getvalueof var="sites" param="sites"/>
    </dsp:oparam>
    </dsp:droplet>
               
      <dsp:droplet name="CSRProductLookup">
        <dsp:param name="repositoryKey" bean="RequestLocale.locale"/>
        <dsp:param name="id" param="productId"/>
        <dsp:oparam name="noCatalog">
          <dsp:getvalueof var="product" param="element"/>
        </dsp:oparam>
        <dsp:oparam name="output">
          <dsp:getvalueof var="product" param="element"/>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:tomap var="productItem" value="${product}"/>
      <svc-ui:frameworkUrl var="url"/>
      <dsp:form id="addCrossSellForm" formid="addCrossSellItemForm">
        <input id="atg.successMessage" name="atg.successMessage" type="hidden"
               value=""/>
        <dsp:input type="hidden" value="1" 
          bean="${renderInfo.pageOptions.formHandler}.addItemCount"/>
        <dsp:input type="hidden" value="${url}"
          bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.successUrlProperty}"/>
        <dsp:input type="hidden" value="${url}"
          bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.errorUrlProperty}"/>
        <dsp:input id="addCrossSellProductId" type="hidden" value=""
          bean="${renderInfo.pageOptions.formHandler}.items[0].productId"/>
        <dsp:input id="addCrossSellSkuId" type="hidden" value=""
          bean="${renderInfo.pageOptions.formHandler}.items[0].catalogRefId"/>
        <dsp:input type="hidden" value="1"
          bean="${renderInfo.pageOptions.formHandler}.items[0].quantity"/>
        <dsp:input type="hidden" value="hi"
          bean="${renderInfo.pageOptions.formHandler}.addItemToOrder"/>
      </dsp:form>
      
      <%-- /include/order/filterCrossSellItems.jsp file sets the following request scoped variables.
           1) filteredCrossSellItems
           2) filteredCrossSellItemsCount
      --%>
      <dsp:include src="/include/order/filterCrossSellItems.jsp" otherContext="${CSRConfigurator.contextRoot}">
       <dsp:param name="relatedProducts" value="${productItem.relatedProducts}"/>
      </dsp:include>
      
      <c:forEach var="relatedItem" items="${filteredCrossSellItems}" end="3">
        <dsp:tomap var="related" value="${relatedItem}"/>
        <dsp:tomap var="image" value="${related.smallImage}"/>
        <!-- item start -->
        <div class="atg_commerce_csr_coreProduct_Cross_sell_items">
          <c:set var="isSiteActive" value=""/>
          <c:set var="isShareable" value="${true}"/>
                
          <dsp:droplet name="SiteIdForCatalogItem">
            <dsp:param name="item" value="${relatedItem}"/>
            <dsp:param name="currentSiteFirst" value="true"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="siteId" param="siteId"/>
              <dsp:getvalueof var="isSiteActive" param="active"/>              
              <dsp:getvalueof var="isShareable" param="inGroup"/>
            </dsp:oparam>
          </dsp:droplet>

          <%-- Do not render cross sell items for disabled or deleted sites --%>
          <c:if test="${(isMultiSiteEnabled == false) || ((isMultiSiteEnabled == true) && (isSiteActive != false) && (isShareable == true))}">
              <c:if test="${isMultiSiteEnabled == true}">
                <div class="atg_commerce_csr_crossSellSiteIcon">
                  <csr:siteIcon siteId="${siteId}" />
                </div>
              </c:if>

            <div class="atg_commerce_csr_crossSellThumb">
              <dsp:img src="${image.url}" height="60"/>
            </div>
            <dl class="atg_commerce_csr_crossSellItems">
              <dt> 
                <c:choose>
                  <c:when test="${isMultiSiteEnabled == true}">
                    <a href="#" onclick="atg.commerce.csr.catalog.showProductViewInSiteContext('${related.id}', '${siteId}', '${fn:escapeXml(currentSiteId)}')">
                     ${fn:escapeXml(related.displayName)} 
                   </a> 
                  </c:when>
                  <c:otherwise>
                    <a href="#" onclick="atgNavigate({
                       panelStack : '${renderInfo.pageOptions.successPanelStacks}',
                       queryParams: { productId : '${related.id}' }});return false;">
                     ${fn:escapeXml(related.displayName)} 
                   </a> 
                  </c:otherwise>
                </c:choose>  
              </dt>
              <dd>
              <dsp:include src="/include/catalog/displayProductPriceRange.jsp" otherContext="${CSRConfigurator.contextRoot}">
              <dsp:param name="productToPrice" value="${relatedItem}"/>
              </dsp:include>
              </dd>
              <dt class="atg_commerce_csr_formButtonsLabel">
              </dt>
              <dd class="atg_commerce_csr_formButtons">
                <c:if test="${1 == fn:length(related.childSKUs)}">
                  <dsp:tomap var="sku" value="${related.childSKUs[0]}"/>
                  <c:set var="isOrderModifiableDisableAttribute" value=" disabled='disabled'"/>
                  <c:if test="${!empty shoppingCart.originalOrder}">
                    <dsp:droplet name="OrderIsModifiable">
                      <dsp:param name="order" value="${shoppingCart.originalOrder}"/>
                      <dsp:oparam name="true">
                        <c:set var="isOrderModifiableDisableAttribute" value=""/>
                      </dsp:oparam>
                    </dsp:droplet>
                  </c:if>
                  <fmt:message key="crossSellRenderer.itemAddedToOrder.js" var="confirmMsg">
                    <fmt:param>${fn:escapeXml(related.displayName)}</fmt:param>
                  </fmt:message>
                  <input type="button" <c:out value="${isOrderModifiableDisableAttribute}"/> 
                    value="<fmt:message key='crossSellRenderer.addToOrder'/>" 
                    onClick="atg.commerce.csr.order.crossSellItems('${related.id}', '${sku.id}', '${confirmMsg}', '${renderInfo.pageOptions.successPanelStacks}');return false;">
                </c:if>
              </dd>
            </dl>
            </c:if>
          </div>
        
        <!-- item end -->
      </c:forEach>
      </div>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/crossSellItems.jsp#1 $$Change: 946917 $--%>
