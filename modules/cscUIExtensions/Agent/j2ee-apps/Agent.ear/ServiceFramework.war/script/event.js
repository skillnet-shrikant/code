/*************************************************************************
//
// event.js File
//
// (C) Copyright 1997-2004 Primus Knowledge Solutions, Inc.
// All rights reserved.
//
*************************************************************************/

var g_objCurrentEvent = new EventContainer();

function EventContainer()
{
  // Initialize Properties
  //
  this.event       = "";
  this.xCoordinate  = "";
  this.yCoordinate  = "";
  this.windowObject = "";

  // Initialize Methods
  //
  this.saveEvent    = EventContainerSaveEvent;
}

function EventContainerSaveEvent(e, xPos, yPos, nLeftOffset, nTopOffset)
{
  this.event = (e) ? e : window.event;

  g_objCurrentEvent.xCoordinate = xPos + nLeftOffset;
  g_objCurrentEvent.yCoordinate = yPos + nTopOffset;

  g_objCurrentEvent.windowObject   = this;
  g_objCurrentEvent.event          = this.event;   
}
