<%--
 This is a confirmation page for deletion of a giftlist


 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlistDeleteConfirm.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf"%>
<dsp:page xml="true">
  <dsp:importbean bean="/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler" />
  <dsp:getvalueof var="giftlistId" param="giftlistId" scope="request" />
  <dsp:getvalueof var="windowId" param="windowId" scope="request" />
  <svc-ui:frameworkPopupUrl var="url" value="/include/gift/giftlistDeleteConfirm.jsp"
    context="${CSRConfigurator.contextRoot}" giftlistId="${giftlistId}" success="true" windowId="${windowId}" />
  <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
  
    <fmt:message key='giftlist.delete.deleteConfirmMessage' />
      <div class="atg_commerce_csr_panelFooter">  
      <input value="<fmt:message key='giftlist.delete.label'/>"
        type="button" id="deleteGiftListDeleteChoice"
        onClick="
            atgSubmitPopup({url: '${url}', 
              form: dojo.byId('deleteGiftlist'),
              popup: getEnclosingPopup('deleteGiftListDeleteChoice')});
            return false;" />
      <input value="<fmt:message key='giftlist.cancel.label'/>" type="button" id="deleteGiftListCancelChoice" name="deleteGiftListCancelChoice"
        onClick="hidePopupWithResults('deleteGiftListCancelChoice', {result:'cancel'});return false;" /></div>

    <c:if test="${param.success}">
      <script type="text/javascript">
        hidePopupWithResults( 'deleteGiftListDeleteChoice', {result : 'delete'});
      </script>
    </c:if>
    
  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gift/giftlistDeleteConfirm.jsp#1 $$Change: 946917 $--%>
