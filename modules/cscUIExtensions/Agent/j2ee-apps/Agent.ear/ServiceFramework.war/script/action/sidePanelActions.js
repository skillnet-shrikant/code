function viewRecentTickets() {
  var theForm = document.getElementById("viewRecentTicketsForm");
  if (theForm) {
	  theForm.type.value = window.ticketing.ticketAccessType;
	  atgSubmitAction({
	    form: theForm,
	    panels: ["sideViewRecentTicketsPanel"]
	  });
	}
}

