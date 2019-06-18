<%--
This page produces the gwp link for an item in the order. 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/changeGiftLink.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<script type="text/javascript">

<%-- change gift popup --%>
dojo.addOnLoad(function () {
  if (!dijit.byId("selectGiftPopup")) {
    new dojox.Dialog({ id: "selectGiftPopup",
                       cacheContent: "false",
                       executeScripts: "true",
                       scriptHasHooks: "true",
                        duration: 100,
                       "class": "atg_commerce_csr_popup"});
  }
});
</script>
<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:importbean var="changeGiftPopupPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gwp/ChangeGiftPopupPageFragment" />
<dsp:getvalueof var="item" param="item"/>
<dsp:getvalueof var="order" param="order"/>

<dsp:droplet name="/atg/commerce/promotion/GiftWithPurchaseSelectionsDroplet">
<dsp:param name="item" value="${item}" />
<dsp:param name="order" value="${order}" />
<dsp:oparam name="output">
  <dsp:getvalueof var="selections" param="selections"/>
  
  <c:if test="${!empty selections}">
    
    <%-- if there are multiple gwp for this item we only concern ourselves with the first one for changing --%>
    <c:set var="found" value="${false}"/>
    <c:forEach var="selection" items="${selections}">
      <c:if test="${!found}">
  
        <%-- check if there are more the one product/sku combination before displaying change link  --%>
        <dsp:droplet name="/atg/commerce/promotion/GiftWithPurchaseSelectionChoicesDroplet">
        <dsp:param name="selection" value="${selection}" />
        <dsp:param name="alwaysReturnSkus" value="${true}" />
        <dsp:oparam name="output">
          
          <dsp:getvalueof var="choices" param="choices"/>
          <c:if test="${(fn:length(choices) > 1) || (choices[0].skuCount > 1)}">
            <c:set var="found" value="${true}"/>
            <c:url var="changeGiftUrl" context="/${changeGiftPopupPageFragment.servletContext}" value="${changeGiftPopupPageFragment.URL}">
              <c:param name="itemId" value="${item.id}" />
              <c:param name="promotionId" value="${selection.promotionId}" />
              <c:param name="quantity" value="1" />
              <c:param name="giftHashCode" value="${selection.giftHashCode}" />
              <c:param name="giftType" value="${selection.giftType}" />
              <c:param name="giftDetail" value="${selection.giftDetail}" />
             <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}" />
            </c:url>
      
            <img src="${CSRConfigurator.contextRoot}/images/icons/icon_gwp.gif" style="cursor:pointer"
              title="<fmt:message key="gwp.changeGift.link.title"/>"
              onclick="atg.commerce.csr.common.showPopupWithReturn({
                    popupPaneId: 'selectGiftPopup',
                    url: '${changeGiftUrl}',
                    title: '<fmt:message key='gwp.changeGift.popup.title' />',
                    onClose: function( args ) {  } });event.cancelBubble=true;return false;"></img> 
                     &nbsp;
            <a id="changegift" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
                    popupPaneId: 'selectGiftPopup',
                    url: '${changeGiftUrl}',
                    title: '<fmt:message key='gwp.changeGift.popup.title' />',
                    onClose: function( args ) {  } });event.cancelBubble=true;return false;" 
                    title="<fmt:message key="gwp.changeGift.link.title"/>"
                    ><fmt:message key="gwp.changeGift.link.text"/>
            </a>
      
          </c:if>
        </dsp:oparam>
        </dsp:droplet><%-- End GiftWithPurchaseSelectionChoicesDroplet --%>
      </c:if><!-- end if we founf a selection with multiple choices -->
    
    </c:forEach><!-- end for each selection on the item -->
    
  </c:if><%-- End if there are gwp for this commerce item --%>
</dsp:oparam>
</dsp:droplet><%-- End GiftWithPurchaseSelectionsDroplet --%>

  
    

  </dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/changeGiftLink.jsp#1 $$Change: 946917 $--%>
