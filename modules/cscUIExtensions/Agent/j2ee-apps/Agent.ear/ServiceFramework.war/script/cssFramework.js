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
// cssFramework.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// Defines client-side framework behavior
//
*************************************************************************/
function frameworkSetColumnStyles()
{
  var columnContainer            = dojo.byId("columns");
  var researchColumn             = dojo.byId("column2");
  var sidebarColumn              = dojo.byId("column3");
  var contentColumn              = dojo.byId("contentColumn");
  var column1Widget = dijit.byId("column1");
  var column2Widget = dijit.byId("column2");
  var column3Widget = dijit.byId("column3");

  var needRelayout = false;

  if (!columnContainer){
    return;
  }

  if (atg.service.framework.showResearch && atg.service.framework.researchColumnAccess) 
  {
    if (dojo.style(column2Widget.domNode, "display") == "none") {
      dojo.style(column2Widget.domNode, "display", "block");
      needRelayout = true;
    }
    if (atg.service.framework.isResearchBarOpen)
    {
      // research bar is open, remove any styles that mark it closed
      if (dojo.hasClass(columnContainer, "close_BC")) {
        dojo.removeClass(columnContainer, "close_BC");
        needRelayout = true;
      }
      else if (dojo.hasClass(columnContainer, "close_B")) {
        dojo.removeClass(columnContainer, "close_B");
        needRelayout = true;
      }
      // now check if the side bar is open
      if (atg.service.framework.isSideBarOpen)
      {
        // research bar is open, side bar is open.  We've already removed close_BC and close_B, so remove close_C
        if (dojo.hasClass(columnContainer, "close_C")) {
          dojo.removeClass(columnContainer, "close_C");
          needRelayout = true;
        }
      }
      else
      {
        // sidebar is closed.  That means that we need to set add close_C
        dojo.addClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
    else
    {
      // the research bar is closed
      if (atg.service.framework.isSideBarOpen)
      {
        // research closed, side open = close_B.  Remove close_BC
        if (dojo.hasClass(columnContainer, "close_BC")) {
          dojo.removeClass(columnContainer, "close_BC");
          dojo.addClass(columnContainer, "close_B");
          needRelayout = true;
        }
        if (!dojo.hasClass(columnContainer, "close_B")) {
          dojo.addClass(columnContainer, "close_B");
          needRelayout = true;
        }
      }
      else
      {
        // research closed, sidebar closed = close_BC
        if (dojo.hasClass(columnContainer, "close_C")) {
          dojo.removeClass(columnContainer, "close_C");
          dojo.addClass(columnContainer, "close_BC");
          needRelayout = true;
        }
        if (!dojo.hasClass(columnContainer, "close_BC")) {
          dojo.addClass(columnContainer, "close_BC");
          needRelayout = true;
        }
      }
    }
  }
  else
  {
    if (dojo.style(column2Widget.domNode, "display") != "none") {
      dojo.style(column2Widget.domNode, "display", "none");
      needRelayout = true;
    }
    if (atg.service.framework.isSideBarOpen)
    {
      if (dojo.hasClass(columnContainer, "close_C")) {
        dojo.removeClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
    else
    {
      if (!dojo.hasClass(columnContainer, "close_C")) {
        dojo.addClass(columnContainer, "close_C");
        needRelayout = true;
      }
    }
  }
  if (needRelayout) {
    cssSetFrameworkHeight();
  }
}

function frameworkCloseSidebar()
{
  if (atg.service.framework.isSideBarOpen)
  {
    atg.service.framework.toggleSidebar();
  }
}

function frameworkOpenSidebar()
{
  if (!atg.service.framework.isSideBarOpen)
  {
    atg.service.framework.toggleSidebar();
  }
}

function frameworkAddEvent( obj, type, fn )
{
  dojo.connect(obj, "on" + type, fn);
}

// get an events trigger cross browser //

function getEventSrc(event) {
 // get a reference to the IE/windows event object
 var e = (event) ? event : window.event;

 // DOM-compliant name of event source property
 if (e.target) {
   return e.target;
 }
 // IE/windows name of event source property
 else if (e.srcElement){
   return e.srcElement;
 }
}

function cssSetFrameworkHeight()
{
  var startCFH = new Date();

  dijit.byId('wholeWindow').resize();
  if(dijit.byId('contributePanelLayout')){
    dijit.byId('contributePanelLayout').resize();
  }

  var endCFH = new Date();
  console.debug("cssSetFrameworkHeight took " + (endCFH - startCFH));
}
