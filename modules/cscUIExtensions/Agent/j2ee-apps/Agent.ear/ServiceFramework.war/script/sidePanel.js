 /* Copyright (C) 1999-2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 * </ATGCOPYRIGHT>
 */
/*************************************************************************
//
// Service.Framework sidePanel.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side side panel behavior
//
*************************************************************************/

//##########################################################################
// 
// Methods for Ticketing Panel
//
//##########################################################################
function openByIdTicket(idValue){
  if(isEmpty(idValue))
    return;
  document.getElementById('globalViewTicketForm').ticketId.value=idValue;
  viewTicket('globalViewTicketForm');
}      
function openTicketTab(){
  var tab = document.getElementById("ticketsTab");
  if (!tab)
    return;
  // If not Already on Find tab? then switch to ticketTab
  if (!(tab.className && tab.className == "current"))
  {
    ticketsTab();
  }
}
function openCustomerInfo(idValue){
  if(isEmpty(idValue))
    return;
  document.getElementById('globalViewCustomerForm').customerId.value=idValue;      
  viewCustomer('globalViewCustomerForm');
}
function openWorkedTickets(){
  window.ticketing = new Object();
  window.ticketing.ticketAccessType="worked";     
  viewRecentTickets();
}
function openViewedTickets(){
  window.ticketing = new Object();
  window.ticketing.ticketAccessType="viewed";  
  viewRecentTickets();
}

//##########################################################################
// 
// Utility Functions
//
//##########################################################################
function isEmpty(strVal){
  if (strVal==null || strVal =="" || strVal.length==0)
    return true;
  strVal=trim(strVal);
  if (strVal.length==0)
    return true;
  else
    return false;
}
function trim(strVal) {
  // removing leading spaces
  while (strVal.substring(0,1) == ' ') {
    strVal = strVal.substring(1,strVal.length);
  }
  // removing trailing spaces
  while (strVal.substring(strVal.length-1,strVal.length) == ' ') {
    strVal = strVal.substring(0,strVal.length-1);
  }
  return strVal;
}
function replaceEscapeCharacters(oriStr){
  var escStr = oriStr.replace(/\\\\'/g, "'");
  escStr = escStr.replace(/\\\\\"/g, "\"");
  return escStr;
}

function eventTicketFind(e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var ev  = e || e.event;
  if (ev != null) {
    if (ev.keyCode == 13) {
        openByIdTicket(document.getElementById('OPTBID').OPBIDTicketText.value);
        dojo.stopEvent(ev);
        return false;
    }
  }
}

function eventOrderFind(e) {
  // On IE you get the window passed in the 
  // param. Use the event in the window object
  var ev  = e || e.event;
  if (ev != null) {
    if (ev.keyCode == 13) {
        atg.commerce.csr.order.findByIdOrder(escape(document.getElementById('OPBIDOrder').OPBIDOrderText.value));
        dojo.stopEvent(ev);
        return false;
    }
  }
}
