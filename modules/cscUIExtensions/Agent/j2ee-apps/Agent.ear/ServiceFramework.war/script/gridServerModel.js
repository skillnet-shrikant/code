dojo.provide("atg.data");
dojo.require('dojox.grid._data.model');

dojo.declare("atg.data.FormhandlerData", dojox.grid.data.Dynamic, { 
	constructor: function(inFields, inUrl) {
		this.server = inUrl;
		this.status = null;
		this.sortField = '';
		this.sortIndex = 0;
    this.formId = null;
    this.formCurrentPageField = "currentResultPageNum";
    this.formSortPropertyField = "sortProperty";
    this.formSortDirectionField = "sortDirection";
	},
	clearData: function() {
		this.cache = [ ];
		this.status = null;
		this.inherited(arguments);
	},
	setStatus: function(inStatus) {
		this.status = inStatus;
	},
	canModify: function() {
		return (!this.status);
	},
	// server send / receive
	send: function(inAsync, inParams, inCallbacks) {
	  if (this.formId !== null && this.formId !== "") {
        console.debug("Grid send called with form " + this.formId);
        if (this.table) {
          console.debug("setting sort fields on the form");
          this.setFormSortProperty(this.sortField, this.sortDesc);
        }
        if (inParams.offset) {
          this.setCurrentPageNumber(inParams.offset);
        }
        else {
          this.setCurrentPageNumber(0);
        }
        var contentMap = {};
        contentMap["atg.formHandlerUseForwards"] = true;
        contentMap._windowid = window.windowId;
        if (window.requestid) { contentMap._requestid = window.requestid;}
  
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
        deferred.addCallbacks(dojo.hitch(this, "receive", inCallbacks), dojo.hitch(this, "receiveError", inCallbacks));
        var mb = dijit.byId("messageBar");
        if (mb) {
          deferred.addCallback(function() { dijit.byId("messageBar").retrieveMessages();});
        }
      }
      else {
        console.debug("skipping send because the needed properties aren't set up yet");
      }
      return deferred;
	},
	_callback: function(cb, eb, data) {
		try{ cb && cb(data); } 
		catch(e){ eb && eb(data, e); }
	},
	receive: function(inCallbacks, inData) {
		inCallbacks && this._callback(inCallbacks.callback, inCallbacks.errback, inData);
	},
	receiveError: function(inCallbacks, inErr) {
		this._callback(inCallbacks.errback, null, inErr)
	},
	encodeRow: function(inParams, inRow) {
		for (var i=0, l=inRow.length; i < l; i++) {
			inParams['_' + i] = (inRow[i] ? inRow[i] : '');
		}
	},
	fetchRowCount: function(inCallbacks) {
		this.send(true, { command: 'count' }, inCallbacks );
	},
	requestRows: function(inRowIndex, inCount)	{
		var params = { 
			orderby: this.sortField, 
			desc: (this.sortDesc ? "true" : ''),
			offset: inRowIndex, 
			limit: inCount
		};
		this.send(true, params, {callback: dojo.hitch(this, this.rows, inRowIndex)});
	},
	// sorting
	canSort: function (inSortIndex) { 
	  var field = this.fields.get(Math.abs(inSortIndex) - 1);
	  if (field.canSort) {
	    return field.canSort;
	  }
	  else {
	    return false;
	  }
	},
	sort: function(inSortIndex) {
		this.sortField = this.fields.get(Math.abs(inSortIndex) - 1).name;
		this.sortDesc = (inSortIndex < 0);
		if (this.formId) {
		  var form = dojo.byId(this.formId);
		  if (form) {
		    if (form[this.formSortPropertyField]) {
		      form[this.formSortPropertyField].value = this.fields.get(Math.abs(inSortIndex) - 1).property;
		      form[this.formSortDirectionField].value = (inSortIndex < 0) ? "desc" : "asc";
		    }
		  }
		}
		this.clearData();
	},
	setCurrentPageNumber: function(inRowIndex) {
	  console.debug("need row index " + inRowIndex);
	  var currentPage = Math.floor(inRowIndex / this.rowsPerPage);
	  console.debug("asking for page " + currentPage);
	  var form = dojo.byId(this.formId);
    if (form) {
      if (form[this.formCurrentPageField]){
	      form[this.formCurrentPageField].value = currentPage;
	    }
	  }
	},
	// server callbacks (called with this == model)
	update: function(inRowIndex, inData) {
		if (inData.error) {
			this.updateError(inData);
		}
		else {
			this.setStatus(null);
			var d = (inData&&inData[0]);
			if (d) {
				this.setRow(d, inRowIndex);
			}
		}
	},
	updateError: function(inRowIndex) {
		this.setStatus(null);
		this.change(inRowIndex);
		alert('Update error. Please refresh.');
	},
	rows: function (inRowIndex, inData) {
    for (var i=0, l=inData.results.length; i<l; i++) {
      var value = inData.results[i];
      this.setRow(value, inRowIndex + i);
    }
	}
	
});