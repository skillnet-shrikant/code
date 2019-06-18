// -------------------------------------------------------------------
// Keyboard navigation topics for Service
// -------------------------------------------------------------------
atg.keyboard.registerServiceTopics = function () {
  
  dojo.subscribe("/atg/service/keyboardShortcut/respondTab", null, function() { 
    atgChangeTab(atg.service.framework.changeTab('respondTab'),'communicateNextSteps','respondPanels',['nextStepsPanel']);
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/minimizeMaximizeUtilities", null, function() { 
    atg.service.framework.toggleSidebar();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/ticketsTab", null, function() { 
    atgChangeTab(atg.service.framework.changeTab('ticketsTab'),'ticketSearchNextSteps','ticketSearchPanels',['nextStepsPanel']);
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/customersTab", null, function() { 
    viewCurrentCustomer('customersTab');
  });

  dojo.subscribe("/atg/service/keyboardShortcut/activeTickets", null, function() { 
    showActiveTicketsPopup();
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/ticketDetails", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/addNote", null, function() { 
    atg.service.ticketing.addNotePrompt();atg.service.framework.cancelEvent(event); 
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/addCallNote", null, function() { 
    atg.service.ticketing.addCallActivityPrompt();atg.service.framework.cancelEvent(event);
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/startCall", null, function() { 
    atg.service.framework.startCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/endCall", null, function() { 
    atg.service.framework.endCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/endCallStartNew", null, function() { 
    atg.service.framework.endAndStartCall();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/maxMinGlobalContextArea", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/customerInformationPanel", null, function() { 
    viewCurrentCustomer('customersTab');
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/createNewCustomer", null, function() { 
    createNewCustomer();
    return false;
  });
  
  dojo.subscribe("/atg/service/keyboardShortcut/searchForProfile", null, function() { 
    showCustomerSearch();
    return false;
  });

  dojo.subscribe("/atg/service/keyboardShortcut/maxMinUtilitiesPane", null, function() { 
  });

  dojo.subscribe("/atg/service/keyboardShortcut/backToTicketSearch", null, function() { 
    backToTicketSearch();
    return false;
  });
  
  dojo.subscribe("/atg/csc/keyboardShortcut/dockUndockCurrentPanel", null, function() { 
  });

  dojo.subscribe("/atg/csc/keyboardShortcut/minResCurrentPanel", null, function() { 
  });

}

dojo.addOnLoad(atg.keyboard.registerServiceTopics);