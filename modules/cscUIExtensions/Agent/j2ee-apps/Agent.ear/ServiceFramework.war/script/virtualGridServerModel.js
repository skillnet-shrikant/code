dojo.provide("atg.data");

/*
 * Client-side model for dojo VirtualGrid with SearchFormHandler
 *   SearchFormHandler returns JSON object with following API:
 * 
 *   - resultLength: total number of results
 *   - currentPage: 1-based index for current page
 *   - results: array of results with format corresponding to the grid structure.
 * 
 * The model supports virtual paging which is managed via
 *   the dojo scroller widget (a part of VirtualGrid) and 
 *   results in callbacks for the rendering of cell data
 *   as new rows become visible through the user's scrolling.
 *   When cell data is requested, the rows cache is checked first,
 *   if the row is not found a send is submitted for the row.
 */
dojo.declare("atg.data.VirtualGridData", dojox.grid.data.Dynamic, { 
  constructor: function(inFormId, inUrl, inRowsPerPage) {
    this.formId = inFormId;
    this.server = inUrl;
    this.rowsPerPage = inRowsPerPage;

    this.lastServerError = null;
    this.currentPage = 1; // paging is one-based due to SearchFormHandler
    this.isShowDetails = []; // array of true-false flags for whether details are visible
    this.isCheckRowCount = false;
    
    this.sortField = '';
    this.sortIndex = 0;
    this.sortPropertyElementName = "sortProperty";
    this.sortDirectionElementName = "sortDirection";

    // callbacks for progress message
    this.startProgress = null;
    this.endProgress = null;

    // form input for paging
    this.currentPageElementName = "currentPage";
    this.pageBaseOffset = 0; // 0 for 0-based paging, 1 for 1-based paging, etc.
  },
  /*
   * Clears the results ONLY without bouncing the grid structure
   */
  clearData: function() {
    this.currentPage = this.pageBaseOffset;
    this.lastServerError = null;
    this.count = 0;
    this.isShowDetails = []
    this.isCheckRowCount = false;
    this.cache = [ ];
    this.inherited(arguments);
  },
  /*
   * Performs an HTTP GET to retrieve row details then caches the returned
   *   value in the result cache.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inUrl - URL to HTTP GET
   * inParams - query parameters appended to the URL
   * inProperty - the property in the result object in which to put the returned value
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
  doGet: function(inAsync, inUrl, inParams, inProperty, inRowIndex, inCount) {
	  if (inUrl === null && inUrl === "") {
      console.debug("VirtualGridData unable to GET: null or empty URL");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData sending GET: " + this.formId);
    var deferred = 
		dojo.xhrGet({
      url: inUrl,
      mimetype: "text/html",
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "text",
      content: inParams,
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receiveGet", inProperty, inRowIndex, inCount),
                          dojo.hitch(this, "receiveError", null /* no extra callbacks */));
    return this.checkMessages(deferred);
  },
  /*
   * Submits a form to retrieve a single page of JSON-encoded results.
   *   The form submitted, the successURL to render the results and the number of results 
   *   to render per page are set in the constructor.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
	doJson: function(inAsync, inRowIndex, inCount) {
	  if (this.formId === null && this.formId === "") {
      console.debug("VirtualGridData unable to retrieve JSON: null or empty form ID");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData requesting data using: " + this.formId);
	  this.startProgressIndicator();
    var contentMap = {"atg.formHandlerUseForwards": true,
                      "_windowid": window.windowId};
    if (window.requestid) { contentMap["_requestid"] = window.requestid; }
    var deferred =
    dojo.xhrPost({
      content: contentMap,
      form: dojo.byId(this.formId),
      url: this.server,
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "json",
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receiveJson", inRowIndex, inCount),
                          dojo.hitch(this, "receiveError"));
    return this.checkMessages(deferred);
	},
  /*
   * Performs an HTTP POST using a form on the grid page to retrieve row details 
   *   then caches the returned value in the result cache.
   * 
   * inAsync - flag to indicate whether the call is asynchronous or blocking
   * inForm - the DOM form node from which to post data to the server
   * inUrl - the success URL to render the form handler results
   * inProperty - the property in the result object in which to put the returned value
   * inRowIndex - the index of the row that is being fetched
   * inCount - the number of rows to retrieve
   */
  doPost: function(inAsync, inForm, inUrl, inProperty, inRowIndex, inCount) {
	  if (inForm === null && inForm === "") {
      console.debug("VirtualGridData unable to POST: null or empty form");
	    return null; // no deferred object
	  }
    console.debug("VirtualGridData sending POST: " + inForm.formId);
    var contentMap = {"atg.formHandlerUseForwards": true,
                      "_windowid": window.windowId};
    if (window.requestid) { contentMap["_requestid"] = window.requestid; }
    var deferred =
    dojo.xhrPost({
      content: contentMap,
      form: inForm,
      url: inUrl,
      encoding: "utf-8",
      timeout: atgXhrTimeout,
      handleAs: "text",
      sync: !inAsync
    });
    deferred.addCallbacks(dojo.hitch(this, "receivePost", inProperty, inRowIndex, inCount),
                          dojo.hitch(this, "receiveError"));
    return this.checkMessages(deferred);
  },
  getRowCount: function() {
    if (this.isCheckRowCount === false) {
      this.fetchRowCount(); // gets the count from the server synchronously
      this.isCheckRowCount = true;
    }
    return this.count;
  },
  /*
   * Returns the total number of results available by making a server
   *   request for the first page of data. The server paging model must be 
   *   capable of supplying a number for the row count even when only a subset 
   *   of the results are actually returned.
   */
  fetchRowCount: function() {
    console.debug("in fetchRowCount");
    dojo.byId(this.formId)[this.currentPageElementName].value = this.pageBaseOffset;
    this.doJson(false, 0);
    this.pages[0] = true;
    return this.count;
  },
  requestRows: function(inRowIndex, inCount)  {
  	console.debug("in requestRows this.isCheckRowCount " + this.isCheckRowCount, this.count);
  	if (this.isCheckRowCount === true && this.count == 0)
  		return;

  	console.debug("in requestRows for row " + inRowIndex);
  	// are first and last indices cached? 
  	// if yes, this page is already in the cache - skip request
    if (this.getRow(inRowIndex + i) === undefined && 
    		this.getRow(inRowIndex + inCount - 1) === undefined) {
	    var currentPage = Math.floor(inRowIndex / this.rowsPerPage) + this.pageBaseOffset;
	    dojo.byId(this.formId)[this.currentPageElementName].value = currentPage;
	    console.debug("set current page to " + currentPage);
	    this.doJson(true, inRowIndex, inCount);
    }
    else {
    	this.rowsProvided(inRowIndex, inCount);
    }	
  },
  /*
   * Returns data for cell via HTTP GET
   * 
   * inProperty - row property into which to insert returned data
   * inUrl - URL to GET
   * inParams - query params for the URL
   * inRowIndex - index of the row in the grid
   */
  getCellByGet: function(inProperty, inUrl, inParams, inRowIndex) {
    var row = this.getRow(inRowIndex);
    if(row && !row[inProperty]) {
      this.doGet(false, inUrl, inParams, inProperty, inRowIndex, 1);
      row = this.getRow(inRowIndex);
    }
    return (row && row[inProperty]) ? row[inProperty] : "...";
  },
  /*
   * Returns data for cell via HTTP POST
   * 
   * inProperty - row property into which to insert returned data
   * inUrl - form action
   * inParams - forms to post
   * inRowIndex - index of the row in the grid
   */
  getCellByPost: function(inProperty, inForm, inUrl, inRowIndex) {
    var row = this.getRow(inRowIndex);
    if(row && !row[inProperty]) {
      this.doPost(false, inForm, inUrl, inProperty, inRowIndex, 1);
      row = this.getRow(inRowIndex);
    }
    return (row && row[inProperty]) ? row[inProperty] : "...";
  },
  /*
   * Method to receive data from a GET call, it is not recommended
   *   to call this method directly
   */
  receiveGet: function(inProperty, inRowIndex, inCount, inData) {
    var row = this.getRow(inRowIndex);
    row[inProperty] = inData;
    this.setRow(row, inRowIndex);
    this.rowsProvided(inRowIndex, inCount);
    return inData;
  },
  /*
   * Method to receive data from a POST call, it is not recommended
   *   to call this method directly
   */
  receivePost: function(inProperty, inRowIndex, inCount, inData) {
    var row = this.getRow(inRowIndex);
    row[inProperty] = inData;
    this.setRow(row, inRowIndex);
    this.rowsProvided(inRowIndex, inCount);
    return inData;
  },
  /*
   * Method to receive JSON data
   */
  receiveJson: function(inRowIndex, inCount, inData) {
    this.currentPage = (inData.currentPage || typeof inData.currentPage == "number") ? inData.currentPage : 1; // paging on server is one-based
    if (inData.results && inData.results.length) {
      this._setupFields(inData.results[0]);
      if (this.isCheckRowCount === false) {
        this.setRowCount(inData.resultLength ? inData.resultLength : 0);
        this.isCheckRowCount = true;
      }
      // grid rows on client are zero-based
      for (var i = 0, length = inData.results.length; i < length; i++) {
        this.setRow(inData.results[i], inRowIndex + i);
      }
    }
    this.rowsProvided(inRowIndex, inCount);
	  this.endProgressIndicator();
		return inData;
  },
  /*
   * Method to handle a server error
   */
	receiveError: function(inErr) {
	  this.lastServerError = inErr;
	  this.endProgressIndicator();
		return inErr;
	},
  _setupFields: function(dataItem){
    // abort if we already have setup fields
    if(this.fields._setup){
      return;
    }
    //console.debug("setting up fields", m);
    var fields = [];
    for(var fieldName in dataItem){
      var newField = { key: fieldName };
      fields.push(newField);
    }
    console.debug("new fields:", fields);
    this.fields.set(fields);
    this.fields._setup = true;
    this.notify("FieldsChange");
  },
	/* 
	 * Gets the data for a given cell 
	 */
  getDatum: function(inRowIndex, inFieldIndex) {
    var row = this.getRow(inRowIndex);
    if (row) {
      var field = this.fields.get(inFieldIndex);
      //console.debug("in getDatum for row " + inRowIndex + " field index " + inFieldIndex + " field " + field.key);
      var propertyName = field.key;
      var row = this.getRow(inRowIndex);
      return row[propertyName]; 
    }
    else {
      return this.fields.get(inFieldIndex).na;
    }
  },
  sort: function(inSortIndex) {
    this.sortField = this.fields.get(Math.abs(inSortIndex) - 1).name;
    this.sortDesc = (inSortIndex < 0);
    if (this.formId) {
      var form = dojo.byId(this.formId);
      if (form) {
        if (form[this.sortPropertyElementName]) {
          form[this.sortPropertyElementName].value = this.fields.get(Math.abs(inSortIndex) - 1).key;
          form[this.sortDirectionElementName].value = (inSortIndex < 0) ? "desc" : "asc";
        }
      }
    }
    this.clearData();
  },
  setFormSortProperty: function (/*int*/columnIndex, /*bool*/sortDesc)  {
    var theForm = dojo.byId(this.formId);
    if (columnIndex != -1) {
      theForm[this.sortPropertyElementName].value = this.fields.get(columnIndex).key;
      theForm[this.sortDirectionElementName].value = sortDesc ? "desc" : "asc";
    }
    else
    {
      theForm[this.sortPropertyElementName].value = "id";
      theForm[this.sortDirectionElementName].value = "asc";
    }
  },
  /*
   * Starts the in-progress message
   */
	startProgressIndicator: function() {
    if (dojo.isFunction(this.startProgress)) {
      this.startProgress();
    }	  
	},
	/*
	 * Ends the in-progress message and displays the number of results returned 
	 *   or the message for no results.
	 */
	endProgressIndicator: function() {
	  if (dojo.isFunction(this.endProgress)) {
	    this.endProgress();
	  }
	},
	/*
	 * Calls the message bar
	 */
	checkMessages: function(inDeferred) {
    var mb = dijit.byId("messageBar");
    if (mb) {
      inDeferred.addCallback(function() { dijit.byId("messageBar").retrieveMessages();});
    }
    return inDeferred;
	}
});
