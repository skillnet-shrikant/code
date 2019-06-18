// -------------------------------------------------------------------
// Keyboard shortcut map for Service
// -------------------------------------------------------------------
atg.keyboard.registerServiceShortcuts = function () {

  atg.keyboard.registerShortcut(
  "ALT+F1", {
    shortcut: "ALT + F1",
    name: getResource("keyboard.service.help.name"),
    description: getResource("keyboard.service.help.description"),
    area: getResource("keyboard.area.workspace"),
    action: function () {atg.keyboard.showKeyboardShortcutHelpWindow();},
    notify: false
  });

  atg.keyboard.registerShortcut(
  "CTRL+ALT+U", {
    shortcut: "CTRL + ALT + U",
    name: getResource("keyboard.service.minimizeUtilities.name"),
    description: getResource("keyboard.service.minimizeUtilities.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/minimizeMaximizeUtilities",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "CTRL+SHIFT+L", {
    shortcut: "CTRL + SHIFT + L",
    name: getResource("keyboard.service.openFirebug.name"),
    description: getResource("keyboard.service.openFirebug.name"),
    area: getResource("keyboard.area.workspace"),
    action: function () {dojo.toggleConsole();},
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+F11", {
    shortcut: "ALT + F11",
    name: getResource("keyboard.service.startCall.name"),
    description: getResource("keyboard.service.startCall.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/startCall",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+F12", {
    shortcut: "ALT + F12",
    name: getResource("keyboard.service.endCall.name"),
    description: getResource("keyboard.service.endCall.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/endCall",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+F12", {
    shortcut: "ALT + SHIFT + F12",
    name: getResource("keyboard.service.endCallStartNew.name"),
    description: getResource("keyboard.service.endCallStartNew.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/endCallStartNew",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+N", {
    shortcut: "ALT + SHIFT + N",
    name: getResource("keyboard.service.addNote.name"),
    description: getResource("keyboard.service.addNote.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/addNote",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+N", {
    shortcut: "CTRL + ALT + SHIFT + N",
    name: getResource("keyboard.service.addCallNote.name"),
    description: getResource("keyboard.service.addCallNote.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/addCallNote",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+K", {
    shortcut: "ALT + SHIFT + K",
    name: getResource("keyboard.service.ticketSearch.name"),
    description: getResource("keyboard.service.ticketSearch.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/backToTicketSearch",
    notify: true
  });
  
  //Changed ALT+SHIFT+C to ALT+SHIFT+U due to IE8 incompatability  
  atg.keyboard.registerShortcut(
  "ALT+SHIFT+U", {
    shortcut: "ALT + SHIFT + U",
    name: getResource("keyboard.service.searchProfile.name"),
    description: getResource("keyboard.service.searchProfile.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/searchForProfile",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+3", {
    shortcut: "ALT + 3",
    name: getResource("keyboard.service.respondTab.name"),
    description: getResource("keyboard.service.respondTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/respondTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "ALT+5", {
    shortcut: "ALT + 5",
    name: getResource("keyboard.service.ticketsTab.name"),
    description: getResource("keyboard.service.ticketsTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/ticketsTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+1", {
    shortcut: "CTRL + ALT + 1",
    name: getResource("keyboard.service.activeTickets.name"),
    description: getResource("keyboard.service.activeTickets.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/activeTickets",
    notify: true
  });

  atg.keyboard.registerShortcut(
  "ALT+6", {
    shortcut: "ALT + 6",
    name: getResource("keyboard.service.customersTab.name"),
    description: getResource("keyboard.service.customersTab.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/customersTab",
    notify: true
  });
  
  atg.keyboard.registerShortcut(
  "CTRL+ALT+SHIFT+C", {
    shortcut: "CTRL + ALT + SHIFT + C",
    name: getResource("keyboard.service.newProfle.name"),
    description: getResource("keyboard.service.newProfle.description"),
    area: getResource("keyboard.area.workspace"),
    topic: "/atg/service/keyboardShortcut/createNewCustomer",
    notify: true
  });
  
};

dojo.addOnLoad(atg.keyboard.registerServiceShortcuts);