// -------------------------------------------------------------------
// Keyboard shortcut map for Commerce Service Center
// -------------------------------------------------------------------
atg.keyboard.registerCSCShortcuts = function () {

  atg.keyboard.registerShortcut(
  "ALT+7", {
    shortcut: "ALT + 7",
    name: getResource("keyboard.service.commerceTab.name"),
    description: getResource("keyboard.service.commerceTab.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/service/keyboardShortcut/commerceTab",
    notify: true
   });
	
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+S", {
    shortcut: "CTRL + ALT + SHIFT + S",
    name: getResource("keyboard.csc.searchOrder.name"),
    description: getResource("keyboard.csc.searchOrder.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/searchForOrder",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "ALT+SHIFT+F2", {
    shortcut: "ALT + SHIFT + F2",
    name: getResource("keyboard.csc.productCatalog.name"),
    description: getResource("keyboard.csc.productCatalog.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/productCatalog",
    notify: true
  });

  //Changing shortcut from ALT+SHIFT+S to ALT+SHIFT+G due to IE8 Key reservations  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+G", {
    shortcut: "ALT + SHIFT + G",
    name: getResource("keyboard.csc.shipping.name"),
    description: getResource("keyboard.csc.shipping.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/shipping",
    notify: true
  });

  //Changing shortcut from ALT+SHIFT+B to ALT+SHIFT+I due to IE8 key reservations
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+I", {
    shortcut: "ALT + SHIFT + I",
    name: getResource("keyboard.csc.billing.name"),
    description: getResource("keyboard.csc.billing.description"),
    area: getResource("keyboard.area.commerce"),
    topic: "/atg/csc/keyboardShortcut/billing",
    notify: true
  });
  
  
};

dojo.addOnLoad(atg.keyboard.registerCSCShortcuts);