dojo.provide("atg.commerce.csr.catalog.endeca.search");

atg.commerce.csr.catalog.endeca.search =
{
  //Category Tree functionality
  showHideMainCategories: function (rootCategoriesUrl) {
    if (this._isNotLoadedMainCategories()) {
      this._loadAndShowMainCategories(rootCategoriesUrl);
    } else {
      if (this._isVisibleMainCategories()) {
        this._hideMainCategories();
      } else {
        this._showMainCategories();
      }
    }
  },

  _isNotLoadedMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    if (categoriesObject.categoriesLoaded != true) return true;
    return false;
  },

  _loadAndShowMainCategories: function (rootCategoriesUrl) {
    this._showMainCategories();
    dojo.xhrGet({
      url: rootCategoriesUrl,
      content: {
        _windowid: window.windowId
      },
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if (!(response instanceof Error)) {
          var value = document.getElementById('atg_commerce_csr_catalog_endeca_categories');
          value.innerHTML = response;
          value.categoriesLoaded = true;
        } else {
          document.getElementById('atg_commerce_csr_catalog_endeca_categories').innerHTML = "An error occured...";
        }
      },
      mimetype: "text/html"
    });
  },

  _isVisibleMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    return categoriesObject.style.display == 'block';
  },

  _hideMainCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "none";
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    categoriesObject.style.display = "none";
    $('body').unbind('keyup', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
    $('body').unbind('click', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
  },

  _showMainCategories: function () {
    var categoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_categories");
    categoriesObject.style.display = "block";
    $('body').bind('click', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
    $('body').bind('keyup', atg.commerce.csr.catalog.endeca.search._hideMainCategories);
  },

  showHideSubCategories: function (selection, rootCategoryId, subCategoriesUrl) {
    if (this._isNotLoadedSubCategoryFlyOut(selection)) {
      this._loadAndShowSubCategoryFlyOut(selection, rootCategoryId, subCategoriesUrl);
    } else {
      this._fillSubCategoriesContent(selection);
      this._showSubCategories();
    }
  },

  _isNotLoadedSubCategoryFlyOut: function (selection) {
    return document.getElementById("atg_commerce_csr_catalog_endeca_category" + selection) == null;
  },
  
  _findSubcategoriesCount: function (collection) {
    for (var i = 0; i < collection.length; i++) {
      var levelString = collection[i].getElementsByTagName("a")[0].className;
      var level = parseInt(levelString.substring(5), 10);
      collection[i].level = level;
    }
    for (i = 0; i < collection.length-1; i++) {
      var count = 0;
      for (var j = i + 1; (j < collection.length) && (collection[i].level < collection[j].level); j++) count++;
      collection[i].subCategoryCount = count;
    }
  },

  _convertHTML: function(containerId, maxColumnCount, minRowsInColumn, nonBreakableSubCount) {
    var container = document.getElementById(containerId);
    var collection = container.getElementsByTagName("div");
    atg.commerce.csr.catalog.endeca.search._findSubcategoriesCount(collection);
    
    var rowsInColumn = minRowsInColumn;
    var itemCount = collection.length;
    var columnCount = Math.ceil(itemCount / minRowsInColumn);
    if (columnCount > maxColumnCount) {
      rowsInColumn = Math.ceil(itemCount / maxColumnCount);
      columnCount = maxColumnCount;
    }
    var columnIndex = 0;
    var rowIndex = 0;
    columnElement = document.createElement("TD");
    columnElement.id = "column"+columnIndex;
    columnElement.className = "column"+columnIndex;
    var colLength = collection.length;
    var nonBreakableCount=0;
    var nonBreakableItemBefore=false;
    var rowElement = document.createElement("TR");
    for (var i = 0; i < colLength; i++) {        
      //1) when rowIndex > rowsInColumn;
      //2) when category has less than 5 subcategories and rowIndex + 1+subCategoryCount > rowsInColumn
      var isNotLastColumn = columnIndex < maxColumnCount-1;
      var rowIndexExceeds = rowIndex >= rowsInColumn;
      var haveNonBreakableCat = (collection[0].subCategoryCount > 0) && (collection[0].subCategoryCount <= nonBreakableSubCount);
      var haveNonBreakableCatOnBorder = rowIndex + 1 + collection[0].subCategoryCount > rowsInColumn;
      var rest_of_items = colLength - i;
      var free_space_in_columns_with_error = (maxColumnCount - 1 - columnIndex) * (rowsInColumn -  nonBreakableSubCount - 1);
      var haveEnoughSpaceInNextCol = rest_of_items <= free_space_in_columns_with_error;
      if (haveNonBreakableCatOnBorder && isNotLastColumn && haveNonBreakableCat && (nonBreakableCount == 0)) {
        nonBreakableCount = collection[0].subCategoryCount+1;
      }
      if (  //if end of the row and item breakable or non-breakable but we have already non-breakable item at the border...
        (isNotLastColumn && rowIndexExceeds && ((nonBreakableCount == 0) || nonBreakableItemBefore)) 
        // if non-breakable item at the border and enough space in columns left...
        ||(isNotLastColumn && haveNonBreakableCatOnBorder && haveNonBreakableCat && haveEnoughSpaceInNextCol && (rest_of_items > rowsInColumn-rowIndex)) 
        //if last non-breakable item at the border and free columns left...
        ||(isNotLastColumn && haveNonBreakableCatOnBorder && haveNonBreakableCat && (rest_of_items == nonBreakableCount))) { 
        // ...start a new column
        //container.appendChild(columnElement);
        rowElement.appendChild(columnElement);
        columnIndex++;
        rowIndex=0;
        columnElement = document.createElement("TD");
        /*columnElement.id = "column"+columnIndex;*/
        columnElement.className = "column"+columnIndex;
      } 
      nonBreakableItemBefore = (nonBreakableCount == 1);  //avoid to prohibit new row when non-breakable items goes one by one on a border. 
      if (nonBreakableCount > 0) nonBreakableCount--;
      columnElement.appendChild(collection[0]);
      rowIndex++;
    }
    rowElement.appendChild(columnElement); //last row
    var tableElement = document.createElement("TABLE");
    tableElement.appendChild(rowElement);
    container.appendChild(tableElement);
  },
  

  _loadAndShowSubCategoryFlyOut: function (selection, rootCategoryId, subCategoriesUrl) {
    this._showSubCategories();
    dojo.xhrGet({
      url: subCategoriesUrl,
      content: {
        _windowid: window.windowId,
        rootCategoryId: rootCategoryId,
        categoryNumber: selection
      },
      encoding: "utf-8",
      handle: function(response, ioArgs) {
        if (!(response instanceof Error)) {
          document.getElementById("atg_commerce_csr_catalog_endeca_subcategoryFlyOutContentCache").innerHTML += response;
          atg.commerce.csr.catalog.endeca.search._convertHTML("atg_commerce_csr_catalog_endeca_category" + selection, 3, 15, 5);
          atg.commerce.csr.catalog.endeca.search._fillSubCategoriesContent(selection);
        } else {
          document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories").innerHTML = "An error occured..."; /* TODO: error message */
        }
      },
      mimetype: "text/html"
    });
  },

  _isVisibleSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    return subCategoriesObject.style.display == 'block';
  },

  _hideSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "none";
  },

  _fillSubCategoriesContent: function (selection) {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    var subCategoriesContent = document.getElementById("atg_commerce_csr_catalog_endeca_category" + selection);
    subCategoriesObject.innerHTML = subCategoriesContent.innerHTML;
    var numberResults = this._getNumberResults(selection);
  },

  _showSubCategories: function () {
    var subCategoriesObject = document.getElementById("atg_commerce_csr_catalog_endeca_sub-categories");
    subCategoriesObject.style.display = "block";
  },

  _getNumberResults: function (selection) {
    var subCategoriesContent = document.getElementById("atg_commerce_csr_catalog_endeca_category"+selection);
    var inputObject = subCategoriesContent.getElementsByTagName("input")[0];
    return inputObject.value;
  },

  //Category Tree functionality end

  //Autocomplete functionality
  submitSearchTermRequest: function (searchTermURL)
  {
    //replace the search term input with user typed value
    var searchTerm = dojo.byId("searchTermInput").value;      
    var searchURL=searchTermURL.replace("SEARCHTERMINPUT",escape(searchTerm));
    if ($('#searchTermInput').catcomplete) $('#searchTermInput').catcomplete("destroy");
    atgSubmitAction({url: searchURL});
  },
  setupAutoComplete: function (jsonUrl, contentCollectionTemplate, autoSuggestMinLength)
  {
    $(function() {
      $.widget( "custom.catcomplete", $.ui.autocomplete, {
        _renderMenu: function( ul, items ) {
          var self = this, currentCategory = "";
          $.each( items, function( index, item ) {
            if ( item.category != currentCategory ) {
              ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
              currentCategory = item.category;
            }
            self._renderItem( ul, item );
          });
        }
      });

      $( "#searchTermInput" ).catcomplete({
        source: function( request, response ) {
          //replace the search term input with user typed value
          var contentCollectionURI = contentCollectionTemplate.replace("SEARCHTERMINPUT", escape(request.term));
          $.ajax({
            url: jsonUrl,
            dataType: "json",
            data: {
              _windowid: window.windowId,
              contentCollection: contentCollectionURI
            },
            error: function(jqXHR, textStatus, errorThrown) {
              alert('AJAX Error: ' + jqXHR.status);
            },
            success: function( data ) {
              var list = [];
              $(data.dimensionSearchResults.dimensionSearchGroups).each(function(i, dimension) {
                $(dimension.dimensionSearchValues).each(function(j, result) {
                  var label = [];
                  $(result.ancestors).each(function(j, ancestor) {
                    label.push(ancestor.label);
                  });
                  label.push(result.label);
                  list.push({ label: label.join("  >  "), value: result.label, term: request.term, category: dimension.displayName, contentURL: result.contentURL });
                });
              });

              response(list);
            }
          }).done(function( msg ) {
            //We will need to clear out the search box. This could easily be done here
          });
        },
        minLength: autoSuggestMinLength,
        select: function( event, ui ) {
        },
        open: function() {
          $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
        },
        close: function() {
          $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
        },
        appendTo: "#catalog-nav"
      })
      .data("catcomplete")._renderItem = function(ul, item) {
        // highlight only first occurence in words regardless of words or search term capitalization
        var words = item.label.split(" ");
        for (var i = 0; i < words.length; i++) {
          if (words[i].toLowerCase().indexOf(item.term.toLowerCase()) === 0) {
            words[i] = "<span class='highlight'>" + words[i].substr(0, item.term.length) + "</span>" + words[i].substring(item.term.length);
          }
        }
        var lbl = words.join(" ");
        var $link = $("<a>").html(lbl).click(function () {
          atgSubmitAction({url: item.contentURL});
        });
        return $("<li></li>")
        .data("item.autocomplete", item)
        .append($link)
        .appendTo(ul);
      }; 
    });
  }
  //Autocomplete functionality end

}
