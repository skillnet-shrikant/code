<%--
 This page defines the order link template
 @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/selectGiftPopup.jsp#1 $
 @updated $DateTime: 2015/01/26 17:26:27 $
--%>

<%@ include file="/include/top.jspf" %>
<dsp:page xml="true">

<dsp:layeredBundle basename="atg.svc.commerce.WebAppResources">
<dsp:getvalueof var="itemId" param="itemId"/>
<dsp:getvalueof var="promotionId" param="promotionId"/>
<dsp:getvalueof var="quantity" param="quantity"/>
<dsp:getvalueof var="giftHashCode" param="giftHashCode"/>
<dsp:getvalueof var="giftType" param="giftType"/>
<dsp:getvalueof var="giftDetail" param="giftDetail"/>



<script type="text/javascript">
dojo.provide( "atg.commerce.csr.gwp" );


//closes the select popup. this dialog is defined by globalJS.jsp
closeGWPSelectionPopup = function() {
  dojo.debug("GWP | closeGWPSelectionPopup started");
 var selectGiftPopup = dijit.byId("selectGiftPopup");

  if (selectGiftPopup) {
    atg.commerce.csr.common.hidePopup (selectGiftPopup);
  }
  
};
//sets the sku in the form based on the current selection, submits the form and closes the popup
submitSelection = function(pProductId) {
 dojo.debug("GWP | submitSelection started");
 
 //find the sku that's been selected for this product
 var sku = document.getElementsByName(pProductId + '_radio');
 for(var i=0; i<sku.length; i++)
 {
   if(sku[i].checked == true)
   {
     dojo.debug("GWP sku selection is : " + sku[i].value);
     atg.commerce.csr.gwp.setGWPGiftSelectionFormInputValues({productId:pProductId, skuId:sku[i].value});
     break;
   }
 }
 
 atg.commerce.csr.gwp.submitGWPGiftSelectionForm();
 closeGWPSelectionPopup();  
};

// enables the add to cart when the product is selected has a sku selected.  
enableAddWhenSkuSelected = function(pProductId) {
  dojo.debug("GWP | enableAddWhenSkuSelected started");
  
  //find if a sku that's been selected for this product
  var sku = document.getElementsByName(pProductId + '_radio');

  for(var i=0; i<sku.length; i++)
  {
    if(sku[i].checked == true)
    {
      enableAddToCart(pProductId);
      dojo.debug("GWP productSelect sku selection is : " + sku[i].value);
      break;
    }
  }
 };

 //enables the add to cart button for the given product id
 enableAddToCart = function(pProductId) {
   dojo.debug("GWP | enableAddToCart started");
   var addToCartButton = document.getElementById(pProductId + '_submitGWP');
   dojo.debug("GWP | addtocartbutton is " + addToCartButton);
   addToCartButton.disabled = false; 
  };

  disableAddToCart = function(pProductId) {
    dojo.debug("GWP | disableAddToCart started");
    var addToCartButton = document.getElementById(pProductId + '_submitGWP');
    dojo.debug("GWP | addtocartbutton is " + addToCartButton);
    addToCartButton.disabled = true; 
   };

 
 </script>
  
  
  <dsp:droplet name="/atg/commerce/promotion/GiftWithPurchaseSelectionChoicesDroplet">
  <dsp:param name="giftType" value="${giftType}" />
  <dsp:param name="giftDetail" value="${giftDetail}" />
  <dsp:param name="alwaysReturnSkus" value="${true}" />
  
  <dsp:oparam name="empty">
    <fmt:message key="gwp.msg.nochoices" />
    <input type="button"
      name="cancel_no_choices" value="<fmt:message key="gwp.selectGift.popup.button.cancel" />"
      onClick="closeGWPSelectionPopup(); return false;" />
  </dsp:oparam>
  <dsp:oparam name="output">
  
    <dsp:getvalueof var="choices" param="choices"/>

    <!-- if there is only one choice with one sku, it's add to cart button will be enabled when the popup is first opened. this happens in onLoad if this var is set -->
    <c:set var="singleChoiceProductId" value="${null}"/>  
    <c:if test="${ empty singleChoiceProductId  && (fn:length(choices) == 1) && (choices[0].skuCount == 1)}">
      <c:set var="singleChoiceProductId" value="${choices[0].product.repositoryId}"/>
    </c:if>
  
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param name="array" param="choices"/>
    <dsp:param name="elementName" value="gwpChoice"/>
    <dsp:oparam name="outputStart">
      <ul class="giftSelect">
    </dsp:oparam>
    <dsp:oparam name="outputEnd">
      </ul>
    </dsp:oparam>
    <dsp:oparam name="output">
    
    <dsp:getvalueof var="choice" param="gwpChoice"/>
    <dsp:getvalueof var="product" param="gwpChoice.product"/>
    <dsp:tomap var="productItemMap" value="${product}"/>
    <dsp:tomap var="image" value="${productItemMap.smallImage}"/>
    <c:set var="skus" value="${choice.skus}"/>
    
    
      <li>
      <div class="productContainer" id="${product.repositoryId}">
        <a href="#"><img src="${image.url}" class="productImage" width="150" height="150"/></a>
        <div class="productInfo">
          <div class="productTitle">${fn:escapeXml(productItemMap.displayName)}<span class="productID atg-csc-base-spacing-one-left">${fn:escapeXml(product.repositoryId)}</span></div>
          <p class="productDescription">${fn:escapeXml(productItemMap.description)}<p>
        </div>
      </div>
    

      <!-- if the product only has one sku then preselect it-->
      <c:set var="preSelectedSkuId" value="${null}"/>  
      <c:if test="${fn:length(skus) == 1}">
        <dsp:getvalueof var="preSelectedSkuId" value="${skus[0].repositoryId}"/>
      </c:if>
      
      
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" value="${skus}"/>
      <dsp:param name="elementName" value="sku"/>
      <dsp:oparam name="outputStart">
        <table class="atg_dataTable" width="100%">
          <tr>
          <th class="giftChoose">&nbsp;</th>
          <th><fmt:message key="gwp.selectGift.popup.column.sku" /></th>
          <th><fmt:message key="gwp.selectGift.popup.column.name" /></th>
          <th class="atg_numberValue"><fmt:message key="gwp.selectGift.popup.column.price" /></th>
          <th><fmt:message key="gwp.selectGift.popup.column.status" /></th>
          </tr>
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
        <tr>
        <td class="giftActions" colspan="5">
          <input value="<fmt:message key="gwp.selectGift.popup.button.addToCart" />" type="button" disabled
            name="${product.repositoryId}_submitGWP" id="${product.repositoryId}_submitGWP"
            onClick="submitSelection('${product.repositoryId}');return false;">&nbsp; 
  
          <input type="button"
            name="${product.repositoryId}_cancelGWPSelection" value="<fmt:message key="gwp.selectGift.popup.button.cancel" />"
            onClick="closeGWPSelectionPopup(); return false;" />
        </td>
        </tr>
        </table>
      </dsp:oparam>
      <dsp:oparam name="output">

        <dsp:tomap var="skuItemMap" param="sku"/>
        <tr>
          <td class="giftChoose">
          <input type="radio" ${(!empty preSelectedSkuId && skuItemMap.repositoryId == preSelectedSkuId) ? 'checked' : ''} onClick="enableAddToCart('${product.repositoryId}');" value="${skuItemMap.repositoryId}"  name="${product.repositoryId}_radio"/></td>
          <td>${fn:escapeXml(skuItemMap.repositoryId)}</td>
          <td>${fn:escapeXml(skuItemMap.displayName)}</td>
          <td class="atg_numberValue"><dsp:include src="/include/catalog/displaySkuPrice.jsp" otherContext="${CSRConfigurator.contextRoot}"></dsp:include></td>
          <td><csr:inventoryStatus commerceItemId="${skuItemMap.repositoryId}"/></td>
        </tr>
        
        
        
      </dsp:oparam>
      </dsp:droplet><%-- End ForEach sku--%>
      
      </li>
    </dsp:oparam>
    </dsp:droplet><%-- End ForEach choice--%>
      
     
     
  </dsp:oparam>
  </dsp:droplet><%-- End GiftWithPurchaseSelectionChoicesDroplet --%>
  
  
  </dsp:layeredBundle>
  
  <script type="text/javascript">
    dojo.addOnLoad(function(){
      

      //this auto-expands if there is only on product
      if(dojo.query(".giftSelect li .atg_dataTable").length < 2){
        dojo.query(".atg_dataTable").forEach(
             function(node, index, array){
             dojo.style(node,"display","table");
        });
      }
      else {
      dojo.query(".giftSelect li .atg_dataTable").forEach(
         function(node, index, array){
           dojo.style(node,"display","none");
         }
      );  
      }  
        
      dojo.query(".productContainer").forEach(
         function(node, index, array){
           var containerNode = node;
           console.debug("Adding Table Show Trigger Behavior to: ", node);
           dojo.connect(node, 'onclick', null, function(evt){
               dojo.query('.atg_dataTable', node.parentNode).forEach(function(node,index,array){
                 if(dojo.style(node,"display")=="table"){
                 dojo.style(node,"display","none"); 
                 } else {
                   dojo.query(".giftSelect .atg_dataTable").forEach(
                       function(node, index, array){
                         dojo.style(node,"display","none");
                       }
                    );
                   
                   //when a product is picked, we enable add to cart right away if a sku is already selected. 
                   
                   enableAddWhenSkuSelected(containerNode.id);
                   
                   dojo.style(node,"display","table"); 
                 }
               });
               
           });
         }
      );
       
    atg.commerce.csr.gwp.setGWPGiftSelectionFormInputValues({giftHashCode:'${giftHashCode}',
                      promotionId:'${promotionId}',
                      quantity: '${quantity}', 
                      currentSelectedItemId: '${itemId }'});
    
    
    <c:if test="${!empty singleChoiceProductId}">
      enableAddToCart('${singleChoiceProductId}');
    </c:if>

   }); 

  </script>
  

  

</dsp:page>
<%-- @version $Id: //application/DCS-CSR-UI/version/11.2/src/web-apps/DCS-CSR-UI/include/gwp/selectGiftPopup.jsp#1 $$Change: 946917 $--%>
