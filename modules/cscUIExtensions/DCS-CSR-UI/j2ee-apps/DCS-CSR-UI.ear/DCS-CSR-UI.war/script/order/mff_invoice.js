dojo.provide( "atg.commerce.csr.order.invoice" );

/**
* Send an Order Invoice email.
*/
atg.commerce.csr.order.invoice.sendInvoiceEmail = function (){
 atgSubmitAction({
   form:document.getElementById("csrSendInvoiceMessage")
 });
};