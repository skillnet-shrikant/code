// -------------------------------------------------------------------
// Keyboard navigation topics for Commerce Service Center
// -------------------------------------------------------------------
atg.keyboard.registerCSCTopics = function () {
	
  dojo.subscribe("/atg/service/keyboardShortcut/commerceTab", null, function() { 
    atg.commerce.csr.openPanelStackWithTab('cmcShoppingCartPS','commerceTab');
	});
	
  
  dojo.subscribe("/atg/csc/keyboardShortcut/createNewOrder", null, function() { 
    atg.commerce.csr.createOrder(); 
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/searchForOrder", null, function() { 
    atg.commerce.csr.openPanelStackWithTab('cmcOrderSearchPS','commerceTab');
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/productCatalog", null, function() { 
    atg.commerce.csr.openPanelStackWithTabbedPanel('cmcCatalogPS','cmcProductCatalogSearchP','commerceTab');
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shoppingCart", null, function() { 
    var _node = dojo.byId("keyboardShortcutShoppingCart");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipping", null, function() { 
    var _node = dojo.byId("keyboardShortcutShipping");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/billing", null, function() { 
    var _node = dojo.byId("keyboardShortcutBilling");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/completeOrder", null, function() { 
    var _node = dojo.byId("Complete Order");
    if (_node != null) {
      atg.commerce.csr.order.finish.submitOrder('atg_commerce_csr_finishOrderSubmitForm');
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipToMultiple", null, function() { 
    var _node = dojo.byId("atg_commerce_csr_shipToMultipleAddresses");
    if (_node != null) {
      addressGrid.render();
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/shipToOneAddress", null, function() { 
    var _node = dojo.byId("singleShipping");
    if (_node != null) {
      atgNavigate({panelStack:'cmcShippingAddressPS', queryParams:{'mode':'singleShipping'}}); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/saveOrder", null, function() { 
    var _node = dojo.byId("orderSave");
    if (_node != null) {
      atg.commerce.csr.commitOrder('globalCommitOrderForm');
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/orderDetails", null, function() { 
    var _node = dojo.byId("orderLinkAnchor");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/cancelOrder", null, function() { 
    var _node = dojo.byId("orderCancel");
    if (_node != null) {
      _node.onclick(); 
    }
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/createReturnExchange", null, function() { 
    var _node = dojo.byId("createReturnExchange");
    if (_node != null) {
      _node.onclick(); 
    }
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/createAppeasementProcess", null, function() { 
	    var _node = dojo.byId("createAppeasementProcess");
	    if (_node != null) {
	      _node.onclick(); 
	    }
	  });

  dojo.subscribe("/atg/service/keyboardShortcut/customerDetails", null, function() { 
    viewCurrentCustomer('commerceTab');
  });
  
}

dojo.addOnLoad(atg.keyboard.registerCSCTopics);