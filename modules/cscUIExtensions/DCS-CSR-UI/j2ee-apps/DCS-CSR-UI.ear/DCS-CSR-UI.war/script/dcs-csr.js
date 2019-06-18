dojo.provide("atg.commerce.csr.cart");
atg.commerce.csr.cart={deleteCartItem:function(_1){
dojo.byId("removeCommerceId").value=_1;
atgSubmitAction({form:document.getElementById("deleteItemForm"),panelStack:"cmcShoppingCartPS"});
},deleteManualAdjustment:function(_2,_3){
dojo.byId("dmaId").value=_2;
dojo.byId("dmaReason").value=_3;
atgSubmitAction({form:document.getElementById("dma"),panelStack:"cmcShoppingCartPS"});
},submitNextAction:function(){
atg.commerce.csr.common.setPropertyOnItems(["modifyOrderSubmitter"],"disabled",true);
atg.commerce.csr.common.setPropertyOnItems(["moveToPurchaseInfoSubmitter"],"disabled",false);
atgSubmitAction({queryParams:{"init":"true"},form:document.getElementById("itemsForm")});
},updatePrice:function(){
atg.commerce.csr.common.setPropertyOnItems(["moveToPurchaseInfoSubmitter"],"disabled",true);
atg.commerce.csr.common.setPropertyOnItems(["modifyOrderSubmitter"],"disabled",false);
atgSubmitAction({panelStack:["cmcShoppingCartPS","globalPanels"],form:document.getElementById("itemsForm"),sync:true});
},saveAction:function(){
atg.commerce.csr.common.setPropertyOnItems(["moveToPurchaseInfoSubmitter"],"disabled",true);
atg.commerce.csr.common.setPropertyOnItems(["modifyOrderSubmitter"],"disabled",false);
atgSubmitAction({panelStack:["cmcShoppingCartPS","globalPanels"],form:document.getElementById("itemsForm")});
},skuChangePopupOkSelected:function(_4,_5,_6,_7,_8){
var _9=atg.commerce.csr.common.getEnclosingForm(_6);
if("apply"==_4){
atg.commerce.csr.common.submitPopup(_5,_9,atg.commerce.csr.common.getEnclosingPopup(_6));
}else{
if("return"==_4){
_9=dojo.byId("productSkuForm-editSKU-return");
var _a=atg.commerce.csr.common.getCheckedItem(_9[_7]);
if(_a!=""){
_a=_a.value;
}
atg.commerce.csr.common.hidePopupWithReturn(_8,{result:"ok",sku:_a});
}
}
},adjustmentAmountChanged:function(_b){
var _c=dojo.byId("amountTxt").value;
dojo.byId("adjustmentSubmitButton").disabled=!_c.match(_b);
},deleteCartItemByRelationalshipId:function(_d){
dojo.byId("removeRelationshipId").value=_d;
atgSubmitAction({form:document.getElementById("deleteItemByRelationshipIdForm"),panelStack:"cmcShoppingCartPS"});
},setOrderByRelationshipIdForm:function(){
var _e;
var _f;
var _10=document.getElementById("setOrderByRelationshipIdForm");
var _11=dojo.query(".ciRelationship");
var _12=false;
var _13=document.getElementById("setOrderByRelationshipIdFormInnerDiv");
if(!_13){
var _13=document.createElement("div");
_13.id="setOrderByRelationshipIdFormInnerDiv";
_10.appendChild(_13);
}else{
_13.innerHTML="";
}
for(i=0;i<_11.length;i++){
var _14=dojo.byId(_11[i].id+"_quantity");
if(_11[i].value!=0){
if(_14&&_14.value!=_11[i].value){
var _15=atg.commerce.csr.catalog.createInputFieldWithoutId(_11[i].id,"hidden");
_15.value=_11[i].value;
_12=true;
_13.appendChild(_15);
}
}else{
atg.commerce.csr.cart.deleteCartItemByRelationalshipId(_11[i].id);
}
_f="IPO:"+_11[i].name;
_e=document.getElementById(_f);
if(_e){
_13.appendChild(_e);
}
}
atgSubmitAction({form:_10,panelStack:"cmcShoppingCartPS"});
}};
dojo.provide("atg.commerce.csr.catalog");
dojo.require("dojo.date.locale");
dojo.require("dojox.i18n.currency");
dojo.require("dojo.currency");
dojo.require("dojo.string");
atg.commerce.csr.catalog={addExchangeItemToOrder:function(_16,_17,_18,_19,_1a,_1b){
var _1c=dojo.byId("buyForm");
if(_1c){
if(_1a&&_1a!=""){
_1c.successURL.value=_1a;
}
if(_1b&&_1b!=""){
_1c.errorURL.value=_1b;
}
_1c.quantity.value=1;
this._addVerifiedItemToOrder(_1c,_16,_17,_18,_19);
}
},addItemToOrder:function(_1d,_1e,_1f,_20,_21,_22,_23){
var _24=dojo.byId("buyForm");
if(typeof _22==="undefined"){
_22=false;
}
if(_24){
var _25=document.getElementById("itemQuantity"+_1d);
if(_25&&_25.value!=""){
var _26=dojo.number.parse(_25.value);
if(_22==="true"){
dojo.debug("addItemToOrder: A fractional quantity was added for SKU: "+_1e+" quantity: "+_26);
_24.quantityWithFraction.value=_26;
_24.quantity.value=0;
}else{
dojo.debug("addItemToOrder: Quantity added for SKU: "+_1e+" quantity: "+_26);
_24.quantity.value=_26;
_24.quantityWithFraction.value=0;
}
this._addVerifiedItemToOrder(_24,_1e,_1f,_20,_21);
_25.value="";
}else{
dojo.debug("addItemToOrder: User did not specify a value for the amount to add to the cart");
atg.commerce.csr.catalog.showError(_23);
}
}
},_addVerifiedItemToOrder:function(_27,_28,_29,_2a,_2b){
_27.catalogRefId.value=_28;
_27.productId.value=_29;
if(_2b){
_27.siteId.value=_2b;
}
_27["atg.successMessage"].value=_2a;
var _2c=atgSubmitAction({form:_27});
_2c.addCallback(function(){
atg.progress.update("cmcCatalogPS");
});
},onCloseHandler_impl:function(_2d){
if(window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls){
return;
}
window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls=true;
if(_2d.result=="ok"){
var _2e=window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj;
var _2f=window.atg_commerce_csr_catalog_OnCloseHandler_rowId;
var _30=atg.commerce.csr.catalog.getRowIndexByRowId(_2f);
var _31=new Array();
for(var ii=0;ii<_2d.product.skus.length;++ii){
var qty=dojo.string.trim(_2d.product.skus[ii].quantity);
if(qty>0){
_2e.skus[ii].quantity=qty;
_31.push(_2e.skus[ii]);
}
}
atg.commerce.csr.catalog.insertSkusIntoTable(_31,_2f,_30,_2d.product.id);
window.atg_commerce_csr_catalog_OnCloseHandler_rowId=null;
window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj=null;
}else{
atg.commerce.csr.catalog.rememberProductId("");
}
var _32=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
if(_32){
var ind=atg.commerce.csr.catalog.getRowIdFromTr(_32.rows[_32.rows.length-1]);
if(document.getElementById("atg_commerce_csr_catalog_productId"+ind)){
document.getElementById("atg_commerce_csr_catalog_productId"+ind).focus();
}
if(_2d.result!="ok"){
document.getElementById("atg_commerce_csr_catalog_productId"+ind).value="";
}
}
},isShowProductEntryField:function(){
return document.getElementById("atg_commerce_csr_catalog_showProductEntryField").value;
},isShowSKUEntryField:function(){
return document.getElementById("atg_commerce_csr_catalog_showSKUEntryField").value;
},isMultiSiteEnabled:function(){
return document.getElementById("atg_commerce_csr_catalog_isMultiSiteEnabled").value==="true";
},insertSkusIntoTable:function(_33,_34,_35,_36){
var _37=false;
var _38=atg.commerce.csr.catalog.isLastRowIndex(_35);
for(var ii=0;ii<_33.length;++ii){
if(_37){
_34=atg.commerce.csr.catalog.addProductsByIdAddRow(_35);
}else{
_37=true;
}
var _39=_33[ii].productId?_33[ii].productId:_36;
var _3a=_33[ii].productSiteId;
atg.commerce.csr.catalog.displaySKU(_33[ii],_34,_39,_3a);
document.getElementById("atg_commerce_csr_catalog_productId"+_34).value=_39;
++_35;
}
if(_38&&_33.length>0){
atg.commerce.csr.catalog.addProductsByIdAddRow();
}
},getOnCloseHandler:function(_3b,_3c,_3d){
window.atg_commerce_csr_catalog_OnCloseHandler_ignoreCalls=false;
if(_3d){
window.atg_commerce_csr_catalog_OnCloseHandler_rowId=_3c;
}else{
var _3e=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _3f=atg.commerce.csr.catalog.getRowIdFromTr(_3e.rows[_3e.rows.length-1]);
window.atg_commerce_csr_catalog_OnCloseHandler_rowId=_3f;
}
window.atg_commerce_csr_catalog_OnCloseHandler_productSkusObj=_3b;
return atg.commerce.csr.catalog.onCloseHandler_impl;
},editLineItemOnCloseHandler:function(_40,_41){
if(_40.result=="ok"){
atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("sku",_40.sku,_41);
}
},showProductViewPopup:function(_42,_43,_44,_45){
var _46=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_siteId"+_44).value);
if(!_46){
_46=_42.productSiteId;
}
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"atg_commerce_csr_catalog_productQuickViewPopup",url:atg.commerce.csr.catalog.getProductQuickViewURL()+_43+"&siteId="+_46,onClose:atg.commerce.csr.catalog.getOnCloseHandler(_42,_44,_45),title:document.getElementById("atg_commerce_csr_catalog_addProducts").value});
},showProductView:function(_47,_48,_49){
var _4a=document.getElementById("selectTabbedPanel");
if(_4a){
atgSubmitAction({form:_4a,selectTabbedPanels:["cmcProductViewP"],queryParams:{productId:_47,skuId:_48}});
}
},showProductViewInSiteContext:function(_4b,_4c,_4d){
if(_4c&&_4c!=_4d){
var _4e=dojo.byId("atg_commerce_csr_productDetailsForm");
atgSubmitAction({form:_4e,sync:true,queryParams:{productId:_4b},selectTabbedPanels:["cmcProductViewP"],panelStack:["cmcCatalogPS","globalPanels"]});
_4e=document.getElementById("atg_commerce_csr_productDetailsForm");
if(_4e){
atgSubmitAction({form:_4e,panelStack:["cmcCatalogPS","globalPanels"],queryParams:{contentHeader:true,siteId:_4c}});
}
}else{
var _4e=dojo.byId("atg_commerce_csr_productDetailsForm");
atgSubmitAction({form:_4e,queryParams:{productId:_4b},selectTabbedPanels:["cmcProductViewP"],panelStack:["cmcCatalogPS","globalPanels"]});
}
},showProductCatalog:function(_4f,_50,_51){
var _52=document.getElementById("selectTabbedPanel");
if(_52){
atg.commerce.csr.catalog.setCatalogInfo(true);
if(_4f&&(_50!=null)&&_51){
atg.commerce.csr.catalog.setTreeInfo(_50+"$div$category_div_"+_4f,atg.commerce.csr.catalog.createInfo(_4f,_50,_51),"category");
}
atgSubmitAction({sync:true,form:_52,selectTabbedPanels:["cmcProductCatalogBrowseP"]});
}
},onNewProductSearch:function(){
var sku=document.getElementById("sku");
if(sku){
sku.value="";
}
var _53=document.getElementById("productID");
if(_53){
_53.value="";
}
var _54=document.getElementById("itemPrice");
if(_54){
_54.value="";
}
var _55=document.getElementById("searchInput");
if(_55){
_55.value="";
}
var _56=document.getElementById("priceRelation");
if(_56){
_56.selectedIndex=0;
}
var _57=document.getElementById("siteSelect");
if(_57){
_57.selectedIndex=0;
}
var _58=document.getElementById("categorySelect");
if(_58){
_58.selectedIndex=0;
_58.disabled="disabled";
}
window.catalogInfo=null;
},createInfo:function(_59,_5a,_5b){
var _5c="atgasset:/CustomProductCatalog/category/";
var _5d="atgasset:/ProductCatalog/category/";
var _5e={};
if(_5b=="true"){
_5e.URI=_5c+_59;
}else{
_5e.URI=_5d+_59;
}
if(_5a==""){
_5e.path="$div$category_div_"+_59;
}else{
_5e.path=_5a;
}
return _5e;
},nodeClicked:function(_5f,_60,_61){
var _62="atgasset:/CustomProductCatalog/category/";
var _63="atgasset:/ProductCatalog/category/";
var _64=atg.commerce.csr.catalog.createInfo(_5f,_60,_61);
atg.commerce.csr.catalog.getTree().nodeSelected(_60+"$div$category_div_"+_5f,_64);
if(_60!=""){
var _65=atg.commerce.csr.catalog.getTreeNodeCategoryId(_60);
var _66={};
if(_61){
_66.URI=_62+_65;
}else{
_66.URI=_63+_65;
}
_66.path=_60;
atg.commerce.csr.catalog.getTree().openNode(_60);
}
},nodeSelected:function(_67,_68){
if(atg.commerce.csr.catalog.getTreeInfo().path&&atg.commerce.csr.catalog.getTreeInfo().path!=_67&&atg.commerce.csr.catalog.getTreeFrame().getElementById("node_"+atg.commerce.csr.catalog.getTreeInfo().path)){
atg.commerce.csr.catalog.getTreeFrame().getElementById("node_"+atg.commerce.csr.catalog.getTreeInfo().path).className="treeNode";
}
var _69=atg.commerce.csr.catalog.getCommaSeparatedTreePath(_67);
atg.commerce.csr.catalog.setTreeInfo(_67,_68,"category");
var _6a=document.getElementById("selectTreeNode");
if(_6a){
_6a.hierarchicalCategoryId.value=atg.commerce.csr.catalog.getTreeNodeCategoryId(_67);
_6a.path.value=_69;
atgSubmitAction({form:_6a,sync:true});
atg.commerce.csr.catalog.reloadSubCategoriesList(_6a.hierarchicalCategoryId.value,_69);
atg.commerce.csr.catalog.productCatalogPagedData.formId="selectTreeNode";
atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();
}
atg.commerce.csr.catalog.setCatalogInfo(true);
},onCatalogBrowseSearch:function(_6b){
var _6c=document.getElementById("searchByProductForm");
if(_6c){
atg.commerce.csr.catalog.setCatalogInfo(false,_6c);
if(_6c.itemPrice&&_6c.priceRelation){
var _6d=_6c.itemPrice.value;
if(_6c.priceRelation.value==" "){
_6c.itemPrice.value="";
}
}
if(_6c.productID){
_6c.productID.value=dojo.string.trim(_6c.productID.value);
}
if(_6c.sku){
_6c.sku.value=dojo.string.trim(_6c.sku.value);
}
}
atg.commerce.csr.catalog.productCatalogPagedData.formId="searchByProductForm";
atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();
if(dojo.byId("allHierarchicalCategoryId")&&dojo.byId("allHierarchicalCategoryId").checked){
var _6e=atg.commerce.csr.catalog.createInfo("","");
_6e.URI="";
atg.commerce.csr.catalog.getTree().nodeSelected("",_6e);
atg.commerce.csr.catalog.reloadSubCategoriesList("","");
}
},catalogSearch:function(_6f){
var _70=document.getElementById("searchByProductForm");
if(_70){
atg.commerce.csr.catalog.setCatalogInfo(false,_70);
if(_70.itemPrice&&_70.priceRelation){
var _71=_70.itemPrice.value;
if(_70.priceRelation.value==" "){
_70.itemPrice.value="";
}
}
if(_70.sitesSelectValue){
var _72=document.getElementById("siteSelect");
if(_72){
_70.sitesSelectValue.value=_72.options[_70.siteSelect.selectedIndex].value;
}
}
if(_70.hierarchicalCategoryId){
var _73=document.getElementById("categorySelect");
if(_73){
_70.hierarchicalCategoryId.value=_73.options[_70.categorySelect.selectedIndex].value;
}
}
if(_70.productID){
_70.productID.value=dojo.string.trim(_70.productID.value);
}
if(_70.sku){
_70.sku.value=dojo.string.trim(_70.sku.value);
}
}
atg.commerce.csr.catalog.productCatalogPagedData.formId="searchByProductForm";
atg.commerce.csr.catalog.productCatalog.searchRefreshGrid();
},reloadSubCategoriesList:function(_74,_75){
dojo.xhrGet({url:atg.commerce.csr.getContextRoot()+"/include/catalog/subCategoriesList.jsp?categoryId="+_74+"&path="+_75+"&_windowid="+window.windowId,encoding:"utf-8",handle:function(_76,_77){
if(!(_76 instanceof Error)){
var _78=document.getElementById("atg_commerce_csr_catalog_subCategoriesListContainer");
_78.innerHTML=_76;
}
},mimetype:"text/html"});
},selectCatalog:function(_79){
var _7a=document.getElementById("setCatalogForm");
if(_7a){
_7a.catalogId.value=_79;
atgSubmitAction({form:_7a,selectTabbedPanels:["cmcProductCatalogSearchP"],sync:true});
}
},searchForCatalogs:function(_7b){
var _7c=dojo.date.locale.format(dojo.date.add(new Date(),"year",19),{datePattern:_7b,selector:"date"});
var _7d=dojo.date.locale.format(new Date(-1,0,1),{datePattern:_7b,selector:"date"});
var _7e=document.getElementById("moreCatalogsStartDateInput").value;
var _7f=document.getElementById("moreCatalogsEndDateInput").value;
if(_7e!=""&&_7e!=_7b){
if(!dojo.date.locale.parse(_7e,{datePattern:_7b,selector:"date"})){
document.getElementById("moreCatalogsStartDate").value=_7c;
}else{
document.getElementById("moreCatalogsStartDate").value=_7e;
}
}else{
document.getElementById("moreCatalogsStartDate").value="";
}
if(_7f!=""&&_7f!=_7b){
if(!dojo.date.locale.parse(_7f,{datePattern:_7b,selector:"date"})){
document.getElementById("moreCatalogsEndDate").value=_7d;
}else{
var _80=dojo.date.add(dojo.date.locale.parse(_7f,{datePattern:_7b,selector:"date"}),"day",1);
document.getElementById("moreCatalogsEndDate").value=dojo.date.locale.format(_80,{datePattern:_7b,selector:"date"});
}
}else{
document.getElementById("moreCatalogsEndDate").value="";
}
atg.commerce.csr.catalog.moreCatalogs.searchRefreshGrid();
},selectSite:function(_81,_82){
var _83=document.getElementById("siteSelect");
if(_83){
dojo.xhrGet({sync:true,url:atg.commerce.csr.getContextRoot()+"/include/catalog/getCategoriesForSite.jsp",content:{siteId:_83.options[_83.selectedIndex].value,_windowid:window.windowId},encoding:"utf-8",handle:function(_84,_85){
if(!(_84 instanceof Error)){
var _86=atg.commerce.csr.catalog.createObjectFromJSON(_84);
var _87=document.getElementById("categorySelect");
_87.length=0;
_87.options[0]=new Option(_81,"");
if(_83.selectedIndex==0||_86==null||_86.categories.length==0){
_87.disabled="disabled";
}else{
_87.disabled="";
var _88=_86.categories.length;
for(i=0;i<_88;i++){
var _89=_86.categories[i];
_87.options[i+1]=new Option(_89.name,_89.id);
}
}
if(_82){
_87.selectedIndex=_82;
}
}else{
atg.commerce.csr.catalog.showDojoIoBindError(_84,_85);
}
return _84;
},mimetype:"text/plain"});
}
},createInputField:function(_8a,_8b,_8c){
var _8d=document.createElement("input");
if(_8c){
_8d.type=_8c;
}else{
_8d.type="text";
}
_8d.id=_8a;
_8d.name=_8b;
return _8d;
},createInputFieldWithoutId:function(_8e,_8f){
return atg.commerce.csr.catalog.createInputField("",_8e,_8f);
},getNextRowId:function(){
var _90=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _91=new Array;
for(var _92=0;_92<_90.rows.length;_92++){
_91[_92]=atg.commerce.csr.catalog.getRowIdFromTr(_90.rows[_92]);
}
_91.sort(function(a,b){
return a-b;
});
for(var _93=0;_93<_91.length;_93++){
if(_93<_91[_93]){
return _93;
}
}
return _91.length;
},getNextRowIndex:function(){
var _94=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _95=_94.rows.length;
return _95;
},generateTrId:function(_96){
return "atg_commerce_csr_catalog_addProductsByIdTr"+_96;
},getRowIdFromTr:function(_97){
return parseInt(_97.id.replace(/^atg_commerce_csr_catalog_addProductsByIdTr/,""));
},addProductsByIdAddRow:function(_98){
var _99=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _9a=atg.commerce.csr.catalog.getNextRowId();
var _9b=_98?_98:atg.commerce.csr.catalog.getNextRowIndex();
_99.insertRow(_9b);
atg.commerce.csr.catalog.getPanelProducts().splice(_9b-1,0,null);
var _9c=_99.rows[_9b];
if(_9c){
_9c.id=atg.commerce.csr.catalog.generateTrId(_9a);
var _9d=0;
if(atg.commerce.csr.catalog.isShowProductEntryField()!="false"){
var _9e=_9c.insertCell(_9d);
_9e.id="productCell"+_9a;
_9e.className="atg_numberValue";
var _9f=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_productId"+_9a,"atg_commerce_csr_catalog_productId"+_9a);
_9f.size="10";
_9f.onblur=function(){
atg.commerce.csr.catalog.loadProductByProductIdTabOut(_9f,_9a);
};
_9f.onfocus=function(){
atg.commerce.csr.catalog.onFocusProductId(_9f);
};
_9f.onchange=function(){
atg.commerce.csr.catalog.onProductRowChanged(_9a);
};
_9e.appendChild(_9f);
}else{
var _9e=_9c.insertCell(_9d);
_9e.id="productCell"+_9a;
_9e.className="atg_numberValue";
_9e.style.display="none";
var _9f=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_productId"+_9a,"atg_commerce_csr_catalog_productId"+_9a,"hidden");
_9e.appendChild(_9f);
}
var _a0=document.createElement("div");
_a0.id="atg_commerce_csr_catalog_productNotFoundWarning"+_9a;
_9e.appendChild(_a0);
_9d+=1;
if(atg.commerce.csr.catalog.isShowSKUEntryField()!="false"){
var _a1=_9c.insertCell(_9d);
_a1.id="skuCell"+_9a;
_a1.className="atg_numberValue";
var _a2=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_skuId"+_9a,"atg_commerce_csr_catalog_skuId"+_9a);
_a2.size="10";
_a2.onblur=function(){
atg.commerce.csr.catalog.loadProductBySkuIdTabOut(_a2,_9a);
};
_a2.onfocus=function(){
atg.commerce.csr.catalog.onFocusSkuId(_a2);
};
_a2.onchange=function(){
atg.commerce.csr.catalog.onProductRowChanged(_9a);
};
_a1.appendChild(_a2);
}else{
var _a1=_9c.insertCell(_9d);
_a1.id="skuCell"+_9a;
_a1.className="atg_numberValue";
_a1.style.display="none";
var _a2=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_skuId"+_9a,"atg_commerce_csr_catalog_skuId"+_9a);
_a1.appendChild(_a2);
}
var _a0=document.createElement("div");
_a0.id="atg_commerce_csr_catalog_skuNotFoundWarning"+_9a;
_a1.appendChild(_a0);
_9d+=1;
var _a3=_9c.insertCell(_9d);
_a3.id="quantityCell"+_9a;
_a3.className="atg_numberValue";
var _a4=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_isFractional"+_9a,"atg_commerce_csr_catalog_isFractional"+_9a,"hidden");
var _a5=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_qty"+_9a,"atg_commerce_csr_catalog_qty"+_9a);
_a5.size=document.getElementById("atg_commerce_csr_catalog_quantityInputTagSize").value;
_a5.maxLength=document.getElementById("atg_commerce_csr_catalog_quantityInputTagMaxlength").value;
_a5.onchange=function(){
atg.commerce.csr.catalog.onProductRowChanged(_9a);
};
var _a6=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_salePrice"+_9a,"atg_commerce_csr_catalog_salePrice"+_9a,"hidden");
var _a7=atg.commerce.csr.catalog.createInputField("atg_commerce_csr_catalog_siteId"+_9a,"atg_commerce_csr_catalog_siteId"+_9a,"hidden");
_a3.appendChild(_a5);
_a3.appendChild(_a7);
_a3.appendChild(_a6);
_a3.appendChild(_a4);
_9d+=1;
var _a8=_9c.insertCell(_9d);
_a8.id="atg_commerce_csr_catalog_productName"+_9a;
_9d+=1;
var _a9=_9c.insertCell(_9d);
_a9.id="atg_commerce_csr_catalog_skuStatus"+_9a;
_9d+=1;
var _aa=_9c.insertCell(_9d);
_aa.id="atg_commerce_csr_catalog_priceEach"+_9a;
_aa.className="atg_numberValue";
_9d+=1;
var _ab=_9c.insertCell(_9d);
_ab.id="atg_commerce_csr_catalog_priceTotal"+_9a;
_ab.className="atg_numberValue";
_9d+=1;
var _ac=_9c.insertCell(_9d);
_ac.id="atg_commerce_csr_catalog_editCell"+_9a;
_ac.className="atg_iconCell";
_9d+=1;
var _ad=_9c.insertCell(_9d);
_ad.id="atg_commerce_csr_catalog_deleteCell"+_9a;
_ad.className="atg_iconCell";
}
var _ae=document.getElementById("atg_commerce_csr_catalog_addToShoppingButton");
if(_ae&&atg.commerce.csr.catalog.isOrderModifiable()){
_ae.disabled=false;
}
return _9a;
},isOrderModifiable:function(){
var _af=document.getElementById("atg_commerce_csr_catalog_isOrderModifiableHiddenValue");
return (_af&&_af.value!="false");
},setDisplayedProductName:function(_b0,_b1,_b2,_b3){
var _b4=document.getElementById("atg_commerce_csr_catalog_productName"+_b2);
var _b5="";
if(atg.commerce.csr.catalog.isMultiSiteEnabled()){
_b5="<img src=\""+_b1.siteIconURL+"\" title=\""+_b1.siteIconHover+"\"/> ";
}
_b4.innerHTML=_b5+"<a href=\"#\" onclick=\"atg.commerce.csr.catalog.showAddProductPopup('"+_b0+"', '"+_b2+"');return false;\">"+_b1.displayName+"</a>";
if(_b1.ageRestriction!=null&&_b1.ageRestriction!=""){
_b4.innerHTML=_b4.innerHTML+"<br/><span style=\"font-weight:bold\"><font  color=\"FF0000\">"+_b1.ageRestriction+"</font></span>";
}
if(_b1.fulfillmentMethod!=null&&_b1.fulfillmentMethod!=""){
_b4.innerHTML=_b4.innerHTML+"<br/><span style=\"font-weight:bold\"><font  color=\"0000FF\">"+_b1.fulfillmentMethod+"</font></span>";
}
},displaySKU:function(_b6,_b7,_b8,_b9){
var _ba=document.getElementById("atg_commerce_csr_catalog_skuId"+_b7);
var _bb=document.getElementById("atg_commerce_csr_catalog_skuStatus"+_b7);
var _bc=document.getElementById("atg_commerce_csr_catalog_priceEach"+_b7);
var _bd=document.getElementById("atg_commerce_csr_catalog_priceTotal"+_b7);
var _be=document.getElementById("atg_commerce_csr_catalog_salePrice"+_b7);
var _bf=document.getElementById("atg_commerce_csr_catalog_editCell"+_b7);
var _c0=document.getElementById("atg_commerce_csr_catalog_deleteCell"+_b7);
var _c1=document.getElementById("atg_commerce_csr_catalog_siteId"+_b7);
var _c2=atg.commerce.csr.catalog.getRowIndexByRowId(_b7);
_b6.productId=_b8;
atg.commerce.csr.catalog.getPanelProducts().splice(_c2-1,1,_b6);
_ba.value=_b6.skuId;
_bb.innerHTML=_b6.skuStatus;
if(_b6.skuStatusCssClass){
_bb.className=_b6.skuStatusCssClass;
}
if(_b6.skuDiscountedPrice&&_b6.skuDiscountedPrice!=""&&_b6.skuDiscountedPrice!=_b6.skuPriceEach){
_bc.innerHTML=_b6.skuDiscountedPriceFormatted+"<br><div class='oldPrice'>"+_b6.skuPriceEachFormatted+"</div>";
_be.value=_b6.skuDiscountedPrice;
}else{
_bc.innerHTML=_b6.skuPriceEachFormatted;
_be.value=_b6.skuPriceEach;
}
var _c3=document.getElementById("atg_commerce_csr_catalog_productId"+_b7);
if(_c3&&_c3.value==""&&_b8){
_c3.value=_b8;
}
if(_b6.quantity!=null){
var _c4=document.getElementById("atg_commerce_csr_catalog_qty"+_b7);
_c4.value=_b6.quantity;
}
if(_b6.fractionalQuantitiesAllowed!=null){
var _c5=document.getElementById("atg_commerce_csr_catalog_isFractional"+_b7);
_c5.value=_b6.fractionalQuantitiesAllowed;
}
if(_b9!=null&&_c1!=null){
_c1.value=_b9;
}
atg.commerce.csr.catalog.updateTotalPrice(_b7);
if(atg.commerce.csr.catalog.isShowSKUEntryField()!="false"){
var _c6=document.createElement("a");
_c6.href="#";
_c6.className="atg_tableIcon atg_propertyEdit";
var _c7=new Object();
_c7.skus=new Array(1);
_c7.skuCount=1;
_c7.skus[0]=_b6;
var _c8=document.getElementById("atg_commerce_csr_catalog_productEditLineItem").value;
var _c9=function(_ca){
atg.commerce.csr.catalog.editLineItemOnCloseHandler(_ca,_b7);
};
_c6.onclick=function(){
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"editLineItemPopup",url:""+_c8.replace("SKUIDPLACEHOLDER",_b6.skuId)+_b8,onClose:_c9,title:document.getElementById("atg_commerce_csr_catalog_editLineItem").value});
};
_c6.title=document.getElementById("atg_commerce_csr_catalog_editTooltip").value;
_c6.innerHTML=document.getElementById("atg_commerce_csr_catalog_edit").value;
_bf.innerHTML="";
_bf.appendChild(_c6);
}
var _cb=document.createElement("a");
_cb.href="#";
_cb.className="atg_tableIcon atg_propertyClear";
_cb.onclick=function(){
atg.commerce.csr.catalog.deleteProductRowByRowId(_b7);
};
_cb.title=document.getElementById("atg_commerce_csr_catalog_deleteTooltip").value;
_cb.innerHTML=document.getElementById("atg_commerce_csr_catalog_delete").value;
_c0.innerHTML="";
_c0.appendChild(_cb);
atg.commerce.csr.catalog.setDisplayedProductName(_b8,_b6,_b7,_b9);
},clearProduct:function(_cc,_cd){
document.getElementById("atg_commerce_csr_catalog_productName"+_cc).innerHTML="";
if(_cd!="productId"){
var _ce=document.getElementById("atg_commerce_csr_catalog_productId"+_cc);
if(_ce){
_ce.value="";
}
}
if(_cd!="skuId"){
var _ce=document.getElementById("atg_commerce_csr_catalog_skuId"+_cc);
if(_ce){
_ce.value="";
}
}
document.getElementById("atg_commerce_csr_catalog_siteId"+_cc).value="";
document.getElementById("atg_commerce_csr_catalog_qty"+_cc).value="";
document.getElementById("atg_commerce_csr_catalog_skuStatus"+_cc).innerHTML="";
document.getElementById("atg_commerce_csr_catalog_priceEach"+_cc).innerHTML="";
document.getElementById("atg_commerce_csr_catalog_salePrice"+_cc).value="";
document.getElementById("atg_commerce_csr_catalog_priceTotal"+_cc).innerHTML="";
document.getElementById("atg_commerce_csr_catalog_editCell"+_cc).innerHTML="";
document.getElementById("atg_commerce_csr_catalog_productNotFoundWarning"+_cc).innerHTML="";
document.getElementById("atg_commerce_csr_catalog_skuNotFoundWarning"+_cc).innerHTML="";
var _cf=atg.commerce.csr.catalog.getRowIndexByRowId(_cc);
atg.commerce.csr.catalog.getPanelProducts().splice(_cf-1,1,null);
},createObjectFromJSON:function(_d0){
if(_d0==null){
return null;
}
var _d1=_d0;
try{
_d1=dojo.string.trim(dojo.string.trim(_d0).replace(/^[\r\n\s]+/,""));
_d1=_d1.replace(/\s*[\r\n]+[\s\r\n]*/g,"\n");
var _d2=(_d1?eval("("+_d1+")"):null);
return _d2;
}
catch(err){
var _d3="convertObjectFromJSON(): error catched: \""+err+"\" for JSON:"+_d1;
atg.commerce.csr.catalog.showError(_d3);
return null;
}
},showDojoIoBindError:function(_d4,_d5){
var _d6="";
var _d7="dojo.xhrGet failed: error="+_d4;
atg.commerce.csr.catalog.showError(_d7);
},showError:function(_d8){
atg.commerce.csr.common.addMessageInMessagebar("error",_d8);
},verifyId:function(_d9){
return _d9!=null&&_d9.match(/^[\w\d-]+$/);
},getReadProductJsonURL:function(){
return document.getElementById("atg_commerce_csr_catalog_readProductJsonURL").value;
},getProductQuickViewURL:function(){
return document.getElementById("atg_commerce_csr_catalog_productQuickViewURL").value;
},getPanelProducts:function(){
var _da=document.getElementById("atg_commerce_csr_catalog_products").productData;
if(_da==null){
document.getElementById("atg_commerce_csr_catalog_products").productData=new Array(1);
_da=document.getElementById("atg_commerce_csr_catalog_products").productData;
_da[0]=null;
}
return _da;
},setDefaultQuantity:function(_db,_dc){
var _dd=document.getElementById("atg_commerce_csr_catalog_qty"+_db);
if(_dc==null||dojo.string.trim(_dc)==""){
_dd.value="1";
}else{
_dd.value=_dc;
}
},viewSkuDescription:function(_de,_df,_e0,_e1,_e2,_e3){
var _e4=document.getElementById("atg_commerce_csr_catalog_product_info_currSkuId");
var _e5=document.getElementById("atg_commerce_csr_catalog_product_info_image");
var _e6=document.getElementById("atg_commerce_csr_catalog_product_info_display_name");
var _e7=document.getElementById("atg_commerce_csr_catalog_product_info_repository_id");
var _e8=document.getElementById("atg_commerce_csr_catalog_product_info_description");
var _e9=document.getElementById("atg_commerce_csr_catalog_product_info_price");
if(_e4.value&&_e4.value==_de){
_e5.src=document.getElementById("atg_commerce_csr_catalog_product_info_product_image").value;
_e6.innerHTML=document.getElementById("atg_commerce_csr_catalog_product_info_product_display_name").value;
_e7.innerHTML=document.getElementById("atg_commerce_csr_catalog_product_info_product_repository_id").value;
_e8.innerHTML=document.getElementById("atg_commerce_csr_catalog_product_info_product_description").value;
if(_e9){
_e9.innerHTML=document.getElementById("atg_commerce_csr_catalog_product_info_product_price").innerHTML;
}
_e4.value="";
}else{
if(dojo.string.trim(_e2)!=""){
_e5.src=_e2;
}else{
_e5.src=document.getElementById("atg_commerce_csr_catalog_product_info_product_image").value;
}
_e6.innerHTML=_e0;
_e7.innerHTML=_de;
_e8.innerHTML=_e1;
if(!_e3||dojo.string.trim(_e3)==""){
_e3="productView";
}
var _ea=document.getElementById(_de+"-"+_df+"-"+_e3+"-price-td");
if(_ea){
if(_e9){
_e9.innerHTML=_ea.innerHTML;
}
}else{
if(_e9){
_e9.innerHTML="";
}
}
_e4.value=_de;
}
},asyncLoadAndDisplaySKU:function(_eb,_ec,_ed){
var _ee=_eb+"Id";
var _ef=document.getElementById("atg_commerce_csr_catalog_qty"+_ed).value;
atg.commerce.csr.catalog.clearProduct(_ed,_ee);
if(atg.commerce.csr.catalog.verifyId(_ec)){
dojo.xhrGet({sync:true,url:atg.commerce.csr.catalog.getReadProductJsonURL()+"&"+_ee+"="+_ec,encoding:"utf-8",handle:function(_f0,_f1){
if(!(_f0 instanceof Error)){
var _f2=atg.commerce.csr.catalog.createObjectFromJSON(_f0);
if(_f2==null||_f2.skuCount==null){
atg.commerce.csr.catalog.showNotFound(_eb,_ed);
}else{
if(_eb=="product"){
if(_f2.skuCount==1){
atg.commerce.csr.catalog.setDefaultQuantity(_ed,_ef);
atg.commerce.csr.catalog.displaySKU(_f2.skus[0],_ed,_f2.productId,_f2.skus[0].productSiteId);
atg.commerce.csr.catalog.rememberSkuId(_f2.skus[0].skuId);
atg.commerce.csr.catalog.onProductRowChanged(_ed);
document.getElementById("atg_commerce_csr_catalog_qty"+_ed).focus();
}else{
if(_f2.skuCount>=2){
atg.commerce.csr.catalog.showProductViewPopup(_f2,_ec,_ed,true);
}
}
}else{
if(_f2.skuCount!=1){
var _f3="sku not available for adding to cart";
atg.commerce.csr.catalog.showError(_f3);
}else{
atg.commerce.csr.catalog.setDefaultQuantity(_ed,_ef);
atg.commerce.csr.catalog.displaySKU(_f2.skus[0],_ed,_f2.productId,_f2.skus[0].productSiteId);
atg.commerce.csr.catalog.rememberProductId(_f2.productId);
atg.commerce.csr.catalog.onProductRowChanged(_ed);
document.getElementById("atg_commerce_csr_catalog_qty"+_ed).focus();
}
}
}
}else{
atg.commerce.csr.catalog.showDojoIoBindError(_f0,_f1);
}
return _f0;
},mimetype:"text/plain"});
}else{
atg.commerce.csr.catalog.showNotFound(_eb,_ed);
}
},showAddProductPopup:function(_f4,_f5){
if(atg.commerce.csr.catalog.verifyId(_f4)){
dojo.xhrGet({sync:true,url:atg.commerce.csr.catalog.getReadProductJsonURL()+"&productId="+_f4,encoding:"utf-8",handle:function(_f6,_f7){
if(!(_f6 instanceof Error)){
var _f8=atg.commerce.csr.catalog.createObjectFromJSON(_f6);
if(_f8==null||_f8.skuCount==null){
var _f9="ERROR: can not load product information for productId="+_f4;
atg.commerce.csr.catalog.showError(_f9);
}else{
atg.commerce.csr.catalog.showProductViewPopup(_f8,_f4,_f5,false);
}
}else{
atg.commerce.csr.catalog.showDojoIoBindError(_f6,_f7);
}
return _f6;
},mimetype:"text/plain"});
}
},loadProductBySkuIdTabOut:function(_fa,_fb){
var _fc=_fa.value;
if(_fc!=null){
_fc=dojo.string.trim(_fc);
}
var _fd=_fc!=document.getElementById("atg_commerce_csr_catalog_tmpSkuId").value;
var _fe=atg.commerce.csr.catalog.isShowProductEntryField()!="false"&&(_fd?false:dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId"+_fb).value)=="");
var _ff=_fc!=null&&dojo.string.trim(_fc)!=""&&(_fd||_fe);
if(_ff){
atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("sku",_fc,_fb);
}else{
if((_fc==null||dojo.string.trim(_fc)=="")&&_fd){
atg.commerce.csr.catalog.deleteProductRowByRowId(_fb);
}
}
},loadProductByProductIdTabOut:function(_100,_101){
var _102=_100.value;
if(_102!=null){
_102=dojo.string.trim(_102);
}
var _103=_102!=document.getElementById("atg_commerce_csr_catalog_tmpProductId").value;
var _104=atg.commerce.csr.catalog.isShowSKUEntryField()!="false"&&(_103?false:dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId"+_101).value)=="");
var _105=_102!=null&&dojo.string.trim(_102)!=""&&(_103||_104);
if(_105){
atg.commerce.csr.catalog.asyncLoadAndDisplaySKU("product",_102,_101);
}else{
if((_102==null||dojo.string.trim(_102)=="")&&_103){
atg.commerce.csr.catalog.deleteProductRowByRowId(_101);
}
}
},showNotFound:function(_106,_107){
document.getElementById("atg_commerce_csr_catalog_"+_106+"NotFoundWarning"+_107).innerHTML=document.getElementById("atg_commerce_csr_catalog_"+_106+"NotFoundError").value;
},getFilledRowsId:function(){
var _108=new Array();
var _109=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
if(_109){
var _10a=_109.rows.length-2;
var _10b=0;
for(ii=1;ii<=_10a;ii++){
_10b=atg.commerce.csr.catalog.getRowIdFromTr(_109.rows[ii]);
if(atg.commerce.csr.catalog.isRowFilledIn(_10b)){
_108.push(_10b);
}
}
}
return _108;
},addProductsByIdToShoppingCart:function(){
var _10c=document.getElementById("atg_commerce_csr_catalog_addProductsByIdForm");
if(_10c){
var _10d=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _10e=atg.commerce.csr.catalog.getFilledRowsId(true,true);
if(_10e.length>0){
var skus=new Array();
for(ii=0;ii<_10e.length;ii++){
skus[ii]=new Array();
skus[ii].siteIdToAdd=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_siteId"+_10e[ii]).value);
skus[ii].productIdToAdd=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId"+_10e[ii]).value);
skus[ii].skuIdToAdd=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId"+_10e[ii]).value);
skus[ii].qtyToAdd=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_qty"+_10e[ii]).value);
skus[ii].fractionalQuantitiesAllowed=dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_isFractional"+_10e[ii]).value);
}
document.getElementById("atg_commerce_csr_catalog_dontStore").value="1";
atg.commerce.csr.catalog.clearAddProductByIdData();
atg.commerce.csr.catalog.addProductsToShoppingCart(skus);
}
}
},addProductsToShoppingCartHandler:function(args,_10f,_110){
if(args.result=="ok"){
var _111=args.product;
var skus=new Array();
for(ii=0;ii<_111.skus.length;ii++){
skus[ii]=new Array();
skus[ii].siteIdToAdd=dojo.string.trim(_10f);
skus[ii].productIdToAdd=dojo.string.trim(_111.id);
skus[ii].skuIdToAdd=dojo.string.trim(_111.skus[ii].id);
skus[ii].qtyToAdd=dojo.string.trim(_111.skus[ii].quantity);
}
atg.commerce.csr.catalog.addProductsToShoppingCart(skus,_110);
}
},addProductsToShoppingCart:function(skus,_112){
dojo.xhrGet({url:atg.commerce.csr.getContextRoot()+"/include/catalog/addToCartForm.jsp?count="+skus.length+"&_windowid="+window.windowId,encoding:"utf-8",handle:function(_113,_114){
if(!(_113 instanceof Error)){
var _115=document.getElementById("atg_commerce_csr_catalog_addToCartContainer");
_115.innerHTML=_113;
var _116=1;
for(ii=0;ii<skus.length;ii++){
document.getElementById("atg_commerce_csr_catalog_siteIdToAdd"+_116).value=skus[ii].siteIdToAdd;
document.getElementById("atg_commerce_csr_catalog_productIdToAdd"+_116).value=skus[ii].productIdToAdd;
document.getElementById("atg_commerce_csr_catalog_skuIdToAdd"+_116).value=skus[ii].skuIdToAdd;
if(skus[ii].fractionalQuantitiesAllowed==="true"){
document.getElementById("atg_commerce_csr_catalog_qtyWithFractionToAdd"+_116).value=skus[ii].qtyToAdd;
}else{
document.getElementById("atg_commerce_csr_catalog_qtyToAdd"+_116).value=skus[ii].qtyToAdd;
}
_116=_116+1;
}
var _117;
if(_112==null){
landingPanelStacks="cmcShoppingCartPS";
}else{
landingPanelStacks=_112;
}
atgSubmitAction({form:document.getElementById("atg_commerce_csr_catalog_addToCartForm"),panelStack:[landingPanelStacks]});
}
},mimetype:"text/html"});
},verifyQty:function(_118){
var qty=document.getElementById("atg_commerce_csr_catalog_qty"+_118).value;
return qty&&qty!=""&&qty.match(/^[+-]?\d+(\.\d+)?$/)&&!isNaN(parseFloat(qty))&&parseFloat(qty)>0;
return true;
},isRowFilledIn:function(_119){
var _11a=(atg.commerce.csr.catalog.isShowSKUEntryField()=="false"||dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_skuId"+_119).value)!="")&&(atg.commerce.csr.catalog.isShowProductEntryField()=="false"||dojo.string.trim(document.getElementById("atg_commerce_csr_catalog_productId"+_119).value)!="")&&atg.commerce.csr.catalog.verifyQty(_119);
return _11a;
},updateTotalPrice:function(_11b){
var _11c=document.getElementById("atg_commerce_csr_catalog_qty"+_11b);
var _11d=document.getElementById("atg_commerce_csr_catalog_salePrice"+_11b);
var _11e=document.getElementById("atg_commerce_csr_catalog_priceTotal"+_11b);
if(_11c&&_11d&&_11e){
if(atg.commerce.csr.catalog.verifyQty(_11b)){
var _11f=parseFloat(_11c.value)*_11d.value;
_11e.innerHTML=atg.commerce.csr.catalog.formatCurrency(_11f);
}else{
_11e.innerHTML="";
}
}
},onProductRowChanged:function(_120){
atg.commerce.csr.catalog.updateTotalPrice(_120);
var _121=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _122=_121.rows[_121.rows.length-1];
var _123=atg.commerce.csr.catalog.generateTrId(_120)==_122.id;
if(_123&&atg.commerce.csr.catalog.isRowFilledIn(_120)){
var _124=atg.commerce.csr.catalog.addProductsByIdAddRow();
var _125=document.getElementById(atg.commerce.csr.catalog.generateTrId(_124));
_125.scrollIntoView(false);
}
atg.commerce.csr.catalog.setAddToShoppingButtonDisabled();
},rememberProductId:function(_126){
document.getElementById("atg_commerce_csr_catalog_tmpProductId").value=dojo.string.trim(_126);
},rememberSkuId:function(_127){
document.getElementById("atg_commerce_csr_catalog_tmpSkuId").value=dojo.string.trim(_127);
},onFocusProductId:function(_128){
atg.commerce.csr.catalog.rememberProductId(_128.value);
},onFocusSkuId:function(_129){
atg.commerce.csr.catalog.rememberSkuId(_129.value);
},getRowIndexByRowId:function(_12a){
var _12b=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _12c=atg.commerce.csr.catalog.generateTrId(_12a);
for(var _12d=1;_12d<_12b.rows.length;++_12d){
if(_12b.rows[_12d].id==_12c){
return _12d;
}
}
var _12e="ERROR in getRowIndexByRowId(): row id="+_12a+" not found";
atg.commerce.csr.catalog.showError(_12e);
return -1;
},isLastRowIndex:function(_12f){
var _130=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _131=_12f==_130.rows.length-1;
return _131;
},deleteProductRowByRowId:function(_132){
var _133=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
var _134=_133.rows.length-1;
if(_134<=1){
return;
}
var _135=atg.commerce.csr.catalog.getRowIndexByRowId(_132);
if(_135!=-1){
var _136=atg.commerce.csr.catalog.isLastRowIndex(_135);
_133.deleteRow(_135);
atg.commerce.csr.catalog.getPanelProducts().splice(_135-1,1);
if(_136){
atg.commerce.csr.catalog.addProductsByIdAddRow();
}
}
atg.commerce.csr.catalog.setAddToShoppingButtonDisabled();
},setAddToShoppingButtonDisabled:function(){
var _137=atg.commerce.csr.catalog.getFilledRowsId().length;
var _138=document.getElementById("atg_commerce_csr_catalog_addToShoppingButton");
if(_138){
if(_137<1){
_138.disabled="disabled";
}else{
if(atg.commerce.csr.catalog.isOrderModifiable()){
_138.disabled=false;
}
}
}
},getAddProductByIdDataToStore:function(){
var data=atg.commerce.csr.catalog.getPanelProducts();
while(data.length>0&&data[data.length-1]==null){
data.splice(data.length-1,1);
}
if(data.length<1){
return null;
}
var _139=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
for(var i=0;i<data.length;++i){
var _13a=i+1;
var row=_139.rows[_13a];
var _13b=atg.commerce.csr.catalog.getRowIdFromTr(row);
if(data[i]==null){
var _13c="ERROR: getAddProductByIdDataToStore: unexpected null element ("+i+") of an array";
atg.commerce.csr.catalog.showError(_13c);
}else{
data[i].quantity=document.getElementById("atg_commerce_csr_catalog_qty"+_13b).value;
}
}
return dojo.toJson(data);
},storeAddProductByIdData:function(){
if(document.getElementById("atg_commerce_csr_catalog_dontStore").value=="1"){
return;
}
var data=atg.commerce.csr.catalog.getAddProductByIdDataToStore();
if(data==null){
atg.commerce.csr.catalog.clearAddProductByIdData();
}else{
data=data.replace("'","\\'").replace("\n","");
var _13d=document.getElementById("atg_commerce_csr_catalog_addProductsByIdWindowScopeForm");
_13d.atg_commerce_csr_catalog_valueForWindowScope.value=data;
atgSubmitAction({form:_13d});
}
},restoreAddProductByIdData:function(_13e){
var _13f=document.getElementById("atg_commerce_csr_catalog_addProductsByIdTable");
if(atg.commerce.csr.catalog.isLastRowIndex(0)){
atg.commerce.csr.catalog.addProductsByIdAddRow();
}
var _140=dojo.fromJson(_13e);
if(_140==null){
return;
}
var _141=1;
var _142=1;
atg.commerce.csr.catalog.insertSkusIntoTable(_140,_141,_142,null);
},clearAddProductByIdData:function(){
var _143=document.getElementById("atg_commerce_csr_catalog_addProductsByIdWindowScopeForm");
_143.atg_commerce_csr_catalog_valueForWindowScope.value="";
atgSubmitAction({form:_143});
},restoreTreeState:function(){
var tree=atg.commerce.csr.catalog.getTree();
tree.populateNodeURL+="&_windowid="+window.windowId;
tree.openURL+="&_windowid="+window.windowId;
tree.closeURL+="&_windowid="+window.windowId;
tree.checkURL+="&_windowid="+window.windowId;
tree.clearAllAndCheckURL+="&_windowid="+window.windowId;
tree.uncheckURL+="&_windowid="+window.windowId;
tree.selectURL+="&_windowid="+window.windowId;
if(window.treeInfo){
var _144=false;
if(window.treeInfo.selectedItemType=="category"){
if(window.catalogInfo){
tree.nodeSelected(window.treeInfo.path,window.treeInfo.info);
}
}
if(window.treeInfo.path){
var _145=window.treeInfo.path.substring(window.treeInfo.path.indexOf("_div_")+5);
var _146=_145.indexOf("$div$");
var _147=_145.substring(0,_146);
if(_147!=""){
tree.openNode("$div$category_div_"+_147);
_144=true;
}else{
if(_145!=""){
tree.openNode("$div$category_div_"+_145);
_144=true;
}
}
}else{
if(!_144&&window.treeInfo.rootItemExpanded&&window.treeInfo.rootCategoryId){
tree.openNode("$div$category_div_"+window.treeInfo.rootCategoryId);
}
}
}
if(window.catalogInfo){
atg.commerce.csr.catalog.productCatalogPagedData.formId="selectTreeNode";
}
},formatCurrency:function(_148){
var _149=document.getElementById("atg_commerce_csr_catalog_activeCurrencyCode").value;
var _14a=document.getElementById("atg_commerce_csr_catalog_currentOrderCurrencySymbol").value;
var _14b=document.getElementById("atg_commerce_csr_catalog_activeCurrencyCodeNumberOfDecimalPlaces").value;
if(""==_149||""==dojo.string.trim(_149)){
atg.commerce.csr.catalog.showError("atg.commerce.csr.catalog.formatCurrency failed: currencyCode="+_149);
return ""+_148;
}
if(""==_14a||""==dojo.string.trim(_14a)){
atg.commerce.csr.catalog.showError("atg.commerce.csr.catalog.formatCurrency failed: currencySymbol="+_14a);
return ""+_14a;
}
if(""==_14b||""==dojo.string.trim(_14b)){
_14b=2;
}
try{
var _14c=dojo.currency.format(_148,{currency:_149,places:_14b,round:0,symbol:_14a});
return _14c;
}
catch(e){
atg.commerce.csr.catalog.showError("dojo.currency.format failed: "+e);
return ""+_148;
}
},getTree:function(){
if(!window.frames.treeContainer||!window.frames.treeContainer.tree){
var tree=window.frames[1].tree;
}else{
var tree=window.frames.treeContainer.tree;
}
if(!tree){
var _14d=window.frames.length;
for(i=0;i<_14d;i++){
var _14e=window.frames[i];
if(_14e&&_14e.tree){
tree=_14e.tree;
break;
}
}
}
return tree;
},getTreeFrame:function(){
if(!window.frames.treeContainer||!window.frames.treeContainer.tree){
return window.frames[1].document;
}else{
return window.frames.treeContainer.document;
}
},getCommaSeparatedTreePath:function(path){
if(path!=null){
return path.replace(/\$div\$category_div_/g,",");
}
},getTreeNodeItemType:function(uri){
if(uri!=null){
var _14f=uri.substring(uri.indexOf("/")+1);
var ind1=_14f.indexOf("/")+1;
var ind2=_14f.lastIndexOf("/");
return _14f.substring(ind1,ind2);
}
},getTreeNodeCategoryId:function(path){
if(path!=null){
return path.substring(path.lastIndexOf("_div_")+5);
}
},getCatalogInfo:function(){
if(!window.catalogInfo){
window.catalogInfo={};
}
return window.catalogInfo;
},setCatalogInfo:function(_150,form){
var _151=atg.commerce.csr.catalog.getCatalogInfo();
if(_150){
_151.isCatalogBrowsing=true;
}else{
_151.isCatalogSearching=true;
}
if(form){
if(form.sku){
_151.sku=form.sku.value;
}
if(form.productID){
_151.productID=form.productID.value;
}
if(form.searchInput){
_151.searchInput=form.searchInput.value;
}
if(form.siteSelect){
_151.siteSelect=form.siteSelect.selectedIndex;
}
if(form.categorySelect){
_151.categorySelect=form.categorySelect.selectedIndex;
}
if(form.itemPrice){
_151.itemPrice=form.itemPrice.value;
}
if(form.priceRelation){
_151.priceRelation=form.priceRelation.value;
}
}
},getTreeInfo:function(){
if(!window.treeInfo){
window.treeInfo={};
}
return window.treeInfo;
},setTreeInfo:function(path,info,_152){
atg.commerce.csr.catalog.getTreeInfo().path=path;
atg.commerce.csr.catalog.getTreeInfo().info=info;
atg.commerce.csr.catalog.getTreeInfo().selectedItemType=_152;
},clearTreeState:function(){
window.catalogInfo=null;
window.treeInfo=null;
},clearTreeStateIfCatalogChanged:function(_153){
var _154=atg.commerce.csr.catalog.getCatalogInfo();
var _155=_154.currentCatalogId;
if(_155&&_153&&_155!==_153){
atg.commerce.csr.catalog.clearTreeState();
_154.currentCatalogId=_153;
}else{
if(_153){
_154.currentCatalogId=_153;
}
}
}};
dojo.provide("atg.commerce.csr");
if(dojo.isArray(atg.progress.panels)&&dojo.indexOf(atg.progress.panels,"orderSummaryPanel")<0){
atg.progress.panels.push("orderSummaryPanel");
}
atg.commerce.csr.openOrder=function(){
atgSubmitAction({url:atg.commerce.csr.getContextRoot()+"/include/orderIsModifiable.jsp",queryParams:{frameworkContext:"/agent"},tab:atg.service.framework.changeTab("commerceTab"),form:"transformForm"});
};
atg.commerce.csr.createOrder=function(){
var _156=atg.progress.update("cmcCustomerSearchPS","createNewOrder");
_156.addCallback(function(){
atgSubmitAction({panelStack:["cmcCatalogPS","globalPanels"],form:dojo.byId("envNewOrderForm"),selectTabbedPanels:["cmcProductCatalogSearchP"],queryParams:{"contentHeader":true}});
});
};
atg.commerce.csr.commitOrder=function(_157){
var _158=document.getElementById(_157);
atgSubmitAction({form:_158,panelStack:["globalPanels"]});
};
atg.commerce.csr.openPanelStackWithTabbedPanel=function(_159,_15a){
return atgSubmitAction({"panelStack":[_159],"selectTabbedPanels":[_15a],"form":dojo.byId("transformForm")});
};
atg.commerce.csr.openPanelStackWithTabbedPanel=function(_15b,_15c,tab){
return atgSubmitAction({"panelStack":[_15b],"selectTabbedPanels":[_15c],"tab":atg.service.framework.changeTab(tab),"form":dojo.byId("transformForm")});
};
atg.commerce.csr.openPanelStackWithTab=function(_15d,tab){
return atgSubmitAction({"panelStack":[_15d],"tab":atg.service.framework.changeTab(tab),"form":dojo.byId("transformForm")});
};
atg.commerce.csr.openPanelStack=function(ps){
return atgSubmitAction({"panelStack":[ps,"cmcHelpfulPanels"],"form":dojo.byId("transformForm")});
};
atg.commerce.csr.openPanelStackWithForm=function(ps,f){
return atgSubmitAction({"panelStack":[ps,"cmcHelpfulPanels"],"form":dojo.byId(f)});
};
atg.commerce.csr.openUrl=function(url,_15e){
atgSubmitAction({"url":url,"queryParams":{"frameworkContext":_15e},"form":"transformForm"});
};
atg.commerce.csr.initDojo=function(){
dojo.require("dijit.Menu");
dojo.registerModulePath("framework",atg.commerce.csr.getContextRoot()+"/script/widget");
dojo.require("framework.FrameworkLink");
};
atg.commerce.csr.setContextRoot=function(_15f){
window.top.contextRoot=_15f;
};
atg.commerce.csr.getContextRoot=function(){
return window.top.contextRoot;
};
dojo.addOnLoad(atg.commerce.csr.initDojo);
dojo.provide("atg.commerce.csr.common");
atg.commerce.csr.common.selectAll=function(_160,_161){
var len;
var ii=0;
if(_161){
len=_161.length;
if(len===undefined){
_161.checked=_160.checked;
return;
}
for(ii=0;ii<len;ii++){
_161[ii].checked=_160.checked;
}
}
};
atg.commerce.csr.common.getCheckedItem=function(_162){
if(!_162){
return "";
}
var len=_162.length;
if(len===undefined){
if(_162.checked){
return _162;
}else{
return "";
}
}
for(var ii=0;ii<len;ii++){
if(_162[ii].checked){
return _162[ii];
}
}
return "";
};
atg.commerce.csr.common.setCheckedItem=function(_163,_164){
var len;
var ii=0;
if(_163){
len=_163.length;
if(len===undefined){
_163.checked=(_163.value=="checked");
return;
}
for(ii=0;ii<len;ii++){
_163[ii].checked=false;
if(_163[ii].value==_164.toString()){
_163[ii].checked=true;
}
}
}
};
atg.commerce.csr.common.setIfValue=function(id,_165,_166){
var elem=document.getElementById(id);
if(elem.value==_165){
elem.value=_166;
}
};
atg.commerce.csr.common.setPropertyOnItems=function(ids,_167,_168){
var ii;
if(!dojo.isArray(ids)){
ids=[ids];
}
if(ids){
for(ii=0;ii<ids.length;ii++){
var _169=null;
if(dojo.isObject(ids[ii])&&ids[ii]["form"]&&ids[ii]["name"]){
_169=document.getElementById(ids[ii]["form"])[ids[ii]["name"]];
}else{
_169=dijit.byId(ids[ii]);
if(!_169){
_169=dojo.byId(ids[ii]);
}
}
if(_169){
_169[_167]=_168;
}
}
}
};
atg.commerce.csr.common.enableDisable=function(_16a,_16b){
atg.commerce.csr.common.setPropertyOnItems(_16b,"disabled",true);
atg.commerce.csr.common.setPropertyOnItems(_16a,"disabled",false);
};
atg.commerce.csr.common.submitPopup=function(pURL,_16c,_16d){
atgSubmitPopup({url:pURL,form:_16c,popup:_16d});
};
atg.commerce.csr.common.showPopup=function(_16e,pURL,_16f){
if(_16e&&pURL){
_16e.titleBarText.innerHTML=_16f||"";
_16e.setUrl(pURL);
_16e.show();
}
};
atg.commerce.csr.common.hidePopup=function(_170){
if(_170){
_170.hide();
}
};
atg.commerce.csr.common.getEnclosingPopup=function(_171){
var node=dojo.dom.getAncestors(dojo.byId(_171),function(node){
if(node.className&&node.className.indexOf("dijitDialogPaneContent")==-1){
if(node.className.indexOf("dijitDialog")>=0){
if(node.tagName=="DIV"){
return true;
}
}
}
return false;
},true);
return dijit.byId(node.id);
};
atg.commerce.csr.common.getEnclosingForm=function(_172){
return dojo.dom.getAncestors(document.getElementById(_172),function(node){
return "FORM"==node.tagName;
},true);
};
atg.commerce.csr.common.setPopupData=function(_173,name,data){
var old=atg.commerce.csr.common.getPopupData(_173,name);
atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)[name]=data;
return old;
};
atg.commerce.csr.common.getPopupData=function(_174,name){
return atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)[name];
};
atg.commerce.csr.common.showPopupWithReturn=function(args){
var _175=dijit.byId(args.popupPaneId);
_175._atg_results={};
var _176=function(){
if(args.onClose){
args.onClose(atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)._atg_results);
}
};
atg.commerce.csr.common.getEnclosingPopup(args.popupPaneId)._atg_args=args;
if(_175.closeHandle){
dojo.disconnect(_175.closeHandle);
}
_175.closeHandle=dojo.connect(_175,"hide",dojo.hitch(this,_176,args));
_175.titleNode.innerHTML=args.title||"";
_175.setHref(args.url);
_175.show();
};
atg.commerce.csr.common.hidePopupWithReturn=function(_177,_178){
var _179=this.getEnclosingPopup(_177);
_179._atg_results=_178;
_179.hide();
};
atg.commerce.csr.common.toggle=function(_17a,_17b,_17c,_17d){
dojo.toggleClass(_17b,"hidden_node");
dojo.toggleClass(_17a,_17d);
dojo.toggleClass(_17a,_17c);
};
atg.commerce.csr.common.prepareFormToPersistOrder=function(_17e){
var _17f;
var _180;
if(_17e&&_17e.form){
if(!(_17e.persistOrder==undefined)){
_17f=document.getElementById(_17e.form).persistOrder;
_180=document.getElementById(_17e.form).successURL;
_180.value=document.getElementById(_17e.form).successURL.value;
_17f.value=true;
}
}
};
atg.commerce.csr.common.disableTextboxWidget=function(_181){
var _182=null;
if(typeof _181==="string"){
_182=dijit.byId(_181);
}else{
_182=_181;
}
if(_182){
_182.textbox.disabled="true";
}
};
atg.commerce.csr.common.disableComboBoxWidget=function(_183){
var _184=dijit.byId(_183);
if(_184){
_184.domNode.disabled="true";
}
};
atg.service.framework.togglePanel=function(_185){
frameworkPanelAction("togglePanel",_185,["helpfulPanels","cmcHelpfulPanels"]);
};
atg.commerce.csr.common.setSite=function(_186,_187){
dojo.debug("MultiSite | atg.commerce.csr.common.setSite called with siteId = "+_186+" currentSiteId = "+_187);
var _188=document.getElementById("atg_commerce_csr_loadExistingSiteForm");
if(_188){
if(_186!=_187){
atg.commerce.csr.catalog.clearTreeState();
atg.commerce.csr.catalog.clearAddProductByIdData();
}
_188.siteId.value=_186;
atgSubmitAction({selectTabbedPanels:["cmcProductCatalogSearchP"],form:_188});
}
};
atg.commerce.csr.common.changeSite=function(_189,_18a){
dojo.debug("MultiSite | atg.commerce.csr.common.changeSite called with siteId = "+_189);
if(!_18a){
var _18b=dojo.byId("atg_commerce_csr_productDetailsForm");
}else{
var _18b=dojo.byId(_18a);
}
atgSubmitAction({form:_18b,sync:true,queryParams:{contentHeader:true,siteId:_189}});
};
atg.commerce.csr.common.searchForSite=function(){
dojo.debug("MultiSite | atg.commerce.csr.common.searchForSite called");
atgSubmitAction({sync:true,panelStack:["cmcMultisiteSelectionPickerPS"],tab:atg.service.framework.changeTab("commerceTab")});
};
atg.commerce.csr.common.searchForCatalog=function(){
dojo.debug("MultiSite | atg.commerce.csr.common.searchForCatalog called");
atgSubmitAction({sync:true,panelStack:["cmcMoreCatalogsPS"],tab:atg.service.framework.changeTab("commerceTab")});
};
atg.commerce.csr.common.searchForPricelist=function(){
dojo.debug("MultiSite | atg.commerce.csr.common.searchForPricelist called");
atgSubmitAction({sync:true,panelStack:["cmcMorePriceListsPS"]});
};
atg.commerce.csr.common.addMessageInMessagebar=function(_18c,_18d){
if(dijit.byId("messageBar")){
dijit.byId("messageBar").addMessage({type:_18c,summary:_18d});
}
};
dojo.provide("atg.commerce.csr.customer");
atg.commerce.csr.customer={addNewCredit:function(){
var _18e=dojo.byId("addNewCreditForm");
if(_18e){
atgSubmitAction({form:_18e,panelStack:["globalPanels","customerPanels"]});
atg.commerce.csr.common.hidePopupWithReturn("addNewCreditPopup",{result:"ok"});
}
},isExistingChecked:function(){
return dojo.byId("editCreditCardForm")["/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress"][0].checked;
},isNewChecked:function(){
return dojo.byId("editCreditCardForm")["/atg/commerce/custsvc/repository/CreditCardFormHandler.createNewAddress"][1].checked;
},existingCreditCardAddressChanged:function(){
var id=dojo.byId("editCreditCardForm_existingAddressList").value;
if(id!==""){
dojo.byId("editCreditCardForm").editCreditCardForm_firstName.value=atg.commerce.csr.customer.addrList[id].first;
dojo.byId("editCreditCardForm").editCreditCardForm_middleName.value=atg.commerce.csr.customer.addrList[id].middle;
dojo.byId("editCreditCardForm").editCreditCardForm_lastName.value=atg.commerce.csr.customer.addrList[id].last;
dojo.byId("editCreditCardForm")["/atg/commerce/custsvc/repository/CreditCardFormHandler.value.billingAddress.REPOSITORYID"].value=dojo.byId("editCreditCardForm_existingAddressList").value;
}
},syncToCustomerCatalog:function(_18f){
var _190=document.getElementById("syncCurrentCustomerCatalog");
atgSubmitAction({form:_190});
},syncToCustomerPriceLists:function(_191){
var _192=document.getElementById("syncCurrentCustomerPriceLists");
atgSubmitAction({form:_192});
}};
atg.commerce.csr.customer.addrList=[];
atg.ea.registerCSCHelpArray=function(){
atg.ea.registerHelpArray({id:"ea_csc_order_search",type:"inline",helpId:"ea_csc_order_search"});
atg.ea.registerHelpArray({id:"ea_csc_product_view",type:"inline",helpId:"ea_csc_product_view"});
atg.ea.registerHelpArray({id:"ea_csc_product_item_price",type:"popup",helpId:"ea_csc_product_item_price"});
atg.ea.registerHelpArray({id:"ea_csc_order_submit",type:"popup",helpId:"ea_csc_order_submit"});
atg.ea.registerHelpArray({id:"ea_csc_order_submit_footer",type:"popup",helpId:"ea_csc_order_submit_footer"});
atg.ea.registerHelpArray({id:"ea_csc_order_submit_create_schedule",type:"popup",helpId:"ea_csc_order_submit_create_schedule"});
atg.ea.registerHelpArray({id:"ea_csc_order_submit_create_schedule_footer",type:"popup",helpId:"ea_csc_order_submit_create_schedule_footer"});
atg.ea.registerHelpArray({id:"ea_csc_order_create_schedule",type:"popup",helpId:"ea_csc_order_create_schedule"});
atg.ea.registerHelpArray({id:"ea_csc_order_create_schedule_footer",type:"popup",helpId:"ea_csc_order_create_schedule_footer"});
atg.ea.registerHelpArray({id:"ea_csc_order_scheduled_days_of_week",type:"popup",helpId:"ea_csc_order_scheduled_days_of_week"});
atg.ea.registerHelpArray({id:"ea_csc_order_scheduled_weeks",type:"popup",helpId:"ea_csc_order_scheduled_weeks"});
atg.ea.registerHelpArray({id:"ea_csc_order_scheduled_dates_in_month",type:"popup",helpId:"ea_csc_order_scheduled_dates_in_month"});
atg.ea.registerHelpArray({id:"ea_csc_order_scheduled_actions",type:"popup",helpId:"ea_csc_order_scheduled_actions"});
atg.ea.registerHelpArray({id:"ea_csc_order_scheduled_status_failed",type:"popup",helpId:"ea_csc_order_scheduled_status_failed"});
atg.ea.registerHelpArray({id:"ea_csc_order_copy",type:"popup",helpId:"ea_csc_order_copy"});
atg.ea.registerHelpArray({id:"ea_csc_order_view_cancel",type:"popup",helpId:"ea_csc_order_view_cancel"});
atg.ea.registerHelpArray({id:"ea_csc_purchased_isReturnable",type:"popup",helpId:"ea_csc_purchased_isReturnable"});
atg.ea.registerHelpArray({id:"ea_csc_instore_pickup_available",type:"popup",helpId:"ea_csc_instore_pickup_available"});
atg.ea.registerHelpArray({id:"ea_csc_instore_pickup_billing_logic",type:"popup",helpId:"ea_csc_instore_pickup_billing_logic"});
};
dojo.addOnLoad(atg.ea.registerCSCHelpArray);
atg.ea.registerCSCHelpContent=function(){
atg.ea.registerHelpContent({"id":"ea_csc_order_search","excerpt":getResource("ea.csc.helpContent.ea_csc_order_search"),"content":""});
atg.ea.registerHelpContent({"id":"ea_csc_product_view","excerpt":getResource("ea.csc.helpContent.ea_csc_product_view"),"content":""});
atg.ea.registerHelpContent({"id":"ea_csc_product_item_price","content":getResource("ea.csc.helpContent.ea_csc_product_item_price")});
atg.ea.registerHelpContent({"id":"ea_csc_order_submit","content":getResource("ea.csc.helpContent.ea_csc_order_submit")});
atg.ea.registerHelpContent({"id":"ea_csc_order_submit_footer","content":getResource("ea.csc.helpContent.ea_csc_order_submit_footer")});
atg.ea.registerHelpContent({"id":"ea_csc_order_submit_create_schedule","content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule")});
atg.ea.registerHelpContent({"id":"ea_csc_order_submit_create_schedule_footer","content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule_footer")});
atg.ea.registerHelpContent({"id":"ea_csc_order_create_schedule","content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule")});
atg.ea.registerHelpContent({"id":"ea_csc_order_create_schedule_footer","content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule_footer")});
atg.ea.registerHelpContent({"id":"ea_csc_order_scheduled_days_of_week","content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_days_of_week")});
atg.ea.registerHelpContent({"id":"ea_csc_order_scheduled_weeks","content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_weeks")});
atg.ea.registerHelpContent({"id":"ea_csc_order_scheduled_dates_in_month","content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_dates_in_month")});
atg.ea.registerHelpContent({"id":"ea_csc_order_scheduled_actions","content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_actions")});
atg.ea.registerHelpContent({"id":"ea_csc_order_scheduled_status_failed","content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_status_failed")});
atg.ea.registerHelpContent({"id":"ea_csc_order_copy","content":getResource("ea.csc.helpContent.ea_csc_order_copy")});
atg.ea.registerHelpContent({"id":"ea_csc_order_view_cancel","content":getResource("ea.csc.helpContent.ea_csc_order_view_cancel")});
atg.ea.registerHelpContent({"id":"ea_csc_purchased_isReturnable","content":getResource("ea.csc.helpContent.ea_csc_purchased_isReturnable")});
atg.ea.registerHelpContent({"id":"ea_csc_instore_pickup_available","content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_available")});
atg.ea.registerHelpContent({"id":"ea_csc_instore_pickup_billing_logic","content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_billing_logic")});
};
dojo.addOnLoad(atg.ea.registerCSCHelpContent);
atg.ea.registerCSCTooltips=function(){
atg.ea.registerTooltip("orderLink",getResource("ea.csc.tooltip.orderLink"));
atg.ea.registerTooltip("orderSave",getResource("ea.csc.tooltip.orderSave"));
atg.ea.registerTooltip("orderCancel",getResource("ea.csc.tooltip.orderCancel"));
atg.ea.registerTooltip("submitOrderButton",getResource("ea.csc.tooltip.submitOrderButton"));
atg.ea.registerTooltip("submitCreateScheduleButton",getResource("ea.csc.tooltip.submitCreateScheduleButton"));
atg.ea.registerTooltip("createScheduleButton",getResource("ea.csc.tooltip.createScheduleButton"));
};
dojo.subscribe("UpdateGlobalContext",null,function(){
atg.ea.registerCSCTooltips();
});
dojo.provide("atg.commerce.csr.catalog.endeca.search");
atg.commerce.csr.catalog.endeca.search={showHideMainCategories:function(_193){
if(this._isNotLoadedMainCategories()){
this._loadAndShowMainCategories(_193);
}else{
if(this._isVisibleMainCategories()){
this._hideMainCategories();
}else{
this._showMainCategories();
}
}
},_isNotLoadedMainCategories:function(){
var _194=document.getElementById("atg_commerce_csr_catalog_endeca_categories");
if(_194.categoriesLoaded!=true){
return true;
}
return false;
},_loadAndShowMainCategories:function(_195){
this._showMainCategories();
dojo.xhrGet({url:_195,content:{_windowid:window.windowId},encoding:"utf-8",handle:function(_196,_197){
if(!(_196 instanceof Error)){
var _198=document.getElementById("atg_commerce_csr_catalog_endeca_categories");
_198.innerHTML=_196;
_198.categoriesLoaded=true;
}else{
document.getElementById("atg_commerce_csr_catalog_endeca_categories").innerHTML="An error occured...";
}
},mimetype:"text/html"});
},_isVisibleMainCategories:function(){
var _199=document.getElementById("atg_commerce_csr_catalog_endeca_categories");
return _199.style.display=="block";
},_hideMainCategories:function(){
var _19a=document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
_19a.style.display="none";
var _19b=document.getElementById("atg_commerce_csr_catalog_endeca_categories");
_19b.style.display="none";
$("body").unbind("keyup",atg.commerce.csr.catalog.endeca.search._hideMainCategories);
$("body").unbind("click",atg.commerce.csr.catalog.endeca.search._hideMainCategories);
},_showMainCategories:function(){
var _19c=document.getElementById("atg_commerce_csr_catalog_endeca_categories");
_19c.style.display="block";
$("body").bind("click",atg.commerce.csr.catalog.endeca.search._hideMainCategories);
$("body").bind("keyup",atg.commerce.csr.catalog.endeca.search._hideMainCategories);
},showHideSubCategories:function(_19d,_19e,_19f){
if(this._isNotLoadedSubCategoryFlyOut(_19d)){
this._loadAndShowSubCategoryFlyOut(_19d,_19e,_19f);
}else{
this._fillSubCategoriesContent(_19d);
this._showSubCategories();
}
},_isNotLoadedSubCategoryFlyOut:function(_1a0){
return document.getElementById("atg_commerce_csr_catalog_endeca_category"+_1a0)==null;
},_findSubcategoriesCount:function(_1a1){
for(var i=0;i<_1a1.length;i++){
var _1a2=_1a1[i].getElementsByTagName("a")[0].className;
var _1a3=parseInt(_1a2.substring(5),10);
_1a1[i].level=_1a3;
}
for(i=0;i<_1a1.length-1;i++){
var _1a4=0;
for(var j=i+1;(j<_1a1.length)&&(_1a1[i].level<_1a1[j].level);j++){
_1a4++;
}
_1a1[i].subCategoryCount=_1a4;
}
},_convertHTML:function(_1a5,_1a6,_1a7,_1a8){
var _1a9=document.getElementById(_1a5);
var _1aa=_1a9.getElementsByTagName("div");
atg.commerce.csr.catalog.endeca.search._findSubcategoriesCount(_1aa);
var _1ab=_1a7;
var _1ac=_1aa.length;
var _1ad=Math.ceil(_1ac/_1a7);
if(_1ad>_1a6){
_1ab=Math.ceil(_1ac/_1a6);
_1ad=_1a6;
}
var _1ae=0;
var _1af=0;
columnElement=document.createElement("TD");
columnElement.id="column"+_1ae;
columnElement.className="column"+_1ae;
var _1b0=_1aa.length;
var _1b1=0;
var _1b2=false;
var _1b3=document.createElement("TR");
for(var i=0;i<_1b0;i++){
var _1b4=_1ae<_1a6-1;
var _1b5=_1af>=_1ab;
var _1b6=(_1aa[0].subCategoryCount>0)&&(_1aa[0].subCategoryCount<=_1a8);
var _1b7=_1af+1+_1aa[0].subCategoryCount>_1ab;
var _1b8=_1b0-i;
var _1b9=(_1a6-1-_1ae)*(_1ab-_1a8-1);
var _1ba=_1b8<=_1b9;
if(_1b7&&_1b4&&_1b6&&(_1b1==0)){
_1b1=_1aa[0].subCategoryCount+1;
}
if((_1b4&&_1b5&&((_1b1==0)||_1b2))||(_1b4&&_1b7&&_1b6&&_1ba&&(_1b8>_1ab-_1af))||(_1b4&&_1b7&&_1b6&&(_1b8==_1b1))){
_1b3.appendChild(columnElement);
_1ae++;
_1af=0;
columnElement=document.createElement("TD");
columnElement.className="column"+_1ae;
}
_1b2=(_1b1==1);
if(_1b1>0){
_1b1--;
}
columnElement.appendChild(_1aa[0]);
_1af++;
}
_1b3.appendChild(columnElement);
var _1bb=document.createElement("TABLE");
_1bb.appendChild(_1b3);
_1a9.appendChild(_1bb);
},_loadAndShowSubCategoryFlyOut:function(_1bc,_1bd,_1be){
this._showSubCategories();
dojo.xhrGet({url:_1be,content:{_windowid:window.windowId,rootCategoryId:_1bd,categoryNumber:_1bc},encoding:"utf-8",handle:function(_1bf,_1c0){
if(!(_1bf instanceof Error)){
document.getElementById("atg_commerce_csr_catalog_endeca_subcategoryFlyOutContentCache").innerHTML+=_1bf;
atg.commerce.csr.catalog.endeca.search._convertHTML("atg_commerce_csr_catalog_endeca_category"+_1bc,3,15,5);
atg.commerce.csr.catalog.endeca.search._fillSubCategoriesContent(_1bc);
}else{
document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories").innerHTML="An error occured...";
}
},mimetype:"text/html"});
},_isVisibleSubCategories:function(){
var _1c1=document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
return _1c1.style.display=="block";
},_hideSubCategories:function(){
var _1c2=document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
_1c2.style.display="none";
},_fillSubCategoriesContent:function(_1c3){
var _1c4=document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
var _1c5=document.getElementById("atg_commerce_csr_catalog_endeca_category"+_1c3);
_1c4.innerHTML=_1c5.innerHTML;
var _1c6=this._getNumberResults(_1c3);
},_showSubCategories:function(){
var _1c7=document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
_1c7.style.display="block";
},_getNumberResults:function(_1c8){
var _1c9=document.getElementById("atg_commerce_csr_catalog_endeca_category"+_1c8);
var _1ca=_1c9.getElementsByTagName("input")[0];
return _1ca.value;
},submitSearchTermRequest:function(_1cb){
var _1cc=dojo.byId("searchTermInput").value;
var _1cd=_1cb.replace("SEARCHTERMINPUT",escape(_1cc));
if($("#searchTermInput").catcomplete){
$("#searchTermInput").catcomplete("destroy");
}
atgSubmitAction({url:_1cd});
},setupAutoComplete:function(_1ce,_1cf,_1d0){
$(function(){
$.widget("custom.catcomplete",$.ui.autocomplete,{_renderMenu:function(ul,_1d1){
var self=this,_1d2="";
$.each(_1d1,function(_1d3,item){
if(item.category!=_1d2){
ul.append("<li class='ui-autocomplete-category'>"+item.category+"</li>");
_1d2=item.category;
}
self._renderItem(ul,item);
});
}});
$("#searchTermInput").catcomplete({source:function(_1d4,_1d5){
var _1d6=_1cf.replace("SEARCHTERMINPUT",escape(_1d4.term));
$.ajax({url:_1ce,dataType:"json",data:{_windowid:window.windowId,contentCollection:_1d6},error:function(_1d7,_1d8,_1d9){
alert("AJAX Error: "+_1d7.status);
},success:function(data){
var list=[];
$(data.dimensionSearchResults.dimensionSearchGroups).each(function(i,_1da){
$(_1da.dimensionSearchValues).each(function(j,_1db){
var _1dc=[];
$(_1db.ancestors).each(function(j,_1dd){
_1dc.push(_1dd.label);
});
_1dc.push(_1db.label);
list.push({label:_1dc.join("  >  "),value:_1db.label,term:_1d4.term,category:_1da.displayName,contentURL:_1db.contentURL});
});
});
_1d5(list);
}}).done(function(msg){
});
},minLength:_1d0,select:function(_1de,ui){
},open:function(){
$(this).removeClass("ui-corner-all").addClass("ui-corner-top");
},close:function(){
$(this).removeClass("ui-corner-top").addClass("ui-corner-all");
},appendTo:"#catalog-nav"}).data("catcomplete")._renderItem=function(ul,item){
var _1df=item.label.split(" ");
for(var i=0;i<_1df.length;i++){
if(_1df[i].toLowerCase().indexOf(item.term.toLowerCase())===0){
_1df[i]="<span class='highlight'>"+_1df[i].substr(0,item.term.length)+"</span>"+_1df[i].substring(item.term.length);
}
}
var lbl=_1df.join(" ");
var _1e0=$("<a>").html(lbl).click(function(){
atgSubmitAction({url:item.contentURL});
});
return $("<li></li>").data("item.autocomplete",item).append(_1e0).appendTo(ul);
};
});
}};
atg.keyboard.registerCSCShortcuts=function(){
atg.keyboard.registerShortcut("ALT+7",{shortcut:"ALT + 7",name:getResource("keyboard.service.commerceTab.name"),description:getResource("keyboard.service.commerceTab.description"),area:getResource("keyboard.area.commerce"),topic:"/atg/service/keyboardShortcut/commerceTab",notify:true});
atg.keyboard.registerShortcut("CTRL+ALT+SHIFT+S",{shortcut:"CTRL + ALT + SHIFT + S",name:getResource("keyboard.csc.searchOrder.name"),description:getResource("keyboard.csc.searchOrder.description"),area:getResource("keyboard.area.commerce"),topic:"/atg/csc/keyboardShortcut/searchForOrder",notify:true});
atg.keyboard.registerShortcut("ALT+SHIFT+F2",{shortcut:"ALT + SHIFT + F2",name:getResource("keyboard.csc.productCatalog.name"),description:getResource("keyboard.csc.productCatalog.description"),area:getResource("keyboard.area.commerce"),topic:"/atg/csc/keyboardShortcut/productCatalog",notify:true});
atg.keyboard.registerShortcut("ALT+SHIFT+G",{shortcut:"ALT + SHIFT + G",name:getResource("keyboard.csc.shipping.name"),description:getResource("keyboard.csc.shipping.description"),area:getResource("keyboard.area.commerce"),topic:"/atg/csc/keyboardShortcut/shipping",notify:true});
atg.keyboard.registerShortcut("ALT+SHIFT+I",{shortcut:"ALT + SHIFT + I",name:getResource("keyboard.csc.billing.name"),description:getResource("keyboard.csc.billing.description"),area:getResource("keyboard.area.commerce"),topic:"/atg/csc/keyboardShortcut/billing",notify:true});
};
dojo.addOnLoad(atg.keyboard.registerCSCShortcuts);
atg.keyboard.registerCSCTopics=function(){
dojo.subscribe("/atg/service/keyboardShortcut/commerceTab",null,function(){
atg.commerce.csr.openPanelStackWithTab("cmcShoppingCartPS","commerceTab");
});
dojo.subscribe("/atg/csc/keyboardShortcut/createNewOrder",null,function(){
atg.commerce.csr.createOrder();
});
dojo.subscribe("/atg/csc/keyboardShortcut/searchForOrder",null,function(){
atg.commerce.csr.openPanelStackWithTab("cmcOrderSearchPS","commerceTab");
});
dojo.subscribe("/atg/csc/keyboardShortcut/productCatalog",null,function(){
atg.commerce.csr.openPanelStackWithTabbedPanel("cmcCatalogPS","cmcProductCatalogSearchP","commerceTab");
});
dojo.subscribe("/atg/csc/keyboardShortcut/shoppingCart",null,function(){
var _1e1=dojo.byId("keyboardShortcutShoppingCart");
if(_1e1!=null){
_1e1.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/shipping",null,function(){
var _1e2=dojo.byId("keyboardShortcutShipping");
if(_1e2!=null){
_1e2.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/billing",null,function(){
var _1e3=dojo.byId("keyboardShortcutBilling");
if(_1e3!=null){
_1e3.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/completeOrder",null,function(){
var _1e4=dojo.byId("Complete Order");
if(_1e4!=null){
atg.commerce.csr.order.finish.submitOrder("atg_commerce_csr_finishOrderSubmitForm");
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/shipToMultiple",null,function(){
var _1e5=dojo.byId("atg_commerce_csr_shipToMultipleAddresses");
if(_1e5!=null){
addressGrid.render();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/shipToOneAddress",null,function(){
var _1e6=dojo.byId("singleShipping");
if(_1e6!=null){
atgNavigate({panelStack:"cmcShippingAddressPS",queryParams:{"mode":"singleShipping"}});
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/saveOrder",null,function(){
var _1e7=dojo.byId("orderSave");
if(_1e7!=null){
atg.commerce.csr.commitOrder("globalCommitOrderForm");
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/orderDetails",null,function(){
var _1e8=dojo.byId("orderLinkAnchor");
if(_1e8!=null){
_1e8.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/cancelOrder",null,function(){
var _1e9=dojo.byId("orderCancel");
if(_1e9!=null){
_1e9.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/createReturnExchange",null,function(){
var _1ea=dojo.byId("createReturnExchange");
if(_1ea!=null){
_1ea.onclick();
}
});
dojo.subscribe("/atg/csc/keyboardShortcut/createAppeasementProcess",null,function(){
var _1eb=dojo.byId("createAppeasementProcess");
if(_1eb!=null){
_1eb.onclick();
}
});
dojo.subscribe("/atg/service/keyboardShortcut/customerDetails",null,function(){
viewCurrentCustomer("commerceTab");
});
};
dojo.addOnLoad(atg.keyboard.registerCSCTopics);
dojo.provide("atg.commerce.csr.order.appeasement");
atg.commerce.csr.order.appeasement.initiateAppeasementProcess=function(_1ec){
var form=document.getElementById("createAppeasement");
if(_1ec.orderId!==undefined&&_1ec.orderId!==null){
form.orderId.value=_1ec.orderId;
}
atgSubmitAction({form:form});
};
atg.commerce.csr.order.appeasement.applyAppeasementRefundValues=function(){
var _1ed=document.getElementById("atg_commerce_csr_appeasement_appeasementType");
var _1ee=_1ed.options[_1ed.selectedIndex].value;
var _1ef=document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
var _1f0=_1ef.options[_1ef.selectedIndex].value;
dojo.byId("atg_commerce_csr_appeasement_updateAppeasementType").value=_1ee;
dojo.byId("atg_commerce_csr_appeasement_updateAppeasementAmount").value=dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value;
dojo.byId("atg_commerce_csr_appeasement_updateAppeasementReasonCode").value=_1f0;
dojo.byId("atg_commerce_csr_appeasement_updateAppeasementNotes").value=dojo.byId("atg_commerce_csr_appeasement_appeasementsCommentsId").value;
var _1f1=dojo.byId("atg_commerce_csr_updateAppeasement");
atgSubmitAction({form:_1f1,panelStack:["globalPanels","cmcAppeasementsPS"]});
};
atg.commerce.csr.order.appeasement.cancelAppeasement=function(){
var form=document.getElementById("cancelAppeasement");
var _1f2=atgSubmitAction({form:form});
_1f2.addCallback(function(){
atg.progress.update("cmcExistingOrderPS");
});
};
atg.commerce.csr.order.appeasement.setAppeasementValues=function(el){
console.log("In setAppeasementValue "+el.value);
if(el.value=="selectAmountOff"){
console.log("Hiding percent text box");
dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display="inline-block";
dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display="none";
}else{
console.log("Hiding amount text box");
dojo.byId("atg_commerce_csr_appeasement_appeasementAmountInputDiv").style.display="none";
dojo.byId("atg_commerce_csr_appeasement_appeasementPercentInputDiv").style.display="inline-block";
}
dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value=el.value;
};
atg.commerce.csr.order.appeasement.displayAppeasementAmount=function(){
var _1f3=document.getElementById("atg_commerce_csr_order_appeasements_activeCurrencyCode").value;
var _1f4=dojo.byId("atg_commerce_csr_appeasement_total");
var _1f5;
var _1f6=0;
var _1f7=true;
var _1f8=false;
if(dojo.byId("atg_commerce_csr_appeasement_appeasementType").value==="shipping"){
_1f5=dojo.byId("shippingBalance").value;
}else{
_1f5=dojo.byId("itemBalance").value;
}
if(dojo.byId("atg_commerce_csr_appeasement_amountOrPercentageSelectedValue").value==="selectPercentageOff"){
var _1f9=dojo.byId("atg_commerce_csr_appeasement_appeasementPercentValue").value;
console.log("Precent off "+_1f9);
if(!isNaN(_1f9)&&_1f9<=100&&_1f9>0){
_1f6=_1f5/100*_1f9;
}
if(_1f6>0){
console.log("Calculated percent off "+_1f6);
_1f8=true;
dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value=atg.commerce.csr.order.billing.roundAmount(_1f6);
var _1fa=atg.commerce.csr.order.billing.formatAmount(_1f6,container.currencyCode);
if(_1f4.textContent!==undefined){
_1f4.textContent=_1fa;
}else{
_1f4.innerText=_1fa;
}
}else{
dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value=0;
if(_1f4.textContent!==undefined){
_1f4.textContent="";
}else{
_1f4.innerText="";
}
}
}else{
var _1fb=dojo.byId("atg_commerce_csr_appeasement_appeasementAmountValue").value;
var _1fc=atg.commerce.csr.order.billing.parseAmount(_1fb);
if(!isNaN(_1fc)&&(_1fc<=_1f5)&&(_1fc>0)){
var _1fa=atg.commerce.csr.order.billing.formatAmount(_1fc,container.currencyCode);
if(_1f4.textContent!==undefined){
_1f4.textContent=_1fa;
}else{
_1f4.innerText=_1fa;
}
dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value=atg.commerce.csr.order.billing.roundAmount(_1fc);
_1f8=true;
console.log("Valid amountoff  "+_1fc);
}else{
dojo.byId("atg_commerce_csr_appeasement_appeasementResult").value=0;
if(_1f4.textContent!==undefined){
_1f4.textContent="";
}else{
_1f4.innerText="";
}
}
}
var _1fd=document.getElementById("atg_commerce_csr_appeasement_appeasementReasonCode");
var _1fe=_1fd.options[_1fd.selectedIndex].value;
console.log("The Reason Code selection is "+_1fe+" valid amount "+_1f8);
if(_1f8&&dojo.string.trim(_1fe).length>0){
console.log("Enabling the apply appeasements button");
_1f7=false;
}
dojo.byId("applyAppeasementValuesButton").disabled=_1f7;
};
atg.commerce.csr.order.appeasement.openAppeasementSummary=function(){
console.debug("Calling atg.commerce.csr.appeasements.openAppeasementsPage");
var form=document.getElementById("csrApplyAppeasements");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.appeasement.sendConfirmationMessage=function(){
atgSubmitAction({form:document.getElementById("csrSendAppeasementConfirmationMessage")});
};
atg.commerce.csr.order.appeasement.openConfirmAppeasement=function(){
console.debug("Calling atg.commerce.csr.order.appeasement.openConfirmAppeasement");
atg.commerce.csr.openPanelStack("cmcConfirmAppeasementPS");
};
atg.commerce.csr.order.appeasement.submitAppeasement=function(){
console.debug("Calling atg.commerce.csr.order.appeasement.submitAppeasement");
var form=document.getElementById("csrSubmitAppeasement");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.appeasement.selectAppeasementHistory=function(_1ff){
atgSubmitAction({panels:["cmcAppeasementHistoryP"],form:dojo.byId("transformForm"),queryParams:{"historyAppeasementId":_1ff}});
};
dojo.provide("atg.commerce.csr.order.billing");
dojo.require("dojox.i18n.currency");
dojo.require("dojox.i18n.number");
dojo.require("dojo.date");
dojo.require("dojox.validate._base");
dojo.require("dojox.validate.creditCard");
atg.commerce.csr.order.billing.container=null;
atg.commerce.csr.order.billing.creditCardTypeDataContainer=null;
atg.commerce.csr.order.billing.applyPaymentGroups=function(_200){
var form=document.getElementById("csrBillingForm");
if(container&&container.availablePaymentMethods){
for(id in container.availablePaymentMethods){
var _201=container.availablePaymentMethods[id];
if(_201.paymentGroupType=="inStorePayment"){
var _202=dijit.byId(_201.paymentGroupId);
var _203=dojo.byId(_201.paymentGroupId+"_checkbox");
var _204=dojo.byId(_201.paymentGroupId+"_relationshipType");
if(_202&&_203&&!_203.checked){
_202.setValue(0);
}
if(_202&&_204&&_203&&!_203.checked){
_204.value="ORDERAMOUNT";
}
}
}
}
atg.commerce.csr.common.enableDisable([{form:"csrBillingForm",name:"csrHandleApplyPaymentGroups"}],[{form:"csrBillingForm",name:"csrPaymentGroupsPreserveUserInputOnServerSide"}]);
atg.commerce.csr.common.prepareFormToPersistOrder(_200);
atgSubmitAction({form:form});
};
atg.commerce.csr.order.billing.savePaymentGroups=function(_205){
var form=document.getElementById("csrBillingForm");
atg.commerce.csr.common.enableDisable([{form:"csrBillingForm",name:"csrHandleApplyPaymentGroups"}],[{form:"csrBillingForm",name:"csrPaymentGroupsPreserveUserInputOnServerSide"}]);
atg.commerce.csr.common.prepareFormToPersistOrder(_205);
atgSubmitAction({panelStack:["globalPanels"],form:form});
};
atg.commerce.csr.order.billing.claimClaimables=function(){
atgSubmitAction({form:dojo.byId("csrBillingClaimableForm")});
};
atg.commerce.csr.order.billing.editCreditCard=function(pURL){
atg.commerce.csr.common.submitPopup(pURL,document.getElementById("csrBillingEditCreditCard"),dijit.byId("editPaymentOptionFloatingPane"));
};
atg.commerce.csr.order.billing.addCreditCard=function(){
atgSubmitAction({form:dojo.byId("csrBillingAddCreditCard"),panelStack:["globalPanels"]});
};
atg.commerce.csr.order.billing.renderBillingPage=function(){
atgNavigate({panelStack:"cmcBillingPS"});
};
atg.commerce.csr.order.billing.initializePaymentContainer=function(_206,_207,_208){
container=new atg.commerce.csr.order.billing.CSRPaymentContainer();
container.initialize(_206,_207,_208);
};
atg.commerce.csr.order.billing.addPaymentMethod=function(_209){
dojo.debug("addPaymentMethod: "+_209.paymentGroupId+" amount "+_209.amount);
var _20a=new atg.commerce.csr.order.billing.AvailablePaymentMethod(_209);
container.addAvailablePaymentMethod(_20a);
};
atg.commerce.csr.order.billing.initializeCreditCardTypeDataContainer=function(){
creditCardTypeDataContainer=new atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer();
creditCardTypeDataContainer.initialize();
};
atg.commerce.csr.order.billing.addCreditCardTypeData=function(_20b,_20c){
var _20d=new atg.commerce.csr.order.billing.CreditCardTypeData(_20b,_20c);
creditCardTypeDataContainer.addCreditCardTypeData(_20d);
};
atg.commerce.csr.order.billing.firePaymentBalanceDojoEvent=function(){
container.firePaymentBalanceDojoEvent();
};
atg.commerce.csr.order.billing.paymentBalanceEventListener=function(_20e){
if(!_20e){
return;
}
var _20f;
var _210=_20e.balance;
if((_210*1)===0){
this.disableCheckoutButtons(false);
}else{
this.disableCheckoutButtons(true);
}
balanceDivtag=document.getElementById("displayCSRCustomerPaymentBalance");
if(balanceDivtag){
_20f=this.formatAmount(_210,container.currencyCode);
if(typeof _20f!=="undefined"){
balanceDivtag.innerHTML=_20f;
}
}
};
atg.commerce.csr.order.billing.disableCheckoutButtons=function(_211){
var _212=dijit.byId("checkoutFooterNextButton");
if(_212){
_211?_212.disableButton():_212.enableButton();
}
var _213=dijit.byId("checkoutFooterSaveButton");
if(_213){
_211?_213.disableButton():_213.enableButton();
}
};
atg.commerce.csr.order.billing.recalculatePaymentBalance=function(_214){
if(!_214.pmtWidget){
dojo.debug("The payment widget is not available.");
return;
}
paymentGroupId=_214.pmtWidget.id;
var _215=container.getAvailablePaymentMethodByKey(paymentGroupId);
if(!_215){
return;
}
var _216=_214.pmtWidget;
var _217=_216.getValue()*1;
var _218=_215.amount*1;
var _219=container.balance*1;
var _21a=0*1;
var _21b=null;
if(isNaN(_217)){
var _21c=getResource("csc.billing.invalidAmount");
_216.invalidMessage=_21c;
atg.commerce.csr.common.addMessageInMessagebar("error",_21c);
return false;
}
if(this.isMaxAmountDefined(_215)){
if(this.isMaxAmountReached(_215,_217)){
var _21d=this.calculateRemainingAmount(_215,_217);
if(typeof _21d!=="undefined"&&((_21d*1)===_21a)){
_215.amount=_217;
if(_217>=_218){
container.balance=this.roundAmount(_219+(_217-_218));
}else{
container.balance=this.roundAmount(_219+(_218-_217));
}
}else{
_216.state="Error";
_216._setStateClass();
_216.displayMessage(getResource("csc.billing.invalidMaximumLimit"));
_216.setValue(_218);
_216.validate(false);
}
}else{
_215.amount=_217;
if(_217>=_218){
container.balance=this.roundAmount(_219+(_217-_218));
}else{
container.balance=this.roundAmount(_219+(_218-_217));
}
}
}else{
if(_218||(_218===0)){
_219=this.roundAmount(_219+_218);
}
if(_217||(_217===0)){
_219=this.roundAmount(_219-_217);
_215.amount=_217;
}
container.balance=_219;
}
this.reconsileBalanceAmount();
this.firePaymentBalanceDojoEvent();
return true;
};
atg.commerce.csr.order.billing.reconsileBalanceAmount=function(){
var _21e=container.availablePaymentMethods;
if(!_21e){
container.balance=container.amountDue;
return;
}
var _21f=0;
var i=0;
for(i=0;i<_21e.length;i++){
dojo.debug("before adding amount for paymentGroup"+_21e[i].paymentGroupId+" amount :"+_21f);
_21f=this.roundAmount((_21f*1)+(_21e[i].amount*1));
dojo.debug("after adding amount for paymentGroup"+_21e[i].paymentGroupId+" amount :"+_21f);
}
var _220=this.roundAmount((container.amountDue*1)-(_21f*1));
dojo.debug("Actual balance is ::"+_220);
dojo.debug("Calculated balance is ::"+container.balance);
if(_220==container.balance){
dojo.debug("There is no problem with the calculation");
}else{
dojo.debug("There is a difference with the calculation. Resetting actual amount.");
container.balance=_220;
}
};
atg.commerce.csr.order.billing.applyRemainder=function(_221){
if(!this.csrBillingFormValidate()){
dojo.debug("The payment form contains errors.");
var _222=getResource("csc.billing.form.error");
atg.commerce.csr.common.addMessageInMessagebar("error",_222);
return;
}
if(!_221.pmtWidget){
dojo.debug("The payment widget is not available.");
return;
}
var _223=_221.pmtWidget.id;
var _224=container.getAvailablePaymentMethodByKey(_223);
if(!_224){
return;
}
var _225=_221.pmtWidget;
if(this.isZeroBalance()){
var _222=getResource("csc.billing.zeroBalance");
_225.invalidMessage=_222;
atg.commerce.csr.common.addMessageInMessagebar("warning",_222);
this.firePaymentBalanceDojoEvent();
return false;
}
var _226=container.balance*1;
var _227=_224.amount*1;
var _228=this.roundAmount(_227+_226);
var _229=_228*1;
var _22a=0*1;
var _22b=0*1;
if(this.isMaxAmountDefined(_224)){
if(this.isMaxAmountReached(_224,_227)){
if(_226>=_22a){
_225.state="Error";
_225._setStateClass();
_225.displayMessage(getResource("csc.billing.maxAmountReached"));
_225.setValue(_227);
_225.validate(false);
return false;
}else{
if((_226*-1)>=_227){
_225.setValue(_22a);
_225.validate(false);
_224.amount=_22a;
container.balance=this.roundAmount(_226+_227);
}else{
_22b=this.roundAmount(_226+_227);
_225.setValue(_22b);
_225.validate(false);
_224.amount=_22b;
container.balance=_22a;
}
}
}else{
var _22c=this.calculateRemainingAmount(_224,_227);
if(_22c&&!((_22c*1)===0)){
if(_22c*1>=_226*1){
_225.setValue(_229);
_225.validate(false);
_224.amount=_229;
container.balance=0;
}else{
_22b=this.roundAmount((_227*1)+(_22c*1));
_225.setValue(_22b);
_225.validate(false);
_224.amount=_22b;
container.balance=this.roundAmount((container.balance*1)-(_22c*1));
}
}
}
}else{
if((((_229*1)===0)||((_229*1)>0))){
if(this.isValidAmount(_229,container.currencyCode)){
_225.setValue(_229);
_225.validate(false);
_224.amount=_229;
container.balance=0;
}
}else{
var _222=getResource("csc.billing.negativeAmount");
_225.invalidMessage=_222;
atg.commerce.csr.common.addMessageInMessagebar("error",_222);
}
}
this.reconsileBalanceAmount();
this.firePaymentBalanceDojoEvent();
};
atg.commerce.csr.order.billing.checkInStorePaymentCheckbox=function(_22d,_22e,_22f,_230,_231){
if(_22d.checked){
document.getElementById(_22f).innerHTML=_231;
var _232=container.getAvailablePaymentMethodByKey(_22e);
if(parseInt(_230)){
document.getElementById(_22e).value=_230;
}else{
document.getElementById(_22e).value=container.balance;
}
if(parseInt(_232.amount)){
_232.initialAmount=_232.amount;
}else{
if(!parseInt(_232.initialAmount)&&!parseInt(_232.amount)){
_232.amount=container.balance;
}else{
_232.amount=_232.initialAmount;
}
}
}else{
document.getElementById(_22e).value=0;
document.getElementById(_22f).innerHTML=0;
var _232=container.getAvailablePaymentMethodByKey(_22e);
_232.amount="0.0";
}
atg.commerce.csr.order.billing.reconsileBalanceAmount();
this.firePaymentBalanceDojoEvent();
};
atg.commerce.csr.order.billing.isMaxAmountDefined=function(_233){
if(!_233){
return false;
}
var _234=_233.maxAllowedAmount;
if(!_234){
return false;
}
if(_234==="Infinity"){
return false;
}
if(typeof _234!="undefined"&&((_234*1)>=0*1)){
return true;
}
return false;
};
atg.commerce.csr.order.billing.isMaxAmountReached=function(_235,_236){
if(this.isMaxAmountDefined(_235)){
var _237=_235.maxAllowedAmount;
if((_237*1)<=(_236*1)){
return true;
}
}
return false;
};
atg.commerce.csr.order.billing.calculateRemainingAmount=function(_238,_239){
var _23a;
var _23b=0*1;
if(!atg.commerce.csr.order.billing.isMaxAmountReached(_238,_239)){
_23a=_238.maxAllowedAmount;
if((_23a*1)===(_239*1)){
return _23b;
}else{
return this.roundAmount((_23a*1)-(_239*1));
}
}else{
if(atg.commerce.csr.order.billing.isMaxAmountDefined(_238)){
_23a=_238.maxAllowedAmount;
if((_23a*1)===(_239*1)){
return _23b;
}
}
}
};
atg.commerce.csr.order.billing.existingCreditCardAddressSelectedRule=function(_23c,_23d,_23e){
return atg.commerce.csr.order.billing.selectionRule(_23c,_23d,"true");
};
atg.commerce.csr.order.billing.newCreditCardAddressSelectedRule=function(_23f,_240){
return atg.commerce.csr.order.billing.selectionRule(_23f,_240,"false");
};
atg.commerce.csr.order.billing.selectionRule=function(_241,_242,_243){
var form=document.getElementById(_241);
var _244=form.elements[_242];
if(!_244){
return false;
}
var _245;
if(_244.length===undefined){
_245=_244.value;
}else{
for(i=0;i<_244.length;i++){
if(_244[i].checked){
_245=_244[i].value;
break;
}
}
}
if(_245===_243){
return true;
}else{
return false;
}
};
atg.commerce.csr.order.billing.notifyAddNewCreditCardValidators=function(){
};
atg.commerce.csr.order.billing.notifyEditCreditCardValidators=function(){
};
atg.commerce.csr.order.billing.isValidAmount=function(_246,_247,_248){
if(typeof _246==="undefined"){
return false;
}
if(isNaN(_246)){
result=this.parseAmount(_246,_248);
if(isNaN(result)){
return false;
}else{
return true;
}
}else{
return true;
}
};
atg.commerce.csr.order.billing.isZeroBalance=function(){
if((container.balance*1)===0){
return true;
}
return false;
};
atg.commerce.csr.order.billing.parseAmount=function(_249,_24a){
var _24b;
if(typeof _249==="undefined"){
return;
}
if(typeof _24a=="undefined"){
_24a={places:("places" in container)?container.places:2,fractional:true,locale:container.locale,currency:container.currencyCode,symbol:container.currencySymbol};
}
_24b=dojo.currency.parse(_249,_24a);
return _24b;
};
atg.commerce.csr.order.billing.formatAmount=function(_24c,_24d,_24e,_24f){
if(typeof _24f=="undefined"){
_24f={places:("places" in container)?container.places:2,round:true,locale:container.locale,currency:container.currencyCode,symbol:container.currencySymbol};
}
if(typeof _24c==="undefined"){
return;
}
if(typeof _24d==="undefined"||_24d===""){
return;
}
return dojo.currency.format(_24c,_24f);
};
atg.commerce.csr.order.billing.disableExpiredCreditCardControls=function(_250){
var _251=null;
var _252=null;
var _253=null;
var _254=null;
var _255=0;
var _256=null;
if(_250.paymentWidgetId){
_251=_250.paymentWidgetId;
_253=dijit.byId(_251);
if(_253){
atg.commerce.csr.common.disableTextboxWidget(_253);
_255=_253.getValue();
if((_255*1)>0){
container.balance=(container.balance*1)+(_255*1);
_256=container.getAvailablePaymentMethodByKey(_251);
if(_256){
_256.amount=0;
}
this.reconsileBalanceAmount();
this.firePaymentBalanceDojoEvent();
}
_253.setValue("0.0");
}
}
if(_250.cvv){
_252=_250.cvv;
_254=dijit.byId(_252);
if(_254){
atg.commerce.csr.common.disableTextboxWidget(_254);
}
}
};
atg.commerce.csr.order.billing.saveUserInput=function(_257){
dojo.debug("entering saveUserInput()");
var form=document.getElementById("csrBillingForm");
atg.commerce.csr.common.enableDisable([{form:"csrBillingForm",name:"csrPaymentGroupsPreserveUserInputOnServerSide"}],[{form:"csrBillingForm",name:"csrHandleApplyPaymentGroups"}]);
var d=atgSubmitAction({form:form,handleAs:"json"});
var _258=d.addCallback(function(_259){
return _259;
});
var _25a;
if(_258){
_25a=_258.error;
if(!_25a){
dojo.debug("There is no error in saving user data.");
return true;
}
}
dojo.debug("leaving saveUserInput()");
return false;
};
atg.commerce.csr.order.billing.isValidCreditCardMonth=function(_25b,_25c){
if(!_25b){
return false;
}
var _25d=_25b.getValue();
var _25e=new Number(_25d);
if(_25d===""||!((typeof _25e=="number")||(_25e instanceof Number))){
dojo.debug("Supplied month is not a number.");
_25b.invalidMessage=getResource("csc.billing.invalidMonth");
return false;
}
if(!this.isValidCreditCardYear(_25c)){
return true;
}
if(!this.isValidCreditCardExpDate(_25b,_25c)){
_25b.invalidMessage=getResource("csc.billing.invalidMonth");
return false;
}
return true;
};
atg.commerce.csr.order.billing.isValidCreditCardYear=function(_25f){
if(!_25f){
return false;
}
var year=_25f.getValue();
var _260=new Number(year);
if(year===""||!((typeof _260=="number")||(_260 instanceof Number))){
dojo.debug("Supplied year is not a number.");
_25f.invalidMessage=getResource("csc.billing.invalidYear");
return false;
}
return true;
};
atg.commerce.csr.order.billing.isValidCreditCardExpDate=function(_261,_262){
var _263=_261.getValue();
var year=_262.getValue();
var _264=new Number(_263);
var _265=new Number(year);
dojo.debug("month and year is valid numbers.");
var _266=new Date();
var _267=_264-1;
var _268=new Date(_265,_267);
var _269=dojo.date.getDaysInMonth(_268);
_268=new Date(_265,_267,_269);
if(dojo.date.compare(_266,_268,"day")>0){
dojo.debug("You can't choose the non-current date.");
return false;
}else{
dojo.debug("User selected a future date.");
return true;
}
};
atg.commerce.csr.order.billing.isValidCreditCardNumberInEditContext=function(_26a){
if(!_26a.originalMaskedCreditCardNumber){
return false;
}
if(!_26a.creditCardType){
return false;
}
if(!_26a.creditCardNumber){
return false;
}
var _26b=_26a.creditCardNumber.getValue();
if(_26a.originalMaskedCreditCardNumber===_26b){
return true;
}else{
return this.isValidCreditCardNumber(_26a.creditCardType,_26a.creditCardNumber);
}
};
atg.commerce.csr.order.billing.isValidCreditCardNumber=function(_26c,_26d){
if(!_26c){
return false;
}
if(!_26d){
return false;
}
var _26e=_26c.getValue();
var _26f=_26d.getValue();
if(_26e===""){
dojo.debug("Supplied credit card type is not valid.");
_26c.promptMessage="Please select a valid credit card type.";
return false;
}
if(_26f===""){
dojo.debug("Supplied card number is not valid.");
_26d.invalidMessage=getResource("csc.billing.invalidCreditCardNumber");
return false;
}
var _270=creditCardTypeDataContainer.getCreditCardTypeDataByKey(_26e);
if(!_270){
dojo.debug("Supplied credit card type is not valid.");
_26c.invalidMessage=getResource("csc.billing.invalidCreditCardType");
return false;
}
var code=_270.code;
if(!_270){
dojo.debug("Supplied credit card type is not valid.");
_26c.invalidMessage=getResource("csc.billing.invalidCreditCardType");
return false;
}
if(code=="PL"||dojox.validate.isValidCreditCard(_26f,code)){
dojo.debug("This is a valid credit card number.");
return true;
}else{
dojo.debug("Please provide a valid credit card number.");
_26d.invalidMessage=getResource("csc.billing.invalidCreditCardNumber");
return false;
}
};
atg.commerce.csr.order.billing.AvailablePaymentMethod=function(_271){
this.paymentGroupId=_271.paymentGroupId;
this.paymentGroupType=_271.paymentGroupType;
this.amount=_271.amount;
this.initialAmount=_271.initialAmount;
this.maxAllowedAmount=_271.maxAllowedAmount;
};
atg.commerce.csr.order.billing.CSRPaymentContainer=function(_272,_273,_274,_275){
this.balance=_272;
this.availablePaymentMethods=[];
this.amountDue=_273;
this.currencyCode=_274;
if(_275!=null){
this.locale=_275.locale;
this.currencySymbol=_275.currencySymbol;
this.places=_275.places;
}
this.getAvailablePaymentMethodByKey=function(pKey){
var i=0;
if(!this.availablePaymentMethods){
return null;
}
if(this.availablePaymentMethods.length===undefined){
if(this.availablePaymentMethods.paymentGroupId==pKey){
return this.availablePaymentMethods;
}else{
return null;
}
}else{
for(i=0;i<this.availablePaymentMethods.length;i++){
if(this.availablePaymentMethods[i].paymentGroupId==pKey){
return this.availablePaymentMethods[i];
}
}
return null;
}
};
this.addAvailablePaymentMethod=function(_276){
var _277=this.availablePaymentMethods.length;
this.availablePaymentMethods[_277]=_276;
var _278=_276.amount;
var _279;
if(_278>0){
_279=this.balance;
this.balance=atg.commerce.csr.order.billing.roundAmount((_279*1)-(_278*1));
}
};
this.deleteAvailablePaymentMethodByKey=function(pKey){
};
this.initialize=function(_27a,_27b,_27c){
this.availablePaymentMethods=[];
this.amountDue=_27a;
this.balance=_27a;
this.currencyCode=_27b;
this.locale=_27c.locale;
this.currencySymbol=_27c.currencySymbol;
this.places=_27c.places;
};
this.firePaymentBalanceDojoEvent=function(){
dojo.publish("/atg/commerce/csr/order/PaymentBalance",[{event:"PaymentBalance",balance:this.balance}]);
};
};
atg.commerce.csr.order.billing.CSRCreditCardTypeDataContainer=function(){
this.creditCardTypeData=[];
this.initialize=function(){
this.creditCardTypeData=[];
};
this.getCreditCardTypeDataByKey=function(pKey){
for(var i=0;i<this.creditCardTypeData.length;i++){
if(this.creditCardTypeData[i].cardType==pKey){
return this.creditCardTypeData[i];
}
}
};
this.addCreditCardTypeData=function(_27d){
var _27e=this.creditCardTypeData.length;
this.creditCardTypeData[_27e]=_27d;
};
};
atg.commerce.csr.order.billing.CreditCardTypeData=function(_27f,_280){
this.cardType=_27f;
this.code=_280;
};
atg.commerce.csr.order.billing.CurrencyValidationResultHolder=function(_281,_282,_283){
this.valid=_281;
this.formatted=_282;
this.amount=_283;
};
atg.commerce.csr.order.billing.assignBalance=function(){
var _284=container.availablePaymentMethods;
if(!_284){
dojo.debug(" There is no payment method. Thus leave the method.");
return;
}
if((container.balance*1)===0){
dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
return;
}
var i=0;
var _285;
var _286;
var _287;
var _288;
var _289=container.balance;
var _28a;
for(i=0;i<_284.length;i++){
_285=_284[i].paymentGroupId;
_286=_284[i].amount;
_288=_284[i].maxAllowedAmount;
_28a=dijit.byId(_285);
dojo.debug("Widget Id"+_285+" amount :"+_286+" maxAllowedAmount :"+_288);
dojo.debug("Looping through a paymentGroup with payment group Id ::"+_285);
dojo.debug("Locale value is ::"+container.locale);
if((container.balance*1)===0){
dojo.debug(" The balance is zero. No need to assign the balance to the payment types. Thus leave the method.");
return;
}
if((container.balance*1)>0){
if(_28a&&_288&&_288!=="Infinity"){
_287=this.roundAmount((_288*1)-(_286*1));
if((_287*1)>=(container.balance*1)){
_284[i].amount=container.balance*1;
container.balance=0;
}else{
_284[i].amount=_287*1;
container.balance=this.roundAmount((container.balance*1)-(_287*1));
}
_28a.setValue(_284[i].amount);
_28a.validate(false);
}else{
if(_28a&&((typeof _288==="undefined")||(_288==="Infinity"))){
_284[i].amount=this.roundAmount((_284[i].amount*1)+container.balance);
container.balance=0;
_28a.setValue(_284[i].amount);
_28a.validate(false);
}
}
}else{
if((container.balance*-1)>=(_286*1)){
_284[i].amount=0;
container.balance=this.roundAmount((container.balance*1)+_284[i].amount);
_28a.setValue(_284[i].amount);
_28a.validate(false);
}else{
_284[i].amount=this.roundAmount((_284[i].amount*1)+(container.balance*1));
container.balance=0;
_28a.setValue(_284[i].amount);
_28a.validate(false);
}
}
}
};
atg.commerce.csr.order.billing.isValidCVV=function(_28b){
if(!_28b){
return false;
}
var _28c="????";
var _28d={format:_28c};
var _28e=_28b.getValue();
return dojox.validate.isNumberFormat(_28e,_28d);
};
atg.commerce.csr.order.billing.csrBillingFormValidate=function(){
var _28f=true;
var _290=container.availablePaymentMethods;
if(!_290){
dojo.debug(" There is no payment method. Thus leave the method.");
return true;
}
var _291=null;
var _292=null;
var _293=null;
var _294=null;
for(i=0;i<_290.length;i++){
_291=_290[i].paymentGroupId;
paymentGroupType=_290[i].paymentGroupType;
dojo.debug("Looping through a paymentGroup with payment group Id ::"+_291);
_292=_291+"CVV";
dojo.debug("CVV element name is ::"+_292);
_293=dijit.byId(_292);
_294=dijit.byId(_291);
if(_291&&_293){
if(!atg.commerce.csr.order.billing.isValidCVV(_293)){
var _295=getResource("csc.billing.invalidCVVNumber");
_293.invalidMessage=_295;
atg.commerce.csr.common.addMessageInMessagebar("error",_295);
_28f=false;
break;
}
}
if(_291&&_294){
if(!_294.isValid()&&paymentGroupType!="inStorePayment"){
_28f=false;
break;
}
}
if(_290.length==1&&_291&&paymentGroupType=="inStorePayment"&&!dojo.byId(_291+"_checkbox").checked){
_28f=false;
break;
}
}
if(_28f){
if(atg.commerce.csr.order.billing.isZeroBalance()){
atg.commerce.csr.order.billing.disableCheckoutButtons(false);
}
}else{
atg.commerce.csr.order.billing.disableCheckoutButtons(true);
}
return _28f;
};
atg.commerce.csr.order.billing.roundAmount=function(_296){
dojo.debug("The original amount is ::"+_296);
var _297=dojo.number.round(_296,("places" in container)?container.places:2);
dojo.debug("The rounded original amount is ::"+_297);
return _297;
};
dojo.provide("atg.commerce.csr.order.confirm");
atg.commerce.csr.order.confirm.sendConfirmationMessage=function(){
atgSubmitAction({form:document.getElementById("atg_commerce_csr_sendConfirmationMessageForm")});
};
atg.commerce.csr.order.confirm.saveCustomerProfile=function(_298,_299){
var _29a=dojo.byId("atg_commerce_csr_customerCreateForm");
var _29b=_29a["atg_commerce_csr_confirm_fName"].value;
var _29c=_29a["atg_commerce_csr_confirm_lastName"].value;
_29a["atg.successMessage"].value=dojo.string.substitute(_298,[_29b,_29c]);
_29a["atg.failureMessage"].value=dojo.string.substitute(_299,[_29b,_29c]);
atgSubmitAction({form:_29a,panelStack:["globalPanels"]});
};
atg.commerce.csr.order.confirm.renderProductCatalogPanel=function(){
atgNavigate({panelStack:"cmcCatalogPS"});
};
dojo.provide("atg.commerce.csr.order.finish");
atg.commerce.csr.order.finish.submitOrder=function(_29d){
atgSubmitAction({form:_29d,panelStack:["globalPanels"]});
};
atg.commerce.csr.order.finish.submitAndScehduleOrder=function(_29e){
atgSubmitAction({form:_29e,queryParams:{"contentHeader":true,"cancelScheduleProcess":"reviewSubmitAndSchedule"},panelStack:["globalPanels"]});
};
atg.commerce.csr.order.finish.scheduleOrder=function(_29f){
atgSubmitAction({form:_29f,queryParams:{"contentHeader":true,"cancelPS":"cmcCompleteOrderPS","cancelScheduleProcess":"reviewAndSchedule"},panelStack:["globalPanels"]});
};
atg.commerce.csr.order.finish.submitExchange=function(){
atgSubmitAction({form:document.getElementById("atg_commerce_csr_submitExchangeForm"),panelStack:["globalPanels"]});
};
atg.commerce.csr.order.finish.cancelOrder=function(){
atg.commerce.csr.common.enableDisable([{form:"atg_commerce_csr_finishOrderCancelForm",name:"csrCancelOrderHandler"}],[{form:"atg_commerce_csr_finishOrderCancelForm",name:"csrCancelExchangeOrderHandler"}]);
atgSubmitAction({form:document.getElementById("atg_commerce_csr_finishOrderCancelForm"),panelStack:["globalPanels"],sync:true});
this.hideCancelOrderPrompt();
};
atg.commerce.csr.order.finish.cancelExchangeOrder=function(_2a0){
var _2a1=document.getElementById("atg_commerce_csr_finishOrderCancelForm");
atg.commerce.csr.common.enableDisable([{form:"atg_commerce_csr_finishOrderCancelForm",name:"csrCancelExchangeOrderHandler"}],[{form:"atg_commerce_csr_finishOrderCancelForm",name:"csrCancelOrderHandler"}]);
var _2a2=atgSubmitAction({form:_2a1,panelStack:["globalPanels"]});
var _2a3=atg.commerce.csr.common.getCheckedItem(_2a1.desiredOption);
if(_2a3!==""){
_2a3=_2a3.value;
}
if(_2a3==="cancelExchangeOnly"){
_2a2.addCallback(function(){
atg.progress.update("cmcRefundTypePS");
});
}else{
_2a2.addCallback(function(){
atg.progress.update("cmcExistingOrderPS");
});
}
this.hideCancelOrderPrompt();
};
atg.commerce.csr.order.finish.navActionCancelOrder=function(_2a4){
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"cancelOrderPopup",url:_2a4,onClose:function(args){
}});
};
atg.commerce.csr.order.finish.resizeCancelOrderWindow=function(){
var _2a5=dijit.byId("cancelOrderPopup");
if(_2a5){
_2a5.resizeTo("550","250");
}
};
atg.commerce.csr.order.finish.saveOrder=function(){
atgSubmitAction({form:document.getElementById("atg_commerce_csr_finishOrderSaveForm"),panelStack:["globalPanels"]});
};
atg.commerce.csr.order.finish.createNewOrderNote=function(){
var _2a6=document.getElementById("atg_commerce_csr_order_note_addNewOrderNoteForm");
if(_2a6){
atgSubmitAction({form:_2a6,sync:true});
atg.commerce.csr.common.hidePopupWithReturn("addOrderNotePopup",{result:"ok"});
}
};
atg.commerce.csr.order.finish.editExistingOrder=function(_2a7,_2a8){
var _2a9=document.getElementById(_2a7);
_2a9.orderId.value=_2a8;
atgSubmitAction({form:_2a9,queryParams:{init:true}});
};
atg.commerce.csr.order.finish.hideCancelOrderPrompt=function(){
var _2aa=dijit.byId("cancelOrderPopup");
if(_2aa){
atg.commerce.csr.common.hidePopup(_2aa);
}
};
dojo.provide("atg.commerce.csr.order.gift");
atg.commerce.csr.order.gift.giftlistSelect=function(_2ab){
var _2ac=dojo.byId("atg_commerce_csr_customer_gift_showSelectedGiftlist");
_2ac.giftlistId.value=_2ab;
atgSubmitAction({form:_2ac,panels:["cmcGiftlistsViewP"],queryParams:{"giftlistId":_2ab}});
};
atg.commerce.csr.order.gift.giftlistBuyFrom=function(_2ad){
var _2ae=dojo.byId("atg_commerce_csr_buyFromGiftlist");
_2ae.purchaseGiftlistId.value=_2ad;
atgSubmitAction({form:_2ae,panels:["cmcGiftlistViewPurchaseModeP"],panelStack:["cmcGiftlistSearchPS","globalPanels"],selectTabbedPanels:["cmcGiftlistViewPurchaseModeP"],queryParams:{"giftlistId":_2ad},tab:atg.service.framework.changeTab("commerceTab")});
};
atg.commerce.csr.order.gift.giftlistDelete=function(_2af,_2b0){
var _2b1=dojo.byId("deleteGiftlist");
_2b1.giftlistId.value=_2af;
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"deleteGiftListPopup",title:_2b0,url:"/DCS-CSR/include/gift/giftlistDeleteConfirm.jsp?_windowid="+window.windowId+"&giftlistId="+_2af,onClose:function(args){
if(args.result=="delete"){
atgSubmitAction({panels:["cmcGiftlistsViewP"],panelStack:["globalPanels"]});
}
}});
};
atg.commerce.csr.order.gift.removeItemFromGiftlist=function(_2b2,_2b3,_2b4){
var _2b5=dojo.byId(_2b2);
if(_2b5){
_2b5.giftItemId.value=_2b3;
_2b5.giftlistId.value=_2b4;
atgSubmitAction({form:_2b5,panels:["cmcGiftlistsViewP"],panelStack:["globalPanels"],queryParams:{"giftlistId":_2b4}});
}
};
atg.commerce.csr.order.gift.addWishlistItemToOrder=function(_2b6,_2b7,_2b8,_2b9,_2ba,_2bb,_2bc){
var _2bd=dojo.byId(_2b6);
if(_2bd){
_2bd.catalogRefId.value=_2b7;
_2bd.productId.value=_2b8;
if(_2bc){
_2bd.siteId.value=_2bc;
}
_2bd["atg.successMessage"].value=_2bb;
var _2be=atgSubmitAction({form:_2bd,queryParams:{"giftlistId":_2b9}});
_2be.addCallback(function(){
atg.progress.update("cmcCatalogPS");
});
}
};
atg.commerce.csr.order.gift.addGiftlistItemsToOrder=function(_2bf,_2c0,_2c1){
var _2c2=dojo.byId(_2bf);
if(_2c2){
_2c2["atg.successMessage"].value=_2c1;
atgSubmitAction({form:_2c2,panels:["cmcGiftlistViewPurchaseModeP"],panelStack:["cmcGiftlistSearchPS"],selectTabbedPanels:["cmcGiftlistViewPurchaseModeP"]});
}
};
atg.commerce.csr.order.gift.addItemsToGiftlist=function(_2c3,_2c4,_2c5){
var _2c6=dojo.byId(_2c3);
var _2c7=dojo.query(".quantity-input");
var _2c8=[];
var _2c9=document.getElementById("giftlistSelect");
if(!_2c5){
var _2ca=_2c9.options[_2c9.selectedIndex].value;
}else{
var _2ca=_2c5;
}
dojo.byId(_2c3+"_productId").value=_2c4;
dojo.byId(_2c3+"_selectedGiftlistId").value=_2ca;
for(var x=0;x<_2c7.length;x++){
if((typeof (_2c7[x].value)!="undefined")&&(_2c7[x].value!="")){
var _2cb=dojo.number.parse(_2c7[x].value);
dojo.debug("Parsed giftlist value = "+_2cb);
if(_2cb>0){
input=document.createElement("input");
input.setAttribute("type","hidden");
input.setAttribute("name",_2c7[x].id);
input.setAttribute("value",_2cb);
for(var i=0;i<_2c6.childNodes.length;i++){
var _2cc=_2c6.childNodes[i];
if(_2cc.name&&_2cc.name==input.name){
_2c6.removeChild(_2cc);
}
}
_2c6.appendChild(input);
_2c8.push(_2c7[x].id);
}
}
}
atgSubmitAction({form:_2c6,panelStack:["cmcCatalogPS","globalPanels"],selectTabbedPanels:["cmcProductViewP"],listParams:{catalogRefIds:_2c8},formHandler:"/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"});
};
atg.commerce.csr.order.gift.addItemsToWishlist=function(_2cd,_2ce,_2cf){
var _2d0=dojo.byId(_2cd);
var _2d1=dojo.query(".quantity-input");
var _2d2=[];
dojo.byId(_2cd+"_productId").value=_2ce;
dojo.byId(_2cd+"_selectedGiftlistId").value=_2cf;
for(var x=0;x<_2d1.length;x++){
if((typeof (_2d1[x].value)!="undefined")&&(_2d1[x].value!="")){
var _2d3=dojo.number.parse(_2d1[x].value);
dojo.debug("Parsed giftlist value = "+_2d3);
if(_2d3>0){
input=document.createElement("input");
input.setAttribute("type","hidden");
input.setAttribute("name",_2d1[x].id);
input.setAttribute("value",_2d3);
for(var i=0;i<_2d0.childNodes.length;i++){
var _2d4=_2d0.childNodes[i];
if(_2d4.name&&_2d4.name==input.name){
_2d0.removeChild(_2d4);
}
}
_2d0.appendChild(input);
_2d2.push(_2d1[x].id);
}
}
}
atgSubmitAction({form:_2d0,panelStack:["cmcCatalogPS","globalPanels"],selectTabbedPanels:["cmcProductViewP"],listParams:{catalogRefIds:_2d2},formHandler:"/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler"});
};
atg.commerce.csr.order.gift.updateGiftlistItems=function(_2d5){
var _2d6=dojo.byId("atg_commerce_csr_updateGiftlist");
var _2d7=dojo.query(".quantity-input");
var _2d8=[];
dojo.byId("giftlistId").value=_2d5;
for(var x=0;x<_2d7.length;x++){
if((typeof (_2d7[x].value)!="undefined")&&(_2d7[x].value!="")){
var _2d9=dojo.number.parse(_2d7[x].value);
dojo.debug("Parsed giftlist value = "+_2d9);
if(_2d9>0){
input=document.createElement("input");
input.setAttribute("type","hidden");
input.setAttribute("name",_2d7[x].id);
input.setAttribute("value",_2d9);
for(var i=0;i<_2d6.childNodes.length;i++){
var _2da=_2d6.childNodes[i];
if(_2da.name&&_2da.name==input.name){
_2d6.removeChild(_2da);
}
}
_2d6.appendChild(input);
_2d8.push(_2d7[x].id);
}
}
}
atgSubmitAction({form:_2d6,listParams:{catalogRefIds:_2d8},formHandler:"/atg/commerce/custsvc/gifts/CSRGiftlistFormHandler",panels:["cmcGiftlistsViewP"],panelStack:["globalPanels"],queryParams:{"giftlistId":_2d5}});
};
atg.commerce.csr.order.gift.disableExistingAddress=function(_2db,_2dc){
if(_2dc=="false"){
dojo.byId(_2db+"_existingAddressList").disabled=true;
dojo.byId(_2db+"_addressRadioSelection_existing").disabled=true;
dojo.byId(_2db+"_addressRadioSelection_existing_text").style.color="#aaaaaa";
}
if(_2dc&&!dojo.byId(_2db+"_addressRadioSelection_existing").checked){
dojo.byId(_2db+"_existingAddressList").disabled=true;
dojo.byId(_2db+"_addressRadioSelection_existing_text").style.color="#333333";
}
if(_2dc&&dojo.byId(_2db+"_addressRadioSelection_existing").checked){
dojo.byId(_2db+"_existingAddressList").disabled=false;
dojo.byId(_2db+"_addressRadioSelection_existing_text").style.color="#333333";
}
};
atg.commerce.csr.order.gift.validateEventType=function(_2dd){
if(_2dd<0){
return false;
}
return true;
};
atg.commerce.csr.order.gift.validateGiftlistEventDate=function(_2de,_2df){
var _2e0=dojo.date.locale.parse(_2de,{datePattern:_2df,selector:"date"});
if(_2de==""||!_2e0){
return false;
}
return true;
};
atg.commerce.csr.order.gift.isSearchFormEmpty=function(_2e1,_2e2,_2e3){
var _2e4=dojo.query("input",_2e1);
for(var i=0,_2e5=_2e4.length;i<_2e5;i++){
var item=_2e4[i];
var type=item.type;
if(type=="text"||type=="textarea"||type=="password"){
var _2e6=_2e3?dojo.string.trim(item.value):item.value;
if(_2e6!=""&&_2e6!=_2e2){
return false;
}
}else{
if(type=="checkbox"||type=="radio"){
if(item.checked==true){
return false;
}
}
}
}
var _2e4=dojo.query("select",_2e1);
for(var i=0,_2e5=_2e4.length;i<_2e5;i++){
var item=_2e4[i];
var type=item.type;
if(type.match("select")=="select"){
if(item.value!=""){
return false;
}
}
}
return true;
};
atg.commerce.csr.order.gift.changeSiteContext=function(_2e7,_2e8){
dojo.debug("MultiSite | atg.commerce.csr.common.changeSite called with siteId = "+_2e7);
if(!_2e8){
var _2e9=dojo.byId("atg_commerce_csr_productDetailsForm");
}else{
var _2e9=dojo.byId(_2e8);
}
atgSubmitAction({selectTabbedPanels:["cmcGiftlistsViewP"],panelStack:["cmcCatalogPS","globalPanels"],form:_2e9,sync:true,queryParams:{contentHeader:true,siteId:_2e7}});
};
dojo.provide("atg.commerce.csr.gwp");
atg.commerce.csr.gwp.submitGWPGiftSelectionForm=function(_2ea){
dojo.debug("GWP | Submitting Gift Selection form");
var _2eb=atg.commerce.csr.gwp.getGWPGiftSelectionForm();
atgSubmitAction({form:_2eb});
};
atg.commerce.csr.gwp.getGWPGiftSelectionForm=function(){
dojo.debug("GWP | Getting Gift Selection form");
var _2ec=dojo.byId("gwpMakeGiftSelection");
return _2ec;
};
atg.commerce.csr.gwp.setGWPGiftSelectionFormInputValues=function(_2ed){
dojo.debug("GWP | Setting Form Input Values");
var _2ee=atg.commerce.csr.gwp.getGWPGiftSelectionForm();
atgBindFormValues(_2ee,_2ed);
return _2ee;
};
dojo.provide("atg.commerce.csr.order");
atg.commerce.csr.order.loadExistingOrder=function(_2ef,_2f0){
dojo.debug("atg.commerce.csr.order.loadExistingOrder: orderstate is "+_2f0);
dojo.debug("atg.commerce.csr.order.loadExistingOrder: orderId is "+_2ef);
var _2f1;
if(_2f0=="TEMPLATE"&&atg.commerce.csr.order.scheduled.isScheduledOrders=="true"){
_2f1=dojo.byId("atg_commerce_csr_loadExistingScheduledOrderForm");
}else{
_2f1=dojo.byId("atg_commerce_csr_loadExistingOrderForm");
}
_2f1.orderId.value=_2ef;
atgSubmitAction({form:_2f1,queryParams:{"contentHeader":true}});
};
atg.commerce.csr.order.orderHistoryLoadOrder=function(_2f2){
var _2f3=dojo.byId("atg_commerce_csr_loadExistingOrderForm");
_2f3.orderId.value=_2f2;
atgSubmitAction({form:_2f3,panelStack:["globalPanels","cmcExistingOrderPS"],queryParams:{"contentHeader":true},tab:atg.service.framework.changeTab("commerceTab")});
};
atg.commerce.csr.order.viewExistingOrder=function(_2f4,_2f5){
if(_2f5=="TEMPLATE"&&atg.commerce.csr.order.scheduled.isScheduledOrders=="true"){
var _2f6=dojo.byId("atg_commerce_csr_viewScheduledOrderForm");
_2f6.viewOrderId.value=_2f4;
atgSubmitAction({form:_2f6});
}else{
var _2f6=dojo.byId("atg_commerce_csr_viewExistingOrderForm");
_2f6.viewOrderId.value=_2f4;
atgSubmitAction({form:_2f6});
}
};
atg.commerce.csr.order.findByIdOrder=function(_2f7){
if(isEmpty(_2f7)){
return;
}
var _2f8=document.getElementById("atg_commerce_csr_globalFindOrderByIdForm");
if(_2f8){
_2f8.viewOrderId.value=_2f7;
atgSubmitAction({form:_2f8});
}
};
atg.commerce.csr.order.selectCrossSellLink=function(_2f9,_2fa){
dojo.debug("MultiSite | Selecting cross-sell link");
var _2fb=dojo.byId("atg_commerce_csr_productDetailsForm");
atgSubmitAction({form:_2fb,queryParams:{contentHeader:true,productId:_2f9}});
};
atg.commerce.csr.order.selectCrossSellSku=function(_2fc,_2fd,_2fe,_2ff,_300,_301){
var _302=document.getElementById("productEditLineItem").value;
var _303=function(args){
if(args.result=="ok"&&args.sku!=""){
var _304=document.getElementById("selectCrossSellSkuLinkContainer"+_2fe);
if(_304){
_304.innerHTML="";
var _305=document.createElement("a");
_305.href="#";
_305.innerHTML=args.sku;
_305.onclick=function(args){
atg.commerce.csr.order.selectCrossSellSku(window.atg_commerce_csr_order_editLineItemHandler_productId,window.atg_commerce_csr_order_editLineItemHandler_skuId,window.atg_commerce_csr_order_editLineItemHandler_rowNumber,window.atg_commerce_csr_order_editLineItemHandler_displayName);
};
_304.appendChild(_305);
var _306=document.getElementById("skuId"+_2fe);
if(_306){
_306.value=args.sku;
}
if(document.getElementById("qty"+_2fe)){
var _307=document.getElementById("qty"+_2fe);
if(_307){
_307.disabled=false;
}
}else{
if(document.getElementById("fractqty"+_2fe)){
var _307=document.getElementById("fractqty"+_2fe);
if(_307){
_307.disabled=false;
}
}
}
var _308=document.getElementById("readSkuInfoURL");
if(_308){
dojo.xhrGet({url:_308.value+"&skuId"+"="+args.sku+"&productId"+"="+_2fc,encoding:"utf-8",load:function(data){
var _309=atg.commerce.csr.catalog.createObjectFromJSON(data);
if(_309!=null){
statusItem=document.getElementById("status"+_2fe);
if(statusItem){
statusItem.innerHTML=_309.status;
}
priceItem=document.getElementById("price"+_2fe);
if(priceItem){
priceItem.innerHTML=_309.price;
}
}
},error:function(_30a){
atg.commerce.csr.catalog.showDojoIoBindError(_30a);
},mimetype:"text/plain"});
}
}
}
};
window.atg_commerce_csr_order_editLineItemHandler_skuId=_2fd;
window.atg_commerce_csr_order_editLineItemHandler_productId=_2fc;
window.atg_commerce_csr_order_editLineItemHandler_rowNumber=_2fe;
window.atg_commerce_csr_order_editLineItemHandler_displayName=_2ff;
if(_2fd==""){
var _30b=document.getElementById("skuId"+window.atg_commerce_csr_order_editLineItemHandler_rowNumber);
if(_30b){
_2fd=_30b.value;
}
}
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"editLineItemPopup",title:_2ff,url:""+_302.replace("SKUIDPLACEHOLDER",_2fd)+_2fc,onClose:_303});
};
atg.commerce.csr.order.addCrossSellsToCart=function(){
var _30c=0;
while(document.getElementById("qty"+_30c)||document.getElementById("fractqty"+_30c)){
if(document.getElementById("qty"+_30c)){
var _30d=document.getElementById("qty"+_30c);
if(_30d&&!_30d.value.match(/^[+-]?\d+(\.\d+)?$/)){
_30d.value=0;
}
var _30e=document.getElementsByName("qty"+_30c);
if(_30e){
var _30f=_30e.length;
for(var i=0;i<_30f;i++){
if(_30e[i].value!=_30d.value){
_30e[i].value=_30d.value;
}
}
}
_30e[1].value=_30d.value;
}else{
var _30d=document.getElementById("fractqty"+_30c);
if(_30d&&!_30d.value.match(/^[+-]?\d+(\.\d+)?$/)){
_30d.value=0;
}
var _30e=document.getElementsByName("fractqty"+_30c);
if(_30e){
var _30f=_30e.length;
for(var i=0;i<_30f;i++){
if(_30e[i].value!=_30d.value){
_30e[i].value=_30d.value;
}
}
}
_30e[1].value=_30d.value;
}
_30c=_30c+1;
}
var _310=document.getElementById("addCrossSellsToCartForm");
if(_310){
_310.addItemCount.value=_30c;
atgSubmitAction({panelStack:"cmcShoppingCartPS",form:_310});
}
};
atg.commerce.csr.order.copy=function(_311){
var _312=dojo.byId("atg_commerce_csr_copyOrder");
_312.orderId.value=_311;
atgSubmitAction({form:_312,queryParams:{"contentHeader":true}});
};
atg.commerce.csr.order.skuBrowserAction=function(_313,_314){
atgSubmitAction({panelStack:[_313,"globalPanels"],queryParams:{"productId":_314},form:atg.commerce.csr.common.getEnclosingForm("skuBrowserAction")});
};
atg.commerce.csr.order.crossSellItems=function(_315,_316,_317,_318){
document.getElementById("addCrossSellProductId").value=_315;
document.getElementById("addCrossSellSkuId").value=_316;
document.getElementById("addCrossSellForm")["atg.successMessage"].value=_317;
atgSubmitAction({panelStack:[_318,"globalPanels"],form:document.getElementById("addCrossSellForm")});
};
atg.commerce.csr.order.returnToCustomerInformationPage=function(){
console.debug("Calling atg.commerce.csr.order.returnToCustomerInformationPage");
viewCurrentCustomer("customersTab");
};
atg.commerce.csr.order.returnToOrderSearchPage=function(){
console.debug("Calling atg.commerce.csr.order.returnToOrderSearchPage");
atg.commerce.csr.openPanelStack("cmcOrderSearchPS");
};
atg.commerce.csr.catalog.pickupInStoreAction=function(_319,_31a,_31b){
for(itemKey in window.skuArray){
if(!window.skuArray[itemKey]&&dojo.byId(itemKey).value){
var _31c=dojo.number.parse(dojo.byId(itemKey).value);
dojo.debug("Parsed pickup in store value = "+_31c);
if(_31c>0){
skuId=itemKey;
quantity=_31c;
break;
}
}
}
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:"pickupLocationsPopup",title:_319,url:_31a+"&productId="+_31b+"&skuId="+skuId+"&quantity="+quantity});
};
atg.commerce.csr.catalog.pickupInStoreSearchStores=function(_31d,_31e,_31f,_320){
if(!_31d&&!_31e&&!_31f){
var form=dojo.byId("shippingInStorePickupSearchForm");
var _321=dojo.byId("shippingInStorePickupSearchContainer");
if(_321){
form.countryCode.value=dojo.byId("shippingInStorePickupCountry").value;
form.postalCode.value=dojo.byId("shippingInStorePickupPostalCode").value;
if(dojo.byId("shippingInStorePickupState")){
form.state.value=dojo.byId("shippingInStorePickupState").value;
}
var _322=dojo.byId("shippingInStorePickupPostalCity");
if(_322&&_322.value!=""&&_322.value!="City"){
form.city.value=_322.value;
}
if(form["geoLocatorServiceProviderEmpty"]){
if(form["geoLocatorServiceProviderEmpty"].value=="-1"){
form.distance.value=-1;
}else{
form.distance.value=form["geoLocatorServiceProviderEmpty"].value;
}
}else{
form.distance.value=dojo.byId("shippingInStorePickupProximity").value;
}
}
}else{
var form=dojo.byId("inStorePickupSearchForm");
var _321=dojo.byId("inStorePickupSearchContainer");
if(_321){
form.countryCode.value=dojo.byId("inStorePickupCountry").value;
form.postalCode.value=dojo.byId("inStorePickupPostalCode").value;
if(dojo.byId("inStorePickupState")){
form.state.value=dojo.byId("inStorePickupState").value;
}
var _322=dojo.byId("inStorePickupPostalCity");
if(_322&&_322.value!=""&&_322.value!="City"){
form.city.value=_322.value;
}
if(form["geoLocatorServiceProviderEmpty"]){
if(form["geoLocatorServiceProviderEmpty"].value=="-1"){
form.distance.value=-1;
}else{
form.distance.value=form["geoLocatorServiceProviderEmpty"].value;
}
}else{
form.distance.value=dojo.byId("inStorePickupProximity").value;
}
}
}
if(_320){
if(form["allItemsSuccessURLHidden"]){
form["successURL"].value=form["allItemsSuccessURLHidden"].value;
}
}else{
if(form["successURLHidden"]){
form["successURL"].value=form["successURLHidden"].value;
}
}
if(!_31d&&!_31e&&!_31f){
dojo.xhrPost({form:form,url:atg.commerce.csr.getContextRoot()+"/include/catalog/shippingLocationsSearchResults.jsp?_windowid="+window.windowId,encoding:"utf-8",handle:function(_323,_324){
if(document.getElementById("storesSearchResults")){
document.getElementById("storesSearchResults").innerHTML=_323;
}
},mimetype:"text/html"});
}else{
dojo.xhrPost({form:form,url:atg.commerce.csr.getContextRoot()+"/include/catalog/pickupLocationsResults.jsp?_windowid="+window.windowId,queryParams:{"productId":_31d,"skuId":_31e,"quantity":_31f,"allItems":_320},encoding:"utf-8",preventCache:true,handle:function(_325,_326){
if(document.getElementById("inStorePickupResults")){
document.getElementById("inStorePickupResults").innerHTML=_325;
}
},mimetype:"text/html"});
}
};
atg.commerce.csr.catalog.pickupInStoreCountryChange=function(url,_327){
if(_327){
var _328=dijit.byId("shippingInStorePickupState");
var _329=dijit.byId("shippingInStorePickupCountry");
}else{
var _328=dijit.byId("inStorePickupState");
var _329=dijit.byId("inStorePickupCountry");
}
var _32a=url;
_32a+=_329.getValue();
console.log(_32a);
stateStore=new dojo.data.ItemFileReadStore({url:_32a});
_328.store=stateStore;
_328.setValue("");
if(_327){
dojo.byId("shippingInStorePickupState").value="";
}else{
dojo.byId("inStorePickupState").value="";
}
};
atg.commerce.csr.catalog.pickupInStoreAddToCart=function(_32b,_32c,_32d,_32e){
var form=document.getElementById("inStorePickupForm");
if(form){
if(_32d&&_32e){
form.quantity.value=_32e;
}
form.locationId.value=_32b;
atgSubmitAction({panelStack:["cmcCatalogPS","globalPanels"],panels:["orderSummaryPanel"],form:form,sync:true});
atg.commerce.csr.common.hidePopupWithReturn("inStorePickupForm",{result:"cancel"});
dojo.forEach(dojo.query(".atg_commerce_csr_coreProductViewData input[type=\"text\"]"),function(node){
node.value="";
});
dojo.byId("pickupInStoreAction").disabled="disabled";
var _32f={};
_32f.type="confirmation";
_32f.summary=_32c;
dijit.byId("messageBar").messages.push(_32f);
if(_32d&&_32e){
var _330={};
_330.type="information";
_330.summary=_32d;
dijit.byId("messageBar").messages.push(_330);
dijit.byId("messageBar").refresh(2);
}else{
dijit.byId("messageBar").refresh(1);
}
}
};
atg.commerce.csr.catalog.createInStorePickupShippingGroupForm=function(_331){
var form=document.getElementById("createInStorePickupShippingGroupForm");
if(form){
document.getElementById("locationId").value=_331;
atgSubmitAction({panelStack:["globalPanels"],form:form,sync:true});
atgNavigate({panelStack:"cmcShippingAddressPS",queryParams:{init:"true"}});
}
};
atg.commerce.csr.catalog.checkPickupInStoreButton=function(_332){
var _333=false;
var _334=dojo.byId("pickupInStoreAction");
var _335=dojo.byId("pickupInStoreLabel");
for(itemKey in _332){
if(dojo.byId(itemKey).value&&dojo.byId(itemKey).value>0){
if(!_333){
_333=itemKey;
}else{
_333=false;
break;
}
}
}
if(_333&&!_332[_333]){
_333=true;
}else{
_333=false;
}
if(_333){
_334.disabled=false;
_335.className="hidden";
}else{
_334.disabled=true;
_335.className="visible";
}
};
dojo.provide("atg.commerce.csr.promotion");
atg.commerce.csr.promotion.openPromotionsBrowser=function(_336){
dijit.byId("atg_commerce_csr_promotionsBrowserDialog").closeButtonNode.onclick=atg.commerce.csr.promotion.revertWallet;
dijit.byId("atg_commerce_csr_promotionsBrowserDialog").show();
var _337=atgSubmitAction({showLoadingCurtain:false,sync:true,form:dojo.byId("promotionUpdateForm")});
_336();
var _338=dojo.byId("promotionSearchForm")["/atg/commerce/custsvc/promotion/PromotionSearch.site"].options;
_338.length=0;
for(var i=0;i<atg.commerce.csr.promotion.sites.length;i++){
var site=atg.commerce.csr.promotion.sites[i];
_338[i]=new Option(site.name,site.value,i==0?true:false,false);
}
dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
};
atg.commerce.csr.promotion.checkPromotion=function(_339,_33a){
if(_33a){
dojo.query("."+_339).forEach(function(node,_33b,arr){
node.checked=true;
node.setAttribute("checked",true);
});
var _33c=dojo.byId("promotionExcludeForm");
_33c["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value=_339;
atgSubmitAction({showLoadingCurtain:false,form:_33c});
}else{
dojo.query("."+_339).forEach(function(node,_33d,arr){
node.checked=false;
node.removeAttribute("checked");
});
var _33c=dojo.byId("promotionIncludeForm");
_33c["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value=_339;
atgSubmitAction({showLoadingCurtain:false,form:_33c});
}
};
atg.commerce.csr.promotion.grantPromotion=function(_33e,_33f){
var _340=dojo.byId("promotionGrantForm");
_340["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value=_33e;
atgSubmitAction({showLoadingCurtain:false,sync:true,form:_340});
dijit.byId("atg_commerce_csr_promotionsTabContainer").selectChild(dijit.byId("atg_commerce_csr_availablePromotions"));
dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
_33f();
};
atg.commerce.csr.promotion.removePromotion=function(_341,_342){
var _343=dojo.byId("promotionRemoveForm");
_343["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.stateId"].value=_341;
atgSubmitAction({showLoadingCurtain:false,sync:true,form:_343});
dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
_342();
};
atg.commerce.csr.promotion.revertWallet=function(){
dijit.byId("atg_commerce_csr_promotionsBrowserDialog").hide();
dijit.byId("atg_commerce_csr_promotionsTabContainer").selectChild(dijit.byId("atg_commerce_csr_availablePromotions"));
atgSubmitAction({panelStack:["globalPanels","cmcShoppingCartPS"],form:dojo.byId("revertPromotionWalletForm")});
};
atg.commerce.csr.promotion.saveWallet=function(){
dijit.byId("atg_commerce_csr_promotionsBrowserDialog").hide();
atgSubmitAction({panelStack:["globalPanels","cmcShoppingCartPS"],form:dojo.byId("savePromotionWalletForm")});
};
atg.commerce.csr.promotion.update=function(_344,_345){
_345.disabled=true;
var _346=atgSubmitAction({showLoadingCurtain:false,sync:true,form:dojo.byId("promotionUpdateForm")});
dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
_344();
_345.disabled=false;
};
atg.commerce.csr.promotion.search=function(_347,_348){
this.searchGridInstanceId=_348;
_347({o:atg.commerce.csr.promotion,p:"searchModel"});
};
atg.commerce.csr.promotion.searchModelAllChange=function(){
var _349=null;
if(this.searchGridInstanceId){
_349=eval(this.searchGridInstanceId);
}
if(_349&&_349.dataModel&&_349.dataModel.count>0){
dojo.byId("promotionInstructions").style.display="inline";
}else{
dojo.byId("promotionInstructions").style.display="none";
}
};
dojo.provide("atg.commerce.csr.order.returns");
atg.commerce.csr.order.returns.selectReturnRequest=function(_34a){
atgSubmitAction({panels:["cmcReturnsHistoryP"],form:dojo.byId("transformForm"),queryParams:{"historyReturnRequestId":_34a}});
};
atg.commerce.csr.order.returns.selectOriginatingOrder=function(_34b){
atgSubmitAction({panels:["cmcReturnsHistoryP"],form:dojo.byId("transformForm"),queryParams:{"originatingOrderId":_34b}});
};
atg.commerce.csr.order.returns.resetRefundValues=function(_34c){
var form=document.getElementById("resetRefundValues");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.modifyRefundValues=function(_34d){
var form=document.getElementById("modifyRefundValuesForm");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.initiateReturnProcess=function(_34e){
var form=document.getElementById("csrCreateReturnRequest");
if(_34e.orderId!==undefined&&_34e.orderId!==null){
form.orderId.value=_34e.orderId;
}
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.selectReturnItems=function(_34f){
var form=document.getElementById("csrSelectReturnItems");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.startReturnProcess=function(_350){
var form=document.getElementById("csrSelectReturnItems");
form.processName.value="Return";
var _351=form.startReturnSuccessURL.value;
form.selectItemsSuccessURL.value=_351;
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.startExchangeProcess=function(_352){
var form=document.getElementById("csrSelectReturnItems");
form.processName.value="Exchange";
var _353=form.startExchangeSuccessURL.value;
form.selectItemsSuccessURL.value=_353;
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.cancelReturnRequest=function(_354){
var form=document.getElementById("cancelReturnRequest");
var _355=atgSubmitAction({form:form});
_355.addCallback(function(){
atg.progress.update("cmcExistingOrderPS");
});
};
atg.commerce.csr.order.returns.applyRefunds=function(_356){
var form=document.getElementById("csrApplyRefunds");
atg.commerce.csr.common.enableDisable({form:"csrApplyRefunds",name:"handleApplyRefunds"},{form:"csrApplyRefunds",name:"handleCancelReturnRequest"});
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.submitReturn=function(_357){
var form=document.getElementById("atg_commerce_csr_submitReturnForm");
atg.commerce.csr.common.enableDisable("handleSubmitReturnRequest","handleCancelReturnRequest");
atgSubmitAction({form:form});
};
atg.commerce.csr.order.returns.cancelReturnRequestInRefundPage=function(_358){
var form=document.getElementById("csrApplyRefunds");
atg.commerce.csr.common.enableDisable({form:"csrApplyRefunds",name:"handleCancelReturnRequest"},{form:"csrApplyRefunds",name:"handleApplyRefunds"});
var _359=atgSubmitAction({form:form});
_359.addCallback(function(){
atg.progress.update("cmcExistingOrderPS");
});
};
atg.commerce.csr.order.returns.cancelReturnRequestInCompletePage=function(_35a){
var form=document.getElementById("atg_commerce_csr_submitReturnForm");
atg.commerce.csr.common.enableDisable({form:"atg_commerce_csr_submitReturnForm",name:"handleCancelReturnRequest"},{form:"atg_commerce_csr_submitReturnForm",name:"handleSubmitReturnRequest"});
var _35b=atgSubmitAction({form:form});
_35b.addCallback(function(){
atg.progress.update("cmcExistingOrderPS");
});
};
atg.commerce.csr.order.returns.editCreditCard=function(pURL){
atg.commerce.csr.common.submitPopup(pURL,document.getElementById("csrEditCreditCard"),dijit.byId("editPaymentOptionFloatingPane"));
};
atg.commerce.csr.order.returns.disableReturnProcessButtons=function(_35c){
var _35d=dijit.byId("StartReturnProcess");
if(_35d){
_35c?_35d.disableButton():_35d.enableButton();
}
var _35e=dijit.byId("StartExchangeProcess");
if(_35e){
_35c?_35e.disableButton():_35e.enableButton();
}
};
dojo.provide("atg.commerce.csr.order.scheduled");
atg.commerce.csr.order.scheduled.populateData=function(_35f){
console.debug("atg.commerce.csr.order.scheduled.populateData started");
var _360;
var l;
var _361;
var _362;
var _363=dojo.byId("schedulesTable");
for(var x=0;x<_35f.length;x++){
_360=_35f[x];
for(var y=0;y<_360.length;y++){
_362=_360[y];
_361=_363.rows[x+1].cells;
_361[y].innerHTML=_362;
}
}
};
atg.commerce.csr.order.scheduled.viewScheduleErrors=function(pURL,_364){
console.debug("viewScheduleErrors.pURL: "+pURL);
console.debug("viewScheduleErrors.pURL: testing");
var _365="viewScheduleErrors";
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:_365,title:_364,url:pURL,onClose:function(args){
}});
};
atg.commerce.csr.order.scheduled.createSchedule=function(_366){
var _367=dojo.byId(_366);
atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(_367);
atgSubmitAction({form:_367,panelStack:["globalPanels"]});
};
atg.commerce.csr.order.scheduled.updateSchedule=function(_368){
var _369=dojo.byId(_368);
atg.commerce.csr.order.scheduled.setFormFromScheduleWidget(_369);
atgSubmitAction({form:_369,panelStack:["globalPanels"]});
};
atg.commerce.csr.order.scheduled.setFormFromScheduleWidget=function(_36a){
var _36b=dijit.byId("cscDateRange").scheduleType();
_36a.scheduleType.value=_36b;
console.debug("setFormFromScheduleWidget scheduleType: "+_36b);
var _36c=dijit.byId("cscDateRange").daysOption();
_36a.daysOption.value=_36c;
console.debug("setFormFromScheduleWidget daysOption: "+_36c);
var _36d=dijit.byId("cscDateRange").weeksOption();
_36a.occurrencesOption.value=_36d;
console.debug("setFormFromScheduleWidget occurrencesOption: "+_36d);
var _36e=dijit.byId("cscDateRange").monthsOption();
_36a.monthsOption.value=_36e;
console.debug("setFormFromScheduleWidget monthsOption: "+_36e);
var _36f=dijit.byId("cscDateRange").intervalOption();
_36a.intervalOption.value=_36f;
console.debug("setFormFromScheduleWidget intervalOption: "+_36f);
var _370=dijit.byId("cscDateRange").interval();
_36a.selectedInterval.value=_370;
console.debug("setFormFromScheduleWidget selectedInterval: "+_370);
var _371=dijit.byId("cscDateRange").days();
var days=_371.join(",");
_36a.selectedDays.value=days;
console.debug("setFormFromScheduleWidget selectedDays: "+days);
var _372=dijit.byId("cscDateRange").months();
var _373=_372.join(",");
_36a.selectedMonths.value=_373;
console.debug("setFormFromScheduleWidget selectedMonths: "+_373);
var _374=dijit.byId("cscDateRange").occurrences();
var _375=_374.join(",");
_36a.selectedOccurrences.value=_375;
console.debug("setFormFromScheduleWidget selectedOccurrences: "+_375);
var _376=dijit.byId("cscDateRange").dates();
var _377=_376.join(",");
_36a.selectedDates.value=_377;
console.debug("setFormFromScheduleWidget selectedDates: "+_377);
};
atg.commerce.csr.order.scheduled.cancelCreate=function(_378){
var _379=dojo.byId(_378);
atgSubmitAction({form:_379});
};
atg.commerce.csr.order.scheduled.cancelUpdate=function(_37a){
var _37b=dojo.byId(_37a);
atgSubmitAction({form:_37b});
};
atg.commerce.csr.order.scheduled.loadOrderForAddSchedule=function(_37c){
var _37d=dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleAdd");
_37d.orderId.value=_37c;
atgSubmitAction({form:_37d,queryParams:{cancelScheduleProcess:"createNewSchedule"}});
};
atg.commerce.csr.order.scheduled.loadOrderForChangeSchedule=function(_37e,_37f){
var _380=dojo.byId("atg_commerce_csr_loadScheduledOrderForScheduleChange");
_380.orderId.value=_37e;
atgSubmitAction({form:_380,queryParams:{cancelScheduleProcess:"cancelUpdateSchedule",scheduledOrderId:_37f}});
};
atg.commerce.csr.order.scheduled.activateSchedule=function(_381,_382){
var _383=dojo.byId("atg_commerce_csr_scheduled_activateSchedule");
_383.orderId.value=_381;
_383.scheduledOrderId.value=_382;
atgSubmitAction({form:_383});
};
atg.commerce.csr.order.scheduled.deactivateSchedule=function(_384,_385){
var _386=dojo.byId("atg_commerce_csr_scheduled_deactivateSchedule");
_386.orderId.value=_384;
_386.scheduledOrderId.value=_385;
atgSubmitAction({form:_386});
};
atg.commerce.csr.order.scheduled.submitNow=function(_387){
var _388=dojo.byId("atg_commerce_csr_scheduled_duplicateAndSubmit");
_388.orderId.value=_387;
atgSubmitAction({form:_388});
};
atg.commerce.csr.order.scheduled.loadCreateForm=function(_389){
console.debug("atg.commerce.csr.order.scheduled.loadCreateForm");
var _38a=new atg.csc.dateRangePicker({},dojo.byId("cscDateRange"));
var _38b=dojo.byId(_389);
atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(_38b);
_38a.render();
console.debug("atg.commerce.csr.order.scheduled.loadCreateForm DONE");
};
atg.commerce.csr.order.scheduled.loadUpdateForm=function(_38c){
var _38d=new atg.csc.dateRangePicker({},dojo.byId("cscDateRange"));
var _38e=dojo.byId(_38c);
atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm(_38e);
_38d.render();
};
atg.commerce.csr.order.scheduled.setScheduleWidgetFromForm=function(_38f){
var _390=_38f.scheduleType.value;
console.debug("setScheduleWidgetFromForm scheduleTypeInput: "+_390);
dijit.byId("cscDateRange").scheduleType(_390);
var _391=_38f.daysOption.value;
console.debug("setScheduleWidgetFromForm daysOption: "+_391);
dijit.byId("cscDateRange").daysOption(_391);
var _392=_38f.occurrencesOption.value;
console.debug("setScheduleWidgetFromForm occurrencesOption: "+_392);
dijit.byId("cscDateRange").weeksOption(_392);
var _393=_38f.monthsOption.value;
console.debug("setScheduleWidgetFromForm monthsOption: "+_393);
dijit.byId("cscDateRange").monthsOption(_393);
var _394=_38f.intervalOption.value;
console.debug("setScheduleWidgetFromForm intervalOption: "+_394);
dijit.byId("cscDateRange").intervalOption(_394);
var _395=_38f.selectedInterval.value;
console.debug("setScheduleWidgetFromForm selectedInterval: "+_395);
dijit.byId("cscDateRange").interval(_395);
var _396=_38f.selectedDays.value;
var days=_396.split(",");
console.debug("setScheduleWidgetFromForm days: "+days);
dijit.byId("cscDateRange").days(days);
var _397=_38f.selectedOccurrences.value;
var _398=_397.split(",");
console.debug("setScheduleWidgetFromForm occurrences: "+_398);
dijit.byId("cscDateRange").occurrences(_398);
var _399=_38f.selectedMonths.value;
var _39a=_399.split(",");
console.debug("setScheduleWidgetFromForm months: "+_39a);
dijit.byId("cscDateRange").months(_39a);
var _39b=_38f.selectedDates.value;
var _39c=_39b.split(",");
console.debug("setScheduleWidgetFromForm dates: "+_39c);
dijit.byId("cscDateRange").dates(_39c);
};
dojo.provide("atg.commerce.csr.order.shipping");
atg.commerce.csr.order.shipping.applySingleShippingGroup=function(_39d){
atg.commerce.csr.common.prepareFormToPersistOrder(_39d);
atgSubmitAction({form:dojo.byId("singleShippingAddressForm")});
};
atg.commerce.csr.order.shipping.saveApplySingleShippingGroup=function(_39e){
var form=dojo.byId("singleShippingAddressForm");
atg.commerce.csr.common.prepareFormToPersistOrder(_39e);
atgSubmitAction({panelStack:["globalPanels"],form:dojo.byId("singleShippingAddressForm")});
};
atg.commerce.csr.order.shipping.applyMultipleShippingGroup=function(_39f){
atg.commerce.csr.common.prepareFormToPersistOrder(_39f);
atg.commerce.csr.common.enableDisable("csrHandleApplyShippingGroups","csrPreserveUserInputOnServerSide");
atgSubmitAction({form:dojo.byId("csrMultipleShippingAddressForm")});
};
atg.commerce.csr.order.shipping.applySelectShippingMethods=function(_3a0){
dojo.debug("entering applySelectShippingMethods()");
atg.commerce.csr.common.prepareFormToPersistOrder(_3a0);
atgSubmitAction({form:dojo.byId("csrSelectShippingMethods")});
};
atg.commerce.csr.order.shipping.addShippingAddress=function(){
atgSubmitAction({form:dojo.byId("csrAddShippingAddress"),sync:true});
atgNavigate({panelStack:"cmcShippingAddressPS",queryParams:{init:"true"}});
};
atg.commerce.csr.order.shipping.addElectronicAddress=function(){
atgSubmitAction({form:dojo.byId("csrAddElectronicAddress"),sync:true});
atgNavigate({panelStack:"cmcShippingAddressPS",queryParams:{init:"true"}});
};
atg.commerce.csr.order.shipping.editShippingAddress=function(pURL){
atg.commerce.csr.common.submitPopup(pURL,dojo.byId("csrEditShippingAddressForm"),dijit.byId("csrEditAddressFloatingPane"));
};
atg.commerce.csr.order.shipping.renderShippingPage=function(_3a1){
atgNavigate({panelStack:"cmcShippingAddressPS",queryParams:{mode:_3a1}});
};
atg.commerce.csr.order.shipping.splitQtyPrompt=function(pURL,_3a2){
var _3a3="csrMultipleShippingFloatingPane";
if(_3a3){
atg.commerce.csr.common.showPopupWithReturn({popupPaneId:_3a3,title:_3a2||"",url:pURL});
}
};
atg.commerce.csr.order.shipping.cancelSplitQtyPrompt=function(_3a4){
var _3a5=dijit.byId("csrMultipleShippingFloatingPane");
if(_3a5){
atg.commerce.csr.common.hidePopup(_3a5);
var _3a6=atgSubmitAction({form:"transformForm",panelStack:"cmcShippingAddressPS",queryParams:{"select":"multiple"}});
_3a6.addCallback(function(){
var _3a7=dijit.byId("atg_commerce_csr_shipToMultipleAddresses");
if(_3a7&&!_3a7.open){
_3a7.toggle();
}
});
}
};
atg.commerce.csr.order.shipping.splitShippingGroupQty=function(pURL){
atg.commerce.csr.common.submitPopup(pURL,dojo.byId("csrSplitShippingGroupQty"),dijit.byId("csrMultipleShippingFloatingPane"));
};
atg.commerce.csr.order.shipping.newShippingGroupSelectedRule=function(){
var _3a8=dojo.byId("singleShippingAddressForm").singleShippingShipToAddressNickname;
if(!_3a8){
return false;
}
var _3a9;
if(_3a8.length===undefined){
_3a9=_3a8.value;
}else{
for(var i=0;i<_3a8.length;i++){
if(_3a8[i].checked){
_3a9=_3a8[i].value;
break;
}
}
}
if(_3a9=="atg_nsg_nickname"){
return true;
}else{
return false;
}
};
atg.commerce.csr.order.shipping.notifySingleShippingValidators=function(){
};
atg.commerce.csr.order.shipping.notifyEditShippingAddressValidators=function(){
};
atg.commerce.csr.order.shipping.notifyAddShippingAddressValidators=function(){
};
dojo.provide("atg.commerce.csr.pricing.priceLists");
dojo.require("dojo.date.locale");
atg.commerce.csr.pricing.priceLists={selectPriceList:function(_3aa,_3ab){
if(_3aa=="setPriceListForm"){
var form=document.getElementById("setPriceListForm");
if(form){
form.priceListId.value=_3ab;
}
}else{
var form=document.getElementById("setSalePriceListForm");
if(form){
form.salePriceListId.value=_3ab;
}
}
if(form){
atgSubmitAction({form:form,selectTabbedPanels:["cmcProductCatalogSearchP"],sync:true});
}
},searchForPriceLists:function(_3ac){
var _3ad=dojo.date.locale.format(dojo.date.add(new Date(),"year",19),{datePattern:_3ac,selector:"date"});
var _3ae=dojo.date.locale.format(new Date(-1,0,1),{datePattern:_3ac,selector:"date"});
var _3af=document.getElementById("morePriceListsStartDateInput").value;
var _3b0=document.getElementById("morePriceListsEndDateInput").value;
if(_3af!=""&&_3af!=_3ac){
if(!dojo.date.locale.parse(_3af,{datePattern:_3ac,selector:"date"})){
document.getElementById("morePriceListsStartDate").value=_3ad;
}else{
document.getElementById("morePriceListsStartDate").value=_3af;
}
}else{
document.getElementById("morePriceListsStartDate").value="";
}
if(_3b0!=""&&_3b0!=_3ac){
if(!dojo.date.locale.parse(_3b0,{datePattern:_3ac,selector:"date"})){
document.getElementById("morePriceListsEndDate").value=_3ae;
}else{
var _3b1=dojo.date.add(dojo.date.locale.parse(_3b0,{datePattern:_3ac,selector:"date"}),"day",1);
document.getElementById("morePriceListsEndDate").value=dojo.date.locale.format(_3b1,{datePattern:_3ac,selector:"date"});
}
}else{
document.getElementById("morePriceListsEndDate").value="";
}
atg.commerce.csr.pricing.priceLists.morePriceLists.searchRefreshGrid();
}};
dojo.provide("framework.FrameworkLink");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.declare("framework.FrameworkLink",[dijit._Widget,dijit._Templated],{panelStack:"",templateString:"<span><a dojoAttachPoint='containerNode' dojoAttachEvent='onclick: onClick'></a></span>",onClick:function(evt){
if(this.panelStack){
atgSubmitAction({panelStack:this.panelStack,form:dojo.byId("transformForm")});
}
}});
dojo.provide("atg.csc.dateRangePicker");
dojo.declare("atg.csc.dateRangePicker",[dijit._Widget,dijit._Templated,dijit._Container],{widgetsInTemplate:true,ppData:"",templatePath:atg.commerce.csr.getContextRoot()+"/script/widget/templates/dateRangePicker.jsp",parentForm:{},_scheduleType:"Interval",_intervalOption:"days",_interval:1,_daysOption:"allDays",_weeksOption:"allOccurrences",_occurrences:new Array(),_days:new Array(),_dates:new Array(),_monthsOption:"allMonths",_months:new Array(),_daysSelectHandle:"",_datesSelectHandle:"",_monthsSelectHandle:"",_weeksSelectHandle:"",postCreate:function(){
this._daysSelectHandle=dojo.subscribe("atg/csc/scheduleorder/daysSelect",this,"onWeekDays");
this._datesSelectHandle=dojo.subscribe("atg/csc/scheduleorder/datesSelect",this,"onDates");
this._monthsSelectHandle=dojo.subscribe("atg/csc/scheduleorder/monthsSelect",this,"onMonths");
this._weeksSelectHandle=dojo.subscribe("atg/csc/scheduleorder/weeksSelect",this,"onOccurrences");
this.inherited("postCreate",arguments);
dojo.byId("atg_dateRangePicker_intervalTab").style.position="static";
dojo.byId("atg_dateRangePicker_calendarTab").style.position="static";
},destroy:function(){
dojo.unsubscribe(this._daysSelectHandle);
dojo.unsubscribe(this._datesSelectHandle);
dojo.unsubscribe(this._monthsSelectHandle);
dojo.unsubscribe(this._weeksSelectHandle);
},toggleHandler:function(e){
console.debug("toggleHandler: "+e);
},scheduleType:function(_3b2){
if(_3b2!=null){
console.debug("SET scheduleType",_3b2);
this._scheduleType=_3b2;
}else{
if(dijit.byId("atg_dateRangePicker_intervalTab").selected){
return "Interval";
}else{
return "Calendar";
}
return this._scheduleType;
}
},intervalOption:function(_3b3){
if(_3b3!=null){
console.debug("SET intervalOption",_3b3);
this._intervalOption=_3b3;
}else{
return this._intervalOption;
}
},interval:function(_3b4){
if(_3b4!=null){
console.debug("SET interval",_3b4);
this._interval=_3b4;
}else{
return this._interval;
}
},daysOption:function(_3b5){
if(_3b5!=null){
console.debug("SET daysOption",_3b5);
this._daysOption=_3b5;
}else{
return this._daysOption;
}
},weeksOption:function(_3b6){
if(_3b6!=null){
console.debug("SET weeksOption",_3b6);
this._weeksOption=_3b6;
}else{
return this._weeksOption;
}
},occurrences:function(_3b7){
if(_3b7!=null){
console.debug("SET occurrences",_3b7);
this._occurrences=_3b7;
}else{
return this.parseIntArray(this._occurrences);
}
},days:function(_3b8){
if(_3b8!=null){
console.debug("SET days",_3b8);
this._days=_3b8;
}else{
return this.parseIntArray(this._days);
}
},dates:function(_3b9){
if(_3b9!=null){
console.debug("SET dates",_3b9);
this._dates=_3b9;
}else{
return this.parseIntArray(this._dates);
}
},monthsOption:function(_3ba){
if(_3ba!=null){
console.debug("SET monthsOption",_3ba);
this._monthsOption=_3ba;
}else{
return this._monthsOption;
}
},months:function(_3bb){
if(_3bb!=null){
console.debug("SET months",_3bb);
this._months=_3bb;
}else{
return this.parseIntArray(this._months);
}
},parseIntArray:function(_3bc){
var _3bd=new Array();
var item=0;
for(i=0;i<_3bc.length;i++){
item=_3bc[i]+"";
if((!isNaN(item)&&(item!=""))){
_3bd.push(parseInt(item));
}
}
return _3bd;
},onSelectChild:function(obj){
console.debug("onSelectChild");
console.debug(obj.id);
this.selectedTab=obj.id;
if(this.selectedTab=="intervalTab"){
dojo.byId("atg_schedOrder_interval").checked=true;
this.scheduleType("Interval");
}else{
dojo.byId("atg_schedOrder_calendar").checked=true;
this.scheduleType("Calendar");
}
},onSelectChange:function(e){
console.debug("TARGET:"+e.target.value);
var _3be=e.target.name;
dojo.query(".expandedContent").forEach(function(_3bf){
var _3c0=dojo.query("input[name="+_3be+"]",_3bf.parentNode);
if(_3c0.length!=0){
_3bf.style.display="none";
dojo.query("select",_3bf).forEach(function(_3c1){
_3c1.selectedIndex=-1;
});
}
});
switch(e.target.value){
case "everyDay":
this.daysOption("allDays");
break;
case "selectDay":
this.daysOption("selectedDays");
break;
case "selectDate":
this.daysOption("selectedDates");
break;
case "everyMonth":
this.monthsOption("allMonths");
break;
case "selectMonth":
this.monthsOption("selectedMonths");
break;
default:
break;
}
dojo.query(".expandedContent",e.target.parentNode.parentNode).forEach(function(_3c2){
_3c2.style.display="block";
});
},render:function(e){
console.debug("dateRangePicker.render()"+this.scheduleType());
if(this._scheduleType=="Interval"){
dijit.byId("atg_dateRangePicker_intervalTab").selected=true;
dijit.byId("atg_dateRangePicker_calendarTab").selected=false;
dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_intervalTab"));
this.selectedTab="atg_schedOrder_interval";
}else{
dijit.byId("atg_dateRangePicker_intervalTab").selected=false;
dijit.byId("atg_dateRangePicker_calendarTab").selected=true;
dijit.byId("atg_dateRangePickerTabs").selectChild(dijit.byId("atg_dateRangePicker_calendarTab"));
this.selectedTab="atg_schedOrder_calendar";
}
console.debug("render: update interval.value");
this.intervalValue.value=this.interval();
var _3c3=this.intervalSelect.options;
for(i=0;i<_3c3.length;i++){
if(_3c3[i].value==this.intervalOption()){
_3c3[i].selected=true;
}
}
if(this.daysOption()=="allDays"){
this.everyDayOption.checked=true;
this.selectDaysPanel.style.display="none";
this.selectDatesPanel.style.display="none";
console.debug("allDays");
}else{
if(this.daysOption()=="selectedDays"){
this.selectDaysOption.checked=true;
this.selectDaysPanel.style.display="block";
this.selectDatesPanel.style.display="none";
console.debug("selectedDays");
}else{
if(this.daysOption()=="selectedDates"){
this.selectDatesOption.checked=true;
this.selectDaysPanel.style.display="none";
this.selectDatesPanel.style.display="block";
console.debug("selectedDates");
}
}
}
if(this.weeksOption()=="allOccurrences"){
this.allOccurrencesOption.checked=true;
this.selectedOccurrencesOption.checked=false;
this.weeksSelectPanel.style.display="none";
}else{
this.allOccurrencesOption.checked=false;
this.weeksSelectPanel.style.display="block";
this.selectedOccurrencesOption.checked=true;
}
if(this.monthsOption()=="allMonths"){
this.allMonthsOption.checked=true;
this.selectedMonthsOption.checked=false;
console.debug("hiding monthsSelect");
this.monthsSelectPanel.style.display="none";
}else{
this.allMonthsOption.checked=false;
this.selectedMonthsOption.checked=true;
console.debug("showing monthsSelect");
this.monthsSelectPanel.style.display="block";
}
var _3c4=dijit.registry;
for(var id in _3c4._hash){
var item=_3c4._hash[id];
if(item.declaredClass=="atg.csc.toggleLink"){
var _3c5=new Array();
switch(item.type){
case "month":
var _3c5=this.months();
break;
case "day":
var _3c5=this.days();
break;
case "week":
var _3c5=this.occurrences();
break;
case "date":
var _3c5=this.dates();
break;
default:
break;
}
for(j=0;j<_3c5.length;j++){
if(_3c5[j]==item.value){
item.show();
}
}
}
}
},onIntervalOption:function(e){
console.debug("Interval Option:"+e.target.value);
options=this.intervalSelect;
for(i=0;i<options.length;i++){
if(options[i].selected==true){
this.intervalOption(options[i].value);
}
}
console.debug("intervalOption:"+this.intervalOption());
},onOccurrences:function(item){
console.debug("Occurrence:"+item.value);
if(item.toggleState==1){
this._occurrences[this._occurrences.length]=item.value;
}else{
this._occurrences=this.occurrences();
var _3c6=this.getItemIndex(this.occurrences(),item.value);
this._occurrences.splice(_3c6,1);
}
},onWeekDays:function(item){
console.debug("onWeekDays: "+item.label+" : "+this.days());
if(item.toggleState==1){
this._days[this._days.length]=item.value;
}else{
this._days=this.days();
var _3c7=this.getItemIndex(this.days(),item.value);
this._days.splice(_3c7,1);
}
console.debug("days array: "+this.days());
},onDay:function(e){
console.debug("WeekDay:"+e.target.id);
},onDates:function(item){
console.debug("Day:"+item.value);
if(item.toggleState==1){
this._dates[this._dates.length]=item.value;
}else{
this._dates=this.dates();
var _3c8=this.getItemIndex(this.dates(),item.value);
this._dates.splice(_3c8,1);
}
console.debug("dates:"+this.dates().join(","));
},onMonths:function(item){
console.debug("month:"+item.value);
if(item.toggleState==1){
this._months[this._months.length]=item.value;
}else{
this._months=this.months();
var _3c9=this.getItemIndex(this.months(),item.value);
this._months.splice(_3c9,1);
}
console.debug("months:"+this.months().join(","));
},onWeekSelect:function(e){
var _3ca=e.target.name;
this.weeksOption("selectedOccurrences");
dojo.query(".atg_dateRangePicker_weeksSelect",e.target.parentNode.parentNode).forEach(function(_3cb){
_3cb.style.display="block";
});
},onAllWeeksSelect:function(e){
var _3cc=e.target.name;
dojo.byId("weekListing").style.display="none";
this.weeksOption("allOccurrences");
},onScheduleType:function(e){
if(dojo.byId("atg_schedOrder_interval").checked){
this.scheduleType("Interval");
}else{
this.scheduleType("Calendar");
}
},onInterval:function(e){
console.debug(this.intervalValue.value);
var _3cd=this.intervalValue.value;
var _3ce="";
for(i=0;i<_3cd.length;i++){
if(!isNaN(_3cd.charAt(i))){
_3ce+=_3cd.charAt(i);
}
}
this.intervalValue.value=_3ce;
this.interval(_3cd);
},getItemIndex:function(list,obj){
for(var i=0;i<list.length;i++){
if(list[i]==obj){
return i;
}
}
return -1;
},hijackForm:function(){
this.parentForm=this.getParentOfType(this.domNode,["FORM"]);
this.formSubmitButton=dojo.query("input[type=\"submit\"]",this.parentForm)["0"];
dojo.connect(this.formSubmitButton,"onclick",this,"submitClicked");
},submitClicked:function(e){
},isTag:function(node,tags){
if(node&&node.tagName){
var _3cf=node.tagName.toLowerCase();
for(var i=0;i<tags.length;i++){
var _3d0=String(tags[i]).toLowerCase();
if(_3cf==_3d0){
return _3d0;
}
}
}
return "";
},getParentOfType:function(node,tags){
while(node){
if(this.isTag(node,tags).length){
return node;
}
node=node.parentNode;
}
return null;
},sanitySaver:""});
dojo.provide("atg.csc.toggleLink");
dojo.declare("atg.csc.toggleLink",[dijit._Widget,dijit._Templated,dijit._Container],{widgetsInTemplate:true,ppData:"",templatePath:atg.commerce.csr.getContextRoot()+"/script/widget/templates/toggleLink.jsp",parentForm:{},toggleState:0,value:0,label:"",type:"",eventTopic:"",postCreate:function(){
this.linkPoint.innerHTML=this.label;
this.inherited("postCreate",arguments);
},show:function(){
if(this.toggleState==0){
this.toggleState=1;
this.linkContainer.className="atg_commerce_csr_selectedItem";
}else{
this.toggleState=0;
this.linkContainer.className="atg_commerce_csr_nonSelectedItem";
}
},selectLink:function(e){
this.show();
dojo.publish("atg/csc/scheduleorder/"+this.eventTopic,[this]);
console.debug("ToggleLink:"+this.label+":"+this.value+":"+this.toggleState);
},isTag:function(node,tags){
if(node&&node.tagName){
var _3d1=node.tagName.toLowerCase();
for(var i=0;i<tags.length;i++){
var _3d2=String(tags[i]).toLowerCase();
if(_3d1==_3d2){
return _3d2;
}
}
}
return "";
},getParentOfType:function(node,tags){
while(node){
if(this.isTag(node,tags).length){
return node;
}
node=node.parentNode;
}
return null;
},sanitySaver:""});

