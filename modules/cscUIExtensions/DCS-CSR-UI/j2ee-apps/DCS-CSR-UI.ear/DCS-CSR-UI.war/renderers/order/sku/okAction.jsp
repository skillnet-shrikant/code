<%--
 Renders an "OK" option to close the popup

 @renderInfo - The RenderInfo
 @commerceItem - The commerce item to change

 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/okAction.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>
<%@ include file="/include/top.jspf" %>

<c:catch var="exception">
  <dsp:page xml="true">
    <dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
      <div class="atg_dataTableActions">
        <div class="atg_actionTo">
          <input value="<fmt:message key='genericRenderer.ok'/>" 
            type="button" id="okChoice"
            onClick="atg.commerce.csr.common.hidePopupWithReturn('okChoice', {result:'ok'}); 
              return false;"/>
        </div>
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
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/renderers/order/sku/okAction.jsp#1 $$Change: 946917 $--%>
