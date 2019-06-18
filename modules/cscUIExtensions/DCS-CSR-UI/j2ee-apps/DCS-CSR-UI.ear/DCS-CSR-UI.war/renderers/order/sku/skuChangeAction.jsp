<%--
 Renders the action bits of the Sku Change Popup

 @renderInfo - The RenderInfo
 @commerceItem - The commerce item to change
 @commerceItemId - The commerce item to change
 @productId - The product ID
 @renderInfo - the RenderInfo for the Sku table renderer
 @product - the product object

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuChangeAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <dsp:getvalueof var="renderInfo" param="renderInfo"/>
      <dsp:getvalueof var="mode" param="mode"/>
      <dsp:getvalueof var="commerceItemId" param="commerceItemId"/>
      <dsp:getvalueof var="productId" param="productId"/>
      <dsp:getvalueof var="skuId" param="skuId"/>
      <svc-ui:frameworkPopupUrl var="url" windowId="${windowId}" productId="${productId}"
        commerceItemId="${commerceItemId}"/>
      <dsp:input type="hidden" value="${url}" 
        bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.errorUrlProperty}"/>
      <svc-ui:frameworkPopupUrl var="url" windowId="${windowId}" productId="${productId}"
        commerceItemId="${commerceItemId}" success="true"/>
      <dsp:input type="hidden" value="${url}"
        bean="${renderInfo.pageOptions.formHandler}.${renderInfo.pageOptions.successUrlProperty}"/>
      <dsp:input type="hidden" value="hi"
        bean="${renderInfo.pageOptions.formHandler}.changeSkus"/>
      <c:choose>
        <c:when test="${ not empty skuId }">
          <c:set var="ssradioname" value="sku-select"/>
        </c:when>
        <c:otherwise>
          <c:set var="ssradioname"
            value="SKUCNG:${commerceItemId}"/>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${mode == 'return'}">
          <c:set var="popupOkId" value="editLineItemPopup" />
          <c:set var="popupCancelId" value="editLineItemPopup" />
        </c:when>
        <c:otherwise>
          <c:set var="popupOkId" value="okSkuPick" />
          <c:set var="popupCancelId" value="cancelSkuPick" />
        </c:otherwise>
      </c:choose>
      <div class="atg_dataTableActions">
        <div class="atg_actionTo">
          <input value="<fmt:message key='genericRenderer.ok'/>" 
            type="button" id="okSkuPick" priority="-100"
            onClick="atg.commerce.csr.cart.skuChangePopupOkSelected(
              '${mode}', '${url}', 'okSkuPick', '${ssradioname}', '${popupOkId}' );
            return false;"/>
          <input value="<fmt:message key='genericRenderer.cancel'/>" 
            type="button" id="cancelSkuPick"
            onClick="atg.commerce.csr.common.hidePopupWithReturn('${popupCancelId}', {result:'cancel'});
              return false;"/>
        </div>
      </div>
      <c:if test="${param.success}">
        <script type="text/javascript">
          atg.commerce.csr.common.hidePopupWithReturn( '${popupCancelId}', {result : 'ok'});
        </script>
      </c:if>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/skuChangeAction.jsp#1 $$Change: 946917 $--%>
