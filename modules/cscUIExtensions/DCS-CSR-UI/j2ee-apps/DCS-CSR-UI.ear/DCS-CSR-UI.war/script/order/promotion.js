dojo.provide("atg.commerce.csr.promotion");
atg.commerce.csr.promotion.openPromotionsBrowser = function(gridFunction) {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').closeButtonNode.onclick = atg.commerce.csr.promotion.revertWallet
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').show();
  var deferred = atgSubmitAction({
    showLoadingCurtain:false,
    sync:true,
    form:dojo.byId("promotionUpdateForm")
  });
  gridFunction();
  var options = dojo.byId("promotionSearchForm")["/atg/commerce/custsvc/promotion/PromotionSearch.site"].options;
  options.length = 0;
  for (var i = 0; i < atg.commerce.csr.promotion.sites.length; i++) {
    var site = atg.commerce.csr.promotion.sites[i];
    options[i] = new Option(site.name, site.value, i == 0 ? true : false, false)
  }
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
};
atg.commerce.csr.promotion.checkPromotion = function (promotionId, isChecked){
  if (isChecked) {
		dojo.query("." + promotionId).forEach(function(node, index, arr){
      node.checked = true;
      node.setAttribute("checked", true);
		});
    var formElement  = dojo.byId("promotionExcludeForm");
    formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
    atgSubmitAction({
      showLoadingCurtain:false,
      form:formElement
    });
  }
  else {
		dojo.query("." + promotionId).forEach(function(node, index, arr){
      node.checked = false;
      node.removeAttribute("checked");
		});
    var formElement  = dojo.byId("promotionIncludeForm");
    formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
    atgSubmitAction({
      showLoadingCurtain:false,
      form:formElement
    });
  }
};
atg.commerce.csr.promotion.grantPromotion = function (promotionId, gridFunction){
  var formElement  = dojo.byId("promotionGrantForm");
  formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.promotionId"].value = promotionId;
  atgSubmitAction({
    showLoadingCurtain:false,
    sync:true, // we have to wait until this is done before rendering the grid to avoid stale data in the grid
    form:formElement
  });
  dijit.byId('atg_commerce_csr_promotionsTabContainer').selectChild(dijit.byId('atg_commerce_csr_availablePromotions'));
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
  gridFunction();
};
atg.commerce.csr.promotion.removePromotion = function (stateId, gridFunction){
  var formElement  = dojo.byId("promotionRemoveForm");
  formElement["/atg/commerce/custsvc/promotion/PromotionWalletFormHandler.stateId"].value = stateId;
  atgSubmitAction({
    showLoadingCurtain:false,
    sync:true, // we have to wait until this is done before rendering the grid to avoid stale data in the grid
    form:formElement
  });
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
  gridFunction();
};
atg.commerce.csr.promotion.revertWallet = function () {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').hide();
  dijit.byId('atg_commerce_csr_promotionsTabContainer').selectChild(dijit.byId('atg_commerce_csr_availablePromotions'));
  atgSubmitAction({
    panelStack: ["globalPanels", "cmcShoppingCartPS"],
    form:dojo.byId("revertPromotionWalletForm")
  });
};
atg.commerce.csr.promotion.saveWallet = function () {
  dijit.byId('atg_commerce_csr_promotionsBrowserDialog').hide();
  atgSubmitAction({
    panelStack: ["globalPanels", "cmcShoppingCartPS"],
    form:dojo.byId("savePromotionWalletForm")
  });
};
atg.commerce.csr.promotion.update = function (gridFunction, button) {
	button.disabled = true;
	var deferred = atgSubmitAction({
    showLoadingCurtain:false,
    sync:true,
    form:dojo.byId("promotionUpdateForm")
  });
  dijit.byId("atg_commerce_csr_promotionsOrderSummary").refresh();
	gridFunction();
  button.disabled = false;
};
atg.commerce.csr.promotion.search = function (gridFunction, gridInstanceId) {
	this.searchGridInstanceId = gridInstanceId;
  gridFunction({o: atg.commerce.csr.promotion, p: "searchModel"});
};
atg.commerce.csr.promotion.searchModelAllChange = function () {
  var gridInstance = null;
	if (this.searchGridInstanceId) {
		gridInstance = eval(this.searchGridInstanceId);
  }
  if (gridInstance && gridInstance.dataModel && gridInstance.dataModel.count > 0) {
    dojo.byId('promotionInstructions').style.display='inline';
  }
  else {
    dojo.byId('promotionInstructions').style.display='none';
  }
};
