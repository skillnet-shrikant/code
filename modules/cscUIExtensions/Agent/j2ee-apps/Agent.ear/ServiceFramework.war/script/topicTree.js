//*************************************************************************
//
// topicTree.js File
//
// (C) Copyright 1997-2009 ATG, Inc.
// All rights reserved.
//
// This page defines the topic tree object. This object is just a base
// for the topic tree.  All display is done in the topicLoad.jsp page.  
//
//*************************************************************************

function TopicTree(id, div)
{
	if (div != null && div != "undefined") {
		this.topicTree = div;
	}
	else {
		this.topicTree = document.createElement("div");
	}
  
  // Set tree properties
  //
  this.topicTree.id                    = id;
  this.topicTree.form                  = null;
  this.topicTree.locale                = "";
  this.topicTree.selectAction          = new Function();
  this.topicTree.openAction            = new Function();
  this.topicTree.closeAction           = new Function();
  this.topicTree.loadAction            = new Function();
  
  
  // Set Tree methods
  //  
  this.topicTree.openBranch            = topicTreeOpenBranch;
  this.topicTree.selectBranch          = topicTreeSelectBranch;
  this.topicTree.closeBranch           = topicTreeCloseBranch;
  this.topicTree.load                  = topicTreeLoad;
  
  return this.topicTree;
}

function topicTreeOpenBranch(topicId, insertId, treeId, closedIcon, openIcon)
{
  var tree                      = document.getElementById(treeId);
  
  // Change the tree branch icon
  //
  var openIconImg               = document.getElementById(openIcon);
  openIconImg.style.display     = "";
  var closedIconImg             = document.getElementById(closedIcon);
  closedIconImg.style.display   = "none";
  
  // Are the children allready loaded?
  // If so, just show them, if not, reload
  //
  var insertArea            = document.getElementById(insertId);
  if (insertArea.style.display != "none")
    tree.load(topicId, insertId);
  else
    insertArea.style.display = "";
    
  // Call integration function
  //
  tree.openAction(topicId);
}

function topicTreeSelectBranch(topicId, topicName, treeId)
{
  var tree                      = document.getElementById(treeId);
  tree.selectAction(topicId, topicName, treeId); 
}

function topicTreeCloseBranch(displayId, treeId, closedIcon, openIcon)
{
  var tree                  = document.getElementById(treeId);
  var displayArea           = document.getElementById(displayId);
  
  
  // Change the tree branch icon
  //
  var closedIconImg            = document.getElementById(closedIcon);
  closedIconImg.style.display  = "";
  var openIconImg              = document.getElementById(openIcon);
  openIconImg.style.display    = "none";
  
  displayArea.style.display = "none";
}

function topicTreeLoad(topicId, targetId)
{
  var tree                  = this;
  var theForm               = tree.form;
  
  if (theForm.id && (theForm.innerHTML == ""))
  {
    theForm = document.getElementById(theForm.id);
  }
  
  if (theForm)
  {
    theForm.targetId.value = targetId;
    if (topicId != "") theForm.topicId.value = topicId;
    theForm.treeId.value = tree.id;
		theForm["parameterMap.topicsLocale"].value = tree.locale;
	  atgSubmitAction({
	  	form: theForm
	  });
  }
}