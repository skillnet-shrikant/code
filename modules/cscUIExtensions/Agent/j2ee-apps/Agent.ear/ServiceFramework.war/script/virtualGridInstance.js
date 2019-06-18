dojo.provide("atg.data.grid");

/*
 * Manages a virtual scrolling grid instance
 */
dojo.declare("atg.data.grid.VirtualGridInstance", null, {
  /*
   * Constructs a data model for the grid instance
   *
   * inFormId - the ID of the search form for the grid results
   * inUrl - the URL to get the JSON data
   * inRowsPerPage - number of rows to fetch per server request
   */
  constructor: function(params) {
    this.formId = params.formId;
    this.gridWidgetId = params.gridWidgetId;
    this.progressNodeId = params.progressNodeId;
    this.url = params.url;
    this.rowsPerPage = params.rowsPerPage;
    this.dataModel = params.dataModel;

    // Create data model and attach progress notification handlers
    if (this.dataModel) {
      this.dataModel.endProgress = dojo.hitch(this, "endProgressMessage");
      this.dataModel.startProgress = dojo.hitch(this, "startProgressMessage");
      if (params.pageBaseOffset) this.dataModel.pageBaseOffset = params.pageBaseOffset; // defaults to 0 if not set
      if (params.currentPageElementName) { this.dataModel.currentPageElementName = params.currentPageElementName;}
    }
    this.messages = params.messages;
    this.structure = params.structure;
    
    this.hitchGridMethods();
  },
  /*
   * Re-attaches methods in the grid model instance (which survives page changes) to the widget
   * (which is destroyed during page changes).
   */
  hitchGridInstanceToWidget: function() {
    var grid = this.getGridWidget();
    if (!grid) return;
    grid.setModel(this.dataModel);
    this.hitchGridMethods();
  },
  hitchGridMethods: function() {
    var grid = this.getGridWidget();
    if (!grid) return;
    grid.canSort = dojo.hitch(grid, this.canSort);
    grid.setSortIndex = dojo.hitch(grid, this.setSortIndex, this);
  },
  // sorting needs to be on the grid so we can look at the cell info
  canSort: function(inSortInfo){
    var c = this.getCell(this.getSortIndex(inSortInfo));
    return (c && c.defaultSort && 
       (c.defaultSort.indexOf("asc") == 0 || c.defaultSort.indexOf("desc") == 0)) ? true : false;
  },
  getSortIndex: function(){
    
  },
	setSortIndex: function(inGridInstance, inIndex, inAsc){
		// override base method to read from configuration - 
		// since not all the fields that we could sort on are visible, we
		// have to map the column header that was clicked on to a field in the data model.
		var si = inIndex + 1;
    var c = this.getCell(this.getSortIndex(si));
    if (c.originalFieldIndex === undefined) { // reset only first time
      c.originalFieldIndex = c.fieldIndex;
    }
    var sortField = "";
    if (c && c.defaultSort && c.sortField) { // maybe override the sort field
      sortField = c.sortField;
    }
    else if (c && c.defaultSort && c.field) {
      sortField = c.field;
    }
    if (sortField) {
      for (var i = 0 ; i < inGridInstance.dataModel.fields.values.length; i++) {
        if (inGridInstance.dataModel.fields.values[i] && inGridInstance.dataModel.fields.values[i].key && 
            inGridInstance.dataModel.fields.values[i].key == sortField) {
          console.debug("VIRTUAL GRID INSTANCE - resetting sortIndex from: ", inIndex, ", to: ", i);
          c.fieldIndex = i;
          break;
        }        
      }
    }
		if(inAsc != undefined){
			si *= (inAsc ? 1 : -1);
		} 
		else if (this.getSortIndex() == inIndex){
			si = -this.sortInfo;
		}
		else { // read configuration by default
      if (c && c.defaultSort && c.defaultSort.indexOf("desc") == 0) {
        si = si * -1;
      }
		}
		this.setSortInfo(si);
	},
  /*
   * Get reference to the data model
   */
  getDataModel: function() {
    return this.dataModel;
  },
  /*
   * Set the grid structure
   */
  setStructure: function(inStructure) {
    this.structure = inStructure;
  },
  setFields: function(inFields) {
    this.dataModel.fields.set(inFields);
  },
  /*
   * Set the current page index form input name
   */
  setCurrentPageElementName: function(inCurrentPageElementName) {
    if (this.dataModel) {this.dataModel.currentPageElementName = inCurrentPageElementName;}
  },
  /*
   * Set the page-base offset for the grid model:
   * 0 for zero-based paging, 1 for one-based paging, etc.
   */
  setPageBaseOffset: function(inPageBaseOffset) {
    if (this.dataModel) this.dataModel.pageBaseOffset = inPageBaseOffset;
  },
  /*
   * Set the sort direction form input name
   */
  setSortDirectionElementName: function(inSortDirectionElementName) {
    this.sortDirectionElementName = inSortDirectionElementName;
  },
  /*
   * Set the sort field form input name
   */
  setSortFieldElementName: function(inSortFieldElementName) {
    this.sortFieldElementName = inSortFieldElementName;
  },
  /*
   * Set the grid widget ID and set up sorting for the instance
   */
  setGridWidgetId: function(inGridWidgetId) {
    this.gridWidgetId = inGridWidgetId;
    this.hitchGridInstanceToWidget();
  },
  /*
   * Sets the array of sortable columns
   */
  setSortableColumns: function(inSortableColumns) {
    this.sortableColumns = inSortableColumns;
  },
  /*
   * Get reference to the grid widget
   */
  getGridWidget: function() {
    if (!this.gridWidgetId) return null;
    return dijit.byId(this.gridWidgetId);
  },
  /*
   * Set the progress node DOM ID
   */
  setProgressNodeId: function(inProgessNodeId) {
    this.progressNodeId = inProgessNodeId;
  },
  /*
   * Get reference to the progress node
   */
  getProgressNode: function() {
    if (!this.progressNodeId) return null;
    return dojo.byId(this.progressNodeId);
  },
  /*
   * Return the data for a cell from the model
   *
   * inProperty - name of the cell in the returned JSON object for the row
   * inRowIndex - index of the row
   */
  getCellData: function(inProperty, inRowIndex) {
    var row;
    var data = "";
    if (this.dataModel) {
      row = this.dataModel.getRow(inRowIndex);
      if (row) {
        data = row[inProperty];
      } 
    }
    return data;
  },
  /*
   * Return the data for a cell by URL
   *
   * inProperty - name of the cell in the grid structure for which to return data
   * inUrl - URL to request
   * inQueryParams - query parameters to add to the URL
   * inRowIndex - index of the row
   */
  getCellDataByGet: function(inProperty, inUrl, inQueryParams, inRowIndex) {
    var data = "";
    if (this.dataModel) {
      data = this.dataModel.getCellByGet(inProperty, inUrl, inQueryParams, inRowIndex);
    }
    this.executeScripts(data, inUrl, true); // eval script blocks in returned cell data
    return data;
  },
  /*
   * Return the data for a cell by form submission
   * Create the row detail by posting a form to get the details
   *
   * inProperty - name of the cell in the grid structure for which to return data
   * inFormId - identifier to look up DOM node of the form to submit
   * inFormData - map of functions or values to submit with form keyed by input,
   *   functions are invoked to obtain the value to submit
   * inSuccessUrl - success URL to render the form result
   * inRowIndex - index of the row
   */
  getCellDataByPost: function(inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex) {
    var data = "...";
    if (!this.dataModel) return data;
    var formNode = dojo.byId(inFormId);
    for (var element in inFormData) {
      var value = inFormData[element];
      if (dojo.isFunction(value)) {
        value = value(inRowIndex);
      }
      formNode[element].value = value;
    }
    data = this.dataModel.getCellByPost(inProperty, formNode, inSuccessUrl, inRowIndex)
    this.executeScripts(data, inSuccessUrl, true); // eval script blocks in returned cell data
    return data;
  },
  /*
   * Create a link for a row based on the passed in template and replacement parameters.
   *   For example, the view link and select link can both be created via this function.
   *   Multiple replacement parameters can be passed in the inReplacementParams argument.
   *   Replacement parameters are keyed by the pattern to replace in the template string.
   *   The template string is processed for each replacement parameter in sequence.
   *   The replacement parameter value can be a function or a value - if a function,
   *   the function will be invoked with the row index to obtain a value for replacement.
   *
   * inTemplate - a URL with patterns to replace that correspond to keys in the inReplacementParams
   * inReplacementParams - a map keyed to patterns in the inTemplate which are replaced by the corresponding values
   * inRowIndex - index of the row
   */
  createLink: function(inTemplate, inReplacementParams, inRowIndex) {
    var link = inTemplate;
    for (var pattern in inReplacementParams) {
      var value = inReplacementParams[pattern];
      if (dojo.isFunction(value)) {
        value = value(inRowIndex);
      }

      pattern = new RegExp(pattern,"gi"); // global replace
      link = link.replace(pattern, value);
    }
    return link;
  },
  /*
   * Creates html for the cell that toggles the details
   *
   * inImagePath - web path to the image directory
   * inImages - map containing image file names keyed to the following keys: "open" and "closed"
   * inGridInstance - global variable name for the grid instance (due to event handler)
   * inRowIndex - index of the row
   */
  createToggler: function(inImagePath, inImages, inGridInstance, inRowIndex) {
    var image = this.isShowRowDetails(inRowIndex) ? inImages["open"] : inImages["closed"];
    var show = this.isShowRowDetails(inRowIndex) ? 'false' : 'true';
    return '<img src="' + inImagePath + image + '" onclick="' + inGridInstance + '.toggleDetail(' + inRowIndex + ', ' + show + ')">';
  },
  /*
   * Creates html for the cell that toggles the details
   *
   * inImagePath - web path to the image directory
   * inImages - map containing image file names keyed to the following keys: "open" and "closed"
   * inGridInstance - global variable name for the grid instance (due to event handler)
   * inRowIndex - index of the row
   */
   createHoverToggler: function(inImagePath, inImages, inGridInstance, inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex){
     var image = this.isShowRowDetails(inRowIndex) ? inImages["open"] : inImages["closed"];
     var show = this.isShowRowDetails(inRowIndex) ? 'false' : 'true';
     var params = "";
     for (var element in inFormData) {
       var value = inFormData[element];
       if (dojo.isFunction(value)) {
         value = value(inRowIndex);
       }
       params += element + ":\'" + value + "\'";
     }
     return '<img width="16" height="14" id="gridCell' + inGridInstance + inRowIndex + '" src="' + inImagePath + image + '" onmouseover="' + inGridInstance + '.toggleHoverDetail(\'' + inGridInstance + inRowIndex  + '\', ' + inRowIndex + ', ' + show + ',\'' + inProperty + '\',\'' + inFormId + '\',{' + params + '} , \'' + inSuccessUrl + '\');" onMouseOut="' + inGridInstance + '.toggleHoverDetail(\'' + inGridInstance + inRowIndex  + '\');">';
   },

   /*
    * Loads the row details into a hovering content pane
    *
    * cellId - id of DOM node need to be hovered to show tooltip
    * inRowIndex - index of the row
    * inShow - flag indicating whether details should be shown or hidden
    * inProperty - name of the cell in the grid structure for which to return data
    * inFormId - identifier to look up DOM node of the form to submit
    * inFormData - map of functions or values to submit with form keyed by input,
    *   functions are invoked to obtain the value to submit
    * inSuccessUrl - success URL to render the form result
   */
   toggleHoverDetail: function(cellId, inRowIndex, inShow, inProperty, inFormId, inFormData, inSuccessUrl) {
     if(arguments.length > 1){
       console.debug("toggleHoverDetail:" + inFormData);
       var data = this.getCellDataByPost(inProperty, inFormId, inFormData, inSuccessUrl, inRowIndex);
       dijit.showTooltip(  data   , dojo.byId("gridCell" + cellId));
     }else{
       dijit.hideTooltip(dojo.byId("gridCell" + cellId));
     }
   },
  /*
   * Changes the toggle detail state
   *
   * inRowIndex - index of the row
   * inShow - flag indicating whether details should be shown or hidden
   */
  toggleDetail: function(inRowIndex, inShow) {
    var grid = this.getGridWidget();
    if (!grid) return;
    this.setShowRowDetails(inRowIndex, inShow);
    grid.updateRow(inRowIndex);
  },
  /*
   * Runs the script blocks in the passed in data when current thread finishes
   *
   * inData - html data that may have script blocks to evaluate
   * inUrl - URL from which the html data was downloaded (required for error reporting purposes only)
   * inWaitForCurrentScriptBlock - controls whether the script blocks are eval'd immediately
   *   or when the current block is finished. If the the scripts depend on HTML that is
   *   being rendered by the currently running script block, wait should be 'true', so
   *   that the required DOM is in existence for the script. If the currently running script
   *   block depends on script blocks in the HTML, wait should be 'false' (but doing this as
   *   a matter of design is not recommended).
   */
  executeScripts: function(inData, inUrl, inWaitForCurrentScriptBlock) {
    var element = document.createElement("div");
    element.innerHTML = inData;
    var scripts = element.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; i++) {
      try {
        inWaitForCurrentScriptBlock ? setTimeout(scripts[i].innerHTML, 1) : eval(scripts[i].innerHTML);
      }
      catch (e) {
        console.debug("Error ", e.message, " running eval() on script in ", inUrl, ": ", e);
      }
    }
  },
  /*
   * Returns the visibility of specified row detail
   *
   * inRowIndex - index of the row
   */
  isShowRowDetails: function(inRowIndex) {
    var isVisible = false;
    if (this.dataModel) {
      isVisible = this.dataModel.isShowDetails[inRowIndex];
    }
    return isVisible;
  },
  /*
   * Sets the visibility of the details for a row specified by index:
   *
   * inRowIndex - index of the row
   * inVisibility - sets the visibility of the row details, true for visible
   */
  setShowRowDetails: function(inRowIndex, inVisibility) {
    this.dataModel.isShowDetails[inRowIndex] = inVisibility;
  },
  /*
   * Callback for data model to update search progress
   *   when a search is starting
   */
  startProgressMessage: function() {
    var node = this.getProgressNode();
    if (!node) return;
    node.innerHTML = this.messages.inProgress;
  },
  /*
   * Set progress and status messages
   */
  setMessages: function(inMessages) {
    this.messages = inMessages;
  },
  /*
   * Callback for data model to update search progress
   *   when a search is ending
   */
  endProgressMessage: function() {
    var node = this.getProgressNode();
    if (!node) return; // progress node optional - not necessarily an error
    if (this.dataModel.lastServerError != null) {
      node.innerHTML = this.dataModel.lastServerError.message;
    }
    else if (this.dataModel.count === 0 || this.dataModel.count == -1) {
      node.innerHTML = this.messages.noResultsFound;
    }
    else {
      var matchingStr = this.messages.resultsFound;
      node.innerHTML = matchingStr.replace(/\{0\}/g, this.dataModel.count);
    }
  },
  /*
   * Sets the details in a row to visible or hidden by setting a visibility flag
   *   on the detail sub row (e.g. the sub row with index=1)
   *
   * inRowIndex - index of the row
   * inSubRows - array of the rows in the grid structure, a grid with details will usually
   *   have 2 sub rows: one for the main row and another below it for the details
   */
  handleDetailVisibility: function(inRowIndex, inSubRows) {
    if (inSubRows && inSubRows[1]) {
      inSubRows[1].hidden = !this.dataModel.isShowDetails[inRowIndex];
    }  
  },
  /*
   * Initializes structure (if needed) and renders contents
   */
  render: function() {
    // provide html for the Detail cell in the master grid    
    // Setup main grid structure 
    var grid = this.getGridWidget();
    if (!grid) {
      console.debug("Cannot find grid widget for VirtualGridInstance");
      return;
    }
    if (!this.structure) {
      console.debug("No structure specified for VirtualGridInstance", this.gridWidgetId);
      return;
    }
    if (!grid.structure) { // Will occur when coming from another page due to re-creation of widget on page
      console.debug("grid didn't have a structure, setting one")
      for (var i = 0; i < this.structure.length; i++) {
        if (!this.structure[i].onBeforeRow) {
          // initially hide detail sub-row
          this.structure[i].onBeforeRow = dojo.hitch(this, "handleDetailVisibility");
        }
      }
      grid.setStructure(this.structure);
      grid.setModel(this.dataModel);
    }
    // occurs on submit, when widget already exists but there is new data
    else if (!atg.service.form.isFormEmpty(this.dataModel.formId)) {
      console.debug("refreshing the grid");
      grid.refresh();
      grid.update();
    }

  }
});

dojo.require("dojox.grid._grid.cell");
dojo.extend(dojox.grid.cell, {
  get: function(inRowIndex) { 
		return this.grid.model.getDatum(inRowIndex, this.originalFieldIndex == undefined ? this.fieldIndex : this.originalFieldIndex);
  }
});
