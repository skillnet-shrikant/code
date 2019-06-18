/*
  Custom Dojo Fixes that depend on other Agent application javascript customizations.
  If the Dojo fix can apply directly to the core level dojo, it belongs in the WebUI dojo-fixes.js

*/

/* 
--------------------------------------------------------------------
This variable determines whether to output all the Agent Dojo Fixes 
debug statements to Firebug. It is extremely useful when trying to debug 
grid issues, but may overwhelm the console in cases where these
debug statements are not necessary
-------------------------------------------------------------------- 
*/

var isAgentDojoFixesDebug = false;

consoleDebugAgentDojoFixes = function(_debugContents) {
  if (isAgentDojoFixesDebug) {
    console.debug(_debugContents);
  }
}


/* 
--------------------------------------------------------------------
Grid AutoHeight Functionality 
-------------------------------------------------------------------- 
*/


/* 
--------------------------------------------------------------------
Grid Rows
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.rows");
dojo.extend(dojox.grid.rows, {
   defaultRowHeight: 1, // lines 
   overRow: -2, 

   // metrics 
   getHeight: function(inRowIndex){ 
		return ''; 
   }, 
  

   getDefaultHeightPx: function(){ 
     consoleDebugAgentDojoFixes("Grid Rows: getDefaultHeightPx()");
     // summmary: 
     // retrieves the default row height 
     // returns: int, default row height 
    
      consoleDebugAgentDojoFixes("Grid Rows: getDefaultHeightPx() | grid.contentPixelToEmRatio = (): " + this.grid.contentPixelToEmRatio);
    
     // If the contentPixelToEmRatio value is null, assume that the value is 32
     if (this.grid.contentPixelToEmRatio == null) {
       return 32;
     }
    
     return Math.round(this.defaultRowHeight * this.linesToEms * this.grid.contentPixelToEmRatio); 
   }

});

/* 
--------------------------------------------------------------------
Grid View
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.view");
dojo.extend(dojox.GridView, {
	resizeHeight: function(){
	  consoleDebugAgentDojoFixes("Grid View: resizeHeight(): " + this.domNode.clientHeight);
		if(!this.grid._autoHeight){
			var h = this.domNode.clientHeight;
			consoleDebugAgentDojoFixes("Grid View: resizeHeight() | clientHeight = " + h);
			consoleDebugAgentDojoFixes("Grid View: resizeHeight() | scrollbar width = " + dojox.grid.getScrollbarWidth());
			
			if(!this.hasScrollbar()){ // no scrollbar is rendered
				h -= dojox.grid.getScrollbarWidth();
			}
			dojox.grid.setStyleHeightPx(this.scrollboxNode, h);
		}
	},
	
	
	renderRow: function(inRowIndex){
	  consoleDebugAgentDojoFixes("Grid View: renderRow()");
		var rowNode = this.createRowNode(inRowIndex);
		this.buildRow(inRowIndex, rowNode);
		this.grid.edit.restore(this, inRowIndex);
		return rowNode;
	},
	
	updateRow: function(inRowIndex){
	  consoleDebugAgentDojoFixes("Grid View: updateRow()");
		var rowNode = this.getRowNode(inRowIndex);
		if(rowNode){
			rowNode.style.height = '';
			this.buildRow(inRowIndex, rowNode);
		}
		return rowNode;
	}
	
});


/* 
--------------------------------------------------------------------
Grid Views
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.views");
dojo.extend(dojox.grid.views, {

	updateRow: function(inRowIndex){
		consoleDebugAgentDojoFixes("Grid Views: updateRow()");
		consoleDebugAgentDojoFixes("Grid Views: view = " + this.views[i]);
		for(var i=0, v; v=this.views[i]; i++){
			v.updateRow(inRowIndex);
		}
		this.renormalizeRow(inRowIndex);
		
	}
});


/* 
--------------------------------------------------------------------
Grid Scroller
--------------------------------------------------------------------
*/
dojo.require("dojox.grid._grid.scroller");
dojo.extend(dojox.grid.scroller, {
	
	defaultRowHeight: 45,
	averageRowHeight: 45, // the average height of a row, preset to the default
	// rendering implementation
	renderPage: function(inPageIndex){
		var nodes = [];
		consoleDebugAgentDojoFixes("Grid Scroller: colCount = " + this.colCount);
		for(var i=0; i<this.colCount; i++){
			nodes[i] = this.pageNodes[i][inPageIndex];
		}

    consoleDebugAgentDojoFixes("Grid Scroller: rowsPerPage = " + this.rowsPerPage);
    consoleDebugAgentDojoFixes("Grid Scroller: rowCount = " + this.rowCount);
    consoleDebugAgentDojoFixes("Grid Scroller: inPageIndex*this.rowsPerPage = " + inPageIndex*this.rowsPerPage);
		for(var i=0, j=inPageIndex*this.rowsPerPage; (i<this.rowsPerPage)&&(j<this.rowCount); i++, j++){
			this.renderRow(j, nodes);
		}
		
	},
	
	calculateAverageRowHeight: function(){
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight()");
		// Calculate the average row height and update the defaults (row and page). 
		if (!((this.page == 0) && (this.pageTop == 0))) {
		  this.needPage(this.page, this.pageTop); 
		}
    var rowsOnPage = 0;    
    if(this.page < this.pageCount - 1 || (this.rowCount % this.rowsPerPage) == 0 ){
      rowsOnPage = this.rowsPerPage;
    }else{
      rowsOnPage = (this.rowCount % this.rowsPerPage);
    }
		
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | rowsOnPage = " + rowsOnPage);
		var pageHeight = this.getPageHeight(this.page);
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | pageHeight = " + pageHeight);
		pageHeight += 15;
		consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | adding 15 to pageHeight");
		this.averageRowHeight = (pageHeight > 0 && rowsOnPage > 0) ? (pageHeight / rowsOnPage) : 0; 
		consoleDebugAgentDojoFixes("Grid Scroller: averageRowHeight = " + this.averageRowHeight);
		
		// Insert if statement to test for valid Scroller
		if (dojox.grid._Scroller) {
		  consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | scroller detected");
		  this.defaultRowHeight = this.averageRowHeight || dojox.grid._Scroller.prototype.defaultRowHeight; 
		  this.defaultPageHeight = this.defaultRowHeight * this.rowsPerPage;
		  consoleDebugAgentDojoFixes("Grid Scroller: calculateAverageRowHeight() | defaultRowHeight = " + this.defaultRowHeight + ", defaultPageHeight = " + this.defaultPageHeight);
		}
	}
	
});


/* 
--------------------------------------------------------------------
Virtual Grid
--------------------------------------------------------------------
*/

dojo.require("dojox.grid.VirtualGrid");
dojo.extend(dojox.VirtualGrid, {
	
	showHorizontalScrollbar: false,    // Set to true in order to always show the horizontal scrollbar at the bottom of the grid
	autoHeight: "8",                   // number | boolean  Set to the max desired rows to show at a time or true to grow to fit container

	_perRowPaddingAdjustment: 6,       // Per row Adjustment to correct auto measurement inaccuracies
	_scrollPaddingAdjustment: 55,      // total amount of padding to calibrate the full scroll properly
	_heightPaddingAdjustment: 16,      // needed to account for horizontal scroll measurement calibration
	
  // sizing
  resize: function(){
    consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoheight = " + this.autoHeight);
    // summary:
    //    Update the grid's rendering dimensions and resize it
    
    // if we have set up everything except the DOM, we cannot resize
    if(!this.domNode || !this.domNode.parentNode){
      return;
    }
    
    if(!this.domNode.parentNode || this.domNode.parentNode.nodeType != 1){
      return;
    }
    
    // useful measurement
    var padBorder = dojo._getPadBorderExtents(this.domNode);

    if ((this.model != null) && (this.model.count != null)) {
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | testing model :" + this.model);
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | model.count :" + this.model.count);
      // Whether or not to hide the horizontal scrollbar
      if(this.model.count > 0){
        if((typeof(this.scroller.contentNodes) != undefined) && (this.scroller.contentNodes != null)){
          this.scroller.contentNodes[0].parentNode.style.overflowX="hidden";
        }
      }
    
      // if the autoHeight is set to true, then grow as big as it needs to be
      if( (typeof this.autoHeight != "number") && this.autoHeight && this.model.count > 0){
        this.autoHeight = this.model.count;
        consoleDebugAgentDojoFixes("Virtual Grid: resize() | calculated autoHeight :" + this.autoHeight);
      }  
    }
    
    if( (this.autoHeight == 'true' || this.autoHeight == true )&&  this.domNode.parentNode.style.height != ''){
      this.autoHeight = false;
      this._autoHeight = false;
    }
  
    if(this.autoHeight == 'true' || this.autoHeight == true){
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoHeight set to true");
      console.info("Virtual Grid: resize() | autoHeight set to true, testing for height");
      if (this.domNode.clientHeight <= padBorder.h) {
        console.info("Virtual Grid: resize() | switching autoHeight from true to false");
        // If the height of the container is smaller than the calculated height,
        // set the height based on the container
        t = 0;
        this.scroller.calculateAverageRowHeight();
        this.domNode.style.height = 'auto';
        this.viewsNode.style.height = '';
      
        t += (this.scroller.averageRowHeight * this.autoHeight); 
        // add a padding buffer for scrollbars
        t += this._scrollPaddingAdjustment;
        this.domNode.style.height = t + "px";
      }
      else {
        // grid height mode selection 
        console.info("Virtual Grid: resize() | keeping autoHeight set to true");
        this.domNode.style.height = 'auto';
        this.viewsNode.style.height = '';
      }
    }
    else if(typeof this.autoHeight == "number" ){ 
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | autoHeight set to a number");
       t =  this.views.measureHeader();
      // add the padding amount adjustment
      t += this._heightPaddingAdjustment;
      this.scroller.calculateAverageRowHeight();
      this.domNode.style.height = 'auto';
      this.viewsNode.style.height = '';
      // Check to see if there are enough rows to render the full requested height      
      if ((this.model) && (this.autoHeight > this.model.count)){
        t += ((this.scroller.averageRowHeight + this._perRowPaddingAdjustment) * this.model.count);          
      }
      else{
        t += ((this.scroller.averageRowHeight + this._perRowPaddingAdjustment) * this.autoHeight);   
      }
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | final height :" + t + "px");
      // set the final height
      this.domNode.style.height = t + "px";
    }
    else if(this.flex > 0){
    }
    else if(this.domNode.clientHeight <= padBorder.h){
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | this.domNode.clientHeight less then padBorder.h");
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | padBorder.h = " + padBorder.h);
      if(this.domNode.parentNode == document.body){
        this.domNode.style.height = this.defaultHeight;
      }else{
        this.fitTo = "parent";
      }
    }
    else {
      consoleDebugAgentDojoFixes("Virtual Grid: autoHeight is not a number and not set to true");
      t = 0;
      this.scroller.calculateAverageRowHeight();
      this.domNode.style.height = 'auto';
      this.viewsNode.style.height = '';
    
      t += (this.scroller.averageRowHeight * this.autoHeight); 
      // add a padding buffer for scrollbars
      t += this._scrollPaddingAdjustment;
       this.domNode.style.height = t + "px";
    }
    
    if(this.fitTo == "parent"){
      var h = dojo._getContentBox(this.domNode.parentNode).h;
      consoleDebugAgentDojoFixes("Virtual Grid: resize() | fitTo = parent, height = " + h);
      dojo.marginBox(this.domNode, { h: Math.max(0, h) });
    }
    // header height
    var t = this.views.measureHeader();
    consoleDebugAgentDojoFixes("Virtual Grid: resize() | header height = " + t);
    this.headerNode.style.height = t + 'px';
    // content extent
    var l = 1;
    h = (this._autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
    if(this.autoWidth){
      // grid width set to total width
      this.domNode.style.width = this.views.arrange(l, 0, 0, h) + 'px';
    }else{
      // views fit to our clientWidth
      var w = this.domNode.clientWidth || (this.domNode.offsetWidth - padBorder.w);
      this.views.arrange(l, 0, w, h);
    }

    // virtual scroller height
    this.scroller.windowHeight = h; 
    this.scroller.defaultRowHeight = this.rows.getDefaultHeightPx() + 1;
    this.postresize();
  },

	_setAutoHeightAttr: function(ah, skipRender){
	  consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr()");
	  consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | type of autoHeight is " + typeof ah);

		if(typeof ah == "string"){
			if(!ah || ah == "false"){
				ah = false;
			}
			else if (ah == "true" || ah == true){
				ah = true;
			}
			else{
				ah = window.parseInt(ah, 10);
				if(isNaN(ah)){
					ah = false;
				}
				// Autoheight must be at least 1, if it's a number.  If it's
				// less than 0, we'll take that to mean "all" rows (same as 
				// autoHeight=true - if it is equal to zero, we'll take that
				// to mean autoHeight=false
				if(ah < 0){
					ah = true;
				}
				else if (ah === 0){
					ah = false;
				}
			}
		}

		consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | autoHeight = " + this.autoHeight);
		this.autoHeight = ah;
		if(typeof ah == "boolean"){
			this._autoHeight = ah;
		}
		else if(typeof ah == "number"){			
			if((ah >= this.rowCount)){
				this.rowCount = ah;
				this._autoHeight = false;				
			}
			else{
				this._autoHeight = false;
			}
			
			if (this.model && this.model.data) {
  			consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | model.data = " + this.model.data);
  			consoleDebugAgentDojoFixes("Virtual Grid: _setAutoHeightAttr() | model.data.length = " + this.model.data.length);
  			if(this.rowCount > this.model.data.length){
  				this.rowCount = this.model.data.length;
  				this._autoHeight = true;
  			}
  		}
			
			//use the autoHeight number as the max
			this.render();
		}
		else{
			this._autoHeight = false;
		}
		if(this._started && !skipRender){
			this.resize();
		}

	},

	resizeHeight: function(){
	  consoleDebugAgentDojoFixes("Virtual Grid: resizeHeight()");
		var t = this.views.measureHeader();
		this.headerNode.style.height = t + 'px';
		consoleDebugAgentDojoFixes("Virtual Grid: resizeHeight() | headerNode.style.height = " + this.headerNode.style.height);
		// content extent
		var h = (this.autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
		consoleDebugAgentDojoFixes("Virtual Grid: h = " + h);
		//this.views.arrange(0, 0, 0, h);
		this.views.onEach('setSize', [0, h]);
		this.views.onEach('resizeHeight');
		this.scroller.windowHeight = h; 
	},
  postrender: function(){
    consoleDebugAgentDojoFixes("Virtual Grid: postrender()");
		this.postresize();
		this.focus.initFocusView();
		
		/*Fix for CSC-168369, that corrects for an IE7 redraw issue with the grids*/
    if(dojo.isIE && dojo.isIE < 8){
      // Position the content column off viewport
      dojo.byId("contentColumn").style.position = "absolute";
      dojo.byId("contentColumn").style.left = "0px";
      // set the scrollbar to the bottom
      // this line is the actual fix for the IE rendering bug
      dojo.byId("contentColumn").scrollTop = dojo.byId("contentColumn").scrollHeight;
    }
		
	},

	postMixInProperties: function(){
	  consoleDebugAgentDojoFixes("Virtual Grid: postMixInProperties()");
		// Call this to update our autoheight to start out
		this._setAutoHeightAttr(this.autoHeight, true);
  },
	
  destroy: function(){
    // verify that DOM node exists before attempting to access
    if (this.domNode) {
      this.domNode.onReveal = null;
      this.domNode.onSizeChange = null;
    }
    if (this.edit) this.edit.destroy();
    if (this.views) this.views.destroyViews();
    if (this.inherited && dojo.isFunction(this.inherited)) this.inherited("destroy", arguments);
  },

  loadContent: function() {
    // set in JSP to refresh grid content
  },
  postCreate: function(){
		this.inherited("postCreate", arguments);
		if (this.loadContent) this.loadContent();
  }
});

/* 
--------------------------------------------------------------------
End Grid autoheight overrides
--------------------------------------------------------------------
*/


/* 
--------------------------------------------------------------------
Menu update to support icon URLs
-------------------------------------------------------------------- 
*/

dojo.require("dijit.Menu");
dojo.extend(dijit.MenuItem, {
	// Make 3 columns 
	// icon, label, and expand arrow (BiDi-dependent) indicating sub-menu
	templateString:
		 '<tr class="dijitReset dijitMenuItem"'
		+'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
		+'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass}" dojoAttachPoint="iconNode"></div></td>'
		+'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem"></td>'
		+'<td class="dijitReset" dojoAttachPoint="arrowCell">'
			+'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
			+'<span class="dijitInline dijitArrowNode dijitMenuExpandInner">+</span>'
			+'</div>'
		+'</td>'
		+'</tr>',

	// label: String
	//	menu text
	label: '',

	// iconClass: String
	//	class to apply to div in button to make it display an icon
	iconClass: "",

	// iconURL: String
	//	URL to path and file of icon to be displayed in the case when CSS is not feasible
	iconURL: "",
	
	// iconHover: String
	//	Hover text to be used as an ALT tag over the icon
	iconHover: "",

	// disabled: Boolean
	//  if true, the menu item is disabled
	//  if false, the menu item is enabled
	disabled: false,

	postCreate: function(){
		if (this.iconURL) {
		  this.iconNode.innerHTML='<img src="'+this.iconURL+'" border="0" title="'+this.iconHover+'">';
		}
		
		dojo.setSelectable(this.domNode, false);
		this.setDisabled(this.disabled);
		if(this.label){
			this.containerNode.innerHTML=this.label;
		}
	},

	_onHover: function(){
		// summary: callback when mouse is moved onto menu item
		this.getParent().onItemHover(this);
	},

	_onUnhover: function(){
		// summary: callback when mouse is moved off of menu item
		// if we are unhovering the currently selected item
		// then unselect it
		this.getParent().onItemUnhover(this);
	},

	_onClick: function(evt){
		this.getParent().onItemClick(this);
		dojo.stopEvent(evt);
	},

	onClick: function() {
		// summary
		//	User defined function to handle clicks
	},

	focus: function(){
		dojo.addClass(this.domNode, 'dijitMenuItemHover');
		try{
			dijit.focus(this.containerNode);
		}catch(e){
			// this throws on IE (at least) in some scenarios
		}
	},

	_blur: function(){
		dojo.removeClass(this.domNode, 'dijitMenuItemHover');
	},

	setDisabled: function(/*Boolean*/ value){
		// summary: enable or disable this menu item
		this.disabled = value;
		dojo[value ? "addClass" : "removeClass"](this.domNode, 'dijitMenuItemDisabled');
		dijit.setWaiState(this.containerNode, 'disabled', value ? 'true' : 'false');
	}
});

/* 
--------------------------------------------------------------------
End of Menu update to support icon URLs
-------------------------------------------------------------------- 
*/


/* 
--------------------------------------------------------------------
Toaster update to suppress the functionality that hides the Toaster
when clicked directly
-------------------------------------------------------------------- 
*/
dojo.require("dojox.widget.Toaster");
dojo.extend(dojox.widget.Toaster, {
  
  // We are overriding the templateString to omit the onSelect event, thereby preventing the toaster from being closed manually
  templateString: '<div dojoAttachPoint="clipNode"><div dojoAttachPoint="containerNode"><div dojoAttachPoint="contentNode"></div></div></div>',
  
  setContent: function(/*String*/message, /*String*/messageType, /*int?*/duration){
			// summary
			//		sets and displays the given message and show duration
			// message:
			//		the message
			// messageType:
			//		type of message; possible values in messageTypes enumeration ("message", "warning", "error", "fatal")
			// duration:
			//		duration in milliseconds to display message before removing it. Widget has default value.
			duration = duration||this.duration;
			// sync animations so there are no ghosted fades and such
			if(this.slideAnim){
				if(this.slideAnim.status() != "playing"){
					this.slideAnim.stop();
				}
				if(this.slideAnim.status() == "playing" || (this.fadeAnim && this.fadeAnim.status() == "playing")){
					setTimeout(dojo.hitch(this, function(){
						this.setContent(message, messageType);
					}), 50);
					return;
				}
			}

			var capitalize = function(word){
				return word.substring(0,1).toUpperCase() + word.substring(1);
			};

			// determine type of content and apply appropriately
			for(var type in this.messageTypes){
				dojo.removeClass(this.containerNode, "dijitToaster" + capitalize(this.messageTypes[type]));
			}

			dojo.style(this.containerNode, "opacity", 1);

			if(message && this.isVisible){
				//We want to deliberately override the default Dojo behavior and only support a single instance of the widget.
				//Instead of appending the additional content to the widget, we will simply overwrite the old message bar
				//with the new one.
				
				//message = this.contentNode.innerHTML + this.separator + message;
			}
			this.contentNode.innerHTML = message;

			dojo.addClass(this.containerNode, "dijitToaster" + capitalize(messageType || this.defaultType));

			// now do funky animation of widget appearing from
			// bottom right of page and up
			this.show();
			var nodeSize = dojo.marginBox(this.containerNode);
			
			if(this.isVisible){
				this._placeClip();
			}else{
				var style = this.containerNode.style;
				var pd = this.positionDirection;
				// sets up initial position of container node and slide-out direction
				if(pd.indexOf("-up") >= 0){
					style.left=0+"px";
					style.top=nodeSize.h + 10 + "px";
				}else if(pd.indexOf("-left") >= 0){
					style.left=nodeSize.w + 10 +"px";
					style.top=0+"px";
				}else if(pd.indexOf("-right") >= 0){
					style.left = 0 - nodeSize.w - 10 + "px";
					style.top = 0+"px";
				}else if(pd.indexOf("-down") >= 0){
					style.left = 0+"px";
					style.top = 0 - nodeSize.h - 10 + "px";
				}else{
					throw new Error(this.id + ".positionDirection is invalid: " + pd);
				}

				this.slideAnim = dojo.fx.slideTo({
					node: this.containerNode,
					top: 0, left: 0,
					duration: 450});
				dojo.connect(this.slideAnim, "onEnd", this, function(nodes, anim){
						//we build the fadeAnim here so we dont have to duplicate it later
						// can't do a fadeHide because we're fading the
						// inner node rather than the clipping node
						this.fadeAnim = dojo.fadeOut({
							node: this.containerNode,
							duration: 1000});
						dojo.connect(this.fadeAnim, "onEnd", this, function(evt){
							this.isVisible = false;
							this.hide();
						});
						//if duration == 0 we keep the message displayed until clicked
						//TODO: fix so that if a duration > 0 is displayed when a duration==0 is appended to it, the fadeOut is canceled
						if(duration>0){
							setTimeout(dojo.hitch(this, function(evt){
								// we must hide the iframe in order to fade
								// TODO: figure out how to fade with a BackgroundIframe
								if(this.bgIframe && this.bgIframe.iframe){
									this.bgIframe.iframe.style.display="none";
								}
								this.fadeAnim.play();
							}), duration);
						}else{
							dojo.connect(this, 'onSelect', this, function(evt){
								this.fadeAnim.play();
							});
						}
						this.isVisible = true;
					});
				this.slideAnim.play();
			}
		}

});


/* 
--------------------------------------------------------------------
Split Container - Added Typeof test instead of just () since 0 returned erroneous result, bug in Dojo codeline
--------------------------------------------------------------------
*/
dojo.extend(dijit.layout.SplitContainer, {

	beginSizing: function(e, i){
		var children = this.getChildren();
		this.paneBefore = children[i];
		this.paneAfter = children[i+1];

		this.isSizing = true;
		this.sizingSplitter = this.sizers[i];

		if(!this.cover){
			this.cover = dojo.doc.createElement('div');
			this.domNode.appendChild(this.cover);
			var s = this.cover.style;
			s.position = 'absolute';
			s.zIndex = 1;
			s.top = 0;
			s.left = 0;
			s.width = "100%";
			s.height = "100%";
		}else{
			this.cover.style.zIndex = 1;
		}
		this.sizingSplitter.style.zIndex = 2000;

		// TODO: REVISIT - we want MARGIN_BOX and core hasn't exposed that yet (but can't we use it anyway if we pay attention? we do elsewhere.)
		this.originPos = dojo.coords(children[0].domNode, true);
		if(this.isHorizontal){
			var client = (typeof e.layerX === 'undefined') ?  e.offsetX : e.layerX;
			var screen = e.pageX;
			this.originPos = this.originPos.x;
		}else{
			var client = (typeof e.layerY === 'undefined') ? e.offsetY : e.layerY;
			var screen = e.pageY;
			this.originPos = this.originPos.y;
		}
		this.startPoint = this.lastPoint = screen;
		this.screenToClientOffset = screen - client;
		
	
		this.dragOffset = this.lastPoint - this.paneBefore.sizeActual - this.originPos - this.paneBefore.position;

		if(!this.activeSizing){
			this._showSizingLine();
		}

		//					
		// attach mouse events
		//
		this._connects = [];
		this._connects.push(dojo.connect(document.documentElement, "onmousemove", this, "changeSizing"));
		this._connects.push(dojo.connect(document.documentElement, "onmouseup", this, "endSizing"));

		dojo.stopEvent(e);
	}
	
});

/* 
--------------------------------------------------------------------
Tool-tip fix for weird flixering when the tooltip contents is rolledover in IE7+
--------------------------------------------------------------------
*/
dojo.extend(dijit._MasterTooltip, {

_onShow: function(){
	if(dojo.isIE){
		// the arrow won't show up on a node w/an opacity filter
		// this.domNode.style.filter="";
	}
}

});


/* 
--------------------------------------------------------------------
Menu button update to for active parent menu buttons when the menu is opened
-------------------------------------------------------------------- 
*/

dojo.require("dijit.form._FormWidget");
dojo.require("dijit._Container");

dojo.extend(dijit.form.DropDownButton, {

	_openDropDown: function(){
		var dropDown = this.dropDown;
		var oldWidth=dropDown.domNode.style.width;
		var self = this;

		dijit.popup.open({
			parent: this,
			popup: dropDown,
			around: this.domNode,
			orient: this.isLeftToRight() ? {'BL':'TL', 'BR':'TR', 'TL':'BL', 'TR':'BR'}
				: {'BR':'TR', 'BL':'TL', 'TR':'BR', 'TL':'BL'},
			onExecute: function(){
				self._closeDropDown(true);
			},
			onCancel: function(){
				self._closeDropDown(true);
			},
			onClose: function(){
				dropDown.domNode.style.width = oldWidth;
				self.popupStateNode.removeAttribute("popupActive");
				dojo.removeClass(self.titleNode.parentNode, "popupActive");
				this._opened = false;
			}
		});
		if(this.domNode.offsetWidth > dropDown.domNode.offsetWidth){
			var adjustNode = null;
			if(!this.isLeftToRight()){
				adjustNode = dropDown.domNode.parentNode;
				var oldRight = adjustNode.offsetLeft + adjustNode.offsetWidth;
			}
			// make menu at least as wide as the button
			dojo.marginBox(dropDown.domNode, {w: this.domNode.offsetWidth});
			if(adjustNode){
				adjustNode.style.left = oldRight - this.domNode.offsetWidth + "px";
			}
		}
		this.popupStateNode.setAttribute("popupActive", "true");
		dojo.addClass(this.titleNode.parentNode, "popupActive");
		
		this._opened=true;
		if(dropDown.focus){
			dropDown.focus();
		}
		// TODO: set this.checked and call setStateClass(), to affect button look while drop down is shown
	}  

});


/* 
--------------------------------------------------------------------
Re-add 'name' attribute to text field in a FilteringSelect (CSC-159770)
-------------------------------------------------------------------- 
*/

dojo.registerModulePath("atg.widget", "/WebUI/dijit");
dojo.require('atg.widget.form.FilteringSelect');

dojo.extend(atg.widget.form.FilteringSelect, {
    
    postCreate: function(){
        this.inherited('postCreate', arguments);
        this.textbox.name = this.textbox.id;
        this.valueNode.id = this.valueNode.name;
    }
    
});

/*
--------------------------------------------------------------------
When DateTextBox text input is empty and not focused, than it displays the correct date format in gray. 
-------------------------------------------------------------------- 
*/

dojo.require("dijit.form.DateTextBox");
dojo.extend(dijit.form.DateTextBox, {
		
		_setInputValue: function(/* String */ value) {
			dojo.byId(this.id).value = value
		},
		
		_isDatePatternInInput: function() {
			return (dojo.byId(this.id).value == this.constraints.datePattern);
		},
		
		_setupPlaceholderIfNeeded: function(/*Boolean*/ isInitialization) {
			if(this._isConstrainedByDatePattern) {
				var isEmptyInput = !dojo.byId(this.id).value;
				var isInputDisabledOrItIsInitialization =(!this.disabled || isInitialization );
				if(isEmptyInput && isInputDisabledOrItIsInitialization) {
					dojo.style(this.id,"color", "gray");
					this._setInputValue(this.constraints.datePattern);
					
					// TODO: When dojo version > 1.0, check if it is possible to remove this
					if(this.disabled){ // Looks weird, but otherwise disabled input will look like enabled input
						this.setDisabled(true);
					}
				}
			}
		},
		
		_removePlaceholderIfNeeded: function() {
			if(this._isConstrainedByDatePattern) {
				if(this._isDatePatternInInput() && !this.disabled) {
					dojo.style(this.id,"color", "black");
					this._setInputValue('');
				}
			}
		},
		
		// Had to override _onBlur and _onFocus instead of connection by dojo.connect(Overriding gives more controll)
		_onBlur: function() {
			this.inherited('_onBlur', arguments);
			this._setupPlaceholderIfNeeded();
		},
		
		_onFocus: function() {
			this._removePlaceholderIfNeeded();
			this.inherited('_onFocus', arguments);
		},
		
		isValid: function() {
			if (this._isDatePatternInInput()) {
				return true;
			} else {
				return this.inherited('isValid', arguments);
			}
		},
		
		// Use this method to get value. Do not call dojo.byId($id).value directly
		getValue: function() {
			if (this._isDatePatternInInput()) {
				return '';
			} else {
				return this.inherited('getValue', arguments);
			}
		},
		
		// TODO: Uncomment when used dojo version will be >= 1.2
		/*
		_valueChanged: function() {
			if(this._isDatePatternInInput) {
				dojo.style(this.id,"color", "gray");
			} else {
				dojo.style(this.id,"color", "black");
			}
		},
		*/
		
		postCreate: function(){
			this.inherited('postCreate', arguments);
			this._isConstrainedByDatePattern = false;
			if(this.constraints.datePattern) {
				this._isConstrainedByDatePattern = true;
				var context = dijit.byId(this.id);
				// TODO: Replace with commented lines below, when used dojo version will be >= 1.2
				setTimeout(dojo.hitch(context, "_setupPlaceholderIfNeeded", "true"), 100);
				/* context.watch("value", _valueChanged);
				this._setupPlaceholderIfNeeded(); */
			}
		}
});


/* 
--------------------------------------------------------------------
IE8 compatibility fix
-------------------------------------------------------------------- 
*/

dojo.require("dijit.layout.ContentPane");
dojo.extend(dijit.layout.ContentPane, {

  excludeFromTabs: false,
  _setContent: function(cont){
    this.destroyDescendants();

    // FORM Tag switcherooo
    if(dojo.isIE){
      var replacePattern = new RegExp("(<form([^>])*>)", "gmi");
      var matchPattern = new RegExp("(<form)([^>])*action=([^>])*>", "gmi");
      cont = cont.replace(replacePattern, function($0)
      {
        if ($0 != "")
          if ($0.match(matchPattern))
            return $0.replace(/<FORM/i, "<DIV")
          else
            return $0.replace(/<FORM/i, "<DIV action=\"#\"")
       }).replace(/<\/FORM>/gi, "</DIV>");
    }

    try{
      var node = this.containerNode || this.domNode;
      while(node.firstChild){
        dojo._destroyElement(node.firstChild);
      }
      if(typeof cont == "string"){
        // dijit.ContentPane does only minimal fixes,
        // No pathAdjustments, script retrieval, style clean etc
        // some of these should be available in the dojox.layout.ContentPane
        if(this.extractContent){
          match = cont.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
          if(match){ cont = match[1]; }
        }
        node.innerHTML = cont;
      }else{
        // domNode or NodeList
        if(cont.nodeType){ // domNode (htmlNode 1 or textNode 3)
          node.appendChild(cont);
        }else{// nodelist or array such as dojo.Nodelist
          dojo.forEach(cont, function(n){
            node.appendChild(n.cloneNode(true));
          });
        }
      }

      if(dojo.isIE){
        var divTags = node.getElementsByTagName('DIV');
        for(var k = 0; k<divTags.length; k++){
          var thisDiv = divTags[k];
          if(thisDiv.getAttribute('action')){
            newFormNode = atg.formManager.createForm();
            newFormNode.innerHTML = thisDiv.innerHTML;

            // copy over all attributes and styles from the oDIV to the recycled form node
           for (var attrName in  thisDiv.attributes)
              if (thisDiv.attributes[attrName] &&
                  thisDiv.attributes[attrName].nodeValue)
                newFormNode.setAttribute(thisDiv.attributes[attrName].nodeName, thisDiv.attributes[attrName].nodeValue);
            for (var currentStyle in thisDiv.style){
              if (currentStyle != "font")
                  newFormNode.style[currentStyle] = thisDiv.style[currentStyle];
            }
            newFormNode.style.cssText = thisDiv.style.cssText;
            thisDiv.replaceNode(newFormNode);
            thisDiv.outerHTML = "";
            thisDiv.removeNode();
            k--;
          }
        }
      }

    }catch(e){
      // check if a domfault occurs when we are appending this.errorMessage
      // like for instance if domNode is a UL and we try append a DIV
      var errMess = this.onContentError(e);
      try{
        node.innerHTML = errMess;
      }catch(e){
        console.error('Fatal '+this.id+' could not change content due to '+e.message, e);
      }
    }
  }

});

/* 
--------------------------------------------------------------------
Bug fix 16023367
-------------------------------------------------------------------- 
*/

if(dojo.isIE || dojo.isOpera){
  dojo.byId = function(id, doc){
    if(dojo.isString(id)){
      var _d = doc || dojo.doc;
      var te = _d.getElementById(id);
      // attributes.id.value is better than just id in case the
      // user has a name=id inside a form
      if(te && te.attributes.id.value == id){
        return te;
      }else{
        var eles = _d.all[id];
        if(!eles){ return; }
        if(!eles.length){ return eles; }
        // if more than 1, choose first with the correct id
        var i=0;
        while((te=eles[i++])){
          //Bug fix 16023367, null value check for te.attributes.id has been added
          if(te.attributes.id && te.attributes.id.value == id){ return te; }
        }
      }
    }else{
      return id; // DomNode
    }
  }
}

/**
 * Override dojox email validation as the dojo implementation is too retrictive.
 * The purpose of this is to be make the validation simple and minimally 
 * restrictive. Please resist the temptation to "improve" this regular 
 * expression, as it is considered "good enough".
 */
dojo.require("dojox.validate.web");
dojox.validate.isEmailAddress = function (value) {
  return /.+@.+/.test(value);
}

/*
  The default behaiour listens to keypress event, which is a non standard event.
  Keys such ask Alt, Ctrl, ESC don't trigger a keypress event in some browser.
  Must listen on keyup.
*/
dojo.require("dojox.Dialog");
dojo.extend(dojox.Dialog, {
  _onKey: function(/*Event*/ evt){
    // summary: handles the keyboard events for accessibility reasons
    if(evt.keyCode){
      var node = evt.target;
      // see if we are shift-tabbing from titleBar
      if(node == this.titleBar && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
        if(this._lastFocusItem){
          this._lastFocusItem.focus(); // send focus to last item in dialog if known
        }
        dojo.stopEvent(evt);
      }else{

        // see if the key is for the dialog
        while (node) {
          if (node == this.domNode || dojo.hasClass(node, "dijitDialog")) {
            if (evt.keyCode == dojo.keys.ESCAPE) {
              this.hide();
            } else {
              return; // just let it go
            }
          }
          node = node.parentNode;
        }

        // this key is for the disabled document window
        if(evt.keyCode != dojo.keys.TAB){ // allow tabbing into the dialog for a11y
          dojo.stopEvent(evt);
        // opera will not tab to a div
        }else if (!dojo.isOpera){
          try{
            this.titleBar.focus();
          }catch(e){/*squelch*/}
        }
      }
    }
  },

  show: function(){
    // summary: display the dialog

    // first time we show the dialog, there's some initialization stuff to do
    if(!this._alreadyInitialized){
      this._setup();
      this._alreadyInitialized=true;
    }

    if(this._fadeOut.status() == "playing"){
      this._fadeOut.stop();
    }

    this._modalconnects.push(dojo.connect(window, "onscroll", this, "layout"));
    // Listen on keyup instead of keypress, as keypress os not as widely supported.
    this._modalconnects.push(dojo.connect(document.documentElement, "onkeyup", this, "_onKey"));

    // IE doesn't bubble onblur events - use ondeactivate instead
    var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
    this._modalconnects.push(dojo.connect(this.containerNode, ev, this, "_findLastFocus"));

    dojo.style(this.domNode, "opacity", 0);
    this.domNode.style.display="block";
    this.open = true;
    this._loadCheck(); // lazy load trigger

    this._position();

    if (djConfig.usesApplets) {
      // add class to body to hide the applet
      dojo.addClass(document.body, "appletKiller");
      dojo.query("iframe").forEach(

        function(eachIframe) {
          if(eachIframe.contentDocument){
            // Firefox, Opera
            doc = eachIframe.contentDocument;
          }else if(eachIframe.contentWindow){
            // Internet Explorer
            doc = eachIframe.contentWindow.document;
          }else if(eachIframe.document){
            // Others?
            doc = eachIframe.document;
          }
          if (doc.body) {
            dojo.addClass(doc.body, "appletKiller");
          }
        }
      );
    }

    this._fadeIn.play();

    try {
      this._savedFocus = dijit.getFocus(this);
    }
    catch (e) {
      // On IE7 this is a bogus error caused by creating ranges
      // on text in a DIV that is display:none - apparently getFocus does
      // somewhere internally - when this happens just reset the property
      // gracefully without a major page blow-up - the dialog will just
      // behave as though there was no prior focus - we can live with this
      this._savedFocus = null;
      delete this._savedFocus;
    }

    // set timeout to allow the browser to render dialog
    setTimeout(dojo.hitch(this, function(){
      dijit.focus(this.titleBar);
    }), 50);
  }
});
