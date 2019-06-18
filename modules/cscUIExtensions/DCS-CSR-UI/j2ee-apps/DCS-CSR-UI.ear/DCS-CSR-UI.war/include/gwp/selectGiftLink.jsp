<%--
 This page produces the gwp links for the order. 
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/selectGiftLink.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">
<dsp:importbean var="selectGiftPopupPageFragment" bean="/atg/commerce/custsvc/ui/fragments/gwp/SelectGiftPopupPageFragment" />
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
<dsp:getvalueof var="order" param="order"/>
  
<dsp:droplet name="/atg/commerce/custsvc/promotion/GiftWithPurchaseSelectionsDroplet">
<dsp:param name="order" value="${order}" />
<dsp:oparam name="output">
  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param name="array" param="selections"/>
  <dsp:param name="elementName" value="gwpSelection"/>
  <dsp:oparam name="output">

    <dsp:getvalueof var="selection" param="gwpSelection"/>
    <!--quantity missing from order <c:out value="${selection.quantityMissingFromOrder}"/>-->
    
    
    
    <c:if test="${selection.quantityMissingFromOrder > 0}">
    <c:choose>
      <c:when test="${selection.giftType == 'sku'}">
        <c:url var="selectGiftUrl" context="/${selectGiftPopupPageFragment.servletContext}" value="${selectGiftPopupPageFragment.URL}">
        <c:param name="promotionId" value="${selection.promotionId}" />
        <c:param name="quantity" value="${selection.quantityMissingFromOrder}" />
        <c:param name="giftHashCode" value="${selection.giftHashCode}" />
        <c:param name="giftType" value="${selection.giftType}" />
        <c:param name="giftDetail" value="${selection.giftDetail}" />
        <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}" />
        </c:url>
    
        <a id="selectgift" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
            popupPaneId: 'selectGiftPopup',
            url: '${selectGiftUrl}',
            title: '<fmt:message key='gwp.selectGift.popup.title' />',
            onClose: function( args ) {  } });event.cancelBubble=true;return false;" 
            title="<fmt:message key="gwp.selectGift.link.title"/>"
            ><fmt:message key="gwp.selectGift.link.text"/>
        </a>
        &nbsp;

        <img src="${CSRConfigurator.contextRoot}/images/icons/icon_gwp.gif" style="cursor:pointer"
          title="<fmt:message key="gwp.selectGift.link.title"/>"
          onclick="atg.commerce.csr.common.showPopupWithReturn({
          popupPaneId: 'selectGiftPopup',
          url: '${selectGiftUrl}',
          title: '<fmt:message key='gwp.selectGift.popup.title' />',
          onClose: function( args ) {  } });event.cancelBubble=true;return false;"></img> 
          <br>
      </c:when>
      <c:otherwise>
        <c:forEach begin="1" end="${selection.quantityMissingFromOrder}" var="noop">
          <c:url var="selectGiftUrl" context="/${selectGiftPopupPageFragment.servletContext}" value="${selectGiftPopupPageFragment.URL}">
          <c:param name="promotionId" value="${selection.promotionId}" />
          <c:param name="quantity" value="${1}" />
          <c:param name="giftHashCode" value="${selection.giftHashCode}" />
          <c:param name="giftType" value="${selection.giftType}" />
          <c:param name="giftDetail" value="${selection.giftDetail}" />
          <c:param name="${stateHolder.windowIdParameterName}" value="${windowId}" />
          </c:url>
      
          <a id="selectgift" href="#" onclick="atg.commerce.csr.common.showPopupWithReturn({
              popupPaneId: 'selectGiftPopup',
              url: '${selectGiftUrl}',
              title: '<fmt:message key='gwp.selectGift.popup.title' />',
              onClose: function( args ) {  } });event.cancelBubble=true;return false;" 
              title="<fmt:message key="gwp.selectGift.link.title"/>"
              ><fmt:message key="gwp.selectGift.link.text"/>
          </a>
          &nbsp;
 
          <img src="${CSRConfigurator.contextRoot}/images/icons/icon_gwp.gif" style="cursor:pointer"
            title="<fmt:message key="gwp.selectGift.link.title"/>"
            onclick="atg.commerce.csr.common.showPopupWithReturn({
              popupPaneId: 'selectGiftPopup',
              url: '${selectGiftUrl}',
              title: '<fmt:message key='gwp.selectGift.popup.title' />',
              onClose: function( args ) {  } });event.cancelBubble=true;return false;"></img> 
          <br>
        </c:forEach>

      </c:otherwise>
    </c:choose>
    
    </c:if>
    
    
  </dsp:oparam>
  </dsp:droplet><%-- End ForEach --%>
    
</dsp:oparam>
</dsp:droplet><%-- End GiftWithPurchaseSelectionsDroplet --%>

</dsp:layeredBundle>
</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/selectGiftLink.jsp#1 $$Change: 946917 $--%>
