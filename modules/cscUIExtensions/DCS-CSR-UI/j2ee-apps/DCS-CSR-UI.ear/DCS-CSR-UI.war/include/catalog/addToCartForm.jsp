<%@ include file="../top.jspf"%>
<dsp:page>

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  <dsp:getvalueof param="count" var="count"/>
  <dsp:importbean bean="/atg/commerce/custsvc/order/CartModifierFormHandler" var="cartModifierFormHandler"/>
  <dsp:setvalue bean="CartModifierFormHandler.addItemCount" value="${count}"/>
  <dsp:form formid="addToCartForm" id="atg_commerce_csr_catalog_addToCartForm">
    <svc-ui:frameworkUrl var="url"/>
    <input name="atg.successMessage" type="hidden" value="<fmt:message key='cart.items.successfully.added'/>"/>
    <dsp:input bean="CartModifierFormHandler.addItemCount" id="addItemCount" name="addItemCount" type="hidden" value="${count}"/>
    <dsp:input bean="CartModifierFormHandler.addItemToOrderErrorURL" type="hidden" value="${url}" />
    <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="${url}" />
    <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="hidden" value="" priority="-10"/>
    <c:forEach var="rowId" begin="1" end="${count}"> 
      <dsp:input bean="CartModifierFormHandler.items[${rowId - 1}].siteId" id="atg_commerce_csr_catalog_siteIdToAdd${rowId}" name="atg_commerce_csr_catalog_siteIdToAdd${rowId}" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.items[${rowId - 1}].productId" id="atg_commerce_csr_catalog_productIdToAdd${rowId}" name="atg_commerce_csr_catalog_productIdToAdd${rowId}" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.items[${rowId - 1}].catalogRefId" id="atg_commerce_csr_catalog_skuIdToAdd${rowId}" name="atg_commerce_csr_catalog_skuIdToAdd${rowId}" type="hidden"/>
      <dsp:input bean="CartModifierFormHandler.items[${rowId - 1}].quantity" id="atg_commerce_csr_catalog_qtyToAdd${rowId}" name="atg_commerce_csr_catalog_qtyToAdd${rowId}" type="hidden" value="0"/>
      <dsp:input bean="CartModifierFormHandler.items[${rowId - 1}].quantityWithFraction" id="atg_commerce_csr_catalog_qtyWithFractionToAdd${rowId}" name="atg_commerce_csr_catalog_qtyWithFractionToAdd${rowId}" type="hidden" value="0.0"/>
    </c:forEach>
  </dsp:form>
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/catalog/addToCartForm.jsp#2 $$Change: 1179550 $--%>
