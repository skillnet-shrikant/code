/*
 * Create an object to handle processing of spelling markup and correction.
 * Spell checking will be done inline via context menu
 */

var ATGSpellChecker = function( name )
{
  this.Name = name ;
}

ATGSpellChecker.prototype =
{
  GetState : function()
  {
    // Disabled if not WYSIWYG.
    if ( FCK.EditMode != FCK_EDITMODE_WYSIWYG || ! FCK.EditorWindow )
      return FCK_TRISTATE_DISABLED;

    return FCK_TRISTATE_OFF;
  },

  Execute : function()
  {
    var spellForm       = window.parent.document.getElementById("spellCheckForm");

    spellForm.spellCheckString.value  =  window.parent.ResponseGetStrippedValue();
    window.parent.atgSubmitAction({
      form: spellForm,
      formHandler: "/atg/svc/agent/ui/formhandlers/EditorActionFormHandler"
    });
  }
};

var ATGSpellCorrect = function( name )
{
  this.Name = name ;
}

ATGSpellCorrect.prototype =
{
  Execute : function(action)
  {
    var span                  = FCKSelection.MoveToAncestorNode( 'SPAN' );
    switch (action) {
      case 'ignore' :
        var correctionNode    = document.createTextNode(span.innerHTML);
        span.parentNode.replaceChild(correctionNode, span);
        break;
      case 'ignore all' :
        var word     = span.innerHTML;
        aSpans       = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.getElementsByTagName("span");

        for (i = aSpans.length; i > 0; i--)
        {
          if ((aSpans[i-1].className.toUpperCase() == "SPELLERROR") && (aSpans[i-1].innerHTML == word))
          {
            newNode  = FCKeditorAPI.GetInstance('RespondEditor').EditorDocument.createTextNode(word);
            aSpans[i-1].parentNode.replaceChild(newNode, aSpans[i-1]);
          }
        }
        break;
      default :
        var doc = FCK.EditorDocument;
        var correctionNode = doc.createTextNode(action);
        span.parentNode.replaceChild(correctionNode, span);
    }
  }
}

//Register context menu listeners.
FCK.ContextMenu.RegisterListener(
  {
    AddItems : function( menu, tag, tagName )
    {
      var span            = FCKSelection.MoveToAncestorNode( 'SPAN' );
      if (span && (span.className == "spellerror")) {
        menu.AddSeparator() ;
        var editor          = window.parent.captureFieldEditor;
        var word            = span.innerHTML;

        // Get spelling suggestions
        //
        var spellCheckerDiv      = window.parent.dojo.byId("spellCheckDiv");
        for (var x = 0; x < spellCheckerDiv.childNodes.length; x++)
        {
          if (spellCheckerDiv.childNodes[x].getElementsByTagName)
          {
            var aMisspelledWords     = spellCheckerDiv.childNodes[x].getElementsByTagName("div");

            for (var i = 0; i < aMisspelledWords.length; i++)
            {
              if (aMisspelledWords[i].innerHTML == word)
              {
                var aSuggestions     = spellCheckerDiv.childNodes[x].getElementsByTagName("span");

                for (var s = 0; s < aSuggestions.length; s++)
                {
                  var suggestionString = aSuggestions[s].innerHTML;
                  menu.AddItem( 'CorrectSpelling', suggestionString, null, false, suggestionString) ;
                }
              }
            }
          }
        }
        menu.AddSeparator() ;
        menu.AddItem( 'CorrectSpelling', getResource("editor.spellcheckignore"), null, false, 'ignore') ;
        menu.AddItem( 'CorrectSpelling', getResource("editor.spellcheckignoreall"), null, false, 'ignore all') ;
      }
    }
  }
);

// Register the related commands.
FCKCommands.RegisterCommand( 'Spell' , new ATGSpellChecker("ATGSpellChecker") ) ;
FCKCommands.RegisterCommand( 'CorrectSpelling' , new ATGSpellCorrect("ATGSpellCorrect") ) ;

// Create the "Spell" toolbar button.
var oSpellItem = new FCKToolbarButton('Spell', getResource("editor.spellcheck"), getResource("editor.spellcheck"), null, false, null, 13) ;

FCKToolbarItems.RegisterItem('Spell', oSpellItem) ; // 'My_Find' is the name used in the Toolbar config.