<%--
 A page fragment that displays product SKUs

 @param productId - The ID of the product to display
 @param commerceItemId - The ID of the product to display
 @param formSuffix - Identify
 @param panelId - The ID of the panel using this form
 @param skuId - Optional ID of SKU that should appear selected

 Either productId or commerceItemId must be specified

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productSkus.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="panelId" param="panelId"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
      <dsp:getvalueof var="formSuffix" param="formSuffix"/>
      <c:if test="${ not empty param.skuId }">
        <c:set var="skuId" value="${param.skuId}"/>
      </c:if>
      <csr:getProduct productId="${productId}" commerceItemId="${commerceItemId}">
        <c:set var="productItem" value="${product}"/>
        <dsp:tomap var="product" value="${productItem}"/>
        <div class="atg_commerce_csr_coreProductView">
          <dsp:form id="productSkuForm-${formSuffix}-${param.mode}"
            formid="productSkuForm-${formSuffix}-${param.mode}" onsubmit="return false">
            <%-- Include optional setup JSP --%>
            <c:if test="${not empty renderInfo.pageOptions.pageSetup}">
              <dsp:include src="${renderInfo.pageOptions.pageSetup}" otherContext="${renderInfo.contextRoot}">
                <dsp:param name="renderInfo" value="${renderInfo}"/>
                <dsp:param name="product" value="${productItem}"/>
              </dsp:include>
            </c:if>
            <div class="atg_commerce_csr_coreProductViewData">
              <table id="skus-${productId}" class="atg_dataTable" cellpadding="0" cellspacing="0">
                <c:forEach items="${product.childSKUs}" var="skuItem" varStatus="skuVs">
                  <dsp:tomap var="sku" value="${skuItem}"/>
                  <c:if test="${skuVs.first}">
                    <%-- 
                      First time through the SKUs display the table column header. This
                      happens inside the 'product.childSKUs' forEach loop so that the
                      SKU's repository descriptor is available during the rendering of
                      the column header.
                     --%>
                    <thead>
                      <c:set var="trId" value="trHead-${sku.id}"/>
                      <tr id="${trId}">
                        <%-- Display the configurable column headers --%>
                        <c:forEach var="property" items="${renderInfo.properties}">
                          <c:set var="css" 
                            value='class="${renderInfo.cssThClass[property]}"'/>
                          <c:set var="tdId" value="${property}-td-${sku.id}"/>
                          <th scope="col" id="${tdId}"
                            ${empty renderInfo.cssThClass[property] ? "" : css}>
                            <c:choose>
                              <c:when test="${not empty renderInfo.renderer[property]}">
                                <%-- User renderer to render table column header --%>
                                <dsp:include src="${renderInfo.renderer[property]}" otherContext="${renderInfo.contextRoot}">
                                  <dsp:param name="product" value="${productItem}"/>
                                  <%-- commerceItem supplied by csr:getProduct --%>
                                  <dsp:param name="commerceItem" value="${commerceItem}"/>
                                  <dsp:param name="commerceItemId" 
                                    value="${commerceItemId}"/>
                                  <dsp:param name="productId" value="${productId}"/>
                                  <dsp:param name="sku" value="${sku}"/>
                                  <dsp:param name="skuItem" value="${skuItem}"/>
                                  <dsp:param name="property" value="${property}"/>
                                  <dsp:param name="area" value="header"/>
                                  <dsp:param name="renderInfo" value="${renderInfo}"/>
                                  <dsp:param name="trId" value="${trId}"/>
                                  <dsp:param name="tdId" value="${tdId}"/>
                                  <dsp:param name="panelId" value="${panelId}"/>
                                  <dsp:param name="loopTagStatus" value="${skuVs}"/>
                                </dsp:include>
                              </c:when>
                              <c:otherwise>
                                ${fn:escapeXml(renderInfo.displayName[property])}
                              </c:otherwise>
                            </c:choose>
                          </th>
                        </c:forEach>
                      </tr>
                    </thead>
                  </c:if>
                  <c:if test="${skuVs.first}">
                    <tbody>
                  </c:if>
                  <c:set var="trId" value="trBody-${panelId}-${sku.id}"/>
                  <tr id="${trId}">
                    <%-- Display the SKU properties --%>
                    <c:forEach var="property" items="${renderInfo.properties}">
                      <c:set var="css" value='class="${renderInfo.cssTdClass[property]}"'/>
                      <c:set var="tdId" value="${sku.id}-${productId}-${panelId}-${property}-td"/>
                      <td ${empty renderInfo.cssTdClass[property] ? "" : css} id="${tdId}">
                        <c:choose>
                          <c:when test="${not empty renderInfo.renderer[property]}">
                            <dsp:include src="${renderInfo.renderer[property]}" otherContext="${renderInfo.contextRoot}">
                              <dsp:param name="product" value="${productItem}"/>
                              <%-- commerceItem supplied by csr:getProduct --%>
                              <dsp:param name="commerceItem" value="${commerceItem}"/>
                              <dsp:param name="commerceItemId" value="${commerceItemId}"/>
                              <dsp:param name="productId" value="${productId}"/>
                              <dsp:param name="skuItem" value="${skuItem}"/>
                              <dsp:param name="sku" value="${skuItem}"/>
                              <dsp:param name="property" value="${property}"/>
                              <dsp:param name="area" value="cell"/>
                              <dsp:param name="trId" value="${trId}"/>
                              <dsp:param name="tdId" value="${tdId}"/>
                              <dsp:param name="panelId" value="${panelId}"/>
                              <dsp:param name="loopTagStatus" value="${skuVs}"/>
                            </dsp:include>
                          </c:when>
                          <c:when test="${fn:startsWith(property,'$')}">
                            <c:out value="${skuItem[fn:substringAfter(property,'$')]}"/>
                          </c:when>
                          <c:otherwise>
                            <c:out value="${sku[property]}"/>
                          </c:otherwise>
                        </c:choose>
                      </td>
                    </c:forEach>
                  </tr>
                  <c:if test="${skuVs.last}">
                    </tbody>
                  </c:if>
                </c:forEach>
              </table>

              <script type="text/javascript">
              _container_.onLoadDeferred.addCallback(function () {
                atg.keyboard.registerFormDefaultEnterKey("<c:out value='productSkuForm-${formSuffix}-${param.mode}' />","skuBrowserAction");
              });
              _container_.onUnloadDeferred.addCallback(function () {
                atg.keyboard.unRegisterFormDefaultEnterKey("<c:out value='productSkuForm-${formSuffix}-${param.mode}' />");
              });
              </script>

              <%-- Confirmation message --%>
              <fmt:message key="catalogBrowse.searchResults.productAddedToOrder" var="addToOrderMsg">
                <fmt:param value="${product.displayName}"/>
              </fmt:message>
              <input id="atg.successMessage" name="atg.successMessage" type="hidden" 
                value="${fn:escapeXml(addToOrderMsg)}"/>
              <%-- Include the bit that submits the form, if specified --%>
              <c:if test="${not empty renderInfo.pageOptions.actionRenderer}">
                <dsp:include src="${renderInfo.pageOptions.actionRenderer}" otherContext="${renderInfo.contextRoot}">
                  <%-- commerceItem supplied by csr:getProduct --%>
                  <dsp:param name="commerceItem" value="${commerceItem}"/>
                  <dsp:param name="commerceItemId" value="${commerceItemId}"/>
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:param name="renderInfo" value="${renderInfo}"/>
                  <dsp:param name="product" value="${productItem}"/>
                  <dsp:param name="panelId" value="${panelId}"/>
                </dsp:include>
              </c:if>
            </div>
          </dsp:form>
          
          <!-- Display gift list controls -->
          <c:if test="${not empty renderInfo.pageOptions.giftlistActionRenderer}">
                <dsp:include src="${renderInfo.pageOptions.giftlistActionRenderer}" otherContext="${renderInfo.contextRoot}">
                  <%-- commerceItem supplied by csr:getProduct --%>
                  <dsp:param name="commerceItem" value="${commerceItem}"/>
                  <dsp:param name="commerceItemId" value="${commerceItemId}"/>
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:param name="renderInfo" value="${renderInfo}"/>
                  <dsp:param name="product" value="${productItem}"/>
                  <dsp:param name="panelId" value="${panelId}"/>
                </dsp:include>
              </c:if>
          
        </div>
      </csr:getProduct>
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

<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/productSkus.jsp#1 $$Change: 946917 $--%>
